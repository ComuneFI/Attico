package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
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

import it.linksmt.assatti.datalayer.domain.ConfigurazioneRiversamento;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.ConfigurazioneRiversamentoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing configurazioneRiversamento.
 */
@RestController
@RequestMapping("/api")
public class ConfigurazioneRiversamentoResource {

    private final Logger log = LoggerFactory.getLogger(ConfigurazioneRiversamentoResource.class);

    @Inject
	private ConfigurazioneRiversamentoService configurazioneRiversamentoService;
    
    /**
     * PUT /configurazioneRiversamentos/disable -> Disable an configurazioneRiversamento
     */
    @RequestMapping(value = "/configurazioneRiversamentos/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConfigurazioneRiversamento> disableClassificazione(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable ConfigurazioneSerie : {}", id);
	    	ConfigurazioneRiversamento configurazioneRiversamento = configurazioneRiversamentoService.disableConfigurazioneRiversamento(Long.parseLong(id));
			return new ResponseEntity<ConfigurazioneRiversamento>(configurazioneRiversamento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

    /**
     * PUT /configurazioneRiversamentos/enable -> Enable an configurazioneRiversamento
     */
    @RequestMapping(value = "/configurazioneRiversamentos/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<ConfigurazioneRiversamento> enableConfigurazioneSerie(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable ConfigurazioneSerie : {}", id);
	    	ConfigurazioneRiversamento configurazioneRiversamento = configurazioneRiversamentoService.findOne(Long.parseLong(id));
			if(configurazioneRiversamentoService.isAvailable(configurazioneRiversamento)) {
				if(configurazioneRiversamentoService.findActiveConfigurazione(configurazioneRiversamento.getTipoDocumento().getId(), configurazioneRiversamento.getTipoAttoId(), configurazioneRiversamento.getAooId())==null) {
					configurazioneRiversamento = configurazioneRiversamentoService.enableConfigurazioneSerie(Long.parseLong(id));
					return new ResponseEntity<ConfigurazioneRiversamento>(configurazioneRiversamento, HttpStatus.OK);
				} else {
					HttpHeaders headers = new HttpHeaders();
		            headers.add("Failure", "Esiste già una configurazione attiva con le stesse caratteristiche");
		        	return new ResponseEntity<ConfigurazioneRiversamento>(configurazioneRiversamento, headers, HttpStatus.BAD_REQUEST);  
				}
	        }else {
	        	HttpHeaders headers = new HttpHeaders();
	            headers.add("Failure", "Esiste già una configurazione attiva con le stesse caratteristiche");
	        	return new ResponseEntity<ConfigurazioneRiversamento>(configurazioneRiversamento, headers, HttpStatus.BAD_REQUEST);        	
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    

    /**
     * POST  /configurazioneRiversamentos -> Create a new configurazioneRiversamento.
     */
    @RequestMapping(value = "/configurazioneRiversamentos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> createUpdate(@RequestBody ConfigurazioneRiversamento configurazioneRiversamento) throws GestattiCatchedException{
    	
    	try{
	    	log.debug("REST request to save configurazioneRiversamento : {}", configurazioneRiversamento);
	        if(configurazioneRiversamentoService.isAvailable(configurazioneRiversamento)) {
	        	ConfigurazioneRiversamento contr = configurazioneRiversamentoService.findActiveConfigurazione(configurazioneRiversamento.getTipoDocumento().getId(), configurazioneRiversamento.getTipoAttoId(), configurazioneRiversamento.getAooId());
	        	if(contr==null || (configurazioneRiversamento.getId()!=null && contr.getId()!=null && contr.getId().equals(configurazioneRiversamento.getId()))) {
	        		configurazioneRiversamentoService.save(configurazioneRiversamento);	        	
		        	return ResponseEntity.created(new URI("/api/configurazioneRiversamentos/" + configurazioneRiversamento.getId())).build();
				} else {
		        	return ResponseEntity.badRequest().header("Failure", "Esiste già una configurazione attiva con le stesse caratteristiche").build();
				}
	        }else {
	        	return ResponseEntity.badRequest().header("Failure", "Esiste già una configurazione attiva con le stesse caratteristiche").build();	        	
	        }
	   
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  //configurazioneRiversamentos/search -> get configurazioneRiversamentos by criterias.
     */
    @RequestMapping(value = "/configurazioneRiversamentos/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ConfigurazioneRiversamento>> search(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestBody String searchStr) throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Sort sort = new Sort(new Order(Direction.ASC, "tipoDocumento.descrizione"), new Order(Direction.DESC, "id" ));
    		Page<ConfigurazioneRiversamento> page = configurazioneRiversamentoService.findAll(PaginationUtil.generatePageRequest(offset, limit, sort), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/configurazioneRiversamentos", offset, limit);
	        return new ResponseEntity<List<ConfigurazioneRiversamento>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /configurazioneRiversamentos/:id -> get the "id" serie.
     */
    @RequestMapping(value = "/configurazioneRiversamentos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ConfigurazioneRiversamento> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Serie : {}", id);
	    	ConfigurazioneRiversamento configurazioneRiversamento = configurazioneRiversamentoService.findOne(id);
	        if (configurazioneRiversamento == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(configurazioneRiversamento, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

}
