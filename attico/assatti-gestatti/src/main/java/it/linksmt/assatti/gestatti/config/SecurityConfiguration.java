package it.linksmt.assatti.gestatti.config;

import javax.inject.Inject;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.data.repository.query.SecurityEvaluationContextExtension;
import org.springframework.security.web.session.HttpSessionEventPublisher;

import it.linksmt.assatti.auth.web.WebAuthFactory;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
	
//	private AssattiAuthenticationManagerBuilder assattiAuthenticationManagerBuilder = getWebAuthFactory().getAssattiAuthenticationManagerBuilder();
	
    @Bean
    public WebAuthFactory getWebAuthFactory() {
        return WebAuthFactory.getWebAuthFactory();
    }
	
    @Inject
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//    	AssattiAuthenticationManagerBuilder assattiAuthenticationManagerBuilder = getWebAuthFactory().getAssattiAuthenticationManagerBuilder();
    	
    	auth = getWebAuthFactory().getAssattiAuthenticationManagerBuilder().config(auth);
	}

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
        .ignoring()
            .antMatchers("/scripts/**/*.{js,html}")
            .antMatchers("/bower_components/**")
            .antMatchers("/i18n/**")
            .antMatchers("/assets/**")
            .antMatchers("/swagger-ui/**")
            .antMatchers("/api/register")
            .antMatchers("/api/attos/open/**")
            .antMatchers("/api/toggle")
            .antMatchers("/api/configurations")
            .antMatchers("/api/utentes/checkstato/**")
            .antMatchers("/api/files/manuale")
            .antMatchers("/api/uploadmoduloregistrazione")
            .antMatchers("/api/activate")
            .antMatchers("/api/account/reset_password/init")
            .antMatchers("/api/account/reset_password/finish")
            .antMatchers("/api/account/logged")
            .antMatchers("/api/public/**")
            .antMatchers("/api/indirizzos/**")
            .antMatchers("/test/**")
        	.antMatchers("/api/assistenzatecnica/**");
    }

	@Override
	@Bean
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public SecurityEvaluationContextExtension securityEvaluationContextExtension() {
		return new SecurityEvaluationContextExtension();
	}

	@Bean
	public SessionRegistry sessionRegistry() {
		SessionRegistry sessionRegistry = new SessionRegistryImpl();
		return sessionRegistry;
	}

	@Bean
	public static HttpSessionEventPublisher httpSessionEventPublisher() {
		return new HttpSessionEventPublisher();
	}

}
