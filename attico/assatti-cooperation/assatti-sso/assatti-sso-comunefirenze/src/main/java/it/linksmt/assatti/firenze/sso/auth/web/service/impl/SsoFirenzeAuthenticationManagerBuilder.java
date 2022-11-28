package it.linksmt.assatti.firenze.sso.auth.web.service.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;

/**
 * Concrete Product for SSO Firenze AuthenticationManagerBuilder abstract factory.
 *
 * @author marco ingrosso
 *
 */
@Component("ssoFirenzeAuthenticationManagerBuilder")
public class SsoFirenzeAuthenticationManagerBuilder extends AssattiAuthenticationManagerBuilder {

	private final Logger log = LoggerFactory.getLogger(SsoFirenzeAuthenticationManagerBuilder.class.getName());

	@Inject
	@Qualifier("firenzeUserDetailsService")
	private UserDetailsService userDetailsService;

	@Override
	public AuthenticationManagerBuilder config(AuthenticationManagerBuilder auth) throws Exception {

		log.debug("SsoFirenzeAuthenticationManagerBuilder :: config()");

		auth.userDetailsService(userDetailsService).passwordEncoder(new SsoFirenzeTokenPassword());

		return auth;
	}

}
