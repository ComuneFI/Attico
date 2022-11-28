package it.linksmt.assatti.cooperation.albo.recuperoinfo.dto;

public class ResponseInfoDto {
	private Boolean success;
	private String progressivo;
	private String anno;
	private String datainizio;
	private String datafine;
	private String urlrelata;
	private String message;
	
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getProgressivo() {
		return progressivo;
	}
	public void setProgressivo(String progressivo) {
		this.progressivo = progressivo;
	}
	public String getAnno() {
		return anno;
	}
	public void setAnno(String anno) {
		this.anno = anno;
	}
	public String getDatainizio() {
		return datainizio;
	}
	public void setDatainizio(String datainizio) {
		this.datainizio = datainizio;
	}
	public String getDatafine() {
		return datafine;
	}
	public void setDatafine(String datafine) {
		this.datafine = datafine;
	}
	public String getUrlrelata() {
		return urlrelata;
	}
	public void setUrlrelata(String urlrelata) {
		this.urlrelata = urlrelata;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	
	@Override
	public String toString() {
		return "ResponseInfoDto [success=" + success + ", progressivo=" + progressivo + ", anno=" + anno
				+ ", datainizio=" + datainizio + ", datafine=" + datafine + ", urlrelata=" + urlrelata + ", message="
				+ message + "]";
	}
	
}
