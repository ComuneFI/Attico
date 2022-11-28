package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.ArrayList;
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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AssessoratoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.AssessoratoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.dto.AssessoratoSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Assessorato.
 */
@RestController
@RequestMapping("/api")
public class AssessoratoResource {

    private final Logger log = LoggerFactory.getLogger(AssessoratoResource.class);

    @Inject
    private AssessoratoRepository assessoratoRepository;
    
    @Inject
    private AssessoratoService assessoratoService;

    
    /**
     * GET  /assessoratos -> get all the assessoratos.
     */
    @RequestMapping(value = "/assessoratos/profili",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Profilo>> getAllProfili()
    	throws GestattiCatchedException {
    	try{
    		List<Profilo> page = new ArrayList<Profilo>();
    		List<Assessorato> ass = assessoratoService.findAll();
    		for(Assessorato assessorato : ass){
    			page.add(assessorato.getProfiloResponsabile());
    		}
    		
	        return new ResponseEntity<Iterable<Profilo>>(page ,  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /assessoratos -> Create a new assessorato.
     */
    @RequestMapping(value = "/assessoratos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody Assessorato assessorato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Assessorato : {}", assessorato);
	        if (assessorato.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new assessorato cannot already have an ID").build();
	        }
	        assessoratoService.save(assessorato);
	        return ResponseEntity.created(new URI("/api/assessoratos/" + assessorato.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /assessoratos -> Updates an existing assessorato.
     */
    @RequestMapping(value = "/assessoratos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Assessorato assessorato) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Assessorato : {}", assessorato);
	        if (assessorato.getId() == null) {
	            return create(assessorato);
	        }
	        assessoratoService.save(assessorato);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private AssessoratoSearchDTO buildAssessoratoSearchDTO(String id, String codice, String responsabile, String denominazione, String qualifica, String stato) throws GestattiCatchedException{
    	try{
	    	AssessoratoSearchDTO search = new AssessoratoSearchDTO();
	    	search.setId(id);
	    	search.setCodice(codice);
	    	search.setResponsabile(responsabile);
	    	search.setDenominazione(denominazione);
	    	search.setQualifica(qualifica);
	    	search.setStato(stato);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
     * GET  /assessoratos -> get all the assessoratos.
     */
    @RequestMapping(value = "/assessoratos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Assessorato>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "idAssessorato" , required = false) String id,
            @RequestParam(value = "codice" , required = false) String codice,
            @RequestParam(value = "responsabile" , required = false) String responsabile,
            @RequestParam(value = "denominazione" , required = false) String denominazione,
            @RequestParam(value = "qualifica" , required = false) String qualifica,
            @RequestParam(value = "stato" , required = false) String stato
    		)
    	throws GestattiCatchedException {
    	try{
	    	AssessoratoSearchDTO search = this.buildAssessoratoSearchDTO(id, codice, responsabile, denominazione, qualifica, stato);
	        Page<Assessorato> page = assessoratoService.findAll(search, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/assessoratos", offset, limit);
	        return new ResponseEntity<List<Assessorato>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /assessoratos/:id -> get the "id" assessorato.
     */
    @RequestMapping(value = "/assessoratos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Assessorato> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Assessorato : {}", id);
	        Assessorato assessorato = assessoratoService.findOne(id);
	        if (assessorato == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(assessorato, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
	 * PUT /assessoratos/:id/disable -> disable assessorato with id
	 */
	@RequestMapping(value = "/assessoratos/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<String> disableUtente(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to disable assessorato : {}", id);
			JsonObject json = new JsonObject();
			try{
				assessoratoService.disableAssessorato(Long.parseLong(id));
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
	 * PUT /assessoratos/:id/enable -> enable utente with id
	 */
	@RequestMapping(value = "/assessoratos/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<String> enableUtente(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to enable assessorato : {}", id);
			JsonObject json = new JsonObject();
			try{
				assessoratoService.enableAssessorato(Long.parseLong(id));
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
     * DELETE  /assessoratos/:id -> delete the "id" assessorato.
     */
    @RequestMapping(value = "/assessoratos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Assessorato : {}", id);
	        assessoratoRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
