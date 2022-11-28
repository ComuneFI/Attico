package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import it.linksmt.assatti.datalayer.domain.Atto;
import com.mysema.query.types.Predicate;

public interface AttoRepositoryCustom {
	
	public Page<Atto> findAllInnerJoinAvanzamenti(
			Predicate predicate, 
			Pageable generatePageRequest);
	
}
