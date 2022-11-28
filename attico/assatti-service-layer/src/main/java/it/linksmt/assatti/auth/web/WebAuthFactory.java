package it.linksmt.assatti.auth.web;

import java.lang.reflect.Constructor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.auth.web.service.AssattiAuthenticationManagerBuilder;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.GlobalProps;

/**
 * Abstract Factory for Web Authentication
 *
 * @author marco ingrosso
 *
 */
@Component("webAuthFactory")
public abstract class WebAuthFactory {

	private final static Logger log = LoggerFactory.getLogger(WebAuthFactory.class.getName());

	public static WebAuthFactory getWebAuthFactory() {
		WebAuthFactory object = null;
		String concreteWebAuthFactory = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_CONCRETE_FACTORY);

		try {
			Class<?> c = Class.forName(concreteWebAuthFactory);

			// check if concreteWebAuthFactory class extends WebAuthFactory
			if (c != WebAuthFactory.class && WebAuthFactory.class.isAssignableFrom(c)) {
				// get concreteWebAuthFactory constructor
				Constructor<?> cons = c.getConstructor();
				// create concreteWebAuthFactory instance
				object = (WebAuthFactory) cons.newInstance();
			}
		}
		catch (Exception e) {
			log.error("unable to load concrete factory :: " + e.getMessage(), e);
		}

		return object;
	}

	public abstract AssattiAuthenticationManagerBuilder getAssattiAuthenticationManagerBuilder();

}
