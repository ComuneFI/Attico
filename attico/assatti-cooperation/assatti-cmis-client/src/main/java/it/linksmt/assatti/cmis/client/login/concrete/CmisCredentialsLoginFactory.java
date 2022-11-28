package it.linksmt.assatti.cmis.client.login.concrete;

import it.linksmt.assatti.cmis.client.login.LoginFactory;
import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.cmis.client.login.service.impl.CmisCredentialsLoginService;

/**
 * Concrete LoginFactory implementation for generic Alfresco CMIS environment.
 * 
 * @author marco ingrosso
 *
 */
public class CmisCredentialsLoginFactory extends LoginFactory {

	@Override
	public LoginService getLoginService() {
		return new CmisCredentialsLoginService();
	}

}