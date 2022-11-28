package it.linksmt.assatti.auth.web.service.impl;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;

/**
 * Concrete Product for Database AuthenticationManagerBuilder abstract factory.
 * 
 * @author marco ingrosso
 *
 */
@Component("databaseAuthenticationManagerBuilder")
public class DatabaseAuthenticationManagerBuilder extends AssattiAuthenticationManagerBuilder {
	
	private Logger log = LoggerFactory.getLogger(DatabaseAuthenticationManagerBuilder.class.getName());
	
	@Inject
	@Qualifier("databaseUserDetailsService")
	private UserDetailsService userDetailsService;
	
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
	
	@Override
	public AuthenticationManagerBuilder config(AuthenticationManagerBuilder auth) throws Exception {
		log.debug("DatabaseAuthenticationManagerBuilder :: config()");

		auth
        .userDetailsService(userDetailsService)
        .passwordEncoder(passwordEncoder());
		
		return auth;
	}

}
