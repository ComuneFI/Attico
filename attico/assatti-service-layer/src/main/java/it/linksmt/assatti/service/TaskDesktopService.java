package it.linksmt.assatti.service;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.collections4.IteratorUtils;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricIdentityLinkLog;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.task.Task;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.itextpdf.text.pdf.parser.clipper.Clipper.Direction;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.FirmaMassivaDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.JsonUtil;
import it.linksmt.assatti.bpm.util.TipoTaskConverter;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.EsitoParereEnum;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAtto;
import it.linksmt.assatti.datalayer.domain.QAvanzamento;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.TaskPerRicerca;
import it.linksmt.assatti.datalayer.domain.TipologiaRicerca;
import it.linksmt.assatti.datalayer.domain.TipologiaRicercaCodeEnum;
import it.linksmt.assatti.datalayer.domain.UfficioFilterEnum;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.fdr.service.FirmaRemotaService;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.TaskDesktopDTO;
import it.linksmt.assatti.service.dto.TaskDesktopGroupDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.service.util.TaskDesktopEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.RoleCodes;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
public class TaskDesktopService {
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private TipoTaskConverter tipoTaskConverter;
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private HistoryService historyService;
	
	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private RuoloRepository ruoloRepository;
	
	@Inject
	private ServiceUtil serviceUtil;
	
	@Inject
	private BpmWrapperUtil camundaUtil;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private UtenteService utenteService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private TipologiaRicercaService tipologiaRicercaService;
	
	private static final DateFormat DF_FILTER = new SimpleDateFormat("yyyy-MM-dd");
	
	private final Logger log = LoggerFactory.getLogger(TaskDesktopService.class);
	
	public void eseguiFirmaMassiva(final List<String> idTasks, final JsonArray attiGenerazioneFirmaJson, final Long profiloId, final String codiceFiscale,
			final String password, final String otp) throws Exception {
		Map<String, List<DecisioneWorkflowDTO>> mapGenFirmeIdTaskDecisione = new HashMap<String, List<DecisioneWorkflowDTO>>();
		
		log.info("Starting firma massiva process");
		
		boolean checkRequiredFieldEnabled = false;
		try {
			checkRequiredFieldEnabled = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_CHECK_REQUIRED_FIELDS, "false"));
		}catch(Exception e) {}
		
		if(checkRequiredFieldEnabled) {
			for (String idTask : idTasks) {
				Atto atto = workflowService.getAttoByTaskId(idTask);
				if(!attoService.hasRequiredFields(atto.getId(), idTask, profiloId)) {
					throw new GestattiCatchedException("Procedura interrotta in quanto l'atto "+ atto.getCodiceCifra()+ " non ha i dati obbligatori valorizzati.\u003Cbr\u003ESi prega di selezionare soltanto atti con dati completi.");
				}
			}
		}
		log.info("Starting Gestione Parere in Firma massiva - Total elements " + idTasks.size());
		int n = 1;
		for (String idTask : idTasks) {
			log.info("Check Parere " + n++ + " idTask " + idTask);
			
			List<DecisioneWorkflowDTO> lstDecMassiva = serviceUtil.getDecisioneMassiva(idTask, profiloId);
			if (lstDecMassiva == null || lstDecMassiva.isEmpty()) {
				throw new Exception("Impossibile leggere i dati relativi all'operazione massiva da eseguire.");
			}
			Collections.sort(lstDecMassiva, new Comparator<DecisioneWorkflowDTO>() {
		        @Override
		        public int compare(DecisioneWorkflowDTO a, DecisioneWorkflowDTO b) {
		            if (a!= null && a.getTipoParere()!= null && a.getTipoParere().toLowerCase().contains("parere")) return -1;
		            else return 1;
		        }
		    });
			
			for (DecisioneWorkflowDTO decMassiva : lstDecMassiva) {
				if (decMassiva == null) {
					throw new Exception("Impossibile leggere i dati relativi all'operazione massiva da eseguire.");
				}

				log.info("Codice Azione: " + decMassiva.getCodiceAzioneUtente());
				Atto atto = workflowService.getAttoByTaskId(idTask);
				if(decMassiva.getTipoParere()!= null && decMassiva.getTipoParere().toLowerCase().contains("parere")) {
					log.info("Parere " + decMassiva.getTipoParere() + " previsto su idTask " + idTask + " - atto " + atto.getCodiceCifra());
					boolean checkParere = true;
	    			if(atto.getPareri() != null && !atto.getPareri().isEmpty()) {
	    				for (Parere par : atto.getPareri()) {
							if((par.getAnnullato() == null || !par.getAnnullato().booleanValue()) && par.getTipoAzione().getCodice().equalsIgnoreCase(decMassiva.getTipoParere())) {
								checkParere = false;
							}
						}
	    			}

	    			if(checkParere) {
	    				Parere parere = new Parere();
	    				if(decMassiva.getTipoParere().toLowerCase().contains("parere")) {
	    					if(decMassiva.getTipoParere().toLowerCase().contains("contabile")) {
	    						parere.setParereSintetico(EsitoParereEnum.PARERE_FAVOREVOLE_RESP_CONTABILE.name());
	    					}
	    					else if(decMassiva.getTipoParere().toLowerCase().contains("tecnico")) {
	    						parere.setParereSintetico(EsitoParereEnum.PARERE_FAVOREVOLE_RESP_TECNICO.name());
	    					}
	    					
	    				}
	        			parere.setTitolo(decMassiva.getTipoParere());
	        			
	        			try {
	        				transactionService.salvaParereAndEseguiAzione(idTask, profiloId, parere, atto, decMassiva);
	        				log.info("Parere creato  Atto " + atto.getCodiceCifra());
	        			}
	        			catch (Exception ae) {
	        				throw new GestattiCatchedException(ae, "Si \u00E8 verificato un errore durante l'elaborazione dell'Atto " + atto.getCodiceCifra()
	        					+"\u003Cbr\u003ESi prega di riprovare.");
	    				}
	    			}else {
	    				log.info("Parere non creato. Pre-esistente. Atto " + atto.getCodiceCifra());
	    			}
				}else {
					log.info("Parere non previsto Atto " + atto.getCodiceCifra());
					if(!mapGenFirmeIdTaskDecisione.containsKey(idTask)) {
						mapGenFirmeIdTaskDecisione.put(idTask, new ArrayList<DecisioneWorkflowDTO>());
					}
					mapGenFirmeIdTaskDecisione.get(idTask).add(decMassiva);
				}
			}
		}
		log.info("End of Gestione Parere in firma massiva");
		
		//aggiungere sessione di firma
		if(mapGenFirmeIdTaskDecisione!=null && mapGenFirmeIdTaskDecisione.size() > 0) {
			log.info("Starting Firma - Total elements " + mapGenFirmeIdTaskDecisione.size());
			n = 1;	
			Map<String, JsonElement> attiGenerazioneFirmaMap = new HashMap<String, JsonElement>();
    		if(attiGenerazioneFirmaJson!=null) {
	    		for(JsonElement el : attiGenerazioneFirmaJson) {
	    			attiGenerazioneFirmaMap.put(el.getAsJsonObject().get("taskId").getAsString().trim(), el);
	    		}
    		}
	    	if(mapGenFirmeIdTaskDecisione!=null && mapGenFirmeIdTaskDecisione.size() > 0) {
    			int errCount = 0;
    			log.info("Starting sign session");
    			String sessionId = FirmaRemotaService.openSession(codiceFiscale, password, otp);
    			if(sessionId==null || sessionId.trim().isEmpty()) {
    				throw new GestattiCatchedException("Impossibile stabilire la sessione di firma multipla");
    			}else if(sessionId.equalsIgnoreCase("KO-0004")) {
    				throw new GestattiCatchedException("OTP Errato - Si prega di riprovare effettuando una nuova richiesta di OTP");
    			}else if(sessionId.equalsIgnoreCase("KO-0001")) {
    				throw new GestattiCatchedException("Errore generico in fase di firma - Si prega di riprovare");
    			}else if(sessionId.equalsIgnoreCase("KO-0003")) {
    				throw new GestattiCatchedException("Errore in fase di verifica delle credenziali di firma - Si prega di riprovare");
    			}else if(sessionId.equalsIgnoreCase("KO-0009")) {
    				throw new GestattiCatchedException("Credenziali di delega non valide - Si prega di riprovare");
    			}else if(sessionId.equalsIgnoreCase("KO-0010")) {
    				throw new GestattiCatchedException("Impossibile firmare - Il profilo di firma potrebbe essere sospeso");
    			}
    			
    			log.info("Sign session " + sessionId + " started for user " + codiceFiscale);
    			
		    	for(String idTask : mapGenFirmeIdTaskDecisione.keySet()){
	    			try {
	    				log.info("Starting of " + n++ + " idTask " + idTask);
	    				transactionService.generazioneFirma(idTask, mapGenFirmeIdTaskDecisione.get(idTask), attiGenerazioneFirmaMap.get(idTask), profiloId, codiceFiscale, password, otp, sessionId);
	    				log.info("Sign of idTask " + idTask + " end with success");
	    			}catch(Exception e) {
	    				n++;
		    			log.error("errore in firma massiva idTask " + idTask , e);
		    			errCount++;
		    		}
		    	}
		    	
		    	log.info("Closing sign session");
		    	try {
			    	boolean closed = FirmaRemotaService.closeSession(sessionId, codiceFiscale, password, otp);
			    	if(closed) {
			    		log.info("Session " + sessionId + " closed for user " + codiceFiscale);
			    	}else {
			    		log.error("Session " + sessionId + " failed to be closed for user " + codiceFiscale);
			    	}
			    	
		    	}catch(Exception e) {
		    		log.error("Session " + sessionId + " failed to be closed for user " + codiceFiscale);
		    		e.printStackTrace();
		    	}

		    	if(errCount > 0) {
		    		String msg = "";
		    		if(errCount==1) {
		    			msg = "Una firma non &#232; andata a buon fine";
		    		}else {
		    			msg = "Nel processo di firma massiva, " + errCount + " firme risultano non essere andate a buon fine";
		    		}
		    		throw new GestattiCatchedException(msg);
		    	}
	    	}
			
		}
	}

	public Page<TaskDesktopDTO> findAllTaskInCarico(final Pageable paginazione, final JsonObject criteriJson) throws Exception {
		return workflowService.getAllTaskAssegnati(paginazione, criteriJson);
	}
	
	public List<TaskDesktopGroupDTO> findRagioneriaGroup(JsonObject search, String taskType, final Iterable<Profilo> profili, final Integer firstPageSize) throws Exception{
		UfficioFilterEnum ufficioFilter = null;
		if(JsonUtil.hasFilter(search, "ufficioFilter")) {
			ufficioFilter = UfficioFilterEnum.getByKey(StringUtil.trimStr(search.get("ufficioFilter").getAsString().trim()));
		}
		List<TaskDesktopGroupDTO> groups = workflowService.findRagioneriaGroup(search, taskType, profili, firstPageSize, ufficioFilter);
		return groups;
	}
	
	public List<TaskDesktopGroupDTO> findMonitoraggioGroup(JsonObject search, String taskType, final Iterable<Profilo> profili, final Integer firstPageSize) throws Exception{
		UfficioFilterEnum ufficioFilter = null;
		if(JsonUtil.hasFilter(search, "ufficioFilter")) {
			ufficioFilter = UfficioFilterEnum.getByKey(StringUtil.trimStr(search.get("ufficioFilter").getAsString().trim()));
		}
		List<TaskDesktopGroupDTO> groups = workflowService.findMonitoraggioGroup(search, taskType, profili, firstPageSize, ufficioFilter);
		return groups;
	}

	public Page<TaskDesktopDTO> findTasks(String taskType, final Iterable<Profilo> profili, 
			final Pageable paginazione, JsonObject criteriJson, Aoo aooRaggruppamento) throws Exception {
		
		boolean checkTesto = false;
        try {
        	checkTesto = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_TESTO_CHECK_NULL, "false"));
        }catch(Exception e) {
        	log.error("Errore nel parsing della property checkTesto");
        }
		
		String azioneDaEffettuare = null;
		String assegnatario = null;
		Date assegnazioneAfter = null;
		Date assegnazioneBefore = null;
		String inLavorazione = null;
		
		String taskId = null;
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			azioneDaEffettuare = StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim());
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inLavorazione")) {
			inLavorazione = StringUtil.trimStr(criteriJson.get("inLavorazione").getAsString().trim());
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inCaricoA")) {
			assegnatario = 	StringUtil.trimStr(criteriJson.get("inCaricoA").getAsString().trim());
		}
		
		if(JsonUtil.hasFilter(criteriJson, "taskId")) {
			taskId = StringUtil.trimStr(criteriJson.get("taskId").getAsString().trim());
		}
		
		UfficioFilterEnum ufficioFilter = null;
		if(JsonUtil.hasFilter(criteriJson, "ufficioFilter")) {
			ufficioFilter = UfficioFilterEnum.getByKey(StringUtil.trimStr(criteriJson.get("ufficioFilter").getAsString().trim()));
		}
		
		//range data assegnazione
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataAssegnazioneStart")) {
				assegnazioneAfter = DF_FILTER.parse(criteriJson.get("dataAssegnazioneStart").getAsString().trim());
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataAssegnazioneStart'", e);
		}
		
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataAssegnazioneEnd")) {
				assegnazioneBefore = DF_FILTER.parse(criteriJson.get("dataAssegnazioneEnd").getAsString().trim());
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataAssegnazioneEnd'", e);
		}
		
		List<Task> listTaskbpmTemp = null;
		if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.IN_CARICO.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskAssegnati(profili, 
					azioneDaEffettuare, assegnatario, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.IN_ARRIVO.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskInArrivo(profili, 
					azioneDaEffettuare, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter, inLavorazione);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.VISTO_MASSIVO.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskAssegnati(profili,
					azioneDaEffettuare, assegnatario, assegnazioneAfter, assegnazioneBefore, taskId, 
					AttoProcessVariables.TASK_OPERAZIONE_MASSIVA, AttoProcessVariables.TASK_VAL_VISTO_MASSIVO 
						+ AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + "%", ufficioFilter);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.FIRMA_MASSIVA.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskAssegnati(profili,
					azioneDaEffettuare, assegnatario, assegnazioneAfter, assegnazioneBefore, taskId, 
					AttoProcessVariables.TASK_OPERAZIONE_MASSIVA, AttoProcessVariables.TASK_VAL_FIRMA_MASSIVA 
						+ AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + "%", ufficioFilter);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO.getDescrizione()) || StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO_GROUP.getDescrizione())) {
			
			List<Profilo> profAoo = null;
			String filterAooStr = null;
			
			if (criteriJson.get("filterAooId") != null) {
				filterAooStr = criteriJson.get("filterAooId").getAsString().trim();
			}
			if (!StringUtil.isNull(filterAooStr)) {
				try {
					long aooId = Long.parseLong(filterAooStr);
					Aoo aooRif = aooRepository.findOne(aooId);
					
					profAoo = profiloRepository.findByAooId(aooRif.getId().longValue());
				}
				catch (Exception e) {
					log.error("Impossibile convertire il valore: " + criteriJson.get("filterAooId"));
				}
			}
			else {
				if(aooRaggruppamento!=null) {
					List<Aoo> aoos = new ArrayList<Aoo>();
					aoos.add(aooRaggruppamento);
					profAoo = profiloService.findProfiloByAooList(aoos);
				}else	if ((profili != null) && !Iterables.isEmpty(profili)) {
					Aoo aooRif = profili.iterator().next().getAoo();
					List<Aoo> aoos = aooService.getAooRicorsiva(aooRif.getId());
					profAoo = profiloService.findProfiloByAooList(aoos);
				}
			}
			
			listTaskbpmTemp = workflowService.getTaskAssegnati(profAoo, 
					azioneDaEffettuare, assegnatario, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter);
			List<Task> taskInCoda = new ArrayList<Task>();
			if(aooRaggruppamento!=null && assegnatario==null) {
				
				taskInCoda = StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO.getDescrizione())?
						workflowService.getTaskInArrivo(profAoo , 
						azioneDaEffettuare, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter, "N"):
							workflowService.getTaskInArrivoPerMonitoraggioGroup(profAoo , 
									azioneDaEffettuare, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter, "N");
			}else {
				//prendo i profili degli uffici della sottostruttura per riprendere gli atti in coda
				Set<Aoo> aooDellaStessaSottostruttura = null;
				List<Profilo> profiliDellaSottostruttura = null;
					Aoo aooRif = profili.iterator().next().getAoo();
					if(aooRif != null) {
					Aoo aooDirezione = aooService.getAooDirezione(aooRif);
					if(aooDirezione!=null) {
						aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), aooRif, true);
					}
					
					if(aooDellaStessaSottostruttura!=null && aooDellaStessaSottostruttura.size()>0) {
						profiliDellaSottostruttura = profiloService.findProfiloByAooList(aooDellaStessaSottostruttura);
					}
				}
					
					taskInCoda =StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO.getDescrizione())? workflowService.getTaskInArrivo(profiliDellaSottostruttura , 
							azioneDaEffettuare, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter, "N"):
								 workflowService.getTaskInArrivoPerMonitoraggioGroup(profiliDellaSottostruttura , 
											azioneDaEffettuare, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter, "N");
			}
			
			listTaskbpmTemp.addAll(taskInCoda);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.CARICHI_ASSESSORI.getDescrizione())) {
			
			ArrayList<Long> listIdRuolo = new ArrayList<Long>();
			List<Ruolo> foundAssessore = ruoloRepository.findByCodice(RoleCodes.ROLE_COMPONENTE_GIUNTA);
			if (foundAssessore != null && foundAssessore.size() > 0) {
				for (Ruolo ruolo : foundAssessore) {
					listIdRuolo.add(ruolo.getId());
				}
				
				List<Profilo> profAssessori = profiloService.findByRuoloAoo(listIdRuolo, null);
				listTaskbpmTemp = workflowService.getTaskAssegnati(profAssessori, 
						azioneDaEffettuare, assegnatario, assegnazioneAfter, assegnazioneBefore, taskId, ufficioFilter);
			}
			else {
				listTaskbpmTemp = new ArrayList<Task>();
			}
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.COORDINAMENTO_TESTO_CONSIGLIO.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskInCoordinametoTesto(profili, false, ufficioFilter);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.COORDINAMENTO_TESTO_GIUNTA.getDescrizione())) {
			listTaskbpmTemp = workflowService.getTaskInCoordinametoTesto(profili, true, ufficioFilter);
		}else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_CONSIGLIO.getDescrizione()) || StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione())) {
			List<String> taskDefs = new ArrayList<String>();
			
			String code = StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_CONSIGLIO.getDescrizione())?TipologiaRicercaCodeEnum.POST_SEDUTA_CONSIGLIO.name():TipologiaRicercaCodeEnum.POST_SEDUTA_GIUNTA.name();
			List<TipologiaRicerca> tipologiaRicercas = tipologiaRicercaService.findByCode(code);
			
			if(tipologiaRicercas!=null && tipologiaRicercas.size()>0) {
				
				TipologiaRicerca tipologiaRicerca = tipologiaRicercaService.findOne(tipologiaRicercas.get(0).getId());
				
				if(tipologiaRicerca.getTasksPerRicerca()!=null) {
					for (TaskPerRicerca task : tipologiaRicerca.getTasksPerRicerca()) {
						taskDefs.add(task.getKeycode());
					}
				}
				
				listTaskbpmTemp = workflowService.getTaskInPostSeduta(profili, ufficioFilter,taskDefs);
			}
		}
		else if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.ATTI_IN_RAGIONERIA_PER_ARRIVO.getDescrizione())) {
			return workflowService.getTaskInRagioneria(criteriJson, paginazione, false, ufficioFilter, profili);
		}else if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.ATTI_IN_RAGIONERIA_PER_SCADENZA.getDescrizione())) {
			return workflowService.getTaskInRagioneria(criteriJson, paginazione, true, ufficioFilter, profili);
		}else if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.RIASSEGNAZIONE.getDescrizione())) {
			return workflowService.getTaskRiassegnazione(criteriJson, paginazione, profili);
		}else if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.ATTUAZIONE_IN_SCADENZA.getDescrizione())) {
			return workflowService.getTaskAttuazioneInScadenza(criteriJson, paginazione, profili);
		}
		
		List<TaskDesktopDTO> lista = new ArrayList<TaskDesktopDTO>();
		long totaleRisultati = 0;
		
		if (listTaskbpmTemp != null) {
			Map<Long, Task> taskMap = new HashMap<Long, Task>();
			List<Long> idAttoTasks = new ArrayList<Long>();
			
			for (Task task : listTaskbpmTemp) {
				String businessKey = workflowService.getBusinessKey(task.getProcessInstanceId());
				
				// La business key deve corrispondere all'id Atto
				long idAtto = Long.parseLong(businessKey.trim());
				idAttoTasks.add(idAtto);
				
				taskMap.put(idAtto, task);
			}
			
			if (checkTesto && (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.FIRMA_MASSIVA.getDescrizione())
					||StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.VISTO_MASSIVO.getDescrizione()))) {
				criteriJson.addProperty("onlyWithText", "true");
			}
			Predicate expression = null;
			
			
			if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_CONSIGLIO.getDescrizione()) || StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione())) {
				
				List<Execution> listExecTemp = workflowService.getAttesaEsitoSeduta(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione()));
				
				for (Execution exec : listExecTemp) {
					String businessKey = workflowService.getBusinessKey(exec.getProcessInstanceId());
					
					long idAtto = Long.parseLong(businessKey.trim());
					idAttoTasks.add(idAtto);
				}
				
				expression = getFilterQueryPostSeduta(criteriJson, idAttoTasks, null, StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione()));
			}else {
				expression = getFilterQuery(criteriJson, idAttoTasks, null);
			}
			
			
			
						
			Iterable<Atto> listAttoFilter = attoRepository.findAll(expression,
					new QSort(QAtto.atto.lastModifiedDate.desc()).getOrderSpecifiers().get(0));
			
			// Filtraggio sui parametri collegati agli avanzamenti possibile solo a posteriori
			List<Atto> listAttoTemp = filterByAvanzamento(listAttoFilter, criteriJson);
			totaleRisultati = listAttoTemp.size();
			
			if (paginazione != null && totaleRisultati > 0) {
				listAttoTemp = listAttoTemp.subList(paginazione.getOffset(), 
						Math.min(listAttoTemp.size(), paginazione.getOffset() + paginazione.getPageSize()));
			}

			for(Atto atto : listAttoTemp) {
//				if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_CONSIGLIO.getDescrizione()) || StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione())) {
//					//controllo il tipoAtto
//					
//					if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_CONSIGLIO.getDescrizione()) && (atto.getTipoAtto().getConsiglio() == null || !atto.getTipoAtto().getConsiglio().booleanValue())) {
//						 continue;
//					}
//					if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.POST_SEDUTA_GIUNTA.getDescrizione()) && (atto.getTipoAtto().getGiunta() == null || !atto.getTipoAtto().getGiunta().booleanValue())) {
//						continue;
//					}
//				}
				TaskDesktopDTO curTaskDesktopDTO = new TaskDesktopDTO();
				curTaskDesktopDTO.setHighLighted(false);
				
				TipoTaskDTO taskBpm = tipoTaskConverter.toTipoTask(taskMap.get(atto.getId()), atto);
				if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.FIRMA_MASSIVA.getDescrizione())) {
					FirmaMassivaDTO firmaDto = new FirmaMassivaDTO();
					VariableInstance varFirmaMassiva = workflowService.getVariabileTask(taskMap.get(atto.getId()).getId(), AttoProcessVariables.TASK_OPERAZIONE_MASSIVA);
					if(varFirmaMassiva.getValue()!=null && varFirmaMassiva.getValue() instanceof String) {
						if(((String)varFirmaMassiva.getValue()).endsWith(AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + AttoProcessVariables.TASK_VAL_FIRMA_ATTO)) {
							firmaDto.setType(AttoProcessVariables.TASK_VAL_FIRMA_ATTO);
						}else if(((String)varFirmaMassiva.getValue()).endsWith((AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + AttoProcessVariables.TASK_VAL_NUMERA_GENERA_FIRMA_ATTO))) {
							firmaDto.setType(AttoProcessVariables.TASK_VAL_NUMERA_GENERA_FIRMA_ATTO);
							VariableInstance varTipiDocumentoFirma = workflowService.getVariabileTask(taskMap.get(atto.getId()).getId(), AttoProcessVariables.LISTA_DOCUMENTI_FIRMA);
							if(varTipiDocumentoFirma.getValue()!=null && varTipiDocumentoFirma.getValue() instanceof ArrayList) {
								@SuppressWarnings("unchecked")
								List<String> codTipiDocumento = (ArrayList<String>)varTipiDocumentoFirma.getValue();
								List<Long> modelliIds = modelloHtmlService.findIdModelloByCodiciTipiAtto(codTipiDocumento);
								firmaDto.setModelli(modelliIds);
							}
						}else if(((String)varFirmaMassiva.getValue()).endsWith((AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + AttoProcessVariables.TASK_VAL_GENERA_FIRMA_ATTO))
								|| ((String)varFirmaMassiva.getValue()).endsWith((AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + AttoProcessVariables.TASK_VAL_GENERA_FIRMA_REG_TECN))
								|| ((String)varFirmaMassiva.getValue()).endsWith((AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP + AttoProcessVariables.TASK_VAL_GENERA_FIRMA_REG_CONT))) {
							String[] lstType = ((String)varFirmaMassiva.getValue()).split(AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP);
							firmaDto.setType(lstType[1]);
							if(lstType.length>2) {
								for (int i = 1; i < lstType.length; i++) {
									if(lstType[i]!= null && lstType[i].toLowerCase().contains("firma")) {
										firmaDto.setType(lstType[i]);
										break;
									}
								}
							}
							else {
									
							}
							VariableInstance varTipiDocumentoFirma = workflowService.getVariabileTask(taskMap.get(atto.getId()).getId(), AttoProcessVariables.LISTA_DOCUMENTI_FIRMA);
							if(varTipiDocumentoFirma.getValue()!=null && varTipiDocumentoFirma.getValue() instanceof ArrayList) {
								@SuppressWarnings("unchecked")
								List<String> codTipiDocumento = (ArrayList<String>)varTipiDocumentoFirma.getValue();
								List<Long> modelliIds = modelloHtmlService.findIdModelloByCodiciTipiAtto(codTipiDocumento);
								firmaDto.setModelli(modelliIds);
							}
						}
					}
					List<String> tipiSolaGenerazione = workflowService.getElencoDocumentiSolaGenerazione(taskBpm.getId());
					if(tipiSolaGenerazione!=null && tipiSolaGenerazione.size() > 0) {
						List<Long> modelliIdsSolaGenerazione = modelloHtmlService.findIdModelloByCodiciTipiAtto(tipiSolaGenerazione);
						firmaDto.setModelliSolaGenerazione(modelliIdsSolaGenerazione);
					}
					taskBpm.setFirmaDto(firmaDto);
				}
				if(taskBpm!= null && taskBpm.getIdAssegnatario()!=null && !taskBpm.getIdAssegnatario().trim().isEmpty()) {
					Profilo profilo = camundaUtil.getProfiloByUsernameBpm(taskBpm.getIdAssegnatario().trim());
					if(profilo!=null) {
						if(profilo.getAoo()!=null) {
							taskBpm.setUfficioGiacenza("(" + profilo.getAoo().getCodice() + ") " + profilo.getAoo().getDescrizione());
						}else {
							Aoo aooProf = aooService.findAooByProfiloId(profilo.getId());
							if(aooProf!=null) {
								taskBpm.setUfficioGiacenza("(" + aooProf.getCodice() + ") " + aooProf.getDescrizione());
							}
						}
						if(profilo.getValidita()!=null && profilo.getValidita().getValidoal() != null) {
							curTaskDesktopDTO.setHighLighted(true);
						}
					}
				}
				
				VariableInstance varRagioneria = null;
				VariableInstance varPredisposizione = null;
				
				try {
					varRagioneria = workflowService.getVariabileTask(
							taskMap.get(atto.getId()).getId(), AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				try {
					varPredisposizione = workflowService.getVariabileTask(
							taskMap.get(atto.getId()).getId(), AttoProcessVariables.VAR_ATTO_IN_PREDISPOSIZIONE);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (varRagioneria != null && atto.getDatiContabili() != null && 
						atto.getDatiContabili().getNumArriviRagioneria() != null &&
						atto.getDatiContabili().getNumArriviRagioneria().intValue() > 1) {
					
					curTaskDesktopDTO.setHighLighted(true);
					curTaskDesktopDTO.setTrasformazioneWarning(atto.getDatiContabili().getTrasformazioneWarning());
					curTaskDesktopDTO.setNumArriviRagioneria(atto.getDatiContabili().getNumArriviRagioneria());
				}
				
				if(varPredisposizione==null) {
					if (atto.getAvanzamento() != null && (atto.getAvanzamento().size() > 0)) {
						Avanzamento lastAv = atto.getAvanzamento().iterator().next();
						if (!StringUtil.isNull(lastAv.getNote()) && lastAv.getWarningType() != null && !lastAv.getWarningType().isEmpty()) {
							curTaskDesktopDTO.setHighLighted(true);
							curTaskDesktopDTO.setNoteStepPrecedente(StringUtil.trimStr(lastAv.getNote()));
						}
						else {
							curTaskDesktopDTO.setNoteStepPrecedente(null);
						}
					}
				}else {
					if (atto.getAvanzamento() != null && (atto.getAvanzamento().size() > 0)) {
						Iterator<Avanzamento> it = atto.getAvanzamento().iterator();
						boolean isLast = true;
						while(it.hasNext()) {
							Avanzamento lastAv = it.next();
							if (!StringUtil.isNull(lastAv.getNote()) && lastAv.getWarningType() != null && !lastAv.getWarningType().isEmpty()) {
								if(isLast || lastAv.getWarningType().equalsIgnoreCase("RESTITUISCI_PROPONENTE")) {
									curTaskDesktopDTO.setNoteStepPrecedente(StringUtil.trimStr(lastAv.getNote()));
									curTaskDesktopDTO.setHighLighted(true);
									break;
								}
							}
							
							if(isLast) {
								isLast = false;
							}
						}
					}
				}
				
				if (StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.CARICHI_ASSESSORI.getDescrizione())) {
					List<Task> tasks = workflowService.getTaskAttiviAssessori(atto);
					
					if(tasks!=null) {
						for (Task task: tasks) {
							if(task.getAssignee()!=null) {
								TipoTaskDTO taskBpmAssessore = tipoTaskConverter.toTipoTask(task, atto);
								TaskDesktopDTO taskAssessore = new TaskDesktopDTO();
								taskAssessore.setHighLighted(curTaskDesktopDTO.getHighLighted());
								taskAssessore.setTrasformazioneWarning(curTaskDesktopDTO.getTrasformazioneWarning());
								taskAssessore.setNumArriviRagioneria(curTaskDesktopDTO.getNumArriviRagioneria());
								taskAssessore.setNoteStepPrecedente(curTaskDesktopDTO.getNoteStepPrecedente());
								taskAssessore.setTaskBpm(taskBpmAssessore);
								
								Long idProfiloAssegnatario = camundaUtil.getIdProfiloByUsernameBpm(task.getAssignee());
								if (serviceUtil.hasRuolo(idProfiloAssegnatario, RoleCodes.ROLE_COMPONENTE_GIUNTA)) {
									lista.add(taskAssessore);
								}
								
								
							}
						}
					}
					
					
					
				}
				else {
					if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.FIRMA_MASSIVA.getDescrizione()) || StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.VISTO_MASSIVO.getDescrizione())) {
						curTaskDesktopDTO.setAtto(attoService.findOneScrivaniaBasic(atto.getId()));
					}
					curTaskDesktopDTO.setTaskBpm(taskBpm);
					lista.add(curTaskDesktopDTO);
				}
				
			}
		}
		else {
			
			boolean filtraPerProfili = StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.LAVORATI.getDescrizione());
			
			String listaProfiliSql = "";
			for(Profilo p : profili){
				if(!listaProfiliSql.isEmpty()) {
					listaProfiliSql += ",";
				}
				listaProfiliSql += p.getId();
			}
			
			String listaAooSql = null;

			if(profili!=null && ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
				listaAooSql = "";
				for(Profilo p : profili){
					if(!listaAooSql.isEmpty()) {
						listaAooSql += ",";
					}
					listaAooSql += p.getAoo().getId();
				}
			}
			
			
			Page<Object> page = workflowService.getLavorati(listaProfiliSql, paginazione, criteriJson, ufficioFilter, listaAooSql);
			totaleRisultati = page.getTotalElements();
			List<Long> avIds = new ArrayList<Long>();
			for(Object obj : page) {
				avIds.add(((BigInteger)obj).longValue());
			}
			
			List<Avanzamento> avanzamenti = Lists.newArrayList(avanzamentoRepository.findAll(QAvanzamento.avanzamento.id.in(avIds), new OrderSpecifier<>(Order.DESC, QAvanzamento.avanzamento.dataAttivita)));
			
			for(Avanzamento av : avanzamenti) {
				TaskDesktopDTO cur = new TaskDesktopDTO();
				cur.setHighLighted(false);
				
				TipoTaskDTO taskBpm = tipoTaskConverter.toTipoTask(av,profili,filtraPerProfili);
				if(taskBpm.getIdAssegnatario()!=null && !taskBpm.getIdAssegnatario().trim().isEmpty()) {
					Profilo profilo = camundaUtil.getProfiloByUsernameBpm(taskBpm.getIdAssegnatario().trim());
					if(profilo!=null) {
						taskBpm.setUfficioGiacenza("(" + profilo.getAoo().getCodice() + ") " +
								profilo.getAoo().getDescrizione());
						if(profilo.getValidita()!=null && profilo.getValidita().getValidoal() != null) {
							cur.setHighLighted(true);
						}
					}
				}
				
				String azioneDaEffettuareVis = "";
				String idAssegnatario = "";
				String candidateGroups ="";
				taskBpm.setAssegnatarioName("");
				List<Task> tasks = workflowService.getTaskByIdAtto(Long.parseLong(taskBpm.getBusinessKey()));
				for (Iterator<Task> iterator = tasks.iterator(); iterator.hasNext();) {
					Task task = (Task) iterator.next();
					
					if(task!=null) {
						if(!azioneDaEffettuareVis.isEmpty()) {
							azioneDaEffettuareVis += ", ";
						}
						azioneDaEffettuareVis += task.getName();
						if(task.getAssignee() != null) {
							idAssegnatario = task.getAssignee();
							if(!taskBpm.getAssegnatarioName().trim().isEmpty()) {
								taskBpm.setAssegnatarioName(taskBpm.getAssegnatarioName() + ", ");
							}
							taskBpm.setAssegnatarioName(taskBpm.getAssegnatarioName() + utenteService.getNameByUser(camundaUtil.getUsernameByUsernameBpm(idAssegnatario)));
						}else {
							List<String> cg = camundaUtil.getCandidategroups(task.getId());
							if (cg != null) {
								
								for (int i = 0; i < cg.size(); i++) {
									
									String codiceRuolo = cg.get(i).indexOf("!")>-1?
											cg.get(i).substring(0,cg.get(i).indexOf("!")-1)
											:cg.get(i);
									
									List<Ruolo> ruoli =  ruoloRepository.findByCodice(codiceRuolo);
									
									for (int j = 0; j < ruoli.size(); j++) {
										if(!candidateGroups.isEmpty()) {
											candidateGroups += ",";
										}
										candidateGroups += ruoli.get(j).getDescrizione();
									}
								}
							}

						}
					}
					
				}
				
				taskBpm.setAzioneDaEffettuare(azioneDaEffettuareVis );
				taskBpm.setIdAssegnatario(idAssegnatario);
				taskBpm.setCandidateGroups(candidateGroups);
				cur.setTaskBpm(taskBpm);
				if( azioneDaEffettuare == null || azioneDaEffettuare.isEmpty() || 
					taskBpm.getUltimaAzione().toLowerCase().contains(azioneDaEffettuare.toLowerCase())) {
					lista.add(cur);
				}
			}
		}
				
		Page<TaskDesktopDTO> foundPage = null;
		if(paginazione==null){
			foundPage = new PageImpl<TaskDesktopDTO>(lista);
		}
		else if(totaleRisultati > 0){			
			foundPage = new PageImpl<TaskDesktopDTO>(lista, paginazione, totaleRisultati);
		}
		else{
			foundPage = new PageImpl<TaskDesktopDTO>(new ArrayList<TaskDesktopDTO>());
		}
		
		return foundPage;
	}
	
	public Page<Atto> findAttesaEsito(
			boolean isOdgGiunta, boolean includiSospesi, String tipoRicerca,
			final Pageable paginazione, JsonObject criteriJson) 
					throws DatatypeConfigurationException, ServiceException {
		
		boolean isQuartieriRevisori = "QUART_REV".equalsIgnoreCase(StringUtil.trimStr(tipoRicerca));
		List<Long> idAttoTasks = new ArrayList<Long>();
		List<Execution> listExecTemp = workflowService.getAttesaEsitoSeduta(isOdgGiunta);
		
		for (Execution exec : listExecTemp) {
			String businessKey = workflowService.getBusinessKey(exec.getProcessInstanceId());
			
			long idAtto = Long.parseLong(businessKey.trim());
			if(!"PAR_COMM".equalsIgnoreCase(StringUtil.trimStr(tipoRicerca))) {
				idAttoTasks.add(idAtto);
			}else {
				boolean existsConfComm = configurazioneIncaricoService.existsConfIncaricoByConfTask(
						idAtto, ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
				if(existsConfComm) {
					idAttoTasks.add(idAtto);
				}
			}
		}
				
		String[] tipiAtto = WebApplicationProps.getProperty(
				ConfigPropNames.PARERE_COMMISSIONI_TIPI_ATTO).split(" ");
		
		if (isQuartieriRevisori) {
			tipiAtto = WebApplicationProps.getProperty(
					ConfigPropNames.PARERE_QUARTIERE_REVISORE_TIPI_ATTO).split(" ");
		}
		
		Predicate expression = getFilterQuery(criteriJson, idAttoTasks, tipiAtto);
		
		Iterable<Atto> listAttoTemp = attoRepository.findAll(expression,
				new QSort(QAtto.atto.lastModifiedDate.desc()).getOrderSpecifiers().get(0),
				new QSort(QAtto.atto.codiceCifra.desc()).getOrderSpecifiers().get(0));
		
		// Filtro quelli con esito confermato
		List<Atto> lista = new ArrayList<Atto>();
		for (Atto atto : listAttoTemp) {
			
			if(atto.getStato().equalsIgnoreCase(
					SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
							SedutaGiuntaConstants.organoSeduta.C.name()))){
				lista.add(atto);
			}else {
				if(atto.getOrdineGiornos() != null) {
					Iterator<AttiOdg> attiOdg = atto.getOrdineGiornos().iterator();
					while (attiOdg.hasNext()) {
						AttiOdg curAttoOdg = (AttiOdg) attiOdg.next();
						if ((curAttoOdg.getBloccoModifica() == null || curAttoOdg.getBloccoModifica().equals(false)) &&
								curAttoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano().equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.C.name())
						) {
							lista.add(atto);
							break;
						}
					}
				}
			}
		}		

		Page<Atto> foundPage = null;
		if(paginazione==null){
			cleanInfoAtti(lista);
			foundPage = new PageImpl<Atto>(lista);
		}
		else if(lista.size() > 0) {
			long totaleRisultati = lista.size();
			try {
				lista = lista.subList(paginazione.getOffset(), 
						Math.min(lista.size(), paginazione.getOffset() + paginazione.getPageSize()));
			}catch(Exception e) {}
			cleanInfoAtti(lista);
			foundPage = new PageImpl<Atto>(lista, paginazione, totaleRisultati);
		}
		else {
			foundPage = new PageImpl<Atto>(new ArrayList<Atto>());
		}
		if("PAR_COMM".equalsIgnoreCase(StringUtil.trimStr(tipoRicerca))) {
			this.inserisciInfoCommissioni(foundPage);
		}
		return foundPage;
	}
	
	public Page<Atto> findAttiCommissioni(
			boolean isOdgGiunta, boolean includiSospesi, String tipoRicerca,
			final Pageable paginazione, JsonObject criteriJson) 
					throws DatatypeConfigurationException, ServiceException {
		
		ArrayList<String> esitiAtto = new ArrayList<String>(Arrays.asList(WebApplicationProps.getProperty(
				ConfigPropNames.PARERE_COMMISSIONI_EDITABLE_ESITI_ATTO).split(" "))) ;
		
		TipologiaRicercaCodeEnum tipoCode = "PAR_COMM".equalsIgnoreCase(StringUtil.trimStr(tipoRicerca)) ? 
				TipologiaRicercaCodeEnum.COMM_CONS:TipologiaRicercaCodeEnum.CONS_QUART_REV_CONT;
		Page<Atto> foundPage = workflowService.findAttiRicercaCommissione(tipoCode, paginazione, criteriJson);
		for (Atto atto : foundPage.getContent()) {
			atto.setEditCommissioniEnable(true);
			if(atto.getOrdineGiornos()!= null && !atto.getOrdineGiornos().isEmpty()) {
				for (AttiOdg attoOdg : atto.getOrdineGiornos()) {
					if(attoOdg != null && attoOdg.getEsito() != null && esitiAtto!= null && esitiAtto.contains(attoOdg.getEsito())) {
						atto.setEditCommissioniEnable(false);
					}
				}
			}
		}		
		cleanInfoAtti(foundPage.getContent());
		if("PAR_COMM".equalsIgnoreCase(StringUtil.trimStr(tipoRicerca))) {
			this.inserisciInfoCommissioni(foundPage);
		}
		return foundPage;
	}

	@SuppressWarnings("unchecked")
	private void inserisciInfoCommissioni(Page<Atto> foundPage) throws ServiceException {
		if(foundPage!=null && foundPage.getContent()!=null && foundPage.getContent().size() > 0) {
			List<Atto> attos = foundPage.getContent();
			for(Atto atto : attos) {
				@SuppressWarnings("rawtypes")
				List list = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
				atto.setObjs(list);
			}
		}
	}
	
	private void cleanInfoAtti(Iterable<Atto> listAttoTemp) {
		for (Atto atto : listAttoTemp) {
			// Inserimento delle informazioni minime
			atto.setSedutaGiunta(null);
			atto.setOrdineGiornos(null);
			atto.setAvanzamento(null);
			
			// Riporto i pareri inseriti
			Atto minAtto = DomainUtil.minimalAtto(atto);
			if (atto.getPareri() != null) {
				
				Set<Parere> cleanPareri = new HashSet<Parere>();
				for (Parere parere : atto.getPareri()) {
					
					// Salto i pareri eventualmente annullati
					if ((parere.getAnnullato() != null && parere.getAnnullato().booleanValue())) {
						continue;
					}
					
					Aoo aooMinParere = DomainUtil.minimalAoo(parere.getAoo());
					parere.setAoo(aooMinParere);
					parere.setAtto(minAtto);
					parere.getTipoAzione();

					if (parere.getDocumentiPdf() != null) {
						for (DocumentoPdf docParere : parere.getDocumentiPdf()) {
							docParere.setAttoId(null);
							docParere.setParereId(null);
							if (docParere.getFile() != null) {
								docParere.getFile().getNomeFile();
							}
						}
					}

					if (parere.getAllegati() != null) {
						for (DocumentoInformatico allegato : parere.getAllegati()) {
							allegato.setAtto(null);
							allegato.setParere(null);
							if (allegato.getFile() != null) {
								allegato.getFile().getNomeFile();
							}
						}
					}
					cleanPareri.add(parere);
				}
				atto.setPareri(cleanPareri);
			}
							
			workflowService.loadAllTasks(atto);
		}
	}
	
	public Page<Atto> findPostSeduta(boolean isOdgGiunta, boolean includiSospesi,
			final Pageable paginazione, JsonObject criteriJson) throws DatatypeConfigurationException, ServiceException {
	
		List<Atto> lista = new ArrayList<Atto>();
		
		
		List<Long> idAttos = new ArrayList<Long>();
		HashMap<Long, String> idAttosInAttesaDiEsito = new HashMap<Long, String>();
		
		HashMap<Long, String> attoTask = new HashMap<Long, String>();
		List<Execution> listExecTemp = workflowService.getAttesaEsitoSeduta(isOdgGiunta);
		
		for (Execution exec : listExecTemp) {
			String businessKey = workflowService.getBusinessKey(exec.getProcessInstanceId());
			
			long idAtto = Long.parseLong(businessKey.trim());
			idAttos.add(idAtto);
			idAttosInAttesaDiEsito.put(idAtto,"");
		}
		
		List<String> taskDefs = new ArrayList<String>();
		
		String code = isOdgGiunta?TipologiaRicercaCodeEnum.POST_SEDUTA_GIUNTA.name():TipologiaRicercaCodeEnum.POST_SEDUTA_CONSIGLIO.name();
		List<TipologiaRicerca> tipologiaRicercas = tipologiaRicercaService.findByCode(code);
		TipologiaRicerca tipologiaRicerca = null;
		if(tipologiaRicercas!=null && tipologiaRicercas.size()>0) {
			
			tipologiaRicerca = tipologiaRicercaService.findOne(tipologiaRicercas.get(0).getId());
			
			if(tipologiaRicerca.getTasksPerRicerca()!=null) {
				for (TaskPerRicerca task : tipologiaRicerca.getTasksPerRicerca()) {
					taskDefs.add(task.getKeycode());
				}
			}
			
			List<Task> listTaskbpmTemp = workflowService.getTaskInPostSeduta(null, null,taskDefs);
			
			for (Task task : listTaskbpmTemp) {
				String businessKey = workflowService.getBusinessKey(task.getProcessInstanceId());
				// La business key deve corrispondere all'id Atto
				long idAtto = Long.parseLong(businessKey.trim());
				idAttos.add(idAtto);
				attoTask.put(idAtto, task.getTaskDefinitionKey());
			}
		}
		
		
		Predicate expression = getFilterQueryPostSeduta(criteriJson, idAttos, null,isOdgGiunta);
		Iterable<Atto> listAttoTemp = attoRepository.findAll(expression,
				new QSort(QAtto.atto.lastModifiedDate.desc()).getOrderSpecifiers().get(0));
		
		for (Atto atto : listAttoTemp) {
			for(Avanzamento avanzamento: atto.getAvanzamento()) {
				avanzamento.setAtto(null);
			}
		}
		

		for (Atto atto : listAttoTemp) {
			
			if(idAttosInAttesaDiEsito.containsKey(atto.getId())) {
				lista.add(atto);
			}else {
				String taskDefinitionKey = attoTask.get(atto.getId());
				
				//se c'è corrispondenza tra il tipo atto e il tipo atto del task associato alla ricerca
				if(atto.getTipoAtto().getId() != null && tipologiaRicerca != null )
				
				for (TaskPerRicerca taskPerRicerca : tipologiaRicerca.getTasksPerRicerca()) {
					if(taskPerRicerca!=null&&taskPerRicerca.getKeycode().equalsIgnoreCase(taskDefinitionKey) && taskPerRicerca.getTipoAtto().getId().longValue() == atto.getTipoAtto().getId().longValue()) {
						lista.add(atto);
						break;
					}
				}
			}
		}
		long totaleRisultati = lista.size();
		if (paginazione != null && totaleRisultati > 0) {
			lista = lista.subList(paginazione.getOffset(), 
					Math.min(lista.size(), paginazione.getOffset() + paginazione.getPageSize()));
		}
		// Assessore proponente da assegnazioni incarichi
		String codiceTaskAssessore = WebApplicationProps.getProperty(ConfigPropNames.VISTO_ASSESSORE_CODICE_CONFIGURAZIONE);
		for (Atto atto : lista) {
			atto.setSedutaGiunta(null);
			atto.setOrdineGiornos(null);
							
			workflowService.loadAllTasksPostSeduta(atto);
			
			// Per il filtro su assessore proponente
			if (isOdgGiunta) {
				@SuppressWarnings("rawtypes")
				List list = configurazioneIncaricoService.getMinimalProfiliIncaricati(codiceTaskAssessore, atto.getId().longValue());
				if(list!= null) {
					atto.setObjs(list);
					String nomiProponenti = "";
					for (Object prof : atto.getObjs()) {
						String valAssessore = ((Profilo) prof).getUtente().getCognome() + " " + ((Profilo) prof).getUtente().getNome();
						if (nomiProponenti.length() > 0) {
							nomiProponenti += ", ";
						}
						nomiProponenti += valAssessore;
					}
					atto.setAssessoreProponente(nomiProponenti);
				}
			}
		}
		
		Page<Atto> foundPage = null;
		if(paginazione==null){
			foundPage = new PageImpl<Atto>(lista);
		}
		else if(lista.size() > 0) {
			foundPage = new PageImpl<Atto>(lista, paginazione, totaleRisultati);
		}
		else {
			foundPage = new PageImpl<Atto>(new ArrayList<Atto>());
		}
		
		return foundPage;
	}
	
	
	
	public Page<Atto> findInseribiliOdg(boolean isOdgGiunta, boolean includiSospesi,
			final Pageable paginazione, JsonObject criteriJson) throws DatatypeConfigurationException, ServiceException {
		
		List<Long> idAttoTasks = new ArrayList<Long>();
		List<Execution> listExecTemp = workflowService.getAttesaEsitoSeduta(isOdgGiunta);
		
		for (Execution exec : listExecTemp) {
			String businessKey = workflowService.getBusinessKey(exec.getProcessInstanceId());
			
			long idAtto = Long.parseLong(businessKey.trim());
			idAttoTasks.add(idAtto);
		}
		boolean parComNotReq = false;
		boolean parComAll = false;
		boolean parComExp = false;
		boolean parAll = true;
		
		if(JsonUtil.hasFilter(criteriJson, "parComNotReq")) {
			String check = criteriJson.get("parComNotReq").getAsString();
			if(!StringUtil.isNull(check) && Boolean.parseBoolean(check)) {
				parComNotReq = Boolean.parseBoolean(check);
			}
		}
		if(JsonUtil.hasFilter(criteriJson, "parComAll")) {
			String check = criteriJson.get("parComAll").getAsString();
			if(!StringUtil.isNull(check) && Boolean.parseBoolean(check)) {
				parComAll = Boolean.parseBoolean(check);
			}
		}
		if(JsonUtil.hasFilter(criteriJson, "parComExp")) {
			String check = criteriJson.get("parComExp").getAsString();
			if(!StringUtil.isNull(check) && Boolean.parseBoolean(check)) {
				parComExp = Boolean.parseBoolean(check);
			}
		}
		if(JsonUtil.hasFilter(criteriJson, "parAll")) {
			String check = criteriJson.get("parAll").getAsString();
			if(!StringUtil.isNull(check) && !Boolean.parseBoolean(check)) {
				parAll = Boolean.parseBoolean(check);
			}
		}
		
		Predicate expression = getFilterQuery(criteriJson, idAttoTasks, null);
		Iterable<Atto> listAttoTemp = attoRepository.findAll(expression,
				new QSort(QAtto.atto.lastModifiedDate.desc()).getOrderSpecifiers().get(0));
		
		// Filtro quelli già inseriti in Odg
		List<Atto> lista = new ArrayList<Atto>();
		
		//per filtro odl pareri commissione
		String codiceTipoParereCommCons = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO);
		String codiceTipoParereQuartRev = WebApplicationProps.getProperty(ConfigPropNames.PARERE_QUARTIERE_REVISORE_CODICE_TIPO);
		
		
		
		
		//inizio modifica antonio
		if(parAll) {
			for (Atto atto : listAttoTemp) {
				long idAttoOdg = -1;
				boolean sedutaValida = false;
				
				@SuppressWarnings("rawtypes")
				List list = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
				if(list!= null) {
					atto.setObjs(list);
				}
				if (atto.getAvanzamento() != null && !atto.getAvanzamento().isEmpty()) {
					atto.setNote(atto.getAvanzamento().iterator().next().getNote());
				}
				atto.setAvanzamento(null);
				if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
					Set<Parere> pareri = new HashSet<Parere>();
					for (Parere par : atto.getPareri()) {
						Parere newPar = new Parere();
						newPar.setId(par.getId());
						newPar.setAoo(par.getAoo());
						newPar.setAnnullato(par.getAnnullato());
						newPar.setTipoAzione(par.getTipoAzione());
						newPar.setParerePersonalizzato(par.getParerePersonalizzato());
						newPar.setParereSintetico(par.getParereSintetico());
						newPar.setDataScadenza(par.getDataScadenza());
						pareri.add(newPar);
					}
					atto.setPareri(pareri);
				}
				
				//controllo lo stato dell'atto e la seduta
				if(atto.getOrdineGiornos() != null) {
					Iterator<AttiOdg> attiOdg = atto.getOrdineGiornos().iterator();
					while (attiOdg.hasNext()) {
						AttiOdg curAttoOdg = (AttiOdg) attiOdg.next();
						
						SedutaGiunta seduta = curAttoOdg.getOrdineGiorno().getSedutaGiunta();
						String statoSeduta = StringUtil.trimStr(seduta.getStato());
						
						if (curAttoOdg.getId().longValue() > idAttoOdg ) {
							idAttoOdg = curAttoOdg.getId().longValue();
							
							sedutaValida = true;
							if (SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString().equalsIgnoreCase(statoSeduta) ||
								// SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString().equalsIgnoreCase(statoSeduta) ||
								SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.toString().equalsIgnoreCase(statoSeduta)
								// ||
								// SedutaGiuntaConstants.statiSeduta.sedutaConclusa.toString().equalsIgnoreCase(statoSeduta)
								) {
								sedutaValida = false;
							}
						}
					}
				}
				
				// Per gli atti in stato "Rinviato" devo considerare anche lo stato "Inseribile in Odg"
				boolean checkStato = false;
				if (isOdgGiunta) {
					checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
							SedutaGiuntaConstants.organoSeduta.G.name()).equalsIgnoreCase(atto.getStato());
				}
				else {
					checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
							SedutaGiuntaConstants.organoSeduta.C.name()).equalsIgnoreCase(atto.getStato());
				}
				
				if (includiSospesi) {
					checkStato = checkStato || SedutaGiuntaConstants.statiAtto.propostaSospesa.toString().equalsIgnoreCase(atto.getStato());
				}
				
				if (!sedutaValida || checkStato) {
					
					lista.add(atto);
					
					boolean aggiunto = false;
					boolean parQuarNotReq = true;
					boolean parQuarAllEspr = true;
					Boolean tuttiIPareriQuartieriScaduti = null;
					boolean almenoUnParereEspresso = false;
					List<Long>idsAooPareri= new ArrayList<Long>();
					List<Long>idsAooPareriEspressi= new ArrayList<Long>();
					List<Long>idsAooTask= new ArrayList<Long>();
					List<Long>idsAooInfo= new ArrayList<Long>();
					if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
						for (Parere parere : atto.getPareri()) {
							if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null) {
								if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
									idsAooPareri.add(parere.getAoo().getId());
									if(!StringUtils.isEmpty(parere.getParereSintetico())) {
										almenoUnParereEspresso = true;
										if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
											idsAooPareriEspressi.add(parere.getAoo().getId());
										}
									}
								}
								else if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereQuartRev)) {
									if(!StringUtils.isEmpty(parere.getParereSintetico())) {
										almenoUnParereEspresso = true;
										if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
											idsAooPareriEspressi.add(parere.getAoo().getId());
										}
									}
									parQuarNotReq = false;
									if(StringUtils.isEmpty(parere.getParereSintetico())){
										parQuarAllEspr = false;
									}
									if((tuttiIPareriQuartieriScaduti == null || tuttiIPareriQuartieriScaduti == true) && parere.getDataScadenza()!= null && parere.getDataScadenza().isBeforeNow()) {
										tuttiIPareriQuartieriScaduti = true;
									}else {
										tuttiIPareriQuartieriScaduti = false;
									}
								}
							}
						}
					}
					List<Task> listTask = workflowService.getAllTasks(atto.getId());
					if(listTask!= null && !listTask.isEmpty()) {
						for (Task task : listTask) {
							if(task.getName() != null && task.getName().toLowerCase().contains("commissione")) {
								Aoo aoo = null;
								if (!StringUtil.isNull(task.getAssignee())) {
									aoo = camundaUtil.getAoo(task.getAssignee());
								}
								else{
									List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
											.taskId(task.getId()).orderByTime().desc().list();
									for (HistoricIdentityLinkLog identityLink : identityLinks) {
										if ( "candidate".equalsIgnoreCase(identityLink.getType()) &&
												!StringUtil.isNull(identityLink.getGroupId())) {
											
											List<Aoo> candidates = camundaUtil.getAooByCandidate(identityLink.getGroupId());
											if (candidates != null && candidates.size() > 0) {
												aoo = candidates.get(0);
												break;
											}
										}
									}
								}
								if(aoo!= null && aoo.getId() != null) {
									idsAooTask.add(aoo.getId());
								}
							}
						}
					}
					List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
					
					Boolean tuttiScaduti = null;
					if(listInc != null) {
						for (ConfigurazioneIncaricoDto inc : listInc) {
							if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
								for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
										idsAooInfo.add(incAoo.getIdAoo());
										if(incAoo.getDataManuale()!= null && incAoo.getGiorniScadenza()!= null) {
											if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataManuale().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
												tuttiScaduti = true;
											}else {
												tuttiScaduti = false;
											}
										}
										else if(incAoo.getDataCreazione()!= null && incAoo.getGiorniScadenza()!= null) {
											if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataCreazione().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
												tuttiScaduti = true;
											}else {
												tuttiScaduti = idsAooPareriEspressi.contains(incAoo.getIdAoo());
											}
										}
								}
							}
						}
					}
					
					
					boolean valoreFrontEndParComNotReq = false;
					if(!almenoUnParereEspresso) {
						//parComNotReq: Parere Comm. \ Cons. Quart. \ Rev. Cont. non richiesto
						boolean controlloPareriNonRichiesti = false;
						
						if(idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty() && parQuarNotReq) {
							controlloPareriNonRichiesti = true;
						}
						
						if(controlloPareriNonRichiesti) {
							aggiunto = true;
							//Setto il filtro per i flag frontend
							valoreFrontEndParComNotReq = true;
						}
					}
					atto.setParComNotReq(valoreFrontEndParComNotReq);
					boolean valoreFrontEndParComAll = false;
					if(almenoUnParereEspresso) {//parComAll: Parere espresso da tutte le Comm. \ Cons. Quart. \ Rev. Cont. previsti
						//si entra qui con il presupposto che sia stato espresso almenoUnParere
						
						boolean controlloTuttiIParereEspressi = parQuarNotReq || (!parQuarNotReq && parQuarAllEspr);
						
						//arrivati a questo punto controlloparComAll Ã¨ true se i pareri Quart non erano richiesti o se erano richiesti e sono stati tutti inseriti
						//controlloparComAll == false quando pareri Quart erano richiesti ma non sono stati inseriti tutti
						if(controlloTuttiIParereEspressi) {
						
							//inizio il controllo sui pareri commissione
							if(!idsAooPareri.isEmpty() && idsAooTask.isEmpty() && !idsAooInfo.isEmpty()) {
								if(!idsAooPareri.isEmpty() && !idsAooInfo.isEmpty()) {
									for (Long idAooInfo : idsAooInfo) {
										if(!idsAooPareri.contains(idAooInfo)) {
											controlloTuttiIParereEspressi = false ;
											break;
										}
									}
								}
							}
						
						}
						if(controlloTuttiIParereEspressi) {
							if(!aggiunto) {
								aggiunto = true;
							}
							//Setto il filtro per i flag frontend 
							valoreFrontEndParComAll = true;
						}
					}
					atto.setParComAll(valoreFrontEndParComAll);
					
					boolean valoreFrontEndParComExp = false;
					//if(parComExp) { //parComExp: Scaduti termini per espressione parere di TUTTE le Comm. \ Cons. Quart. \Rev. Cont. previsti : un atto viene visualizzato se tutti i termini dei pareri sono scaduti indipendentemente dal fatto
						//che il parere sia stato inserito o meno
						boolean controlloTerminiScadutiPerTuttiIPareriPrevisti = false;
						
						if( (tuttiScaduti!=null || tuttiIPareriQuartieriScaduti !=null) &&   (tuttiScaduti== null || (tuttiScaduti!= null && tuttiScaduti.booleanValue())) && (tuttiIPareriQuartieriScaduti == null || (tuttiIPareriQuartieriScaduti!= null && tuttiIPareriQuartieriScaduti.booleanValue()))) {
							controlloTerminiScadutiPerTuttiIPareriPrevisti = true;
						} 
						if(controlloTerminiScadutiPerTuttiIPareriPrevisti) {
							if(!aggiunto) {
								aggiunto = true;
							}
							//Setto il filtro per i flag frontend
							valoreFrontEndParComExp = true;
						}
					//}
					atto.setParComExpired(valoreFrontEndParComExp);
				}
				
				
				
				
				
				
				
				
				
			}
		}else {
			HashMap<Long,Atto> attiCheSoddisfanoAlmenoUnaCondizione = new HashMap<Long, Atto>();
			//ciclo per vedere quali sono gli atti da visualizzare: i tre filtri sono in OR quindi basta che un atto soddisfi una condizione per venire visualizzato
			for (Atto atto : listAttoTemp) {
				
				long idAttoOdg = -1;
				boolean sedutaValida = false;
				//setto a valori di default i filtri per i flag frontend
				atto.setParComAll(false);
				atto.setParComNotReq(false);
				atto.setParComExpired(false);
				
				if(atto.getOrdineGiornos() != null) {
					Iterator<AttiOdg> attiOdg = atto.getOrdineGiornos().iterator();
					while (attiOdg.hasNext()) {
						AttiOdg curAttoOdg = (AttiOdg) attiOdg.next();
						
						SedutaGiunta seduta = curAttoOdg.getOrdineGiorno().getSedutaGiunta();
						String statoSeduta = StringUtil.trimStr(seduta.getStato());
						
						if (curAttoOdg.getId().longValue() > idAttoOdg ) {
							idAttoOdg = curAttoOdg.getId().longValue();
							
							sedutaValida = true;
							if (SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString().equalsIgnoreCase(statoSeduta) ||
								// SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString().equalsIgnoreCase(statoSeduta) ||
								SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.toString().equalsIgnoreCase(statoSeduta)
								// ||
								// SedutaGiuntaConstants.statiSeduta.sedutaConclusa.toString().equalsIgnoreCase(statoSeduta)
								) {
								sedutaValida = false;
							}
						}
					}
				}
				
				// Per gli atti in stato "Rinviato" devo considerare anche lo stato "Inseribile in Odg"
				boolean checkStato = false;
				if (isOdgGiunta) {
					checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
							SedutaGiuntaConstants.organoSeduta.G.name()).equalsIgnoreCase(atto.getStato());
				}
				else {
					checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
							SedutaGiuntaConstants.organoSeduta.C.name()).equalsIgnoreCase(atto.getStato());
				}
				
				if (includiSospesi) {
					checkStato = checkStato || SedutaGiuntaConstants.statiAtto.propostaSospesa.toString().equalsIgnoreCase(atto.getStato());
				}
				
				if (!sedutaValida || checkStato) {
					// TODO: ribalto il valore della motivazione dato che gli avanzamenti non vengono riportati lato client
					if (atto.getAvanzamento() != null && !atto.getAvanzamento().isEmpty()) {
						atto.setNote(atto.getAvanzamento().iterator().next().getNote());
					}
					atto.setAvanzamento(null);
					if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
						Set<Parere> pareri = new HashSet<Parere>();
						for (Parere par : atto.getPareri()) {
							Parere newPar = new Parere();
							newPar.setId(par.getId());
							newPar.setAoo(par.getAoo());
							newPar.setAnnullato(par.getAnnullato());
							newPar.setTipoAzione(par.getTipoAzione());
							newPar.setParerePersonalizzato(par.getParerePersonalizzato());
							newPar.setParereSintetico(par.getParereSintetico());
							newPar.setDataScadenza(par.getDataScadenza());
							pareri.add(newPar);
						}
						atto.setPareri(pareri);
					}

					if(!isOdgGiunta) {
						
						boolean aggiunto = false;
						boolean parQuarNotReq = true;
						boolean parQuarAllEspr = true;
						Boolean tuttiIPareriQuartieriScaduti = null;
						boolean almenoUnParereEspresso = false;
						List<Long>idsAooPareri= new ArrayList<Long>();
						List<Long>idsAooPareriEspressi= new ArrayList<Long>();
						List<Long>idsAooTask= new ArrayList<Long>();
						List<Long>idsAooInfo= new ArrayList<Long>();
						if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
							for (Parere parere : atto.getPareri()) {
								if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null) {
									if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
										idsAooPareri.add(parere.getAoo().getId());
										if(!StringUtils.isEmpty(parere.getParereSintetico())) {
											almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
									}
									else if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereQuartRev)) {
										if(!StringUtils.isEmpty(parere.getParereSintetico())) {
											almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
										parQuarNotReq = false;
										if(StringUtils.isEmpty(parere.getParereSintetico())){
											parQuarAllEspr = false;
										}
										if((tuttiIPareriQuartieriScaduti == null || tuttiIPareriQuartieriScaduti == true) && parere.getDataScadenza()!= null && parere.getDataScadenza().isBeforeNow()) {
											tuttiIPareriQuartieriScaduti = true;
										}else {
											tuttiIPareriQuartieriScaduti = false;
										}
									}
								}
							}
						}
						List<Task> listTask = workflowService.getAllTasks(atto.getId());
						if(listTask!= null && !listTask.isEmpty()) {
							for (Task task : listTask) {
								if(task.getName() != null && task.getName().toLowerCase().contains("commissione")) {
									Aoo aoo = null;
									if (!StringUtil.isNull(task.getAssignee())) {
										aoo = camundaUtil.getAoo(task.getAssignee());
									}
									else{
										List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
												.taskId(task.getId()).orderByTime().desc().list();
										for (HistoricIdentityLinkLog identityLink : identityLinks) {
											if ( "candidate".equalsIgnoreCase(identityLink.getType()) &&
													!StringUtil.isNull(identityLink.getGroupId())) {
												
												List<Aoo> candidates = camundaUtil.getAooByCandidate(identityLink.getGroupId());
												if (candidates != null && candidates.size() > 0) {
													aoo = candidates.get(0);
													break;
												}
											}
										}
									}
									if(aoo!= null && aoo.getId() != null) {
										idsAooTask.add(aoo.getId());
									}
								}
							}
						}
						List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
						
						Boolean tuttiScaduti = null;
						if(listInc != null) {
							for (ConfigurazioneIncaricoDto inc : listInc) {
								if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
									for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
											idsAooInfo.add(incAoo.getIdAoo());
											if(incAoo.getDataManuale()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataManuale().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScaduti = true;
												}else {
													tuttiScaduti = false;
												}
											}
											else if(incAoo.getDataCreazione()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataCreazione().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScaduti = true;
												}else {
													tuttiScaduti = idsAooPareriEspressi.contains(incAoo.getIdAoo());
												}
											}
									}
								}
							}
						}
						boolean valoreFrontEndParComNotReq = false;
						if(parComNotReq && !almenoUnParereEspresso) {
							//parComNotReq: Parere Comm. \ Cons. Quart. \ Rev. Cont. non richiesto
							boolean controlloPareriNonRichiesti = false;
							
							if(idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty() && parQuarNotReq) {
								controlloPareriNonRichiesti = true;
							}
							
							if(controlloPareriNonRichiesti) {
								attiCheSoddisfanoAlmenoUnaCondizione.put(atto.getId(), atto);
								aggiunto = true;
								//Setto il filtro per i flag frontend
								valoreFrontEndParComNotReq = true;
							}
						}
						atto.setParComNotReq(valoreFrontEndParComNotReq);
						boolean valoreFrontEndParComAll = false;
						if(parComAll && almenoUnParereEspresso) {//parComAll: Parere espresso da tutte le Comm. \ Cons. Quart. \ Rev. Cont. previsti
							//si entra qui con il presupposto che sia stato espresso almenoUnParere
							
							boolean controlloTuttiIParereEspressi = parQuarNotReq || (!parQuarNotReq && parQuarAllEspr);
							
							//arrivati a questo punto controlloparComAll Ã¨ true se i pareri Quart non erano richiesti o se erano richiesti e sono stati tutti inseriti
							//controlloparComAll == false quando pareri Quart erano richiesti ma non sono stati inseriti tutti
							if(controlloTuttiIParereEspressi) {
							
								//inizio il controllo sui pareri commissione
								if(!idsAooPareri.isEmpty() && idsAooTask.isEmpty() && !idsAooInfo.isEmpty()) {
									if(!idsAooPareri.isEmpty() && !idsAooInfo.isEmpty()) {
										for (Long idAooInfo : idsAooInfo) {
											if(!idsAooPareri.contains(idAooInfo)) {
												controlloTuttiIParereEspressi = false ;
												break;
											}
										}
									}
								}
							
							}
							if(controlloTuttiIParereEspressi) {
								if(!aggiunto) {
									attiCheSoddisfanoAlmenoUnaCondizione.put(atto.getId(), atto);
									aggiunto = true;
								}
								//Setto il filtro per i flag frontend 
								valoreFrontEndParComAll = true;
							}
						}
						atto.setParComAll(valoreFrontEndParComAll);
						
						boolean valoreFrontEndParComExp = false;
						if(parComExp) { //parComExp: Scaduti termini per espressione parere di TUTTE le Comm. \ Cons. Quart. \Rev. Cont. previsti : un atto viene visualizzato se tutti i termini dei pareri sono scaduti indipendentemente dal fatto
							//che il parere sia stato inserito o meno
							boolean controlloTerminiScadutiPerTuttiIPareriPrevisti = false;
							
							if( (tuttiScaduti!=null || tuttiIPareriQuartieriScaduti !=null) &&   (tuttiScaduti== null || (tuttiScaduti!= null && tuttiScaduti.booleanValue())) && (tuttiIPareriQuartieriScaduti == null || (tuttiIPareriQuartieriScaduti!= null && tuttiIPareriQuartieriScaduti.booleanValue()))) {
								controlloTerminiScadutiPerTuttiIPareriPrevisti = true;
							} 
							if(controlloTerminiScadutiPerTuttiIPareriPrevisti) {
								if(!aggiunto) {
									attiCheSoddisfanoAlmenoUnaCondizione.put(atto.getId(), atto);
									aggiunto = true;
								}
								//Setto il filtro per i flag frontend
								valoreFrontEndParComExp = true;
							}
						}
						atto.setParComExpired(valoreFrontEndParComExp);
						
					}else {
						attiCheSoddisfanoAlmenoUnaCondizione.put(atto.getId(), atto);
					}
				}
			}//fine for per valorizzare l'hashmap attiCheSoddisfanoAlmenoUnaCondizione 
			
			for (Long idAtto : attiCheSoddisfanoAlmenoUnaCondizione.keySet()) {
				Atto atto = attiCheSoddisfanoAlmenoUnaCondizione.get(idAtto);
				@SuppressWarnings("rawtypes")
				List list = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
				if(list!= null) {
					atto.setObjs(list);
				}
				lista.add(atto);
			}
			
		}
		
		
		
		
		//fine modifica antonio
		
		
		//inizio codice simone
//		List<Long> idsAooPareri= null;
//		List<Long> idsAooPareriEspressi= null;
//		List<Long> idsAooTask= null;
//		List<Long> idsAooInfo= null;
//		for (Atto atto : listAttoTemp) {
//			
//			long idAttoOdg = -1;
//			boolean sedutaValida = false;
//			
//			if(atto.getOrdineGiornos() != null) {
//				Iterator<AttiOdg> attiOdg = atto.getOrdineGiornos().iterator();
//				while (attiOdg.hasNext()) {
//					AttiOdg curAttoOdg = (AttiOdg) attiOdg.next();
//					
//					SedutaGiunta seduta = curAttoOdg.getOrdineGiorno().getSedutaGiunta();
//					String statoSeduta = StringUtil.trimStr(seduta.getStato());
//					
//					if (curAttoOdg.getId().longValue() > idAttoOdg ) {
//						idAttoOdg = curAttoOdg.getId().longValue();
//						
//						sedutaValida = true;
//						if (SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString().equalsIgnoreCase(statoSeduta) ||
//							// SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString().equalsIgnoreCase(statoSeduta) ||
//							SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.toString().equalsIgnoreCase(statoSeduta)
//							// ||
//							// SedutaGiuntaConstants.statiSeduta.sedutaConclusa.toString().equalsIgnoreCase(statoSeduta)
//							) {
//							sedutaValida = false;
//						}
//					}
//				}
//			}
//			
//			// Per gli atti in stato "Rinviato" devo considerare anche lo stato "Inseribile in Odg"
//			boolean checkStato = false;
//			if (isOdgGiunta) {
//				checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
//						SedutaGiuntaConstants.organoSeduta.G.name()).equalsIgnoreCase(atto.getStato());
//			}
//			else {
//				checkStato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(
//						SedutaGiuntaConstants.organoSeduta.C.name()).equalsIgnoreCase(atto.getStato());
//			}
//			
//			if (includiSospesi) {
//				checkStato = checkStato || SedutaGiuntaConstants.statiAtto.propostaSospesa.toString().equalsIgnoreCase(atto.getStato());
//			}
//			
//			if (!sedutaValida || checkStato) {
//				// TODO: ribalto il valore della motivazione dato che gli avanzamenti non vengono riportati lato client
//				if (atto.getAvanzamento() != null && !atto.getAvanzamento().isEmpty()) {
//					atto.setNote(atto.getAvanzamento().iterator().next().getNote());
//				}
//				atto.setAvanzamento(null);
//				if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
//					Set<Parere> pareri = new HashSet<Parere>();
//					for (Parere par : atto.getPareri()) {
//						Parere newPar = new Parere();
//						newPar.setId(par.getId());
//						newPar.setAoo(par.getAoo());
//						newPar.setAnnullato(par.getAnnullato());
//						newPar.setTipoAzione(par.getTipoAzione());
//						newPar.setParerePersonalizzato(par.getParerePersonalizzato());
//						newPar.setParereSintetico(par.getParereSintetico());
//						newPar.setDataScadenza(par.getDataScadenza());
//						pareri.add(newPar);
//					}
//					atto.setPareri(pareri);
//				}
//				// CHECK PARERI COMMISSIONE
//				if(!isOdgGiunta) {
//					// resetto le liste 
//					boolean parQuarNotReq = true;
//					boolean parQuarAllEspr = true;
//					Boolean allQuarExp = null;
//					boolean almenoUnParereEspresso = false;
//					idsAooPareri= new ArrayList<Long>();
//					idsAooPareriEspressi= new ArrayList<Long>();
//					idsAooTask= new ArrayList<Long>();
//					idsAooInfo= new ArrayList<Long>();
//					if(atto.getPareri()!= null && !atto.getPareri().isEmpty()) {
//						for (Parere parere : atto.getPareri()) {
//							if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null) {
//								if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
//									idsAooPareri.add(parere.getAoo().getId());
//									if(!StringUtils.isEmpty(parere.getParereSintetico())) {
//										almenoUnParereEspresso = true;
//										if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
//											idsAooPareriEspressi.add(parere.getAoo().getId());
//										}
//									}
//								}
//								else if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereQuartRev)) {
//									if(!StringUtils.isEmpty(parere.getParereSintetico())) {
//										almenoUnParereEspresso = true;
//										if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
//											idsAooPareriEspressi.add(parere.getAoo().getId());
//										}
//									}
//									parQuarNotReq = false;
//									if(StringUtils.isEmpty(parere.getParereSintetico())){
//										parQuarAllEspr = false;
//									}
//									if((allQuarExp == null || allQuarExp == true) && parere.getDataScadenza()!= null && parere.getDataScadenza().isBeforeNow()) {
//										allQuarExp = true;
//									}else {
//										allQuarExp = false;
//									}
//								}
//							}
//						}
//					}
//					List<Task> listTask = workflowService.getAllTasks(atto.getId());
//					if(listTask!= null && !listTask.isEmpty()) {
//						for (Task task : listTask) {
//							if(task.getName() != null && task.getName().toLowerCase().contains("commissione")) {
//								Aoo aoo = null;
//								if (!StringUtil.isNull(task.getAssignee())) {
//									aoo = camundaUtil.getAoo(task.getAssignee());
//								}
//								else{
//									List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
//											.taskId(task.getId()).orderByTime().desc().list();
//									for (HistoricIdentityLinkLog identityLink : identityLinks) {
//										if ( "candidate".equalsIgnoreCase(identityLink.getType()) &&
//												!StringUtil.isNull(identityLink.getGroupId())) {
//											
//											List<Aoo> candidates = camundaUtil.getAooByCandidate(identityLink.getGroupId());
//											if (candidates != null && candidates.size() > 0) {
//												aoo = candidates.get(0);
//												break;
//											}
//										}
//									}
//								}
//								if(aoo!= null && aoo.getId() != null) {
//									idsAooTask.add(aoo.getId());
//								}
//							}
//						}
//					}
//					List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
//					
//					Boolean allExp = null;
//					if(listInc != null) {
//						for (ConfigurazioneIncaricoDto inc : listInc) {
//							if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
//								for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
//										idsAooInfo.add(incAoo.getIdAoo());
//										if(incAoo.getDataManuale()!= null && incAoo.getGiorniScadenza()!= null) {
//											if((allExp == null || allExp == true) && incAoo.getDataManuale().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
//												allExp = true;
//											}else {
//												//prima di settare allExp a false devo controllare che il parere non sia stato cmq espresso (anche dopo la data scadenza)
//												allExp = idsAooPareriEspressi.contains(incAoo.getIdAoo());
//											}
//										}
//										else if(incAoo.getDataCreazione()!= null && incAoo.getGiorniScadenza()!= null) {
//											if((allExp == null || allExp == true) && incAoo.getDataCreazione().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
//												allExp = true;
//											}else {
//												allExp = idsAooPareriEspressi.contains(incAoo.getIdAoo());
//											}
//										}
//								}
//							}
//						}
//					}
//					
//					@SuppressWarnings("rawtypes")
//					List list = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
//					if(list!= null) {
//						atto.setObjs(list);
//					}
//					
//					//Setto i filtri per i flag frontend 
//					boolean add = false;
//					atto.setParComAll(false);
//					atto.setParComNotReq(false);
//					atto.setParComExpired(false);
//					
//					if(idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty() && parQuarNotReq) {
//						atto.setParComNotReq(true);
//						if(!add && parComNotReq) {
//							add = true;
//						}
//					}
//					if(((!idsAooPareri.isEmpty() && idsAooTask.isEmpty() && !idsAooInfo.isEmpty()) 
//							|| (idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty())) 
//							&& (!parQuarNotReq && parQuarAllEspr)) {
//						boolean check = true;
//						if(!idsAooPareri.isEmpty() && !idsAooInfo.isEmpty()) {
//							for (Long idAooInfo : idsAooInfo) {
//								if(!idsAooPareri.contains(idAooInfo)) {
//									check = false ;
//									break;
//								}
//							}
//						}
//						atto.setParComAll(check);
//						if(check && !add && parComAll ) {
//							add = true;
//						}
//					}
//					if(( (allExp== null && almenoUnParereEspresso) || (allExp!= null && allExp.booleanValue())) && (allQuarExp == null || (allQuarExp!= null && allQuarExp.booleanValue()))) {
//						atto.setParComExpired(true);
//						if(!add && parComExp) {
//							add = true;
//						}
//					} 
//					
//					if(parAll || add) {
//						lista.add(atto);
//					}
//					log.info("idsAooPareri id:" +atto.getCodiceCifra()+" " + idsAooPareri.size());
//					log.info("idsAooTask id:" + atto.getCodiceCifra()+" " + idsAooTask.size());
//					log.info("idsAooInfo id:" + atto.getCodiceCifra()+" " +idsAooInfo.size());
//					log.info("idsAooInfo allExp:" + atto.getCodiceCifra()+" " +allExp);
//				}
//				else {
//					lista.add(atto);
//				}
//				
//			}
//		}	fine codice simone	
		
//		for (Atto attoCheck : lista) {
//			if(attoCheck.getPareri()!= null && !attoCheck.getPareri().isEmpty()) {
//				for (Parere parere : attoCheck.getPareri()) {
//					if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null &&
//							parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
//						idsAooPareri.add(parere.getAoo().getId());
//					}
//				}
//			}
//			List<Task> listTask = workflowService.getAllTasks(attoCheck.getId());
//			if(listTask!= null && !listTask.isEmpty()) {
//				for (Task task : listTask) {
//					if(task.getDescription() != null && task.getName().toLowerCase().contains("commissione")) {
//						Aoo aoo = camundaUtil.getAoo(task.getAssignee());
//						if(aoo!= null && aoo.getId() != null) {
//							idsAooTask.add(aoo.getId());
//						}
//					}
//				}
//			}
//			List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(attoCheck.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
//			if(listInc != null) {
//				for (ConfigurazioneIncaricoDto inc : listInc) {
//					if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
//						for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
//							if(!idsAooPareri.contains(incAoo.getIdAoo()) && !idsAooTask.contains(incAoo.getIdAoo())) {
//								idsAooInfo.add(incAoo.getIdAoo());
//							}
//						}
//					}
//				}
//			}
//			log.info("idsAooPareri " +attoCheck+" " + idsAooPareri.size());
//			log.info("idsAooTask " + attoCheck+" " + idsAooTask.size());
//			log.info("idsAooInfo " + attoCheck+" " +idsAooInfo.size());
//			
//			
//			// rimuovo i task per non mandarli al frontend
//			attoCheck.setTaskAttivi(null);
//			// resetto le liste 
//			idsAooPareri= new ArrayList<Long>();
//			idsAooTask= new ArrayList<Long>();
//			idsAooInfo= new ArrayList<Long>();
//		}
		
		// Preparo le informazioni per inviarle al client		
		long totaleRisultati = lista.size();
		if (paginazione != null && totaleRisultati > 0) {
			lista = lista.subList(paginazione.getOffset(), 
					Math.min(lista.size(), paginazione.getOffset() + paginazione.getPageSize()));
		}
		
		// Assessore proponente da assegnazioni incarichi
		String codiceTaskAssessore = WebApplicationProps.getProperty(ConfigPropNames.VISTO_ASSESSORE_CODICE_CONFIGURAZIONE);
		for (Atto atto : lista) {
			atto.setSedutaGiunta(null);
			atto.setOrdineGiornos(null);
							
			workflowService.loadAllTasks(atto);
			
			// Per il filtro su assessore proponente
			if (isOdgGiunta) {
				@SuppressWarnings("rawtypes")
				List list = configurazioneIncaricoService.getMinimalProfiliIncaricati(codiceTaskAssessore, atto.getId().longValue());
				if(list!= null) {
					atto.setObjs(list);
					String nomiProponenti = "";
					for (Object prof : atto.getObjs()) {
						String valAssessore = ((Profilo) prof).getUtente().getCognome() + " " + ((Profilo) prof).getUtente().getNome();
						if (nomiProponenti.length() > 0) {
							nomiProponenti += ", ";
						}
						nomiProponenti += valAssessore;
					}
					atto.setAssessoreProponente(nomiProponenti);
				}
			}
		}
		
		Page<Atto> foundPage = null;
		if(paginazione==null){
			foundPage = new PageImpl<Atto>(lista);
		}
		else if(lista.size() > 0) {
			foundPage = new PageImpl<Atto>(lista, paginazione, totaleRisultati);
		}
		else {
			foundPage = new PageImpl<Atto>(new ArrayList<Atto>());
		}
		
		return foundPage;
	}
	
	
	private Predicate getFilterQuery(JsonObject criteriJson, List<Long> idAttoTasks, String[] filterTipiAtto) {
		QAtto qAtto = QAtto.atto;
		BooleanExpression expression = qAtto.id.in(idAttoTasks);
		BooleanExpression filterAtto = getFilterQueryAtto(qAtto, expression, criteriJson);
		
		if ((filterTipiAtto != null) && (filterTipiAtto.length > 0)) {
			BooleanExpression p = null;
			
			for (String codiceTipo : filterTipiAtto) {
				if (!StringUtil.isNull(codiceTipo)) {
					if (p == null) {
						p = qAtto.tipoAtto.codice.equalsIgnoreCase(codiceTipo.trim());
					}
					else {
						p = p.or(qAtto.tipoAtto.codice.equalsIgnoreCase(codiceTipo.trim()));
					}
				}
			}
			
			if (p != null) {
				filterAtto = filterAtto.and(p);
			}
		}
		
		return filterAtto;
	}
	
	private Predicate getFilterQueryPostSeduta(JsonObject criteriJson, List<Long> idAttoTasks, String[] filterTipiAtto, boolean giunta) {
		QAtto qAtto = QAtto.atto;
		BooleanExpression expression = qAtto.id.in(idAttoTasks);
		BooleanExpression filterAtto = getFilterQueryAtto(qAtto, expression, criteriJson);
		
		if ((filterTipiAtto != null) && (filterTipiAtto.length > 0)) {
			BooleanExpression p = null;
			
			for (String codiceTipo : filterTipiAtto) {
				if (!StringUtil.isNull(codiceTipo)) {
					if (p == null) {
						p = qAtto.tipoAtto.codice.equalsIgnoreCase(codiceTipo.trim());
					}
					else {
						p = p.or(qAtto.tipoAtto.codice.equalsIgnoreCase(codiceTipo.trim()));
					}
				}
			}
			
			if (p != null) {
				filterAtto = filterAtto.and(p);
			}
		}
		
		if(giunta) {
			BooleanExpression p = qAtto.tipoAtto.giunta.eq(true);
			filterAtto = filterAtto.and(p);
		}else {
			BooleanExpression p = qAtto.tipoAtto.consiglio.eq(true);
			filterAtto = filterAtto.and(p);
		}
		
		return filterAtto;
	}
	
	
	private BooleanExpression getFilterQuery(JsonObject criteriJson, final Iterable<Profilo> profili, boolean addProfili) {
		QAvanzamento qAvanzamento = QAvanzamento.avanzamento;
		BooleanExpression expression = qAvanzamento.profilo.in(IteratorUtils.toList(profili.iterator()));
		BooleanExpression filterAtto = getFilterQueryAtto(qAvanzamento.atto, expression, criteriJson);
		Iterable<Avanzamento> listAvanzamentoFilter = avanzamentoRepository.findAll(filterAtto);
		List<Atto> atti = new ArrayList<Atto>();
		HashMap<Long, Long> idAtto = new HashMap<>();
		for(Avanzamento av : listAvanzamentoFilter) {
			if(av!=null && av.getAtto()!=null) {
				if(!idAtto.containsKey(av.getAtto().getId())) {
					atti.add(av.getAtto());
					idAtto.put(av.getAtto().getId(), av.getAtto().getId());
				}
			}
		}
		BooleanExpression filterAvanzamenti = qAvanzamento.atto.in(atti);
		if(addProfili) {
			List<Profilo> profiliList = new ArrayList<Profilo>();
			for (Profilo profilo : profili) {
				profiliList.add(profilo);
			}
			BooleanExpression p = qAvanzamento.profilo.in(profiliList);
			filterAvanzamenti = filterAvanzamenti.and(p);
		}
		
		return filterAvanzamenti;
	}

	private BooleanExpression getFilterQueryAtto(QAtto attoCriteria, BooleanExpression baseQuery, JsonObject criteriJson) {
		
		if(JsonUtil.hasFilter(criteriJson, "dataArrivoRagioneriaDa")) {
			
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataArrivoRagioneriaA")) {
			
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataScadenzaDa")) {
			//range data creazione
			try {
					LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataScadenzaDa").getAsString().trim()));
					BooleanExpression p = attoCriteria.datiContabili.dataScadenza.goe(ld);
					baseQuery = baseQuery.and(p);
			}
			catch(Exception e){
				log.error("Errore lettura 'dataScadenzaDa'", e);
			}
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataScadenzaA")) {
			try {
				if(JsonUtil.hasFilter(criteriJson, "dataScadenzaA")) {
					LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataScadenzaA").getAsString().trim()));
					BooleanExpression p = attoCriteria.datiContabili.dataScadenza.loe(ld);
					baseQuery = baseQuery.and(p);
				}
			}
			catch(Exception e){
				log.error("Errore lettura 'dataScadenzaA'", e);
			}
		}
		
		
		if(JsonUtil.hasFilter(criteriJson, "importoEntrataDa")) {
			String imp = StringUtil.trimStr(criteriJson.get("importoEntrataDa").getAsString().trim().replaceAll("\\.", "").replaceAll(",", "."));
			BooleanExpression r = attoCriteria.datiContabili.importoEntrata.goe(new BigDecimal(imp));
			baseQuery = baseQuery.and(r);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoEntrataA")) {
			String imp = StringUtil.trimStr(criteriJson.get("importoEntrataA").getAsString().trim().replaceAll("\\.", "").replaceAll(",", "."));
			BooleanExpression r = attoCriteria.datiContabili.importoEntrata.loe(new BigDecimal(imp));
			baseQuery = baseQuery.and(r);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoUscitaDa")) {
			String imp = StringUtil.trimStr(criteriJson.get("importoUscitaDa").getAsString().trim().replaceAll("\\.", "").replaceAll(",", "."));
			BooleanExpression r = attoCriteria.datiContabili.importoUscita.goe(new BigDecimal(imp));
			baseQuery = baseQuery.and(r);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoUscitaA")) {
			String imp = StringUtil.trimStr(criteriJson.get("importoUscitaA").getAsString().trim().replaceAll("\\.", "").replaceAll(",", "."));
			BooleanExpression r = attoCriteria.datiContabili.importoUscita.loe(new BigDecimal(imp));
			baseQuery = baseQuery.and(r);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "onlyWithText")) {
			String check = criteriJson.get("onlyWithText").getAsString();
			if(!StringUtil.isNull(check) && Boolean.parseBoolean(check)) {
				baseQuery = baseQuery.and(attoCriteria.domanda.testo.isNotNull()).and(attoCriteria.domanda.testo.isNotEmpty());
			}
		}
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			BooleanExpression p = attoCriteria.codiceCifra.containsIgnoreCase(criteriJson.get("codiceCifra").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "numeroAdozione")) {
			BooleanExpression p = attoCriteria.numeroAdozione.containsIgnoreCase(criteriJson.get("numeroAdozione").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		if(criteriJson.has("cercaSospesi") && !criteriJson.get("cercaSospesi").isJsonNull()) {
			JsonObject objCercaSosp = criteriJson.get("cercaSospesi").getAsJsonObject();
			if(JsonUtil.hasFilter(objCercaSosp, "id") && objCercaSosp.get("id").getAsBoolean()) {
				BooleanExpression p = attoCriteria.stato.equalsIgnoreCase(SedutaGiuntaConstants.statiAtto.propostaSospesa.toString());
				baseQuery = baseQuery.and(p);
			}else if(JsonUtil.hasFilter(objCercaSosp, "id") && !objCercaSosp.get("id").getAsBoolean()) {
				BooleanExpression p = attoCriteria.stato.notEqualsIgnoreCase(SedutaGiuntaConstants.statiAtto.propostaSospesa.toString());
				baseQuery = baseQuery.and(p);
			}
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			BooleanExpression p = attoCriteria.oggetto.containsIgnoreCase(criteriJson.get("oggetto").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "statoProposta")) {
			BooleanExpression p = attoCriteria.stato.containsIgnoreCase(criteriJson.get("statoProposta").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		//tipo atto
		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			BooleanExpression p = attoCriteria.tipoAtto.codice.equalsIgnoreCase(criteriJson.get("tipoAtto").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		if(JsonUtil.hasFilter(criteriJson, "tipoIter")) {
			BooleanExpression p = attoCriteria.tipoIter.descrizione.containsIgnoreCase(criteriJson.get("tipoIter").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		//creato da
		if(JsonUtil.hasFilter(criteriJson, "avviatoDa")) {
			BooleanExpression p = attoCriteria.createdBy.containsIgnoreCase(criteriJson.get("avviatoDa").getAsString().trim());
			baseQuery = baseQuery.and(p);
		}
		
		//range data creazione
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataCreazioneDa")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataCreazioneDa").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataCreazione.goe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataCreazioneDa'", e);
		}
		
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataCreazioneA")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataCreazioneA").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataCreazione.loe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataCreazioneA'", e);
		}
		
		//range data Adozione
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataAdozioneDa")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataAdozioneDa").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataAdozione.goe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataAdozioneDa'", e);
		}
		
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataAdozioneA")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataAdozioneA").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataAdozione.loe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataCreazioneA'", e);
		}
		
		//range data Esecutività
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaDa")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataEsecutivitaDa").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataEsecutivita.goe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataEsecutivitaDa'", e);
		}
		
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaA")) {
				LocalDate ld = new LocalDate(DF_FILTER.parse(criteriJson.get("dataEsecutivitaA").getAsString().trim()));
				BooleanExpression p = attoCriteria.dataEsecutivita.loe(ld);
				baseQuery = baseQuery.and(p);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataEsecutivitaA'", e);
		}
		
		// passaggi in ragioneria
		if (JsonUtil.hasFilter(criteriJson, "lavRagioneriaFilter")) {
			String valRagioneria = StringUtil.trimStr(criteriJson.get("lavRagioneriaFilter").getAsString());
			if (valRagioneria.equalsIgnoreCase("eq1")) {
				BooleanExpression r = attoCriteria.datiContabili.numArriviRagioneria.eq(1);
				baseQuery = baseQuery.and(r);
			}
			else if (valRagioneria.equalsIgnoreCase("gt1")) {
				BooleanExpression r = attoCriteria.datiContabili.numArriviRagioneria.gt(1);
				baseQuery = baseQuery.and(r);
			}
		}
		
		return baseQuery;
	}
	
	
	private List filterByAvanzamento(Iterable results, JsonObject criteriJson) {
		
		String ultimaAzione = null;
		String assegnatario = null;
		Date assegnazioneAfter = null;
		Date assegnazioneBefore = null;
		
		//azione effettuata
//		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
//			azioneDaEffettuare = criteriJson.get("lavorazione").getAsString().trim().toLowerCase();
//		}
		
		if (JsonUtil.hasFilter(criteriJson, "ultimaAzione")) {
			ultimaAzione = criteriJson.get("ultimaAzione").getAsString().trim().toLowerCase();
		}
		if (JsonUtil.hasFilter(criteriJson, "esecutore")) {
			assegnatario = criteriJson.get("esecutore").getAsString().trim().toLowerCase();
		}
		//range data ultima azione
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataStart")) {
				assegnazioneAfter = DF_FILTER.parse(criteriJson.get("dataStart").getAsString().trim());
				assegnazioneAfter.setHours(0);
				assegnazioneAfter.setMinutes(0);
				assegnazioneAfter.setSeconds(0);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataStart'", e);
		}
		
		try {
			if(JsonUtil.hasFilter(criteriJson, "dataEnd")) {
				assegnazioneBefore = DF_FILTER.parse(criteriJson.get("dataEnd").getAsString().trim());
				assegnazioneBefore.setHours(23);
				assegnazioneBefore.setMinutes(59);
				assegnazioneBefore.setSeconds(59);
			}
		}
		catch(Exception e){
			log.error("Errore lettura 'dataEnd'", e);
		}
		
		List retVal = new ArrayList();
		List<Long> attiConsiderati = new ArrayList<Long>();
				
		for(Object cur : results) {
			Avanzamento check = null;
			
			if (cur instanceof Atto) {
				Set<Avanzamento> avs = ((Atto)cur).getAvanzamento();
				if ( (avs != null) && (avs.size() > 0) ) {
					check = avs.iterator().next();
				}
			}
			else if (cur instanceof Avanzamento) {
				check = (Avanzamento)cur;
				Long curAttoId = check.getAtto().getId();
				
				if (attiConsiderati.contains(curAttoId)) {
					continue;
				}else {
					attiConsiderati.add(curAttoId);
				}
			}
			
			if (check == null) {
				continue;
			}
			
			boolean ok = true;
		
			//ultima azione effettuata
			if(!StringUtil.isNull(ultimaAzione)) {
				if ((check.getAttivita() == null) || (!check.getAttivita()
						.toLowerCase().contains(ultimaAzione))) {
					ok = false;
				}
			}
					
			// esecutore
			if(!StringUtil.isNull(assegnatario)) {
				if ((check.getCreatedBy() == null) || 
					(!check.getCreatedBy().toLowerCase().contains(assegnatario))) {
					ok = false;
				}
			}
					
			// data ultima azione effettuata
			if (assegnazioneAfter != null) {
				if ((check.getCreatedDate() == null) || 
					(check.getCreatedDate().isBefore(assegnazioneAfter.getTime()))) {
					ok = false;
				}
			}
			
			if (assegnazioneBefore != null) {
				if ((check.getCreatedDate() == null) || 
					(check.getCreatedDate().isAfter(assegnazioneBefore.getTime()))) {
					ok = false;
				}
			}
			
			if (ok) {
				retVal.add(cur);
			}
		}
		
		return retVal;
	}
	
	
	public List<String> findAllTaskNames() throws Exception {
		return avanzamentoRepository.findNomiAttivita();
	}
}
