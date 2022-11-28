package it.linksmt.assatti.service.exception;

public class SmsCommunicationException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private String message;

	public SmsCommunicationException(int statusCode) {
		
		switch (statusCode) {
		case 0:
			this.message = "Operazione eseguita con successo.";
			break;
		case 3:
			this.message = "Errore generico nella chiamata. La casistica maggiore è legata agli errori di autenticazione.";
			break;
		case 111:
			this.message = "L'invio non può essere fatto se non si abilita l'autorizzazione all'overFranchigia";
			break;
		case 112:
			this.message = "L'elenco dei contatti liberi non è formalmente corretto";
			break;
		case 113:
			this.message = "L'elenco dei contatti liberi è vuoto o il parametro non è presente";
			break;
		case 114:
			this.message = "La lista dei contatti non esiste o il parametro non è presente o non valorizzato";
			break;
		case 115:
			this.message = "Il nome dell’invio non è corretto (troppo lungo o caratteri non supportati)";
			break;
		case 116:
			this.message = "Il parametro idInvio non è presente";
			break;
		case 120:
			this.message = "Il parametro Testo non è corretto (non presente, lungo 0 caratteri, troppo lungo o caratteri non supportati)";
			break;
		case 122:
			this.message = "L'elenco dei contatti liberi supera il numero massimo permesso.";
			break;
		case 123:
			this.message = "L'identificativo dell’invio non è presente.";
			break;
		case 501:
			this.message = "Il parametro data inizio o data fine non sono corretti.";
			break;
		case 999:
			this.message = "Errore di validazione campi in input.";
			break;

		default:
			this.message = "Errore generico nella chiamata.";
			break;
		}

		// 3: M2M-Errore-Generico
		
		//StatusCode getInvio:
		// 501: M2M-Errore-IntervalloDateFilterCollectionNOK
		
		//StatusCode getMsgMTFromInvioResponse:
		// 116: M2M-Errore-InvioIdNonPresente
		// 123: M2M-Errore-InvioIdNOK
		
		//StatusCode addNewIstantInvio: 
		// 111: M2M-Errore-InvioMSGInOverFranchigia
		// 112: M2M-Errore-InvioMSGContattiLiberiNOK
		// 113: M2M-Errore-InvioMSGContattiLiberiVuota
		// 114: M2M-Errore-InvioMSGListaNOK
		// 115: M2M-Errore-InvioMSGNomeNOK
		// 120: M2M-Errore-InvioMSGTestoNOK
		// 122: M2M-Errore-InvioMSGContattiLiberiMax
	}
	
	@Override
    public String getMessage(){
        return message;
    }
}
