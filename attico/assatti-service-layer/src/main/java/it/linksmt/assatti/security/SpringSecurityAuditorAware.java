package it.linksmt.assatti.security;

import org.springframework.data.domain.AuditorAware;
import org.springframework.stereotype.Component;

import it.linksmt.assatti.utility.Constants;

/**
 * Implementation of AuditorAware based on Spring Security.
 */
@Component
public class SpringSecurityAuditorAware implements AuditorAware<String> {

    public String getCurrentAuditor() {
        String userName = SecurityUtils.getCurrentLogin();
        return (userName != null ? userName : Constants.SYSTEM_ACCOUNT);
    }
}
