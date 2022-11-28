package it.linksmt.assatti.service;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.CodiciAzioniUtente;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.dto.DocumentoPdfDto;
import it.linksmt.assatti.fdr.config.FdrClientProps;
import it.linksmt.assatti.fdr.dto.FirmaRemotaRequestDTO;
import it.linksmt.assatti.fdr.dto.FirmaRemotaResponseDTO;
import it.linksmt.assatti.fdr.exception.FirmaRemotaException;
import it.linksmt.assatti.fdr.service.FdrWsUtil;
import it.linksmt.assatti.fdr.service.FirmaRemotaService;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;

/**
 * Service class for managing atti.
 */
@Service
@Transactional
public class TransactionService {
	private final Logger log = LoggerFactory.getLogger(TransactionService.class);
	
	@PersistenceContext
	private EntityManager entityManager;

    @Inject
    private ParereService parereService;
    
    @Inject
	private BpmWrapperUtil camundaUtil;
    
    @Inject
    private AttoService attoService;
    
    @Inject
    private WorkflowServiceWrapper workflowService;
    
    @Inject
	private DmsService dmsService;
    
    @Inject
	private ServiceUtil serviceUtil;
    
    @Inject
	private DocumentoPdfService documentoPdfService;
    
    @Inject ModelloHtmlService modelloHtmlService;
    
    @Inject
	private ReportService reportService;
    
    @Inject
	private ContabilitaServiceWrapper contabilitaServiceWrapper;
    
    @Transactional
	public void salvaParereAndEseguiAzione(final String idTask, final Long profiloId, Parere parere, Atto atto, DecisioneWorkflowDTO decisione) throws Exception {
    	parereService.save(idTask, profiloId, parere, atto, decisione);
	    workflowService.eseguiAzione(idTask, profiloId, decisione, null);
	}
    
    @Transactional
    public void visto(final String taskBpmId, final Long profiloId, Parere parere, Atto atto, DecisioneWorkflowDTO decisione) {
    	try {
    		
	    	boolean datiContabiliCambiati = false;
	    	DocumentoPdf docPdf = null;
	    	if(decisione!=null && (decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO) || decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO_DL)) ) {
	    		log.info("VISTO MASSIVO: "+decisione.getCodiceAzioneUtente()+" per "+atto.getCodiceCifra());
	    		String rigenerazioneDocumento = "";
	    		VariableInstance v = workflowService.getVariabileTask(taskBpmId, AttoProcessVariables.VAR_RIGENERAZIONE_DOCUMENTO);
	    		if(v!=null) {
	    			rigenerazioneDocumento = (String)v.getValue();
	    		}
	    		
	    		if(rigenerazioneDocumento.equalsIgnoreCase("SI")) {
	    			
	    			datiContabiliCambiati = false;
	    			
	    			if(decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO_DL) || (atto.getTipoIter()!= null && atto.getTipoIter().getCodice().equalsIgnoreCase("CON_VERIFICA_CONTABILE")))
	    			{
	    				//fix problema timeout 20220331 
	    				if(atto.getDatiContabili()!=null && atto.getDatiContabili().getIncludiMovimentiAtto()!=null && atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue()==true) {
	    					datiContabiliCambiati = contabilitaServiceWrapper.updateMovimentiContabiliAggiornati(atto, profiloId, true, true,decisione.getCodiceAzioneUtente());
		    				log.info("VISTO MASSIVO: datiContabiliCambiati="+datiContabiliCambiati+" per "+atto.getCodiceCifra());
	    				}
	    			}
	    			if(datiContabiliCambiati) {
	    				atto = attoService.save(atto);
	    			}
	    			
	    			String tipoDocumento = null;
	    			if(atto.getTipoAtto()!=null && atto.getTipoAtto().getCodice().equalsIgnoreCase("dec")) {
	    				tipoDocumento = "PROPOSTA_DECRETO";
	    			}else {
	    				tipoDocumento = decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO)?"PROPOSTA_DETERMINA_DIRIGENZIALE":"PROPOSTA_DETERMINA_LIQUIDAZIONE";
	    			}
					List<ModelloHtml> modelloHtmlList = modelloHtmlService.findByTipoDocumento(tipoDocumento);
					if(modelloHtmlList!=null && modelloHtmlList.size() == 1) {
						boolean omissis = false;
						if(decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO)) {
							if ((atto.getRiservato() == null || atto.getRiservato().equals(false)) && (atto.getPubblicazioneIntegrale() == null || atto.getPubblicazioneIntegrale().equals(false))) {
								omissis = true;
							}
						}
						ReportDTO reportDto1 = new ReportDTO();
						ModelloHtml modelloHtml1 = modelloHtmlList.get(0);
						reportDto1.setIdAtto(atto.getId());
						reportDto1.setIdModelloHtml(modelloHtml1.getId());
						reportDto1.setOmissis(false);
						java.io.File pdfAtto1 = reportService.previewAtto(atto, reportDto1);
						log.info("VISTO MASSIVO: salvaDocumentoPdf per "+atto.getCodiceCifra());
						docPdf = documentoPdfService.salvaDocumentoPdf(atto, pdfAtto1, tipoDocumento, false, false, false, false);
						if(docPdf!=null) {
							log.info("VISTO MASSIVO: salvaDocumentoPdf - documento creato "+atto.getCodiceCifra());
						}else {
							log.info("VISTO MASSIVO: salvaDocumentoPdf - documento NON creato "+atto.getCodiceCifra());
						}
						//se omissis genero anche la versione omissis della proposta
						if(omissis) {
							ReportDTO reportDtoOmissis = new ReportDTO();
							ModelloHtml modelloHtmlOmissis = modelloHtmlList.get(0);
							reportDtoOmissis.setIdAtto(atto.getId());
							reportDtoOmissis.setIdModelloHtml(modelloHtmlOmissis.getId());
							reportDtoOmissis.setOmissis(omissis);
							java.io.File pdfAttoOmissis = reportService.previewAtto(atto, reportDtoOmissis);
							log.info("VISTO MASSIVO: salvaDocumentoPdf omissis per "+atto.getCodiceCifra());
							documentoPdfService.salvaDocumentoPdf(atto, pdfAttoOmissis, tipoDocumento, omissis, false, false, false);
						}
					}
	    		}
	    		
	    		
	    	}
	    	//datiContabiliCambiati e docPDf != null non deve fare nulla
	    	
	    	if(datiContabiliCambiati && docPdf!=null) {
	    		log.info("VISTO MASSIVO:  "+atto.getCodiceCifra()+" Risultano modificati i dati contabili ed è stata creata una nuova versione del documento");
	    	}else if(!datiContabiliCambiati)  {
	    		log.info("VISTO MASSIVO: "+atto.getCodiceCifra()+" salvo il visto ed avanzo il processo per "+atto.getCodiceCifra());
				parereService.save(taskBpmId, profiloId, parere, atto, decisione);
				workflowService.eseguiAzione(taskBpmId, profiloId, decisione, null);
	    	}
	    	
			
    	}catch(Exception e) {
    		if(e instanceof RuntimeException) {
    			throw (RuntimeException)e;
    		}else {
    			throw new RuntimeException("Errore durante il processo di visto massivo", e);
    		}
    	}
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void generazioneFirma(String idTask, List<DecisioneWorkflowDTO> decisioni, JsonElement el, Long profiloId, String codiceFiscale, String password, String otp, String sessionId) {
    	try {
    		List<File> pdfDaFirmare = new ArrayList<File>();
			List<byte[]> filesDaFirmare = new ArrayList<byte[]>();
			List<TipoDocumento> tipiDocumento = new ArrayList<TipoDocumento>();
			List<FirmaMassivaBean> firmaMassivaList = new ArrayList<>();
	    	List<DocumentoPdf> documentiGenerati = new ArrayList<DocumentoPdf>();
	    	
	    	if(el!=null) {
	    		//prevede generazione
				for(DecisioneWorkflowDTO decisione : decisioni) {
			    	if(decisione!=null) {
						Long attoId = el.getAsJsonObject().get("attoId").getAsLong();
						Long modelloId = el.getAsJsonObject().get("modelloId").getAsLong();
						JsonArray modelliSolaGenerazioneJsonArr = (el.getAsJsonObject().has("modelliSolaGenerazioneIds") && el.getAsJsonObject().get("modelliSolaGenerazioneIds").isJsonArray() ? el.getAsJsonObject().get("modelliSolaGenerazioneIds").getAsJsonArray() : null);
				    	List<Long> modelliSolaGenerazioneList = null;
						if(modelliSolaGenerazioneJsonArr!=null && modelliSolaGenerazioneJsonArr.iterator().hasNext()) {
							modelliSolaGenerazioneList = new ArrayList<Long>();
							for(JsonElement je : modelliSolaGenerazioneJsonArr) {
								if(je!=null) {
									modelliSolaGenerazioneList.add(je.getAsLong());
								}
							}
						}
						Atto atto = attoService.findOneSimple(attoId);
						
						if(modelliSolaGenerazioneList!=null && modelliSolaGenerazioneList.size() > 0) {
							List<DocumentoPdf> docs = attoService.generaDocumenti(profiloId, atto, idTask, modelliSolaGenerazioneList, false);
							documentiGenerati.addAll(docs);
						}
						
						List<Long> modelloList = new ArrayList<Long>();
						modelloList.add(modelloId);
						if(CodiciAzioniUtente.NUMERA_GENERA_FIRMA_ATTO.equalsIgnoreCase(el.getAsJsonObject().get("typeOperazione").getAsString())) {
							if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
								if(atto.getDatiContabili()!=null && atto.getDatiContabili().getIncludiMovimentiAtto()!=null && atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue()==true) {
									contabilitaServiceWrapper.updateMovimentiContabili(atto, profiloId, false, false);
								}
							}
							List<DocumentoPdf> docs = attoService.generaDocumenti(profiloId, atto, idTask, modelloId, true);
							documentiGenerati.addAll(docs);
						}else if(CodiciAzioniUtente.GENERA_FIRMA_ATTO.equalsIgnoreCase(el.getAsJsonObject().get("typeOperazione").getAsString())) {
							List<DocumentoPdf> docs = attoService.generaDocumenti(profiloId, atto, idTask, modelloId, false);
							documentiGenerati.addAll(docs);
						}else if(CodiciAzioniUtente.GENERA_FIRMA_REG_TECN.equalsIgnoreCase(el.getAsJsonObject().get("typeOperazione").getAsString())) {
							Parere parere = new Parere();
							parere.setTitolo(decisione.getTipoParere());
							parere.setParereSintetico("visto");
							VariableInstance vResp = workflowService.getVariabileTask(idTask, ConfigurazioneTaskEnum.VERIFICA_RESPONSABILE_TECNICO.getCodice());
							Long idProfiloResp = camundaUtil.getIdProfiloByUsernameBpm(vResp.toString());
							parere = parereService.save(idTask, idProfiloResp, parere, atto, decisione);
							if(atto.getPareri()==null) {
								atto.setPareri(new HashSet<Parere>());
							}
							atto.getPareri().add(parere);
							List<DocumentoPdf> docs = attoService.generaDocumenti(profiloId, atto, idTask, modelloId, false);
							documentiGenerati.addAll(docs);
						}else if(CodiciAzioniUtente.GENERA_FIRMA_REG_CONT.equalsIgnoreCase(el.getAsJsonObject().get("typeOperazione").getAsString())){
							Parere parere = new Parere();
							parere.setTitolo(decisione.getTipoParere());
							parere.setParereSintetico("visto");
							VariableInstance vResp = workflowService.getVariabileTask(idTask, ConfigurazioneTaskEnum.VERIFICA_RESPONSABILE_CONTABILE.getCodice());
							Long idProfiloResp = camundaUtil.getIdProfiloByUsernameBpm(vResp.toString());
							parere = parereService.save(idTask, idProfiloResp, parere, atto, decisione);
							if(atto.getPareri()==null) {
								atto.setPareri(new HashSet<Parere>());
							}
							atto.getPareri().add(parere);
							List<DocumentoPdf> docs = attoService.generaDocumenti(profiloId, atto, idTask, modelloId, false);
							documentiGenerati.addAll(docs);
						}else {
							throw new GestattiCatchedException("Tipo firma massiva non gestita per l'atto " + atto.getCodiceCifra());
						}
			    	}
				}
	    	}
			
			List<DocumentoPdfDto> listDocumentoPdfDto = workflowService.getElencoDocumentiDaFirmare(idTask);
    		if (listDocumentoPdfDto.size() > 0) {
    			FirmaMassivaBean beanFirma = new FirmaMassivaBean();
    			beanFirma.taskId = idTask;
    			beanFirma.fileNames = new ArrayList<>();
    			
    			for (DocumentoPdfDto docPdf : listDocumentoPdfDto) {
    				
    				Long idDocumentoPdf = new Long(docPdf.getId());
    				DocumentoPdf documentoPdf = documentoPdfService.findById(idDocumentoPdf);
    				
    				File pdf = documentoPdf.getFile();
    				pdfDaFirmare.add(pdf);
    				filesDaFirmare.add(dmsService.getContent(pdf.getCmisObjectId()));
    				tipiDocumento.add(documentoPdf.getTipoDocumento());
    				
    				beanFirma.fileNames.add(pdf.getNomeFile());
    			}
    			
    			firmaMassivaList.add(beanFirma);
    		}
    		
    		int [] firmeOrig = new int [filesDaFirmare.size()];
			List<FirmaRemotaRequestDTO> reqFirma = new ArrayList<FirmaRemotaRequestDTO>();
			for (int i=0; i< filesDaFirmare.size(); i++) {
				if(filesDaFirmare.get(i)==null || filesDaFirmare.get(i).length == 0L) {
					throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "content of file to be signed is empty");
				}
				PDDocument signedFile = PDDocument.load(new ByteArrayInputStream(filesDaFirmare.get(i)));
				int nFirme = signedFile.getSignatureFields().size();
				firmeOrig[i] = nFirme;
				FirmaRemotaRequestDTO req = new FirmaRemotaRequestDTO(
						pdfDaFirmare.get(i).getNomeFile(), filesDaFirmare.get(i));
				reqFirma.add(req);
				
				log.info("File da firmare: " + pdfDaFirmare.get(i).getNomeFile() );
			}			
			
			List<FirmaRemotaResponseDTO> listFirmaRemotaResponseDTO = FirmaRemotaService.firmaPades(codiceFiscale, password, otp, reqFirma, sessionId);
			
			Atto curAtto = null;
			FirmaMassivaBean beanFirma = null;
			int fileCounter = 0;
			int taskCounter = 0;
			
			List<DocumentoPdf> documentiFirmati = new ArrayList<>();
			
			for(int i=0; i<listFirmaRemotaResponseDTO.size(); i++) {
				FirmaRemotaResponseDTO firmaRemotaResponseDTO = listFirmaRemotaResponseDTO.get(i);
				
				PDDocument signedFile = PDDocument.load(new ByteArrayInputStream(firmaRemotaResponseDTO.getDocument()));
				if(!FdrClientProps.getProperty("fdrws.mode").trim().equalsIgnoreCase("fake")) {
					int nFirme = signedFile.getSignatureFields().size();
					if(firmeOrig==null || firmeOrig.length < i+1) {
						throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "errore in fase di firma");
					}
					if(firmeOrig[i] + 1 != nFirme) {
						throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "errore in fase di firma - al documento non risulta essere stata apportata la firma");
					}
				}
				
				log.info("File firmato: " + firmaRemotaResponseDTO.getFileName() );
				
				if (fileCounter < 1) {
					beanFirma = firmaMassivaList.get(taskCounter);
					taskCounter++;
					
					fileCounter = beanFirma.fileNames.size();
					if (fileCounter < 1) {
						continue;
					}
					
					curAtto = workflowService.getAttoByTaskId(beanFirma.taskId);
				}
				
				String unsignedFile = beanFirma.fileNames.get(beanFirma.fileNames.size() - fileCounter);
				fileCounter--;
				
				if (!unsignedFile.equals(firmaRemotaResponseDTO.getFileName())) {
					throw new GestattiCatchedException("Errore di allineamento dei file firmati con quelli originali.");
				}
				
				// Salvo documento su repository documentale
				String cmisObjectId = null;
				String attoFolderPath = serviceUtil.buildDocumentPath(
						tipiDocumento.get(i), curAtto.getAoo(), curAtto.getTipoAtto(), curAtto.getCodiceCifra());
				
				cmisObjectId = dmsService.save(
						attoFolderPath, firmaRemotaResponseDTO.getDocument(), 
						pdfDaFirmare.get(i).getNomeFile(), pdfDaFirmare.get(i).getContentType(), null);
				
				File pdfFirmato = new File();
				pdfFirmato.setContentType(pdfDaFirmare.get(i).getContentType());
				pdfFirmato.setNomeFile(pdfDaFirmare.get(i).getNomeFile());
				pdfFirmato.setCmisObjectId(cmisObjectId);
				pdfFirmato.setSha256Checksum(FileChecksum.calcolaImpronta(firmaRemotaResponseDTO.getDocument()));
				pdfFirmato.setSize(new Long(firmaRemotaResponseDTO.getDocument().length));
	
				log.debug("Allegato without parere");
				DocumentoPdf documentoRiferimento = documentoPdfService.saveFileFirmato(
						curAtto, pdfFirmato, profiloId, tipiDocumento.get(i), false);
				
				documentiFirmati.add(documentoRiferimento);
				documentiGenerati.add(documentoRiferimento);
				if (fileCounter < 1) {
					// Integrazione con BPM
					List<DecisioneWorkflowDTO> lstDecMassiva = serviceUtil.getDecisioneMassiva(beanFirma.taskId, profiloId);
					if(lstDecMassiva != null && !lstDecMassiva.isEmpty()) {
						workflowService.eseguiAzione(beanFirma.taskId, profiloId, lstDecMassiva.get(0), null);
					}
				}
			}
    	}catch(Exception e) {
    		if(e instanceof RuntimeException) {
    			throw (RuntimeException)e;
    		}else {
    			throw new RuntimeException("Errore durante il processo di firma massiva", e);
    		}
    	}
    }

    private class FirmaMassivaBean {
    	String taskId = null;
    	List<String> fileNames = null;
    }
}
