package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QQualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class QualificaprofessionaleService {

	private final Logger log = LoggerFactory.getLogger(QualificaprofessionaleService.class);

	@Inject
	private QualificaProfessionaleRepository qualificaProfessionaleRepository;
	
	@Inject
	private ValiditaService validitaService;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<QualificaProfessionale>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoAtto = QQualificaProfessionale.qualificaProfessionale.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("denominazione")!=null && !search.get("denominazione").isJsonNull() && search.get("denominazione").getAsString()!=null && !"".equals(search.get("denominazione").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QQualificaProfessionale.qualificaProfessionale.denominazione.containsIgnoreCase(search.get("denominazione").getAsString()));
			}
			
			JsonElement stato = search.get("stato");
			if(stato !=null && !stato.isJsonNull() && stato.getAsString()!=null && !"".equals(stato.getAsString())){
				String statoString = stato.getAsString();
				if("0".equals(statoString)){ // Qualifiche professionali attive
					predicateTipoAtto = predicateTipoAtto.and(QQualificaProfessionale.qualificaProfessionale.enabled.eq(true));
				}
				else if("1".equals(statoString)){ // Qualifiche professionali disattivate
					predicateTipoAtto = predicateTipoAtto.and(QQualificaProfessionale.qualificaProfessionale.enabled.eq(false));
				}
				else{ // Tutte

				}
			}
		}
		Page<QualificaProfessionale> page = qualificaProfessionaleRepository.findAll(predicateTipoAtto, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/qualificaProfessionales", offset, limit);
        return new ResponseEntity<List<QualificaProfessionale>>(page.getContent(), headers, HttpStatus.OK);
	}

	@Transactional(readOnly=false)
	public void disableQualificaProfessionale(Long idQualificaProfessioanale){
		QualificaProfessionale qp = qualificaProfessionaleRepository.findOne(idQualificaProfessioanale);
		qp.setEnabled(false);
		qualificaProfessionaleRepository.save(qp);
	}
	
	@Transactional(readOnly=false)
	public void enableQualificaProfessionale(Long idQualificaProfessioanale){
		QualificaProfessionale qp = qualificaProfessionaleRepository.findOne(idQualificaProfessioanale);
		qp.setEnabled(true);
		qualificaProfessionaleRepository.save(qp);
	}

	@Transactional( readOnly=true )
	public Page<QualificaProfessionale> findAll(Pageable generatePageRequest) {
		Page<QualificaProfessionale> l = qualificaProfessionaleRepository.findAll(generatePageRequest);
		return l;
	}

	@Transactional(readOnly=true)
	public Page<QualificaProfessionale> findAllEnabled(Pageable generatePageRequest){
		//BooleanExpression predicateQualificaProfessionale =  QQualificaProfessionale.qualificaProfessionale.enabled.eq(true);
		return new PageImpl<QualificaProfessionale>(qualificaProfessionaleRepository.findEnabledOrderedByDenominazione());
	}
	
	public QualificaProfessionale save(QualificaProfessionale profilo) {
		return qualificaProfessionaleRepository.save(profilo);
	}
	
	public void delete(Long id) {
		QualificaProfessionale entity = qualificaProfessionaleRepository.findOne(id);
		qualificaProfessionaleRepository.save( entity);
	}

	@Transactional(readOnly=true)
	public QualificaProfessionale findOne(Long id) {
		return qualificaProfessionaleRepository.findOne(id);		 
	}

	@Transactional(readOnly=true)
	public List<QualificaProfessionale> findByProfiloId(Long profiloId) {
		return qualificaProfessionaleRepository.findByProfiloId(profiloId);
	}
	
	@Transactional(readOnly=true)
	public List<QualificaProfessionale> findEnabledByProfiloId(Long profiloId) {
		return qualificaProfessionaleRepository.findEnabledByProfiloId(profiloId);
	}

//	public List<QualificaProfessionale> findByAooId(Long aooId) {
//		return qualificaProfessionaleRepository.findByAooId(aooId);	
//	}
	
	
}
