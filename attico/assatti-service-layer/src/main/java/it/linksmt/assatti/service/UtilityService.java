package it.linksmt.assatti.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.domain.StatoRelataEnum;
import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;
import it.linksmt.assatti.datalayer.domain.dto.RigaAssentiDto;
import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;

@Service
public class UtilityService {
	
	private final static Logger log = LoggerFactory.getLogger(UtilityService.class);
	
	@Inject
	private EsitoService esitoService;
		
	@Inject
	private JobPubblicazioneService jobPubblicazioneService;
	
	@Inject
	private UtenteService utenteService;
	
	public List<Map<String, String>> listMapAtti(List<Atto> atti, List<String> colNamesFilter){
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
		List<Map<String, String>> listMap = new ArrayList<Map<String, String>>();
		if(atti!=null){
			for(Atto atto : atti){
				Map<String, String> attoMap = new HashMap<String, String>();
				if(colNamesFilter.contains("estremiSeduta")){
					String estremiSeduta = "";
					if(atto.getSedutaGiunta()!=null && atto.getSedutaGiunta().getPrimaConvocazioneInizio()!=null){
						estremiSeduta += df.format(atto.getSedutaGiunta().getPrimaConvocazioneInizio().toDate());
					}
					if(atto.getSedutaGiunta()!=null && atto.getSedutaGiunta().getTipoSeduta()!=null){
						if(!estremiSeduta.isEmpty()){
							estremiSeduta += " ";
						}
						estremiSeduta += atto.getSedutaGiunta().getTipoSeduta().equals(1) ? "Ordinaria" : "Straordinaria";
					}
					attoMap.put("estremiSeduta", estremiSeduta);
				}
				if(colNamesFilter.contains("esitoSeduta")){
					if(atto.getEsito()!=null && !atto.getEsito().isEmpty()){
						Esito esito = esitoService.findById(atto.getEsito());
						if(esito!=null){
							attoMap.put("esitoSeduta", esito.getLabel());
						}
					}
				}
				if(colNamesFilter.contains("regolamento")){
					String regolamento = "";
					if(atto.getRegolamento()!=null && !atto.getRegolamento().isEmpty()){
						if(atto.getRegolamento().equalsIgnoreCase("provvisorio")){
							regolamento = "Regolamento provvisorio";
						}else if(atto.getRegolamento().equalsIgnoreCase("definitivo")){
							regolamento = "Regolamento definitivo";
						}else if(atto.getRegolamento().equalsIgnoreCase("no")){
							regolamento = "NO";
						}
					}
					attoMap.put("regolamento", regolamento);
				}
//				if(colNamesFilter.contains("tipoProvvedimento")){
//					attoMap.put("tipoProvvedimento", atto.getTipoProvvedimento()!=null ? atto.getTipoProvvedimento() : "");
//				}
				if(colNamesFilter.contains("attoRiferimento")){
					attoMap.put("attoRiferimento", atto.getCodicecifraAttoRevocato()!=null ? atto.getCodicecifraAttoRevocato() : "");
				}
//				if(colNamesFilter.contains("relatore")){
//					attoMap.put("relatore", atto.getDenominazioneRelatore()!=null ? atto.getDenominazioneRelatore() : "");
//				}
				if(colNamesFilter.contains("assenti")){
					attoMap.put("assenti", atto.getAssenti()!=null ? atto.getAssenti() : "");
				}
				
				
				if(colNamesFilter.contains("codiceCifra")){
					attoMap.put("codiceCifra", atto.getCodiceCifra());
				}
				if(colNamesFilter.contains("codiceAtto")){
					attoMap.put("codiceAtto", atto.getCodiceCifra());
				}
				if(colNamesFilter.contains("codiceCig")){
					attoMap.put("codiceCig", atto.getCodiceCig());
				}
				if(colNamesFilter.contains("codiceCup")){
					attoMap.put("codiceCup", atto.getCodiceCup());
				}
				if(colNamesFilter.contains("dataCreazione")){
					attoMap.put("dataCreazione", atto.getDataCreazione() != null ? df.format(atto.getDataCreazione().toDate()) : "");
				}
				if(colNamesFilter.contains("oggetto")){
					attoMap.put("oggetto", atto.getOggetto() != null ? atto.getOggetto() : "");
				}
				if(colNamesFilter.contains("aoo")){
					attoMap.put("aoo", atto.getAoo() != null ? atto.getAoo().getCodice() + "-" + atto.getAoo().getDescrizione() : "");
				}
				if(colNamesFilter.contains("strutturaProponente")){
					attoMap.put("strutturaProponente", atto.getAoo() != null ? atto.getAoo().getCodice() + "-" + atto.getAoo().getDescrizione() : "");
				}
				if(colNamesFilter.contains("tipoAtto")){
					attoMap.put("tipoAtto", atto.getTipoAtto() != null ? atto.getTipoAtto().getDescrizione() : "");
				}
				if(colNamesFilter.contains("tipoIter")){
					attoMap.put("tipoIter", atto.getTipoIter() != null ? atto.getTipoIter().getDescrizione() : "");
				}
				if(colNamesFilter.contains("stato")){
					attoMap.put("stato", atto.getStato() != null ? atto.getStato() : "");
				}
				if(colNamesFilter.contains("numeroAdozione")){
					attoMap.put("numeroAdozione", atto.getNumeroAdozione() != null ? atto.getNumeroAdozione() : "");
				}
				if(colNamesFilter.contains("dataAdozione")){
					attoMap.put("dataAdozione", atto.getDataAdozione() != null ? df.format(atto.getDataAdozione().toDate()) : "");
				}
				if(colNamesFilter.contains("dataInizio")){
					attoMap.put("dataInizio", atto.getInizioPubblicazioneAlbo() != null ? df.format(atto.getInizioPubblicazioneAlbo().toDate()) : "");
				}
				if(colNamesFilter.contains("dataFine")){
					attoMap.put("dataFine", atto.getFinePubblicazioneAlbo() != null ? df.format(atto.getFinePubblicazioneAlbo().toDate()) : "");
				}
				if(colNamesFilter.contains("dataEsecutivita")){
					attoMap.put("dataEsecutivita", atto.getDataEsecutivita()!=null ? df.format(atto.getDataEsecutivita().toDate()) : "");
				}
				if(colNamesFilter.contains("inCarico")){
					String username = atto.getAvanzamento()!=null && atto.getAvanzamento().iterator().hasNext() ? atto.getAvanzamento().iterator().next().getCreatedBy() : null;
					String name = null;
					if(username!=null && !username.trim().isEmpty()) {
						UtenteDto uDto = utenteService.getUtenteBasicByUsername(username);
						if(uDto!=null) {
							name = uDto.getName();
						}
					}
					if(name==null) {
						name = "";
					}
					attoMap.put("inCarico", name);
				}
				if(colNamesFilter.contains("dataInCarico")){
					attoMap.put("dataInCarico", atto.getAvanzamento()!=null && atto.getAvanzamento().iterator().hasNext() ? df.format(atto.getAvanzamento().iterator().next().getDataAttivita().toDate()) : "");
				}
				if(colNamesFilter.contains("istruttore")){
					attoMap.put("istruttore", atto.getCreatedBy()!=null && !atto.getCreatedBy().isEmpty() ? atto.getCreatedBy() : "");
				}
				if(colNamesFilter.contains("emanante")){
					attoMap.put("emanante", atto.getEmananteProfilo()!=null && atto.getEmananteProfilo().getUtente()!= null ? atto.getEmananteProfilo().getUtente().getUsername() : "");
				}
				if(colNamesFilter.contains("statoPubblicazione")){
					attoMap.put("statoPubblicazione", atto.getStatoPubblicazione()!=null && !atto.getStatoPubblicazione().isEmpty() ? atto.getStatoPubblicazione() : "");
				}
				if(colNamesFilter.contains("statoProceduraPubblicazione")){
					attoMap.put("statoProceduraPubblicazione", atto.getStatoProceduraPubblicazione()!=null && !atto.getStatoProceduraPubblicazione().isEmpty() ? atto.getStatoProceduraPubblicazione() : "");
				}
				if(colNamesFilter.contains("statoRelata") && atto.getStatoRelata() != null && atto.getStatoRelata().intValue()>0 ){
					attoMap.put("statoRelata", atto.getStatoRelata() != null && atto.getStatoRelata().intValue()>0 ? StatoRelataEnum.fromValue(atto.getStatoRelata().intValue()).toString():"");
				}
				
				if(colNamesFilter.contains("erroriComunicazioneAlbo")){
					String errore = "";
					List<String> errori = jobPubblicazioneService.findErroreByAttoId(atto.getId());
					if(errori!=null && errori.size() > 0)
					for (int i = 0; i < errori.size(); i++) {
						if(errori.get(i)!=null && !errori.get(i).isEmpty()){
							errore = errori.get(i);
						}
					}
					attoMap.put("erroriComunicazioneAlbo",errore);
				}
				if(colNamesFilter.contains("dataFineIter")){
					attoMap.put("dataFineIter", atto.getLastModifiedDate()!=null ? df.format(atto.getLastModifiedDate().toDate()) : "");
				}
				if(colNamesFilter.contains("tipoFinanziamento")){
					String tipiFinanziamento = "";
					if(atto.getHasTipoFinanziamenti()!=null) {
						
						for(TipoFinanziamento tipoFin : atto.getHasTipoFinanziamenti()) {
							if(!tipiFinanziamento.isEmpty()) {
								tipiFinanziamento+=", ";
							}
							tipiFinanziamento+=tipoFin.getDescrizione();
						}
						
					}
					attoMap.put("tipoFinanziamento", tipiFinanziamento );
				}
				listMap.add(attoMap);
			}
		}
		return listMap;
	}
	
	public List<RigaAssentiDto> getRigheAssentiDto(List<ComponentiGiunta> componentiGiunta){
		List<RigaAssentiDto> list = null;
		List<RigaAssentiDto> listCompatta = null;
		if(componentiGiunta!=null && componentiGiunta.size()>0){
			//prendo la lista degli id_atto
			HashMap<String, String> numeriAdozioneAttoAssentiMap = new HashMap<String, String>();
			for (ComponentiGiunta componenteGiunta : componentiGiunta) {
				try {
					if(componenteGiunta.getAtto()!=null && componenteGiunta.getAtto().getAtto()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto()!=null && 
						componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice().equals("DEL")){
						String nominativo = componenteGiunta.getProfilo().getUtente().getCognome()+" "+componenteGiunta.getProfilo().getUtente().getNome();
						if(!componenteGiunta.getPresente() && !nominativo.isEmpty()){
							String numeroAdozione = componenteGiunta.getAtto().getAtto().getNumeroAdozione();
							if(numeroAdozione != null){
								if(numeriAdozioneAttoAssentiMap.containsKey(numeroAdozione)){
									String assenti = numeriAdozioneAttoAssentiMap.get(numeroAdozione);
									assenti = assenti+", "+nominativo;
									numeriAdozioneAttoAssentiMap.put(numeroAdozione, assenti);
								}else{
									numeriAdozioneAttoAssentiMap.put(numeroAdozione, nominativo);
								}
							}
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage() );
				}
				
			}
			List<String> sortedNumeriAdozione=new ArrayList<String>(numeriAdozioneAttoAssentiMap.keySet());
			
			if(sortedNumeriAdozione!=null && sortedNumeriAdozione.size() > 0){
				
				Collections.sort(sortedNumeriAdozione);
				list = new ArrayList<RigaAssentiDto>();
				for (String numeroAdozione : sortedNumeriAdozione) {
					String numeroDal = numeroAdozione;
					String numeroAl = numeroAdozione;
					String assenti = numeriAdozioneAttoAssentiMap.get(numeroAdozione);
					RigaAssentiDto riga = new RigaAssentiDto();
					riga.setNumeroDal(numeroDal);
					riga.setNumeroAl(numeroAl);
					riga.setAssenti(assenti);
					list.add(riga);
				}
			}
			if(list!=null && list.size()>0)
			{
				listCompatta = new ArrayList<RigaAssentiDto>();
				RigaAssentiDto rigaCompatta = null;
				boolean inserita = false;
				//compatto la lista
				for (int i = 0; i < list.size(); i++) {
					inserita = false;
					RigaAssentiDto riga = (RigaAssentiDto)list.get(i);
					if(rigaCompatta==null){
						rigaCompatta = new RigaAssentiDto();
						rigaCompatta.setAssenti(riga.getAssenti());
						rigaCompatta.setNumeroAl(riga.getNumeroAl());
						rigaCompatta.setNumeroDal(riga.getNumeroDal());
					}else{
						try {
							int numeroAdozione = Integer.parseInt(riga.getNumeroAl());
							String assenti = riga.getAssenti();
							if (numeroAdozione == (Integer.parseInt(rigaCompatta.getNumeroAl())+1) && assenti.equals(rigaCompatta.getAssenti())){
								//se il numero adozione è consecutivo e gli assenti sono gli stessi procedo alla compattazione
								rigaCompatta.setNumeroAl(riga.getNumeroDal());
							}else{
								//altrimenti registro la rigaCompatta e la annullo.
								listCompatta.add(rigaCompatta);
								inserita = true;
								rigaCompatta=new RigaAssentiDto();
								rigaCompatta.setAssenti(riga.getAssenti());
								rigaCompatta.setNumeroAl(riga.getNumeroAl());
								rigaCompatta.setNumeroDal(riga.getNumeroDal());
								if(i==list.size()-1){
									inserita=false;
								}
							}
							
						} catch (NumberFormatException e) {
							listCompatta.add(riga);
						}
					}
					if(i==list.size()-1&&!inserita && rigaCompatta!=null){
						listCompatta.add(rigaCompatta);
					}
				}
			}
		}
		return listCompatta;
	}
	public String getProgressivoInizio(List<ComponentiGiunta> componentiGiunta){
		int min = 0;
		if(componentiGiunta!=null && componentiGiunta.size()>0){
			//prendo la lista degli id_atto
			for (ComponentiGiunta componenteGiunta : componentiGiunta) {
				try {
					if(componenteGiunta.getAtto()!=null && componenteGiunta.getAtto().getAtto()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto()!=null && 
							componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice().equals("DEL")){
						int numeroAdozione = Integer.parseInt(componenteGiunta.getAtto().getAtto().getNumeroAdozione());
						if(min == 0){
							min=numeroAdozione;
						}else{
							if(numeroAdozione < min){
								min=numeroAdozione;
							}
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage() );
				}
				
			}
		}
		return String.valueOf(min);
	}
	public String getProgressivoFine(List<ComponentiGiunta> componentiGiunta){
		int max = 0;
		if(componentiGiunta!=null && componentiGiunta.size()>0){
			//prendo la lista degli id_atto
			for (ComponentiGiunta componenteGiunta : componentiGiunta) {
				try {
					if(componenteGiunta.getAtto()!=null && componenteGiunta.getAtto().getAtto()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto()!=null && 
							componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice()!=null && componenteGiunta.getAtto().getAtto().getTipoAtto().getCodice().equals("DEL")){
						int numeroAdozione = Integer.parseInt(componenteGiunta.getAtto().getAtto().getNumeroAdozione());
						if(max == 0){
							max =numeroAdozione;
						}else{
							if(numeroAdozione>max){
								max=numeroAdozione;
							}
						}
					}
				} catch (Exception e) {
					log.error(e.getMessage() );
				}
				
			}
		}
		return String.valueOf(max);
	}
}
