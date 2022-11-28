package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
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
import it.linksmt.assatti.datalayer.domain.QAoo;
import it.linksmt.assatti.datalayer.domain.QMateria;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.QSottoMateria;
import it.linksmt.assatti.datalayer.domain.QTipoMateria;
import it.linksmt.assatti.datalayer.domain.SottoMateria;
import it.linksmt.assatti.datalayer.domain.TipoMateria;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.MateriaRepository;
import it.linksmt.assatti.datalayer.repository.SottoMateriaRepository;
import it.linksmt.assatti.datalayer.repository.TipoMateriaRepository;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class TipoMateriaService {
	private final Logger log = LoggerFactory
			.getLogger(TipoMateriaService.class);

	@Inject
	private TipoMateriaRepository tipoMateriaRepository;

	@Inject
	private MateriaRepository materiaRepository;

	@Inject
	private SottoMateriaRepository sottoMateriaRepository;

	@Inject
	private ValiditaService validitaService;
	
	@Inject
	private AooService aooService;
	
	@Transactional(readOnly=false)
	public void disableTipoMateria(final Long id){
		log.debug("disableTipoMateria idTipoMateria" + id);
		TipoMateria tipoMateria = tipoMateriaRepository.findOne(id);
		if(tipoMateria!=null){
			if(tipoMateria.getValidita()==null){
				tipoMateria.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			tipoMateria.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			tipoMateriaRepository.save(tipoMateria);
		}
	}

	@Transactional(readOnly=false)
	public void enableTipoMateria(final Long id){
		log.debug("enableTipoMateria idTipoMateria" + id);
		TipoMateria tipoMateria = tipoMateriaRepository.findOne(id);
		if(tipoMateria!=null){
			if(tipoMateria.getValidita()==null){
				tipoMateria.setValidita(new Validita());
			}
			tipoMateria.getValidita().setValidoal(null);
			tipoMateriaRepository.save(tipoMateria);
		}
	}

	@Transactional(readOnly = true)
	public TipoMateria findOne(Long id) {
		log.debug("findOne id" + id);
		TipoMateria domain = tipoMateriaRepository.findOne(id);
		if (domain != null) {
			if (domain.getAoo() != null) {
				domain.setAoo(new Aoo(domain.getAoo()));
			}
		}
		return domain;
	}

	@Transactional(readOnly=true)
	public Iterable<TipoMateria> findAllAoo(Long aooId, Boolean validita) {
		 
		BooleanExpression predicate = QTipoMateria.tipoMateria.aoo.id.eq(aooId);
		predicate = predicate.or(QTipoMateria.tipoMateria.aoo.isNull());
		
		if(validita){
			predicate = predicate.and(validitaService
				.createPredicate(QTipoMateria.tipoMateria.validita));

		}
		
		Iterable<TipoMateria> tipoMaterie = tipoMateriaRepository
				.findAll(predicate);
		
		if (tipoMaterie != null) {
			for (TipoMateria tipoMateria : tipoMaterie) {
				tipoMateria.setAoo( DomainUtil.minimalAoo(tipoMateria.getAoo() ));
				
				
				BooleanExpression predicate2 = QMateria.materia.tipoMateria.id.eq(tipoMateria.getId());
				predicate2 = predicate2.and(
						QMateria.materia.aoo.id.eq(aooId).or(QMateria.materia.aoo.isNull() )
						);
				
				if(validita){
					predicate2 =predicate2.and( validitaService
						.createPredicate( QMateria.materia.validita)) ;
				}
				Iterable<Materia> materie = materiaRepository.findAll(predicate2);
				Set<Materia> materieValide = new HashSet<Materia>(); 
				
				if(materie != null ){
					for (Materia materia : materie) {
						materia.setTipoMateria(new TipoMateria(tipoMateria
								.getId()));
						materia.setAoo( DomainUtil.minimalAoo(materia.getAoo())  );
						BooleanExpression predicate3 = QSottoMateria.sottoMateria.materia.id.eq(materia.getId());
						predicate3 = predicate3.and(
								QSottoMateria.sottoMateria.aoo.id.eq(aooId).or(QSottoMateria.sottoMateria.aoo.isNull() )
								);
						
						if(validita){
							predicate3 =predicate3.and( validitaService
								.createPredicate( QSottoMateria.sottoMateria.validita)) ;
						}
						
						Iterable<SottoMateria> sottoMaterie = sottoMateriaRepository.findAll(predicate3);
						Set<SottoMateria> sottoMaterieValide = new HashSet<SottoMateria>(); 
						if(sottoMaterie != null ){
							for (SottoMateria sottoMateria : sottoMaterie) {
								
								sottoMateria.setAoo( DomainUtil.minimalAoo(sottoMateria.getAoo() ));
								
								sottoMateria.setMateria(new Materia(materia
										.getId()));
								sottoMaterieValide.add(sottoMateria);
							}
						}
						materia.setSottoMaterie( sottoMaterieValide);
						materieValide.add(materia);
					}
					tipoMateria.setMaterie(materieValide);
				}
				
			}
		}
		return tipoMaterie;
		
	}

	

	@Transactional(readOnly = true)
	public List<TipoMateria> findByAooIsNull() {
		List<TipoMateria> tipoMaterie = tipoMateriaRepository.findByAooIsNull();
		loadRecursive(tipoMaterie);

		return tipoMaterie;
	}
	
	@Transactional(readOnly = true)
	public List<TipoMateria> findAll() {
		List<TipoMateria> tipoMaterie = tipoMateriaRepository.findAll();
		loadRecursive(tipoMaterie);

		return tipoMaterie;
	}
	
	@Transactional(readOnly = true)
	public List<TipoMateria> findAllByFilter(String tipoRicerca, String descrizione, String aoo, String stato){
		List<TipoMateria> risultato = new ArrayList<TipoMateria>();
		if(tipoRicerca != null){
			switch(tipoRicerca){
				case "materia":
					if(descrizione != null && !"".equals(descrizione.trim()) || (aoo!=null && !"".equals(aoo.trim()))){
						BooleanExpression materiaExpression = QMateria.materia.id.isNotNull();
						if(descrizione != null && !"".equals(descrizione.trim())){
							materiaExpression = materiaExpression.and(QMateria.materia.descrizione.containsIgnoreCase(descrizione));
						}
						
						if(stato != null && !"".equals(stato)){
							if("0".equals(stato)){ // Materie attive
								materiaExpression = materiaExpression.and(validitaService.createPredicate(QMateria.materia.validita));
							}
							else if("1".equals(stato)){ // Materie disattivate
								materiaExpression = materiaExpression.and(validitaService.validAoo(QMateria.materia.validita));
							}
							else{ // Tutte

							}
						}
						
						if(aoo!=null && !"".equals(aoo.trim())){
							BooleanExpression aooExpression = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo)).or(
									(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo));
							Iterable<Aoo> aoos = aooService.findByPredicate(aooExpression);
							BooleanExpression internalAooExpression = null;
							for(Aoo a : aoos){
								if(internalAooExpression == null){
									internalAooExpression = QMateria.materia.aoo.eq(a);
								}else{
									internalAooExpression = internalAooExpression.or(QMateria.materia.aoo.eq(a));
								}
							}
							if(internalAooExpression!=null){
								materiaExpression = materiaExpression.and(internalAooExpression);
							}
							
							if(internalAooExpression == null){
								//Force 0 results
								materiaExpression = materiaExpression.and(QMateria.materia.id.isNull());
							} else {
								materiaExpression = materiaExpression.and(internalAooExpression);
							}
						}
						
						Iterable<Materia> materie = materiaRepository.findAll(materiaExpression, new OrderSpecifier<>(Order.ASC, QMateria.materia.id));
						BooleanExpression tipoMateriaExpression = QTipoMateria.tipoMateria.id.isNotNull();
						BooleanExpression tipoMateriaInternalExpression = null;
						for(Materia materia : materie){
							if(tipoMateriaInternalExpression == null){
								tipoMateriaInternalExpression = QTipoMateria.tipoMateria.materie.contains(materia);
							}else{
								tipoMateriaInternalExpression = tipoMateriaInternalExpression.or(QTipoMateria.tipoMateria.materie.contains(materia));
							}
						}
						if(tipoMateriaInternalExpression!=null){
							tipoMateriaExpression = tipoMateriaExpression.and(tipoMateriaInternalExpression);
							
							Iterable<TipoMateria> tipiMateriaI = tipoMateriaRepository.findAll(tipoMateriaExpression, new OrderSpecifier<>(Order.ASC, QTipoMateria.tipoMateria.id));
							for(TipoMateria t : tipiMateriaI){
								Set<Materia> tipoMateriaMaterie = t.getMaterie();
								Iterator<Materia> tipoMateriaMaterieI = tipoMateriaMaterie.iterator();
								while (tipoMateriaMaterieI.hasNext()) {
									Materia element = tipoMateriaMaterieI.next();
									//Remove materia that doesn't match
								    if (!findMateria(materie, element)) {
								    	tipoMateriaMaterieI.remove();
								    }
								}
								
								risultato.add(t);
							}
						}
					}
					break;
				case "sottomateria":
					if(descrizione != null && !"".equals(descrizione.trim()) || (aoo!=null && !"".equals(aoo.trim()))){
						BooleanExpression sottoMateriaExpression = QSottoMateria.sottoMateria.id.isNotNull();
						if(descrizione != null && !"".equals(descrizione.trim())){
							sottoMateriaExpression = sottoMateriaExpression.and(QSottoMateria.sottoMateria.descrizione.containsIgnoreCase(descrizione));
						}
						
						if(stato != null && !"".equals(stato)){
							if("0".equals(stato)){ // Sottomaterie attive
								sottoMateriaExpression = sottoMateriaExpression.and(validitaService.createPredicate(QSottoMateria.sottoMateria.validita));
							}
							else if("1".equals(stato)){ // Sottomaterie disattivate
								sottoMateriaExpression = sottoMateriaExpression.and(validitaService.validAoo(QSottoMateria.sottoMateria.validita));
							}
							else{ // Tutte

							}
						}
						
						if(aoo!=null && !"".equals(aoo.trim())){
							BooleanExpression aooExpression = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo)).or(
											(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo));
							Iterable<Aoo> aoos = aooService.findByPredicate(aooExpression);
							BooleanExpression internalAooExpression = null;
							for(Aoo a : aoos){
								if(internalAooExpression == null){
									internalAooExpression = QSottoMateria.sottoMateria.aoo.eq(a);
								}else{
									internalAooExpression = internalAooExpression.or(QSottoMateria.sottoMateria.aoo.eq(a));
								}
							}
							
							if(internalAooExpression == null){
								//Force 0 results
								sottoMateriaExpression = sottoMateriaExpression.and(QSottoMateria.sottoMateria.id.isNull());
							} else {
								sottoMateriaExpression = sottoMateriaExpression.and(internalAooExpression);
							}
						}

						Iterable<SottoMateria> sottoMaterie = sottoMateriaRepository.findAll(sottoMateriaExpression, new OrderSpecifier<>(Order.ASC, QSottoMateria.sottoMateria.id));
						
						BooleanExpression tipoMateriaInternalMateriaExpression = null;
						for(SottoMateria sottoMateria : sottoMaterie){
							if(tipoMateriaInternalMateriaExpression == null){
								tipoMateriaInternalMateriaExpression = QMateria.materia.sottoMaterie.contains(sottoMateria);
							}else{
								tipoMateriaInternalMateriaExpression = tipoMateriaInternalMateriaExpression.or(QMateria.materia.sottoMaterie.contains(sottoMateria));
							}
						}
						if(tipoMateriaInternalMateriaExpression != null){
							Iterable<Materia> materie = materiaRepository.findAll(tipoMateriaInternalMateriaExpression, new OrderSpecifier<>(Order.ASC, QMateria.materia.id));
							
							BooleanExpression tipoMateriaInternalExpression = null;
							for(Materia materia : materie){
								if(tipoMateriaInternalExpression == null){
									tipoMateriaInternalExpression = QTipoMateria.tipoMateria.materie.contains(materia);
								}else{
									tipoMateriaInternalExpression = tipoMateriaInternalExpression.or(QTipoMateria.tipoMateria.materie.contains(materia));
								}
							}
							if(tipoMateriaInternalExpression!=null){
								BooleanExpression tipoMateriaExpression = QTipoMateria.tipoMateria.id.isNotNull();
								tipoMateriaExpression = tipoMateriaExpression.and(tipoMateriaInternalExpression);
								
								Iterable<TipoMateria> tipiMateriaI = tipoMateriaRepository.findAll(tipoMateriaExpression, new OrderSpecifier<>(Order.ASC, QTipoMateria.tipoMateria.id));
								for(TipoMateria t : tipiMateriaI){
									Set<Materia> tipoMateriaMaterie = t.getMaterie();
									Iterator<Materia> tipoMateriaMaterieI = tipoMateriaMaterie.iterator();
									while (tipoMateriaMaterieI.hasNext()) {
										Materia element = tipoMateriaMaterieI.next();
										Set<SottoMateria> materiaSottoMaterie = element.getSottoMaterie();
										Iterator<SottoMateria> tipoMateriaSottoMaterieI = materiaSottoMaterie.iterator();
										while (tipoMateriaSottoMaterieI.hasNext()) {
											SottoMateria sottoMateriaElement = tipoMateriaSottoMaterieI.next();
											
											//Remove sottomateria that doesn't match
										    if (!findSottoMateria(sottoMaterie, sottoMateriaElement)) {
										    	tipoMateriaSottoMaterieI.remove();
										    }
										}
										
										//Remove materia that doesn't contain sotto materia
									    if (materiaSottoMaterie.isEmpty()) {
									    	tipoMateriaMaterieI.remove();
									    }
									}
									
									risultato.add(t);
								}
							}
						}
						
					}
					
					break;
				case "tipomateria":
					if((descrizione!=null && !"".equals(descrizione.trim())) || (aoo!=null && !"".equals(aoo.trim()))){
						BooleanExpression tipoMateriaExpression = QTipoMateria.tipoMateria.id.isNotNull();
						if(descrizione != null){
							tipoMateriaExpression = tipoMateriaExpression.and(QTipoMateria.tipoMateria.descrizione.containsIgnoreCase(descrizione));
						}
						
						if(stato != null && !"".equals(stato)){
							if("0".equals(stato)){ // Tipo materia attivo
								tipoMateriaExpression = tipoMateriaExpression.and(validitaService.createPredicate(QTipoMateria.tipoMateria.validita));
							}
							else if("1".equals(stato)){ // Tipo materia disattivato
								tipoMateriaExpression = tipoMateriaExpression.and(validitaService.validAoo(QTipoMateria.tipoMateria.validita));
							}
							else{ // Tutti

							}
						}
						
						if(aoo!=null && !"".equals(aoo.trim())){
							BooleanExpression aooExpression = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo)).or(
											(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(aoo));
							Iterable<Aoo> aoos = aooService.findByPredicate(aooExpression);
							BooleanExpression internalAooExpression = null;
							for(Aoo a : aoos){
								if(internalAooExpression == null){
									internalAooExpression = QTipoMateria.tipoMateria.aoo.eq(a);
								}else{
									internalAooExpression = internalAooExpression.or(QTipoMateria.tipoMateria.aoo.eq(a));
								}
							}
							
							if(internalAooExpression == null){
								//Force 0 results
								tipoMateriaExpression = tipoMateriaExpression.and(QTipoMateria.tipoMateria.id.isNull());
							} else {
								tipoMateriaExpression = tipoMateriaExpression.and(internalAooExpression);
							}
						}
						
						Iterable<TipoMateria> tipiMateriaI = tipoMateriaRepository.findAll(tipoMateriaExpression, new OrderSpecifier<>(Order.ASC, QTipoMateria.tipoMateria.id));
						for(TipoMateria t : tipiMateriaI){
							risultato.add(t);
						}
					}
					break;
			}
			loadRecursive(risultato);
		}
		
		return risultato;
	}

	private void loadRecursive(Iterable<TipoMateria> tipoMaterie) {
		if (tipoMaterie != null) {

			for (TipoMateria tipoMateria : tipoMaterie) {
				tipoMateria.setAoo( DomainUtil.minimalAoo(tipoMateria.getAoo() ));
				
				if (tipoMateria.getMaterie() != null) {
					for (Materia materia : tipoMateria.getMaterie()) {
						
						materia.setAoo( DomainUtil.minimalAoo(materia.getAoo() ));
						
						materia.setTipoMateria(new TipoMateria(tipoMateria
								.getId()));
						
						if (materia.getSottoMaterie() != null) {
							for (SottoMateria sottoMateria : materia
									.getSottoMaterie()) {
								
								sottoMateria.setAoo( DomainUtil.minimalAoo(sottoMateria.getAoo() ));
								sottoMateria.setMateria(new Materia(materia
										.getId()));
							}
						}

					}
				}
			}

		}
	}
 
	public void delete(Long id) {
		TipoMateria tipo = tipoMateriaRepository.findOne(id);
		if(tipo.getValidita()== null){
			tipo.setValidita(new Validita());
		}
			
		tipo.getValidita().setValidoal( new LocalDate() );
		tipoMateriaRepository.save( tipo );
	}

	public TipoMateria save(TipoMateria entity) {
		return tipoMateriaRepository.save(entity);
	}
	
	public Materia save(Materia entity) {
		return materiaRepository.save(entity);
	}
	
	public SottoMateria save(SottoMateria entity) {
		return sottoMateriaRepository.save(entity);
	}
	
	/**
	 * Search the {@link Materia} by the given {@link Iterable} of {@link Materia}.
	 * @param materie The {@link Iterable} of {@link Materia}s from which search.
	 * @param materia The {@link Materia} to find.
	 * @return Returns true if {@link Materia} is found, false otherwise.
	 */
	private boolean findMateria(Iterable<Materia> materie, Materia materia){
		for (Materia sm : materie) {
			if(sm.getId() == materia.getId()){
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Search the {@link SottoMateria} by the given {@link Iterable} of {@link SottoMateria}.
	 * @param sottoMaterie The {@link Iterable} of {@link SottoMateria}s from which search.
	 * @param sottoMateria The {@link SottoMateria} to find.
	 * @return Returns true if {@link SottoMateria} is found, false otherwise.
	 */
	private boolean findSottoMateria(Iterable<SottoMateria> sottoMaterie, SottoMateria sottoMateria){
		for (SottoMateria sm : sottoMaterie) {
			if(sm.getId() == sottoMateria.getId()){
				return true;
			}
		}
		return false;
	}

}
