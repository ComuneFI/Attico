package it.linksmt.assatti.gestatti.config;

import org.apache.commons.lang.CharEncoding;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;

import it.linksmt.assatti.datalayer.config.DatabaseConfiguration;
import it.linksmt.assatti.datalayer.repository.ModelloHtmlRepository;
import it.linksmt.assatti.datalayer.repository.ReportRuntimeRepository;
import it.linksmt.assatti.service.util.DbLoaderTemplateResolver;
import it.linksmt.assatti.service.util.ReportRuntimeLoaderTemplateResolver;

@Configuration
@AutoConfigureAfter(value = { DatabaseConfiguration.class})
public class ThymeleafConfiguration {

    private final Logger log = LoggerFactory.getLogger(ThymeleafConfiguration.class);

    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 models")
    public ClassLoaderTemplateResolver modelliTemplateResolver() {
        ClassLoaderTemplateResolver modelliTemplateResolver = new ClassLoaderTemplateResolver();
        modelliTemplateResolver.setPrefix("modelli/");
        modelliTemplateResolver.setSuffix(".html");
        modelliTemplateResolver.setTemplateMode("HTML5");
        modelliTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
        modelliTemplateResolver.setOrder(2);
        return modelliTemplateResolver;
    }
 
    
    @Bean
    @Description("Thymeleaf template resolver serving HTML 5 emails")
    public ClassLoaderTemplateResolver emailTemplateResolver() {
        ClassLoaderTemplateResolver emailTemplateResolver = new ClassLoaderTemplateResolver();
        emailTemplateResolver.setPrefix("mails/");
        emailTemplateResolver.setSuffix(".html");
        emailTemplateResolver.setTemplateMode("HTML5");
        emailTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
        emailTemplateResolver.setOrder(1);
        return emailTemplateResolver;
    }

    @Bean
    @Description("Spring mail message resolver")
    public MessageSource emailMessageSource() {
        log.info("loading non-reloadable mail messages resources");
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        messageSource.setBasename("classpath:/mails/messages/messages");
        messageSource.setDefaultEncoding(CharEncoding.UTF_8);
        return messageSource;
    }
    
    @Bean
    @Description("Thymeleaf template resolver serving LEGACYHTML5 from db")
    public DbLoaderTemplateResolver dbTemplateResolver( ModelloHtmlRepository modelloHtmlRepository) {
    	DbLoaderTemplateResolver dbTemplateResolver = new DbLoaderTemplateResolver(modelloHtmlRepository);
    	dbTemplateResolver.setPrefix("db:");
    	dbTemplateResolver.setTemplateMode("LEGACYHTML5");
    	dbTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
    	dbTemplateResolver.setOrder(2);
    	dbTemplateResolver.setCacheable(false);
    	
        return dbTemplateResolver;
    }
    
    @Bean
    @Description("Thymeleaf template resolver serving LEGACYHTML5 from db reportruntime")
    public ReportRuntimeLoaderTemplateResolver rtTemplateResolver(ReportRuntimeRepository reportRuntimeRepository) {
    	ReportRuntimeLoaderTemplateResolver rtTemplateResolver = new ReportRuntimeLoaderTemplateResolver(reportRuntimeRepository);
    	rtTemplateResolver.setPrefix("reportruntime:");
    	rtTemplateResolver.setTemplateMode("LEGACYHTML5");
    	rtTemplateResolver.setCharacterEncoding(CharEncoding.UTF_8);
    	rtTemplateResolver.setOrder(3);
    	rtTemplateResolver.setCacheable(false);
    	
        return rtTemplateResolver;
    }
}
