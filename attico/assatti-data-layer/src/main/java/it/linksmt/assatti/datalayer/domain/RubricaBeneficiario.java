package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import org.hibernate.validator.constraints.Email;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.joda.deser.LocalDateDeserializer;

/**
 * Rubrica Beneficiario.
 */
@Entity
@Table(name = "RUBRICABENEFICIARIO")
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class RubricaBeneficiario implements Serializable {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.TABLE)
    private Long id;
	
	@Email
    @Column(name="email")
    private String email;
    
    @Email
    @Column(name="pec")
    private String pec;
    
    @Column(name = "cf_estero")
    private String codiceFiscaleEstero;
    
    @Column(name = "pi_estero")
    private String partitaIvaEstero;
    
    @Column(name = "attivo")
    private Boolean attivo;
    
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="aoo_id",insertable=true, updatable=false )
	private Aoo aoo;
    
	@Column(name = "id_ascot")
    private String idAscot;
	
	@Column(name = "iban")
	private String iban;
	
	@Enumerated(EnumType.STRING)
	@Column(name="tipo_soggetto")
	private TipoSoggettoEnum tipoSoggetto;
	
	@Column(name="cap2", length=5)
	private String cap2;
	
	@Column(name="citta1")
	private String citta1;
	
	@Column(name="citta2")
	private String citta2;
	
	@Column(name="provincia1")
	private String provincia1;
	
	@Column(name="provincia2")
	private String provincia2;
	
	@Column(name="localita1")
	private String localita1;
	
	@Column(name="localita2")
	private String localita2;
	
	@Column(name="stato")
	private String stato;
	
    @Column(name = "sogg_nome")
    private String nome;

    @Column(name = "sogg_cognome")
    private String cognome;

    @Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = CustomLocalDateSerializer.class)
    @Column(name = "data_nascita")
    private LocalDate dataNascita;

    @Column(name = "prov_nascita")
    private String provNascita;

    @Column(name = "comune_nascita")
    private String comuneNascita;

    @Column(name = "sesso")
    private String sesso;

    @Column(name = "sogg_cf")
    private String codiceFiscale;

    @Column(name = "benef_denominazione")
    private String denominazione;

    @Column(name = "benef_piva")
    private String partitaIva;

    @Column(name = "indirizzo1")
    private String indirizzo1;

    @Column(name = "indirizzo2")
    private String indirizzo2;

    @Column(name = "cap")
    private String cap;

    @Column(name = "quietanzante")
    private String quietanzante;

    @Column(name = "mod_pagamento_id")
    private String modalitaPagamento;

    public RubricaBeneficiario(Long id){
    	this.id = id;
    }
    
    public RubricaBeneficiario(){
    }
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public String getIdAscot() {
		return idAscot;
	}

	public void setIdAscot(String idAscot) {
		this.idAscot = idAscot;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public LocalDate getDataNascita() {
		return dataNascita;
	}

	public void setDataNascita(LocalDate dataNascita) {
		this.dataNascita = dataNascita;
	}

	public String getProvNascita() {
		return provNascita;
	}

	public void setProvNascita(String provNascita) {
		this.provNascita = provNascita;
	}

	public String getComuneNascita() {
		return comuneNascita;
	}

	public void setComuneNascita(String comuneNascita) {
		this.comuneNascita = comuneNascita;
	}

	public String getSesso() {
		return sesso;
	}

	public void setSesso(String sesso) {
		this.sesso = sesso;
	}

	public String getCodiceFiscale() {
		return codiceFiscale;
	}

	public void setCodiceFiscale(String codiceFiscale) {
		this.codiceFiscale = codiceFiscale;
	}

	public String getDenominazione() {
		return denominazione;
	}

	public void setDenominazione(String denominazione) {
		this.denominazione = denominazione;
	}

	public String getPartitaIva() {
		return partitaIva;
	}

	public void setPartitaIva(String partitaIva) {
		this.partitaIva = partitaIva;
	}

	public String getIndirizzo1() {
		return indirizzo1;
	}

	public void setIndirizzo1(String indirizzo1) {
		this.indirizzo1 = indirizzo1;
	}

	public String getIndirizzo2() {
		return indirizzo2;
	}

	public void setIndirizzo2(String indirizzo2) {
		this.indirizzo2 = indirizzo2;
	}

	public String getCap() {
		return cap;
	}

	public void setCap(String cap) {
		this.cap = cap;
	}

	public String getQuietanzante() {
		return quietanzante;
	}

	public void setQuietanzante(String quietanzante) {
		this.quietanzante = quietanzante;
	}

	public String getModalitaPagamento() {
		return modalitaPagamento;
	}

	public void setModalitaPagamento(String modalitaPagamento) {
		this.modalitaPagamento = modalitaPagamento;
	}

	public String getDescrizioneLeggibileBeneficiario() {
		String valore;
		if(denominazione!=null && partitaIva!=null){
			valore = denominazione + " (P.I. " + partitaIva + ")";
		}else if(nome!=null && cognome!=null && codiceFiscale!=null){
			valore = nome + " " + cognome + " (C.F. " + codiceFiscale + ")";
		}else{
			if(id!=null){
				valore = "Beneficiario ID " + id;
			}else{
				valore = "Beneficiario";
			}
		}
		return valore;
	}

	public String getIban() {
		return iban;
	}

	public void setIban(String iban) {
		this.iban = iban;
	}

	public TipoSoggettoEnum getTipoSoggetto() {
		return tipoSoggetto;
	}

	public void setTipoSoggetto(TipoSoggettoEnum tipoSoggetto) {
		this.tipoSoggetto = tipoSoggetto;
	}

	public String getCap2() {
		return cap2;
	}

	public void setCap2(String cap2) {
		this.cap2 = cap2;
	}

	public String getCitta1() {
		return citta1;
	}

	public void setCitta1(String citta1) {
		this.citta1 = citta1;
	}

	public String getCitta2() {
		return citta2;
	}

	public void setCitta2(String citta2) {
		this.citta2 = citta2;
	}

	public String getProvincia1() {
		return provincia1;
	}

	public void setProvincia1(String provincia1) {
		this.provincia1 = provincia1;
	}

	public String getProvincia2() {
		return provincia2;
	}

	public void setProvincia2(String provincia2) {
		this.provincia2 = provincia2;
	}

	public String getLocalita1() {
		return localita1;
	}

	public void setLocalita1(String localita1) {
		this.localita1 = localita1;
	}

	public String getLocalita2() {
		return localita2;
	}

	public void setLocalita2(String localita2) {
		this.localita2 = localita2;
	}

	public String getStato() {
		return stato;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public Aoo getAoo() {
		return aoo;
	}

	public void setAoo(Aoo aoo) {
		this.aoo = aoo;
	}

	public Boolean getAttivo() {
		return attivo;
	}

	public void setAttivo(Boolean attivo) {
		this.attivo = attivo;
	}

	public String getCodiceFiscaleEstero() {
		return codiceFiscaleEstero;
	}

	public void setCodiceFiscaleEstero(String codiceFiscaleEstero) {
		this.codiceFiscaleEstero = codiceFiscaleEstero;
	}

	public String getPartitaIvaEstero() {
		return partitaIvaEstero;
	}

	public void setPartitaIvaEstero(String partitaIvaEstero) {
		this.partitaIvaEstero = partitaIvaEstero;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPec() {
		return pec;
	}

	public void setPec(String pec) {
		this.pec = pec;
	}

	@Override
	public int hashCode() {
		if(id!=null){
			return id.hashCode();
		}else{			
			final int prime = 31;
			int result = 1;
			result = prime * result + ((cap == null) ? 0 : cap.hashCode());
			result = prime * result + ((cap2 == null) ? 0 : cap2.hashCode());
			result = prime * result + ((citta1 == null) ? 0 : citta1.hashCode());
			result = prime * result + ((citta2 == null) ? 0 : citta2.hashCode());
			result = prime * result
					+ ((codiceFiscale == null) ? 0 : codiceFiscale.hashCode());
			result = prime * result + ((cognome == null) ? 0 : cognome.hashCode());
			result = prime * result
					+ ((comuneNascita == null) ? 0 : comuneNascita.hashCode());
			result = prime * result
					+ ((dataNascita == null) ? 0 : dataNascita.hashCode());
			result = prime * result
					+ ((denominazione == null) ? 0 : denominazione.hashCode());
			result = prime * result + ((iban == null) ? 0 : iban.hashCode());
			result = prime * result + ((id == null) ? 0 : id.hashCode());
			result = prime * result + ((idAscot == null) ? 0 : idAscot.hashCode());
			result = prime * result
					+ ((indirizzo1 == null) ? 0 : indirizzo1.hashCode());
			result = prime * result
					+ ((indirizzo2 == null) ? 0 : indirizzo2.hashCode());
			result = prime * result
					+ ((localita1 == null) ? 0 : localita1.hashCode());
			result = prime * result
					+ ((localita2 == null) ? 0 : localita2.hashCode());
			result = prime
					* result
					+ ((modalitaPagamento == null) ? 0 : modalitaPagamento
							.hashCode());
			result = prime * result + ((nome == null) ? 0 : nome.hashCode());
			result = prime * result
					+ ((partitaIva == null) ? 0 : partitaIva.hashCode());
			result = prime * result
					+ ((provNascita == null) ? 0 : provNascita.hashCode());
			result = prime * result
					+ ((provincia1 == null) ? 0 : provincia1.hashCode());
			result = prime * result
					+ ((provincia2 == null) ? 0 : provincia2.hashCode());
			result = prime * result
					+ ((quietanzante == null) ? 0 : quietanzante.hashCode());
			result = prime * result + ((sesso == null) ? 0 : sesso.hashCode());
			result = prime * result
					+ ((tipoSoggetto == null) ? 0 : tipoSoggetto.hashCode());
			result = prime * result
					+ ((stato == null) ? 0 : stato.hashCode());
			return result;
		}
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}else if (obj == null){
			return false;
		}else if (getClass() != obj.getClass()){
			return false;
		}else{
			return this.hashCode() == obj.hashCode();
		}		
	}
	
	
}
