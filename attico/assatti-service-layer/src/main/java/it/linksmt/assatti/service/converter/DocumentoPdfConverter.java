package it.linksmt.assatti.service.converter;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.dto.DocumentoPdfDto;

public class DocumentoPdfConverter {

	public static DocumentoPdfDto convertToDto(DocumentoPdf documentoPdf, Aoo aooSerieDoc) {
		DocumentoPdfDto dto = null;
		if(documentoPdf!=null) {
			dto = new DocumentoPdfDto(documentoPdf.getId(), documentoPdf.getFile().getNomeFile(), "", aooSerieDoc, null, documentoPdf.getTipoDocumento());
		}
		return dto;
	}


}
