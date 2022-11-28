package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import it.linksmt.assatti.datalayer.domain.Profilo;

public interface ProfiloRepositoryCustom {

	List<Profilo> findByRuoloAoo(List<Long> listIdRuolo, List<Long> idAoo);

	List<Profilo> findEnabledByRuoloAoo(List<Long> listIdRuolo, List<Long> listIdAoo);

}