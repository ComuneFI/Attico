package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.Cat_obbligo_dl33;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.Cat_obbligo_DL33Repository;
import it.linksmt.assatti.service.CatObbligoDl33Service;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Cat_obbligo_DL33.
 */
@RestController
@RequestMapping("/api")
public class Cat_obbligo_DL33Resource {

    private final Logger log = LoggerFactory.getLogger(Cat_obbligo_DL33Resource.class);

    @Inject
    private Cat_obbligo_DL33Repository cat_obbligo_DL33Repository;
    
    @Inject
    private CatObbligoDl33Service catObbligoDl33Service;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * GET  /macro_cat_obbligo_dl33s/:id/checkAlert
     */
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}/checkAlert",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> checkAlert(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request check alert on disableing cat_obbligo_DL33 : {}", id);
	    	List<String> obblighi = cat_obbligo_DL33Repository.findObblighiOfCategoria(id);
	    	JsonObject json = new JsonObject();
	    	if(obblighi == null || obblighi.size() == 0){
	    		json.addProperty("alert", "no");
	    	}else{
	    		json.addProperty("alert", "yes");
	    		json.add("obblighi", new Gson().toJsonTree(obblighi));
	    	}
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /macro_cat_obbligo_dl33s/:id/abilita
     */
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}/abilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> abilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request abilita cat_obbligo_DL33 : {}", id);
	    	catObbligoDl33Service.abilitaCategoria(id);
	    	JsonObject json = new JsonObject();
	    	json.addProperty("abilitazione", "ok");
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /macro_cat_obbligo_dl33s/:id/disabilita
     */
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}/disabilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disabilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request disabilita cat_obbligo_DL33 : {}", id);
	    	catObbligoDl33Service.disabilitaCategoria(id);
	    	JsonObject json = new JsonObject();
	    	json.addProperty("disabilitazione", "ok");
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}/alreadyexist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkAlreadyExists(
			@PathVariable final Long id,
			@RequestParam(value = "macro_cat_obbligo_DL33id", required = true) String macro_cat_obbligo_DL33id,
			@RequestParam(value = "codice", required = true) String codice,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to check if codice obbligo already exists : {}", macro_cat_obbligo_DL33id, codice);
			boolean exists = catObbligoDl33Service.checkIfAlreadyExists(id,Long.parseLong(macro_cat_obbligo_DL33id), codice);
			JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    
    /**
     * POST  /cat_obbligo_DL33s -> Create a new cat_obbligo_DL33.
     */
    @RequestMapping(value = "/cat_obbligo_DL33s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody Cat_obbligo_dl33 cat_obbligo_DL33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Cat_obbligo_DL33 : {}", cat_obbligo_DL33);
//	        if (cat_obbligo_DL33.getId() != null) {
//	            return ResponseEntity.badRequest().header("Failure", "A new cat_obbligo_DL33 cannot already have an ID").build();
//	        }
	        //controllo che non esista una categoria con un codice uguale a quello inserito dall'utente per la macrocategoria di appartenenza
	        if(cat_obbligo_DL33.getCodice()!=null && cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx()!=null && cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx().getId()!=null){
	        	 List<Cat_obbligo_dl33> list = cat_obbligo_DL33Repository.findByMacroIdECodice(cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx().getId(), cat_obbligo_DL33.getCodice());
	        	 if(list!=null&&list.size()>0){
	        		 for (int i = 0; i < list.size(); i++) {
	        			 Cat_obbligo_dl33 listElemento = (Cat_obbligo_dl33)list.get(i);
		        			if(listElemento.getId().longValue()!=cat_obbligo_DL33.getId().longValue()){
				        		 throw new GestattiCatchedException("Codice già esistente per la macro categoria di appartenenza");
		        			}
	        		 }
		        }
	        }
	        cat_obbligo_DL33Repository.save(cat_obbligo_DL33);
	        return ResponseEntity.created(new URI("/api/cat_obbligo_DL33s/" + cat_obbligo_DL33.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /cat_obbligo_DL33s -> Updates an existing cat_obbligo_DL33.
     */
    @RequestMapping(value = "/cat_obbligo_DL33s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> update(@RequestBody Cat_obbligo_dl33 cat_obbligo_DL33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Cat_obbligo_DL33 : {}", cat_obbligo_DL33);
	        if (cat_obbligo_DL33.getId() == null) {
	            return create(cat_obbligo_DL33);
	        }
	        if(cat_obbligo_DL33.getCodice()!=null&&cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx()!=null&&cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx().getId()!=null){
	        	 List<Cat_obbligo_dl33> list = cat_obbligo_DL33Repository.findByMacroIdECodice(cat_obbligo_DL33.getFk_cat_obbligo_macro_cat_obbligo_idx().getId(), cat_obbligo_DL33.getCodice());
	        	 if(list!=null&&list.size()>0){
	        		 for (int i = 0; i < list.size(); i++) {
	        			 Cat_obbligo_dl33 listElemento = (Cat_obbligo_dl33)list.get(i);
		        			if(listElemento.getId().longValue()!=cat_obbligo_DL33.getId().longValue()){
		        				//return ResponseEntity.badRequest().header("Failure", "Codice già esistente").build();
		        				return new ResponseEntity<>("Codice già esistente per la macrocategoria di appartenenza", HttpStatus.BAD_REQUEST);
		        			}
	        		 }
			       }
	        }
	        cat_obbligo_DL33Repository.save(cat_obbligo_DL33);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /cat_obbligo_DL33s -> get all the cat_obbligo_DL33s.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/cat_obbligo_DL33s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Cat_obbligo_dl33>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "attiva", required = false) String attiva,
            @RequestParam(value = "fk_cat_obbligo_macro_cat_obbligo_idx", required = false) String fk_cat_obbligo_macro_cat_obbligo_idx) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all Cat_obbligo_DL33s");
	        JsonObject jsonSearch = this.buildSearchObject(codice, descrizione, attiva, fk_cat_obbligo_macro_cat_obbligo_idx);
	        return catObbligoDl33Service.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String codice, String descrizione, String attiva, String fk_cat_obbligo_macro_cat_obbligo_idx)throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("codice", codice);
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("attiva", attiva);
	    	json.addProperty("fk_cat_obbligo_macro_cat_obbligo_idx", fk_cat_obbligo_macro_cat_obbligo_idx);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /cat_obbligo_DL33s/:id -> get the "id" cat_obbligo_DL33.
     */
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Cat_obbligo_dl33> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Cat_obbligo_DL33 : {}", id);
	    	Cat_obbligo_dl33 cat_obbligo_DL33 = cat_obbligo_DL33Repository.findOne(id);
	    	Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (cat_obbligo_DL33 == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByCatObbligoDl33Id(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	cat_obbligo_DL33.setAtti(atti);
	        }
	        return new ResponseEntity<>(cat_obbligo_DL33, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /cat_obbligo_DL33s/:id -> delete the "id" cat_obbligo_DL33.
     */
    @RequestMapping(value = "/cat_obbligo_DL33s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Cat_obbligo_DL33 : {}", id);
	        cat_obbligo_DL33Repository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
