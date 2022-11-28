package it.linksmt.assatti.datalayer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QueryDslPredicateExecutor;

import it.linksmt.assatti.datalayer.domain.InsediamentoConsiglio;

public interface InsediamentoConsiglioRepository extends JpaRepository<InsediamentoConsiglio,Long>, QueryDslPredicateExecutor<InsediamentoConsiglio> {

}
