package it.linksmt.assatti.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.ProgressivoProposta;
import it.linksmt.assatti.datalayer.domain.QProgressivoProposta;
import it.linksmt.assatti.datalayer.repository.ProgressivoPropostaRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class ProgressivoPropostaService {
	private final Logger log = LoggerFactory.getLogger(ProgressivoPropostaService.class);
	
    @Inject
    private ProgressivoPropostaRepository progressivoPropostaRepository;
    
	@Transactional(readOnly=true)
	public Page<ProgressivoProposta> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		log.debug("Enter:"+searchJson);
		BooleanExpression predicate = QProgressivoProposta.progressivoProposta.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoos")){
			List<Long> aoosId = new ArrayList<Long>();
			for(JsonElement je : searchJson.getAsJsonArray("aoos")){
				aoosId.add(je.getAsLong());
			}
			if(aoosId.size()>0){
				predicate = predicate.and((QProgressivoProposta.progressivoProposta.aoo.id.in(aoosId)).or((QProgressivoProposta.progressivoProposta.aoo.id).isNull()));
			}
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("anno") && !searchJson.get("anno").isJsonNull() && !searchJson.get("anno").getAsString().isEmpty()){
			predicate = predicate.and(QProgressivoProposta.progressivoProposta.anno.eq(searchJson.get("anno").getAsInt()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("progressivo") && !searchJson.get("progressivo").isJsonNull() && !searchJson.get("progressivo").getAsString().isEmpty()){
			predicate = predicate.and(QProgressivoProposta.progressivoProposta.progressivo.eq(searchJson.get("progressivo").getAsInt()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo") && !searchJson.get("aoo").isJsonNull() && !searchJson.get("aoo").getAsString().isEmpty()){
			predicate = predicate.and((((QProgressivoProposta.progressivoProposta.aoo.codice.concat(" - ").concat(QProgressivoProposta.progressivoProposta.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())).or(
					(QProgressivoProposta.progressivoProposta.aoo.codice.concat("-").concat(QProgressivoProposta.progressivoProposta.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString()))).or(
					(QProgressivoProposta.progressivoProposta.aoo.id).isNull()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipoProgressivo") && !searchJson.get("tipoProgressivo").isJsonNull()){
			predicate = predicate.and(QProgressivoProposta.progressivoProposta.tipoProgressivo.id.eq(searchJson.get("tipoProgressivo").getAsJsonObject().get("id").getAsLong()));
		}
		return progressivoPropostaRepository.findAll(predicate , generatePageRequest);
	}
}
