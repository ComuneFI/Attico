package it.linksmt.assatti.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import it.linksmt.assatti.utility.StringUtil;

import java.util.Collection;

/**
 * Utility class for Spring Security.
 */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    /**
     * Get the login of the current user.
     */
    public static String getCurrentLogin() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
                
                if (authentication.getPrincipal() instanceof CustomUserDetails) {
                	CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
                	if (!StringUtil.isNull(customUser.getUsernameImpersonificato())) {
                		 userName = customUser.getUsernameImpersonificato();
                	}
                }
            }
            else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }

    /**
     * Restituisce l'utente originale del sistema quando viene utilizzata l'impersonificazione 
     *  
     */
    public static String getPrimitiveLogin() {
    	SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        UserDetails springSecurityUser = null;
        String userName = null;
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                springSecurityUser = (UserDetails) authentication.getPrincipal();
                userName = springSecurityUser.getUsername();
            }
            else if (authentication.getPrincipal() instanceof String) {
                userName = (String) authentication.getPrincipal();
            }
        }
        return userName;
    }
    
    
    /**
     * Check if a user is authenticated.
     *
     * @return true if the user is authenticated, false otherwise
     */
    public static boolean isAuthenticated() {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Collection<? extends GrantedAuthority> authorities = securityContext.getAuthentication().getAuthorities();
        if (authorities != null) {
            for (GrantedAuthority authority : authorities) {
                if (authority.getAuthority().equals(AuthoritiesConstants.ANONYMOUS)) {
                    return false;
                }
            }
        }
        return true;
    }
    
    public static String getUsernameImpersonificato() {
    	SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof CustomUserDetails) {
            	CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            	return customUser.getUsernameImpersonificato();
            }
        }
        
        return null;
    }
    
    
    public static Long getProfiloIdImpersonificato() {
    	SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof CustomUserDetails) {
            	CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            	return customUser.getProfiloIdImpersonificato();
            }
        }
        
        return null;
    }


    /**
     * If the current user has a specific security role.
     */
    public static boolean isUserInRole(String role) {
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        if(authentication != null) {
            if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                return springSecurityUser.getAuthorities().contains(new SimpleGrantedAuthority(role));
            }
        }
        return false;
    }

	public static void setImpUsername(String impUtente) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof CustomUserDetails) {
            	CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            	customUser.setUsernameImpersonificato(impUtente);
            }
        }
	}

	public static void setImpProfiloId(long profiloId) {
		SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();
        
        if(authentication != null) {
            if(authentication.getPrincipal() instanceof CustomUserDetails) {
            	CustomUserDetails customUser = (CustomUserDetails) authentication.getPrincipal();
            	customUser.setProfiloIdImpersonificato(profiloId);
            }
        }
	}
}
