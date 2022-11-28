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
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.RuoloService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.gestatti.web.rest.dto.RuoloSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Ruolo.
 */
@RestController
@RequestMapping("/api")
public class RuoloResource {

    private final Logger log = LoggerFactory.getLogger(RuoloResource.class);

    @Inject
    private RuoloRepository ruoloRepository;

    @Inject
    private RuoloService ruoloService;
    
    /**
     * GET  /ruolos/{id}/enable
     */
    @RequestMapping(value = "/ruolos/{id}/enable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> enable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable ruolo ", id);
	    	ruoloService.enable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /ruolos/{id}/disable
     */
    @RequestMapping(value = "/ruolos/{id}/disable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> disable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable ruolo ", id);
	    	ruoloService.disable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /ruolos -> Create a new ruolo.
     */
    @RequestMapping(value = "/ruolos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody Ruolo ruolo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Ruolo : {}", ruolo);
	        if (ruolo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new ruolo cannot already have an ID").build();
	        }
	        if(ruolo.getEnabled()==null){
	        	ruolo.setEnabled(true);
	        }
	        ruoloRepository.save(ruolo);
	        return ResponseEntity.created(new URI("/api/ruolos/" + ruolo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /ruolos -> Updates an existing ruolo.
     */
    @RequestMapping(value = "/ruolos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> update(@Valid @RequestBody Ruolo ruolo) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update Ruolo : {}", ruolo);
	        if (ruolo.getId() == null) {
	            return create(ruolo);
	        }
	        ruoloRepository.save(ruolo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private RuoloSearchDTO buildRuoloSearchDTO(String id, String codice, String descrizione, Boolean qualifica, String tipo) throws GestattiCatchedException{
    	try{
	    	RuoloSearchDTO search = new RuoloSearchDTO();
	    	search.setId(id);
	    	search.setCodice(codice);
	    	search.setDescrizione(descrizione);
	    	search.setQualifica(qualifica);
	    	search.setTipo(tipo);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /ruolos/getAllEnabled -> get all the ruolos enabled
     */
    @RequestMapping(value = "/ruolos/getAllEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ruolo>> getAllEnabled()
                                		  throws GestattiCatchedException {
    	try{
	    	List<Ruolo> ruoliAttivi = ruoloService.findAllEnabled();
	        return new ResponseEntity<List<Ruolo>>(ruoliAttivi, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
	 * GET /ruolos/:id/isRoleUsedInGruppoRuolo -> check if role is already used in a grupporuolo
	 */

	@RequestMapping(value = "/ruolos/{id}/isRoleUsedInGruppoRuolo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> isRoleUsedInGruppoRuolo(
			@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to check isRoleUsedInGruppoRuolo : {}", id);
			boolean used = ruoloService.isRoleUsedInGruppoRuolo(id);
			JsonObject json = new JsonObject();
			json.addProperty("isUsed", used);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

    /**
     * GET  /ruolos -> get all the ruolos.
     */
    @RequestMapping(value = "/ruolos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Ruolo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "idRuolo", required = false) String id,
                                  @RequestParam(value = "codiceRuolo", required = false) String codice,
                                  @RequestParam(value = "descrizione", required = false) String descrizione,
                                  @RequestParam(value = "tipo", required = false) String tipo)
                                		  throws GestattiCatchedException {
    	try{
	    	RuoloSearchDTO search = this.buildRuoloSearchDTO(id, codice, descrizione, null, tipo);
	    	BooleanExpression predicateRuolo = QRuolo.ruolo.id.isNotNull();
			if(search!=null){
				Long idLong = null;
				if(search.getId()!=null && !"".equals(search.getId().trim())){
					try{
						idLong = Long.parseLong(search.getId().trim());
					}catch(Exception e){};
				}
				if(idLong!=null){
					predicateRuolo = predicateRuolo.and(QRuolo.ruolo.id.eq(idLong));
				}
				if(search.getCodice()!=null && !"".equals(search.getCodice().trim())){
					predicateRuolo = predicateRuolo.and(QRuolo.ruolo.codice.containsIgnoreCase(search.getCodice().trim()));
				}
				if(search.getDescrizione()!=null && !"".equals(search.getDescrizione().trim())){
					predicateRuolo = predicateRuolo.and(QRuolo.ruolo.descrizione.containsIgnoreCase(search.getDescrizione().trim()));
				}
				if(search.getQualifica()!=null){
					predicateRuolo = predicateRuolo.and(QRuolo.ruolo.haqualifica.eq(search.getQualifica()));
				}
				if(search.getTipo()!=null && !search.getTipo().trim().isEmpty()) {
					predicateRuolo = predicateRuolo.and(QRuolo.ruolo.tipo.stringValue().upper().eq(search.getTipo().trim().toUpperCase()));
				}
			}
	        Page<Ruolo> page = ruoloRepository.findAll(predicateRuolo, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ruolos", offset, limit);
	        return new ResponseEntity<List<Ruolo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ruolos/:id -> get the "id" ruolo.
     */
    @RequestMapping(value = "/ruolos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Ruolo> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Ruolo : {}", id);
	        Ruolo ruolo = ruoloService.findOne(id);
	        if (ruolo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(ruolo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * GET  /ruolos/getDescriptionByCode/:codice -> get description from "codice" of ruolo.
     */
    @RequestMapping(value = "/ruolos/getDescriptionByCode/{codice}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getDescriptionByCode(@PathVariable String codice, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Ruolo description by code: {}", codice);
	        String description = ruoloService.getDescrizioneByCodiceRuolo(codice);
	        if (description == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        JsonObject json = new JsonObject();
			try{
				json.addProperty("descrizione", description);
			}catch(Exception e){
				json.addProperty("descrizione", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * DELETE  /ruolos/:id -> delete the "id" ruolo.
     */
    @RequestMapping(value = "/ruolos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Ruolo : {}", id);
	        ruoloRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
