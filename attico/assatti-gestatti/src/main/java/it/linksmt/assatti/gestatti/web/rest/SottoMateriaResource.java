package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.joda.time.LocalDate;
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

import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.SottoMateriaRepository;
import it.linksmt.assatti.service.SottoMateriaService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing SottoMateria.
 */
@RestController
@RequestMapping("/api")
public class SottoMateriaResource {

    private final Logger log = LoggerFactory.getLogger(SottoMateriaResource.class);

    @Inject
    private SottoMateriaRepository sottoMateriaRepository;
    
    @Inject
    private SottoMateriaService sottoMateriaService;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * PUT /materias/disable -> Disable a materia
     */
    @RequestMapping(value = "/sottoMaterias/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableSottoMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable sottomateria : {}", id);
			JsonObject json = new JsonObject();
			try{
				sottoMateriaService.disableSottoMateria(Long.parseLong(id));
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
     * PUT /materias/enable -> Enable a materia
     */
    @RequestMapping(value = "/sottoMaterias/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableSottoMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable sottomateria : {}", id);
			JsonObject json = new JsonObject();
			try{
				sottoMateriaService.enableSottoMateria(Long.parseLong(id));
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
     * POST  /sottoMaterias -> Create a new sottoMateria.
     */
    @RequestMapping(value = "/sottoMaterias",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody SottoMateria sottoMateria) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save SottoMateria : {}", sottoMateria);
	        if (sottoMateria.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new sottoMateria cannot already have an ID").build();
	        }
	        if(sottoMateria.getValidita()==null){
	        	sottoMateria.setValidita(new Validita());
	        }
	        if(sottoMateria.getValidita().getValidodal()==null){
	        	sottoMateria.getValidita().setValidodal(new LocalDate());
	        }
	        sottoMateriaRepository.save(sottoMateria);
	        return ResponseEntity.created(new URI("/api/sottoMaterias/" + sottoMateria.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /sottoMaterias -> Updates an existing sottoMateria.
     */
    @RequestMapping(value = "/sottoMaterias",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody SottoMateria sottoMateria) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update SottoMateria : {}", sottoMateria);
	        if (sottoMateria.getId() == null) {
	            return create(sottoMateria);
	        }
	        sottoMateriaRepository.save(sottoMateria);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sottoMaterias -> get all the sottoMaterias.
     */
    @RequestMapping(value = "/sottoMaterias",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SottoMateria>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException{
    	try{
	    	Page<SottoMateria> page = sottoMateriaRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sottoMaterias", offset, limit);
	        return new ResponseEntity<List<SottoMateria>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sottoMaterias/:id -> get the "id" sottoMateria.
     */
    @RequestMapping(value = "/sottoMaterias/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SottoMateria> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get SottoMateria : {}", id);
	        SottoMateria sottoMateria = sottoMateriaRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (sottoMateria == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countBySottomateriaId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	sottoMateria.setAtti(atti);
	        }
	        return new ResponseEntity<>(sottoMateria, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /sottoMaterias/:id -> delete the "id" sottoMateria.
     */
    @RequestMapping(value = "/sottoMaterias/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete SottoMateria : {}", id);
	        sottoMateriaRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
