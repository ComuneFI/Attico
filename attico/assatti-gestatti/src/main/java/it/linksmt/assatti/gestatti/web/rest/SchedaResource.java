package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.SchedaRepository;
import it.linksmt.assatti.service.SchedaService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Scheda.
 */
@RestController
@RequestMapping("/api")
public class SchedaResource {

    private final Logger log = LoggerFactory.getLogger(SchedaResource.class);

    @Inject
    private SchedaRepository schedaRepository;

    @Inject
    private SchedaService schedaService;
    
    /**
     * POST  /schedas -> Create a new scheda.
     */
    @RequestMapping(value = "/schedas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Scheda scheda) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Scheda : {}", scheda);
	        if (scheda.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new scheda cannot already have an ID").build();
	        }
	        schedaRepository.save(scheda);
	        return ResponseEntity.created(new URI("/api/schedas/" + scheda.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /schedas -> Updates an existing scheda.
     */
    @RequestMapping(value = "/schedas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Scheda scheda) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update Scheda : {}", scheda);
	        if (scheda.getId() == null) {
	            return create(scheda);
	        }
	        schedaRepository.save(scheda);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /schedas -> get all the schedas.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/schedas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Scheda>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "etichetta", required = false) String etichetta,
            @RequestParam(value = "ripetitiva", required = false) String ripetitiva,
            @RequestParam(value = "ordine", required = false) String ordine) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all Schedas");
	        JsonObject jsonSearch = this.buildSearchObject(etichetta, ripetitiva, ordine);
	        return schedaService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private JsonObject buildSearchObject(String etichetta, String ripetitiva, String ordine) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("etichetta", etichetta);
	    	json.addProperty("ripetitiva", ripetitiva);
	    	json.addProperty("ordine", ordine);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /schedas/:id -> get the "id" scheda.
     */
    @RequestMapping(value = "/schedas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Scheda> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Scheda : {}", id);
	        Scheda scheda = schedaRepository.findOne(id);
	        if (scheda == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(scheda, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /macro_cat_obbligo_dl33s -> get all the macro_cat_obbligo_dl33s.
     */
    @RequestMapping(value = "/schedas/{id}/SchedaDato",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public Set<SchedaDato> getSchedaDato(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all Macro_cat_obbligo_dl33s");
	        return schedaService.getSchedaDato( id );
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * DELETE  /schedas/:id -> delete the "id" scheda.
     */
    @RequestMapping(value = "/schedas/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Scheda : {}", id);
	        Map<String, String> mappa = schedaService.delete(id);
	        JsonObject json = new JsonObject();
	        for(String key : mappa.keySet()){
	        	json.addProperty(key, mappa.get(key));
	        }		
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * DELETE  /schedas/:id -> delete the "id" scheda.
     */
    @RequestMapping(value = "/schedas/{id}/deleteForce",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deleteForce(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Scheda and the references in Obbligo DL33 : {}", id);
	        schedaService.deleteForce(id);
			return new ResponseEntity<Void>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
