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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.StatoRichiestaHD;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.StatoRichiestaHDRepository;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing StatoRichiestaHD.
 */
@RestController
@RequestMapping("/api")
public class StatoRichiestaHDResource {

    private final Logger log = LoggerFactory.getLogger(StatoRichiestaHDResource.class);

    @Inject
    private StatoRichiestaHDRepository statoRichiestaHDRepository;

    /**
     * POST  /statoRichiestaHDs -> Create a new statoRichiestaHD.
     */
    @RequestMapping(value = "/statoRichiestaHDs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody StatoRichiestaHD statoRichiestaHD) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save StatoRichiestaHD : {}", statoRichiestaHD);
	        if (statoRichiestaHD.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new statoRichiestaHD cannot already have an ID").build();
	        }
	        statoRichiestaHDRepository.save(statoRichiestaHD);
	        return ResponseEntity.created(new URI("/api/statoRichiestaHDs/" + statoRichiestaHD.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /statoRichiestaHDs -> Updates an existing statoRichiestaHD.
     */
    @RequestMapping(value = "/statoRichiestaHDs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody StatoRichiestaHD statoRichiestaHD) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update StatoRichiestaHD : {}", statoRichiestaHD);
	        if (statoRichiestaHD.getId() == null) {
	            return create(statoRichiestaHD);
	        }
	        statoRichiestaHDRepository.save(statoRichiestaHD);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /statoRichiestaHDs -> get all the statoRichiestaHDs.
     */
    @RequestMapping(value = "/statoRichiestaHDs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<StatoRichiestaHD>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<StatoRichiestaHD> page = statoRichiestaHDRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/statoRichiestaHDs", offset, limit);
	        return new ResponseEntity<List<StatoRichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /statoRichiestaHDs/:id -> get the "id" statoRichiestaHD.
     */
    @RequestMapping(value = "/statoRichiestaHDs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<StatoRichiestaHD> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get StatoRichiestaHD : {}", id);
	        StatoRichiestaHD statoRichiestaHD = statoRichiestaHDRepository.findOne(id);
	        if (statoRichiestaHD == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(statoRichiestaHD, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /statoRichiestaHDs/:id -> delete the "id" statoRichiestaHD.
     */
    @RequestMapping(value = "/statoRichiestaHDs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete StatoRichiestaHD : {}", id);
	        statoRichiestaHDRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
