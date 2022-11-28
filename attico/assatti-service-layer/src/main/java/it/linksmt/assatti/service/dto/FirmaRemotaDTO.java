package it.linksmt.assatti.service.dto;

import java.util.List;

import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

public class FirmaRemotaDTO {
	
	private String codiceFiscale;
	private String password;
	private String otp;
	private List<Long> filesId;
	private List<Boolean> filesOmissis;
	private List<Boolean> filesAdozione;
	private List<Long> filesParereId;
	private List<Boolean> filesScheda;
	private List<Boolean> filesAttoInesistente;
	private List<Boolean> filesRelataPubblicazione;
	private Long modelloHtmlId;
	private Long modelloHtmlIdSchedaAnagraficoContabile;
	private String errorMessage;
	private String errorCode;
	private String codiceTipoDocumento;
	@JsonIgnore
	private List<DocumentoPdf> documentiFirmati;
	@JsonIgnore
	private List<DocumentoInformatico> marcature;
	
	private String numeroAdozione;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozione;
	
	public FirmaRemotaDTO() {
	}

	public FirmaRemotaDTO(String errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

	public List<Long> getFilesId() {
		return filesId;
	}

	public void setFilesId(List<Long> filesId) {
		this.filesId = filesId;
	}
	public List<Boolean> getFilesOmissis() {
		return filesOmissis;
	}

	public void setFilesOmissis(List<Boolean> filesOmissis) {
		this.filesOmissis = filesOmissis;
	}

	public List<Boolean> getFilesAdozione() {
		return filesAdozione;
	}

	public void setFilesAdozione(List<Boolean> filesAdozione) {
		this.filesAdozione = filesAdozione;
	}

	public List<Long> getFilesParereId() {
		return filesParereId;
	}

	public void setFilesParereId(List<Long> filesParereId) {
		this.filesParereId = filesParereId;
	}

	public List<Boolean> getFilesScheda() {
		return filesScheda;
	}

	public void setFilesScheda(List<Boolean> filesScheda) {
		this.filesScheda = filesScheda;
	}

	public List<Boolean> getFilesAttoInesistente() {
		return filesAttoInesistente;
	}

	public void setFilesAttoInesistente(List<Boolean> filesAttoInesistente) {
		this.filesAttoInesistente = filesAttoInesistente;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getOtp() {
		return otp;
	}

	public void setOtp(String otp) {
		this.otp = otp;
	}

	public Long getModelloHtmlId() {
		return modelloHtmlId;
	}

	public void setModelloHtmlId(Long modelloHtmlId) {
		this.modelloHtmlId = modelloHtmlId;
	}

	public Long getModelloHtmlIdSchedaAnagraficoContabile() {
		return modelloHtmlIdSchedaAnagraficoContabile;
	}

	public void setModelloHtmlIdSchedaAnagraficoContabile(Long modelloHtmlIdSchedaAnagraficoContabile) {
		this.modelloHtmlIdSchedaAnagraficoContabile = modelloHtmlIdSchedaAnagraficoContabile;
	}

	public String getNumeroAdozione() {
		return numeroAdozione;
	}

	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}

	public LocalDate getDataAdozione() {
		return dataAdozione;
	}

	public void setDataAdozione(LocalDate localDate) {
		this.dataAdozione = localDate;
	}

	public List<DocumentoPdf> getDocumentiFirmati() {
		return documentiFirmati;
	}

	public void setDocumentiFirmati(List<DocumentoPdf> documentiFirmati) {
		this.documentiFirmati = documentiFirmati;
	}

	public List<DocumentoInformatico> getMarcature() {
		return marcature;
	}

	public void setMarcature(List<DocumentoInformatico> marcature) {
		this.marcature = marcature;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public void setErrorMessage(String errorMessage) {
		this.errorMessage = errorMessage;
	}

	public String getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(String errorCode) {
		this.errorCode = errorCode;
	}

	public List<Boolean> getFilesRelataPubblicazione() {
		return filesRelataPubblicazione;
	}

	public void setFilesRelataPubblicazione(List<Boolean> filesRelataPubblicazione) {
		this.filesRelataPubblicazione = filesRelataPubblicazione;
	}

	public String getCodiceTipoDocumento() {
		return codiceTipoDocumento;
	}

	public void setCodiceTipoDocumento(String codiceTipoDocumento) {
		this.codiceTipoDocumento = codiceTipoDocumento;
	}
	
	

}
