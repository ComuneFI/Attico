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

import it.linksmt.assatti.datalayer.domain.QTipoRichiestaHD;
import it.linksmt.assatti.datalayer.domain.TipoRichiestaHD;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.TipoRichiestaHDRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.TipoRichiestaHDService;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing TipoRichiestaHD.
 */
@RestController
@RequestMapping("/api")
public class TipoRichiestaHDResource {

    private final Logger log = LoggerFactory.getLogger(TipoRichiestaHDResource.class);

    @Inject
    private TipoRichiestaHDRepository tipoRichiestaHDRepository;
    @Inject
    private TipoRichiestaHDService tipoRichiestaHDService;

    /**
     * GET  /tipoRichiestaHDs/{id}/enable
     */
    @RequestMapping(value = "/tipoRichiestaHDs/{id}/enable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> enable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable tipoRichiestaHD ", id);
	    	tipoRichiestaHDService.enable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoRichiestaHDs/{id}/disable
     */
    @RequestMapping(value = "/tipoRichiestaHDs/{id}/disable",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> disable(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable tipoRichiestaHD ", id);
	    	tipoRichiestaHDService.disable(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /tipoRichiestaHDs -> Create a new tipoRichiestaHD.
     */
    @RequestMapping(value = "/tipoRichiestaHDs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoRichiestaHD tipoRichiestaHD) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoRichiestaHD : {}", tipoRichiestaHD);
	        if (tipoRichiestaHD.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoRichiestaHD cannot already have an ID").build();
	        }
	        tipoRichiestaHDRepository.save(tipoRichiestaHD);
	        return ResponseEntity.created(new URI("/api/tipoRichiestaHDs/" + tipoRichiestaHD.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoRichiestaHDs -> Updates an existing tipoRichiestaHD.
     */
    @RequestMapping(value = "/tipoRichiestaHDs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoRichiestaHD tipoRichiestaHD) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoRichiestaHD : {}", tipoRichiestaHD);
	        if (tipoRichiestaHD.getId() == null) {
	            return create(tipoRichiestaHD);
	        }
	        tipoRichiestaHDRepository.save(tipoRichiestaHD);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoRichiestaHDs -> get all the tipoRichiestaHDs not dirigente.
     */
    @RequestMapping(value = "/tipoRichiestaHDs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoRichiestaHD>> getAllNotDirigente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<TipoRichiestaHD> page = tipoRichiestaHDRepository.findAll(QTipoRichiestaHD.tipoRichiestaHD.dirigente.eq(false), PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoRichiestaHDs", offset, limit);
	        return new ResponseEntity<List<TipoRichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoRichiestaHDs/dirigente -> get all the tipoRichiestaHDs dirigente.
     */
    @RequestMapping(value = "/tipoRichiestaHDs/dirigente",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoRichiestaHD>> getAllDirigente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<TipoRichiestaHD> page = tipoRichiestaHDRepository.findAll(QTipoRichiestaHD.tipoRichiestaHD.dirigente.eq(true), PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoRichiestaHDs", offset, limit);
	        return new ResponseEntity<List<TipoRichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoRichiestaHDs/:id -> get the "id" tipoRichiestaHD.
     */
    @RequestMapping(value = "/tipoRichiestaHDs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoRichiestaHD> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoRichiestaHD : {}", id);
	        TipoRichiestaHD tipoRichiestaHD = tipoRichiestaHDRepository.findOne(id);
	        if (tipoRichiestaHD == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(tipoRichiestaHD, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoRichiestaHDs/:id -> delete the "id" tipoRichiestaHD.
     */
    @RequestMapping(value = "/tipoRichiestaHDs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete TipoRichiestaHD : {}", id);
	        tipoRichiestaHDRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
