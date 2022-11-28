package it.linksmt.assatti.service.exception;

public class ServiceException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public ServiceException(Throwable t) {
		super(t);
	}

	public ServiceException(String message) {
		super(message);
	}

	public ServiceException(String message, Throwable t) {
		super(message, t);
	}

}
