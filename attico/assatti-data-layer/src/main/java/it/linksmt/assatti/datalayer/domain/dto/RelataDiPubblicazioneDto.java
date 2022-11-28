package it.linksmt.assatti.datalayer.domain.dto;

import it.linksmt.assatti.datalayer.domain.Aoo;

import org.joda.time.LocalDate;

public class RelataDiPubblicazioneDto {

	private String numeroAdozione;
	private LocalDate dataAdozione;
	private String codiceCifra;
	private String oggetto;
	private LocalDate inizioPubblicazioneAlbo;
	private LocalDate finePubblicazioneAlbo;
	private String nominativoResponsabilePubblicazione;
	private Aoo aooProponente;
	
	public String getNumeroAdozione() {
		return numeroAdozione;
	}
	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}
	public LocalDate getDataAdozione() {
		return dataAdozione;
	}
	public void setDataAdozione(LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
	}
	public String getCodiceCifra() {
		return codiceCifra;
	}
	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public LocalDate getInizioPubblicazioneAlbo() {
		return inizioPubblicazioneAlbo;
	}
	public void setInizioPubblicazioneAlbo(LocalDate inizioPubblicazioneAlbo) {
		this.inizioPubblicazioneAlbo = inizioPubblicazioneAlbo;
	}
	public LocalDate getFinePubblicazioneAlbo() {
		return finePubblicazioneAlbo;
	}
	public void setFinePubblicazioneAlbo(LocalDate finePubblicazioneAlbo) {
		this.finePubblicazioneAlbo = finePubblicazioneAlbo;
	}
	public String getNominativoResponsabilePubblicazione() {
		return nominativoResponsabilePubblicazione;
	}
	public void setNominativoResponsabilePubblicazione(
			String nominativoResponsabilePubblicazione) {
		this.nominativoResponsabilePubblicazione = nominativoResponsabilePubblicazione;
	}
	public Aoo getAooProponente() {
		return aooProponente;
	}
	public void setAooProponente(Aoo aooProponente) {
		this.aooProponente = aooProponente;
	}
	
	
	
	
	
}
