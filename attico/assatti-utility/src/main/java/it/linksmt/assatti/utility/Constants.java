package it.linksmt.assatti.utility;

/**
 * Application constants.
 */
public interface Constants {
   String SYSTEM_ACCOUNT = "system";
   
   String PACKAGE_ASSATTI_BASE = "it.linksmt.assatti";
   String PACKAGE_ASSATTI_DOMAIN = PACKAGE_ASSATTI_BASE + ".datalayer.domain";
   String PACKAGE_ASSATTI_REPOSITORY = PACKAGE_ASSATTI_BASE + ".datalayer.repository";
   
   String BPM_USERNAME_SEPARATOR = "_!U#_";
   String BPM_INCARICO_SEPARATOR = "_!I#_";
   String BPM_ROLE_SEPARATOR = "_!R#_";
   String DELEGA_INFO_SEPARATOR = "#D#";
   
//   String LDAP_ENABLED = "ldap.enabled";
   	String SECTION_TAB_UPLOAD_DOCUMENTO_FIRMATO="tab.upload.documento.firmato.visibility";
    String SECTION_PUBBLICAZIONE_DIRITTO_OBLIO_VISIBILITY="pubblicazione.dirittooblio.visibility";
	String SECTION_DATIIDENTIFICATIVI_VISIBILITY = "section.datiIdentificativi.visibility";
	String SECTION_ASSEGNAZIONEINCARICHI_VISIBILITY = "section.assegnazioneIncarichi.visibility";
	String SECTION_RISPOSTA_VISIBILITY = "section.risposta.visibility";
	String SECTION_SOTTOSCRIZIONI_VISIBILITY = "section.sottoscrizioni.visibility";
	String SECTION_RIFERIMENTINORMATIVI_VISIBILITY = "section.riferimentiNormativi.visibility";
	String SECTION_TESTO_VISIBILITY = "section.testo.visibility";
	String SECTION_PREAMBOLO_VISIBILITY = "section.preambolo.visibility";
	String SECTION_MOTIVAZIONE_VISIBILITY = "section.motivazione.visibility";
	String SECTION_GARANZIERISERVATEZZA_VISIBILITY = "section.garanzieRiservatezza.visibility";
	String SECTION_SCHEDE_VISIBILITY = "section.schede.visibility";
	String SECTION_DICHIARAZIONI_VISIBILITY = "section.dichiarazioni.visibility";
	String SECTION_DISPOSITIVO_VISIBILITY = "section.dispositivo.visibility";
	String SECTION_DOMANDA_VISIBILITY = "section.domanda.visibility";
	String SECTION_ALLEGATI_VISIBILITY = "section.allegati.visibility";
	String SECTION_TRASPARENZA_VISIBILITY = "section.trasparenza.visibility";
	String SECTION_NOTIFICA_VISIBILITY = "section.notifica.visibility";
	String SECTION_ARCHIVIO_VISIBILITY = "section.archivio.visibility";
	String SECTION_SISTEMIREGIONALI_VISIBILITY = "section.sistemiRegionali.visibility";
	String SECTION_NOTE_VISIBILITY = "section.note.visibility";
	String SECTION_DOCUMENTIPDF_VISIBILITY = "section.documentiPdf.visibility";
	String SECTION_PARERE_VISIBILITY = "section.parere.visibility";
	String SECTION_DIVULGAZIONE_VISIBILITY = "section.divulgazione.visibility";
	String SECTION_DETTAGLIATTO_VISIBILITY = "section.dettagliAtto.visibility";
	String SECTION_LISTATASK_VISIBILITY = "section.listaTask.visibility";
	String SECTION_EVENTIATTO_VISIBILITY = "section.eventiAtto.visibility";
	String SECTION_SEDUTEATTO_VISIBILITY = "section.seduteAtto.visibility";
	String SECTION_DATIIDENTIFICATIVICONSIGLIO_VISIBILITY = "section.datiIdentificativiConsiglio.visibility";
	String SECTION_DIVULGAZIONESEMPLIFICATA_VISIBILITY = "section.divulgazioneSemplificata.visibility";
	String SECTION_DOCUMENTIPDFCONSIGLIO_VISIBILITY = "section.documentiPdfConsiglio.visibility";
	String SECTION_DATIIDENTIFICATIVIVERBALE_VISIBILITY = "section.datiIdentificativiVerbale.visibility";
	
	String COMPONENT_GESTIONEREGISTRAZIONEUTENTE_VISIBILITY = "component.gestioneRegistrazioneUtente.visibility";
	String COMPONENT_NOTIFICHE_POPUP_VISIBILITY = "component.notifiche.popup.visibility";
	
	String NAVBAR_CONTATTI_VISIBILITY = "navbar.contatti.visibility";
	String NAVBAR_FAQ_VISIBILITY = "navbar.faq.visibility";
	String NAVBAR_LINK_VISIBILITY = "navbar.link.visibility";
	String NAVBAR_MANUALE_VISIBILITY = "navbar.manuale.visibility";
	String NAVBAR_MANUALE_URL_VISIBILITY = "navbar.manuale.url.visibility";
   
   String MENU_MONITORAGGIOPUBBLICAZIONI_VISIBILITY = "menu.monitoraggiopubblicazioni.visibility";
   String MENU_MONITORAGGIO_REPORTDOCUMENTALE_VISIBILITY = "menu.monitoraggio.reportdocumentale.visibility";
   String MENU_RICERCA_STORICA_VISIBILITY = "menu.ricerca.storica.visibility";
   String MENU_RICERCA_LIBERA_VISIBILITY = "menu.ricerca.libera.visibility";
   
   String MENU_ANAGRAFICHE_SISTEMAACCREDITATO_VISIBILITY = "menu.anagrafiche.sistemaAccreditato.visibility";
   String MENU_ANAGRAFICHE_TIPOADEMPIMENTO_VISIBILITY = "menu.anagrafiche.tipoAdempimento.visibility";
   String MENU_ANAGRAFICHE_MATERIADL33_VISIBILITY = "menu.anagrafiche.materiaDl33.visibility";
   String MENU_ANAGRAFICHE_AMBITODL33_VISIBILITY = "menu.anagrafiche.ambitoDl33.visibility";
   String MENU_ANAGRAFICHE_CLASSIFICAZIONE_VISIBILITY = "menu.anagrafiche.classificazione.visibility";
   String MENU_MONITORAGGIO_REPORTSMS_VISIBILITY = "menu.monitoraggio.reportSms.visibility";
   String MENU_MONITORAGGIO_SCHEDULERMANAGEMENT_VISIBILITY = "menu.monitoraggio.schedulerManagement.visibility";
   String MENU_MONITORAGGIO_METRICHE_VISIBILITY = "menu.monitoraggio.metriche.visibility";
   String MENU_MONITORAGGIO_ACCESSI_VISIBILITY = "menu.monitoraggio.accessi.visibility";
   String MENU_MONITORAGGIO_LOG_VISIBILITY = "menu.monitoraggio.log.visibility";
   String MENU_MONITORAGGIO_API_VISIBILITY = "menu.monitoraggio.api.visibility";
   String MENU_MONITORAGGIO_REPORTNOTIFICHE_VISIBILITY = "menu.monitoraggio.reportnotifiche.visibility";
   
   String MENU_HELPDESK_VISIBILITY = "menu.helpdesk.visibility";
   String MENU_MESSAGGI_VISIBILITY = "menu.messaggi.visibility";
   String MENU_GESTIONEHELPDESK_VISIBILITY = "menu.gestioneHelpdesk.visibility";
   String MENU_GESTIONEHELPDESK_MESSAGGIO_VISIBILITY =  "menu.gestioneHelpdesk.messaggio.visibility";
   String MENU_GESTIONEHELPDESK_CATEGORIAMESSAGGIO_VISIBILITY = "menu.gestioneHelpdesk.categoriamessaggio.visibility";
   String MENU_GESTIONEHELPDESK_MESSAGGIOISTANTANEO_VISIBILITY = "menu.gestioneHelpdesk.messaggioistantaneo.visibility";
   String MENU_GESTIONEHELPDESK_RICHIESTEHD_VISIBILITY = "menu.gestioneHelpdesk.richiestehd.visibility";
   String MENU_GESTIONEHELPDESK_GESTIONERICHIESTEHD_VISIBILITY = "menu.gestioneHelpdesk.gestionerichiestehd.visibility";
   String MENU_GESTIONEHELPDESK_STATORICHIESTAHD_VISIBILITY = "menu.gestioneHelpdesk.statorichiestahd.visibility";
   String MENU_GESTIONEHELPDESK_TIPORICHIESTAHD_VISIBILITY = "menu.gestioneHelpdesk.tiporichiestahd.visibility";
   String MENU_GESTIONEHELPDESK_FAQ_VISIBILITY =  "menu.gestioneHelpdesk.faq.visibility";
   String MENU_GESTIONEHELPDESK_CATEGORIAFAQ_VISIBILITY =    "menu.gestioneHelpdesk.categoriafaq.visibility";
   
   
   
   String MENU_ORGANIGRAMMA_SERVIZIO_VISIBILITY = "menu.organigramma.servizio.visibility";
   String MENU_ORGANIGRAMMA_ASSESSORATO_VISIBILITY = "menu.organigramma.assessorato.visibility";
   String MENU_ANAGRAFICHE_TIPOODG_VISIBILITY = "menu.anagrafiche.tipoodg.visibility";
   String MENU_ANAGRAFICHE_TIPODETERMINAZIONE_VISIBILITY = "menu.anagrafiche.tipodeterminazione.visibility";
   String MENU_ANAGRAFICHE_RUBRICADESTINATARIOESTERNO_VISIBILITY = "menu.anagrafiche.rubricadestinatarioesterno.visibility";
   String MENU_ANAGRAFICHE_RUBRICABENEFICIARIO_VISIBILITY = "menu.anagrafiche.rubricabeneficiario.visibility";
   String MENU_PREDISPOSIZIONEATTO_CLONA_VISIBILITY = "menu.predisposizioneatto.clona.visibility";
   String MENU_RICHIESTEDIRIGENTE_VISIBILITY = "menu.richiestedirigente.visibility";
   
   String WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_PROFILO_ID = "task.tipoconfigurazione.profilo.id";
   String WEB_APPLICATION_TRASPARENZA_BASE_URL = "componente.trasparenza.baseurl";
   String WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_PROFILO_DESCRIZIONE = "task.tipoconfigurazione.profilo.descrizione";
   String WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_UFFICIO_ID = "task.tipoconfigurazione.ufficio.id";
   String WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_UFFICIO_DESCRIZIONE = "task.tipoconfigurazione.ufficio.descrizione";
   String WEB_APPLICATION_ENABLE_SIGNED_FILES_UPLOAD = "enable.signed.files.upload";
   String WEB_APPLICATION_AVANZAMENTO_WARNING_TYPES = "avanzamento.warning.types";
   
   String WEB_APPLICATION_PROPOSTA_COMPLETO_FRONTESPIZIO_TIPOATTO_MODELLO = "pcf.tipoatto.modello";
   
   String GLOBAL_ENV = "global.env";
   String GLOBAL_VERSION = "global.version";
   String GLOBAL_URL_CONVERT = "global.urlConvert";
   String GLOBAL_SITE_TITLE = "global.siteTitle";
   String GLOBAL_SITE_CSS = "global.siteCss";
   
}
