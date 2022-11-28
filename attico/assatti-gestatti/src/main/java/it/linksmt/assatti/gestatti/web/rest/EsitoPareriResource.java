package it.linksmt.assatti.gestatti.web.rest;

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

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.EsitoPareri;
import it.linksmt.assatti.datalayer.domain.QEsitoPareri;
import it.linksmt.assatti.datalayer.repository.EsitoPareriRepository;
import it.linksmt.assatti.datalayer.repository.ParereRepository;
import it.linksmt.assatti.gestatti.web.rest.dto.EsitoPareriSearchDTO;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.EsitoPareriService;
import it.linksmt.assatti.service.exception.CantEditAlreadyUsedEsitoPareriException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing EsitoPareri.
 */
@RestController
@RequestMapping("/api")
public class EsitoPareriResource {

    private final Logger log = LoggerFactory.getLogger(EsitoPareriResource.class);

    @Inject
    private EsitoPareriRepository esitoPareriRepository;

    @Inject
    private EsitoPareriService esitoPareriService;
    
    @Inject
    private ParereRepository parereRepository;
    
    @RequestMapping(value = "/esitoPareris/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.USER)
    @Timed
    public ResponseEntity<Void> disable(
    		@PathVariable(value="id") Long esitoPareriId)
            		throws GestattiCatchedException {
    	try{
    		EsitoPareri esitoPareri = esitoPareriRepository.findOne(esitoPareriId);
    		esitoPareri.setEnabled(false);
    		esitoPareriRepository.save(esitoPareri);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/esitoPareris/{id}/enable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.USER)
    @Timed
    public ResponseEntity<Void> enable(
    		@PathVariable(value="id") Long esitoPareriId)
            		throws GestattiCatchedException {
    	try{
    		EsitoPareri esitoPareri = esitoPareriRepository.findOne(esitoPareriId);
    		esitoPareri.setEnabled(true);
    		esitoPareriRepository.save(esitoPareri);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /esitoPareris -> Create a new esitoPareri.
     */
    @RequestMapping(value = "/esitoPareris",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> create(@Valid @RequestBody EsitoPareri esitoPareri) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save EsitoPareri : {}", esitoPareri);
	        if (esitoPareri.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new esitoPareri cannot already have an ID").build();
	        }
	        if(esitoPareri.getEnabled()==null){
	        	esitoPareri.setEnabled(true);
	        }
	        esitoPareriRepository.save(esitoPareri);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /esitoPareris -> Updates an existing esitoPareri.
     */
    @RequestMapping(value = "/esitoPareris",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> update(@Valid @RequestBody EsitoPareri esitoPareri) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update EsitoPareri : {}", esitoPareri);
	        if (esitoPareri.getId() == null) {
	            return create(esitoPareri);
	        }
	        
	        //controllo che l'esitoParere non sia utilizzato in qualche parere se l'utente ha provato a cambiare il soggetto e il tipo atto
	        
	        EsitoPareri esitoPareriDaDb = esitoPareriService.findOne(esitoPareri.getId());
	        
	        
	        if(esitoPareriDaDb!=null) {
	        	long idTipoAtto = esitoPareri.getTipoAtto()!=null&&esitoPareri.getTipoAtto().getId()!=null?esitoPareri.getTipoAtto().getId().longValue():0;
	        	String tipo = esitoPareri.getTipo()!=null?esitoPareri.getTipo():"";
	        	
	        	long idTipoAttoDaDb = esitoPareriDaDb.getTipoAtto()!=null&&esitoPareriDaDb.getTipoAtto().getId()!=null?esitoPareriDaDb.getTipoAtto().getId().longValue():0;
	        	String tipoDaDb = esitoPareriDaDb.getTipo()!=null?esitoPareriDaDb.getTipo():"";
	        	
	        	if(idTipoAtto!=idTipoAttoDaDb || !tipo.equalsIgnoreCase(tipoDaDb)) {
	        		
	        		Long nPareri = parereRepository.countByParereSintetico(esitoPareri.getCodice()); 
	        		
	        		if(nPareri!=null && nPareri.longValue()>0) {
	        			throw new CantEditAlreadyUsedEsitoPareriException(esitoPareriDaDb);
	        		}
	        	}
	        }
	        
	        
	        esitoPareriRepository.save(esitoPareri);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private EsitoPareriSearchDTO buildEsitoPareriSearchDTO(String id, String codice, String tipo, String tipoAtto, String valore) throws GestattiCatchedException{
    	try{
	    	EsitoPareriSearchDTO search = new EsitoPareriSearchDTO();
	    	search.setId(id);
	    	search.setCodice(codice);
	    	search.setTipo(tipo);
	    	search.setTipoAtto(tipoAtto);
	    	search.setValore(valore);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /esitoPareris/getAllEnabled -> get all the esitoPareris enabled
     */
    @RequestMapping(value = "/esitoPareris/getAllEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EsitoPareri>> getAllEnabled()
                                		  throws GestattiCatchedException {
    	try{
	    	List<EsitoPareri> esitiPareri = esitoPareriService.findAllEnabled();
	        return new ResponseEntity<List<EsitoPareri>>(esitiPareri, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /esitoPareris -> get all the esitoPareris.
     */
    @RequestMapping(value = "/esitoPareris",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<EsitoPareri>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "idEsitoPareri", required = false) String id,
                                  @RequestParam(value = "codice", required = false) String codice,
                                  @RequestParam(value = "tipoEsitoPareri", required = false) String tipo,
                                  @RequestParam(value = "tipoAtto", required = false) String tipoAtto,
                                  @RequestParam(value = "valore", required = false) String valore
                                  )
                                		  throws GestattiCatchedException {
    	try{
	    	EsitoPareriSearchDTO search = this.buildEsitoPareriSearchDTO(id, codice, tipo, tipoAtto, valore);
	    	BooleanExpression predicateEsitoPareri = QEsitoPareri.esitoPareri.id.isNotNull();
			if(search!=null){
				Long idLong = null;
				if(search.getId()!=null && !"".equals(search.getId().trim())){
					try{
						idLong = Long.parseLong(search.getId().trim());
					}catch(Exception e){};
				}
				if(idLong!=null){
					predicateEsitoPareri = predicateEsitoPareri.and(QEsitoPareri.esitoPareri.id.eq(idLong));
				}
				if(search.getCodice()!=null && !"".equals(search.getCodice().trim())){
					predicateEsitoPareri = predicateEsitoPareri.and(QEsitoPareri.esitoPareri.codice.containsIgnoreCase(search.getCodice().trim()));
				}
				if(search.getTipo()!=null && !"".equals(search.getTipo().trim())){
					predicateEsitoPareri = predicateEsitoPareri.and(QEsitoPareri.esitoPareri.tipo.containsIgnoreCase(search.getTipo().trim()));
				}
				if(search.getValore()!=null && !"".equals(search.getValore().trim())){
					predicateEsitoPareri = predicateEsitoPareri.and(QEsitoPareri.esitoPareri.valore.containsIgnoreCase(search.getValore().trim()));
				}
				if(search.getTipoAtto()!=null && !"".equals(search.getTipoAtto().trim())){
					predicateEsitoPareri = predicateEsitoPareri.and(QEsitoPareri.esitoPareri.tipoAtto.descrizione.containsIgnoreCase(search.getTipoAtto().trim()));
				}
				
			}
	        Page<EsitoPareri> page = esitoPareriRepository.findAll(predicateEsitoPareri, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/esitoPareris", offset, limit);
	        return new ResponseEntity<List<EsitoPareri>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /esitoPareris/:id -> get the "id" esitoPareri.
     */
    @RequestMapping(value = "/esitoPareris/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<EsitoPareri> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get EsitoPareri : {}", id);
	        EsitoPareri esitoPareri = esitoPareriService.findOne(id);
	        if (esitoPareri == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(esitoPareri, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * GET  /esitoPareris/getDescriptionByCode/:codice -> get description from "codice" of esitoPareri.
     */
    @RequestMapping(value = "/esitoPareris/getDescriptionByCode/{codice}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getDescriptionByCode(@PathVariable String codice, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get EsitoPareri description by code: {}", codice);
	        String description = esitoPareriService.getDescrizioneByCodiceEsitoPareri(codice);
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
     * DELETE  /esitoPareris/:id -> delete the "id" esitoPareri.
     */
    @RequestMapping(value = "/esitoPareris/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete EsitoPareri : {}", id);
	        esitoPareriRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
