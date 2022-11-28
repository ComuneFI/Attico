package it.linksmt.assatti.datalayer.domain.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class UtenteDto implements Serializable{

	private static final long serialVersionUID = 8242954283139292369L;
	
	@JsonIgnore
	private Long id;
	
    private String username;
    
    private String name;
    
    public UtenteDto() {
	}
    
    public UtenteDto(String name, String username, Long id) {
    	this.id = id;
    	this.username = username;
    	this.name = name;
	}
    
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
}
