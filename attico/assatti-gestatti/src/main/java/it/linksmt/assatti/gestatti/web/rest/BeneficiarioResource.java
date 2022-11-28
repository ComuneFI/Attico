package it.linksmt.assatti.gestatti.web.rest;

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

import it.linksmt.assatti.datalayer.domain.Beneficiario;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.BeneficiarioRepository;
import it.linksmt.assatti.service.BeneficiarioService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Beneficiario.
 */
@RestController
@RequestMapping("/api")
public class BeneficiarioResource {

    private final Logger log = LoggerFactory.getLogger(BeneficiarioResource.class);

    @Inject
    private BeneficiarioRepository beneficiarioRepository;
    
    @Inject
    private BeneficiarioService beneficiarioService;

    /**
     * POST  /beneficiarios -> Create a new beneficiario.
     */
    @RequestMapping(value = "/beneficiarios",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void create(@RequestBody Beneficiario beneficiario) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to save Beneficiario : {}", beneficiario);
	        beneficiarioRepository.save(beneficiario);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /beneficiarios/trascriviBeneficiari -> Restituisce beneficiari e fatture in html.
     */
    @RequestMapping(value = "/beneficiarios/htmlFromBeneficiari",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> htmlFromBeneficiari(@RequestBody List<Beneficiario> beneficiari) throws GestattiCatchedException{
    	try{
    		String html = beneficiarioService.getHtmlFromBeneficiari(beneficiari);
    		JsonObject json = new JsonObject();
			json.addProperty("html", html);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /beneficiarios -> get all the beneficiarios.
     */
    @RequestMapping(value = "/beneficiarios",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Beneficiario> getAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all Beneficiarios");
	        return beneficiarioRepository.findAll();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /beneficiarios/:id -> get the "id" beneficiario.
     */
    @RequestMapping(value = "/beneficiarios/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Beneficiario> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Beneficiario : {}", id);
	        Beneficiario beneficiario = beneficiarioRepository.findOne(id);
	        if (beneficiario == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(beneficiario, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /beneficiarios/:id -> delete the "id" beneficiario.
     */
    @RequestMapping(value = "/beneficiarios/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Beneficiario : {}", id);
	        beneficiarioRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
}
