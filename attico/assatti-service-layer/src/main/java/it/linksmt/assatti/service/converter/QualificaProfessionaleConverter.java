package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.service.dto.QualificaProfessionaleDto;

public class QualificaProfessionaleConverter {

	public static QualificaProfessionaleDto convertToDto(QualificaProfessionale qualificaProfessionale) {
		QualificaProfessionaleDto qualificaProfessionaleDto = null;
		if(qualificaProfessionale!=null) {
			qualificaProfessionaleDto = new QualificaProfessionaleDto();
			qualificaProfessionaleDto.setDenominazione(qualificaProfessionale.getDenominazione());
			qualificaProfessionaleDto.setEnabled(qualificaProfessionale.getEnabled());
			qualificaProfessionaleDto.setId(qualificaProfessionale.getId());
		}
		return qualificaProfessionaleDto;
	}
	
	public static List<QualificaProfessionaleDto> convertToDto(List<QualificaProfessionale> qualificaProfessionale) {
		List<QualificaProfessionaleDto> listQualificaProfessionaleDto = null;
		if(qualificaProfessionale!=null) {
			listQualificaProfessionaleDto = new ArrayList<>();
			for(QualificaProfessionale s : qualificaProfessionale) {
				QualificaProfessionaleDto sd = convertToDto(s);
				if(sd!=null) {
					listQualificaProfessionaleDto.add(sd);
				}
			}
		}
		return listQualificaProfessionaleDto;
	}

	public static QualificaProfessionale convertToModel(QualificaProfessionaleDto qualificaProfessionaleDto) {
		QualificaProfessionale qualificaProfessionale = null;
		if(qualificaProfessionaleDto!=null) {
			qualificaProfessionale = new QualificaProfessionale();
			
			qualificaProfessionale.setDenominazione(qualificaProfessionaleDto.getDenominazione());
			qualificaProfessionale.setEnabled(qualificaProfessionaleDto.getEnabled());
			qualificaProfessionale.setId(qualificaProfessionaleDto.getId());
		}
		return qualificaProfessionale;
	}

}
