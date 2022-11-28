package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.CampoTipoAttoService;
import it.linksmt.assatti.service.SezioneTipoAttoService;
import it.linksmt.assatti.service.TipoAttoService;
import it.linksmt.assatti.service.converter.TipoAttoConverter;
import it.linksmt.assatti.service.dto.CampoTipoAttoDto;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;
import it.linksmt.assatti.service.dto.TipoAttoDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing TipoAtto.
 */
@RestController
@RequestMapping("/api")
public class TipoAttoResource {

    private final Logger log = LoggerFactory.getLogger(TipoAttoResource.class);

    @Inject
    private TipoAttoRepository tipoAttoRepository;

    @Inject
    private TipoAttoService tipoAttoService;
    
    @Autowired
    private CampoTipoAttoService campoTipoAttoService;
    
    @Autowired
    private SezioneTipoAttoService sezioneTipoAttoService;
    
    @Inject
    private AttoRepository attoRepository;

    @RequestMapping(value = "/tipoAttos/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> disable(
    		@PathVariable(value="id") Long tipoAttoId)
            		throws GestattiCatchedException {
    	try{
    		TipoAtto tipoAtto = tipoAttoRepository.findOne(tipoAttoId);
    		tipoAtto.setEnabled(false);
    		tipoAttoRepository.save(tipoAtto);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/tipoAttos/{id}/enable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> enable(
    		@PathVariable(value="id") Long tipoAttoId)
            		throws GestattiCatchedException {
    	try{
    		TipoAtto tipoAtto = tipoAttoRepository.findOne(tipoAttoId);
    		tipoAtto.setEnabled(true);
    		tipoAttoRepository.save(tipoAtto);
    		return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /tipoAttos/getStatiByTipoAttoId -> get all the stati of tipoatto.
     */
    @RequestMapping(value = "/tipoAttos/{id}/getStatiByTipoAttoId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<String>> getStatiByTipoAttoId(
    		@PathVariable(value="id") Long tipoAttoId)
            		throws GestattiCatchedException {
    	try{
    		return new ResponseEntity<>(tipoAttoService.getStatiByTipoAttoId(tipoAttoId), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /tipoAttos -> Create a new tipoAtto.
     */
    @RequestMapping(value = "/tipoAttos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoAttoDto tipoAttoDto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoAtto : {}", tipoAttoDto);
	        if (tipoAttoDto.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoAtto cannot already have an ID").build();
	        }
	        /*
	         * salvataggio tipoAtto
	         */
	        tipoAttoDto = tipoAttoService.save(tipoAttoDto);

	        return ResponseEntity.created(new URI("/api/tipoAttos/" + tipoAttoDto.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoAttos -> Updates an existing tipoAtto.
     */
    @RequestMapping(value = "/tipoAttos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoAttoDto tipoAttoDto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoAtto : {}", tipoAttoDto);
	        if (tipoAttoDto.getId() == null) {
	            return create(tipoAttoDto);
	        }
	        
	        /*
	         * salvataggio tipoAtto
	         */
	        tipoAttoService.save(tipoAttoDto);
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String codice, String descrizione, Long tipoProgressivo, Boolean proponente, Boolean consiglio, Boolean giunta, Boolean enabled) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("codice", codice);
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("tipoProgressivo", tipoProgressivo);
	    	if(proponente!=null){
	    		json.addProperty("proponente", proponente);
	    	}
	    	if(consiglio!=null){
	    		json.addProperty("consiglio", consiglio);
	    	}
	    	if(giunta!=null){
	    		json.addProperty("giunta", giunta);
	    	}
	    	if(enabled!=null) {
	    		json.addProperty("enabled", enabled);
	    	}

	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoAttos -> get all the tipoAttos.
     */
    @RequestMapping(value = "/tipoAttos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoAtto>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "tipoProgressivo", required = false) Long tipoProgressivo,
    		@RequestParam(value = "proponente", required = false) Boolean proponente,
    		@RequestParam(value = "consiglio", required = false) Boolean consiglio,
    		@RequestParam(value = "giunta", required = false) Boolean giunta,
    		@RequestParam(value = "enabled", required = false) Boolean enabled
    		)
            		throws GestattiCatchedException {
    	try{
	    	JsonObject jsonSearch = this.buildSearchObject(codice, descrizione, tipoProgressivo, proponente, consiglio, giunta, enabled);
	        return tipoAttoService.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoAttos/:id -> get the "id" tipoAtto.
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/tipoAttos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get TipoAtto : {}", id);
	        TipoAtto tipoAtto = tipoAttoService.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (tipoAtto == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByTipoAttoId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	tipoAtto.setAtti(atti);
	        }
	        
	        TipoAttoDto tipoAttoDto = TipoAttoConverter.convertToDto(tipoAtto);
	        
	        List<SezioneTipoAttoDto> listSezionetipoAttoDto = sezioneTipoAttoService.findByTipoAtto(tipoAttoDto.getId());
	        tipoAttoDto.setSezioni(listSezionetipoAttoDto);
	        
	        List<CampoTipoAttoDto> listCampotipoAttoDto = campoTipoAttoService.findByTipoAtto(tipoAttoDto.getId());
	        tipoAttoDto.setCampi(listCampotipoAttoDto);
	        
	        return new ResponseEntity<TipoAttoDto>(tipoAttoDto, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * GET  /tipoAttos/:codice -> get the "codice" tipoAtto.
     */
	@RequestMapping(value = "/tipoAttos/byCodice/{codice}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoAttoDto> get(@PathVariable String codice, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug(codice);
	        TipoAtto tipoAtto = tipoAttoService.findByCodice(codice);
	        TipoAttoDto tipoAttoDto = TipoAttoConverter.getSimpleDto(tipoAtto);
	        return new ResponseEntity<TipoAttoDto>(tipoAttoDto, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	} 
    }

	/**
	 * DELETE /tipoAttos/:id -> delete the "id" tipoAtto.
	 */
	@RequestMapping(value = "/tipoAttos/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	@Timed
	public ResponseEntity<String> delete(@PathVariable Long id) throws GestattiCatchedException {
		JsonObject j = new JsonObject();
		try {
			tipoAttoService.delete(id);
			j.addProperty("success", true);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
		} catch (Exception e) {
			j.addProperty("success", false);
			String errore = "Errore generico";
			if (e.getCause() != null && e.getCause() instanceof Exception) {
				Exception ex = (Exception) e.getCause();
				for (int i = 0; i < 3; i++) {
					if (ex instanceof MySQLIntegrityConstraintViolationException) {
						errore = ex.getMessage();
						break;
					} else if (ex.getCause() != null && ex.getCause() instanceof Exception) {
						ex = (Exception) ex.getCause();
					} else {
						break;
					}
				}
			}
			j.addProperty("message", errore);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
		}
	}

}
