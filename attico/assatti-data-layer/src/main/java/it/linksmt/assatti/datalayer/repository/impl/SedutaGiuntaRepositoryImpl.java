package it.linksmt.assatti.datalayer.repository.impl;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QueryDslRepositorySupport;

import it.linksmt.assatti.datalayer.domain.QAttiOdg;
import it.linksmt.assatti.datalayer.domain.QComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.QOrdineGiorno;
import it.linksmt.assatti.datalayer.domain.QSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepositoryCustom;
import com.mysema.query.SearchResults;
import com.mysema.query.jpa.JPQLQuery;
import com.mysema.query.types.Predicate;

public class SedutaGiuntaRepositoryImpl extends QueryDslRepositorySupport implements SedutaGiuntaRepositoryCustom {

	@PersistenceContext
	private EntityManager em;
	
	public SedutaGiuntaRepositoryImpl() {
		super(SedutaGiunta.class);
	}

	@Override
	public Page<SedutaGiunta> findAllInnerJoinAtto(Predicate predicate, Pageable generatePageRequest) {
		JPQLQuery query = from(QSedutaGiunta.sedutaGiunta).
				leftJoin(QSedutaGiunta.sedutaGiunta.odgs, QOrdineGiorno.ordineGiorno).
				leftJoin(QOrdineGiorno.ordineGiorno.attos, QAttiOdg.attiOdg).
				leftJoin(QAttiOdg.attiOdg.componenti, QComponentiGiunta.componentiGiunta).
				where(predicate).distinct();
		
		query = super.getQuerydsl().applyPagination(generatePageRequest, query);
		SearchResults<SedutaGiunta> entitys = query.listResults(QSedutaGiunta.sedutaGiunta);
		return new PageImpl<SedutaGiunta>(entitys.getResults(), generatePageRequest, entitys.getTotal());
	}

}
