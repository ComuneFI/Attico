package it.linksmt.assatti.service.converter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Delega;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.dto.DelegaDto;

public class DelegaConverter {

	public static DelegaDto convertToDto(Delega delega) {
		DelegaDto delegaDto = null;
		if(delega!=null) {
			delegaDto = new DelegaDto();
			delegaDto.setId(delega.getId());
			Profilo delegante = delega.getProfiloDelegante();
			if(delegante!=null && delegante.getAoo()!=null) {
				delegante.setAoo(new Aoo(delegante.getAoo().getId(), delegante.getAoo().getDescrizione(), delegante.getAoo().getCodice(), null));
			}
			delegaDto.setProfiloDelegante(delegante);
			delegaDto.setDataValiditaFine(delega.getDataValiditaFine());
			delegaDto.setDataValiditaInizio(delega.getDataValiditaInizio());
			delegaDto.setDataCreazione(delega.getDataCreazione());
			delegaDto.setEnabled(delega.getEnabled());
			
			List<Profilo> delegati = null;
			if(delega.getDelegati()!=null) {
				delegati = new ArrayList<>();
				for(Profilo delegato : delega.getDelegati()) {
					delegati.add(delegato);
					delegato.setAoo(new Aoo(delegato.getAoo().getId(), delegato.getAoo().getDescrizione(), delegato.getAoo().getCodice(), null));
				}
			}
			delegaDto.setDelegati(delegati);
		}
		return delegaDto;
	}
	
	public static List<DelegaDto> convertToDto(List<Delega> delega) {
		List<DelegaDto> listDelega = null;
		if(delega!=null) {
			listDelega = new ArrayList<>();
			for(Delega d : delega) {
				listDelega.add(convertToDto(d));
			}
		}
		return listDelega;
	}

	public static Delega convertToModel(DelegaDto delegaDto) {
		Delega delega = null;
		if(delegaDto!=null) {
			delega = new Delega();
			delega.setId(delegaDto.getId());
			delega.setProfiloDelegante(delegaDto.getProfiloDelegante());
			delega.setDataValiditaFine(delegaDto.getDataValiditaFine());
			delega.setDataValiditaInizio(delegaDto.getDataValiditaInizio());
			delega.setDataCreazione(delegaDto.getDataCreazione());
			delega.setEnabled(delegaDto.getEnabled());
			
			Set<Profilo> delegati = null;
			if(delegaDto.getDelegati()!=null) {
				delegati = new HashSet<>();
				for(Profilo delegato : delegaDto.getDelegati()) {
					if(delegato!=null && delegato.getAoo()!=null) {
						delegato.setAoo(new Aoo(delegato.getAoo().getId(), delegato.getAoo().getDescrizione(), delegato.getAoo().getCodice(), null));
					}
					delegati.add(delegato);
				}
			}
			delega.setDelegati(delegati);
		}
		return delega;
	}

}
