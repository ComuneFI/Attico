package it.linksmt.assatti.fdr.config;

import org.apache.commons.configuration.PropertiesConfiguration;

import it.linksmt.assatti.utility.AbstractConfigProperties;
import it.linksmt.assatti.utility.ConfigPropNames;

public class FdrClientProps extends AbstractConfigProperties {
	private FdrClientProps() { }

	private static PropertiesConfiguration _props = null;

    static {
    	_props = loadProperties(ConfigPropNames.FDR_CLIENT_CONFIG_FILENAME);       
    }

	public static String getProperty(final String name) {
		return getProperty(name, "");
	}

	public static String getProperty(final String name, final String defaultValue) {
		return getProperty(_props, name, defaultValue);
	}
}
