package it.linksmt.assatti.datalayer.repository.impl;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;
import it.linksmt.assatti.datalayer.repository.UtenteBasicRepository;

@Repository
public class UtenteBasicRepositoryImpl implements UtenteBasicRepository{
	@PersistenceContext
	private EntityManager entityManager;
	
	@Override
	public List<UtenteDto> getUtentiBasic(){
		List<UtenteDto> list = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.UtenteDto("
						+ "CONCAT(u.nome, ' ', u.cognome) as name, u.username, u.id)"
						+ " From Utente u"
				    , UtenteDto.class)
				.getResultList();
		return list; 
	}
	
	@Override
	public List<UtenteDto> getUtentiBasicAttivi(){
		List<UtenteDto> list = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.UtenteDto("
						+ "CONCAT(u.nome, ' ', u.cognome) as name, u.username, u.id)"
						+ " From Utente u WHERE validoal is null"
				    , UtenteDto.class)
				.getResultList();
		return list; 
	}
	
	@Override
	public UtenteDto getUtenteBasicByUsername(String username){
		UtenteDto dto = entityManager
				.createQuery(
						"SELECT new it.linksmt.assatti.datalayer.domain.dto.UtenteDto("
						+ "CONCAT(u.nome, ' ', u.cognome) as name, u.username, u.id)"
						+ " From Utente u WHERE username like '"+username+"'"
				    , UtenteDto.class)
				.getSingleResult();
		return dto; 
	}
}
