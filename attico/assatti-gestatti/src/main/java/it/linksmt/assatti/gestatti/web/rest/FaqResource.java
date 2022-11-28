package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
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

import it.linksmt.assatti.datalayer.domain.Faq;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.FAQService;
import it.linksmt.assatti.service.exception.NotFoundException;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.dto.FaqSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Faq.
 */
@RestController
@RequestMapping("/api")
public class FaqResource {

    private final Logger log = LoggerFactory.getLogger(FaqResource.class);
    
    @Inject
    private FAQService fAQService;

    /**
     * POST  /faqs -> Create a new faq.
     */
    @RequestMapping(value = "/faqs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<Void> create(@Valid @RequestBody Faq faq) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Faq : {}", faq);
	        
	        if (faq.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new faq cannot already have an ID").build();
	        }
	        
	        faq = fAQService.save(faq);
	        
	        return ResponseEntity.created(new URI("/api/faqs/" + faq.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /faqs -> Updates an existing faq.
     */
    @RequestMapping(value = "/faqs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Faq faq) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Faq : {}", faq);
	        if (faq.getId() == null) {
	            return create(faq);
	        }
	       
	        faq = fAQService.save(faq);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /faqs -> get all the faqs.
     */
    @RequestMapping(value = "/faqs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public ResponseEntity<List<Faq>> getAll(@RequestParam(value = "page"     , required = false) Integer offset,
    										@RequestParam(value = "per_page" , required = false) Integer limit,
    										@RequestParam(value = "domanda" , required = false) String domanda,
    										@RequestParam(value = "risposta" , required = false) String risposta,
    										@RequestParam(value = "categoria" , required = false) Long categoria,
    										@RequestParam(value = "aoo" , required = false) Long aoo) throws GestattiCatchedException {
    	try{
    		FaqSearchDTO searchObject = this.buildSearchObject(domanda, risposta, categoria, aoo);
    		return fAQService.search(searchObject, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    

    /**
     * GET  /search -> get all the faqs searchByProfiloId
     * @throws  
     */
    @RequestMapping(value = "/faqs/searchByProfiloId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Faq>> searchByProfiloId(@RequestParam(value = "categoriaID", required = false) Long categoriaID,
    												   @RequestParam(value = "profiloId", required = true) Long profiloId,
    										   		   @RequestParam(value = "page"     , required = false) Integer offset,
    										   		   @RequestParam(value = "per_page" , required = false) Integer limit) throws GestattiCatchedException  {
    	try{
	    	log.debug("REST request to get Faq with profiloId : {}", profiloId);
	    	
	    	
	    	Page<Faq> page;
			try {
				page = fAQService.findAllByProfiloIDAndByCategoriaID(categoriaID, profiloId, offset, limit, null);
			} catch (NotFoundException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/public/faqs", offset, limit);
	        return new ResponseEntity<List<Faq>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    

    /**
     * GET  /public/faqs -> get all PUBLIC faqs.
     */
    @RequestMapping(value = "/public/faqs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Faq>> getPublic(@RequestParam(value = "categoriaID", required = false) Long categoriaID,
    										   @RequestParam(value = "page"    , required = false) Integer offset,
            								   @RequestParam(value = "per_page", required = false) Integer limit) throws GestattiCatchedException {
    	try{   	
	    	Page<Faq> page;
			try {
				page = fAQService.findAllAndByCategoriaID(categoriaID, offset, limit, new Sort(Direction.DESC, "categoria"));
			} catch (NotFoundException e) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/public/faqs", offset, limit );
	        return new ResponseEntity<List<Faq>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
        
    }    
    
    /**
     * GET  /faqs/:id -> get the "id" faq.
     */
    @RequestMapping(value = "/faqs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Faq> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Faq : {}", id);
	        Faq faq = fAQService.findOne(id);
	        
	        if (faq == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	
	        return new ResponseEntity<>(faq, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /faqs/:id -> delete the "id" faq.
     */
    @RequestMapping(value = "/faqs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.ADMIN)
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	        log.debug("REST request to delete Faq : {}", id);
	        fAQService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private FaqSearchDTO buildSearchObject(String domanda, String risposta, Long categoria, Long aoo) throws GestattiCatchedException{
    	try{
    		FaqSearchDTO searchObject = new FaqSearchDTO();
    		searchObject.setDomanda(domanda);
    		searchObject.setRisposta(risposta);
    		searchObject.setCategoria(categoria);
    		searchObject.setAoo(aoo);
	    	return searchObject;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
