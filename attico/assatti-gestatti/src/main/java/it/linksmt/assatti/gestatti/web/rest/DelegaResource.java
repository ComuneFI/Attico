package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.DelegaService;
import it.linksmt.assatti.service.dto.DelegaDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Delega.
 */
@RestController
@RequestMapping("/api")
public class DelegaResource {

    private final Logger log = LoggerFactory.getLogger(DelegaResource.class);

    @Inject
    private DelegaService delegaService;
    
    /**
  	 * POST/delega/search
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/delega/search", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<List<DelegaDto>> search(
  			@RequestParam(value = "page" , required = true) final Integer offset,
  			@RequestParam(value = "per_page", required = true) final Integer limit,
  			@RequestBody final String searchStr,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get deleghe by criterias");
	  		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Long totalElements = 0L;
	  		
	  		Page<DelegaDto> page = delegaService.search(search, totalElements, offset, limit, null, null);
			if (page == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/search", offset, limit);
    		
	        return new ResponseEntity<List<DelegaDto>>(page.getContent(), headers, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * POST/delega/exists
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/delega/exists", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public ResponseEntity<String> exists(
  			@RequestBody final String searchStr,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get deleghe by criterias");
	  		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Long totalElements = 0L;
	  		
	  		Page<DelegaDto> page = delegaService.search(search, totalElements, 1, 1, null, null);
			if (page == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			JsonObject j = new JsonObject();
			j.addProperty("exists", page.getTotalElements() > 0);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
    
    /**
     * POST  /delega -> Create a new delega.
     */
    @RequestMapping(value = "/delega",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody DelegaDto delegaDto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Delega : {}", delegaDto);
	        if (delegaDto.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new delega cannot already have an ID").build();
	        }
	        
        	if(delegaDto.getDataValiditaFine() != null && delegaDto.getDataValiditaInizio()!=null && delegaDto.getDataValiditaInizio().isAfter(delegaDto.getDataValiditaFine())) {
        		return ResponseEntity.badRequest().header("Failure", "Inserire un range date valido").build();
        	}
        	
	        /*
	         * salvataggio delega
	         */
//	        Delega delega = DelegaConverter.convertToModel(delegaDto);
	        delegaDto = delegaService.save(delegaDto);

	        return ResponseEntity.created(new URI("/api/delega/" + delegaDto.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /delega -> Updates an existing delega.
     */
    @RequestMapping(value = "/delega",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody DelegaDto delegaDto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Delega : {}", delegaDto);
	        if (delegaDto.getId() == null) {
	            return create(delegaDto);
	        }
	        if(delegaDto.getDataValiditaFine() != null && delegaDto.getDataValiditaInizio()!=null && delegaDto.getDataValiditaInizio().isAfter(delegaDto.getDataValiditaFine())) {
        		return ResponseEntity.badRequest().header("Failure", "Inserire un range date valido").build();
        	}
	        
	        /*
	         * salvataggio delega
	         */
	        delegaDto = delegaService.save(delegaDto);

	        return ResponseEntity.ok().build();
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /delega -> find all the delega.
     */
    @RequestMapping(value = "/delega",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DelegaDto>> findAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "onlyActive", required = false) Boolean onlyActive)
            		throws GestattiCatchedException {
    	List<DelegaDto> listDelegaDto = null;
    	Long totalElements = null;
    	try {
    		if(onlyActive==null || !onlyActive.booleanValue()) {
    			listDelegaDto = delegaService.findAll(offset, limit, null, null);
    			totalElements = delegaService.countAll();
    		} else {
    			listDelegaDto = delegaService.findByActive(offset, limit, null, null);
    			totalElements = delegaService.countByActive();
    		}
    		
    		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(totalElements, "/api/tipoAttos", offset, limit);
    		
	        return new ResponseEntity<List<DelegaDto>>(listDelegaDto, headers, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /delega/:id -> get the "id" delega.
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/delega/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get Delega : {}", id);
	    	DelegaDto delegaDto = delegaService.get(id);
	        
	        if (delegaDto == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        return new ResponseEntity<DelegaDto>(delegaDto, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	} 
    }

	/**
	 * DELETE /delega/:id -> delete the "id" delega (cancellazione logica).
	 */
	@RequestMapping(value = "/delega/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException {
		JsonObject j = new JsonObject();
		try {
			/*
			 * Elimino Delega
			 */
			log.debug("REST request to delete Delega : {}", id);
			delegaService.toggle(id);
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

	/**
	 * GET /delega/:id -> delete the "id" delega.
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/delega/deleganti/{idDelegato}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity findDeleganti(@PathVariable Long idDelegato) throws GestattiCatchedException {
    	List<Profilo> listProfiloDto = null;
    	try {
    		listProfiloDto = delegaService.findByDelegatoActive(idDelegato);
	        return new ResponseEntity<List<Profilo>>(listProfiloDto, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
	}

}
