package it.linksmt.assatti.firenze.cmis.client.login.concrete;

import it.linksmt.assatti.cmis.client.login.LoginFactory;
import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.firenze.cmis.client.login.service.impl.FirenzeLoginService;

/**
 * Concrete LoginFactory implementation for Firenze Alfresco environment.
 * 
 * @author marco ingrosso
 *
 */
public class FirenzeLoginFactory extends LoginFactory {

	@Override
	public LoginService getLoginService() {
		return new FirenzeLoginService();
	}

}