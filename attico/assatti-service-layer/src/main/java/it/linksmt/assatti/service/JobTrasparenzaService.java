package it.linksmt.assatti.service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.JobTrasparenza;
import it.linksmt.assatti.datalayer.domain.QJobTrasparenza;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.JobTrasparenzaRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.SchedulerProps;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class JobTrasparenzaService {

	private final Logger log = LoggerFactory.getLogger(JobTrasparenzaService.class);
	
	private final String  delayVal = SchedulerProps.getProperty("scheduler.job.pubblicazione.trasparenza.delay.seconds");

	@Autowired
	private JobTrasparenzaRepository jobTrasparenzaRepository;
	
	@Autowired
	private AttoService attoService;
	
	@Autowired
	private AttoRepository attoRepository;
	
	@Autowired
	private EventoService eventoService;

	@Transactional(readOnly = true)
	public List<JobTrasparenza> getDaPubblicare() {
		
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
		stati.add(StatoJob.UPDATED);
		// Considero anche quelli in progress, in base al numero di ore
		stati.add(StatoJob.IN_PROGRESS);
		stati.add(StatoJob.FORCED_SENDING);
//		BooleanExpression controlloDataPubblicazione = QJobTrasparenza.jobTrasparenza.atto.pubblicazioneTrasparenzaNolimit.isTrue();
//		controlloDataPubblicazione = controlloDataPubblicazione.or(QJobTrasparenza.jobTrasparenza.atto.inizioPubblicazioneAlbo.loe(LocalDate.now()));
		BooleanExpression p = QJobTrasparenza.jobTrasparenza.stato.in(stati).and(
				QJobTrasparenza.jobTrasparenza.createdDate.before(DateTime.now().minusSeconds(delaySec)));
		return Lists.newArrayList(jobTrasparenzaRepository.findAll(p));
	}
	
	@Transactional
	public void delete(Long jobId) {
		jobTrasparenzaRepository.delete(jobId);
	}

	@Transactional
	public void inProgress(Long jobId) {
		JobTrasparenza job = jobTrasparenzaRepository.findOne(jobId);
		jobTrasparenzaRepository.updateStato(jobId, StatoJob.IN_PROGRESS.name(), SecurityUtils.getCurrentLogin(), job.getTentativi() != null ? job.getTentativi() + 1 : 1);
	}

	@Transactional
	public void error(Long jobId, String dettaglio) {
		JobTrasparenza job = jobTrasparenzaRepository.findOne(jobId);
		jobTrasparenzaRepository.updateStatoAndErrore(jobId, StatoJob.ERROR.name(), SecurityUtils.getCurrentLogin(), job.getTentativi() + 1, dettaglio);
	}

	@Transactional
	public void done(Long jobId) {
		// Giorgio: modifica logica per aggiornare la data di invio in trasparenza
		JobTrasparenza job = jobTrasparenzaRepository.findOne(jobId);
		// jobTrasparenzaRepository.updateStato(jobId, StatoJob.DONE.name(), SecurityUtils.getCurrentLogin(), job.getTentativi());
		job.setStato(StatoJob.DONE);
		
		String username = SecurityUtils.getCurrentLogin();
		if (!StringUtil.isNull(username)) {
			job.setModifiedBy(username);
		}
		job.setModifiedDate(new Date());
		job.setDettaglioErrore(null);
		job.setDataInvioTrasparenza(new Date());
		
		jobTrasparenzaRepository.save(job);
		
		Atto atto = job.getAtto();
		LocalDate inizioPubblicazione = atto.getDataInizioPubblicazionePresunta();
		if (inizioPubblicazione != null) {
			atto.setDataPubblicazioneTrasparenza(inizioPubblicazione);
			attoRepository.save(atto);
		}
		
		eventoService.saveEvento(EventoEnum.EVENTO_ATTO_PUBBLICATO_TRASPARENZA.getDescrizione(), job.getAtto());
	}

	@Transactional(readOnly = true)
	public List<JobTrasparenza> getAllPubblicazioni() {
		return jobTrasparenzaRepository.findAll();
	}

	@Transactional(readOnly = true)
	public Page<JobTrasparenza> search(JsonObject search, Pageable paginazione) {
		if (search == null) {
			return jobTrasparenzaRepository.findAll(QJobTrasparenza.jobTrasparenza.id.isNotNull(), paginazione);
		}
		else {
			BooleanExpression predicate = QJobTrasparenza.jobTrasparenza.id.isNotNull();
			// inserire qui la logica per impostare i filtri di ricerca
			Page<JobTrasparenza> pubblicazioni = jobTrasparenzaRepository.findAll(predicate, paginazione);
			for (JobTrasparenza jobPubblicazione : pubblicazioni) {
				jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
			}
			return pubblicazioni;
		}
	}

	@Transactional(readOnly = true)
	public Page<JobTrasparenza> findAll(Pageable generatePageRequest) {
		Page<JobTrasparenza> pubblicazioni = jobTrasparenzaRepository.findAll(generatePageRequest);
		for (JobTrasparenza jobPubblicazione : pubblicazioni) {
			jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
		}
		return pubblicazioni;
	}

	@Transactional(readOnly = true)
	public Page<JobTrasparenza> findByAooId(Long aooId, Pageable generatePageRequest) {
		log.debug("findByAooIdAndValidi aooId:" + aooId);
		BooleanExpression predicate = QJobTrasparenza.jobTrasparenza.atto.aoo.id.eq(aooId).or(QJobTrasparenza.jobTrasparenza.atto.aoo.id.isNull());
		Page<JobTrasparenza> pubblicazioni = jobTrasparenzaRepository.findAll(predicate, generatePageRequest);
		for (JobTrasparenza jobPubblicazione : pubblicazioni) {
			jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
		}
		return pubblicazioni;
	}

	@Transactional(readOnly = true)
	public List<String> findErroreByAttoId(Long attoId) {
		return jobTrasparenzaRepository.getDettaglioErroreByAttoId(attoId);
	}

	@Transactional(readOnly = false)
	public void disable(final Long id) {
		log.debug("disable JobPubblicazione with id " + id);
		JobTrasparenza jobPubblicazione = jobTrasparenzaRepository.findOne(id);
		if (jobPubblicazione != null) {
			jobTrasparenzaRepository.save(jobPubblicazione);
		}
	}

	@Transactional(readOnly = true)
	public JobTrasparenza findOne(Long id) {
		return jobTrasparenzaRepository.findOne(id);
	}

	@Transactional(readOnly = true)
	public Page<JobTrasparenza> findAll(Pageable generatePageRequest, JsonObject searchJson, Long profiloId) throws ParseException {
		log.debug("Enter:" + searchJson);
		BooleanExpression predicate = QJobTrasparenza.jobTrasparenza.id.isNotNull();
		if (null != searchJson && !searchJson.isJsonNull()) {

			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
//			if (searchJson.has("aoos")) {
//				List<Long> aoosId = new ArrayList<Long>();
//				for (JsonElement je : searchJson.getAsJsonArray("aoos")) {
//					aoosId.add(je.getAsLong());
//				}
//				if (aoosId.size() > 0) {
//					predicate = predicate.and(QJobTrasparenza.jobTrasparenza.atto.aoo.id.in(aoosId).or(QJobTrasparenza.jobTrasparenza.atto.aoo.isNull()));
//				}
//			}
			if (searchJson.has("esito") && !searchJson.get("esito").isJsonNull()) {
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.stato.stringValue().eq(searchJson.get("esito").getAsString().toUpperCase().trim());
				predicate = predicate.and(p);
			}
			if (searchJson.has("codiceCifra") && !searchJson.get("codiceCifra").isJsonNull()) {
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.codiceCifra.containsIgnoreCase(searchJson.get("codiceCifra").getAsString().trim());
				predicate = predicate.and(p);
			}
			if(searchJson.has("numeroAdozione") && !searchJson.get("numeroAdozione").isJsonNull()){
				String numAdozione = searchJson.get("numeroAdozione").getAsString().trim();
				if(!StringUtil.isNull(numAdozione)) {
					numAdozione = StringUtil.riempiASinistra(numAdozione, "0", 5);
					BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.numeroAdozione.eq(numAdozione);
					predicate = predicate.and(p);
				}
			}
			if (searchJson.has("dataAdozioneStart") && !searchJson.get("dataAdozioneStart").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataAdozioneStart").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.dataAdozione.goe(ld);
				predicate = predicate.and(p);
			}

			if (searchJson.has("dataAdozioneEnd") && !searchJson.get("dataAdozioneEnd").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataAdozioneEnd").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.dataAdozione.loe(ld);
				predicate = predicate.and(p);
			}

			if (searchJson.has("oggetto") && !searchJson.get("oggetto").isJsonNull()) {
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.oggetto.containsIgnoreCase(searchJson.get("oggetto").getAsString().trim());
				predicate = predicate.and(p);
			}

			
			
//			if (searchJson.has("statoPubblicazione") && !searchJson.get("statoPubblicazione").isJsonNull()) {
//				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.statoPubblicazione.containsIgnoreCase(searchJson.get("statoPubblicazione").getAsString().trim());
//				predicate = predicate.and(p);
//			}
			
			if (searchJson.has("dataInvioStart") && !searchJson.get("dataInvioStart").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataInvioStart").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.dataInvioTrasparenza.goe(ld.toDate());
				predicate = predicate.and(p);
			}

			if (searchJson.has("dataInvioEnd") && !searchJson.get("dataInvioEnd").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataInvioEnd").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.dataInvioTrasparenza.loe(ld.toDate());
				predicate = predicate.and(p);
			}
			
			if (searchJson.has("dataInizioPubblicazioneStart") && !searchJson.get("dataInizioPubblicazioneStart").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataInizioPubblicazioneStart").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.dataPubblicazioneTrasparenza.goe(ld);
				predicate = predicate.and(p);
			}

			if (searchJson.has("dataInizioPubblicazioneEnd") && !searchJson.get("dataInizioPubblicazioneEnd").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataInizioPubblicazioneEnd").getAsString().trim()));
				BooleanExpression p = QJobTrasparenza.jobTrasparenza.atto.dataPubblicazioneTrasparenza.loe(ld);
				predicate = predicate.and(p);
			}

			if (searchJson.has("dataFinePubblicazioneStart") && !searchJson.get("dataFinePubblicazioneStart").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataFinePubblicazioneStart").getAsString().trim()));
				BooleanExpression p = (QJobTrasparenza.jobTrasparenza.atto.pubblicazioneTrasparenzaNolimit.isNull().or(QJobTrasparenza.jobTrasparenza.atto.pubblicazioneTrasparenzaNolimit.isFalse())).and(
						QJobTrasparenza.jobTrasparenza.atto.dataFinePubblicazionePresunta.goe(ld)
				);
				predicate = predicate.and(p);
			}

			if (searchJson.has("dataFinePubblicazioneEnd") && !searchJson.get("dataFinePubblicazioneEnd").isJsonNull()) {
				LocalDate ld = new LocalDate(df.parse(searchJson.get("dataFinePubblicazioneEnd").getAsString().trim()));
				BooleanExpression p = (QJobTrasparenza.jobTrasparenza.atto.pubblicazioneTrasparenzaNolimit.isNull().or(QJobTrasparenza.jobTrasparenza.atto.pubblicazioneTrasparenzaNolimit.isFalse())).and(
						QJobTrasparenza.jobTrasparenza.atto.dataFinePubblicazionePresunta.loe(ld)
				);
				predicate = predicate.and(p);
			}

		}

//		if (null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo") && !searchJson.get("aoo").isJsonNull() && !searchJson.get("aoo").getAsString().isEmpty()) {
//			if (searchJson.get("aoo").getAsString().equals("-")) {
//				predicate = predicate.and(QJobTrasparenza.jobTrasparenza.atto.aoo.isNull());
//			}
//			else {
//				predicate = predicate.and(((QJobTrasparenza.jobTrasparenza.atto.aoo.codice.concat(" - ").concat(QJobTrasparenza.jobTrasparenza.atto.aoo.descrizione))
//						.containsIgnoreCase(searchJson.get("aoo").getAsString()))
//								.or((QJobTrasparenza.jobTrasparenza.atto.aoo.codice.concat("-").concat(QJobTrasparenza.jobTrasparenza.atto.aoo.descrizione))
//										.containsIgnoreCase(searchJson.get("aoo").getAsString())));
//			}
//		}
		List<JobTrasparenza> pubblicazioni = (List<JobTrasparenza>) jobTrasparenzaRepository.findAll(predicate, new OrderSpecifier<>(Order.DESC, QJobTrasparenza.jobTrasparenza.id));
		
		long lastIdAtto = -1;
		
		List<JobTrasparenza> list = new ArrayList<JobTrasparenza>();
		
		for (JobTrasparenza jobPubblicazione : pubblicazioni) {
			
			
			jobPubblicazione.setAtto(this.getMinimalAtto(jobPubblicazione.getAtto()));
			
			if(lastIdAtto!=jobPubblicazione.getAtto().getId()) {
				list.add(jobPubblicazione);
				lastIdAtto = jobPubblicazione.getAtto().getId();
			}
		}
		long totaleRisultati = list.size();
		
		list = list.subList(generatePageRequest.getOffset(), 
				Math.min(list.size(), generatePageRequest.getOffset() + generatePageRequest.getPageSize()));
		
		for(JobTrasparenza j : list) {
			boolean canView = attoService.hasAccessGrant(profiloId, j.getAtto().getId(), false);
			j.getAtto().setFullAccess(canView);
		}
		
		//removing recoursive fasiRirerca for client
		for(JobTrasparenza j : list) {
			j.getAtto().getTipoAtto().setFasiRicerca(null);
		}
		return new PageImpl<JobTrasparenza>(list, generatePageRequest, totaleRisultati);
	}

	@Transactional
	public Long richiediNuovaPubblicazione(Long attoId) {
		JobTrasparenza pub = new JobTrasparenza();
		pub.setCreatedDate(DateTime.now());
		pub.setCreatedBy(SecurityUtils.getCurrentLogin());
		pub.setLastModifiedBy(SecurityUtils.getCurrentLogin());
		pub.setStato(StatoJob.NEW);
		pub.setAtto(new Atto(attoId));
		pub.setTentativi(0);
		pub.setLastModifiedDate(DateTime.now());
		pub.setVersion(0);
		pub = jobTrasparenzaRepository.save(pub);
//		jobTrasparenzaRepository.richiediNuovaPubblicazione(SecurityUtils.getCurrentLogin(), StatoJob.NEW.name(), attoId);
		log.debug("richiediNuovaPubblicazione done for attoId " + attoId);
		return pub.getId();
	}

	private Atto getMinimalAtto(Atto atto) {

		Atto newAtto = new Atto();
		newAtto.setId(atto.getId());
		newAtto.setCodiceCifra(atto.getCodiceCifra() != null ? atto.getCodiceCifra() : "");
		newAtto.setDataAdozione(atto.getDataAdozione() != null ? atto.getDataAdozione() : null);
		newAtto.setOggetto(atto.getOggetto() != null ? atto.getOggetto() : "");
		newAtto.setStatoPubblicazione(atto.getStatoPubblicazione() != null ? atto.getStatoPubblicazione() : "");
		newAtto.setStatoProceduraPubblicazione(atto.getStatoProceduraPubblicazione() != null ? atto.getStatoProceduraPubblicazione() : "");
		newAtto.setInizioPubblicazioneAlbo(atto.getInizioPubblicazioneAlbo() != null ? atto.getInizioPubblicazioneAlbo() : null);
		newAtto.setFinePubblicazioneAlbo(atto.getFinePubblicazioneAlbo() != null ? atto.getFinePubblicazioneAlbo() : null);
		newAtto.setNumeroAdozione(atto.getNumeroAdozione() != null ? atto.getNumeroAdozione() : "");
		newAtto.setDataNumerazione(atto.getDataNumerazione()!=null ? atto.getDataNumerazione() : null);
		newAtto.setDataEsecutivita(atto.getDataEsecutivita() != null ? atto.getDataEsecutivita() : null);
		newAtto.setCreatedBy(atto.getCreatedBy() != null ? atto.getCreatedBy() : "");
		newAtto.setEmananteProfilo(atto.getEmananteProfilo() != null ? atto.getEmananteProfilo() : null);
		newAtto.setMotivazioneRichiestaAnnullamento(atto.getMotivazioneRichiestaAnnullamento() != null ? atto.getMotivazioneRichiestaAnnullamento() : null);
		return atto;
	}
}