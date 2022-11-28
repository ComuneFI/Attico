package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
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
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.ReportRuntime;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.ReportRuntimeRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing ReportRuntime.
 */
@RestController
@RequestMapping("/api")
public class ReportRuntimeResource {

    private final Logger log = LoggerFactory.getLogger(ReportRuntimeResource.class);

    @Inject
    private ReportRuntimeRepository reportRuntimeRepository;

    /**
     * POST  /reportRuntimes -> Create a new reportRuntime.
     */
    @RequestMapping(value = "/reportRuntimes",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody ReportRuntime reportRuntime) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save ReportRuntime : {}", reportRuntime);
	        if (reportRuntime.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new reportRuntime cannot already have an ID").build();
	        }
	        reportRuntimeRepository.save(reportRuntime);
	        return ResponseEntity.created(new URI("/api/reportRuntimes/" + reportRuntime.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /reportRuntimes -> Updates an existing reportRuntime.
     */
    @RequestMapping(value = "/reportRuntimes",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody ReportRuntime reportRuntime) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update ReportRuntime : {}", reportRuntime);
	        if (reportRuntime.getId() == null) {
	            return create(reportRuntime);
	        }
	        reportRuntimeRepository.save(reportRuntime);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /reportRuntimes -> get all the reportRuntimes.
     */
    @RequestMapping(value = "/reportRuntimes",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ReportRuntime> getAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all ReportRuntimes");
	        return reportRuntimeRepository.findAll();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /reportRuntimes/:id -> get the "id" reportRuntime.
     */
    @RequestMapping(value = "/reportRuntimes/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ReportRuntime> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get ReportRuntime : {}", id);
	        ReportRuntime reportRuntime = reportRuntimeRepository.findOne(id);
	        if (reportRuntime == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(reportRuntime, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /reportRuntimes/:id -> delete the "id" reportRuntime.
     */
    @RequestMapping(value = "/reportRuntimes/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ReportRuntime : {}", id);
	        reportRuntimeRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
