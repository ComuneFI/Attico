package it.linksmt.assatti.gestatti.web.filter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.GenericFilterBean;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

public class CustomSecuritykeyBasedFilter extends GenericFilterBean {
	
	private Logger log = LoggerFactory.getLogger(CustomSecuritykeyBasedFilter.class.getName());
	
	private ProfiloService profiloService;
	
	private static String SECURITY_SEPARATOR = "#!@";
    
    public CustomSecuritykeyBasedFilter(ProfiloService profiloService) {
		this.profiloService = profiloService;
	}

	@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		boolean securityEnabled = true;
		try{
			securityEnabled = Boolean.parseBoolean(WebApplicationProps.getProperty("security_appkey_enabled", "true"));
		}catch(Exception e) {
			log.warn("securityEnabled error " + e.getMessage());
		}
		if(!securityEnabled) {
			chain.doFilter(request, response);
			return;
		}
		
		List<Object> freePathPost = WebApplicationProps.getPropertyList("security_appkey_freepath_post");
		List<Object> freePathGet = WebApplicationProps.getPropertyList("security_appkey_freepath_get");
		
    	HttpServletRequest httpReq = (HttpServletRequest)request;
    	HttpServletResponse httpRes = (HttpServletResponse)response;
    	Enumeration<String> headerProperties = httpReq.getHeaderNames();
    	List<String> headerProps = new ArrayList<String>();
    	while(headerProperties.hasMoreElements()) {
    		headerProps.add(headerProperties.nextElement());
    	}
    	String requestPath = httpReq.getRequestURI();
    	if(!requestPath.startsWith("/api/manutenzione/") && !requestPath.toLowerCase().contains("oauth/token") && !requestPath.contains("/lastprofile") && !(httpReq.getMethod().equalsIgnoreCase("get") && 
    			((requestPath.startsWith("/api/utentes/") && requestPath.contains("/getAoosDirigente")) || 
    			requestPath.contains("/activeprofilos") || requestPath.startsWith("/api/profilos/") || requestPath.startsWith("/api/configurations") || requestPath.startsWith("/api/account") || requestPath.startsWith("/api/tipoMaterias/active")
    			|| (requestPath.contains("/reportpdf/") && requestPath.contains("/endSearch"))
    			)
    	)) {
    		
    		if(freePathGet!=null && freePathGet.size() > 0 && httpReq.getMethod().equalsIgnoreCase("get")) {
    			for(Object freePath : freePathGet) {
    				String strPath = ((String)freePath).trim().toLowerCase();
    				if(!strPath.trim().isEmpty() && requestPath.toLowerCase().startsWith(strPath)) {
    					chain.doFilter(request, response);
    					return;
    				}
    			}
    		}
    		if(freePathPost!=null && freePathPost.size() > 0 && httpReq.getMethod().equalsIgnoreCase("post")) {
    			for(Object freePath : freePathPost) {
    				String strPath = ((String)freePath).trim().toLowerCase();
    				if(!strPath.trim().isEmpty() && requestPath.toLowerCase().startsWith(strPath)) {
    					chain.doFilter(request, response);
    					return;
    				}
    			}
    		}
    		
	    	String securitykey = httpReq.getHeader("appkey");
	    	String originPath = httpReq.getHeader("originPath");
	    	String profiloid = httpReq.getHeader("profiloid");
	    	
	    	String username = null;
	    	String impProfilo = httpReq.getHeader("imp_profiloid");
	    	if (!StringUtil.isNull(impProfilo)) {
	    		try {
	    			Long.parseLong(impProfilo);
	    			
	    			long idProfiloImp = Long.parseLong(profiloid);
	    			SecurityUtils.setImpProfiloId(idProfiloImp);
	    			
	    			Profilo profObj = profiloService.findOne(idProfiloImp);
	    			if (profObj != null) {
	    				username = profObj.getUtente().getUsername();
	    				SecurityUtils.setImpUsername(username);
	    			}
	    		}
	    		catch (Exception e) {
	    			log.error("Errore di lettura del profiloId.", e);
				}
	    	}
	    	
	    	if (StringUtil.isNull(username)) {
	    		username = SecurityUtils.getCurrentLogin();
//		    	String username = currentUser != null && !currentUser.isEmpty() ? currentUser : httpReq.getHeader("user");
	    	}
	    	
	    	if(username!=null) {
	    		username = username.toLowerCase();
	    	}
	    	
	    	if(requestPath.startsWith("/api/")) {
	    		//in caso di download/preview di documenti
	    		if(originPath == null) {
	    			originPath = requestPath;
	    			profiloid = "";
	    			if(securitykey==null) {
	    				securitykey = httpReq.getParameter("appkey");
	    			}
	    		}
	    		if(username!=null && !username.isEmpty() && originPath!=null && !originPath.isEmpty() && securitykey!=null && !securitykey.isEmpty()) {
    				String encrypted = this.encrypt(originPath + SECURITY_SEPARATOR + username + profiloid);
	    			if(encrypted!=null) {
	    				encrypted = encrypted.replaceAll("/", "").replaceAll("=", "");
		    			if(encrypted.equals(securitykey)) {
		    				chain.doFilter(request, response);
		    			}else {
		    				httpRes.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid_appkey");
		    			}
	    			}else {
	    				httpRes.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid_appkey");
	    			}
	    		//}else if(username==null || username.isEmpty()){
	    		//	chain.doFilter(request, response);
	    		}else {
	    			httpRes.sendError(HttpServletResponse.SC_UNAUTHORIZED, "invalid_appkey");
	    		}
	    	}else {
	    		chain.doFilter(request, response);
	    	}
    	}else {
    		chain.doFilter(request, response);
    	}
    }
    
    private String encrypt(String strToEncrypt)
    {
        try
        {
        	return Base64.encodeBase64String(strToEncrypt.getBytes("UTF-8"));
        }
        catch (Exception e)
        {
        	log.error("Errore di cifratura dei dati.", e);
        }
        return null;
    }
}