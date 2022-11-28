package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.CategoriaEvento;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.CategoriaEventoRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing CategoriaEvento.
 */
@RestController
@RequestMapping("/api")
public class CategoriaEventoResource {

    @Inject
    private CategoriaEventoRepository categoriaEventoRepository;
    
    
    /**
     * GET  /categoriaEvento -> get all the categoriaEvento.
     */
    @RequestMapping(value = "/categoriaEvento",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<CategoriaEvento> getAll() throws GestattiCatchedException {
    	try{
	    	return categoriaEventoRepository.findAllByOrderByOrdineAsc();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
