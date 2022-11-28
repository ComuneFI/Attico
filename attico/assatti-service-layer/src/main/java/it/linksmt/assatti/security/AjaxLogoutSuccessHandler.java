package it.linksmt.assatti.security;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {

    public static final String BEARER_AUTHENTICATION = "Bearer ";

    @Inject
    private TokenStore tokenStore;
    
    @Inject
    private SessionRegistry sessionRegistry;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication)
            throws IOException, ServletException {

        // Request the token
        String token = request.getHeader("authorization");
        if (token != null && token.startsWith(BEARER_AUTHENTICATION)) {
            final OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(StringUtils.substringAfter(token, BEARER_AUTHENTICATION));
            OAuth2Authentication auth = tokenStore.readAuthentication(oAuth2AccessToken);
            List<SessionInformation> sessions = sessionRegistry.getAllSessions(auth.getPrincipal(), false);
    		if(sessions.size()>0) {        		
    			for(SessionInformation session : sessions){
    				session.expireNow();
    			}
    		}   
    		if (oAuth2AccessToken != null) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
