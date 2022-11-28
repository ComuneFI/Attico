package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import com.mysema.query.types.Predicate;

public interface AttiOdgRepositoryCustom {
	
	public Page<AttiOdg> findAllInnerJoinComponentiGiuta(
			Predicate predicate, 
			Pageable generatePageRequest);
	
}
