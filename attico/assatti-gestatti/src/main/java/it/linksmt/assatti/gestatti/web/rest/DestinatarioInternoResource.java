package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.IDestinatarioInterno;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.DestinatarioInternoService;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing DestinatarioInterno.
 */
@RestController
@RequestMapping("/api")
public class DestinatarioInternoResource {

	private final Logger log = LoggerFactory.getLogger(DestinatarioInternoResource.class);

	@Inject
	private DestinatarioInternoService destinatarioInternoService;


	/**
  	 * GET/destinatariointernos/
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/destinatariointernos", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<Set<IDestinatarioInterno>> getAllDestinatariInterni(
  			@RequestParam(value = "page" , required = true) final Integer offset,
  			@RequestParam(value = "per_page", required = true) final Integer limit,
  			@RequestParam(value = "tipoDestinatario", required = true) final String tipoDestinatario,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get all destinatariointernos of type specified");
	
			Set<IDestinatarioInterno> destinatari = destinatarioInternoService.findAllByTipo(tipoDestinatario, offset, limit);
			if (destinatari == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
			return new ResponseEntity<Set<IDestinatarioInterno>>(destinatari, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}

	
}
