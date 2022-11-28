package it.linksmt.assatti.cooperation.service.contabilita;

/* 
 * TODO: classe utilizzata per mostrare un messaggio comprensibile all'utente
 *       in fase di invio dei dati in contabilita. Occorre gestire meglio le eccezioni
 */

public class ContabilitaServiceException extends RuntimeException {

	public ContabilitaServiceException(String message, Throwable cause) {
		super(message, cause);
	}

	public ContabilitaServiceException(String message) {
		super(message);
	}
}
