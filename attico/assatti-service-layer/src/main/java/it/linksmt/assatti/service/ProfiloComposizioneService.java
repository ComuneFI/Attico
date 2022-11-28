package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.ProfiloComposizione;
import it.linksmt.assatti.datalayer.repository.ProfiloComposizioneRepository;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * Service class for managing ProfiloComposizione.
 */
@Service
@Transactional
public class ProfiloComposizioneService {
	private final Logger log = LoggerFactory
			.getLogger(ProfiloComposizioneService.class);

	@Inject
	private ProfiloComposizioneRepository profiloComposizioneRepository;
	

	@Transactional(readOnly = true)
	public ProfiloComposizione findOne(final Long id) {
		log.debug("getProfiloComposizione id" + id);
		ProfiloComposizione domain = profiloComposizioneRepository.findOne(id);
		
		return domain;
	}
	
	@Transactional
	public void saveOrupdate(ProfiloComposizione profiloComposizione) throws GestattiCatchedException{
		profiloComposizioneRepository.save(profiloComposizione);
	}
	
	
	
	

}
