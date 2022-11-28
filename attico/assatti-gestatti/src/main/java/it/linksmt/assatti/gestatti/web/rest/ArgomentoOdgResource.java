package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.ArgomentoOdg;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.ArgomentoOdgRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing ArgomentoOdg.
 */
@RestController
@RequestMapping("/api")
public class ArgomentoOdgResource {

    private final Logger log = LoggerFactory.getLogger(ArgomentoOdgResource.class);

    @Inject
    private ArgomentoOdgRepository argomentoOdgRepository;

    /**
     * POST  /argomentoOdgs -> Create a new argomentoOdg.
     */
    @RequestMapping(value = "/argomentoOdgs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody ArgomentoOdg argomentoOdg) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save ArgomentoOdg : {}", argomentoOdg);
	        if (argomentoOdg.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new argomentoOdg cannot already have an ID").build();
	        }
	        argomentoOdgRepository.save(argomentoOdg);
	        return ResponseEntity.created(new URI("/api/argomentoOdgs/" + argomentoOdg.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /argomentoOdgs -> Updates an existing argomentoOdg.
     */
    @RequestMapping(value = "/argomentoOdgs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody ArgomentoOdg argomentoOdg) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update ArgomentoOdg : {}", argomentoOdg);
	        if (argomentoOdg.getId() == null) {
	            return create(argomentoOdg);
	        }
	        argomentoOdgRepository.save(argomentoOdg);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /argomentoOdgs -> get all the argomentoOdgs.
     */
    @RequestMapping(value = "/argomentoOdgs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<ArgomentoOdg> getAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all ArgomentoOdgs");
	        return argomentoOdgRepository.findAll();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /argomentoOdgs/:id -> get the "id" argomentoOdg.
     */
    @RequestMapping(value = "/argomentoOdgs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ArgomentoOdg> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get ArgomentoOdg : {}", id);
	        ArgomentoOdg argomentoOdg = argomentoOdgRepository.findOne(id);
	        if (argomentoOdg == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(argomentoOdg, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /argomentoOdgs/:id -> delete the "id" argomentoOdg.
     */
    @RequestMapping(value = "/argomentoOdgs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ArgomentoOdg : {}", id);
	        argomentoOdgRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
