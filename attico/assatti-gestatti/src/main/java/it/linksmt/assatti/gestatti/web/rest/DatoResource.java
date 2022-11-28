package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.Dato;
import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.service.exception.CantDeleteAlreadyUsedObjectException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.DatoRepository;
import it.linksmt.assatti.datalayer.repository.SchedaDatoRepository;
import it.linksmt.assatti.service.DatoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Dato.
 */
@RestController
@RequestMapping("/api")
public class DatoResource {

    private final Logger log = LoggerFactory.getLogger(DatoResource.class);

    @Inject
    private DatoRepository datoRepository;
    
    @Inject
    private SchedaDatoRepository schedaDatoRepository;
    
    @Inject
    private DatoService datoService;

    /**
     * POST  /datos -> Create a new dato.
     */
    @RequestMapping(value = "/datos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Dato dato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Dato : {}", dato);
	        if (dato.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new dato cannot already have an ID").build();
	        }
	        datoRepository.save(dato);
	        return ResponseEntity.created(new URI("/api/datos/" + dato.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /datos -> Updates an existing dato.
     */
    @RequestMapping(value = "/datos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Dato dato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Dato : {}", dato);
	        if (dato.getId() == null) {
	            return create(dato);
	        }
	        datoRepository.save(dato);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /datos -> get all the datos.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/datos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Dato>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "etichetta", required = false) String etichetta,
            @RequestParam(value = "dato_tipdat_fk", required = false) String dato_tipdat_fk,
            @RequestParam(value = "multivalore", required = false) String multivalore) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all Datos");
	        JsonObject jsonSearch = this.buildSearchObject(etichetta, dato_tipdat_fk, multivalore);
	        return datoService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private JsonObject buildSearchObject(String etichetta, String dato_tipdat_fk, String multivalore) throws GestattiCatchedException{
    	try{	
    		JsonObject json = new JsonObject();
	    	json.addProperty("etichetta", etichetta);
	    	json.addProperty("dato_tipdat_fk", dato_tipdat_fk);
	    	json.addProperty("multivalore", multivalore);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /datos/:id -> get the "id" dato.
     */
    @RequestMapping(value = "/datos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Dato> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Dato : {}", id);
	        Dato dato = datoRepository.findOne(id);
	        if (dato == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(dato, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /datos/:id -> delete the "id" dato.
     */
    @RequestMapping(value = "/datos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Dato : {}", id);
	    	Dato dato = datoRepository.findOne(id);
	    	List<SchedaDato> schedeDato = schedaDatoRepository.findByDatoId(id);
	    	if(schedeDato.isEmpty()){	    		
	    		datoRepository.delete(id);
	    	} else {
	    		List<Scheda> schedas = new ArrayList<Scheda>();
	    		for (SchedaDato schedaDato : schedeDato) {
	    			Scheda scheda = schedaDato.getScheda();
	    			if(!schedas.contains(scheda)){	    				
	    				schedas.add(scheda);
	    			}
				}
	    		throw new CantDeleteAlreadyUsedObjectException(dato, schedas);
	    	}
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
