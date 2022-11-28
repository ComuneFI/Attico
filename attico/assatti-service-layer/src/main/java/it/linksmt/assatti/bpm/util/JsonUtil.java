package it.linksmt.assatti.bpm.util;

import com.google.gson.JsonObject;

import it.linksmt.assatti.utility.StringUtil;

public class JsonUtil {
	public static boolean hasFilter(JsonObject criteriJson, String propertyName) {
		if ( (criteriJson == null) || StringUtil.isNull(propertyName)) {
			return false;
		}
		
		return criteriJson.has(propertyName) && (!criteriJson.get(propertyName).isJsonNull())
				&& (!StringUtil.isNull(criteriJson.get(propertyName).getAsString()));
	}
	
	public static boolean hasFilterArray(JsonObject criteriJson, String propertyName) {
		if ( (criteriJson == null) || StringUtil.isNull(propertyName)) {
			return false;
		}
		
		return criteriJson.has(propertyName) 
				&& !criteriJson.get(propertyName).isJsonNull()
				&& criteriJson.get(propertyName).isJsonArray()
				&& criteriJson.get(propertyName).getAsJsonArray().size() > 0;
	}
}
