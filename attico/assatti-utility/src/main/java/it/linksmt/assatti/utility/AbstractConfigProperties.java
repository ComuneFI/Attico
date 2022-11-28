package it.linksmt.assatti.utility;

import java.util.List;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.reloading.FileChangedReloadingStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractConfigProperties {

	private static Logger log = LoggerFactory.getLogger(AbstractConfigProperties.class);

	protected static PropertiesConfiguration loadProperties(final String filename) {

		try {
			String confPath = StringUtil.cleanEnvVar(System.getenv(ConfigPropNames.CONFIG_FILE_FOLDER));

			if (confPath.trim().length() < 1) {
				confPath = StringUtil.cleanEnvVar(System.getProperty(ConfigPropNames.CONFIG_FILE_FOLDER));
			}

			PropertiesConfiguration retVal = new PropertiesConfiguration(confPath + "/" + filename);
			retVal.setReloadingStrategy(new FileChangedReloadingStrategy());

			return retVal;
		}
		catch (Exception e) {
			log.error("Error to get configuration file : " + filename, e);
			return null;
		}
	}

	protected static String getProperty(final PropertiesConfiguration props, final String name, final String defaultValue) {
		String retVal = null;
		if ((props != null) && (name != null)) {
			retVal = props.getString(name.trim(), defaultValue);
		}
		return retVal;
	}
	
	protected static List<Object> getPropertyList(final PropertiesConfiguration props, final String name, final List<Object> defaultValue) {
		List<Object> retVal = null;
		if ((props != null) && (name != null)) {
			retVal = props.getList(name.trim(), defaultValue);
		}
		return retVal;
	}
	
	protected static List<Object> getPropertyList(final PropertiesConfiguration props, final String name) {
		List<Object> retVal = null;
		if ((props != null) && (name != null)) {
			retVal = props.getList(name.trim());
		}
		return retVal;
	}
}
