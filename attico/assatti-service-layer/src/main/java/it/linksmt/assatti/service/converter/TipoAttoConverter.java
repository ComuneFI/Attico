package it.linksmt.assatti.service.converter;

import java.util.TreeSet;

import it.linksmt.assatti.datalayer.domain.ColonneRicercaHiddenEnum;
import it.linksmt.assatti.datalayer.domain.FaseRicercaHasCriterio;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.StatoConclusoEnum;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAttoHasFaseRicerca;
import it.linksmt.assatti.service.dto.TipoAttoDto;

public class TipoAttoConverter {

	public static TipoAttoDto convertToDto(TipoAtto tipoAtto) {
		TipoAttoDto tipoAttoDto = null;
		if(tipoAtto!=null) {
			tipoAttoDto = new TipoAttoDto();
			tipoAttoDto.setEnabled(tipoAtto.getEnabled());
			tipoAttoDto.setAtti(tipoAtto.getAtti());
			tipoAttoDto.setCodice(tipoAtto.getCodice());
			tipoAttoDto.setDescrizione(tipoAtto.getDescrizione());
			tipoAttoDto.setId(tipoAtto.getId());
			tipoAttoDto.setProcessoBpmName(tipoAtto.getProcessoBpmName());
			tipoAttoDto.setGiorniPubblicazioneAlbo(tipoAtto.getGiorniPubblicazioneAlbo());
			tipoAttoDto.setProgressivoPropostaAoo(tipoAtto.getProgressivoPropostaAoo());
			tipoAttoDto.setProgressivoAdozioneAoo(tipoAtto.getProgressivoAdozioneAoo());
			tipoAttoDto.setStatiAnnullamento(tipoAtto.getStatiAnnullamento());
			tipoAttoDto.setTipiIter(tipoAtto.getTipiIter());
			tipoAttoDto.setTipoProgressivo(tipoAtto.getTipoProgressivo());
			if(tipoAtto.getModelloHtmlCopiaNonConformeId()!=null) {
				ModelloHtml modelloHtml = new ModelloHtml();
				modelloHtml.setId(tipoAtto.getModelloHtmlCopiaNonConformeId());
				tipoAttoDto.setModelloHtmlCopiaNonConforme(modelloHtml);
			}
			tipoAttoDto.setProponente(tipoAtto.getProponente());
			tipoAttoDto.setConsiglio(tipoAtto.getConsiglio());
			tipoAttoDto.setGiunta(tipoAtto.getGiunta());
			
			if(tipoAtto.getStatoConclusoRicerca()!=null && !tipoAtto.getStatoConclusoRicerca().isEmpty()) {
				String[] statiConclusoStr = tipoAtto.getStatoConclusoRicerca().split("\\|");
				for(int i = 0; i < statiConclusoStr.length; i++) {
					if(StatoConclusoEnum.COMPLETO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoCompleto(true);
					}else if(StatoConclusoEnum.RITIRATO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoRitirato(true);
					}else if(StatoConclusoEnum.ANNULLATO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoAnnullato(true);
					}else if(StatoConclusoEnum.RESPINTO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoRespinto(true);
					}else if(StatoConclusoEnum.DECADUTO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoDecaduto(true);
					}else if(StatoConclusoEnum.DATA_ESECUTIVITA.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoDataEsecutivita(true);
					}else if(StatoConclusoEnum.ATTESA_RELATA.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoAttesaRelata(true);
					}else if(StatoConclusoEnum.ARCHIVIATO.getCodice().equals(statiConclusoStr[i])) {
						tipoAttoDto.setStatoConclusoArchiviato(true);
					}
				}
			}
			
			//ordinamento
			if(tipoAtto.getFasiRicerca()!=null && tipoAtto.getFasiRicerca().size() > 0) {
				tipoAttoDto.setFasiRicerca(new TreeSet<TipoAttoHasFaseRicerca>(tipoAtto.getFasiRicerca()));
				for(TipoAttoHasFaseRicerca fase : tipoAttoDto.getFasiRicerca()) {
					if(fase.getCriteri()!=null && fase.getCriteri().size() > 0) {
						fase.setCriteri(new TreeSet<FaseRicercaHasCriterio>(fase.getCriteri()));
					}
				}
			}
			
			setColonneNascosteInDto(tipoAttoDto, tipoAtto);
		}
		return tipoAttoDto;
	}
	
	public static TipoAttoDto getSimpleDto(TipoAtto entity) {
		TipoAttoDto dto = new TipoAttoDto();
		dto.setCodice(entity.getCodice());
		dto.setId(entity.getId());
		setColonneNascosteInDto(dto, entity);
		return dto;
	}
	
	private static void setColonneNascosteInDto(TipoAttoDto dto, TipoAtto entity) {
		if(entity.getColonneNascosteRicerca()!=null && !entity.getColonneNascosteRicerca().isEmpty()) {
			String[] colonneNascosteStr = entity.getColonneNascosteRicerca().split("\\|");
			for(int i = 0; i < colonneNascosteStr.length; i++) {
				if(ColonneRicercaHiddenEnum.ATTO_REVOCATO.toString().equals(colonneNascosteStr[i])) {
					dto.setAttoRevocatoHidden(true);
				}else if(ColonneRicercaHiddenEnum.TIPO_ITER.toString().equals(colonneNascosteStr[i])) {
					dto.setTipoIterHidden(true);
				}else if(ColonneRicercaHiddenEnum.CUP.toString().equals(colonneNascosteStr[i])) {
					dto.setCodiceCupHidden(true);
				}else if(ColonneRicercaHiddenEnum.CIG.toString().equals(colonneNascosteStr[i])) {
					dto.setCodiceCigHidden(true);
				}else if(ColonneRicercaHiddenEnum.TIPO_FINANZIAMENTO.toString().equals(colonneNascosteStr[i])) {
					dto.setTipoFinanziamentoHidden(true);
				}
			}
		}
	}

	public static TipoAtto convertToModel(TipoAttoDto tipoAttoDto) {
		TipoAtto tipoAtto = null;
		if(tipoAttoDto!=null) {
			tipoAtto = new TipoAtto();
			tipoAtto.setEnabled(tipoAttoDto.getEnabled());
			tipoAtto.setAtti(tipoAttoDto.getAtti());
			tipoAtto.setCodice(tipoAttoDto.getCodice());
			tipoAtto.setDescrizione(tipoAttoDto.getDescrizione());
			tipoAtto.setId(tipoAttoDto.getId());
			tipoAtto.setProcessoBpmName(tipoAttoDto.getProcessoBpmName());
			tipoAtto.setGiorniPubblicazioneAlbo(tipoAttoDto.getGiorniPubblicazioneAlbo());
			tipoAtto.setProgressivoPropostaAoo(tipoAttoDto.getProgressivoPropostaAoo());
			tipoAtto.setProgressivoAdozioneAoo(tipoAttoDto.getProgressivoAdozioneAoo());
			tipoAtto.setStatiAnnullamento(tipoAttoDto.getStatiAnnullamento());
			tipoAtto.setTipiIter(tipoAttoDto.getTipiIter());
			tipoAtto.setTipoProgressivo(tipoAttoDto.getTipoProgressivo());
			if(tipoAttoDto.getModelloHtmlCopiaNonConforme()!=null) {
				tipoAtto.setModelloHtmlCopiaNonConformeId(tipoAttoDto.getModelloHtmlCopiaNonConforme().getId());
			}
			tipoAtto.setProponente(tipoAttoDto.getProponente());
			tipoAtto.setConsiglio(tipoAttoDto.getConsiglio());
			tipoAtto.setGiunta(tipoAttoDto.getGiunta());
			tipoAtto.setFasiRicerca(tipoAttoDto.getFasiRicerca());
			
			String colonneNascoste = null;
			
			if(tipoAttoDto.getAttoRevocatoHidden()!=null && tipoAttoDto.getAttoRevocatoHidden()) {
				colonneNascoste = ColonneRicercaHiddenEnum.ATTO_REVOCATO.toString();
			}
			if(tipoAttoDto.getTipoIterHidden()!=null && tipoAttoDto.getTipoIterHidden()) {
				colonneNascoste = colonneNascoste != null ? colonneNascoste + "|" : "";
				colonneNascoste += ColonneRicercaHiddenEnum.TIPO_ITER.toString();
			}
			if(tipoAttoDto.getCodiceCupHidden()!=null && tipoAttoDto.getCodiceCupHidden()) {
				colonneNascoste = colonneNascoste != null ? colonneNascoste + "|" : "";
				colonneNascoste += ColonneRicercaHiddenEnum.CUP.toString();
			}
			if(tipoAttoDto.getCodiceCigHidden()!=null && tipoAttoDto.getCodiceCigHidden()) {
				colonneNascoste = colonneNascoste != null ? colonneNascoste + "|" : "";
				colonneNascoste += ColonneRicercaHiddenEnum.CIG.toString();
			}
			if(tipoAttoDto.getTipoFinanziamentoHidden()!=null && tipoAttoDto.getTipoFinanziamentoHidden()) {
				colonneNascoste = colonneNascoste != null ? colonneNascoste + "|" : "";
				colonneNascoste += ColonneRicercaHiddenEnum.TIPO_FINANZIAMENTO.toString();
			}
			tipoAtto.setColonneNascosteRicerca(colonneNascoste);
			
			
			String statiConcluso = null;
			
			if(tipoAttoDto.getStatoConclusoCompleto()!=null && tipoAttoDto.getStatoConclusoCompleto()) {
				statiConcluso = StatoConclusoEnum.COMPLETO.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoRitirato()!=null && tipoAttoDto.getStatoConclusoRitirato()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.RITIRATO.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoAnnullato()!=null && tipoAttoDto.getStatoConclusoAnnullato()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.ANNULLATO.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoRespinto()!=null && tipoAttoDto.getStatoConclusoRespinto()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.RESPINTO.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoDecaduto()!=null && tipoAttoDto.getStatoConclusoDecaduto()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.DECADUTO.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoDataEsecutivita()!=null && tipoAttoDto.getStatoConclusoDataEsecutivita()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.DATA_ESECUTIVITA.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoAttesaRelata()!=null && tipoAttoDto.getStatoConclusoAttesaRelata()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.ATTESA_RELATA.getCodice();
			}
			if(tipoAttoDto.getStatoConclusoArchiviato()!=null && tipoAttoDto.getStatoConclusoArchiviato()) {
				statiConcluso = statiConcluso != null ? statiConcluso + "|" : "";
				statiConcluso += StatoConclusoEnum.ARCHIVIATO.getCodice();
			}
			tipoAtto.setStatoConclusoRicerca(statiConcluso);
		}
		return tipoAtto;
	}

}