package it.linksmt.assatti.auth.web.concrete;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.WebAuthFactory;
import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;
import it.linksmt.assatti.auth.web.service.impl.DatabaseAuthenticationManagerBuilder;

/**
 * Concrete WebAuthFactory implementation for database authentication environment.
 * 
 * @author marco ingrosso
 *
 */
@Component("databaseWebAuthFactory")
public class DatabaseWebAuthFactory extends WebAuthFactory {
	
	@Inject
	private DatabaseAuthenticationManagerBuilder databaseAuthenticationManagerBuilder;

	@Override
	public AssattiAuthenticationManagerBuilder getAssattiAuthenticationManagerBuilder() {
		return databaseAuthenticationManagerBuilder;
	}

}