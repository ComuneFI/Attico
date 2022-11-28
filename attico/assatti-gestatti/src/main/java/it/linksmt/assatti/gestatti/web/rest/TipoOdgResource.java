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

import it.linksmt.assatti.datalayer.domain.TipoOdg;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.TipoOdgRepository;
import it.linksmt.assatti.service.TipoOdgService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing TipoOdg.
 */
@RestController
@RequestMapping("/api")
public class TipoOdgResource {

    private final Logger log = LoggerFactory.getLogger(TipoOdgResource.class);

    @Inject
    private TipoOdgRepository tipoOdgRepository;
    
    @Inject
    private TipoOdgService tipoOdgService;

    /**
     * POST  /tipoOdgs -> Create a new tipoOdg.
     */
    @RequestMapping(value = "/tipoOdgs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody TipoOdg tipoOdg) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoOdg : {}", tipoOdg);
	        if (tipoOdg.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoOdg cannot already have an ID").build();
	        }
	        tipoOdgRepository.save(tipoOdg);
	        return ResponseEntity.created(new URI("/api/tipoOdgs/" + tipoOdg.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoOdgs -> Updates an existing tipoOdg.
     */
    @RequestMapping(value = "/tipoOdgs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody TipoOdg tipoOdg) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoOdg : {}", tipoOdg);
	        if (tipoOdg.getId() == null) {
	            return create(tipoOdg);
	        }
	        tipoOdgRepository.save(tipoOdg);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoOdgs -> get all the tipoOdgs.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/tipoOdgs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoOdg>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all TipoOdgs");
	        JsonObject jsonSearch = this.buildSearchObject(descrizione);
	        return tipoOdgService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String descrizione) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("descrizione", descrizione);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoOdgs/:id -> get the "id" tipoOdg.
     */
    @RequestMapping(value = "/tipoOdgs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoOdg> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get TipoOdg : {}", id);
	        TipoOdg tipoOdg = tipoOdgRepository.findOne(id);
	        if (tipoOdg == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoOdg, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoOdgs/:id -> delete the "id" tipoOdg.
     */
    @RequestMapping(value = "/tipoOdgs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete TipoOdg : {}", id);
	        tipoOdgRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
