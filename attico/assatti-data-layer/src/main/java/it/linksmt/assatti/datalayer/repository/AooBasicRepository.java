package it.linksmt.assatti.datalayer.repository;

import java.math.BigInteger;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicRicercaDto;

public interface AooBasicRepository{
	AooBasicDto getAooBasic(Long aooId);
	AooBasicDto getAooBasic(String codice);
	List<AooBasicDto> getDirezioniBasic();
	List<AooBasicRicercaDto> getAllDirezioniBasic();
	List<BigInteger> findAooIdsByNativeQuery(String queryIds);
}
