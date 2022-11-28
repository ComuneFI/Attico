package it.linksmt.assatti.gestatti.web.rest.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CamundaUtils {

	private final Logger log = LoggerFactory.getLogger(CamundaUtils.class);

	public static Long getProfiloIdFromCamundaUsername(String camundaUsername){
		
		/*
		 * TODO: implementazione errata, non permette numeri nello username
		 * 
		int index = 0;
		for(char c : camundaUsername.toCharArray()){
			if(Character.isDigit(c)){
				break;
			}else{
				index++;
			}
		}
		return Long.parseLong(camundaUsername.substring(index));
		*/
		return new Long(-1);
	}
}
