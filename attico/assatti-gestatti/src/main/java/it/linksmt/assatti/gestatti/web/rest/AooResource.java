package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collection;
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
import org.springframework.security.crypto.codec.Base64;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicRicercaDto;
import it.linksmt.assatti.datalayer.domain.dto.AooDto;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Aoo.
 */
@RestController
@RequestMapping("/api")
public class AooResource {

    private final Logger log = LoggerFactory.getLogger(AooResource.class);

    @Inject
    private AooService aooService;
    
    @Inject
    private ProfiloService profiloService;

      /**
  	 * GET /rest/faces/:id -> get the "id" face.
  	 */
  	@RequestMapping(value = "/aoos/{id}/photo", method = RequestMethod.GET )
  	@Timed
  	public ResponseEntity<byte[]> logo(@PathVariable Long id,
  			HttpServletResponse response) throws GestattiCatchedException{
  		try{
	  		log.debug("REST request to get Face : {}", id);
	  		Aoo aoo  = aooService.findOne(id);
	  		if (aoo == null) {
	  			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
	  		String logo = aoo.getLogo();
	  		logo = logo.substring(22);
	  		log.debug("logo:"+logo);
	  		byte[] imgByteArray = Base64.decode(logo.getBytes());
	
	  		return new ResponseEntity<>(imgByteArray, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * GET /aoos/getDirezioneOfAoo/
  	 */
  	@RequestMapping(value = "/aoos/getDirezioneOfAoo", method = RequestMethod.GET )
  	@Timed
  	public ResponseEntity<AooBasicDto> direzioneOfAoo(
  			@RequestParam(value = "aooId" , required = false) Long id,
  			HttpServletResponse response) throws GestattiCatchedException{
  		try{
  			AooBasicDto dto = aooService.getAooDirezioneBasic(id);
	  		return new ResponseEntity<>(dto, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * GET /aoos/getDirezioni/
  	 */
  	@RequestMapping(value = "/aoos/getDirezioni", method = RequestMethod.GET )
  	@Timed
  	public ResponseEntity<List<AooBasicDto>> getDirezioni(
  			@RequestParam(value = "aooid" , required = false) Long id,
  			HttpServletResponse response) throws GestattiCatchedException{
  		try{
  			List<AooBasicDto> dtos = aooService.getDirezioniBasic();
	  		return new ResponseEntity<>(dtos, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * GET /aoos/getAllDirezioni/
  	 */
  	@RequestMapping(value = "/aoos/getAllDirezioni", method = RequestMethod.GET )
  	@Timed
  	public ResponseEntity<List<AooBasicRicercaDto>> getAllDirezioni(
  			@RequestParam(value = "aooid" , required = false) Long id,
  			HttpServletResponse response) throws GestattiCatchedException{
  		try{
  			List<AooBasicRicercaDto> dtos = aooService.getAllDirezioniBasic();
	  		return new ResponseEntity<>(dtos, HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	/**
  	 * GET /aoos/codiceExists/
  	 */
  	@RequestMapping(value = "/aoos/codiceExists", method = RequestMethod.GET )
  	@Timed
  	public ResponseEntity<String> codiceExists(
  			@RequestParam(value = "aooid" , required = false) Long id,
  			@RequestParam(value = "codice" , required = false) String codice,
  			HttpServletResponse response) throws GestattiCatchedException{
  		try{
  			String risultato = "";
	  		if(aooService.checkIfCodiceExists(id, codice)){
	  			risultato = "y";
	  		}else{
	  			risultato = "n";
	  		}
	  		JsonObject json = new JsonObject();
			json.addProperty("exists", risultato);
	  		return new ResponseEntity<>(json.toString(), HttpStatus.OK);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	 	

    /**
     * POST  /aoos -> Create a new aoo.
     */
    @RequestMapping(value = "/aoos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody Aoo aoo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Aoo : {}", aoo);
	        if (aoo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new aoo cannot already have an ID").build();
	        }
	        aooService.save(aoo);
	        return ResponseEntity.created(new URI("/api/aoos/" + aoo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /aoos -> Updates an existing aoo.
     */
    @RequestMapping(value = "/aoos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody Aoo aoo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Aoo : {}", aoo);
	        if (aoo.getId() == null) {
	            return create(aoo);
	        }
	        aooService.save(aoo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /aoos -> Updates the parent of an existing aoo.
     */
    @RequestMapping(value = "/aoos/{id}/parentUpd",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateParent(@Valid @RequestBody Aoo aoo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Aoo : {}", aoo);
	    	
	    	aooService.updateAooPadre(aoo);
	        return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
   
    /**
     * parentAnnulla  /aoos/parentAnnulla/:id -> parentAnnulla the "id" aoo.
     */
    @RequestMapping(value = "/aoos/{id}/parentAnnulla",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void parentAnnulla(@PathVariable Long id) throws GestattiCatchedException{
    	try{
    		aooService.annullaAooPadre(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    
    /**
     * GET  /aoos/getAllEnabled -> get all the aoos enabled.
     */
    @RequestMapping(value = "/aoos/getAllEnabled",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> getAllEnabled() throws GestattiCatchedException {
    	try{
	    	List<Aoo> aoos = aooService.getAllEnabled();
	        return new ResponseEntity<List<Aoo>>(aoos, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }    

    /**
     * GET  /aoos/getAllEnabled -> get all the aoos enabled.
     */
    @RequestMapping(value = "/aoos/{id}/queryRicorsiva",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> queryRicorsiva(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	List<Aoo> aoos = aooService.getAooRicorsiva(id);
	        return new ResponseEntity<List<Aoo>>(aoos, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    

    /**
     * GET  /aoos -> get all the aoos.
     */
    @RequestMapping(value = "/aoos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit 
    		)throws GestattiCatchedException {
    	try{
	    	Page<Aoo> page = aooService.findAll( PaginationUtil.generatePageRequest(offset, limit) );
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/aoos", offset, limit);
	        return new ResponseEntity<List<Aoo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /aoos/minimal -> get all the aoos.
     */
    @RequestMapping(value = "/aoos/minimal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AooDto>> getAllMinimal(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit 
    		)throws GestattiCatchedException {
    	try{
	    	Page<AooDto> page = aooService.findAllDto( PaginationUtil.generatePageRequest(offset, limit) );
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/aoos", offset, limit);
	        return new ResponseEntity<List<AooDto>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /aoos/findById -> get all the aoos with given aoo_id list.
     */
    @RequestMapping(value = "/aoos/findById",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> findById(
    		@RequestParam(value = "listIdAoo" , required = false) List<Long> listIdAoo
    		) throws GestattiCatchedException {
    	try {
	    	List<Aoo> listAoo = aooService.findById(listIdAoo);
	        return new ResponseEntity<List<Aoo>>(listAoo, HttpStatus.OK);
    	} catch(Exception e) {
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /aoos -> get all the aoos.
     */
    @RequestMapping(value = "/aoos/organigramma",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Collection<AooDto>> getOrganigramma( 
    		@RequestParam(value = "disattivate", required = false) Boolean disattivate,
    		@RequestParam(value = "tipologia" , required = false) String tipoAoo,
    		@RequestParam(value = "descrizione" , required = false) String descrizione,
    		@RequestParam(value = "assessorato" , required = false) String assessorato,
    		@RequestParam(value = "stato" , required = false) String stato)
    	throws GestattiCatchedException {
    	try{
    		Collection<AooDto> page = aooService.findByAooPadreIsNull( disattivate,descrizione,tipoAoo,assessorato,stato );
	        return new ResponseEntity<Collection<AooDto>>(page ,  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /aoos -> get all the aoos.
     */
    @RequestMapping(value = "/aoos/padri",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Aoo>> padriaoos( )
    	throws GestattiCatchedException {
    	try{
	    	List<Aoo> page = aooService.findPadri( );
	        return new ResponseEntity<List<Aoo>>(page,  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /aoos -> get all the aoos.
     */
    @RequestMapping(value = "/aoos/nodipadre",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Aoo>> nodipadreaoos( )
        throws URISyntaxException {
        Iterable<Aoo> page = aooService.findNodiPadre( );
        return new ResponseEntity<Iterable<Aoo>>(page,  HttpStatus.OK);
    }
    
    /**
     * GET  /aoos -> get all the aoos.
     */
    @RequestMapping(value = "/aoos/{id}/sezioni",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Aoo>> sezioniaoos(@PathVariable Long id, HttpServletResponse response )
        throws URISyntaxException {
        Iterable<Aoo> page = aooService.findSezioniByAooPadreId(id, false);
        
        
        return new ResponseEntity<Iterable<Aoo>>(page,  HttpStatus.OK);
    }
    
    /**
     * GET  /aoos -> get all the assessorato from aoo.
     */
    @RequestMapping(value = "/aoos/{id}/assessorati",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<Assessorato>> assessoratiaoos(@PathVariable Long id, HttpServletResponse response )
        throws URISyntaxException {
        Iterable<Assessorato> page = aooService.findAssessoratiByAooId(id);
        
        
        return new ResponseEntity<Iterable<Assessorato>>(page,  HttpStatus.OK);
    }
    
    /**
     * GET  /aoos/:id -> get the "id" aoo.
     */
    @RequestMapping(value = "/aoos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Aoo> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Aoo : {}", id);
	        Aoo aoo = aooService.findOne(id);
	        if (aoo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        else{
	        	if(aoo.getProfiloResponsabile() != null){
	        		aoo.getProfiloResponsabile().setAoo(null);
	        		aoo.getProfiloResponsabile().setHasQualifica(null);
	        		aoo.getProfiloResponsabile().getUtente().setAoos(null);
	        	}
	        }
	        return new ResponseEntity<>(aoo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * DELETE  /aoos/:id -> delete the "id" aoo.
     */
    @RequestMapping(value = "/aoos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete Aoo : {}", id);
	        aooService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * ABILITA  /aoos/abilita/:id -> abilita the "id" aoo.
     */
    @RequestMapping(value = "/aoos/{id}/abilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> abilita(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to abilita Aoo : {}", id);
	    	boolean warnCodice = false;
	    	Aoo aooDaAbilitare = aooService.findOne(id);
	    	Iterable<Aoo> aoos = aooService.findByCodice(aooDaAbilitare.getCodice());
	    	if(aoos!=null){
	    		for(Aoo aoo : aoos){
	    			if(!id.equals(aoo.getId()) && aoo.getValidita()!=null && aoo.getValidita().getValidoal()==null){
	    				warnCodice = true;
	    				break;
	    			}
	    		}
	    	}
	    	if(!warnCodice){
	    		aooService.abilita(id);
	    	}
	    	JsonObject json = new JsonObject();
	    	json.addProperty("warnCodice", warnCodice);
	    	return new ResponseEntity<String>(json.toString(),  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * DISABILITA  /aoos/disabilita/:id -> disabilita the "id" aoo.
     */
    @RequestMapping(value = "/aoos/{id}/disabilita",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String>  disabilita(@PathVariable Long id) throws GestattiCatchedException{
    	try{
    		boolean warnCodice = false;
    		List<Long> aooIds = new ArrayList<Long>();
    		aooIds.add(id);
			List<Profilo> profs = profiloService.findActiveByAooIds(aooIds);
    		warnCodice = profs!= null && profs.size()>0;
    		if(!warnCodice){
    			log.debug("REST request to disabilita Aoo : {}", id);
    	        aooService.disabilita(id);
	    	}
    		JsonObject json = new JsonObject();
	    	json.addProperty("warnCodice", warnCodice);
	    	return new ResponseEntity<String>(json.toString(),  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
