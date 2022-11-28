package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.datalayer.domain.Materia;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.ProgressivoAdozione;
import it.linksmt.assatti.datalayer.domain.ProgressivoProposta;
import it.linksmt.assatti.datalayer.domain.QAoo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.domain.TipoMateria;
import it.linksmt.assatti.datalayer.domain.Ufficio;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicRicercaDto;
import it.linksmt.assatti.datalayer.domain.dto.AooDto;
import it.linksmt.assatti.datalayer.domain.util.AooTransformer;
import it.linksmt.assatti.datalayer.repository.AooBasicRepository;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AssessoratoRepository;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AooService {
	private static final int MAX_LEV = 10;
	
	@Inject
	private AooBasicRepository aooBasicRepository;

	@Inject
	private AooRepository aooRepository;

	@Inject
	private ProfiloService profiloService;

	@Inject
	private ValiditaService validitaService;

	@Inject
	private UfficioService ufficioService;

	@Inject
	private TipoMateriaService tipoMateriaService;

	@Inject
	private RubricaDestinatarioEsternoService rubricaDestinatarioEsternoService;

	@Inject
	private CodiceProgressivoService codiceProgressivoService;

	@Inject
	private IndirizzoService indirizzoService;

	@Inject
	private AssessoratoRepository assessoratoRepository;
	
	private final Logger log = LoggerFactory.getLogger(AooService.class);
	
	@Transactional(readOnly = true)
	public AooBasicDto getBasicAoo(Long aooId) {
		return aooBasicRepository.getAooBasic(aooId);
	}
	
	@Transactional(readOnly = true)
	public List<Aoo> findAooByNativeQuery(String query){
		List<BigInteger> ids = aooBasicRepository.findAooIdsByNativeQuery(query);
		List<Aoo> list = new ArrayList<Aoo>();
		if(ids!=null && ids.size() > 0) {
			List<Long> ln = new ArrayList<Long>();
			for(BigInteger bi : ids) {
				ln.add(bi.longValue());
			}
			Iterable<Aoo> aoos = aooRepository.findAll(ln);
			for(Aoo aoo : aoos) {
				list.add(DomainUtil.simpleAoo(aoo));
			}
		}
		return list;
	}
	
	@Transactional(readOnly = true)
	public List<Aoo> findAoosDirigente(Long utenteId){
		List<Profilo> profs = profiloService.findActiveByUtenteId(utenteId);
		Set<Aoo> aooDir = new HashSet<Aoo>();
		for(Profilo p : profs) {
			if(p.getAoo().getProfiloResponsabileId()!=null && p.getAoo().getProfiloResponsabileId().equals(p.getId())) {
				aooDir.add(DomainUtil.simpleAoo(p.getAoo()));
			}
		}
		return Lists.newArrayList(aooDir);
	}
	
	@Transactional(readOnly = true)
	public List<Long> searchAooIdsByCodiceDescrizione(String search){
		BooleanExpression predicateAoo = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(search)).or(
				(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(search));
		Iterable<Aoo> aoos = aooRepository.findAll(predicateAoo);
		List<Long> ids = new ArrayList<Long>();
		for(Aoo aoo : aoos){
			ids.add(aoo.getId());
		}
		return ids;
	}
	
	@Transactional(readOnly = true)
	public Aoo findMinimalAooById(Long aooId){
		return DomainUtil.simpleAoo(aooRepository.findOne(aooId));
	}
	
	@Transactional(readOnly = true)
	public List<Aoo> findGerarchiaAooByProfiloId(Long profiloId){
		List<Aoo> gerarchia = new ArrayList<Aoo>();
		Aoo aoo = aooRepository.findAooByProfiloId(profiloId);
		gerarchia.add(aoo);
		this.addAooPadre(aoo, gerarchia);
		return gerarchia;
	}
	
	@Transactional(readOnly = true)
	public List<Aoo> findGerarchiaOfAoo(Long aooId){
		List<Aoo> gerarchia = new ArrayList<Aoo>();
		Aoo aoo = aooRepository.findOne(aooId);
		gerarchia.add(aoo);
		this.addAooPadre(aoo, gerarchia);
		return gerarchia;
	}
	
	@Transactional(readOnly = true)
	public Aoo findAooByProfiloId(Long profiloId){
		return aooRepository.findAooByProfiloId(profiloId);
	}
	
	private void addAooPadre(Aoo aoo, List<Aoo> gerarchia){
		if(aoo.getAooPadre()!=null){
			gerarchia.add(aoo.getAooPadre());
			addAooPadre(aoo.getAooPadre(), gerarchia);
		}
	}
	
	@Transactional(readOnly = true)
	public List<Aoo> getGerarchiaAoo(Aoo aoo){
		List<Aoo> gerarchia = new ArrayList<Aoo>();
		Long id = aoo !=null && aoo.getAooPadre() != null && aoo.getAooPadre().getId()!=null ? aoo.getAooPadre().getId(): 0L;
		gerarchia.add(aoo);
		this.addAooPadre(aoo, gerarchia);
		return gerarchia;
	}
	
	@Transactional(readOnly=true)
	public Aoo getAooConTipoAoo(Long aooId){
		Aoo aoo = aooRepository.findOne(aooId);
		TipoAoo tipoAoo = aoo.getTipoAoo(); //read lazy info
		return aoo;
	}
	
	@Transactional(readOnly=true)
	public Iterable<Aoo> findByCodice(String codice){
		if(codice!=null && !codice.isEmpty()){
			return aooRepository.findAll(QAoo.aoo.codice.eq(codice));
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly=true)
	public AooBasicDto getAooDirezioneBasic(Long aooId) {
		AooBasicDto dto = null;
		Aoo retVal = this.getAooDirezione(aooId);
		if(retVal != null) {
			dto = new AooBasicDto(retVal.getId(), retVal.getCodice(), retVal.getDescrizione());
		}
		return dto;
	}
	
	@Transactional(readOnly=true)
	public Aoo getAooDirezione(Long aooId) {
		Aoo retVal = null;
		if(aooId!=null && aooId.longValue() > 0L) {
			Aoo aoo = aooRepository.findOne(aooId);
			retVal = this.getAooDirezione(aoo);
		}
		return retVal;
	}
	
	@Transactional(readOnly=true)
	public List<AooBasicDto> getDirezioniBasic(){
		return aooBasicRepository.getDirezioniBasic();
	}
	
	@Transactional(readOnly=true)
	public List<AooBasicRicercaDto> getAllDirezioniBasic(){
		return aooBasicRepository.getAllDirezioniBasic();
	}
	
	@Transactional(readOnly=true)
	public Aoo getAooDirezione(Aoo aoo) {
		Aoo retVal = null;
		if(aoo!=null) {
			if(aoo.getTipoAoo() == null) {
				aoo = this.findEnabledByCodice(aoo.getCodice());
			}
			if (aoo != null) {
				if(aoo.getTipoAoo()!=null && aoo.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
					return aoo;
				}else {
					retVal = this.getAooDirezione(aoo.getAooPadre());
				}
			}else return retVal;
		}
		return retVal;
	}
	
	@Transactional(readOnly=true)
	public Aoo findEnabledByCodice(String codice){
		if(codice!=null && !codice.isEmpty()){
			return aooRepository.findOne(QAoo.aoo.codice.eq(codice).and(QAoo.aoo.validita.validoal.isNull()));
		}else{
			return null;
		}
	}

	@Transactional(readOnly=true)
	public boolean checkIfCodiceExists(Long aooId, String codice){
		if(codice==null || codice.equals("")){
			return false;
		}
		BooleanExpression predicate = QAoo.aoo.codice.eq(codice);
		if(aooId!=null){
			predicate = predicate.and(QAoo.aoo.id.notIn(aooId));
		}
		if(aooRepository.count(predicate)>0L){
			return true;
		}else{
			return false;
		}
	}
	
	public Set<Aoo> findDiscendentiOfAoo(Long aooId, boolean includiMainAoo) {
		Aoo aoo = null;
		Set<Aoo> set = null;
		if(includiMainAoo) {
			aoo = aooRepository.findOne(aooId);
			set = this.findDiscendentiOfAoo(aoo, null);
			set.add(aoo);
		}else {
			aoo = new Aoo();
			aoo.setId(aooId);
			set = this.findDiscendentiOfAoo(aoo, null);
		}
		return set;
	}
	
	public Set<Aoo> findAllDiscendentiOfAoo(Long aooId, boolean includiMainAoo) {
		Aoo aoo = null;
		Set<Aoo> set = null;
		if(includiMainAoo) {
			aoo = aooRepository.findOne(aooId);
			set = this.findAllDiscendentiOfAoo(aoo, null);
			set.add(aoo);
		}else {
			aoo = new Aoo();
			aoo.setId(aooId);
			set = this.findAllDiscendentiOfAoo(aoo, null);
		}
		return set;
	}
	
	public Set<Aoo> findDiscendentiOfAooNotDirezione(Long aooIdDirezione, Aoo aooPartenza, boolean includiMainAoo) {
		Aoo aoo = null;
		Set<Aoo> set = null;
		if(includiMainAoo) {
			aoo = aooRepository.findOne(aooIdDirezione);
			set = this.findDiscendentiOfAooNotDirezione(aoo, null, aooPartenza);
			set.add(aoo);
		}else {
			aoo = new Aoo();
			aoo.setId(aooIdDirezione);
			set = this.findDiscendentiOfAooNotDirezione(aoo, null, aooPartenza);
		}
		return set;
	}
	
	public Set<Aoo> findDiscendentiOfAoo(Aoo aoo, Set<Aoo> discendenti) {
		if(discendenti==null) {
			discendenti = new HashSet<Aoo>();
		}
		if(aoo!=null) {
			Iterable<Aoo> figlieDirette = aooRepository.findAll(QAoo.aoo.aooPadre.id.eq(aoo.getId()).and(QAoo.aoo.validita.validoal.isNull()));
			if(figlieDirette!=null && figlieDirette.iterator().hasNext()) {
				List<Aoo> figlieDiretteList = Lists.newArrayList(figlieDirette);
				discendenti.addAll(figlieDiretteList);
				for(Aoo figlia : figlieDiretteList) {
					findDiscendentiOfAoo(figlia, discendenti);
				}
			}
		}
		return discendenti;
	}
	
	public Set<Aoo> findAllDiscendentiOfAoo(Aoo aoo, Set<Aoo> discendenti) {
		if(discendenti==null) {
			discendenti = new HashSet<Aoo>();
		}
		if(aoo!=null) {
			Iterable<Aoo> figlieDirette = aooRepository.findAll(QAoo.aoo.aooPadre.id.eq(aoo.getId()));
			if(figlieDirette!=null && figlieDirette.iterator().hasNext()) {
				List<Aoo> figlieDiretteList = Lists.newArrayList(figlieDirette);
				discendenti.addAll(figlieDiretteList);
				for(Aoo figlia : figlieDiretteList) {
					findDiscendentiOfAoo(figlia, discendenti);
				}
			}
		}
		return discendenti;
	}
	
	public Set<Aoo> findDiscendentiOfAooNotDirezione(Aoo aoo, Set<Aoo> discendenti, Aoo aooPartenza) {
		if(discendenti==null) {
			discendenti = new HashSet<Aoo>();
		}
		if(aoo!=null) {
			Iterable<Aoo> figlieDirette = aooRepository.findAll(QAoo.aoo.aooPadre.id.eq(aoo.getId()).and(QAoo.aoo.validita.validoal.isNull()));
			if(figlieDirette!=null && figlieDirette.iterator().hasNext()) {
				List<Aoo> figlieDiretteList = Lists.newArrayList(figlieDirette);
				for(Aoo figlia : figlieDiretteList) {
					if( figlia.getTipoAoo()== null || (figlia.getTipoAoo().getCodice()!=null && !figlia.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) 
							|| figlia.getId()==aooPartenza.getId()) {
						discendenti.add(figlia);
					}
						
				}
				
				for(Aoo figlia : figlieDiretteList) {
					if( figlia.getTipoAoo()== null || (figlia.getTipoAoo().getCodice()!=null && !figlia.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) 
							|| figlia.getId()==aooPartenza.getId()) {
						findDiscendentiOfAooNotDirezione(figlia, discendenti, aooPartenza);
					}
				}
			}
		}
		return discendenti;
	}
	
	public List<Aoo> findFiglieDiretteOfAoo(Long aooId) {
		if(aooId != null) {
			Iterable<Aoo> figlieDirette = aooRepository.findAll(QAoo.aoo.aooPadre.id.eq(aooId).and(QAoo.aoo.validita.validoal.isNull()));
			if(figlieDirette!=null && figlieDirette.iterator().hasNext()) {
				return Lists.newArrayList(figlieDirette);
			}
		}
		return new ArrayList<Aoo>();
	}
	
	public List<Aoo> findAllFiglieDiretteOfAoo(Long aooId) {
		if(aooId != null) {
			Iterable<Aoo> figlieDirette = aooRepository.findAll(QAoo.aoo.aooPadre.id.eq(aooId));
			if(figlieDirette!=null && figlieDirette.iterator().hasNext()) {
				return Lists.newArrayList(figlieDirette);
			}
		}
		return new ArrayList<Aoo>();
	}
	
	@Transactional(readOnly = true)
	public Collection<AooDto> findByAooPadreIsNull( final Boolean disattivate,final String descrizione, final String tipoAoo, final String assessorato, final String stato  ) {
		BooleanExpression predicateAoo = QAoo.aoo.id.isNotNull();
		if((stato != null && !"".equals(stato)) || (descrizione != null && !"".equals(descrizione)) || (tipoAoo != null && !"".equals(tipoAoo)) || (assessorato != null && !"".equals(assessorato))){
			if(stato != null && !"".equals(stato)){
				if("0".equals(stato)){ // Aoo attive
					predicateAoo = validitaService.createPredicate(QAoo.aoo.validita);
				}
				else if("1".equals(stato)){ // Aoo disattivate
					predicateAoo = validitaService.validAoo(QAoo.aoo.validita);
				}
			}

		}

		Long idTipoAoo = null;
		Long idAssessorato = null;
		if(tipoAoo!=null && !"".equals(tipoAoo.trim())){
			try{
				idTipoAoo = Long.parseLong(tipoAoo.trim());
			}catch(Exception e){};
		}
		if(assessorato!=null && !"".equals(assessorato.trim())){
			try{
				idAssessorato = Long.parseLong(assessorato.trim());
			}catch(Exception e){};
		}

		if(idTipoAoo!=null){
			if(predicateAoo != null){
				predicateAoo = predicateAoo.and(QAoo.aoo.tipoAoo.id.eq(idTipoAoo));
			}
			else{
				predicateAoo = QAoo.aoo.tipoAoo.id.eq(idTipoAoo);
			}
		}

		if(idAssessorato!=null){
			Assessorato _assessorato = assessoratoRepository.findOne(idAssessorato);
			if(predicateAoo != null){
				predicateAoo = predicateAoo.and(QAoo.aoo.hasAssessorati.contains(_assessorato));
			}
			else{
				predicateAoo = QAoo.aoo.hasAssessorati.contains(_assessorato);
			}
		}


		if(descrizione!=null && !"".equals(descrizione.trim())){
			if(predicateAoo != null){
				predicateAoo = predicateAoo.and(((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(descrizione)).or(
						(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(descrizione)));
			}
			else{
				predicateAoo = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(descrizione)).or(
						(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(descrizione));
			}
		}

		Iterable<Aoo> aoos = aooRepository.findAll(predicateAoo);
		Set<Aoo> aoosPrimoLivello = new HashSet<Aoo>();
		Set<Aoo> aooDaVisualizzare = new HashSet<Aoo>();
		
		List<Long> aooIdsTrovati = new ArrayList<Long>();
		for(Aoo aoo : aoos){
			aooIdsTrovati.add(aoo.getId());
		}
		
		Map<Aoo, List<Aoo>> foglieAscendenti = new HashMap<Aoo, List<Aoo>>();
		for(Aoo aoo : aoos){
			aooDaVisualizzare.add(aoo);
			if(aoo.getSottoAoo()==null) {
				foglieAscendenti.put(aoo, this.findAscendenza(aoo, new ArrayList<Aoo>()));
				if(foglieAscendenti.get(aoo).size() > 0) {
					aooDaVisualizzare.addAll(foglieAscendenti.get(aoo));
					aoosPrimoLivello.add(foglieAscendenti.get(aoo).get(foglieAscendenti.get(aoo).size() - 1));
				}else {
					aoosPrimoLivello.add(aoo);
				}
			}else {
				boolean isFoglia = true;
				for(Aoo saoo : aoo.getSottoAoo()) {
					if(aooIdsTrovati.contains(saoo.getId())) {
						isFoglia = false;
						break;
					}
				}
				if(isFoglia) {
					foglieAscendenti.put(aoo, this.findAscendenza(aoo, new ArrayList<Aoo>()));
					if(foglieAscendenti.get(aoo).size() > 0) {
						aooDaVisualizzare.addAll(foglieAscendenti.get(aoo));
						aoosPrimoLivello.add(foglieAscendenti.get(aoo).get(foglieAscendenti.get(aoo).size() - 1));
					}else {
						aoosPrimoLivello.add(aoo);
					}
				}
			}
		}
		
		for(Aoo aoo : aoosPrimoLivello){
			rimuoviDiscendentiNonTrovati(aoo, aooDaVisualizzare);
		}
		
		/*ricorsiva( null, aoosPrimoLivello, -1);*/
		
		includiProfiloResponsabile(aoosPrimoLivello, -1, null);
		
		return AooTransformer.toDto(aoosPrimoLivello, false, true, false);

	}
	
	private void includiProfiloResponsabile(final Iterable<Aoo> aoos, int lev, final Aoo padre) {
		lev++;
		if (aoos != null || lev < MAX_LEV) {
			for (Aoo aoo : aoos) {
				if(aoo.getProfiloResponsabileId() != null){
					aoo.setProfiloResponsabile(profiloService.findOne(aoo.getProfiloResponsabileId()));
					aoo.getProfiloResponsabile().setAoo(null);
					if(aoo.getProfiloResponsabile().getUtente() != null){
						aoo.getProfiloResponsabile().getUtente().setAoos(null);
					}
				}
				includiProfiloResponsabile(aoo.getSottoAoo(), lev, aoo);
			}
		}
		lev--;
	}
	
	public List<Aoo> getAooByProfiloResponsabile(Long profiloId){
		Iterable<Aoo> it = aooRepository.findAll(QAoo.aoo.profiloResponsabileId.in(profiloId));
		if(it!=null) {
			return Lists.newArrayList(it);
		}else {
			return new ArrayList<Aoo>();
		}
	}
	
	private void rimuoviDiscendentiNonTrovati(Aoo aoo, final Set<Aoo> aooDaVisualizzare){
		Set<Aoo> sottoAoos = aoo.getSottoAoo();
		List<Aoo> toRemove = new ArrayList<Aoo>();
		if(sottoAoos!=null && sottoAoos.size()>0){
			for(Aoo sottoAoo : sottoAoos){
				if(!aooDaVisualizzare.contains(sottoAoo)){
					toRemove.add(sottoAoo);
				}else{
					this.rimuoviDiscendentiNonTrovati(sottoAoo, aooDaVisualizzare);
				}
			}
			sottoAoos.removeAll(toRemove);
		}
	}
	
	public List<Aoo> findAscendenza(Aoo aoo, List<Aoo> ascendenza){
		if(aoo.getAooPadre()==null){
			return ascendenza;
		}else{
			ascendenza.add(aoo.getAooPadre());
			ascendenza.addAll(findAscendenza(aoo.getAooPadre(), ascendenza));
			return ascendenza;
		}
	}

	@Transactional(readOnly = true)
	public Iterable<Aoo> findByAooPadreId(final Long aooPadreId, final Boolean disattivate  ) {

		BooleanExpression predicateAoo = QAoo.aoo.aooPadre.id.eq( aooPadreId );

		if(!disattivate){
		predicateAoo = predicateAoo.and(validitaService
				.createPredicate(QAoo.aoo.validita));
		}

		Iterable<Aoo> aoos = aooRepository.findAll( predicateAoo);
		log.debug("aoos:" + aoos);
		int lev = -1;
		ricorsiva( null, aoos, lev);
		return aoos;
	}
	
	@Transactional(readOnly = true)
	public Iterable<Aoo> findSezioniByAooPadreId(final Long aooPadreId, final Boolean disattivate  ){
		
		Iterable<Aoo> aoos = this.findByAooPadreId(aooPadreId, disattivate);
		List<Aoo> page = new ArrayList<Aoo>();
		
		ricorsivaSezioni(page,aoos);
		
        return page;
	}
	
	@Transactional(readOnly = true)
	public Iterable<Assessorato> findAssessoratiByAooId(final Long aooId){
		
		Aoo aoo = aooRepository.findOne(aooId);
		
		List<Assessorato> page = new ArrayList<Assessorato>();
		page.addAll(aoo.getHasAssessorati());
		
		for(Assessorato ass : page){
			if(ass.getProfiloResponsabileId() != null){
    			log.debug("ProfiloResponsabile:{}",ass.getProfiloResponsabileId());
				ass.setProfiloResponsabile(profiloService.findOne(ass.getProfiloResponsabileId()));
				ass.getProfiloResponsabile().setAoo(null);
				if(ass.getProfiloResponsabile().getUtente() != null){
					ass.getProfiloResponsabile().getUtente().setAoos(null);
				}
			}
		}
		
        return page;
	}
	
	
	private void ricorsivaSezioni( List<Aoo> page, final Iterable<Aoo> aoos) {
		
		if (aoos != null) {
			log.debug("ricorsivaSezioni{}",page.size());
			for (Aoo aoo : aoos) {
				log.debug(aoo.getTipoAoo().getDescrizione());
				if(aoo.getTipoAoo().getId() == 2 || aoo.getSottoAoo() != null){
					if(aoo.getTipoAoo().getId() == 2){
						log.debug("Added aoo:{}",aoo.getCodice());
						page.add(aoo);
					}
	        		
	        		if(aoo.getProfiloResponsabileId() != null){
	        			log.debug("ProfiloResponsabile:{}",aoo.getProfiloResponsabileId());
						aoo.setProfiloResponsabile(profiloService.findOne(aoo.getProfiloResponsabileId()));
						aoo.getProfiloResponsabile().setAoo(null);
						if(aoo.getProfiloResponsabile().getUtente() != null){
							aoo.getProfiloResponsabile().getUtente().setAoos(null);
						}
						aoo.getProfiloResponsabile().setHasQualifica(null);
					}
					aoo.setIndirizzo(null);

					Iterator<Aoo> iterator = aoo.getSottoAoo().iterator();
					while (iterator.hasNext()) {
					    Aoo element = iterator.next();
					    if(element.getValidita().getValidoal() != null){
					    	iterator.remove();
					    }
					}

					ricorsivaSezioni(  page, aoo.getSottoAoo());
	        	}
			}
		}
	}

	@Transactional(readOnly = true)
	public Iterable<Aoo> findByPredicate(final Predicate predicate){
		return aooRepository.findAll(predicate, new OrderSpecifier<>(Order.ASC, QAoo.aoo.id));
	}

	@Transactional(readOnly = true)
	public List<Aoo> findPadri() {
		log.debug("findPadri:");
		List<Aoo> aoos = aooRepository.findAll();
		log.debug("aoos:" + aoos);
		return aoos;
	}
	
	@Transactional(readOnly = true)
	public Iterable<Aoo> findNodiPadre() {
		log.debug("findNodiPadre:");
		BooleanExpression predicateAoo = QAoo.aoo.aooPadre.isNull();
		Iterable<Aoo> aoos = aooRepository.findAll( predicateAoo);
		log.debug("aoos:" + aoos);
		return aoos;
	}


	private void ricorsiva(  final Aoo padre, final Iterable<Aoo> aoos,
			int lev) {
		lev++;
//		log.debug("ricorsiva lev:" + lev);
		if (aoos != null || lev < MAX_LEV) {
			if(aoos!=null) {
				for (Aoo aoo : aoos) {
					aoo.getDescrizione();
					aoo.getHasAssessorati();
					
					for(Assessorato assessorato : aoo.getHasAssessorati()){
						
						if(assessorato.getProfiloResponsabileId() != null){
							assessorato.setProfiloResponsabile(profiloService.findOne(assessorato.getProfiloResponsabileId()));
							assessorato.getProfiloResponsabile().getHasQualifica();
							if(assessorato.getProfiloResponsabile().getAoo()!=null){
								assessorato.getProfiloResponsabile().getAoo().setAooPadre(null);
								assessorato.getProfiloResponsabile().getAoo().setSottoAoo(null);
							}
						}
						
					}
					if (padre != null) {
						aoo.setAooPadre(new Aoo(padre));
					}
	
					if(aoo.getProfiloResponsabileId() != null){
	//					Profilo profilo = aoo.getProfiloResponsabile();
	//					profilo.setAoo(null);
	//					profilo.setGrupporuolo(null);
						aoo.setProfiloResponsabile(profiloService.findOne(aoo.getProfiloResponsabileId()));
						
						aoo.getProfiloResponsabile().setAoo(null);
						
						if(aoo.getProfiloResponsabile().getUtente() != null){
							aoo.getProfiloResponsabile().getUtente().setAoos(null);
						}
						
						//aoo.getProfiloResponsabile().setHasQualifica(null);
					}
	
					aoo.setIndirizzo(null);
				//	aoo.setDenominazioneRelatore(null);
	
					ricorsiva(  aoo, aoo.getSottoAoo(), lev);
				}
			}
		}
		lev--;
	}

	
	@Transactional
	public void updateAooPadre(final Aoo aoo) {
		
		Long idAooPadre = null;
		if (aoo.getAooPadre() != null) {
			idAooPadre = aoo.getAooPadre().getId();
		}
		aoo.setAooPadre(new Aoo(idAooPadre));
		aooRepository.save(aoo);
	}



	@Transactional
	public void save(final Aoo aoo) throws GestattiCatchedException {
		if(aoo.getIndirizzo()!=null && aoo.getIndirizzo().getId()!=null){
			Indirizzo indirizzo = indirizzoService.getById(aoo.getIndirizzo().getId());
			aoo.setIndirizzo(indirizzo);
		}
		if(aoo.getProfiloResponsabile()!=null){
			aoo.setProfiloResponsabileId(aoo.getProfiloResponsabile().getId());
		}
		if(aoo.getId() != null ){
			Aoo aooDb = aooRepository.findOne(aoo.getId());
			if( !aooDb.getCodice().equals( aoo.getCodice()) ){
				modificaCodiceAoo(aooDb, aoo,null);
			}else{
				aooRepository.save(aoo);
			}
		}
		else{
			List<Aoo> aoosStessoCodice = aooRepository.findAoosByCodice(aoo.getCodice().trim());
			aooRepository.save(aoo);
			for(Aoo a : aoosStessoCodice){
				this.disabilita(a.getId());
			}			
			if(aoosStessoCodice!=null && aoosStessoCodice.size() > 0){
				Calendar c = new GregorianCalendar();
				List<ProgressivoAdozione> progressiviAdozione = codiceProgressivoService.findAllProgressivoAdozioneByAooId(c.get(Calendar.YEAR), aoosStessoCodice.get(0).getId());
				for(ProgressivoAdozione p : progressiviAdozione){
					ProgressivoAdozione newP = new ProgressivoAdozione();
					newP.setAnno(p.getAnno());
					newP.setAoo(aoo);
					newP.setProgressivo(p.getProgressivo());
					newP.setTipoProgressivo(p.getTipoProgressivo());
					codiceProgressivoService.saveProgressivoAdozione(newP);
				}
				List<ProgressivoProposta> progressiviProposta = codiceProgressivoService.findAllProgressivoPropostaByAooId(c.get(Calendar.YEAR), aoosStessoCodice.get(0).getId());
				for(ProgressivoProposta p : progressiviProposta){
					ProgressivoProposta newP = new ProgressivoProposta();
					newP.setAnno(p.getAnno());
					newP.setAoo(aoo);
					newP.setProgressivo(p.getProgressivo());
					newP.setTipoProgressivo(p.getTipoProgressivo());
					codiceProgressivoService.saveProgressivoProposta(newP);
				}
			}
			else{
				//non è più necessario generare i progressivi per una nuova aoo quindi commento le due successive righe di codice
				//codiceProgressivoService.generaProgressiviPropostaPerNuovaAoo(aoo);
				//codiceProgressivoService.generaProgressiviAdozionePerNuovaAoo(aoo);
			}
		}
	}

	@Transactional(readOnly = true)
	public Aoo findOne(final Long id) {
		Aoo aoo = aooRepository.findOneWithEagerRelationships(id);
		log.debug("findOne");
		if (aoo != null) {
			log.debug(aoo.toString());
			if (aoo.getAooPadre() != null && aoo.getId() != aoo.getAooPadre().getId()) {
				aoo.setAooPadre(new Aoo(aoo.getAooPadre()));
			}

			if(aoo.getProfiloResponsabileId() != null){
				aoo.setProfiloResponsabile(profiloService.findOne(aoo.getProfiloResponsabileId()));
			}

			if (aoo.getSottoAoo() != null) {
				aoo.setSottoAoo(null);
			}

			if (aoo.getIndirizzo() != null) {
				aoo.getIndirizzo().getToponimo();
				log.debug(aoo.getIndirizzo().toString());
			}

			aoo.getLogo();
			String sp = aoo.getSpecializzazione();
			aoo.setSpecializzazione(sp);
		}

		return aoo;
	}
	
	@Transactional(readOnly = true)
	public Aoo findOneSimple(final Long id) {
		return aooRepository.findOne(id);
	}

	@Transactional(readOnly=true)
	public List<Aoo> getAllEnabled(){
		BooleanExpression predicate = QAoo.aoo.validita.validoal.isNull();
		return Lists.newArrayList(aooRepository.findAll(predicate));
	}

	@Transactional(readOnly=true)
	public Page<Aoo> findAll(final Pageable generatePageRequest ) {
		BooleanExpression predicateAoo =  validitaService
				.createPredicate(QAoo.aoo.validita);

		Page<Aoo> l =  aooRepository.findAll(predicateAoo,generatePageRequest);
		if( l!= null  ){
			for (Aoo aoo2 : l) {
				mimalAooForList(aoo2);
			}

		}
		return l;
	}
	
	@Transactional(readOnly=true)
	public Page<AooDto> findAllDto(final Pageable generatePageRequest ) {
		BooleanExpression predicateAoo =  validitaService
				.createPredicate(QAoo.aoo.validita);

		Page<Aoo> l =  aooRepository.findAll(predicateAoo,generatePageRequest);
		
		List<AooDto> dtoList = AooTransformer.toDto(l.getContent());
		
		Page<AooDto> dtoPage = new PageImpl<AooDto>(dtoList, generatePageRequest, l.getTotalElements());
		return dtoPage;
	}
	
	@Transactional(readOnly=true)
	public List<Aoo> getAooSuperiori(long idFiglia) {
		List<Aoo> retVal = new ArrayList<>();
		
		Aoo figlia = aooRepository.findOne(idFiglia);
		if(figlia!=null) {
			Aoo padre = figlia.getAooPadre();
			if (padre != null) {
				retVal.addAll(getAooSuperiori(padre.getId().longValue()));
			}
			
			retVal.add(figlia);
		}
		return retVal;
	}
	
	@Transactional(readOnly=true)
	public List<Aoo> getAooRicorsiva(long idParent) {
		List<Aoo> retVal = new ArrayList<>();
		
		Aoo parent = aooRepository.findOne(idParent);
		if(parent!=null && (parent.getValidita()==null || parent.getValidita().getValidoal()==null)) {
			Set<Aoo> figlie = parent.getSottoAoo();
			if (figlie != null) {
				for (Aoo aoo : figlie) {
					retVal.addAll(getAooRicorsiva(aoo.getId().longValue()));
				}
			}
			
			retVal.add(DomainUtil.minimalAoo(parent));
		}
		return retVal;
	}
	
	private void mimalAooForList(Aoo aooEl) {
		if (aooEl.getAooPadre() != null) {
			aooEl.setAooPadre(new Aoo(aooEl.getAooPadre()));
		}

		if(aooEl.getProfiloResponsabileId() != null){
			aooEl.setProfiloResponsabile(profiloService
					.findOne(aooEl.getProfiloResponsabileId()));
			
			aooEl.getProfiloResponsabile().setAoo(null);
			if(aooEl.getProfiloResponsabile().getHasQualifica() != null){
				aooEl.getProfiloResponsabile().setHasQualifica(null);
			}
		}
		aooEl.setIndirizzo(null);
	}
	
	
	@Transactional(readOnly=true)
	public List<Aoo> findAll(){
		return aooRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	public List<Aoo> findById(List<Long> listIdAoo){
		List<Aoo> list = new ArrayList<Aoo>();
		List<Aoo> aoos = null;
		if(listIdAoo!=null && listIdAoo.size() > 0) {
			aoos = aooRepository.findAll(listIdAoo);
		}
		if(aoos!=null) {
			for(Aoo aoo : aoos) {
				Aoo simple = DomainUtil.simpleAoo(aoo);
				list.add(simple);
			}
		}
		return list;
	}
	
	@Transactional(readOnly=true)
	public List<Aoo> findEnabledById(List<Long> listIdAoo){
		List<Aoo> list = new ArrayList<Aoo>();
		List<Aoo> aoos = null;
		if(listIdAoo!=null && listIdAoo.size() > 0) {
			BooleanExpression pred = QAoo.aoo.id.in(listIdAoo).and(QAoo.aoo.validita.validoal.isNull());
			aoos = Lists.newArrayList(aooRepository.findAll(pred));
		}
		if(aoos!=null) {
			for(Aoo aoo : aoos) {
				Aoo simple = DomainUtil.simpleAoo(aoo);
				list.add(simple);
			}
		}
		return list;
	}


	@Transactional
	public void modificaCodiceAoo(final Aoo aooDismessa , final Aoo aoo, final Aoo aooPadre) throws GestattiCatchedException {
		log.debug("modificaCodiceAoo aooDismessa:"+aooDismessa);
		log.debug("modificaCodiceAoo aooPadre:"+aooPadre);
		log.debug("aoo:"+aoo);

		clonaAooAndAnagrafiche(aooDismessa, aoo, aooPadre);

	}

	private void clonaAooAndAnagrafiche(Aoo aooDismessa, final Aoo aoo, final Aoo aooPadre) throws GestattiCatchedException {
		if(aooDismessa.getValidita() == null ){
			Validita validitaDismessa = new Validita();
			validitaDismessa.setValidoal( new LocalDate() );
			aooDismessa.setValidita( validitaDismessa);
		}else{
			aooDismessa.getValidita().setValidoal(new LocalDate());
		}

		Aoo nuovaAoo = new Aoo();
		BeanUtils.copyProperties(aoo, nuovaAoo,  "id", "validita","indirizzo","aooPadre","sottoAoo");
		nuovaAoo.setAooPadre(aooPadre);

		if(aooDismessa.getIndirizzo() != null ){
			Indirizzo indirizzo = new Indirizzo();
			BeanUtils.copyProperties(aooDismessa.getIndirizzo(),indirizzo,"id");
			nuovaAoo.setIndirizzo(indirizzo);
		}

		Validita validita = new Validita();
		validita.setValidodal(new LocalDate());
		nuovaAoo.setValidita(validita);
		nuovaAoo.setCodice( aoo.getCodice() );

		aooDismessa =  aooRepository.save(aooDismessa);
		nuovaAoo = aooRepository.save(nuovaAoo);

		Map<Long,QualificaProfessionale> idVecchioNuovaQualifica = new HashMap<Long, QualificaProfessionale>();
		Map<Long,Profilo> idVecchioNuovaProfilo = new HashMap<Long, Profilo>();
		//Map<Long,DenominazioneRelatore> idDenominazioneRelatore = new HashMap<Long, DenominazioneRelatore>();
		Map<Long,GruppoRuolo> idVecchioNuovaGrupporuolo = new HashMap<Long, GruppoRuolo>();

		// Iterable<GruppoRuolo> gruppi =  gruppoRuoloRepository.findByAooId( aooDismessa.getId()  );
		// for (GruppoRuolo source : gruppi) {
		//	GruppoRuolo target = new GruppoRuolo();
		//	BeanUtils.copyProperties(source, target ,"id","aoo"  );
		//	target = gruppoRuoloRepository.save(target);
		//	idVecchioNuovaGrupporuolo.put(source.getId() , target);
		// }

//		INNOVCIFRA-187
//		Iterable<QualificaProfessionale> qualifiche = qualificaProfessionaleRepository.findByAooId(aooDismessa.getId());
//		for (QualificaProfessionale source : qualifiche) {
//			QualificaProfessionale target = new QualificaProfessionale();
//			BeanUtils.copyProperties(source, target ,"id","aoo" );
//			target.setAoo(nuovaAoo);
//			target = qualificaProfessionaleRepository.save(target);
//			idVecchioNuovaQualifica.put(source.getId() , target);
//		}

		Iterable<Ufficio> uffici =  ufficioService.findByAooIdAndValidi( aooDismessa.getId()  );
		for (Ufficio source : uffici) {
			Ufficio target = new Ufficio();
			BeanUtils.copyProperties(source, target ,"id" ,"validita","aoo");
			target.setAoo(nuovaAoo);
			ufficioService.save(target);
		}

		Iterable<RubricaDestinatarioEsterno> destinatari =  rubricaDestinatarioEsternoService.findByAooIdAndValidi( aooDismessa.getId()   );
		for (RubricaDestinatarioEsterno source : destinatari) {
			RubricaDestinatarioEsterno target = new RubricaDestinatarioEsterno();
			BeanUtils.copyProperties(source, target ,"id" ,"validita","aoo");
			target.setAoo(nuovaAoo);
			rubricaDestinatarioEsternoService.save(target);
		}

		Iterable<TipoMateria> tipoMaterie =  tipoMateriaService.findAllAoo( aooDismessa.getId() ,true );
		for (TipoMateria source : tipoMaterie) {
			TipoMateria target = new TipoMateria();
			BeanUtils.copyProperties(source, target ,"id" ,"validita","aoo","materie");
			target.setAoo(nuovaAoo);
			target = tipoMateriaService.save(target);
			for (Materia source2 : source.getMaterie() ) {
				Materia target2 = new Materia();
				BeanUtils.copyProperties(source2, target2 ,"id" ,"validita","aoo","sottoMaterie","tipoMateria");
				target2.setAoo(nuovaAoo);
				target2.setTipoMateria(target);
				target2 = tipoMateriaService.save(target2);
				for (SottoMateria source3 : source2.getSottoMaterie()  ) {
					SottoMateria target3 = new SottoMateria();
					BeanUtils.copyProperties(source3, target3 ,"id" ,"validita","aoo","sottoMaterie","materia");
					target3.setAoo(nuovaAoo);
					target3.setMateria(target2);
					target3 = tipoMateriaService.save(target3);
				}
			}
		}

		Iterable<Profilo> profili = profiloService.findByAooIdAndValidi( aooDismessa.getId() );
		for (Profilo profilo : profili) {
			Profilo targetProfilo = new Profilo();
			BeanUtils.copyProperties(profilo, targetProfilo ,"id","aoo","validita","grupporuolo");
			targetProfilo.setAoo(nuovaAoo);
			targetProfilo.setValidita(validita);

			if(profilo.getHasQualifica() != null ){
				Set<QualificaProfessionale> hasQualifica = new HashSet<QualificaProfessionale>();
				for (QualificaProfessionale source : profilo.getHasQualifica()) {
//					INNOVCIFRA-187
//					if(source.getAoo() != null ){
//						hasQualifica.add( idVecchioNuovaQualifica.get(source.getId())  );
//					}else{
						hasQualifica.add( source );
//					}
				}
				targetProfilo.setHasQualifica(hasQualifica);
			}
		targetProfilo.setGrupporuolo( idVecchioNuovaGrupporuolo.get(profilo.getGrupporuolo().getId()) );
		targetProfilo.setAoo(nuovaAoo);
		targetProfilo = profiloService.save(targetProfilo);
		idVecchioNuovaProfilo.put(profilo.getId(), targetProfilo);
		}

		/*if(aooDismessa.getDenominazioneRelatore() != null ){
			nuovaAoo.setDenominazioneRelatore( idDenominazioneRelatore.get(aooDismessa.getDenominazioneRelatore().getId()));
		}*/
		if(aooDismessa.getProfiloResponsabileId()  != null ){
			nuovaAoo.setProfiloResponsabileId(  aooDismessa.getProfiloResponsabileId());
		}
		save(nuovaAoo);
		Integer anno = LocalDate.now().getYear();

		List<ProgressivoProposta> progressivi = codiceProgressivoService.findAllProgressivoPropostaByAooId(anno, aooDismessa.getId() );
		for (ProgressivoProposta prog : progressivi) {
			codiceProgressivoService.creaProgressivoProposta( nuovaAoo, prog.getTipoProgressivo()  );
		}

		List<ProgressivoAdozione> progressiviA = codiceProgressivoService.findAllProgressivoAdozioneByAooId(anno, aooDismessa.getId() );
		for (ProgressivoAdozione prog : progressiviA) {
			codiceProgressivoService.creaProgressivoAdozione( nuovaAoo, prog.getTipoProgressivo()  );
		}





		//TODO PROGRESSIVI APPROVAZIONI se ne esistono rigrearli

		for (Aoo aoo2 : aooDismessa.getSottoAoo() ) {
			if(aoo2.getValidita()==null || aoo2.getValidita().getValidoal() == null ){
				Aoo nuovaAoo2 = new Aoo();
				BeanUtils.copyProperties(aoo2, nuovaAoo2,  "id", "validita","indirizzo","aooPadre");
				modificaCodiceAoo(aoo2, nuovaAoo2, nuovaAoo);
			}
		}

	}


	public void delete(final Long id) {
		Aoo  aoo = aooRepository.findOne(id);
		if(aoo.getValidita()== null){
			aoo.setValidita(new Validita());
		}

		aoo.getValidita().setValidoal( new LocalDate() );
		aooRepository.save( aoo );
	}

	/**
	 * Riabilita una aoo disabilitata
	 * @param id
	 */
	@Transactional(readOnly=false)
	public void abilita(final Long id) {
		Aoo  aoo = aooRepository.findOne(id);
		if(aoo!=null){
			if(aoo.getValidita()==null){
				aoo.setValidita(new Validita());
			}
			aoo.getValidita().setValidoal(null);
			aooRepository.save(aoo);
		}
	}
	
	/**
	 * Disabilita una aoo abilitata.
	 * @param id
	 */
	@Transactional(readOnly=false)
	public void disabilita(final Long id) {
		Aoo  aoo = aooRepository.findOne(id);
		if(aoo!=null){
			if(aoo.getValidita()==null){
				aoo.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			aoo.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			aooRepository.save(aoo);
		}
	}
	
	
	/**
	 * Set null aoo Padre
	 * @param id
	 */
	@Transactional(readOnly=false)
	public void annullaAooPadre(final Long id) {
		Aoo  aoo = aooRepository.findOne(id);
		if(aoo!=null){
			aoo.setAooPadre(null);
			aooRepository.save(aoo);
		}
	}
	
	@Transactional
	public Iterable<Aoo> findByAssessorato(Assessorato assessorato){
		BooleanExpression predicate = QAoo.aoo.hasAssessorati.contains(assessorato);
		return aooRepository.findAll(predicate);
	}
}
