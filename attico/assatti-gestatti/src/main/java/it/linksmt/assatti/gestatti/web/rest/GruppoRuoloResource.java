package it.linksmt.assatti.gestatti.web.rest;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.aspectj.weaver.IUnwovenClassFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;

import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QGruppoRuolo;
import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.datalayer.repository.GruppoRuoloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.gestatti.web.rest.dto.GruppoRuoloSearchDTO;
import it.linksmt.assatti.service.GruppoRuoloService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.security.AuthoritiesConstants;

/**
 * REST controller for managing GruppoRuolo.
 */
@RestController
@RequestMapping("/api")
public class GruppoRuoloResource {

    private final Logger log = LoggerFactory.getLogger(GruppoRuoloResource.class);

    @Inject
    private GruppoRuoloRepository gruppoRuoloRepository;

    @Inject
    private GruppoRuoloService gruppoRuoloService;

    @Inject
    private RuoloRepository ruoloRepository;
    
    @Inject 
    private ProfiloService profiloService;
    
    @Inject
    private UserService userService;


    /**
     * POST  /gruppoRuolos -> Create a new gruppoRuolo.
     */
    @RequestMapping(value = "/gruppoRuolos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody final GruppoRuolo gruppoRuolo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save GruppoRuolo : {}", gruppoRuolo);
	        if (gruppoRuolo.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new gruppoRuolo cannot already have an ID").build();
	        }
	        gruppoRuoloRepository.save(gruppoRuolo);
	        return ResponseEntity.created(new URI("/api/gruppoRuolos/" + gruppoRuolo.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

    /**
     * PUT  /gruppoRuolos -> Updates an existing gruppoRuolo.
     */
    @RequestMapping(value = "/gruppoRuolos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody final GruppoRuolo gruppoRuolo) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update GruppoRuolo : {}", gruppoRuolo);
	        if (gruppoRuolo.getId() == null) {
	            return create(gruppoRuolo);
	        }
	        gruppoRuoloService.update(gruppoRuolo);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    private GruppoRuoloSearchDTO buildGruppoRuoloSearchDTO(final String id, final String denominazione, final String aoo, final String ruolo, final Long aooId) throws GestattiCatchedException{
    	try{
	    	GruppoRuoloSearchDTO search = new GruppoRuoloSearchDTO();
	    	search.setId(id);
	    	search.setDenominazione(denominazione);
	    	search.setAoo(aoo);
	    	search.setRuolo(ruolo);
	    	search.setAooId(aooId);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /gruppoRuolos -> get all the gruppoRuolos.
     */
    @RequestMapping(value = "/gruppoRuolos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<GruppoRuolo>> getAll(@RequestParam(value = "page" , required = false) final Integer offset,
                                  @RequestParam(value = "per_page", required = false) final Integer limit,
                                  @RequestParam(value = "aoo_id", required = false) final Long aooId,
                                  @RequestParam(value = "idGruppoRuolo", required = false) final String id,
                                  @RequestParam(value = "denominazione", required = false) final String denominazione,
                                  @RequestParam(value = "aoo", required = false) final String aoo,
                                  @RequestParam(value = "ruolo", required = false) final String ruolo,
                                  @RequestParam(value = "idUser", required = false) final Long idUser
                                  )
                                		  throws GestattiCatchedException {
    	try{
	    	GruppoRuoloSearchDTO search = this.buildGruppoRuoloSearchDTO(id, denominazione, aoo, ruolo, aooId);
	    	BooleanExpression predicateGruppoRuolo = QGruppoRuolo.gruppoRuolo.id.isNotNull();
	
			Long idLong = null;
			if(search.getId()!=null && !"".equals(search.getId().trim())){
				try{
					idLong = Long.parseLong(search.getId().trim());
				}catch(Exception e){};
			}
			if(idLong!=null){
				predicateGruppoRuolo = predicateGruppoRuolo.and(QGruppoRuolo.gruppoRuolo.id.eq(idLong));
			}
			if(idUser != null) {
				boolean isAdmin = userService.isLoggedUserAnAdmin() || userService.isLoggedUserAnAdminRP();
				if(!isAdmin) {
					BooleanExpression predicateRuolo =  QRuolo.ruolo.onlyAdmin.isTrue();
					Iterable<Ruolo> ruoli = ruoloRepository.findAll(predicateRuolo, new OrderSpecifier<>(Order.ASC, QRuolo.ruolo.id));
					BooleanExpression internalPredicate = null;
					for(Ruolo r : ruoli){
						if(internalPredicate == null){
							internalPredicate = QGruppoRuolo.gruppoRuolo.hasRuoli.contains(r).not();
						}else{
							internalPredicate = internalPredicate.and(QGruppoRuolo.gruppoRuolo.hasRuoli.contains(r).not());
						}
					}
					if(internalPredicate!=null){
						predicateGruppoRuolo = predicateGruppoRuolo.and(internalPredicate);
					}
				}
			}
			if(search.getDenominazione()!=null && !"".equals(search.getDenominazione().trim())){
				predicateGruppoRuolo = predicateGruppoRuolo.and(QGruppoRuolo.gruppoRuolo.denominazione.containsIgnoreCase(search.getDenominazione().trim()));
			}
			if(search.getRuolo()!=null && !"".equals(search.getRuolo().trim())){
				BooleanExpression predicateRuolo =  QRuolo.ruolo.descrizione.containsIgnoreCase(search.getRuolo().trim());
				Iterable<Ruolo> ruoli = ruoloRepository.findAll(predicateRuolo, new OrderSpecifier<>(Order.ASC, QRuolo.ruolo.id));
				BooleanExpression internalPredicate = null;
				for(Ruolo r : ruoli){
					if(internalPredicate == null){
						internalPredicate = QGruppoRuolo.gruppoRuolo.hasRuoli.contains(r);
					}else{
						internalPredicate = internalPredicate.or(QGruppoRuolo.gruppoRuolo.hasRuoli.contains(r));
					}
				}
				if(internalPredicate!=null){
					predicateGruppoRuolo = predicateGruppoRuolo.and(internalPredicate);
				}
			}
			Page<GruppoRuolo> page = gruppoRuoloRepository.findAll(predicateGruppoRuolo, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/gruppoRuolos", offset, limit);
	    	return new ResponseEntity<List<GruppoRuolo>>(page.getContent(), headers, HttpStatus.OK);
	
	//        if(aooId== null ){
	//        	Page<GruppoRuolo> page = gruppoRuoloRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	//        	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/gruppoRuolos", offset, limit);
	//        	return new ResponseEntity<List<GruppoRuolo>>(page.getContent(), headers, HttpStatus.OK);
	//        }else{
	//        	List<GruppoRuolo> page = gruppoRuoloRepository.findByAooId(aooId );
	//        	return new ResponseEntity<List<GruppoRuolo>>(page, HttpStatus.OK);
	//
	//        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /gruppoRuolos -> getByAooId the gruppoRuolos.
     */
    @RequestMapping(value = "/gruppoRuolos/getByAooId",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<GruppoRuolo>> getByAooId( @RequestParam(value = "aooId" , required = true) final Long aooId)
    		throws GestattiCatchedException {
    	try{
	        List<GruppoRuolo> page = gruppoRuoloService.findByAooId(aooId);
	        return new ResponseEntity<List<GruppoRuolo>>(page, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /gruppoRuolos/:id -> get the "id" gruppoRuolo.
     */
    @RequestMapping(value = "/gruppoRuolos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<GruppoRuolo> get(@PathVariable final Long id, final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get GruppoRuolo : {}", id);
	        GruppoRuolo gruppoRuolo = gruppoRuoloService.findOne(id);
	        if (gruppoRuolo == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(gruppoRuolo, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /gruppoRuolos/:id -> delete the "id" gruppoRuolo.
     */
    @RequestMapping(value = "/gruppoRuolos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> delete(@PathVariable final Long id) throws GestattiCatchedException {
    	JsonObject j = new JsonObject();
    	try{
	    	log.debug("REST request to delete GruppoRuolo : {}", id);
	        gruppoRuoloRepository.delete(id);
	        j.addProperty("success", true);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		j.addProperty("success", false);
			String errore = "Errore generico";
			if (e.getCause() != null && e.getCause() instanceof Exception) {
				Exception ex = (Exception) e.getCause();
				for (int i = 0; i < 3; i++) {
					if (ex instanceof MySQLIntegrityConstraintViolationException) {
						errore = ex.getMessage();
						break;
					} else if (ex.getCause() != null && ex.getCause() instanceof Exception) {
						ex = (Exception) ex.getCause();
					} else {
						break;
					}
				}
			}
			j.addProperty("message", errore);
			return new ResponseEntity<>(j.toString(), HttpStatus.OK);
    	}
    }
}
