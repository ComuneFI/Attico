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

import it.linksmt.assatti.datalayer.domain.CategoriaFaq;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.CategoriaFaqRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.CategoriaFaqService;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.dto.CategoriaFaqDTO;
import it.linksmt.assatti.service.dto.CategoriaFaqSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing CategoriaFaq.
 */
@RestController
@RequestMapping("/api")
public class CategoriaFaqResource {

    private final Logger log = LoggerFactory.getLogger(CategoriaFaqResource.class);

    @Inject
    private CategoriaFaqRepository categoriaFaqRepository;
    
    @Inject
    private CategoriaFaqService categoriaFaqService;

    /**
     * POST  /categoriaFaqs -> Create a new categoriaFaq.
     */
    @RequestMapping(value = "/categoriaFaqs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody CategoriaFaq categoriaFaq) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save CategoriaFaq : {}", categoriaFaq);
	        if (categoriaFaq.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new categoriaFaq cannot already have an ID").build();
	        }
	        categoriaFaqRepository.save(categoriaFaq);
	        return ResponseEntity.created(new URI("/api/categoriaFaqs/" + categoriaFaq.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /categoriaFaqs -> Updates an existing categoriaFaq.
     */
    @RequestMapping(value = "/categoriaFaqs",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody CategoriaFaq categoriaFaq) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update CategoriaFaq : {}", categoriaFaq);
	        if (categoriaFaq.getId() == null) {
	            return create(categoriaFaq);
	        }
	        categoriaFaqRepository.save(categoriaFaq);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /categoriaFaqs -> get all the categoriaFaqs.
     */
    @RequestMapping(value = "/categoriaFaqs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<List<CategoriaFaqDTO>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "idRicerca", required = false) Long id,
                                  @RequestParam(value = "descrizione", required = false) String descrizione)
                                		  throws GestattiCatchedException {
    	try{
	    	//Page<CategoriaFaq> page = categoriaFaqRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	    	CategoriaFaqSearchDTO searchObject = this.buildSearchObject(id, descrizione);
	    	return categoriaFaqService.search(searchObject, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /categoriaFaqs/:id -> get the "id" categoriaFaq.
     */
    @RequestMapping(value = "/categoriaFaqs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<CategoriaFaq> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get CategoriaFaq : {}", id);
	        CategoriaFaq categoriaFaq = categoriaFaqRepository.findOne(id);
	        if (categoriaFaq == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(categoriaFaq, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /categoriaFaqs/:id -> delete the "id" categoriaFaq.
     */
    @RequestMapping(value = "/categoriaFaqs/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public void delete(@PathVariable Long id)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete CategoriaFaq : {}", id);
	        categoriaFaqRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /public/categoriaFaqs -> get all the categoriaFaqs.
     */
    @RequestMapping(value = "/public/categoriaFaqs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<CategoriaFaq>> getAllPublic(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	if(null==limit)
	    		limit = PaginationUtil.MAX_LIMIT;
	        Page<CategoriaFaq> page = categoriaFaqRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/public/categoriaFaqs", offset, limit);
	        return new ResponseEntity<List<CategoriaFaq>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private CategoriaFaqSearchDTO buildSearchObject(Long id, String descrizione) throws GestattiCatchedException{
    	try{
    		CategoriaFaqSearchDTO searchObject = new CategoriaFaqSearchDTO();
    		searchObject.setId(id);
    		searchObject.setDescrizione(descrizione);
	    	return searchObject;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
