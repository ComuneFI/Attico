package it.linksmt.assatti.bpm.dto;

public class AssegnazioneIncaricoDTO {
	
	private String codiceConfigurazione = null;
	private String varAssegnatario = null;
	
	public AssegnazioneIncaricoDTO() { }
	
	public String getCodiceConfigurazione() {
		return codiceConfigurazione;
	}
	public void setCodiceConfigurazione(String codiceConfigurazione) {
		this.codiceConfigurazione = codiceConfigurazione;
	}
	public String getVarAssegnatario() {
		return varAssegnatario;
	}
	public void setVarAssegnatario(String varAssegnatario) {
		this.varAssegnatario = varAssegnatario;
	}
}
