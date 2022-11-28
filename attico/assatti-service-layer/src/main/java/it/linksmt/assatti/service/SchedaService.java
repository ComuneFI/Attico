package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.QScheda;
import it.linksmt.assatti.datalayer.domain.QTipoAtto;
import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.repository.SchedaRepository;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing profile.
 */
@Service
@Transactional
public class SchedaService {

	private final Logger log = LoggerFactory.getLogger(SchedaService.class);

	@Inject
	private SchedaRepository  schedaRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<Scheda>> search(final JsonObject search, final Integer offset, final Integer limit) throws URISyntaxException{
		BooleanExpression predicateTipoAtto = QScheda.scheda.id.isNotNull();
		if(search!=null && !search.isJsonNull()){
			if(search.get("etichetta")!=null && !search.get("etichetta").isJsonNull() && search.get("etichetta").getAsString()!=null && !"".equals(search.get("etichetta").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QScheda.scheda.etichetta.containsIgnoreCase(search.get("etichetta").getAsString()));
			}
			if(search.get("ripetitiva")!=null && !search.get("ripetitiva").isJsonNull() && search.get("ripetitiva").getAsString()!=null && !"".equals(search.get("ripetitiva").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QScheda.scheda.ripetitiva.eq(search.get("ripetitiva").getAsBoolean()));
			}
			if(search.get("ordine")!=null && !search.get("ordine").isJsonNull() && search.get("ordine").getAsString()!=null && !"".equals(search.get("ordine").getAsString())){
				predicateTipoAtto = predicateTipoAtto.and(QScheda.scheda.ordine.eq(search.get("ordine").getAsInt()));
			}
		}
		Page<Scheda> page = schedaRepository.findAll(predicateTipoAtto, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/schedas", offset, limit);
        return new ResponseEntity<List<Scheda>>(page.getContent(), headers, HttpStatus.OK);
	}
	
	@Transactional(readOnly=true)
	public Set<SchedaDato> getSchedaDato(Long id){
		log.debug("getSchedaDato");
		Scheda scheda = schedaRepository.findOne( id );
		if(scheda != null){
			Scheda minScheda = new Scheda(id);
		for (SchedaDato schedadato : scheda.getCampi()) {
			schedadato.getDato().getTipoDato();
			schedadato.setScheda(minScheda);
		}
		
		return  scheda.getCampi();
		}
		
		return null;
	}
	
	@Transactional
	public Map<String, String> delete(Long id){
		Map<String, String> risultato = new HashMap<String, String>();
		Integer attiThatUseScheda = schedaRepository.countAttiThatUseSchedaId(id);
		Integer obblighiThatUseScheda = schedaRepository.countObblighiThatUseSchedaId(id);
		String stato = "";
		if( (obblighiThatUseScheda == null || obblighiThatUseScheda.equals(0)) && (attiThatUseScheda == null || attiThatUseScheda.equals(0)) ){
			schedaRepository.deleteSchedaDatoOfSchedaId(id);
			schedaRepository.delete(id);
			stato = "ok";
		}else if(attiThatUseScheda == null || attiThatUseScheda.equals(0)){
			stato = "rif_obblighi";
			risultato.put("obblighiThatUseScheda", obblighiThatUseScheda+"");
		}else{
			List<BigInteger> obblighiIds = schedaRepository.selectObblighiIdsThatUseSchedaId(id);
			String obblighiIdsStr = "";
			int i = 0;
			for(BigInteger obbId : obblighiIds){
				i++;
				obblighiIdsStr += obbId;
				if(i<obblighiIds.size()){
					obblighiIdsStr += ", ";
				}
			}
			stato = "rif_atti";
			risultato.put("obblighiThatUseScheda", obblighiThatUseScheda+"");
			risultato.put("attiThatUseScheda", attiThatUseScheda+"");
			risultato.put("obblighiIdsStr", obblighiIdsStr);
		}
		risultato.put("stato", stato);
		return risultato;
	}
	
	@Transactional
	public void deleteForce(Long id){
		schedaRepository.deleteObblighiThatUseSchedaId(id);
		schedaRepository.deleteSchedaDatoOfSchedaId(id);
		schedaRepository.delete(id);
	}
}
