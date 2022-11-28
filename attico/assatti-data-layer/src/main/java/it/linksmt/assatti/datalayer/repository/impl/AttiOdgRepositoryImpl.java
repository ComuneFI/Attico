package it.linksmt.assatti.datalayer.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.QAttiOdg;
import it.linksmt.assatti.datalayer.domain.QComponentiGiunta;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepositoryCustom;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.Predicate;

public class AttiOdgRepositoryImpl extends QueryDslRepositorySupport implements AttiOdgRepositoryCustom {
	
	@PersistenceContext
	private EntityManager em;

	public AttiOdgRepositoryImpl() {
		super(AttiOdg.class);
	}

	@Override
	public Page<AttiOdg> findAllInnerJoinComponentiGiuta(
			Predicate predicate, 
			Pageable generatePageRequest) {
		
		JPQLQuery query = from(QAttiOdg.attiOdg).
				leftJoin(QAttiOdg.attiOdg.componenti, QComponentiGiunta.componentiGiunta).
				where(predicate).distinct();
		
		query = super.getQuerydsl().applyPagination(generatePageRequest, query);
		SearchResults<AttiOdg> entitys = query.listResults(QAttiOdg.attiOdg);
		return new PageImpl<AttiOdg>(entitys.getResults(), generatePageRequest, entitys.getTotal());
	}

}
