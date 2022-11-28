package it.linksmt.assatti.gestatti.web.rest;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

@RestController
@RequestMapping("/api")
public class KeepAliveResource {

    private final Logger log = LoggerFactory.getLogger(KeepAliveResource.class);

    @Inject
    private UserService userService;
    
    private Map<String, LocalDate> aliveUsers = new HashMap<String, LocalDate>();
    

    @RequestMapping(value = "/keepAlive", method = RequestMethod.PUT)
	public ResponseEntity<Void> keepAlive(
			@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	String user = SecurityUtils.getPrimitiveLogin();
	    	if(user!=null && !user.trim().isEmpty()) {
	    		aliveUsers.put(user, LocalDate.now());
	    	}
			return new ResponseEntity<Void>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    private void cancellaSessione(String username) {
    	//if passa troppo tempo
    	userService.logoutUser(username);
    }
}
