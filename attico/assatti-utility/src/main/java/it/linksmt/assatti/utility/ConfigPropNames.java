package it.linksmt.assatti.utility;

public interface ConfigPropNames {

	/* FOLDER  */
	String CONFIG_FILE_FOLDER = "ASSATTI_CONFIG_FOLDER";
	
	String GLOBAL_CONFIG_FILENAME  				= "global.properties";
	String DATASOURCE_CONFIG_FILENAME			= "datasource.properties";
	String SCHEDULER_CONFIG_FILENAME			= "scheduler.properties";
	
	String WEB_APPLICATION_CONFIG_FILENAME		= "web-application.properties";
	String SECTIONS_VISIBILITY_CONFIG_FILENAME  = "sections-visibility.properties";
	String CMIS_CONFIG_FILENAME					= "cmis.properties";
	
	// String MAIL_CONFIG_FILENAME				= "mail.properties";
	
	String LDAP_CONFIG_FILENAME					= "ldap.properties";
	String SSO_CONFIG_FILENAME					= "sso.properties";
	String CONTABILITA_CONFIG_FILENAME			= "contabilita.properties";
	String FDR_CLIENT_CONFIG_FILENAME			= "fdr-client.properties";
	
	// String PROTOCOLLO_CONFIG_FILENAME		= "protocollo.properties";
	// String PUBBLICAZIONE_CONFIG_FILENAME		= "pubblicazione.properties";

	/*
	 * CONFIGURAZIONE DB
	 */
	String DATASOURCE_POOL_NAME				= "datasource.db.poolname";
	String DATASOURCE_DB_URL				= "datasource.db.url";
	String DATASOURCE_DB_USERNAME			= "datasource.db.username";
	String DATASOURCE_DB_PASSWORD			= "datasource.db.password";
	String DATASOURCE_CLASSNAME				= "datasource.dataSourceClassName";
	String DATASOURCE_MAXIMUMPOOLSIZE		= "datasource.maximumPoolSize";
	String DATASOURCE_MINPOOLSIZE			= "datasource.minIdleConnection";
	String DATASOURCE_ATTICO_DB_NAME		= "datasource.gestatti.dbname";
	String DATASOURCE_CAMUNDA_DB_NAME		= "datasource.camunda.dbname";
	
	String CACHE_HAZELCAST_BACKUPCOUNT		= "datasource.cache.hazelcast.backupCount";
	String CACHE_TIME_TO_LIVE_SECONDS		= "datasource.cache.timeToLiveSeconds";
	
	String JPA_HIBERNATE_DDL_AUTO			= "jpa.hibernate.ddl-auto";
	String JPA_DATABASE_PLATFORM			= "jpa.database-platform";
	String JPA_HIBERNATE_NAMING_STRATEGY	= "jpa.hibernate.naming-strategy";
	String JPA_SHOW_SQL						= "jpa.show_sql";
	
	String JPA_PROPERTIES_HIBERNATE_CACHE_REGION_FACTORY_CLASS 		= "jpa.properties.hibernate.cache.region.factory_class";
	String JPA_PROPERTIES_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE	= "jpa.properties.hibernate.cache.use_second_level_cache";
	String JPA_PROPERTIES_HIBERNATE_CACHE_USE_QUERY_CACHE			= "jpa.properties.hibernate.cache.use_query_cache";
	String JPA_PROPERTIES_HIBERNATE_GENERATE_STATISTICS				= "jpa.properties.hibernate.generate_statistics";
	String JPA_PROPERTIES_HIBERNATE_CACHE_USE_MINIMAL_PUTS 			= "jpa.properties.hibernate.cache.use_minimal_puts";
	String JPA_PROPERTIES_HIBERNATE_CACHE_HAZELCAST_USE_LITE_MEMBER = "jpa.properties.hibernate.cache.hazelcast.use_lite_member";
	String JPA_PROPERTIES_HIBERNATE_CONNECTION_CHARACTERENCODING	= "jpa.properties.hibernate.connection.characterEncoding";
	String JPA_PROPERTIES_HIBERNATE_CONNECTION_USEUNICODE			= "jpa.properties.hibernate.connection.useUnicode";
	String JPA_PROPERTIES_HIBERNATE_CONNECTION_CHARSET				= "jpa.properties.hibernate.connection.CharSet";
	
	/*
	 * CONFIGURAZIONE WEB-APP
	 */
	String GENERAZIONE_IMPRONTA_ATTO_ENABLED = "generazione.impronta.atto.enabled";
	String GENERAZIONE_IMPRONTA_BOZZA_ENABLED = "generazione.impronta.bozza.enabled";
	
	String TIPIATTO_DOC_COMPLETO_LIST = "tipiatto.doc.completo";
	
	String DISABILITAZIONE_PROFILI_CHECK_ATTOWORKER = "disabilitazione_profili_check_attoworker";
	
	String GIORNI_SCADENZA_ATTUAZIONE = "giorni_scadenza_attuazione";
	String ATTO_RETRIEVAL_ACCESS_CONTROL_ENABLED = "atto.retrieval.access.control.enabled";
	String ATTO_RETRIEVAL_ACCESS_CONTROL_CLASS = "atto.retrieval.access.control.class";
	
	String MULTIPART_MAXFILESIZE 			= "multipart.maxFileSize";
	String MULTIPART_MAXREQUESTSIZE 		= "multipart.maxRequestSize";
	String MESSAGES_CACHESECONDS			= "messages.cacheSeconds";
	
	String ALFRESCO_RETRY_SECONDS			= "alfresco.retry.seconds";
	
	String AUTHENTICATION_OAUTH_CLIENTID	= "authentication.oauth.clientid";
	String AUTHENTICATION_OAUTH_SECRET		= "authentication.oauth.secret";
	String AUTHENTICATION_OAUTH_TOKENVALIDITYINSECONDS	= "authentication.oauth.tokenValidityInSeconds";
	
	String CONVERT_PDF						= "convert.pdf";
	String CONVERT_PDF_LANDSCAPE			= "convertlandscape.pdf";
	String CONVERT_DOCX						= "converthtmltodoc.docx";
	String CONVERT_DOCX_LANDSCAPE			= "converthtmltodoclandscape.docx";
	
	String ENTE_DENOMINAZIONE				= "ente.denominazione";
	String ENTE_URL_SITO					= "ente.url.sito";
	String RUOLO_CAPO_ENTE					= "ruolo.capo.ente";
	String RUOLO_VICE_CAPO_ENTE				= "ruolo.vice.capo.ente";
	
	String SOTTOSCRIZIONE_QUALIFICHE_DIRIGENZIALI		= "sottoscrizione.qualifichedirigenziali";
	String SOTTOSCRIZIONE_RUOLI_DIRIGENZIALI 			= "sottoscrizione.ruolidiregenziali";
	
	String ATTO_PREFISSO_DELEGANTE 						= "atto.prefisso.delegante";
	String ATTO_ESECUTIVITA_PLUS_DAYS 					= "atto.esecutivita.plus.days";
	String STATO_ATTO_ANNULLAMENTO_AUTOMATICO 			= "stato.atto.annullamento.automatico";
	
	String PARERE_COMMISSIONE_CONSIGLIARE_OBBLIGATORIO	= "parere.commissione.consigliare.obbligatorio";
	String PARERE_CONS_QUART_REV_CONT_OBBLIGATORIO		= "parere.cons.quart.rev.cont.obbligatorio";
	String PARERE_ASSESSORE_OBBLIGATORIO				= "parere.assessore.obbligatorio";
	
	String ATTO_ALLEGATO_ESTENSIONI_ABILITATE 			= "atto.allegato.estensioni.abilitate";
	
	String NOTIFICA_GRUPPORUOLO_CONSIGLIERI 			= "notifica.grupporuolo.consiglieri";
	String NOTIFICA_GRUPPORUOLO_ALTRESTRUTTURE 			= "notifica.grupporuolo.altrestrutture";
	
	String PDF_MARGIN_LEFT = "document.margin.left";
	String PDF_MARGIN_TOP = "document.margin.top";
	String PDF_MARGIN_BOTTOM = "document.margin.bottom";
	String PDF_MARGIN_RIGHT = "document.margin.right";
		
	/*
	 * Configurazione repository CMIS
	 */
	String DMS_LOGIN_CONCRETE_FACTORY = "dms.login.concrete.factory";
    String CMIS_HOST = "cmis.host";
    String CMIS_URL = "cmis.url";
    String CMIS_USERNAME = "cmis.username";
    String CMIS_PASSWORD = "cmis.password";
    String CMIS_VERSION = "cmis.version";
    String CMIS_MAIN_FOLDER_PATH = "cmis.main.folder.path";
    String CMIS_YEAR_FOLDER_FIRST = "cmis.year.folder.first";
    
    /*
     * Configurazione del servizio di autenticazione
     */
    String WEB_AUTH_CONCRETE_FACTORY = "auth.web.concrete.factory";
    String WEB_AUTH_USER_MANAGE_ENABLED = "auth.web.user.manage.enabled";
    String WEB_AUTH_LOGIN_URL = "auth.web.login.url";
    String WEB_AUTH_LOGOUT_URL = "auth.web.logout.url";
    String WEB_AUTH_CHANGEUSER_URL = "auth.web.changeuser.url";
    String WEB_AUTH_LOGIN_SSO_ID_PARAMETER_NAME = "auth.web.login.sso.id.parameter.name";
    String WEB_AUTH_LOGOUT_SSO_ID_PARAMETER_NAME = "auth.web.logout.sso.id.parameter.name";
    
    /*
     * Configurazione SSO per DMS
     */
    String SSO_ALFRESCO_TOKEN_URL = "sso.alfresco.token.url";
    String SSO_ALFRESCO_TOKEN_KEY = "sso.alfresco.token.key";
    String SSO_ALFRESCO_TOKEN_INIT_VECTOR = "sso.alfresco.token.init.vector";
    String SSO_DELAY_CHECK_MILLISECONDS = "sso.delay.check.milliseconds";
    
    /*
     * Configurazione reportistica
     */
    String GLOBAL_LOGO_ENTE_FILENAME = "logo.ente.filename";
    String GLOBAL_LOGO_ENTE_FULL_FILENAME = "logo.ente.full.filename";

			
	/*
	 * PARERI COMMISSIONI, QUARTIERI, REVISORI
	 */
    String PARERE_COMMISSIONI_QUARTREV_CODICE_CONFIGURAZIONE		= "parere.commissioni.quartrev.codice.configurazione";
    String PARERE_COMMISSIONI_CONSILIARI_CODICE_CONFIGURAZIONE		= "parere.commissioni.consiliari.codice.configurazione";
    String PARERE_COMMISSIONI_CONSILIARI_PROCESS_DEFINITION_KEY		= "parere.commissioni.consiliari.process.definition.key";
    String PARERE_COMMISSIONI_CONSILIARI_PROCESS_ELEMENT_VARIABLE	= "parere.commissioni.consiliari.process.element.variable";
    String PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO				= "parere.commissioni.consiliari.codice.tipo";
    String PARERE_QUARTIERE_REVISORE_CODICE_TIPO					= "parere.quartiere.revisore.codice.tipo";
    String MESSAGGIO_MANUTENZIONE									= "messaggio_manutenzione";
    
    String PARERE_COMMISSIONI_TIPI_ATTO								= "parere.commissioni.tipi.atto";
    String PARERE_QUARTIERE_REVISORE_TIPI_ATTO						= "parere.quartiere.revisore.tipi.atto";
    String PARERE_COMMISSIONI_EDITABLE_ESITI_ATTO					= "parere.commissioni.editable.esiti.atto";
    
    String VISTO_ASSESSORE_CODICE_CONFIGURAZIONE					= "visto.assessore.codice.configurazione";
    
    String PROPOSTA_COMPLETO_FRONTESPIZIO_TIPOATTO_MODELLO			= "pcf.tipoatto.modello";
    
    
    /*
     * Configurazione checkTesto
     */
    String ATTO_TESTO_CHECK_NULL = "atto.testo.check";
    
    String ATTO_CHECK_REQUIRED_FIELDS = "atto.check.required.fields.enabled";
    
    String LIST_TIPO_ATTO_AGGIORNA_DATA_ADOZIONE = "tipiatto.aggiorna.data.adozione";
    
    String FORMATO_DATA_SEDUTA_TESTUALE = "formato.data.seduta.testuale";
    
    String ASSISTENZA_TECNICA_USER = "assistenza.tecnica.user";
    String ASSISTENZA_TECNICA_PASSWORD = "assistenza.tecnica.password";
    
    String NUMERO_COLONNE_TABELLE_CONSIGLIO = "numero.colonne.tabelle.consiglio";
    
    String AZIONI_DISABILITATE_X_TASK= "azioni.disabilitate.x.task";
    
    String ATTIVITA_NON_INCLUSE_IN_TAB_LAVORATI = "attivita.non.incluse.in.tab.lavorati";
}
