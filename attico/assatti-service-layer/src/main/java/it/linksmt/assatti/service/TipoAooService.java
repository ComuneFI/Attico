package it.linksmt.assatti.service;

import it.linksmt.assatti.datalayer.domain.QTipoAoo;
import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.repository.TipoAooRepository;
import it.linksmt.assatti.service.dto.TipoAooSearchDTO;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing tipoAoo
 */
@Service
@Transactional
public class TipoAooService {
	
	@Inject
	private TipoAooRepository tipoAooRepository;
	
	@Transactional(readOnly = true)
	public Page<TipoAoo> findAll(TipoAooSearchDTO search, Pageable pageInfo){
		
		BooleanExpression predicateTipoAoo = QTipoAoo.tipoAoo.isNotNull();
		
		Long idLong = null;
		if(search.getId()!=null && !"".equals(search.getId().trim())){
			try{
				idLong = Long.parseLong(search.getId().trim());
			}catch(Exception e){};
		}
		
		if(idLong!=null){
			predicateTipoAoo = predicateTipoAoo.and(QTipoAoo.tipoAoo.id.eq(idLong));
		}
		
		if(search.getNote()!=null && !"".equals(search.getNote().trim())){
			predicateTipoAoo = predicateTipoAoo.and(QTipoAoo.tipoAoo.note.containsIgnoreCase(search.getNote()));
		}
		if(search.getCodice()!=null && !"".equals(search.getCodice().trim())){
			predicateTipoAoo = predicateTipoAoo.and(QTipoAoo.tipoAoo.codice.containsIgnoreCase(search.getCodice()));
		}
		if(search.getDescrizione()!=null && !"".equals(search.getDescrizione().trim())){
			predicateTipoAoo = predicateTipoAoo.and(QTipoAoo.tipoAoo.descrizione.containsIgnoreCase(search.getDescrizione()));
		}
		
		Page<TipoAoo> page = tipoAooRepository.findAll(predicateTipoAoo, pageInfo);
		
		return page;
	}
	
}
