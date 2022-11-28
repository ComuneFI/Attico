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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.ProgressivoAdozione;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ProgressivoException;
import it.linksmt.assatti.datalayer.repository.ProgressivoAdozioneRepository;
import it.linksmt.assatti.service.ProgressivoAdozioneService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing ProgressivoAdozione.
 */
@RestController
@RequestMapping("/api")
public class ProgressivoAdozioneResource {

    private final Logger log = LoggerFactory.getLogger(ProgressivoAdozioneResource.class);

    @Inject
    private ProgressivoAdozioneRepository progressivoAdozioneRepository;
    
    @Inject
    private ProgressivoAdozioneService progressivoAdozioneService;

    /**
     * POST  /progressivoAdoziones -> Create a new progressivoAdozione.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/progressivoAdoziones",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody ProgressivoAdozione progressivoAdozione) throws GestattiCatchedException, URISyntaxException {
    	log.debug("REST request to save ProgressivoAdozione : {}", progressivoAdozione);
        if (progressivoAdozione.getId() != null) {
            return ResponseEntity.badRequest().header("Failure", "A new progressivoAdozione cannot already have an ID").build();
        }
        if(progressivoAdozione.getAoo().getId()==null) {
        	progressivoAdozione.setAoo(null);
        	if(progressivoAdozioneRepository.findByAnnoAndTipoProgressivoIdAndAooIsNull(progressivoAdozione.getAnno(), progressivoAdozione.getTipoProgressivo().getId()).size()>0) {
        		throw new GestattiCatchedException( new ProgressivoException("Progressivo esistente") );
        	}
        } else {
        	if(progressivoAdozioneRepository.findByAnnoAndAooIdAndTipoProgressivoId(progressivoAdozione.getAnno(), progressivoAdozione.getAoo().getId(), progressivoAdozione.getTipoProgressivo().getId()).size()>0) {
        		throw new GestattiCatchedException( new ProgressivoException("Progressivo esistente") );
        	}
        }
        try {
        	progressivoAdozioneRepository.save(progressivoAdozione);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
        return ResponseEntity.created(new URI("/api/progressivoAdoziones/" + progressivoAdozione.getId())).build();
    }

    /**
     * PUT  /progressivoAdoziones -> Updates an existing progressivoAdozione.
     */
    @RequestMapping(value = "/progressivoAdoziones",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody ProgressivoAdozione progressivoAdozione) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update ProgressivoAdozione : {}", progressivoAdozione);
	        if (progressivoAdozione.getId() == null) {
	            return create(progressivoAdozione);
	        }
	        progressivoAdozioneRepository.save(progressivoAdozione);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /progressivoAdoziones/search -> get all the progressivoAdoziones match with filters.
     */
    @RequestMapping(value = "/progressivoAdoziones/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ProgressivoAdozione>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr)
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Sort sort = null;
	  		if(null != search && !search.isJsonNull() && ((search.has("aoo") && !search.get("aoo").isJsonNull() && !search.get("aoo").getAsString().isEmpty() && search.get("aoo").getAsString().equals("-")) || (search.has("aoos") && search.getAsJsonArray("aoos").size() > 0))){
	  			sort = new Sort(new Order(Direction.ASC, "tipoProgressivo.descrizione"), new Order(Direction.DESC, "id" ));
	  		}else{
	  			sort = new Sort(new Order(Direction.ASC, "tipoProgressivo.descrizione"), new Order(Direction.ASC, "aoo.codice"), new Order(Direction.DESC, "id" ));
	  		}
    		Page<ProgressivoAdozione> page = progressivoAdozioneService.findAll(PaginationUtil.generatePageRequest(offset, limit, sort), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/progressivoAdoziones", offset, limit);
	        return new ResponseEntity<List<ProgressivoAdozione>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /progressivoAdoziones/:id -> get the "id" progressivoAdozione.
     */
    @RequestMapping(value = "/progressivoAdoziones/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ProgressivoAdozione> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get ProgressivoAdozione : {}", id);
	        ProgressivoAdozione progressivoAdozione = progressivoAdozioneRepository.findOne(id);
	        if (progressivoAdozione == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(progressivoAdozione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /progressivoAdoziones/:id -> delete the "id" progressivoAdozione.
     */
    @RequestMapping(value = "/progressivoAdoziones/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ProgressivoAdozione : {}", id);
	        progressivoAdozioneRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
