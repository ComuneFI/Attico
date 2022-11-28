package it.linksmt.assatti.cmis.client.exception;


/**
 * Classe dei codici di errore del proxy Cmis.
 * 
 * @author marco ingrosso
 *
 */
public enum CmisServiceErrorCode {

	HOST_NOT_FOUND_ERROR(9001),
	MISSING_LOGIN_PARAMETERS_ERROR(9002),
	LOGIN_ERROR(9003),
	DOCUMENT_CONTENT_IO_EXCEPTION(9010),
	DOCUMENT_ALREADY_EXIST(9020),
	OBJECT_NOT_FOUND(9030),
	UNSUPPORTED_PATH_ENCODING(9040),
	UNABLE_TO_PERFORM_ACTION(9100);

	private final int number;

	private CmisServiceErrorCode(int number) {
		this.number = number;
	}

	public int getCode() {
		return number;
	}

}
