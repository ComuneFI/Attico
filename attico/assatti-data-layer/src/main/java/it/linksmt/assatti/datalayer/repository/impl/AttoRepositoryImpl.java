package it.linksmt.assatti.datalayer.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.QAtto;
import it.linksmt.assatti.datalayer.domain.QAvanzamento;
import it.linksmt.assatti.datalayer.repository.AttoRepositoryCustom;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.Predicate;

public class AttoRepositoryImpl extends QueryDslRepositorySupport implements AttoRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;

	public AttoRepositoryImpl() {
		super(QAtto.class);
	}

	@Override
	public Page<Atto> findAllInnerJoinAvanzamenti(
			Predicate predicate, 
			Pageable generatePageRequest) {
		
		JPQLQuery query = from(QAtto.atto).
				leftJoin(QAtto.atto.avanzamento, QAvanzamento.avanzamento).
				where(predicate).distinct();
		
		query = super.getQuerydsl().applyPagination(generatePageRequest, query);
		SearchResults<Atto> entitys = query.listResults(QAtto.atto);
		return new PageImpl<Atto>(entitys.getResults(), generatePageRequest, entitys.getTotal());
	}

}
