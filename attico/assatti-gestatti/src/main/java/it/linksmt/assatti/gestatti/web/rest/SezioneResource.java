package it.linksmt.assatti.gestatti.web.rest;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.TipoListaTaskDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Sezione;
import it.linksmt.assatti.service.SezioneService;
//TODO integrazione import it.linksmt.assatti.gestatti.bpmwrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing Sezione.
 */
@RestController
@RequestMapping("/api")
public class SezioneResource {

    private final Logger log = LoggerFactory.getLogger(SezioneResource.class);

    @Inject
    private SezioneService sezioneService;

    /**
     * GET  /sezioni
     */
    @RequestMapping(value = "/sezioni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Sezione>> findAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request findAll");
	    		
	    	Iterable<Sezione> page = sezioneService.findAll();
	    	return new ResponseEntity<Iterable<Sezione>>( page , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


}
