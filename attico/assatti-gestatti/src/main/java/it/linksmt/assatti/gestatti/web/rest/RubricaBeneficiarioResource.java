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

import it.linksmt.assatti.datalayer.domain.RubricaBeneficiario;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.RubricaBeneficiarioRepository;
import it.linksmt.assatti.service.RubricaBeneficiarioService;
import it.linksmt.assatti.service.UserService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing rubricaBeneficiario.
 */
@RestController
@RequestMapping("/api")
public class RubricaBeneficiarioResource {

    private final Logger log = LoggerFactory.getLogger(RubricaBeneficiarioResource.class);

    @Inject
    private RubricaBeneficiarioRepository rubricaBeneficiarioRepository;
    
    @Inject
    private RubricaBeneficiarioService rubricaBeneficiarioService;
    
    @Inject
    private UserService userService;
    
    /**
  	 * POST/rubricaBeneficiarios/search
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/rubricaBeneficiarios/search", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<List<RubricaBeneficiario>> search(
  			@RequestParam(value = "page" , required = true) final Integer offset,
  			@RequestParam(value = "per_page", required = true) final Integer limit,
  			@RequestBody final String searchStr,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get rubirca beneficiari by criterias");
	  		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
			Page<RubricaBeneficiario> page = rubricaBeneficiarioService.search(search, PaginationUtil.generatePageRequest(offset, limit));
			if (page == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaBeneficiarios/search", offset, limit);
	        return new ResponseEntity<List<RubricaBeneficiario>>(page.getContent(), headers, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
    
    /**
     * POST  /rubricaBeneficiarios -> Create a new rubricaBeneficiario.
     */
    @RequestMapping(value = "/rubricaBeneficiarios",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RubricaBeneficiario> create(@RequestBody RubricaBeneficiario rubricaBeneficiario) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save rubricaBeneficiario : {}", rubricaBeneficiario);
	       
	        if(rubricaBeneficiario.getAoo()!=null && rubricaBeneficiario.getAoo().getId() == null)
	        	rubricaBeneficiario.setAoo(null);
	        rubricaBeneficiarioService.save(rubricaBeneficiario);
	        return new ResponseEntity<RubricaBeneficiario>(rubricaBeneficiario, HttpStatus.OK);
	        
	//        return ResponseEntity.created(new URI("/api/rubricaBeneficiarios/" + rubricaBeneficiario.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /rubricaBeneficiarios -> Updates an existing rubricaBeneficiario.
     */
    @RequestMapping(value = "/rubricaBeneficiarios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RubricaBeneficiario> update(@RequestBody RubricaBeneficiario rubricaBeneficiario) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update rubricaBeneficiario : {}", rubricaBeneficiario);
	    	if(userService.isLoggedUserAnAdmin()){
	    		rubricaBeneficiario.setAoo(null);
	    	}else if(rubricaBeneficiario.getAoo()==null || rubricaBeneficiario.getAoo().getId()==null){
	    		return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);  
	    	}
	        if (rubricaBeneficiario.getId() == null) {
	            return create(rubricaBeneficiario);
	        }
	        rubricaBeneficiarioService.save(rubricaBeneficiario);
	        return new ResponseEntity<RubricaBeneficiario>(rubricaBeneficiario, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /rubricaBeneficiarios -> get all the rubricaBeneficiarios.
     */
    @RequestMapping(value = "/rubricaBeneficiarios",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RubricaBeneficiario>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<RubricaBeneficiario> page = rubricaBeneficiarioService.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaBeneficiarios", offset, limit);
	        return new ResponseEntity<List<RubricaBeneficiario>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /rubricaBeneficiarios -> getByAooId the rubricaBeneficiarios.
     */
    @RequestMapping(value = "/rubricaBeneficiarios/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RubricaBeneficiario>> getByAooId(@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit, @RequestParam(value = "aooId" , required = true) Long aooId)
            		throws GestattiCatchedException {
    	try{
	        Page<RubricaBeneficiario> page = rubricaBeneficiarioService.findByAooIdAndValidi(aooId, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/rubricaBeneficiarios/getByAooId", offset, limit);
	        return new ResponseEntity<List<RubricaBeneficiario>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    

    /**
     * GET  /rubricaBeneficiarios/:id -> get the "id" rubricaBeneficiario.
     */
    @RequestMapping(value = "/rubricaBeneficiarios/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RubricaBeneficiario> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get rubricaBeneficiario : {}", id);
	        RubricaBeneficiario rubricaBeneficiario = rubricaBeneficiarioService.findOne(id);
	        if (rubricaBeneficiario == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(rubricaBeneficiario, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /rubricaBeneficiarios/:id -> get the "id" rubricaBeneficiario.
     */
    @RequestMapping(value = "/rubricaBeneficiarios/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disableRubricaBeneficiario(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
    		log.debug("REST request to disable rubrica beneficiario : {}", id);
			JsonObject json = new JsonObject();
			try{
				if(id!=null){
					rubricaBeneficiarioService.disable(id);
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
     * PUT /rubricaBeneficiarios/enable -> Enable a rubrica beneficiario
     */
    @RequestMapping(value = "/rubricaBeneficiarios/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableRubricaBeneficiario(
			@PathVariable final String id,
			final HttpServletResponse response)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to enable profile : {}", id);
			JsonObject json = new JsonObject();
			try{
				rubricaBeneficiarioService.enable(Long.parseLong(id));
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
     * DELETE  /rubricaBeneficiarios/:id -> delete the "id" rubricaBeneficiario.
     */
    @RequestMapping(value = "/rubricaBeneficiarios/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete rubricaBeneficiario : {}", id);
	        rubricaBeneficiarioService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
