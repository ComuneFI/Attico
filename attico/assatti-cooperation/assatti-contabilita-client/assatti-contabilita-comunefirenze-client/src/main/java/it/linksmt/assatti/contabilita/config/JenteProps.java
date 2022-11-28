package it.linksmt.assatti.contabilita.config;

import org.apache.commons.configuration.PropertiesConfiguration;

import it.linksmt.assatti.utility.AbstractConfigProperties;
import it.linksmt.assatti.utility.ConfigPropNames;

public final class JenteProps extends AbstractConfigProperties {
	private JenteProps() { }

	private static PropertiesConfiguration _props = null;

    static {
    	_props = loadProperties(ConfigPropNames.CONTABILITA_CONFIG_FILENAME);       
    }

	public static String getProperty(final String name) {
		return getProperty(name, "");
	}

	public static String getProperty(final String name, final String defaultValue) {
		return getProperty(_props, name, defaultValue);
	}
}

