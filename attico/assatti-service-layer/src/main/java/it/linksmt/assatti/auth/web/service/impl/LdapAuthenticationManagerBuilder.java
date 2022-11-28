package it.linksmt.assatti.auth.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;
import it.linksmt.assatti.utility.configuration.LdapProps;

/**
 * Concrete Product for Ldap AuthenticationManagerBuilder abstract factory.
 * 
 * @author marco ingrosso
 *
 */
@Component("ldapAuthenticationManagerBuilder")
public class LdapAuthenticationManagerBuilder extends AssattiAuthenticationManagerBuilder {
	
	private Logger log = LoggerFactory.getLogger(LdapAuthenticationManagerBuilder.class.getName());

	@Override
	public AuthenticationManagerBuilder config(AuthenticationManagerBuilder auth) throws Exception {
		log.debug("LdapAuthenticationManagerBuilder :: config()");
		
		auth.ldapAuthentication()
		.userSearchBase(LdapProps.getProperty("ldap.userSearchBase"))
		.userSearchFilter(LdapProps.getProperty("ldap.userSearchFilter"))
//		.groupSearchBase(ldapGroupSearchBase)
//		.groupSearchFilter(ldapGroupSearchFilter)
		.rolePrefix("")
		.contextSource()
			.url(LdapProps.getProperty("ldap.serverUrl"))
			.managerDn(LdapProps.getProperty("ldap.managerDn"))
			.managerPassword(LdapProps.getProperty("ldap.managerPassword"));
		
		return auth;
	}

}
