package it.linksmt.assatti.datalayer.domain.util;

import org.springframework.beans.BeanUtils;

import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.dto.AttiOdgWithIEDto;

public class AttiOdgWithIEDtoTransformer {
	public static AttiOdgWithIEDto toAttiOdgWithIEDto(AttiOdg aOdg) {
		AttiOdgWithIEDto aOdgWithIE = null;
		if(aOdg!=null) {
			aOdgWithIE = new AttiOdgWithIEDto();
			BeanUtils.copyProperties(aOdg, aOdgWithIE);
			aOdgWithIE.setNumFavorevoliIE(0);
			aOdgWithIE.setNumContrariIE(0);
			aOdgWithIE.setNumAstenutiIE(0);
			aOdgWithIE.setNumNpvIE(0);
			aOdgWithIE.setNumPresentiIE(0);
			if(aOdg.getComponenti()!=null && aOdg.getComponenti().size() > 0) {
				for(ComponentiGiunta comp : aOdg.getComponenti()) {
					if(comp!=null) {
						if(comp.getVotazioneIE()!=null && !comp.getVotazioneIE().trim().isEmpty()) {
							if(comp.getPresenteIE()!=null && comp.getPresenteIE()) {
								aOdgWithIE.setNumPresentiIE(aOdgWithIE.getNumPresentiIE() + 1);
							}
							if(comp.getVotazioneIE().equalsIgnoreCase("FAV")) {
								aOdgWithIE.setNumFavorevoliIE(aOdgWithIE.getNumFavorevoliIE() + 1);
							}else if(comp.getVotazioneIE().equalsIgnoreCase("AST")) {
								aOdgWithIE.setNumAstenutiIE(aOdgWithIE.getNumAstenutiIE() + 1);
							}else if(comp.getVotazioneIE().equalsIgnoreCase("CON")) {
								aOdgWithIE.setNumContrariIE(aOdgWithIE.getNumContrariIE() + 1);
							}else if(comp.getVotazioneIE().equalsIgnoreCase("NPV")) {
								aOdgWithIE.setNumNpvIE(aOdgWithIE.getNumNpvIE() + 1);
							}
						}
					}
				}
			}
		}
		return aOdgWithIE;
	}
}
