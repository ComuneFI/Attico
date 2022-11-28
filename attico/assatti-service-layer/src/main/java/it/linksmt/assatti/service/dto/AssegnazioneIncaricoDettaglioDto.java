package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Profilo;

public class AssegnazioneIncaricoDettaglioDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private ConfigurazioneTaskDto configurazioneTaskDto;

	private List<Aoo> listAoo;
	private List<Profilo> listProfilo;

	/*
	 * Get & Set
	 */

	public List<Aoo> getListAoo() {
		return listAoo;
	}

	public void setListAoo(List<Aoo> listAoo) {
		this.listAoo = listAoo;
	}

	public List<Profilo> getListProfilo() {
		return listProfilo;
	}

	public void setListProfilo(List<Profilo> listProfilo) {
		this.listProfilo = listProfilo;
	}

	public ConfigurazioneTaskDto getConfigurazioneTaskDto() {
		return configurazioneTaskDto;
	}

	public void setConfigurazioneTaskDto(ConfigurazioneTaskDto configurazioneTaskDto) {
		this.configurazioneTaskDto = configurazioneTaskDto;
	}

}
