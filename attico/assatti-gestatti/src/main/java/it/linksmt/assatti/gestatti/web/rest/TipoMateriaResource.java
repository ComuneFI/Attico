package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.joda.time.LocalDate;
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

import it.linksmt.assatti.datalayer.domain.Materia;
import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.TipoMateria;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.MateriaRepository;
import it.linksmt.assatti.datalayer.repository.SottoMateriaRepository;
import it.linksmt.assatti.datalayer.repository.TipoMateriaRepository;
import it.linksmt.assatti.service.TipoMateriaService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing TipoMateria.
 */
@RestController
@RequestMapping("/api")
public class TipoMateriaResource {

    private final Logger log = LoggerFactory.getLogger(TipoMateriaResource.class);

    @Inject
    private TipoMateriaRepository tipoMateriaRepository;
    
    @Inject
    private MateriaRepository materiaRepository;
    
    @Inject
    private SottoMateriaRepository sottoMateriaRepository;

    @Inject
    private TipoMateriaService tipoMateriaService;
    
    @Inject
    private AttoRepository attoRepository;
    
    /**
     * PUT /tipoMaterias/disable -> Disable a tipoMateria
     */
    @RequestMapping(value = "/tipoMaterias/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> disableTipoMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to disable tipoMateria : {}", id);
			JsonObject json = new JsonObject();
			try{
				tipoMateriaService.disableTipoMateria(Long.parseLong(id));
				json.addProperty("stato", "ok");
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    /**
     * PUT /tipoMaterias/enable -> Enable a tipoMateria
     */
    @RequestMapping(value = "/tipoMaterias/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableTipoMateria(
			@PathVariable String id,
			HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to enable tipoMateria : {}", id);
			JsonObject json = new JsonObject();
			try{
				tipoMateriaService.enableTipoMateria(Long.parseLong(id));
				json.addProperty("stato", "ok");
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

    
    /**
     * POST  /tipoMaterias -> Create a new tipoMateria.
     */
    @RequestMapping(value = "/tipoMaterias",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody TipoMateria tipoMateria) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save TipoMateria : {}", tipoMateria);
	        if (tipoMateria.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new tipoMateria cannot already have an ID").build();
	        }
	        if(tipoMateria.getValidita()==null){
	        	tipoMateria.setValidita(new Validita());
	        }
	        if(tipoMateria.getValidita().getValidodal()==null){
	        	tipoMateria.getValidita().setValidodal(new LocalDate());
	        }
	        tipoMateriaRepository.save(tipoMateria);
	        return ResponseEntity.created(new URI("/api/tipoMaterias/" + tipoMateria.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /tipoMaterias -> Updates an existing tipoMateria.
     */
    @RequestMapping(value = "/tipoMaterias",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody TipoMateria tipoMateria) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update TipoMateria : {}", tipoMateria.getAoo());
	        if (tipoMateria.getId() == null) {
	            return create(tipoMateria);
	        }
	        
	        tipoMateriaRepository.save(tipoMateria);
	        
	        for(Materia materia : tipoMateria.getMaterie()){
	        	materia.setAoo(tipoMateria.getAoo());
	        	materiaRepository.save(materia);
	        	
	        	for(SottoMateria sotto : materia.getSottoMaterie()){
	        		sotto.setAoo(tipoMateria.getAoo());
	        		sottoMateriaRepository.save(sotto);
	        	}
	        }
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /tipoMaterias -> get all the tipoMaterias.
     */
    @RequestMapping(value = "/tipoMaterias",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoMateria>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<TipoMateria> page = tipoMateriaRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/tipoMaterias", offset, limit);
	        return new ResponseEntity<List<TipoMateria>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoMaterias -> getByAooId the tipoMaterias.
     */
    @RequestMapping(value = "/tipoMaterias/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoMateria>> getByAooId( @RequestParam(value = "aooId" , required = true) Long aooId)
    		throws GestattiCatchedException {
    	try{
	        List<TipoMateria> page = tipoMateriaRepository.findByAooId(aooId);
	        return new ResponseEntity<List<TipoMateria>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoMaterias -> get all the tipoMaterias.
     */
    @RequestMapping(value = "/tipoMaterias/active",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<TipoMateria>> getActive( @RequestHeader(value="aooId", required=true) Long aooId ,  @RequestHeader(value="profiloId", required=true) Long profiloId )
    		throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to active TipoMateria :aooId"+  aooId +" profiloId:"+profiloId);
	          
	    	Iterable<TipoMateria> page = tipoMateriaService.findAllAoo(aooId, true );
	        
	        return new ResponseEntity<Iterable<TipoMateria>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /tipoMaterias -> get all the activeByAoo.
     */
    @RequestMapping(value = "/tipoMaterias/activeByAoo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<TipoMateria>> getActiveByAoo( @RequestParam(value="aooId", required=true) Long aooId  )
    		throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to active getActiveByAoo :aooId"+  aooId  );
	          
	    	Iterable<TipoMateria> page = tipoMateriaService.findAllAoo(aooId, true );
	        
	        return new ResponseEntity<Iterable<TipoMateria>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    
    /**
     * GET  /tipoMaterias -> get all the tipoMaterias.
     */
    @RequestMapping(value = "/tipoMaterias/gestione",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<TipoMateria>> findAllGestione( @RequestParam(value="aooId", required=true) Long aooId )
    		throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to active TipoMateria :aooId"+  aooId );
	          
	    	Iterable<TipoMateria> page = tipoMateriaService. findAllAoo(aooId, false );
	        
	        return new ResponseEntity<Iterable<TipoMateria>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /tipoMaterias -> get all the tipoMaterias.
     */
    @RequestMapping(value = "/tipoMaterias/aoovuota",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoMateria>> findByAooIsNull( )
    		throws GestattiCatchedException {
    	try{
	    	// List<TipoMateria> page = tipoMateriaService.findByAooIsNull( );
	    	List<TipoMateria> page = tipoMateriaService.findAll();
	        return new ResponseEntity<List<TipoMateria>>(page,  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
   
    /**
     * GET  /tipoMaterias -> get all the tipoMaterias by search values.
     */
    @RequestMapping(value = "/tipoMaterias/search",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<TipoMateria>> findByAooIsNullWithFilter(
    		@RequestParam(value = "tipoRicerca", required = false) String tipoRicerca,
    		@RequestParam(value = "descrizione", required = false) String descrizione, 
    		@RequestParam(value = "aoo", required = false) String aoo,
    		@RequestParam(value = "stato", required = false) String stato)
    				throws GestattiCatchedException {
    	try{
	    	List<TipoMateria> page = tipoMateriaService.findAllByFilter(tipoRicerca, descrizione, aoo, stato);
	        return new ResponseEntity<List<TipoMateria>>(page,  HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    

    /**
     * GET  /tipoMaterias/:id -> get the "id" tipoMateria.
     */
    @RequestMapping(value = "/tipoMaterias/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<TipoMateria> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get TipoMateria : {}", id);
	        TipoMateria tipoMateria = tipoMateriaRepository.findOne(id);
	        Long numAtti = 0l;
	        Boolean atti = Boolean.FALSE;
	        
	        if (tipoMateria == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	numAtti = attoRepository.countByTipomateriaId(id);
	        	if(numAtti>0) {
	        		atti=Boolean.TRUE;
	        	}
	        	tipoMateria.setAtti(atti);
	        }
	        
	        return new ResponseEntity<>(tipoMateria, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /tipoMaterias/:id -> delete the "id" tipoMateria.
     */
    @RequestMapping(value = "/tipoMaterias/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete TipoMateria : {}", id);
	        tipoMateriaRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
}
