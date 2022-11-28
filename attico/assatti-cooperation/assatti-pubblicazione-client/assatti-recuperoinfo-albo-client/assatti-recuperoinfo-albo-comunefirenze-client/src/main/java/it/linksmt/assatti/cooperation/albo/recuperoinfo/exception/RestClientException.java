package it.linksmt.assatti.cooperation.albo.recuperoinfo.exception;

public class RestClientException extends RecuperoInfoAlboException{

	private static final long serialVersionUID = 1L;
	
	public RestClientException() {
	}
	
	public RestClientException(Exception e) {
		super(e);
	}

	public RestClientException(String message) {
		super(message);
	}
}
