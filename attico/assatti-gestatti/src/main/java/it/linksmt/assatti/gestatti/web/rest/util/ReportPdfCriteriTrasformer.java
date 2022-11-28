package it.linksmt.assatti.gestatti.web.rest.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.inject.Inject;

import org.springframework.stereotype.Component;

import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.service.TipoAttoService;
import it.linksmt.assatti.service.dto.AttoCriteriaDTO;
import it.linksmt.assatti.service.dto.CriteriReportPdfRicercaDTO;

@Component
public class ReportPdfCriteriTrasformer {
	@Inject
	private TipoAttoService tipoAttoService;
	
	public Map<String, String> generateMap(AttoCriteriaDTO criteri, Properties dizionario){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		Map<String, String> mappa = new HashMap<String, String>();
		if(criteri.getArea()!=null && !criteri.getArea().trim().isEmpty()){
			mappa.put(dizionario.getProperty("aoo"), criteri.getArea());
		}
		if(criteri.getAttoRevocato() != null && !criteri.getAttoRevocato().trim().isEmpty()){
			mappa.put(dizionario.getProperty("attoRiferimento"), criteri.getAttoRevocato());
		}
		if(criteri.getCodiceCifra() != null && !criteri.getCodiceCifra().trim().isEmpty()){
			mappa.put(dizionario.getProperty("codiceCifra"), criteri.getCodiceCifra());
		}
		if(criteri.getCodiceCig() != null && !criteri.getCodiceCig().trim().isEmpty()){
			mappa.put(dizionario.getProperty("codiceCig"), criteri.getCodiceCig());
		}
		if(criteri.getCodiceCup() != null && !criteri.getCodiceCup().trim().isEmpty()){
			mappa.put(dizionario.getProperty("codiceCup"), criteri.getCodiceCup());
		}
		if(criteri.getDataAdozione()!=null){
			mappa.put(dizionario.getProperty("dataAdozione"), df.format(criteri.getDataAdozione().toDate()));
		}
		if(criteri.getDataAdozioneA() != null){
			mappa.put(dizionario.getProperty("dataAdozioneA"), df.format(criteri.getDataAdozioneA().toDate()));
		}
		if(criteri.getDataCreazione()!=null){
			mappa.put(dizionario.getProperty("dataCreazione"), df.format(criteri.getDataCreazione().toDate()));
		}
		if(criteri.getDataCreazioneDa()!=null){
			mappa.put(dizionario.getProperty("dataCreazioneDa"), df.format(criteri.getDataCreazioneDa().toDate()));
		}
		if(criteri.getDataCreazioneA() !=null){
			mappa.put(dizionario.getProperty("dataCreazioneA"), df.format(criteri.getDataCreazioneA().toDate()));
		}
		if(criteri.getDataEsecutiva()!=null){
			mappa.put(dizionario.getProperty("dataEsecutivita"), df.format(criteri.getDataEsecutiva().toDate()));
		}
		if(criteri.getDataIncarico() != null){
			mappa.put(dizionario.getProperty("dataInCarico"), df.format(criteri.getDataIncarico().toDate()));
		}
		if(criteri.getEsitoSeduta()!=null && !criteri.getEsitoSeduta().trim().isEmpty()){
			mappa.put(dizionario.getProperty("esitoSeduta"), criteri.getEsitoSeduta());
		}
		if(criteri.getEstremiSeduta()!=null && !criteri.getEstremiSeduta().trim().isEmpty()){
			mappa.put(dizionario.getProperty("estremiSeduta"), criteri.getEstremiSeduta());
		}
		if(criteri.getInizioPubblicazioneAlbo()!=null){
			mappa.put(dizionario.getProperty("dataInizio"), df.format(criteri.getInizioPubblicazioneAlbo().toDate()));
		}
		if(criteri.getFinePubblicazioneAlbo()!=null){
			mappa.put(dizionario.getProperty("dataFine"), df.format(criteri.getFinePubblicazioneAlbo().toDate()));
		}
		if(criteri.getIncaricoa()!=null && !criteri.getIncaricoa().trim().isEmpty()){
			mappa.put(dizionario.getProperty("inCarico"), criteri.getIncaricoa());
		}
		if(criteri.getIstruttore()!=null && !criteri.getIstruttore().trim().isEmpty()){
			mappa.put(dizionario.getProperty("istruttore"), criteri.getIstruttore());
		}
		if(criteri.getLastModifiedDateDa()!=null){
			mappa.put(dizionario.getProperty("dataFineIterA"), df.format(criteri.getLastModifiedDateDa().toDate()));
		}
		if(criteri.getLastModifiedDateA()!=null){
			mappa.put(dizionario.getProperty("dataFineIterDa"), df.format(criteri.getLastModifiedDateA().toDate()));
		}
//		if(criteri.getLuogoAdozione()!=null && !criteri.getLuogoAdozione().trim().isEmpty()){
//			mappa.put(dizionario.getProperty("dataFineIter"), criteri.getLuogoAdozione());
//		}
		if(criteri.getTipoIter()!=null && !criteri.getTipoIter().trim().isEmpty()){
			mappa.put(dizionario.getProperty("tipoIter"), criteri.getTipoIter());
		}
		if(criteri.getTipoFinanziamento()!=null && !criteri.getTipoFinanziamento().trim().isEmpty()){
			mappa.put(dizionario.getProperty("tipoFinanziamento"), criteri.getTipoFinanziamento());
		}
		if(criteri.getNumeroAdozione()!=null && !criteri.getNumeroAdozione().trim().isEmpty()){
			mappa.put(dizionario.getProperty("numeroAdozione"), criteri.getNumeroAdozione());
		}
		if(criteri.getOggetto()!=null && !criteri.getOggetto().trim().isEmpty()){
			mappa.put(dizionario.getProperty("oggetto"), criteri.getOggetto());
		}
		if(criteri.getRegolamento()!=null && !criteri.getRegolamento().trim().isEmpty()){
			mappa.put(dizionario.getProperty("regolamento"), criteri.getRegolamento());
		}
		if(criteri.getStato()!= null && !criteri.getStato().trim().isEmpty()){
			mappa.put(dizionario.getProperty("stato"), criteri.getStato());
		}
		if(criteri.getTipoProvvedimento()!=null && !criteri.getTipoProvvedimento().trim().isEmpty()){
			mappa.put(dizionario.getProperty("tipoProvvedimento"), criteri.getTipoProvvedimento());
		}
		if(criteri.getStatoRelata() != null && !criteri.getStatoRelata().trim().isEmpty()){
			mappa.put(dizionario.getProperty("statoRelata"), criteri.getStatoRelata());
		}
		if(criteri.getStatoPubblicazione() != null && !criteri.getStatoPubblicazione().trim().isEmpty()){
			mappa.put(dizionario.getProperty("sStatoPubblicazione"), criteri.getStatoPubblicazione());
		}
		if(criteri.getStatoProceduraPubblicazione() != null && !criteri.getStatoProceduraPubblicazione().trim().isEmpty()){
			mappa.put(dizionario.getProperty("statoProceduraPubblicazione"), criteri.getStatoProceduraPubblicazione());
		}
		return mappa;
	}
	
	public CriteriReportPdfRicercaDTO toPdfDto(AttoCriteriaDTO criteri){
		CriteriReportPdfRicercaDTO pdfDto = new CriteriReportPdfRicercaDTO();
		pdfDto.setAnno(criteri.getAnno() != null ? criteri.getAnno() : "");
		if(criteri.getTipoAtto()!=null && !criteri.getTipoAtto().equalsIgnoreCase("sedute")){
			TipoAtto tipoAtto = tipoAttoService.findByCodice(criteri.getTipoAtto());
			if(tipoAtto!=null){
				pdfDto.setTipoAtto(tipoAtto.getDescrizione());
			}else{
				pdfDto.setTipoAtto("");
			}
		}else{
			pdfDto.setTipoAtto("");
		}
		if(criteri.getViewtype()!=null){
		switch (criteri.getViewtype()) {
		case "jobPubblicaziones":
			pdfDto.setTipoAtto("");
			pdfDto.setAnno("");
			
			
			break;
			
		case "jobTrasparenzas":
			pdfDto.setTipoAtto("");
			pdfDto.setAnno("");
			
			
			break;
		
		
			case "determinevercontab":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
					TipoAtto tipoAtto = tipoAttoService.findByCodice("DIR");
					if(tipoAtto!=null){
						pdfDto.setTipoAtto(tipoAtto.getDescrizione());
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				pdfDto.setViewtype("Con verifica contabile");
				break;
			case "deliberecontabile":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
					TipoAtto tipoAtto = tipoAttoService.findByCodice("DEL");
					if(tipoAtto!=null){
						pdfDto.setTipoAtto(tipoAtto.getDescrizione());
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				pdfDto.setViewtype("Con verifica contabile");
				break;
			case "deliberesenzacontabile":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
					TipoAtto tipoAtto = tipoAttoService.findByCodice("DEL");
					if(tipoAtto!=null){
						pdfDto.setTipoAtto(tipoAtto.getDescrizione());
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				pdfDto.setViewtype("Senza verifica contabile");
				break;
			case "tutti":
				pdfDto.setViewtype("Tutti");
				if(criteri.getTipiAttoIds()!=null && !criteri.getTipiAttoIds().isEmpty()){
					List<TipoAtto> tipiAtto = tipoAttoService.findByIdIn(criteri.getTipiAttoIds());
					if(tipiAtto!=null){
						String tipoAtto = "";
						for(TipoAtto ta : tipiAtto){
							if(tipoAtto.isEmpty()){
								tipoAtto = ta.getDescrizione();
							}else{
								tipoAtto += " - " + ta.getDescrizione();
							}
						}
						pdfDto.setTipoAtto(tipoAtto);
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				break;
			case "elencopersedute":
				pdfDto.setViewtype("Elenco atti per seduta");
				break;
			case "pubblicati":
				pdfDto.setViewtype("Concluse");
				if(criteri.getTipiAttoIds()!=null && !criteri.getTipiAttoIds().isEmpty()){
					List<TipoAtto> tipiAtto = tipoAttoService.findByIdIn(criteri.getTipiAttoIds());
					if(tipiAtto!=null){
						String tipoAtto = "";
						for(TipoAtto ta : tipiAtto){
							if(tipoAtto.isEmpty()){
								tipoAtto = ta.getDescrizione();
							}else{
								tipoAtto += " - " + ta.getDescrizione();
							}
						}
						pdfDto.setTipoAtto(tipoAtto);
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				break;
			case "itinere":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
				//	TipoAtto tipoAtto = tipoAttoService.findByCodice("DIR");
				//	if(tipoAtto!=null){
				//		pdfDto.setTipoAtto(tipoAtto.getDescrizione());
				//	}else{
						pdfDto.setTipoAtto("");
				//	}
				}
				pdfDto.setViewtype("In itinere");
				break;
			case "determineesec":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
					TipoAtto tipoAtto = tipoAttoService.findByCodice("DIR");
					if(tipoAtto!=null){
						pdfDto.setTipoAtto(tipoAtto.getDescrizione());
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				pdfDto.setViewtype("Direttamente esecutive");
				break;
			case "determinecontab":
				if(pdfDto.getTipoAtto()==null || pdfDto.getTipoAtto().isEmpty()){
					TipoAtto tipoAtto = tipoAttoService.findByCodice("DIR");
					if(tipoAtto!=null){
						pdfDto.setTipoAtto(tipoAtto.getDescrizione());
					}else{
						pdfDto.setTipoAtto("");
					}
				}
				pdfDto.setViewtype("Con adempimenti contabili");
				break;
			default:
				pdfDto.setViewtype("");
				break;
		}
		}else{
			pdfDto.setViewtype("");
		}
		return pdfDto;
	}
}