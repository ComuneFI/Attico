package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.InstantMessage;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.InstantMessageService;
import it.linksmt.assatti.service.dto.MessaggioDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing instantMessage.
 */
@RestController
@RequestMapping("/api")
public class InstantMessageResource {

    @Inject
    private InstantMessageService instantMessageService;
    
    /**
     * POST  /instantMessage -> Create a new instantMessage.
     */
    @RequestMapping(value = "/instantMessage",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody MessaggioDTO messaggio) throws GestattiCatchedException {
    	try{
    		instantMessageService.nuovoMessaggio(messaggio);
	        return ResponseEntity.created(new URI("/api/instantMessage/")).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /instantMessage
     */
    @RequestMapping(value = "/instantMessage",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<InstantMessage>> get() throws GestattiCatchedException{
    	try{
	        return new ResponseEntity<>(instantMessageService.getMessagesForUser(SecurityUtils.getCurrentLogin()), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
}
