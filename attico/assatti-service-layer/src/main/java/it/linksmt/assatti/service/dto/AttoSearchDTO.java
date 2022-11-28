package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeSerializer;

public class AttoSearchDTO implements Serializable{
	private static final long serialVersionUID = 1L;
	
	private Long id;
	private String numeroAdozione;
	
	private String codiceCig;
	private String codiceCup;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate dataAdozione;
	
	private String codiceCifra;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate inizioPubblicazioneAlbo;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate finePubblicazioneAlbo;
	
	private String stato;
	private String tipoIter;
	private String tipiFinanziamento;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate dataEsecutivita;
	
	private String oggetto;
	private String aoo;
	private String createdBy;
	
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	private LocalDateTime fineIterDate;
	
	private String codicecifraAttoRevocato;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	private LocalDate dataCreazione;
	
	private String noteAvanzamento;
	
	private String createdByAvanzamento;
	
	private Boolean accessoNegato;
	private String omissisLink;
	
	/**
	 * popolato solo quando l'utente non ha i permessi di accesso all'atto
	 * contiene solo allegato pubblicabili, soltanto in versione omissis se previsto, altrimenti in versione completa
	 */
	private List<AllegatoSearchDTO> allegati;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
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
	public String getStato() {
		return stato;
	}
	public void setStato(String stato) {
		this.stato = stato;
	}
	public String getTipoIter() {
		return tipoIter;
	}
	public void setTipoIter(String tipoIter) {
		this.tipoIter = tipoIter;
	}
	public String getTipiFinanziamento() {
		return tipiFinanziamento;
	}
	public void setTipiFinanziamento(String tipiFinanziamento) {
		this.tipiFinanziamento = tipiFinanziamento;
	}
	public LocalDate getDataEsecutivita() {
		return dataEsecutivita;
	}
	public void setDataEsecutivita(LocalDate dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}
	public String getOggetto() {
		return oggetto;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public String getAoo() {
		return aoo;
	}
	public void setAoo(String aoo) {
		this.aoo = aoo;
	}
	public String getCreatedBy() {
		return createdBy;
	}
	public void setCreatedBy(String createdBy) {
		this.createdBy = createdBy;
	}
	public LocalDateTime getFineIterDate() {
		return fineIterDate;
	}
	public void setFineIterDate(LocalDateTime fineIterDate) {
		this.fineIterDate = fineIterDate;
	}
	public String getCodicecifraAttoRevocato() {
		return codicecifraAttoRevocato;
	}
	public void setCodicecifraAttoRevocato(String codicecifraAttoRevocato) {
		this.codicecifraAttoRevocato = codicecifraAttoRevocato;
	}
	public LocalDate getDataCreazione() {
		return dataCreazione;
	}
	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public String getNoteAvanzamento() {
		return noteAvanzamento;
	}
	public void setNoteAvanzamento(String noteAvanzamento) {
		this.noteAvanzamento = noteAvanzamento;
	}
	public String getOmissisLink() {
		return omissisLink;
	}
	public void setOmissisLink(String omissisLink) {
		this.omissisLink = omissisLink;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((accessoNegato == null) ? 0 : accessoNegato.hashCode());
		result = prime * result + ((aoo == null) ? 0 : aoo.hashCode());
		result = prime * result + ((codiceCifra == null) ? 0 : codiceCifra.hashCode());
		result = prime * result + ((codicecifraAttoRevocato == null) ? 0 : codicecifraAttoRevocato.hashCode());
		result = prime * result + ((createdBy == null) ? 0 : createdBy.hashCode());
		result = prime * result + ((dataAdozione == null) ? 0 : dataAdozione.hashCode());
		result = prime * result + ((dataCreazione == null) ? 0 : dataCreazione.hashCode());
		result = prime * result + ((dataEsecutivita == null) ? 0 : dataEsecutivita.hashCode());
		result = prime * result + ((fineIterDate == null) ? 0 : fineIterDate.hashCode());
		result = prime * result + ((finePubblicazioneAlbo == null) ? 0 : finePubblicazioneAlbo.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((inizioPubblicazioneAlbo == null) ? 0 : inizioPubblicazioneAlbo.hashCode());
		result = prime * result + ((noteAvanzamento == null) ? 0 : noteAvanzamento.hashCode());
		result = prime * result + ((numeroAdozione == null) ? 0 : numeroAdozione.hashCode());
		result = prime * result + ((oggetto == null) ? 0 : oggetto.hashCode());
		result = prime * result + ((omissisLink == null) ? 0 : omissisLink.hashCode());
		result = prime * result + ((stato == null) ? 0 : stato.hashCode());
		result = prime * result + ((tipoIter == null) ? 0 : tipoIter.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AttoSearchDTO other = (AttoSearchDTO) obj;
		if (accessoNegato == null) {
			if (other.accessoNegato != null)
				return false;
		} else if (!accessoNegato.equals(other.accessoNegato))
			return false;
		if (aoo == null) {
			if (other.aoo != null)
				return false;
		} else if (!aoo.equals(other.aoo))
			return false;
		if (codiceCifra == null) {
			if (other.codiceCifra != null)
				return false;
		} else if (!codiceCifra.equals(other.codiceCifra))
			return false;
		if (codicecifraAttoRevocato == null) {
			if (other.codicecifraAttoRevocato != null)
				return false;
		} else if (!codicecifraAttoRevocato.equals(other.codicecifraAttoRevocato))
			return false;
		if (createdBy == null) {
			if (other.createdBy != null)
				return false;
		} else if (!createdBy.equals(other.createdBy))
			return false;
		if (dataAdozione == null) {
			if (other.dataAdozione != null)
				return false;
		} else if (!dataAdozione.equals(other.dataAdozione))
			return false;
		if (dataCreazione == null) {
			if (other.dataCreazione != null)
				return false;
		} else if (!dataCreazione.equals(other.dataCreazione))
			return false;
		if (dataEsecutivita == null) {
			if (other.dataEsecutivita != null)
				return false;
		} else if (!dataEsecutivita.equals(other.dataEsecutivita))
			return false;
		if (fineIterDate == null) {
			if (other.fineIterDate != null)
				return false;
		} else if (!fineIterDate.equals(other.fineIterDate))
			return false;
		if (finePubblicazioneAlbo == null) {
			if (other.finePubblicazioneAlbo != null)
				return false;
		} else if (!finePubblicazioneAlbo.equals(other.finePubblicazioneAlbo))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (inizioPubblicazioneAlbo == null) {
			if (other.inizioPubblicazioneAlbo != null)
				return false;
		} else if (!inizioPubblicazioneAlbo.equals(other.inizioPubblicazioneAlbo))
			return false;
		if (noteAvanzamento == null) {
			if (other.noteAvanzamento != null)
				return false;
		} else if (!noteAvanzamento.equals(other.noteAvanzamento))
			return false;
		if (numeroAdozione == null) {
			if (other.numeroAdozione != null)
				return false;
		} else if (!numeroAdozione.equals(other.numeroAdozione))
			return false;
		if (oggetto == null) {
			if (other.oggetto != null)
				return false;
		} else if (!oggetto.equals(other.oggetto))
			return false;
		if (omissisLink == null) {
			if (other.omissisLink != null)
				return false;
		} else if (!omissisLink.equals(other.omissisLink))
			return false;
		if (stato == null) {
			if (other.stato != null)
				return false;
		} else if (!stato.equals(other.stato))
			return false;
		if (tipoIter == null) {
			if (other.tipoIter != null)
				return false;
		} else if (!tipoIter.equals(other.tipoIter))
			return false;
		return true;
	}
	
	@Override
	public String toString() {
		return "AttoSearchDTO [id=" + id + ", numeroAdozione=" + numeroAdozione + ", dataAdozione=" + dataAdozione
				+ ", codiceCifra=" + codiceCifra + ", inizioPubblicazioneAlbo=" + inizioPubblicazioneAlbo
				+ ", finePubblicazioneAlbo=" + finePubblicazioneAlbo + ", stato=" + stato + ", tipoIter=" + tipoIter
				+ ", dataEsecutivita=" + dataEsecutivita + ", oggetto=" + oggetto + ", aoo=" + aoo + ", createdBy="
				+ createdBy + ", fineIterDate=" + fineIterDate + ", codicecifraAttoRevocato=" + codicecifraAttoRevocato
				+ ", dataCreazione=" + dataCreazione + ", noteAvanzamento=" + noteAvanzamento + ", accessoNegato="
				+ accessoNegato + ", omissisLink=" + omissisLink + "]";
	}
	public String getCreatedByAvanzamento() {
		return createdByAvanzamento;
	}
	public void setCreatedByAvanzamento(String createdByAvanzamento) {
		this.createdByAvanzamento = createdByAvanzamento;
	}
	public Boolean getAccessoNegato() {
		return accessoNegato;
	}
	public void setAccessoNegato(Boolean accessoNegato) {
		this.accessoNegato = accessoNegato;
	}
	public List<AllegatoSearchDTO> getAllegati() {
		return allegati;
	}
	public void setAllegati(List<AllegatoSearchDTO> allegati) {
		this.allegati = allegati;
	}
	public String getCodiceCig() {
		return codiceCig;
	}
	public void setCodiceCig(String codiceCig) {
		this.codiceCig = codiceCig;
	}
	public String getCodiceCup() {
		return codiceCup;
	}
	public void setCodiceCup(String codiceCup) {
		this.codiceCup = codiceCup;
	}
	
}
