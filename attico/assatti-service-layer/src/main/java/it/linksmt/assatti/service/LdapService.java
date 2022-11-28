package it.linksmt.assatti.service;

import static org.springframework.ldap.query.LdapQueryBuilder.query;

import java.io.UnsupportedEncodingException;
import java.util.List;

import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.ldap.core.AttributesMapper;
import org.springframework.ldap.core.DistinguishedName;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.service.dto.LdapUserDTO;
import it.linksmt.assatti.utility.configuration.LdapProps;

@Service
@Transactional
public class LdapService {

	@Autowired(required = true)
    @Qualifier(value = "ldapTemplate")
    private LdapTemplate ldapTemplate;
	
	private final Logger log = LoggerFactory.getLogger(LdapService.class);

	@Transactional(readOnly=true)
	public List<LdapUserDTO> getAllUsers() {
		SearchControls controls = new SearchControls();
        controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
        return ldapTemplate.search("", "(objectclass=person)", controls, new UserAttributesMapper());
       
	}

	@Transactional(readOnly=true)
	public LdapUserDTO getUserDetails(String userName) {
		log.info("executing {getUserDetails}");
        List<LdapUserDTO> list = ldapTemplate.search(query().base("ou=users").where("uid").is(userName), new UserAttributesMapper());
        if (list != null && !list.isEmpty()) {
         return list.get(0);
        }
        return null;
	}

	@Bean
	public LdapContextSource contextSourceTarget() {
	    LdapContextSource ldapContextSource = new LdapContextSource();
	    ldapContextSource.setUrl(LdapProps.getProperty("ldap.serverUrl"));
	    ldapContextSource.setBase(LdapProps.getProperty("ldap.userSearchBase"));
	    ldapContextSource.setUserDn(LdapProps.getProperty("ldap.managerDn"));
	    ldapContextSource.setPassword(LdapProps.getProperty("ldap.managerPassword"));
	    ldapContextSource.afterPropertiesSet();

	    return ldapContextSource;
	}

	@Bean
	public LdapTemplate ldapTemplate() throws Exception {
		LdapTemplate ldapTemplate = new LdapTemplate(contextSourceTarget());
		ldapTemplate.afterPropertiesSet();
	    return ldapTemplate;

	}
	
	public static javax.naming.Name bindDN(String _x){
        @SuppressWarnings("deprecation")
		javax.naming.Name name = new DistinguishedName("uid=" + _x + ",ou=users");
        return name;
       }

       private class UserAttributesMapper implements AttributesMapper<LdapUserDTO> {

        @Override
        public LdapUserDTO mapFromAttributes(Attributes attributes) throws NamingException {
         LdapUserDTO user;
         if (attributes == null) {
          return null;
         }
         
         user = new LdapUserDTO();
         user.setUserIdAttribute(attributes.get(LdapProps.getProperty("ldap.userIdAttribute")).get().toString());
         
         if (attributes.get(LdapProps.getProperty("ldap.userLastnameAttribute")) != null) {
             user.setUserLastnameAttribute(attributes.get(LdapProps.getProperty("ldap.userLastnameAttribute")).get().toString());
            }
         
         if (attributes.get(LdapProps.getProperty("ldap.userFirstnameAttribute")) != null) {
          user.setUserFirstnameAttribute(attributes.get(LdapProps.getProperty("ldap.userFirstnameAttribute")).get().toString());
         }
         
         if (attributes.get(LdapProps.getProperty("ldap.userEmailAttribute")) != null) {
             user.setUserEmailAttribute(attributes.get(LdapProps.getProperty("ldap.userEmailAttribute")).get().toString());
            }
         
         if (attributes.get("userPassword") != null) {
             String userPassword = null;
             try {
              userPassword = new String((byte[]) attributes.get("userPassword").get(), "UTF-8");
             } catch (UnsupportedEncodingException e) {
              log.error("unable to process", e);
             }
             user.setUserPassword(userPassword);
            }

         return user;
        }
       }

       private class SingleAttributesMapper implements AttributesMapper<String> {

        @Override
        public String mapFromAttributes(Attributes attrs) throws NamingException {
         Attribute cn = attrs.get("cn");
         return cn.toString();
        }
       }

       private class MultipleAttributesMapper implements AttributesMapper<String> {

        @Override
        public String mapFromAttributes(Attributes attrs) throws NamingException {
         NamingEnumeration<? extends Attribute> all = attrs.getAll();
         StringBuffer result = new StringBuffer();
         result.append("\n Result { \n");
         while (all.hasMore()) {
          Attribute id = all.next();
          result.append(" \t |_  #" + id.getID() + "= [ " + id.get() + " ]  \n");
          log.info(id.getID() + "\t | " + id.get());
         }
         result.append("\n } ");
         return result.toString();
        }
       }
    
}
