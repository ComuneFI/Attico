/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.trasparenza.service;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClientException;

import it.linksmt.assatti.cooperation.dto.AllegatoDto;
import it.linksmt.assatti.cooperation.service.AllegatoService;
import it.linksmt.assatti.cooperation.service.trasparenza.JobPubblicazioneTrasparenzaService;
import it.linksmt.assatti.cooperation.trasparenza.client.ComponenteAttiTrasparenzaClient;
import it.linksmt.assatti.cooperation.trasparenza.dto.AttoDto;
import it.linksmt.assatti.cooperation.trasparenza.dto.DefaultResponse;
import it.linksmt.assatti.cooperation.trasparenza.dto.ParereDto;
import it.linksmt.assatti.cooperation.trasparenza.dto.ResponseResult;
import it.linksmt.assatti.cooperation.trasparenza.dto.StatoAnnullamentoEnum;
import it.linksmt.assatti.cooperation.trasparenza.dto.TipoAttoDto;
import it.linksmt.assatti.cooperation.trasparenza.dto.VotazioniDto;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.JobTrasparenza;
import it.linksmt.assatti.datalayer.domain.OrigineParereEnum;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAzioneEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.VotazioneEnum;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.ComponentiGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.EsitoRepository;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.JobTrasparenzaService;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * Implementazione del servizio per la pubblicazione degli atti nel sistema
 * "componente-atti-trasparenza".
 *
 * @author Gianluca Pindinelli
 *
 */
@Service
public class JobPubblicazioneTrasparenzaServiceImpl implements JobPubblicazioneTrasparenzaService {

	private final Logger log = LoggerFactory.getLogger(JobPubblicazioneTrasparenzaServiceImpl.class);

	@Autowired
	private JobTrasparenzaService jobTrasparenzaService;

	@Autowired
	private ComponenteAttiTrasparenzaClient componenteAttiTrasparenzaClient;

	@Autowired
	private AllegatoService allegatoService;

	@Autowired
	private AttoService attoService;
	
	@Autowired
	private AttiOdgRepository attiOdgRepository;
	
	@Autowired
	private ComponentiGiuntaRepository componentiGiuntaRepository;
	
	@Autowired
	private EsitoRepository esitoRepository;
	
	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private AooService aooService;

	@Override
	public void sendAtti() throws ServiceException {
		try {
			List<JobTrasparenza> findByStatoIn = jobTrasparenzaService.getDaPubblicare();
			if (findByStatoIn != null) {
				for (JobTrasparenza jobTrasparenza : findByStatoIn) {
					jobTrasparenzaService.inProgress(jobTrasparenza.getId());
					try {
						sendToTrasparenza(jobTrasparenza);
					}
					catch (Exception e) {
						log.error("sendAtti :: " + e.getMessage(), e);
						jobTrasparenzaService.error(jobTrasparenza.getId(), e.getMessage());
					}
				}
			}
		}
		catch (Exception e) {
			log.error("sendAtti :: " + e.getMessage(), e);
			throw new ServiceException("Impossibile inviare gli atti per la trasparenza : " + e.getMessage(), e);
		}
	}

	/**
	 * @param jobTrasparenza
	 * @throws ServiceException
	 */
	@Transactional
	public void sendToTrasparenza(JobTrasparenza jobTrasparenza) throws ServiceException {

		Atto atto = attoService.findOne(jobTrasparenza.getAtto().getId());

		// JobTrasparenza --> AttoDto
		AttoDto attoDto = new AttoDto();
		attoDto.setAttoId(atto.getId());
		attoDto.setVisibilitaDocumenti(true);

		// Tipo atto
		TipoAtto tipoAtto = atto.getTipoAtto();
		if (tipoAtto != null) {
			TipoAttoDto tipoAttoDto = new TipoAttoDto();
			tipoAttoDto.setCodice(tipoAtto.getCodice());
			attoDto.setTipoAttoDto(tipoAttoDto);
		}

		if (atto.getRiservato() == null || atto.getRiservato() == false) {
			attoDto.setRiservato(false);

			// Allegati (con documento principale)
			List<AllegatoDto> allegati = allegatoService.getAllegati(atto, true);
			boolean completo = false;
			
			if(WebApplicationProps.getPropertyList(ConfigPropNames.TIPIATTO_DOC_COMPLETO_LIST, new ArrayList<Object>()).contains(atto.getTipoAtto().getCodice())) {
				completo = true;
			}
			
			AllegatoDto documenoPrincipale = allegatoService.getDocumenoPrincipaleAsAllegatoDto(atto, completo);
			if (allegati == null) {
				allegati = new ArrayList<>();
			}
			allegati.add(documenoPrincipale);

			attoDto.setAllegati(allegati);
		}
		else {
			attoDto.setRiservato(true);
		}
		
		LocalDate dataAdozione = atto.getDataAdozione();
		if (dataAdozione != null) {
			attoDto.setDataAdozione(dataAdozione.toDate());
		}
		
		LocalDate dataEsecutivita = atto.getDataEsecutivita();
		if (dataEsecutivita != null) {
			attoDto.setDataEsecutivita(dataEsecutivita.toDate());
		}
		if (atto.getDataRicevimento() != null) {
			attoDto.setDataPresentazione(atto.getDataRicevimento().toDate());
		}
		
		//flag pubblicazioneImmediata realizzato solo per facilitare i test
		
		LocalDate inizioPubblicazioneAlbo = atto.getDataInizioPubblicazionePresunta();
		if (inizioPubblicazioneAlbo != null) {
			attoDto.setDataPubblicazione(inizioPubblicazioneAlbo.toDateTimeAtStartOfDay().toDate());
		}
		if(atto.getPubblicazioneTrasparenzaNolimit() != null && !atto.getPubblicazioneTrasparenzaNolimit()) {
			LocalDate finePubblicazioneAlbo = atto.getDataFinePubblicazionePresunta();
			if (finePubblicazioneAlbo != null) {
				attoDto.setDataFinePubblicazione(finePubblicazioneAlbo.toDateTimeAtStartOfDay().toDate());
			}
		}
		//FIX non mando il codice dell'esito ma la descrizione
		String codiceEsito = atto.getEsito();
		
		Esito esito = null;
		if(codiceEsito!=null) {
			esito = this.esitoRepository.findOne(codiceEsito);
		}else {
			codiceEsito = "";
		}
		String descrizioneEsito = esito!=null&&esito.getLabel()!=null?esito.getLabel():codiceEsito;
		attoDto.setEsito(descrizioneEsito);
		
		attoDto.setNumeroAdozione(atto.getNumeroAdozione());
		attoDto.setOggetto(atto.getOggetto());
		
		// il proponente sono i consiglieri proponenti
		String nomiProponenti = "";
		if (atto.getProponenti() != null) {
			for (Profilo propostoDa : atto.getProponenti()) {
				Utente ut = propostoDa.getUtente();
						
				if (ut != null) {
					if (nomiProponenti.length() > 0) {
						nomiProponenti += ", ";
					}
					nomiProponenti += ut.getCognome() + " " + ut.getNome();
				}
			}
		}

		attoDto.setProponente(nomiProponenti);
		
		attoDto.setStato(atto.getStato());
		
		if ((atto.getAttoRevocatoId() != null) && (atto.getAttoRevocatoId().longValue() > 0) ) {
			attoDto.setAttoRevocatoId(atto.getAttoRevocatoId());
			
			if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("REVOCA")) {
				attoDto.setStatoAnnullamento(StatoAnnullamentoEnum.REVOCATO.getCodice());
				if(atto.getTipoDeterminazione().getStatoTrasparenza()!=null && !atto.getTipoDeterminazione().getStatoTrasparenza().isEmpty()) {
					attoDto.setTipoCollegamentoAtto(atto.getTipoDeterminazione().getStatoTrasparenza());
					
				}else {
					attoDto.setTipoCollegamentoAtto("REVOCA");
				}
			}else if(atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("MOD-INT")) {
				attoDto.setStatoAnnullamento(StatoAnnullamentoEnum.MODIFICATO.getCodice());
				
				if(atto.getTipoDeterminazione().getStatoTrasparenza()!=null && !atto.getTipoDeterminazione().getStatoTrasparenza().isEmpty()) {
					attoDto.setTipoCollegamentoAtto(atto.getTipoDeterminazione().getStatoTrasparenza());
					
				}else {
					attoDto.setTipoCollegamentoAtto("MOD-INT");
				}
			}
			if(atto.getTipoDeterminazione()!=null){
				attoDto.setStatoAnnullamento(atto.getTipoDeterminazione().getCodice());
				attoDto.setDescrizioneStatoAnnullamento( atto.getTipoDeterminazione().getStatoTrasparenza());
			}
			
		}
		else {
			attoDto.setAttoRevocatoId(null);
		}
		
		try {
			Atto attoDiRevoca = attoService.findAttoDiRevoca(atto.getId());
			if(attoDiRevoca!=null && attoDiRevoca.getId()!=null) {
				attoDto.setNumeroAttoAnnullamento(attoDiRevoca.getNumeroAdozione());
				if(attoDiRevoca.getDataAdozione()!=null) {
					attoDto.setDataAttoAnnullamento(attoDiRevoca.getDataAdozione().toDate());
				}
				if(attoDiRevoca.getTipoDeterminazione()!=null) {
					attoDto.setStatoAnnullamento(attoDiRevoca.getTipoDeterminazione().getCodice());
					attoDto.setDescrizioneStatoAnnullamento(attoDiRevoca.getTipoDeterminazione().getStatoTrasparenza());
				}
			}
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		Aoo aooProponente = atto.getAoo();
		if (aooProponente != null) {
			//trasmettiamo il nome della direzione dell'aoo proponente
			Aoo direzione = null;
			if(aooProponente!=null) {
				if(aooProponente.getTipoAoo()!=null && aooProponente.getTipoAoo().getCodice()!=null && aooProponente.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
					direzione = aooProponente;
				}else {
					direzione = aooService.getAooDirezione(aooProponente);
				}
			}
			
			if(direzione != null) {
				attoDto.setUfficio(direzione.getDescrizione());
			}else {
				attoDto.setUfficio(aooProponente.getDescrizione());
			}
		}
		
		// Votazioni
		AttiOdg esitoSel = null;
		List<AttiOdg> ordineGiornoSet = attiOdgRepository.findByAtto(atto);
				
		for (AttiOdg attoOdg : ordineGiornoSet) {
			if (attoOdg.getEsito() != null && atto.getEsito() != null
					&& attoOdg.getEsito().equals(atto.getEsito())) {
				
				// Prendo l'ultimo odg in cui l'atto è stato inserito
				if (esitoSel == null || esitoSel.getId().longValue() < attoOdg.getId().longValue()) {
					esitoSel = attoOdg;
				}
			}
		}
		
		String relatoreVal = null;
		
		if (esitoSel != null) {
			if(esitoSel.getDataConfermaEsito()!=null) {
				attoDto.setDataApprovazione(esitoSel.getDataConfermaEsito().toDate());
			}
			
			int numPresenti = esitoSel.getNumPresenti();
			
			int numAstenuti = esitoSel.getNumAstenuti();
			String nomiAstenuti = "";
			
			int numContrari = esitoSel.getNumContrari();
			String nomiContrari = "";
			
			int numFavorevoli = esitoSel.getNumFavorevoli();
			String nomiFavorevoli = "";
			
			int numNpv = esitoSel.getNumNpv();
			String nomiNpv = "";
			
			List<ComponentiGiunta> votanti = componentiGiuntaRepository.findByAtto(esitoSel);
			if (votanti != null && !votanti.isEmpty()) {
				numAstenuti = 0;
				numContrari = 0;
				numFavorevoli = 0;
				numNpv = 0;
				
				for (ComponentiGiunta votante : votanti) {
					if (VotazioneEnum.ASTENUTO.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(votante.getVotazione()))) {
						numAstenuti = numAstenuti + 1;
						nomiAstenuti = aggiungiNominativo(nomiAstenuti, votante);
					}
					else if (VotazioneEnum.FAVOREVOLE.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(votante.getVotazione()))) {
						numFavorevoli = numFavorevoli + 1;
						nomiFavorevoli = aggiungiNominativo(nomiFavorevoli, votante);
					}
					else if (VotazioneEnum.CONTRARIO.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(votante.getVotazione()))) {
						numContrari = numContrari + 1;
						nomiContrari = aggiungiNominativo(nomiContrari, votante);
					}
					else if (VotazioneEnum.NON_VOTANTE.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(votante.getVotazione()))) {
						numNpv = numNpv + 1;
						nomiNpv = aggiungiNominativo(nomiNpv, votante);
					}
				}
			}
			
			VotazioniDto votazioni = new VotazioniDto();
			votazioni.setNumPresenti(numPresenti);

			votazioni.setNumAstenuti(numAstenuti);
			votazioni.setNomiAstenuti(nomiAstenuti);

			votazioni.setNumContrari(numContrari);
			votazioni.setNomiContrari(nomiContrari);

			votazioni.setNumFavorevoli(numFavorevoli);
			votazioni.setNomiFavorevoli(nomiFavorevoli);

			votazioni.setNumNpv(numNpv);
			votazioni.setNomiNpv(nomiNpv);

			attoDto.setVotazioni(votazioni);
			
			relatoreVal = esitoSel.getAttoPresentato();
		}
		
		boolean inserisciRelatore = StringUtil.isNull(relatoreVal);
		if (atto.getPareri() != null) {

			// Pareri
			List<ParereDto> pareriDto = new ArrayList<ParereDto>();

			for (Parere parere : atto.getPareri()) {
				if (parere.getAnnullato() != null && parere.getAnnullato().booleanValue()) {
					continue;
				}else if(parere.getTipoAzione()!=null && parere.getOrigine()!=null && parere.getOrigine().equalsIgnoreCase("C") &&
						(
							parere.getTipoAzione().getCodice().equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice()) ||
							parere.getTipoAzione().getCodice().equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice())
						)
					  ) {
					continue;
				}
				
				Utente relatore = null;
								
				// Assessore recuperabile dai pareri di tipo "R"
				if (OrigineParereEnum.RISPOSTA.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine())) ||
					OrigineParereEnum.RELATORE.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine()))){

					if (parere.getProfiloRelatore() != null && parere.getProfiloRelatore().getUtente() != null) {
						relatore = parere.getProfiloRelatore().getUtente();
						
						if (inserisciRelatore) {
							if (StringUtil.isNull(relatoreVal)) {
								relatoreVal = relatore.getCognome() + " " + relatore.getNome();
							}
							else {
								relatoreVal += (", " + relatore.getCognome() + " " + relatore.getNome());
							}
						}
						
						attoDto.setAssessore(relatore.getCognome() + " " + relatore.getNome());
					}
					else if(parere.getProfilo() != null && parere.getProfilo().getUtente() != null){
						Utente ut = parere.getProfilo().getUtente();
						attoDto.setAssessore(ut.getCognome() + " " + ut.getNome());
					}
				}
				
				if (inserisciRelatore && (parere.getTipoAzione() != null)) {
					if (TipoAzioneEnum.VISTO_ASSESSORE.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(parere.getTipoAzione().getCodice())) || 
						TipoAzioneEnum.VISTO_CONSIGLIERE.getCodice().equalsIgnoreCase(
							StringUtil.trimStr(parere.getTipoAzione().getCodice()))) {
						
						if (parere.getProfilo() != null && parere.getProfilo().getUtente() != null) {
							Utente relCons = parere.getProfilo().getUtente();
						
							if (StringUtil.isNull(relatoreVal)) {
								relatoreVal = relCons.getCognome() + " " + relCons.getNome();
							}
							else {
								relatoreVal += (", " + relCons.getCognome() + " " + relCons.getNome());
							}
						}
					}
				}
				
				if(parere.getDataScadenza() != null) {
					attoDto.setDataScadenza(parere.getDataScadenza().toDate());
				}

				if(OrigineParereEnum.RISPOSTA.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine())) ||
						OrigineParereEnum.COMMISSIONE.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine())) ||
						OrigineParereEnum.QUARTEREREVISORI.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine()))
						) {
					
					ParereDto parereDto = new ParereDto();
					parereDto.setOrigine(StringUtil.trimStr(parere.getOrigine()));
					
					if(OrigineParereEnum.QUARTEREREVISORI.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine())) || 
							OrigineParereEnum.COMMISSIONE.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine()))){
						if(parere.getAoo() != null) {
							parereDto.setNome(parere.getAoo().getDescrizione());
						}else if (parere.getProfilo() != null && parere.getProfilo().getAoo() != null) {
							parereDto.setNome(parere.getProfilo().getAoo().getDescrizione());
						}
					}else if(relatore!=null && OrigineParereEnum.RISPOSTA.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine()))) {
						parereDto.setNome(relatore.getCognome() + " " + relatore.getNome());
					} else {
						if (parere.getProfilo() != null && parere.getProfilo().getAoo() != null) {
							parereDto.setNome(parere.getProfilo().getAoo().getDescrizione());
						}
						else if(parere.getAoo() != null) {
							parereDto.setNome(parere.getAoo().getDescrizione());
						}
					}

					if (!StringUtil.isNull(parere.getParere())) {
						parereDto.setTesto(parere.getParere());
					}
					else {
						parereDto.setTesto(parere.getTitolo());
					}

					if(
							OrigineParereEnum.COMMISSIONE.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine())) ||
							OrigineParereEnum.QUARTEREREVISORI.getCodice().equalsIgnoreCase(StringUtil.trimStr(parere.getOrigine()))
							) {
						if(parere.getDataInvio()!=null) {
							parereDto.setDataInvio(parere.getDataInvio().toDate());
						}
						if(parere.getDataScadenza() != null) {
							parereDto.setDataScadenza(parere.getDataScadenza().toDate());
						}
					}

					if (parere.getData() != null) {
						parereDto.setDataParere(parere.getData().toDate());
					}
					else {
						parereDto.setDataParere(parere.getCreatedDate().toDate());
					}

					pareriDto.add(parereDto);
					
					//aggiungo eventuali allegati al parere come allegati all'atto
					if(parere.getAllegati()!=null) {
						List<AllegatoDto> allegatiParere = new ArrayList<AllegatoDto>();
						for (DocumentoInformatico allegatoAlParere : parere.getAllegati() ) {
//							if(allegatoAlParere!=null && allegatoAlParere.getPubblicabile()!=null && allegatoAlParere.getPubblicabile()) {
							if(allegatoAlParere!=null) {
								File fileAll = null;
								if (allegatoAlParere.getOmissis() == null || allegatoAlParere.getOmissis() == false) {
									fileAll = allegatoAlParere.getFile();
								}
								else {
									fileAll = allegatoAlParere.getFileomissis();
								}
	
								AllegatoDto allegatoDto = new AllegatoDto();
								allegatoDto.setContentType(fileAll.getContentType());
								allegatoDto.setPrincipale(false);
								allegatoDto.setNome(fileAll.getNomeFile());
								allegatoDto.setTitolo(allegatoAlParere.getTitolo());
								allegatoDto.setDescrizione(atto.getTipoAtto().getDescrizione());
								allegatoDto.setRiservato(allegatoAlParere.getPubblicabile() == null || !allegatoAlParere.getPubblicabile());
								if(!allegatoDto.isRiservato()) {
									String cmisObjectId = fileAll.getCmisObjectId();
									try {
										byte[] content = dmsService.getContent(cmisObjectId);
										allegatoDto.setContenuto(content);
									}
									catch (DmsException e) {
										throw new ServiceException("Impossibile caricare il contenuto del file allegato con id : " + fileAll.getId() + " :: messaggio eccezione : " + e.getMessage(), e);
									}
								}
								allegatiParere.add(allegatoDto);
							}
						}
						
						
						if(allegatiParere.size()>0) {
							List<AllegatoDto> allegati = attoDto.getAllegati();
							
							for (AllegatoDto allegatoDto :allegatiParere ) {
								if (allegati == null) {
									allegati = new ArrayList<>();
								}
								allegati.add(allegatoDto);
							}
							
							attoDto.setAllegati(allegati);
						}
						
					}
					
					
				}
			}

			if (pareriDto.size() > 0) {
				attoDto.setPareri(pareriDto);
			}	

		}
		attoDto.setRelatore(relatoreVal);

		DefaultResponse sendAttoResponse;
		try {
			sendAttoResponse = componenteAttiTrasparenzaClient.sendAtto(attoDto);
		}
		catch (RestClientException e) {
			log.error("sendToTrasparenza :: " + e.getMessage(), e);
			throw new ServiceException("sendToTrasparenza :: " + e.getMessage(), e);
		}

		if (sendAttoResponse.getEsito().equals(ResponseResult.ERROR)) {
			jobTrasparenzaService.error(jobTrasparenza.getId(), sendAttoResponse.getError());
		}
		else {
			jobTrasparenzaService.done(jobTrasparenza.getId());
		}
	}

	
	private String aggiungiNominativo(String nominativi, ComponentiGiunta votante) {
		String retVal = StringUtil.trimStr(nominativi);
		if ((votante != null) && (votante.getProfilo() != null)) {
			if (retVal.length() > 0) {
				retVal += ", ";
			}
			
			retVal += votante.getProfilo().getUtente().getCognome() + " " + 
					votante.getProfilo().getUtente().getNome();
		}
		
		return retVal;
	}
}
