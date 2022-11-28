package it.linksmt.assatti.bpm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

@Configuration
@ImportResource("classpath*:/assatti-bpm-beans.xml")
public class BpmEngineConfiguration {

}
