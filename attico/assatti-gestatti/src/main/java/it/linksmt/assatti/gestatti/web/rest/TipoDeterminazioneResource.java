package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.TipoDeterminazione;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoDeterminazioneRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoDeterminazioneService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing TipoDeterminazione.
 */
@RestController
@RequestMapping("/api")
public class TipoDeterminazioneResource {

    private final Logger log = LoggerFactory.getLogger(TipoDeterminazioneResource.class);

    @Inject
    private TipoDeterminazioneRepository tipoDeterminazioneRepository;
    
    @Inject
    private TipoDeterminazioneService tipoDeterminazioneService;
    
    @Inject
    private AttoRepository attoRepository;

    /**
     * POST  /tipoDeterminaziones -> Create a new tipoDeterminazione.
     */
    @RequestMapping(value = "/tipoDeterminaziones",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody TipoDeterminazione tipoDeterminazione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoDeterminazione : {}", tipoDeterminazione);
	        if (tipoDeterminazione.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoDeterminazione cannot already have an ID").build();
	        }
	        tipoDeterminazione.setEnabled(Boolean.TRUE);
	        tipoDeterminazioneRepository.save(tipoDeterminazione);
	        return ResponseEntity.created(new URI("/api/tipoDeterminaziones/" + tipoDeterminazione.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoDeterminaziones -> Updates an existing tipoDeterminazione.
     */
    @RequestMapping(value = "/tipoDeterminaziones",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody TipoDeterminazione tipoDeterminazione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoDeterminazione : {}", tipoDeterminazione);
	        if (tipoDeterminazione.getId() == null) {
	            return create(tipoDeterminazione);
	        }
	        tipoDeterminazioneRepository.save(tipoDeterminazione);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoDeterminaziones -> get all the tipoDeterminaziones.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/tipoDeterminaziones",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoDeterminazione>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "tipoAtto", required = false) final String tipoAtto,
            @RequestParam(value = "fileVisibiliInTrasparenza", required = false) final String fileVisibiliInTrasparenza,
			@RequestParam(value = "statoTrasparenza", required = false) String statoTrasparenza)  throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all TipoDeterminaziones");
	        JsonObject jsonSearch = this.buildSearchObject(descrizione,statoTrasparenza,tipoAtto,fileVisibiliInTrasparenza);
	        return tipoDeterminazioneService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String descrizione, String statoTrasparenza, String tipoAtto, String fileVisibiliInTrasparenza) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("statoTrasparenza", statoTrasparenza);
	    	json.addProperty("fileVisibiliInTrasparenza", fileVisibiliInTrasparenza);
	    	json.addProperty("tipoAtto", tipoAtto);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoDeterminaziones/:id -> get the "id" tipoDeterminazione.
     */
    @RequestMapping(value = "/tipoDeterminaziones/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoDeterminazione> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get TipoDeterminazione : {}", id);
	        TipoDeterminazione tipoDeterminazione = tipoDeterminazioneRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (tipoDeterminazione == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByTipodeterminazioneId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	tipoDeterminazione.setAtti(atti);
	        }
	        return new ResponseEntity<>(tipoDeterminazione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoDeterminaziones/:id -> delete the "id" tipoDeterminazione.
     */
    @RequestMapping(value = "/tipoDeterminaziones/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete TipoDeterminazione : {}", id);
	        tipoDeterminazioneRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/tipoDeterminaziones/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> disable(
    		@PathVariable(value="id") Long tipoDeterminazioneId)
            		throws GestattiCatchedException {
    	try{
    		TipoDeterminazione tipoDeterminazione = tipoDeterminazioneRepository.findOne(tipoDeterminazioneId);
    		tipoDeterminazione.setEnabled(false);
    		tipoDeterminazioneRepository.save(tipoDeterminazione);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/tipoDeterminaziones/{id}/enable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> enable(
    		@PathVariable(value="id") Long tipoDeterminazioneId)
            		throws GestattiCatchedException {
    	try{
    		TipoDeterminazione tipoDeterminazione = tipoDeterminazioneRepository.findOne(tipoDeterminazioneId);
    		tipoDeterminazione.setEnabled(true);
    		tipoDeterminazioneRepository.save(tipoDeterminazione);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
}
