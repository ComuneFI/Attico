package it.linksmt.assatti.datalayer.repository.impl;

import java.math.BigInteger;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicRicercaDto;
import it.linksmt.assatti.datalayer.repository.AooBasicRepository;

@Repository
public class AooBasicRepositoryImpl implements AooBasicRepository{
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	@SuppressWarnings("unchecked")
	public List<BigInteger> findAooIdsByNativeQuery(String queryIds){
		Query q = entityManager.createNativeQuery(queryIds);
		return q.getResultList();
	}
	
	@Override
	public List<AooBasicDto> getDirezioniBasic(){
		List<AooBasicDto> list = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.AooBasicDto("
						+ "u.id, u.codice, u.descrizione)"
						+ " From Aoo u where u.tipoAoo.codice = 'DIREZIONE' and u.validita.validoal is null order by concat(u.codice, ' - ', u.descrizione) asc"
				    , AooBasicDto.class)
				.getResultList();
		return list;
	}
	
	@Override
	public List<AooBasicRicercaDto> getAllDirezioniBasic(){
		List<AooBasicRicercaDto> list = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.AooBasicRicercaDto("
						+ "u.id, u.codice, u.descrizione, u.validita.validoal)"
						+ " From Aoo u where u.tipoAoo.codice = 'DIREZIONE' order by concat(u.codice, ' - ', u.descrizione) asc"
				    , AooBasicRicercaDto.class)
				.getResultList();
		return list;
	}
	
	@Override
	public AooBasicDto getAooBasic(Long aooId){
		AooBasicDto obj = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.AooBasicDto("
						+ "u.id, u.codice, u.descrizione)"
						+ " From Aoo u where u.id = " +aooId
				    , AooBasicDto.class)
				.getSingleResult();
		return obj; 
	}
	
	@Override
	public AooBasicDto getAooBasic(String codice){
		AooBasicDto obj = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.AooBasicDto("
						+ "u.id, u.codice, u.descrizione)"
						+ " From Aoo u where u.codice = '" +codice + "'"
				    , AooBasicDto.class)
				.getSingleResult();
		return obj; 
	}
	
}
