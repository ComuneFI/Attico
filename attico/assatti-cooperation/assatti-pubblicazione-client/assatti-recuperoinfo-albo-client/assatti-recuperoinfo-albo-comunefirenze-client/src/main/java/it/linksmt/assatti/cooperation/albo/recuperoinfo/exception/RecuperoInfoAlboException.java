package it.linksmt.assatti.cooperation.albo.recuperoinfo.exception;

public class RecuperoInfoAlboException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public RecuperoInfoAlboException() {
	}
	
	public RecuperoInfoAlboException(Exception e) {
		super(e);
	}

	public RecuperoInfoAlboException(String message) {
		super(message);
	}
}
