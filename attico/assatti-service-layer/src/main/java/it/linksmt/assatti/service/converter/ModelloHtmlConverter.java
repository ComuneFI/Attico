package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.service.dto.ModelloHtmlDto;

public class ModelloHtmlConverter {

	public static ModelloHtmlDto convertToDto(ModelloHtml modelloHtml) {
		ModelloHtmlDto modelloHtmlDto = null;
		if(modelloHtml!=null) {
			modelloHtmlDto = new ModelloHtmlDto();
			modelloHtmlDto.setIdModelloHtml(modelloHtml.getId());
			modelloHtmlDto.setTitolo(modelloHtml.getTitolo());
			modelloHtmlDto.setHtml(modelloHtml.getHtml());
			modelloHtmlDto.setPageOrientation(modelloHtml.getPageOrientation());
		}
		return modelloHtmlDto;
	}

	public static List<ModelloHtmlDto> convertToDto(List<ModelloHtml> elencoModelloHtml) {
		List<ModelloHtmlDto> elencoModelloHtmlDto = null;
		if(elencoModelloHtml!=null) {
			elencoModelloHtmlDto = new ArrayList<>();
			for(ModelloHtml td : elencoModelloHtml) {
				elencoModelloHtmlDto.add(convertToDto(td));
			}
		}
		return elencoModelloHtmlDto;
	}

	public static ModelloHtml convertToModel(ModelloHtmlDto modelloHtmlDto) {
		ModelloHtml modelloHtml = null;
		if(modelloHtmlDto!=null) {
			modelloHtml = new ModelloHtml();
			modelloHtml.setId(modelloHtmlDto.getIdModelloHtml());
			modelloHtml.setTitolo(modelloHtmlDto.getTitolo());
			modelloHtml.setHtml(modelloHtmlDto.getHtml());
			modelloHtml.setPageOrientation(modelloHtmlDto.getPageOrientation());
		}
		return modelloHtml;
	}

}
