package it.linksmt.assatti.cooperation.service.contabilita;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.google.gson.Gson;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DettaglioLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DocumentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpAccertamentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpegnoDaStampareDto;
import it.linksmt.assatti.cooperation.dto.contabilita.LiquidazioneImpegnoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.RigaLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.SoggettoDto;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DatiContabili;
import it.linksmt.assatti.datalayer.repository.DatiContabiliRepository;
import it.linksmt.assatti.service.DatiContabiliService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.StringUtil;

@Service
public class ContabilitaServiceWrapper {
	
	@Inject
	private ContabilitaService contabilitaService;
	
	@Inject
	private DatiContabiliRepository datiContabiliRepository;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private ReportService reportService;
	
	@Inject
	private DatiContabiliService datiContabiliService;
	
	private final Logger log = LoggerFactory.getLogger(ContabilitaServiceWrapper.class);
	
	public void sendBozza(Atto atto, long idProfilo) throws ContabilitaServiceException {
		
		if (StringUtil.isNull(atto.getContabileOggetto())) {
			throw new ContabilitaServiceException("Per effettuare l'invio dei dati al sistema contabile" + StringUtil.USER_MESSAGE_NEW_LINE
					+ "occorre specificare l'oggetto" + StringUtil.USER_MESSAGE_NEW_LINE
		        	+ "all'interno della Sezione Contabile.");
		}
		
		ContabilitaDto contabilitaDto = new ContabilitaDto();
		contabilitaDto.setCodiceTipoAtto(atto.getTipoAtto().getCodice());
		contabilitaDto.setOggetto(atto.getContabileOggetto());
//		contabilitaDto.setImportoTotale(new BigDecimal(atto.getContabileImporto().doubleValue()));
		contabilitaDto.setNumeroProposta(atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5));
		contabilitaDto.setDataCreazioneProposta(atto.getDataCreazione().toDate());
		contabilitaDto.setAnnoCreazioneProposta(atto.getDataCreazione().getYear());
		
		if (atto.getDataEsecutivita() != null) {
			contabilitaDto.setDataEsecutivita(atto.getDataEsecutivita().toDate());
		}
		
		try {
			contabilitaService.sendBozza(contabilitaDto);
		}
		catch(ServiceException se) {
			throw new ContabilitaServiceException("Ci scusiamo per il disagio." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Il sistema ha riscontrato un errore durante l'invio dei dati al sistema contabile." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Si prega di effettuare un nuovo tentativo.", se);
		}
		
		// Inserisco la data di ultimo invio per i dati contabili in maniera da fare andare avanti il processo
		DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
		if (datiCont == null) {
			datiCont = new DatiContabili(false);
			datiCont.setId(atto.getId());
			
			List<MovimentoContabileDto> listMovimenti = new ArrayList<MovimentoContabileDto>();
			datiCont.setDaticontabili(new Gson().toJson(listMovimenti));
		}
		datiCont.setDataUltimoInvio(new DateTime());
		datiContabiliRepository.save(datiCont);
		
		if (idProfilo > 0) {
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), idProfilo, atto.getStato(), 
					NomiAttivitaAtto.CONTABILITA_INVIO, null);
		}
	}
	
	
	public void updateMovimentiContabili(Atto atto, long idProfilo, boolean isBozza, boolean verificaMovimenti) throws ContabilitaServiceException {
		
		// Controllo che sia stato effettuato l'invio dei dati
		if (verificaMovimenti) {
			verificaMovimentiContabili(atto);
		}
		
		try {
			String codiceTipoAtto = atto.getTipoAtto().getCodice();
			String numeroRif = atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5);
			int annoRif = atto.getDataCreazione().getYear();
			
			if (!isBozza) {
				numeroRif = atto.getNumeroAdozione();
				annoRif = atto.getDataAdozione().getYear();
			}
			
			// TODO: patch atti che non prevedono movimenti contabili
			if (!verificaMovimenti && !contabilitaService.esisteBozzaAtto(codiceTipoAtto, numeroRif, annoRif, isBozza)) {
				return;
			}
			List<MovimentoContabileDto> listMovimenti = null;
			try {
				listMovimenti = contabilitaService
						.getMovimentiContabili(codiceTipoAtto, numeroRif, annoRif, isBozza);
			}
			catch(ServiceException se) {
				se.printStackTrace();
			}
			BigDecimal importoEntrata = null;
			BigDecimal importoUscita = null;
			
			DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
			if (datiCont == null) {
				datiCont = new DatiContabili(false);
				datiCont.setId(atto.getId());
			}else {
				datiCont.setIncludiMovimentiAtto(datiCont.getDatiRicevuti() != null?datiCont.getDatiRicevuti().booleanValue():false);
			}
			
			int countImpegni = 0;
			int countAccertamenti = 0;
			int countLiquidazioni = 0;
			int countRigheLiquidazioni = 0;
			
			if(listMovimenti!=null) {
				for(MovimentoContabileDto mov : listMovimenti) {
					//i dettagliliquidazione sono info aggiuntive inserite nella listMovimenti come oggetti a parte, ma non sono ulteriori movimenti
					if(mov!=null) {
						if(mov.getMovImpAcce() != null && mov.getMovImpAcce().getEu()!=null) {
							if(mov.getMovImpAcce().getEu().trim().toLowerCase().equals("e")) {
								countAccertamenti++;
							}else if(mov.getMovImpAcce().getEu().trim().toLowerCase().equals("u")) {
								countImpegni++;
							}
						}
						if(mov.getLiquidazione()!=null) {
							countLiquidazioni++;
							if(mov.getLiquidazione().getDocumento()!=null){
								countRigheLiquidazioni += mov.getLiquidazione().getDocumento().size();
							}
						}
					}
					
					if(mov.getMovImpAcce()!=null && mov.getMovImpAcce().getImporto()!=null && !mov.getMovImpAcce().getImporto().isEmpty() && mov.getMovImpAcce().getEu()!=null && !mov.getMovImpAcce().getEu().isEmpty()) {
						if(mov.getMovImpAcce().getEu().equalsIgnoreCase("e")) {
							if(importoEntrata!=null) {
								importoEntrata = importoEntrata.add(new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", ".")));
							}else {
								importoEntrata = new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", "."));
							}
						}else if(mov.getMovImpAcce().getEu().equalsIgnoreCase("u")) {
							if(importoUscita!=null) {
								importoUscita = importoUscita.add(new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", ".")));
							}else {
								importoUscita = new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", "."));
							}
						}
					}
					if(mov.getDettaglioLiquidazione()!=null && mov.getDettaglioLiquidazione().getListaCapitoli()!=null) {
						for(RigaLiquidazioneDto liq : mov.getDettaglioLiquidazione().getListaCapitoli()) {
							if(liq!=null && liq.getListaImpegni()!=null) {
								for(LiquidazioneImpegnoDto imp : liq.getListaImpegni()) {
									if(imp!=null && imp.getImportoTotaleImpegno()!=null && !imp.getImportoTotaleImpegno().isEmpty()) {
										if(importoUscita!=null) {
											importoUscita = importoUscita.add(new BigDecimal(imp.getImportoTotaleImpegno().replaceAll("\\.", "").replaceAll(",", ".")));
										}else {
											importoUscita = new BigDecimal(imp.getImportoTotaleImpegno().replaceAll("\\.", "").replaceAll(",", "."));
										}
									}
								}
							}
						}
					}
				}
				if(listMovimenti.size() > 0) {
					datiCont.setDatiRicevuti(true);
				}
			}
			
			if(listMovimenti!=null && listMovimenti.size()>0) {
				datiCont.setDaticontabili(new Gson().toJson(listMovimenti));
				if(importoEntrata!=null || importoUscita!=null) {
					if(importoEntrata!=null) {
						datiCont.setImportoEntrata(importoEntrata);
					}else {
						datiCont.setImportoEntrata(BigDecimal.ZERO);
					}
					if(importoUscita!=null) {
						datiCont.setImportoUscita(importoUscita);
					}else {
						datiCont.setImportoUscita(BigDecimal.ZERO);
					}
				}
			}else {
				if(datiCont.getDatiRicevuti() != null && datiCont.getDatiRicevuti().equals(true)) {
					datiCont.setImportoEntrata(BigDecimal.ZERO);
					datiCont.setImportoUscita(BigDecimal.ZERO);
				}
				datiCont.setDaticontabili(null);
				datiCont.setDatiRicevuti(false);
			}
			datiContabiliRepository.save(datiCont);
			
			if (idProfilo > 0) {
				String msg = "Num. Impegni: " + countImpegni + "<br />";
				msg += "Num. Accertamenti: " + countAccertamenti + "<br />";
				msg += "Num. Liquidazioni: " + countLiquidazioni + (countLiquidazioni > 0 ? " (" + countRigheLiquidazioni + " righe)" : "");
				registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), idProfilo, atto.getStato(), 
					NomiAttivitaAtto.CONTABILITA_RECUPERO, msg);
			}
		}
		catch(ServiceException se) {
			throw new ContabilitaServiceException("Ci scusiamo per il disagio." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Il sistema ha riscontrato un errore durante la lettura dei dati dal sistema contabile." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Si prega di effettuare un nuovo tentativo.", se);
		}
	}
	public boolean updateMovimentiContabiliAggiornati(Atto atto, long idProfilo, boolean isBozza, boolean verificaMovimenti, String codiceAzioneUtente) throws ContabilitaServiceException {
		
		boolean modificati = false;
		boolean isDD = Lists.newArrayList("DD").contains(atto.getTipoAtto().getCodice());
		boolean isDL = Lists.newArrayList("DL").contains(atto.getTipoAtto().getCodice());
		
		try {
			String codiceTipoAtto = atto.getTipoAtto().getCodice();
			String numeroRif = atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 5);
			int annoRif = atto.getDataCreazione().getYear();
			
			if (!isBozza) {
				numeroRif = atto.getNumeroAdozione();
				annoRif = atto.getDataAdozione().getYear();
			}
			
			// TODO: patch atti che non prevedono movimenti contabili
			if (!verificaMovimenti && !contabilitaService.esisteBozzaAtto(codiceTipoAtto, numeroRif, annoRif, isBozza)) {
				return modificati;
			}
			
			
			DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
			//INIZIO MODIFICA
			String datiContabiliPrec ="";
			if(atto.getDatiContabili()!=null) {
				
				
				List<MovimentoContabileDto> listMovimentiContabili = null;
				listMovimentiContabili = datiContabiliService.elencoMovimento(atto.getId());
				
				List<DettaglioLiquidazioneDto> listDettLiquidazione = new ArrayList<DettaglioLiquidazioneDto>();
				List<MovimentoLiquidazioneDto> listLiquidazione = new ArrayList<MovimentoLiquidazioneDto>();
				List<ImpAccertamentoDto> listMovImpAcce = new ArrayList<ImpAccertamentoDto>();
				
				if(listMovimentiContabili!=null) {
					for (MovimentoContabileDto movimentoContabileDto : listMovimentiContabili) {
						if (movimentoContabileDto.getDettaglioLiquidazione()!=null) {
							listDettLiquidazione.add(movimentoContabileDto.getDettaglioLiquidazione());
						}
						if(movimentoContabileDto.getLiquidazione()!=null) {
							listLiquidazione.add(movimentoContabileDto.getLiquidazione());
						} 
						if(movimentoContabileDto.getMovImpAcce()!=null) {
							listMovImpAcce.add(movimentoContabileDto.getMovImpAcce());
						}
					}
				}
				
				List<ImpegnoDaStampareDto> listaImpegniDaStampare = new ArrayList<ImpegnoDaStampareDto>();
				listaImpegniDaStampare = reportService.getlistaImpegniDaStampare(listDettLiquidazione);
				if(isDD || isDL) {
					
					for (ImpAccertamentoDto impAccertamentoDto : listMovImpAcce) {
						datiContabiliPrec += impAccertamentoDto.getEu()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEu();
						datiContabiliPrec += impAccertamentoDto.getEsercizio()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEsercizio();
						datiContabiliPrec += impAccertamentoDto.getCapitolo()!=null?impAccertamentoDto.getCapitolo():impAccertamentoDto.getCapitolo();
						datiContabiliPrec += impAccertamentoDto.getArticolo()!=null?impAccertamentoDto.getArticolo():impAccertamentoDto.getArticolo();
						datiContabiliPrec += impAccertamentoDto.getAnnoImpacc()!=null?impAccertamentoDto.getAnnoImpacc():impAccertamentoDto.getAnnoImpacc();
						datiContabiliPrec += impAccertamentoDto.getSubImpacc()!=null?impAccertamentoDto.getSubImpacc():impAccertamentoDto.getSubImpacc();
						datiContabiliPrec += impAccertamentoDto.getImportoImpacc()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getImportoImpacc();
						datiContabiliPrec += impAccertamentoDto.getCodDebBen()!=null?impAccertamentoDto.getCodDebBen():impAccertamentoDto.getCodDebBen();
						datiContabiliPrec += impAccertamentoDto.getDescCodDebBen()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getDescCodDebBen();
					}
					
					
					for (ImpegnoDaStampareDto impegnoDaStampareDto : listaImpegniDaStampare) {
						datiContabiliPrec += impegnoDaStampareDto.getCodiceCapitolo()!=null?impegnoDaStampareDto.getCodiceCapitolo():"";
						datiContabiliPrec += impegnoDaStampareDto.getMeccanograficoCapitolo()!=null?impegnoDaStampareDto.getMeccanograficoCapitolo():"";
						datiContabiliPrec += impegnoDaStampareDto.getDescrizioneCapitolo()!=null?impegnoDaStampareDto.getDescrizioneCapitolo():"";
						datiContabiliPrec += impegnoDaStampareDto.getImportoCapitolo()!=null?impegnoDaStampareDto.getImportoCapitolo():"";
						datiContabiliPrec += impegnoDaStampareDto.getImpegnoFormattato()!=null?impegnoDaStampareDto.getImpegnoFormattato():"";
						datiContabiliPrec += impegnoDaStampareDto.getDatiAtto()!=null?impegnoDaStampareDto.getDatiAtto():"";
						datiContabiliPrec += impegnoDaStampareDto.getDescrizioneImpegno()!=null?impegnoDaStampareDto.getDescrizioneImpegno():"";
						datiContabiliPrec += impegnoDaStampareDto.getImportoImpegno()!=null?impegnoDaStampareDto.getImportoImpegno():"";
						datiContabiliPrec += impegnoDaStampareDto.getCupImpegno()!=null?impegnoDaStampareDto.getCupImpegno():"";
						datiContabiliPrec += impegnoDaStampareDto.getCigImpegno()!=null?impegnoDaStampareDto.getCigImpegno():"";
						datiContabiliPrec += impegnoDaStampareDto.getAnnoLiq()!=null?impegnoDaStampareDto.getAnnoLiq():"";
						datiContabiliPrec += impegnoDaStampareDto.getNumeroLiq()!=null?impegnoDaStampareDto.getNumeroLiq():"";
						
						
						if(impegnoDaStampareDto.getListaSoggetti()!=null) {
							for (SoggettoDto soggettoDto : impegnoDaStampareDto.getListaSoggetti()) {
								datiContabiliPrec += soggettoDto.getSoggetto()!=null?soggettoDto.getSoggetto():"";
								datiContabiliPrec += soggettoDto.getDescr()!=null?soggettoDto.getDescr():"";
								datiContabiliPrec += soggettoDto.getNotePagamento()!=null?soggettoDto.getNotePagamento():"";
								datiContabiliPrec += soggettoDto.getModoPaga()!=null?soggettoDto.getModoPaga():"";
								for (DocumentoDto documentoDto : soggettoDto.getListaDocumenti()) {
									datiContabiliPrec += documentoDto.getTipoDoc()!=null?documentoDto.getTipoDoc():"";
									datiContabiliPrec += documentoDto.getNumeroDoc()!=null?documentoDto.getNumeroDoc():"";
									datiContabiliPrec += documentoDto.getDataDoc()!=null?documentoDto.getDataDoc().toString():"";
									datiContabiliPrec += documentoDto.getDataScadDoc()!=null?documentoDto.getDataScadDoc().toString():"";
									datiContabiliPrec += documentoDto.getImporto()!=null?documentoDto.getImporto():"";
									datiContabiliPrec += documentoDto.getCodiceCup()!=null?documentoDto.getCodiceCup():"";
									datiContabiliPrec += documentoDto.getCodiceCig()!=null?documentoDto.getCodiceCig():"";
								}
							}
						}
					}
				}
				
			}
			
			//FINE MODIFICA
			
			if (datiCont == null) {
				datiCont = new DatiContabili(false);
				datiCont.setId(atto.getId());
			}

			//VALORI INIZIALI
			BigDecimal impE1 = datiCont.getImportoEntrata()!=null?datiCont.getImportoEntrata():new BigDecimal(0);
			BigDecimal impU1 = datiCont.getImportoUscita()!=null?datiCont.getImportoUscita():new BigDecimal(0);
			
			int countImpegni = 0;
			int countAccertamenti = 0;
			int countLiquidazioni = 0;
			int countRigheLiquidazioni = 0;
			
			
			
			List<MovimentoContabileDto> listMovimenti = null;
			try {
				listMovimenti = contabilitaService
						.getMovimentiContabili(codiceTipoAtto, numeroRif, annoRif, isBozza);
			}
			catch(ServiceException se) {
				se.printStackTrace();
			}
			BigDecimal importoEntrata = null;
			BigDecimal importoUscita = null;
			
			if(listMovimenti!=null) {
				for(MovimentoContabileDto mov : listMovimenti) {
					//i dettagliliquidazione sono info aggiuntive inserite nella listMovimenti come oggetti a parte, ma non sono ulteriori movimenti
					if(mov!=null) {
						if(mov.getMovImpAcce() != null && mov.getMovImpAcce().getEu()!=null) {
							if(mov.getMovImpAcce().getEu().trim().toLowerCase().equals("e")) {
								countAccertamenti++;
							}else if(mov.getMovImpAcce().getEu().trim().toLowerCase().equals("u")) {
								countImpegni++;
							}
						}
						if(mov.getLiquidazione()!=null) {
							countLiquidazioni++;
							if(mov.getLiquidazione().getDocumento()!=null){
								countRigheLiquidazioni += mov.getLiquidazione().getDocumento().size();
							}
						}
					}
					
					if(mov.getMovImpAcce()!=null && mov.getMovImpAcce().getImporto()!=null && !mov.getMovImpAcce().getImporto().isEmpty() && mov.getMovImpAcce().getEu()!=null && !mov.getMovImpAcce().getEu().isEmpty()) {
						if(mov.getMovImpAcce().getEu().equalsIgnoreCase("e")) {
							if(importoEntrata!=null) {
								importoEntrata = importoEntrata.add(new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", ".")));
							}else {
								importoEntrata = new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", "."));
							}
						}else if(mov.getMovImpAcce().getEu().equalsIgnoreCase("u")) {
							if(importoUscita!=null) {
								importoUscita = importoUscita.add(new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", ".")));
							}else {
								importoUscita = new BigDecimal(mov.getMovImpAcce().getImporto().replaceAll("\\.", "").replaceAll(",", "."));
							}
						}
					}
					if(mov.getDettaglioLiquidazione()!=null && mov.getDettaglioLiquidazione().getListaCapitoli()!=null) {
						for(RigaLiquidazioneDto liq : mov.getDettaglioLiquidazione().getListaCapitoli()) {
							if(liq!=null && liq.getListaImpegni()!=null) {
								for(LiquidazioneImpegnoDto imp : liq.getListaImpegni()) {
									if(imp!=null && imp.getImportoTotaleImpegno()!=null && !imp.getImportoTotaleImpegno().isEmpty()) {
										if(importoUscita!=null) {
											importoUscita = importoUscita.add(new BigDecimal(imp.getImportoTotaleImpegno().replaceAll("\\.", "").replaceAll(",", ".")));
										}else {
											importoUscita = new BigDecimal(imp.getImportoTotaleImpegno().replaceAll("\\.", "").replaceAll(",", "."));
										}
									}
								}
							}
						}
					}
				}
				if(listMovimenti.size() > 0) {
					datiCont.setDatiRicevuti(true);
				}
			}
			
			if(listMovimenti!=null && listMovimenti.size()>0) {
				datiCont.setDaticontabili(new Gson().toJson(listMovimenti));
				if(importoEntrata!=null || importoUscita!=null) {
					if(importoEntrata!=null) {
						datiCont.setImportoEntrata(importoEntrata);
					}else {
						datiCont.setImportoEntrata(BigDecimal.ZERO);
					}
					if(importoUscita!=null) {
						datiCont.setImportoUscita(importoUscita);
					}else {
						datiCont.setImportoUscita(BigDecimal.ZERO);
					}
				}
			}else {
				if(datiCont.getDatiRicevuti() != null && datiCont.getDatiRicevuti().equals(true)) {
					datiCont.setImportoEntrata(BigDecimal.ZERO);
					datiCont.setImportoUscita(BigDecimal.ZERO);
				}
				datiCont.setDaticontabili(null);
				datiCont.setDatiRicevuti(false);
			}
			
			List<DettaglioLiquidazioneDto> listDettLiquidazione = new ArrayList<DettaglioLiquidazioneDto>();
			List<MovimentoLiquidazioneDto> listLiquidazione = new ArrayList<MovimentoLiquidazioneDto>();
			List<ImpAccertamentoDto> listMovImpAcce = new ArrayList<ImpAccertamentoDto>();
			if(listMovimenti!=null) {
				for (MovimentoContabileDto movimentoContabileDto : listMovimenti) {
					if (movimentoContabileDto.getDettaglioLiquidazione()!=null) {
						listDettLiquidazione.add(movimentoContabileDto.getDettaglioLiquidazione());
					}
					if(movimentoContabileDto.getLiquidazione()!=null) {
						listLiquidazione.add(movimentoContabileDto.getLiquidazione());
					} 
					if(movimentoContabileDto.getMovImpAcce()!=null) {
						listMovImpAcce.add(movimentoContabileDto.getMovImpAcce());
					}
				}
			}
			String datiContabiliJente = "";
			List<ImpegnoDaStampareDto> listaImpegniDaStampare = new ArrayList<ImpegnoDaStampareDto>();
			listaImpegniDaStampare = reportService.getlistaImpegniDaStampare(listDettLiquidazione);
			if(isDD || isDL) {
				
				for (ImpAccertamentoDto impAccertamentoDto : listMovImpAcce) {
					datiContabiliJente += impAccertamentoDto.getEu()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEu();
					datiContabiliJente += impAccertamentoDto.getEsercizio()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEsercizio();
					datiContabiliJente += impAccertamentoDto.getCapitolo()!=null?impAccertamentoDto.getCapitolo():impAccertamentoDto.getCapitolo();
					datiContabiliJente += impAccertamentoDto.getArticolo()!=null?impAccertamentoDto.getArticolo():impAccertamentoDto.getArticolo();
					datiContabiliJente += impAccertamentoDto.getAnnoImpacc()!=null?impAccertamentoDto.getAnnoImpacc():impAccertamentoDto.getAnnoImpacc();
					datiContabiliJente += impAccertamentoDto.getSubImpacc()!=null?impAccertamentoDto.getSubImpacc():impAccertamentoDto.getSubImpacc();
					datiContabiliJente += impAccertamentoDto.getImportoImpacc()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getImportoImpacc();
					datiContabiliJente += impAccertamentoDto.getCodDebBen()!=null?impAccertamentoDto.getCodDebBen():impAccertamentoDto.getCodDebBen();
					datiContabiliJente += impAccertamentoDto.getDescCodDebBen()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getDescCodDebBen();
				}
				
				
				for (ImpegnoDaStampareDto impegnoDaStampareDto : listaImpegniDaStampare) {
					datiContabiliJente += impegnoDaStampareDto.getCodiceCapitolo()!=null?impegnoDaStampareDto.getCodiceCapitolo():"";
					datiContabiliJente += impegnoDaStampareDto.getMeccanograficoCapitolo()!=null?impegnoDaStampareDto.getMeccanograficoCapitolo():"";
					datiContabiliJente += impegnoDaStampareDto.getDescrizioneCapitolo()!=null?impegnoDaStampareDto.getDescrizioneCapitolo():"";
					datiContabiliJente += impegnoDaStampareDto.getImportoCapitolo()!=null?impegnoDaStampareDto.getImportoCapitolo():"";
					datiContabiliJente += impegnoDaStampareDto.getImpegnoFormattato()!=null?impegnoDaStampareDto.getImpegnoFormattato():"";
					datiContabiliJente += impegnoDaStampareDto.getDatiAtto()!=null?impegnoDaStampareDto.getDatiAtto():"";
					datiContabiliJente += impegnoDaStampareDto.getDescrizioneImpegno()!=null?impegnoDaStampareDto.getDescrizioneImpegno():"";
					datiContabiliJente += impegnoDaStampareDto.getImportoImpegno()!=null?impegnoDaStampareDto.getImportoImpegno():"";
					datiContabiliJente += impegnoDaStampareDto.getCupImpegno()!=null?impegnoDaStampareDto.getCupImpegno():"";
					datiContabiliJente += impegnoDaStampareDto.getCigImpegno()!=null?impegnoDaStampareDto.getCigImpegno():"";
					datiContabiliJente += impegnoDaStampareDto.getAnnoLiq()!=null?impegnoDaStampareDto.getAnnoLiq():"";
					datiContabiliJente += impegnoDaStampareDto.getNumeroLiq()!=null?impegnoDaStampareDto.getNumeroLiq():"";
					
					
					if(impegnoDaStampareDto.getListaSoggetti()!=null) {
						for (SoggettoDto soggettoDto : impegnoDaStampareDto.getListaSoggetti()) {
							datiContabiliJente += soggettoDto.getSoggetto()!=null?soggettoDto.getSoggetto():"";
							datiContabiliJente += soggettoDto.getDescr()!=null?soggettoDto.getDescr():"";
							datiContabiliJente += soggettoDto.getNotePagamento()!=null?soggettoDto.getNotePagamento():"";
							datiContabiliJente += soggettoDto.getModoPaga()!=null?soggettoDto.getModoPaga():"";
							for (DocumentoDto documentoDto : soggettoDto.getListaDocumenti()) {
								datiContabiliJente += documentoDto.getTipoDoc()!=null?documentoDto.getTipoDoc():"";
								datiContabiliJente += documentoDto.getNumeroDoc()!=null?documentoDto.getNumeroDoc():"";
								datiContabiliJente += documentoDto.getDataDoc()!=null?documentoDto.getDataDoc().toString():"";
								datiContabiliJente += documentoDto.getDataScadDoc()!=null?documentoDto.getDataScadDoc().toString():"";
								datiContabiliJente += documentoDto.getImporto()!=null?documentoDto.getImporto():"";
								datiContabiliJente += documentoDto.getCodiceCup()!=null?documentoDto.getCodiceCup():"";
								datiContabiliJente += documentoDto.getCodiceCig()!=null?documentoDto.getCodiceCig():"";
							}
						}
					}
				}
			}
			
			//VALORI FINALI DA CONFRONTARE
			BigDecimal impE2 = datiCont.getImportoEntrata()!=null?datiCont.getImportoEntrata():new BigDecimal(0);
			BigDecimal impU2 = datiCont.getImportoUscita()!=null?datiCont.getImportoUscita():new BigDecimal(0);

			datiContabiliRepository.save(datiCont);
			
			log.info("Importo Entrata vecchio"+impE1);
			log.info("Importo Entrata nuovo"+impE2);
			log.info("Importo Uscita vecchio"+impU1);
			log.info("Importo Uscita nuovo"+impU2);
			log.info("Importo Uscita vecchio"+impU1);
			log.info("*****datiContabiliSuDB: "+datiContabiliPrec);
			log.info("*****datiContabiliJente: "+datiContabiliJente);
			modificati = impE1.compareTo(impE2) != 0|| impU1.compareTo(impU2) != 0 || !datiContabiliPrec.equalsIgnoreCase(datiContabiliJente);
			
			if (modificati && idProfilo > 0) {
				String msg = "Il sistema ha riscontrato una VARIAZIONE DEI DATI CONTABILI rispetto quelli contenuti nella PRECEDENTE VERSIONE DELLA PROPOSTA: pertanto,  NE E' STATA CREATA UNA NUOVA VERSIONE. \n"
						+ " VISUALIZZARE la Sezione Contabile e la NUOVA VERSIONE DELLA PROPOSTA (contenuta nella sezione Documenti), allo scopo di prendere visione delle suddette modifiche PRIMA di apporre definitivamente il visto.";
				registrazioneAvanzamentoService.registraAvanzamentoConWarning(atto.getId().longValue(), idProfilo, NomiAttivitaAtto.CONTABILITA_RECUPERO, codiceAzioneUtente, msg);
			}
			
		}
		catch(ServiceException se) {
			throw new ContabilitaServiceException("Ci scusiamo per il disagio." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Il sistema ha riscontrato un errore durante la lettura dei dati dal sistema contabile." + StringUtil.USER_MESSAGE_NEW_LINE
		        	  +"Si prega di effettuare un nuovo tentativo.", se);
		}
		
		return modificati;
	}
	
	
	
	
	

	public void verificaMovimentiContabili(Atto atto) throws ContabilitaServiceException {
		
		DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
		if (datiCont == null || datiCont.getDataUltimoInvio()==null) {
			throw new ContabilitaServiceException("Occorre effettuare l'invio dei dati " + StringUtil.USER_MESSAGE_NEW_LINE
					+"al sistema contabile prima di proseguire.");
		}
	}
}
