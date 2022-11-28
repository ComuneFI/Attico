package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
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

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ReportService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing ModelloHtml.
 */
@RestController
@RequestMapping("/api")
public class ModelloHtmlResource{

    private final Logger log = LoggerFactory.getLogger(ModelloHtmlResource.class);
  
    @Inject
    private ModelloHtmlService modelloHtmlService;

    @Inject
    private ReportService reportService;

    
    /**
     * GET  /modelloHtmls -> Get attotest.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/attotest",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Atto> getAttoTest( ) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save getAttoTest : {}" );
	        
	        return new ResponseEntity<Atto>(reportService.getAttoTest(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloHtmls -> Get populate.
     * @throws IOException 
     */
    
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/populate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Void> populate() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createAttoDirigenziale();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popDIR.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popDIR",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popDIR() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createAttoDirigenziale();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popRelataPubblicazione.
     * @throws IOException 
     */
    
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popRelataPubblicazione",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popRelataPubblicazione() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createRelataPubblicazione();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popReportRicerca.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/popReportRicerca",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popReportRicerca() throws GestattiCatchedException {
    	try{
	    	modelloHtmlService.createReportRicerca();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloHtmls -> Get popDEL.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popDEL",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popDEL() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createDeliberaGiunta();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popSDL.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popSDL",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popSDL() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createSchemaDisegnoLegge();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popCOM.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popCOM",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popCOM() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createComunicazione();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popDDL.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popDDL",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popDDL() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createDisegnoLegge();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popRFT.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popRFT",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popRFT() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createRefertoTecnico();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popORD.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popORD",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popORD() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createOrdinanza();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popDPR.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popDPR",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popDPR() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createDecretoPresidente();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popOdG.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popParere",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popParere() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createParere();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popSchedaAnagraficoContabile.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popSchedaAnagraficoContabile",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popSchedaAnagraficoContabile() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createSchedaAnagraficoContabile();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */

    /**
     * GET  /modelloHtmls -> Get popAttoInesistente.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popAttoInesistente",
    		method = RequestMethod.GET,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popAttoInesistente() throws CifraCatchedException {
    	try{
    		modelloHtmlService.createAttoInesistente();
    		
    		return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popRestituzioneSuIstanzaUfficioProponente.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popRestituzioneSuIstanzaUfficioProponente",
    		method = RequestMethod.GET,
    		produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popRestituzioneSuIstanzaUfficioProponente() throws CifraCatchedException {
    	try{
    		modelloHtmlService.createRestituzioneSuIstanzaUfficioProponente();
    		
    		return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popLettera.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsto
	 * 
    @RequestMapping(value = "/modelloHtmls/popLettera",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popLettera() throws CifraCatchedException {
    	try{
	    	modelloHtmlService.createLettera();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popVerbaleGiunta.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/popVerbaleGiunta",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popVerbaleGiunta() throws GestattiCatchedException {
    	try{
	    	modelloHtmlService.createVerbaleGiunta();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloHtmls -> Get popVerbaleConsiglio.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/popVerbaleConsiglio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popVerbaleConsiglio() throws GestattiCatchedException {
    	try{
	    	modelloHtmlService.createVerbaleConsiglio();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloHtmls -> Get popResoconto.
     * @throws IOException 
     */
    /*
	 * TODO: In ATTICO non previsti
	 * 
    @RequestMapping(value = "/modelloHtmls/popResoconto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popResoconto() throws CifraCatchedException{
    	try{
	    	modelloHtmlService.createResoconto();
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /**
     * GET  /modelloHtmls -> Get popOdgGiunta.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/popOdgGiunta",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popOdgGiunta() throws GestattiCatchedException{
    	try{
	    	modelloHtmlService.createOdgGiunta();
	        
	        return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloHtmls -> Get popOdgConsiglio.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls/popOdgConsiglio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    
    public ResponseEntity<Void> popOdConsiglio() throws GestattiCatchedException{
    	try{
	    	modelloHtmlService.createOdgConsiglio();
	        
	        return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /modelloHtmls -> Create a new modelloHtml.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ModelloHtml> create(@RequestBody ModelloHtml modelloHtml) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save ModelloHtml : {}", modelloHtml);
	        if (modelloHtml.getId() != null) {
	            //return ResponseEntity.badRequest().header("Failure", "A new modelloHtml cannot already have an ID").build();
	        	new ResponseEntity<>(HttpStatus.BAD_REQUEST).getHeaders().set("Failure", "A new modelloHtml cannot already have an ID");
	        }
	        modelloHtmlService.save(modelloHtml);
	       // return ResponseEntity.created(new URI("/api/modelloHtmls/" + modelloHtml.getId())).build();
	        return new ResponseEntity<>(modelloHtml, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /modelloHtmls -> Updates an existing modelloHtml.
     * @throws IOException 
     */
    @RequestMapping(value = "/modelloHtmls",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ModelloHtml> update(@RequestBody ModelloHtml modelloHtml) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update ModelloHtml : {}", modelloHtml);
	        if (modelloHtml.getId() == null) {
	            return create(modelloHtml);
	        }else{
	        	modelloHtmlService.save(modelloHtml);
	        }
	        return new ResponseEntity<>(modelloHtml, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /modelloHtmls -> get all the modelloHtmls.
     * @throws URISyntaxException 
     */
    @RequestMapping(value = "/modelloHtmls",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ModelloHtml>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestParam(value = "titolo", required = false) String titolo,
            @RequestParam(value = "tipoDocumento", required = false) String tipoDocumento,
            @RequestParam(value = "tipoAtto", required = false) String tipoAtto) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get all ModelloHtmls");
	        JsonObject jsonSearch = this.buildSearchObject(titolo, tipoDocumento, tipoAtto);
	        ResponseEntity<List<ModelloHtml>> list = modelloHtmlService.search(jsonSearch, offset, limit);
	        if(list!=null && list.getBody()!=null && list.getBody().size() > 0) {
	        	for(ModelloHtml mod : list.getBody()) {
	        		if(mod!=null) {
	        			mod.setHtml(null);
	        		}
	        	}
	        }
	        return list;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private JsonObject buildSearchObject(String titolo, String tipoDocumento, String tipoAtto) throws GestattiCatchedException{
    	try{
	    	JsonObject json = new JsonObject();
	    	json.addProperty("titolo", titolo);
	    	json.addProperty("tipoDocumento", tipoDocumento);
	    	json.addProperty("tipoAtto", tipoAtto);
	    	return json;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /modelloHtmls/:id -> get the "id" modelloHtml.
     */
    @RequestMapping(value = "/modelloHtmls/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ModelloHtml> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get ModelloHtml : {}", id);
	        ModelloHtml modelloHtml = modelloHtmlService.findOne(id);
	        if (modelloHtml == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	       
	        return new ResponseEntity<>(modelloHtml, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /modelloHtmls/:id -> delete the "id" modelloHtml.
     */
    @RequestMapping(value = "/modelloHtmls/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ModelloHtml : {}", id);
	        modelloHtmlService.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}  
    }
}
