package it.linksmt.assatti.cmis.client.login.service.impl;

import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.linksmt.assatti.cmis.client.exception.CmisServiceErrorCode;
import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.CmisProps;

/**
 * Concrete Product for login abstract factory, suitable for username+password authentication.
 * 
 * @author marco ingrosso
 *
 */
public class CmisCredentialsLoginService extends LoginService {
	
	private Logger log = LoggerFactory.getLogger(CmisCredentialsLoginService.class.getName());
	
	private static String cmisVersion = CmisProps.getProperty(ConfigPropNames.CMIS_VERSION);	// cmis version
	private static String cmisUrl = CmisProps.getProperty(ConfigPropNames.CMIS_URL);			// endpoint interfaccia cmis
	private static String cmisUsername = CmisProps.getProperty(ConfigPropNames.CMIS_USERNAME);	// username login su alfresco
	private static String cmisPassword = CmisProps.getProperty(ConfigPropNames.CMIS_PASSWORD);	// password login su alfresco

	@Override
	public Session getLoginSession() throws CmisServiceException {
		log.info("CmisCredentialsLoginService :: getLoginSession()");

        log.info("CmisCredentialsLoginService :: getLoginSession() :: cmisUrl: " + cmisUrl);
        log.info("CmisCredentialsLoginService :: getLoginSession() :: cmisUsername: " + cmisUsername);
        log.info("CmisCredentialsLoginService :: getLoginSession() :: cmisPassword: " + cmisPassword);
        log.info("CmisCredentialsLoginService :: getLoginSession() :: cmisVersion: " + cmisVersion);

		if (cmisUrl==null || cmisUrl.isEmpty()
				|| cmisUsername==null || cmisUsername.isEmpty()
				|| cmisPassword==null || cmisPassword.isEmpty()) {
			throw new CmisServiceException(CmisServiceErrorCode.MISSING_LOGIN_PARAMETERS_ERROR);
		}

		Map<String, String> parameter = new HashMap<String, String>();

		// user credentials
		parameter.put(SessionParameter.USER, cmisUsername);
		parameter.put(SessionParameter.PASSWORD, cmisPassword);

		// connection settings
		parameter.put(SessionParameter.ATOMPUB_URL, cmisUrl);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());

		if(cmisVersion.equals("1.0")) {
			// set the alfresco object factory
			parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.CmisObjectFactoryImpl");
		}

		try {
			// create session
			log.info("CmisCredentialsLoginService :: getLoginSession() :: creating new session...");
			SessionFactory factory = SessionFactoryImpl.newInstance();
			Session lastSession = factory.getRepositories(parameter).get(0).createSession();
			log.info("CmisCredentialsLoginService :: getLoginSession() :: new session created successfully!");

			return lastSession;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CmisServiceException(CmisServiceErrorCode.LOGIN_ERROR);
		}
	}

}
