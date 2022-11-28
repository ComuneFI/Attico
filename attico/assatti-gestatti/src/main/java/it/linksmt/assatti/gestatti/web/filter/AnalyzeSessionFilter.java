package it.linksmt.assatti.gestatti.web.filter;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;

import it.linksmt.assatti.security.AuthoritiesConstants;

public class AnalyzeSessionFilter implements Filter {

	private SessionRegistry sessionRegistry;

	private boolean active;

	public AnalyzeSessionFilter(SessionRegistry sessionRegistry, boolean active) {
		this.sessionRegistry = sessionRegistry;
		this.active = active;
    }

    public void destroy() {
	}
    
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		
		if(active) {
			Authentication currentUser = SecurityContextHolder.getContext().getAuthentication();
			if(currentUser!=null && !currentUser.getAuthorities().contains(AuthoritiesConstants.ADMIN)) {
				
				for(Object principal : sessionRegistry.getAllPrincipals()) {
					List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
					if(sessions.size()>1) {        		
						String username = ((org.springframework.security.core.userdetails.User)principal).getUsername();
						
						if(currentUser.getName().equalsIgnoreCase(username)) {
							HttpServletResponse res = (HttpServletResponse)response;
							SecurityContextHolder.clearContext();
							if (res.isCommitted() == false) {
								res.sendError(HttpServletResponse.SC_FORBIDDEN);
								return;
							}
							
						}
					}        		
				}
				
			}
		}
		
		// pass the request along the filter chain
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig fConfig) throws ServletException {
	}

}
