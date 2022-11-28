package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
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

import it.linksmt.assatti.datalayer.domain.Msg;
import it.linksmt.assatti.datalayer.domain.PrioritaMsgEnum;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.MsgService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing RichiestaHD.
 */
@RestController
@RequestMapping("/api")
public class MsgResource {

    private final Logger log = LoggerFactory.getLogger(MsgResource.class);

    @Inject
    private MsgService msgService;

    /**
     * POST  /Msgs -> Create a new richiestaHD.
     */
    @RequestMapping(value = "/msgs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> create(@Valid @RequestBody Msg msg) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to create/update Msg");
	        if (msg.getId() != null) {
	        	msg = msgService.update(msg);
	        }else{	        
	        	msg = msgService.create(msg);
	        }
	        
	        return ResponseEntity.created(new URI("/api/msgs/" + msg.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /msgs/{id}/forceExpire
     */
    @RequestMapping(value = "/msgs/{id}/forceExpire",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> forceExpire(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to forceExpire msg ", id);
	        msgService.forceExpire(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /msgs/{id}
     */
    @RequestMapping(value = "/msgs/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Msg> readOne(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to read msg ", id);
	        Msg msg = msgService.readOne(id);
	        return new ResponseEntity<>(msg, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /msg/prioritas
     */
    @RequestMapping(value = "/msgs/prioritas",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<PrioritaMsgEnum[]> getPrioritas() throws GestattiCatchedException{
    	try{
	        return new ResponseEntity<PrioritaMsgEnum[]>(PrioritaMsgEnum.values(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /msgs/search -> get all the msgs search by criteria
     */
    @RequestMapping(value = "/msgs/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<List<Msg>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Page<Msg> page = msgService.findAll(PaginationUtil.generatePageRequest(offset, limit), search, null);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/msgs", offset, limit);
	        return new ResponseEntity<List<Msg>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /msgs/search -> get all the msgs search by criteria
     */
    @RequestMapping(value = "/msgs/searchUtente",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Msg>> searchUtente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Page<Msg> page = msgService.findAllUtente(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/msgs", offset, limit);
	        return new ResponseEntity<List<Msg>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
