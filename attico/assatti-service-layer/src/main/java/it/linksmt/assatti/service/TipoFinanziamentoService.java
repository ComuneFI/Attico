package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.QTipoFinanziamento;
import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;
import it.linksmt.assatti.datalayer.repository.TipoFinanziamentoRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoFinanziamentoService {
	private final Logger log = LoggerFactory.getLogger(TipoFinanziamentoService.class);

	@Inject
	private TipoFinanziamentoRepository repository;
	
	@Transactional
	public void disable(Long id) {
		repository.enableDisable(false, id);
	}
	
	@Transactional
	public void enable(Long id) {
		repository.enableDisable(true, id);
	}
	
	@Transactional(readOnly = true)
	public List<TipoFinanziamento> findAllEnabled(){
		BooleanExpression predicate = QTipoFinanziamento.tipoFinanziamento.enabled.eq(true);
		return Lists.newArrayList(repository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public List<TipoFinanziamento> findAll(){
		return Lists.newArrayList(repository.findAll());
	}
	


	@Transactional(readOnly = true)
	public List<TipoFinanziamento> findByCodice(String codice){
		return repository.findByCodice(codice);
	}

	@Transactional(readOnly = true)
	public  TipoFinanziamento findOne(Long id) {
		log.debug("findOne id" + id);
		TipoFinanziamento domain = repository.findOne(id);

		return domain;
	}
	
	@Transactional(readOnly = true)
	public String getDescrizioneByCodiceTipoFinanziamento(String codiceTipoFinanziamento){
		return repository.getDescrizioneByCodiceTipoFinanziamento(codiceTipoFinanziamento);
	}
}
