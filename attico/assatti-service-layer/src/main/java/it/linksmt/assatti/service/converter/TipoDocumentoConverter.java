package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.service.dto.TipoDocumentoDto;

public class TipoDocumentoConverter {

	public static TipoDocumentoDto convertToDto(TipoDocumento tipoDocumento) {
		TipoDocumentoDto tipoDocumentoDto = null;
		if(tipoDocumento!=null) {
			tipoDocumentoDto = new TipoDocumentoDto();
			tipoDocumentoDto.setIdTipoDocumento(tipoDocumento.getId());
			tipoDocumentoDto.setCodice(tipoDocumento.getCodice());
			tipoDocumentoDto.setDescrizione(tipoDocumento.getDescrizione());
			tipoDocumentoDto.setRiversamentoTipoatto(tipoDocumento.getRiversamentoTipoatto());
			tipoDocumentoDto.setDmsContentType(tipoDocumento.getDmsContentType());
		}
		return tipoDocumentoDto;
	}

	public static List<TipoDocumentoDto> convertToDto(List<TipoDocumento> elencoTipoDocumento) {
		List<TipoDocumentoDto> elencoTipoDocumentoDto = null;
		if(elencoTipoDocumento!=null) {
			elencoTipoDocumentoDto = new ArrayList<>();
			for(TipoDocumento td : elencoTipoDocumento) {
				elencoTipoDocumentoDto.add(convertToDto(td));
			}
		}
		return elencoTipoDocumentoDto;
	}

	public static TipoDocumento convertToModel(TipoDocumentoDto tipoDocumentoDto) {
		TipoDocumento tipoDocumento = null;
		if(tipoDocumentoDto!=null) {
			tipoDocumento = new TipoDocumento();
			tipoDocumento.setId(tipoDocumentoDto.getIdTipoDocumento());
			tipoDocumento.setCodice(tipoDocumentoDto.getCodice());
			tipoDocumento.setDescrizione(tipoDocumentoDto.getDescrizione());
			tipoDocumento.setRiversamentoTipoatto(tipoDocumentoDto.getRiversamentoTipoatto());
			tipoDocumento.setDmsContentType(tipoDocumentoDto.getDmsContentType());
		}
		return tipoDocumento;
	}

}
