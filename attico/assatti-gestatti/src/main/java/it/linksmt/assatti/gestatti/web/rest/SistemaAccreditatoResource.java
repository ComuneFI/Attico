package it.linksmt.assatti.gestatti.web.rest;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.SistemaAccreditato;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.SistemaAccreditatoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing SistemaAccreditato.
 */
@RestController
@RequestMapping("/api")
public class SistemaAccreditatoResource {

    private final Logger log = LoggerFactory.getLogger(SistemaAccreditatoResource.class);

    @Inject
    private SistemaAccreditatoService sistemaAccreditatoService;
    
    /**
     * POST  /sistemaAccreditatos -> Create a new sistemaAccreditato.
     */
    @RequestMapping(value = "/sistemaAccreditatos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> save(@Valid @RequestBody SistemaAccreditato sistemaAccreditato) throws GestattiCatchedException {
    	try{
    		sistemaAccreditatoService.createUpdate(sistemaAccreditato);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /sistemaAccreditatos -> get all the sistemaAccreditatos.
     */
    @RequestMapping(value = "/sistemaAccreditatos/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SistemaAccreditato>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestBody final String searchStr)
            		throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Page<SistemaAccreditato> page = sistemaAccreditatoService.findbyCriteria(search, limit, offset);
	  		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page.getTotalElements(), "/api/sistemaAccreditatos/search", offset, limit);
	        return new ResponseEntity<List<SistemaAccreditato>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

}
