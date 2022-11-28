package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.dto.QualificaProfessionaleDto;
import it.linksmt.assatti.utility.StringUtil;

public class ConfigurazioneIncaricoProfiloConverter {
	
	private static ProfiloService profiloService;
	
	public static void init(ProfiloService profiloService) {
		ConfigurazioneIncaricoProfiloConverter.profiloService = profiloService;
	}

	public static ConfigurazioneIncaricoProfiloDto convertToDto(ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo, Date dataIncarico) {
		ConfigurazioneIncaricoProfiloDto configurazioneIncaricoProfiloDto = null;
		if(configurazioneIncaricoProfilo!=null) {
			configurazioneIncaricoProfiloDto = new ConfigurazioneIncaricoProfiloDto();
			configurazioneIncaricoProfiloDto.setDataCreazione(configurazioneIncaricoProfilo.getDataCreazione());
			configurazioneIncaricoProfiloDto.setIdProfilo(configurazioneIncaricoProfilo.getPrimaryKey().getIdProfilo());
			configurazioneIncaricoProfiloDto.setProfilo(profiloService.findOneEssentialDet(configurazioneIncaricoProfilo.getPrimaryKey().getIdProfilo()));
			configurazioneIncaricoProfiloDto.setIdConfigurazioneIncarico(configurazioneIncaricoProfilo.getPrimaryKey().getIdConfigurazioneIncarico());
			
			configurazioneIncaricoProfiloDto.setOrdineFirma(configurazioneIncaricoProfilo.getOrdineFirma());
			
			QualificaProfessionaleDto qualificaProfessionaleDto = QualificaProfessionaleConverter.convertToDto(configurazioneIncaricoProfilo.getQualificaProfessionale());
			configurazioneIncaricoProfiloDto.setQualificaProfessionaleDto(qualificaProfessionaleDto);
			
			configurazioneIncaricoProfiloDto.setGiorniScadenza(configurazioneIncaricoProfilo.getGiorniScadenza());
			
			if ((dataIncarico != null) && (configurazioneIncaricoProfilo.getGiorniScadenza() != null) && (configurazioneIncaricoProfilo.getGiorniScadenza().intValue() > 0)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataIncarico);
				cal.add(Calendar.DATE, configurazioneIncaricoProfilo.getGiorniScadenza().intValue());
				
				configurazioneIncaricoProfiloDto.setDataScadenza(StringUtil.IT_DATE_FORMAT.format(cal.getTime()));
			}
		}
		return configurazioneIncaricoProfiloDto;
	}
	
	public static List<ConfigurazioneIncaricoProfiloDto> convertToDto(List<ConfigurazioneIncaricoProfilo> configurazioneIncaricoProfilo, Date dataIncarico) {
		List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto = null;
		if(configurazioneIncaricoProfilo!=null) {
			listConfigurazioneIncaricoProfiloDto = new ArrayList<>();
			for(ConfigurazioneIncaricoProfilo s : configurazioneIncaricoProfilo) {
				ConfigurazioneIncaricoProfiloDto sd = convertToDto(s, dataIncarico);
				if(sd!=null) {
					listConfigurazioneIncaricoProfiloDto.add(sd);
				}
			}
		}
		return listConfigurazioneIncaricoProfiloDto;
	}

//	public static ConfigurazioneIncaricoProfilo convertToModel(ConfigurazioneIncaricoProfiloDto configurazioneIncaricoProfiloDto) {
//		ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo = null;
//		if(configurazioneIncaricoProfiloDto!=null) {
//			configurazioneIncaricoProfilo = new ConfigurazioneIncaricoProfilo();
//			
//			configurazioneIncaricoProfilo.setCodice(configurazioneIncaricoProfiloDto.getCodice());
//			configurazioneIncaricoProfilo.setIdConfigurazioneIncaricoProfilo(configurazioneIncaricoProfiloDto.getIdConfigurazioneIncaricoProfilo());
//			configurazioneIncaricoProfilo.setMultipla(configurazioneIncaricoProfiloDto.isMultipla());
//			configurazioneIncaricoProfilo.setNome(configurazioneIncaricoProfiloDto.getNome());
//			configurazioneIncaricoProfilo.setObbligatoria(configurazioneIncaricoProfiloDto.isObbligatoria());
//			configurazioneIncaricoProfilo.setProponente(configurazioneIncaricoProfiloDto.isProponente());
//			configurazioneIncaricoProfilo.setDataCreazione(configurazioneIncaricoProfiloDto.getDataCreazione());
//			configurazioneIncaricoProfilo.setDataModifica(configurazioneIncaricoProfiloDto.getDataModifica());
//			
//			TipoConfigurazioneIncaricoProfilo tipoConfigurazioneIncaricoProfilo = new TipoConfigurazioneIncaricoProfilo();
//			tipoConfigurazioneIncaricoProfilo.setIdTipoConfigurazioneIncaricoProfilo(configurazioneIncaricoProfiloDto.getTipoConfigurazioneIncaricoProfiloId());
//			tipoConfigurazioneIncaricoProfilo.setNome(configurazioneIncaricoProfiloDto.getNome());
//			configurazioneIncaricoProfilo.setTipoConfigurazioneIncaricoProfilo(tipoConfigurazioneIncaricoProfilo);
//		}
//		return configurazioneIncaricoProfilo;
//	}

}
