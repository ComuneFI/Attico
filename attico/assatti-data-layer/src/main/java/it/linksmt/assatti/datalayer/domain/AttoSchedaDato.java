package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.ValutaCustomSerializer;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
 
/**
 * A DocumentoInformatico.
 */
@Entity
@Table(name = "ATTO_HAS_DATIDLG33")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class AttoSchedaDato implements Serializable {

	@EmbeddedId
	private AttoSchedaDatoId id = new AttoSchedaDatoId();
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="atto_id",insertable=false, updatable=false )
	private Atto atto;
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="scheda_dato_id",insertable=false, updatable=false )
	private SchedaDato schedaDato;
	
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="scheda_id",insertable=false, updatable=false )
	private Scheda  scheda ;

	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_dato")
	private TipoDatoEnum tipoDato;
	
	@Column(name = "testo_valore",length=500)
	private String testoValore;
	
	@Column(name = "numero_valore")
	@Type(type = "java.math.BigDecimal")
	@JsonSerialize(using = ValutaCustomSerializer.class)
	private BigDecimal numeroValore;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDateTime")
	@JsonSerialize(using = CustomLocalDateTimeSerializer.class)
	@JsonDeserialize(using = CustomLocalDateTimeDeserializer.class)
	@Column(name = "data_valore")
	private LocalDateTime dataValore;
	
	@ManyToOne
	@JoinColumn(name = "file_valore_id")
	private File fileValore;
	
	@ManyToOne
	@JoinColumn(name = "beneficiario_id")
	private Beneficiario beneficiario;
	
	@Column(name = "valuta_valore")
	@Type(type = "java.math.BigDecimal")
	@JsonSerialize(using = ValutaCustomSerializer.class)
	private BigDecimal valutaValore;
	

	@Column(name = "url_valore")
	private String urlValore;
	
		public Atto getAtto() {
		return atto;
	}

	public void setAtto(Atto atto) {
		this.atto = atto;
	}

	public SchedaDato getSchedaDato() {
		return schedaDato;
	}

	public void setSchedaDato(SchedaDato schedaDato) {
		this.schedaDato = schedaDato;
	}
  
	public AttoSchedaDatoId getId() {
		return id;
	}

	public void setId(AttoSchedaDatoId id) {
		this.id = id;
	}

	public Scheda getScheda() {
		return scheda;
	}

	public void setScheda(Scheda scheda) {
		this.scheda = scheda;
	}

	public TipoDatoEnum getTipoDato() {
		return tipoDato;
	}

	public void setTipoDato(TipoDatoEnum tipoDato) {
		this.tipoDato = tipoDato;
	}

	public String getTestoValore() {
		return testoValore;
	}

	public void setTestoValore(String testoValore) {
		this.testoValore = testoValore;
	}

	public BigDecimal getNumeroValore() {
		return numeroValore;
	}

	public void setNumeroValore(BigDecimal numeroValore) {
		this.numeroValore = numeroValore;
	}


	public LocalDateTime getDataValore() {
		return dataValore;
	}

	public void setDataValore(LocalDateTime dataValore) {
		this.dataValore = dataValore;
	}

	public File getFileValore() {
		return fileValore;
	}

	public void setFileValore(File fileValore) {
		this.fileValore = fileValore;
	}

	public Beneficiario getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(Beneficiario beneficiario) {
		this.beneficiario = beneficiario;
	}

	public BigDecimal getValutaValore() {
		return valutaValore;
	}

	public void setValutaValore(BigDecimal valutaValore) {
		this.valutaValore = valutaValore;
	}

	public String getUrlValore() {
		return urlValore;
	}

	public void setUrlValore(String urlValore) {
		this.urlValore = urlValore;
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
		AttoSchedaDato other = (AttoSchedaDato) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "AttoSchedaDato [id=" + id + ", tipoDato=" + tipoDato + ", testoValore=" + testoValore
				+ ", numeroValore=" + numeroValore + ", dataValore=" + dataValore + "]";
	}
 
	 
}
