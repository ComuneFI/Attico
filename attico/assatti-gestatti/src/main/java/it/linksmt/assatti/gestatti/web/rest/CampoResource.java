package it.linksmt.assatti.gestatti.web.rest;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.datalayer.domain.Campo;
import it.linksmt.assatti.service.CampoService;
//TODO integrazione import it.linksmt.assatti.gestatti.bpmwrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing Campo.
 */
@RestController
@RequestMapping("/api")
public class CampoResource {

    private final Logger log = LoggerFactory.getLogger(CampoResource.class);

    @Inject
    private CampoService campoService;

    /**
     * GET  /campi
     */
    @RequestMapping(value = "/campi",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Campo>> findAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request findAll");
	    		
	    	Iterable<Campo> page = campoService.findAll();
	    	return new ResponseEntity<Iterable<Campo>>( page , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


}
