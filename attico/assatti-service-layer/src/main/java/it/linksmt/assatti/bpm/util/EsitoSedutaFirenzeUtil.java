package it.linksmt.assatti.bpm.util;

public class EsitoSedutaFirenzeUtil {
	private EsitoSedutaFirenzeUtil() {}
	
	public static String getStatoAtto(String codiceEsito) {
		String retVal = "";
		switch (codiceEsito) {
			case "approvata":
			case "approvata_del":
			case "G_approvata":
			case "G_approvata_del":
			case "G_approvata_dpc":
			case "approvato":
				retVal = "Atto Approvato";
				break;
			case "approvata_modifiche":
			case "approvata_modifiche_del":
			case "G_approvata_modifiche_dpc":
				retVal = "Approvato emendato";
				break;
			case "G_approvata_modifiche":
			case "G_approvata_modifiche_del":
				retVal = "Approvata con Modifiche";
				break;
			case "respinto":
			case "G_respinto":
				retVal = "Atto Respinto";
				break;
			case "respinto_emendato":
			case "G_respinto_emendato":
				retVal = "Atto Respinto Emendato";
				break;
			case "rinviato":
			case "G_rinviato":
				retVal = "Atto Rinviato";
				break;
			case "risposta_in_aula":
				retVal = "Risposta in Aula";
				break;
			case "ritirato":
				retVal = "Atto Ritirato";
				break;
			case "decaduta":
				retVal = "Decaduta";
				break;
			case "svolta":
				retVal = "Svolta";
				break;
			default:
				retVal = "";
				break;
		}
		return retVal;
	}
	
	
	public static String getCodiceEvento(String codiceEsito) {
		String retVal = "";
		switch (codiceEsito) {
			case "approvata":
			case "approvata_del":
			case "G_approvata":
			case "G_approvata_del":
			case "G_approvata_dpc":
			case "approvato":
				retVal = "APPROVAZIONE";
				break;
			case "approvata_modifiche":
			case "approvata_modifiche_del":
			case "G_approvata_modifiche":
			case "G_approvata_modifiche_del":
			case "G_approvata_modifiche_dpc":
				retVal = "APPROVAZIONE_MODIFICHE";
				break;
			case "respinto":
			case "G_respinto":
				retVal = "RESPINTO";
				break;
			case "respinto_emendato":
			case "G_respinto_emendato":
				retVal = "RESPINTO_EMENDATO";
				break;
			case "rinviato":
			case "G_rinviato":
				retVal = "RINVIO";
				break;
			case "risposta_in_aula":
				retVal = "RISPOSTA_IN_AULA";
				break;
			case "ritirato":
				retVal = "RITIRO";
				break;
			case "decaduta":
				retVal = "DECADUTA";
				break;
			case "svolta":
				retVal = "SVOLTA";
				break;
			default:
				retVal = "";
				break;
		}
		return retVal;
	}
}
