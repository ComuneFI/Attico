package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;

public class ConfigurazioneIncaricoConverter {

	public static ConfigurazioneIncaricoDto convertToDto(ConfigurazioneIncarico configurazioneIncarico) {
		ConfigurazioneIncaricoDto configurazioneIncaricoDto = null;
		if(configurazioneIncarico!=null) {
			configurazioneIncaricoDto = new ConfigurazioneIncaricoDto();
			configurazioneIncaricoDto.setEditor(configurazioneIncarico.getEditor());
			configurazioneIncaricoDto.setEnabled(configurazioneIncarico.getEnabled());
			configurazioneIncaricoDto.setId(configurazioneIncarico.getId());
			configurazioneIncaricoDto.setIdAtto(configurazioneIncarico.getAtto().getId());
			configurazioneIncaricoDto.setIdConfigurazioneTask(configurazioneIncarico.getConfigurazioneTask().getIdConfigurazioneTask());
			configurazioneIncaricoDto.setTipologia(configurazioneIncarico.getTipologia());
			configurazioneIncaricoDto.setDataCreazione(configurazioneIncarico.getDataCreazione());
			configurazioneIncaricoDto.setDataModifica(configurazioneIncarico.getDataModifica());
			configurazioneIncaricoDto.setConfigurazioneTaskNome(configurazioneIncarico.getConfigurazioneTask().getNome());
			configurazioneIncaricoDto.setConfigurazioneTaskCodice(configurazioneIncarico.getConfigurazioneTask().getCodice());
		}
		return configurazioneIncaricoDto;
	}
	
	public static List<ConfigurazioneIncaricoDto> convertToDto(List<ConfigurazioneIncarico> configurazioneIncarico) {
		List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto = null;
		if(configurazioneIncarico!=null) {
			listConfigurazioneIncaricoDto = new ArrayList<>();
			for(ConfigurazioneIncarico s : configurazioneIncarico) {
				ConfigurazioneIncaricoDto sd = convertToDto(s);
				if(sd!=null) {
					listConfigurazioneIncaricoDto.add(sd);
				}
			}
		}
		return listConfigurazioneIncaricoDto;
	}

	public static ConfigurazioneIncarico convertToModel(ConfigurazioneIncaricoDto configurazioneIncaricoDto) {
		ConfigurazioneIncarico configurazioneIncarico = null;
		if(configurazioneIncaricoDto!=null) {
			configurazioneIncarico = new ConfigurazioneIncarico();
			
			configurazioneIncarico.setEditor(configurazioneIncaricoDto.getEditor());
			configurazioneIncarico.setEnabled(configurazioneIncaricoDto.getEnabled());
			configurazioneIncarico.setId(configurazioneIncaricoDto.getId());
			configurazioneIncarico.setTipologia(configurazioneIncaricoDto.getTipologia());
			configurazioneIncarico.setDataCreazione(configurazioneIncaricoDto.getDataCreazione());
			configurazioneIncarico.setDataModifica(configurazioneIncaricoDto.getDataModifica());
			
			Atto atto = new Atto();
			atto.setId(configurazioneIncaricoDto.getIdAtto());
			configurazioneIncarico.setAtto(atto);
			
			ConfigurazioneTask configurazioneTask = new ConfigurazioneTask();
			configurazioneTask.setIdConfigurazioneTask(configurazioneIncaricoDto.getIdConfigurazioneTask());
			configurazioneIncarico.setConfigurazioneTask(configurazioneTask);
		}
		return configurazioneIncarico;
	}

}
