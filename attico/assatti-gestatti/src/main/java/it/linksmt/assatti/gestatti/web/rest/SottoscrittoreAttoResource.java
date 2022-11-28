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

import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreAttoRepository;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing SottoscrittoreAtto.
 */
@RestController
@RequestMapping("/api")
public class SottoscrittoreAttoResource {

    private final Logger log = LoggerFactory.getLogger(SottoscrittoreAttoResource.class);

    @Inject
    private SottoscrittoreAttoRepository sottoscrittoreAttoRepository;

    /**
     * POST  /sottoscrittoreAttos -> Create a new sottoscrittoreAtto.
     */
    @RequestMapping(value = "/sottoscrittoreAttos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody SottoscrittoreAtto sottoscrittoreAtto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save SottoscrittoreAtto : {}", sottoscrittoreAtto);
	        log.debug("REST request to save sottoscrittoreAtto.getAtto() : {}", sottoscrittoreAtto.getAtto());
	          if (sottoscrittoreAtto.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new sottoscrittoreAtto cannot already have an ID").build();
	        }
	        sottoscrittoreAttoRepository.save(sottoscrittoreAtto);
	        return ResponseEntity.created(new URI("/api/sottoscrittoreAttos/" + sottoscrittoreAtto.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /sottoscrittoreAttos -> Updates an existing sottoscrittoreAtto.
     */
    @RequestMapping(value = "/sottoscrittoreAttos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody SottoscrittoreAtto sottoscrittoreAtto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update SottoscrittoreAtto : {}", sottoscrittoreAtto);
	        log.debug("REST request to update sottoscrittoreAtto.getAtto() : {}", sottoscrittoreAtto.getAtto());
	        if (sottoscrittoreAtto.getId() == null) {
	            return create(sottoscrittoreAtto);
	        }
	        sottoscrittoreAttoRepository.save(sottoscrittoreAtto);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sottoscrittoreAttos -> get all the sottoscrittoreAttos.
     */
    @RequestMapping(value = "/sottoscrittoreAttos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SottoscrittoreAtto>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<SottoscrittoreAtto> page = sottoscrittoreAttoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sottoscrittoreAttos", offset, limit);
	        return new ResponseEntity<List<SottoscrittoreAtto>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sottoscrittoreAttos/:id -> get the "id" sottoscrittoreAtto.
     */
    @RequestMapping(value = "/sottoscrittoreAttos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SottoscrittoreAtto> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get SottoscrittoreAtto : {}", id);
	        SottoscrittoreAtto sottoscrittoreAtto = sottoscrittoreAttoRepository.findOne(id);
	        if (sottoscrittoreAtto == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(sottoscrittoreAtto, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /sottoscrittoreAttos/:id -> delete the "id" sottoscrittoreAtto.
     */
    @RequestMapping(value = "/sottoscrittoreAttos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete SottoscrittoreAtto : {}", id);
	        sottoscrittoreAttoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * DELETE  /sottoscrittoreAttos/deletebyattoaoo/:aooid/:attoid -> delete the "id" sottoscrittoreAtto.
     */
    @RequestMapping(value = "/sottoscrittoreAttos/deletebyattoaoo/{aooid}/{attoid}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long aooid,@PathVariable Long attoid) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete SottoscrittoreAtto : {}", aooid + "," + attoid);
	        
	        List<SottoscrittoreAtto> lista = sottoscrittoreAttoRepository.findByAooAtto(aooid, attoid);
	        
	        for(SottoscrittoreAtto sottoscrittore : lista){
	        	sottoscrittoreAttoRepository.delete(sottoscrittore.getId());
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	        
    }
}
