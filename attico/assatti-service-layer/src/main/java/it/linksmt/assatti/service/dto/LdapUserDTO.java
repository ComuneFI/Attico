package it.linksmt.assatti.service.dto;

public class LdapUserDTO {

	private String userIdAttribute;
	private String userLastnameAttribute;
	private String userFirstnameAttribute;
	private String userEmailAttribute;
	private String userPassword;
	
	public String getUserIdAttribute() {
		return userIdAttribute;
	}
	public void setUserIdAttribute(String userIdAttribute) {
		this.userIdAttribute = userIdAttribute;
	}
	public String getUserLastnameAttribute() {
		return userLastnameAttribute;
	}
	public void setUserLastnameAttribute(String userLastnameAttribute) {
		this.userLastnameAttribute = userLastnameAttribute;
	}
	public String getUserFirstnameAttribute() {
		return userFirstnameAttribute;
	}
	public void setUserFirstnameAttribute(String userFirstnameAttribute) {
		this.userFirstnameAttribute = userFirstnameAttribute;
	}
	public String getUserEmailAttribute() {
		return userEmailAttribute;
	}
	public void setUserEmailAttribute(String userEmailAttribute) {
		this.userEmailAttribute = userEmailAttribute;
	}
	public String getUserPassword() {
		return userPassword;
	}
	public void setUserPassword(String userPassword) {
		this.userPassword = userPassword;
	}
	

}
