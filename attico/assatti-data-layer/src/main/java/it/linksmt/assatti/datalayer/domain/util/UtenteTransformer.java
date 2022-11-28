package it.linksmt.assatti.datalayer.domain.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;

public class UtenteTransformer {
	public static UtenteDto toDto(Utente utente) {
		UtenteDto dto = null;
		if(utente!=null) {
			dto = new UtenteDto();
			dto.setId(utente.getId());
			dto.setName(utente.getNome());
			dto.setUsername(utente.getUsername());
		}
		return dto;
	}
	
	public static List<UtenteDto> toDto(Collection<Utente> utentes) {
		List<UtenteDto> dtos = null;
		if(utentes!=null) {
			dtos = new ArrayList<UtenteDto>();
			for(Utente utente : utentes) {
				UtenteDto dto = UtenteTransformer.toDto(utente);
				if(dto!=null) {
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}
}
