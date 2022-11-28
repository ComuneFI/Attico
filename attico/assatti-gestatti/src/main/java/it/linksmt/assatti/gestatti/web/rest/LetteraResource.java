package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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

import it.linksmt.assatti.datalayer.domain.Lettera;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.LetteraRepository;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Lettera.
 */
@RestController
@RequestMapping("/api")
public class LetteraResource {

    private final Logger log = LoggerFactory.getLogger(LetteraResource.class);

    @Inject
    private LetteraRepository letteraRepository;

    /**
     * POST  /letteras -> Create a new lettera.
     */
    @RequestMapping(value = "/letteras",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Lettera lettera) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Lettera : {}", lettera);
	        if (lettera.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new lettera cannot already have an ID").build();
	        }
	        letteraRepository.save(lettera);
	        return ResponseEntity.created(new URI("/api/letteras/" + lettera.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /letteras -> Updates an existing lettera.
     */
    @RequestMapping(value = "/letteras",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Lettera lettera) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Lettera : {}", lettera);
	        if (lettera.getId() == null) {
	            return create(lettera);
	        }
	        letteraRepository.save(lettera);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * GET  /letteras -> get all the letteras.
     */
    @RequestMapping(value = "/letteras",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Lettera>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Lettera> page = letteraRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/letteras", offset, limit);
	        return new ResponseEntity<List<Lettera>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /letteras/:id -> get the "id" lettera.
     */
    @RequestMapping(value = "/letteras/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Lettera> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Lettera : {}", id);
	        Lettera lettera = letteraRepository.findOne(id);
	        if (lettera == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(lettera, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /letteras/:id -> delete the "id" lettera.
     */
    @RequestMapping(value = "/letteras/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Lettera : {}", id);
	        letteraRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
