package it.linksmt.assatti.datalayer.domain;


public interface IDestinatarioInterno {
	public String getClassName();
	public String getDescrizioneAsDestinatario();
	public Indirizzo getIndirizzo();
	public String getFax();
	public String getEmail();
	public String getPec();
	public Long getId();
}
