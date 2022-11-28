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

import it.linksmt.assatti.datalayer.domain.ProgressivoAdozione;
import it.linksmt.assatti.datalayer.domain.QProgressivoAdozione;
import it.linksmt.assatti.datalayer.repository.ProgressivoAdozioneRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

@Service
@Transactional
public class ProgressivoAdozioneService {
	private final Logger log = LoggerFactory.getLogger(ProgressivoAdozioneService.class);
	
    @Inject
    private ProgressivoAdozioneRepository progressivoAdozioneRepository;
    
	@Transactional(readOnly=true)
	public Page<ProgressivoAdozione> findAll(Pageable generatePageRequest, JsonObject searchJson) throws ParseException {
		log.debug("Enter:"+searchJson);
		BooleanExpression predicate = QProgressivoAdozione.progressivoAdozione.id.isNotNull();
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoos")){
			List<Long> aoosId = new ArrayList<Long>();
			for(JsonElement je : searchJson.getAsJsonArray("aoos")){
				aoosId.add(je.getAsLong());
			}
			if(aoosId.size()>0){
				predicate = predicate.and(QProgressivoAdozione.progressivoAdozione.aoo.id.in(aoosId).or(QProgressivoAdozione.progressivoAdozione.aoo.isNull()));
			}
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("anno") && !searchJson.get("anno").isJsonNull() && !searchJson.get("anno").getAsString().isEmpty()){
			predicate = predicate.and(QProgressivoAdozione.progressivoAdozione.anno.eq(searchJson.get("anno").getAsInt()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("progressivo") && !searchJson.get("progressivo").isJsonNull() && !searchJson.get("progressivo").getAsString().isEmpty()){
			predicate = predicate.and(QProgressivoAdozione.progressivoAdozione.progressivo.eq(searchJson.get("progressivo").getAsInt()));
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("aoo") && !searchJson.get("aoo").isJsonNull() && !searchJson.get("aoo").getAsString().isEmpty()){
			if(searchJson.get("aoo").getAsString().equals("-")){
				predicate = predicate.and(QProgressivoAdozione.progressivoAdozione.aoo.isNull());
			}else{
				predicate = predicate.and(((QProgressivoAdozione.progressivoAdozione.aoo.codice.concat(" - ").concat(QProgressivoAdozione.progressivoAdozione.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())).or(
						(QProgressivoAdozione.progressivoAdozione.aoo.codice.concat("-").concat(QProgressivoAdozione.progressivoAdozione.aoo.descrizione)).containsIgnoreCase(searchJson.get("aoo").getAsString())));
			}
		}
		if(null != searchJson && !searchJson.isJsonNull() && searchJson.has("tipoProgressivo") && !searchJson.get("tipoProgressivo").isJsonNull()){
			predicate = predicate.and(QProgressivoAdozione.progressivoAdozione.tipoProgressivo.id.eq(searchJson.get("tipoProgressivo").getAsJsonObject().get("id").getAsLong()));
		}
		return progressivoAdozioneRepository.findAll(predicate , generatePageRequest);
	}
}
