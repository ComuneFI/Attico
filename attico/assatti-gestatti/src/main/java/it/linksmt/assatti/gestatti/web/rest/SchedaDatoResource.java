package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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

import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.SchedaDatoRepository;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing SchedaDato.
 */
@RestController
@RequestMapping("/api")
public class SchedaDatoResource {

    private final Logger log = LoggerFactory.getLogger(SchedaDatoResource.class);

    @Inject
    private SchedaDatoRepository schedaDatoRepository;

    /**
     * POST  /schedaDatos -> Create a new schedaDato.
     */
    @RequestMapping(value = "/schedaDatos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody SchedaDato schedaDato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save SchedaDato : {}", schedaDato);
	        if (schedaDato.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new schedaDato cannot already have an ID").build();
	        }
	        schedaDatoRepository.save(schedaDato);
	        return ResponseEntity.created(new URI("/api/schedaDatos/" + schedaDato.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /schedaDatos -> Updates an existing schedaDato.
     */
    @RequestMapping(value = "/schedaDatos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody SchedaDato schedaDato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update SchedaDato : {}", schedaDato);
	        if (schedaDato.getId() == null) {
	            return create(schedaDato);
	        }
	        schedaDatoRepository.save(schedaDato);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /schedaDatos -> get all the schedaDatos.
     */
    @RequestMapping(value = "/schedaDatos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<SchedaDato> getAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all SchedaDatos");
	        return schedaDatoRepository.findAll();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /schedaDatos/:id -> get the "id" schedaDato.
     */
    @RequestMapping(value = "/schedaDatos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SchedaDato> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get SchedaDato : {}", id);
	        SchedaDato schedaDato = schedaDatoRepository.findOne(id);
	        if (schedaDato == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(schedaDato, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /schedaDatos/:id -> delete the "id" schedaDato.
     */
    @RequestMapping(value = "/schedaDatos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete SchedaDato : {}", id);
	        String stato = "ok";
	        try{
	        	schedaDatoRepository.delete(id);
	        }catch(Exception e){
	        	stato = "ko";
	        }
	        JsonObject json = new JsonObject();
			json.addProperty("stato", stato);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
