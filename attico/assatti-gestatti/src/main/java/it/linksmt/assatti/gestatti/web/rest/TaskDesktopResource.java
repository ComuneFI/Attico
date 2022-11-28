package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.camunda.bpm.engine.task.Task;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;
import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceException;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;
import it.linksmt.assatti.datalayer.domain.EsitoParereEnum;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.RuntimeOperationTaskEnum;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.util.AooTransformer;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.fdr.exception.FirmaRemotaException;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.ConfigurazioneIncaricoService;
import it.linksmt.assatti.service.ExcelService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.RuntimeOperationService;
import it.linksmt.assatti.service.TaskDesktopService;
import it.linksmt.assatti.service.TransactionService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.FirmaRemotaDTO;
import it.linksmt.assatti.service.dto.TaskDesktopDTO;
import it.linksmt.assatti.service.dto.TaskDesktopGroupDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.LiquidazioniEmptyException;
import it.linksmt.assatti.service.exception.RuntimeOperationNotAlloweException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.service.util.SpecializzazioneAooEnum;
import it.linksmt.assatti.service.util.TaskDesktopEnum;
import it.linksmt.assatti.service.util.TipiAttivitaEnum;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller for managing TaskBpm.
 */
@RestController
@RequestMapping("/api")
public class TaskDesktopResource {

    private final Logger log = LoggerFactory.getLogger(TaskDesktopResource.class);

	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private TaskDesktopService taskDesktopService;

	@Inject
	private UtenteService utenteService;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
    private AttoRepository attoRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private ServiceUtil serviceUtil;
	
	@Inject
	private ExcelService excelService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private TransactionService transactionService;
	
	@Inject
	private QualificaProfessionaleRepository qualificaProfessionaleRepository;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	 
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	 
	@Inject
	private RuntimeOperationService runtimeOperationService;
	
	@Inject
	ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	ConfigurazioneIncaricoRepository configurazioneIncaricoRepository;
	
	@Inject
	AooRepository aooRepository;
	
	@RequestMapping(value = "/taskBpms/taskRiassegnabili",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> taskRiassegnabili(@RequestParam final Long profiloOrigineId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloIdOperatore) throws GestattiCatchedException
    {
    	try{

    		return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
	 
    @RequestMapping(value = "/taskBpms/prendiInCaricoTask",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> prendiInCaricoTask(@RequestBody final TipoTaskDTO taskbpm,
    		@RequestParam(value = "isPresaInCaricoPerDelega", required = false) final Boolean isPresaInCaricoPerDelega,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException
    {
    	try{
	    	log.debug("REST request to prendiInCaricoTask TipoTask : {}", taskbpm);
	    	if (!workflowService.prendiInCaricoTask(taskbpm.getId() ,profiloId, (isPresaInCaricoPerDelega != null ? isPresaInCaricoPerDelega : false))) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	        return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/taskBpms/riassegnazioneTaskMyOwn",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> riassegnazioneTaskMyOwn(@RequestBody final TipoTaskDTO taskbpm,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException
    {
    	try{
	    	log.debug("REST request to riassegnazioneTaskMyOwn TipoTask : {}", taskbpm);
	    	if (!workflowService.riassegnazioneTaskMyOwn(taskbpm.getId(), profiloId)) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	        return new ResponseEntity<Void>(HttpStatus.OK);
    	}
    	catch(Exception e){
    		if(e instanceof GestattiCatchedException) {
    			throw (GestattiCatchedException)e;
    		}else {
    			throw new GestattiCatchedException(e);
    		}
    	}
    }
    
    /**
     * POST  /taskBpms -> Restituisce l'atto alla struttura proponente (per Sedute Consiglio).
     */
    @RequestMapping(value = "/taskBpms/ritiraAttoC",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> ritiraAttoC(
    		@RequestBody final Atto atto,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException {
    	
    	return ritiraAtto(atto, false, profiloId);
    }
    
    /**
     * POST  /taskBpms -> Restituisce l'atto alla struttura proponente (per Sedute Giunta).
     */
    @RequestMapping(value = "/taskBpms/ritiraAttoG",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> ritiraAttoG(
    		@RequestBody final Atto atto,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException {
    	
    	return ritiraAtto(atto, true, profiloId);
    }
    
    @RequestMapping(value = "/taskBpms/existsActiveTask", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> existsActiveTask(@RequestParam(value = "taskId", required = true) final String taskId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to existsActiveTask taskId : {}", taskId);
	    	boolean exists = workflowService.existsActiveTask(taskId);
			JsonObject json = new JsonObject();
			json.addProperty("taskExists", exists);
	        return new ResponseEntity<String>(json.toString(),HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    
    private ResponseEntity<String> ritiraAtto(final Atto atto, boolean isOdgGiunta, final Long profiloId)
    		throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to ritiraAtto Atto : {}", atto);
	        if (atto.getProcessoBpmId() == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	        
	        String motivazione = StringUtil.trimStr(atto.getNote());
	      
	        
	        workflowService.ritiroAttoNonDiscusso(isOdgGiunta, 
	        		atto.getId().longValue(), profiloId.longValue(),
	        		NomiAttivitaAtto.ESITO_RESTITUZIONE_PROPONENTE,
	        		NomiAttivitaAtto.PROPOSTA_RESTITUZIONE,
	        		motivazione);
			
			String taskId = workflowService.getNextTaskId(atto.getId(), true);
			JsonObject json = new JsonObject();
			json.addProperty("taskId", taskId);
			
	        return new ResponseEntity<String>(json.toString(),HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /taskBpms ->  rilascia task.
     */
    @RequestMapping(value = "/taskBpms/unclaim",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> unclaim(@RequestParam(value = "taskId", required = true) final String taskId,
    		@RequestParam(value = "verifyOriginalProfId", required = false) final Long verifyOriginalProfId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to unclaim TaskDesktop : {}", taskId);
	    	
	    	boolean retVal = workflowService.rilasciaTask(taskId, profiloId, verifyOriginalProfId);
	    	
	    	if (retVal) {
	    		 return new ResponseEntity<Void>(HttpStatus.OK);
	    	}
	    	
	    	return new ResponseEntity<Void>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /taskBpms ->  cambiaCoda assegna un task ad un'altro ufficio.
     */
    @RequestMapping(value = "/taskBpms/cambiaCoda",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> cambiaCoda(
    		@RequestParam(value = "taskId", required = true) final String taskId,
    		@RequestParam(value = "profiloIdDirigente", required = true) final String profiloIdDirigente,
    		@RequestParam(value = "attoId", required = true) final Long attoId,
    		@RequestParam(value = "aooIdAssegna", required = true) final String aooIdAssegna)throws GestattiCatchedException {
    	
    	try {
    		Atto atto = attoRepository.findOne(attoId);
	    	boolean warnCodice = false;
	        if(atto!=null){
	        	TipoTaskDTO task = workflowService.getDettaglioTask(taskId);
	        	int pos1 = task.getCandidateGroups().indexOf(Constants.BPM_INCARICO_SEPARATOR);
		    	long idConfInc = 0;
		    	if (pos1 > -1) {
		    		idConfInc = new Long(task.getCandidateGroups().substring(pos1+Constants.BPM_INCARICO_SEPARATOR.length()));
				}
		    	
//		    	int pos = task.getCandidateGroups().indexOf(Constants.BPM_ROLE_SEPARATOR);
//		    	long idAooPrecedente = 0;
//		    	if (pos > -1) {
//		    		String codiceAooPrecededente = task.getCandidateGroups().substring(pos+Constants.BPM_ROLE_SEPARATOR.length(),task.getCandidateGroups().indexOf(Constants.BPM_INCARICO_SEPARATOR));
//		    		
//		    		Iterable<Aoo> aoos = aooService.findByCodice(codiceAooPrecededente);
//			    	if(aoos!=null){
//			    		for(Aoo aoo : aoos){
//			    			if(aoo.getValidita()==null || aoo.getValidita().getValidoal()==null){
//			    				idAooPrecedente = aoo.getId();
//			    				break;
//			    			}
//			    		}
//			    	}
//		    		
//				}
		    	
		    	if(idConfInc>0) {
		    		List<ConfigurazioneIncaricoAoo> confs = configurazioneIncaricoAooRepository.findByConfigurazioneIncarico(idConfInc);
		    		if(confs!=null) {
		    			for (ConfigurazioneIncaricoAoo configurazioneIncaricoAoo : confs) {

								ConfigurazioneIncaricoAooId configurazioneIncaricoAooIdPrec = configurazioneIncaricoAoo.getPrimaryKey();
								Long idAooPrecedente = configurazioneIncaricoAooIdPrec.getIdAoo();
								
								configurazioneIncaricoAooRepository.delete(configurazioneIncaricoAooIdPrec);
								
								
								ConfigurazioneIncaricoAooId configurazioneIncaricoAooId = new ConfigurazioneIncaricoAooId();
								configurazioneIncaricoAooId.setIdAoo(Long.parseLong(aooIdAssegna));
								configurazioneIncaricoAooId.setIdConfigurazioneIncarico(idConfInc);
								
								
								ConfigurazioneIncaricoAoo newConf = new ConfigurazioneIncaricoAoo();
								newConf.setPrimaryKey(configurazioneIncaricoAooId);
								newConf.setDataCreazione(new DateTime());
								newConf.setDataManuale(configurazioneIncaricoAoo.getDataManuale());
								newConf.setGiorniScadenza(configurazioneIncaricoAoo.getGiorniScadenza());
								newConf.setOrdineFirma(configurazioneIncaricoAoo.getOrdineFirma());
								newConf.setQualificaProfessionale(null);
								configurazioneIncaricoAooRepository.save(newConf );
								
								ConfigurazioneIncarico configurazioneIncarico = configurazioneIncaricoRepository.findOne(idConfInc);
								configurazioneIncarico.setDataCreazione(new Date());
								configurazioneIncarico.setDataModifica(new Date());
								
								configurazioneIncaricoRepository.save(configurazioneIncarico);
								
								Iterable<AssegnazioneIncaricoDTO> listAssegnazioneIncaricoDTO = workflowService.getAssegnazioniIncarichi(taskId);
								
								bpmWrapperUtil.salvaCambioCoda(attoId, taskId, configurazioneIncarico, listAssegnazioneIncaricoDTO, new Long(profiloIdDirigente),idAooPrecedente);
								
								JsonObject json = new JsonObject();
						    	json.addProperty("warnCodice", warnCodice);
						    	return new ResponseEntity<String>(json.toString(),  HttpStatus.OK);
						}
		    		}
		    	}
	        }else{
	        	return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        JsonObject json = new JsonObject();
	    	json.addProperty("warnCodice", true);
        	return new ResponseEntity<String>(json.toString(),  HttpStatus.OK);
	    	
    	}catch(Exception e){
    		log.error("TaskDesktopResource :: cambiaCoda :: " + e.getMessage());
    		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
    		
    		
    		
    
    /**
     * POST  /taskBpms ->  riassegna un task ad un altro profilo.
     */
    @RequestMapping(value = "/taskBpms/reassignee",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> reassignee(
    		@RequestParam(value = "taskId", required = true) final String taskId,
    		@RequestParam(value = "profiloIdDirigente", required = true) final String profiloIdDirigente,
    		@RequestParam(value = "attoId", required = true) final Long attoId,
    		@RequestParam(value = "profiloIdAssegna", required = true) final String profiloIdAssegna,
    		@RequestParam(value = "idQualificaAssegna", required = true) final String idQualifica,
    		@RequestParam(value = "tasktype", required = false) final String tasktype) throws GestattiCatchedException {
    	try {
	    	log.debug("REST request to reassignee a Task : {}", taskId);
	    	Atto atto = attoRepository.findOne(attoId);
	    	boolean warnCodice = false;
	        if(atto!=null){
	        	
	    		Profilo profiloRiassegna = profiloRepository
	    				.findOne(Long.parseLong(profiloIdAssegna));
	    		if(profiloRiassegna.getFutureEnabled()==null || profiloRiassegna.getFutureEnabled().equals(false)) {
	    			JsonObject json = new JsonObject();
			    	json.addProperty("error", "Impossibile assegnare il task al profilo selezionato in quanto risulta disabilitato");
	    			return new ResponseEntity<String>(json.toString(), HttpStatus.INTERNAL_SERVER_ERROR);
	    		}
	    		Profilo profiloResponsabile = profiloRepository
	    				.findOne(Long.parseLong(profiloIdDirigente));
	    		QualificaProfessionale qualificaRiassegna = qualificaProfessionaleRepository
	    				.findOne(Long.parseLong(idQualifica));
	        	
	        	
	        	if(tasktype!=null && StringUtil.trimStr(tasktype).equalsIgnoreCase(TaskDesktopEnum.CARICHI_ASSESSORI.getDescrizione())) {
	        		
	        		//Controllo che l'assessore selezionato non abbia già inserito un visto
	        		Set<Avanzamento> lavorazioni = atto.getAvanzamento();
	    			if ( (lavorazioni != null) && (lavorazioni.size() > 0) ) {
	    				for (Iterator<Avanzamento> iterator = lavorazioni.iterator(); iterator.hasNext();) {
	    					Avanzamento avanzamento = (Avanzamento)iterator.next();
	    					if(avanzamento!=null && avanzamento.getProfilo()!=null && avanzamento.getProfilo().getId()!=null && avanzamento.getAttivita()!= null && avanzamento.getAttivita().equalsIgnoreCase(TipiAttivitaEnum.VISTA_E_INOLTRA.getDescrizione())) {
	    						long profiloAvanzamento = avanzamento.getProfilo().getId().longValue();
	    						long profiloIdAssegnalong = new Long(profiloIdAssegna).longValue();
	    						
	    						if(profiloAvanzamento==profiloIdAssegnalong) {
	    							warnCodice = true;
	    							break;
	    						}
	    						
	    					}
	    				}
	    			}
	    			
	    			if(!warnCodice) {
		        		List<Task> tasks = workflowService.getTaskAttiviAssessori(atto);
		        		
		        		
		        		long profiloIdAssegnalong = new Long(profiloIdAssegna).longValue();
		        		//prima controllo che non ci sia già un task assegnato all'assessore selezionato
		        		for (Task task : tasks) {
		        			if(task.getAssignee()!=null) {
		        				
		        				Profilo profiloTaskDaRiassegnare = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee());
		        				if(profiloTaskDaRiassegnare !=null) {
		        					long idProfiloTaskDaRiassegnare= profiloTaskDaRiassegnare.getId().longValue();
		        					
		        					if(idProfiloTaskDaRiassegnare==profiloIdAssegnalong) {
		    							warnCodice = true;
		    							break;
		    						}
		        				}
		        			}
		        		}
		        		
		        		if(!warnCodice) {
		        			Task taskDaRiassegnare = null;	
			        		for (Task task : tasks) {
								if(task.getId().equalsIgnoreCase(taskId)) {
									taskDaRiassegnare=task;
									break;
								}
							}
			        		if(taskDaRiassegnare!=null) {
			        		
			        			Profilo profiloTaskDaRiassegnare = bpmWrapperUtil.getProfiloByUsernameBpm(taskDaRiassegnare.getAssignee());
			        			Long idProfOriginario = profiloTaskDaRiassegnare!=null&&profiloTaskDaRiassegnare.getId()!=null?profiloTaskDaRiassegnare.getId():-1L;
			        		
				        		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(atto.getId());
				        		if(listConfigurazioneIncarico!=null) {
				        			for (ConfigurazioneIncaricoDto configurazioneIncaricoDto : listConfigurazioneIncarico) {
										if(configurazioneIncaricoDto!=null && configurazioneIncaricoDto.getConfigurazioneTaskCodice().equals(TipiParereEnum.PARERE_ASSESORE.getCodice())){
											for (int i = 0; i < configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto().size(); i++) {
												if(configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto().get(i).getIdProfilo().longValue() == idProfOriginario.longValue()){
													configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto().get(i).setIdProfilo(profiloRiassegna.getId());
													if(profiloRiassegna.getHasQualifica()!=null) {
														QualificaProfessionale qpRiassegna = null;
														for (QualificaProfessionale source : profiloRiassegna.getHasQualifica()) {
															qpRiassegna = source;
		
														}
														if(qpRiassegna!=null) {
															configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto().get(i).getQualificaProfessionaleDto().setId(qpRiassegna.getId());
														}
													}
													configurazioneIncaricoService.save(configurazioneIncaricoDto);
													break;
												}
											}
										}
									}
				        		}
			        		}
			        		workflowService.riassegnaTask(taskId, profiloRiassegna, qualificaRiassegna, profiloResponsabile);
		        		}
	    			}
	        	}else {
	        		workflowService.riassegnaTask(taskId, profiloRiassegna, qualificaRiassegna, profiloResponsabile);
	        	}
	        	
	        	
	        	
	        	
	        	JsonObject json = new JsonObject();
		    	json.addProperty("warnCodice", warnCodice);
	        	return new ResponseEntity<String>(json.toString(),  HttpStatus.OK);
	        }
	        else{
	        	return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
    	}
    	catch(Exception e){
    		log.error("TaskDesktopResource :: reassignee :: " + e.getMessage());
    		return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }

    /**
     * POST  /taskBpms -> get all the taskBpms by criteria.
     */
    @RequestMapping(value = "/taskBpms/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TaskDesktopDTO>> getAll(@RequestParam(value = "page" , required = false) final Integer offset,
                                  @RequestParam(value = "per_page", required = false) final Integer limit,
                                  @RequestParam(value = "tasktype", required = false) final String tasktype,
                                  @RequestParam(value = "userId", required = false) final Long userId,
                                  @RequestBody final String searchStr,
                          		  @RequestHeader(value = "profiloId" ) final Long profiloId )
                          				throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Pageable paginazione = null;
	    	if(offset!=null && limit!=null){
	    		paginazione = PaginationUtil.generatePageRequest(offset, limit);
	    	}
	    	
	    	Page<TaskDesktopDTO> page = null;
	    	if(profiloId == 0){ // list of task by profiles
	    		Utente utente = utenteService.findOne(userId);
	    		Iterable<Profilo> profili = utenteService.activeprofilos(utente.getUsername());
	    		page = taskDesktopService.findTasks(tasktype,profili, paginazione, search,null);
	    	}
	    	else{
	    		List<Profilo> profili = new ArrayList<Profilo>();
	    		profili.add(profiloService.findOne(profiloId));
	    		page = taskDesktopService.findTasks(tasktype,profili, paginazione, search,null);
	    	}
	
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms", offset, limit);
	        return new ResponseEntity<List<TaskDesktopDTO>>(page.getContent(), headers, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /taskBpms -> restituisce i gruppi costituiti dalle aoo di giacenza e per ogni gruppo, la prima pagina.
     */
    @RequestMapping(value = "/taskBpms/searchGroups",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TaskDesktopGroupDTO>> searchGroups(
    							  @RequestParam(value = "page" , required = false) final Integer offset,
    							  @RequestParam(value = "per_page" , required = false) final Integer firstPageSize,
                                  @RequestParam(value = "tasktype", required = false) final String tasktype,
                                  @RequestParam(value = "userId", required = false) final Long userId,
                          		  @RequestHeader(value = "profiloId" ) final Long profiloId,
                          		  @RequestBody final String searchStr)
                          				throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
    		List<TaskDesktopGroupDTO> groups = null;
	    	if(profiloId == 0){ // list of task by profiles
	    		Utente utente = utenteService.findOne(userId);
	    		Iterable<Profilo> profili = utenteService.activeprofilos(utente.getUsername());
	    		if (StringUtil.trimStr(tasktype).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO_GROUP.getDescrizione())) {
	    			List<Aoo> uffici = new ArrayList<Aoo>();
	    			
	    			Aoo aooRif = profili.iterator().next().getAoo();
					//prendo i profili degli uffici della sottostruttura per riprendere gli atti in coda
	    			//Set<Aoo> aooDellaStessaSottostruttura = null;
	    			Set<Aoo> aooSottostanti = null;
					
					if(aooRif != null) {
						Aoo aoo = aooService.findOne(aooRif.getId());
						boolean direttoreGenerale = aoo.getSpecializzazione()!=null && aoo.getSpecializzazione().equalsIgnoreCase(SpecializzazioneAooEnum.DIREZIONE_GENERALE.getDescrizione());
						//Aoo aooDirezione = aooService.getAooDirezione(aooRif);
						//if(aooDirezione!=null) {
							//aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), aooRif, true);
							if(direttoreGenerale) {
								aooSottostanti = aooService.findDiscendentiOfAooNotDirezione(aooRif.getId(), aooRif, true);
							}else {
								aooSottostanti = aooService.findDiscendentiOfAoo(aooRif.getId(), true);
							}
								
						//}
					}
					uffici.addAll(aooSottostanti);
	    			for(Aoo ufficio : uffici) {
	    				if(ufficio!=null) {
	    					TaskDesktopGroupDTO group = new TaskDesktopGroupDTO();
	    					group.setUfficio(AooTransformer.toDto(ufficio));
	    					search.addProperty("idUfficioGiacenza", ufficio.getId());
	    					Pageable paginazione = null;
	    			    	if(offset!=null && firstPageSize!=null){
	    			    		paginazione = PaginationUtil.generatePageRequest(offset, firstPageSize);
	    			    	}else {
	    			    		paginazione = PaginationUtil.generatePageRequest(1, 10);
	    			    	}
	    					Page<TaskDesktopDTO> tasks = taskDesktopService.findTasks(tasktype,profili, paginazione, search, ufficio);
	    					for (TaskDesktopDTO task : tasks) {
	    						task.setAtto(attoService.findOneScrivaniaBasic(Long.parseLong(task.getTaskBpm().getBusinessKey())));
	    					}
	    					group.setPage(tasks);
	    					if(groups==null) {
	    						groups = new ArrayList<TaskDesktopGroupDTO>();
	    					}
	    					groups.add(group);
	    				}
	    			}
	    			
	    			
	    		}else {
	    			groups = taskDesktopService.findRagioneriaGroup(search, tasktype,profili, firstPageSize);
	    		}
	    		
	    	}
	    	else{
	    		List<Profilo> profili = new ArrayList<Profilo>();
	    		Profilo profilo = profiloService.findOne(profiloId);
	    		profili.add(profilo);
	    		if (StringUtil.trimStr(tasktype).equalsIgnoreCase(TaskDesktopEnum.MONITORAGGIO_GROUP.getDescrizione())) {
	    			List<Aoo> uffici = new ArrayList<Aoo>();
	    			
	    			Aoo aooRif = profili.iterator().next().getAoo();
					//prendo i profili degli uffici della sottostruttura per riprendere gli atti in coda
					//Set<Aoo> aooDellaStessaSottostruttura = null;
	    			Set<Aoo> aooSottostanti = null;
						
					if(aooRif != null) {
						Aoo aoo = aooService.findOne(aooRif.getId());
						boolean direttoreGenerale = aoo.getSpecializzazione()!=null && aoo.getSpecializzazione().equalsIgnoreCase(SpecializzazioneAooEnum.DIREZIONE_GENERALE.getDescrizione());
						//Aoo aooDirezione = aooService.getAooDirezione(aooRif);
						//if(aooDirezione!=null) {
							//aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), aooRif, true);
						if(direttoreGenerale) {
							aooSottostanti = aooService.findDiscendentiOfAooNotDirezione(aooRif.getId(), aooRif, true);
						}else {
							aooSottostanti = aooService.findDiscendentiOfAoo(aooRif.getId(), true);
						}
						//}
					}
					uffici.addAll(aooSottostanti);
	    			for(Aoo ufficio : uffici) {
	    				if(ufficio!=null) {
	    					TaskDesktopGroupDTO group = new TaskDesktopGroupDTO();
	    					group.setUfficio(AooTransformer.toDto(ufficio));
	    					search.addProperty("idUfficioGiacenza", ufficio.getId());
	    					Pageable paginazione = null;
	    			    	if(offset!=null && firstPageSize!=null){
	    			    		paginazione = PaginationUtil.generatePageRequest(offset, firstPageSize);
	    			    	}else {
	    			    		paginazione = PaginationUtil.generatePageRequest(1, 10);
	    			    	}
	    					Page<TaskDesktopDTO> tasks = taskDesktopService.findTasks(tasktype,profili, paginazione, search,ufficio);
	    					for (TaskDesktopDTO task : tasks) {
	    						task.setAtto(attoService.findOneScrivaniaBasic(Long.parseLong(task.getTaskBpm().getBusinessKey())));
	    					}
	    					group.setPage(tasks);
	    					if(groups==null) {
	    						groups = new ArrayList<TaskDesktopGroupDTO>();
	    					}
	    					groups.add(group);
	    				}
	    			}
	    		}else {
	    			groups = taskDesktopService.findRagioneriaGroup(search, tasktype,profili, firstPageSize);
	    		}
	    	}
	    	HttpHeaders headers = null;
	    	for(TaskDesktopGroupDTO group : groups) {
	    		headers = PaginationUtil.generateGroupPaginationHttpHeaders(group.getUfficio().getId().intValue(), group.getPage().getTotalPages(), group.getPage().getTotalElements(), "/api/taskBpms", 1, firstPageSize, headers);
	    	}
	        return new ResponseEntity<List<TaskDesktopGroupDTO>>(groups, headers, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /taskBpms/findAllInCarico -> get all the taskBpms with assignee by criteria.
     */
    @RequestMapping(value = "/taskBpms/findAllInCarico",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TaskDesktopDTO>> findAllInCarico(@RequestParam(value = "page" , required = false) final Integer offset,
                                  @RequestParam(value = "per_page", required = false) final Integer limit, @RequestBody final String searchStr)
                          				throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Pageable paginazione = null;
	    	if(offset!=null && limit!=null){
	    		paginazione = PaginationUtil.generatePageRequest(offset, limit);
	    	}
	    	
	    	Page<TaskDesktopDTO> page = taskDesktopService.findAllTaskInCarico(paginazione, search);
	
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms", offset, limit);
	        return new ResponseEntity<List<TaskDesktopDTO>>(page.getContent(), headers, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /taskBpms/vistoMassivo -> vistoMassivo
     */
    @RequestMapping(value = "/taskBpms/vistoMassivo",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> vistoMassivo(
    		@RequestParam( value = "idTasks", required=true) final List<String> idTasks,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
    	try {
    		runtimeOperationService.add(RuntimeOperationTaskEnum.VISTO_MASSIVO, profiloId);
            Set<String> skipAttos = new HashSet<String>();
            
    		for (String idTask : idTasks) {
				log.info("Visto Massivo: " + idTask);
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
    			
    			Atto atto = workflowService.getAttoByTaskId(idTask);
    			
    			// Ciclo le azioni
    			for (DecisioneWorkflowDTO decMassiva : lstDecMassiva) {
    				log.info("Codice Azione: " + decMassiva.getCodiceAzioneUtente());
        			
        			boolean checkParere = true;
        			if(atto.getPareri() != null && !atto.getPareri().isEmpty()) {
        				for (Parere par : atto.getPareri()) {
    						if((par.getAnnullato() == null || !par.getAnnullato().booleanValue()) && par.getTipoAzione().getCodice().equalsIgnoreCase(decMassiva.getTipoParere())) {
    							checkParere = false;
    						}
    					}
        			}
        			
        			boolean checkRequiredFieldEnabled = false;
        			try {
        				checkRequiredFieldEnabled = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_CHECK_REQUIRED_FIELDS, "false"));
        			}catch(Exception e) {}
        			
        			if(checkParere && (!checkRequiredFieldEnabled || attoService.hasRequiredFields(atto.getId(), idTask, profiloId))) {
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
            				transactionService.visto(idTask, profiloId, parere, atto, decMassiva);
            				if(skipAttos.contains(atto.getCodiceCifra())) {
            					skipAttos.remove(atto.getCodiceCifra());
            				}
            			}
            			catch (Exception ae) {
            				if(ae instanceof LiquidazioniEmptyException) {
            					log.info("LiquidazioniEmptyException rilevata per atto "+atto.getCodiceCifra() + " "+ae.getMessage());
    							registrazioneAvanzamentoService.registraAvanzamentoConWarning(atto.getId().longValue(), profiloId, NomiAttivitaAtto.CONTABILITA_RECUPERO, decMassiva.getCodiceAzioneUtente(), "Non ci sono liquidazioni associate al provvedimento");
            				}else if(ae instanceof ContabilitaServiceException) {
            					log.info("ContabilitaServiceException rilevata per atto "+atto.getCodiceCifra() + " "+ae.getMessage());
    							registrazioneAvanzamentoService.registraAvanzamentoConWarning(atto.getId().longValue(), profiloId, NomiAttivitaAtto.CONTABILITA_RECUPERO, decMassiva.getCodiceAzioneUtente(), "Occorre effettuare l'invio dei dati al sistema contabile prima di proseguire.");
            				}else {
            				
	            				throw new GestattiCatchedException(ae, "Si \u00E8 verificato un errore durante l'elaborazione dell'Atto " + atto.getCodiceCifra()
	            					+"\u003Cbr\u003ESi prega di riprovare.");
            				}
        				}
        			}else {
        				if(!checkParere) {
        					log.warn("Parere " + decMassiva.getTipoParere() + " gi\u00E0 presente su atto " + atto.getCodiceCifra());
        				}
        				skipAttos.add(atto.getCodiceCifra());
        			}
				}
			}
    		if(skipAttos.size() > 0) {
    			String skipMessage = "";
    			if(skipAttos.size() == 1) {
    				skipMessage = "Impossibile procedere con il visto massivo sull'atto " + skipAttos.iterator().next() + " in quanto non contiene alcuni dei dati obbligatori.\u003Cbr\u003ESi prega di compilare tutti i dati richiesti accedendo alla schermata di dettaglio.";
    			}else {
    				String attiString = "";
    				for(String atto : skipAttos) {
    					if(attiString.isEmpty()) {
    						attiString = atto;
    					}else {
    						attiString += "," + atto;
    					}
    				}
    				skipMessage = "Impossibile procedere con il visto massivo sugli atti " + attiString + " in quanto non contengono alcuni dei dati obbligatori.\u003Cbr\u003ESi prega di compilare tutti i dati richiesti accedendo alle relative schermate di dettaglio.";
    			}
    			throw new GestattiCatchedException(skipMessage);
    		}
    		runtimeOperationService.end(RuntimeOperationTaskEnum.VISTO_MASSIVO, profiloId);
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(RuntimeOperationNotAlloweException e) {
    		throw e;
    	}catch(Exception e){
    		runtimeOperationService.end(RuntimeOperationTaskEnum.VISTO_MASSIVO, profiloId);
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * POST  /taskBpms/firmaMassiva -> firmaMassiva
     */
    @RequestMapping(value = "/taskBpms/firmaMassiva",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<FirmaRemotaDTO> firmaMassiva(
    		@RequestParam( value = "idTasks", required=true) final List<String> idTasks,
    		@RequestParam( value = "documentiDaGenerareFirmare", required=false) String attiGenerazioneFirma,
    		@RequestParam( value = "codiceFiscale", required=true) final String codiceFiscale,
    		@RequestParam( value = "password", required=true) final String password,
    		@RequestParam( value = "otp", required=true) final String otp,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
    	try {
    		runtimeOperationService.add(RuntimeOperationTaskEnum.FIRMA_MASSIVA, profiloId);
    		JsonParser parser = new JsonParser();
    		if(attiGenerazioneFirma!=null) {
    			attiGenerazioneFirma = "[" + attiGenerazioneFirma + "]";
    			JsonElement attiGenerazioneFirmaJson = parser.parse(attiGenerazioneFirma);
    			if(attiGenerazioneFirmaJson!=null && !attiGenerazioneFirmaJson.isJsonNull() && attiGenerazioneFirmaJson.isJsonArray()) {
	    			taskDesktopService.eseguiFirmaMassiva(idTasks, attiGenerazioneFirmaJson.getAsJsonArray(), profiloId, codiceFiscale, password, otp);
    			}else {
    				throw new GestattiCatchedException("Errore nel parsing dei dati");
    			}
    		}else {
    			taskDesktopService.eseguiFirmaMassiva(idTasks, null, profiloId, codiceFiscale, password, otp);
    		}
    		runtimeOperationService.end(RuntimeOperationTaskEnum.FIRMA_MASSIVA, profiloId);
    		return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO() , HttpStatus.OK);
    	}
    	catch(RuntimeOperationNotAlloweException e) {
    		throw e;
    	}
    	catch (FirmaRemotaException e) {
    		runtimeOperationService.end(RuntimeOperationTaskEnum.FIRMA_MASSIVA, profiloId);
			return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(e.getErrorCode(), e.getMessage()),	HttpStatus.BAD_REQUEST);
		}
    	catch(Exception e){
    		runtimeOperationService.end(RuntimeOperationTaskEnum.FIRMA_MASSIVA, profiloId);
    		throw new GestattiCatchedException(e);
    	} 
    }
    		
    
    /**
     * POST  /taskBpms/searchByDelegante -> get all the taskBpms by delegante.
     */
    @RequestMapping(value = "/taskBpms/searchByDelegante",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TaskDesktopDTO>> getAllByDelegante(@RequestParam(value = "page" , required = false) final Integer offset,
                                  @RequestParam(value = "per_page", required = false) final Integer limit,
                                  @RequestParam(value = "tasktype", required = false) final String tasktype,
                                  @RequestParam(value = "userId", required = false) final Long userId,
                                  @RequestParam(value = "profiloId" ) final Long profiloId,
                                  @RequestBody final String searchStr )
                          				throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Pageable paginazione = null;
	    	if(offset!=null && limit!=null){
	    		paginazione = PaginationUtil.generatePageRequest(offset, limit);
	    	}
	    	
	    	Page<TaskDesktopDTO> page = null;
	    	List<Profilo> profili = new ArrayList<Profilo>();
    		profili.add(profiloService.findOne(profiloId));
    		page = taskDesktopService.findTasks(tasktype,profili, paginazione, search,null);
	
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms", offset, limit);
	        return new ResponseEntity<List<TaskDesktopDTO>>(page.getContent(), headers, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /taskBpms -> proposte inseribili in Odg.
     */
    @RequestMapping(value = "/taskBpms/searchOdg",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Atto>> getInseribiliOdg(
    		@RequestParam(value = "page" , required = false) final Integer offset,
            @RequestParam(value = "per_page", required = false) final Integer limit,
            @RequestParam(value = "organo", required = true) String organo,
            @RequestBody final String searchStr,
    		@RequestHeader(value = "profiloId" ) final Long profiloId )
    				throws GestattiCatchedException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Pageable paginazione = null;
			if (offset != null && limit != null) {
				paginazione = PaginationUtil.generatePageRequest(offset, limit);
			}
			
			// TODO: gestire la ricerca dei sospesi (SI/NO)
			boolean includiSospesi = true;
			
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
			Page<Atto> page = taskDesktopService.findInseribiliOdg(isOdgGiunta, includiSospesi, paginazione, search);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms/searchOdg", offset, limit);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers, HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}
    
    
    /**
     * POST  /taskBpms -> PostSeduta
     */
    @RequestMapping(value = "/taskBpms/searchPostSeduta",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Atto>> getPostSeduta(
    		@RequestParam(value = "page" , required = false) final Integer offset,
            @RequestParam(value = "per_page", required = false) final Integer limit,
            @RequestParam(value = "organo", required = true) String organo,
            @RequestBody final String searchStr,
    		@RequestHeader(value = "profiloId" ) final Long profiloId )
    				throws GestattiCatchedException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Pageable paginazione = null;
			if (offset != null && limit != null) {
				paginazione = PaginationUtil.generatePageRequest(offset, limit);
			}
			
			// TODO: gestire la ricerca dei sospesi (SI/NO)
			boolean includiSospesi = true;
			
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
			Page<Atto> page = taskDesktopService.findPostSeduta(isOdgGiunta, includiSospesi, paginazione, search);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms/searchPostSeduta", offset, limit);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers, HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}
    
    @SuppressWarnings("unchecked")
	@RequestMapping(value = "/taskBpms/searchOdgXls", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Timed
	public ResponseEntity<FileSystemResource> searchOdgXls(
			@RequestParam final String searchStr)
			throws GestattiCatchedException {
		try{
			JsonParser parser = new JsonParser();
			JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Pageable paginazione = PaginationUtil.generatePageRequest(1, Integer.MAX_VALUE);
			
			boolean includiSospesi = true;
			
			Page<Atto> page = taskDesktopService.findInseribiliOdg(true, includiSospesi, paginazione, search);
			
			java.io.File file = excelService.createExcelOdg(page.getContent());
			file.deleteOnExit();
			return DownloadFileUtil.responseStream(file, "atti.xls");
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    
    /**
     * POST  /taskBpms -> proposte in attesa esito seduta
     */
    @RequestMapping(value = "/taskBpms/searchAttesaEsito",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Atto>> searchAttesaEsito(
    		@RequestParam(value = "page" , required = false) final Integer offset,
            @RequestParam(value = "per_page", required = false) final Integer limit,
            @RequestParam(value = "organo", required = true) String organo,
            @RequestParam(value = "tipoRicerca", required = true) String tipoRicerca,
            @RequestBody final String searchStr,
    		@RequestHeader(value = "profiloId" ) final Long profiloId )
    				throws GestattiCatchedException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Pageable paginazione = null;
			if (offset != null && limit != null) {
				paginazione = PaginationUtil.generatePageRequest(offset, limit);
			}
			
			// TODO: gestire la ricerca dei sospesi (SI/NO)
			boolean includiSospesi = true;
			
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
			
			Page<Atto> page = taskDesktopService.findAttesaEsito(
					isOdgGiunta, includiSospesi, tipoRicerca,
					paginazione, search);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms/searchAttesaEsito", offset, limit);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers, HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}
    
    /**
     * POST  /taskBpms -> atti visualizzabili in Commissioni cons, rev cont, cons quart
     */
    @RequestMapping(value = "/taskBpms/searchAttiCommissioni",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Atto>> searchAttiCommissioni(
    		@RequestParam(value = "page" , required = false) final Integer offset,
            @RequestParam(value = "per_page", required = false) final Integer limit,
            @RequestParam(value = "organo", required = true) String organo,
            @RequestParam(value = "tipoRicerca", required = true) String tipoRicerca,
            @RequestBody final String searchStr,
    		@RequestHeader(value = "profiloId" ) final Long profiloId )
    				throws GestattiCatchedException {
		try {
			JsonParser parser = new JsonParser();
			JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Pageable paginazione = null;
			if (offset != null && limit != null) {
				paginazione = PaginationUtil.generatePageRequest(offset, limit);
			}
			
			// TODO: gestire la ricerca dei sospesi (SI/NO)
			boolean includiSospesi = true;
			
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
			
			Page<Atto> page = taskDesktopService.findAttiCommissioni(
					isOdgGiunta, includiSospesi, tipoRicerca,
					paginazione, search);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/taskBpms/searchAttesaVistoSegretario", offset, limit);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers, HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}


    @RequestMapping(value = "/taskBpms/{idAtto}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getMyNextTask(@PathVariable final Long idAtto,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to getMyNextTask IdAtto : {}", idAtto);
	
	        String retVal = workflowService.getMyNextTaskId(idAtto, profiloId);
	
	        JsonObject json = new JsonObject();
			json.addProperty("taskId", retVal);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    @RequestMapping(value = "/taskBpms/listaLavorazioni",
	    method = RequestMethod.GET,
	    produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> findNomiTaskDeleganteOfCamundaProcesses() throws GestattiCatchedException{
		try{
			log.debug("REST request to listaLavorazioni");
			return new ResponseEntity<List<String>>(taskDesktopService.findAllTaskNames(), HttpStatus.OK);
		}
		catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}
}
