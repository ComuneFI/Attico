package it.linksmt.assatti.fdr.exception;

public class FirmaRemotaException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private String errorCode;

	public FirmaRemotaException(String code, String message) {
		super(message);
		this.errorCode = code;
	}

	public FirmaRemotaException(String code, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = code;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}
}
