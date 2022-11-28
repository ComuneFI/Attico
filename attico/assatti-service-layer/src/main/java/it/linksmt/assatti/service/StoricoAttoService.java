package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyPath;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.News;
import it.linksmt.assatti.datalayer.domain.QStoricoAttoDirigenziale;
import it.linksmt.assatti.datalayer.domain.QStoricoAttoGiunta;
import it.linksmt.assatti.datalayer.domain.StatoAttoEnum;
import it.linksmt.assatti.datalayer.domain.StoricoAssenteGiunta;
import it.linksmt.assatti.datalayer.domain.StoricoAttoDirigenziale;
import it.linksmt.assatti.datalayer.domain.StoricoAttoGiunta;
import it.linksmt.assatti.datalayer.domain.StoricoCodiceAbilitazione;
import it.linksmt.assatti.datalayer.domain.StoricoDocumento;
import it.linksmt.assatti.datalayer.domain.StoricoPresenteGiunta;
import it.linksmt.assatti.datalayer.domain.dto.StoricoLavorazioniDto;
import it.linksmt.assatti.datalayer.domain.dto.StoricoSedutaDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.StoricoAttoDirigenzialeRepository;
import it.linksmt.assatti.datalayer.repository.StoricoAttoGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.StoricoDocumentoRepository;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.service.dto.StoricoAttoCriteriaDTO;
import com.mysema.commons.lang.Pair;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.Expression;
import com.mysema.query.types.Order;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.path.PathBuilder;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class StoricoAttoService {
	private final Logger log = LoggerFactory.getLogger(StoricoAttoService.class);
	
	@PersistenceContext
	private EntityManager entityManager;
	
	@Inject
	private StoricoAttoDirigenzialeRepository attoDirRepository;
	@Inject
	private StoricoAttoGiuntaRepository attoGiuntaRepository;
	@Inject
	private StoricoDocumentoRepository documentoRepository;
	
	private static DateTimeFormatter dbDf = DateTimeFormat.forPattern("yyyy-MM-dd");
	
	@Transactional(readOnly = true)
	public  Map<String, String> getMinMaxAnno(final String tipoRicerca) {
		log.debug("getMinMaxAnno tipoRicerca=" + tipoRicerca);
		
		String minAnno = "";
		String maxAnno = "";
		
		//:TODO Implementare la ricerca nel DB...
		if (tipoRicerca.equalsIgnoreCase("atti-dirigenziali")){
			minAnno = "2004";
			maxAnno = "2010";
		} else {
			minAnno = "1996";
			maxAnno = "2016";
		}
		
		Map<String, String> retValue = new HashMap<String, String>();
		retValue.put("minAnno", minAnno);
		retValue.put("maxAnno", maxAnno);
		
		return retValue;
	}
	
	@Transactional(readOnly = true)
	public  StoricoAttoDirigenziale findOneAttoDirigenziale(final Long id) {
		log.debug("findOneAttoDirigenziale id" + id);
		StoricoAttoDirigenziale atto = attoDirRepository.findOne(id);
		
		return atto;
	}
	
	@Transactional(readOnly = true)
	public  StoricoAttoGiunta findOneAttoGiunta(final Long id) {
		log.debug("findOneAttoGiunta id" + id);
		StoricoAttoGiunta atto = attoGiuntaRepository.findOne(id);

		return minimalAtto(atto);
	}

	@Transactional(readOnly = true)
	public  List<StoricoAttoGiunta> minimalAttos(final List<StoricoAttoGiunta> attiList) {

		if (attiList != null){
			List<StoricoAttoGiunta> retValue = new ArrayList<StoricoAttoGiunta>();
			for (StoricoAttoGiunta atto : attiList){
				retValue.add(minimalAtto(atto));
			}

			return retValue;
		} else
			return null;
	}

	private StoricoAttoGiunta minimalAtto(final StoricoAttoGiunta atto) {
		if (atto.getCodiciAbilitazione()!=null){
			for (StoricoCodiceAbilitazione sca : atto.getCodiciAbilitazione()){
				sca.setAttoGiunta(null);
			}
		}
		if (atto.getAssenti()!= null){
			for (StoricoAssenteGiunta asg : atto.getAssenti()){
				asg.setAttoGiunta(null);
			}
		}
		if (atto.getPresenti()!= null){
			for (StoricoPresenteGiunta psg : atto.getPresenti()){
				psg.setAttoGiunta(null);
			}
		}
		if (atto.getDocumenti()!= null){
			for (StoricoDocumento doc : atto.getDocumenti()){
				doc.setAttoGiunta(null);
			}
		}
		
		return atto;
	}
	
	@Transactional(readOnly=true)
	public Page<Aoo> getAllAooByTipoAttoAndAnno(String codTipoAtto, String anno, Pageable pageable){
		log.debug(String.format("getAllAooByTipoAttoAndAnno :: codTipoAtto=%s - anno=%s", codTipoAtto, anno));
		
		List<Aoo> retValue = new ArrayList<Aoo>();
		
		String tableName = "storico_atto_dirigenziale";
		if (!codTipoAtto.equals("DIR"))
			tableName = "storico_atto_giunta";
		
		String txtQuery = "SELECT DISTINCT id_uff_prop, uff_prop, anno "
						+ "FROM " + tableName + " "
						+ "WHERE anno = :p_anno "
						+ "ORDER BY id_uff_prop, uff_prop";
		String txtQueryCount = "SELECT COUNT(DISTINCT id_uff_prop, uff_prop, anno) "
				+ "FROM " + tableName + " "
				+ "WHERE anno = :p_anno ";
		log.debug(String.format("getAllAooByTipoAttoAndAnno :: query to be performed=%s...", txtQuery));
		
		Query query = entityManager.createNativeQuery(txtQuery);
		Query queryCount = entityManager.createNativeQuery(txtQueryCount);
		if (anno != null && !anno.equals("")) {
			query.setParameter("p_anno", anno);
			queryCount.setParameter("p_anno", anno);
		} else {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date());
		    int annoCorrente = cal.get(Calendar.YEAR);
			query.setParameter("p_anno", String.valueOf(annoCorrente));
			queryCount.setParameter("p_anno", String.valueOf(annoCorrente));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> pageContent =  query.setFirstResult(pageable.getOffset())
										   .setMaxResults(pageable.getPageSize())
										   .getResultList();
		long totalCount = ((BigInteger) queryCount.getSingleResult()).intValue();
		log.debug(String.format("getAllAooByTipoAttoAndAnno :: query performed! Found %s records.", pageContent.size()));

		long idx = 0;
		for (Object[] record : pageContent){
			Aoo aoo = new Aoo();
			aoo.setId(idx);
			aoo.setCodice((String) record[0]); 	        
			aoo.setDescrizione((String) record[1]);
			
			retValue.add(aoo);
			idx++;
		}
		
		return new PageImpl<Aoo>(retValue, pageable, totalCount);
	}
	
	@Transactional(readOnly=true)
	public Page<StoricoSedutaDto> getAllSeduteByTipiAttoAndAnno(String[] codTipiAtto, String anno, Pageable pageable){
		log.debug(String.format("getAllAooByTipoAttoAndAnno ::  anno=%s", anno));
		
		List<StoricoSedutaDto> retValue = new ArrayList<StoricoSedutaDto>();
		
		String txtTipiAttoWhereClause = "";
		if(codTipiAtto != null && codTipiAtto.length > 0){
			if (codTipiAtto.length == 1){
				txtTipiAttoWhereClause = String.format("AND tipo_prop_cod='%s'", codTipiAtto[0]);
			} else {
				txtTipiAttoWhereClause = "AND (";
				for (int ii=0; ii<codTipiAtto.length-1; ii++){
					txtTipiAttoWhereClause += String.format("tipo_prop_cod='%s' OR ", codTipiAtto[ii]);
				}
				txtTipiAttoWhereClause += String.format("tipo_prop_cod='%s')", codTipiAtto[codTipiAtto.length-1]);
			}
		}
		
		String txtQuery = "SELECT DISTINCT data_riunione_giunta, ora_riunione_giunta, tipo_riunione_giunta, num_riunione_giunta "
						+ "FROM storico_atto_giunta "
						+ "WHERE data_riunione_giunta IS NOT NULL AND anno = ?1 " + txtTipiAttoWhereClause + " "
						+ "GROUP BY data_riunione_giunta, ora_riunione_giunta, tipo_riunione_giunta, num_riunione_giunta " 
						+ "ORDER BY data_riunione_giunta";
		String txtQueryCount = "SELECT COUNT(DISTINCT data_riunione_giunta, ora_riunione_giunta, tipo_riunione_giunta, num_riunione_giunta) "
				+ "FROM storico_atto_giunta "
				+ "WHERE data_riunione_giunta IS NOT NULL AND anno = ?1 " + txtTipiAttoWhereClause;
		log.debug(String.format("getAllAooByTipoAttoAndAnno :: query to be performed=%s...", txtQuery));
		
		Query query = entityManager.createNativeQuery(txtQuery);
		Query queryCount = entityManager.createNativeQuery(txtQueryCount);
		if (anno != null && !anno.equals("")) {
			query.setParameter(1, anno);
			queryCount.setParameter(1, anno);
		} else {
		    Calendar cal = Calendar.getInstance();
		    cal.setTime(new Date());
		    int annoCorrente = cal.get(Calendar.YEAR);
			query.setParameter(1, String.valueOf(annoCorrente));
			queryCount.setParameter(1, String.valueOf(annoCorrente));
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> pageContent =  query.setFirstResult(pageable.getOffset())
										   .setMaxResults(pageable.getPageSize())
										   .getResultList();
		long totalCount = ((BigInteger) queryCount.getSingleResult()).intValue();
		log.debug(String.format("getAllSeduteByTipiAttoAndAnno :: query performed! Found %s records.", pageContent.size()));

		int idx = 0;
		for (Object[] record : pageContent){
			StoricoSedutaDto dto = new StoricoSedutaDto();
			dto.setId(idx);
			java.sql.Date dbData = (java.sql.Date) record[0];
			if (dbData !=null){
				dto.setDataRiunione(new SimpleDateFormat("dd/MM/yyyy").format(dbData));
				dto.setDataRiunioneDB(new SimpleDateFormat("yyyy-MM-dd").format(dbData));
			}
			else{
				dto.setDataRiunione("");
				dto.setDataRiunioneDB("");
			}
			dto.setOraRiunione((String) record[1]);
			dto.setTipoRiunione((String) record[2]);
			dto.setNumRiunione((String) record[3]);
			
			retValue.add(dto);
			idx++;
		}
		
		return new PageImpl<StoricoSedutaDto>(retValue, pageable, totalCount);
	}
	
	private Page<Object[]> performPaginatedQuery(
			final String txtQuery, 
			final String txtQueryCount, 
			final StoricoAttoCriteriaDTO criteria, 
			final Pageable pageable){
		Query query = entityManager.createNativeQuery(txtQuery);
		Query queryCount = entityManager.createNativeQuery(txtQueryCount);
		
		if (criteria.getAnno() != null && !criteria.getAnno().equals("")) {
			query.setParameter("p_anno", criteria.getAnno());
			queryCount.setParameter("p_anno", criteria.getAnno());
		} else {
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int annoCorrente = cal.get(Calendar.YEAR);
			query.setParameter("p_anno", String.valueOf(annoCorrente));
			queryCount.setParameter("p_anno", String.valueOf(annoCorrente));
		}
		if (criteria.getAooCodice() != null && !criteria.getAooCodice().equals("")) {
			query.setParameter("p_aooCod", criteria.getAooCodice());
			queryCount.setParameter("p_aooCod", criteria.getAooCodice());
		}
		if (criteria.getAooDescr() != null && !criteria.getAooDescr().equals("")) {
			query.setParameter("p_aooDescr", criteria.getAooDescr());
			queryCount.setParameter("p_aooDescr", criteria.getAooDescr());
		}
		if (criteria.getDataRiunioneGiunta() != null && !criteria.getDataRiunioneGiunta().equals("")) {
			query.setParameter("p_dataRiunioneDa", dbDf.print(criteria.getDataRiunioneGiunta()));
			queryCount.setParameter("p_dataRiunioneDa", dbDf.print(criteria.getDataRiunioneGiunta()));
		}
		if (criteria.getDataRiunioneGiuntaA() != null && !criteria.getDataRiunioneGiuntaA().equals("")) {
			query.setParameter("p_dataRiunioneA", dbDf.print(criteria.getDataRiunioneGiuntaA()));
			queryCount.setParameter("p_dataRiunioneA", dbDf.print(criteria.getDataRiunioneGiuntaA()));
		}
		if (criteria.getOraRiunioneGiunta() != null && !criteria.getOraRiunioneGiunta().equals("")) {
			query.setParameter("p_oraRiunione", criteria.getOraRiunioneGiunta());
			queryCount.setParameter("p_oraRiunione", criteria.getOraRiunioneGiunta());
		}
		if (criteria.getTipoRiunioneGiunta() != null && !criteria.getTipoRiunioneGiunta().equals("")) {
			query.setParameter("p_tipoRiunione", criteria.getTipoRiunioneGiunta());
			queryCount.setParameter("p_tipoRiunione", criteria.getTipoRiunioneGiunta());
		}
		if (criteria.getNumRiunioneGiunta() != null && !criteria.getNumRiunioneGiunta().equals("")) {
			query.setParameter("p_numRiunione", criteria.getNumRiunioneGiunta());
			queryCount.setParameter("p_numRiunione", criteria.getNumRiunioneGiunta());
		}
		
		@SuppressWarnings("unchecked")
		List<Object[]> pageContent =  query.setFirstResult(pageable.getOffset())
		  				    			   .setMaxResults(pageable.getPageSize())
		  				    			   .getResultList();
		long totalCount = ((BigInteger) queryCount.getSingleResult()).intValue();
		
		return new PageImpl<Object[]>(pageContent, pageable, totalCount);
	}
	
	@Transactional(readOnly=true)
	public Page<StoricoAttoDirigenziale> findAllAttoDirigenzialeByAoo(final StoricoAttoCriteriaDTO criteria, final Pageable pageable) throws GestattiCatchedException{
		if (criteria.getAooCodice() == null || criteria.getAooCodice().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaByAoo richiede il criteria.getAooCodice()");
		}
		if (criteria.getAooDescr() == null || criteria.getAooDescr().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaByAoo richiede il criteria.getAooDescr()");
		}
		
		log.debug(String.format("findAllAttoDirigenzialeByAoo ::  anno=%s; aoo=[%s|%s]", criteria.getAnno(), criteria.getAooCodice(), criteria.getAooDescr()));

		String txtQuery = "SELECT DISTINCT p.id_prop, f.num_provv, f.data_provv "
				+ "FROM storico_atto_dirigenziale p LEFT OUTER JOIN storico_atto_dirigenziale f ON "
						+ "(p.id_attodirigenziale = f.id_attodirigenziale AND "
						+ "(f.num_provv IS NOT NULL AND f.num_provv != '') AND "
						+ "(f.data_provv IS NOT NULL AND f.data_provv != '')) "
				+ "WHERE p.anno = :p_anno AND p.id_uff_prop = :p_aooCod AND p.uff_prop = :p_aooDescr "
				+ "GROUP BY p.id_prop " 
				+ "ORDER BY p.id_prop ASC, p.datainvio ASC";
		String txtQueryCount = "SELECT COUNT(DISTINCT p.id_prop) "
				+ "FROM storico_atto_dirigenziale p LEFT OUTER JOIN storico_atto_dirigenziale f ON "
						+ "(p.id_attodirigenziale = f.id_attodirigenziale AND "
						+ "(f.num_provv IS NOT NULL AND f.num_provv != '') AND "
						+ "(f.data_provv IS NOT NULL AND f.data_provv != '')) "
				+ "WHERE p.anno = :p_anno AND p.id_uff_prop = :p_aooCod AND p.uff_prop = :p_aooDescr ";
		
		log.debug(String.format("findAllAttoDirigenzialeByAoo :: query to be performed=%s...", txtQuery));

		Page<Object[]> dbRecordsPage = performPaginatedQuery(txtQuery, txtQueryCount, criteria, pageable);
		log.debug(String.format("findAllAttoDirigenzialeByAoo :: query performed! Found %s records.", dbRecordsPage.getTotalElements()));
		
		List<StoricoAttoDirigenziale> retValue = new ArrayList<StoricoAttoDirigenziale>();
		for (Object[] record : dbRecordsPage.getContent()){
			StoricoAttoDirigenziale atto = new StoricoAttoDirigenziale();
			atto.setCodiceCifra((String) record[0]); 	        
			atto.setNumeroAdozione((String) record[1]);
			java.sql.Date sqlDate = (java.sql.Date) record[2];
			atto.setDataProvvedimento(new LocalDate(sqlDate.getTime()));
			
			retValue.add(atto);
		}
		
		return new PageImpl<StoricoAttoDirigenziale>(retValue, pageable, dbRecordsPage.getTotalElements());
	}
	
	@Transactional(readOnly=true)
	public Page<StoricoAttoGiunta> findAllAttoGiuntaByAoo(final StoricoAttoCriteriaDTO criteria, final Pageable pageable) throws GestattiCatchedException{
		if (criteria.getAooCodice() == null || criteria.getAooCodice().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaByAoo richiede il criteria.getAooCodice()");
		}
		if (criteria.getAooDescr() == null || criteria.getAooDescr().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaByAoo richiede il criteria.getAooDescr()");
		}
		
		log.debug(String.format("findAllAttoGiuntaByAoo ::  anno=%s; aoo=[%s|%s]", criteria.getAnno(), criteria.getAooCodice(), criteria.getAooDescr()));

		String txtQuery = "SELECT DISTINCT p.id_prop, f.num_provv, f.data_provv "
				+ "FROM storico_atto_giunta p LEFT OUTER JOIN storico_atto_giunta f ON "
						+ "(p.idatto_giunta = f.idatto_giunta AND "
						+ "(f.num_provv IS NOT NULL AND f.num_provv != '') AND "
						+ "(f.data_provv IS NOT NULL AND f.data_provv != '')) "
				+ "WHERE p.anno = :p_anno AND p.id_uff_prop = :p_aooCod AND p.uff_prop = :p_aooDescr "
				+ "GROUP BY p.id_prop " 
				+ "ORDER BY p.id_prop ASC, p.datainvio ASC";
		String txtQueryCount = "SELECT COUNT(DISTINCT p.id_prop) "
				+ "FROM storico_atto_giunta p LEFT OUTER JOIN storico_atto_giunta f ON "
						+ "(p.idatto_giunta = f.idatto_giunta AND "
						+ "(f.num_provv IS NOT NULL AND f.num_provv != '') AND "
						+ "(f.data_provv IS NOT NULL AND f.data_provv != '')) "
				+ "WHERE p.anno = :p_anno AND p.id_uff_prop = :p_aooCod AND p.uff_prop = :p_aooDescr ";
		
		log.debug(String.format("findAllAttoGiuntaByAoo :: query to be performed=%s...", txtQuery));
		Page<Object[]> dbRecordsPage = performPaginatedQuery(txtQuery, txtQueryCount, criteria, pageable);
		log.debug(String.format("findAllAttoGiuntaByAoo :: query performed! Found %s records.", dbRecordsPage.getTotalElements()));
		
		List<StoricoAttoGiunta> retValue = new ArrayList<StoricoAttoGiunta>();
		for (Object[] record : dbRecordsPage.getContent()){
			StoricoAttoGiunta atto = new StoricoAttoGiunta();
			atto.setCodiceCifra((String) record[0]); 	        
			atto.setNumeroAdozione((String) record[1]);
			
			if (record[2] != null){
				try{
					java.sql.Date sqlDate = (java.sql.Date) record[2];
					atto.setDataAdozione(new LocalDate(sqlDate.getTime()));
				} catch (Exception exp) { 
					atto.setDataAdozione(null);
				}
			}
			else 
				atto.setDataAdozione(null);
			
			retValue.add(atto);
		}
		
		return new PageImpl<StoricoAttoGiunta>(retValue, pageable, dbRecordsPage.getTotalElements());
	}
	
	@Transactional(readOnly=true)
	public Page<StoricoAttoGiunta> findAllAttoGiuntaBySeduta(final StoricoAttoCriteriaDTO criteria, final Pageable pageable) throws GestattiCatchedException{
		if (criteria.getDataRiunioneGiunta() == null ){
			throw new GestattiCatchedException("Service findAllAttoGiuntaBySeduta richiede il criteria.getDataRiunioneGiunta()");
		}
		if (criteria.getDataRiunioneGiuntaA() == null ){
			throw new GestattiCatchedException("Service findAllAttoGiuntaBySeduta richiede il criteria.getDataRiunioneGiuntaA()");
		}
		if (criteria.getOraRiunioneGiunta() == null || criteria.getOraRiunioneGiunta().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaBySeduta richiede il criteria.getOraRiunioneGiunta()");
		}
		if (criteria.getTipoRiunioneGiunta() == null || criteria.getTipoRiunioneGiunta().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaBySeduta richiede il criteria.getTipoRiunioneGiunta()");
		}
		if (criteria.getNumRiunioneGiunta() == null || criteria.getNumRiunioneGiunta().isEmpty()){
			throw new GestattiCatchedException("Service findAllAttoGiuntaBySeduta richiede il criteria.getNumRiunioneGiunta()");
		}
		
		log.debug(String.format("findAllAttoGiuntaBySeduta ::  anno=%s;", criteria.getAnno()));

		String txtQuery = "SELECT DISTINCT p.id_prop, p.data_prop "
				+ "FROM storico_atto_giunta p "
				+ "WHERE p.anno = :p_anno AND "
					  + "p.data_riunione_giunta >= :p_dataRiunioneDa AND p.data_riunione_giunta <= :p_dataRiunioneA AND "
					  + "p.ora_riunione_giunta = :p_oraRiunione AND "
					  + "p.tipo_riunione_giunta = :p_tipoRiunione AND "
					  + "p.num_riunione_giunta = :p_numRiunione "
				+ "GROUP BY p.id_prop " 
				+ "ORDER BY p.id_prop ASC, p.data_prop ASC";
		String txtQueryCount = "SELECT COUNT(DISTINCT p.id_prop, p.data_prop) "
				+ "FROM storico_atto_giunta p "
				+ "WHERE p.anno = :p_anno AND "
					  + "p.data_riunione_giunta >= :p_dataRiunioneDa AND p.data_riunione_giunta <= :p_dataRiunioneA AND "
					  + "p.ora_riunione_giunta = :p_oraRiunione AND "
					  + "p.tipo_riunione_giunta = :p_tipoRiunione AND "
					  + "p.num_riunione_giunta = :p_numRiunione ";
		
		log.debug(String.format("findAllAttoGiuntaBySeduta :: query to be performed=%s...", txtQuery));

		Page<Object[]> dbRecordsPage = performPaginatedQuery(txtQuery, txtQueryCount, criteria, pageable);
		log.debug(String.format("findAllAttoGiuntaBySeduta :: query performed! Found %s records.", dbRecordsPage.getTotalElements()));
		
		List<StoricoAttoGiunta> retValue = new ArrayList<StoricoAttoGiunta>();
		for (Object[] record : dbRecordsPage.getContent()){
			StoricoAttoGiunta atto = new StoricoAttoGiunta();
			atto.setCodiceCifra((String) record[0]); 	        
			java.sql.Date sqlDate = (java.sql.Date) record[1];
			atto.setDataCreazione(new LocalDate(sqlDate.getTime()));
			
			retValue.add(atto);
		}
		
		return new PageImpl<StoricoAttoGiunta>(retValue, pageable, dbRecordsPage.getTotalElements());
		
	}
	
	@Transactional(readOnly=true)
	public Page<StoricoAttoDirigenziale> findAllAttoDirigenzialePaginato(final Pageable generatePageRequest, final StoricoAttoCriteriaDTO criteria) {
		
		BooleanExpression expression = buildAttoDirSearchExpression(criteria);
		
		Page<StoricoAttoDirigenziale> page = attoDirRepository.findAll(expression, generatePageRequest);
		log.debug("Lunghezza Pagina:" + page.getSize());
		
		return page;
	}

	@Transactional(readOnly=true)
	public Page<StoricoAttoGiunta> findAllAttoGiuntaPaginato(final Pageable generatePageRequest, final StoricoAttoCriteriaDTO criteria) {
		
		BooleanExpression expression = buildAttoGiuntaSearchExpression(criteria);
		
		Page<StoricoAttoGiunta> page = attoGiuntaRepository.findAll(expression, generatePageRequest);
		for (StoricoAttoGiunta atto : page.getContent()){
			minimalAtto(atto);
		}
		
		log.debug("Lunghezza Pagina:" + page.getSize());
		
		return page;
	}
	
	@Transactional(readOnly=true)
	public List<String> getAllTipiIter(){
		log.debug("getAllTipiIter :: ");
		
//		String txtQuery = "SELECT DISTINCT tipo_iter "
//						+ "FROM storico_atto_dirigenziale "
//						+ "WHERE tipo_iter != '' "
//						+ "ORDER BY tipo_iter";
//		log.debug(String.format("getAllTipiIter :: query to be performed=%s...", txtQuery));
//		
//		Query query = entityManager.createNativeQuery(txtQuery);
//
//		@SuppressWarnings("unchecked")
//		List<String> retValue = query.getResultList();
//		log.debug(String.format("getAllTipiIter :: query performed! Found %s records.", retValue.size()));
		
		List<String> retValue = new ArrayList<String>();
		retValue.add("Da sottoporre a verifica contabile");
		retValue.add("Direttamente esecutivo con adempimenti contabili");
		retValue.add("Direttamente esecutivo senza adempimenti contabili");

		return retValue;
	}
	
	@Transactional(readOnly=true)
	public List<String> getAllTipiAdempimenti(){
		log.debug("getAllTipiAdempimenti :: ");
		
		String txtQuery = "SELECT DISTINCT tipo_adem "
						+ "FROM storico_atto_dirigenziale "
						+ "WHERE tipo_adem != '' "
						+ "ORDER BY tipo_adem";
		log.debug(String.format("getAllTipiAdempimenti :: query to be performed=%s...", txtQuery));
		
		Query query = entityManager.createNativeQuery(txtQuery);
		
		@SuppressWarnings("unchecked")
		List<String> retValue = query.getResultList();
		log.debug(String.format("getAllTipiAdempimenti :: query performed! Found %s records.", retValue.size()));

		return retValue;
	}
	
	@Transactional(readOnly=true)
	public List<String> getAllTipiRiunioneGiunta(){
//		log.debug("getAllTipiRiunioneGiunta :: ");
//		
//		String txtQuery = "SELECT DISTINCT tipo_riunione_giunta "
//						+ "FROM storico_atto_giunta "
//						+ "WHERE tipo_riunione_giunta != '' "
//						+ "ORDER BY tipo_riunione_giunta";
//		log.debug(String.format("getAllTipiRiunioneGiunta :: query to be performed=%s...", txtQuery));
//		
//		Query query = entityManager.createNativeQuery(txtQuery);
//		
//		@SuppressWarnings("unchecked")
//		List<String> retValue = query.getResultList();
//		log.debug(String.format("getAllTipiRiunioneGiunta :: query performed! Found %s records.", retValue.size()));
		List<String> retValue = new ArrayList<String>();
		retValue.add("Fuori Sacco");
		retValue.add("FuoriSacco");
		retValue.add("Ordinario");
		retValue.add("Straordinario");
		retValue.add("Suppletivo");

		return retValue;
	}
	
	@Transactional(readOnly=true)
	public List<StoricoLavorazioniDto> findAllLavorazioniAttoDirigenziale(final String codiceCifra){
		StoricoAttoCriteriaDTO criteria = new StoricoAttoCriteriaDTO();
		criteria.setCodiceCifra(codiceCifra);
		BooleanExpression expression = buildAttoDirSearchExpression(criteria);
		Iterable<StoricoAttoDirigenziale> attiIT = attoDirRepository.findAll(expression);
        
        List<StoricoAttoDirigenziale> atti = new ArrayList<StoricoAttoDirigenziale>();
		StoricoAttoDirigenziale attoPrec = null;
		if (attiIT != null){
			Iterator<StoricoAttoDirigenziale> itr = attiIT.iterator();
			
			while (itr.hasNext()){
				StoricoAttoDirigenziale atto = itr.next();
				attoPrec = caricaLavorazioniDir(atto, atti, attoPrec);
			}
			
			log.debug(String.format("findAllLavorazioniAttoDirigenziale :: end while: caricate %s StoricoAttoDirigenziale.", atti.size()));
		}
		
		List<StoricoLavorazioniDto> retValue = null;
		if (atti != null && atti.size() > 0){
			if (atti.get(0).getCodiceCifra().equalsIgnoreCase(codiceCifra)){
				retValue = atti.get(0).getListaLavorazioni();
			}
		}
        
		return retValue;
	}
	
	@Transactional(readOnly=true)
	public List<StoricoLavorazioniDto> findAllLavorazioniAttoGiunta(final String codiceCifra){
		StoricoAttoCriteriaDTO criteria = new StoricoAttoCriteriaDTO();
		criteria.setCodiceCifra(codiceCifra);
		BooleanExpression expression = buildAttoGiuntaSearchExpression(criteria);
		
		Iterable<StoricoAttoGiunta> attiIT = attoGiuntaRepository.findAll(expression);
        
        List<StoricoAttoGiunta> atti = new ArrayList<StoricoAttoGiunta>();
		StoricoAttoGiunta attoPrec = null;
		if (attiIT != null){
			Iterator<StoricoAttoGiunta> itr = attiIT.iterator();
			
			while (itr.hasNext()){
				StoricoAttoGiunta atto = itr.next();
				attoPrec = caricaLavorazioniGiunta(atto, atti, attoPrec);
			}
			log.debug(String.format("findAllLavorazioniAttoGiunta :: end while: caricate %s StoricoAttoGiunta.", atti.size()));
		}
		
		List<StoricoLavorazioniDto> retValue = null;
		if (atti != null && atti.size() > 0){
			if (atti.get(0).getCodiceCifra().equalsIgnoreCase(codiceCifra)){
				retValue = atti.get(0).getListaLavorazioni();
			}
		}
        
		return retValue;
	}
	
	private StoricoAttoDirigenziale caricaLavorazioniDir(StoricoAttoDirigenziale currentAtto, List<StoricoAttoDirigenziale> listaAtti, StoricoAttoDirigenziale precAtto){
		StoricoAttoDirigenziale retValue = null;
		List<StoricoLavorazioniDto> listaLavorazioni = null;
		
		if (precAtto == null || (precAtto != null && !precAtto.getCodiceCifra().equals(currentAtto.getCodiceCifra()))){
			listaAtti.add(currentAtto);
			retValue = currentAtto;
			listaLavorazioni = currentAtto.getListaLavorazioni();
		} else {
			retValue = precAtto;
			listaLavorazioni = precAtto.getListaLavorazioni();
		}
		
		if (listaLavorazioni != null){
			StoricoLavorazioniDto dto = new StoricoLavorazioniDto();
			dto.setIdAtto(currentAtto.getId());
			dto.setLavorazione(currentAtto.getLavorazioneEffettuata());
			dto.setOggetto(currentAtto.getOggetto());
			listaLavorazioni.add(dto);
		}

		return retValue;
	}
	private StoricoAttoGiunta caricaLavorazioniGiunta(StoricoAttoGiunta currentAtto, List<StoricoAttoGiunta> listaAtti, StoricoAttoGiunta precAtto){
		StoricoAttoGiunta retValue = null;
		List<StoricoLavorazioniDto> listaLavorazioni = null;
		
		if (precAtto == null || (precAtto != null && !precAtto.getCodiceCifra().equals(currentAtto.getCodiceCifra()))){
			listaAtti.add(currentAtto);
			retValue = currentAtto;
			listaLavorazioni = currentAtto.getListaLavorazioni();
		} else {
			retValue = precAtto;
			listaLavorazioni = precAtto.getListaLavorazioni();
		}
		
		if (listaLavorazioni != null){
			StoricoLavorazioniDto dto = new StoricoLavorazioniDto();
			dto.setIdAtto(currentAtto.getId());
			
			if (currentAtto.getAnnullato() != null && currentAtto.getAnnullato().equalsIgnoreCase("si"))
				dto.setLavorazione("Annullato dal Proponente");
			else
				dto.setLavorazione(currentAtto.getLavorazioneEffettuata());
			
			dto.setOggetto(currentAtto.getOggetto());
			if (currentAtto.getDataAdozione()!=null)
				dto.setDataAdozione(DateTimeFormat.forPattern("dd-MM-yyyy").print(currentAtto.getDataAdozione()));
			dto.setNumeroAdozione(currentAtto.getNumeroAdozione());
			dto.setTipoProvvedimento(currentAtto.getDescrizioneTipoProvvedimento());
			
			listaLavorazioni.add(dto);
		}

		return retValue;
	}
	
	private BooleanExpression buildAttoDirSearchExpression(final StoricoAttoCriteriaDTO criteria) {
		BooleanExpression expression = QStoricoAttoDirigenziale.storicoAttoDirigenziale.id.isNotNull();
		
		if(criteria.getAooCodice()!=null){
			expression = expression.and(QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceArea.eq(criteria.getAooCodice()));
		}
		
		// Gestione dei casi che richiedono query ad hoc (rif. Sincon Redmine Bug #12295 (SEG_07))
		if(criteria.getViewtype()!= null && criteria.getViewtype().length() > 0){
			if (criteria.getViewtype().equalsIgnoreCase("esecutive")){
				criteria.setCodiceLavorazione("(ema-adir-dire-esec);(esec-adir)");
			} else if (criteria.getViewtype().equalsIgnoreCase("annullate")){
				criteria.setCodiceLavorazione("(ines-adir);(esec-nega-adir);(rinu-adir)");
			} else if (criteria.getViewtype().equalsIgnoreCase("dir_esecutive")){
				criteria.setCodiceLavorazione("(ema-adir-dire-esec)");
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoIter.equalsIgnoreCase("Direttamente esecutivo senza adempimenti contabili")
						            		   .or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoAdempimento.equalsIgnoreCase("Senza Adempimenti Contabili")));
			} else if (criteria.getViewtype().equalsIgnoreCase("adem_cont")){
				criteria.setCodiceLavorazione("(ema-adir-dire-esec)");
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoIter.equalsIgnoreCase("Direttamente esecutivo con adempimenti contabili")
			            		   .or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoAdempimento.notEqualsIgnoreCase("Senza Adempimenti Contabili")));
			} else if (criteria.getViewtype().equalsIgnoreCase("verif_cont")){
				criteria.setCodiceLavorazione("(esec-adir);(esec-nega-adir)");
			}
		}
		if(criteria.getCodiceLavorazione() != null && !"".equals(criteria.getCodiceLavorazione()) ){
			if(!criteria.getCodiceLavorazione().contains(";") || criteria.getCodiceLavorazione().split(";").length < 2){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase(criteria.getCodiceLavorazione().split(";")[0]) );
			}else{
				String lavorazioni[] = criteria.getCodiceLavorazione().split(";");
				BooleanExpression internalExp = null;
				for(int i = 0; i<lavorazioni.length; i++){
					if(i == 0){
						internalExp = QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase(lavorazioni[i]);
					}else{
						internalExp = internalExp.or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase(lavorazioni[i]));
					}
				}
				expression = expression.and(internalExp);
			}
		}

		if(criteria.getTipiAttoCodici() != null && criteria.getTipiAttoCodici().size() > 0){
			BooleanExpression tipiAttoExpression = null;
			for(String codTipoAtto : criteria.getTipiAttoCodici()){
				if(tipiAttoExpression == null){
					tipiAttoExpression = QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceTipoAtto.eq(codTipoAtto);
				}else{
					tipiAttoExpression = tipiAttoExpression.or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceTipoAtto.eq(codTipoAtto));
				}
			}
			if(tipiAttoExpression!=null){
				expression = expression.and( tipiAttoExpression );
			}
		}

		if(criteria.getArea()  != null && !"".equals(criteria.getArea()) ){
			BooleanExpression internalPredicate = ((QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceArea.concat(" - ").concat(QStoricoAttoDirigenziale.storicoAttoDirigenziale.descrizioneArea)).containsIgnoreCase(criteria.getArea())).
					or((QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceArea.concat("-").concat(QStoricoAttoDirigenziale.storicoAttoDirigenziale.descrizioneArea)).containsIgnoreCase(criteria.getArea()));
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}

		if(criteria.getCodiceCifra()  != null && !"".equals(criteria.getCodiceCifra()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()  ) );
		}
		if(criteria.getAnno()  != null && !"".equals(criteria.getAnno()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataCreazione.year().eq(Integer.parseInt(criteria.getAnno() ) ) );
		}
		if(criteria.getNumeroAdozione() != null && !"".equals(criteria.getNumeroAdozione()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.numeroAdozione.containsIgnoreCase(criteria.getNumeroAdozione()  ) );
		}
		if(criteria.getOggetto()  != null && !"".equals(criteria.getOggetto()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.oggetto.containsIgnoreCase(criteria.getOggetto()  ) );
		}
		if(criteria.getTipoIter()  != null && !"".equals(criteria.getTipoIter()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoIter.equalsIgnoreCase(criteria.getTipoIter()  ) );
		}
		if(criteria.getTipoAdempimento()  != null && !"".equals(criteria.getTipoAdempimento()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.tipoAdempimento.equalsIgnoreCase(criteria.getTipoAdempimento()  ) );
		}
		if((criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())) && (criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA()))  ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataProvvedimento.goe(criteria.getDataAdozione()  ) );
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataProvvedimento.loe(criteria.getDataAdozioneA()  ) );
		}
		else{
			if(criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataProvvedimento.goe(criteria.getDataAdozione()  ) );
			}
			
			if(criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataProvvedimento.loe(criteria.getDataAdozioneA()  ) );
			}
		}
		if(criteria.getStato()  != null && !"".equals(criteria.getStato()) ){
			if (criteria.getStato().equalsIgnoreCase("inesistente")){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(ines-adir)"));
			} else if (criteria.getStato().equalsIgnoreCase("annullato")){
				BooleanExpression internalExp = (QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(esec-nega-adir)")).
						or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(rinu-adir)")).
						or(QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(annu-adir)"));
				
				expression = expression.and(internalExp);
			}
		}
		if(criteria.getEsitoVerificaContabile() != null && !"".equals(criteria.getEsitoVerificaContabile()) ){
			if (criteria.getEsitoVerificaContabile().equalsIgnoreCase("Atto Esecutivo")){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(esec-adir)"));
			} else if (criteria.getEsitoVerificaContabile().equalsIgnoreCase("Atto Non Esecutivo")){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.codiceLavorazione.equalsIgnoreCase("(esec-nega-adir)"));
			}
		}
		if((criteria.getDataEsecutivita()  != null && !"".equals(criteria.getDataEsecutivita())) && (criteria.getDataEsecutivitaA()  != null && !"".equals(criteria.getDataEsecutivitaA()))  ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataEsecutivita.goe(criteria.getDataEsecutivita()  ) );
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataEsecutivita.loe(criteria.getDataEsecutivitaA()  ) );
		}
		else{
			if(criteria.getDataEsecutivita()  != null && !"".equals(criteria.getDataEsecutivita())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataEsecutivita.goe(criteria.getDataEsecutivita()  ) );
			}
			
			if(criteria.getDataEsecutivitaA()  != null && !"".equals(criteria.getDataEsecutivitaA())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataEsecutivita.loe(criteria.getDataEsecutivitaA()  ) );
			}
		}
		if((criteria.getDataInizioAffissione()  != null && !"".equals(criteria.getDataInizioAffissione())) && (criteria.getDataInizioAffissioneA()  != null && !"".equals(criteria.getDataInizioAffissioneA()))  ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneInizio.goe(criteria.getDataInizioAffissione()  ) );
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneInizio.loe(criteria.getDataInizioAffissioneA()  ) );
		}
		else{
			if(criteria.getDataInizioAffissione()  != null && !"".equals(criteria.getDataInizioAffissione())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneInizio.goe(criteria.getDataInizioAffissione()  ) );
			}
			
			if(criteria.getDataInizioAffissioneA()  != null && !"".equals(criteria.getDataInizioAffissioneA())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneInizio.loe(criteria.getDataInizioAffissioneA()  ) );
			}
		}
		if((criteria.getDataFineAffissione()  != null && !"".equals(criteria.getDataFineAffissione())) && (criteria.getDataFineAffissioneA()  != null && !"".equals(criteria.getDataFineAffissioneA()))  ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneFine.goe(criteria.getDataFineAffissione()  ) );
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneFine.loe(criteria.getDataFineAffissioneA()  ) );
		}
		else{
			if(criteria.getDataFineAffissione()  != null && !"".equals(criteria.getDataFineAffissione())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneFine.goe(criteria.getDataFineAffissione()  ) );
			}
			
			if(criteria.getDataFineAffissioneA()  != null && !"".equals(criteria.getDataFineAffissioneA())){
				expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.dataAffissioneFine.loe(criteria.getDataFineAffissioneA()  ) );
			}
		}
		if(criteria.getRedigente() != null && !"".equals(criteria.getRedigente()) ){
			expression = expression.and( QStoricoAttoDirigenziale.storicoAttoDirigenziale.redigente.containsIgnoreCase(criteria.getRedigente()));
		}
		return expression;
	}
	
	private BooleanExpression buildAttoGiuntaSearchExpression(final StoricoAttoCriteriaDTO criteria) {
		BooleanExpression expression = QStoricoAttoGiunta.storicoAttoGiunta.id.isNotNull();
		
		// Gestione dei casi che richiedono query ad hoc (rif. Sincon Redmine Bug #12295 (SEG_07))
		if(criteria.getViewtype()!= null && criteria.getViewtype().length() > 0){
			if (criteria.getViewtype().equalsIgnoreCase("adottate")){
				criteria.setCodiceLavorazione("adoz-giun-deli-dire-esec");
			} else if (criteria.getViewtype().equalsIgnoreCase("annullate")){
				criteria.setCodiceLavorazione("rinu-prop");
				criteria.setAnnullato("SI");
			} else if (criteria.getViewtype().equalsIgnoreCase("com_verbalizzate")){
				criteria.setCodiceLavorazione("verb-comu-giun");
			} else if (criteria.getViewtype().equalsIgnoreCase("sdl_presiatto")){
				criteria.setCodiceLavorazione("pres-atto");
			} else if (criteria.getViewtype().equalsIgnoreCase("sdl_ddl_approvati")){
				criteria.setCodiceLavorazione("appr-dise-legg");
			} else if (criteria.getViewtype().equalsIgnoreCase("sedute_giunta")){
				criteria.setCodiceLavorazione("appr-dise-legg;pres-atto;verb-comu-giun;adoz-giun-deli-dire-esec;riti-giun;argo-nt;rinv-giun");
			}
		}
		if(criteria.getCodiceLavorazione() != null && !"".equals(criteria.getCodiceLavorazione()) ){
			if(!criteria.getCodiceLavorazione().contains(";") || criteria.getCodiceLavorazione().split(";").length < 2){
				expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.codiceLavorazione.equalsIgnoreCase(criteria.getCodiceLavorazione().split(";")[0]) );
			}else{
				String lavorazioni[] = criteria.getCodiceLavorazione().split(";");
				BooleanExpression internalExp = null;
				for(int i = 0; i<lavorazioni.length; i++){
					if(i == 0){
						internalExp = QStoricoAttoGiunta.storicoAttoGiunta.codiceLavorazione.equalsIgnoreCase(lavorazioni[i]);
					}else{
						internalExp = internalExp.or(QStoricoAttoGiunta.storicoAttoGiunta.codiceLavorazione.equalsIgnoreCase(lavorazioni[i]));
					}
				}
				expression = expression.and(internalExp);
			}
		}
		
		if(criteria.getAooCodice()!=null){
			expression = expression.and(QStoricoAttoGiunta.storicoAttoGiunta.codiceArea.eq(criteria.getAooCodice()));
		}
		if(criteria.getAooDescr()!=null){
			expression = expression.and(QStoricoAttoGiunta.storicoAttoGiunta.descrizioneArea.eq(criteria.getAooDescr()));
		}
		
		if (criteria.getTipiAttoCodici()!= null){
			BooleanExpression tipiAttoExpression = null;
			for(String codTipoAtto : criteria.getTipiAttoCodici()){
				if(tipiAttoExpression == null){
					tipiAttoExpression = QStoricoAttoGiunta.storicoAttoGiunta.codiceTipoAtto.eq(codTipoAtto);
				}else{
					tipiAttoExpression = tipiAttoExpression.or(QStoricoAttoGiunta.storicoAttoGiunta.codiceTipoAtto.eq(codTipoAtto));
				}
			}
			if(tipiAttoExpression!=null){
				expression = expression.and( tipiAttoExpression );
			}
		}

		if(criteria.getArea()  != null && !"".equals(criteria.getArea()) ){
			BooleanExpression internalPredicate = ((QStoricoAttoGiunta.storicoAttoGiunta.codiceArea.concat(" - ").concat(QStoricoAttoGiunta.storicoAttoGiunta.descrizioneArea)).containsIgnoreCase(criteria.getArea())).
					or((QStoricoAttoGiunta.storicoAttoGiunta.codiceArea.concat("-").concat(QStoricoAttoGiunta.storicoAttoGiunta.descrizioneArea)).containsIgnoreCase(criteria.getArea()));
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}
		
		if(criteria.getAnnullato()  != null && !"".equals(criteria.getAnnullato()) ){
			expression = expression.or( QStoricoAttoGiunta.storicoAttoGiunta.annullato.equalsIgnoreCase(criteria.getAnnullato()  ) );
		}
		
		if(criteria.getCodiceCifra()  != null && !"".equals(criteria.getCodiceCifra()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()  ) );
		}
		if(criteria.getAnno()  != null && !"".equals(criteria.getAnno()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataCreazione.year().eq(Integer.parseInt(criteria.getAnno() ) ) );
		}
		if(criteria.getOggetto()  != null && !"".equals(criteria.getOggetto()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.oggetto.containsIgnoreCase(criteria.getOggetto()  ) );
		}
		if((criteria.getDataRiunioneGiunta()  != null && !"".equals(criteria.getDataRiunioneGiunta())) && (criteria.getDataRiunioneGiuntaA()  != null && !"".equals(criteria.getDataRiunioneGiuntaA()))  ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataRiunioneGiunta.goe(criteria.getDataRiunioneGiunta()  ) );
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataRiunioneGiunta.loe(criteria.getDataRiunioneGiuntaA()  ) );
		}
		else{
			if(criteria.getDataRiunioneGiunta()  != null && !"".equals(criteria.getDataRiunioneGiunta())){
				expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataRiunioneGiunta.goe(criteria.getDataRiunioneGiunta()  ) );
			}
			
			if(criteria.getDataRiunioneGiuntaA()  != null && !"".equals(criteria.getDataRiunioneGiuntaA())){
				expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataRiunioneGiunta.loe(criteria.getDataRiunioneGiuntaA()  ) );
			}
		}
		if(criteria.getOraRiunioneGiunta()  != null && !"".equals(criteria.getOraRiunioneGiunta()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.oraRiunioneGiunta.containsIgnoreCase(criteria.getOraRiunioneGiunta()  ) );
		}
		if(criteria.getTipoRiunioneGiunta()  != null && !"".equals(criteria.getTipoRiunioneGiunta()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.tipoRiunioneGiunta.containsIgnoreCase(criteria.getTipoRiunioneGiunta()  ) );
		}
		if(criteria.getNumRiunioneGiunta() != null && !"".equals(criteria.getNumRiunioneGiunta()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.numRiunioneGiunta.containsIgnoreCase(criteria.getNumRiunioneGiunta()  ) );
		}
		if((criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())) && (criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA()))  ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataAdozione.goe(criteria.getDataAdozione()));
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataAdozione.loe(criteria.getDataAdozioneA()) );
		}
		else{
			if(criteria.getDataAdozione()  != null && !"".equals(criteria.getDataAdozione())){
				expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataAdozione.goe(criteria.getDataAdozione()) );
			}
			
			if(criteria.getDataAdozioneA()  != null && !"".equals(criteria.getDataAdozioneA())){
				expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.dataAdozione.loe(criteria.getDataAdozioneA()));
			}
		}
		if(criteria.getNumeroAdozione() != null && !"".equals(criteria.getNumeroAdozione()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.numeroAdozione.containsIgnoreCase(criteria.getNumeroAdozione()));
		}
		if(criteria.getRegolamento() != null && !"".equals(criteria.getRegolamento()) ){
			expression = expression.and( QStoricoAttoGiunta.storicoAttoGiunta.regolamento.equalsIgnoreCase(criteria.getRegolamento()));
		}
		return expression;
	}
	
	public StoricoDocumento download(Long idDocumento) {
		log.debug(String.format("download ::  idDocumento : %s", idDocumento));
		
		StoricoDocumento doc = documentoRepository.findOne(idDocumento);
		if (doc!=null){
			doc.getFileContent();
			return doc;
		}
		return null;

	}

}
