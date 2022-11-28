package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.TipoProgressivo;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.TipoProgressivoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoProgressivoService;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * REST controller for managing TipoProgressivo.
 */
@RestController
@RequestMapping("/api")
public class TipoProgressivoResource {

    private final Logger log = LoggerFactory.getLogger(TipoProgressivoResource.class);

    @Inject
    private TipoProgressivoRepository tipoProgressivoRepository;

    @Inject
    private TipoProgressivoService tipoProgressivoService;
    
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
     * POST  /tipoProgressivos -> Create a new tipoProgressivo.
     */
    @RequestMapping(value = "/tipoProgressivos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoProgressivo tipoProgressivo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoProgressivo : {}", tipoProgressivo);
	        if (tipoProgressivo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoProgressivo cannot already have an ID").build();
	        }
	        tipoProgressivoRepository.save(tipoProgressivo);
	        return ResponseEntity.created(new URI("/api/tipoProgressivos/" + tipoProgressivo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoProgressivos -> Updates an existing tipoProgressivo.
     */
    @RequestMapping(value = "/tipoProgressivos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoProgressivo tipoProgressivo) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update TipoProgressivo : {}", tipoProgressivo);
	        if (tipoProgressivo.getId() == null) {
	            return create(tipoProgressivo);
	        }
	        tipoProgressivoRepository.save(tipoProgressivo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoProgressivos -> get all the tipoProgressivos.
     */
    @RequestMapping(value = "/tipoProgressivos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoProgressivo>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione)
            		throws GestattiCatchedException {
    	try{
	    	JsonObject jsonSearch = this.buildSearchObject(descrizione);
	        return tipoProgressivoService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoProgressivos/:id -> get the "id" tipoProgressivo.
     */
    @RequestMapping(value = "/tipoProgressivos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoProgressivo> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoProgressivo : {}", id);
	    	TipoProgressivo tipoProgressivo = tipoProgressivoRepository.findOne(id);
	        
	        if (tipoProgressivo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        return new ResponseEntity<>(tipoProgressivo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * DELETE  /tipoProgressivos/:id -> delete the "id" tipoProgressivo.
     */
    @RequestMapping(value = "/tipoProgressivos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException {
    	JsonObject j = new JsonObject();
    	try{
	    	log.debug("REST request to delete TipoProgressivo : {}", id);
	        tipoProgressivoRepository.delete(id);
	        j.addProperty("success", true);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		j.addProperty("success", false);
			String errore = "Errore generico";
			if (e.getCause() != null && e.getCause() instanceof Exception) {
				Exception ex = (Exception) e.getCause();
				for (int i = 0; i < 3; i++) {
					if (ex instanceof MySQLIntegrityConstraintViolationException) {
						errore = ex.getMessage();
						break;
					} else if (ex.getCause() != null && ex.getCause() instanceof Exception) {
						ex = (Exception) ex.getCause();
					} else {
						break;
					}
				}
			}
			j.addProperty("message", errore);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
    	}
    }
}
