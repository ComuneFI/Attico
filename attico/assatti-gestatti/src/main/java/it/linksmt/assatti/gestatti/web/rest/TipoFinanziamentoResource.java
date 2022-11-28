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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;
import it.linksmt.assatti.datalayer.repository.TipoFinanziamentoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoFinanziamentoService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing TipoFinanziamento.
 */
@RestController
@RequestMapping("/api")
public class TipoFinanziamentoResource {

    private final Logger log = LoggerFactory.getLogger(TipoFinanziamentoResource.class);

    @Inject
    private TipoFinanziamentoRepository tipoFinanziamentoRepository;

    @Inject
    private TipoFinanziamentoService tipoFinanziamentoService;
    
    /**
     * GET  /tipoFinanziamentos/{id}/enable
     */
    @RequestMapping(value = "/tipoFinanziamentos/{id}/enable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> enable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable tipoFinanziamento ", id);
	    	tipoFinanziamentoService.enable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoFinanziamentos/{id}/disable
     */
    @RequestMapping(value = "/tipoFinanziamentos/{id}/disable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> disable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable tipoFinanziamento ", id);
	    	tipoFinanziamentoService.disable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /tipoFinanziamentos -> Create a new tipoFinanziamento.
     */
    @RequestMapping(value = "/tipoFinanziamentos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody TipoFinanziamento tipoFinanziamento) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoFinanziamento : {}", tipoFinanziamento);
	        if (tipoFinanziamento.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoFinanziamento cannot already have an ID").build();
	        }
	        if(tipoFinanziamento.getEnabled()==null){
	        	tipoFinanziamento.setEnabled(true);
	        }
	        tipoFinanziamentoRepository.save(tipoFinanziamento);
	        return ResponseEntity.created(new URI("/api/tipoFinanziamentos/" + tipoFinanziamento.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoFinanziamentos -> Updates an existing tipoFinanziamento.
     */
    @RequestMapping(value = "/tipoFinanziamentos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> update(@Valid @RequestBody TipoFinanziamento tipoFinanziamento) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update TipoFinanziamento : {}", tipoFinanziamento);
	        if (tipoFinanziamento.getId() == null) {
	            return create(tipoFinanziamento);
	        }
	        tipoFinanziamentoRepository.save(tipoFinanziamento);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoFinanziamentos/getAllEnabled -> get all the tipoFinanziamentos enabled
     */
    @RequestMapping(value = "/tipoFinanziamentos/getAllEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoFinanziamento>> getAllEnabled()
                                		  throws GestattiCatchedException {
    	try{
	    	List<TipoFinanziamento> tipiFinanziamento = tipoFinanziamentoService.findAllEnabled();
	        return new ResponseEntity<List<TipoFinanziamento>>(tipiFinanziamento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoFinanziamentos/getAllEnabled -> get all the tipoFinanziamentos enabled
     */
    @RequestMapping(value = "/tipoFinanziamentos/getAll",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoFinanziamento>> getAll()
                                		  throws GestattiCatchedException {
    	try{
	    	List<TipoFinanziamento> tipiFinanziamento = tipoFinanziamentoService.findAll();
	        return new ResponseEntity<List<TipoFinanziamento>>(tipiFinanziamento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
     * GET  /tipoFinanziamentos/:id -> get the "id" tipoFinanziamento.
     */
    @RequestMapping(value = "/tipoFinanziamentos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoFinanziamento> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoFinanziamento : {}", id);
	        TipoFinanziamento tipoFinanziamento = tipoFinanziamentoService.findOne(id);
	        if (tipoFinanziamento == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoFinanziamento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * GET  /tipoFinanziamentos/getDescriptionByCode/:codice -> get description from "codice" of tipoFinanziamento.
     */
    @RequestMapping(value = "/tipoFinanziamentos/getDescriptionByCode/{codice}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getDescriptionByCode(@PathVariable String codice, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoFinanziamento description by code: {}", codice);
	        String description = tipoFinanziamentoService.getDescrizioneByCodiceTipoFinanziamento(codice);
	        if (description == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        JsonObject json = new JsonObject();
			try{
				json.addProperty("descrizione", description);
			}catch(Exception e){
				json.addProperty("descrizione", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * DELETE  /tipoFinanziamentos/:id -> delete the "id" tipoFinanziamento.
     */
    @RequestMapping(value = "/tipoFinanziamentos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete TipoFinanziamento : {}", id);
	        tipoFinanziamentoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
