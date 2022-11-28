package it.linksmt.assatti.service;

import it.linksmt.assatti.datalayer.domain.Authority;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.datalayer.repository.AuthorityRepository;
import it.linksmt.assatti.datalayer.repository.OAuthTokenRepository;
import it.linksmt.assatti.datalayer.repository.UserRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.util.RandomUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UserService {

	private final Logger log = LoggerFactory.getLogger(UserService.class);

	@Inject
	private PasswordEncoder passwordEncoder;

	@Inject
	private UserRepository userRepository;

	@Inject
	private AuthorityRepository authorityRepository;
	
	@Inject
	private AuthenticationManager authenticationManager;
	
    @Autowired
	private SessionRegistry sessionRegistry;
    
    @Inject
    private TokenStore tokenStore;
    
    public static final String BEARER_AUTHENTICATION = "Bearer ";
    
    @Inject
    private OAuthTokenRepository oAuthTokenRepository;

	public User activateRegistration(String key) {
		log.debug("Activating user for activation key {}", key);
		User user = userRepository.findOneByActivationKey(key);
		// activate given user for the registration key.
		if (user != null) {
			user.setActivated(true);
			user.setActivationKey(null);
			if(user.getVersion()==null) {
				user.setVersion(new Integer(1));
			}
			userRepository.save(user);
			log.debug("Activated user: {}", user);
		}
		return user;
	}
	
	public boolean existUsername(String username){
		return userRepository.findOneByLogin(username)!=null;
	}
	public User getUser(String username) {
		return userRepository.findOneByLogin(username);
	}
	
	@Transactional
	public void updateAuthoritiesOfUser(Long userId, List<String> authorities){
		userRepository.deleteAuthoritiesOfUser(userId);
		if(authorities!=null){
			for(String authority : authorities){
				userRepository.addAuthorityToUser(userId, authority);
			}
		}
	}

	public User findOneByLogin(String login) {
		log.debug("Activating user for activateRegistrationInternal key {}",
				login);
		User user = userRepository.findOneByLogin(login);
		return user;
	}
	
	public List<String> findAllLoginId(){
		List<String> listLogin = userRepository.findAllLoginId();
		return listLogin;
	}

	public void save(User user) {
		if(user.getVersion()==null) {
			user.setVersion(new Integer(1));
		}
		user =  userRepository.save(user);
		log.debug("user:"+user);
	}

	public User activateRegistrationInternal(String login) {
		log.debug("Activating user for activateRegistrationInternal key {}",
				login);
		User user = userRepository.findOneByLogin(login);
		// activate given user for the registration key.
		if (user != null) {
			user.setActivated(true);
			user.setActivationKey(null);
			if(user.getVersion()==null) {
				user.setVersion(new Integer(1));
			}
			userRepository.save(user);
			log.debug("Activated user: {}", user);
		}
		return user;
	}
	
	public User disableRegistrationInternal(String login) {
		log.debug("Disabling user for activateRegistrationInternal key {}",
				login);
		User user = userRepository.findOneByLogin(login);
		// activate given user for the registration key.
		if (user != null) {
			user.setActivated(false);
			user.setActivationKey(null);
			if(user.getVersion()==null) {
				user.setVersion(new Integer(1));
			}
			userRepository.save(user);
			log.debug("Disabled user: {}", user);
		}
		return user;
	}

	public User completePasswordReset(String newPassword, String key) {
		log.debug("Reset user password for reset key {}", key);
		User user = userRepository.findOneByResetKey(key);
		DateTime oneDayAgo = DateTime.now().minusHours(24);
		if (user != null) {
			if (user.getResetDate().isAfter(oneDayAgo.toInstant().getMillis())) {
				user.setActivated(true);
				user.setPassword(passwordEncoder.encode(newPassword));
				user.setResetKey(null);
				user.setResetDate(null);
				if(user.getVersion()==null) {
					user.setVersion(new Integer(1));
				}
				userRepository.save(user);
				return user;
			} else {
				return null;
			}
		}
		return null;
	}

	public User requestPasswordReset(String mail) {
		User user = userRepository.findOneByEmail(mail);
		if (user != null) {
			user.setResetKey(RandomUtil.generateResetKey());
			user.setResetDate(DateTime.now());
			if(user.getVersion()==null) {
				user.setVersion(new Integer(1));
			}
			userRepository.save(user);
			return user;
		}
		return user;
	}

	public User createUserInformation(String login, String password,
			String firstName, String lastName, String email, String langKey) {

		User newUser = new User();
		Authority authority = authorityRepository.findOne("ROLE_USER");
		Set<Authority> authorities = new HashSet<>();
		String encryptedPassword = passwordEncoder.encode(password);
		newUser.setLogin(login);
		// new user gets initially a generated password
		newUser.setPassword(encryptedPassword);
		newUser.setFirstName(firstName);
		newUser.setLastName(lastName);
		newUser.setEmail(email);
		newUser.setLangKey(langKey);
		// new user is not active
		newUser.setActivated(false);
		// new user gets registration key
		newUser.setActivationKey(RandomUtil.generateActivationKey());
		authorities.add(authority);
		newUser.setAuthorities(authorities);
		newUser.setVersion(new Integer(1));
		userRepository.save(newUser);
		log.debug("Created Information for User: {}", newUser);
		return newUser;
	}

	public void updateUserInformation(String firstName, String lastName,
			String email) {
		User currentUser = userRepository.findOneByLogin(SecurityUtils
				.getCurrentLogin());
		currentUser.setFirstName(firstName);
		currentUser.setLastName(lastName);
		currentUser.setEmail(email);
		userRepository.save(currentUser);
		log.debug("Changed Information for User: {}", currentUser);
	}

	public void changePassword(String password) {
		User currentUser = userRepository.findOneByLogin(SecurityUtils
				.getCurrentLogin());
		String encryptedPassword = passwordEncoder.encode(password);
		currentUser.setPassword(encryptedPassword);
		userRepository.save(currentUser);
		log.debug("Changed password for User: {}", currentUser);
	}
	
	public void changePassword(String username, String password) {
		User user = userRepository.findOneByLogin(username);
		String encryptedPassword = passwordEncoder.encode(password);
		user.setPassword(encryptedPassword);
		userRepository.save(user);
		log.debug("Changed password for User: {}", user);
	}

	@Transactional(readOnly = true)
	public User getUserWithAuthorities() {
		String currentLogin = SecurityUtils.getCurrentLogin();
		User currentUser = userRepository.findOneByLogin(currentLogin);
		if(currentUser!=null) {
			if(currentUser.getAuthorities()!=null) {
				currentUser.getAuthorities().size(); // eagerly load the association
			}
		}
		return currentUser;
	}

	/**
	 * Not activated users should be automatically deleted after 3 days.
	 * <p/>
	 * <p>
	 * This is scheduled to get fired everyday, at 01:00 (am).
	 * </p>
	 */
	// @Scheduled(cron = "0 0 1 * * ?")
	public void removeNotActivatedUsers() {
		DateTime now = new DateTime();
		List<User> users = userRepository
				.findAllByActivatedIsFalseAndCreatedDateBefore(now.minusDays(3));
		for (User user : users) {
			log.debug("Deleting not activated user {}", user.getLogin());
			userRepository.delete(user);
		}
	}
	
	public boolean isLoggedUserAnAdmin(){
		User user = this.getUserWithAuthorities();
		if (user == null) {
			return false;
		}else{
	        List<String> roles = new ArrayList<>();
	        for (Authority authority : user.getAuthorities()) {
	            roles.add(authority.getName());
	        }
	        if(roles.contains(AuthoritiesConstants.ADMIN)){
	        	return true;
	        }else{
	        	return false;
	        }
		}
	}
	
	public boolean isLoggedUserAnAdminRP(){
		User user = this.getUserWithAuthorities();
		if (user == null) {
			return false;
		}else{
	        List<String> roles = new ArrayList<>();
	        for (Authority authority : user.getAuthorities()) {
	            roles.add(authority.getName());
	        }
	        if(roles.contains(AuthoritiesConstants.AMMINISTRATORE_RP)){
	        	return true;
	        }else{
	        	return false;
	        }
		}
	}

    public Boolean isCredenzialiCorrette(String username, String password){
    	Boolean corrette = null;
    	try{
	    	UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
	        authenticationManager.authenticate(token);
	        corrette = true;
    	}
	    catch (AuthenticationException e){
	    	corrette = false;
	    }
    	return corrette;
    }
    
    public void logoutUser(String username) {
    	oAuthTokenRepository.deleteTokenByUsername(username);
        List<SessionInformation> sessions = sessionRegistry.getAllSessions(username, false);
		if(sessions.size()>0) {        		
			for(SessionInformation session : sessions){
				session.expireNow();
			}
		}
    }

	public boolean isAccountLogged(String username, String password, boolean logout, HttpServletRequest request, HttpServletResponse response) {
		try
	    {
	        UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(username, password);
	        Authentication auth = authenticationManager.authenticate(token);
	        
	        if(logout) {
	            oAuthTokenRepository.deleteTokenByUsername(username);
                List<SessionInformation> sessions = sessionRegistry.getAllSessions(auth.getPrincipal(), false);
        		if(sessions.size()>0) {        		
        			for(SessionInformation session : sessions){
        				session.expireNow();
        			}
        		}   
	        	
	        }
	        else if(auth.isAuthenticated()) {
	        	for(Object principal : sessionRegistry.getAllPrincipals()) {
	        		org.springframework.security.core.userdetails.User user = (org.springframework.security.core.userdetails.User)principal;
	        		List<SessionInformation> sessions = sessionRegistry.getAllSessions(user, false);
	         		if(!sessions.isEmpty()) {        		
	         			String loggedUsername = user.getUsername();
		        		if(loggedUsername.equalsIgnoreCase(username)) {
		        			return true;
		        		}
	         		} 
	        	}        	
	        }
	        return false;
	        
	    }
	    catch (AuthenticationException e){
	        return false;
	    }
		
	}

}
