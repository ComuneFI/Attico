package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.support.Expressions;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.ModelloCampo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QModelloCampo;
import it.linksmt.assatti.datalayer.repository.ModelloCampoRepository;
import it.linksmt.assatti.service.dto.ModelloCampoSearchDTO;
import it.linksmt.assatti.service.util.ServiceUtil;

@Service
@Transactional
public class ModelloCampoService {

	@Inject
	private ModelloCampoRepository modelloCampoRepository;
	    
	@Inject
	private ServiceUtil serviceUtil;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private AooService aooService;
	
	@Inject
    private UserService userService;
	
	public ModelloCampoService() {
		
	}
	
	@Transactional(readOnly=true)
	public ModelloCampo findOne(Long id, Long profiloId){
		BooleanExpression predicate = QModelloCampo.modelloCampo.id.isNotNull();
        predicate = predicate.and(QModelloCampo.modelloCampo.id.eq(id));
        
        if(profiloId!=null && !serviceUtil.hasRuolo(profiloId, "ROLE_AMMINISTRATORE_RP")){
    		predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.eq(profiloId));
    	}
		ModelloCampo modelloCampo = modelloCampoRepository.findOne(predicate);
        if (modelloCampo != null && modelloCampo.getProfilo()!=null && modelloCampo.getProfilo().getId()!=null) {
        	modelloCampo.getProfilo().setAoo(DomainUtil.minimalAoo(modelloCampo.getProfilo().getAoo()));
        }
        if(modelloCampo != null && modelloCampo.getAoo()!=null) {
        	modelloCampo.setAoo(DomainUtil.minimalAoo(modelloCampo.getAoo()));
        }
        return modelloCampo;
	}
	
	@Transactional(readOnly=true)
	public Page<ModelloCampo> getAll(ModelloCampoSearchDTO search, Pageable pageable){
		
		BooleanExpression predicate = QModelloCampo.modelloCampo.id.isNotNull();
		
		/*
		if(search.getProfilo()==null && search.getProfiloIdLoggato() != null){
			List<Long> profilosId = new ArrayList<Long>();
	    	Profilo profilo = profiloRepository.findOne(search.getProfiloIdLoggato());
	    	if(serviceUtil.hasRuolo(profilo.getId(), "ROLE_AMMINISTRATORE_RP")){
	            List<Profilo> profilos = profiloRepository.findByAooId(profilo.getAoo().getId());
	            for(Profilo p : profilos){
	            	profilosId.add(p.getId());
	            }
	    	}else{
	    		profilosId.add(profilo.getId());
	    	}
	    	
	    	
	    	predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.in(profilosId));
		}
		*/
		
		if(search.getAoo()!=null && !search.getAoo().isEmpty()) {
			BooleanExpression aooExp = null;
			if("tutte".equalsIgnoreCase(search.getAoo())) {
				aooExp = QModelloCampo.modelloCampo.aoo.isNull();
			}else {
				aooExp = Expressions.stringTemplate("'('").concat(QModelloCampo.modelloCampo.aoo.codice).concat(Expressions.stringTemplate("') '")).concat(QModelloCampo.modelloCampo.aoo.descrizione).containsIgnoreCase(search.getAoo());;
			}
			predicate = predicate.and(aooExp);
		}
		
    	if(null != search.getTipoCampo() && !"".equals(search.getTipoCampo())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.tipoCampo.containsIgnoreCase(search.getTipoCampo()));
    	}
    	
    	if(search.getCodice()!=null && !"".equals(search.getCodice())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.codice.containsIgnoreCase(search.getCodice()));
    	}
    	
    	if(search.getTitolo()!=null && !"".equals(search.getTitolo())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.titolo.containsIgnoreCase(search.getTitolo()));
    	}
    	
    	if(search.getProfilo()!=null){
    		if(search.getProfilo().equals(0L)){
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.isNull());
    		}else{
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.eq(search.getProfilo()));
    		}
    	}
    	if(search.getAooIdReferente()!=null && search.getAooIdReferente() > 0) {
    		List<Aoo> aoos = aooService.getAooRicorsiva(search.getAooIdReferente());
    		predicate = predicate.and(QModelloCampo.modelloCampo.aoo.in(aoos));
    	}else if(search.getProfilo()==null && !userService.isLoggedUserAnAdmin() && search.getProfiloIdLoggato()!=null && !userService.isLoggedUserAnAdminRP()){
			BooleanExpression internalPredicate = QModelloCampo.modelloCampo.profilo.isNull().and(QModelloCampo.modelloCampo.aoo.isNull());
			/*
			List<Profilo> profiliUtente = profiloService.findActiveByUtenteId(search.getUtenteid());
			if(profiliUtente!=null && profiliUtente.size()>0){
				List<Long> profUtenteLong = new ArrayList<Long>();
				for(Profilo prof : profiliUtente){
					profUtenteLong.add(prof.getId());*/
					Profilo prof = profiloService.findOneBase(search.getProfiloIdLoggato());
					Aoo aoo = prof.getAoo();
					if(aoo==null) {
						aoo = aooService.findAooByProfiloId(prof.getId());
					}
					List<Aoo> aoos = aooService.getAooSuperiori(aoo.getId());
					internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.isNull().and(QModelloCampo.modelloCampo.aoo.in(aoos).and(QModelloCampo.modelloCampo.propagazioneAoo.isTrue()).or(QModelloCampo.modelloCampo.aoo.id.eq(aoo.getId()))));
					internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.id.eq(search.getProfiloIdLoggato()));
				/*}
				internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.id.in(profUtenteLong));
			}*/
			predicate = predicate.and(internalPredicate);
		}
    	
    	if(search.getProfilo()!=null){
    		if(search.getProfilo().equals(0L)){
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.isNull());
    		}else{
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.eq(search.getProfilo()));
    		}
    	}
		
		if(search.getTipoAttoId()!=null && search.getTipoAttoId()>0L){
			predicate = predicate.and(QModelloCampo.modelloCampo.tipoAtto.id.eq(search.getTipoAttoId()));
		}else if(search.getTipoAttoId()!=null && search.getTipoAttoId().equals(0L)){
			predicate = predicate.and(QModelloCampo.modelloCampo.tipoAtto.isNull());
		}
    	
        Page<ModelloCampo> page = modelloCampoRepository.findAll(predicate, pageable);
        for(ModelloCampo mc : page){
        	if(mc.getProfilo()!=null) {
        		if(mc.getProfilo().getAoo()!=null) {
        			mc.getProfilo().setAoo(DomainUtil.minimalAoo(mc.getProfilo().getAoo()));
        		}
        		else {
        			Aoo aoo = aooService.findAooByProfiloId(mc.getProfilo().getId());
        			mc.getProfilo().setAoo(DomainUtil.minimalAoo(aoo));
        		}
        	}
        	if(mc != null && mc.getAoo()!=null) {
    			mc.setAoo(DomainUtil.minimalAoo(mc.getAoo()));
            }
        }
        return page;
	}
	
	@Transactional(readOnly=true)
	public Page<ModelloCampo> getAllMixed(ModelloCampoSearchDTO search, Pageable pageable){
		
		BooleanExpression predicate = QModelloCampo.modelloCampo.id.isNotNull();
		if(search.getProfilo()==null && search.getProfiloIdLoggato() != null){
			BooleanExpression internalPredicate = QModelloCampo.modelloCampo.profilo.isNull().and(QModelloCampo.modelloCampo.aoo.isNull());
			/*
			List<Profilo> profiliUtente = profiloService.findActiveByUtenteId(search.getUtenteid());
			if(profiliUtente!=null && profiliUtente.size()>0){
				List<Long> profUtenteLong = new ArrayList<Long>();
				for(Profilo prof : profiliUtente){
					profUtenteLong.add(prof.getId());*/
					Profilo prof = profiloService.findOneBase(search.getProfiloIdLoggato());
					Aoo aoo = prof.getAoo();
					if(aoo==null) {
						aoo = aooService.findAooByProfiloId(prof.getId());
					}
					List<Aoo> aoos = aooService.getAooSuperiori(aoo.getId());
					internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.isNull().and(QModelloCampo.modelloCampo.aoo.in(aoos).and(QModelloCampo.modelloCampo.propagazioneAoo.isTrue()).or(QModelloCampo.modelloCampo.aoo.id.eq(aoo.getId()))));
					internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.id.eq(search.getProfiloIdLoggato()));
				/*}
				internalPredicate = internalPredicate.or(QModelloCampo.modelloCampo.profilo.id.in(profUtenteLong));
			}*/
			predicate = predicate.and(internalPredicate);
		}
		
    	if(null != search.getTipoCampo() && !"".equals(search.getTipoCampo())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.tipoCampo.equalsIgnoreCase(search.getTipoCampo()));
    	}
    	
    	if(search.getCodice()!=null && !"".equals(search.getCodice())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.codice.containsIgnoreCase(search.getCodice()));
    	}
    	
    	if(search.getTitolo()!=null && !"".equals(search.getTitolo())){
    		predicate = predicate.and(QModelloCampo.modelloCampo.titolo.containsIgnoreCase(search.getTitolo()));
    	}
    	
    	if(search.getProfilo()!=null){
    		if(search.getProfilo().equals(0L)){
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.isNull());
    		}else{
    			predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.eq(search.getProfilo()));
    		}
    	}
    	if(search.getTipoAttoId()!=null && search.getTipoAttoId()>0L){
			predicate = predicate.and(QModelloCampo.modelloCampo.tipoAtto.id.eq(search.getTipoAttoId()).or(QModelloCampo.modelloCampo.tipoAtto.isNull()));
		}else if(search.getTipoAttoId()!=null && search.getTipoAttoId().equals(0L)){
			predicate = predicate.and(QModelloCampo.modelloCampo.tipoAtto.isNull());
		}
    	
        Page<ModelloCampo> page = modelloCampoRepository.findAll(predicate, pageable);
        for(ModelloCampo mc : page){
        	if(mc.getProfilo()!=null){
        		String load = mc.getProfilo().getAoo().getCodice();
        	}
        }
        return page;
	}
}
