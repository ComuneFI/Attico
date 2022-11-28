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

import it.linksmt.assatti.datalayer.domain.Materia;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.MateriaRepository;
import it.linksmt.assatti.service.MateriaService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Materia.
 */
@RestController
@RequestMapping("/api")
public class MateriaResource {

    private final Logger log = LoggerFactory.getLogger(MateriaResource.class);

    @Inject
    private MateriaRepository materiaRepository;
    
    @Inject
    private MateriaService materiaService;
    
    @Inject
    private AttoRepository attoRepository;

    /**
     * PUT /materias/disable -> Disable a materia
     */
    @RequestMapping(value = "/materias/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable materia : {}", id);
			JsonObject json = new JsonObject();
			try{
				materiaService.disableMateria(Long.parseLong(id));
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
    @RequestMapping(value = "/materias/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable materia : {}", id);
			JsonObject json = new JsonObject();
			try{
				materiaService.enableMateria(Long.parseLong(id));
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
     * POST  /materias -> Create a new materia.
     */
    @RequestMapping(value = "/materias",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Materia materia) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Materia : {}", materia);
	        if (materia.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new materia cannot already have an ID").build();
	        }
	        if(materia.getValidita()==null){
	        	materia.setValidita(new Validita());
	        }
	        if(materia.getValidita().getValidodal()==null){
	        	materia.getValidita().setValidodal(new LocalDate());
	        }
	        materiaRepository.save(materia);
	        return ResponseEntity.created(new URI("/api/materias/" + materia.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /materias -> Updates an existing materia.
     */
    @RequestMapping(value = "/materias",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Materia materia) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Materia : {}", materia);
	        if (materia.getId() == null) {
	            return create(materia);
	        }
	        materiaRepository.save(materia);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /materias -> get all the materias.
     */
    @RequestMapping(value = "/materias",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Materia>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Materia> page = materiaRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/materias", offset, limit);
	        return new ResponseEntity<List<Materia>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /materias/:id -> get the "id" materia.
     */
    @RequestMapping(value = "/materias/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Materia> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Materia : {}", id);
	        Materia materia = materiaRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (materia == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByMateriaId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	materia.setAtti(atti);
	        }
	        
	        return new ResponseEntity<>(materia, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /materias/:id -> delete the "id" materia.
     */
    @RequestMapping(value = "/materias/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Materia : {}", id);
	        materiaRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
