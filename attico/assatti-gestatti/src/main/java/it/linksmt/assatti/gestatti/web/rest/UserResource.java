package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.datalayer.repository.UserRepository;
import com.codahale.metrics.annotation.Timed;

/**
 * REST controller for managing users.
 */
@RestController
@RequestMapping("/api")
public class UserResource {

    private final Logger log = LoggerFactory.getLogger(UserResource.class);

    @Inject
    private UserRepository userRepository;

    /**
     * GET  /users -> get all users.
     */
    @RequestMapping(value = "/users",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public List<User> getAll() throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get all Users");
	        return userRepository.findAll();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /users/:login -> get the "login" user.
     */
    @RequestMapping(value = "/users/{login}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public User getUser(@PathVariable String login, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get User : {}", login);
	        User user = userRepository.findOneByLogin(login);
	        if (user == null) {
	            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	        }
	        return user;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
