package it.linksmt.assatti.gestatti.config;

import javax.inject.Inject;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import it.linksmt.assatti.gestatti.web.filter.CustomSecuritykeyBasedFilter;
import it.linksmt.assatti.security.AjaxLogoutSuccessHandler;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.security.Http401UnauthorizedEntryPoint;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Configuration
public class OAuth2ServerConfiguration {

    @Configuration
    @EnableResourceServer
    protected static class ResourceServerConfiguration extends ResourceServerConfigurerAdapter {

        @Inject
        private Http401UnauthorizedEntryPoint authenticationEntryPoint;

        @Inject
        private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;
        
        @Autowired
    	private SessionRegistry sessionRegistry;
        
        @Autowired
    	private ProfiloService profiloService;

        @Override
        public void configure(HttpSecurity http) throws Exception {
            http
            	.addFilterAfter(new CustomSecuritykeyBasedFilter(profiloService), BasicAuthenticationFilter.class)
                .exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint)
            .and()
                .logout()
                .logoutUrl("/api/logout")
                .logoutSuccessHandler(ajaxLogoutSuccessHandler)
            .and()
                .csrf()
                .requireCsrfProtectionMatcher(new AntPathRequestMatcher("/oauth/authorize"))
                .disable()
                .headers()
                .frameOptions().disable()
            .and()
            	.authorizeRequests()
                .antMatchers("/api/authenticate").permitAll()
                .antMatchers("/api/uploadmoduloregistrazione").permitAll()
                .antMatchers("/api/utentes/checkstato/**").permitAll()
                .antMatchers("/api/register").permitAll()
                .antMatchers("/api/configurations").permitAll()
                .antMatchers("/api/toggle").permitAll()
                .antMatchers("/api/attos/open").permitAll()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/files/manuale").permitAll()
                .antMatchers("/api/logs/**").hasAnyAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api/**").authenticated()
                .antMatchers("/websocket/tracker").hasAuthority(AuthoritiesConstants.ADMIN)
//                .antMatchers("/websocket/**").hasAuthority(AuthoritiesConstants.USER)
                .antMatchers("/websocket/**").denyAll()
                .antMatchers("/metrics/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/health/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/dump/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/shutdown/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/beans/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/configprops/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP)
                .antMatchers("/info/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/autoconfig/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/env/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/trace/**").hasAuthority(AuthoritiesConstants.ADMIN)
                .antMatchers("/api-docs/**").hasAnyAuthority(AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP)
                .antMatchers("/protected/**").authenticated()
            .and()
	            .sessionManagement()
	            .maximumSessions(1)
//	            .expiredUrl("/expired")
//	            .maxSessionsPreventsLogin(true)
//	            .sessionManagement()
//	            .maximumSessions(-1)
	            .expiredUrl("/")
	            .sessionRegistry(sessionRegistry);
        }
    }
    
    @Configuration
    @EnableAuthorizationServer
    protected static class AuthorizationServerConfiguration extends AuthorizationServerConfigurerAdapter {

        @Inject
        private DataSource dataSource;

        @Bean
        public TokenStore tokenStore() {
            return new JdbcTokenStore(dataSource);
        }

        @Inject
        @Qualifier("authenticationManagerBean")
        private AuthenticationManager authenticationManager;

        @Override
        public void configure(AuthorizationServerEndpointsConfigurer endpoints)
                throws Exception {

            endpoints
                    .tokenStore(tokenStore())
                    .authenticationManager(authenticationManager);
        }

        @Override
        public void configure(ClientDetailsServiceConfigurer clients) throws Exception {
        	
        	clients
            .inMemory()
            .withClient(WebApplicationProps.getProperty(ConfigPropNames.AUTHENTICATION_OAUTH_CLIENTID))
            .scopes("read", "write")
            .authorities(AuthoritiesConstants.AMMINISTRATORE_RP, AuthoritiesConstants.ADMIN, AuthoritiesConstants.USER)
            .authorizedGrantTypes("password", "refresh_token")
            .secret(WebApplicationProps.getProperty(ConfigPropNames.AUTHENTICATION_OAUTH_SECRET))
            .accessTokenValiditySeconds(Integer.parseInt(WebApplicationProps.getProperty(ConfigPropNames.AUTHENTICATION_OAUTH_TOKENVALIDITYINSECONDS)));
            
        }

        
    }
}
