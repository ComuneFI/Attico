package it.linksmt.assatti.service;

import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class RuoloService {
	private final Logger log = LoggerFactory.getLogger(RuoloService.class);

	@Inject
	private RuoloRepository ruoloRepository;
	
	@Transactional
	public void disable(Long id) {
		ruoloRepository.enableDisable(false, id);
	}
	
	@Transactional
	public void enable(Long id) {
		ruoloRepository.enableDisable(true, id);
	}
	
	@Transactional(readOnly = true)
	public List<Ruolo> findAllEnabled(){
		BooleanExpression predicate = QRuolo.ruolo.enabled.eq(true);
		return Lists.newArrayList(ruoloRepository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public boolean isRoleUsedInGruppoRuolo(Long roleId){
		Ruolo ruolo = ruoloRepository.findOne(roleId);
		if(ruolo == null){
			return false;
		}else{
			return ruoloRepository.countGruppiRuoloOfRuolo(roleId) > 0;
		}
	}

	@Transactional(readOnly = true)
	public List<Ruolo> findByCodice(String codice){
		return ruoloRepository.findByCodice(codice);
	}

	@Transactional(readOnly = true)
	public  Ruolo findOne(Long id) {
		log.debug("findOne id" + id);
		Ruolo domain = ruoloRepository.findOne(id);

		return domain;
	}
	
	@Transactional(readOnly = true)
	public String getDescrizioneByCodiceRuolo(String codiceRuolo){
		return ruoloRepository.getDescrizioneByCodiceRuolo(codiceRuolo);
	}
}
