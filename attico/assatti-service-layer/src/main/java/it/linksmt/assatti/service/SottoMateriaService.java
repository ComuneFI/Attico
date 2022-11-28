package it.linksmt.assatti.service;

import java.util.Calendar;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.SottoMateriaRepository;

/**
 * Service class for managing sottomaterias.
 */
@Service
@Transactional
public class SottoMateriaService {
	private final Logger log = LoggerFactory
			.getLogger(SottoMateriaService.class);

	@Inject
	private SottoMateriaRepository sottoMateriaRepository;
	
	@Transactional(readOnly=false)
	public void disableSottoMateria(final Long id){
		log.debug("disableSottoMateria idSottoMateria" + id);
		SottoMateria sottoMateria = sottoMateriaRepository.findOne(id);
		if(sottoMateria!=null){
			if(sottoMateria.getValidita()==null){
				sottoMateria.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			sottoMateria.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			sottoMateriaRepository.save(sottoMateria);
		}
	}

	@Transactional(readOnly=false)
	public void enableSottoMateria(final Long id){
		log.debug("enableSottoMateria idSottoMateria" + id);
		SottoMateria sottoMateria = sottoMateriaRepository.findOne(id);
		if(sottoMateria!=null){
			if(sottoMateria.getValidita()==null){
				sottoMateria.setValidita(new Validita());
			}
			sottoMateria.getValidita().setValidoal(null);
			sottoMateriaRepository.save(sottoMateria);
		}
	}
}
