package it.linksmt.assatti.service.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

/**
 * Domanil class for ConfigurazioneIncaricoAoo.
 */
public class ConfigurazioneIncaricoAooDto implements Serializable, Comparable<ConfigurazioneIncaricoAooDto> {

	private static final long serialVersionUID = 1L;

	private Long idConfigurazioneIncarico;
	private Long idAoo;
	private String descrizioneAoo;
	private DateTime dataCreazione;
	
	private Integer ordineFirma;

	private QualificaProfessionaleDto qualificaProfessionaleDto;
	
	private Integer giorniScadenza;
	private String dataScadenza;
	private DateTime dataManuale;
	
	@Override
	public int compareTo(ConfigurazioneIncaricoAooDto b) {
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

	public Long getIdAoo() {
		return idAoo;
	}

	public void setIdAoo(Long idAoo) {
		this.idAoo = idAoo;
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

	public String getDescrizioneAoo() {
		return descrizioneAoo;
	}

	public void setDescrizioneAoo(String descrizioneAoo) {
		this.descrizioneAoo = descrizioneAoo;
	}

	public Integer getGiorniScadenza() {
		return giorniScadenza;
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

	public DateTime getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(DateTime dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public DateTime getDataManuale() {
		return dataManuale;
	}

	public void setDataManuale(DateTime dataManuale) {
		this.dataManuale = dataManuale;
	}
}
