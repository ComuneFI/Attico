package it.linksmt.assatti.gestatti.web.rest;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoAooRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoAooService;
import it.linksmt.assatti.service.dto.TipoAooSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing TipoAoo.
 */
@RestController
@RequestMapping("/api")
public class TipoAooResource {

    private final Logger log = LoggerFactory.getLogger(TipoAooResource.class);

    @Inject
    private TipoAooRepository tipoAooRepository;
    
    @Inject
    private TipoAooService tipoAooService;
    
    @Inject
    private AttoRepository attoRepository;

    /**
     * POST  /tipoAoos -> Create a new tipoAoo.
     */
    @RequestMapping(value = "/tipoAoos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody TipoAoo tipoAoo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoAoo : {}", tipoAoo);
	        if (tipoAoo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoAoo cannot already have an ID").build();
	        }
	        tipoAooRepository.save(tipoAoo);
	        return ResponseEntity.created(new URI("/api/tipoAoos/" + tipoAoo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoAoos -> Updates an existing tipoAoo.
     */
    @RequestMapping(value = "/tipoAoos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> update(@Valid @RequestBody TipoAoo tipoAoo) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update TipoAoo : {}", tipoAoo);
	        if (tipoAoo.getId() == null) {
	            return create(tipoAoo);
	        }
	        tipoAooRepository.save(tipoAoo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private TipoAooSearchDTO buildTipoAooSearchDTO(String id, String codice, String descrizione, String note) throws GestattiCatchedException{
    	try{
	    	TipoAooSearchDTO search = new TipoAooSearchDTO();
	    	search.setId(id);
	    	search.setCodice(codice);
	    	search.setDescrizione(descrizione);
	    	search.setNote(note);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoAoos -> get all the tipoAoos.
     */
    @RequestMapping(value = "/tipoAoos",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoAoo>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit,
    		@RequestParam(value = "idTipoAoo" , required = false) String id,
    		@RequestParam(value = "codice" , required = false) String codice,
    		@RequestParam(value = "descrizione" , required = false) String descrizione,
			@RequestParam(value = "note" , required = false) String note)
					throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to search TipoAoo : {}", descrizione);
	    	TipoAooSearchDTO search = this.buildTipoAooSearchDTO(id, codice, descrizione, note);
	    	log.debug("REST request to search TipoAoo : {}", search.getDescrizione());
	    	Page<TipoAoo> page = tipoAooService.findAll(search, PaginationUtil
					.generatePageRequest(offset, limit));
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/tipoAoos", offset, limit);
			return new ResponseEntity<List<TipoAoo>>(page.getContent(), headers,
					HttpStatus.OK);
	//        Page<TipoAoo> page = tipoAooRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	//        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoAoos", offset, limit);
	//        return new ResponseEntity<List<TipoAoo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoAoos/:id -> get the "id" tipoAoo.
     */
    @RequestMapping(value = "/tipoAoos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoAoo> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoAoo : {}", id);
	        TipoAoo tipoAoo = tipoAooRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (tipoAoo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByAooId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	tipoAoo.setAtti(atti);
	        }
	        return new ResponseEntity<>(tipoAoo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoAoos/:id -> delete the "id" tipoAoo.
     */
    @RequestMapping(value = "/tipoAoos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException{
    	JsonObject j = new JsonObject();
    	try{
	    	log.debug("REST request to delete TipoAoo : {}", id);
	        tipoAooRepository.delete(id);
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
