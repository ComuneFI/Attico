package it.linksmt.assatti.service.dto;

import java.io.Serializable;

/**
 * Domani class: ImpegnoDocumentoLiquidazioneDto.
 */
public class ImpegnoDocumentoLiquidazioneDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long id;

	private Integer anno;
	
	private String numero;
	
	private String sub;
	
	private String oggetto;

//	@Override
//	public boolean equals(final Object o) {
//		if (this == o) {
//			return true;
//		}
//		if (o == null || getClass() != o.getClass()) {
//			return false;
//		}
//
//		ImpegnoDocumentoLiquidazioneDto tipoAtto = (ImpegnoDocumentoLiquidazioneDto) o;
//
//		if (!Objects.equals(id, tipoAtto.id)) {
//			return false;
//		}
//
//		return true;
//	}
//
//	@Override
//	public int hashCode() {
//		return Objects.hashCode(id);
//	}
	

	/*
	 * Get & Set
	 */

	public Long getId() {
		return id;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public Integer getAnno() {
		return anno;
	}

	public void setAnno(Integer anno) {
		this.anno = anno;
	}

	public String getNumero() {
		return numero;
	}

	public void setNumero(String numero) {
		this.numero = numero;
	}

	public String getOggetto() {
		return oggetto;
	}

	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}

	public String getSub() {
		return sub;
	}

	public void setSub(String sub) {
		this.sub = sub;
	}

}
