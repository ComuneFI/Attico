package it.linksmt.assatti.service.dto;

import java.util.List;
import java.util.Map;

import it.linksmt.assatti.cmis.client.model.CmisAssociationDTO;

public class DmsPermanentProperties {
	
	private String targetDocumentId;

	private List<String> secondaryObjectTypes;
	
	private Map<String, Object> documentProperties;
	
	private List<CmisAssociationDTO> associations;
	
	
	public String getTargetDocumentId() {
		return targetDocumentId;
	}

	public void setTargetDocumentId(String targetDocumentId) {
		this.targetDocumentId = targetDocumentId;
	}

	public List<String> getSecondaryObjectTypes() {
		return secondaryObjectTypes;
	}

	public void setSecondaryObjectTypes(List<String> secondaryObjectTypes) {
		this.secondaryObjectTypes = secondaryObjectTypes;
	}

	public Map<String, Object> getDocumentProperties() {
		return documentProperties;
	}

	public void setDocumentProperties(Map<String, Object> documentProperties) {
		this.documentProperties = documentProperties;
	}

	public List<CmisAssociationDTO> getAssociations() {
		return associations;
	}

	public void setAssociations(List<CmisAssociationDTO> associations) {
		this.associations = associations;
	}
}
