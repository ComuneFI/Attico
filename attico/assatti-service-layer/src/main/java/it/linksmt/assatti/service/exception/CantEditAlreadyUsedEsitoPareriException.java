package it.linksmt.assatti.service.exception;

import it.linksmt.assatti.datalayer.domain.EsitoPareri;

/**
 * Eccezione da richiamare nel caso in cui un record non possa essere eliminato
 * perchè già utilizzato.
 * @author Antonio Magrì
 *
 */
public class CantEditAlreadyUsedEsitoPareriException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4663708539020869617L;
	private String message;

	public CantEditAlreadyUsedEsitoPareriException(EsitoPareri esitoPareri) {
		StringBuilder sb = new StringBuilder();
		sb.append("Tipo Atto e Soggetto non sono modificabili poiche' Esito Parere già utilizzato. ");
		this.message = sb.toString();
	}
	
	@Override
    public String getMessage(){
        return message;
    }
}
