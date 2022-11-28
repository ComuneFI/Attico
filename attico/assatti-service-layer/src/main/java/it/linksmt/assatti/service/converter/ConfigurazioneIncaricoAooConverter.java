package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.QualificaProfessionaleDto;
import it.linksmt.assatti.utility.StringUtil;

public class ConfigurazioneIncaricoAooConverter {

	public static ConfigurazioneIncaricoAooDto convertToDto(ConfigurazioneIncaricoAoo configurazioneIncaricoAoo, Date dataIncarico) {
		ConfigurazioneIncaricoAooDto configurazioneIncaricoAooDto = null;
		if(configurazioneIncaricoAoo!=null) {
			configurazioneIncaricoAooDto = new ConfigurazioneIncaricoAooDto();
			configurazioneIncaricoAooDto.setIdAoo(configurazioneIncaricoAoo.getPrimaryKey().getIdAoo());
			configurazioneIncaricoAooDto.setIdConfigurazioneIncarico(configurazioneIncaricoAoo.getPrimaryKey().getIdConfigurazioneIncarico());
			configurazioneIncaricoAooDto.setDataCreazione(configurazioneIncaricoAoo.getDataCreazione());
			configurazioneIncaricoAooDto.setOrdineFirma(configurazioneIncaricoAoo.getOrdineFirma());
			configurazioneIncaricoAooDto.setGiorniScadenza(configurazioneIncaricoAoo.getGiorniScadenza());
			configurazioneIncaricoAooDto.setDataManuale(configurazioneIncaricoAoo.getDataManuale());
			
			if ((dataIncarico != null) && (configurazioneIncaricoAoo.getGiorniScadenza() != null) && (configurazioneIncaricoAoo.getGiorniScadenza().intValue() > 0)) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(dataIncarico);
				cal.add(Calendar.DATE, configurazioneIncaricoAoo.getGiorniScadenza().intValue());
				
				configurazioneIncaricoAooDto.setDataScadenza(StringUtil.IT_DATE_FORMAT.format(cal.getTime()));
			}
			
			QualificaProfessionaleDto qualificaProfessionaleDto = QualificaProfessionaleConverter.convertToDto(configurazioneIncaricoAoo.getQualificaProfessionale());
			configurazioneIncaricoAooDto.setQualificaProfessionaleDto(qualificaProfessionaleDto);
		}
		return configurazioneIncaricoAooDto;
	}
	
	public static List<ConfigurazioneIncaricoAooDto> convertToDto(List<ConfigurazioneIncaricoAoo> configurazioneIncaricoAoo, Date dataIncarico) {
		List<ConfigurazioneIncaricoAooDto> listConfigurazioneIncaricoAooDto = null;
		if(configurazioneIncaricoAoo!=null) {
			listConfigurazioneIncaricoAooDto = new ArrayList<>();
			for(ConfigurazioneIncaricoAoo s : configurazioneIncaricoAoo) {
				ConfigurazioneIncaricoAooDto sd = null;
				if(s.getDataManuale() != null) {
					sd = convertToDto(s, s.getDataManuale().toDate());
				}
				else {
					sd = convertToDto(s, dataIncarico);
				}
				if(sd!=null) {
					listConfigurazioneIncaricoAooDto.add(sd);
				}
			}
		}
		return listConfigurazioneIncaricoAooDto;
	}

//	public static ConfigurazioneIncaricoAoo convertToModel(ConfigurazioneIncaricoAooDto configurazioneIncaricoAooDto) {
//		ConfigurazioneIncaricoAoo configurazioneIncaricoAoo = null;
//		if(configurazioneIncaricoAooDto!=null) {
//			configurazioneIncaricoAoo = new ConfigurazioneIncaricoAoo();
//			
//			configurazioneIncaricoAoo.setCodice(configurazioneIncaricoAooDto.getCodice());
//			configurazioneIncaricoAoo.setIdConfigurazioneIncaricoAoo(configurazioneIncaricoAooDto.getIdConfigurazioneIncaricoAoo());
//			configurazioneIncaricoAoo.setMultipla(configurazioneIncaricoAooDto.isMultipla());
//			configurazioneIncaricoAoo.setNome(configurazioneIncaricoAooDto.getNome());
//			configurazioneIncaricoAoo.setObbligatoria(configurazioneIncaricoAooDto.isObbligatoria());
//			configurazioneIncaricoAoo.setProponente(configurazioneIncaricoAooDto.isProponente());
//			configurazioneIncaricoAoo.setDataCreazione(configurazioneIncaricoAooDto.getDataCreazione());
//			configurazioneIncaricoAoo.setDataModifica(configurazioneIncaricoAooDto.getDataModifica());
//			
//			TipoConfigurazioneIncaricoAoo tipoConfigurazioneIncaricoAoo = new TipoConfigurazioneIncaricoAoo();
//			tipoConfigurazioneIncaricoAoo.setIdTipoConfigurazioneIncaricoAoo(configurazioneIncaricoAooDto.getTipoConfigurazioneIncaricoAooId());
//			tipoConfigurazioneIncaricoAoo.setNome(configurazioneIncaricoAooDto.getNome());
//			configurazioneIncaricoAoo.setTipoConfigurazioneIncaricoAoo(tipoConfigurazioneIncaricoAoo);
//		}
//		return configurazioneIncaricoAoo;
//	}

}
