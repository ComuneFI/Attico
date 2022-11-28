package it.linksmt.assatti.cmis.client.login.session;

import org.apache.chemistry.opencmis.client.api.Session;

/**
 * Singleton used to store CMIS session
 * 
 * @author marco ingrosso
 *
 */
public enum SessionStore {

	INSTANCE;

	Session session;

	public Session getSession() {
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

}