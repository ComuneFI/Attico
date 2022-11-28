package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.dto.StoricoLavorazioniDto;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

/**
 * A StoricoAttoDirigenziale.
 */
@Entity
@Table(name = "STORICO_ATTO_DIRIGENZIALE" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StoricoAttoDirigenziale implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoricoAttoDirigenziale(final Long id) {
		this.id = id;
	}

	public StoricoAttoDirigenziale() {
	}
	
	@Id
	@Column(name = "id_attodirigenziale")
	private Long id;

	@Column(name = "id_uff_prop", insertable = true, updatable = false)
	private String codiceArea;

	@Column(name = "uff_prop", length = 400, insertable = true, updatable = false)
	private String descrizioneArea;
	
	@Column(name = "id_prop", insertable = true, updatable = false, nullable = false, unique = true)
	@NotNull
	private String codiceCifra;
	
	@Column(name = "num_provv", length = 5)
	private String numeroAdozione;
	
	@Column(name = "cod_descrizione")
	private String lavorazioneEffettuata;
	
	@Column(name = "ogg_prop", length = 1400)
	private String oggetto;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_prop", insertable = true, updatable = false)
	private LocalDate dataCreazione;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_esec", insertable = true, updatable = false)
	private LocalDate dataEsecutivita;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_albo_in", insertable = true, updatable = false)
	private LocalDate dataAffissioneInizio;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_albo_out", insertable = true, updatable = false)
	private LocalDate dataAffissioneFine;
	
	@Column(name = "tipo_iter", length = 50)
	private String tipoIter;
	@Column(name = "tipo_adem", length = 50)
	private String tipoAdempimento;
	
	@Column(name = "cod_lav", insertable = true, updatable = false)
	private String codiceLavorazione;
	
	@Column(name = "tipo_prop_cod", insertable = true, updatable = false, length = 3)
	private String codiceTipoAtto;
	@Column(name = "tipo_proc_desc", insertable = true, updatable = false, length = 20)
	private String descrizioneTipoAtto;
	
	@Column(name = "redigente")
	private String redigente;
	
	
	@Column(name = "codiceproposta")
	private String codiceProposta;
	@Column(name = "abilitato_edit")
	private String abilitatoEdit;
	@Column(name = "abilitato_read")
	private String abilitatoRead;
	@Column(name = "beneficiario", length = 1000)
	private String beneficiario;
	@Column(name = "cod_lav_prec")
	private String codiceLavPrec;
	@Column(name = "cod_uff_corr")
	private String codiceUffCorr;
	@Column(name = "cod_uff_dest")
	private String codiceUffDest;
	@Column(name = "cod_uff_prec")
	private String codiceUffPrec;
	@Column(name = "cod_uff_prop")
	private String codiceUffProp;
	@Column(name = "copiaragioneria")
	private String copiaRagioneria;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_provv", insertable = true, updatable = false)
	private LocalDate dataProvvedimento;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_ricez")
	private DateTime dataRicezione;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_ricez_uff_dest")
	private DateTime dataRicezioneUfficioDest;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "datainvio", insertable = true, updatable = false)
	private LocalDate dataInvio;
	@Column(name = "del_collegata", length = 600)
	private String deliberaCollegata;
	@Column(name = "fine_pratica", length = 2)
	private String finePratica;
	@Column(name = "flag_firma", length = 2)
	private String flagFirma;
	@Column(name = "id_provv", length = 18)
	private String idProvvedimento;
	@Column(name = "legge", length = 450)
	private String legge;
	@Column(name = "motivazione", length = 800)
	private String motivazione;
	@Column(name = "nota_responsabile_ff", length = 200)
	private String notaResponsabileFF;
	@Column(name = "note_lav", length = 1000)
	private String noteLavorazione;
	@Column(name = "num_prop", length = 5)
	private String numeroProposta;
	@Column(name = "orainvio", length = 8)
	private String oraInvio;
	@Column(name = "responsabile_ff", length = 200)
	private String responsabileFF;
	@Column(name = "responsabile_settore", length = 200)
	private String responsabileSettore;
	@Column(name = "tipo_proc", length = 2)
	private String tipoProcedimento;
	@Column(name = "tipo_provv_cod", length = 2)
	private String codiceTipoProvvedimento;
	@Column(name = "uff_corr", length = 400)
	private String uffCorr;
	@Column(name = "uff_dest", length = 400)
	private String uffDest;
	@Column(name = "uff_prec", length = 400)
	private String uffPrec;
	@Column(name = "utente_accetta", length = 50)
	private String utenteAccetta;
	@Column(name = "utente_lavora", length = 50)
	private String utenteLavora;
	@Column(name = "utente_trasmette", length = 50)
	private String utenteTrasmette;
	@Column(name = "verif_contab", length = 2)
	private String verificaContabile;
	@Column(name = "versione", length = 30)
	private String versione;
	@Column(name = "anno", length = 4)
	private String anno;
	
	@Transient
	private String stato;
	@Transient
	@JsonSerialize
	private List<StoricoLavorazioniDto> listaLavorazioni = new ArrayList<StoricoLavorazioniDto>();

	/**
	 * GETTER
	 */
	public Long getId() {
		return id;
	}
	public String getCodiceArea() {
		return codiceArea;
	}
	public String getDescrizioneArea() {
		return descrizioneArea;
	}
	public String getCodiceCifra() {
		return codiceCifra;
	}
	public String getNumeroAdozione() {
		return numeroAdozione;
	}
	public String getLavorazioneEffettuata() {
		return lavorazioneEffettuata;
	}
	public String getOggetto() {
		return oggetto;
	}
	public LocalDate getDataCreazione() {
		return dataCreazione;
	}
	public LocalDate getDataEsecutivita() {
		return dataEsecutivita;
	}
	public String getTipoIter() {
		return tipoIter;
	}
	public String getCodiceLavorazione() {
		return codiceLavorazione;
	}
	public String getCodiceTipoAtto() {
		return codiceTipoAtto;
	}
	public String getDescrizioneTipoAtto() {
		return descrizioneTipoAtto;
	}
	public String getTipoAdempimento() {
		return tipoAdempimento;
	}

	public String getStato() {
		if (codiceLavorazione.contains("ines-adir"))
			stato = "Inesistente";
		else if(codiceLavorazione.contains("esec-nega-adir") || codiceLavorazione.contains("rinu-adir") || codiceLavorazione.contains("annu-adir"))
			stato = "Annullato";
		else
			stato = "";
		
		return stato;
	}
	public LocalDate getDataAffissioneInizio() {
		return dataAffissioneInizio;
	}

	public LocalDate getDataAffissioneFine() {
		return dataAffissioneFine;
	}

	public String getRedigente() {
		return redigente;
	}

	public String getCodiceProposta() {
		return codiceProposta;
	}

	public String getAbilitatoEdit() {
		return abilitatoEdit;
	}

	public String getAbilitatoRead() {
		return abilitatoRead;
	}

	public String getBeneficiario() {
		return beneficiario;
	}

	public String getCodiceLavPrec() {
		return codiceLavPrec;
	}

	public String getCodiceUffCorr() {
		return codiceUffCorr;
	}

	public String getCodiceUffDest() {
		return codiceUffDest;
	}

	public String getCodiceUffPrec() {
		return codiceUffPrec;
	}

	public String getCodiceUffProp() {
		return codiceUffProp;
	}

	public String getCopiaRagioneria() {
		return copiaRagioneria;
	}

	public LocalDate getDataProvvedimento() {
		return dataProvvedimento;
	}

	public DateTime getDataRicezione() {
		return dataRicezione;
	}

	public DateTime getDataRicezioneUfficioDest() {
		return dataRicezioneUfficioDest;
	}

	public LocalDate getDataInvio() {
		return dataInvio;
	}

	public String getDeliberaCollegata() {
		return deliberaCollegata;
	}

	public String getFinePratica() {
		return finePratica;
	}

	public String getFlagFirma() {
		return flagFirma;
	}

	public String getIdProvvedimento() {
		return idProvvedimento;
	}

	public String getLegge() {
		return legge;
	}

	public String getMotivazione() {
		return motivazione;
	}

	public String getNotaResponsabileFF() {
		return notaResponsabileFF;
	}

	public String getNoteLavorazione() {
		return noteLavorazione;
	}

	public String getNumeroProposta() {
		return numeroProposta;
	}

	public String getOraInvio() {
		return oraInvio;
	}

	public String getResponsabileFF() {
		return responsabileFF;
	}

	public String getResponsabileSettore() {
		return responsabileSettore;
	}

	public String getTipoProcedimento() {
		return tipoProcedimento;
	}

	public String getCodiceTipoProvvedimento() {
		return codiceTipoProvvedimento;
	}

	public String getUffCorr() {
		return uffCorr;
	}

	public String getUffDest() {
		return uffDest;
	}

	public String getUffPrec() {
		return uffPrec;
	}

	public String getUtenteAccetta() {
		return utenteAccetta;
	}

	public String getUtenteLavora() {
		return utenteLavora;
	}

	public String getUtenteTrasmette() {
		return utenteTrasmette;
	}

	public String getVerificaContabile() {
		return verificaContabile;
	}

	public String getVersione() {
		return versione;
	}

	public String getAnno() {
		return anno;
	}

	/**
	 *  SETTER...
	 */
	public void setId(Long id) {
		this.id = id;
	}
	public void setCodiceArea(String codiceArea) {
		this.codiceArea = codiceArea;
	}
	public void setDescrizioneArea(String descrizioneArea) {
		this.descrizioneArea = descrizioneArea;
	}
	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}
	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}
	public void setLavorazioneEffettuata(String lavorazioneEffettuata) {
		this.lavorazioneEffettuata = lavorazioneEffettuata;
	}
	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	public void setTipoIter(String tipoIter) {
		this.tipoIter = tipoIter;
	}
	public void setTipoAdempimento(String tipoAdempimento) {
		this.tipoAdempimento = tipoAdempimento;
	}
	public void setCodiceLavorazione(String codiceLavorazione) {
		this.codiceLavorazione = codiceLavorazione;
	}
	public void setCodiceTipoAtto(String codiceTipoAtto) {
		this.codiceTipoAtto = codiceTipoAtto;
	}
	public void setDescrizioneTipoAtto(String descrizioneTipoAtto) {
		this.descrizioneTipoAtto = descrizioneTipoAtto;
	}

	public void setDataEsecutivita(LocalDate dataEsecutivita) {
		this.dataEsecutivita = dataEsecutivita;
	}

	public void setDataAffissioneInizio(LocalDate dataAffissioneInizio) {
		this.dataAffissioneInizio = dataAffissioneInizio;
	}

	public void setDataAffissioneFine(LocalDate dataAffissioneFine) {
		this.dataAffissioneFine = dataAffissioneFine;
	}

	public void setRedigente(String redigente) {
		this.redigente = redigente;
	}

	public void setCodiceProposta(String codiceProposta) {
		this.codiceProposta = codiceProposta;
	}

	public void setAbilitatoEdit(String abilitatoEdit) {
		this.abilitatoEdit = abilitatoEdit;
	}

	public void setAbilitatoRead(String abilitatoRead) {
		this.abilitatoRead = abilitatoRead;
	}

	public void setBeneficiario(String beneficiario) {
		this.beneficiario = beneficiario;
	}

	public void setCodiceLavPrec(String codiceLavPrec) {
		this.codiceLavPrec = codiceLavPrec;
	}

	public void setCodiceUffCorr(String codiceUffCorr) {
		this.codiceUffCorr = codiceUffCorr;
	}

	public void setCodiceUffDest(String codiceUffDest) {
		this.codiceUffDest = codiceUffDest;
	}

	public void setCodiceUffPrec(String codiceUffPrec) {
		this.codiceUffPrec = codiceUffPrec;
	}

	public void setCodiceUffProp(String codiceUffProp) {
		this.codiceUffProp = codiceUffProp;
	}

	public void setCopiaRagioneria(String copiaRagioneria) {
		this.copiaRagioneria = copiaRagioneria;
	}

	public void setDataProvvedimento(LocalDate dataProvvedimento) {
		this.dataProvvedimento = dataProvvedimento;
	}

	public void setDataRicezione(DateTime dataRicezione) {
		this.dataRicezione = dataRicezione;
	}

	public void setDataRicezioneUfficioDest(DateTime dataRicezioneUfficioDest) {
		this.dataRicezioneUfficioDest = dataRicezioneUfficioDest;
	}

	public void setDataInvio(LocalDate dataInvio) {
		this.dataInvio = dataInvio;
	}

	public void setDeliberaCollegata(String deliberaCollegata) {
		this.deliberaCollegata = deliberaCollegata;
	}

	public void setFinePratica(String finePratica) {
		this.finePratica = finePratica;
	}

	public void setFlagFirma(String flagFirma) {
		this.flagFirma = flagFirma;
	}

	public void setIdProvvedimento(String idProvvedimento) {
		this.idProvvedimento = idProvvedimento;
	}

	public void setLegge(String legge) {
		this.legge = legge;
	}

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
	}

	public void setNotaResponsabileFF(String notaResponsabileFF) {
		this.notaResponsabileFF = notaResponsabileFF;
	}

	public void setNoteLavorazione(String noteLavorazione) {
		this.noteLavorazione = noteLavorazione;
	}

	public void setNumeroProposta(String numeroProposta) {
		this.numeroProposta = numeroProposta;
	}

	public void setOraInvio(String oraInvio) {
		this.oraInvio = oraInvio;
	}

	public void setResponsabileFF(String responsabileFF) {
		this.responsabileFF = responsabileFF;
	}

	public void setResponsabileSettore(String responsabileSettore) {
		this.responsabileSettore = responsabileSettore;
	}

	public void setTipoProcedimento(String tipoProcedimento) {
		this.tipoProcedimento = tipoProcedimento;
	}

	public void setCodiceTipoProvvedimento(String codiceTipoProvvedimento) {
		this.codiceTipoProvvedimento = codiceTipoProvvedimento;
	}

	public void setUffCorr(String uffCorr) {
		this.uffCorr = uffCorr;
	}

	public void setUffDest(String uffDest) {
		this.uffDest = uffDest;
	}

	public void setUffPrec(String uffPrec) {
		this.uffPrec = uffPrec;
	}

	public void setUtenteAccetta(String utenteAccetta) {
		this.utenteAccetta = utenteAccetta;
	}

	public void setUtenteLavora(String utenteLavora) {
		this.utenteLavora = utenteLavora;
	}

	public void setUtenteTrasmette(String utenteTrasmette) {
		this.utenteTrasmette = utenteTrasmette;
	}

	public void setVerificaContabile(String verificaContabile) {
		this.verificaContabile = verificaContabile;
	}

	public void setVersione(String versione) {
		this.versione = versione;
	}

	public void setAnno(String anno) {
		this.anno = anno;
	}

	public void setStato(String stato) {
		this.stato = stato;
	}

	public List<StoricoLavorazioniDto> getListaLavorazioni() {
		return listaLavorazioni;
	}

	public void setListaLavorazioni(List<StoricoLavorazioniDto> listaLavorazioni) {
		this.listaLavorazioni = listaLavorazioni;
	}
	
	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        StoricoAttoDirigenziale sad = (StoricoAttoDirigenziale) o;

        if ( ! Objects.equals(id, sad.id)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
	
}
