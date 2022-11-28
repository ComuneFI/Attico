package it.linksmt.assatti.service;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.QSottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreAttoRepository;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class SottoScrittoreAttoService {

	private final Logger log = LoggerFactory.getLogger(SottoScrittoreAttoService.class);

	@Inject
	private SottoscrittoreAttoRepository sottoscrittoreAttoRepository;

	@Transactional(readOnly = true)
	public Iterable<SottoscrittoreAtto> getSottoscrittoriByAttoId(Long attoId){
		BooleanExpression predicate = QSottoscrittoreAtto.sottoscrittoreAtto.atto.id.eq(attoId).and(QSottoscrittoreAtto.sottoscrittoreAtto.profilo.validita.validoal.isNull().and(QSottoscrittoreAtto.sottoscrittoreAtto.enabled.eq(true)));
		return sottoscrittoreAttoRepository.findAll(predicate);
	}

	/*
	 * Per ATTICO esclusa gestione sottoscrittori
	 *
	@Transactional
	public void save(Atto atto, SottoscrittoreAtto sottoscrittoreAtto) {
		int order = 0;
		if( (sottoscrittoreAtto.getOrdineFirma() == null) && (atto.getSottoscrittori() != null) ) {
			order = atto.getSottoscrittori().size() + 1;
			sottoscrittoreAtto.setOrdineFirma(order);
		}

		if(sottoscrittoreAtto.getProfilo() != null && sottoscrittoreAtto.getProfilo().equals( atto.getProfilo()) ){
			sottoscrittoreAtto.setEditor(true);
		}

		 sottoscrittoreAttoRepository.save(sottoscrittoreAtto);
	}
	*/

	/**
	 * Aggiunge un sottoscrittore. Si presuppone che il sottoscrittore inserito come parametro abbia l'ordine già correttamente preimpostato
	 * @param sottoscrittoreAtto
	 */
	@Transactional
	public void addSottoscrittore(SottoscrittoreAtto sottoscrittoreAtto) {		
		sottoscrittoreAtto.setEditor(false);
		sottoscrittoreAttoRepository.save(sottoscrittoreAtto);
	}
	
	@Transactional
	public void deleteByAttoId(Long attoId) {		
		sottoscrittoreAttoRepository.deleteByAttoId(attoId);
	}
}
