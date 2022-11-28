package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.TaskPerRicerca;
import it.linksmt.assatti.datalayer.domain.TipologiaRicerca;
import it.linksmt.assatti.datalayer.repository.TipologiaRicercaRepository;

/**
 * Service class for managing GruppoRuolo.
 */
@Service
@Transactional
public class TipologiaRicercaService {
	private final Logger log = LoggerFactory
			.getLogger(TipologiaRicercaService.class);

	@Inject
	private TipologiaRicercaRepository tipologiaRicercaRepository;
	
	@Transactional(readOnly = true)
	public TipologiaRicerca findOne(final Long id) {
		log.debug("getTasksPerRicerca id" + id);
		TipologiaRicerca domain = tipologiaRicercaRepository.findOneWithEagerRelationships(id);
		if (domain != null) {
			domain.getTasksPerRicerca().size();

			if(domain.getTasksPerRicerca() != null){
				for (TaskPerRicerca task: domain.getTasksPerRicerca()) {
					task.getDescrizione();
				}
			}
		}
		return domain;
	}
	
	public List<TipologiaRicerca> findByCode(String code){
		return tipologiaRicercaRepository.findByLikeCode(code);
	}

}
