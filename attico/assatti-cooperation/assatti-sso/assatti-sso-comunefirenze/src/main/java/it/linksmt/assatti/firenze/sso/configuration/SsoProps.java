package it.linksmt.assatti.firenze.sso.configuration;

import org.apache.commons.configuration.PropertiesConfiguration;

import it.linksmt.assatti.utility.AbstractConfigProperties;
import it.linksmt.assatti.utility.ConfigPropNames;

public final class SsoProps extends AbstractConfigProperties {
	private SsoProps() { }

	private static PropertiesConfiguration _props = null;

    static {
    	_props = loadProperties(ConfigPropNames.SSO_CONFIG_FILENAME);       
    }

	public static String getProperty(final String name) {
		return getProperty(name, "");
	}

	public static String getProperty(final String name, final String defaultValue) {
		return getProperty(_props, name, defaultValue);
	}
}

