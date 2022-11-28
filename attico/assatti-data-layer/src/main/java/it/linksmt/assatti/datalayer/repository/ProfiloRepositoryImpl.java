package it.linksmt.assatti.datalayer.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import it.linksmt.assatti.datalayer.domain.Profilo;

/**
 * ProfiloRepository Repository.
 * 
 * @author Marco Ingrosso
 *
 */
@Repository
public class ProfiloRepositoryImpl implements ProfiloRepositoryCustom {
	
	private final Logger log = LoggerFactory.getLogger(ProfiloRepository.class);

	@Autowired
	private EntityManager em;

	@Override
	@SuppressWarnings("unchecked")
	public List<Profilo> findByRuoloAoo(List<Long> listIdRuolo, List<Long> listIdAoo) {
		Query query = buildSearchQuery(listIdRuolo, listIdAoo, Boolean.FALSE);
		
		return query.getResultList();
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public List<Profilo> findEnabledByRuoloAoo(List<Long> listIdRuolo, List<Long> listIdAoo) {
		Query query = buildSearchQuery(listIdRuolo, listIdAoo, Boolean.TRUE);
		
		return query.getResultList();
	}

	private Query buildSearchQuery(
			List<Long> listIdRuolo, List<Long> listIdAoo, Boolean enabled
			) {
		/*
		 * Ricavo la lista di ruoli seprati da virgola
		 */
		String commaSeparatedRuoloId = toCommaSeparated(listIdRuolo);
		log.debug("ProfiloRepositoryCustomImpl :: buildSearchQuery() :: commaSeparatedRuoloId: "+commaSeparatedRuoloId);
		
		/*
		 * Ricavo la lista di aoo seprate da virgola
		 */
		String commaSeparatedAooId = toCommaSeparated(listIdAoo);
		log.debug("ProfiloRepositoryCustomImpl :: buildSearchQuery() :: commaSeparatedAooId: "+commaSeparatedAooId);
		
		/*
		 * Creo la query dinamicamente
		 */
		String sql = "";
		
		sql += "SELECT p.* FROM ";
		
		sql += "profilo AS p JOIN grupporuolo AS gr ON p.grupporuolo_id = gr.id " + 
				"	JOIN ruolo_hasgruppi AS rhg ON gr.id = rhg.grupporuolo_id " + 
				"   JOIN ruolo AS r ON rhg.ruolo_id = r.id ";
		
		sql += "WHERE 1=1 ";
		if( commaSeparatedRuoloId!=null && !commaSeparatedRuoloId.isEmpty() ) {
			sql += "AND rhg.ruolo_id IN ("+commaSeparatedRuoloId+") ";
		}
		if( commaSeparatedAooId!=null && !commaSeparatedAooId.isEmpty() ) {
			sql += "AND aoo_id IN ("+commaSeparatedAooId+") ";
		}
		
		if(enabled){
			sql += "AND validoal is null ";
		}
		
		sql += "GROUP BY p.id ORDER BY p.descrizione ASC ";
		
		log.debug("ProfiloRepositoryCustomImpl :: buildSearchQuery() :: sql: "+sql);

		Query query = em.createNativeQuery(sql, Profilo.class);
		
		return query;
	}
	
	private String toCommaSeparated(List<Long> values) {
		String commaSeparatedId = null;
		if(values!=null && !values.isEmpty()) {
			commaSeparatedId = "";
			for(Long id : values) {
				commaSeparatedId += id + ",";
			}
			if(commaSeparatedId.endsWith(",")) {
				commaSeparatedId = commaSeparatedId.substring(0, commaSeparatedId.lastIndexOf(","));
			}
			log.debug("ProfiloRepositoryCustomImpl :: toCommaSeparated() :: commaSeparatedId: "+commaSeparatedId);
		}
		return commaSeparatedId;
	}

}
