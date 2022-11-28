package it.linksmt.assatti.bpm.delegate.rollback;

import javax.inject.Inject;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.service.JobTrasparenzaService;

/**
 * Service Task Camunda per la pubblicazione di un atto in trasparenza.
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class PubblicazioneTrasparenzaRollbackService implements JavaDelegate {

	private final Logger log = LoggerFactory.getLogger(PubblicazioneTrasparenzaRollbackService.class);

	@Inject
	private JobTrasparenzaService jobTrasparenzaService;

	@Inject
	private EventoService eventoService;

	
	@Override
	@Transactional
	public void execute(DelegateExecution execution) throws Exception {

		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		
		if(rollbackInfo.entrySet().size() > 1) {
	  		
			if(rollbackInfo.has("JobPubblicazioniTrasparenza")) {
				JsonArray pubblicazioni = rollbackInfo.get("JobPubblicazioniTrasparenza").getAsJsonArray();
				for(JsonElement je : pubblicazioni) {
					jobTrasparenzaService.delete(je.getAsLong());
					log.info("JobPubblicazioniTrasparenza " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("JobPubblicazioniTrasparenza");
			}
			
			if(rollbackInfo.has("eventiPubblicazioneTrasparenza")) {
				JsonArray eventi = rollbackInfo.get("eventiPubblicazioneTrasparenza").getAsJsonArray();
				for(JsonElement je : eventi) {
					eventoService.delete(je.getAsLong());
					log.info("eventiPubblicazioneTrasparenza " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("eventiPubblicazioneTrasparenza");
			}
			
			if(rollbackInfo.entrySet().size() <= 1) {
				execution.removeVariable(AttoProcessVariables.ROLLBACK_INFO);
			}else {
				execution.setVariable(AttoProcessVariables.ROLLBACK_INFO, rollbackInfo.toString());
			}
		}else {
			log.info("nothing to rollback");
			execution.removeVariable(AttoProcessVariables.ROLLBACK_INFO);
		}
	}
}
