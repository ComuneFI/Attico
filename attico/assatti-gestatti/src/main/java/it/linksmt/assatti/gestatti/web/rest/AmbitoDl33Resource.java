package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.AmbitoDl33;
import it.linksmt.assatti.datalayer.repository.AmbitoDl33Repository;
import it.linksmt.assatti.service.AmbitoDl33Service;
import it.linksmt.assatti.service.AnagraficaTrasperanzaService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
 * REST controller for managing AmbitoDl33.
 */
@RestController
@RequestMapping("/api")
public class AmbitoDl33Resource {

    private final Logger log = LoggerFactory.getLogger(AmbitoDl33Resource.class);

    @Inject
    private AmbitoDl33Repository ambitoDl33Repository;
    
    @Inject
    private AmbitoDl33Service ambitoDl33Service;

    @Inject
    private AnagraficaTrasperanzaService anagraficaTrasparenzaService;
   
    

    /**
     * GET  /ambitoDl33s/attivo -> get all attivo the ambitoDl33s.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/ambitoDl33s/attivo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<AmbitoDl33> getAllAttivo() throws GestattiCatchedException {
    	try{
	        log.debug("REST request to get all AmbitoDl33s");
	        return anagraficaTrasparenzaService.getAllAmbitoAttivo();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    
    /**
     * POST  /ambitoDl33s -> Create a new ambitoDl33.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/ambitoDl33s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody AmbitoDl33 ambitoDl33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save AmbitoDl33 : {}", ambitoDl33);
	        if (ambitoDl33.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new ambitoDl33 cannot already have an ID").build();
	        }
	        ambitoDl33Repository.save(ambitoDl33);
	        return ResponseEntity.created(new URI("/api/ambitoDl33s/" + ambitoDl33.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /ambitoDl33s -> Updates an existing ambitoDl33.
     */
    @RequestMapping(value = "/ambitoDl33s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody AmbitoDl33 ambitoDl33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update AmbitoDl33 : {}", ambitoDl33);
	        if (ambitoDl33.getId() == null) {
	            return create(ambitoDl33);
	        }
	        ambitoDl33Repository.save(ambitoDl33);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String denominazione, String attivo) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("denominazione", denominazione);
	    	json.addProperty("attivo", attivo);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ambitoDl33s -> get all the ambitoDl33s.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/ambitoDl33s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AmbitoDl33>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "denominazione", required = false) String denominazione,
            @RequestParam(value = "attivo", required = false) String attivo) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all AmbitoDl33s");
	        JsonObject jsonSearch = this.buildSearchObject(denominazione, attivo);
	        return ambitoDl33Service.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ambitoDl33s/:id -> get the "id" ambitoDl33.
     */
    @RequestMapping(value = "/ambitoDl33s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<AmbitoDl33> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get AmbitoDl33 : {}", id);
	        AmbitoDl33 ambitoDl33 = ambitoDl33Repository.findOne(id);
	        if (ambitoDl33 == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(ambitoDl33, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /ambitoDl33s/:id -> delete the "id" ambitoDl33.
     */
    @RequestMapping(value = "/ambitoDl33s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete AmbitoDl33 : {}", id);
	        ambitoDl33Repository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
