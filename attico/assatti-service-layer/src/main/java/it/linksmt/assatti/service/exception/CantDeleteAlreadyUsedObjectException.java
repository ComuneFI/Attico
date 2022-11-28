package it.linksmt.assatti.service.exception;

import java.util.List;

import it.linksmt.assatti.datalayer.domain.Dato;
import it.linksmt.assatti.datalayer.domain.Scheda;

/**
 * Eccezione da richiamare nel caso in cui un record non possa essere eliminato
 * perchè già utilizzato da un'altra tabella, che lo referenzia.
 * @author Davide Pastore
 *
 */
public class CantDeleteAlreadyUsedObjectException extends Exception {
	
	private String message;

	public CantDeleteAlreadyUsedObjectException(Dato dato, List<Scheda> schedas) {
		StringBuilder sb = new StringBuilder();
		sb.append("ATTENZIONE: Impossibile eliminare il Dato <b>" + dato.getEtichetta() + "</b> poichè già utilizzato nelle schede: <ul>");
		for(int i = 0; i < schedas.size(); i++){
			Scheda scheda = schedas.get(i);
			sb.append("<li>" + scheda.getEtichetta());
			if(i + 1 != schedas.size()){
				sb.append(";");
			} else {
				sb.append(".");
			}
			sb.append("</li>");
		}
		sb.append("</ul>");
		this.message = sb.toString();
	}
	
	@Override
    public String getMessage(){
        return message;
    }
}
