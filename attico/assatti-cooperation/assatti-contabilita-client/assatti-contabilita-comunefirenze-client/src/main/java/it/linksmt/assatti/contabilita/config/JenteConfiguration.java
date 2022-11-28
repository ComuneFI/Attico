/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.contabilita.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author Gianluca Pindinelli
 *
 */
@Configuration
@ComponentScan(basePackages = { "it.linksmt.assatti.contabilita" })
//@PropertySources({ @PropertySource("classpath:contabilitaComuneFirenze.properties"), @PropertySource(value = "file:${ASSATTI_CONFIG_FOLDER}/contabilita.properties", ignoreResourceNotFound = true) })
public class JenteConfiguration {

	/*
	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	*/

}