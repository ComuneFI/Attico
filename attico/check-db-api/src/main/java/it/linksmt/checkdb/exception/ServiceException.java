package it.linksmt.checkdb.exception;


public class ServiceException extends BasicException {


	/**
	 * 
	 */
	private static final long serialVersionUID = 4150003727912816890L;

	public ServiceException(String code, String message, Throwable cause) {
		super(code, message, cause);
	}

	public ServiceException(String code, String message) {
		super(code, message);
	}

	public ServiceException(Throwable cause) {
		super(cause);
	}
	
	public ServiceException(String message) {
		super(message, message);
	}
	
}


