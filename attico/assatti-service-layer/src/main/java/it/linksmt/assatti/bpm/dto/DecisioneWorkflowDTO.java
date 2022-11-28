package it.linksmt.assatti.bpm.dto;

public class DecisioneWorkflowDTO {

	private String codiceAzioneUtente = null;
	private String descrizione = null;
	private String tipoParere = null;
	private String origineParere = null;
	private String variableSet = null;
	private String variableValue = null;
	private String descrizioneAlternativa = null;
	private Boolean sempreAttiva = false; 
	private boolean loggaAzione = false;
	
	private boolean completaTask = false;

	public DecisioneWorkflowDTO() { }

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	
	public String getDescrizioneAlternativa() {
		return descrizioneAlternativa;
	}

	public void setDescrizioneAlternativa(String descrizioneAlternativa) {
		this.descrizioneAlternativa = descrizioneAlternativa;
	}

	public String getVariableSet() {
		return variableSet;
	}

	public void setVariableSet(String variableSet) {
		this.variableSet = variableSet;
	}

	public String getVariableValue() {
		return variableValue;
	}

	public void setVariableValue(String variableValue) {
		this.variableValue = variableValue;
	}

	public String getCodiceAzioneUtente() {
		return codiceAzioneUtente;
	}

	public void setCodiceAzioneUtente(String codiceAzioneUtente) {
		this.codiceAzioneUtente = codiceAzioneUtente;
	}

	public boolean isCompletaTask() {
		return completaTask;
	}

	public void setCompletaTask(boolean completaTask) {
		this.completaTask = completaTask;
	}

	public String getTipoParere() {
		return tipoParere;
	}

	public void setTipoParere(String tipoParere) {
		this.tipoParere = tipoParere;
	}

	public String getOrigineParere() {
		return origineParere;
	}

	public void setOrigineParere(String origineParere) {
		this.origineParere = origineParere;
	}

	public boolean isLoggaAzione() {
		return loggaAzione;
	}

	public void setLoggaAzione(boolean loggaAzione) {
		this.loggaAzione = loggaAzione;
	}

	public Boolean getSempreAttiva() {
		return sempreAttiva;
	}

	public void setSempreAttiva(Boolean sempreAttiva) {
		this.sempreAttiva = sempreAttiva;
	}
}
