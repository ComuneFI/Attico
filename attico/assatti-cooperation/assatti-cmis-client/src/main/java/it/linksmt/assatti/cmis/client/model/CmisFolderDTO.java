package it.linksmt.assatti.cmis.client.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Folder di alfresco.
 * 
 * @author marco ingrosso
 */
public class CmisFolderDTO implements Serializable {

	private static final long serialVersionUID = 1L;

	private String id;
	private String parentId;
	private String name;
	private String description;
	private String path;
	private Date creationDate;
	
	// metadati folder
	private List<CmisMetadataDTO> metadata;

	public CmisFolderDTO() {
		// empty contructor
	}

	public CmisFolderDTO(String id) {
		this.id = id;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
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

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getParentId() {
		return parentId;
	}

	public void setParentId(String parentId) {
		this.parentId = parentId;
	}

	@Override
	public String toString() {
		return "id=" + this.id + "; name=" + this.name + "; description="
				+ this.description + "; created=" + this.creationDate.getTime() + "; url="
				+ this.path;
	}

	public List<CmisMetadataDTO> getMetadata() {
		return metadata;
	}

	public void setMetadata(List<CmisMetadataDTO> metadata) {
		this.metadata = metadata;
	}

}
