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

import it.linksmt.assatti.datalayer.domain.CategoriaMsg;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.CategoriaMsgService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing RichiestaHD.
 */
@RestController
@RequestMapping("/api")
public class CategoriaMsgResource {

    private final Logger log = LoggerFactory.getLogger(CategoriaMsgResource.class);

    @Inject
    private CategoriaMsgService categoriaMsgService;

    /**
     * POST  /categoriaMsg -> Create a new CategoriaMsg.
     */
    @RequestMapping(value = "/categoriaMsg",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> create(@Valid @RequestBody CategoriaMsg categoriaMsg) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to create Msg");
	        if (categoriaMsg.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new categoriaMsg cannot already have an ID").build();
	        }
	        categoriaMsg = categoriaMsgService.create(categoriaMsg);
	        return ResponseEntity.created(new URI("/api/categoriaMsg/" + categoriaMsg.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /categoriaMsg/update -> categoriaMsg
     */
    @RequestMapping(value = "/categoriaMsg/update",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> update(@Valid @RequestBody CategoriaMsg categoriaMsg) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update : {}", categoriaMsg.getId());
	    	if (categoriaMsg == null || categoriaMsg.getId() == null || categoriaMsg.getId() <= 0L) {
	            return ResponseEntity.badRequest().build();
	        }
	    	categoriaMsgService.update(categoriaMsg);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /categoriaMsg/{id}/enable
     */
    @RequestMapping(value = "/categoriaMsg/{id}/enable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> enable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable categoriaMsg ", id);
	    	categoriaMsgService.enable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /categoriaMsg/{id}/disable
     */
    @RequestMapping(value = "/categoriaMsg/{id}/disable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> disable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable categoriaMsg ", id);
	    	categoriaMsgService.disable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /categoriaMsg/{id}
     */
    @RequestMapping(value = "/categoriaMsg/{id}",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<CategoriaMsg> readOne(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to read categoriaMsg ", id);
	    	CategoriaMsg categoriaMsg = categoriaMsgService.readOne(id);
	        return new ResponseEntity<>(categoriaMsg, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /categoriaMsg/search -> get all the categoriaMsgs search by criteria
     */
    @RequestMapping(value = "/categoriaMsg/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<List<CategoriaMsg>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Page<CategoriaMsg> page = categoriaMsgService.findAll(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/categoriaMsg", offset, limit);
	        return new ResponseEntity<List<CategoriaMsg>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /categoriaMsg/getAllEnabled -> get all the categoriaMsgs enabled
     */
    @RequestMapping(value = "/categoriaMsg/getAllEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CategoriaMsg>> getAllEnabled()
                                		  throws GestattiCatchedException {
    	try{
	    	List<CategoriaMsg> categorieAttive = categoriaMsgService.findAllEnabled();
	        return new ResponseEntity<List<CategoriaMsg>>(categorieAttive, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
