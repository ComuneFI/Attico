package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.InstantMessage;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.InstantMessageRepository;
import it.linksmt.assatti.service.dto.MessaggioDTO;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class InstantMessageService {
	private final Logger log = LoggerFactory.getLogger(InstantMessageService.class);
	
	@Inject
	private UtenteService utenteService;

	@Inject
	private InstantMessageRepository instantMessageRepository;
	
	@Transactional
	public void nuovoMessaggio(MessaggioDTO messaggio){
		Set<String> destinatari = new HashSet<String> ();
		if((messaggio.getAoos()==null || messaggio.getAoos().size() == 0) && (messaggio.getDestinatari()==null || messaggio.getDestinatari().size() == 0)){
			List<Utente> utenti = utenteService.findUtentiLoggati();
			for(Utente utente : utenti){
				destinatari.add(utente.getUsername());
			}
		}else{
			if(messaggio.getAoos()!=null && messaggio.getAoos().size() > 0){
				Set<Utente> utenti = utenteService.findLoggatiByAoosId(messaggio.getAoos());
				for(Utente utente : utenti){
					destinatari.add(utente.getUsername());
				}
			}
			if(messaggio.getDestinatari()!=null && messaggio.getDestinatari().size() > 0){
				destinatari.addAll(messaggio.getDestinatari());
			}
		}
		List<InstantMessage> entities = new ArrayList<InstantMessage>();
		for(String username : destinatari){
			InstantMessage im = new InstantMessage();
			im.setColore(messaggio.getLevel());
			im.setTesto(messaggio.getTesto());
			im.setTimeInvio(new LocalDate());
			im.setUsername(username);
			entities.add(im);
		}
		if(!entities.isEmpty()){
			instantMessageRepository.save(entities);
		}
	}
	
	@Transactional
	public void cleanInstantMessage() {
		instantMessageRepository.cleanInstantMessage();
	}
	
	@Transactional
	public List<InstantMessage> getMessagesForUser(String username){
		List<InstantMessage> messages = new ArrayList<InstantMessage>();
		if(instantMessageRepository.countMessagesForUser(username) > 0){
			messages = instantMessageRepository.getMessagesForUser(username);
		}
		for(InstantMessage message : messages){
			instantMessageRepository.deleteInstantMessage(message.getId());
		}
		return messages;
	}
	
}
