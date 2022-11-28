package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

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

import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.DocumentoInformaticoRepository;
import it.linksmt.assatti.service.DocumentoInformaticoService;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing DocumentoInformatico.
 */
@RestController
@RequestMapping("/api")
public class DocumentoInformaticoResource {

    private final Logger log = LoggerFactory.getLogger(DocumentoInformaticoResource.class);

    @Inject
    private DocumentoInformaticoRepository documentoInformaticoRepository;
    
    @Inject
    private DocumentoInformaticoService documentoInformaticoService;

    /**
     * POST  /documentoInformaticos -> Create a new documentoInformatico.
     */
    @RequestMapping(value = "/documentoInformaticos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody DocumentoInformatico documentoInformatico) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save DocumentoInformatico : {}", documentoInformatico);
	        if (documentoInformatico.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new documentoInformatico cannot already have an ID").build();
	        }
	        documentoInformaticoRepository.save(documentoInformatico);
	        return ResponseEntity.created(new URI("/api/documentoInformaticos/" + documentoInformatico.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /documentoInformaticos -> Updates an existing documentoInformatico.
     */
    @RequestMapping(value = "/documentoInformaticos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody DocumentoInformatico documentoInformatico) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update DocumentoInformatico : {}", documentoInformatico);
	        if (documentoInformatico.getId() == null) {
	            return create(documentoInformatico);
	        }
	        documentoInformaticoRepository.save(documentoInformatico);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /documentoInformaticos -> get all the documentoInformaticos.
     */
    @RequestMapping(value = "/documentoInformaticos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<DocumentoInformatico>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<DocumentoInformatico> page = documentoInformaticoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/documentoInformaticos", offset, limit);
	        return new ResponseEntity<List<DocumentoInformatico>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /documentoInformaticos/:id -> get the "id" documentoInformatico.
     */
    @RequestMapping(value = "/documentoInformaticos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<DocumentoInformatico> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get DocumentoInformatico : {}", id);
	        DocumentoInformatico documentoInformatico = documentoInformaticoRepository.findOne(id);
	        if (documentoInformatico == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(documentoInformatico, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * DELETE  /documentoInformaticos/:id -> delete the "id" documentoInformatico.
     */
    @RequestMapping(value = "/documentoInformaticos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete DocumentoInformatico : {}", id);
	        documentoInformaticoService.deleteDocumentoInformaticoAndFile(id);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
