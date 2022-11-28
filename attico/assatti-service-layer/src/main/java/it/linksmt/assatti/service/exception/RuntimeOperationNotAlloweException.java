package it.linksmt.assatti.service.exception;

public class RuntimeOperationNotAlloweException extends GestattiCatchedException {
	
	private static final long serialVersionUID = 1L;

	public RuntimeOperationNotAlloweException() {
        super("Attenzione il sistema ha rilevato di avere in corso la medesima operazione di cui si \u00E8 fatto richiesta.\u003Cbr\u003ESi prega di attendere alcuni minuti, poi ricaricare i risultati tramite il pulsante Cerca e riprovare.");
    }
	
	public RuntimeOperationNotAlloweException(String message) {
        super(message);
    }
	
	public RuntimeOperationNotAlloweException(Exception ex, String message) throws RuntimeOperationNotAlloweException {
		super(ex, message);
	}
	
	public RuntimeOperationNotAlloweException(Exception ex) throws RuntimeOperationNotAlloweException {
		super(ex);
    }

}
