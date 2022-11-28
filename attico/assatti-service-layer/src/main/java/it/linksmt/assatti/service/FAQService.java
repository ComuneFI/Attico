package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.List;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.CategoriaFaq;
import it.linksmt.assatti.datalayer.domain.Faq;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QFaq;
import it.linksmt.assatti.datalayer.repository.CategoriaFaqRepository;
import it.linksmt.assatti.datalayer.repository.FaqRepository;
import it.linksmt.assatti.service.exception.NotFoundException;
import it.linksmt.assatti.service.dto.FaqSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class FAQService {
	
	@Inject
    private FaqRepository faqRepository;
    
    @Inject
    private CategoriaFaqRepository  categoriaFaqRepository;
    
    @Inject
    private ProfiloService profiloService;
    
    @Inject
    private AooService aooService;

	public FAQService() {
		// TODO Auto-generated constructor stub
	}

	public Faq save(Faq faq) {
		if(faq.getAoo().isEmpty()){
        	faq.setPubblica(Boolean.TRUE);
        }
		return faqRepository.save(faq);
	}
	
	
	
	@Transactional(readOnly = true)
	public Page<Faq> findAll(Pageable generatePageRequest) {
    	Page<Faq> page = faqRepository.findAll(generatePageRequest);
		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<Faq> findAll(Integer offset, Integer limit, Sort sort) {
		if(sort==null)
			sort = new Sort(Direction.DESC, "categoria") ;
    	Page<Faq> page = findAll(PaginationUtil.generatePageRequest(offset, limit, sort));
		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<Faq> findAllByProfiloIDAndByCategoriaID(Long categoriaID, Long profiloId, Integer offset,	Integer limit, Sort sort) throws NotFoundException {
		CategoriaFaq categoriaFaq = null;
    	
		Profilo profilo = profiloService.findOne(profiloId);
		if (profilo == null) {
            throw new NotFoundException(HttpStatus.NOT_FOUND.toString());
        }
		
		if(categoriaID != null){
			categoriaFaq = categoriaFaqRepository.findOne(categoriaID);
			if(categoriaFaq == null){
				throw new NotFoundException(HttpStatus.NOT_FOUND.toString());
			}
		}
		
		BooleanExpression aooCriteria = QFaq.faq.aoo.isEmpty() ;
		aooCriteria = aooCriteria.or(QFaq.faq.aoo.any().in(profilo.getAoo()));
				
		BooleanExpression criteria = QFaq.faq.id.isNotNull();
		criteria = criteria.and(aooCriteria);
		if(categoriaFaq != null){
			criteria = criteria.and( QFaq.faq.categoria.id.eq(categoriaFaq.getId()) );
		}
		
		Page<Faq> page = faqRepository.findAll(criteria  , PaginationUtil.generatePageRequest(offset, limit, new Sort(Direction.DESC, "categoria") ));
		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<Faq> findAllAndByCategoriaID(Long categoriaID, Integer offset,	Integer limit, Sort sort) throws NotFoundException {
		CategoriaFaq categoriaFaq = null;
    	
		if(categoriaID != null){
			categoriaFaq = categoriaFaqRepository.findOne(categoriaID);
			if(categoriaFaq == null){
				throw new NotFoundException(HttpStatus.NOT_FOUND.toString());
			}
		}
		
		BooleanExpression aooCriteria = QFaq.faq.aoo.isEmpty() ;		
		BooleanExpression criteria = QFaq.faq.id.isNotNull();
		criteria = criteria.and(aooCriteria);
		if(categoriaFaq != null){
			criteria = criteria.and( QFaq.faq.categoria.id.eq(categoriaFaq.getId()) );
		}
		
		Page<Faq> page = faqRepository.findAll(criteria  , PaginationUtil.generatePageRequest(offset, limit, new Sort(Direction.DESC, "categoria") ));
		return page;
	}
	
	@Transactional(readOnly = true)
	private Page<Faq> findAllByAooIsNull(Integer offset,	Integer limit, Sort sort) {
		if(sort==null)
			sort = new Sort(Direction.DESC, "categoria") ;
		Page<Faq> page = findAllByAooIsNull(PaginationUtil.generatePageRequest(offset, limit, sort));
		return page;
	}    	
	@Transactional(readOnly = true)
	private Page<Faq> findAllByAooIsNull(Pageable generatePageRequest) {
		Page<Faq> page = faqRepository.findAllByAooIsNull(generatePageRequest);
		return page;
	}
	@Transactional(readOnly = true)
	public Faq findOne(Long id) {
		Faq faq = faqRepository.findOne(id);
		if(null != faq.getAoo()){
			faq.getAoo().toArray();
		}
		return faq;
	}

	public void delete(Long id) {
		faqRepository.delete(id);
	}
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Faq>> search(FaqSearchDTO search, Integer offset, Integer limit) throws URISyntaxException{
		BooleanExpression predicateCategoriaFaq = QFaq.faq.id.isNotNull();
		
		if(search.getAoo() != null){
			Aoo aoo = aooService.findOne(search.getAoo());
			predicateCategoriaFaq = predicateCategoriaFaq.and(QFaq.faq.aoo.any().in(aoo));
		}
		
		if(search.getCategoria() != null){
			predicateCategoriaFaq = predicateCategoriaFaq.and(QFaq.faq.categoria.id.eq(search.getCategoria()));
		}
		
		if(search.getDomanda() != null && !"".equals(search.getDomanda().trim())){
			predicateCategoriaFaq = predicateCategoriaFaq.and(QFaq.faq.domanda.containsIgnoreCase(search.getDomanda().trim()));
		}
		
		if(search.getRisposta() != null && !"".equals(search.getRisposta().trim())){
			predicateCategoriaFaq = predicateCategoriaFaq.and(QFaq.faq.risposta.containsIgnoreCase(search.getRisposta().trim()));
		}
    	
        Page<Faq> page = faqRepository.findAll(predicateCategoriaFaq, PaginationUtil.generatePageRequest(offset, limit, new Sort(Direction.DESC, "categoria")));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/faqs", offset, limit);
        return new ResponseEntity<List<Faq>>(page.getContent(), headers, HttpStatus.OK);
	}
}
