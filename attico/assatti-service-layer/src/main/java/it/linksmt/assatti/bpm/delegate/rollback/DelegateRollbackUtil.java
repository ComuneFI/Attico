package it.linksmt.assatti.bpm.delegate.rollback;

import org.camunda.bpm.engine.delegate.DelegateExecution;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.bpm.util.AttoProcessVariables;

public class DelegateRollbackUtil {
	public static JsonObject setupRollbackInfo(DelegateExecution execution) {
		
		JsonObject rollbackInfo = new JsonObject();
		
		String strEn = (String)execution.getVariableLocal(AttoProcessVariables.ROLLBACK_ENABLED);
		Boolean rollbackEnabled = false;
		if(strEn!=null) {
			rollbackEnabled = Boolean.parseBoolean(strEn);
		}

		String rollbackInfoStr = (String)execution.getVariable(AttoProcessVariables.ROLLBACK_INFO);
		
		if(rollbackInfoStr!=null && !rollbackInfoStr.isEmpty()) {
			JsonParser parser = new JsonParser();
	  		rollbackInfo = parser.parse(rollbackInfoStr).getAsJsonObject();
		}
		
		rollbackInfo.addProperty("enabled", rollbackEnabled);
		
		return rollbackInfo;
	}
}
