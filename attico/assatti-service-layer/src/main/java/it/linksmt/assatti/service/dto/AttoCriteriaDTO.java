package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.validation.constraints.NotNull;

import org.joda.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import it.linksmt.assatti.datalayer.domain.util.CustomLocalDateSerializer;
import it.linksmt.assatti.datalayer.domain.util.ISO8601LocalDateDeserializer;

@JsonIgnoreProperties(ignoreUnknown = true)
public class AttoCriteriaDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3825954530118961171L;

	private Long aooIdGroup;
	
	private String type;
	
	private String aooId;
	
	private String istruttore;
	
	private String codiceCup;
	private String codiceCig;
	
	private String fineIterType;
	
	private Boolean escludiRevocati;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate lastModifiedDateDa;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate lastModifiedDateA;
	
	private List<Long> tipiAttoIds;
	
	private List<String> tipiIterForzati;

	private Long idOdg;
	
	private Long idAttoFilterType;
	
	private String stato;
	
	private Long profiloId;
	
	private String incaricoa;
	
	private TreeSet<String> colnames;
	
	@NotNull
	private String ordinamento;
	
	@NotNull
	private String tipoOrinamento;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozione;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataAdozioneA;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataEsecutiva;

	private String numeroAdozione;
	
	private String numeroAdozioneDa;
	private String numeroAdozioneA;

	private String luogoAdozione;
	
	private String codiceCifra;
	
	private String area;

	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataCreazione;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataCreazioneDa;
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataCreazioneA;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataIncarico;
	
	private String oggetto;
	
	private String tipoAtto;
	
	private String codiceTipoAtto;
	
	private String tipoIter;
	
	private String tipoFinanziamento;
	
	
	private String taskStato;
	
	private String viewtype;
	
	private String anno;
	
	private String regolamento;
	
	private String estremiSeduta;
	private String esitoSeduta;
	private String attoRevocato;
	private String tipoProvvedimento;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate inizioPubblicazioneAlbo;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate finePubblicazioneAlbo;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate dataSeduta;
	
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate inizioDataSeduta;
	
	@JsonSerialize(using = CustomLocalDateSerializer.class)
	@JsonDeserialize(using = ISO8601LocalDateDeserializer.class)
	private LocalDate fineDataSeduta;
	
	private String luogoSeduta;
	
	private String relatore;
	
	private String assenti;
	
	private String numeroSeduta;
	
	private TipoSedutaDTO tipoSeduta;
	
	private String statoPubblicazione;
	
	private String statoProceduraPubblicazione;
	
	private String statoRelata;
	
	private Boolean ufficiDisattivati;
	
	
	public LocalDate getInizioDataSeduta() {
		return inizioDataSeduta;
	}


	public void setInizioDataSeduta(LocalDate inizioDataSeduta) {
		this.inizioDataSeduta = inizioDataSeduta;
	}


	public LocalDate getFineDataSeduta() {
		return fineDataSeduta;
	}


	public void setFineDataSeduta(LocalDate fineDataSeduta) {
		this.fineDataSeduta = fineDataSeduta;
	}


	public String getLuogoSeduta() {
		return luogoSeduta;
	}


	public void setLuogoSeduta(String luogoSeduta) {
		this.luogoSeduta = luogoSeduta;
	}


	public String getRelatore() {
		return relatore;
	}


	public void setRelatore(String relatore) {
		this.relatore = relatore;
	}

	public String getAssenti() {
		return assenti;
	}


	public void setAssenti(String assenti) {
		this.assenti = assenti;
	}


	public void setInizioPubblicazioneAlbo(LocalDate inizioPubblicazioneAlbo) {
		this.inizioPubblicazioneAlbo = inizioPubblicazioneAlbo;
	}


	public void setFinePubblicazioneAlbo(LocalDate finePubblicazioneAlbo) {
		this.finePubblicazioneAlbo = finePubblicazioneAlbo;
	}


	public LocalDate getDataSeduta() {
		return dataSeduta;
	}


	public void setDataSeduta(LocalDate dataSeduta) {
		this.dataSeduta = dataSeduta;
	}


	public String getNumeroSeduta() {
		return numeroSeduta;
	}


	public void setNumeroSeduta(String numeroSeduta) {
		this.numeroSeduta = numeroSeduta;
	}


	public TipoSedutaDTO getTipoSeduta() {
		return tipoSeduta;
	}


	public void setTipoSeduta(TipoSedutaDTO tipoSeduta) {
		this.tipoSeduta = tipoSeduta;
	}


	public String getAnno() {
		return anno;
	}


	public void setAnno(String anno) {
		this.anno = anno;
	}


	public String getViewtype() {
		return viewtype;
	}


	public void setViewtype(String viewtype) {
		this.viewtype = viewtype;
	}


	public Long getIdOdg() {
		return idOdg;
	}


	public void setIdOdg(Long idOdg) {
		this.idOdg = idOdg;
	}


	public String getTaskStato() {
		return taskStato;
	}


	public void setTaskStato(String taskStato) {
		this.taskStato = taskStato;
	}

	public String getAooId() {
		return aooId;
	}


	public void setAooId(String aooId) {
		this.aooId = aooId;
	}


	public String getStato() {
		return stato;
	}


	public void setStato(String stato) {
		this.stato = stato;
	}


	public LocalDate getDataAdozione() {
		return dataAdozione;
	}


	public void setDataAdozione(LocalDate dataAdozione) {
		this.dataAdozione = dataAdozione;
	}
	
	public LocalDate getDataAdozioneA() {
		return dataAdozioneA;
	}


	public void setDataAdozioneA(LocalDate dataAdozioneA) {
		this.dataAdozioneA = dataAdozioneA;
	}


	public String getNumeroAdozione() {
		return numeroAdozione;
	}


	public void setNumeroAdozione(String numeroAdozione) {
		this.numeroAdozione = numeroAdozione;
	}
	
	public String getNumeroAdozioneDa() {
		return numeroAdozioneDa;
	}


	public void setNumeroAdozioneDa(String numeroAdozioneDa) {
		this.numeroAdozioneDa = numeroAdozioneDa;
	}


	public String getNumeroAdozioneA() {
		return numeroAdozioneA;
	}


	public void setNumeroAdozioneA(String numeroAdozioneA) {
		this.numeroAdozioneA = numeroAdozioneA;
	}


	public String getLuogoAdozione() {
		return luogoAdozione;
	}


	public void setLuogoAdozione(String luogoAdozione) {
		this.luogoAdozione = luogoAdozione;
	}


	public String getCodiceCifra() {
		return codiceCifra;
	}


	public void setCodiceCifra(String codiceCifra) {
		this.codiceCifra = codiceCifra;
	}
	
	public List<Long> getTipiAttoIds() {
		return tipiAttoIds;
	}


	public void setTipiAttoIds(List<Long> tipiAttoIds) {
		this.tipiAttoIds = tipiAttoIds;
	}


	public String getOrdinamento() {
		return ordinamento;
	}


	public void setOrdinamento(String ordinamento) {
		this.ordinamento = ordinamento;
	}


	public String getTipoOrinamento() {
		return tipoOrinamento;
	}


	public void setTipoOrinamento(String tipoOrinamento) {
		this.tipoOrinamento = tipoOrinamento;
	}
	
	public LocalDate getDataCreazione() {
		return dataCreazione;
	}


	public void setDataCreazione(LocalDate dataCreazione) {
		this.dataCreazione = dataCreazione;
	}
	
	public LocalDate getDataCreazioneDa() {
		return dataCreazioneDa;
	}

	public void setDataCreazioneDa(LocalDate dataCreazioneDa) {
		this.dataCreazioneDa = dataCreazioneDa;
	}

	public LocalDate getDataCreazioneA() {
		return dataCreazioneA;
	}

	public void setDataCreazioneA(LocalDate dataCreazioneA) {
		this.dataCreazioneA = dataCreazioneA;
	}


	public String getOggetto() {
		return oggetto;
	}


	public void setOggetto(String oggetto) {
		this.oggetto = oggetto;
	}
	
	public Long getProfiloId() {
		return profiloId;
	}


	public void setProfiloId(Long profiloId) {
		this.profiloId = profiloId;
	}


	public String getTipoAtto() {
		return tipoAtto;
	}


	public void setTipoAtto(String tipoAtto) {
		this.tipoAtto = tipoAtto;
	}


	public String getTipoIter() {
		return tipoIter;
	}


	public void setTipoIter(String tipoIter) {
		this.tipoIter = tipoIter;
	}
	
	public String getTipoFinanziamento() {
		return tipoFinanziamento;
	}


	public void setTipoFinanziamento(String tipoFinanziamento) {
		this.tipoFinanziamento = tipoFinanziamento;
	}


	public String getArea() {
		return area;
	}


	public void setArea(String area) {
		this.area = area;
	}
	
	public String getIncaricoa() {
		return incaricoa;
	}

	public void setIncaricoa(String incaricoa) {
		this.incaricoa = incaricoa;
	}
	
	public LocalDate getDataEsecutiva() {
		return dataEsecutiva;
	}


	public void setDataEsecutiva(LocalDate dataEsecutiva) {
		this.dataEsecutiva = dataEsecutiva;
	}
	
	public LocalDate getDataIncarico() {
		return dataIncarico;
	}


	public void setDataIncarico(LocalDate dataIncarico) {
		this.dataIncarico = dataIncarico;
	}
	
	public LocalDate getInizioPubblicazioneAlbo() {
		return inizioPubblicazioneAlbo;
	}


	public void setiInizioPubblicazioneAlbo(LocalDate inizioPubblicazioneAlbo) {
		this.inizioPubblicazioneAlbo = inizioPubblicazioneAlbo;
	}
	
	public LocalDate getFinePubblicazioneAlbo() {
		return finePubblicazioneAlbo;
	}


	public void setiFinePubblicazioneAlbo(LocalDate finePubblicazioneAlbo) {
		this.finePubblicazioneAlbo = finePubblicazioneAlbo;
	}


	public String getRegolamento() {
		return regolamento;
	}


	public void setRegolamento(String regolamento) {
		this.regolamento = regolamento;
	}


	public Set<String> getColnames() {
		return colnames;
	}


	public String getIstruttore() {
		return istruttore;
	}


	public LocalDate getLastModifiedDateDa() {
		return lastModifiedDateDa;
	}


	public LocalDate getLastModifiedDateA() {
		return lastModifiedDateA;
	}


	public void setIstruttore(String istruttore) {
		this.istruttore = istruttore;
	}


	public void setLastModifiedDateDa(LocalDate lastModifiedDateDa) {
		this.lastModifiedDateDa = lastModifiedDateDa;
	}


	public void setLastModifiedDateA(LocalDate lastModifiedDateA) {
		this.lastModifiedDateA = lastModifiedDateA;
	}


	public void setColnames(TreeSet<String> colnames) {
		this.colnames = colnames;
	}


	public String getEstremiSeduta() {
		return estremiSeduta;
	}


	public void setEstremiSeduta(String estremiSeduta) {
		this.estremiSeduta = estremiSeduta;
	}


	public String getEsitoSeduta() {
		return esitoSeduta;
	}


	public void setEsitoSeduta(String esitoSeduta) {
		this.esitoSeduta = esitoSeduta;
	}


	public String getAttoRevocato() {
		return attoRevocato;
	}


	public void setAttoRevocato(String attoRevocato) {
		this.attoRevocato = attoRevocato;
	}


	public String getTipoProvvedimento() {
		return tipoProvvedimento;
	}


	public void setTipoProvvedimento(String tipoProvvedimento) {
		this.tipoProvvedimento = tipoProvvedimento;
	}


	public String getStatoPubblicazione() {
		return statoPubblicazione;
	}


	public void setStatoPubblicazione(String statoPubblicazione) {
		this.statoPubblicazione = statoPubblicazione;
	}


	public String getStatoProceduraPubblicazione() {
		return statoProceduraPubblicazione;
	}


	public void setStatoProceduraPubblicazione(String statoProceduraPubblicazione) {
		this.statoProceduraPubblicazione = statoProceduraPubblicazione;
	}


	public String getStatoRelata() {
		return statoRelata;
	}


	public void setStatoRelata(String statoRelata) {
		this.statoRelata = statoRelata;
	}


	public List<String> getTipiIterForzati() {
		return tipiIterForzati;
	}

	public void setTipiIterForzati(List<String> tipiIterForzati) {
		this.tipiIterForzati = tipiIterForzati;
	}

	public String getCodiceTipoAtto() {
		return codiceTipoAtto;
	}

	public void setCodiceTipoAtto(String codiceTipoAtto) {
		this.codiceTipoAtto = codiceTipoAtto;
	}


	public Long getIdAttoFilterType() {
		return idAttoFilterType;
	}


	public void setIdAttoFilterType(Long idAttoFilterType) {
		this.idAttoFilterType = idAttoFilterType;
	}


	public String getCodiceCup() {
		return codiceCup;
	}


	public void setCodiceCup(String codiceCup) {
		this.codiceCup = codiceCup;
	}


	public String getCodiceCig() {
		return codiceCig;
	}


	public void setCodiceCig(String codiceCig) {
		this.codiceCig = codiceCig;
	}


	public String getFineIterType() {
		return fineIterType;
	}


	public void setFineIterType(String fineIterType) {
		this.fineIterType = fineIterType;
	}


	public Boolean getEscludiRevocati() {
		return escludiRevocati;
	}


	public void setEscludiRevocati(Boolean escludiRevocati) {
		this.escludiRevocati = escludiRevocati;
	}


	public String getType() {
		return type;
	}


	public void setType(String type) {
		this.type = type;
	}


	public Long getAooIdGroup() {
		return aooIdGroup;
	}


	public void setAooIdGroup(Long aooIdGroup) {
		this.aooIdGroup = aooIdGroup;
	}


	public Boolean getUfficiDisattivati() {
		return ufficiDisattivati;
	}


	public void setUfficiDisattivati(Boolean ufficiDisattivati) {
		this.ufficiDisattivati = ufficiDisattivati;
	}
	
	

}
