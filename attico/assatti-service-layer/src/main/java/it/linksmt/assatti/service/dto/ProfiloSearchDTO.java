package it.linksmt.assatti.service.dto;


public class ProfiloSearchDTO {
	private String id;
	private String descrizione;
	private String delega;
	private String tipoAtto;
	private String utente;
	private String aoo;
	private String ruoli;
	private String qualificaProfessionale;
	private String stato;
	private String aooAttiva;
	private String profiloAooId;
	private String statoFuture;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getDelega() {
		return delega;
	}
	public void setDelega(String delega) {
		this.delega = delega;
	}
	public String getTipoAtto() {
		return tipoAtto;
	}
	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}
	public String getUtente() {
		return utente;
	}
	public void setUtente(String utente) {
		this.utente = utente;
	}
	
	@Override
	public String toString() {
		return "ProfiloSearchDTO [id=" + id + ", descrizione=" + descrizione
				+ ", delega=" + delega + ", tipoAtto=" + tipoAtto + ", utente="
				+ utente + ", aoo=" + aoo + ", ruoli=" + ruoli
				+ ", qualificaProfessionale=" + qualificaProfessionale
				+ ", stato=" + stato + "]";
	}
	public String getAoo() {
		return aoo;
	}
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}
	public String getQualificaProfessionale() {
		return qualificaProfessionale;
	}
	public void setQualificaProfessionale(String qualificaProfessionale) {
		this.qualificaProfessionale = qualificaProfessionale;
	}
	public String getRuoli() {
		return ruoli;
	}
	public void setRuoli(String ruoli) {
		this.ruoli = ruoli;
	}
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	
	public String getAooAttiva(){
		return this.aooAttiva;
	}
	
	public void setAooAttiva(String attiva){
		this.aooAttiva = attiva;
	}
	public String getProfiloAooId() {
		return profiloAooId;
	}
	public void setProfiloAooId(String profiloAooId) {
		this.profiloAooId = profiloAooId;
	}
	public String getStatoFuture() {
		return statoFuture;
	}
	public void setStatoFuture(String statoFuture) {
		this.statoFuture = statoFuture;
	}
}
