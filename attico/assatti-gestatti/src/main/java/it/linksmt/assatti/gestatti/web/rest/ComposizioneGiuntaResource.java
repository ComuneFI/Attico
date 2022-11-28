package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.dto.ProfiloSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing Composizione Giunta.
 */
@RestController
@RequestMapping("/api")
public class ComposizioneGiuntaResource {

    private final Logger log = LoggerFactory.getLogger(ComposizioneGiuntaResource.class);

    @Inject
    private ProfiloService profiloService;
    

    /**
     * PUT  /composizioneGiuntas -> Updates an existing composizioneGiuntas.
     */
    @RequestMapping(value = "/composizioneGiuntas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody final List<Profilo> profili) throws GestattiCatchedException {
    	try{

    		log.debug("Aggiornamento composizione giunta");
    		List<Integer> idxs = new ArrayList<Integer>();
	        //ciclo su ogni profilo con il ruolo componente giunta visualizzato in pagina
			if(profili!=null) {
				for (Profilo profilo : profili) {
					if(profilo!= null && profilo.getOrdineGiunta()!= null && idxs.contains(profilo.getOrdineGiunta())) {
						throw new GestattiCatchedException("Errore nella numerazione ordine Giunta");
					}
					idxs.add(profilo.getOrdineGiunta());
					if(profilo!= null && profilo.getValidoSedutaGiunta()!= null && profilo.getValidoSedutaGiunta().booleanValue() && 
							(profilo.getQualificaProfessionaleGiunta() == null || profilo.getQualificaProfessionaleGiunta().getId() == null)) {
						throw new GestattiCatchedException("Errore: qualifica obbligatoria per i profili validi");
					}
				}
				for (Profilo profilo : profili) {
					if(profilo.getValidoSedutaGiunta()==null) {
						profilo.setValidoSedutaGiunta(new Boolean(false));
					}
			        profiloService.save(profilo);
				}
			}
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    /**
     * GET  /composizioneGiuntas/getComponentiGiuntaConsiglio -> get all the profilos with role.
     */
    @RequestMapping(value = "/composizioneGiuntas/getComponentiGiuntaConsiglio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getComponentiGiuntaConsiglio(
    		@RequestParam(value = "ruolo" , required = true) final String role
    		)
    				throws GestattiCatchedException {
    	try{
	    	ProfiloSearchDTO search = this.buildProfiloSearchDTO(null, null, null, null, null, null, null, role, "0", null);
	    	
	    	ResponseEntity<List<Profilo>> response =  profiloService.search(search, 0, Integer.MAX_VALUE);
	    	
	    	return response;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }

    private ProfiloSearchDTO buildProfiloSearchDTO(final String id, final String descrizione, final String delega, final String tipoAtto,  final String utente, final String aoo, final String qualificaProfessionale,final String ruoli, final String stato, final String profiloAooId) throws GestattiCatchedException{
    	try{
	    	ProfiloSearchDTO search = new ProfiloSearchDTO();
	    	search.setId(id);
	    	search.setDescrizione(descrizione);
	    	search.setDelega(delega);
	    	search.setTipoAtto(tipoAtto);
	    	search.setUtente(utente);
	    	search.setAoo(aoo);
	    	search.setRuoli(ruoli);
	    	search.setQualificaProfessionale(qualificaProfessionale);
	    	search.setStato(stato);
	    	search.setProfiloAooId(profiloAooId);
	    	return search;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}

    }
}
