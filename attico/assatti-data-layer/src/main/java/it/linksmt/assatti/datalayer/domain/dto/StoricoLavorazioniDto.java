package it.linksmt.assatti.datalayer.domain.dto;

public class StoricoLavorazioniDto {
	private Long idAtto;
	private String lavorazione;
	private String oggetto;
	private String dataAdozione;
	private String numeroAdozione;
	private String tipoProvvedimento;
	
	public Long getIdAtto() {
		return idAtto;
	}
	public void setIdAtto(Long idAtto) {
		this.idAtto = idAtto;
	}
	public String getLavorazione() {
		return lavorazione;
	}
	public void setLavorazione(String lavorazione) {
		this.lavorazione = lavorazione;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getDataAdozione() {
		return dataAdozione;
	}
	public void setDataAdozione(String dataAdozione) {
		this.dataAdozione = dataAdozione;
	}
	public String getNumeroAdozione() {
		return numeroAdozione;
	}
	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}
	public String getTipoProvvedimento() {
		return tipoProvvedimento;
	}
	public void setTipoProvvedimento(String tipoProvvedimento) {
		this.tipoProvvedimento = tipoProvvedimento;
	}
	
}
