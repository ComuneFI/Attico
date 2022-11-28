package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.repository.EsitoRepository;

/**
 * Service class for managing esiti.
 */
@Service
@Transactional
public class EsitoService {
	
	@Inject
	private EsitoRepository esitoRepository;
	
	private final Logger log = LoggerFactory.getLogger(EsitoService.class);
	
	@Transactional(readOnly=true)
	public List<Esito> findAll() {
		List<Esito> l = esitoRepository.findAll();
		
		return l;
	}
	
	@Transactional(readOnly = true)
	public Esito findById(String id){
		return esitoRepository.findById(id);
	}
}
