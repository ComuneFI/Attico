package it.linksmt.assatti.datalayer.domain;

import java.util.ArrayList;
import java.util.List;

public enum UtenteStato {
	REGISTRATO, ATTIVO, CANCELLATO, DISABILITATO;
	
	static public List<UtenteStato> searchInStato(String query){
		List<UtenteStato> stati = new ArrayList<UtenteStato>();
		for(UtenteStato stato : UtenteStato.values()){
			if(stato.toString().toLowerCase().contains(query.toLowerCase())){
				stati.add(stato);
			}
		}		
		return stati;
	}
}
