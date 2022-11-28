package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Faq;

/**
 * Spring Data JPA repository for the Faq entity.
 */
public interface FaqRepository extends JpaRepository<Faq,Long> , QueryDslPredicateExecutor<Faq>{

	Page<Faq> findAllByAooIsNull(Pageable generatePageRequest);

	List<Faq> findAllByAoo(Aoo aoo);
	
	List<Faq> findAllByAooIsNull();
	
	Page<Faq> findAllByAooIdIsNullOrAooId(Long id, Pageable generatePageRequest);
	
	List<Faq> findListByAooIdIsNullOrAooId(Long id, Pageable generatePageRequest);

}
