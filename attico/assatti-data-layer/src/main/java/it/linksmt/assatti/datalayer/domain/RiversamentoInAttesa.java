package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * Un riversamento in attesa.
 */
@Entity
@Table(name = "riversamento_in_attesa")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RiversamentoInAttesa implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name="documentopdf_id",insertable=true, updatable=false)
    private DocumentoPdf documentoPdf;
    
    @ManyToOne
    @JoinColumn(name="attachment_to_document_id",insertable=true, updatable=false)
    private DocumentoPdf attachmentToDocument;
    
    @ManyToOne
    @JoinColumn(name="documentoinformatico_id",insertable=true, updatable=false)
    private DocumentoInformatico documentoInformatico;
    
    @ManyToOne
    @JoinColumn(name="configurazione_riversamento_id",insertable=true, updatable=false)
    private ConfigurazioneRiversamento configurazioneRiversamento;
    
    @Column(name = "oggetti_da_riversare")
    private Integer oggettiDaRiversare;
    
    @Column(name = "as_attachment")
    private Boolean asAttachment;
    
    @Column(name = "aoo_id")
    private Long aooId;
    
    @Column(name = "fascicolo_rup_id")
    private String fascicoloRupId;

	public Long getId() {
		return id;
	}

	public DocumentoPdf getDocumentoPdf() {
		return documentoPdf;
	}

	public DocumentoPdf getAttachmentToDocument() {
		return attachmentToDocument;
	}

	public DocumentoInformatico getDocumentoInformatico() {
		return documentoInformatico;
	}

	public ConfigurazioneRiversamento getConfigurazioneRiversamento() {
		return configurazioneRiversamento;
	}

	public Integer getOggettiDaRiversare() {
		return oggettiDaRiversare;
	}

	public Boolean getAsAttachment() {
		return asAttachment;
	}

	public Long getAooId() {
		return aooId;
	}

	public String getFascicoloRupId() {
		return fascicoloRupId;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public void setDocumentoPdf(DocumentoPdf documentoPdf) {
		this.documentoPdf = documentoPdf;
	}

	public void setAttachmentToDocument(DocumentoPdf attachmentToDocument) {
		this.attachmentToDocument = attachmentToDocument;
	}

	public void setDocumentoInformatico(DocumentoInformatico documentoInformatico) {
		this.documentoInformatico = documentoInformatico;
	}

	public void setConfigurazioneRiversamento(ConfigurazioneRiversamento configurazioneRiversamento) {
		this.configurazioneRiversamento = configurazioneRiversamento;
	}

	public void setOggettiDaRiversare(Integer oggettiDaRiversare) {
		this.oggettiDaRiversare = oggettiDaRiversare;
	}

	public void setAsAttachment(Boolean asAttachment) {
		this.asAttachment = asAttachment;
	}

	public void setAooId(Long aooId) {
		this.aooId = aooId;
	}

	public void setFascicoloRupId(String fascicoloRupId) {
		this.fascicoloRupId = fascicoloRupId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((aooId == null) ? 0 : aooId.hashCode());
		result = prime * result + ((asAttachment == null) ? 0 : asAttachment.hashCode());
		result = prime * result + ((attachmentToDocument == null) ? 0 : attachmentToDocument.hashCode());
		result = prime * result + ((configurazioneRiversamento == null) ? 0 : configurazioneRiversamento.hashCode());
		result = prime * result + ((documentoInformatico == null) ? 0 : documentoInformatico.hashCode());
		result = prime * result + ((documentoPdf == null) ? 0 : documentoPdf.hashCode());
		result = prime * result + ((fascicoloRupId == null) ? 0 : fascicoloRupId.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((oggettiDaRiversare == null) ? 0 : oggettiDaRiversare.hashCode());
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
		RiversamentoInAttesa other = (RiversamentoInAttesa) obj;
		if (aooId == null) {
			if (other.aooId != null)
				return false;
		} else if (!aooId.equals(other.aooId))
			return false;
		if (asAttachment == null) {
			if (other.asAttachment != null)
				return false;
		} else if (!asAttachment.equals(other.asAttachment))
			return false;
		if (attachmentToDocument == null) {
			if (other.attachmentToDocument != null)
				return false;
		} else if (!attachmentToDocument.equals(other.attachmentToDocument))
			return false;
		if (configurazioneRiversamento == null) {
			if (other.configurazioneRiversamento != null)
				return false;
		} else if (!configurazioneRiversamento.equals(other.configurazioneRiversamento))
			return false;
		if (documentoInformatico == null) {
			if (other.documentoInformatico != null)
				return false;
		} else if (!documentoInformatico.equals(other.documentoInformatico))
			return false;
		if (documentoPdf == null) {
			if (other.documentoPdf != null)
				return false;
		} else if (!documentoPdf.equals(other.documentoPdf))
			return false;
		if (fascicoloRupId == null) {
			if (other.fascicoloRupId != null)
				return false;
		} else if (!fascicoloRupId.equals(other.fascicoloRupId))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (oggettiDaRiversare == null) {
			if (other.oggettiDaRiversare != null)
				return false;
		} else if (!oggettiDaRiversare.equals(other.oggettiDaRiversare))
			return false;
		return true;
	}
    
}
