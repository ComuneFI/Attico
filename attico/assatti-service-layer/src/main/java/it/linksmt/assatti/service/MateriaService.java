package it.linksmt.assatti.service;

import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Materia;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QMateria;
import it.linksmt.assatti.datalayer.domain.QSottoMateria;
import it.linksmt.assatti.datalayer.domain.QTipoMateria;
import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.TipoMateria;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.MateriaRepository;
import it.linksmt.assatti.datalayer.repository.SottoMateriaRepository;
import it.linksmt.assatti.datalayer.repository.TipoMateriaRepository;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing materias.
 */
@Service
@Transactional
public class MateriaService {
	private final Logger log = LoggerFactory
			.getLogger(MateriaService.class);

	@Inject
	private MateriaRepository materiaRepository;
	
	@Transactional(readOnly=false)
	public void disableMateria(final Long id){
		log.debug("disableMateria idMateria" + id);
		Materia materia = materiaRepository.findOne(id);
		if(materia!=null){
			if(materia.getValidita()==null){
				materia.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			materia.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			materiaRepository.save(materia);
		}
	}

	@Transactional(readOnly=false)
	public void enableMateria(final Long id){
		log.debug("enableMateria idMateria" + id);
		Materia materia = materiaRepository.findOne(id);
		if(materia!=null){
			if(materia.getValidita()==null){
				materia.setValidita(new Validita());
			}
			materia.getValidita().setValidoal(null);
			materiaRepository.save(materia);
		}
	}
}
