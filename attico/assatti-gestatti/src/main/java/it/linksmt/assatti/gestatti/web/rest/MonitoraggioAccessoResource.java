package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.MonitoraggioAccesso;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.MonitoraggioAccessoService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing MonitoraggioAccesso.
 */
@RestController
@RequestMapping("/api")
public class MonitoraggioAccessoResource {

    private final Logger log = LoggerFactory.getLogger(MonitoraggioAccessoResource.class);
    
    @Inject
    private MonitoraggioAccessoService monitoraggioAccessoService;
   
    /**
     * POST  /monitoraggioAccessos/search -> get all the monitoraggioAccessos search by criteria
     */
    @RequestMapping(value = "/monitoraggioAccessos/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<MonitoraggioAccesso>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                  
                                		  throws GestattiCatchedException {
    	try{
    		log.debug("MonitoraggioAccessoResource :: search :: ", searchStr);
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Sort sort = null;
	  		if(null != search && !search.isJsonNull() && search.has("sortField") && !search.get("sortField").isJsonNull() && !search.get("sortField").getAsString().isEmpty() && search.has("sortType") && !search.get("sortType").isJsonNull() && !search.get("sortType").getAsString().isEmpty()){
	  			Direction dir = null;
	  			if(search.get("sortType").getAsString().equalsIgnoreCase("asc")){
	  				dir = Direction.ASC;
	  			}else{
	  				dir = Direction.DESC;
	  			}
	  			sort = new Sort(new Order(dir, search.get("sortField").getAsString()), new Order(Direction.DESC, "id" ));
	  		}
	  		Pageable p = null;
	  		if(sort!=null){
	  			p = PaginationUtil.generatePageRequest(offset, limit, sort);
	  		}else{
	  			p = PaginationUtil.generatePageRequest(offset, limit, new Sort(new Order(Direction.DESC, "id" )));
	  		}
	    	Page<MonitoraggioAccesso> page = monitoraggioAccessoService.findAll(p, search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/monitoraggioAccessos", offset, limit);
	        return new ResponseEntity<List<MonitoraggioAccesso>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
