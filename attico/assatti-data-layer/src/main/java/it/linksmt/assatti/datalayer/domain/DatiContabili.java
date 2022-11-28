package it.linksmt.assatti.datalayer.domain;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomBigDecimalDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;
import it.linksmt.assatti.datalayer.domain.util.ValutaCustomSerializer;

@Entity
@Table(name = "DATICONTABILI")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class DatiContabili {

	@Id
	@Column(name = "atto_id", insertable = true, updatable = false)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="atto_id", insertable = true, updatable = false)
	private Atto atto;
	
	@Type(type = "java.math.BigDecimal")
    @Column(name = "importo_entrata")
    @JsonSerialize(using = ValutaCustomSerializer.class)
	@JsonDeserialize(using = CustomBigDecimalDeserializer.class)
    private BigDecimal importoEntrata;
	
	@Type(type = "java.math.BigDecimal")
    @Column(name = "importo_uscita")
    @JsonSerialize(using = ValutaCustomSerializer.class)
	@JsonDeserialize(using = CustomBigDecimalDeserializer.class)
    private BigDecimal importoUscita;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_scadenza")
	private LocalDate dataScadenza;
	
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_arrivo_ragioneria")
	private DateTime dataArrivoRagioneria;
	
	@Column(name = "num_arrivi_ragioneria")
	private Integer numArriviRagioneria;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
    @JsonSerialize(using = CustomDateTimeSerializer.class)
    @JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_ultimo_invio")
	private DateTime dataUltimoInvio;
	
	@Column(name = "dati_ricevuti")
	private Boolean datiRicevuti;
	
	@Column(name = "includi_movimenti_atto", columnDefinition = "tinyint(4) default 0")
	private Boolean includiMovimentiAtto;
	
	@Column(name = "nascondi_beneficiari_movimenti_atto", columnDefinition = "tinyint(4) default 0")
	private Boolean nascondiBeneficiariMovimentiAtto;
	
	@Column(name = "trasformazione_warning")
	private Boolean trasformazioneWarning;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "tipo_bilancio")
	private TipoBilancioEnum tipoBilancio;
	
	@Transient
	@JsonProperty
	private BigDecimal differenzaImportoEU;
	
	@Transient
	@JsonProperty
	private String importoEntrataFormattato;
	
	@Transient
	@JsonProperty
	private String importoUscitaFormattato;
	
	@JsonIgnore
	private String daticontabili;

	public DatiContabili() {
	}
	
	public DatiContabili(boolean includiMovimentiAtto) {
		this.includiMovimentiAtto = includiMovimentiAtto;
		this.nascondiBeneficiariMovimentiAtto = false;
	}
	
	public DatiContabili(Long attoId) {
		this.includiMovimentiAtto = false;
		this.nascondiBeneficiariMovimentiAtto = false;
		this.id = attoId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDaticontabili() {
		return daticontabili;
	}

	public void setDaticontabili(String daticontabili) {
		this.daticontabili = daticontabili;
	}

	public BigDecimal getImportoEntrata() {
		return importoEntrata;
	}

	public void setImportoEntrata(BigDecimal importoEntrata) {
		this.importoEntrata = importoEntrata;
	}

	public BigDecimal getImportoUscita() {
		return importoUscita;
	}

	public void setImportoUscita(BigDecimal importoUscita) {
		this.importoUscita = importoUscita;
	}

	public LocalDate getDataScadenza() {
		return dataScadenza;
	}

	public void setDataScadenza(LocalDate dataScadenza) {
		this.dataScadenza = dataScadenza;
	}

	public DateTime getDataArrivoRagioneria() {
		return dataArrivoRagioneria;
	}

	public void setDataArrivoRagioneria(DateTime dataArrivoRagioneria) {
		this.dataArrivoRagioneria = dataArrivoRagioneria;
	}
	
	public Integer getNumArriviRagioneria() {
		return numArriviRagioneria;
	}

	public void setNumArriviRagioneria(Integer numArriviRagioneria) {
		this.numArriviRagioneria = numArriviRagioneria;
	}

	public DateTime getDataUltimoInvio() {
		return dataUltimoInvio;
	}

	public void setDataUltimoInvio(DateTime dataUltimoInvio) {
		this.dataUltimoInvio = dataUltimoInvio;
	}

	public Boolean getDatiRicevuti() {
		return datiRicevuti;
	}

	public void setDatiRicevuti(Boolean datiRicevuti) {
		this.datiRicevuti = datiRicevuti;
	}

	public Boolean getIncludiMovimentiAtto() {
		return includiMovimentiAtto;
	}

	public void setIncludiMovimentiAtto(Boolean includiMovimentiAtto) {
		this.includiMovimentiAtto = includiMovimentiAtto;
	}

	public TipoBilancioEnum getTipoBilancio() {
		return tipoBilancio;
	}

	public void setTipoBilancio(TipoBilancioEnum tipoBilancio) {
		this.tipoBilancio = tipoBilancio;
	}

	public BigDecimal getDifferenzaImportoEU() {
		BigDecimal diff = null;
		BigDecimal entrata = BigDecimal.ZERO;
		BigDecimal uscita = BigDecimal.ZERO;
		if(this.importoEntrata != null) {
			entrata = this.importoEntrata;
		}
		if(this.importoUscita != null) {
			uscita = this.importoUscita;
		}
		diff = entrata.subtract(uscita);
		return diff;
	}
	
	

	public void setImportoEntrataFormattato(String importoEntrataFormattato) {
		this.importoEntrataFormattato = importoEntrataFormattato;
	}

	public String getImportoEntrataFormattato() {
		return importoEntrataFormattato;
	}

	public String getImportoUscitaFormattato() {
		return importoUscitaFormattato;
	}

	public void setImportoUscitaFormattato(String importoUscitaFormattato) {
		this.importoUscitaFormattato = importoUscitaFormattato;
	}

	public Boolean getTrasformazioneWarning() {
		return trasformazioneWarning;
	}

	public void setTrasformazioneWarning(Boolean trasformazioneWarning) {
		this.trasformazioneWarning = trasformazioneWarning;
	}

	public Boolean getNascondiBeneficiariMovimentiAtto() {
		return nascondiBeneficiariMovimentiAtto;
	}

	public void setNascondiBeneficiariMovimentiAtto(Boolean nascondiBeneficiariMovimentiAtto) {
		this.nascondiBeneficiariMovimentiAtto = nascondiBeneficiariMovimentiAtto;
	}
}
