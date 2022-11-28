package it.linksmt.assatti.service.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonIgnore;

import it.linksmt.assatti.datalayer.domain.Profilo;

/**
 * Domain class for ConfigurazioneIncaricoProfilo.
 */
public class ConfigurazioneIncaricoProfiloDto implements Serializable, Comparable<ConfigurazioneIncaricoProfiloDto> {

	private static final long serialVersionUID = 1L;

	private Long idConfigurazioneIncarico;
	private Long idProfilo;
	private DateTime dataCreazione;
	private Profilo profilo;
	
	private String descrizioneProfilo;
	private String utenteNome;
	private String utenteCognome;

	private Integer ordineFirma;
	
	private QualificaProfessionaleDto qualificaProfessionaleDto;
	
	private Integer giorniScadenza;
	private String dataScadenza;
	
	@JsonIgnore
	private Long attoId;
	
	@Override
	public int compareTo(ConfigurazioneIncaricoProfiloDto b) {
		if (this.getOrdineFirma() == null || b.getOrdineFirma() == null) {
			return 1;
		}

		return this.getOrdineFirma().compareTo(b.getOrdineFirma());
	}

	/*
	 * Get & Set
	 */

	public Long getIdConfigurazioneIncarico() {
		return idConfigurazioneIncarico;
	}

	public void setIdConfigurazioneIncarico(Long idConfigurazioneIncarico) {
		this.idConfigurazioneIncarico = idConfigurazioneIncarico;
	}

	public Long getIdProfilo() {
		return idProfilo;
	}

	public void setIdProfilo(Long idProfilo) {
		this.idProfilo = idProfilo;
	}

	public Integer getOrdineFirma() {
		return ordineFirma;
	}

	public void setOrdineFirma(Integer ordineFirma) {
		this.ordineFirma = ordineFirma;
	}

	public QualificaProfessionaleDto getQualificaProfessionaleDto() {
		return qualificaProfessionaleDto;
	}

	public void setQualificaProfessionaleDto(QualificaProfessionaleDto qualificaProfessionaleDto) {
		this.qualificaProfessionaleDto = qualificaProfessionaleDto;
	}

	public String getDescrizioneProfilo() {
		return descrizioneProfilo;
	}

	public void setDescrizioneProfilo(String descrizioneProfilo) {
		this.descrizioneProfilo = descrizioneProfilo;
	}

	public String getUtenteNome() {
		return utenteNome;
	}

	public void setUtenteNome(String utenteNome) {
		this.utenteNome = utenteNome;
	}

	public String getUtenteCognome() {
		return utenteCognome;
	}

	public void setUtenteCognome(String utenteCognome) {
		this.utenteCognome = utenteCognome;
	}

	public Integer getGiorniScadenza() {
		return giorniScadenza;
	}

	public Long getAttoId() {
		return attoId;
	}

	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}

	public void setGiorniScadenza(Integer giorniScadenza) {
		this.giorniScadenza = giorniScadenza;
	}

	public String getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(String dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public Profilo getProfilo() {
		return profilo;
	}

	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
}
