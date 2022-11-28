package it.linksmt.assatti.gestatti.web.rest;

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

import it.linksmt.assatti.datalayer.domain.MateriaDl33;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.MateriaDl33Repository;
import it.linksmt.assatti.service.MateriaDl33Service;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing MateriaDl33.
 */
@RestController
@RequestMapping("/api")
public class MateriaDl33Resource {

    private final Logger log = LoggerFactory.getLogger(MateriaDl33Resource.class);

    @Inject
    private MateriaDl33Repository materiaDl33Repository;
    
    @Inject
    private MateriaDl33Service materiaDl33Service;

    /**
     * POST  /materiaDl33s -> Create a new materiaDl33.
     */
    @RequestMapping(value = "/materiaDl33s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody MateriaDl33 materiaDl33) throws GestattiCatchedException {
    	try{
	        log.debug("REST request to save MateriaDl33 : {}", materiaDl33);
	        if (materiaDl33.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new materiaDl33 cannot already have an ID").build();
	        }
	        materiaDl33Repository.save(materiaDl33);
	        return ResponseEntity.created(new URI("/api/materiaDl33s/" + materiaDl33.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /materiaDl33s -> Updates an existing materiaDl33.
     */
    @RequestMapping(value = "/materiaDl33s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody MateriaDl33 materiaDl33) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update MateriaDl33 : {}", materiaDl33);
	        if (materiaDl33.getId() == null) {
	            return create(materiaDl33);
	        }
	        materiaDl33Repository.save(materiaDl33);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /materiaDl33s -> get all the materiaDl33s.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/materiaDl33s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MateriaDl33>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "denominazione", required = false) String denominazione,
            @RequestParam(value = "attivo", required = false) String attivo,
            @RequestParam(value = "ambitoDl33", required = false) String ambitoDl33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all MateriaDl33s");
	        JsonObject jsonSearch = this.buildSearchObject(denominazione, attivo, ambitoDl33);
	        return materiaDl33Service.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private JsonObject buildSearchObject(String denominazione, String attivo, String ambitoDl33) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("denominazione", denominazione);
	    	json.addProperty("attivo", attivo);
	    	json.addProperty("ambitoDl33", ambitoDl33);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /materiaDl33s/:id -> get the "id" materiaDl33.
     */
    @RequestMapping(value = "/materiaDl33s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<MateriaDl33> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get MateriaDl33 : {}", id);
	        MateriaDl33 materiaDl33 = materiaDl33Repository.findOne(id);
	        if (materiaDl33 == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(materiaDl33, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /materiaDl33s/:id -> delete the "id" materiaDl33.
     */
    @RequestMapping(value = "/materiaDl33s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete MateriaDl33 : {}", id);
	        materiaDl33Repository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
