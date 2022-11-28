package it.linksmt.assatti.cooperation.albo.recuperoinfo.exception;

public class GenerazionePathException extends RecuperoInfoAlboException{

	private static final long serialVersionUID = 1L;
	
	public GenerazionePathException() {
	}
	
	public GenerazionePathException(Exception e) {
		super(e);
	}

	public GenerazionePathException(String message) {
		super(message);
	}
}
