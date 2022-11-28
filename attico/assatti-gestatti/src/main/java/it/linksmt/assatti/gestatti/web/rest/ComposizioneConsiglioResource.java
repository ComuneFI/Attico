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
 * REST controller for managing Composizione Consiglio.
 */
@RestController
@RequestMapping("/api")
public class ComposizioneConsiglioResource {

    private final Logger log = LoggerFactory.getLogger(ComposizioneConsiglioResource.class);

    @Inject
    private ProfiloService profiloService;
    

    /**
     * PUT  /composizioneConsiglios -> Updates an existing composizioneConsiglios.
     */
    @RequestMapping(value = "/composizioneConsiglios",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@Valid @RequestBody final List<Profilo> profili) throws GestattiCatchedException {
    	try{

    		log.debug("Aggiornamento composizione consiglio");
    		List<Integer> idxs = new ArrayList<Integer>();
	        //ciclo su ogni profilo con il ruolo componente consiglio visualizzato in pagina
			if(profili!=null) {
				for (Profilo profilo : profili) {
					if(profilo!= null && profilo.getOrdineConsiglio()!= null && idxs.contains(profilo.getOrdineConsiglio())) {
						throw new GestattiCatchedException("Errore nella numerazione ordine Consiglio");
					}
					idxs.add(profilo.getOrdineConsiglio());
					if(profilo!= null && profilo.getValidoSedutaConsiglio()!= null && profilo.getValidoSedutaConsiglio().booleanValue() && 
							(profilo.getQualificaProfessionaleConsiglio()== null || profilo.getQualificaProfessionaleConsiglio().getId() == null)) {
						throw new GestattiCatchedException("Errore: qualifica obbligatoria per i profili validi");
					}
				}
				for (Profilo profilo : profili) {
					if(profilo.getValidoSedutaConsiglio()==null) {
						profilo.setValidoSedutaConsiglio(new Boolean(false));
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
     * GET  /composizioneConsiglios/getComponentiGiuntaConsiglio -> get all the profilos with role.
     */
    @RequestMapping(value = "/composizioneConsiglios/getComponentiGiuntaConsiglio",
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
