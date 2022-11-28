package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Sezione;
import it.linksmt.assatti.datalayer.repository.SezioneRepository;

/**
 * Service class for managing sezioni.
 */
@Service
@Transactional
public class SezioneService {

	private final Logger log = LoggerFactory.getLogger(SezioneService.class);

	@Inject
	private SezioneRepository sezioneRepository;
	
	@Transactional(readOnly=true)
	public List<Sezione> findAll(){
		return sezioneRepository.findAll();
	}
	
}
