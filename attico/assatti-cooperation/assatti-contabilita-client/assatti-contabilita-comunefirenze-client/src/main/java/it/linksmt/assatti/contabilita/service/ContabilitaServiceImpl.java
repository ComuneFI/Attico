/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.contabilita.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.ws.BindingProvider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import it.arezzo.infor.jente.jfinanziaria.services.AttoInBozza;
import it.arezzo.infor.jente.jfinanziaria.services.BozzaInAtto;
import it.arezzo.infor.jente.jfinanziaria.services.BozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.ChiaveBozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.Documento;
import it.arezzo.infor.jente.jfinanziaria.services.FNLiquidazioniRigaCapit;
import it.arezzo.infor.jente.jfinanziaria.services.FNLiquidazioniRigaDoc;
import it.arezzo.infor.jente.jfinanziaria.services.FNLiquidazioniRigaImp;
import it.arezzo.infor.jente.jfinanziaria.services.FNLiquidazioniRigaSogg;
import it.arezzo.infor.jente.jfinanziaria.services.FNLiquidazioniStampaVelocity;
import it.arezzo.infor.jente.jfinanziaria.services.JFinanziariaService;
import it.arezzo.infor.jente.jfinanziaria.services.JFinanziariaServices;
import it.arezzo.infor.jente.jfinanziaria.services.Liquidazione;
import it.arezzo.infor.jente.jfinanziaria.services.Movimento;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaCancellazioneBozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaElencoLiquidazioni;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaElencoMovimenti;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaEsisteBozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaInserimentoBozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaModificaBozzaOAtto;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaTrasformazioneAttoInBozza;
import it.arezzo.infor.jente.jfinanziaria.services.RichiestaGestioneProposte.RichiestaTrasformazioneBozzaInAtto;
import it.arezzo.infor.jente.jfinanziaria.services.RispostaGestioneProposte;
import it.arezzo.infor.jente.jfinanziaria.services.RispostaGestioneProposte.RispostaElencoLiquidazioni;
import it.arezzo.infor.jente.jfinanziaria.services.RispostaGestioneProposte.RispostaElencoMovimenti;
import it.arezzo.infor.jente.jfinanziaria.services.RispostaGestioneProposte.RispostaEsisteBozzaOAtto;
import it.linksmt.assatti.contabilita.config.JenteProps;
import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DettaglioLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DocumentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpAccertamentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.LiquidazioneImpegnoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoDocumentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoImpegnoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.RigaLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.SoggettoDto;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.service.exception.LiquidazioniEmptyException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.StringUtil;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class ContabilitaServiceImpl implements ContabilitaService {

	/**
	 *
	 */
	private static final String VALIDO_S_VALUE = "S";
	
	private static final String INSERIMENTO_BOZZA_O_ATTO_OPERATION	= "IBOA";
	private static final String MODIFICA_BOZZA_O_ATTO_OPERATION 	= "MBOA";
	private static final String TRASFORMAZIONE_BOZZA_ATTO_OPERATION = "TBA";
	private static final String TRASFORMAZIONE_ATTO_BOZZA_OPERATION = "TAB";
	 
	private static final String ELENCO_MOVIMENTI_OPERATION			= "EM";
	private static final String ELENCO_LIQUIDAZIONI_OPERATION		= "EL";
	
	private static final String ESISTENZA_BOZZA_O_ATTO_OPERATION	= "EBOA";
	private static final String CANCELLAZIONE_BOZZA_O_ATTO_OPERATION ="CBOA";
	
	private static final String ATTO_TYPE = "A";
	private static final String BOZZA_TYPE = "B";

	private String jenteWSUrl = JenteProps.getProperty("contabilita.comunefirenze.jente.url");

	private String jenteUsername = JenteProps.getProperty("contabilita.comunefirenze.jente.username");

	private String jenteBasicAuthUsername = JenteProps.getProperty("contabilita.comunefirenze.jente.basicauth.username");

	private String jenteBasicAuthPassword = JenteProps.getProperty("contabilita.comunefirenze.jente.basicauth.password");
	
	private String organoLiquidazioni = JenteProps.getProperty("contabilita.comunefirenze.organosettore.liquidazioni");
	private String organoDetermine = JenteProps.getProperty("contabilita.comunefirenze.organosettore.determine");
	

	private final Logger log = LoggerFactory.getLogger(ContabilitaServiceImpl.class);

	private final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService#sendAtto(it.linksmt.
	 * assatti.cooperation.dto.contabilita.ContabilitaDto)
	 */
	@Override
	public void sendBozza(ContabilitaDto contabilitaDto) throws ServiceException {

		log.debug("entering method");

		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);

		// Verifica esistenza bozza per eventuale modifica
		boolean esistente = esisteBozzaAtto(
				contabilitaDto.getCodiceTipoAtto(), contabilitaDto.getNumeroProposta(), 
				contabilitaDto.getAnnoCreazioneProposta(), true);

		BozzaOAtto bozzaOAtto = new BozzaOAtto();

		bozzaOAtto.setAnno(String.valueOf(contabilitaDto.getAnnoCreazioneProposta()));
		bozzaOAtto.setBozzaOAtto(BOZZA_TYPE);
		bozzaOAtto.setData(simpleDateFormat.format(contabilitaDto.getDataCreazioneProposta()));
		if(contabilitaDto.getDataEsecutivita() != null) {
			bozzaOAtto.setDataEsecutivita(simpleDateFormat.format(contabilitaDto.getDataEsecutivita()));
		}
		if(contabilitaDto.getImportoTotale()!=null) {
			bozzaOAtto.setImporto(contabilitaDto.getImportoTotale().doubleValue());
		}
		bozzaOAtto.setNumero(contabilitaDto.getNumeroProposta());
		bozzaOAtto.setOggetto(contabilitaDto.getOggetto());
		String organoSettoreByMapping = JenteProps.getProperty(
				"contabilita.comunefirenze.jente.mapping.organosettore." + contabilitaDto.getCodiceTipoAtto());
		bozzaOAtto.setOrganoSettore(organoSettoreByMapping);
		bozzaOAtto.setResponsabileProcedimento("");
		bozzaOAtto.setTipoEsecutivita(null);
		bozzaOAtto.setValidoAccertamenti(VALIDO_S_VALUE);
		bozzaOAtto.setValidoAssegnazioni(VALIDO_S_VALUE);
		bozzaOAtto.setValidoImpegni(VALIDO_S_VALUE);
		bozzaOAtto.setValidoLiquidazioni(VALIDO_S_VALUE);
		bozzaOAtto.setValidoVariazioni(VALIDO_S_VALUE);

		// Modifica
		if (esistente) {
			RichiestaModificaBozzaOAtto richiestaModificaBozzaOAtto = new RichiestaModificaBozzaOAtto();
			richiestaModificaBozzaOAtto.setBozzaOAttoModificato(bozzaOAtto);
			richiesta.setRichiestaModificaBozzaOAtto(richiestaModificaBozzaOAtto);
			richiesta.setTipo(MODIFICA_BOZZA_O_ATTO_OPERATION);
		}
		// Inserimento
		else {
			RichiestaInserimentoBozzaOAtto richiestaInserimentoBozzaOAtto = new RichiestaInserimentoBozzaOAtto();
			richiestaInserimentoBozzaOAtto.setBozzaOAtto(bozzaOAtto);
			richiesta.setRichiestaInserimentoBozzaOAtto(richiestaInserimentoBozzaOAtto);
			richiesta.setTipo(INSERIMENTO_BOZZA_O_ATTO_OPERATION);
		}

		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
	}

	@Override
	public boolean esisteBozzaAtto(
			final String codiceTipoAtto, final String numero, 
			final int anno, boolean isBozza) throws ServiceException {

		log.debug("entering method");

		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
		richiesta.setTipo(ESISTENZA_BOZZA_O_ATTO_OPERATION);

		RichiestaEsisteBozzaOAtto richiestaEsistenzaBozzaOAtto = new RichiestaEsisteBozzaOAtto();
		ChiaveBozzaOAtto chiaveBozzaOAtto = new ChiaveBozzaOAtto();
		chiaveBozzaOAtto.setAnno(String.valueOf(anno));
		chiaveBozzaOAtto.setBozzaOAtto(isBozza ? BOZZA_TYPE : ATTO_TYPE);
		chiaveBozzaOAtto.setNumero(numero);

		String organoSettoreByMapping = JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore." + codiceTipoAtto);
		chiaveBozzaOAtto.setOrganoSettore(organoSettoreByMapping);
		log.info("verifica esistenza");
		log.info("anno\n" + (chiaveBozzaOAtto.getAnno()!=null ? chiaveBozzaOAtto.getAnno() : ""));
		log.info("bozzaOatto\n" + (chiaveBozzaOAtto.getBozzaOAtto()!=null ? chiaveBozzaOAtto.getBozzaOAtto() : ""));
		log.info("numero\n" + (chiaveBozzaOAtto.getNumero()!=null ? chiaveBozzaOAtto.getNumero() : ""));
		log.info("organoGestore\n" + (chiaveBozzaOAtto.getOrganoSettore()!=null ? chiaveBozzaOAtto.getOrganoSettore() : ""));
		
		richiestaEsistenzaBozzaOAtto.setChiaveBozzaOAtto(chiaveBozzaOAtto);
		richiesta.setRichiestaEsisteBozzaOAtto(richiestaEsistenzaBozzaOAtto);

		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();
		
		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}

		RispostaEsisteBozzaOAtto rispostaEsisteBozzaOAtto = gestioneProposte.getRispostaEsisteBozzaOAtto();

		Boolean esiste = rispostaEsisteBozzaOAtto.isEsiste();
		log.info("Risposta esistenza");
		log.info("code: " + (code!=null ? code : "null"));
		log.info("message: " + (message!=null ? message : "null"));
		log.info("ok: " + (ok!=null ? ok : "null"));
		log.info("esiste: " + (esiste!=null ? esiste : "null"));
		return (esiste != null) && esiste.booleanValue();
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService#getMovimentiContabili(
	 * java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<MovimentoContabileDto> getMovimentiContabili(
			final String codiceTipoAtto, final String numero, 
			final int anno, boolean isBozza) throws ServiceException {

		log.debug("entering method");
		
		String organoSettoreByMapping = JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore." + codiceTipoAtto);

		ChiaveBozzaOAtto chiaveBozzaOAtto = new ChiaveBozzaOAtto();
		chiaveBozzaOAtto.setAnno(String.valueOf(anno));
		chiaveBozzaOAtto.setBozzaOAtto(isBozza ? BOZZA_TYPE : ATTO_TYPE);
		chiaveBozzaOAtto.setNumero(numero);
		chiaveBozzaOAtto.setOrganoSettore(organoSettoreByMapping);
		
		// TODO: rimuovere, per TEST integrazione JENTE
		if (!StringUtil.isNull(JenteProps.getProperty("contabilita.comunefirenze.jente.numero"))) {
			chiaveBozzaOAtto.setAnno(JenteProps.getProperty("contabilita.comunefirenze.jente.anno"));
			chiaveBozzaOAtto.setNumero(JenteProps.getProperty("contabilita.comunefirenze.jente.numero"));
			chiaveBozzaOAtto.setOrganoSettore(JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore.TEST"));
		}
		
		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
				
		RichiestaElencoMovimenti richiestaElencoMovimenti = new RichiestaElencoMovimenti();
		richiestaElencoMovimenti.setChiaveBozzaOAtto(chiaveBozzaOAtto);
		
		richiesta.setRichiestaElencoMovimenti(richiestaElencoMovimenti);
		richiesta.setTipo(ELENCO_MOVIMENTI_OPERATION);
		
		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if((ok==null || !ok) && message != null && message.startsWith("Non ci sono liquidazioni associate al provvedimento")) {
			throw new LiquidazioniEmptyException("code: " + (code!=null ? code : "") + ", message: " + message);
		}
		
		if (ok == null || !ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
		
		List<MovimentoContabileDto> movimentoContabileDtos = null;
		
		RispostaElencoMovimenti rispostaElencoMovimenti = gestioneProposte.getRispostaElencoMovimenti();
		List<Movimento> movimenti = rispostaElencoMovimenti.getMovimento();
		
		if (movimenti != null) {
			movimentoContabileDtos = new ArrayList<>();
			for (Movimento movimento : movimenti) {
				MovimentoContabileDto movimentoContabileDto = new MovimentoContabileDto();
				if (movimento.getMovImpAcce() != null) {
					ImpAccertamentoDto impDto = new ImpAccertamentoDto();
					BeanUtils.copyProperties(movimento.getMovImpAcce(), impDto);
					movimentoContabileDto.setMovImpAcce(impDto);
				}
				if (movimento.getLiquidazione() != null) {
					Liquidazione liqOrig = movimento.getLiquidazione();
					
					MovimentoLiquidazioneDto liqDto = new MovimentoLiquidazioneDto();
					BeanUtils.copyProperties(liqOrig, liqDto);
					
					List<Documento> listOrig = liqOrig.getDocumento();
					
					if ((listOrig != null) && (!listOrig.isEmpty())) {
						List<MovimentoDocumentoDto> docList = new ArrayList<MovimentoDocumentoDto>();
						
						for (Documento docOrig : listOrig) {
							MovimentoDocumentoDto docDto = new MovimentoDocumentoDto();
							BeanUtils.copyProperties(docOrig, docDto);
							
							if (docOrig.getImpegno() != null) {
								MovimentoImpegnoDto impDto = new MovimentoImpegnoDto();
								BeanUtils.copyProperties(docOrig.getImpegno(), impDto);
								docDto.setImpegno(impDto);
							}
							docList.add(docDto);
						}
						liqDto.setDocumento(docList);
					}
					movimentoContabileDto.setLiquidazione(liqDto);
				}
				movimentoContabileDtos.add(movimentoContabileDto);
			}
		}
		log.info("organoDetermine:"+organoDetermine + " organoSettoreByMapping:"+organoSettoreByMapping);
		if (organoLiquidazioni.equalsIgnoreCase(organoSettoreByMapping)) {
			List<MovimentoContabileDto> dettaglioLiq = getDettaglioLiquidazioni(chiaveBozzaOAtto);
			if ((dettaglioLiq != null) && (!dettaglioLiq.isEmpty())) {
				if (movimentoContabileDtos == null) {
					movimentoContabileDtos = new ArrayList<MovimentoContabileDto>();
				}
				movimentoContabileDtos.addAll(dettaglioLiq);
			}
		}
		
		
		if (organoDetermine.equalsIgnoreCase(organoSettoreByMapping)) {
			List<MovimentoContabileDto> dettaglioLiq = null;
			try {
				dettaglioLiq = getDettaglioLiquidazioni(chiaveBozzaOAtto);
			} catch (LiquidazioniEmptyException e) {
				// TODO: handle exception
			}
			if ((dettaglioLiq != null) && (!dettaglioLiq.isEmpty())) {
				if (movimentoContabileDtos == null) {
					movimentoContabileDtos = new ArrayList<MovimentoContabileDto>();
				}
				movimentoContabileDtos.addAll(dettaglioLiq);
			}
		}
		
		
		return movimentoContabileDtos;
	}
	
	
	private List<MovimentoContabileDto> getDettaglioLiquidazioni(
			ChiaveBozzaOAtto chiaveBozzaOAtto) throws ServiceException {
		
		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
		
		RichiestaElencoLiquidazioni richiestaElencoLiq = new RichiestaElencoLiquidazioni();
		richiestaElencoLiq.setChiaveBozzaOAtto(chiaveBozzaOAtto);
		
		richiesta.setRichiestaElencoLiquidazioni(richiestaElencoLiq);
		richiesta.setTipo(ELENCO_LIQUIDAZIONI_OPERATION);
		
		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if((ok==null || !ok) && message != null && message.startsWith("Non ci sono liquidazioni associate al provvedimento")) {
			throw new LiquidazioniEmptyException("code: " + (code!=null ? code : "") + ", message: " + message);
		}
		
		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
		
		List<MovimentoContabileDto> movimentoContabileDtos = null;
		RispostaElencoLiquidazioni rispostaElencoLiq = gestioneProposte.getRispostaElencoLiquidazioni();
		List<FNLiquidazioniStampaVelocity> liquidazioni = rispostaElencoLiq.getLiquidazione();
		
		if (liquidazioni != null) {
			movimentoContabileDtos = new ArrayList<>();
			
			// Itera sulle Liquidazioni
			for (FNLiquidazioniStampaVelocity liquidazioneStampa : liquidazioni) {
				MovimentoContabileDto movimentoContabileDto = new MovimentoContabileDto();
				
				DettaglioLiquidazioneDto dettaglioDto = new DettaglioLiquidazioneDto();
				BeanUtils.copyProperties(liquidazioneStampa, dettaglioDto);
				
				// Itera sui Capitoli
				List<FNLiquidazioniRigaCapit> righeOrig = liquidazioneStampa.getListaCapitoli();
				if ((righeOrig != null) && (!righeOrig.isEmpty())) {
					List<RigaLiquidazioneDto> listaCapitoli = new ArrayList<RigaLiquidazioneDto>();
					
					for (FNLiquidazioniRigaCapit rigaStampa : righeOrig) {
						RigaLiquidazioneDto rigaDto = new RigaLiquidazioneDto();
						BeanUtils.copyProperties(rigaStampa, rigaDto);
						
						// Itera sugli Impegni
						List<FNLiquidazioniRigaImp> immpegniOrig = rigaStampa.getListaImpegni();
						if ((immpegniOrig != null) && (!immpegniOrig.isEmpty())) {
							List<LiquidazioneImpegnoDto> listaImpegni = new ArrayList<LiquidazioneImpegnoDto> ();
							
							for (FNLiquidazioniRigaImp impegnoOrig : immpegniOrig) {
								LiquidazioneImpegnoDto impegnoDto = new LiquidazioneImpegnoDto();
								BeanUtils.copyProperties(impegnoOrig, impegnoDto);
								
								// Itera sui Soggetti
								List<FNLiquidazioniRigaSogg> soggettiOrig = impegnoOrig.getListaSogg();
								if ((soggettiOrig != null) && (!soggettiOrig.isEmpty())) {
									List<SoggettoDto> listaSoggetti = new ArrayList<SoggettoDto>();
									
									for (FNLiquidazioniRigaSogg soggOrig : soggettiOrig) {
										SoggettoDto soggDto = new SoggettoDto();
										BeanUtils.copyProperties(soggOrig, soggDto);
										
										// Itera sui Documenti
										List<FNLiquidazioniRigaDoc> listDocOrig = soggOrig.getListaDocumenti();
										if ((listDocOrig != null) && (!listDocOrig.isEmpty())) {
											List<DocumentoDto> listaDocumenti = new ArrayList<DocumentoDto>();
											
											for (FNLiquidazioniRigaDoc docOrig : listDocOrig) {
												DocumentoDto docDto = new DocumentoDto();
												BeanUtils.copyProperties(docOrig, docDto);
												
												listaDocumenti.add(docDto);
											}
											soggDto.setListaDocumenti(listaDocumenti);
										}
										listaSoggetti.add(soggDto);
									}
									impegnoDto.setListaSogg(listaSoggetti);
								}
								listaImpegni.add(impegnoDto);
							}
							rigaDto.setListaImpegni(listaImpegni);
						}
						listaCapitoli.add(rigaDto);
					}
					dettaglioDto.setListaCapitoli(listaCapitoli);
				}
				
				movimentoContabileDto.setDettaglioLiquidazione(dettaglioDto);
				movimentoContabileDtos.add(movimentoContabileDto);
			}
		}
		return movimentoContabileDtos;
	}
	
	

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService#confirmAtto(it.linksmt.
	 * assatti.cooperation.dto.contabilita.ContabilitaDto)
	 */
	@Override
	public void confirmAtto(ContabilitaDto contabilitaDto) throws ServiceException {

		log.debug("entering method");
		
		if (!esisteBozzaAtto(contabilitaDto.getCodiceTipoAtto(), contabilitaDto.getNumeroProposta(), 
				contabilitaDto.getAnnoCreazioneProposta(), true)) {
			log.info("bozza non esistente dunque chiamata non effettuata\ncontabilitaDto: " + (contabilitaDto!=null ? contabilitaDto : "null"));
			return;
		}else {
			log.info("la bozza esiste.. si procede con la trasformazione");
		}

		JFinanziariaService jFinanziariaService = new JFinanziariaService();
		
		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
		richiesta.setTipo(TRASFORMAZIONE_BOZZA_ATTO_OPERATION);

		RichiestaTrasformazioneBozzaInAtto richiestaTrasformazioneBozzaInAtto = new RichiestaTrasformazioneBozzaInAtto();
		BozzaInAtto bozzaInAtto = new BozzaInAtto();
		bozzaInAtto.setAnnoAtto(contabilitaDto.getAnnoAtto().toString());
		bozzaInAtto.setAnnoBozza(contabilitaDto.getAnnoCreazioneProposta().toString());
		
		bozzaInAtto.setDataAtto(simpleDateFormat.format(contabilitaDto.getDataAdozioneAtto()));
		if (contabilitaDto.getDataEsecutivita() != null) {
			bozzaInAtto.setDataEsecutivita(simpleDateFormat.format(contabilitaDto.getDataEsecutivita()));
		}
		if(contabilitaDto.getImportoTotale()!=null) {
			bozzaInAtto.setImporto(contabilitaDto.getImportoTotale().doubleValue());
		}
		bozzaInAtto.setNumeroAtto(contabilitaDto.getNumeroAtto());
		bozzaInAtto.setNumeroBozza(contabilitaDto.getNumeroProposta());
		bozzaInAtto.setOggetto(contabilitaDto.getOggetto());

		String organoSettoreByMapping = JenteProps.getProperty(
				"contabilita.comunefirenze.jente.mapping.organosettore." + contabilitaDto.getCodiceTipoAtto());
		bozzaInAtto.setOrganoSettoreBozza(organoSettoreByMapping);
		bozzaInAtto.setOrganoSettoreAtto(organoSettoreByMapping);

		bozzaInAtto.setValidoAccertamenti(VALIDO_S_VALUE);
		bozzaInAtto.setValidoAssegnazioni(VALIDO_S_VALUE);
		bozzaInAtto.setValidoImpegni(VALIDO_S_VALUE);
		bozzaInAtto.setValidoLiquidazioni(VALIDO_S_VALUE);
		bozzaInAtto.setValidoVariazioni(VALIDO_S_VALUE);
		richiestaTrasformazioneBozzaInAtto.setBozzaInAtto(bozzaInAtto);

		richiesta.setRichiestaTrasformazioneBozzaInAtto(richiestaTrasformazioneBozzaInAtto);

		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		log.info("Si sta per chiamare il servizio di trasformazione bozza in atto\nrichiesta: " + (richiesta!=null ? richiesta : ""));
		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);
		log.info("risposta ricevuta:" + (gestioneProposte != null ? gestioneProposte : ""));
		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();
		log.info("Dettaglio risposta:\nCode: " + (code!=null ? code : "null") + "\nMessage: " + (message != null  ? message : "null") + "\nok: " + (ok!=null ? ok : "null"));
		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService#eliminaBozzaAtto(it.linksmt.
	 * assatti.cooperation.dto.contabilita.ContabilitaDto)
	 */
	@Override
	public void eliminaBozza(
			final String codiceTipoAtto, final String numeroProposta, 
			final int annoCreazioneProposta) throws ServiceException {
		log.debug("entering method");

		// Verifica esistenza bozza per eliminazione
		if (!esisteBozzaAtto(codiceTipoAtto, numeroProposta, annoCreazioneProposta, true)) {
			return;
		}
		
		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
		richiesta.setTipo(CANCELLAZIONE_BOZZA_O_ATTO_OPERATION);

		RichiestaCancellazioneBozzaOAtto richiestaCancellazioneBozzaOAtto = new RichiestaCancellazioneBozzaOAtto();
		ChiaveBozzaOAtto chiaveBozzaOAtto = new ChiaveBozzaOAtto();
		chiaveBozzaOAtto.setAnno(String.valueOf(annoCreazioneProposta));
		chiaveBozzaOAtto.setBozzaOAtto(BOZZA_TYPE);
		chiaveBozzaOAtto.setNumero(numeroProposta);

		String organoSettoreByMapping = JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore." + codiceTipoAtto);
		chiaveBozzaOAtto.setOrganoSettore(organoSettoreByMapping);
		
		richiestaCancellazioneBozzaOAtto.setChiaveBozzaOAtto(chiaveBozzaOAtto);
		richiestaCancellazioneBozzaOAtto.setCancellaProvvedimento("S");
		
		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);
		
		richiesta.setRichiestaCancellazioneBozzaOAtto(richiestaCancellazioneBozzaOAtto);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService#confirmAtto(it.linksmt.
	 * assatti.cooperation.dto.contabilita.ContabilitaDto)
	 */
	@Override
	public void revertBozza(ContabilitaDto contabilitaDto) throws ServiceException {
		log.debug("entering method");
				
		JFinanziariaService jFinanziariaService = new JFinanziariaService();
		
		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);
		richiesta.setTipo(TRASFORMAZIONE_ATTO_BOZZA_OPERATION);

		RichiestaTrasformazioneAttoInBozza richiestaTrasformazioneAttoInBozza = new RichiestaTrasformazioneAttoInBozza();
		AttoInBozza attoInBozza = new AttoInBozza();
		attoInBozza.setAnnoAtto(contabilitaDto.getAnnoAtto().toString());
		attoInBozza.setNumeroAtto(contabilitaDto.getNumeroAtto());

		String organoSettoreByMapping = JenteProps.getProperty(
				"contabilita.comunefirenze.jente.mapping.organosettore." + contabilitaDto.getCodiceTipoAtto());
		attoInBozza.setOrganoSettoreAtto(organoSettoreByMapping);

		richiestaTrasformazioneAttoInBozza.setAttoInBozza(attoInBozza);
		richiesta.setRichiestaTrasformazioneAttoInBozza(richiestaTrasformazioneAttoInBozza);

		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
	}

	@Override
	public void dataEsecutivitaAtto(String codiceTipoAtto, String numeroAdozione, int annoAdozione,
			Date dataEsecutivita) throws ServiceException {
		log.debug("entering method");

		
		log.info("******* AGGIORNAMENTO DATA ESECUTIVITA PER ATTO CON NUMERO ADOZIONE "+codiceTipoAtto+"/"+annoAdozione+"/"+numeroAdozione+"*********" );
		if (!esisteBozzaAtto(codiceTipoAtto, numeroAdozione, 
				annoAdozione, false)) {
			return;
		}
		
		JFinanziariaService jFinanziariaService = new JFinanziariaService();

		RichiestaGestioneProposte richiesta = new RichiestaGestioneProposte();
		richiesta.setUserName(jenteUsername);

		BozzaOAtto bozzaOAtto = new BozzaOAtto();

		bozzaOAtto.setBozzaOAtto(ATTO_TYPE);
		bozzaOAtto.setNumero(numeroAdozione);
		bozzaOAtto.setAnno(String.valueOf(annoAdozione));
		
		bozzaOAtto.setDataEsecutivita(dataEsecutivita!=null?simpleDateFormat.format(dataEsecutivita):"");
		if(bozzaOAtto.getDataEsecutivita()==null) {
			log.info("******* imposto la data a null");
		}else {
			log.info("******* imposto la data a "+bozzaOAtto.getDataEsecutivita());
		}
		
		
		String organoSettoreByMapping = JenteProps.getProperty(
				"contabilita.comunefirenze.jente.mapping.organosettore." + codiceTipoAtto);
		bozzaOAtto.setOrganoSettore(organoSettoreByMapping);
		
		RichiestaModificaBozzaOAtto richiestaModificaBozzaOAtto = new RichiestaModificaBozzaOAtto();
		richiestaModificaBozzaOAtto.setBozzaOAttoModificato(bozzaOAtto);
		richiesta.setRichiestaModificaBozzaOAtto(richiestaModificaBozzaOAtto);
		richiesta.setTipo(MODIFICA_BOZZA_O_ATTO_OPERATION);

		JFinanziariaServices jFinanziariaPort = jFinanziariaService.getJFinanziariaPort();
		BindingProvider prov = (BindingProvider) jFinanziariaPort;
		prov.getRequestContext().put(BindingProvider.USERNAME_PROPERTY, jenteBasicAuthUsername);
		prov.getRequestContext().put(BindingProvider.PASSWORD_PROPERTY, jenteBasicAuthPassword);
		prov.getRequestContext().put(BindingProvider.ENDPOINT_ADDRESS_PROPERTY, jenteWSUrl);

		RispostaGestioneProposte gestioneProposte = jFinanziariaPort.gestioneProposte(richiesta);

		String code = gestioneProposte.getCode();
		String message = gestioneProposte.getMessage();
		Boolean ok = gestioneProposte.isOk();

		if (!ok) {
			throw new ServiceException("Errore durante la comunicazione con JEnte :: code: " + code + ", message: " + message);
		}
	}
	
}
