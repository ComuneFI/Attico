package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
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

import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.RubricaDestinatarioEsternoRepository;
import it.linksmt.assatti.service.RubricaDestinatarioEsternoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing RubricaDestinatarioEsterno.
 */
@RestController
@RequestMapping("/api")
public class RubricaDestinatarioEsternoResource {

    private final Logger log = LoggerFactory.getLogger(RubricaDestinatarioEsternoResource.class);

    @Inject
    private RubricaDestinatarioEsternoRepository rubricaDestinatarioEsternoRepository;
    
    @Inject
    private RubricaDestinatarioEsternoService rubricaDestinatarioEsternoService;
    
    /**
  	 * POST/destinatariointernos/search
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/rubricaDestinatarioEsternos/search", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<List<RubricaDestinatarioEsterno>> search(
  			@RequestParam(value = "page" , required = true) final Integer offset,
  			@RequestParam(value = "per_page", required = true) final Integer limit,
  			@RequestBody final String searchStr,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get all destinatariointernos of type specified");
	  		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Page<RubricaDestinatarioEsterno> page = rubricaDestinatarioEsternoService.search(search, PaginationUtil.generatePageRequest(offset, limit));
			if (page == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaDestinatarioEsternos/search", offset, limit);
	        return new ResponseEntity<List<RubricaDestinatarioEsterno>>(page.getContent(), headers, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * POST/destinatariointernos/checkIfAlreadyexist
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/rubricaDestinatarioEsternos/checkIfAlreadyexist", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<String> checkIfAlreadyexist(
  			@RequestBody RubricaDestinatarioEsterno rubricaDestinatarioEsterno,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get all destinatariointernos of type specified");
	  		boolean exists = rubricaDestinatarioEsternoService.checkIfAlreadyExists(rubricaDestinatarioEsterno);
			JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
    
    /**
     * POST  /rubricaDestinatarioEsternos -> Create/update rubricaDestinatarioEsterno.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RubricaDestinatarioEsterno> save(@RequestBody RubricaDestinatarioEsterno rubricaDestinatarioEsterno) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save RubricaDestinatarioEsterno : {}", rubricaDestinatarioEsterno);
	        rubricaDestinatarioEsternoService.save(rubricaDestinatarioEsterno);
	        return new ResponseEntity<RubricaDestinatarioEsterno>(rubricaDestinatarioEsterno, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /rubricaDestinatarioEsternos -> get all the rubricaDestinatarioEsternos.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RubricaDestinatarioEsterno>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<RubricaDestinatarioEsterno> page = rubricaDestinatarioEsternoService.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaDestinatarioEsternos", offset, limit);
	        return new ResponseEntity<List<RubricaDestinatarioEsterno>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /rubricaDestinatarioEsternos -> getByAooId the rubricaDestinatarioEsternos.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RubricaDestinatarioEsterno>> getByAooId(@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit, @RequestParam(value = "aooId" , required = true) Long aooId)
            		throws GestattiCatchedException {
    	try{
	        Page<RubricaDestinatarioEsterno> page = rubricaDestinatarioEsternoService.findByAooIdAndValidi(aooId, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaDestinatarioEsternos/getByAooId", offset, limit);
	        return new ResponseEntity<List<RubricaDestinatarioEsterno>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    

    /**
     * GET  /rubricaDestinatarioEsternos/:id -> get the "id" rubricaDestinatarioEsterno.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RubricaDestinatarioEsterno> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get RubricaDestinatarioEsterno : {}", id);
	        RubricaDestinatarioEsterno rubricaDestinatarioEsterno = rubricaDestinatarioEsternoService.findOne(id);
	        if (rubricaDestinatarioEsterno == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(rubricaDestinatarioEsterno, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /rubricaDestinatarioEsternos/:id -> get the "id" rubricaDestinatarioEsterno.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disableRubricaDestinatario(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
    		log.debug("REST request to disable rubrica destinatario : {}", id);
			JsonObject json = new JsonObject();
			try{
				if(id!=null){
					rubricaDestinatarioEsternoService.disable(id);
					json.addProperty("stato", "ok");
				}else{
					json.addProperty("stato", "errore");
				}
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT /rubricaDestinatarioEsternos/enable -> Enable a rubrica destinatario
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableRubricaDestinatario(
			@PathVariable final String id,
			final HttpServletResponse response)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to enable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				rubricaDestinatarioEsternoService.enable(Long.parseLong(id));
				json.addProperty("stato", "ok");
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}

    /**
     * DELETE  /rubricaDestinatarioEsternos/:id -> delete the "id" rubricaDestinatarioEsterno.
     */
    @RequestMapping(value = "/rubricaDestinatarioEsternos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete RubricaDestinatarioEsterno : {}", id);
	        rubricaDestinatarioEsternoService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
