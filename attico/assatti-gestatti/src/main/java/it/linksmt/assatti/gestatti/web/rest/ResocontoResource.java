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

import it.linksmt.assatti.datalayer.domain.Resoconto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.ResocontoRepository;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Resoconto.
 */
@RestController
@RequestMapping("/api")
public class ResocontoResource {

    private final Logger log = LoggerFactory.getLogger(ResocontoResource.class);

    @Inject
    private ResocontoRepository resocontoRepository;

    /**
     * POST  /resocontos -> Create a new resoconto.
     */
    @RequestMapping(value = "/resocontos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Resoconto resoconto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Resoconto : {}", resoconto);
	        if (resoconto.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new resoconto cannot already have an ID").build();
	        }
	        resocontoRepository.save(resoconto);
	        return ResponseEntity.created(new URI("/api/resocontos/" + resoconto.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /resocontos -> Updates an existing resoconto.
     */
    @RequestMapping(value = "/resocontos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Resoconto resoconto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Resoconto : {}", resoconto);
	        if (resoconto.getId() == null) {
	            return create(resoconto);
	        }
	        resocontoRepository.save(resoconto);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /resocontos -> get all the resocontos.
     */
    @RequestMapping(value = "/resocontos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Resoconto>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Resoconto> page = resocontoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/resocontos", offset, limit);
	        return new ResponseEntity<List<Resoconto>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * GET  /resocontos/:id -> get the "id" resoconto.
     */
    @RequestMapping(value = "/resocontos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Resoconto> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Resoconto : {}", id);
	        Resoconto resoconto = resocontoRepository.findOne(id);
	        if (resoconto == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(resoconto, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /resocontos/:id -> delete the "id" resoconto.
     */
    @RequestMapping(value = "/resocontos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Resoconto : {}", id);
	        resocontoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
