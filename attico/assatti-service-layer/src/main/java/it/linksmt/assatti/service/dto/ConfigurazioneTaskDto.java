package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Domain model class: ConfigurazioneTaskDto.
 */
public class ConfigurazioneTaskDto implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String processVarName;

	private Long idConfigurazioneTask;

	private Long tipoConfigurazioneTaskId;
	
	private String tipoConfigurazioneTaskNome;

	private String nome;

	private String codice;

	private boolean obbligatoria;

	private boolean multipla;

	private boolean proponente;
	
	private boolean ufficioCorrente;
	
	private List<Long> listAoo;

	private List<Long> listRuolo;
	
	private Date dataCreazione;
	
	private Date dataModifica;
	
	private boolean impostaScadenza;
	
	private boolean scadenzaObbligatoria;
	
	private boolean dataManuale;
	
	private boolean ufficiLivelloSuperiore;
	
	private boolean stessaDirezioneProponente;

	/*
	 * Get & Set
	 */

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Long getIdConfigurazioneTask() {
		return idConfigurazioneTask;
	}

	public void setIdConfigurazioneTask(Long idConfigurazioneTask) {
		this.idConfigurazioneTask = idConfigurazioneTask;
	}

	public String getCodice() {
		return codice;
	}

	public void setCodice(String codice) {
		this.codice = codice;
	}

	public boolean isObbligatoria() {
		return obbligatoria;
	}

	public void setObbligatoria(boolean obbligatoria) {
		this.obbligatoria = obbligatoria;
	}

	public boolean isMultipla() {
		return multipla;
	}

	public void setMultipla(boolean multipla) {
		this.multipla = multipla;
	}

	public boolean isProponente() {
		return proponente;
	}

	public void setProponente(boolean proponente) {
		this.proponente = proponente;
	}
	
	public boolean isUfficioCorrente() {
		return ufficioCorrente;
	}

	public void setUfficioCorrente(boolean ufficioCorrente) {
		this.ufficioCorrente = ufficioCorrente;
	}

	public Long getTipoConfigurazioneTaskId() {
		return tipoConfigurazioneTaskId;
	}

	public void setTipoConfigurazioneTaskId(Long tipoConfigurazioneTaskId) {
		this.tipoConfigurazioneTaskId = tipoConfigurazioneTaskId;
	}

	public String getTipoConfigurazioneTaskNome() {
		return tipoConfigurazioneTaskNome;
	}

	public void setTipoConfigurazioneTaskNome(String tipoConfigurazioneTaskNome) {
		this.tipoConfigurazioneTaskNome = tipoConfigurazioneTaskNome;
	}

	public List<Long> getListAoo() {
		return listAoo;
	}

	public void setListAoo(List<Long> listAoo) {
		this.listAoo = listAoo;
	}

	public List<Long> getListRuolo() {
		return listRuolo;
	}

	public void setListRuolo(List<Long> listRuolo) {
		this.listRuolo = listRuolo;
	}

	public Date getDataCreazione() {
		return dataCreazione;
	}

	public void setDataCreazione(Date dataCreazione) {
		this.dataCreazione = dataCreazione;
	}

	public Date getDataModifica() {
		return dataModifica;
	}

	public void setDataModifica(Date dataModifica) {
		this.dataModifica = dataModifica;
	}

	public boolean isImpostaScadenza() {
		return impostaScadenza;
	}

	public void setImpostaScadenza(boolean impostaScadenza) {
		this.impostaScadenza = impostaScadenza;
	}

	public boolean isUfficiLivelloSuperiore() {
		return ufficiLivelloSuperiore;
	}

	public void setUfficiLivelloSuperiore(boolean ufficiLivelloSuperiore) {
		this.ufficiLivelloSuperiore = ufficiLivelloSuperiore;
	}

	public boolean isStessaDirezioneProponente() {
		return stessaDirezioneProponente;
	}

	public void setStessaDirezioneProponente(boolean stessaDirezioneProponente) {
		this.stessaDirezioneProponente = stessaDirezioneProponente;
	}

	public boolean isScadenzaObbligatoria() {
		return scadenzaObbligatoria;
	}

	public void setScadenzaObbligatoria(boolean scadenzaObbligatoria) {
		this.scadenzaObbligatoria = scadenzaObbligatoria;
	}

	public String getProcessVarName() {
		return processVarName;
	}

	public void setProcessVarName(String processVarName) {
		this.processVarName = processVarName;
	}

	public boolean isDataManuale() {
		return dataManuale;
	}

	public void setDataManuale(boolean dataManuale) {
		this.dataManuale = dataManuale;
	}
	
}
