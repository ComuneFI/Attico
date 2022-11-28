package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.CategoriaMsg;
import it.linksmt.assatti.datalayer.domain.QCategoriaMsg;
import it.linksmt.assatti.datalayer.repository.CategoriaMsgRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class CategoriaMsgService {
	private final Logger log = LoggerFactory.getLogger(CategoriaMsgService.class);
	
	@Inject
	private CategoriaMsgRepository categoriaMsgRepository;
	
	public CategoriaMsgService() {
		
	}
	
	@Transactional
	public CategoriaMsg create(CategoriaMsg categoriaMsg) {
		categoriaMsg.setEnabled(true);
        return categoriaMsgRepository.save(categoriaMsg);
	}
	
	@Transactional
	public CategoriaMsg update(CategoriaMsg categoriaMsg) {
        return categoriaMsgRepository.save(categoriaMsg);
	}
	
	@Transactional
	public void disable(Long id) {
		categoriaMsgRepository.enableDisable(false, id);
	}
	
	@Transactional
	public void enable(Long id) {
		categoriaMsgRepository.enableDisable(true, id);
	}
	
	@Transactional(readOnly=true)
	public CategoriaMsg readOne(Long id) {
		return categoriaMsgRepository.findOne(id);
	}
	
	@Transactional(readOnly=true)
	public Page<CategoriaMsg> findAll(Pageable generatePageRequest, JsonObject searchJson){
		Page<CategoriaMsg> page = new PageImpl<CategoriaMsg>(new ArrayList<CategoriaMsg>());
		
		BooleanExpression predicate = QCategoriaMsg.categoriaMsg.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("enabled") && searchJson.get("enabled").getAsJsonObject().has("value") && !searchJson.get("enabled").getAsJsonObject().get("value").getAsString().isEmpty() && !searchJson.get("enabled").getAsJsonObject().get("value").getAsString().equalsIgnoreCase("both")){
			predicate = predicate.and(QCategoriaMsg.categoriaMsg.enabled.eq(searchJson.get("enabled").getAsJsonObject().get("value").getAsBoolean()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("descrizione") && !searchJson.get("descrizione").getAsString().isEmpty()){
			predicate = predicate.and(QCategoriaMsg.categoriaMsg.descrizione.like("%"+searchJson.get("descrizione").getAsString()+"%"));
		}
		Sort sort = new Sort(new org.springframework.data.domain.Sort.Order(Direction.ASC, "descrizione" ));
		page = categoriaMsgRepository.findAll(predicate , PaginationUtil.generatePageRequest(generatePageRequest, sort));
		return page;
	}
	
	@Transactional(readOnly=true)
	public List<CategoriaMsg> findAllEnabled(){
		BooleanExpression predicate = QCategoriaMsg.categoriaMsg.enabled.eq(true);
		Iterable<CategoriaMsg> categorie = categoriaMsgRepository.findAll(predicate , new OrderSpecifier<>(Order.ASC, QCategoriaMsg.categoriaMsg.descrizione));
		return Lists.newArrayList(categorie);
	}
}
