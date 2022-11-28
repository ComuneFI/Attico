package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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

import it.linksmt.assatti.datalayer.domain.ProgressivoProposta;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ProgressivoException;
import it.linksmt.assatti.datalayer.repository.ProgressivoPropostaRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.ProgressivoPropostaService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing ProgressivoProposta.
 */
@RestController
@RequestMapping("/api")
public class ProgressivoPropostaResource {

    private final Logger log = LoggerFactory.getLogger(ProgressivoPropostaResource.class);

    @Inject
    private ProgressivoPropostaRepository progressivoPropostaRepository;
    
    @Inject
    private ProgressivoPropostaService progressivoPropostaService;

    /**
     * POST  /progressivoPropostas -> Create a new progressivoProposta.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/progressivoPropostas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody ProgressivoProposta progressivoProposta) throws GestattiCatchedException, URISyntaxException{
    	
    	log.debug("REST request to save ProgressivoProposta : {}", progressivoProposta);
        if (progressivoProposta.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new progressivoProposta cannot already have an ID").build();
        }
        if(progressivoProposta.getAoo().getId()==null) {
        	progressivoProposta.setAoo(null);
        	if(progressivoPropostaRepository.findByAnnoAndTipoProgressivoIdAndAooIdIsNull(progressivoProposta.getAnno(), progressivoProposta.getTipoProgressivo().getId()).size()>0) {
        		throw new GestattiCatchedException( new ProgressivoException("Progressivo esistente") );
        	}
        } else {
        	if(progressivoPropostaRepository.findByAnnoAndAooIdAndTipoProgressivoId(progressivoProposta.getAnno(), progressivoProposta.getAoo().getId(), progressivoProposta.getTipoProgressivo().getId()).size()>0) {
        		throw new GestattiCatchedException( new ProgressivoException("Progressivo esistente") );
        	}
        }
        try {
        	progressivoPropostaRepository.save(progressivoProposta);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
        return ResponseEntity.created(new URI("/api/progressivoPropostas/" + progressivoProposta.getId())).build();
    }

    /**
     * PUT  /progressivoPropostas -> Updates an existing progressivoProposta.
     */
    @RequestMapping(value = "/progressivoPropostas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public ResponseEntity<Void> update(@RequestBody ProgressivoProposta progressivoProposta) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update ProgressivoProposta : {}", progressivoProposta);
	        if (progressivoProposta.getId() == null) {
	            return create(progressivoProposta);
	        }
	        progressivoPropostaRepository.save(progressivoProposta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /progressivoPropostas/search -> get all the progressivoPropostas match with filters.
     */
    @RequestMapping(value = "/progressivoPropostas/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProgressivoProposta>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr)
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Sort sort = new Sort(new Order(Direction.ASC, "tipoProgressivo.descrizione"), new Order(Direction.ASC, "aoo.codice"), new Order(Direction.DESC, "id" ));
    		Page<ProgressivoProposta> page = progressivoPropostaService.findAll(PaginationUtil.generatePageRequest(offset, limit, sort), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/progressivoPropostas", offset, limit);
	        return new ResponseEntity<List<ProgressivoProposta>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /progressivoPropostas/:id -> get the "id" progressivoProposta.
     */
    @RequestMapping(value = "/progressivoPropostas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProgressivoProposta> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get ProgressivoProposta : {}", id);
	        ProgressivoProposta progressivoProposta = progressivoPropostaRepository.findOne(id);
	        if (progressivoProposta == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(progressivoProposta, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /progressivoPropostas/:id -> delete the "id" progressivoProposta.
     */
    @RequestMapping(value = "/progressivoPropostas/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ProgressivoProposta : {}", id);
	        progressivoPropostaRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
