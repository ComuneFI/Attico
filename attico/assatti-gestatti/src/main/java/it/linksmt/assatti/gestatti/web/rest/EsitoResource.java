package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.service.EsitoService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing Esito.
 */
@RestController
@RequestMapping("/api")
public class EsitoResource {

	private final Logger log = LoggerFactory.getLogger(EsitoResource.class);

	@Inject
	private EsitoService esitoService;

	
	/**
	 * GET /esitos -> get all the esito.
	 */
	@RequestMapping(value = "/esitos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Esito>> getAll()
			throws GestattiCatchedException {
		try{
			List<Esito> page = esitoService.findAll();
			return new ResponseEntity<List<Esito>>(page,HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
}
