package it.linksmt.assatti.fdr.dto;

public class FirmaRemotaResponseDTO {
	
	private String fileName;
	private byte[] document;
	private byte[] attachment;
	
	public byte[] getDocument() {
		return document;
	}
	public void setDocument(byte[] document) {
		this.document = document;
	}
	public byte[] getAttachment() {
		return attachment;
	}
	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
}
