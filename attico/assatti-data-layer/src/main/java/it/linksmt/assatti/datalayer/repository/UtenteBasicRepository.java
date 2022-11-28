package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;

public interface UtenteBasicRepository{
	List<UtenteDto> getUtentiBasic();
	List<UtenteDto> getUtentiBasicAttivi();
	UtenteDto getUtenteBasicByUsername(String username);
}
