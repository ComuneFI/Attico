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
import com.google.gson.JsonParser;

import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.service.AvanzamentoService;
import it.linksmt.assatti.service.EventoService;

@Service
public class RegistrazioneEventoRollbackService implements JavaDelegate {
	
	private final Logger log = LoggerFactory.getLogger(RegistrazioneEventoRollbackService.class);
	
	@Inject
	private AvanzamentoService avanzamentoService;
	
	@Inject
	private EventoService eventoService;
	

	@Override
	@Transactional
	public void execute(DelegateExecution execution) throws Exception {
		
		JsonObject rollbackInfo = DelegateRollbackUtil.setupRollbackInfo(execution);
		
		if(rollbackInfo.entrySet().size() > 1) {
	  		
			if(rollbackInfo.has("avanzamenti")) {
				JsonArray avanzamenti = rollbackInfo.get("avanzamenti").getAsJsonArray();
				for(JsonElement je : avanzamenti) {
					avanzamentoService.delete(je.getAsLong());
					log.info("avanzamento " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("avanzamenti");
			}
			
			if(rollbackInfo.has("eventi")) {
				JsonArray eventi = rollbackInfo.get("eventi").getAsJsonArray();
				for(JsonElement je : eventi) {
					eventoService.delete(je.getAsLong());
					log.info("evento " + je.getAsLong() + " rollbacked");
				}
				rollbackInfo.remove("eventi");
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
