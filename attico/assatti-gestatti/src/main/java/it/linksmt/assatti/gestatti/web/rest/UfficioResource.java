package it.linksmt.assatti.gestatti.web.rest;

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

import it.linksmt.assatti.datalayer.domain.Ufficio;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.UfficioRepository;
import it.linksmt.assatti.service.UfficioService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.dto.UfficioSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Ufficio.
 */
@RestController
@RequestMapping("/api")
public class UfficioResource {

    private final Logger log = LoggerFactory.getLogger(UfficioResource.class);

    @Inject
    private UfficioRepository ufficioRepository;
    
    @Inject
    private UfficioService ufficioService;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * PUT /ufficios/disable -> Disable an office
     */
    @RequestMapping(value = "/ufficios/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableUfficio(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable ufficio : {}", id);
			JsonObject json = new JsonObject();
			try{
				ufficioService.disableUfficio(Long.parseLong(id));
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
     * PUT /ufficios/enable -> Enable an office
     */
    @RequestMapping(value = "/ufficios/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableUfficio(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable ufficio : {}", id);
			JsonObject json = new JsonObject();
			try{
				ufficioService.enableUfficio(Long.parseLong(id));
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
     * POST  /ufficios -> Create a new ufficio.
     */
    @RequestMapping(value = "/ufficios",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Ufficio ufficio) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Ufficio : {}", ufficio);
	        if (ufficio.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new ufficio cannot already have an ID").build();
	        }
	        ufficioRepository.save(ufficio);
	        return ResponseEntity.created(new URI("/api/ufficios/" + ufficio.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /ufficios -> Updates an existing ufficio.
     */
    @RequestMapping(value = "/ufficios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Ufficio ufficio) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Ufficio : {}", ufficio);
	        if (ufficio.getId() == null) {
	            return create(ufficio);
	        }
	        ufficioRepository.save(ufficio);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private UfficioSearchDTO buildUfficioSearchDTO(String id, String codice, String descrizione, String email, String pec, String responsabile, String aoo, String stato) throws GestattiCatchedException{
    	try{
	    	UfficioSearchDTO search = new UfficioSearchDTO();
	    	search.setId(id);
	    	search.setCodice(codice);
	    	search.setDescrizione(descrizione);
	    	search.setEmail(email);
	    	search.setPec(pec);
	    	search.setResponsabile(responsabile);
	    	search.setAoo(aoo);
	    	search.setStato(stato);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ufficios -> get all the ufficios.
     */
    @RequestMapping(value = "/ufficios",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ufficio>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "idUfficio", required = false) String id,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "email", required = false) String email,
            @RequestParam(value = "pec", required = false) String pec,
            @RequestParam(value = "responsabile", required = false) String responsabile,
            @RequestParam(value = "aoo", required = false) String aoo,
            @RequestParam(value = "stato", required = false) String stato)
            		throws GestattiCatchedException {
    	try{
	    	UfficioSearchDTO search = this.buildUfficioSearchDTO(id, codice, descrizione, email, pec, responsabile, aoo, stato);
	    	Page<Ufficio> page = ufficioService.searchUfficioByDto(search, offset, limit);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ufficios", offset, limit);
	        return new ResponseEntity<List<Ufficio>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ufficios -> getByAooId the ufficios.
     */
    @RequestMapping(value = "/ufficios/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ufficio>> getByAooId( @RequestParam(value = "aooId" , required = true) Long aooId)
    		throws GestattiCatchedException{
    	try{
	    	  List<Ufficio> page = ufficioRepository.findByAooId(aooId);
	    	  return new ResponseEntity<List<Ufficio>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /ufficios/:id -> get the "id" ufficio.
     */
    @RequestMapping(value = "/ufficios/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ufficio> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Ufficio : {}", id);
	        Ufficio ufficio = ufficioRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (ufficio == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByUfficioId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	ufficio.setAtti(atti);
	        }
	        return new ResponseEntity<>(ufficio, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /ufficios/:id -> delete the "id" ufficio.
     */
    @RequestMapping(value = "/ufficios/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Ufficio : {}", id);
	        ufficioRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
