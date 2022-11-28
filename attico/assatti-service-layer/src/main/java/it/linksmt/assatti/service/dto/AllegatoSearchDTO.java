package it.linksmt.assatti.service.dto;

import java.io.Serializable;

public class AllegatoSearchDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String fileName;
	private Boolean omissis;
	private String link;
	private String titolo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public Boolean getOmissis() {
		return omissis;
	}
	public void setOmissis(Boolean omissis) {
		this.omissis = omissis;
	}
	public String getLink() {
		return link;
	}
	public void setLink(String link) {
		this.link = link;
	}
	public String getTitolo() {
		return titolo;
	}
	public void setTitolo(String titolo) {
		this.titolo = titolo;
	}
}
