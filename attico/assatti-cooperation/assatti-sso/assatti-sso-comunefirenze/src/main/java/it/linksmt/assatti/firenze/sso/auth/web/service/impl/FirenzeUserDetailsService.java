package it.linksmt.assatti.firenze.sso.auth.web.service.impl;

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
@Component("firenzeUserDetailsService")
public class FirenzeUserDetailsService implements UserDetailsService {

	private final Logger log = LoggerFactory.getLogger(FirenzeUserDetailsService.class.getName());

	@Inject
	private UserService userService;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		log.debug("Authenticating {}", username);

		User userFromDatabase = userService.findOneByLogin(username); // userRepository.findOneByLogin(lowercaseLogin);
		if (userFromDatabase == null) {
			log.error("User " + username + " not found in the database");
			throw new UsernameNotFoundException("User " + username + " not found in the database");
		}
		else if (!userFromDatabase.getActivated()) {
			log.error("User " + username + " not activated");
			throw new UserNotActivatedException("User " + username + " not activated");
		}

		Collection<GrantedAuthority> grantedAuthorities = new ArrayList<>();
		try {
			for (Authority authority : userFromDatabase.getAuthorities()) {
				GrantedAuthority grantedAuthority = new SimpleGrantedAuthority(authority.getName());
				grantedAuthorities.add(grantedAuthority);
			}
		}
		catch (Exception e) {
			log.error("unable to get granted authorities:: " + e.getMessage(), e);
		}

		/*
		 * la password non ha importanza in quanto le credenziali vengono verificate dal sistema
		 * esterno SSO La password (non esistente sul db nel caso di autenticazione SSO) può essere
		 * valorizzato con lo stesso valore di username
		 */
		String password = username;

		CustomUserDetails user = new CustomUserDetails(username, password, grantedAuthorities);

		return user;
	}

}
