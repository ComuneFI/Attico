package it.linksmt.assatti.datalayer.domain.util;

import java.util.ArrayList;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.dto.ProfiloDto;

public class ProfiloTransformer {
	
	public static ProfiloDto toDto(Profilo profilo, Boolean includiAoo, Boolean includiDettagli) {
		ProfiloDto dto = null;
		if(profilo!=null) {
			dto = new ProfiloDto();
			if(includiAoo!=null && includiAoo) {
				dto.setAoo(AooTransformer.toDto(profilo.getAoo()));
			}
			if(includiDettagli!=null && includiDettagli) {
				dto.setGrupporuolo(profilo.getGrupporuolo());
				dto.setHasQualifica(profilo.getHasQualifica());
				dto.setTipiAtto(profilo.getTipiAtto());
				dto.setValidita(profilo.getValidita());
				dto.setPredefinito(profilo.getPredefinito());
			}
			dto.setDelega(profilo.getDelega());
			dto.setDescrizione(profilo.getDescrizione());
			dto.setId(profilo.getId());
			if(profilo.getUtente() != null) {
				profilo.getUtente().setAoos(null);
				dto.setUtente(profilo.getUtente());
			}
		}
		return dto;
	}
	
	public static List<ProfiloDto> toDto(List<Profilo> profili, Boolean includiAoo, Boolean includiDettagli) {
		List<ProfiloDto> dtos = null;
		if(profili!=null) {
			dtos = new ArrayList<ProfiloDto>();
			for(Profilo p : profili) {
				ProfiloDto dto = ProfiloTransformer.toDto(p, includiAoo, includiDettagli);
				if(dto!=null) {
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}
}
