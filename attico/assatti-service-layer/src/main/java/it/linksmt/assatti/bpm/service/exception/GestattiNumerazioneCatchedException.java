package it.linksmt.assatti.bpm.service.exception;

public class GestattiNumerazioneCatchedException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String message;
	
	public GestattiNumerazioneCatchedException(String message) {
		this.message = message;
	}
	
	@Override
    public String getMessage(){
        return message;
    }
}
