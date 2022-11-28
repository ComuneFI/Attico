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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoDocumentoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

/**
 * REST controller for managing TipoDocumento.
 */
@RestController
@RequestMapping("/api")
public class TipoDocumentoResource {

    private final Logger log = LoggerFactory.getLogger(TipoDocumentoResource.class);

    @Inject
    private TipoDocumentoRepository tipoDocumentoRepository;
    
    @Inject
    private TipoDocumentoService tipoDocumentoService;

    /**
     * POST  /tipoDocumentos -> Create a new tipoDocumento.
     */
    @RequestMapping(value = "/tipoDocumentos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody TipoDocumento tipoDocumento) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoDocumento : {}", tipoDocumento);
	        if (tipoDocumento.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoDocumento cannot already have an ID").build();
	        }
	        tipoDocumentoRepository.save(tipoDocumento);
	        return ResponseEntity.created(new URI("/api/tipoDocumentos/" + tipoDocumento.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoDocumentos -> Updates an existing tipoDocumento.
     */
    @RequestMapping(value = "/tipoDocumentos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody TipoDocumento tipoDocumento) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoDocumento : {}", tipoDocumento);
	        if (tipoDocumento.getId() == null) {
	            return create(tipoDocumento);
	        }
	        tipoDocumentoRepository.save(tipoDocumento);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoDocumentos -> get all the tipoDocumentos.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/tipoDocumentos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoDocumento>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "riversamentoTipoatto", required = false) Boolean riversamentoTipoatto
            ) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all TipoDocumentos");
	        JsonObject jsonSearch = this.buildSearchObject(descrizione, codice, riversamentoTipoatto);
	        return tipoDocumentoService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    /**
     * GET  /tipoDocumentos/countByCodice -> get count of tipoDocumentos by codice.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/tipoDocumentos/checkIfAlreadyExists/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> checkIfAlreadyExists(@RequestParam(value = "codice", required = true) String codice,
    		@PathVariable Long id
            ) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to count tipoDocumentos by codice");
	        boolean exists = false;
	        if(tipoDocumentoService.countTipoDocumentoByCodice(codice, id)>0){
	        	exists = true;
	        }
	        JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
	        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String descrizione, String codice, Boolean riversamentoTipoatto) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("codice", codice);
	    	if(riversamentoTipoatto!=null){
	    		json.addProperty("riversamentoTipoatto", riversamentoTipoatto);
	    	}
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoDocumentos/:id -> get the "id" tipoDocumento.
     */
    @RequestMapping(value = "/tipoDocumentos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoDocumento> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoDocumento : {}", id);
	        TipoDocumento tipoDocumento = tipoDocumentoRepository.findOne(id);
	        if (tipoDocumento == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoDocumento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoDocumentos/:id -> delete the "id" tipoDocumento.
     */
    @RequestMapping(value = "/tipoDocumentos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException{
    	JsonObject j = new JsonObject();
    	try{
	    	log.debug("REST request to delete TipoDocumento : {}", id);
	        tipoDocumentoRepository.delete(id);
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
