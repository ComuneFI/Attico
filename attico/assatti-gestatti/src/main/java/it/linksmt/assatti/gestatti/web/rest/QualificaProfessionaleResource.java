package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.service.QualificaprofessionaleService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing QualificaProfessionale.
 */
@RestController
@RequestMapping("/api")
public class QualificaProfessionaleResource {

    private final Logger log = LoggerFactory.getLogger(QualificaProfessionaleResource.class);

    @Inject
    private QualificaProfessionaleRepository qualificaProfessionaleRepository;

    @Inject
    private QualificaprofessionaleService qualificaprofessionaleService;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * PUT /qualificaProfessionales/disable -> Disable a qualificaProfessionale
     */
    @RequestMapping(value = "/qualificaProfessionales/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableQualificaProfessionales(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable qualificaProfessionale : {}", id);
			JsonObject json = new JsonObject();
			try{
				qualificaprofessionaleService.disableQualificaProfessionale(Long.parseLong(id));
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
     * PUT /qualificaProfessionales/enable -> Enable a qualificaProfessionale
     */
    @RequestMapping(value = "/qualificaProfessionales/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableProfilo(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to enable qualificaProfessionale : {}", id);
			JsonObject json = new JsonObject();
			try{
				qualificaprofessionaleService.enableQualificaProfessionale(Long.parseLong(id));
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
     * POST  /qualificaProfessionales -> Create a new qualificaProfessionale.
     */
    @RequestMapping(value = "/qualificaProfessionales",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody QualificaProfessionale qualificaProfessionale) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save QualificaProfessionale : {}", qualificaProfessionale);
	        if (qualificaProfessionale.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new qualificaProfessionale cannot already have an ID").build();
	        }
	        qualificaProfessionaleRepository.save(qualificaProfessionale);
	        return ResponseEntity.created(new URI("/api/qualificaProfessionales/" + qualificaProfessionale.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /qualificaProfessionales -> Updates an existing qualificaProfessionale.
     */
    @RequestMapping(value = "/qualificaProfessionales",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody QualificaProfessionale qualificaProfessionale) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update QualificaProfessionale : {}", qualificaProfessionale);
	        if (qualificaProfessionale.getId() == null) {
	            return create(qualificaProfessionale);
	        }
	        qualificaProfessionaleRepository.save(qualificaProfessionale);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /qualificaProfessionales -> get all the qualificaProfessionales.
     */
    @RequestMapping(value = "/qualificaProfessionales",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualificaProfessionale>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "denominazione", required = false) String denominazione,
            @RequestParam(value = "stato", required = false) final String stato)
            		throws GestattiCatchedException {
    	try{
	    	JsonObject jsonSearch = this.buildSearchObject(denominazione, stato);
	        return qualificaprofessionaleService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String denominazione, String stato) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("denominazione", denominazione);
	    	json.addProperty("stato", stato);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /qualificaProfessionales -> get all the qualificaProfessionales.
     */
    @RequestMapping(value = "/qualificaProfessionales/getOnlyEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualificaProfessionale>> getAllEnabled(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
    		//Sort sort = new Sort(new Order(Direction.DESC, "denominazione" ));
	    	Page<QualificaProfessionale> page = qualificaprofessionaleService.findAllEnabled(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/qualificaProfessionales", offset, limit);
	        return new ResponseEntity<List<QualificaProfessionale>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoMaterias -> getByProfiloId the qualificaProfessionales.
     */
    
    @RequestMapping(value = "/qualificaProfessionales/getByProfiloId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualificaProfessionale>> getByProfiloId( @RequestParam(value = "profiloId" , required = true) Long profiloId)
        throws GestattiCatchedException {
    	
        List<QualificaProfessionale> page = qualificaprofessionaleService.findByProfiloId(profiloId);
        return new ResponseEntity<List<QualificaProfessionale>>(page, HttpStatus.OK);
    }
    
    /**
     * GET  /tipoMaterias -> getEnabledByProfiloId the qualificaProfessionales.
     */
    
    @RequestMapping(value = "/qualificaProfessionales/getEnabledByProfiloId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualificaProfessionale>> getEnabledByProfiloId( @RequestParam(value = "profiloId" , required = true) Long profiloId)
        throws GestattiCatchedException {
    	
        List<QualificaProfessionale> page = qualificaprofessionaleService.findEnabledByProfiloId(profiloId);
        return new ResponseEntity<List<QualificaProfessionale>>(page, HttpStatus.OK);
    }
    
    
    /**
     * GET  /tipoMaterias -> getByAooId the qualificaProfessionales.
     */
    /*
     * INNOVCIFRA-187
    @RequestMapping(value = "/qualificaProfessionales/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<QualificaProfessionale>> getByAooId( @RequestParam(value = "aooId" , required = true) Long aooId)
        throws URISyntaxException {
    	
        List<QualificaProfessionale> page = qualificaprofessionaleService.findByAooId(aooId);
        return new ResponseEntity<List<QualificaProfessionale>>(page, HttpStatus.OK);
    }
    */
    
    /**
     * GET  /qualificaProfessionales/:id -> get the "id" qualificaProfessionale.
     */
    @RequestMapping(value = "/qualificaProfessionales/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<QualificaProfessionale> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get QualificaProfessionale : {}", id);
	        QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (qualificaProfessionale == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByQualificaEmananteId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	qualificaProfessionale.setAtti(atti);
	        }
	        
	        return new ResponseEntity<>(qualificaProfessionale, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /qualificaProfessionales/:id -> delete the "id" qualificaProfessionale.
     */
    @RequestMapping(value = "/qualificaProfessionales/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete QualificaProfessionale : {}", id);
	        qualificaProfessionaleRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
