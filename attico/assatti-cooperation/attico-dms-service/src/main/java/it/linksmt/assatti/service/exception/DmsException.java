package it.linksmt.assatti.service.exception;

public class DmsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DmsException() {

	}
	
	public DmsException(Throwable t) {
		super(t);
	}

	public DmsException(String message) {
		super(message);
	}

}
