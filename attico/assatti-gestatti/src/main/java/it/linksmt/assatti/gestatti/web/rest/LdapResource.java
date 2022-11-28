package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.LdapService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.dto.LdapUserDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing LDAP users.
 */

@RestController
@RequestMapping("/api")
public class LdapResource {
	
	private final Logger log = LoggerFactory.getLogger(LdapResource.class);
	
	@Inject
    private LdapService ldapService;
	
	@Inject
    private UserService userService;
	
	/**
     * GET  /ldap/users -> get the LDAP users.
     */
    @RequestMapping(value = "/ldap/users",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<LdapUserDTO>> getUsers(HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get LDAP users");
	    	
	    	List<String> listaLoginIdCensiti = userService.findAllLoginId();
	    	List<String> listaLoginIdCensitiLowerCase = new ArrayList<String>();
	    	
	    	if(listaLoginIdCensiti!=null) {
	    		for (String id : listaLoginIdCensiti) {
		    		listaLoginIdCensitiLowerCase.add(id.toLowerCase());
				}
	    	}
	    	
	    	List<LdapUserDTO> listaUtentiLdap = ldapService.getAllUsers();
	    	List<LdapUserDTO> listaUtentiLdapFiltrata = new ArrayList<LdapUserDTO>();
	    	
	        if (listaUtentiLdap == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        } else {
	        	for (LdapUserDTO ldapUserDTO : listaUtentiLdap) {
	        		if(listaLoginIdCensitiLowerCase==null) {
	        			listaUtentiLdapFiltrata.add(ldapUserDTO);
					} else {
						if(!listaLoginIdCensitiLowerCase.contains(ldapUserDTO.getUserIdAttribute().toLowerCase())) {
							listaUtentiLdapFiltrata.add(ldapUserDTO);
						}
					}
	        		
				}
	        }
	        return new ResponseEntity<>(listaUtentiLdapFiltrata, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

}
