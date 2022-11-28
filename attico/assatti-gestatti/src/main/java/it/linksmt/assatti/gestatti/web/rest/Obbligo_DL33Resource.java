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

import it.linksmt.assatti.datalayer.domain.Obbligo_DL33;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.Obbligo_DL33Repository;
import it.linksmt.assatti.service.ObbligoDl33Service;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing Obbligo_DL33.
 */
@RestController
@RequestMapping("/api")
public class Obbligo_DL33Resource {

    private final Logger log = LoggerFactory.getLogger(Obbligo_DL33Resource.class);

    @Inject
    private Obbligo_DL33Repository obbligo_DL33Repository;
    
    @Inject
    private ObbligoDl33Service obbligoDl33Service;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * PUT  /obbligo_DL33s/:id/abilita
     */
    @RequestMapping(value = "/obbligo_DL33s/{id}/abilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> abilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request abilita macro_cat_obbligo_DL33 : {}", id);
	    	obbligoDl33Service.abilitaObbligo(id);
	    	JsonObject json = new JsonObject();
	    	json.addProperty("abilitazione", "ok");
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /obbligo_DL33s/:id/disabilita
     */
    @RequestMapping(value = "/obbligo_DL33s/{id}/disabilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disabilita(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request disabilita macro_cat_obbligo_DL33 : {}", id);
	    	obbligoDl33Service.disabilitaObbligo(id);
	    	JsonObject json = new JsonObject();
	    	json.addProperty("disabilitazione", "ok");
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/obbligo_DL33s/{id}/alreadyexist", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkAlreadyExists(
			@PathVariable final Long id,
			@RequestParam(value = "cat_obbligo_DL33id", required = true) String cat_obbligo_DL33id,
			@RequestParam(value = "codice", required = true) String codice,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to check if codice obbligo already exists : {}", cat_obbligo_DL33id, codice);
			boolean exists = obbligoDl33Service.checkIfAlreadyExists(id,Long.parseLong(cat_obbligo_DL33id), codice);
			JsonObject json = new JsonObject();
			json.addProperty("alreadyExists", exists);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

	}

    /**
     * POST  /obbligo_DL33s -> Create a new obbligo_DL33.
     */
    @RequestMapping(value = "/obbligo_DL33s",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> create(@RequestBody Obbligo_DL33 obbligo_DL33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Obbligo_DL33 : {}", obbligo_DL33);
//	        if (obbligo_DL33.getId() != null) {
//	            return ResponseEntity.badRequest().header("Failure", "A new obbligo_DL33 cannot already have an ID").build();
//	        }
	      //controllo che non esista un obbligo con un codice uguale a quello inserito dall'utente per la categoria di appartenenza
	        if(obbligo_DL33.getCodice()!=null && obbligo_DL33.getCat_obbligo_DL33()!=null && obbligo_DL33.getCat_obbligo_DL33().getId()!=null){
	        	 List<Obbligo_DL33> list = obbligo_DL33Repository.findByCategoriaIdECodice(obbligo_DL33.getCat_obbligo_DL33().getId(), obbligo_DL33.getCodice());
	        	 if(list!=null&&list.size()>0){
	        		 for (int i = 0; i < list.size(); i++) {
	        			 Obbligo_DL33 listElemento = (Obbligo_DL33)list.get(i);
		        			if(listElemento.getId().longValue()!=obbligo_DL33.getId().longValue()){
				        		 throw new GestattiCatchedException("Codice già esistente per la categoria di appartenenza");
						        	//return ResponseEntity.badRequest().header("Failure", "Codice già esistente per la categoria di appartenenza").build();
		        			}
	        		 }
			        }
	        }
	        obbligo_DL33Repository.save(obbligo_DL33);
	        return ResponseEntity.created(new URI("/api/obbligo_DL33s/" + obbligo_DL33.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /obbligo_DL33s -> Updates an existing obbligo_DL33.
     */
    @RequestMapping(value = "/obbligo_DL33s",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> update(@RequestBody Obbligo_DL33 obbligo_DL33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Obbligo_DL33 : {}", obbligo_DL33);
	        if (obbligo_DL33.getId() == null) {
	            return create(obbligo_DL33);
	        }
	        if(obbligo_DL33.getCodice()!=null&&obbligo_DL33.getCat_obbligo_DL33()!=null&&obbligo_DL33.getCat_obbligo_DL33().getId()!=null){
	        	 List<Obbligo_DL33> list = obbligo_DL33Repository.findByCategoriaIdECodice(obbligo_DL33.getCat_obbligo_DL33().getId(), obbligo_DL33.getCodice());
	        	 if(list!=null&&list.size()>0){
	        		 for (int i = 0; i < list.size(); i++) {
	        			 Obbligo_DL33 listElemento = (Obbligo_DL33)list.get(i);
		        			if(listElemento.getId().longValue()!=obbligo_DL33.getId().longValue()){
		        				//return ResponseEntity.badRequest().header("Failure", "Codice già esistente").build();
		        				return new ResponseEntity<>("Codice già esistente per la categoria di appartenenza", HttpStatus.BAD_REQUEST);
		        			}
	        		 }
			       }
	        }
	        obbligo_DL33Repository.save(obbligo_DL33);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /obbligo_DL33s -> get all the obbligo_DL33s.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/obbligo_DL33s",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Obbligo_DL33>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "codice", required = false) String codice,
            @RequestParam(value = "descrizione", required = false) String descrizione,
            @RequestParam(value = "attiva", required = false) String attiva,
            @RequestParam(value = "macro_cat_obbligo_DL33", required = false) String macro_cat_obbligo_DL33,
            @RequestParam(value = "schede", required = false) String schede,
            @RequestParam(value = "cat_obbligo_DL33", required = false) String cat_obbligo_DL33) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all Obbligo_DL33s");
	        JsonObject jsonSearch = this.buildSearchObject(codice, descrizione, attiva, cat_obbligo_DL33, macro_cat_obbligo_DL33, schede);
	        return obbligoDl33Service.search(jsonSearch, offset, limit);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String codice, String descrizione, String attiva, String cat_obbligo_DL33, String macro_cat_obbligo_DL33, String schede) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("codice", codice);
	    	json.addProperty("descrizione", descrizione);
	    	json.addProperty("attiva", attiva);
	    	json.addProperty("cat_obbligo_DL33", cat_obbligo_DL33);
	    	json.addProperty("macro_cat_obbligo_DL33", macro_cat_obbligo_DL33);
	    	json.addProperty("schede", schede);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /obbligo_DL33s/:id -> get the "id" obbligo_DL33.
     */
    @RequestMapping(value = "/obbligo_DL33s/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Obbligo_DL33> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Obbligo_DL33 : {}", id);
	        Obbligo_DL33 obbligo_DL33 = obbligo_DL33Repository.findOneWithEagerRelationships(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (obbligo_DL33 == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByObbligoDl33Id(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	obbligo_DL33.setAtti(atti);
	        }
	        return new ResponseEntity<>(obbligo_DL33, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /obbligo_DL33s/:id -> delete the "id" obbligo_DL33.
     */
    @RequestMapping(value = "/obbligo_DL33s/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Obbligo_DL33 : {}", id);
	        obbligo_DL33Repository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
}
