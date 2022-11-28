package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.util.JsonUtil;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Delega;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QDelega;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.repository.DelegaRepository;
import it.linksmt.assatti.service.converter.DelegaConverter;
import it.linksmt.assatti.service.dto.DelegaDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.utility.StringUtil;

/**
 * Service class for managing delega.
 */
@Service
@Transactional
public class DelegaService {

	private final Logger log = LoggerFactory.getLogger(DelegaService.class);
	
	@Inject
	private DelegaRepository delegaRepository;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private AooService aooService;
	
	@Transactional(readOnly = true)
	public  DelegaDto get(Long id) {
		log.debug("DelegaService :: get() :: id:" + id);
		DelegaDto delegaDto = null;
		Delega domain = delegaRepository.findOne(id);
		if(domain != null ){
			delegaDto = DelegaConverter.convertToDto(domain);
		}
		return delegaDto;
	}

	@Transactional
	public  DelegaDto save(DelegaDto delegaDto) {
		Delega delega = DelegaConverter.convertToModel(delegaDto);
		
		if(delega.getProfiloDelegante()!=null) {
			Profilo p = profiloService.findOne(delega.getProfiloDelegante().getId());
			delega.setProfiloDelegante(p);
		}
		
		Set<Profilo> profiliDelegati = null;
		if(delega.getDelegati()!=null) {
			profiliDelegati = new HashSet<>();
			for(Profilo p : delega.getDelegati()) {
				Profilo lp = profiloService.findOne(p.getId());
				if(delega.getId()==null && (lp.getFutureEnabled()==null || lp.getFutureEnabled().equals(false))) {
					throw new GestattiCatchedException("Impossibile inserire come delegato un profilo disabilitato (" + lp.getUtente().getNome() + " " + lp.getUtente().getCognome() + ").");
				}
				profiliDelegati.add(lp);
			}
			delega.setDelegati(profiliDelegati);
		}
		
		if(delega.getId()==null || delega.getId()<=0L) {
			delega.setDataCreazione(LocalDate.now());
			delega.setEnabled(true);
		}
		this.checkUpdate(delega);
		delega  =  delegaRepository.save(delega );
		if(delega!=null) {
			delegaDto = DelegaConverter.convertToDto(delega);
		}
		return delegaDto;
	}
	
	private void checkUpdate(Delega delega) throws GestattiCatchedException{
		if(delega.getEnabled()!=null && delega.getEnabled()) {
			BooleanExpression p = QDelega.delega.enabled.isTrue().and(QDelega.delega.profiloDelegante.id.eq(delega.getProfiloDelegante().getId()));
			if(delega.getId()!=null) {
				p = p.and(QDelega.delega.id.ne(delega.getId()));
			}
			BooleanExpression checkP = QDelega.delega.dataValiditaInizio.between(delega.getDataValiditaInizio(), delega.getDataValiditaFine());
			checkP = checkP.or(QDelega.delega.dataValiditaFine.between(delega.getDataValiditaInizio(), delega.getDataValiditaFine()));
			checkP = checkP.or(QDelega.delega.dataValiditaFine.after(delega.getDataValiditaFine()).and(QDelega.delega.dataValiditaInizio.before(delega.getDataValiditaInizio())));
			long count = delegaRepository.count(p.and(checkP));
			if(count > 0L) {
				throw new GestattiCatchedException("Impossibile inserire la delega.\u003Cbr\u003EEsiste una delega attiva per lo stesso delegante con date che si sovrappongono.\u003Cbr\u003ESi prega di inserire date differenti e riprovare.");
			}
		}
	}
	
	@Transactional(readOnly=true)
	public List<DelegaDto> findAll() {
		return DelegaConverter.convertToDto(delegaRepository.findAll());
	}
	
	@Transactional(readOnly=true)
	public long countAll() {
		return delegaRepository.count();
	}
	
	@Transactional(readOnly=true)
	public List<DelegaDto> findAll(Integer offset, Integer limit, String orderColumn, String orderDirection) {
		List<DelegaDto> listDelega = null;
		if(orderColumn==null || orderColumn.isEmpty()) {
			orderColumn = "dataValiditaFine";
		}
		Pageable pageable = getPageable(offset, limit, orderColumn, orderDirection);
		
		Page<Delega> pDelega = delegaRepository.findAll(pageable);
		if(pDelega!=null) {
			listDelega = new ArrayList<>();
			for(Delega d : pDelega) {
				listDelega.add(DelegaConverter.convertToDto(d));
			}
		}

		return listDelega;
	}
	
	@Transactional(readOnly=true)
	public boolean profiloExistsAsDelegato(Long profiloDelegatoId) {
		BooleanExpression predicate = QDelega.delega.id.isNotNull();
		Profilo profilo = profiloService.findOneBase(profiloDelegatoId);
		predicate = predicate.and(QDelega.delega.delegati.contains(profilo));
		predicate = predicate.and(QDelega.delega.dataValiditaFine.goe(LocalDate.now()));
		predicate = predicate.and(QDelega.delega.enabled.eq(true));
		return delegaRepository.count(predicate) > 0L;
	}
	
	@Transactional(readOnly=true)
	public Page<DelegaDto> search(JsonObject search, Long totalElements, Integer offset, Integer limit, String orderColumn, String orderDirection){
		if(orderColumn==null || orderColumn.isEmpty()) {
			orderColumn = "dataValiditaFine";
		}
		List<DelegaDto> lista = null;
		Page<Delega> page = null;
		Pageable paginazione = getPageable(offset, limit, orderColumn, orderDirection);
		if(search == null){
			page = delegaRepository.findAll(QDelega.delega.id.isNull(), paginazione);
		}else{
			BooleanExpression predicate = QDelega.delega.id.isNotNull();
			if(search.has("profiloAooId") && !search.get("profiloAooId").isJsonNull() && !search.get("profiloAooId").getAsString().isEmpty()){
				List<Aoo> aoos = aooService.getAooRicorsiva(Long.parseLong(search.get("profiloAooId").getAsString()));
				predicate = predicate.and(QDelega.delega.profiloDelegante.aoo.in(aoos).and(QDelega.delega.delegati.isEmpty().or(QDelega.delega.delegati.any().aoo.in(aoos))));
			}
			if(search.has("delegante") && !search.get("delegante").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						QDelega.delega.profiloDelegante.utente.cognome.concat(" ")
						.concat(QDelega.delega.profiloDelegante.utente.nome)
						.concat(" (").concat(QDelega.delega.profiloDelegante.descrizione).concat(")")
						.containsIgnoreCase(search.get("delegante").getAsString().trim()));
			}
			if(search.has("delegato") && !search.get("delegato").getAsString().trim().equalsIgnoreCase("")){
				List<Profilo> delegati = profiloService.findByPredicate(QProfilo.profilo.utente.cognome.concat(" ")
						.concat(QProfilo.profilo.utente.nome)
						.concat(" (").concat(QProfilo.profilo.descrizione).concat(")")
						.containsIgnoreCase(search.get("delegato").getAsString().trim()));
				BooleanExpression delegatiPredicate = null;
				if(delegati!=null) {
					for(Profilo delegato : delegati) {
						if(delegatiPredicate == null) {
							delegatiPredicate = QDelega.delega.delegati.contains(delegato);
						}else {
							delegatiPredicate = delegatiPredicate.or(QDelega.delega.delegati.contains(delegato));
						}
					}
				}
				if(delegatiPredicate!=null) {
					predicate = predicate.and(delegatiPredicate);
				}
			}
			if(search.has("delegatoProfId") && !search.get("delegatoProfId").getAsString().trim().isEmpty()){
				Profilo p = profiloService.findOneBase(search.get("delegatoProfId").getAsLong());
				predicate = predicate.and(QDelega.delega.delegati.contains(p));
			}
			if(JsonUtil.hasFilter(search, "inCorsoOFutura") && search.get("inCorsoOFutura").getAsBoolean()) {
				predicate = predicate.and(QDelega.delega.dataValiditaFine.goe(LocalDate.now()));
				predicate = predicate.and(QDelega.delega.enabled.isTrue());
			}
			if(JsonUtil.hasFilter(search, "validoFilter")) {
				if(StringUtil.trimStr(search.get("validoFilter").getAsString().trim()).equalsIgnoreCase("yes")) {
					predicate = predicate.and(QDelega.delega.dataValiditaInizio.loe(LocalDate.now()).and(QDelega.delega.dataValiditaFine.goe(LocalDate.now())));
				}else if(StringUtil.trimStr(search.get("validoFilter").getAsString().trim()).equalsIgnoreCase("no")) {
					predicate = predicate.and(QDelega.delega.dataValiditaInizio.after(LocalDate.now()).or(QDelega.delega.dataValiditaFine.before(LocalDate.now())));
				}
			}
			if(JsonUtil.hasFilter(search, "enabledFilter")) {
				if(StringUtil.trimStr(search.get("enabledFilter").getAsString().trim()).equalsIgnoreCase("yes")) {
					predicate = predicate.and(QDelega.delega.enabled.isTrue());
				}else if(StringUtil.trimStr(search.get("enabledFilter").getAsString().trim()).equalsIgnoreCase("no")) {
					predicate = predicate.and(QDelega.delega.enabled.isNull().or(QDelega.delega.enabled.isFalse()));
				}
			}
			page = delegaRepository.findAll(predicate, paginazione);
		}
		Page<DelegaDto> dtoPage = null;
		if(page!=null) {
			totalElements = page.getTotalElements();
			lista = new ArrayList<>();
			for(Delega d : page) {
				lista.add(DelegaConverter.convertToDto(d));
			}
		}
		dtoPage = new PageImpl<DelegaDto>(lista, paginazione, page !=null ? page.getTotalElements() : null);
		return dtoPage;
	}
	
	@Transactional(readOnly=true)
	public List<DelegaDto> findByActive(Integer offset, Integer limit, String orderColumn, String orderDirection) {
		if(orderColumn==null || orderColumn.isEmpty()) {
			orderColumn = "data_validita_fine";
		}

		Pageable pageable = getPageable(offset, limit, orderColumn, orderDirection);

		List<Delega> listDelega = delegaRepository.findByActive(pageable);

		return DelegaConverter.convertToDto(listDelega);
	}
	
	@Transactional(readOnly=true)
	public long countByActive() {
		return delegaRepository.countByActive();
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> findByDelegatoActive(Long idDelegato) {
		List<Profilo> deleganti = null;
		
		List<Delega> listDelega = delegaRepository.findByDelegatoActive(idDelegato);
		
		if(listDelega!=null) {
			deleganti = new ArrayList<>();
			for(Delega d : listDelega) {
				deleganti.add(d.getProfiloDelegante());
			}
		}

		return deleganti;
	}
	
	@Transactional
	public void toggle(Long idDelega) {
		Delega del = delegaRepository.findOne(idDelega);
		if ((del.getEnabled() != null) && Boolean.TRUE.equals(del.getEnabled())) {
			del.setEnabled(false);
		}
		else {
			del.setEnabled(true);
		}
		delegaRepository.save(del);
	}
	
	private Pageable getPageable(Integer offset, Integer limit, String orderColumn, String orderDirection) {
		Pageable pageable = null;

		Direction d = null;
		if(orderDirection==null || orderDirection.isEmpty() || orderDirection.equalsIgnoreCase("DESC")) {
			d = Direction.DESC;
		} else {
			d = Direction.ASC;
		}
		
		Sort sort = new Sort(new Order(d, orderColumn));
		pageable = PaginationUtil.generatePageRequest(offset, limit, sort);
		return pageable;
	}
	
}
