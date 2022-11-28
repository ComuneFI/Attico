package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.datatype.DatatypeConfigurationException;

import org.camunda.bpm.engine.task.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Authority;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.TipoDelegaTaskEnum;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.dto.ProfiloDto;
import it.linksmt.assatti.datalayer.domain.util.ProfiloTransformer;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.DelegaService;
import it.linksmt.assatti.service.DelegaTaskService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.TaskRiassegnazioneService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.dto.DelegaDto;
import it.linksmt.assatti.service.dto.DelegaTaskDto;
import it.linksmt.assatti.service.dto.ProfiloCriteriaDTO;
import it.linksmt.assatti.service.dto.ProfiloSearchDTO;
import it.linksmt.assatti.service.dto.TaskRiassegnazioneDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Profilo.
 */
@RestController
@RequestMapping("/api")
public class ProfiloResource {

    private final Logger log = LoggerFactory.getLogger(ProfiloResource.class);

    @Inject
    private ProfiloService profiloService;
    
    @Inject
	private UserService userService;
    
    @Inject
    private AooService aooService;
    
    @Inject
    private WorkflowServiceWrapper workflowServiceWrapper;
    
    @Inject
    private DelegaService delegaService;
    
    @Inject
    private DelegaTaskService delegaTaskService;
    
    @Inject
    private TaskRiassegnazioneService taskRiassegnazioneService;
    
    @Inject
    private BpmWrapperUtil bpmWrapperUtil;
    
    /**
     * PUT /profilos/futureReassignee
     */
    @RequestMapping(value = "/profilos/futureReassignee", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> futureReassignee(
			@RequestBody final TaskRiassegnazioneDto riassegnazione,
			@RequestHeader(value="profiloId" ,required=true) final Long idProfiloEsecutoreRiassegnazione,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
			JsonObject json = new JsonObject();
			try{
				taskRiassegnazioneService.save(riassegnazione, idProfiloEsecutoreRiassegnazione);
				json.addProperty("stato", "ok");
			}
			catch(Exception e){
				e.printStackTrace();
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

    /**
     * GET /profilos/{id}/findTaskRiassignee
     */
    @RequestMapping(value = "/profilos/{id}/findTaskRiassignee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TaskRiassegnazioneDto>> findTaskRiassignee(
			@PathVariable final Long id,
			@RequestParam(value = "page" , required = false) final Integer offset,
    		@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
			try{
				if(id!=null){
					Page<TaskRiassegnazioneDto> page = taskRiassegnazioneService.findTasksRiassignee(id, offset, limit);
					
					HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profilos/", offset, limit);
					return new ResponseEntity<List<TaskRiassegnazioneDto>>(page.getContent(), headers, HttpStatus.OK);
				}else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    /**
     * GET /profilos/{id}/findTaskDaRilasciare
     */
    @RequestMapping(value = "/profilos/{id}/findTaskDaRilasciare", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TaskRiassegnazioneDto>> findTaskDaRilasciare(
			@PathVariable final Long id,
			@RequestParam(value = "page" , required = false) final Integer offset,
    		@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
			try{
				if(id!=null){
					Page<TaskRiassegnazioneDto> page = taskRiassegnazioneService.findTaskDaRilasciare(id, offset, limit);
					
					HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profilos/", offset, limit);
					return new ResponseEntity<List<TaskRiassegnazioneDto>>(page.getContent(), headers, HttpStatus.OK);
				}else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    /**
     * GET /profilos/{id}/findTaskIstruttore
     */
    @RequestMapping(value = "/profilos/{id}/findTaskIstruttore", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<TaskRiassegnazioneDto>> findTaskIstruttore(
			@PathVariable final Long id,
			@RequestParam(value = "page" , required = false) final Integer offset,
    		@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
			try{
				if(id!=null){
					Page<TaskRiassegnazioneDto> page = taskRiassegnazioneService.findTasksIstruttore(id, offset, limit);
					
					HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/profilos/", offset, limit);
					return new ResponseEntity<List<TaskRiassegnazioneDto>>(page.getContent(), headers, HttpStatus.OK);
				}else {
					return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
				}
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    /**
     * PUT /profilos/disable -> Disable a user profile
     */
    @RequestMapping(value = "/profilos/{id}/disable/{idRias}/{idQualifica}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableProfilo(
			@PathVariable final String id,
			@PathVariable final String idRias,
			@PathVariable final String idQualifica,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				if(id!=null && idRias!=null && !"".equals(idRias.trim()) && !"".equals(id.trim())){
					workflowServiceWrapper.riassegnaTaskByProfilo(
							Long.parseLong(id), Long.parseLong(idRias),
							Long.parseLong(idQualifica), profiloId);
					
					profiloService.disableProfilo(Long.parseLong(id));
					json.addProperty("stato", "ok");
				}
				else{
					json.addProperty("stato", "errore");
				}
			}
			catch(Exception e){
				e.printStackTrace();
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    /**
     * PUT /profilos/disabilitazioneDiretta -> Disabilitazione a user profile
     */
    @RequestMapping(value = "/profilos/{id}/disabilitazioneDiretta", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disabilitazioneDiretta(
			@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to disable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				if(id!=null){
					JsonArray res = this.getProfiloLockCriteriaArray(id);
					if(res.size() < 1) {
						profiloService.disableProfilo(id);
						json.addProperty("stato", "ok");
					}else {
						json.addProperty("stato", "errore");
					}
				}else{
					json.addProperty("stato", "errore");
				}
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}

    /**
     * PUT /profilos/enable -> Enable a user profile
     */
    @RequestMapping(value = "/profilos/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableProfilo(
			@PathVariable final String id,
			final HttpServletResponse response)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to enable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				Profilo profilo = profiloService.findOne(Long.parseLong(id));
				boolean errore = false;
				if(profilo!=null && profilo.getAoo()!=null && profilo.getAoo().getId() != null) {
					Aoo aoo = aooService.findOne(profilo.getAoo().getId());
					
					if(aoo!= null && aoo.getValidita()!=null && aoo.getValidita().getValidoal()!=null) {
						json.addProperty("stato", "aooDisabled");
						errore = true;
					}
				}
				if(!errore) {
					profiloService.enableProfilo(Long.parseLong(id));
					json.addProperty("stato", "ok");
				}
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}
    
    /**
     * PUT /profilos/futureEnable -> Enable a user profile
     */
    @RequestMapping(value = "/profilos/{id}/futureEnable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> futureEnable(
			@PathVariable final String id,
			final HttpServletResponse response)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to futureEnable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				profiloService.futureEnableDisable(Long.parseLong(id), true);
				json.addProperty("stato", "ok");
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}
    
    /**
     * PUT /profilos/futureDisable -> Disable a user profile
     */
    @RequestMapping(value = "/profilos/{id}/futureDisable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> futureDisable(
			@PathVariable final String id,
			final HttpServletResponse response)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to futureDisable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				profiloService.futureEnableDisable(Long.parseLong(id), false);
				json.addProperty("stato", "ok");
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}

    /**
     * POST  /profilos -> Create a new profilo.
     */
    @RequestMapping(value = "/profilos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody final Profilo profilo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Profilo : {}", profilo);
	        if (profilo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new profilo cannot already have an ID").build();
	        }
	        if(profilo.getUtente()!=null && profilo.getAoo()!=null && profilo.getGrupporuolo()!=null) {
	        	Profilo profiloEsistente = getProfiloEsistente(null, profilo.getUtente().getId(), profilo.getAoo().getId(), profilo.getGrupporuolo().getId());
	        	if(profiloEsistente!=null) {
	        		String message = profiloEsistente.getValidita()!=null && profiloEsistente.getValidita().getValidoal()!=null ?
	        			"IMPOSSIBILE CREARE IL PROFILO: l'utente selezionato possiede gi\u00E0 un profilo con lo stesso Ufficio e Gruppo Ruolo. Procedere con l'attivazione.":
	        			"IMPOSSIBILE CREARE IL PROFILO: l'utente selezionato possiede gi\u00E0 un profilo con lo stesso Ufficio e Gruppo Ruolo selezionato";
	        		

	        		throw new GestattiCatchedException(message);
	        	}
	        }
	        
	        log.debug("profilo:"+profilo.getGrupporuolo() );
	        profilo.setFutureEnabled(true);
	        profiloService.save(profilo);
	        return ResponseEntity.created(new URI("/api/profilos/" + profilo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
	 * POST /profilos/checkdirigente -> Check if is a diregente profile
	 */
  	@RequestMapping(value = "/profilos/checkdirigente", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
  	public ResponseEntity<String> checkDirigente(@Valid @RequestBody final Profilo profilo) throws GestattiCatchedException{
  		try{
	  		boolean isDirigente = false;
	  		Profilo p = profiloService.findOne(profilo.getId());
	  		Aoo aoo = aooService.findOne(p.getAoo().getId());
	  		if(p.getId().equals(aoo.getProfiloResponsabileId())){
	  			isDirigente = true;
	  		}
	  		JsonObject json = new JsonObject();
			json.addProperty("isDirigente", isDirigente);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

  	}
  	
  	/**
	 * POST /profilos/checkdirigente -> Check if is a diregente profile
	 */
  	@RequestMapping(value = "/profilos/checkImpersonifica", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
  	public ResponseEntity<String> checkImpersonifica(@RequestBody final String username) {
  		try{
	  		User user = userService.findOneByLogin(username);
			List<String> roles = new ArrayList<>();
	        for (Authority authority : user.getAuthorities()) {
	            roles.add(authority.getName());
	        }
	        
	        boolean isAmministratore = 	roles.contains(AuthoritiesConstants.ADMIN) || 
	        							roles.contains(AuthoritiesConstants.AMMINISTRATORE_RP);
	  		
	        JsonObject json = new JsonObject();
			json.addProperty("isAmministratore", isAmministratore);
			
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}

    /**
     * POST  /utentes -> Create a new utente.
     */
    @RequestMapping(value = "/profilos/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Utente>> search(  @RequestBody final ProfiloCriteriaDTO criteria) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to search criteria : {}", criteria);
	
	        List<Utente> l = profiloService.findUsers(criteria );
	        return new ResponseEntity<List<Utente>>(l, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    @RequestMapping(value = "/profilos/getAllActive",
		    method = RequestMethod.GET,
		    produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Profilo>> getAllActive(@RequestParam(value = "profiloAooId", required = false) final String profiloAooId) throws GestattiCatchedException {
	try{
		log.debug("REST request to getAllActive");
	    List<Profilo> l = profiloService.getAllActive(profiloAooId);
	    return new ResponseEntity<List<Profilo>>(l, HttpStatus.OK);
	}catch(Exception e){
		throw new GestattiCatchedException(e);
	}
	
	}
    
    @RequestMapping(value = "/profilos/getAllActiveMinimal",
		    method = RequestMethod.GET,
		    produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Profilo>> getAllActiveMinimal(@RequestParam(value = "profiloAooId", required = false) final String profiloAooId) throws GestattiCatchedException {
	try{
		log.debug("REST request to getAllActive");
	    List<Profilo> l = profiloService.getAllActive(profiloAooId);
	    List<Profilo> minL = new ArrayList<Profilo>();
	    if(l!=null) {
	    	for(Profilo p : l) {
	    		if(p!=null) {
	    			Profilo minP = DomainUtil.minimalProfilo(p);
	    			minP.setGrupporuolo(p.getGrupporuolo());
	    			minP.setHasQualifica(p.getHasQualifica());
	    			minL.add(minP);
	    		}
	    	}
	    }
	    return new ResponseEntity<List<Profilo>>(minL, HttpStatus.OK);
	}catch(Exception e){
		throw new GestattiCatchedException(e);
	}
	
	}
    
    @RequestMapping(value = "/profilos/getAllActiveBasic",
		    method = RequestMethod.GET,
		    produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<ProfiloDto>> getAllActiveBasic(
			@RequestParam(value = "includiDettagli" , required = false) final Boolean includiDettagli, 
			@RequestParam(value = "includiAoo" , required = false) final Boolean includiAoo, 
			@RequestParam(value = "profiloAooId", required = false) final String profiloAooId) throws GestattiCatchedException {
	try{
		log.debug("REST request to getAllActiveBasic");
	    List<Profilo> l = profiloService.getAllActive(profiloAooId);
	    List<ProfiloDto> dtos = ProfiloTransformer.toDto(l, includiAoo, includiDettagli);
	    return new ResponseEntity<List<ProfiloDto>>(dtos, HttpStatus.OK);
	}catch(Exception e){
		throw new GestattiCatchedException(e);
	}
	
	}
    
    /**
     * PUT  /profilos -> Updates an existing profilo.
     */
    @RequestMapping(value = "/profilos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody final Profilo profilo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Profilo : {}", profilo);
	        if (profilo.getId() == null) {
	            return create(profilo);
	        }
	        Profilo dbProf = profiloService.findOne(profilo.getId());
	        if(!profilo.getGrupporuolo().getId().equals(dbProf.getGrupporuolo().getId())) {
	        	//verifica sulla modifica del gruppoRuolo
	        	JsonArray res = this.getProfiloLockCriteriaArray(profilo.getId());
				if(res.size() > 0) {
					throw new GestattiCatchedException("Il gruppo ruolo in questo momento non pu\\u00F2 essere modificato. Per i dettagli utilizzare la funzione 'RIASSEGNAZIONE LAVORAZIONI'.");
				}
	        }
	        if(profilo.getUtente()!=null && profilo.getAoo()!=null && profilo.getGrupporuolo()!=null) {
	        	Profilo profiloEsistente = getProfiloEsistente(profilo.getId(), profilo.getUtente().getId(), profilo.getAoo().getId(), profilo.getGrupporuolo().getId());
	        	if(profiloEsistente!=null) {
	        		String message = profiloEsistente.getValidita()!=null && profiloEsistente.getValidita().getValidoal()!=null ?
	        				"IMPOSSIBILE MODIFICARE IL PROFILO: l'utente selezionato possiede gi\u00E0 un profilo con lo stesso Ufficio e Gruppo Ruolo. Procedere con l'attivazione.":
	        				"IMPOSSIBILE MODIFICARE IL PROFILO: l'utente selezionato possiede gi\u00E0 un profilo con lo stesso Ufficio e Gruppo Ruolo selezionato";
	        		throw new GestattiCatchedException(message);
	        	}
	        }
	        profiloService.save(profilo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    private ProfiloSearchDTO buildProfiloSearchDTO(final String id, final String descrizione, final String delega, final String tipoAtto,  final String utente, final String aoo, final String qualificaProfessionale,
    		final String ruoli, final String stato, final String profiloAooId, final String statoFuture) throws GestattiCatchedException{
    	try{
	    	ProfiloSearchDTO search = new ProfiloSearchDTO();
	    	search.setId(id);
	    	search.setDescrizione(descrizione);
	    	search.setDelega(delega);
	    	search.setTipoAtto(tipoAtto);
	    	search.setUtente(utente);
	    	search.setAoo(aoo);
	    	search.setRuoli(ruoli);
	    	search.setQualificaProfessionale(qualificaProfessionale);
	    	search.setStato(stato);
	    	search.setProfiloAooId(profiloAooId);
	    	search.setStatoFuture(statoFuture);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
     * GET  /profilos -> get all the profilos.
     */
    @RequestMapping(value = "/profilos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getAll(
    		@RequestParam(value = "page" , required = false) final Integer offset,
    		@RequestParam(value = "per_page", required = false) final Integer limit,
    		@RequestParam(value = "idProfilo", required = false) final String idProfilo,
    		@RequestParam(value = "descrizione", required = false) final String descrizione,
    		@RequestParam(value = "delega", required = false) final String delega,
    		@RequestParam(value = "tipoAtto", required = false) final String tipoAtto,
    		@RequestParam(value = "utente", required = false) final String utente,
    		@RequestParam(value = "ruoli", required = false) final String ruoli,
    		@RequestParam(value = "aoo", required = false) final String aoo,
    		@RequestParam(value = "qualificaProfessionale", required = false) final String qualificaProfessionale,
    		@RequestParam(value = "stato", required = false) final String stato,
    		@RequestParam(value = "statoFuture", required = false) final String statoFuture,
    		@RequestParam(value = "profiloAooId", required = false) final String profiloAooId
    		)
    				throws GestattiCatchedException {
    	try{
	    	ProfiloSearchDTO search = this.buildProfiloSearchDTO(idProfilo, descrizione, delega, tipoAtto, utente, aoo, qualificaProfessionale, ruoli, stato, profiloAooId, statoFuture);
	    	return profiloService.search(search, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos/getByRole -> get all the profilos with role.
     */
    @RequestMapping(value = "/profilos/getByRole",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByRole(
    		@RequestParam(value = "ruolo" , required = true) final String role
    		)
    				throws GestattiCatchedException {
    	try{
	    	ProfiloSearchDTO search = this.buildProfiloSearchDTO(null, null, null, null, null, null, null, role, null, null, null);
	    	return profiloService.search(search, 0, Integer.MAX_VALUE);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    @RequestMapping(value = "/profilos/getByCandidateGroup",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByCandidateGroup(
    		@RequestParam(value = "candidateGroup" , required = true) final String candidateGroup
    
    		)
    				throws GestattiCatchedException{
    	try{
    		String ruoli = null;
    		List<Aoo> aoos = null;
    		List<Long> listIdRuolo = new ArrayList<Long>();
			List<Long> listIdAoo = new ArrayList<Long>();
    		if(candidateGroup!=null && !candidateGroup.isEmpty()) {
    			aoos = bpmWrapperUtil.getAooByCandidate(candidateGroup);
    			
    			List<Ruolo> ruolis = bpmWrapperUtil.getRuoliByCandidate(candidateGroup);
    			if(ruolis!=null) {
    				for (Ruolo ruolo : ruolis) {
						if(ruoli==null) {
							ruoli=ruolo.getCodice();
						}else {
							ruoli+= (","+ruolo.getCodice());
						}
						
						listIdRuolo.add(ruolo.getId());
					}
    			}
    		}
    		
    		if(aoos!=null && aoos.size()>0) {
    			List<Profilo> p = new ArrayList<Profilo>();
    			
    			for (Aoo aoo : aoos) {
    				listIdAoo.add(aoo.getId());
    			}
    			
				p.addAll(profiloService.findEnabledByRuoloAoo(listIdRuolo, listIdAoo));
    			
    			return new ResponseEntity<List<Profilo>>(p, HttpStatus.OK);
    		}else {
    			ProfiloSearchDTO search = this.buildProfiloSearchDTO(null, null, null, null, null, null, null, ruoli, null, null, null);
    	    	return profiloService.search(search, 0, Integer.MAX_VALUE);
    		}
    		
	    	
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    

    /**
	 * GET /profilos/alreadyexist/:aooid/:utenteid -> check if exists a profile with specify aooid and utenteid
	 */

	@RequestMapping(value = "/profilos/alreadyexist/{aooid}/{utenteid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkAlreadyExists(
			@PathVariable final String aooid,
			@PathVariable final String utenteid,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to check if profile already exists : {}", aooid, utenteid);
			boolean exists = profiloService.checkIfAlreadyExistsAProfile(Long.parseLong(aooid), Long.parseLong(utenteid));
			JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}
	
	 /**
		 * GET /profilos/existsAsDelegato -> check if exists a profile with delega temporale attiva
		 */

		@RequestMapping(value = "/profilos/{id}/existsAsDelegato", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
		public ResponseEntity<String> profiloExistsAsDelegato(
				@PathVariable final Long id,
				final HttpServletResponse response) throws GestattiCatchedException {
			try{
				boolean exists = delegaService.profiloExistsAsDelegato(id);
				if(!exists) {
					exists = delegaTaskService.profiloExistsAsDelegatoByTipoDelega(id, TipoDelegaTaskEnum.FULL_ITER);
				}
				JsonObject json = new JsonObject();
				json.addProperty("exists", exists);
				return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
			}catch(Exception e){
	    		throw new GestattiCatchedException(e);
	    	}

		}
	
	/**
	 * GET /profilos/getProfiliOfUser/:utenteid -> check if exists a profile with specify aooid and utenteid
	 */

	@RequestMapping(value = "/profilos/getProfiliOfUser/{utenteid}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<List<Profilo>> getProfiliOfUser(
			@PathVariable final String utenteid,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to getProfiliOfUser : {}", utenteid);
			List<Profilo> l = profiloService.getProfiliOfUser(Long.parseLong(utenteid));
	        return new ResponseEntity<List<Profilo>>(l, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}
	
	/**
	 * GET /profilos/candisablewithoutreassegnee/:profiloid -> check if exists active task for this profile and allow or not to disable it without reassegnee tasks to other profilo.
	 */

	@RequestMapping(value = "/profilos/{profiloid}/candisablewithoutreassegnee", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> canDisableWithoutReassegnee(
			@PathVariable final Long profiloid,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("check if exists active task for this profile with id {} and allow or not to disable it without reassegnee tasks to other profilo", profiloid);
			JsonArray res = this.getProfiloLockCriteriaArray(profiloid);
//			Profilo profilo = profiloService.findOneBase(profiloid);
//			long taskInCarico = workflowServiceWrapper.countTaskInCarico(profilo);
//			if(taskInCarico > 0L) {
//				res.add(new JsonPrimitive("TASK_IN_CARICO"));
//			}
//			if(profiloService.profiloHasConfIncaricoOnAttiAttivi(profiloid)) {
//				res.add(new JsonPrimitive("VISTI_PARERI_FIRME"));
//			}
//			if(Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.DISABILITAZIONE_PROFILI_CHECK_ATTOWORKER, "true")) && attoWorkerService.hasAttoWorkNotEnd(profiloid)){
//				res.add(new JsonPrimitive("ATTI_LAVORATI"));
//			}
			
			JsonObject json = new JsonObject();
			json.add("lockTypes", res);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}

	/**
	 * GET /profilos/findByRuoloAoo -> get all the profilos with role and aoo.
	 */
	@RequestMapping(value = "/profilos/findByRuoloAoo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Profilo>> findByRuoloAoo(
			@RequestParam(value = "listIdRuolo", required = false) final List<Long> listIdRuolo,
			@RequestParam(value = "listIdAoo", required = false) final List<Long> listIdAoo)
			throws GestattiCatchedException {
		try {
			return new ResponseEntity<List<Profilo>>(profiloService.findByRuoloAoo(listIdRuolo, listIdAoo), HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}


    /**
     * GET  /profilos -> getByAooId the profilos.
     */
    @RequestMapping(value = "/profilos/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByAooId( @RequestParam(value = "aooId" , required = true) final Long aooId)
    		throws GestattiCatchedException {
    	try{
	        List<Profilo> page = profiloService.findByAooId(aooId);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
 
    }
    
    /**
     * GET  /profilos -> getByAooIdRicorsive the profilos.
     */
    @RequestMapping(value = "/profilos/getByAooIdRicorsive",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByAooIdRicorsive( @RequestParam(value = "aooId" , required = true) final Long aooId)
    		throws GestattiCatchedException {
    	try{
	        List<Profilo> page = profiloService.getByAooIdRicorsive(aooId);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
 
    }
    
    /**
     * GET  /profilos/getEmananti -> getEmananti
     */
    @RequestMapping(value = "/profilos/getEmananti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getEmananti( @RequestParam(value = "aooId" , required = true) final Long aooId,
    		@RequestParam(value = "tipoAttoCodice" , required = true) final String tipoAttoCodice)
    		throws GestattiCatchedException {
    	try{
	        List<Profilo> page = profiloService.getEmananti(aooId, tipoAttoCodice);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
 
    }

    /**
     * GET  /profilos -> getByAooId the profilos.
     */
    @RequestMapping(value = "/profilos/getByAooIdAndTipoAtto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByAooIdAndTipoAtto( @RequestParam(value = "aooId" , required = true) final Long aooId,@RequestParam(value = "codiceTipoAtto" , required = true) final String codiceTipoAtto)
    		throws GestattiCatchedException {
    	try{
	        List<Profilo> page = profiloService.findByAooIdAndTipoAtto(aooId, codiceTipoAtto, false);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /profilos -> getByAooId the profilos.
     */
    @RequestMapping(value = "/profilos/getByAooIdAndTipoAttoForGiunta",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByAooIdAndTipoAttoForGiunta( @RequestParam(value = "aooId" , required = true) final Long aooId,@RequestParam(value = "codiceTipoAtto" , required = true) final String codiceTipoAtto)
    		throws GestattiCatchedException {
    	try{
	        List<Profilo> page = profiloService.findByAooIdAndTipoAttoForGiunta(aooId, codiceTipoAtto, false, null);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    
    /**
     * GET  /profilos -> getByAooId the profilos.
     */
    @RequestMapping(value = "/profilos/getByAooIdAndTipoAttoForPareri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getByAooIdAndTipoAttoForPareri( 
    		@RequestParam(value = "aooId" , required = true) final Long aooId,
    		@RequestParam(value = "codiceTipoAtto" , required = true) final String codiceTipoAtto,
    		@RequestParam(value = "tipoParere" , required = true) final String tipoParere
    		)
    				throws GestattiCatchedException {
    	try{
	    	List<Profilo> page = profiloService.findByAooIdAndTipoAtto(aooId, codiceTipoAtto, true);
	    	// , tipoParere
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos -> getByAooId the profilos.
     */
    @RequestMapping(value = "/profilos/getForControdeduzioni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getForControdeduzioni( 
    		@RequestParam(value = "aoosId" , required = true) final Long[] aoosId
    		)
    				throws GestattiCatchedException {
    	try{
    		
	    	List<Profilo> page = profiloService.findForControdeduzioni(aoosId);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos -> getProfilosRiassegnazione the profilos.
     */
    @RequestMapping(value = "/profilos/getProfilosRiassegnazione",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getProfilosRiassegnazione( 
    		@RequestParam(value = "profiloDisabilitazioneId" , required = true) final Long profiloDisabilitazioneId
    		)
    				throws GestattiCatchedException{
    	try{
	    	List<Profilo> page = profiloService.getProfilosRiassegnazione(profiloDisabilitazioneId);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos -> getProfilosRiassegnazioneTask the profilos.
     */
    @RequestMapping(value = "/profilos/getProfilosRiassegnazioneTask",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getProfilosRiassegnazioneTask( 
    		@RequestParam(value = "profiloRiassegnazioneId" , required = true) final Long profiloRiassegnazioneId,
    		@RequestParam(value = "tipoAttoCodice" , required = true) final String tipoAttoCodice,
    		@RequestParam(value = "profiloAooId" , required = false) final Long profiloAooId,
    		@RequestParam(value = "includiQualifiche" , required = false) final Boolean includiQualifiche,
    		@RequestParam(value = "filterByAooProfiloTask" , required = false) final Boolean filterByAooProfiloTask
    		)
    				throws GestattiCatchedException{
    	try{
	    	List<Profilo> page = profiloService.getProfilosRiassegnazioneTask(profiloRiassegnazioneId, tipoAttoCodice, profiloAooId, includiQualifiche, filterByAooProfiloTask);
	        return new ResponseEntity<List<Profilo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos -> getProfilosRiassegnazioneTaskConfTaskBased the profilos.
     */
    @RequestMapping(value = "/profilos/getProfilosRiassegnazioneTaskConfTaskBased",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getProfilosRiassegnazioneTaskConfTaskBased( 
    		@RequestParam(value = "taskId" , required = true) final String taskId
    		)
    				throws GestattiCatchedException{
    	try{
    		List<Profilo> profiliRiassegnazione = null;
    		Task task = bpmWrapperUtil.getTask(taskId);
    		Long attoId = bpmWrapperUtil.getAttoId(task);
    		Long idProfilo = bpmWrapperUtil.getIdProfiloByUsernameBpm(task.getAssignee());
    		Long confIncaricoId = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(task.getAssignee());
    		TaskRiassegnazioneDto riassegnazione = null;
    		if(confIncaricoId!=null) {
    			riassegnazione = taskRiassegnazioneService.findTaskRiassignee(idProfilo, confIncaricoId);
    		}
    		if(riassegnazione==null) {
    			riassegnazione = taskRiassegnazioneService.findTaskIstruttore(idProfilo, attoId);
    		}
    		if(riassegnazione!=null && riassegnazione.getProfili()!=null) {
    			profiliRiassegnazione = riassegnazione.getProfili();
    		}else {
    			profiliRiassegnazione = new ArrayList<Profilo>();
    		}
	        return new ResponseEntity<List<Profilo>>(profiliRiassegnazione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
     * GET  /profilos -> get all the profilos.
     */
    @RequestMapping(value = "/profilos/searchquery",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Profilo>> getAll(@RequestParam(value = "q" , required = false) final String q )
    		throws GestattiCatchedException{
    	try{
	    	Iterable<Profilo> page =  profiloService.findProfilo( q );
	
	        return new ResponseEntity<Iterable<Profilo>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
     * GET  /profilos/:id -> get the "id" profilo.
     */
    @RequestMapping(value = "/profilos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Profilo> get(@PathVariable final Long id, final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Profilo : {}", id);
	        Profilo profilo = profiloService.findOne(id);
	        if (profilo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        return new ResponseEntity<>(profilo, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    class ProfiloToUpdDto {
        private Profilo profilo;
        private List<Map<String, String>> locks;
        
		public Profilo getProfilo() {
			return profilo;
		}
		public void setProfilo(Profilo profilo) {
			this.profilo = profilo;
		}
		public List<Map<String, String>> getLocks() {
			return locks;
		}
		public void setLocks(List<Map<String, String>> locks) {
			this.locks = locks;
		}
    }
    
    /**
     * Uguale al /profilos/:id con l'aggiunta di eventuali blocchi sulla modifica
     * 
     * GET  /profilos/:id/getToUpt -> get the "id" profilo.
     */
    @RequestMapping(value = "/profilos/{id}/getToUpt",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProfiloToUpdDto> getToUpt(@PathVariable final Long id, final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Profilo : {}", id);
	        Profilo profilo = profiloService.findOne(id);
	        if (profilo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        ProfiloToUpdDto ret = new ProfiloToUpdDto();
	        ret.setProfilo(profilo);
	        List<Map<String, String>> locks = new ArrayList<Map<String, String>>();
	  		
	        String warning = null;
	        JsonArray res = this.getProfiloLockCriteriaArray(id);
			if(res.size() > 0) {
				warning = "Il gruppo ruolo in questo momento non pu\u00F2 essere modificato. Per i dettagli utilizzare la funzione 'RIASSEGNAZIONE LAVORAZIONI'.";
				Map<String, String> lockGR = new HashMap<String, String>();
				lockGR.put("GRUPPO_RUOLO", warning);
				locks.add(lockGR);
			}
			
			ret.setLocks(locks);
	        return new ResponseEntity<ProfiloToUpdDto>(ret, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    /**
     * GET  /profilos/:id/getSimple -> get the "id" profilo.
     */
    @RequestMapping(value = "/profilos/{id}/getSimple",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Profilo> getSimple(@PathVariable final Long id, final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Profilo : {}", id);
	        Profilo profilo = profiloService.findOneEssential(id);
	        if (profilo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        return new ResponseEntity<>(profilo, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
     * DELETE  /profilos/:id -> delete the "id" profilo.
     */
    @RequestMapping(value = "/profilos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable final Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Profilo : {}", id);
	        profiloService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
    
    private Profilo getProfiloEsistente(Long idProfilo, Long idUtente, Long idAoo, Long idGruppoRuolo) {
    	if(idUtente!=null && idAoo!=null && idGruppoRuolo!=null) {
    		
    		List<Profilo> profiliOfUser = profiloService.getProfiliOfUser(idUtente);
    		
    		if(profiliOfUser!=null) {
    			for (Profilo profiloOfUser : profiliOfUser) {
					if(profiloOfUser.getGrupporuolo()!=null && profiloOfUser.getAoo()!=null && profiloOfUser.getGrupporuolo().getId().longValue() == idGruppoRuolo.longValue() && profiloOfUser.getAoo().getId().longValue() == idAoo.longValue()) {
						if(idProfilo==null || (profiloOfUser.getId().longValue() != idProfilo.longValue())) {
							return profiloOfUser;
						}
					}
    			}
    		}
    		
    	}
    	return null;
    }
    
    private JsonArray getProfiloLockCriteriaArray(Long profiloid) throws ServiceException, DatatypeConfigurationException {
		JsonArray res = new JsonArray();
		Page<TaskRiassegnazioneDto> p = taskRiassegnazioneService.findTasksIstruttore(profiloid, 1, 1);
		if(p.getTotalElements() > 0L) {
			res.add(new JsonPrimitive("TASK_ISTRUTTORE"));
		}
		p = taskRiassegnazioneService.findTasksRiassignee(profiloid, 1, 1);
		if(p.getTotalElements() > 0L) {
			res.add(new JsonPrimitive("TASK_RIASSIGNEE"));
		}
		p = taskRiassegnazioneService.findTaskDaRilasciare(profiloid, 1, 1);
		if(p.getTotalElements() > 0L) {
			res.add(new JsonPrimitive("TASK_RILASCIARE"));
		}
		Page<DelegaTaskDto> pds = delegaTaskService.search(new JsonParser().parse("{itinereOnly:true, delegatoProfId: "+profiloid+"}").getAsJsonObject(), 0L, 1, 1, null, null);
		if(pds.getTotalElements() > 0L) {
			res.add(new JsonPrimitive("DELEGA_TASK"));
		}
		Page<DelegaDto> pdt = delegaService.search(new JsonParser().parse("{inCorsoOFutura:true, delegatoProfId:"+profiloid+"}").getAsJsonObject(), 0L, 1, 1, null, null);
		if(pdt.getTotalElements() > 0L) {
			res.add(new JsonPrimitive("DELEGA_TEMPORALE"));
		}
		return res;
	}
}
