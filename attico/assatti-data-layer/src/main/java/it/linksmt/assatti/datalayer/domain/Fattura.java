package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;
import it.linksmt.assatti.datalayer.domain.util.ValutaCustomSerializer;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A Fattura.
 */
@Entity
@Table(name = "FATTURA")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class Fattura implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 7891943186010498064L;

	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @Column(name = "numero_fattura")
    private String numeroFattura;
    
    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
    @Column(name = "data_fattura")
    private LocalDate dataFattura;
    
    @Type(type = "java.math.BigDecimal")
    @Column(name = "importo_fattura")
    @JsonSerialize(using = ValutaCustomSerializer.class)
    private BigDecimal importo;

    @Column(name = "numero_registro")
    private String numeroRegistro;

    @Column(name = "progressivo_registro")
    private String progressivoRegistro;
    
    @Column(name="causale")
    private String causale;
    
    @ManyToOne(fetch=FetchType.LAZY, optional=false)
	@JoinColumn(name="beneficiario_id", insertable=true, updatable = false)
    private Beneficiario beneficiario;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNumeroFattura() {
		return numeroFattura;
	}

	public void setNumeroFattura(String numeroFattura) {
		this.numeroFattura = numeroFattura;
	}

	public LocalDate getDataFattura() {
		return dataFattura;
	}

	public void setDataFattura(LocalDate dataFattura) {
		this.dataFattura = dataFattura;
	}

	public BigDecimal getImporto() {
		return importo;
	}

	public void setImporto(BigDecimal importo) {
		this.importo = importo;
	}

	public String getNumeroRegistro() {
		return numeroRegistro;
	}

	public void setNumeroRegistro(String numeroRegistro) {
		this.numeroRegistro = numeroRegistro;
	}

	public String getProgressivoRegistro() {
		return progressivoRegistro;
	}

	public void setProgressivoRegistro(String progressivoRegistro) {
		this.progressivoRegistro = progressivoRegistro;
	}

	public Beneficiario getBeneficiario() {
		return beneficiario;
	}

	public void setBeneficiario(Beneficiario beneficiario) {
		this.beneficiario = beneficiario;
	}

	public String getCausale() {
		return causale;
	}

	public void setCausale(String causale) {
		this.causale = causale;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((beneficiario == null) ? 0 : beneficiario.hashCode());
		result = prime * result
				+ ((dataFattura == null) ? 0 : dataFattura.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((importo == null) ? 0 : importo.hashCode());
		result = prime * result
				+ ((numeroFattura == null) ? 0 : numeroFattura.hashCode());
		result = prime * result
				+ ((numeroRegistro == null) ? 0 : numeroRegistro.hashCode());
		result = prime
				* result
				+ ((progressivoRegistro == null) ? 0 : progressivoRegistro
						.hashCode());
		result = prime
				* result
				+ ((causale == null) ? 0 : causale
						.hashCode());
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
		Fattura other = (Fattura) obj;
		if (beneficiario == null) {
			if (other.beneficiario != null)
				return false;
		} else if (!beneficiario.equals(other.beneficiario))
			return false;
		if (dataFattura == null) {
			if (other.dataFattura != null)
				return false;
		} else if (!dataFattura.equals(other.dataFattura))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (importo == null) {
			if (other.importo != null)
				return false;
		} else if (!importo.equals(other.importo))
			return false;
		if (numeroFattura == null) {
			if (other.numeroFattura != null)
				return false;
		} else if (!numeroFattura.equals(other.numeroFattura))
			return false;
		if (numeroRegistro == null) {
			if (other.numeroRegistro != null)
				return false;
		} else if (!numeroRegistro.equals(other.numeroRegistro))
			return false;
		if (progressivoRegistro == null) {
			if (other.progressivoRegistro != null)
				return false;
		} else if (!progressivoRegistro.equals(other.progressivoRegistro))
			return false;
		if (causale == null) {
			if (other.causale != null)
				return false;
		} else if (!causale.equals(other.causale))
			return false;
		return true;
	}
}
