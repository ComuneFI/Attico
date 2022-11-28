package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

import it.linksmt.assatti.datalayer.domain.TipoAdempimento;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoAdempimentoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoAdempimentoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing TipoAdempimento.
 */
@RestController
@RequestMapping("/api")
public class TipoAdempimentoResource {

    private final Logger log = LoggerFactory.getLogger(TipoAdempimentoResource.class);

    @Inject
    private TipoAdempimentoRepository tipoAdempimentoRepository;
    
    @Inject
    private TipoAdempimentoService tipoAdempimentoService;
    
    @Inject
    private AttoRepository attoRepository;

    /**
     * POST  /tipoAdempimentos -> Create a new tipoAdempimento.
     */
    @RequestMapping(value = "/tipoAdempimentos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoAdempimento tipoAdempimento) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoAdempimento : {}", tipoAdempimento);
	        if (tipoAdempimento.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoAdempimento cannot already have an ID").build();
	        }
	        tipoAdempimentoRepository.save(tipoAdempimento);
	        return ResponseEntity.created(new URI("/api/tipoAdempimentos/" + tipoAdempimento.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoAdempimentos -> Updates an existing tipoAdempimento.
     */
    @RequestMapping(value = "/tipoAdempimentos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoAdempimento tipoAdempimento) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update TipoAdempimento : {}", tipoAdempimento);
	        if (tipoAdempimento.getId() == null) {
	            return create(tipoAdempimento);
	        }
	        tipoAdempimentoRepository.save(tipoAdempimento);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoAdempimentos -> get all the tipoAdempimentos.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/tipoAdempimentos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoAdempimento>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "tipoiter", required = false) String tipoiter) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all TipoAdempimentos");
	        JsonObject jsonSearch = this.buildSearchObject(descrizione, tipoiter);
	        return tipoAdempimentoService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String descrizione, String tipoiter) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("tipoiter", tipoiter);
	    	json.addProperty("descrizione", descrizione);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoAdempimentos/:id -> get the "id" tipoAdempimento.
     */
    @RequestMapping(value = "/tipoAdempimentos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoAdempimento> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoAdempimento : {}", id);
	        TipoAdempimento tipoAdempimento = tipoAdempimentoRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (tipoAdempimento == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByTipoAttoId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	tipoAdempimento.setAtti(atti);
	        }
	        
	        return new ResponseEntity<>(tipoAdempimento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoAdempimentos/:id -> delete the "id" tipoAdempimento.
     */
    @RequestMapping(value = "/tipoAdempimentos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete TipoAdempimento : {}", id);
	        tipoAdempimentoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
