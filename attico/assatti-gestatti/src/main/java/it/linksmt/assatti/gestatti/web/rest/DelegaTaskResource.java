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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import it.linksmt.assatti.service.DelegaTaskService;
import it.linksmt.assatti.service.dto.DelegaTaskDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Delega.
 */
@RestController
@RequestMapping("/api")
public class DelegaTaskResource {

    private final Logger log = LoggerFactory.getLogger(DelegaTaskResource.class);

    @Inject
    private DelegaTaskService delegaTaskService;
    
  	@RequestMapping(value = "/delegaTask/search", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<List<DelegaTaskDto>> search(
  			@RequestParam(value = "page" , required = true) final Integer offset,
  			@RequestParam(value = "per_page", required = true) final Integer limit,
  			@RequestBody final String searchStr,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get deleghe by criterias");
	  		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Long totalElements = 0L;
	  		
	  		Page<DelegaTaskDto> page = delegaTaskService.search(search, totalElements, offset, limit, null, null);
			if (page == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/search", offset, limit);
    		
	        return new ResponseEntity<List<DelegaTaskDto>>(page.getContent(), headers, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
    
    /**
     * POST  /delegaTask -> Create a new delegaTask.
     */
    @RequestMapping(value = "/delegaTask",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody DelegaTaskDto delegaDto, @RequestHeader(value="profiloId" ,required=false) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save DelegaTask : {}", delegaDto);
	        if (delegaDto.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new delega cannot already have an ID").build();
	        }
	        boolean exists = delegaTaskService.alreadyExists(delegaDto);
	        if(!exists) {
	        	delegaTaskService.save(delegaDto, profiloId);
	        }else {
	        	throw new GestattiCatchedException("Esiste gi\u00E0 una delega per la lavorazione selezionata. Modificare quella esistente se necessario.");
	        }
	        return ResponseEntity.created(new URI("/api/delegaTask/" + delegaDto.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /delega -> Updates an existing delegaTask.
     */
    @RequestMapping(value = "/delegaTask",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody DelegaTaskDto delegaDto, @RequestHeader(value="profiloId" ,required=false) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update DelegaTask : {}", delegaDto);
	        if (delegaDto.getId() == null) {
	            return create(delegaDto, profiloId);
	        }
	        
	        /*
	         * salvataggio delega
	         */
	        delegaTaskService.save(delegaDto, profiloId);

	        return ResponseEntity.ok().build();
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /delegaTask -> find all the delegaTask.
     */
    @RequestMapping(value = "/delegaTask",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DelegaTaskDto>> findAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit)
            		throws GestattiCatchedException {
    	List<DelegaTaskDto> listDelegaDto = null;
    	Long totalElements = null;
    	try {
			listDelegaDto = delegaTaskService.findAll(offset, limit, null, null);
			totalElements = delegaTaskService.countAll();
    		
    		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(totalElements, "/api/delegaTask", offset, limit);
    		
	        return new ResponseEntity<List<DelegaTaskDto>>(listDelegaDto, headers, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /delegaTask/:id -> get the "id" delegaTask.
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/delegaTask/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get DelegaTask : {}", id);
	    	DelegaTaskDto delegaDto = delegaTaskService.get(id);
	        
	        if (delegaDto == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        return new ResponseEntity<DelegaTaskDto>(delegaDto, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	} 
    }

	/**
	 * DELETE /delegaTask/:id -> delete the "id" delegaTask (cancellazione logica).
	 */
	@RequestMapping(value = "/delegaTask/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> delete(@PathVariable Long id, @RequestHeader(value="profiloId" ,required=false) final Long profiloId) throws GestattiCatchedException {
		JsonObject j = new JsonObject();
		try {
			/*
			 * Elimino Delega
			 */
			log.debug("REST request to delete Delega : {}", id);
			delegaTaskService.cancellaDelega(id, profiloId);
			j.addProperty("success", true);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
		}
		catch (Exception e) {
			j.addProperty("success", false);
			String errore = "Errore generico";
			if (e.getCause() != null && e.getCause() instanceof Exception) {
				Exception ex = (Exception) e.getCause();
				for (int i = 0; i < 3; i++) {
					if (ex instanceof MySQLIntegrityConstraintViolationException) {
						errore = ex.getMessage();
						break;
					} else if (ex.getCause() != null && ex.getCause() instanceof Exception) {
						ex = (Exception) ex.getCause();
					} else {
						break;
					}
				}
			}
			j.addProperty("message", errore);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
		}
	}

}
