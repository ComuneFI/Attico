package it.linksmt.assatti.gestatti.web.websocket;

import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.service.UtenteService;
import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.dto.MessaggioDTO;;

/**
 * REST controller for managing News.
 */
@RestController
@RequestMapping("/api")
public class MessaggioService {

    private final Logger log = LoggerFactory.getLogger(MessaggioService.class);
    
	@Autowired
	private SimpMessagingTemplate messagingTemplate;
	
	@Inject
	private UtenteService utenteService;

    /**
     * POST  /messaggios -> Create a new messaggio.
     */
    @RequestMapping(value = "/messaggios",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody MessaggioDTO messaggio) throws URISyntaxException {
    	log.debug("REST request to send Messaggio : {}", messaggio);
        
    	if(messaggio.getAoos()!=null && messaggio.getAoos().size() > 0){
    		//invio ai destinatari, gli utenti loggati appartenenti alle aoo selezionate
    		Set<Utente> utenti = utenteService.findLoggatiByAoosId(messaggio.getAoos());
    		if(utenti != null && utenti.size() > 0){
    			if(messaggio.getDestinatari() == null){
    				messaggio.setDestinatari(new HashSet<String>());
    			}
				for(Utente utente : utenti){
					messaggio.getDestinatari().add(utente.getUsername());
				}
    		}
    	}else if(messaggio.getDestinatari() == null || messaggio.getDestinatari().size() == 0){
    		//invio a tutti i loggati
    		List<Utente> utenti = utenteService.findUtentiLoggati();
    		if(utenti != null && utenti.size() > 0){
    			if(messaggio.getDestinatari() == null){
    				messaggio.setDestinatari(new HashSet<String>());
    			}
				for(Utente utente : utenti){
					messaggio.getDestinatari().add(utente.getUsername());
				}
    		}    		
    	}
    	
        messaggio.setIdentifier(UUID.randomUUID().toString());
        messagingTemplate.convertAndSend("/topic/messaggio", messaggio);
        
        return ResponseEntity.ok().build();
    }

}
