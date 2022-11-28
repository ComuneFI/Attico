package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

/**
 * A TipoDocumento.
 */
public class TipoDocumentoDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private Long idTipoDocumento;

	private String descrizione;

	private String codice;

	private Boolean riversamentoTipoatto;
	
	private String dmsContentType;
	
	private List<ModelloHtmlDto> elencoModelloHtml;

	/*
	 * Get & Set
	 */

	public Long getIdTipoDocumento() {
		return idTipoDocumento;
	}

	public void setIdTipoDocumento(Long idTipoDocumento) {
		this.idTipoDocumento = idTipoDocumento;
	}

	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public Boolean getRiversamentoTipoatto() {
		return riversamentoTipoatto;
	}

	public void setRiversamentoTipoatto(Boolean riversamentoTipoatto) {
		this.riversamentoTipoatto = riversamentoTipoatto;
	}

	public String getDmsContentType() {
		return dmsContentType;
	}

	public void setDmsContentType(String dmsContentType) {
		this.dmsContentType = dmsContentType;
	}

	public List<ModelloHtmlDto> getElencoModelloHtml() {
		return elencoModelloHtml;
	}

	public void setElencoModelloHtml(List<ModelloHtmlDto> elencoModelloHtml) {
		this.elencoModelloHtml = elencoModelloHtml;
	}

}
