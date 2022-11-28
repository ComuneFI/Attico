package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Campo;
import it.linksmt.assatti.datalayer.domain.CampoTipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.service.dto.CampoTipoAttoDto;

public class CampoTipoAttoConverter {

	public static CampoTipoAttoDto convertToDto(CampoTipoAtto campoTipoAtto) {
		CampoTipoAttoDto campoTipoAttoDto = null;
		if(campoTipoAtto!=null) {
			campoTipoAttoDto = new CampoTipoAttoDto();
			campoTipoAttoDto.setCodice(campoTipoAtto.getCampo().getCodice());
			campoTipoAttoDto.setDescrizione(campoTipoAtto.getCampo().getDescrizione());
			campoTipoAttoDto.setId(campoTipoAtto.getCampo().getId());
			campoTipoAttoDto.setIdTipoAtto(campoTipoAtto.getTipoAtto().getId());
			campoTipoAttoDto.setVisibile(campoTipoAtto.isVisibile());
		}
		return campoTipoAttoDto;
	}
	
	public static List<CampoTipoAttoDto> convertToDto(List<CampoTipoAtto> campoTipoAtto) {
		List<CampoTipoAttoDto> listCampoTipoAttoDto = null;
		if(campoTipoAtto!=null) {
			listCampoTipoAttoDto = new ArrayList<>();
			for(CampoTipoAtto s : campoTipoAtto) {
				CampoTipoAttoDto sd = convertToDto(s);
				if(sd!=null) {
					listCampoTipoAttoDto.add(sd);
				}
			}
		}
		return listCampoTipoAttoDto;
	}

	public static CampoTipoAtto convertToModel(CampoTipoAttoDto campoTipoAttoDto) {
		CampoTipoAtto campoTipoAtto = null;
		if(campoTipoAttoDto!=null) {
			campoTipoAtto = new CampoTipoAtto();
			
			Campo campo = new Campo();
			
			campo.setCodice(campoTipoAttoDto.getCodice());
			campo.setDescrizione(campoTipoAttoDto.getDescrizione());
			campo.setId(campoTipoAttoDto.getId());
			
			campoTipoAtto.setVisibile(campoTipoAttoDto.getVisibile());
			
			campoTipoAtto.setCampo(campo);
			
			TipoAtto tipoAtto = new TipoAtto();
			tipoAtto.setId(campoTipoAttoDto.getIdTipoAtto());
			campoTipoAtto.setTipoAtto(tipoAtto);
		}
		return campoTipoAtto;
	}

}
