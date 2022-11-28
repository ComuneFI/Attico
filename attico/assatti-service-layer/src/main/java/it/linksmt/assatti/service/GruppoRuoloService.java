package it.linksmt.assatti.service;

import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.repository.GruppoRuoloRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing GruppoRuolo.
 */
@Service
@Transactional
public class GruppoRuoloService {
	private final Logger log = LoggerFactory
			.getLogger(GruppoRuoloService.class);

	@Inject
	private GruppoRuoloRepository gruppoRuoloRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private ProfiloService profiloService;
	

	@Transactional(readOnly = true)
	public GruppoRuolo findOne(final Long id) {
		log.debug("getHasRuoli id" + id);
		GruppoRuolo domain = gruppoRuoloRepository.findOne(id);
		if (domain != null) {
			domain.getHasRuoli().size();

			// domain.setAoo( DomainUtil.minimalAoo(domain.getAoo()));

			if(domain.getHasRuoli() != null){
				for (Ruolo ruolo: domain.getHasRuoli()) {
					ruolo.getDescrizione();
				}
			}
		}
		return domain;
	}
	
	public List<GruppoRuolo> findByDenominazione(String denominazione){
		return gruppoRuoloRepository.findByLikeDenominazione(denominazione);
	}

	public List<GruppoRuolo> findByAooId(Long aooId) {
		List<GruppoRuolo> gruppoRuolos = new ArrayList<GruppoRuolo>();
		BooleanExpression predicateProfilo = QProfilo.profilo.aoo.id.eq(aooId).and(QProfilo.profilo.validita.validoal.isNull());
		Iterable<Profilo> profili_aoo = profiloRepository.findAll(predicateProfilo);
		for (Profilo profilo : profili_aoo) {
			gruppoRuolos.add(profilo.getGrupporuolo());
		}
		return Lists.newArrayList(gruppoRuolos);
	}
	
	public void update(GruppoRuolo gruppoRuolo) throws GestattiCatchedException{
		gruppoRuoloRepository.save(gruppoRuolo);
	}

}
