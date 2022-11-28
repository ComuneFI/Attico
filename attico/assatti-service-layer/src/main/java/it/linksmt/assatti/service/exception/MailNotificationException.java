package it.linksmt.assatti.service.exception;

/**
 * Eccezione da richiamare nel caso in cui per qualche motivo il sistema non riesca ad inviare un'email di notifica.
 *
 */
public class MailNotificationException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	
	public MailNotificationException(String message) {
		this.message = message;
	}
	
	@Override
    public String getMessage(){
        return message;
    }
}
