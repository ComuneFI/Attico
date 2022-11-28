package it.linksmt.assatti.firenze.sso.auth.web.concrete;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.WebAuthFactory;
import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;
import it.linksmt.assatti.firenze.sso.auth.web.service.impl.SsoFirenzeAuthenticationManagerBuilder;

/**
 * Concrete WebAuthFactory implementation for SSO Firenze authentication environment.
 * 
 * @author marco ingrosso
 *
 */
@Component("ssoFirenzeWebAuthFactory")
public class SsoFirenzeWebAuthFactory extends WebAuthFactory {
	
	@Inject
	private SsoFirenzeAuthenticationManagerBuilder ssoFirenzeAuthenticationManagerBuilder;

	@Override
	public AssattiAuthenticationManagerBuilder getAssattiAuthenticationManagerBuilder() {
		return ssoFirenzeAuthenticationManagerBuilder;
	}

}