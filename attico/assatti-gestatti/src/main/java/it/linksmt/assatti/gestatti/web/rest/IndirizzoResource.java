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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.IndirizzoRepository;
import it.linksmt.assatti.service.IndirizzoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.dto.IndirizzoDTO;
import it.linksmt.assatti.service.dto.IndirizzoSearchDTO;

/**
 * REST controller for managing Indirizzo.
 */
@RestController
@RequestMapping("/api")
public class IndirizzoResource {

    private final Logger log = LoggerFactory.getLogger(IndirizzoResource.class);

    @Inject
    private IndirizzoRepository indirizzoRepository;
    
    @Inject
    private IndirizzoService indirizzoService;

    /**
     * POST  /indirizzos -> Create a new indirizzo.
     */
    @RequestMapping(value = "/indirizzos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Indirizzo indirizzo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Indirizzo : {}", indirizzo);
	        if (indirizzo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new indirizzo cannot already have an ID").build();
	        }
	        indirizzoRepository.save(indirizzo);
	        return ResponseEntity.created(new URI("/api/indirizzos/" + indirizzo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /indirizzos -> Updates an existing indirizzo.
     */
    @RequestMapping(value = "/indirizzos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Indirizzo indirizzo) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update Indirizzo : {}", indirizzo);
	        if (indirizzo.getId() == null) {
	            return create(indirizzo);
	        }
	        indirizzoRepository.save(indirizzo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private IndirizzoSearchDTO buildIndirizzoSearchDTO(String id, String dug, String toponimo, String civico, String cap, String comune, String provincia, String stato) throws GestattiCatchedException{
    	try{
	    	IndirizzoSearchDTO search = new IndirizzoSearchDTO();
	    	search.setId(id);
	    	search.setDug(dug);
	    	search.setToponimo(toponimo);
	    	search.setCivico(civico);
	    	search.setCap(cap);
	    	search.setComune(comune);
	    	search.setProvincia(provincia);
	    	search.setStato(stato);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT /indirizzos/disable -> Disable a indirizzo 
     */
    @RequestMapping(value = "/indirizzos/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableIndirizzo(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to disable indirizzo : {}", id);
			JsonObject json = new JsonObject();
			try{
				indirizzoService.disableIndirizzo(Long.parseLong(id));
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
     * PUT /indirizzos/enable -> Enable a indirizzo
     */
    @RequestMapping(value = "/indirizzos/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableIndirizzo(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to enable indirizzo : {}", id);
			JsonObject json = new JsonObject();
			try{
				indirizzoService.enableIndirizzo(Long.parseLong(id));
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
     * GET  /indirizzos -> get all the indirizzos.
     */
    @RequestMapping(value = "/indirizzos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<IndirizzoDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "dug", required = false) String dug,
                                  @RequestParam(value = "toponimo", required = false) String toponimo,
                                  @RequestParam(value = "civico", required = false) String civico,
                                  @RequestParam(value = "cap", required = false) String cap,
                                  @RequestParam(value = "comune", required = false) String comune,
                                  @RequestParam(value = "provincia", required = false) String provincia,
                                  @RequestParam(value = "idIndirizzo", required = false) String id,
                                  @RequestParam(value = "onlyEnabled", required = false) Boolean onlyEnabled,
                                  @RequestParam(value = "stato" , required = false) String stato
                                  )
                                		  throws GestattiCatchedException {
    	try{
    		IndirizzoSearchDTO search = this.buildIndirizzoSearchDTO(id, dug, toponimo, civico, cap, comune, provincia, stato);
    	
    		return indirizzoService.getAll(search, onlyEnabled, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /indirizzos/:id -> get the "id" indirizzo.
     */
    @RequestMapping(value = "/indirizzos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Indirizzo> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Indirizzo : {}", id);
	        Indirizzo indirizzo = indirizzoRepository.findOne(id);
	        if (indirizzo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(indirizzo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /indirizzos/:id -> delete the "id" indirizzo.
     */
    @RequestMapping(value = "/indirizzos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Indirizzo : {}", id);
	        indirizzoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
