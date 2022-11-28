package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.ModelloCampo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QModelloCampo;
import it.linksmt.assatti.datalayer.repository.ModelloCampoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.service.ModelloCampoService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.dto.ModelloCampoSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.ServiceUtil;

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
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * REST controller for managing ModelloCampo.
 */
@RestController
@RequestMapping("/api")
public class ModelloCampoResource {

    private final Logger log = LoggerFactory.getLogger(ModelloCampoResource.class);
    
    @Inject
    private UserService userService;

    @Inject
    private ModelloCampoRepository modelloCampoRepository;
    
    @Inject
    private ProfiloRepository profiloRepository;
    
    @Inject
    private ServiceUtil serviceUtil;
    
    @Inject
    private ModelloCampoService modelloCampoService;
    /**
     * POST  /modelloCampos -> Create a new modelloCampo.
     */
    @RequestMapping(value = "/modelloCampos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody ModelloCampo modelloCampo,
    								   @RequestHeader(value = "profiloId", required = false) Long profiloId ) throws GestattiCatchedException {
    	try{
	        log.debug("REST request to save ModelloCampo : {}", modelloCampo);
	        if (modelloCampo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new modelloCampo cannot already have an ID").build();
	        }
	       
	        System.out.println("PROFILO ID:"+profiloId);
	        Profilo profilo = null;
	        if(profiloId!=null && profiloId != 0L){
	        	profilo = profiloRepository.findOne(profiloId);
	            if (profilo == null) {
	                return ResponseEntity.badRequest().header("Failure", "A new modelloCampo cannot have an Profilo ID").build();
	            }
	
	            if(modelloCampo.getProfilo() == null ){
	                modelloCampo.setProfilo(profilo);
	            }
	        }	
	        if(modelloCampo.getProfilo()!=null && modelloCampo.getProfilo().getId()==null) {
	        	modelloCampo.setProfilo(null);
	        }
//	        if(modelloCampo.getProfilo() == null || modelloCampo.getProfilo().getId() == null){
//	        	if(userService.isLoggedUserAnAdmin() || userService.isLoggedUserAnAdminRP() || modelloCampo.getAoo()==null){
//	        		modelloCampo.setProfilo(null);
//	        	}else{
//	        		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
//	        	}
//	        }
	        if(modelloCampo.getTipoAtto()!=null && modelloCampo.getTipoAtto().getId()!=null && modelloCampo.getTipoAtto().getId().equals(0L)){
	        	modelloCampo.setTipoAtto(null);
	        }
	        if(modelloCampo.getAoo()==null) {
	        	modelloCampo.setPropagazioneAoo(false);
	        }
	        if(modelloCampo.getProfilo() != null ){
	        	if(profilo!=null && profilo.getAoo()!=null) {
	        		modelloCampo.setAoo(profilo.getAoo());
	        	}
	        	modelloCampo.setPropagazioneAoo(false);
	        }
	        modelloCampoRepository.save(modelloCampo);
	        return ResponseEntity.created(new URI("/api/modelloCampos/" + modelloCampo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /modelloCampos -> Updates an existing modelloCampo.
     */
    @RequestMapping(value = "/modelloCampos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody ModelloCampo modelloCampo,
    								   @RequestHeader(value = "profiloId", required = false) Long profiloId ) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update ModelloCampo : {}", modelloCampo);
	        if (modelloCampo.getId() == null) {
	            return create(modelloCampo, profiloId);
	        }
	        
	        if(profiloId!=null && profiloId != 0){
	        	Profilo profilo = profiloRepository.findOne(profiloId);
	            if (profilo == null) {
	                return ResponseEntity.badRequest().header("Failure", "A new modelloCampo cannot have an Profilo ID").build();
	            }
	            
	            if(modelloCampo.getProfilo() != null ){
	                modelloCampo.setAoo(profilo.getAoo());
	                modelloCampo.setPropagazioneAoo(false);
	            }
	            
	            if(!userService.isLoggedUserAnAdminRP() && !userService.isLoggedUserAnAdmin() && !serviceUtil.hasRuolo(profiloId, "ROLE_REFERENTE_TECNICO")){
	                modelloCampo.setProfilo(profilo);
	            }
	        }
	        if(modelloCampo.getProfilo()!=null && modelloCampo.getProfilo().getId()==null) {
	        	modelloCampo.setProfilo(null);
	        }
//	        if((modelloCampo.getProfilo()==null && !userService.isLoggedUserAnAdmin() && !userService.isLoggedUserAnAdminRP() && !serviceUtil.hasRuolo(profiloId, "ROLE_REFERENTE_TECNICO")) || (!userService.isLoggedUserAnAdminRP() && !userService.isLoggedUserAnAdmin() && modelloCampo.getAoo()==null)){
//	        	return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();      	
//	        }
	        if(modelloCampo.getTipoAtto()!=null && modelloCampo.getTipoAtto().getId()!=null && modelloCampo.getTipoAtto().getId().equals(0L)){
	        	modelloCampo.setTipoAtto(null);
	        }
	        if(modelloCampo.getAoo()==null) {
	        	modelloCampo.setPropagazioneAoo(false);
	        }
	        modelloCampoRepository.save(modelloCampo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /modelloCampos -> get all the modelloCampos.
     */
    @RequestMapping(value = "/modelloCampos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ModelloCampo>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "tipoCampo", required = false) String tipoCampo,
                                  @RequestHeader(value = "profiloId", required = true) Long profiloIdLoggato,
                                  @RequestParam(value = "tipoAttoId", required = false) Long tipoAttoId,
                                  @RequestParam(value = "codice", required = false) String codice,
                                  @RequestParam(value = "titolo", required = false) String titolo,
                                  @RequestParam(value = "profilo", required = false) Long profilo
    		)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get modelloCampos profiloId: " + profiloIdLoggato);
	    	ModelloCampoSearchDTO search = this.buildModelloCampoSearchDTO(tipoAttoId, tipoCampo, codice, titolo, profilo, profiloIdLoggato, null, null, null);
	    	Page<ModelloCampo> page = modelloCampoService.getAll(search, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/modelloCampos", offset, limit);
	        return new ResponseEntity<List<ModelloCampo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    private ModelloCampoSearchDTO buildModelloCampoSearchDTO(final Long tipoAttoId, final String tipoCampo, final String codice, final String titolo, final Long profilo, final Long profiloIdLoggato, final Long utenteid, final Long aooIdReferente, final String aoo) throws GestattiCatchedException{
    	try{
	    	ModelloCampoSearchDTO search = new ModelloCampoSearchDTO();
	    	search.setCodice(codice);
	    	search.setProfilo(profilo);
	    	search.setTipoCampo(tipoCampo);
	    	search.setTitolo(titolo);
	    	search.setProfiloIdLoggato(profiloIdLoggato);
	    	search.setUtenteid(utenteid);
	    	search.setTipoAttoId(tipoAttoId);
	    	search.setAooIdReferente(aooIdReferente);
	    	search.setAoo(aoo);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloCampos -> get all the modelloCampos.
     */
    @RequestMapping(value = "/modelloCampos/getAllMixed",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ModelloCampo>> getAllMixed(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "tipoCampo", required = false) String tipoCampo,
                                  @RequestHeader(value = "profiloId", required = true) Long profiloIdLoggato,
                                  @RequestParam(value = "codice", required = false) String codice,
                                  @RequestParam(value = "titolo", required = false) String titolo,
                                  @RequestParam(value = "tipoAttoId", required = false) Long tipoAttoId,
                                  @RequestParam(value = "profilo", required = false) Long profilo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get modelloCamposMixed profiloId: " + profiloIdLoggato);
	    	ModelloCampoSearchDTO search = this.buildModelloCampoSearchDTO(tipoAttoId, tipoCampo, codice, titolo, profilo, profiloIdLoggato, null, null, null);
	    	Page<ModelloCampo> page = modelloCampoService.getAllMixed(search, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/modelloCampos", offset, limit);
	        return new ResponseEntity<List<ModelloCampo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloCampos -> get all the global modelloCampos.
     */
    @RequestMapping(value = "/modelloCampos/getAllGlobal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<ModelloCampo>> getAllGlobal(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "tipoCampo", required = false) String tipoCampo,
                                  @RequestParam(value = "codice", required = false) String codice,
                                  @RequestParam(value = "aoo", required = false) String aoo,
                                  @RequestParam(value = "titolo", required = false) String titolo,
                                  @RequestParam(value = "profilo", required = false) Long profilo,
                                  @RequestParam(value = "tipoAttoId", required = false) Long tipoAttoId,
                                  @RequestParam(value = "utenteid", required = false) Long utenteid,
                                  @RequestParam(value = "aooIdReferente", required = false) Long aooIdReferente,
                                  @RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get modelloCampos globally: ");
	    	ModelloCampoSearchDTO search = this.buildModelloCampoSearchDTO(tipoAttoId, tipoCampo, codice, titolo, profilo, profiloId, utenteid, aooIdReferente, aoo);
	    	Page<ModelloCampo> page = modelloCampoService.getAll(search, PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/modelloCampos", offset, limit);
	        return new ResponseEntity<List<ModelloCampo>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    

    /**
     * GET  /modelloCampos/:id -> get the "id" modelloCampo.
     */
    @RequestMapping(value = "/modelloCampos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ModelloCampo> get(@PathVariable Long id,  
    										@RequestHeader(value = "profiloId", required = true) Long profiloId, 
    										HttpServletResponse response) throws GestattiCatchedException{
    	try{
	        log.debug("REST request to get ModelloCampo : {}", id);
	        log.debug("REST request to get ModelloCampo profiloId : "+ profiloId);
	        ModelloCampo modelloCampo = modelloCampoService.findOne(id, profiloId);
	        if (modelloCampo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }else{
	        	return new ResponseEntity<>(modelloCampo, HttpStatus.OK);
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /modelloCampos/global/:id -> get the "id" modelloCampo.
     */
    @RequestMapping(value = "/modelloCampos/{id}/getGlobal",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ModelloCampo> getGlobal(@PathVariable Long id,  
    										HttpServletResponse response) throws GestattiCatchedException{
    	try{
	        ModelloCampo modelloCampo = modelloCampoService.findOne(id, null);
	        if (modelloCampo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(modelloCampo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /modelloCampos/:id -> delete the "id" modelloCampo.
     */
    @RequestMapping(value = "/modelloCampos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id,
    				   @RequestHeader(value = "profiloId", required = false) Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete ModelloCampo : {}", id);
	        
	        BooleanExpression predicate =  QModelloCampo.modelloCampo.id.eq(id);
	        if(profiloId != null){
	        	if(!userService.isLoggedUserAnAdmin() && !userService.isLoggedUserAnAdminRP() && !serviceUtil.hasRuolo(profiloId, "ROLE_REFERENTE_TECNICO")){
	        		predicate = predicate.and(QModelloCampo.modelloCampo.profilo.id.eq(profiloId));
	        	}
	        }
	        
	        ModelloCampo modelloCampo = modelloCampoRepository.findOne(predicate);
	        modelloCampoRepository.delete(modelloCampo);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
