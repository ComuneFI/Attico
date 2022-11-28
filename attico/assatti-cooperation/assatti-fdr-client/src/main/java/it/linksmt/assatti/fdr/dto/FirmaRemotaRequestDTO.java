package it.linksmt.assatti.fdr.dto;

public class FirmaRemotaRequestDTO {

	private String fileName;
	private byte[] document;
	
	public FirmaRemotaRequestDTO(String fileName, byte[] document) {
		this.fileName = fileName;
		this.document = document;
	}
	
	public byte[] getDocument() {
		return document;
	}
	public void setDocument(byte[] document) {
		this.document = document;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
