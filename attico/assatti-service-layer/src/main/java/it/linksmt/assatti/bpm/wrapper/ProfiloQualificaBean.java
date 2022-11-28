package it.linksmt.assatti.bpm.wrapper;

import java.util.Date;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.Profilo;

public class ProfiloQualificaBean {

	private Profilo profilo;
	private String descrizioneQualifica;
	private Date dataAvvioTask;
	private Date dataIncarico;
	
	private ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo;
	private ConfigurazioneIncaricoAoo configurazioneIncaricoAoo;
	
	public Profilo getProfilo() {
		return profilo;
	}
	public void setProfilo(Profilo profilo) {
		this.profilo = profilo;
	}
	public String getDescrizioneQualifica() {
		return descrizioneQualifica;
	}
	public void setDescrizioneQualifica(String descrizioneQualifica) {
		this.descrizioneQualifica = descrizioneQualifica;
	}
	public Date getDataAvvioTask() {
		return dataAvvioTask;
	}
	public void setDataAvvioTask(Date dataAvvioTask) {
		this.dataAvvioTask = dataAvvioTask;
	}
	public Date getDataIncarico() {
		return dataIncarico;
	}
	public void setDataIncarico(Date dataIncarico) {
		this.dataIncarico = dataIncarico;
	}
	public ConfigurazioneIncaricoProfilo getConfigurazioneIncaricoProfilo() {
		return configurazioneIncaricoProfilo;
	}
	public void setConfigurazioneIncaricoProfilo(ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo) {
		this.configurazioneIncaricoProfilo = configurazioneIncaricoProfilo;
	}
	public ConfigurazioneIncaricoAoo getConfigurazioneIncaricoAoo() {
		return configurazioneIncaricoAoo;
	}
	public void setConfigurazioneIncaricoAoo(ConfigurazioneIncaricoAoo configurazioneIncaricoAoo) {
		this.configurazioneIncaricoAoo = configurazioneIncaricoAoo;
	}
}
