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

import it.linksmt.assatti.datalayer.domain.Fattura;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.FatturaRepository;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Fattura.
 */
@RestController
@RequestMapping("/api")
public class FatturaResource {

    private final Logger log = LoggerFactory.getLogger(FatturaResource.class);

    @Inject
    private FatturaRepository fatturaRepository;

    /**
     * POST  /fatturas -> Create a new fattura.
     */
    @RequestMapping(value = "/fatturas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Fattura fattura) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Fattura : {}", fattura);
	        if (fattura.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new fattura cannot already have an ID").build();
	        }
	        fatturaRepository.save(fattura);
	        return ResponseEntity.created(new URI("/api/fatturas/" + fattura.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /fatturas -> Updates an existing fattura.
     */
    @RequestMapping(value = "/fatturas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Fattura fattura) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Fattura : {}", fattura);
	        if (fattura.getId() == null) {
	            return create(fattura);
	        }
	        fatturaRepository.save(fattura);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /fatturas -> get all the fatturas.
     */
    @RequestMapping(value = "/fatturas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Fattura>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Fattura> page = fatturaRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/fatturas", offset, limit);
	        return new ResponseEntity<List<Fattura>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * GET  /fatturas/:id -> get the "id" fattura.
     */
    @RequestMapping(value = "/fatturas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Fattura> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Fattura : {}", id);
	        Fattura fattura = fatturaRepository.findOne(id);
	        if (fattura == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(fattura, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /fatturas/:id -> delete the "id" fattura.
     */
    @RequestMapping(value = "/fatturas/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Fattura : {}", id);
	        fatturaRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
