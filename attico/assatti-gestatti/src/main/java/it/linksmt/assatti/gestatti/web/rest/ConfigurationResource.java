package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import javax.servlet.MultipartConfigElement;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;

import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.configuration.DataSourceProps;
import it.linksmt.assatti.utility.configuration.GlobalProps;
import it.linksmt.assatti.utility.configuration.SectionsVisibilityProps;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller for configurations parameters.
 */
@RestController
@RequestMapping("/api")
@PropertySource("classpath:build.properties")
public class ConfigurationResource {
	
	@Autowired
	private MultipartConfigElement multipartConfig;

	private final Logger log = LoggerFactory.getLogger(ConfigurationResource.class);

	@Value("${build.date}")
	private String buildTS;
	
	/**
	 * GET /configurations
	 */
	@RequestMapping(value = "/configurations", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<String> getConfigurations(HttpServletRequest request, HttpServletResponse response) throws GestattiCatchedException {
		log.debug("ConfigurationResource :: getConfigurations()");
		try {

			Boolean userManageEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_USER_MANAGE_ENABLED))) {
				userManageEnabled = Boolean.TRUE;
			}
			String loginUrl = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGIN_URL);
			String logoutUrl = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGOUT_URL);
			String changeUserUrl = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_CHANGEUSER_URL);

			String loginSsoIdParameterName = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGIN_SSO_ID_PARAMETER_NAME);
			String logoutSsoIdParameterName = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGOUT_SSO_ID_PARAMETER_NAME);
			if (logoutSsoIdParameterName != null && !logoutSsoIdParameterName.isEmpty()) {
				Object ssoIdParameterNameAttribute = request.getSession().getAttribute(loginSsoIdParameterName);

				String parameterSeparator = "";
				if (logoutUrl.contains("?")) {
					parameterSeparator = "&";
				}
				else {
					parameterSeparator = "?";
				}

				logoutUrl += parameterSeparator + logoutSsoIdParameterName + "=" + ssoIdParameterNameAttribute;
			}

			Boolean logoutUrlEnabled = Boolean.FALSE;
			if (logoutUrl != null && !logoutUrl.isEmpty()) {
				logoutUrlEnabled = Boolean.TRUE;
			}
			
			Boolean uploadDocFirmatiVisibility = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_TAB_UPLOAD_DOCUMENTO_FIRMATO, "false"))) {
				uploadDocFirmatiVisibility = Boolean.TRUE;
			}
			
			Boolean sectionPubblicazioneDirittoOblioVisibility = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_PUBBLICAZIONE_DIRITTO_OBLIO_VISIBILITY, "false"))) {
				sectionPubblicazioneDirittoOblioVisibility = Boolean.TRUE;
			}
			Boolean sectionDatiIdentificativiEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVI_VISIBILITY))) {
				sectionDatiIdentificativiEnabled = Boolean.TRUE;
			}
			Boolean sectionAssegnazioneIncarichiEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_ASSEGNAZIONEINCARICHI_VISIBILITY))) {
				sectionAssegnazioneIncarichiEnabled = Boolean.TRUE;
			}
			Boolean sectionRispostaEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_RISPOSTA_VISIBILITY))) {
				sectionRispostaEnabled = Boolean.TRUE;
			}
			Boolean sectionSottoscrizioniEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SOTTOSCRIZIONI_VISIBILITY))) {
				sectionSottoscrizioniEnabled = Boolean.TRUE;
			}
			Boolean sectionRiferimentiNormativiEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_RIFERIMENTINORMATIVI_VISIBILITY))) {
				sectionRiferimentiNormativiEnabled = Boolean.TRUE;
			}
			Boolean sectionPreamboloEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_PREAMBOLO_VISIBILITY))) {
				sectionPreamboloEnabled = Boolean.TRUE;
			}
			Boolean sectionTestoEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_TESTO_VISIBILITY))) {
				sectionTestoEnabled = Boolean.TRUE;
			}
			Boolean sectionMotivazioneEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_MOTIVAZIONE_VISIBILITY))) {
				sectionMotivazioneEnabled = Boolean.TRUE;
			}
			Boolean sectionGaranzieRiservatezzaEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_GARANZIERISERVATEZZA_VISIBILITY))) {
				sectionGaranzieRiservatezzaEnabled = Boolean.TRUE;
			}
			Boolean sectionSchedeEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SCHEDE_VISIBILITY))) {
				sectionSchedeEnabled = Boolean.TRUE;
			}
			Boolean sectionDichiarazioniEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DICHIARAZIONI_VISIBILITY))) {
				sectionDichiarazioniEnabled = Boolean.TRUE;
			}
			Boolean sectionDispositivoEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DISPOSITIVO_VISIBILITY))) {
				sectionDispositivoEnabled = Boolean.TRUE;
			}
			Boolean sectionDomandaEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DOMANDA_VISIBILITY))) {
				sectionDomandaEnabled = Boolean.TRUE;
			}
			Boolean sectionAllegatiEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_ALLEGATI_VISIBILITY))) {
				sectionAllegatiEnabled = Boolean.TRUE;
			}
			Boolean sectionTrasparenzaEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_TRASPARENZA_VISIBILITY))) {
				sectionTrasparenzaEnabled = Boolean.TRUE;
			}
			Boolean sectionNotificaEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_NOTIFICA_VISIBILITY))) {
				sectionNotificaEnabled = Boolean.TRUE;
			}
			Boolean sectionArchivioEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_ARCHIVIO_VISIBILITY))) {
				sectionArchivioEnabled = Boolean.TRUE;
			}
			Boolean sectionSistemiRegionaliEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SISTEMIREGIONALI_VISIBILITY))) {
				sectionSistemiRegionaliEnabled = Boolean.TRUE;
			}
			Boolean sectionNoteEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_NOTE_VISIBILITY))) {
				sectionNoteEnabled = Boolean.TRUE;
			}
			Boolean sectionDocumentiPdfEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DOCUMENTIPDF_VISIBILITY))) {
				sectionDocumentiPdfEnabled = Boolean.TRUE;
			}
			Boolean sectionParereEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_PARERE_VISIBILITY))) {
				sectionParereEnabled = Boolean.TRUE;
			}
			Boolean sectionDivulgazioneEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DIVULGAZIONE_VISIBILITY))) {
				sectionDivulgazioneEnabled = Boolean.TRUE;
			}
			Boolean sectionDettagliAttoEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DETTAGLIATTO_VISIBILITY))) {
				sectionDettagliAttoEnabled = Boolean.TRUE;
			}
			Boolean sectionListaTaskEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_LISTATASK_VISIBILITY))) {
				sectionListaTaskEnabled = Boolean.TRUE;
			}
			Boolean sectionEventiAttoEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_EVENTIATTO_VISIBILITY))) {
				sectionEventiAttoEnabled = Boolean.TRUE;
			}
			Boolean sectionSeduteAttoEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SEDUTEATTO_VISIBILITY))) {
				sectionSeduteAttoEnabled = Boolean.TRUE;
			}
			Boolean sectionDatiIdentificativiConsiglioEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVICONSIGLIO_VISIBILITY))) {
				sectionDatiIdentificativiConsiglioEnabled = Boolean.TRUE;
			}
			Boolean sectionDivulgazioneSemplificataEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DIVULGAZIONESEMPLIFICATA_VISIBILITY))) {
				sectionDivulgazioneSemplificataEnabled = Boolean.TRUE;
			}
			Boolean sectionDocumentiPdfConsiglioEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DOCUMENTIPDFCONSIGLIO_VISIBILITY))) {
				sectionDocumentiPdfConsiglioEnabled = Boolean.TRUE;
			}
			Boolean sectionDatiIdentificativiVerbaleEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVIVERBALE_VISIBILITY))) {
				sectionDatiIdentificativiVerbaleEnabled = Boolean.TRUE;
			}
			
			Boolean componentGestioneRegistrazioneUtenteEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.COMPONENT_GESTIONEREGISTRAZIONEUTENTE_VISIBILITY))) {
				componentGestioneRegistrazioneUtenteEnabled = Boolean.FALSE;
			}
			Boolean componentNotifichePopupEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.COMPONENT_NOTIFICHE_POPUP_VISIBILITY))) {
				componentNotifichePopupEnabled = Boolean.FALSE;
			}

			Boolean navbarContattiEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.NAVBAR_CONTATTI_VISIBILITY))) {
				navbarContattiEnabled = Boolean.FALSE;
			}

			Boolean navbarFaqEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.NAVBAR_FAQ_VISIBILITY))) {
				navbarFaqEnabled = Boolean.FALSE;
			}

			Boolean navbarLinkEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.NAVBAR_LINK_VISIBILITY))) {
				navbarLinkEnabled = Boolean.FALSE;
			}

			Boolean navbarManualeEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.NAVBAR_MANUALE_VISIBILITY))) {
				navbarManualeEnabled = Boolean.FALSE;
			}

			Boolean navbarManualeUrlEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.NAVBAR_MANUALE_URL_VISIBILITY))) {
				navbarManualeUrlEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggiopubblicazioniEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIOPUBBLICAZIONI_VISIBILITY))) {
				menuMonitoraggiopubblicazioniEnabled = Boolean.FALSE;
			}
			Boolean menuMonitoraggioReportdocumentaleEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_REPORTDOCUMENTALE_VISIBILITY))) {
				menuMonitoraggioReportdocumentaleEnabled = Boolean.FALSE;
			}
			Boolean menuRicercaStoricaEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_RICERCA_STORICA_VISIBILITY))) {
				menuRicercaStoricaEnabled = Boolean.FALSE;
			}

			Boolean menuRicercaLiberaEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_RICERCA_LIBERA_VISIBILITY))) {
				menuRicercaLiberaEnabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheSistemaAccreditatoEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_SISTEMAACCREDITATO_VISIBILITY))) {
				menuAnagraficheSistemaAccreditatoEnabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheTipoAdempimentoEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_TIPOADEMPIMENTO_VISIBILITY))) {
				menuAnagraficheTipoAdempimentoEnabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheMateriaDl33Enabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_MATERIADL33_VISIBILITY))) {
				menuAnagraficheMateriaDl33Enabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheAmbitoDl33Enabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_AMBITODL33_VISIBILITY))) {
				menuAnagraficheAmbitoDl33Enabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheClassificazioneEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_CLASSIFICAZIONE_VISIBILITY))) {
				menuAnagraficheClassificazioneEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioReportSmsEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_REPORTSMS_VISIBILITY))) {
				menuMonitoraggioReportSmsEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioSchedulerManagementEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_SCHEDULERMANAGEMENT_VISIBILITY))) {
				menuMonitoraggioSchedulerManagementEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioMetricheEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_METRICHE_VISIBILITY))) {
				menuMonitoraggioMetricheEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioAccessiEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_ACCESSI_VISIBILITY))) {
				menuMonitoraggioAccessiEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioLogEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_LOG_VISIBILITY))) {
				menuMonitoraggioLogEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioApiEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_API_VISIBILITY))) {
				menuMonitoraggioApiEnabled = Boolean.FALSE;
			}

			Boolean menuMonitoraggioReportnotificheEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MONITORAGGIO_REPORTNOTIFICHE_VISIBILITY))) {
				menuMonitoraggioReportnotificheEnabled = Boolean.FALSE;
			}

			Boolean menuHelpdeskEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_HELPDESK_VISIBILITY))) {
				menuHelpdeskEnabled = Boolean.FALSE;
			}

			Boolean menuMessaggiEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_MESSAGGI_VISIBILITY))) {
				menuMessaggiEnabled = Boolean.FALSE;
			}

			Boolean menuGestioneHelpdeskEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_VISIBILITY))) {
				menuGestioneHelpdeskEnabled = Boolean.FALSE;
			}
				
			Boolean menuGestioneHelpdeskMessaggioEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_MESSAGGIO_VISIBILITY))) {
				menuGestioneHelpdeskMessaggioEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskCategoriaMessaggioEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_CATEGORIAMESSAGGIO_VISIBILITY))) {
				menuGestioneHelpdeskCategoriaMessaggioEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskMessaggioIstantaneoEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_MESSAGGIOISTANTANEO_VISIBILITY))) {
				menuGestioneHelpdeskMessaggioIstantaneoEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskRichiesteHDEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_RICHIESTEHD_VISIBILITY))) {
				menuGestioneHelpdeskRichiesteHDEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskGestioneRichiesteHDEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_GESTIONERICHIESTEHD_VISIBILITY))) {
				menuGestioneHelpdeskGestioneRichiesteHDEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskStoricoRichiesteHDEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_STATORICHIESTAHD_VISIBILITY))) {
				menuGestioneHelpdeskStoricoRichiesteHDEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskTipoRichiestaHDEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_TIPORICHIESTAHD_VISIBILITY))) {
				menuGestioneHelpdeskTipoRichiestaHDEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskFaqEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_FAQ_VISIBILITY))) {
				menuGestioneHelpdeskFaqEnabled = Boolean.FALSE;
			}
			
			Boolean menuGestioneHelpdeskCategoriaFaqEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_GESTIONEHELPDESK_CATEGORIAFAQ_VISIBILITY))) {
				menuGestioneHelpdeskCategoriaFaqEnabled = Boolean.FALSE;
			}

			Boolean menuOrganigrammaServizioEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ORGANIGRAMMA_SERVIZIO_VISIBILITY))) {
				menuOrganigrammaServizioEnabled = Boolean.FALSE;
			}
			
			Boolean menuOrganigrammaAssessoratoEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ORGANIGRAMMA_ASSESSORATO_VISIBILITY))) {
				menuOrganigrammaAssessoratoEnabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheTipoodgEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_TIPOODG_VISIBILITY))) {
				menuAnagraficheTipoodgEnabled = Boolean.FALSE;
			}

			Boolean menuAnagraficheTipodeterminazioneEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_TIPODETERMINAZIONE_VISIBILITY))) {
				menuAnagraficheTipodeterminazioneEnabled = Boolean.FALSE;
			}
			
			Boolean menuAnagraficheRubricadestinatarioesternoEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_RUBRICADESTINATARIOESTERNO_VISIBILITY))) {
				menuAnagraficheRubricadestinatarioesternoEnabled = Boolean.FALSE;
			}
			
			Boolean menuAnagraficheRubricabeneficiarioEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_ANAGRAFICHE_RUBRICABENEFICIARIO_VISIBILITY))) {
				menuAnagraficheRubricabeneficiarioEnabled = Boolean.FALSE;
			}

			Boolean menuPredisposizioneattoClonaEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_PREDISPOSIZIONEATTO_CLONA_VISIBILITY))) {
				menuPredisposizioneattoClonaEnabled = Boolean.FALSE;
			}

			Boolean menuRichiesteDirigenteEnabled = Boolean.TRUE;
			if (!Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.MENU_RICHIESTEDIRIGENTE_VISIBILITY))) {
				menuRichiesteDirigenteEnabled = Boolean.FALSE;
			}
			
			String tipoConfigurazioneTaskProfiloId = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_PROFILO_ID);
			String tipoConfigurazioneTaskProfiloDescrizione = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_PROFILO_DESCRIZIONE);
			String tipoConfigurazioneTaskUfficioId = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_UFFICIO_ID);
			String tipoConfigurazioneTaskUfficioDescrizione = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_UFFICIO_DESCRIZIONE);
			
			String parereCommissioneObbligatorio = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONE_CONSIGLIARE_OBBLIGATORIO);
			String parereConsQuartRevContObbligatorio = WebApplicationProps.getProperty(ConfigPropNames.PARERE_CONS_QUART_REV_CONT_OBBLIGATORIO);
			String parereAssessoreObbligatorio = WebApplicationProps.getProperty(ConfigPropNames.PARERE_ASSESSORE_OBBLIGATORIO);
			
			String codiceConfigurazioneParereConsiliare = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_CONFIGURAZIONE);
			String codiceConfigurazioneParereQuartRev = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_QUARTREV_CODICE_CONFIGURAZIONE);
			String codiceTipoParereCommissioniConsiliari = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO);
			String codiceTipoParereQuartRev = WebApplicationProps.getProperty(ConfigPropNames.PARERE_QUARTIERE_REVISORE_CODICE_TIPO);
			
			String messaggioManutenzione = WebApplicationProps.getProperty(ConfigPropNames.MESSAGGIO_MANUTENZIONE);
			
			String globalEnv = GlobalProps.getProperty(Constants.GLOBAL_ENV);
			String globalVersion = GlobalProps.getProperty(Constants.GLOBAL_VERSION);
			String globalUrlConvert = GlobalProps.getProperty(Constants.GLOBAL_URL_CONVERT);
			String globalSiteTitle = GlobalProps.getProperty(Constants.GLOBAL_SITE_TITLE);
			String globalSiteCss = GlobalProps.getProperty(Constants.GLOBAL_SITE_CSS);

			JsonObject json = new JsonObject();
			json.addProperty("enableNewConvertMode", WebApplicationProps.getProperty("enableNewConvertMode"));

			JsonArray aoosListByDate = new JsonArray();
			List<Object> strAooList = WebApplicationProps.getPropertyList("enableNewConvertModeAooList");
			if(strAooList!=null && !strAooList.isEmpty()) {
				for(Object i : strAooList) {
					if(!i.toString().trim().isEmpty()) {
						JsonArray aoos = new JsonArray();
						String[] aoosStr = i.toString().trim().split("-");
						for(String aoo : aoosStr) {
							if(!aoo.trim().isEmpty()) {
								aoos.add(new JsonPrimitive(aoo));
							}
						}
						aoosListByDate.add(aoos);
					}
				}
			}
			json.add("enableNewConvertModeAooList", aoosListByDate);	
			
			JsonArray dateList = new JsonArray();
			List<Object> strDateList = WebApplicationProps.getPropertyList("enableNewConvertModeFromDateList");
			if(strDateList!=null && !strDateList.isEmpty()) {
				for(Object data : strDateList) {
					if(!data.toString().trim().isEmpty()) {
						dateList.add(new JsonPrimitive(data.toString().trim()));
					}
				}
			}
			json.add("enableNewConvertModeFromDateList", dateList);	
			
			json.addProperty("user_manage_enabled", userManageEnabled);
			json.addProperty("login_url", loginUrl);
			json.addProperty("logout_url", logoutUrl);
			json.addProperty("changeuser_url", changeUserUrl);
			json.addProperty("logout_url_enabled", logoutUrlEnabled);
			
			try {
				if(messaggioManutenzione!=null && !messaggioManutenzione.trim().isEmpty()) {
					json.addProperty("msgManutenzione", messaggioManutenzione.trim());
				}
			}catch(Exception e) {}
			json.addProperty("cp_name", DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_POOL_NAME, "GestattiCP"));
			json.addProperty("section_datiidentificativi_enabled", sectionDatiIdentificativiEnabled);
			json.addProperty("section_assegnazioneincarichi_enabled", sectionAssegnazioneIncarichiEnabled);
			json.addProperty("section_risposta_enabled", sectionRispostaEnabled);
			json.addProperty("section_sottoscrizioni_enabled", sectionSottoscrizioniEnabled);
			json.addProperty("section_riferimentinormativi_enabled", sectionRiferimentiNormativiEnabled);
			json.addProperty("section_preambolo_enabled", sectionPreamboloEnabled);
			json.addProperty("section_testo_enabled", sectionTestoEnabled);
			json.addProperty("section_motivazione_enabled", sectionMotivazioneEnabled);
			json.addProperty("section_garanzieriservatezza_enabled", sectionGaranzieRiservatezzaEnabled);
			json.addProperty("section_schede_enabled", sectionSchedeEnabled);
			json.addProperty("section_dichiarazioni_enabled", sectionDichiarazioniEnabled);
			json.addProperty("section_dispositivo_enabled", sectionDispositivoEnabled);
			json.addProperty("section_domanda_enabled", sectionDomandaEnabled);
			json.addProperty("section_allegati_enabled", sectionAllegatiEnabled);
			json.addProperty("section_trasparenza_enabled", sectionTrasparenzaEnabled);
			json.addProperty("section_notifica_enabled", sectionNotificaEnabled);
			json.addProperty("section_archivio_enabled", sectionArchivioEnabled);
			json.addProperty("section_sistemiregionali_enabled", sectionSistemiRegionaliEnabled);
			json.addProperty("section_note_enabled", sectionNoteEnabled);
			json.addProperty("section_documentipdf_enabled", sectionDocumentiPdfEnabled);
			json.addProperty("section_parere_enabled", sectionParereEnabled);
			json.addProperty("section_divulgazione_enabled", sectionDivulgazioneEnabled);
			json.addProperty("section_dettagliatto_enabled", sectionDettagliAttoEnabled);
			json.addProperty("section_listatask_enabled", sectionListaTaskEnabled);
			json.addProperty("section_eventiatto_enabled", sectionEventiAttoEnabled);
			json.addProperty("section_datiidentificativiconsiglio_enabled", sectionDatiIdentificativiConsiglioEnabled);
			json.addProperty("section_divulgazionesemplificata_enabled", sectionDivulgazioneSemplificataEnabled);
			json.addProperty("section_documentipdfconsiglio_enabled", sectionDocumentiPdfConsiglioEnabled);
			json.addProperty("section_datiidentificativiverbale_enabled", sectionDatiIdentificativiVerbaleEnabled);
			json.addProperty("section_seduteatto_enabled", sectionSeduteAttoEnabled);

			json.addProperty("component_gestioneregistrazioneutente_enabled", componentGestioneRegistrazioneUtenteEnabled);
			json.addProperty("component_notifiche_popup_enabled", componentNotifichePopupEnabled);

			json.addProperty("navbar_contatti_enabled", navbarContattiEnabled);
			json.addProperty("navbar_faq_enabled", navbarFaqEnabled);
			json.addProperty("navbar_link_enabled", navbarLinkEnabled);
			json.addProperty("navbar_manuale_enabled", navbarManualeEnabled);
			json.addProperty("navbar_manuale_url_enabled", navbarManualeUrlEnabled);

			json.addProperty("menu_monitoraggiopubblicazioni_enabled", menuMonitoraggiopubblicazioniEnabled);
			json.addProperty("menu_monitoraggio_reportdocumentale_enabled", menuMonitoraggioReportdocumentaleEnabled);
			json.addProperty("menu_ricerca_storica_enabled", menuRicercaStoricaEnabled);
			json.addProperty("menu_ricerca_libera_enabled", menuRicercaLiberaEnabled);
			json.addProperty("menu_anagrafiche_sistemaaccreditato_enabled", menuAnagraficheSistemaAccreditatoEnabled);
			json.addProperty("menu_anagrafiche_tipoadempimento_enabled", menuAnagraficheTipoAdempimentoEnabled);
			json.addProperty("menu_anagrafiche_materiadl33_enabled", menuAnagraficheMateriaDl33Enabled);
			json.addProperty("menu_anagrafiche_ambitodl33_enabled", menuAnagraficheAmbitoDl33Enabled);
			json.addProperty("menu_anagrafiche_classificazione_enabled", menuAnagraficheClassificazioneEnabled);
			json.addProperty("menu_monitoraggio_reportsms_enabled", menuMonitoraggioReportSmsEnabled);
			json.addProperty("menu_monitoraggio_schedulermanagement_enabled", menuMonitoraggioSchedulerManagementEnabled);
			json.addProperty("menu_monitoraggio_metriche_enabled", menuMonitoraggioMetricheEnabled);
			json.addProperty("menu_monitoraggio_accessi_enabled", menuMonitoraggioAccessiEnabled);
			json.addProperty("menu_monitoraggio_log_enabled", menuMonitoraggioLogEnabled);
			json.addProperty("menu_monitoraggio_api_enabled", menuMonitoraggioApiEnabled);
			json.addProperty("menu_monitoraggio_reportnotifiche_enabled", menuMonitoraggioReportnotificheEnabled);
			json.addProperty("menu_helpdesk_enabled", menuHelpdeskEnabled);
			json.addProperty("menu_messaggi_enabled", menuMessaggiEnabled);
			json.addProperty("menu_gestionehelpdesk_enabled", menuGestioneHelpdeskEnabled);
			json.addProperty("menu_gestionehelpdesk_messaggio_enabled", menuGestioneHelpdeskMessaggioEnabled);
			json.addProperty("menu_gestionehelpdesk_categoriamessaggio_enabled", menuGestioneHelpdeskCategoriaMessaggioEnabled);
			json.addProperty("menu_gestionehelpdesk_messaggioistantaneo_enabled", menuGestioneHelpdeskMessaggioIstantaneoEnabled);
			json.addProperty("menu_gestionehelpdesk_richiestehd_enabled", menuGestioneHelpdeskRichiesteHDEnabled);
			json.addProperty("menu_gestionehelpdesk_gestionerichiestehd_enabled", menuGestioneHelpdeskGestioneRichiesteHDEnabled);
			json.addProperty("menu_gestionehelpdesk_storicorichiestehd_enabled", menuGestioneHelpdeskStoricoRichiesteHDEnabled);
			json.addProperty("menu_gestionehelpdesk_tiorichiestahd_enabled", menuGestioneHelpdeskTipoRichiestaHDEnabled);
			json.addProperty("menu_gestionehelpdesk_faq_enabled", menuGestioneHelpdeskFaqEnabled);
			json.addProperty("menu_gestionehelpdesk_categoriafaq_enabled", menuGestioneHelpdeskCategoriaFaqEnabled);
			
			json.addProperty("menu_organigramma_servizio_enabled", menuOrganigrammaServizioEnabled);
			json.addProperty("menu_organigramma_assessorato_enabled", menuOrganigrammaAssessoratoEnabled);
			json.addProperty("menu_anagrafiche_tipoodg_enabled", menuAnagraficheTipoodgEnabled);
			json.addProperty("menu_anagrafiche_tipodeterminazione_enabled", menuAnagraficheTipodeterminazioneEnabled);
			json.addProperty("menu_anagrafiche_rubricadestinatarioesterno_enabled", menuAnagraficheRubricadestinatarioesternoEnabled);
			json.addProperty("menu_anagrafiche_rubricabeneficiario_enabled", menuAnagraficheRubricabeneficiarioEnabled);
			json.addProperty("menu_predisposizioneatto_clona_enabled", menuPredisposizioneattoClonaEnabled);
			json.addProperty("menu_richiestedirigente_enabled", menuRichiesteDirigenteEnabled);
			

			json.addProperty("tipo_conf_task_profilo_id", tipoConfigurazioneTaskProfiloId);
			json.addProperty("tipo_conf_task_profilo_desc", tipoConfigurazioneTaskProfiloDescrizione);
			json.addProperty("tipo_conf_task_ufficio_id", tipoConfigurazioneTaskUfficioId);
			json.addProperty("tipo_conf_task_ufficio_desc", tipoConfigurazioneTaskUfficioDescrizione);
			
			json.addProperty("parere_commissione_consigliare_obbligatorio", parereCommissioneObbligatorio);
			json.addProperty("parere_cons_quart_rev_cont_obbligatorio", parereConsQuartRevContObbligatorio);
			json.addProperty("parere_assessore_obbligatorio", parereAssessoreObbligatorio);
			
			json.addProperty("codice_configurazione_parere_consiliare", codiceConfigurazioneParereConsiliare);
			json.addProperty("codice_configurazione_parere_quartrev", codiceConfigurazioneParereQuartRev);
			json.addProperty("codice_tipo_parere_commissioni_consiliari", codiceTipoParereCommissioniConsiliari);
			json.addProperty("codice_tipo_parere_quartieri_revisori", codiceTipoParereQuartRev);
			
			List<Object> tipoAttoPropostaCompletaFrontespizio = WebApplicationProps.getPropertyList(ConfigPropNames.PROPOSTA_COMPLETO_FRONTESPIZIO_TIPOATTO_MODELLO);
			if(tipoAttoPropostaCompletaFrontespizio!=null) {
				String tipoAttoPropostaCompletaFrontespizioString = null;
				for(Object typeLabelObj : tipoAttoPropostaCompletaFrontespizio) {
					if(typeLabelObj!=null && typeLabelObj instanceof String) {
						String typeLabel = (String)typeLabelObj;
						if(!typeLabel.isEmpty() && typeLabel.contains("#") && typeLabel.split("#").length == 2) {
							if(tipoAttoPropostaCompletaFrontespizioString == null) {
								tipoAttoPropostaCompletaFrontespizioString = "";
							}
							tipoAttoPropostaCompletaFrontespizioString+=(typeLabel.split("#")[0]+"#"+typeLabel.split("#")[1]+"**");
						}
					}
				}
				if(tipoAttoPropostaCompletaFrontespizioString!=null) {
					json.addProperty("tipo_atto_proposta_completa_frontespizio", tipoAttoPropostaCompletaFrontespizioString);
				}
			}

			json.addProperty("global_env", globalEnv);
			json.addProperty("global_version", globalVersion);
			json.addProperty("global_url_convert", globalUrlConvert);
			json.addProperty("global_site_title", globalSiteTitle);
			json.addProperty("global_site_css", globalSiteCss);
			json.addProperty("multipart_max_file_size", String.valueOf(multipartConfig.getMaxFileSize()));
			json.addProperty("pubblicazione_oblio_visible", sectionPubblicazioneDirittoOblioVisibility);
			json.addProperty("upload_doc_firmato_visible", uploadDocFirmatiVisibility);
			json.addProperty("buildTS", buildTS);
			
			List<Object> avanzamentoTypes = WebApplicationProps.getPropertyList(Constants.WEB_APPLICATION_AVANZAMENTO_WARNING_TYPES);
			if(avanzamentoTypes!=null) {
				JsonObject avanzamentiTypesJson = null;
				for(Object typeLabelObj : avanzamentoTypes) {
					if(typeLabelObj!=null && typeLabelObj instanceof String) {
						String typeLabel = (String)typeLabelObj;
						if(!typeLabel.isEmpty() && typeLabel.contains("#") && typeLabel.split("#").length == 2) {
							if(avanzamentiTypesJson == null) {
								avanzamentiTypesJson = new JsonObject();
							}
							avanzamentiTypesJson.addProperty(typeLabel.split("#")[0], typeLabel.split("#")[1]);
						}
					}
				}
				if(avanzamentiTypesJson!=null) {
					json.add("avanzamento_warning_types", avanzamentiTypesJson);
				}
			}
			
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

}
