/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.gestatti.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

/**
 * @author Gianluca Pindinelli
 *
 */
@Configuration
@PropertySources({ @PropertySource(value = "file:${ASSATTI_CONFIG_FOLDER}/datasource.properties", ignoreResourceNotFound = false) })
public class DatasourceConfiguration {

}
