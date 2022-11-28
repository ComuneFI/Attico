package it.linksmt.assatti.auth.web.service.impl;

import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Authority;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.security.CustomUserDetails;
import it.linksmt.assatti.security.UserNotActivatedException;
import it.linksmt.assatti.service.UserService;

/**
 * Concrete Product for web authoring userDetail abstract factory.
 * 
 * @author marco ingrosso
 *
 */
@Component("databaseUserDetailsService")
public class DatabaseUserDetailsService implements UserDetailsService {
	
	private Logger log = LoggerFactory.getLogger(DatabaseUserDetailsService.class.getName());
	
    @Inject
    private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.debug("Authenticating {}", username);
        String lowercaseLogin = username.toLowerCase();
        User userFromDatabase =  userService.findOneByLogin(username); //userRepository.findOneByLogin(lowercaseLogin);
        if (userFromDatabase == null) {
            throw new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database");
        } else if (!userFromDatabase.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }

        Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        try {
	        for (Authority authority : userFromDatabase.getAuthorities()) {
	            GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
	            grantedAuthorities.add(grantedAuthority);
	        }
        } catch (Exception e) {
        	log.error("Errore lettura Authority.", e);
        }
        
        CustomUserDetails user = new CustomUserDetails(lowercaseLogin,
                userFromDatabase.getPassword(), grantedAuthorities);

        return user;
	}

}
