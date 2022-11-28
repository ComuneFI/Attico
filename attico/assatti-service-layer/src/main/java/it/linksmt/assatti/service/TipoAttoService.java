package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Annullamento;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.FaseRicercaHasCriterio;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAttoHasFaseRicerca;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.domain.TipoProgressivo;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoProgressivoRepository;
import it.linksmt.assatti.service.converter.TipoAttoConverter;
import it.linksmt.assatti.service.dto.CampoTipoAttoDto;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;
import it.linksmt.assatti.service.dto.TipoAttoDto;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoAttoService {
	private final Logger log = LoggerFactory.getLogger(TipoAttoService.class);

	@Inject
	private TipoAttoRepository tipoAttoRepository;
	
	@Inject
	private TipoProgressivoRepository tipoProgressivoRepository;
	
	@Inject
	private SezioneTipoAttoService sezioneTipoAttoService;
	
	@Inject
	private CampoTipoAttoService campoTipoAttoService;
	
	@Transactional(readOnly = true)
	public List<String> getStatiByTipoAttoId(Long tipoAttoId){
		return tipoAttoRepository.getStatiByTipoAttoId(tipoAttoId);
	}
	
	@Transactional(readOnly = true)
	public List<TipoAtto> findByIdIn(List<Long> ids){
		return tipoAttoRepository.findByIdIn(ids);
	}
	
	@Transactional
	public void delete(Long id) throws Exception{
		/*
		 * elimino eventuali sezioniTipoAtto
		 */
		sezioneTipoAttoService.deleteByTipoAtto(id);
		
		/*
		 * elimino eventuali campiTipoAtto
		 */
		campoTipoAttoService.deleteByTipoAtto(id);
		
		/*
		 * Elimino TipoAtto
		 */
		tipoAttoRepository.delete(id);
	}
	
	@Transactional(readOnly = true)
	public TipoAtto findByCodice(String codice){
		if(codice!=null && !codice.isEmpty()){
			return tipoAttoRepository.findOne(QTipoAtto.tipoAtto.codice.equalsIgnoreCase(codice));
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public Iterable<TipoAtto> findByPredicate(Predicate predicate){
		return tipoAttoRepository.findAll(predicate, new OrderSpecifier<>(Order.ASC, QTipoAtto.tipoAtto.id));
	}
	
	@Transactional(readOnly = true)
	public  TipoAtto findOne(Long id) {
		log.debug("findOne id" + id);
		TipoAtto domain = tipoAttoRepository.findOne(id);
		if(domain != null ){
			for (TipoIter iter : domain.getTipiIter()) {
				iter.setTipoAtto( new TipoAtto(domain.getId(),domain.getCodice(), domain.getDescrizione()));
			}
			simplifyAnnullamento(domain);
			if(domain.getFasiRicerca()!=null) {
				for(TipoAttoHasFaseRicerca fase : domain.getFasiRicerca()) {
					fase.setTipoAtto(null);
					if(fase.getCriteri()!=null) {
						for(FaseRicercaHasCriterio criterio : fase.getCriteri()) {
							criterio.setFaseTipoAtto(null);
							if(criterio.getAoos()!=null) {
								Set<Aoo> simplyAoo = new HashSet<Aoo>();
								for(Aoo aoo : criterio.getAoos()) {
									simplyAoo.add(new Aoo(aoo.getId(), aoo.getDescrizione(), aoo.getCodice(), aoo.getIdentitavisiva()));
								}
								criterio.setAoos(simplyAoo);
							}
							if(criterio.getProfilos()!=null) {
								Set<Profilo> simplyProfilo = new HashSet<Profilo>();
								for(Profilo prof : criterio.getProfilos()) {
									Profilo profiloSimple = new Profilo();
									profiloSimple.setId(prof.getId());
									profiloSimple.setDescrizione(prof.getDescrizione());
									Utente utente = new Utente(prof.getUtente().getId(), prof.getUtente().getCodicefiscale(), prof.getUtente().getUsername(), prof.getUtente().getCognome(), prof.getUtente().getNome());
									profiloSimple.setUtente(utente);
									Aoo aooSimple = new Aoo(prof.getAoo().getId(), prof.getAoo().getDescrizione(), prof.getAoo().getCodice(), prof.getAoo().getIdentitavisiva());
									profiloSimple.setAoo(aooSimple);
									simplyProfilo.add(profiloSimple);
								}
								criterio.setProfilos(simplyProfilo);
							}
						}
					}
				}
			}
		}
		return domain;
	}
	
	@Transactional(readOnly = true)
	public TipoAtto findOneSimple(Long id) {
		TipoAtto domain = tipoAttoRepository.findOne(id);
		TipoAtto simple = new TipoAtto();
		simple.setId(domain.getId());
		simple.setCodice(domain.getCodice());
		simple.setDescrizione(domain.getDescrizione());
		simple.setEnabled(domain.getEnabled());
		return simple;
	}

	//necessario per rimuovere l'entità ricorsiva che non è possibile trasformare in json
	private void simplifyAnnullamento(TipoAtto domain){
		if(domain.getStatiAnnullamento()!=null){
			for(Annullamento an : domain.getStatiAnnullamento()){
				if(an.getTipoAtto()!=null){
					Long idt = an.getTipoAtto().getId();
					an.setTipoAtto(new TipoAtto());
					an.getTipoAtto().setId(idt);
				}
			}
		}
	}

	@Transactional
	public  TipoAttoDto save(TipoAttoDto dto) throws ServiceException {
		TipoAtto domain = TipoAttoConverter.convertToModel(dto);
		
		if(domain!=null && domain.getFasiRicerca()!=null) {
			for(TipoAttoHasFaseRicerca t : domain.getFasiRicerca()) {
				if(t!=null && t.getCriteri()!=null) {
					for(FaseRicercaHasCriterio c : t.getCriteri()) {
						if(c!=null) {
							if(c.getValue()==null || !c.getValue()) {
								c.setVisibilitaCompleta(false);
							}
						}
					}
				}
			}
		}
		
        /*
         * Salvataggio SezioneTipoAtto e CampoTipoAtto
         */
        if(domain!=null && domain.getId()!=null && domain.getId()>0L) {
        	Long idTipoAtto = domain.getId();
	        List<SezioneTipoAttoDto> listaSez = new ArrayList<>();
	        for(SezioneTipoAttoDto s : dto.getSezioni()) {
	        	if(s!=null) {
		        	s.setIdTipoAtto(idTipoAtto);
		        	listaSez.add(s);
	        	}
	        }
	        sezioneTipoAttoService.save(listaSez);
	        
	        List<CampoTipoAttoDto> listaCampi = new ArrayList<>();
	        for(CampoTipoAttoDto c : dto.getCampi()) {
	        	if(c!=null) {
		        	c.setIdTipoAtto(idTipoAtto);
		        	listaCampi.add(c);
	        	}
	        }
	        campoTipoAttoService.save(listaCampi);
        }else {
	        sezioneTipoAttoService.save(dto.getSezioni());
	        campoTipoAttoService.save(dto.getCampi());
        }
		domain  =  tipoAttoRepository.save(domain );
		TipoAttoDto dtoRet = TipoAttoConverter.convertToDto(domain);
		return dtoRet;
	}

	@Transactional(readOnly=true)
	public ResponseEntity<List<TipoAtto>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoAtto = QTipoAtto.tipoAtto.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("codice")!=null && !search.get("codice").isJsonNull() && search.get("codice").getAsString()!=null && !"".equals(search.get("codice").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.codice.containsIgnoreCase(search.get("codice").getAsString()));
			}
			if(search.get("descrizione")!=null && !search.get("descrizione").isJsonNull() && search.get("descrizione").getAsString()!=null && !"".equals(search.get("descrizione").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.descrizione.containsIgnoreCase(search.get("descrizione").getAsString()));
			}
			if(search.get("tipoProgressivo")!=null && !search.get("tipoProgressivo").isJsonNull() && search.get("tipoProgressivo").getAsLong()>0L ){
				TipoProgressivo tipoProgressivoSearch = tipoProgressivoRepository.findOne(search.get("tipoProgressivo").getAsLong());
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.tipoProgressivo.eq(tipoProgressivoSearch));
			}
			if(search.get("proponente")!=null && !search.get("proponente").isJsonNull()){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.proponente.eq(search.get("proponente").getAsBoolean()));
			}
			if(search.get("consiglio")!=null && !search.get("consiglio").isJsonNull()){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.consiglio.eq(search.get("consiglio").getAsBoolean()));
			}
			if(search.get("giunta")!=null && !search.get("giunta").isJsonNull()){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.giunta.eq(search.get("giunta").getAsBoolean()));
			}
			if(search.get("enabled")!=null && !search.get("enabled").isJsonNull()){
				predicateTipoAtto = predicateTipoAtto.and(QTipoAtto.tipoAtto.enabled.eq(search.get("enabled").getAsBoolean()));
			}
			
		}
		Sort sort = new Sort(new org.springframework.data.domain.Sort.Order(Direction.ASC, "descrizione" ));
		Page<TipoAtto> page = tipoAttoRepository.findAll(predicateTipoAtto, PaginationUtil.generatePageRequest(offset, limit, sort));
		for(TipoAtto tipoatto : page){
			simplifyAnnullamento(tipoatto);
		}
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoAttos", offset, limit);
        return new ResponseEntity<List<TipoAtto>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional(readOnly=true)
	public Page<TipoAtto> findAll(Pageable generatePageRequest) {
		Page<TipoAtto> l = tipoAttoRepository.findAll(generatePageRequest);
		for (TipoAtto tipoAtto : l) {
			tipoAtto.setTipiIter(null);
		}
		return l;
	}
	
	@Transactional(readOnly=true)
	public List<TipoAtto> findAll() {
		return tipoAttoRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	public List<TipoAtto> findAllWithStatiAnnullamento() {
		List<TipoAtto> tipiAtto = tipoAttoRepository.findAll();
		if(tipiAtto != null){
			for(TipoAtto tipoAtto : tipiAtto){
				simplifyAnnullamento(tipoAtto);
			}
		}
		return tipiAtto;
	}
	
	
}
