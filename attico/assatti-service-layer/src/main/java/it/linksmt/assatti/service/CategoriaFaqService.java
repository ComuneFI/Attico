package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.CategoriaFaq;
import it.linksmt.assatti.datalayer.domain.QCategoriaFaq;
import it.linksmt.assatti.datalayer.repository.CategoriaFaqRepository;
import it.linksmt.assatti.service.dto.CategoriaFaqDTO;
import it.linksmt.assatti.service.dto.CategoriaFaqSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing categoria faq.
 */
@Service
@Transactional
public class CategoriaFaqService {

	private final Logger log = LoggerFactory.getLogger(CategoriaFaqService.class);

	@Inject
	private CategoriaFaqRepository categoriaFaqRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<CategoriaFaqDTO>> search(CategoriaFaqSearchDTO search, Integer offset, Integer limit) throws URISyntaxException{
		BooleanExpression predicateCategoriaFaq = QCategoriaFaq.categoriaFaq.id.isNotNull();
		
		if(search.getId() != null){
			predicateCategoriaFaq = predicateCategoriaFaq.and(QCategoriaFaq.categoriaFaq.id.eq(search.getId()));
		}
		
		if(search.getDescrizione() != null && !"".equals(search.getDescrizione().trim())){
			predicateCategoriaFaq = predicateCategoriaFaq.and(QCategoriaFaq.categoriaFaq.descrizione.containsIgnoreCase(search.getDescrizione().trim()));
		}
    	
        Page<CategoriaFaq> page = categoriaFaqRepository.findAll(predicateCategoriaFaq, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/categoriaFaqs", offset, limit);
        return new ResponseEntity<List<CategoriaFaqDTO>>(this.convertToDTO(page.getContent()), headers, HttpStatus.OK);
	}
	
	private List<CategoriaFaqDTO> convertToDTO(List<CategoriaFaq> categorieFaq){
		List<CategoriaFaqDTO> categorieFaqDto = new ArrayList<CategoriaFaqDTO>();
		for(CategoriaFaq categoriaFaq : categorieFaq){
			CategoriaFaqDTO dto = new CategoriaFaqDTO();
			BeanUtils.copyProperties(categoriaFaq, dto);
			categorieFaqDto.add(dto);
		}
		return categorieFaqDto;
	}
}
