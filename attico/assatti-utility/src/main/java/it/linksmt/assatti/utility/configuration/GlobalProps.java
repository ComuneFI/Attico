package it.linksmt.assatti.utility.configuration;

import org.apache.commons.configuration.PropertiesConfiguration;

import it.linksmt.assatti.utility.AbstractConfigProperties;
import it.linksmt.assatti.utility.ConfigPropNames;

public final class GlobalProps extends AbstractConfigProperties {
	private GlobalProps() { }

	private static PropertiesConfiguration _props = null;

    static {
    	_props = loadProperties(ConfigPropNames.GLOBAL_CONFIG_FILENAME);       
    }

	public static String getProperty(final String name) {
		return getProperty(name, "");
	}

	public static String getProperty(final String name, final String defaultValue) {
		return getProperty(_props, name, defaultValue);
	}
}

