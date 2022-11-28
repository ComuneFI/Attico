package it.linksmt.assatti.service.exception;

import java.util.Date;
import java.util.UUID;

import org.apache.commons.lang.time.DateFormatUtils;
import org.hibernate.HibernateException;
import org.hibernate.JDBCException;
import org.hibernate.StaleObjectStateException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.orm.ObjectOptimisticLockingFailureException;

import it.linksmt.assatti.bpm.service.exception.GestattiNumerazioneCatchedException;
import it.linksmt.assatti.service.FileUploadException;


public class GestattiCatchedException extends RuntimeException {
	
	private final Logger log = LoggerFactory
			.getLogger(GestattiCatchedException.class);
	
	public static final String NEW_LINE = "\u003Cbr\u003E";
	
	private static final long serialVersionUID = -6333026010167334365L;
	
	public GestattiCatchedException(String message) {
        super(DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss") + " - " + message +NEW_LINE+"(ERR: " + UUID.randomUUID().toString().split("-")[0] + ")");
        log.error("Si è verificato un errore applicativo: " + message, this);
    }
	
	public GestattiCatchedException(Exception ex, String message) throws GestattiCatchedException {
		if (ex instanceof GestattiCatchedException) {
			throw (GestattiCatchedException)ex;
		}
		Throwable cause = ExceptionUtil.getLastExceptionCause(ex);
		
		if(ex instanceof ObjectOptimisticLockingFailureException || ex instanceof StaleObjectStateException || cause instanceof ObjectOptimisticLockingFailureException || cause instanceof StaleObjectStateException){
			message += "Per poter procedere \u00E8 necessario ricaricare la pagina ed effettuare nuovamente l'operazione";
		}
		
		throw new GestattiCatchedException(message);
	}
	
	public GestattiCatchedException(Exception ex) throws GestattiCatchedException {
		
		if (ex instanceof GestattiCatchedException) {
			throw (GestattiCatchedException)ex;
		}
		
		Throwable cause = ExceptionUtil.getLastExceptionCause(ex);
		if(cause!=null) {
			cause.printStackTrace();
		}
		throw new GestattiCatchedException(this.getExceptionDetail(ex, cause));
    }
	
	private String getExceptionDetail(Exception ex, Throwable cause) {
		String data = DateFormatUtils.format(new Date(), "dd/MM/yyyy HH:mm:ss");
		
		if(ex instanceof ObjectOptimisticLockingFailureException || ex instanceof StaleObjectStateException || cause instanceof ObjectOptimisticLockingFailureException || cause instanceof StaleObjectStateException){
			data += "Per poter procedere \u00E8 necessario ricaricare la pagina ed effettuare nuovamente l'operazione";
/*       			" - Il sistema ha rilevato che il contenuto che si sta tentando di salvare \u00E8 stato modificato contestualmente in un'altra sessione." + NEW_LINE + 
        			"Le modifiche apportate non sono quindi applicabili poich\u00E8 non tengono conto dell'ultima versione dei dati presenti nel sistema." + NEW_LINE + 
        			;
*/
		}else 
		if(ex instanceof HibernateException || ex instanceof JDBCException){
			data = (data + " - Si \u00E8 verificato un problema di scrittura o lettura sulla base di dati."+NEW_LINE+"Non \u00E8 stato possibile effettuare l'operazione richiesta."+NEW_LINE+"Si prega di riprovare.");// + this.exceptionDetail(ex));
		}else
		if(ex instanceof CantDeleteAlreadyUsedObjectException){
			data = (data + " - " + ex.getMessage());// + this.exceptionDetail(ex));
        }
		if(ex instanceof SmsCommunicationException){
			data = (data + " - " + ex.getMessage());
        }else
    	if(ex instanceof ProgressivoException){
    		data = (data + " - " + ex.getMessage());
        }else
        if(ex instanceof GestattiNumerazioneCatchedException){
        	data = (data + " - " + ex.getMessage());
        }else
        if(ex instanceof DmsEmptyFileException || ex instanceof EmptyFileException) {
        	String detail = "";
        	if(ex.getMessage()!=null && !ex.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + ex.getMessage();
        	}else if(cause!=null && cause.getMessage()!=null && !cause.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + cause.getMessage();
        	}
        	data = (data + " - " + "Si \u00E8 verificato un errore nella lettura o scrittura del file il quale potrebbe non essere valido." + detail + NEW_LINE + "Si prega di verificare la correttezza del file e riprovare");
        }else
        if(ex instanceof DmsException) {
        	String detail = "";
        	if(ex.getMessage()!=null && !ex.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + ex.getMessage();
        	}else if(cause!=null && cause.getMessage()!=null && !cause.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + cause.getMessage();
        	}
        	
        	data = (data + " - " + "Si \u00E8 verificato un errore nella lettura o scrittura del file." + detail + NEW_LINE + "Si prega di riprovare");
        }else
        if(ex instanceof FileUploadException) {
        	String detail = "";
        	if(cause!=null && (cause instanceof DmsEmptyFileException || cause instanceof EmptyFileException)) {
        		detail = NEW_LINE + "Il file che si sta tentando di caricarire potrebbe essere vuoto o non valido.";
        	}else if(ex.getMessage()!=null && !ex.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + ex.getMessage();
        	}else if(cause!=null && cause.getMessage()!=null && !cause.getMessage().trim().isEmpty()) {
        		detail = NEW_LINE + cause.getMessage();
        	}
        	data = (data + " - " + "Si \u00E8 verificato un errore nel caricamento del file." + detail + NEW_LINE + "Si prega di riprovare");
        }
		else{
			String message = data + " - Non \u00E8 stato possibile effettuare l'operazione richiesta"+NEW_LINE+"Si prega di riprovare.";
			if(ex.getMessage()!=null && !ex.getMessage().trim().isEmpty()) {
				message += NEW_LINE + "Info: " + (ex.getMessage().trim().length() < 100 ? ex.getMessage().trim() : ex.getMessage().trim().subSequence(0, 99));
			}
			data = (message);// + this.exceptionDetail(ex));
        }
		return data;
	}
}
