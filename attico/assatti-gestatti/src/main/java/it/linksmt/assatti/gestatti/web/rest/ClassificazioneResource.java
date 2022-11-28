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

import it.linksmt.assatti.datalayer.domain.Classificazione;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.ClassificazioneService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Classificazione.
 */
@RestController
@RequestMapping("/api")
public class ClassificazioneResource {

    private final Logger log = LoggerFactory.getLogger(ClassificazioneResource.class);

    @Inject
    private ClassificazioneService classificazioneService;
    
    /**
     * PUT /classificaziones/disable -> Disable an classificazione
     */
    @RequestMapping(value = "/classificaziones/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<String> disableClassificazione(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable Classificazione : {}", id);
			JsonObject json = new JsonObject();
			try{
				classificazioneService.disableClassificazione(Long.parseLong(id));
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
     * PUT /classificaziones/enable -> Enable an classificazione
     */
    @RequestMapping(value = "/classificaziones/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<String> enableClassificazione(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable Classificazione : {}", id);
			JsonObject json = new JsonObject();
			try{
				classificazioneService.enableClassificazione(Long.parseLong(id));
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
     * POST  /classificaziones -> Create a new classificazione.
     */
    @RequestMapping(value = "/classificaziones",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Classificazione classificazione) throws URISyntaxException {
        log.debug("REST request to save Classificazione : {}", classificazione);
        if (classificazione.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new classificazione cannot already have an ID").build();
        }
        classificazioneService.save(classificazione);
        return ResponseEntity.created(new URI("/api/classificaziones/" + classificazione.getId())).build();
    }

    /**
     * PUT  /classificaziones -> Updates an existing classificazione.
     */
    @RequestMapping(value = "/classificaziones",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Classificazione classificazione) throws URISyntaxException {
        log.debug("REST request to update Classificazione : {}", classificazione);
        if (classificazione.getId() == null) {
            return create(classificazione);
        }
        classificazioneService.save(classificazione);
        return ResponseEntity.ok().build();
    }

    /**
     * GET  /classificaziones -> get all the classificaziones.
     */
    @RequestMapping(value = "/classificaziones",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Classificazione> getAll(
    		@RequestParam(value = "idTitolario", required = false) String idTitolario,
    		@RequestParam(value = "idVoceTitolario", required = false) String idVoceTitolario,
    		@RequestParam(value = "tipoAtto", required = false) Long tipoAtto,
    		@RequestParam(value = "aoo", required = false) Long aoo,
    		@RequestParam(value = "tipoDocumentoSerie", required = false) Long tipoDocumentoSerie) throws GestattiCatchedException {
        log.debug("REST request to get all Classificaziones");
        return classificazioneService.findAll(idTitolario, idVoceTitolario, tipoAtto, aoo, tipoDocumentoSerie);
    }

    /**
     * GET  /classificaziones/:id -> get the "id" classificazione.
     */
    @RequestMapping(value = "/classificaziones/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Classificazione> get(@PathVariable Long id, HttpServletResponse response) {
        log.debug("REST request to get Classificazione : {}", id);
        Classificazione classificazione = classificazioneService.findOne(id);
        if (classificazione == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(classificazione, HttpStatus.OK);
    }

}
