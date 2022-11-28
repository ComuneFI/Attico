package it.linksmt.assatti.datalayer.domain.dto;

import java.io.Serializable;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoSerie;

public class DocumentoPdfDto  implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -482831183944754180L;
	private Long id;
	private String nomeFile;
	private String annoIstruzione;
	private Aoo aooSerie;
	private TipoDocumentoSerie tipoDocumentoSerie;
	private TipoDocumento tipoDocumento;
	
	public DocumentoPdfDto(Long id, String nomeFile, String annoIstruzione, Aoo aooSerie, TipoDocumentoSerie tipoDocumentoSerie, TipoDocumento tipoDocumento) {
		this.id = id;
		this.nomeFile = nomeFile;
		this.annoIstruzione = annoIstruzione;
		this.aooSerie = aooSerie;
		this.tipoDocumentoSerie = tipoDocumentoSerie;
		this.tipoDocumento = tipoDocumento;
	}
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getNomeFile() {
		return nomeFile;
	}
	public void setNomeFile(String nomeFile) {
		this.nomeFile = nomeFile;
	}
	public String getAnnoIstruzione() {
		return annoIstruzione;
	}
	public void setAnnoIstruzione(String annoIstruzione) {
		this.annoIstruzione = annoIstruzione;
	}
	public Aoo getAooSerie() {
		return aooSerie;
	}
	public void setAooSerie(Aoo aooSerie) {
		this.aooSerie = aooSerie;
	}
	public TipoDocumentoSerie getTipoDocumentoSerie() {
		return tipoDocumentoSerie;
	}
	public void setTipoDocumentoSerie(TipoDocumentoSerie tipoDocumentoSerie) {
		this.tipoDocumentoSerie = tipoDocumentoSerie;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}
	
}
