package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.QQualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.domain.QUtente;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoRuoloEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.datalayer.repository.UtenteRepository;
import it.linksmt.assatti.service.dto.ProfiloCriteriaDTO;
import it.linksmt.assatti.service.dto.ProfiloSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class ProfiloService {

	private final Logger log = LoggerFactory.getLogger(ProfiloService.class);

	@Inject
	private UtenteRepository utenteRepository;
	@Inject
	private TipoAttoRepository tipoAttoRepository;

	@Inject
	private RuoloRepository ruoloRepository;

	@Inject
	private TipoAttoService tipoAttoService;

	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private AooRepository aooRepository;

	@Inject
	private QualificaProfessionaleRepository qualificaProfessionaleRepository;

	@Inject
	private ValiditaService validitaService;
	@Inject
	private GruppoRuoloService gruppoRuoloService;
	@Inject
	private AooService aooService;
	
	@Inject
	private ConfigurazioneIncaricoProfiloService configurazioneIncaricoProfiloService;
	
	@Transactional
	public void updateInfoSeduta(int ordine, long profiloId, boolean giunta, Boolean valido) {
		Profilo p = profiloRepository.findOne(profiloId);
		if(giunta) {
			p.setOrdineGiunta(ordine);
			p.setValidoSedutaGiunta(valido != null ? valido : false);
		}else {
			p.setOrdineConsiglio(ordine);
			p.setValidoSedutaConsiglio(valido != null ? valido : false);
		}
		profiloRepository.save(p);
	}
	
	@Transactional
	public Profilo updateValiditaSeduta(Profilo p, boolean giunta, Boolean valido) {
		if(giunta) {
			p.setValidoSedutaGiunta(valido != null ? valido : false);
		}else {
			p.setValidoSedutaConsiglio(valido != null ? valido : false);
		}
		return profiloRepository.save(p);
	}
	
	/*
	 * profiloIdDest deve avere tutti i ruoli di processo del profiloIdOrig
	 */
	@Transactional(readOnly=true)
	public boolean areProfiliCompatibili(Long profiloIdDest, Long profiloIdOrig) {
		boolean compatibile = false;
		Profilo profilo1 = profiloRepository.findOne(profiloIdDest);
		Profilo profilo2 = profiloRepository.findOne(profiloIdOrig);
		
		if(profilo1!=null && profilo2!=null) {
			List<Long> ruolo1Ids = new ArrayList<Long>();
			for(Ruolo r : profilo1.getGrupporuolo().getHasRuoli()) {
				if(r.getTipo().toString().equals(TipoRuoloEnum.PROCESSO.toString())) {
					ruolo1Ids.add(r.getId());
				}
			}
			List<Long> ruolo2Ids = new ArrayList<Long>();
			for(Ruolo r : profilo2.getGrupporuolo().getHasRuoli()) {
				if(r.getTipo().toString().equals(TipoRuoloEnum.PROCESSO.toString())) {
					ruolo2Ids.add(r.getId());
				}
			}
			compatibile = ruolo1Ids.containsAll(ruolo2Ids);
		}
		
		return compatibile;
	}
	
	@Transactional(readOnly=true)
	public List<Long> getRuoloIdsByType(Long profiloId, TipoRuoloEnum tipo) {
		List<Long> ids = new ArrayList<Long>();
		Profilo profilo = null;
		if(profiloId !=null) {
			profilo = profiloRepository.findOne(profiloId);
		}
		if(profilo!=null && profilo.getGrupporuolo()!=null && profilo.getGrupporuolo().getHasRuoli()!=null && profilo.getGrupporuolo().getHasRuoli().size() > 0) {
			for(Ruolo r : profilo.getGrupporuolo().getHasRuoli()) {
				if(r!=null && r.getTipo()!=null && r.getTipo().toString().equalsIgnoreCase(tipo.toString())) {
					ids.add(r.getId());
				}
			}
		}
		return ids;
	}
	
	@Transactional(readOnly=true)
	public String getCodiceFiscaleByProfiloId(Long profiloId) {
		Profilo profilo = profiloRepository.findOne(profiloId);
		return profilo.getUtente().getCodicefiscale();
	}
	
	@Transactional(readOnly=true)
	public List<String> findRuoliOfProfile(Long profiloId){
		return profiloRepository.findRuoliOfProfile(profiloId);
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> findByPredicate(BooleanExpression predicate){
		return Lists.newArrayList(profiloRepository.findAll(predicate));
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> getProfiliOfUser(Long utenteId){
		if(utenteId == null){
			return new ArrayList<Profilo>();
		}else{
			BooleanExpression predicate = QProfilo.profilo.utente.id.eq(utenteId);
			Iterable<Profilo> profili = profiloRepository.findAll(predicate);
			for(Profilo prof : profili){
				prof.setAoo(DomainUtil.minimalAoo(prof.getAoo()));
			}
			return Lists.newArrayList(profili);
		}
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> findByRuoloAoo(List<Long> listIdRuolo, List<Long> listIdAoo){
		List<Profilo> pSimplese = profiloRepository.findByRuoloAoo(listIdRuolo, listIdAoo);
		return pSimplese;
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> findProfiloByAooList(Collection<Aoo> aoos){
		Iterable<Profilo> profili = profiloRepository.findAll(QProfilo.profilo.aoo.in(aoos));
		return Lists.newArrayList(profili);
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> findEnabledByRuoloAoo(List<Long> listIdRuolo, List<Long> listIdAoo){
		List<Profilo> pSimplese = profiloRepository.findEnabledByRuoloAoo(listIdRuolo, listIdAoo);
		return pSimplese;
	}
	
	/**
	 * Metodo usato per la riassegnazione di un singolo task e per la riassegnazione da carichi di lavoro. Occorre ottenere quali altri profili abbiano il ruolo necessario al task
	 * e l'aoo del profilo che si sta riassegnando
	 * aooFilter è un filtro su un'aoo (non legata necessariamente al profilo del task) - vengono recuperati i profili che siano di tale aoo o figlie
	 * filterByAooProfiloTask se true vengono restituiti i profili che siano della stessa aoo del profilo che ha in carico il task (completamente slegato dal parametro aooFilter)
	 */
	@Transactional(readOnly=true)
	public List<Profilo> getProfilosRiassegnazioneTask(Long profiloRiassegnazioneId, String tipoAttoCodice, Long aooFilter, Boolean includiQualifiche, Boolean filterByAooProfiloTask){
		Profilo profiloRiassegnazione = profiloRepository.findOne(profiloRiassegnazioneId);
		BooleanExpression predicate = QProfilo.profilo.id.isNotNull().and(QProfilo.profilo.validita.validoal.isNull().or(QProfilo.profilo.validita.validoal.after(new LocalDate())));
		predicate = QProfilo.profilo.futureEnabled.eq(true).and(predicate);
		if(profiloRiassegnazione.getGrupporuolo()!=null && profiloRiassegnazione.getGrupporuolo().getHasRuoli()!=null
		&& profiloRiassegnazione.getGrupporuolo().getHasRuoli().size()>0 && profiloRiassegnazione.getTipiAtto()!=null && profiloRiassegnazione.getTipiAtto().size()>0){
			BooleanExpression ruoloExp = null;
			for(Ruolo ruolo : profiloRiassegnazione.getGrupporuolo().getHasRuoli()) {
				if(ruolo.getTipo().toString().equals(TipoRuoloEnum.PROCESSO.toString())){
					if(ruoloExp==null) {
						ruoloExp = QProfilo.profilo.grupporuolo.hasRuoli.any().eq(ruolo);
					}else {
						ruoloExp = ruoloExp.and(QProfilo.profilo.grupporuolo.hasRuoli.any().eq(ruolo));
					}
				}
			}
			predicate = predicate.and(ruoloExp);
			BooleanExpression predicateTipoAtto = QProfilo.profilo.tipiAtto.contains(tipoAttoService.findByCodice(tipoAttoCodice));
			predicate = predicate.and(predicateTipoAtto);
			predicate = predicate.and(QProfilo.profilo.id.notIn(profiloRiassegnazioneId));
			
			if(aooFilter!=null) {
				List<Aoo> aoos = aooService.getAooRicorsiva(aooFilter);
				predicate = predicate.and(QProfilo.profilo.aoo.in(aoos));
			}
			
			if(filterByAooProfiloTask!=null && filterByAooProfiloTask){
				predicate = predicate.and(QProfilo.profilo.aoo.id.eq(profiloRiassegnazione.getAoo().getId()));
			}
			
		}else{
			predicate = QProfilo.profilo.id.isNull();
		}
		List<Profilo> list = new ArrayList<Profilo>();
		Iterable<Profilo> it = profiloRepository.findAll(predicate);
		for(Profilo p : it) {
			if(p.getAoo()==null) {
				p.setAoo(aooService.findAooByProfiloId(p.getId()));
			}
			list.add(DomainUtil.minimalProfilo(p, includiQualifiche == null ? false : includiQualifiche));
		}
		return list;
	}

	/**
	 * Metodo utilizzato per la disabilitazione completa di un profilo. Occorre trovare i profili attivi che abbiano tutti i ruoli e l'aoo del profilo che si intende disabilitare
	 * @param profiloDisabilitazioneId
	 * @return
	 */
	@Transactional(readOnly=true)
	public List<Profilo> getProfilosRiassegnazione(Long profiloDisabilitazioneId){
		Profilo profiloDisabilitazione = profiloRepository.findOne(profiloDisabilitazioneId);
		BooleanExpression predicate = QProfilo.profilo.id.isNotNull();
		if(profiloDisabilitazione.getAoo()!=null && profiloDisabilitazione.getGrupporuolo()!=null && profiloDisabilitazione.getGrupporuolo().getHasRuoli()!=null
		&& profiloDisabilitazione.getGrupporuolo().getHasRuoli().size()>0 && profiloDisabilitazione.getTipiAtto()!=null && profiloDisabilitazione.getTipiAtto().size()>0){
			predicate = predicate.and(QProfilo.profilo.aoo.eq(profiloDisabilitazione.getAoo())).and(QProfilo.profilo.validita.validoal.isNull().or(QProfilo.profilo.validita.validoal.after(new LocalDate(new Date().getTime()))));
			predicate = QProfilo.profilo.futureEnabled.eq(true).and(predicate);
			BooleanExpression predicateRuolo = null;
			for(Ruolo ruolo : profiloDisabilitazione.getGrupporuolo().getHasRuoli()){
				if(ruolo.getTipo().toString().equals(TipoRuoloEnum.PROCESSO.toString())) {
					if(predicateRuolo==null){
						predicateRuolo = QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo);
					}else{
						predicateRuolo = predicateRuolo.and(QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo));
					}
				}
			}
			predicate = predicate.and(predicateRuolo);
			
			BooleanExpression predicateTipoAtto = null;
			for(TipoAtto tipoAtto : profiloDisabilitazione.getTipiAtto()){
				if(predicateTipoAtto==null){
					predicateTipoAtto = QProfilo.profilo.tipiAtto.contains(tipoAtto);
				}else{
					predicateTipoAtto = predicateTipoAtto.and(QProfilo.profilo.tipiAtto.contains(tipoAtto));
				}
			}
			predicate = predicate.and(predicateTipoAtto);
			predicate = predicate.and(QProfilo.profilo.id.notIn(profiloDisabilitazioneId));
		}else{
			predicate = QProfilo.profilo.id.isNull();
		}
		
		List<Profilo> retVal = Lists.newArrayList(profiloRepository.findAll(predicate));
		return retVal;
	}
	
	@Transactional(readOnly=true)
	public boolean checkIfAlreadyExistsAProfile(final Long aooId, final Long utenteId){
		BooleanExpression predicateProfilo = QProfilo.profilo.id.isNotNull();
		predicateProfilo = predicateProfilo.and(QProfilo.profilo.utente.id.eq(utenteId));
		predicateProfilo = predicateProfilo.and(QProfilo.profilo.aoo.id.eq(aooId));
		if(profiloRepository.count(predicateProfilo) > 0){
			return true;
		}else{
			return false;
		}
	}

	@Transactional(readOnly=true)
	public ResponseEntity<List<Profilo>> search(final ProfiloSearchDTO search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateProfilo = QProfilo.profilo.id.isNotNull();
    	if(search!=null){
			Long idLong = null;
			if(search.getId()!=null && !"".equals(search.getId().trim())){
				try{
					idLong = Long.parseLong(search.getId().trim());
				}catch(Exception e){};
			}
			if(search.getProfiloAooId()!=null && !search.getProfiloAooId().isEmpty()) {
				List<Aoo> aoos = aooService.getAooRicorsiva(Long.parseLong(search.getProfiloAooId()));
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.aoo.in(aoos));
			}
			if(idLong!=null){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.id.eq(idLong));
			}
			if(search.getDescrizione()!=null && !"".equals(search.getDescrizione().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.grupporuolo.denominazione.containsIgnoreCase(search.getDescrizione().trim()));
			}
			if(search.getDelega()!=null && !"".equals(search.getDelega().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.delega.containsIgnoreCase(search.getDelega().trim()));
			}
			log.debug("search ruoli" + search.getRuoli());
			if(search.getRuoli()!=null && !"".equals(search.getRuoli().trim())){
				String[] ruoli = search.getRuoli().split(",");
				BooleanExpression predicateRuoli =  null;

				for(int i =0; i < ruoli.length; i++){
					if(predicateRuoli == null){
						predicateRuoli = QRuolo.ruolo.codice.equalsIgnoreCase(ruoli[i]);
					}
					else{
						predicateRuoli = predicateRuoli.or(QRuolo.ruolo.codice.equalsIgnoreCase(ruoli[i]));
					}

				}
				Iterable<Ruolo> roles = ruoloRepository.findAll(predicateRuoli);
				BooleanExpression internalPredicate = null;
				for(Ruolo q : roles){

					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.grupporuolo.hasRuoli.contains(q);
					}
					else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.grupporuolo.hasRuoli.contains(q));
					}

				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}

			}

			if(search.getTipoAtto()!=null && !"".equals(search.getTipoAtto().trim())){
				BooleanExpression predicateTipiAtto =  QTipoAtto.tipoAtto.descrizione.containsIgnoreCase(search.getTipoAtto().trim());
				Iterable<TipoAtto> tipiAtto = tipoAttoRepository.findAll(predicateTipiAtto, new OrderSpecifier<>(Order.ASC, QTipoAtto.tipoAtto.id));
				BooleanExpression internalPredicate = null;
				for(TipoAtto q : tipiAtto){
					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.tipiAtto.contains(q);
					}else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.tipiAtto.contains(q));
					}
				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}
			}
			if(search.getUtente()!=null && !"".equals(search.getUtente().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.utente.username.containsIgnoreCase(search.getUtente().trim()));
			}
			if(search.getAoo()!=null && !"".equals(search.getAoo().trim())){
				predicateProfilo = predicateProfilo.and(
						((QProfilo.profilo.aoo.codice.concat(" - ").concat(QProfilo.profilo.aoo.descrizione)).containsIgnoreCase(search.getAoo())).or(
								(QProfilo.profilo.aoo.codice.concat("-").concat(QProfilo.profilo.aoo.descrizione)).containsIgnoreCase(search.getAoo()))
				);
			}

			if(search.getQualificaProfessionale()!=null && !"".equals(search.getQualificaProfessionale().trim())){
				BooleanExpression predicateQualificaProfessionale =  QQualificaProfessionale.qualificaProfessionale.denominazione.containsIgnoreCase(search.getQualificaProfessionale().trim());
				predicateQualificaProfessionale =  predicateQualificaProfessionale.and(QQualificaProfessionale.qualificaProfessionale.enabled.eq(true));
				Iterable<QualificaProfessionale> qualificheProfessionali = qualificaProfessionaleRepository.findAll(predicateQualificaProfessionale, new OrderSpecifier<>(Order.ASC, QQualificaProfessionale.qualificaProfessionale.id));
				BooleanExpression internalPredicate = null;
				for(QualificaProfessionale q : qualificheProfessionali){
					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.hasQualifica.contains(q);
					}else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.hasQualifica.contains(q));
					}
				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}
			}
			
			String stato = search.getStato();
			if(stato != null && !"".equals(stato)){

				if("0".equals(stato)){ // Profili attivi
					predicateProfilo = predicateProfilo.and(validitaService.createPredicate(QProfilo.profilo.validita));
				}
				else if("1".equals(stato)){ // Profili disattivati
					predicateProfilo = predicateProfilo.and(validitaService.validAoo(QProfilo.profilo.validita));
				}
				else{ // Tutti

				}

			}
			
			String statoFuture = search.getStatoFuture();
			if(statoFuture != null && !"".equals(statoFuture)){

				if("0".equals(statoFuture)){ // Profili attivi
					predicateProfilo = predicateProfilo.and(QProfilo.profilo.futureEnabled.eq(true).and(validitaService.createPredicate(QProfilo.profilo.validita)));
				}
				else if("1".equals(statoFuture)){ // Profili disattivati
					predicateProfilo = predicateProfilo.and(QProfilo.profilo.futureEnabled.eq(false).and(validitaService.createPredicate(QProfilo.profilo.validita)));
				}
				else{ // Tutti

				}

			}
		}
        Page<Profilo> page = this.findAll(predicateProfilo, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profilos", offset, limit);
        loadLazy(page.getContent());
        return new ResponseEntity<List<Profilo>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional(readOnly=true)
	public List<Profilo> searchList(final ProfiloSearchDTO search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateProfilo = QProfilo.profilo.id.isNotNull();
    	if(search!=null){
			Long idLong = null;
			if(search.getId()!=null && !"".equals(search.getId().trim())){
				try{
					idLong = Long.parseLong(search.getId().trim());
				}catch(Exception e){};
			}
			if(idLong!=null){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.id.eq(idLong));
			}
			if(search.getDescrizione()!=null && !"".equals(search.getDescrizione().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.grupporuolo.denominazione.containsIgnoreCase(search.getDescrizione().trim()));
			}
			if(search.getDelega()!=null && !"".equals(search.getDelega().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.delega.containsIgnoreCase(search.getDelega().trim()));
			}
			log.debug("search ruoli" + search.getRuoli());
			if(search.getRuoli()!=null && !"".equals(search.getRuoli().trim())){
				String[] ruoli = search.getRuoli().split(",");
				BooleanExpression predicateRuoli =  null;

				for(int i =0; i < ruoli.length; i++){
					if(predicateRuoli == null){
						predicateRuoli = QRuolo.ruolo.codice.equalsIgnoreCase(ruoli[i]);
					}
					else{
						predicateRuoli = predicateRuoli.or(QRuolo.ruolo.codice.equalsIgnoreCase(ruoli[i]));
					}

				}
				Iterable<Ruolo> roles = ruoloRepository.findAll(predicateRuoli);
				BooleanExpression internalPredicate = null;
				for(Ruolo q : roles){

					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.grupporuolo.hasRuoli.contains(q);
					}
					else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.grupporuolo.hasRuoli.contains(q));
					}

				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}

			}

			if(search.getTipoAtto()!=null && !"".equals(search.getTipoAtto().trim())){
				BooleanExpression predicateTipiAtto =  QTipoAtto.tipoAtto.descrizione.containsIgnoreCase(search.getTipoAtto().trim());
				Iterable<TipoAtto> tipiAtto = tipoAttoRepository.findAll(predicateTipiAtto, new OrderSpecifier<>(Order.ASC, QTipoAtto.tipoAtto.id));
				BooleanExpression internalPredicate = null;
				for(TipoAtto q : tipiAtto){
					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.tipiAtto.contains(q);
					}else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.tipiAtto.contains(q));
					}
				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}
			}
			if(search.getUtente()!=null && !"".equals(search.getUtente().trim())){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.utente.username.containsIgnoreCase(search.getUtente().trim()));
			}
			if(search.getAoo()!=null && !"".equals(search.getAoo().trim())){
				predicateProfilo = predicateProfilo.and(
						((QProfilo.profilo.aoo.codice.concat(" - ").concat(QProfilo.profilo.aoo.descrizione)).containsIgnoreCase(search.getAoo())).or(
								(QProfilo.profilo.aoo.codice.concat("-").concat(QProfilo.profilo.aoo.descrizione)).containsIgnoreCase(search.getAoo()))
				);
			}
			
			if(search.getAooAttiva() != null && "0".equals(search.getAooAttiva())){
				predicateProfilo = predicateProfilo.and(validitaService.createPredicate(QProfilo.profilo.aoo.validita));
			}

			if(search.getQualificaProfessionale()!=null && !"".equals(search.getQualificaProfessionale().trim())){
				BooleanExpression predicateQualificaProfessionale =  QQualificaProfessionale.qualificaProfessionale.denominazione.containsIgnoreCase(search.getQualificaProfessionale().trim());
				predicateQualificaProfessionale =  predicateQualificaProfessionale.and(QQualificaProfessionale.qualificaProfessionale.enabled.eq(true));
				Iterable<QualificaProfessionale> qualificheProfessionali = qualificaProfessionaleRepository.findAll(predicateQualificaProfessionale, new OrderSpecifier<>(Order.ASC, QQualificaProfessionale.qualificaProfessionale.id));
				BooleanExpression internalPredicate = null;
				for(QualificaProfessionale q : qualificheProfessionali){
					if(internalPredicate == null){
						internalPredicate = QProfilo.profilo.hasQualifica.contains(q);
					}else{
						internalPredicate = internalPredicate.or(QProfilo.profilo.hasQualifica.contains(q));
					}
				}
				if(internalPredicate!=null){
					predicateProfilo = predicateProfilo.and(internalPredicate);
				}
			}
			
			String stato = search.getStato();
			if(stato != null && !"".equals(stato)){

				if("0".equals(stato)){ // Profili attivi
					predicateProfilo = predicateProfilo.and(validitaService.createPredicate(QProfilo.profilo.validita));
				}
				else if("1".equals(stato)){ // Profili disattivati
					predicateProfilo = predicateProfilo.and(validitaService.validAoo(QProfilo.profilo.validita));
				}
				else{ // Tutti

				}

			}
		}
        Page<Profilo> page = this.findAll(predicateProfilo, PaginationUtil.generatePageRequest(offset, limit));
        loadLazy(page.getContent());
        return page.getContent();
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> getAllActive(String profiloAooId){
		BooleanExpression p = QProfilo.profilo.validita.validoal.isNull();
		p = QProfilo.profilo.futureEnabled.eq(true).and(p);
		if(profiloAooId!=null && !profiloAooId.isEmpty()) {
			List<Aoo> aoos = aooService.getAooRicorsiva(Long.parseLong(profiloAooId));
			p = p.and(QProfilo.profilo.aoo.in(aoos));
		}
		List<Profilo> l = Lists.newArrayList(profiloRepository.findAll(p));
		loadLazy(l);
		return l;
	}

	@Transactional(readOnly=false)
	public void disableProfilo(final Long id) throws GestattiCatchedException{
		log.debug("disableProfilo idprofilo" + id);
		Profilo profilo = profiloRepository.findOne(id);
		if(profilo!=null){
			if(profilo.getValidita()==null){
				profilo.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			profilo.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			profiloRepository.save(profilo);
		}
	}

	@Transactional(readOnly=false)
	public void enableProfilo(final Long id) throws GestattiCatchedException{
		log.debug("enableProfilo idprofilo" + id);
		Profilo profilo = profiloRepository.findOne(id);
		if(profilo!=null){
			if(profilo.getValidita()==null){
				profilo.setValidita(new Validita());
			}
			profilo.getValidita().setValidoal(null);
			profiloRepository.save(profilo);
		}
	}
	
	@Transactional(readOnly=false)
	public void futureEnableDisable(final Long profiloId, final boolean isEnabling) throws GestattiCatchedException{
		log.debug("futureEnableDisable idprofilo" + profiloId);
		Profilo profilo = profiloRepository.findOne(profiloId);
		if(profilo!=null){
			if(isEnabling) {
				profilo.setFutureEnabled(true);
			}else {
				profilo.setFutureEnabled(false);
			}
			profiloRepository.save(profilo);
		}
	}

	@Transactional(readOnly = true)
	public String getUsernameByProfiloId(Long profiloId) {
		String username = null;
		Profilo p = profiloRepository.findOne(profiloId);
		if(p!=null) {
			username = p.getUtente().getUsername();
		}
		return username;
	}
	
	@Transactional(readOnly = true)
	public List<Utente> findUsers(final ProfiloCriteriaDTO criteria) {
		log.debug("findUsers criteria:" + criteria);

		if (criteria.getHasProfilo()) {
			BooleanExpression predicateProfilo = QProfilo.profilo.aoo.id.eq(criteria.getAooId());

			
			List<Utente> utenti = new ArrayList<Utente>();
			
			
			if (criteria.getUserName() != null) {
				predicateProfilo = predicateProfilo
						.and(QProfilo.profilo.utente.username.contains(criteria.getUserName()));
				
				utenti = utenteRepository.findByHasProfiliAooIdAndUsernameOrderByUsernameAsc(criteria.getAooId(), criteria.getUserName());
				
			}else {
				utenti = utenteRepository.findByHasProfiliAooIdOrderByUsernameAsc(criteria.getAooId());
			}

			
//			TODO FIXME Trovare alternativa
			
//			JPAQuery queryCount = new JPAQuery(entityManager);
//			List<Utente> utenti = queryCount.from(QProfilo.profilo).where(predicateProfilo)
//					.groupBy(QProfilo.profilo.utente).orderBy(QProfilo.profilo.utente.username.asc())
//					.list(QProfilo.profilo.utente);
//
			
			

			return utenti;
			
		} else {
			BooleanExpression predicate = QUtente.utente.id.gt(0);
			// if (criteria.getValido()) {
			// LocalDate right = new LocalDate();
			// predicate = predicate.and(
			// QUtente.utente.validita.validodal.isNull().or(
			// QUtente.utente.validita.validodal.goe(right)))
			// .and(QUtente.utente.validita.validoal.isNull().or(
			// QUtente.utente.validita.validoal.loe(right)));
			// }

			if (criteria.getUserName() != null) {
				predicate = predicate.and(QUtente.utente.username.contains(criteria.getUserName()));
			}

			Iterable<Utente> utenti = utenteRepository.findAll(predicate, QUtente.utente.username.asc());

			return Lists.newArrayList(utenti);
		}

	}

	@Transactional(readOnly = true)
	public Profilo findOneEssential(final Long id) {
		Profilo prof = profiloRepository.findOne(id);
		Profilo ret = new Profilo();
		if(prof.getAoo()!=null) {
			ret.setAoo(new Aoo(prof.getAoo().getId(), prof.getAoo().getDescrizione(), prof.getAoo().getCodice(), null));
		}
		ret.setId(prof.getId());
		ret.setDescrizione(prof.getDescrizione());
		ret.setValidita(prof.getValidita());
		if(prof.getUtente()!=null) {
			ret.setUtente(new Utente(prof.getUtente().getId(), prof.getUtente().getCodicefiscale(), prof.getUtente().getUsername(), prof.getUtente().getCognome(), prof.getUtente().getNome()));
		}
		return ret;
	}
	
	@Transactional(readOnly = true)
	public boolean profiloHasConfIncaricoOnAttiAttivi(Long profiloId) {
		long count = configurazioneIncaricoProfiloService.countConfIncaricoByProfiloAttiAttivi(profiloId);
		return count > 0L;
	}
	
	@Transactional(readOnly = true)
	public Profilo findOneBase(final Long id) {
		Profilo prof = profiloRepository.findOne(id);
		return prof;
	}
	
	@Transactional(readOnly = true)
	public Profilo findOneEssentialDet(final Long id) {
		Profilo ret = new Profilo();
		Profilo prof = profiloRepository.findOne(id);
		if (prof != null) {
			ret.setDelega(prof.getDelega());
			ret.setDescrizione(prof.getDescrizione());
			ret.setId(prof.getId());
			ret.setPredefinito(prof.getPredefinito());
			ret.setUtente(prof.getUtente());
			ret.setValidita(prof.getValidita());
		}
		return ret;
	}
	
	@Transactional(readOnly = true)
	public Profilo findOne(final Long id) {
		Profilo prof = profiloRepository.findOne(id);
		if (prof != null) {
			log.debug(prof.getGrupporuolo() + "");
			log.debug(prof.getAoo() + "");

			if (prof.getUtente() != null) {
				prof.getUtente().getUsername();
			}
			prof.getAoo().getSpecializzazione();
			if (prof.getAoo() != null && prof.getAoo().getCodice() == null) {
				aooService.findOne(prof.getAoo().getId());
				// prof.setAoo(DomainUtil.minimalAoo(prof.getGrupporuolo().getAoo()));
			}

			if (prof.getGrupporuolo() != null) {
				prof.getGrupporuolo().getDenominazione();

				// prof.getGrupporuolo().setAoo(DomainUtil.minimalAoo(prof.getGrupporuolo().getAoo()));
				if (prof.getGrupporuolo().getHasRuoli() != null) {
					for (Ruolo ruolo : prof.getGrupporuolo().getHasRuoli()) {
						ruolo.getDescrizione();
					}
				} else {
					GruppoRuolo grRuolo = gruppoRuoloService.findOne(prof.getGrupporuolo().getId());
					prof.setGrupporuolo(grRuolo);
				}
			}

//			if (prof.getTipoAtto() != null) {
//				TipoAtto tipoAtto = tipoAttoRepository.getOne(prof.getTipoAtto().getId());
//				prof.setTipoAtto(tipoAtto);
//
//			}

			if (prof.getHasQualifica() != null) {
				for (QualificaProfessionale qualificaprofessionale : prof.getHasQualifica()) {
					qualificaprofessionale.getDenominazione();
					log.debug(String.format("Profilo id:%s ha qualifica %s.", id, qualificaprofessionale.getDenominazione()));
				}
			} else {
				log.debug(String.format("Profilo con id:%s ha hasQualifica NULL!!", id));
			}
			if(prof!=null && prof.getUtente()!=null && prof.getUtente().getId()!=null) {
				Utente u = utenteRepository.getOne(prof.getUtente().getId());
				prof.setUtente(u);
			}
			prof.setAoo(DomainUtil.minimalAoo(prof.getAoo()));
		}
		return prof;
	}
	
	@Transactional(readOnly = true)
	public Profilo findOneRicerca(final Long id) {
		Profilo prof = profiloRepository.findOne(id);
		if (prof != null) {
			if(prof.getAoo()!=null) {
				prof.getAoo().getId();
			}
		}
		return prof;
	}

	@Transactional(readOnly = true)
	public Iterable<Profilo> findProfilo(final String q) {
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.descrizione.containsIgnoreCase(q);
		predicateProfilo = predicateProfilo.or(QProfilo.profilo.utente.username.containsIgnoreCase(q));
		predicateProfilo = predicateProfilo.or(QProfilo.profilo.utente.cognome.containsIgnoreCase(q));
		predicateProfilo = predicateProfilo.or(QProfilo.profilo.utente.nome.containsIgnoreCase(q));
		predicateProfilo = predicateProfilo.or(QProfilo.profilo.descrizione.containsIgnoreCase(q));

		Iterable<TipoAtto> tipiAtto = tipoAttoService.findByPredicate(QTipoAtto.tipoAtto.descrizione.containsIgnoreCase(q));
		BooleanExpression tipiAttoExpression = null;
		for(TipoAtto tipoAtto : tipiAtto){
			if(tipiAttoExpression==null){
				tipiAttoExpression = QProfilo.profilo.tipiAtto.contains(tipoAtto);
			}else{
				tipiAttoExpression = tipiAttoExpression.or(QProfilo.profilo.tipiAtto.contains(tipoAtto));
			}
		}
		if(tipiAttoExpression != null){
			predicateProfilo = predicateProfilo.or(tipiAttoExpression);
		}

		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		loadLazy(l);

		return l;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> getEmananti(final Long aooId, final String tipoAttoCodice) {
		String role ="ROLE_SOTTOSCRITTORE_EMANANTE";
		Ruolo ruolo = ruoloRepository.findAll(QRuolo.ruolo.codice.eq(role)).iterator().next();
		TipoAtto tipoAtto = tipoAttoService.findByCodice(tipoAttoCodice);
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.id.eq(aooId).and(QProfilo.profilo.validita.validoal.isNull()).and(QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo)).and(QProfilo.profilo.tipiAtto.contains(tipoAtto));
		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		loadLazy(l);
		List<Profilo> profili = Lists.newArrayList(l);
		return profili;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findByAooId(final Long aooId) {
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.id.eq(aooId).and(QProfilo.profilo.validita.validoal.isNull());
//		List<Profilo> l = profiloRepository.findByAooId(aooId);
		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		loadLazy(l);
		List<Profilo> profili = Lists.newArrayList(l);
		return profili;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> getByAooIdRicorsive(final Long aooId) {
		List<Aoo> aoos = aooService.getAooRicorsiva(aooId);
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.in(aoos).and(QProfilo.profilo.validita.validoal.isNull());
		predicateProfilo = QProfilo.profilo.futureEnabled.eq(true).and(predicateProfilo);
		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		List<Profilo> profili = new ArrayList<Profilo>();
		for(Profilo p : l) {
			if(p.getAoo()==null) {
				p.setAoo(aooService.findAooByProfiloId(p.getId()));
			}
			profili.add(DomainUtil.minimalProfilo(p));
		}
		return profili;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findByAooIdAndTipoAtto(final Long aooId,final String codice, boolean parere) {
		// , String tipoParere
		
		BooleanExpression predicateProfilo = null;
		if(!parere){
			predicateProfilo = QProfilo.profilo.aoo.id.eq(aooId).and(QProfilo.profilo.validita.validoal.isNull());
			if(codice!=null && !codice.isEmpty()){
				predicateProfilo = predicateProfilo.and(QProfilo.profilo.tipiAtto.contains(tipoAttoService.findByCodice(codice)));
			}
		}else{
			predicateProfilo = QProfilo.profilo.validita.validoal.isNull();
		}
		BooleanExpression predicateProfilo2 = null;
//		List<Profilo> l = profiloRepository.findByAooId(aooId);
		if(!parere){
			List<Ruolo> ruolos = ruoloRepository.findByCodice("ROLE_SOTTOSCRITTORE_PROPOSTA");
			log.debug("ruoliSottoscrittori.size="+ruolos.size());
			
			for (Ruolo ruolo2 : ruolos) {
				if(predicateProfilo2 == null) {
					predicateProfilo2 = QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo2);
				} else {
					predicateProfilo2 = predicateProfilo2.or(QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo2));
				}
			}
		}
		else{
			List<Ruolo> ruolos = new ArrayList<Ruolo>();
			/*
			 * TODO: in ATTICO non utilizzato
			 *
			if(codice.equals("DIR") && tipoParere!=null && tipoParere.equalsIgnoreCase("Parere Contabile")) {
				ruolos = ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_CONTABILE_RAGIONERIA_DIRIGENZIALE");
			}else if(codice.equals("DIR") && tipoParere!=null && tipoParere.equalsIgnoreCase("Parere Organo di Controllo")){
				ruolos = ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_PARERE_ORGANO_CONTROLLO");
			}else if(!codice.equals("DIR") && tipoParere!=null && tipoParere.equalsIgnoreCase("Parere Contabile")){				
				ruolos = ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_CONTABILE_RAGIONERIA_GIUNTA");
				//ruolos.addAll(ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_AMMINISTRATIVO_GIUNTA"));
			}else if(!codice.equals("DIR") && tipoParere!=null && tipoParere.equalsIgnoreCase("Parere Amministrativo")){				
				ruolos = ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_AMMINISTRATIVO_GIUNTA");
				
			}else if(!codice.equals("DIR") && tipoParere!=null && tipoParere.equalsIgnoreCase("Controdeduzioni")){				
				ruolos = ruoloRepository.findByLikeCodice("SOTTOSCRITTORE_PROPOSTA_GIUNTA");
				
			}
			else{
				ruolos = new ArrayList<Ruolo>();
			}
			*/
			
			for (Ruolo ruolo : ruolos) {
				if(predicateProfilo2 == null) {
					predicateProfilo2 = QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo);
				}
				else {
					predicateProfilo2 = predicateProfilo2.or(QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolo));
				}
			}
		}
		List<Profilo> profili = null;
		if(predicateProfilo!=null && predicateProfilo2!=null){
			predicateProfilo = predicateProfilo.and(predicateProfilo2);
			Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
			log.debug("ruoliSottoscrittori.size=",l);
			loadLazy(l);
			profili = Lists.newArrayList(l);
		}
		else{
			profili = new ArrayList<Profilo>();
		}
		return profili;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findByAooIdAndTipoAttoForGiunta(final Long aooId,final String codice, boolean parere, String tipoParere) {
		List<Profilo> profili = this.findByAooIdAndTipoAtto(aooId, codice, parere);
		// , tipoParere
		
		log.debug("Aggiunta del direttore di dipartimento e/o di sezione");
		Aoo aooTemp = aooRepository.findOne(aooId);
		
		if(aooTemp.getProfiloResponsabileId() != null 
				&& this.findProfilo(aooTemp.getProfiloResponsabileId(), profili) == false){
			Profilo profilo = this.findOne(aooTemp.getProfiloResponsabileId());
			loadLazyGiunta(profilo,aooTemp);
			profili.add(profilo);
		}
		
		if(aooTemp.getAooPadre() != null && aooTemp.getAooPadre().getProfiloResponsabileId() != null 
				&& this.findProfilo(aooTemp.getAooPadre().getProfiloResponsabileId(), profili) == false) {
			Profilo profilo = this.findOne(aooTemp.getAooPadre().getProfiloResponsabileId());
			loadLazyGiunta(profilo,aooTemp.getAooPadre());
			profili.add(profilo);
		}
		
		return profili;
	}
	
	protected boolean findProfilo(Long id, List<Profilo> profili) {
		
		for(Profilo temp : profili){
			if(temp.getId().equals(id)){
				return true;
			}
		}
		
		return false;
	}
	
	private void loadLazyGiunta(final Profilo profilo,Aoo aoo) {
		
		if(aoo!= null ){
			Aoo aooMin = new Aoo();
			aooMin.setId(aoo.getId());
			aooMin.setCodice(aoo.getCodice());
			aooMin.setDescrizione(aoo.getDescrizione());
			aooMin.setIdentitavisiva(aoo.getIdentitavisiva());
			aooMin.setProfiloResponsabileId(aoo.getProfiloResponsabileId());
			aooMin.setTipoAoo(aoo.getTipoAoo());
			aoo = aooMin;
		}
		profilo.setAoo(aoo);

		if (profilo.getGrupporuolo() != null) {
			profilo.setGrupporuolo(
					new GruppoRuolo(profilo.getGrupporuolo().getId(), profilo.getGrupporuolo().getDenominazione(),profilo.getGrupporuolo().getHasRuoli()));
		}
		if (profilo.getHasQualifica() != null) {
			HashSet<QualificaProfessionale> disabled = new HashSet<QualificaProfessionale>();
			for (QualificaProfessionale qualificaprofessionale : profilo.getHasQualifica()) {
				if(qualificaprofessionale.getEnabled() == null || qualificaprofessionale.getEnabled() == false){
					disabled.add(qualificaprofessionale);
				}else{
				qualificaprofessionale.getDenominazione();
//				qualificaprofessionale.setAoo(null);
				}
			}
			profilo.getHasQualifica().removeAll(disabled);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findForControdeduzioni(final Long[] aoosId) {
		List<Ruolo> ruolos = ruoloRepository.findByLikeCodice("ROLE_SOTTOSCRITTORE_PROPOSTA");
		BooleanExpression predicateProfilo = QProfilo.profilo.validita.validoal.isNull();
		if(ruolos.size() > 0){
			predicateProfilo = predicateProfilo.and(QProfilo.profilo.grupporuolo.hasRuoli.contains(ruolos.get(0)));
		}
		
		List<Long> resp = new ArrayList<Long>();
		for(long id : aoosId){
			Aoo aoo = aooRepository.findOne(id);
			for(Assessorato ass : aoo.getHasAssessorati()){
				resp.add(ass.getProfiloResponsabileId());
			}
		}
		
		List<Profilo> profili = new ArrayList<Profilo>();
		
		predicateProfilo = predicateProfilo.and(QProfilo.profilo.aoo.id.in(aoosId));
		if(resp.size() > 0){
			predicateProfilo = predicateProfilo.or(QProfilo.profilo.id.in(resp));
		}
		
		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		log.debug("Profili Controdeduzioni size=",l);
		loadLazy(l);
		profili = Lists.newArrayList(l);
		
		return profili;
	}

	@Transactional(readOnly = true)
	public List<Profilo> findByAoos(final List<Aoo> aoos) {
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.in(aoos).and(QProfilo.profilo.validita.validoal.isNull());
//		List<Profilo> l = profiloRepository.findByAooId(aooId);
		Iterable<Profilo> l = profiloRepository.findAll(predicateProfilo);
		loadLazy(l);
		List<Profilo> profili = Lists.newArrayList(l);
		return profili;
	}

	@Transactional(readOnly = true)
	public Iterable<Profilo> findByAooIdAndValidi(final Long aooId) {
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.id.eq(aooId);
		predicateProfilo = predicateProfilo.and(validitaService.createPredicate(QProfilo.profilo.validita));

		Iterable<Profilo> profili = profiloRepository.findAll(predicateProfilo);
		loadLazy(profili);
		return profili;
	}

	private void loadLazy(final Iterable<Profilo> l) {
		for (Profilo profilo : l) {

			profilo.setAoo(DomainUtil.minimalAoo(profilo.getAoo()));

			if (profilo.getGrupporuolo() != null) {
				profilo.setGrupporuolo(
						new GruppoRuolo(profilo.getGrupporuolo().getId(), profilo.getGrupporuolo().getDenominazione(),profilo.getGrupporuolo().getHasRuoli()));
			}
			if (profilo.getHasQualifica() != null) {
//				HashSet<QualificaProfessionale> disabled = new HashSet<QualificaProfessionale>();
				for (QualificaProfessionale qualificaprofessionale : profilo.getHasQualifica()) {
//					if(qualificaprofessionale.getEnabled() == null || qualificaprofessionale.getEnabled() == false){
//						disabled.add(qualificaprofessionale);
//					}else{
					qualificaprofessionale.getDenominazione();
//					qualificaprofessionale.setAoo(null);
//					}
				}
//				profilo.getHasQualifica().removeAll(disabled);
			}

		}
	}

	@Transactional(readOnly = true)
	public List<Profilo> findActiveByUtenteAooTipoatto(final Long userId, final Long aooId, final Long tipoAttoId){
		return profiloRepository.findActiveByUtenteAooTipoatto(userId, aooId, tipoAttoId);
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findActiveByUsername(final String username){
		List<Profilo> listaProfili = profiloRepository.findActiveByUsername(username);
		return listaProfili;
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findActiveByAooIds(final List<Long> aooIds){
		BooleanExpression predicate = QProfilo.profilo.aoo.id.in(aooIds).and(QProfilo.profilo.validita.validoal.isNull()).and(QProfilo.profilo.aoo.validita.validoal.isNull());
		return Lists.newArrayList(profiloRepository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public List<BigInteger> findActiveIdsByUtenteId(final Long idUtente){
		return profiloRepository.findActiveIdsByUtenteId(idUtente);
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findActiveByUtenteId(final Long idUtente){
		return profiloRepository.findActiveByUtenteId(idUtente);
	}

	@Transactional(readOnly = true)
	public Page<Profilo> findAll(final BooleanExpression predicateProfilo, final Pageable generatePageRequest) {
		Page<Profilo> l = profiloRepository.findAll(predicateProfilo, generatePageRequest);
		loadLazy(l);
		return l;
	}

	public Profilo save(Profilo profilo) throws GestattiCatchedException {
		profiloRepository.save(profilo);
		profilo = this.findOne(profilo.getId());
		
		return profilo;
	}

	@Transactional
	public void delete(final Long id) throws GestattiCatchedException {
		Profilo profilo = this.findOne(id);
		if (profilo.getValidita() == null) {
			profilo.setValidita(new Validita());
		}

		profilo.getValidita().setValidoal(new LocalDate());
		profiloRepository.delete(profilo);
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findActiveByGruppoRuolo(final String nomeGruppoRuolo){
		return profiloRepository.findActiveByGruppoRuolo(nomeGruppoRuolo);
	}
	
	@Transactional(readOnly = true)
	public List<Profilo> findActiveByGruppoRuoloId(final Long gruppoRuoloid){
		return profiloRepository.findActiveByGruppoRuoloId(gruppoRuoloid);
	}

}
