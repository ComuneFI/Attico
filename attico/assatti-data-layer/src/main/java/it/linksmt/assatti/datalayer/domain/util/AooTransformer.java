package it.linksmt.assatti.datalayer.domain.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.dto.AooDto;

public class AooTransformer {
	public static AooDto toDto(Aoo aoo) {
		return AooTransformer.toDto(aoo, false, false, false);
	}
	
	public static AooDto toDto(Aoo aoo, boolean includiAooPadre, boolean includiSottoAoo, boolean includiIndirizzo) {
		AooDto dto = null;
		if(aoo!=null) {
			dto = new AooDto();
			dto.setCodice(aoo.getCodice());
			dto.setDataType(aoo.getDataType());
			dto.setDescrizione(aoo.getDescrizione());
			dto.setEmail(aoo.getEmail());
			dto.setFax(aoo.getFax());
			dto.setId(aoo.getId());
			dto.setIdentitavisiva(aoo.getIdentitavisiva());
			if(includiIndirizzo) {
				dto.setIndirizzo(aoo.getIndirizzo());
			}
			dto.setLogo(aoo.getLogo());
			dto.setPec(aoo.getPec());
			dto.setProfiloResponsabile(ProfiloTransformer.toDto(aoo.getProfiloResponsabile(), false, false));
			dto.setProfiloResponsabileId(aoo.getProfiloResponsabileId());
			dto.setStato(aoo.getStato());
			dto.setTelefono(aoo.getTelefono());
			dto.setTipoAoo(aoo.getTipoAoo());
			dto.setUo(aoo.getUo());
			dto.setValidita(aoo.getValidita());
			if(includiAooPadre) {
				dto.setAooPadre(AooTransformer.toDto(aoo.getAooPadre(), false, false, false));
			}
			if(includiSottoAoo && aoo.getSottoAoo()!=null && aoo.getSottoAoo().size() > 0) {
				dto.setSottoAoo(new HashSet<AooDto>(AooTransformer.toDto(aoo.getSottoAoo(), false, includiSottoAoo, includiIndirizzo)));
			}else {
				dto.setSottoAoo(new HashSet<AooDto>());
			}
		}
		return dto;
	}
	
	public static List<AooDto> toDto(Collection<Aoo> aoos) {
		return AooTransformer.toDto(aoos, false, false, false);
	}
	
	public static List<AooDto> toDto(Collection<Aoo> aoos, boolean includiAooPadre, boolean includiSottoAoo, boolean includiIndirizzo) {
		List<AooDto> dtos = null;
		if(aoos!=null) {
			dtos = new ArrayList<AooDto>();
			for(Aoo aoo : aoos) {
				AooDto dto = AooTransformer.toDto(aoo, includiAooPadre, includiSottoAoo, includiIndirizzo);
				if(dto!=null) {
					dtos.add(dto);
				}
			}
		}
		return dtos;
	}
}
