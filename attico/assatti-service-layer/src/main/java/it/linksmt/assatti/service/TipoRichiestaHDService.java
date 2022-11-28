package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.repository.TipoRichiestaHDRepository;

/**
 * Service class for managing TipoRichiestaHDs.
 */
@Service
@Transactional
public class TipoRichiestaHDService {

	@Inject
	private TipoRichiestaHDRepository tipoRichiestaHDRepository;
	
	@Transactional
	public void disable(Long id) {
		tipoRichiestaHDRepository.enableDisable(false, id);
	}
	
	@Transactional
	public void enable(Long id) {
		tipoRichiestaHDRepository.enableDisable(true, id);
	}
}
