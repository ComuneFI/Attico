package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Campo;
import it.linksmt.assatti.datalayer.repository.CampoRepository;

/**
 * Service class for managing campi.
 */
@Service
@Transactional
public class CampoService {

	private final Logger log = LoggerFactory.getLogger(CampoService.class);

	@Inject
	private CampoRepository campoRepository;
	
	@Transactional(readOnly=true)
	public List<Campo> findAll(){
		return campoRepository.findAll();
	}
	
}
