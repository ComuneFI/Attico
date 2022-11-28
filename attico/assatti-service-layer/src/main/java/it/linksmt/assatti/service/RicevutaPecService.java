package it.linksmt.assatti.service;


import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.RicevutaPec;
import it.linksmt.assatti.datalayer.repository.RicevutaPecRepository;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class RicevutaPecService {
	@Inject
    private RicevutaPecRepository ricevutaPecRepository;
	

	private final Logger log = LoggerFactory.getLogger(RicevutaPecService.class);

	@Transactional
	public boolean existMessageId(String messageId){
		Long count = ricevutaPecRepository.countByMessageId(messageId);
		if(count == null || count > 0L){
			return true;
		}else{
			return false;
		}
	}
	
	@Transactional
	public void salva(RicevutaPec ricevuta){
		ricevutaPecRepository.save(ricevuta);
	}
}
