package it.linksmt.assatti.cmis.client.exception;

import java.util.ResourceBundle;

/**
 * Eccezione del proxy per alfresco gestita mediante codici di errore.
 * 
 * @author Marco Ingrosso
 *
 */
public class CmisServiceException extends Exception {

	private static final long serialVersionUID = 1L;

	private static final String BUNDLE_PATH = "errormessage.errormessage";
	
	private CmisServiceErrorCode errorCode;	// codice di errore che denota l'eccezione

	public CmisServiceException(CmisServiceErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public CmisServiceException(String errorCode) {
		super(errorCode);
	}

	/**
	 * Restituisce la descrizione associata al codice di errore.<BR />
	 * 
	 * Le descrizioni sono memorizzate in appositi file di properties
	 * e codificate nel seguente modo:
	 * <BR /><BR />
	 * 		< codice_di_errore >_< valore_enumeration >
	 * <BR /><BR />
	 * Esempio:	1001_NOT_AVAILABLE_EMAIL
	 * 
	 * @return
	 */
	public String getDescription() {
		if (errorCode == null) {
			return null;
		}
		String key = errorCode.getCode() + "_" + errorCode;
		ResourceBundle bundle = ResourceBundle.getBundle(BUNDLE_PATH);
		return bundle.getString(key);
	}

	/*
	 * GET and SET
	 */

	public CmisServiceErrorCode getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(CmisServiceErrorCode errorCode) {
		this.errorCode = errorCode;
	}

}
