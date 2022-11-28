package it.linksmt.assatti.dms.util;

public interface DmsComuneFirenzeConstants {

	// Prefissi
	String PREFIX_SEPARATOR = ":";
	
	String CONTENT_TYPE_PREFIX = "D:";
	String ASPECT_PREFIX = "P:";
	String RELATION_PREFIX = "R:";
		
	// Aspects
	String PUBBLICAZIONE_ASPECT = "atc:PubblicazioneAspect";
	String CONSERVAZIONE_SOSTITUITIVA_ASPECT = "atc:ConservazioneSostituitivaAspect";

	// Associations
	String ASSOCIAZIONE_ALLEGATI_ATTO = "atc:AssociazioneAllegatiAtto";
	
	// Valori Vincoli
	String VAL_PARERE_POSITIVO = "POSITIVO";
	
	// Attributi pubblicazione
	String FLAG_PUBBLICAZIONE = "atc:flagPubblicazione";
	String ANNO_PUBBLICAZIONE = "atc:annoPubblicazione";
	String DATA_INIZIO_PUBBLICAZIONE = "atc:dataInizioPubblicazione";
	String DATA_FINE_PUBBLICAZIONE = "atc:dataFinePubblicazione";
	String NUMERO_PUBBLICAZIONE = "atc:numeroPubblicazione";
	String REGISTRO_PUBBLICAZIONE = "atc:registroPubblicazione";
	String REGISTRO_PUBBLICAZIONE_AOL = "AOL";
	
	// Attributi conservazione
	String STATO_CONSERVAZIONE = "atc:statoInvioConservazione";
	String STATO_CONSERVAZIONE_DA_INVIARE_VAL = "DA_INVIARE";
	
	// Attributi AllegatoType
	String NOME_ALLEGATO = "atc:nomeAllegato";
	String TIPO_ALLEGATO = "atc:tipoAllegato";
	
	// Attributi "AttoType"
	String NUMERO_REGISTRAZIONE = "atc:numeroRegistrazione";
	String ANNO_REGISTRAZIONE = "atc:annoRegistrazione";
	String SIGLA_REGISTRO = "atc:siglaRegistro";
	String OGGETTO = "atc:oggetto";
	String DATA_RIFERIMENTO = "atc:dataRiferimento";
	String CLASSIFICA = "atc:classifica";
	String NUMERO_FASCICOLO = "atc:numeroFascicolo";
	String STRUTTURA_PROPONENTE = "atc:strutturaProponente";
	String DATA_ESECUTIVITA = "atc:dataEsecutivita";
	
	String FLAG_VISTO_REGOLARITA_CONTABILE = "atc:flagVistoRegolaritaContabile";
	String RESPONSABILE_VISTO_REGOLARITA_CONTABILE = "atc:responsabileVistoRegolaritaContabile";
	String DATA_VISTO_REGOLARITA_CONTABILE = "atc:dataVistoRegolaritaContabile";
	String TIPO_VISTO_REGOLARITA_CONTABILE = "atc:vistoRegolaritaContabile";
	
	String FLAG_VISTO_REGOLARITA_TECNICA = "atc:flagVistoRegolaritaTecnica";
	String RESPONSABILE_PARERE_REGOLARITA_TECNICA = "atc:responsabileParereRegolaritaTecnica";
	String DATA_PARERE_REGOLARITA_TECNICA = "atc:dataParereRegolaritaTecnica";
	String TIPO_VISTO_REGOLARITA_TECNICA = "atc:vistoRegolaritaTecnica";
	String AUTORE = "atc:autore";
	
	// Attributi "DeterminaType"
	String FIRMATARIO_DETERMINA = "atc:firmatarioDetermina";
	String RUOLO_FIRMATARIO_DETERMINA = "atc:ruoloFirmatarioDetermina";
	
	// Attributi "DeliberaType"
	String NUMERO_PROPOSTA = "atc:numeroProposta";
	String ANNO_PROPOSTA = "atc:annoProposta";
	String DATA_PROPOSTA = "atc:dataProposta";
	String ASSESSORATO_PROPONENTE = "atc:assessoratoProponente";
	String FLAG_INIZIATIVA_POPOLARE = "atc:flagIniziativaPopolare";
	String NUMERO_SEDUTA = "atc:numeroSeduta";
	
	// Attributi "OrdinanzaType"
	String FORMA = "atc:forma";
	String FIRMATARIO_ORDINANZA = "atc:firmatarioOrdinanza";
	String RUOLO_FIRMATARIO_ORDINANZA = "atc:ruoloFirmatarioOrdinanza";
	
	// Attributi "DecretoType"
	String FIRMATARIO_DECRETO = "atc:firmatarioDecreto";
	String RUOLO_FIRMATARIO_DECRETO = "atc:ruoloFirmatarioDecreto";
}
