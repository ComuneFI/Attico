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

import it.linksmt.assatti.datalayer.domain.Macro_cat_obbligo_dl33;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.Macro_cat_obbligo_dl33Repository;
import it.linksmt.assatti.service.AnagraficaTrasperanzaService;
import it.linksmt.assatti.service.MacroCatObbligoDl33Service;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Macro_cat_obbligo_dl33.
 */
@RestController
@RequestMapping("/api")
public class Macro_cat_obbligo_dl33Resource {

    private final Logger log = LoggerFactory.getLogger(Macro_cat_obbligo_dl33Resource.class);

    @Inject
    private Macro_cat_obbligo_dl33Repository macro_cat_obbligo_dl33Repository;

    @Inject
    private MacroCatObbligoDl33Service macroCatObbligoDl33Service;
    
    @Inject
    private AnagraficaTrasperanzaService anagraficaTrasparenzaService;
    
    @Inject
    private AttoRepository attoRepository;
    
    
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}/alreadyexist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkAlreadyExists(
			@PathVariable final Long id,
			@RequestParam(value = "codice", required = true) String codice,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to check if codice obbligo already exists : {}", id, codice);
			boolean exists = macroCatObbligoDl33Service.checkIfAlreadyExists(id, codice);
			JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}
    
    /**
     * POST  /macro_cat_obbligo_dl33s -> Create a new macro_cat_obbligo_dl33.
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody Macro_cat_obbligo_dl33 macro_cat_obbligo_dl33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Macro_cat_obbligo_dl33 : {}", macro_cat_obbligo_dl33);
//	        if (macro_cat_obbligo_dl33.getId() != null) {
//	            return ResponseEntity.badRequest().header("Failure", "A new macro_cat_obbligo_dl33 cannot already have an ID").build();
//	        }
	        //controllo che non esista una macrocategoria con un codice uguale a quello inserito dall'utente
	        if(macro_cat_obbligo_dl33.getCodice()!=null){
		        List<Macro_cat_obbligo_dl33> list = macro_cat_obbligo_dl33Repository.findByCodice(macro_cat_obbligo_dl33.getCodice());
		        if(list!=null&&list.size()>0){
		        	for (int i = 0; i < list.size(); i++) {
	        			Macro_cat_obbligo_dl33 listElemento = (Macro_cat_obbligo_dl33)list.get(i);
	        			if(listElemento.getId().longValue()!=macro_cat_obbligo_dl33.getId().longValue()){
	        				//return ResponseEntity.badRequest().header("Failure", "Codice già esistente").build();
	        				 throw new GestattiCatchedException("Codice già esistente");
	        			}
					}
		        }
	        }
	        macro_cat_obbligo_dl33Repository.save(macro_cat_obbligo_dl33);
	        return ResponseEntity.created(new URI("/api/macro_cat_obbligo_dl33s/" + macro_cat_obbligo_dl33.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /macro_cat_obbligo_dl33s -> Updates an existing macro_cat_obbligo_dl33.
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> update(@RequestBody Macro_cat_obbligo_dl33 macro_cat_obbligo_dl33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Macro_cat_obbligo_dl33 : {}", macro_cat_obbligo_dl33);
	        if (macro_cat_obbligo_dl33.getId() == null) {
	            return create(macro_cat_obbligo_dl33);
	        }
	        if(macro_cat_obbligo_dl33.getCodice()!=null){
	        	 List<Macro_cat_obbligo_dl33> list = macro_cat_obbligo_dl33Repository.findByCodice(macro_cat_obbligo_dl33.getCodice());
	        	if(list!=null&&list.size()>0){
	        		for (int i = 0; i < list.size(); i++) {
	        			Macro_cat_obbligo_dl33 listElemento = (Macro_cat_obbligo_dl33)list.get(i);
	        			if(listElemento.getId().longValue()!=macro_cat_obbligo_dl33.getId().longValue()){
	        				//return ResponseEntity.badRequest().header("Failure", "Codice già esistente").build();
	        				return new ResponseEntity<>("Codice già esistente", HttpStatus.BAD_REQUEST);
	        			}
					}
		        	
		        }
	        }
	        macro_cat_obbligo_dl33Repository.save(macro_cat_obbligo_dl33);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /macro_cat_obbligo_dl33s -> get all the macro_cat_obbligo_dl33s.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Macro_cat_obbligo_dl33>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "attiva", required = false) String attiva) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all Macro_cat_obbligo_dl33s");
	        JsonObject jsonSearch = this.buildSearchObject(codice, descrizione, attiva);
	        return macroCatObbligoDl33Service.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private JsonObject buildSearchObject(String codice, String descrizione, String attiva) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("codice", codice);
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("attiva", attiva);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /macro_cat_obbligo_dl33s -> get all the macro_cat_obbligo_dl33s.
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/attivo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<Macro_cat_obbligo_dl33> getAllAttivo() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all Macro_cat_obbligo_dl33s");
	        return anagraficaTrasparenzaService.getAllAttivo();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

     

    /**
     * GET  /macro_cat_obbligo_dl33s/:id -> get the "id" macro_cat_obbligo_dl33.
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Macro_cat_obbligo_dl33> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Macro_cat_obbligo_dl33 : {}", id);
	        Macro_cat_obbligo_dl33 macro_cat_obbligo_dl33 = macro_cat_obbligo_dl33Repository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (macro_cat_obbligo_dl33 == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByMacroCatObbligoDl33Id(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	macro_cat_obbligo_dl33.setAtti(atti);
	        }
	        
	        return new ResponseEntity<>(macro_cat_obbligo_dl33, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /macro_cat_obbligo_dl33s/:id/checkAlert
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}/checkAlert",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> checkAlert(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request check alert on disableing Macro_cat_obbligo_dl33 : {}", id);
	    	List<String> categorie = macro_cat_obbligo_dl33Repository.findCategorieOfMacroCategoria(id);
	    	JsonObject json = new JsonObject();
	    	if(categorie == null || categorie.size() == 0){
	    		json.addProperty("alert", "no");
	    	}else{
	    		json.addProperty("alert", "yes");
	    		json.add("categorie", new Gson().toJsonTree(categorie));
	    		List<String> obblighi = macro_cat_obbligo_dl33Repository.findObblighiOfMacroCategoria(id);
	    		if(obblighi!=null && obblighi.size()>0){
	    			json.add("obblighi", new Gson().toJsonTree(obblighi));
	    		}
	    	}
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /macro_cat_obbligo_dl33s/:id/abilita
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}/abilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> abilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request abilita macro_cat_obbligo_DL33 : {}", id);
	    	macroCatObbligoDl33Service.abilitaMacroCategoria(id);
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
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}/disabilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disabilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request disabilita macro_cat_obbligo_DL33 : {}", id);
	    	macroCatObbligoDl33Service.disabilitaMacroCategoria(id);
	    	JsonObject json = new JsonObject();
	    	json.addProperty("disabilitazione", "ok");
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /macro_cat_obbligo_dl33s/:id -> delete the "id" macro_cat_obbligo_dl33.
     */
    @RequestMapping(value = "/macro_cat_obbligo_dl33s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete Macro_cat_obbligo_dl33 : {}", id);
	        macro_cat_obbligo_dl33Repository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
