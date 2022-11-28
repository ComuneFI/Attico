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
import it.linksmt.assatti.service.JobPubblicazioneService;

/**
 * Service Task Camunda per la pubblicazione di un atto sull'albo pretorio.
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class PubblicazioneAlboRollbackService implements JavaDelegate {

	private final Logger log = LoggerFactory.getLogger(PubblicazioneAlboRollbackService.class);

	@Inject
	private JobPubblicazioneService jobPubblicazioneService;
	
	@Inject
	private EventoService eventoService;

	@Override
	@Transactional
	public void execute(DelegateExecution execution) throws Exception {

		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		
		if(rollbackInfo.entrySet().size() > 1) {
	  		
			if(rollbackInfo.has("JobPubblicazioniAlbo")) {
				JsonArray pubblicazioni = rollbackInfo.get("JobPubblicazioniAlbo").getAsJsonArray();
				for(JsonElement je : pubblicazioni) {
					jobPubblicazioneService.delete(je.getAsLong());
					log.info("JobPubblicazioniAlbo " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("JobPubblicazioniAlbo");
			}
			
			if(rollbackInfo.has("eventiPubblicazioneAlbo")) {
				JsonArray eventi = rollbackInfo.get("eventiPubblicazioneAlbo").getAsJsonArray();
				for(JsonElement je : eventi) {
					eventoService.delete(je.getAsLong());
					log.info("eventiPubblicazioneAlbo " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("eventiPubblicazioneAlbo");
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
