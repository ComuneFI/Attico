package it.linksmt.assatti.service.dto;

import java.io.Serializable;
import java.util.List;

public class AssegnazioneIncaricoResponseDto implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AssegnazioneIncaricoDettaglioDto> listAssegnazioneIncaricoDettaglio;
	private List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto;

	/*
	 * Get & Set
	 */

	public List<ConfigurazioneIncaricoDto> getListConfigurazioneIncaricoDto() {
		return listConfigurazioneIncaricoDto;
	}

	public void setListConfigurazioneIncaricoDto(List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto) {
		this.listConfigurazioneIncaricoDto = listConfigurazioneIncaricoDto;
	}

	public List<AssegnazioneIncaricoDettaglioDto> getListAssegnazioneIncaricoDettaglio() {
		return listAssegnazioneIncaricoDettaglio;
	}

	public void setListAssegnazioneIncaricoDettaglio(List<AssegnazioneIncaricoDettaglioDto> listAssegnazioneIncaricoDettaglio) {
		this.listAssegnazioneIncaricoDettaglio = listAssegnazioneIncaricoDettaglio;
	}

}
