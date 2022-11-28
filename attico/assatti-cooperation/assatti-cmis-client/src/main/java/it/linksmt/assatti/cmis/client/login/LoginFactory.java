package it.linksmt.assatti.cmis.client.login;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.CmisProps;

/**
 * Abstract Factory for login
 * 
 * @author marco ingrosso
 *
 */
public abstract class LoginFactory {
	
	public static LoginFactory getLoginFactory() {
		LoginFactory object = null;
		String concreteLoginFactory = CmisProps.getProperty(ConfigPropNames.DMS_LOGIN_CONCRETE_FACTORY);
		
		try {
			Class<?> c = Class.forName(concreteLoginFactory);

			// check if concreteLoginFactory class extends LoginFactory
			if (c != LoginFactory.class && LoginFactory.class.isAssignableFrom(c)) {
				// get concreteLoginFactory constructor
				Constructor<?> cons = c.getConstructor();
				// create concreteLoginFactory instance
				object = (LoginFactory) cons.newInstance();
			}
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return object;
	}
	
	public abstract LoginService getLoginService();

}
