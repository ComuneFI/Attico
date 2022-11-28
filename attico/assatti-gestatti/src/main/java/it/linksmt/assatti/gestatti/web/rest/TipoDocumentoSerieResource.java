package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoDocumentoSerieService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

/**
 * REST controller for managing TipoDocumentoSerie.
 */
@RestController
@RequestMapping("/api")
public class TipoDocumentoSerieResource {

    private final Logger log = LoggerFactory.getLogger(TipoDocumentoSerieResource.class);

    @Inject
    private TipoDocumentoSerieService tipoDocumentoSerieService;

    /**
     * POST  /tipoDocumentoSeries -> Create a new tipoDocumentoSerie.
     */
    @RequestMapping(value = "/tipoDocumentoSeries",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody TipoDocumentoSerie tipoDocumentoSerie) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoDocumentoSerie : {}", tipoDocumentoSerie);
	        if (tipoDocumentoSerie.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoDocumentoSerie cannot already have an ID").build();
	        }
	        tipoDocumentoSerieService.save(tipoDocumentoSerie);
	        return ResponseEntity.created(new URI("/api/tipoDocumentoSeries/" + tipoDocumentoSerie.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoDocumentoSeries -> Updates an existing tipoDocumentoSerie.
     */
    @RequestMapping(value = "/tipoDocumentoSeries",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> update(@Valid @RequestBody TipoDocumentoSerie tipoDocumentoSerie) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update TipoDocumentoSerie : {}", tipoDocumentoSerie);
	        if (tipoDocumentoSerie.getId() == null) {
	            return create(tipoDocumentoSerie);
	        }
	        tipoDocumentoSerieService.save(tipoDocumentoSerie);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    
    /**
     * GET  /tipoDocumentoSeries -> get all the tipoDocumentoSeries.
     */
    @RequestMapping(value = "/tipoDocumentoSeries",method = RequestMethod.GET,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoDocumentoSerie>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit)
					throws GestattiCatchedException {
    	try{
    		
	    	Page<TipoDocumentoSerie> page = tipoDocumentoSerieService.findAll(null, PaginationUtil
					.generatePageRequest(offset, limit));
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/tipoDocumentoSeries", offset, limit);
			return new ResponseEntity<List<TipoDocumentoSerie>>(page.getContent(), headers,
					HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    
    /**
     * post  /tipoDocumentoSeries -> get all the tipoDocumentoSeries.
     */
    @RequestMapping(value = "/tipoDocumentoSeries/search",method = RequestMethod.POST,produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoDocumentoSerie>> search(
    		@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit,
    		@RequestBody String searchStr)
					throws GestattiCatchedException {
    	try{
    		
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
    		
	    	Page<TipoDocumentoSerie> page = tipoDocumentoSerieService.findAll(search, PaginationUtil
					.generatePageRequest(offset, limit));
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/tipoDocumentoSeries", offset, limit);
			return new ResponseEntity<List<TipoDocumentoSerie>>(page.getContent(), headers,
					HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoDocumentoSeries/:id -> get the "id" tipoDocumentoSerie.
     */
    @RequestMapping(value = "/tipoDocumentoSeries/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoDocumentoSerie> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoDocumentoSerie : {}", id);
	        TipoDocumentoSerie tipoDocumentoSerie = tipoDocumentoSerieService.findOne(id);
	        if (tipoDocumentoSerie == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoDocumentoSerie, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT /tipoDocumentoSeries/enable -> Enable an tipoDocumentoSerie
     */
    @RequestMapping(value = "/tipoDocumentoSeries/{id}/abilitato/{abilitato}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> setIsAbilitato(
			@PathVariable final String id,
			@PathVariable final Boolean abilitato,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable ConfigurazioneSerie : {}", id);
	    	TipoDocumentoSerie tipoDocumentoSerie = tipoDocumentoSerieService.findOne(Long.parseLong(id));
			if(tipoDocumentoSerie!=null) {
				tipoDocumentoSerieService.setIsAbilitato(abilitato, Long.parseLong(id));
				return new ResponseEntity<Void>(HttpStatus.OK);
	        }
			return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
}
