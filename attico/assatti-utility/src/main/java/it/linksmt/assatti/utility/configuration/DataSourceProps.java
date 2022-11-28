package it.linksmt.assatti.utility.configuration;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;

import it.linksmt.assatti.utility.AbstractConfigProperties;
import it.linksmt.assatti.utility.ConfigPropNames;

public final class DataSourceProps extends AbstractConfigProperties {
	private DataSourceProps() { }

	private static PropertiesConfiguration _props = null;

    static {
    	_props = loadProperties(ConfigPropNames.DATASOURCE_CONFIG_FILENAME);       
    }

	public static String getProperty(final String name) {
		return getProperty(name, "");
	}

	public static String getProperty(final String name, final String defaultValue) {
		return getProperty(_props, name, defaultValue);
	}
	
	public static List<Object> getPropertyList(final String name, final List<Object> defaultValue) {
		return getPropertyList(_props, name, defaultValue);
	}
	
	public static List<Object> getPropertyList(final String name) {
		return getPropertyList(_props, name);
	}
}

