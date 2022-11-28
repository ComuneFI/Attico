package it.linksmt.assatti.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.TreeSet;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.itextpdf.text.DocumentException;

import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.VerbaleRepository;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.NotFoundException;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class VerbaleService {
	private final Logger log = LoggerFactory.getLogger(VerbaleService.class);

	@Inject
	private VerbaleRepository verbaleRepository; 
	
	@Inject
	private ReportService reportService;
	
	@Inject
	private SedutaGiuntaService sedutaGiuntaService;
	@Inject
    private SedutaGiuntaRepository sedutaGiuntaRepository;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
//	@Inject 
//    private DocumentoPdfRepository documentoPdfRepository;
	
	@Inject
	private SottoscrittoreSedutaGiuntaService sottoscrittoreService;
	
//	@Inject
//	private NewsService notificheService;
	
	//TODO @Inject
	//private ProtocollazioneDAO protocollazione;
	
//	@Inject
//    private Environment env;
	
	//TODO @Inject
	//private FirmaRemotaService firmaRemotaService;

//	@Inject
//	private UtenteService utenteService;
//	
//	@Inject
//	private DocumentoInformaticoService documentoInformaticoService;
//	
//	@Inject
//	private RiversamentoPoolService riversamentoPoolService;
//	
//	@Inject
//	private ServiceUtil serviceUtil;
//	
//	private static DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	@Transactional
	public void generaDocumentoPerFirma(ReportDTO reportDto,Boolean firmato,String firmatario,String createdBy) throws IOException, DocumentException, GestattiCatchedException, DmsException  {
		log.debug( "generaDocumentoAnnullamento:"+ reportDto);
		
		Verbale verbale = verbaleRepository.getOne(reportDto.getIdAtto());
		SedutaGiunta seduta = verbale.getSedutaGiunta();
		
		
		
		reportDto.setIdSeduta(reportDto.getIdAtto());
		File result = reportService.previewVerbale(reportDto); 
		
		
		documentoPdfService.saveVerbalePdf(verbale, result, firmato, firmatario, createdBy);
		
		
		//MODIFICA 2019 02 19 IL VERBALE NON VIENE PIU FIRMATO
		//verbale.setStato(SedutaGiuntaConstants.statiVerbale.verbaleInAttesaDiFirma.toString());
		verbale.setStato(SedutaGiuntaConstants.statiVerbale.verbaleConsolidato.toString());
		verbaleRepository.save(verbale);
		
		seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleGenerato.toString());
		seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaVerbalizzata.toString());
		seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaVerbalizzata.getFase());
		//SottoscrittoreSedutaGiunta sottoscrittore = sottoscrittoreService.getNextFirmatarioVerbale(verbale.getId());
		//seduta.setSottoscittoreDocumento(sottoscrittore.getProfilo());
		
		sedutaGiuntaRepository.save(seduta);
	}

	@Transactional
	public boolean isFirmaCompleta(Long id) throws NotFoundException {
		
		Verbale verbale = verbaleRepository.getOne(id);
		if (verbale == null){
			throw new NotFoundException(String.format("Il DB non contiene nessun Verbale con id: %s", id));
		}
		
		return isFirmaCompleta(verbale);
	}	@Transactional
	public boolean isFirmaCompleta(Verbale verbale) {
		boolean retValue = false;
		
		log.debug( "isFirmaCompleta - Verbale id:"+ verbale.getId());
		
		if (verbale.getStato().equals(SedutaGiuntaConstants.statiVerbale.verbaleInAttesaDiFirma.toString())){
			retValue = (sottoscrittoreService.getNextFirmatarioVerbale(verbale.getId()) == null);
		}
		
		log.debug(String.format("\"isFirmaCompleta - Verbale id:%s firma completa:%s", verbale.getId(), retValue));
		
		return retValue;
	}
	
	@Transactional(readOnly = true)
	public Verbale findOne(Long idVerbale){
		Verbale verbale = verbaleRepository.findOne(idVerbale);
		
		if (verbale != null && verbale.getAllegati() != null)
			log.debug(String.format("Nr allegati del verbale :%s", verbale.getAllegati().size()));
		
		if (verbale != null && verbale.getStato().equals(SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione.toString())){
			if (verbale.getDocumentiPdf() == null)
				verbale.setDocumentiPdf(new ArrayList<DocumentoPdf>());
			
			if (verbale.getAllegati() == null)
				verbale.setAllegati(new HashSet<DocumentoInformatico>());
			
			if (verbale.getSottoscrittori() == null)
				verbale.setSottoscrittori(new TreeSet<SottoscrittoreSedutaGiunta>());
		} else {
			if (verbale.getSottoscrittori() != null)
				log.debug(String.format("Verbale id:%s ha %s sottoscrittori", verbale.getId(), verbale.getSottoscrittori().size()));
			else
				log.debug(String.format("Verbale id:%s non ha sottoscrittori!!", verbale.getId()));
		}
		
		return verbale;
	}
	
	public void addSottoscrittoreVerbale(Verbale verbale, Profilo profilo, int ordineFirma) {
		if (profilo != null){
			SottoscrittoreSedutaGiunta sottoscrittore = new SottoscrittoreSedutaGiunta();
			sottoscrittore.setOrdineFirma(ordineFirma);
			sottoscrittore.setFirmato(new Boolean(false));
			sottoscrittore.setProfilo(profilo);
			sottoscrittore.setVerbale(verbale);
			if (profilo.getHasQualifica()!=null && profilo.getHasQualifica().size() > 0){
				sottoscrittore.setQualificaProfessionale(profilo.getHasQualifica().iterator().next());
			}
			
			verbale.getSottoscrittori().add(sottoscrittore);
		}
	}
	
	
	/**
	 * Ripulisce i riferimento alla classe verbale per tutte le entity ad esso collegate,
	 * per evitare l'errore di ERR_INCOMPLETE_CHUNKED_ENCODING...
	 * @param verbale
	 * @return
	 */
	@Transactional(readOnly = true)
	public Verbale svuotaRiferimenti(Verbale verbale){
		Long idVerbale = verbale.getId();
		Verbale verbaleVuoto = new Verbale(idVerbale);
		Verbale retValue = verbaleRepository.findOne(idVerbale);
		
		if (retValue.getNarrativa() != null && retValue.getNarrativa().getTesto() != null){
			log.debug("Verbale id:{} Testo narrativa :{} caratteri...", retValue.getId(), retValue.getNarrativa().getTesto().length());
		}
		if (retValue.getNoteFinali() != null && retValue.getNoteFinali().getTesto() != null){
			log.debug("Verbale id:{} Testo note finali :{} caratteri...", retValue.getId(), retValue.getNoteFinali().getTesto().length());
		}
		
		retValue.setSedutaGiunta(new SedutaGiunta(
				retValue.getSedutaGiunta().getId(), 
				retValue.getSedutaGiunta().getLuogo(), 
				retValue.getSedutaGiunta().getPrimaConvocazioneInizio()));
		
		if (retValue.getAllegati() != null){
			for (DocumentoInformatico allegato : retValue.getAllegati()){
				allegato.setVerbale(verbaleVuoto);
			}
		}
		if (retValue.getDocumentiPdf()!= null){
			for (DocumentoPdf doc : retValue.getDocumentiPdf()){
				doc.setVerbaleId(verbaleVuoto.getId());
			}
		}
		if (retValue.getSottoscrittori()!= null){
			for (SottoscrittoreSedutaGiunta ssg : retValue.getSottoscrittori()){
				int nrQualifiche = 0;
				if (ssg.getProfilo() != null){
					if(ssg.getProfilo().getHasQualifica() != null ){
						nrQualifiche = ssg.getProfilo().getHasQualifica().size();
					} else {
						log.debug(String.format("SottoscrittoreSedutaGiunta = {id:%s - profiloId:%s} IS NULL!!", 
								ssg.getId(), ssg.getProfilo().getId()));
					}
				}  else {
					log.debug(String.format("SottoscrittoreSedutaGiunta = {id:%s} profilo  IS NULL!!", ssg.getId()));
				}
				
				log.debug(String.format("SottoscrittoreSedutaGiunta = {id:%s, profilo: {id:%s, hasQualifica: [%s]} }", 
						ssg.getId(), ssg.getProfilo().getId(), nrQualifiche));
				ssg.setVerbale(verbaleVuoto);
			}
		} 
		
		return retValue;
	}

	/*
	 * IN ATTICO NON PREVISTA FIRMA
	 *
	@Transactional
	public void setFirmato(DocumentoPdf docFirmato, Long idProfilo) throws javassist.NotFoundException, NotFoundException, RiversamentoPoolException {
		Verbale verbale = docFirmato.getVerbale();
		log.debug(String.format("setFirmato = {idVerbale:%s, idProfilo:%s }", verbale.getId(), idProfilo));
		
		verbale = sottoscrittoreService.setVerbaleFirmato(verbale.getId(), idProfilo);
		SedutaGiunta seduta = verbale.getSedutaGiunta();
		if(isFirmaCompleta(verbale.getId())){
  			// Aggiorno lo stato del verbale
  			verbale.setStato(SedutaGiuntaConstants.statiVerbale.verbaleConsolidato.toString());
//  			verbaleRepository.save(verbale);
  			
  			// Se il processo di firma del Verbale è completo metto la seduta in stato conclusa....
  			
  			seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaTerminata.toString());
  			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleFirmato.toString());
  			seduta.setSottoscittoreDocumento(null);
//  	  	sedutaGiuntaRepository.save(seduta);
  	  		
//  			documentoPdfRepository.save(protocolla(docFirmato));
// 			riversamentoPoolService.aggiungiRiversamentoVerbale(docFirmato);
  			protocolla(docFirmato);
  			
  			sedutaGiuntaRepository.save(seduta);
  		} else{
  			SottoscrittoreSedutaGiunta sottoscrittore = sottoscrittoreService.getNextFirmatarioVerbale(verbale.getId());
  			seduta.setSottoscittoreDocumento(sottoscrittore.getProfilo());
  			sedutaGiuntaRepository.save(seduta);
  		}
	}
	*/
	
	/**
	 * @param odg
	 * @param savedDocPdf
	 */
	/*
	 * IN ATTICO NON PREVISTA PROTOCOLLAZIONE
	 *
	private DocumentoPdf protocolla(DocumentoPdf savedDocPdf) {
		Verbale verbale = savedDocPdf.getVerbale();
		String tipoDocumento = "Seduta di Giunta - Verbale";
		
		String dataSeduta = "";
		if (verbale.getSedutaGiunta().getSecondaConvocazioneInizio()!= null){
			dataSeduta = formatter.print(verbale.getSedutaGiunta().getSecondaConvocazioneInizio().toDateTime(DateTimeZone.UTC));
		} else {
			dataSeduta = formatter.print(verbale.getSedutaGiunta().getPrimaConvocazioneInizio().toDateTime(DateTimeZone.UTC));
		}
		String oggetto = String.format("Seduta di Giunta n. %s del %s :: Documento di Verbale",	verbale.getSedutaGiunta().getNumero(), dataSeduta);
		
		List<String> destinatari = notificheService.getDestinatariNotificheSeduta(verbale.getSedutaGiunta());
		
		// TODO IMPOSTARE I RUOLI, CAPIRE COME DETERMINARE CHI E' IL MITENTE
		List<String> ruoli_ = new ArrayList<String>();
		ruoli_.add("Assessore");
		//ruoli_.add("Consigliere");
		String ruoli[] = ruoli_.toArray(new String[ruoli_.size()]);
		
//		TODO try {
//			RispostaProtocollo response = protocollazione.eseguiProtocollazione(env, savedDocPdf.getFile().getContenuto(), 
//					savedDocPdf.getFile().getNomeFile(), tipoDocumento, true, destinatari, ruoli, oggetto);
//			savedDocPdf.setNumeroProtocollo(serviceUtil.getStringSegnaturaProtocollo(response.getSegnaturaProtocollo()));
//			savedDocPdf.setDataProtollo(response.getSegnaturaProtocollo().getDataRegistrazione());
//			savedDocPdf.setFirmatodatutti(true);
//			log.debug(String.format("Verbale id=%s - firmato da tutti = %s", verbale.getId(), savedDocPdf.getFirmatodatutti()));
//		} catch (ProtocolloCatchedException e) {
//			log.error("Errore protocollazione:" + e.getMessage());
//		}
		return savedDocPdf;
	}
	*/
	
	/**
	 * @param idProfilo
	 * @param file
	 * @param utente
	 * @param verbale
	 * @throws IOException
	 * @throws CifraCatchedException
	 * @throws NotFoundException
	 * @throws NotFoundException
	 * @throws javassist.NotFoundException 
	 */
	/*
	 * IN ATTICO NON PREVISTA FIRMA
	 *
	@Transactional
	public void uploadFirmato(final Long idProfilo, final MultipartFile file, Utente utente, Verbale verbale)
			throws IOException, CifraCatchedException, NotFoundException,
			NotFoundException, javassist.NotFoundException {
		DocumentoPdf docFirmato =  documentoPdfService.saveFileFirmato(verbale, file, utente);
		setFirmato(docFirmato, idProfilo);
	}
	*/
	
	/**
	 * @param idProfilo
	 * @param dto
	 * @param idDocumento
	 * @param verbale
	 * @throws FirmaRemotaException
	 * @throws Exception
	 * @throws NotFoundException
	 * @throws NotFoundException
	 */
	/*
	 * IN ATTICO NON PREVISTA FIRMA
	 *
	@Transactional
	public void firmaDocumento(final Long idProfilo, FirmaRemotaDTO dto, Long idDocumento, Verbale verbale)
			throws FirmaRemotaException, Exception, NotFoundException,
			NotFoundException {
		// Firma digitale...
		DocumentoPdf docFirmato = firmaRemota(
				verbale,
				idDocumento, 
				dto.getCodiceFiscale(), 
				dto.getPassword(), 
				dto.getOtp());
		
		setFirmato(docFirmato, idProfilo);
	}
	*/
	
	/*TODO 
	 private DocumentoPdf firmaRemota(Verbale verbale, long docDaFirmareId, String codFiscale, String password, String otp) 
	 	throws FirmaRemotaException, Exception {
  		
  		List<byte[]> files = new ArrayList<byte[]>();
		
  		DocumentoPdf docDaFirmare = documentoPdfRepository.findOne(docDaFirmareId);
  		if (docDaFirmare == null) {
  			throw new CifraCatchedException(String.format("DocumentoPDF con id:%s non trovato!!", docDaFirmareId));
  		}
  		
  		docDaFirmare.getFile().getContenuto();
  		it.linksmt.assatti.datalayer.domain.File pdfToSign = docDaFirmare.getFile();
		if (pdfToSign == null) {
			throw new CifraCatchedException(String.format("File associato al DocumentoPDF con id:%s null!!", docDaFirmareId));
		}
		files.add(pdfToSign.getContenuto());
		
		//firmo docs presi da db
		List<FirmaRemotaResponseDTO> listFirmaRemotaResponseDTO = firmaRemotaService.firmaPades(codFiscale, password, otp, files);
		log.info(String.format("File id:%s associato al DocumentoPDF id:%s firmato da %s.", pdfToSign.getId(), docDaFirmare, codFiscale));

		byte[] content = listFirmaRemotaResponseDTO.get(0).getDocument();
		Long size = new Long(listFirmaRemotaResponseDTO.get(0).getDocument().length);

		it.linksmt.assatti.datalayer.domain.File signed = new it.linksmt.assatti.datalayer.domain.File();
		signed.setContentType(pdfToSign.getContentType());
		signed.setNomeFile(pdfToSign.getNomeFile());
		signed.setContenuto(content);
		signed.setSize(size);
				
		Utente firmatario = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		
		DocumentoPdf docFirmatoSalvato = documentoPdfService.saveFileFirmato(
				verbale, 
				signed, 
				firmatario, 
				"");
		
		if(listFirmaRemotaResponseDTO.get(0).getAttachment() != null) {
			it.linksmt.assatti.datalayer.domain.File pdfFirmatoMarcato = new it.linksmt.assatti.datalayer.domain.File();
			pdfFirmatoMarcato.setContentType("application/timestamp-query");
			pdfFirmatoMarcato.setNomeFile(pdfToSign.getNomeFile()+".tsd");
			pdfFirmatoMarcato.setContenuto(listFirmaRemotaResponseDTO.get(0).getAttachment());
			pdfFirmatoMarcato.setSize(new Long(listFirmaRemotaResponseDTO.get(0).getAttachment().length));
			
			DocumentoInformatico marcatura = new DocumentoInformatico();
			marcatura.setTitolo("Marcatura temporale");
			marcatura.setFile(pdfFirmatoMarcato);
			marcatura.setNomeFile(pdfFirmatoMarcato.getNomeFile());
			marcatura.setParteIntegrante(false);
			marcatura.setOmissis(false);
			marcatura.setVerbale(docDaFirmare.getVerbale());
			marcatura.setDocumentoRiferimento(docFirmatoSalvato);
			documentoInformaticoService.save(marcatura);
		}
  		
  		return docFirmatoSalvato;
  	}
  	
	private DocumentoPdf firmaRemota(Verbale verbale, long docDaFirmareId, String codFiscale, String password, String otp) 
  			throws FirmaRemotaException, Exception {
  		List<byte[]> files = new ArrayList<byte[]>();
		
  		DocumentoPdf docDaFirmare = documentoPdfRepository.findOne(docDaFirmareId);
  		if (docDaFirmare == null) {
  			throw new CifraCatchedException(String.format("DocumentoPDF con id:%s non trovato!!", docDaFirmareId));
  		}
  		return docDaFirmare;
  	}
  	*/
}
