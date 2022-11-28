package it.linksmt.assatti.datalayer.domain;

import java.io.Serializable;

import javax.persistence.CascadeType;
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
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeSerializer;

/**
 * A DocumentoInformatico.
 */
@Entity
@Table(name = "DOCUMENTOPDF")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DocumentoPdf extends AbstractAuditingEntity implements Serializable, ICmisDoc  {

	private static final long serialVersionUID = 1L;


	public DocumentoPdf() {
	}
	
    public DocumentoPdf(Long id) {
		this.id = id;
	}
    
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
	
	@Column(name = "numero_protocollo",insertable=true, updatable=false)
    private String numeroProtocollo;
	
	@Column(name = "data_protocollo",insertable=true, updatable=false)
	private String dataProtollo;
    
    @Column(name = "atto_id" ,insertable=true, updatable=false)
    private Long attoId;
    
    @ManyToOne
    @JoinColumn(name="tipo_documento_id",insertable=true, updatable=false)
    private TipoDocumento tipoDocumento;
    
    @Column(name = "atto_scheda_anagrafico_contabile", insertable=true, updatable=false)
    private Long attoSchedaAnagraficoContabileId;
    
    @Column(name = "atto_report_iter", insertable=true, updatable=false)
    private Long attoReportIterId;
    
    @Column(name = "atto_relata_pubblicazione", insertable=true, updatable=false)
    private Long attoRelataPubblicazioneId;
    
    @Column(name = "atto_adozione_id", insertable=true, updatable=false)
    private Long attoAdozioneId;
    
    @Column(name = "atto_omissis_id", insertable=true, updatable=false)
    private Long attoOmissisId;
    
    @Column(name = "atto_adozione_omissis_id", insertable=true, updatable=false)
    private Long attoAdozioneOmissisId;

    @Column(name = "parere_id", insertable=true, updatable=false)
    private Long parereId;

    @Column(name = "resoconto_id", insertable=true, updatable=false)
    private Long resocontoId;

    @Column(name = "lettera_id", insertable=true, updatable=false)
    private Long letteraId;

    @Column(name = "verbale_id", insertable=true, updatable=false)
    private Long verbaleId;
  
    @Column(name = "ordinegiorno_id", insertable=true, updatable=false)
    private Long ordineGiornoId;

	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "data_firma")
	private LocalDateTime dataFirma;
	
    @Column(name="firmato" , insertable=true, updatable=false)
    private Boolean  firmato;
    
    @Column(name="firmatodatutti")
    private Boolean  firmatodatutti;
    
    @Column(name="completo" , insertable=true, updatable=false)
    private Boolean  completo;
    
    @Column(name="pareremodificato" , insertable=true, updatable=false)
    private Boolean  pareremodificato;
   
  	@Column(name="firmatario", insertable=true, updatable=false)
    private String firmatario;
  	
  	@Column(name="firmatario_delegante", insertable=true, updatable=false)
    private String firmatarioDelegante;
  
  	@Column(name="impronta", insertable=true, updatable=false)
    private String impronta;
    
    @ManyToOne(cascade = { CascadeType.PERSIST })
    @JoinColumn(name="file_id",insertable=true, updatable=false)
    private File file;
    
    @Column(name = "aoo_id", insertable=true, updatable=false)
    private Long aooSerieId;

    
	public Long getId() {
		return id;
	}

	public String getImpronta() {
		return impronta;
	}

	public void setImpronta(String impronta) {
		this.impronta = impronta;
	}

	public void setId(Long id) {
		this.id = id;
	}


	public Boolean getFirmato() {
		return firmato;
	}

	public void setFirmato(Boolean firmato) {
		this.firmato = firmato;
	}


	public LocalDateTime getDataFirma() {
		return dataFirma;
	}

	public void setDataFirma(LocalDateTime dataFirma) {
		this.dataFirma = dataFirma;
	}

	public String getFirmatario() {
		return firmatario;
	}

	public void setFirmatario(String firmatario) {
		this.firmatario = firmatario;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		DocumentoPdf other = (DocumentoPdf) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "DocumentoPdf [id=" + id +    ", filename=" + (file!=null && file.getNomeFile()!=null ? file.getNomeFile() : "empty") +  "]";
	}

	public String getNumeroProtocollo() {
		return numeroProtocollo;
	}

	public void setNumeroProtocollo(String numeroProtocollo) {
		this.numeroProtocollo = numeroProtocollo;
	}

	public String getDataProtollo() {
		return dataProtollo;
	}

	public void setDataProtollo(String dataProtollo) {
		this.dataProtollo = dataProtollo;
	}

	public Boolean getFirmatodatutti() {
		return firmatodatutti;
	}

	public void setFirmatodatutti(Boolean firmatodatutti) {
		this.firmatodatutti = firmatodatutti;
	}

	public Boolean getPareremodificato() {
		return pareremodificato;
	}

	public void setPareremodificato(Boolean pareremodificato) {
		this.pareremodificato = pareremodificato;
	}

	public TipoDocumento getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(TipoDocumento tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Long getAttoId() {
		return attoId;
	}

	public void setAttoId(Long attoId) {
		this.attoId = attoId;
	}

	public Long getAttoSchedaAnagraficoContabileId() {
		return attoSchedaAnagraficoContabileId;
	}

	public void setAttoSchedaAnagraficoContabileId(Long attoSchedaAnagraficoContabileId) {
		this.attoSchedaAnagraficoContabileId = attoSchedaAnagraficoContabileId;
	}

	public Long getAttoReportIterId() {
		return attoReportIterId;
	}

	public void setAttoReportIterId(Long attoReportIterId) {
		this.attoReportIterId = attoReportIterId;
	}

	public Long getAttoRelataPubblicazioneId() {
		return attoRelataPubblicazioneId;
	}

	public void setAttoRelataPubblicazioneId(Long attoRelataPubblicazioneId) {
		this.attoRelataPubblicazioneId = attoRelataPubblicazioneId;
	}

	public Long getAttoAdozioneId() {
		return attoAdozioneId;
	}

	public void setAttoAdozioneId(Long attoAdozioneId) {
		this.attoAdozioneId = attoAdozioneId;
	}

	public Long getAttoOmissisId() {
		return attoOmissisId;
	}

	public void setAttoOmissisId(Long attoOmissisId) {
		this.attoOmissisId = attoOmissisId;
	}

	public Long getAttoAdozioneOmissisId() {
		return attoAdozioneOmissisId;
	}

	public void setAttoAdozioneOmissisId(Long attoAdozioneOmissisId) {
		this.attoAdozioneOmissisId = attoAdozioneOmissisId;
	}

	public Long getParereId() {
		return parereId;
	}

	public Boolean getCompleto() {
		return completo;
	}

	public void setCompleto(Boolean completo) {
		this.completo = completo;
	}

	public void setParereId(Long parereId) {
		this.parereId = parereId;
	}

	public Long getResocontoId() {
		return resocontoId;
	}

	public void setResocontoId(Long resocontoId) {
		this.resocontoId = resocontoId;
	}

	public Long getLetteraId() {
		return letteraId;
	}

	public void setLetteraId(Long letteraId) {
		this.letteraId = letteraId;
	}

	public Long getVerbaleId() {
		return verbaleId;
	}

	public void setVerbaleId(Long verbaleId) {
		this.verbaleId = verbaleId;
	}

	public Long getOrdineGiornoId() {
		return ordineGiornoId;
	}

	public void setOrdineGiornoId(Long ordineGiornoId) {
		this.ordineGiornoId = ordineGiornoId;
	}

	public Long getAooSerieId() {
		return aooSerieId;
	}

	public void setAooSerieId(Long aooSerieId) {
		this.aooSerieId = aooSerieId;
	}

	public String getFirmatarioDelegante() {
		return firmatarioDelegante;
	}

	public void setFirmatarioDelegante(String firmatarioDelegante) {
		this.firmatarioDelegante = firmatarioDelegante;
	}

	@Override
	public String getCmisDocumentId() {
		if(this.getFile()!=null && this.getFile().getCmisObjectId()!=null && !this.getFile().getCmisObjectId().trim().isEmpty()) {
			return this.getFile().getCmisObjectId().trim();
		}else {
			return null;
		}
	}
}
