package it.linksmt.assatti.bpm.dto;

import java.io.Serializable;

import org.joda.time.DateTime;

public class StatoAttoDTO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private AooDTO aoo = null;
	private DateTime datagiacenza;
	private String nominativo;
	private DateTime datacarico;
	private String descrizione;
	private String ruoliTask;
	
	public AooDTO getAoo() {
		return aoo;
	}
	public void setAoo(AooDTO aoo) {
		this.aoo = aoo;
	}
	public DateTime getDatagiacenza() {
		return datagiacenza;
	}
	public void setDatagiacenza(DateTime datagiacenza) {
		this.datagiacenza = datagiacenza;
	}
	public String getNominativo() {
		return nominativo;
	}
	public void setNominativo(String nominativo) {
		this.nominativo = nominativo;
	}
	public DateTime getDatacarico() {
		return datacarico;
	}
	public void setDatacarico(DateTime datacarico) {
		this.datacarico = datacarico;
	}
	public String getDescrizione() {
		return descrizione;
	}
	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}
	public String getRuoliTask() {
		return ruoliTask;
	}
	public void setRuoliTask(String ruoliTask) {
		this.ruoliTask = ruoliTask;
	}	
}
