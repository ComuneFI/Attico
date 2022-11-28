package it.linksmt.assatti.cmis.client.login.service;

import org.apache.chemistry.opencmis.client.api.Session;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;

/**
 * Abstract Product for login abstract factory
 * 
 * @author marco ingrosso
 *
 */
public abstract class LoginService {

	public abstract Session getLoginSession() throws CmisServiceException;

}