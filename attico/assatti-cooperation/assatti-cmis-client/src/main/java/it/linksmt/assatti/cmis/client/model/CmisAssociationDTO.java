package it.linksmt.assatti.cmis.client.model;

public class CmisAssociationDTO {

	private String associationType; 
	private String sourceNodeId; 
	private String targetNodeId;
	
	public String getAssociationType() {
		return associationType;
	}
	public void setAssociationType(String associationType) {
		this.associationType = associationType;
	}
	public String getSourceNodeId() {
		return sourceNodeId;
	}
	public void setSourceNodeId(String sourceNodeId) {
		this.sourceNodeId = sourceNodeId;
	}
	public String getTargetNodeId() {
		return targetNodeId;
	}
	public void setTargetNodeId(String targetNodeId) {
		this.targetNodeId = targetNodeId;
	}
}
