package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.datalayer.domain.TipoConfigurazioneTask;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;

public class ConfigurazioneTaskConverter {

	public static ConfigurazioneTaskDto convertToDto(ConfigurazioneTask configurazioneTask) {
		ConfigurazioneTaskDto configurazioneTaskDto = null;
		if(configurazioneTask!=null) {
			configurazioneTaskDto = new ConfigurazioneTaskDto();
			configurazioneTaskDto.setProcessVarName(configurazioneTask.getProcessVarName());
			configurazioneTaskDto.setCodice(configurazioneTask.getCodice());
			configurazioneTaskDto.setIdConfigurazioneTask(configurazioneTask.getIdConfigurazioneTask());
			configurazioneTaskDto.setMultipla(configurazioneTask.isMultipla());
			configurazioneTaskDto.setNome(configurazioneTask.getNome());
			configurazioneTaskDto.setObbligatoria(configurazioneTask.isObbligatoria());
			configurazioneTaskDto.setStessaDirezioneProponente(configurazioneTask.isStessaDirezioneProponente());
			configurazioneTaskDto.setProponente(configurazioneTask.isProponente());
			if (configurazioneTask.getUfficiLivelloSuperiore() != null) {
				configurazioneTaskDto.setUfficiLivelloSuperiore(configurazioneTask.getUfficiLivelloSuperiore());
			}
			else {
				configurazioneTaskDto.setUfficiLivelloSuperiore(false);
			}
			configurazioneTaskDto.setUfficioCorrente(configurazioneTask.isUfficioCorrente());
			configurazioneTaskDto.setTipoConfigurazioneTaskId(configurazioneTask.getTipoConfigurazioneTask().getIdTipoConfigurazioneTask());
			configurazioneTaskDto.setTipoConfigurazioneTaskNome(configurazioneTask.getTipoConfigurazioneTask().getNome());
			configurazioneTaskDto.setDataCreazione(configurazioneTask.getDataCreazione());
			configurazioneTaskDto.setDataModifica(configurazioneTask.getDataModifica());
			if (configurazioneTask.getImpostaScadenza() != null) {
				configurazioneTaskDto.setImpostaScadenza(configurazioneTask.getImpostaScadenza());
			}
			else {
				configurazioneTaskDto.setImpostaScadenza(false);
			}
			if (configurazioneTask.getScadenzaObbligatoria() != null) {
				configurazioneTaskDto.setScadenzaObbligatoria(configurazioneTask.getScadenzaObbligatoria());
			}
			else {
				configurazioneTaskDto.setScadenzaObbligatoria(false);
			}
			if (configurazioneTask.getDataManuale() != null) {
				configurazioneTaskDto.setDataManuale(configurazioneTask.getDataManuale());
			}
			else {
				configurazioneTaskDto.setDataManuale(false);
			}
		}
		return configurazioneTaskDto;
	}
	
	public static List<ConfigurazioneTaskDto> convertToDto(List<ConfigurazioneTask> configurazioneTask) {
		List<ConfigurazioneTaskDto> listConfigurazioneTaskDto = null;
		if(configurazioneTask!=null) {
			listConfigurazioneTaskDto = new ArrayList<>();
			for(ConfigurazioneTask s : configurazioneTask) {
				ConfigurazioneTaskDto sd = convertToDto(s);
				if(sd!=null) {
					listConfigurazioneTaskDto.add(sd);
				}
			}
		}
		return listConfigurazioneTaskDto;
	}

	public static ConfigurazioneTask convertToModel(ConfigurazioneTaskDto configurazioneTaskDto) {
		ConfigurazioneTask configurazioneTask = null;
		if(configurazioneTaskDto!=null) {
			configurazioneTask = new ConfigurazioneTask();
			configurazioneTask.setProcessVarName(configurazioneTaskDto.getProcessVarName());
			configurazioneTask.setCodice(configurazioneTaskDto.getCodice());
			configurazioneTask.setIdConfigurazioneTask(configurazioneTaskDto.getIdConfigurazioneTask());
			configurazioneTask.setMultipla(configurazioneTaskDto.isMultipla());
			configurazioneTask.setNome(configurazioneTaskDto.getNome());
			configurazioneTask.setObbligatoria(configurazioneTaskDto.isObbligatoria());
			configurazioneTask.setProponente(configurazioneTaskDto.isProponente());
			configurazioneTask.setUfficiLivelloSuperiore(configurazioneTaskDto.isUfficiLivelloSuperiore());
			configurazioneTask.setUfficioCorrente(configurazioneTaskDto.isUfficioCorrente());
			configurazioneTask.setDataCreazione(configurazioneTaskDto.getDataCreazione());
			configurazioneTask.setDataModifica(configurazioneTaskDto.getDataModifica());
			configurazioneTask.setImpostaScadenza(configurazioneTaskDto.isImpostaScadenza());
			configurazioneTask.setScadenzaObbligatoria(configurazioneTaskDto.isScadenzaObbligatoria());
			configurazioneTask.setDataManuale(configurazioneTaskDto.isDataManuale());
			configurazioneTask.setStessaDirezioneProponente(configurazioneTaskDto.isStessaDirezioneProponente());
			TipoConfigurazioneTask tipoConfigurazioneTask = new TipoConfigurazioneTask();
			tipoConfigurazioneTask.setIdTipoConfigurazioneTask(configurazioneTaskDto.getTipoConfigurazioneTaskId());
			tipoConfigurazioneTask.setNome(configurazioneTaskDto.getNome());
			configurazioneTask.setTipoConfigurazioneTask(tipoConfigurazioneTask);
		}
		return configurazioneTask;
	}

}
