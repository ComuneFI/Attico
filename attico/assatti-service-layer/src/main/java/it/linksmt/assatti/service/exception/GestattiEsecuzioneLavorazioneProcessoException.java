package it.linksmt.assatti.service.exception;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.StringUtil;;

public class GestattiEsecuzioneLavorazioneProcessoException extends GestattiCatchedException{
	private static final long serialVersionUID = -63323410167334365L;
		
	public GestattiEsecuzioneLavorazioneProcessoException(Exception ex) throws GestattiCatchedException {
		super(ex);
	}
	public GestattiEsecuzioneLavorazioneProcessoException(Exception ex, String pulsante) throws GestattiCatchedException {
        super(ex, "Ci scusiamo per il disagio." + StringUtil.USER_MESSAGE_NEW_LINE
        	  +"Il sistema ha riscontrato un errore in fase di esecuzione di " + pulsante + "." + StringUtil.USER_MESSAGE_NEW_LINE
        	  +"Si prega di effettuare un nuovo tentativo.");
    }
}
