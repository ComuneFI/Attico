package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;
import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.TipoListaTaskDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.ConfigurazioneIncaricoService;
import it.linksmt.assatti.service.ConfigurazioneTaskService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.dto.AssegnazioneIncaricoDettaglioDto;
import it.linksmt.assatti.service.dto.AssegnazioneIncaricoResponseDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;
//TODO integrazione import it.linksmt.assatti.gestatti.bpmwrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.Constants;

/**
 * REST controller for managing Attivita.
 */
@RestController
@RequestMapping("/api")
public class LavorazioneResource {

    private final Logger log = LoggerFactory.getLogger(LavorazioneResource.class);

    @Inject
    private WorkflowServiceWrapper workflowService;
    
    @Inject
    private ConfigurazioneTaskService configurazioneTaskService;
    
    @Inject
    private ConfigurazioneIncaricoService configurazioneIncaricoService;
    
    @Inject
    private ProfiloService profiloService;
    
    @Inject
    private AooService aooService;


    /**
     * GET  /lavoraziones/listadecisioni -> possibili azioni
     */
    @RequestMapping(value = "/lavoraziones/listadecisioni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<DecisioneWorkflowDTO>> getPulsantiWorkflow(
    		@RequestParam(value = "taskBpmId"  ,required=true ) final String taskBpmId,
    		@RequestHeader(value = "profiloId"  ) final Long profiloId
    		) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request getPulsantiWorkflow taskBpmId: {}, profiloId: {}", taskBpmId, profiloId);
	    		
	    	Iterable<DecisioneWorkflowDTO> page = workflowService.getPulsantiWorkflow(taskBpmId, profiloId );
	    	return new ResponseEntity<Iterable<DecisioneWorkflowDTO>>( page , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /lavoraziones/assegnazioniincarichi -> imposta le assegnazioni a uffici o profili per i task successivi
     */
    @RequestMapping(value = "/lavoraziones/assegnazioniincarichi",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<AssegnazioneIncaricoDTO>> getAssegnazioniIncarichi(
    		@RequestParam(value = "taskBpmId"  ,required=true ) final String taskBpmId,
    		@RequestHeader(value = "profiloId"  ) final Long profiloId
    		) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request getAssegnazioniIncarichi taskBpmId: {}, profiloId: {}", taskBpmId, profiloId);
	    		
	    	Iterable<AssegnazioneIncaricoDTO> page = workflowService.getAssegnazioniIncarichi(taskBpmId);
	    	return new ResponseEntity<Iterable<AssegnazioneIncaricoDTO>>( page , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /lavoraziones/assegnazioniincarichidettaglio
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/lavoraziones/assegnazioniincarichidettaglio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getAssegnazioniIncarichiDettaglio(
    		@RequestParam(value = "idAtto", required=true ) final Long idAtto,
    		@RequestParam(value = "taskBpmId", required=true ) final String taskBpmId,
    		@RequestParam(value = "proponenteAooId", required=false ) final Long proponenteAooId,
    		@RequestParam(value = "profiloAooId", required=false ) final Long profiloAooId,
    		@RequestHeader(value = "profiloId"  ) final Long profiloId
    		) throws GestattiCatchedException {
    	AssegnazioneIncaricoResponseDto assegnazioneIncaricoResponseDto = new AssegnazioneIncaricoResponseDto();
    	
    	try{
    		log.debug("REST request getAssegnazioniIncarichiDettaglio idAtto: {}", idAtto);
	    	log.debug("REST request getAssegnazioniIncarichiDettaglio taskBpmId: {}", taskBpmId);
	    	log.debug("REST request getAssegnazioniIncarichiDettaglio proponenteAooId: {}", proponenteAooId);
	    	log.debug("REST request getAssegnazioniIncarichiDettaglio profiloAooId: {}", profiloAooId);

	    	if(idAtto!=null && idAtto>0L) {
	    		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(idAtto);
	    		assegnazioneIncaricoResponseDto.setListConfigurazioneIncaricoDto(listConfigurazioneIncarico);
	    	}

	    	Boolean assigneed = workflowService.isTaskAssigneedToProfile(taskBpmId, profiloId);
	    	if(assigneed!=null && assigneed) {
		    	List<AssegnazioneIncaricoDettaglioDto> listAssegnazioneIncaricoDettaglioDto = null;
		    	Iterable<AssegnazioneIncaricoDTO> listAssegnazioneIncaricoDTO = workflowService.getAssegnazioniIncarichi(taskBpmId);
		    	
		    	if(listAssegnazioneIncaricoDTO!=null) {
		    		listAssegnazioneIncaricoDettaglioDto = new ArrayList<>();
		    		
			    	for(AssegnazioneIncaricoDTO a : listAssegnazioneIncaricoDTO) {
			    		
			    		AssegnazioneIncaricoDettaglioDto assegnazioneIncaricoDettaglioDto = 
			    				buildAssegnazioneIncaricoDettaglioDto(a.getCodiceConfigurazione(), proponenteAooId, profiloAooId);
			    		listAssegnazioneIncaricoDettaglioDto.add(assegnazioneIncaricoDettaglioDto);
			    	}
		    	}
		    	
		    	assegnazioneIncaricoResponseDto.setListAssegnazioneIncaricoDettaglio(listAssegnazioneIncaricoDettaglioDto);
	    	}
	    	
	    	return new ResponseEntity<AssegnazioneIncaricoResponseDto>(assegnazioneIncaricoResponseDto , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /lavoraziones/cambioCoda
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/lavoraziones/cambioCoda",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity cambioCoda(
    		@RequestParam(value = "idAtto", required=true ) final Long idAtto,
    		@RequestParam(value = "taskBpmId", required=true ) final String taskBpmId,
    		@RequestParam(value = "idAooProfilo", required=true ) final Long idAooProfilo
    		) throws GestattiCatchedException {
    	AssegnazioneIncaricoResponseDto assegnazioneIncaricoResponseDto = new AssegnazioneIncaricoResponseDto();
    	
    	try{
	    	if(idAtto!=null && idAtto>0L) {
	    		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(idAtto);
	    		assegnazioneIncaricoResponseDto.setListConfigurazioneIncaricoDto(listConfigurazioneIncarico);
	    	
		    	List<AssegnazioneIncaricoDettaglioDto> listAssegnazioneIncaricoDettaglioDto = new ArrayList<AssegnazioneIncaricoDettaglioDto>();
		    	
		    	TipoTaskDTO task = workflowService.getDettaglioTask(taskBpmId);
		    	
		    	Set<Aoo> aooDellaStessaSottostruttura = null;
		    	
		    	//let pos = task.taskBpm.candidateGroups.indexOf($scope.bpmSeparator.BPM_INCARICO_SEPARATOR);
		    	int pos = task.getCandidateGroups().indexOf(Constants.BPM_INCARICO_SEPARATOR);
		    	long idConfIncarico = 0;
		    	if (pos > -1) {
					idConfIncarico = new Long(task.getCandidateGroups().substring(pos+Constants.BPM_INCARICO_SEPARATOR.length()));
				}
		    	
		    	if(idAooProfilo != null && idAooProfilo.longValue()>0) {
		    		
		    		Aoo aooRif = aooService.findOne(idAooProfilo);
    		    	if(aooRif != null) {
    					Aoo aooDirezione = aooService.getAooDirezione(aooRif);
    					if(aooDirezione!=null) {
    						aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), aooRif, true);
    					}
    		    	}
		    	}
		    	
		    	if(listConfigurazioneIncarico!=null) {
			    	for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
			    		if(idConfIncarico==c.getId()) {
				    		AssegnazioneIncaricoDettaglioDto assegnazioneIncaricoDettaglioDto = 
				    				buildAssegnazioneIncaricoDettaglioDto(c.getConfigurazioneTaskCodice(), null, null);
				    		
				    		List<Aoo> codeDisponibili = new ArrayList<Aoo>();
				    		
				    		if(assegnazioneIncaricoDettaglioDto != null && assegnazioneIncaricoDettaglioDto.getListAoo()!=null) {
				    			for (int i = 0; i < assegnazioneIncaricoDettaglioDto.getListAoo().size(); i++) {
									Aoo aoo = assegnazioneIncaricoDettaglioDto.getListAoo().get(i);
									if(isAooDellaSottostruttura(aoo,aooDellaStessaSottostruttura)) {
										codeDisponibili.add(aoo);
									}
								}
				    		}
				    		assegnazioneIncaricoDettaglioDto.setListAoo(codeDisponibili);
				    		
				    		listAssegnazioneIncaricoDettaglioDto.add(assegnazioneIncaricoDettaglioDto);
				    		break;
			    		}
			    	}
		    	}
		    	assegnazioneIncaricoResponseDto.setListAssegnazioneIncaricoDettaglio(listAssegnazioneIncaricoDettaglioDto);
		    }
	    	return new ResponseEntity<AssegnazioneIncaricoResponseDto>(assegnazioneIncaricoResponseDto , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private boolean isAooDellaSottostruttura (Aoo aoo, Set<Aoo> sottostruttura) {
    	if(aoo!=null && aoo.getId()!=null && sottostruttura!=null && sottostruttura.size()>0) {
    		for (Aoo aoo2 : sottostruttura) {
				if(aoo.getId().longValue()==aoo2.getId().longValue()) {
					return true;
				}
			}
    	}
    	return false;
    }
    
    
    /**
     * GET  /lavoraziones/assegnazioneincaricodettaglio
     */
	@RequestMapping(value = "/lavoraziones/assegnazioneincaricodettaglio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AssegnazioneIncaricoResponseDto> getAssegnazioneIncaricoDettaglio(
    		@RequestParam(value = "idAtto", required=true ) final Long idAtto,
    		@RequestParam(value = "codiceConfigurazioneTask", required=true) final String codiceConfigurazioneTask,
    		@RequestParam(value = "proponenteAooId", required=false ) final Long proponenteAooId,
    		@RequestParam(value = "profiloAooId", required=false ) final Long profiloAooId
    		) throws GestattiCatchedException {
		
		AssegnazioneIncaricoResponseDto assegnazioneIncaricoResponseDto = new AssegnazioneIncaricoResponseDto();
		try{
    		log.debug("REST request getAssegnazioneIncaricoDettaglio idAtto: {}", idAtto);
	    	log.debug("REST request getAssegnazioneIncaricoDettaglio codiceConfigurazioneTask: {}", codiceConfigurazioneTask);
	    	log.debug("REST request getAssegnazioneIncaricoDettaglio proponenteAooId: {}", proponenteAooId);
	    	log.debug("REST request getAssegnazioneIncaricoDettaglio profiloAooId: {}", profiloAooId);
	    	
	    	if(idAtto!=null && idAtto>0L) {
	    		ConfigurazioneIncaricoDto configurazioneIncaricoDto = configurazioneIncaricoService
	    				.findByLastByCodiceAndAtto(codiceConfigurazioneTask, idAtto);
	    		
	    		ArrayList<ConfigurazioneIncaricoDto> confIncarico = new ArrayList<ConfigurazioneIncaricoDto>();
	    		if (configurazioneIncaricoDto != null) {
	    			confIncarico.add(configurazioneIncaricoDto);
	    		}	    		
	    		
	    		assegnazioneIncaricoResponseDto.setListConfigurazioneIncaricoDto(confIncarico);
	    	}
	    	
			AssegnazioneIncaricoDettaglioDto assegnazioneIncaricoDettaglioDto = 
					buildAssegnazioneIncaricoDettaglioDto(codiceConfigurazioneTask, proponenteAooId, profiloAooId);
			
			ArrayList<AssegnazioneIncaricoDettaglioDto> assIncarico = new ArrayList<AssegnazioneIncaricoDettaglioDto>();
			assIncarico.add(assegnazioneIncaricoDettaglioDto);
			
			assegnazioneIncaricoResponseDto.setListAssegnazioneIncaricoDettaglio(assIncarico);
    	
			return new ResponseEntity<AssegnazioneIncaricoResponseDto>(assegnazioneIncaricoResponseDto , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
	
	/**
     * GET  /lavoraziones/commissioniQuartRev
     */
	@RequestMapping(value = "/lavoraziones/confTaskAoos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> getConfTaskAoos(
    		@RequestParam(value = "codiceConfigurazioneTask", required=true) final String codiceConfigurazioneTask
    		) throws GestattiCatchedException {
		
		try{
	    	log.debug("REST request getConfTaskAoos codiceConfigurazioneTask: {}", codiceConfigurazioneTask);
	    	
	    	ConfigurazioneTaskDto dto = configurazioneTaskService.findByCodice(codiceConfigurazioneTask);
	    	List<Aoo> aoos = new ArrayList<Aoo>();
	    	
	    	if(dto!=null && dto.getListAoo()!=null && dto.getListAoo().size() > 0) {
	    		List<Aoo> tempAoos = aooService.findEnabledById(dto.getListAoo());
	    		for(Aoo aoo : tempAoos) {
	    			aoos.add(DomainUtil.minimalAoo(aoo));
	    		}
	    	}
    	
			return new ResponseEntity<List<Aoo>>(aoos , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
	
	
	private AssegnazioneIncaricoDettaglioDto buildAssegnazioneIncaricoDettaglioDto(String codiceConfigurazioneTask,
			 final Long proponenteAooId, final Long profiloAooId) throws Exception {
		
		AssegnazioneIncaricoDettaglioDto assegnazioneIncaricoDettaglioDto = new AssegnazioneIncaricoDettaglioDto();
		ConfigurazioneTaskDto configurazioneTaskDto = configurazioneTaskService.findByCodice(codiceConfigurazioneTask);
		
		if(configurazioneTaskDto==null) {
			throw new GestattiCatchedException("Codice ConfigurazioneTask inesistente: " + codiceConfigurazioneTask);
		}
		
		assegnazioneIncaricoDettaglioDto.setConfigurazioneTaskDto(configurazioneTaskDto);

		if(configurazioneTaskDto.getTipoConfigurazioneTaskId()==1L) { // profilo
			List<Profilo> listProfiloMinimal = configurazioneTaskService.getCandidateProfList(configurazioneTaskDto, proponenteAooId, profiloAooId);
			assegnazioneIncaricoDettaglioDto.setListProfilo(listProfiloMinimal);
		} else if(configurazioneTaskDto.getTipoConfigurazioneTaskId()==2L) { // ufficio
			List<Aoo> listAoo = null;
			if(configurazioneTaskDto.isProponente()) {
				List<Long> listIdAoo = new ArrayList<>();
				listIdAoo.add(proponenteAooId);
				listAoo = aooService.findEnabledById(listIdAoo);
			} else {
				listAoo = aooService.findEnabledById(configurazioneTaskDto.getListAoo());
			}
			
			if(configurazioneTaskDto.isStessaDirezioneProponente() && listAoo != null && listAoo.size() > 0) {
				//filtro la lista prendendo solo le aoo che fanno parte della stessa direzione del proponente
				Aoo aooProponente = aooService.findOne(proponenteAooId);
				
				//trovo l'id della direzione proponente che può essere l'ufficio proponente oppure uno dei nodi padre
				long idDirezioneProponente = 0L;
				if(aooProponente!=null) {
					if(aooProponente.getTipoAoo()!=null && aooProponente.getTipoAoo().getCodice()!=null && aooProponente.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
						idDirezioneProponente = aooProponente.getId().longValue();
					}else {
						
						Aoo direzione = aooService.getAooDirezione(aooProponente);
						if(direzione != null) {
							idDirezioneProponente = direzione.getId().longValue();
						}
					}
				}
				if(idDirezioneProponente > 0L) {
					List<Aoo> listUfficiConfigurazione = new ArrayList<Aoo>();
					listUfficiConfigurazione.addAll(listAoo);
					
					//listAoo conterrà le aoo da visualizzare per questo incarico
					listAoo.clear();
					
					for(Aoo aoo : listUfficiConfigurazione) {
						if(aoo!=null && (aoo.getValidita()==null || aoo.getValidita().getValidoal() == null)) {
							Long direzione = null;
							if(aoo.getTipoAoo()!=null && aoo.getTipoAoo().getCodice()!=null && aoo.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
								direzione = aoo.getId();
							}else {
								Aoo dir = aooService.getAooDirezione(aoo);
								if(dir!=null) {
									direzione = dir.getId();
								}
							}
							if(direzione!=null && direzione.longValue() > 0L && direzione.equals(idDirezioneProponente)) {
								listAoo.add(aoo);
							}
						}
					}
					
				}
				
			}
			
			
			List<Aoo> listAooMin = null;
			if(listAoo!=null && listAoo.size() > 0) {
				for(Aoo a : listAoo) {
					Aoo aMin = DomainUtil.minimalAoo(a);
					if(listAooMin == null) {
						listAooMin = new ArrayList<Aoo>();
					}
					listAooMin.add(aMin);
				}
			}
			assegnazioneIncaricoDettaglioDto.setListAoo(listAooMin);
		}
		return assegnazioneIncaricoDettaglioDto;
	}
	
	private List<Aoo> getAooRicorsiva(Aoo aoo) {
		List<Aoo> retVal = new ArrayList<>();
		
		
		if(aoo!=null && (aoo.getValidita()==null || aoo.getValidita().getValidoal()==null)) {
			Set<Aoo> figlie = aoo.getSottoAoo();
			if (figlie != null) {
				for (Aoo figlia : figlie) {
					retVal.addAll(getAooRicorsiva(figlia));
				}
			}
			retVal.add(aoo);
			//retVal.add(DomainUtil.minimalAoo(aoo));
		}
		return retVal;
	}
    
    /**
     * GET  /lavoraziones/getincarichi
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/lavoraziones/getincarichi",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity getIncarichi(
    		@RequestParam(value = "idAtto", required=true ) final Long idAtto
    		) throws GestattiCatchedException {
    	AssegnazioneIncaricoResponseDto assegnazioneIncaricoResponseDto = new AssegnazioneIncaricoResponseDto();
    	
    	try{
    		log.debug("REST request getAssegnazioniIncarichiDettaglio idAtto: {}", idAtto);

	    	if(idAtto!=null && idAtto>0L) {
	    		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(idAtto);
	    		assegnazioneIncaricoResponseDto.setListConfigurazioneIncaricoDto(listConfigurazioneIncarico);
	    	}

	    	List<AssegnazioneIncaricoDettaglioDto> listAssegnazioneIncaricoDettaglioDto = new ArrayList<AssegnazioneIncaricoDettaglioDto>();
	    	
	    	assegnazioneIncaricoResponseDto.setListAssegnazioneIncaricoDettaglio(listAssegnazioneIncaricoDettaglioDto);

	    	return new ResponseEntity<AssegnazioneIncaricoResponseDto>(assegnazioneIncaricoResponseDto , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
    * GET  /lavoraziones/scenaridisabilitazione -> possibili azioni
    */
   @RequestMapping(value = "/lavoraziones/scenaridisabilitazione",
           method = RequestMethod.GET,
           produces = MediaType.APPLICATION_JSON_VALUE)
   @Timed
   public ResponseEntity<Iterable<String>> scenariDisabilitazione(
   		@RequestParam(value = "taskBpmId"  ,required=true ) final String taskBpmId,
   		@RequestHeader(value = "profiloId"  ) final Long profiloId
   		) throws GestattiCatchedException{
	   try{
		   log.debug("REST request scenariDisabilitazione taskBpmId: {}, profiloId: {}", taskBpmId, profiloId);
	
	       Iterable<String> page = workflowService.getScenariDisabilitazione(taskBpmId);
	       return new ResponseEntity<Iterable<String>>( page , HttpStatus.OK);
	   }
	   catch(Exception e){
   		throw new GestattiCatchedException(e);
	   }
   }


    /**
     * PUT  /lavoraziones/tasksprendicarico
     */
    @RequestMapping(value = "/lavoraziones/tasksprendicarico",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> prendiCarico(
    		@RequestParam(value = "taskBpmId"  ,required=true ) final String taskBpmId,
    		@RequestParam(value = "attoId"  ,required=true ) final Long attoId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request prendiCarico TipoTask: {}", taskBpmId);
	    	boolean rispostaTask = workflowService.prendiInCaricoTask(taskBpmId, profiloId, false);
	    	
	    	return new ResponseEntity<Boolean>(rispostaTask , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
     * GET  /lavoraziones/tasks/assegnati -> get TipoListaTask.
     */
    @RequestMapping(value = "/lavoraziones/tasksassegnati",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoListaTaskDTO> getTaskAssegnati(
    		@RequestParam(value = "idIstanzaProcesso"  ,required=false ) final String idIstanzaProcesso,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId ) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request getTaskAssegnati idIstanzaProcesso: {}", idIstanzaProcesso);
	    	//TODO INTEGRAZIONE TipoListaTask v = workflowService.getTaskAssegnati( idIstanzaProcesso,profiloId);
	    	/*TODO INTEGRAZIONE*/TipoListaTaskDTO v = null;
	        return new ResponseEntity<TipoListaTaskDTO>(v , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
     * GET  /lavoraziones/tasks/assegnati -> get TipoListaTask.
     */
    @RequestMapping(value = "/lavoraziones/tasksarrivo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoListaTaskDTO> getTaskArrivo(
    		@RequestParam(value = "idIstanzaProcesso"  ,required=false) final  String idIstanzaProcesso,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request getTaskArrivo idIstanzaProcesso: {}", idIstanzaProcesso);
	    	//TODO INTEGRAZIONE TipoListaTask v = workflowService.getTaskArrivo(idIstanzaProcesso,profiloId);
	    	TipoListaTaskDTO v = null;
	        return new ResponseEntity<TipoListaTaskDTO>(v , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    @RequestMapping(value = "/lavoraziones/dettaglioTask",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoTaskDTO> dettaglioTask(
    		@RequestParam(value = "taskBpmId"  ,required=true ) final String taskBpmId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request dettaglioTask TipoTask: {}", taskBpmId);
	    	TipoTaskDTO task = workflowService.getDettaglioTask(taskBpmId);

	    	return new ResponseEntity<TipoTaskDTO>(task , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
