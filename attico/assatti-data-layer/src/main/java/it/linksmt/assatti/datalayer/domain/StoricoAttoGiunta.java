package it.linksmt.assatti.datalayer.domain;

import it.linksmt.assatti.datalayer.domain.dto.StoricoLavorazioniDto;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeDeserializer;
import it.linksmt.assatti.datalayer.domain.util.CustomDateTimeSerializer;
import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
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
 * A StoricoAttoGiunta.
 */
@Entity
@Table(name = "STORICO_ATTO_GIUNTA" )
@Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
public class StoricoAttoGiunta implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public StoricoAttoGiunta(final Long id) {
		this.id = id;
	}

	public StoricoAttoGiunta() {
	}
	
	@Id
	@Column(name = "idatto_giunta")
	private Long id;

	@Column(name = "id_uff_prop", insertable = true, updatable = false)
	private String codiceArea;

	@Column(name = "uff_prop", length = 400, insertable = true, updatable = false)
	private String descrizioneArea;
	
	@Column(name = "id_prop", insertable = true, updatable = false, nullable = false, unique = true)
	@NotNull
	private String codiceCifra;
	
	@Column(name = "num_provv", length = 6)
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
	@Column(name = "data_burp", insertable = true, updatable = false)
	private LocalDate dataBurp;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_comp_noti", insertable = true, updatable = false)
	private LocalDate dataCompNoti;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_riunione_giunta", insertable = true, updatable = false)
	private LocalDate dataRiunioneGiunta;
	
	@Column(name = "cod_lav", insertable = true, updatable = false)
	private String codiceLavorazione;
	
	@Column(name = "tipo_prop_cod", insertable = true, updatable = false, length = 30)
	private String codiceTipoAtto;
	@Column(name = "tipo_proc_desc", insertable = true, updatable = false, length = 40)
	private String descrizioneTipoAtto;
	
	@Column(name = "redigente", length = 800)
	private String redigente;
	
	@Column(name = "codiceproposta")
	private String codiceProposta;
	@Column(name = "abilitato_edit")
	private String abilitatoEdit;
	@Column(name = "abilitato_read")
	private String abilitatoRead;
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
	@Column(name = "cons_regi")
	private String consRegi;
	@Column(name = "controllo")
	private String controllo;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentLocalDate")
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	@Column(name = "data_provv", insertable = true, updatable = false)
	private LocalDate dataAdozione;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_ricez")
	private DateTime dataRicezione;
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_pubblicazione")
	private DateTime dataPubblicazione;
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
	@Column(name = "form", length = 250)
	private String form;
	@Column(name = "motivazione", length = 800)
	private String motivazione;
	@Column(name = "note_lav", length = 1000)
	private String noteLavorazione;
	@Column(name = "num_prop", length = 6)
	private String numeroProposta;
	@Column(name = "orainvio", length = 8)
	private String oraInvio;
	@Column(name = "ora_riunione_giunta", length = 60)
	private String oraRiunioneGiunta;
	@Column(name = "tipo_atto", length = 100)
	private String tipoAtto;
	@Column(name = "tipo_proc", length = 2)
	private String tipoProcedimento;
	@Column(name = "tipo_provv_cod", length = 5)
	private String codiceTipoProvvedimento;
	@Column(name = "tipo_provv_desc", length = 40)
	private String descrizioneTipoProvvedimento;
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
	@Column(name = "versione", length = 30)
	private String versione;
	@Column(name = "anno", length = 4)
	private String anno;
	
	@Column(name = "allegato_sostituito", length = 2)
	private String allegatoSostituito;
	@Column(name = "annullato", length = 2)
	private String annullato;
	@Column(name = "argomento_rinviato", length = 2)
	private String argomentoRinviato;
	@Column(name = "burp", length = 2)
	private String burp;
	@Column(name = "cod_descrizione_prec", length = 100)
	private String codDescrizionePrec;
	
	@Type(type = "org.jadira.usertype.dateandtime.joda.PersistentDateTime")
	@JsonSerialize(using = CustomDateTimeSerializer.class)
	@JsonDeserialize(using = CustomDateTimeDeserializer.class)
	@Column(name = "data_invio_notifica")
	private DateTime dataInvioNotifica;
	
	@Column(name = "dati_ritiro", length = 3000)
	private String datiRitiro;
	@Column(name = "destinatari_notifica", length = 300)
	private String destinatariNotifica;
	@Column(name = "destinatari_notifica_opzionali", length = 300)
	private String destinatariNotificaOpzionali;
	@Column(name = "intestazioneODG", length = 400)
	private String intestazioneODG;
	@Column(name = "num_riunione_giunta", length = 60)
	private String numRiunioneGiunta;
	@Column(name = "numero_burp", length = 25)
	private String numeroBurp;
	@Column(name = "numsuppletivo", length = 6)
	private String numSuppletivo;
	@Column(name = "prima_iscrizione", length = 2)
	private String primaIscrizione;
	@Column(name = "privacy", length = 2)
	private String privacy;
	@Column(name = "regolamento", length = 2)
	private String regolamento;
	@Column(name = "sede_riunione_giunta", length = 100)
	private String sedeRiunioneGiunta;
	@Column(name = "segretario", length = 100)
	private String segretario;
	@Column(name = "tipo_argomenti", length = 20)
	private String tipoArgomenti;
	@Column(name = "tipo_riunione_giunta", length = 15)
	private String tipoRiunioneGiunta;
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "attoGiunta",cascade={CascadeType.ALL}, orphanRemoval=true)
	@OrderBy(value = "id ASC")
	private Set<StoricoAssenteGiunta> assenti = new HashSet<StoricoAssenteGiunta>();
	
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "attoGiunta",cascade={CascadeType.ALL}, orphanRemoval=true)
	@OrderBy(value = "id ASC")
	private Set<StoricoPresenteGiunta> presenti = new HashSet<StoricoPresenteGiunta>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "attoGiunta",cascade={CascadeType.ALL}, orphanRemoval=true)
	@OrderBy(value = "id ASC")
	private Set<StoricoCodiceAbilitazione> codiciAbilitazione = new HashSet<StoricoCodiceAbilitazione>();
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "attoGiunta",cascade={CascadeType.ALL}, orphanRemoval=true)
	@OrderBy(value = "id ASC")
	private Set<StoricoDocumento> documenti = new HashSet<StoricoDocumento>();
	
	@Transient
	private String stato;
	
	@Transient
	@JsonSerialize
	private List<StoricoLavorazioniDto> listaLavorazioni = new ArrayList<StoricoLavorazioniDto>();
	
	/**
	 *  GETTER...
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
	public String getCodiceLavorazione() {
		return codiceLavorazione;
	}
	public String getCodiceTipoAtto() {
		return codiceTipoAtto;
	}
	public String getDescrizioneTipoAtto() {
		return descrizioneTipoAtto;
	}

	public String getStato() {
		if (codiceLavorazione.contains("ines-adir"))
			stato = "Inesistente";
		else if(codiceLavorazione.contains("esec-nega-adir") || codiceLavorazione.contains("rinu-adir") || codiceLavorazione.contains("annu-adir"))
			stato = "Annullato";
		
		return stato;
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

	public LocalDate getDataAdozione() {
		return dataAdozione;
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

	public String getMotivazione() {
		return motivazione;
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

	public String getVersione() {
		return versione;
	}

	public String getAnno() {
		return anno;
	}
		
	public LocalDate getDataBurp() {
		return dataBurp;
	}

	public LocalDate getDataCompNoti() {
		return dataCompNoti;
	}

	public LocalDate getDataRiunioneGiunta() {
		return dataRiunioneGiunta;
	}

	public String getConsRegi() {
		return consRegi;
	}

	public String getControllo() {
		return controllo;
	}

	public DateTime getDataPubblicazione() {
		return dataPubblicazione;
	}

	public String getForm() {
		return form;
	}

	public String getOraRiunioneGiunta() {
		return oraRiunioneGiunta;
	}

	public String getTipoAtto() {
		return tipoAtto;
	}

	public String getDescrizioneTipoProvvedimento() {
		return descrizioneTipoProvvedimento;
	}

	public String getAllegatoSostituito() {
		return allegatoSostituito;
	}

	public String getAnnullato() {
		return annullato;
	}

	public String getArgomentoRinviato() {
		return argomentoRinviato;
	}

	public String getBurp() {
		return burp;
	}

	public String getCodDescrizionePrec() {
		return codDescrizionePrec;
	}

	public DateTime getDataInvioNotifica() {
		return dataInvioNotifica;
	}

	public String getDatiRitiro() {
		return datiRitiro;
	}

	public String getDestinatariNotifica() {
		return destinatariNotifica;
	}

	public String getDestinatariNotificaOpzionali() {
		return destinatariNotificaOpzionali;
	}

	public String getIntestazioneODG() {
		return intestazioneODG;
	}

	public String getNumRiunioneGiunta() {
		return numRiunioneGiunta;
	}

	public String getNumeroBurp() {
		return numeroBurp;
	}

	public String getNumSuppletivo() {
		return numSuppletivo;
	}

	public String getPrimaIscrizione() {
		return primaIscrizione;
	}

	public String getPrivacy() {
		return privacy;
	}

	public String getRegolamento() {
		return regolamento;
	}

	public String getSedeRiunioneGiunta() {
		return sedeRiunioneGiunta;
	}

	public String getSegretario() {
		return segretario;
	}

	public String getTipoArgomenti() {
		return tipoArgomenti;
	}

	public String getTipoRiunioneGiunta() {
		return tipoRiunioneGiunta;
	}

	public Set<StoricoAssenteGiunta> getAssenti() {
		return assenti;
	}

	public Set<StoricoPresenteGiunta> getPresenti() {
		return presenti;
	}

	public Set<StoricoCodiceAbilitazione> getCodiciAbilitazione() {
		return codiciAbilitazione;
	}

	public Set<StoricoDocumento> getDocumenti() {
		return documenti;
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
	public void setCodiceLavorazione(String codiceLavorazione) {
		this.codiceLavorazione = codiceLavorazione;
	}
	public void setCodiceTipoAtto(String codiceTipoAtto) {
		this.codiceTipoAtto = codiceTipoAtto;
	}
	public void setDescrizioneTipoAtto(String descrizioneTipoAtto) {
		this.descrizioneTipoAtto = descrizioneTipoAtto;
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

	public void setDataAdozione(LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
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

	public void setMotivazione(String motivazione) {
		this.motivazione = motivazione;
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
	
	public void setDataBurp(LocalDate dataBurp) {
		this.dataBurp = dataBurp;
	}

	public void setDataCompNoti(LocalDate dataCompNoti) {
		this.dataCompNoti = dataCompNoti;
	}

	public void setDataRiunioneGiunta(LocalDate dataRiunioneGiunta) {
		this.dataRiunioneGiunta = dataRiunioneGiunta;
	}

	public void setConsRegi(String consRegi) {
		this.consRegi = consRegi;
	}

	public void setControllo(String controllo) {
		this.controllo = controllo;
	}

	public void setDataPubblicazione(DateTime dataPubblicazione) {
		this.dataPubblicazione = dataPubblicazione;
	}

	public void setForm(String form) {
		this.form = form;
	}

	public void setOraRiunioneGiunta(String oraRiunioneGiunta) {
		this.oraRiunioneGiunta = oraRiunioneGiunta;
	}

	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}

	public void setDescrizioneTipoProvvedimento(String descrizioneTipoProvvedimento) {
		this.descrizioneTipoProvvedimento = descrizioneTipoProvvedimento;
	}

	public void setAllegatoSostituito(String allegatoSostituito) {
		this.allegatoSostituito = allegatoSostituito;
	}

	public void setAnnullato(String annullato) {
		this.annullato = annullato;
	}

	public void setArgomentoRinviato(String argomentoRinviato) {
		this.argomentoRinviato = argomentoRinviato;
	}

	public void setBurp(String burp) {
		this.burp = burp;
	}

	public void setCodDescrizionePrec(String codDescrizionePrec) {
		this.codDescrizionePrec = codDescrizionePrec;
	}

	public void setDataInvioNotifica(DateTime dataInvioNotifica) {
		this.dataInvioNotifica = dataInvioNotifica;
	}

	public void setDatiRitiro(String datiRitiro) {
		this.datiRitiro = datiRitiro;
	}

	public void setDestinatariNotifica(String destinatariNotifica) {
		this.destinatariNotifica = destinatariNotifica;
	}

	public void setDestinatariNotificaOpzionali(String destinatariNotificaOpzionali) {
		this.destinatariNotificaOpzionali = destinatariNotificaOpzionali;
	}

	public void setIntestazioneODG(String intestazioneODG) {
		this.intestazioneODG = intestazioneODG;
	}

	public void setNumRiunioneGiunta(String numRiunioneGiunta) {
		this.numRiunioneGiunta = numRiunioneGiunta;
	}

	public void setNumeroBurp(String numeroBurp) {
		this.numeroBurp = numeroBurp;
	}

	public void setNumSuppletivo(String numSuppletivo) {
		this.numSuppletivo = numSuppletivo;
	}

	public void setPrimaIscrizione(String primaIscrizione) {
		this.primaIscrizione = primaIscrizione;
	}

	public void setPrivacy(String privacy) {
		this.privacy = privacy;
	}

	public void setRegolamento(String regolamento) {
		this.regolamento = regolamento;
	}

	public void setSedeRiunioneGiunta(String sedeRiunioneGiunta) {
		this.sedeRiunioneGiunta = sedeRiunioneGiunta;
	}

	public void setSegretario(String segretario) {
		this.segretario = segretario;
	}

	public void setTipoArgomenti(String tipoArgomenti) {
		this.tipoArgomenti = tipoArgomenti;
	}

	public void setTipoRiunioneGiunta(String tipoRiunioneGiunta) {
		this.tipoRiunioneGiunta = tipoRiunioneGiunta;
	}

	public void setAssenti(Set<StoricoAssenteGiunta> assenti) {
		this.assenti = assenti;
	}

	public void setPresenti(Set<StoricoPresenteGiunta> presenti) {
		this.presenti = presenti;
	}

	public void setCodiciAbilitazione(Set<StoricoCodiceAbilitazione> codiciAbilitazione) {
		this.codiciAbilitazione = codiciAbilitazione;
	}

	public void setDocumenti(Set<StoricoDocumento> documenti) {
		this.documenti = documenti;
	}

	@Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        
        StoricoAttoGiunta sag = (StoricoAttoGiunta) o;
        
        if ( ! Objects.equals(id, sag.id)) return false;

        return true;
	}
	
	@Override
    public int hashCode() {
        return Objects.hashCode(id);
    }
	
	
}
