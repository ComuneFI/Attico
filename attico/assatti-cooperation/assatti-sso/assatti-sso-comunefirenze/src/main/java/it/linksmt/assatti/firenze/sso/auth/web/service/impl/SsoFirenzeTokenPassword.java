package it.linksmt.assatti.firenze.sso.auth.web.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;

import it.linksmt.assatti.firenze.sso.auth.web.filter.SsoAuthenticationFilter;
import it.linksmt.assatti.utility.StringUtil;

public class SsoFirenzeTokenPassword implements PasswordEncoder {
	
	private static final Logger log = LoggerFactory.getLogger(SsoFirenzeTokenPassword.class);

	@Override
	public String encode(CharSequence rawPassword) {
		return rawPassword.toString();
	}

	@Override
	public boolean matches(CharSequence token, String username) {
		try {
			String usernameVal = SsoAuthenticationFilter.checkLoginSso(token.toString());
			log.info("SSO LOGIN: " + StringUtil.trimStr(username).equals(usernameVal));
			return StringUtil.trimStr(username).equals(usernameVal);
		}
		catch (Exception e) {
			log.error("SsoFirenzeTokenPassword :: " + e.getMessage(), e);
			return false;
		}
	}

}
