package it.linksmt.checkdb.exception;



public class BasicException extends Exception {

	private static final long serialVersionUID = -3433471883871414707L;
	/**
     *
     */

    private final String code;
    
    
	public String getCode() {
		return code;
	}
	public BasicException(String code,String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}
	
	public BasicException(String code, String message) {
		super(message);
		this.code = code;
	}
	
	public BasicException(Throwable cause) {
		super(cause);
		this.code = null;
	}
	
}

