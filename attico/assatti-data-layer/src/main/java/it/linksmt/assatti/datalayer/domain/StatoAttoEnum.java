package it.linksmt.assatti.datalayer.domain;

import java.util.ArrayList;
import java.util.List;

public enum StatoAttoEnum {
	PUBBLICATO("Atto Pubblicato", "concluso"),
	PUBBLICAZIONE_TERMINATA("Pubblicazione terminata", "concluso"),
	ITER_TERMINATO("Iter Terminato", "concluso"),
	PUBBLICATO_NOTIFICATO("Atto Dirigenziale Pubblicato / Notificato", "concluso"),
	AD_ESECUTIVO("Atto Dirigenziale Esecutivo", "concluso"),
	AD_ANNULLATO_PRIMA_ADOZIONE("Proposta Annullata prima dell'Adozione", "concluso"),
	AD_ANNULLATO_DURANTE_FIRME("Proposta Annullata Durante le Firme", "concluso"),
	PROPOSTA_INESISTENTE("Proposta Inesistente", "concluso"),
	AD_ANNULLATO("Atto Dirigenziale Annullato", "concluso"),
	AD_RITIRATO("Atto Dirigenziale Ritirato", "concluso"),
	AD_DA_ANNULLARE_PC_NEGATIVO("Da Annullare per Parere Contabile Negativo", "itinere"),
	AD_DA_ANNULLARE_POC_NEGATIVO("Da Annullare per Parere Organo di Controllo Negativo", "itinere"),
	AD_DA_REVOCARE_RETTIFICARE_VC_NEGATIVA("Da Revocare/Rettificare per Verifica Contabile Negativa", "itinere"),
	AD_DA_REVOCARE_RETTIFICARE_VOC_NEGATIVA("Da Revocare/Rettificare per Verifica Organo di Controllo Negativa", "itinere"),
	AD_ESECUTIVO_CON_VERIFICA_CONTABILE("Atto Dirigenziale Esecutivo con Verifica Contabile Positiva", "concluso"),
	AD_ESECUTIVO_CON_VERIFICA_ORGANO_CONTROLLO("Atto Dirigenziale Esecutivo con Verifica Organo di Controllo Positiva", "concluso"),
//	REVOCATO("Atto Dirigenziale Revocato/Rettificato", "concluso"),
	IN_ATTESA_DI_REVOCA("Atto Dirigenziale in attesa di esecutività Revoca/Rettifica", "concluso"),
	AG_ESECUTIVO("Provvedimento Esecutivo", "concluso"),
	AG_VERBALIZZATO("Provvedimento Verbalizzato", "concluso"),
	AG_PRESO_D_ATTO("Provvedimento Preso D'atto", "concluso"),
	AG_ANNULLATO("Proposta Annullata", "concluso"),
	AG_RITIRATO("Proposta Ritirata", "concluso"),
	AG_DA_ANNULLARE_PC_NEGATIVO("Da Annullare per Parere Contabile Negativo", "itinere"),
	AG_DA_ANNULLARE_POC_NEGATIVO("Da Riproporre/Annullare Per Parere Amministrativo Negativo", "itinere"),
	REVOCATO("Atto Revocato", "concluso"),
	MODIFICATO_INTEGRATO("Atto Modificato-Integrato", "concluso");
	
	static public List<String> descrizioneConclusi(){
		List<String> conclusi = new ArrayList<String>();
		for(StatoAttoEnum stato : StatoAttoEnum.values()){
			if(stato.info.equalsIgnoreCase("concluso")){
				conclusi.add(stato.getDescrizione());
			}
		}
		return conclusi;
	}
	
	private String descrizione;
	private String info;
	
	private StatoAttoEnum(String descrizione, String info) {
		this.descrizione = descrizione;
		this.info = info;
	}
	
	public String getInfo() {
		return info;
	}

	public String getDescrizione() {
		return descrizione;
	}
}
