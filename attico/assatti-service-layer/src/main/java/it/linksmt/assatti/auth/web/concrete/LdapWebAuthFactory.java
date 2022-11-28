package it.linksmt.assatti.auth.web.concrete;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.WebAuthFactory;
import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;
import it.linksmt.assatti.auth.web.service.impl.LdapAuthenticationManagerBuilder;

/**
 * Concrete WebAuthFactory implementation for LDAP authentication environment.
 * 
 * @author marco ingrosso
 *
 */
@Component("ldapWebAuthFactory")
public class LdapWebAuthFactory extends WebAuthFactory {
	
	@Inject
	private LdapAuthenticationManagerBuilder ldapAuthenticationManagerBuilder;

	@Override
	public AssattiAuthenticationManagerBuilder getAssattiAuthenticationManagerBuilder() {
		return ldapAuthenticationManagerBuilder;
	}

}