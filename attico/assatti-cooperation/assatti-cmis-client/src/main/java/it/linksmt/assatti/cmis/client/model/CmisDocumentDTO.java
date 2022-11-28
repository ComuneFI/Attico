package it.linksmt.assatti.cmis.client.model;

import java.io.Serializable;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * Class to contain the information about the result from the query
 * 
 * @author marco ingrosso
 */
public class CmisDocumentDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Long contentSize;
	private String id;
	private String name;
	private String description;
	private String documentUrl;	// url per visualizzare il documento nel browser
	private String downloadUrl; // url per effettuare il download del documento
	private GregorianCalendar createDate;
	private String folderId;	// nodeId della folder che contiene il documento
	
	// metadati documento
	private List<CmisMetadataDTO> metadata;
	
	// lista con gli allegati (se richiesta)
	private List<CmisDocumentDTO> attachments;
	
	// file binario del documento (se richiesto)
	private byte[] content;

	public CmisDocumentDTO(String id) {
		this.id = id;
	}

	public GregorianCalendar getCreateDate() {
		return createDate;
	}

	public void setCreateDate(GregorianCalendar createDate) {
		this.createDate = createDate;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<CmisMetadataDTO> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<CmisMetadataDTO> metadata) {
		this.metadata = metadata;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public String getDownloadUrl() {
		return downloadUrl;
	}

	public void setDownloadUrl(String downloadUrl) {
		this.downloadUrl = downloadUrl;
	}

	public String getFolderId() {
		return folderId;
	}

	public void setFolderId(String folderId) {
		this.folderId = folderId;
	}

	public List<CmisDocumentDTO> getAttachments() {
		return attachments;
	}

	public void setAttachments(List<CmisDocumentDTO> attachments) {
		this.attachments = attachments;
	}

	public byte[] getContent() {
		return content;
	}

	public void setContent(byte[] content) {
		this.content = content;
	}

	@Override
	public String toString() {
		return "id=" + this.id + "; name=" + this.name + "; description="
				+ this.description + "; created=" + this.createDate + "; documentUrl="
				+ this.documentUrl + "; downloadUrl="+this.downloadUrl;
	}

	public Long getContentSize() {
		return contentSize;
	}

	public void setContentSize(Long contentSize) {
		this.contentSize = contentSize;
	}

}
