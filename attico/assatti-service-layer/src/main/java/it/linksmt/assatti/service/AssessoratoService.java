package it.linksmt.assatti.service;


import java.util.Calendar;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Assessorato;
import it.linksmt.assatti.datalayer.domain.QAssessorato;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.repository.AssessoratoRepository;
import it.linksmt.assatti.service.dto.AssessoratoSearchDTO;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AssessoratoService {

	private static final String IT = "it";

	private final Logger log = LoggerFactory.getLogger(AssessoratoService.class);

	@Inject
	private AssessoratoRepository assessoratoRepository;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private ValiditaService validitaService;
	
	@Transactional(readOnly = true)
	public Page<Assessorato> findAll(Pageable pageInfo){
		return assessoratoRepository.findAll(pageInfo);
	}
	
	@Transactional(readOnly = true)
	public List<Assessorato> findAll(){
		
		List<Assessorato> assessorati = assessoratoRepository.findAll();
		
		for(Assessorato assessorato : assessorati){
			if(assessorato.getProfiloResponsabileId() != null){
				assessorato.setProfiloResponsabile(profiloService.findOne(assessorato.getProfiloResponsabileId()));
			}
		}
		
		
		return assessorati;
	}
	
	@Transactional(readOnly = true)
	public Page<Assessorato> findAll(AssessoratoSearchDTO search, Pageable pageInfo){
		
		BooleanExpression predicateAssessorato = QAssessorato.assessorato.id.isNotNull();
		
		Long idLong = null;
		if(search.getId()!=null && !"".equals(search.getId().trim())){
			try{
				idLong = Long.parseLong(search.getId().trim());
			}catch(Exception e){};
		}
		
		if(idLong!=null){
			predicateAssessorato = predicateAssessorato.and(QAssessorato.assessorato.id.eq(idLong));
		}
		if(search.getCodice()!=null && !"".equals(search.getCodice().trim())){
			predicateAssessorato = predicateAssessorato.and(QAssessorato.assessorato.codice.containsIgnoreCase(search.getCodice().trim()));
		}
		if(search.getResponsabile()!=null && !"".equals(search.getResponsabile().trim())){
			predicateAssessorato = predicateAssessorato.and(QAssessorato.assessorato.nominativoResponsabile.containsIgnoreCase(search.getResponsabile().trim()));
		}
		if(search.getDenominazione()!=null && !"".equals(search.getDenominazione().trim())){
			predicateAssessorato = predicateAssessorato.and(QAssessorato.assessorato.denominazione.containsIgnoreCase(search.getDenominazione().trim()));
		}
		if(search.getQualifica()!=null && !"".equals(search.getQualifica().trim())){
			predicateAssessorato = predicateAssessorato.and(QAssessorato.assessorato.qualifica.containsIgnoreCase(search.getQualifica().trim()));
		}
		
		String stato = search.getStato();
		if(stato != null && !"".equals(stato)){

			if("0".equals(stato)){ // Assessorati attivi
				predicateAssessorato = predicateAssessorato.and(validitaService.createPredicate(QAssessorato.assessorato.validita));
			}
			else if("1".equals(stato)){ // Assessorati disattivati
				predicateAssessorato = predicateAssessorato.and(validitaService.validAoo(QAssessorato.assessorato.validita));
			}
			else{ // Tutti

			}

		}
		
		Page<Assessorato> page = assessoratoRepository.findAll(predicateAssessorato, pageInfo);
		
		return page;
	}
	
	@Transactional(readOnly = true)
	public Assessorato findOne(Long id) {
		
		Assessorato assessorato = assessoratoRepository.findOne(id);
		
		log.debug("findOne");
		log.debug(assessorato.toString());
		if (assessorato != null) {
			
			if(assessorato.getProfiloResponsabileId() != null){
				assessorato.setProfiloResponsabile(profiloService.findOne(assessorato.getProfiloResponsabileId()));
			}

		}

		return assessorato;
		
	}
	
	@Transactional(readOnly = true)
	public List<Assessorato> findByProfiloResponsabileId(Long profiloResponsabileId){
		BooleanExpression predicate = null;
		if(profiloResponsabileId != null){
			predicate = QAssessorato.assessorato.id.isNotNull();
			predicate = predicate.and(QAssessorato.assessorato.profiloResponsabileId.eq(profiloResponsabileId));
		}else{
			predicate = QAssessorato.assessorato.id.isNull();
		}
		;
		return Lists.newArrayList(assessoratoRepository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public List<Assessorato> findByProfiloResponsabileIds(List<Long> profiloResponsabileIds){
		BooleanExpression predicate = null;
		if(profiloResponsabileIds != null && profiloResponsabileIds.size()>0){
			predicate = QAssessorato.assessorato.id.isNotNull();
			predicate = predicate.and(QAssessorato.assessorato.profiloResponsabileId.in(profiloResponsabileIds));
		}else{
			predicate = QAssessorato.assessorato.id.isNull();
		}
		;
		return Lists.newArrayList(assessoratoRepository.findAll(predicate));
	}
	
	@Transactional
	public void save(Assessorato assessorato) {

		if(assessorato.getProfiloResponsabile()!=null){
			assessorato.setProfiloResponsabileId(assessorato.getProfiloResponsabile().getId());
		}
		assessoratoRepository.save(assessorato);
	}
	
	@Transactional(readOnly=false)
	public void disableAssessorato(Long id){
		log.debug("disableAssessorato idAssessorato " + id);
		Assessorato assessorato = assessoratoRepository.findOne(id);
		if(assessorato!=null){
			if(assessorato.getValidita()==null){
				assessorato.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
			assessorato.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			assessoratoRepository.save(assessorato);
		}
	}
	
	@Transactional(readOnly=false)
	public void enableAssessorato(Long id){
		log.debug("enableAssessorato Assessorato " + id);
		Assessorato assessorato = assessoratoRepository.findOne(id);
		if(assessorato!=null){
			if(assessorato.getValidita()==null){
				assessorato.setValidita(new Validita());
			}
			assessorato.getValidita().setValidoal(null);
			assessoratoRepository.save(assessorato);
		}
	}
	
}
