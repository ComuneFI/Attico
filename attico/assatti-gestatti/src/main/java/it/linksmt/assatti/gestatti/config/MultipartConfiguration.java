package it.linksmt.assatti.gestatti.config;

import javax.servlet.MultipartConfigElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Configuration
public class MultipartConfiguration {

	private final Logger log = LoggerFactory
			.getLogger(MultipartConfiguration.class);


	@Bean
	public MultipartConfigElement multipartConfigElement() {
		String maxFileSize = WebApplicationProps.getProperty(ConfigPropNames.MULTIPART_MAXFILESIZE);
		String maxRequestSize = WebApplicationProps.getProperty(ConfigPropNames.MULTIPART_MAXREQUESTSIZE);
		log.info("maxFileSize:" + maxFileSize);
		log.info("maxRequestSize:" + maxRequestSize);

		MultipartConfigFactory factory = new MultipartConfigFactory();
		factory.setMaxFileSize(maxFileSize);
		factory.setMaxRequestSize(maxRequestSize);
		return factory.createMultipartConfig();
	}

}
