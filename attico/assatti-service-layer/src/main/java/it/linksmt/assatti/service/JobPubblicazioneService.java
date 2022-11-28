package it.linksmt.assatti.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.datalayer.domain.QJobPubblicazione;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.repository.JobPubblicazioneRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

@Service
@Transactional
public class JobPubblicazioneService{
	
	private final Logger log = LoggerFactory.getLogger(JobPubblicazioneService.class);
	
	private final String  delayVal = SchedulerProps.getProperty("scheduler.job.pubblicazione.albo.delay.seconds");
	
	@Autowired
	private JobPubblicazioneRepository jobPubblicazioneRepository;
	
	private static final DateFormat FILTER_DF = new SimpleDateFormat("yyyy-MM-dd");
	
	public List<JobPubblicazione> findByAttoId(Long attoId){
		return jobPubblicazioneRepository.findByAttoId(attoId);
	}
	
	@Transactional
	public Long richiediNuovaPubblicazione(Long attoId){
		JobPubblicazione pub = new JobPubblicazione();
		pub.setCreatedDate(DateTime.now());
		pub.setCreatedBy(SecurityUtils.getCurrentLogin());
		pub.setLastModifiedBy(SecurityUtils.getCurrentLogin());
		pub.setStato(StatoJob.NEW);
		pub.setAtto(new Atto(attoId));
		pub.setTentativi(0);
		pub.setLastModifiedDate(DateTime.now());
		pub.setVersion(0);
		pub = jobPubblicazioneRepository.save(pub);
//		jobPubblicazioneRepository.richiediNuovaPubblicazione(SecurityUtils.getCurrentLogin(), StatoJob.NEW.name(), attoId);
		log.debug("richiediNuovaPubblicazione done for attoId " +  attoId);
		return pub.getId();
	}
	
	@Transactional
	public void delete(Long jobPubblicazioneId){
		jobPubblicazioneRepository.delete(jobPubblicazioneId);
	}
	
	@Transactional(readOnly = true)
	public List<JobPubblicazione> getDaPubblicare() {
		
		int delaySec = 60;
		try {
			delaySec = Integer.parseInt(delayVal);
		}
		catch (Exception e) {
			log.warn("Errore di lettura del delay di pubblicazione. Viene utilizzato il valore di default di " + delaySec);
		}
		
		List<StatoJob> stati = new ArrayList<StatoJob>();
		stati.add(StatoJob.ERROR);
		stati.add(StatoJob.NEW);
		
		// Considero anche quelli in progress, in base al numero di ore
		stati.add(StatoJob.IN_PROGRESS);
		
		stati.add(StatoJob.FORCED_SENDING);
		
		BooleanExpression p = QJobPubblicazione.jobPubblicazione.stato.in(stati).and(
				QJobPubblicazione.jobPubblicazione.createdDate.before(DateTime.now().minusSeconds(delaySec)));
				
		return Lists.newArrayList(jobPubblicazioneRepository.findAll(p));
	}
	
	@Transactional
	public void inProgress(Long jobId){
		JobPubblicazione job = jobPubblicazioneRepository.findOne(jobId);
		jobPubblicazioneRepository.updateStato(jobId, StatoJob.IN_PROGRESS.name(), SecurityUtils.getCurrentLogin(), job.getTentativi()!=null ? job.getTentativi() + 1 : 1);
	}
	
	@Transactional
	public void error(Long jobId, String dettaglio){
		JobPubblicazione job = jobPubblicazioneRepository.findOne(jobId);
		jobPubblicazioneRepository.updateStatoAndErrore(jobId, StatoJob.ERROR.name(), SecurityUtils.getCurrentLogin(), job.getTentativi(), dettaglio);
	}
	
	@Transactional
	public void waitingInfo(Long jobId){
		JobPubblicazione job = jobPubblicazioneRepository.findOne(jobId);
		jobPubblicazioneRepository.updateStato(jobId, StatoJob.WAITING_INFO.name(), SecurityUtils.getCurrentLogin(), job.getTentativi());
	}
	
	@Transactional
	public Iterable<JobPubblicazione> getJobsWatingInfo(){
		BooleanExpression p = QJobPubblicazione.jobPubblicazione.stato.in(StatoJob.WAITING_INFO, StatoJob.IN_PROGRESS_WAITING, StatoJob.ERROR_WAITING);
		return jobPubblicazioneRepository.findAll(p);
	}
	
	@Transactional
	public void done(Long jobId){
		JobPubblicazione job = jobPubblicazioneRepository.findOne(jobId);
		jobPubblicazioneRepository.updateStato(jobId, StatoJob.DONE.name(), SecurityUtils.getCurrentLogin(), job.getTentativi());
	}
	
	@Transactional
	public List<JobPubblicazione> inProgressForWaitingTask(List<JobPubblicazione> jobs){
		for(JobPubblicazione job : jobs) {
			job.setStato(StatoJob.IN_PROGRESS_WAITING);
			job.setLastModifiedBy("system");
			job.setLastModifiedDate(DateTime.now());
			job = jobPubblicazioneRepository.save(job);
		}
		return jobs;
	}
	
	@Transactional
	public void errorInWaitingTask(JobPubblicazione j, String error){
		JobPubblicazione job = jobPubblicazioneRepository.findOne(j.getId());
		job.setStato(StatoJob.ERROR_WAITING);
		job.setLastModifiedBy("system");
		job.setLastModifiedDate(DateTime.now());
		job.setDettaglioErrore(error);
		job = jobPubblicazioneRepository.save(job);
	}
	
	
// TODO	@Transactional
//	public JobPubblicazione saveDatiProtocollo(Long jobId, RispostaProtocollo rispostaProto) throws Exception {
//		JobPubblicazione job = jobPubblicazioneRepository.findOne(jobId);
//		job.setNumeroProtocollo(serviceUtil.getStringSegnaturaProtocollo(
//				rispostaProto.getSegnaturaProtocollo()));
//		job.setDataProtollo(rispostaProto.getSegnaturaProtocollo().getDataRegistrazione());
//		job.setSegnatura(AxisObjectUtil.serializeAxisObject(rispostaProto.getSegnaturaProtocollo(), true, true));
//		jobPubblicazioneRepository.save(job);
//		
//		return job;
//	}
	
	
	
	/*
	@Transactional
	public void cancellaPubblicazione(JobPubblicazione job){
		job.setStato(StatoJob.CANCELED);
		jobPubblicazioneRepository.save(job);
	}
	*/
	@Transactional(readOnly = true)
	public List<JobPubblicazione> getAllPubblicazioni(){
		return jobPubblicazioneRepository.findAll();
	}
	
	@Transactional(readOnly=true)
	public Page<JobPubblicazione> search(JsonObject search, Pageable paginazione){
		if(search == null){
			return jobPubblicazioneRepository.findAll(QJobPubblicazione.jobPubblicazione.id.isNotNull(), paginazione);
		}else{
			BooleanExpression predicate = QJobPubblicazione.jobPubblicazione.id.isNotNull();
				//inserire qui la logica per impostare i filtri di ricerca
			Page<JobPubblicazione> pubblicazioni = jobPubblicazioneRepository.findAll(predicate, paginazione);
			for (JobPubblicazione jobPubblicazione : pubblicazioni) {
				jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
			}
			return pubblicazioni;
		}
	}
	
	@Transactional( readOnly=true )
	public Page<JobPubblicazione> findAll(Pageable generatePageRequest) {
		Page<JobPubblicazione> pubblicazioni = jobPubblicazioneRepository.findAll(generatePageRequest);
		for (JobPubblicazione jobPubblicazione : pubblicazioni) {
			jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
		}
		return pubblicazioni;
	}
	
	@Transactional(readOnly=true)
	public Page<JobPubblicazione> findByAooId(Long aooId, Pageable generatePageRequest) {
		log.debug("findByAooIdAndValidi aooId:"+aooId);
		BooleanExpression predicate = QJobPubblicazione.jobPubblicazione.atto.aoo.id.eq( aooId).or(QJobPubblicazione.jobPubblicazione.atto.aoo.id.isNull());
		Page<JobPubblicazione> pubblicazioni = jobPubblicazioneRepository.findAll(predicate, generatePageRequest);
		for (JobPubblicazione jobPubblicazione : pubblicazioni) {
			jobPubblicazione.setAtto( this.getMinimalAtto(jobPubblicazione.getAtto()));
		}
		return pubblicazioni;
	}
	
	@Transactional(readOnly=true)
	public List<String> findErroreByAttoId(Long attoId) {
		return jobPubblicazioneRepository.getDettaglioErroreByAttoId(attoId);
	}
	
	@Transactional(readOnly=false)
	public void disable(final Long id){
		log.debug("disable JobPubblicazione with id " + id);
		JobPubblicazione jobPubblicazione = jobPubblicazioneRepository.findOne(id);
		if(jobPubblicazione!=null){

			
			//TODO DO ANNULLAMENTO
			jobPubblicazioneRepository.save(jobPubblicazione);
		}
	}
	
	@Transactional(readOnly=true)
	public JobPubblicazione findOne(Long id) {
		return jobPubblicazioneRepository.findOne(id);		 
	}
	
	@Transactional(readOnly=true)
	public Page<JobPubblicazione> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		log.debug("Enter:"+searchJson);
		BooleanExpression predicate = QJobPubblicazione.jobPubblicazione.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull()) {
			if(searchJson.has("codiceCifra") && !searchJson.get("codiceCifra").isJsonNull()){
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.codiceCifra.containsIgnoreCase(searchJson.get("codiceCifra").getAsString().trim());
				predicate = predicate.and(p);
			}
			if(searchJson.has("numeroAdozione") && !searchJson.get("numeroAdozione").isJsonNull()){
				String numAdozione = searchJson.get("numeroAdozione").getAsString().trim();
				if(!StringUtil.isNull(numAdozione)) {
					numAdozione = StringUtil.riempiASinistra(numAdozione, "0", 5);
					BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.numeroAdozione.eq(numAdozione);
					predicate = predicate.and(p);
				}
			}
			
			if(searchJson.has("dataAdozioneStart") && !searchJson.get("dataAdozioneStart").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataAdozioneStart").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.dataAdozione.goe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("dataAdozioneEnd") && !searchJson.get("dataAdozioneEnd").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataAdozioneEnd").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.dataAdozione.loe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("oggetto") && !searchJson.get("oggetto").isJsonNull()){
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.oggetto.containsIgnoreCase(searchJson.get("oggetto").getAsString().trim());
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("statoPubblicazione") && !searchJson.get("statoPubblicazione").isJsonNull()){
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.statoPubblicazione.containsIgnoreCase(searchJson.get("statoPubblicazione").getAsString().trim());
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("statoProceduraPubblicazione") && !searchJson.get("statoProceduraPubblicazione").isJsonNull()){
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.statoProceduraPubblicazione.containsIgnoreCase(searchJson.get("statoProceduraPubblicazione").getAsString().trim());
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("dataInizioPubblicazioneStart") && !searchJson.get("dataInizioPubblicazioneStart").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataInizioPubblicazioneStart").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.inizioPubblicazioneAlbo.goe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("dataInizioPubblicazioneEnd") && !searchJson.get("dataInizioPubblicazioneEnd").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataInizioPubblicazioneEnd").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.inizioPubblicazioneAlbo.loe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("dataFinePubblicazioneStart") && !searchJson.get("dataFinePubblicazioneStart").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataFinePubblicazioneStart").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.finePubblicazioneAlbo.goe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("dataFinePubblicazioneEnd") && !searchJson.get("dataFinePubblicazioneEnd").isJsonNull()){
				LocalDate ld = new LocalDate(FILTER_DF.parse(searchJson.get("dataFinePubblicazioneEnd").getAsString().trim()));
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.finePubblicazioneAlbo.loe(ld);
				predicate = predicate.and(p);
			}
			
			if(searchJson.has("errori") && !searchJson.get("errori").isJsonNull()){
				
				if(searchJson.get("errori").getAsBoolean()){
					predicate = predicate.and(QJobPubblicazione.jobPubblicazione.dettaglioErrore.isNotNull().or(QJobPubblicazione.jobPubblicazione.dettaglioErrore.isNotEmpty()));
				}
				else{
					predicate = predicate.and(QJobPubblicazione.jobPubblicazione.dettaglioErrore.isNull().or(QJobPubblicazione.jobPubblicazione.dettaglioErrore.isEmpty()));
				}
				//predicate = predicate.and(QConfigurazioneRiversamento.configurazioneRiversamento.onlyPubblicabili.eq(searchJson.get("onlyPubblicabili").getAsBoolean()));
			}
			
			JsonElement statoRelata = searchJson.get("statoRelata");
			if(statoRelata !=null && !statoRelata.isJsonNull() && statoRelata.getAsString()!=null && !"".equals(statoRelata.getAsString()) && statoRelata.getAsInt() >= 0 && statoRelata.getAsInt() <3){
				BooleanExpression p = QJobPubblicazione.jobPubblicazione.atto.statoRelata.eq(searchJson.get("statoRelata").getAsInt());
				predicate = predicate.and(p);
			}
		}


		if (null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo") && 
			!searchJson.get("aoo").isJsonNull() && !searchJson.get("aoo").getAsString().isEmpty()) {
			
			if(searchJson.get("aoo").getAsString().equals("-")) {
				predicate = predicate.and(QJobPubblicazione.jobPubblicazione.atto.aoo.isNull());
			}
			else {
				predicate = predicate.and(((QJobPubblicazione.jobPubblicazione.atto.aoo.codice.concat(" - ").concat(QJobPubblicazione.jobPubblicazione.atto.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())).or(
						(QJobPubblicazione.jobPubblicazione.atto.aoo.codice.concat("-").concat(QJobPubblicazione.jobPubblicazione.atto.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())));
			}
		}
		
		Page<JobPubblicazione> pubblicazioni = jobPubblicazioneRepository.findAll(predicate , generatePageRequest);
		for (JobPubblicazione jobPubblicazione : pubblicazioni) {
			jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
		}
		return pubblicazioni;
	}
	
	
	private Atto getMinimalAtto(Atto atto){
		
		Atto newAtto = new Atto();
		newAtto.setId(atto.getId());
		newAtto.setCodiceCifra(atto.getCodiceCifra()!=null?atto.getCodiceCifra():"");
		newAtto.setDataAdozione(atto.getDataAdozione()!=null?atto.getDataAdozione():null);
		newAtto.setOggetto(atto.getOggetto()!=null?atto.getOggetto():"");
		newAtto.setStatoPubblicazione(atto.getStatoPubblicazione()!=null?atto.getStatoPubblicazione():"");
		newAtto.setStatoProceduraPubblicazione(atto.getStatoProceduraPubblicazione()!=null?atto.getStatoProceduraPubblicazione():"");
		newAtto.setInizioPubblicazioneAlbo(atto.getInizioPubblicazioneAlbo()!=null?atto.getInizioPubblicazioneAlbo():null);
		newAtto.setFinePubblicazioneAlbo(atto.getFinePubblicazioneAlbo()!=null?atto.getFinePubblicazioneAlbo():null);
		newAtto.setStatoRelata(atto.getStatoRelata()!=null?atto.getStatoRelata():0);
		newAtto.setNumeroAdozione(atto.getNumeroAdozione()!=null?atto.getNumeroAdozione():"");
		newAtto.setDataNumerazione(atto.getDataNumerazione()!=null?atto.getDataNumerazione():null);
		newAtto.setDataEsecutivita(atto.getDataEsecutivita()!=null?atto.getDataEsecutivita():null);
		newAtto.setCreatedBy(atto.getCreatedBy()!=null?atto.getCreatedBy():"");
		newAtto.setEmananteProfilo(atto.getEmananteProfilo()!=null?atto.getEmananteProfilo():null);
		newAtto.setMotivazioneRichiestaAnnullamento(atto.getMotivazioneRichiestaAnnullamento()!=null?atto.getMotivazioneRichiestaAnnullamento():null);
		return atto;
	}
}