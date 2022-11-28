package it.linksmt.assatti.service;

public class FileUploadException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public FileUploadException(String mes) {
		super(mes);
	}
	
	public FileUploadException(String mes, Exception ex) {
		super(mes, ex);
	}
}
