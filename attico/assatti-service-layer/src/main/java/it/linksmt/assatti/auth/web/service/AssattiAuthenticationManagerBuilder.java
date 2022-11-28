package it.linksmt.assatti.auth.web.service;

import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;

/**
 * Abstract Product for web authentication abstract factory
 * 
 * @author marco ingrosso
 *
 */
public abstract class AssattiAuthenticationManagerBuilder {
	
	public abstract AuthenticationManagerBuilder config(AuthenticationManagerBuilder auth) throws Exception;

}