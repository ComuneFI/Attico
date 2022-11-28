package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.ModelloCampo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.TipoIterRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoIterService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * REST controller for managing TipoIter.
 */
@RestController
@RequestMapping("/api")
public class TipoIterResource {

    private final Logger log = LoggerFactory.getLogger(TipoIterResource.class);

    @Inject
    private TipoIterRepository tipoIterRepository;

    @Inject
    private TipoIterService tipoIterService;

    /**
     * POST  /tipoIters -> Create a new tipoIter.
     */
    @RequestMapping(value = "/tipoIters",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoIter tipoIter) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoIter : {}", tipoIter);
	        if (tipoIter.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoIter cannot already have an ID").build();
	        }
	        tipoIterService.save(tipoIter);
	        return ResponseEntity.created(new URI("/api/tipoIters/" + tipoIter.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoIters -> Updates an existing tipoIter.
     */
    @RequestMapping(value = "/tipoIters",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoIter tipoIter) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoIter : {}", tipoIter);
	        if (tipoIter.getId() == null) {
	            return create(tipoIter);
	        }
	        tipoIterService.save(tipoIter);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    @RequestMapping(value = "/tipoIters/getByCodiceTipoAtto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoIter>> getByCodiceTipoAtto(@RequestParam(value = "codiceTipoAtto" , required = true) String codiceTipoAtto )
    		throws GestattiCatchedException {
    	try{
	    	List<TipoIter> page = tipoIterService.getByCodiceTipoAtto( codiceTipoAtto );
	        return new ResponseEntity<List<TipoIter>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /tipoIters -> get all the tipoIters.
     */
    @RequestMapping(value = "/tipoIters/getByTipoAtto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoIter>> getByTipoAtto(@RequestParam(value = "tipoAttoId" , required = true) Long tipoAttoId )
    		throws GestattiCatchedException {
    	try{
	    	List<TipoIter> page = tipoIterService.getByTipoAtto( tipoAttoId );
	        return new ResponseEntity<List<TipoIter>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }/**
     * GET  /tipoIters -> get all the tipoIters.
     */
    @RequestMapping(value = "/tipoIters",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoIter>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "codice", required = false) String codice,
    		@RequestParam(value = "descrizione", required = false) String descrizione)
            		throws GestattiCatchedException{
    	try{
	    	JsonObject jsonSearch = this.buildSearchObject(codice, descrizione);
	        return tipoIterService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String codice, String descrizione) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("codice", codice);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoIters/:id -> get the "id" tipoIter.
     */
    @RequestMapping(value = "/tipoIters/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoIter> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoIter : {}", id);
	        TipoIter tipoIter = tipoIterService.findOne(id);
	        if (tipoIter == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoIter, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

//    /**
//     * DELETE  /tipoIters/:id -> delete the "id" tipoIter.
//     */
//    @RequestMapping(value = "/tipoIters/{id}",
//            method = RequestMethod.DELETE,
//            produces = MediaType.APPLICATION_JSON_VALUE)
//    @Timed
//    public void delete(@PathVariable Long id) throws CifraCatchedException{
//    	try{
//	    	log.debug("REST request to delete TipoIter : {}", id);
//	        tipoIterRepository.delete(id);
//    	}catch(Exception e){
//    		throw new CifraCatchedException(e);
//    	}
//    }
    
    
    
    /**
	 * DELETE /tipoAttos/:id -> delete the "id" tipoAtto.
	 */
	@RequestMapping(value = "/tipoIters/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	@Timed
	public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException {
		JsonObject j = new JsonObject();
		try {
			
			/*
			 * Elimino Iter
			 */
			log.debug("REST request to delete TipoAtto : {}", id);
			tipoIterRepository.delete(id);
			j.addProperty("success", true);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
		} catch (Exception e) {
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
