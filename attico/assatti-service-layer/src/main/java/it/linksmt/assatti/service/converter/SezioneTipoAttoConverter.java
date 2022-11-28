package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Sezione;
import it.linksmt.assatti.datalayer.domain.SezioneTipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;

public class SezioneTipoAttoConverter {

	public static SezioneTipoAttoDto convertToDto(SezioneTipoAtto sezioneTipoAtto) {
		SezioneTipoAttoDto sezioneTipoAttoDto = null;
		if(sezioneTipoAtto!=null) {
			sezioneTipoAttoDto = new SezioneTipoAttoDto();
			sezioneTipoAttoDto.setCodice(sezioneTipoAtto.getSezione().getCodice());
			sezioneTipoAttoDto.setDescrizione(sezioneTipoAtto.getSezione().getDescrizione());
			sezioneTipoAttoDto.setId(sezioneTipoAtto.getSezione().getId());
			sezioneTipoAttoDto.setIdTipoAtto(sezioneTipoAtto.getTipoAtto().getId());
			sezioneTipoAttoDto.setVisibile(sezioneTipoAtto.isVisibile());
		}
		return sezioneTipoAttoDto;
	}
	
	public static List<SezioneTipoAttoDto> convertToDto(List<SezioneTipoAtto> sezioneTipoAtto) {
		List<SezioneTipoAttoDto> listSesioneTipoAttoDto = null;
		if(sezioneTipoAtto!=null) {
			listSesioneTipoAttoDto = new ArrayList<>();
			for(SezioneTipoAtto s : sezioneTipoAtto) {
				SezioneTipoAttoDto sd = convertToDto(s);
				if(sd!=null) {
					listSesioneTipoAttoDto.add(sd);
				}
			}
		}
		return listSesioneTipoAttoDto;
	}

	public static SezioneTipoAtto convertToModel(SezioneTipoAttoDto sezioneTipoAttoDto) {
		SezioneTipoAtto sezioneTipoAtto = null;
		if(sezioneTipoAttoDto!=null) {
			sezioneTipoAtto = new SezioneTipoAtto();
			
			Sezione sezione = new Sezione();
			
			sezione.setCodice(sezioneTipoAttoDto.getCodice());
			sezione.setDescrizione(sezioneTipoAttoDto.getDescrizione());
			sezione.setId(sezioneTipoAttoDto.getId());
			
			sezioneTipoAtto.setVisibile(sezioneTipoAttoDto.getVisibile());
			
			sezioneTipoAtto.setSezione(sezione);
			
			TipoAtto tipoAtto = new TipoAtto();
			tipoAtto.setId(sezioneTipoAttoDto.getIdTipoAtto());
			sezioneTipoAtto.setTipoAtto(tipoAtto);
		}
		return sezioneTipoAtto;
	}

}
