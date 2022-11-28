package it.linksmt.assatti;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;

import it.linksmt.assatti.auth.web.service.impl.DatabaseAuthenticationManagerBuilder;
import it.linksmt.assatti.bpm.config.BpmEngineConfiguration;
import it.linksmt.assatti.gestatti.config.OAuth2ServerConfiguration;
import it.linksmt.assatti.security.Http401UnauthorizedEntryPoint;

@Configuration
@EnableAspectJAutoProxy
@ComponentScan(basePackages = { "it.linksmt.assatti.datalayer", "it.linksmt.assatti.service", "it.linksmt.assatti.bpm.service", "it.linksmt.assatti.service.util", "it.linksmt.assatti.bpm.wrapper",
		"it.linksmt.assatti.bpm.util", "it.linksmt.assatti.cmis.client.service", "it.linksmt.assatti.service.converter", "it.linksmt.assatti.dms.service", "it.linksmt.assatti.cooperation.service",
		"it.linksmt.assatti.cooperation.service.contabilita", "it.linksmt.assatti.contabilita.service" }, basePackageClasses = { DatabaseAuthenticationManagerBuilder.class,
				OAuth2ServerConfiguration.class, BpmEngineConfiguration.class, Http401UnauthorizedEntryPoint.class })
@PropertySources({ @PropertySource(value = "file:${ASSATTI_CONFIG_FOLDER}/datasource.properties", ignoreResourceNotFound = false) })
@EnableAutoConfiguration()
public class ServiceConfiguration {

	@Bean
	public static PropertySourcesPlaceholderConfigurer properties() {
		return new PropertySourcesPlaceholderConfigurer();
	}

}
