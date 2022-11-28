package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
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

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.datalayer.repository.UtenteRepository;
import it.linksmt.assatti.service.AvanzamentoService;
import it.linksmt.assatti.service.dto.AvanzamentoCriteriaDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Avanzamento.
 */
@RestController
@RequestMapping("/api")
public class AvanzamentoResource {

    private final Logger log = LoggerFactory.getLogger(AvanzamentoResource.class);

    @Inject
    private AvanzamentoRepository avanzamentoRepository;
    
    @Inject
    private UtenteRepository utenteRepository;
    
    @Inject
	private AvanzamentoService avanzamentoService;
    
    
    /**
     * GET  /avanzamentos -> get all the avanzamentos.
     */
    @RequestMapping(value = "/avanzamentos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Avanzamento>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Avanzamento> page = avanzamentoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/avanzamentos", offset, limit);
	        return new ResponseEntity<List<Avanzamento>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    /**
     * GET  /avanzamentos -> get all the avanzamentos.
     */
    @RequestMapping(value = "/avanzamentos/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Avanzamento>> getAll(
    		@Valid @RequestBody final AvanzamentoCriteriaDTO criteria,
    		@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit)
    				throws GestattiCatchedException {
    	try{
	    	Page<Avanzamento> page = null;
	    	if(criteria.getTipoOrinamento().equalsIgnoreCase("desc")){
	    		page = avanzamentoService.findAll(PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, criteria.getOrdinamento())),criteria);
	    	}
	    	else{
	    		page = avanzamentoService.findAll(PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.ASC, criteria.getOrdinamento())),criteria);
	    	}
	    	if(page!=null && page.getContent()!=null){
		        for(Avanzamento av : page.getContent()){
		        	if(av.getAtto()!=null){
		        		Atto a = new Atto();
		        		a.setId(av.getAtto().getId());
		        		a.setOggetto(av.getAtto().getOggetto());
		        		a.setCodiceCifra(av.getAtto().getCodiceCifra());
		        		av.setAtto(a);
		        	}
		        	if(av.getProfilo()!=null) {
		        		av.setProfilo(new Profilo(av.getProfilo().getId()));
		        	}
		        	av.setLettera(null);
		        	av.setParere(null);
		        	av.setVerbale(null);
		        }
	    	}
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/avanzamentos/search", offset, limit);
	        return new ResponseEntity<List<Avanzamento>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /avanzamentos/:id/getByAtto -> get the "id" avanzamento.
     */
    @RequestMapping(value = "/avanzamentos/{id}/getByAtto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Avanzamento>> getByAtto(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Avanzamenti By Atto : {}", id);
	    	
	    	//Atto atto = attoRepository.findOne(id);
	    	List<Avanzamento> avanzamenti = new ArrayList<Avanzamento>();
	    	
	    	/*
	    	 * In ATTICO non gestito
	    	 *
	    	String[] proposte = new String[] {"DEL","SDL","COM","DDL"};
	    	if(atto.getTipoAtto().getCodice().substring(Math.max(atto.getTipoAtto().getCodice().length() - 2, 0)).equals("_A") && atto.getCodicecifraAttoRevocato() != null){
	    		// Delibera
	    		Atto revocato = attoRepository.findByCodiceCifra(atto.getCodicecifraAttoRevocato());
	    		if(revocato != null){
	    			avanzamenti = avanzamentoService.findByAtto_id(revocato.getId());
	    		}
	    	}
	    	*/
	    	
	    	avanzamenti.addAll(avanzamentoService.findByAtto_id(id));
	    	
	    	/*
	    	 * In ATTICO non gestito
	    	 *
	    	if(Arrays.asList(proposte).contains(atto.getTipoAtto().getCodice())){
	    		// Proposta di delibera
	        	List<Atto> collegati = attoRepository.findByCodicecifraAttoRevocato(atto.getCodiceCifra());
	        	if(collegati.size() > 0){
	        		avanzamenti.addAll(avanzamentoService.findByAtto_id(collegati.get(0).getId()));
	        	}
	    	}
	    	*/
	        for (Avanzamento avanzamento : avanzamenti) {
	        	avanzamento.setAtto(null);
	        	if(!avanzamento.getCreatedBy().equalsIgnoreCase("system")){
		        	Utente utente = utenteRepository.findByUsername(avanzamento.getCreatedBy());
		        	avanzamento.setCreatedBy(utente.getCognome() + " " + utente.getNome());
	        	}
	        	if(avanzamento!=null && avanzamento.getProfilo()!=null) {
	        		avanzamento.setProfilo(DomainUtil.minimalProfilo(avanzamento.getProfilo()));
	        	}
	        }
	        
	        return new ResponseEntity<List<Avanzamento>>(avanzamenti, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /avanzamentos/:id -> get the "id" avanzamento.
     */
    @RequestMapping(value = "/avanzamentos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Avanzamento> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Avanzamento : {}", id);
	        Avanzamento avanzamento = avanzamentoRepository.findOne(id);
	        if (avanzamento == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(avanzamento, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /avanzamentos/:id -> delete the "id" avanzamento.
     *
     * OPERAZIONE NON PREVISTA IN ATTICO
     * 
    @RequestMapping(value = "/avanzamentos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws CifraCatchedException{
    	try{
	    	log.debug("REST request to delete Avanzamento : {}", id);
	        avanzamentoRepository.delete(id);
    	}
    	catch(Exception e){
    		throw new CifraCatchedException(e);
    	} 
    }
    */
}
