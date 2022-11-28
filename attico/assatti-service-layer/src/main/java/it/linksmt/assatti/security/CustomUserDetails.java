package it.linksmt.assatti.security;

import java.util.Collection;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

public class CustomUserDetails extends User {

	private static final long serialVersionUID = 1588405199309085552L;

	private String usernameImpersonificato;
	private Long profiloIdImpersonificato;

	public CustomUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
		super(username, password, authorities);
	}

	public String getUsernameImpersonificato() {
		return usernameImpersonificato;
	}

	public void setUsernameImpersonificato(String usernameImpersonificato) {
		this.usernameImpersonificato = usernameImpersonificato;
	}

	public Long getProfiloIdImpersonificato() {
		return profiloIdImpersonificato;
	}

	public void setProfiloIdImpersonificato(Long profiloIdImpersonificato) {
		this.profiloIdImpersonificato = profiloIdImpersonificato;
	}
}
