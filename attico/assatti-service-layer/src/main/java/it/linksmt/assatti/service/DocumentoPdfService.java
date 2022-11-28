package it.linksmt.assatti.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.google.common.collect.Lists;
import com.itextpdf.text.DocumentException;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.wrapper.ProfiloQualificaBean;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.dto.RelataAlboDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DettaglioLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.DocumentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpAccertamentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpegnoDaStampareDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.SoggettoDto;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.CategoriaEvento;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DocumentoTypeEnum;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QDocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Resoconto;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.domain.dto.AttivitaDTO;
import it.linksmt.assatti.datalayer.domain.dto.AvanzamentoDTO;
import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.datalayer.repository.CategoriaEventoRepository;
import it.linksmt.assatti.datalayer.repository.DocumentoPdfRepository;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreAttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoRepository;
import it.linksmt.assatti.service.converter.DmsMetadataConverter;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;

@Service
@Transactional
public class DocumentoPdfService {
	private final Logger log = LoggerFactory.getLogger(DocumentoPdfService.class);

//	private final Integer TIPO_RESOCONTO_INTEGRALE = 1;
//	private final Integer TIPO_RESOCONTO_PARZIALE = 0;
//	private final Integer TIPO_RESOCONTO_PRESS_ASS = 2;
	
	@Inject
	private FileRepository fileRepository;

	@Inject
	private ModelloHtmlService modelloService;
	
	@Inject
	private DocumentoPdfRepository documentoPdfRepository;
	
	@Inject
    private TipoDocumentoRepository tipoDocumentoRepository;
	
	@Inject
	private TipoDocumentoService tipoDocumentoService;
	
	// IN ATTICO NON PREVISTO
	// @Inject
	// private RiversamentoPoolService riversamentoPoolService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private CategoriaEventoRepository categoriaEventoRepository;
	
	@Inject
	private AvanzamentoService avanzamentoService;
	
	@Inject
	private EventoService eventoService;
	
	@Inject
	private ReportService reportService;
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private SottoscrittoreAttoRepository sottoscrittoreAttoRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
    private DatiContabiliService datiContabiliService;
	
	@Autowired
	private ServiceUtil serviceUtil;
	
	@Inject
	private UtenteService utenteService;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private DmsMetadataConverter dmsMetadataConverter;
	
	@Transactional
	public DocumentoPdf aggiungiRelataPubblicazioneAlbo(Atto atto, RelataAlboDto relata) throws ServiceException{
		try {
			DocumentoPdf relataDb = null;
			if(atto!=null && relata!=null && relata.getContent()!=null && relata.getContent().length > 0) {
				relataDb = new DocumentoPdf();
				relataDb.setAttoRelataPubblicazioneId(atto.getId());
				relataDb.setCreatedBy("system");
				relataDb.setCreatedDate(DateTime.now());
				File file = new File();
				file.setContentType(relata.getContentType() != null && !relata.getContentType().isEmpty() ? relata.getContentType() : "application/pdf");
				file.setNomeFile(relata.getFileName() != null && !relata.getFileName().isEmpty() ? relata.getFileName() : "Relata_pubblicazione_" + atto.getDataAdozione().getYear() + "_" + atto.getTipoAtto().getCodice() + "_" + atto.getNumeroAdozione() + ".pdf");
				file.setSha256Checksum(FileChecksum.calcolaImpronta(relata.getContent()));
				file.setSize(Long.parseLong(relata.getContent().length + ""));
				file.setVersion(0);
				
				
				TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.relata.name());

				Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, false);
				
				String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
				DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String prefixTimeStamp = dfmt.format(new Date()) + "_";
				String cmisObjectId = dmsService.save(attoFolderPath, relata.getContent(), prefixTimeStamp + file.getNomeFile(), file.getContentType(), createProps);
				if(cmisObjectId==null || cmisObjectId.isEmpty()) {
					throw new ServiceException("cmisObjectId is null");
				}
				file.setCmisObjectId(cmisObjectId);
				file.setCreatedDate(DateTime.now());
				file = fileRepository.save(file);
				relataDb.setFile(file);
				relataDb.setImpronta(file.getSha256Checksum());
				relataDb.setTipoDocumento(tipoDocumento);
				relataDb = documentoPdfRepository.save(relataDb);
				
				eventoService.saveEvento(EventoEnum.EVENTO_RELATA_RECUPERATA.getDescrizione(), atto);
			}
			return relataDb;
		}catch(Exception e) {
			log.error("DocumentoPdfService :: aggiungiRelataPubblicazioneAlbo :: AttoId " + atto.getId() + " Relata " + relata + " Error message " + e.getMessage());
			throw new ServiceException(e);
		}
	};
	
	@Transactional
	public void setNumeroDataProtocollo(Long idDocumento, String numeroProtocollo, String dataProtocollo){
		documentoPdfRepository.setNumeroDataProtocollo(idDocumento, numeroProtocollo, dataProtocollo);
	}
	
	@Transactional
	public Object generaReportIter(Long attoId, boolean persisti) throws FileNotFoundException, IOException, DocumentException, GestattiCatchedException {
		List<ModelloHtml> modelli = modelloService.findByTipoDocumento(TipoDocumentoEnum.log_iter_atto.name());
		
		Long idModelloHtml = null;
		if(modelli.size() > 0){
			idModelloHtml = modelli.get(0).getId();
		}
		
		if(idModelloHtml==null) {
			throw new GestattiCatchedException("Nessun modello di Log Iter Atto presente");
		}
		
		Atto atto = attoService.findOne(attoId);
		List<CategoriaEvento> categorieEvento = categoriaEventoRepository.findAllByOrderByOrdineAsc();
		
		// START 
		List<CategoriaEvento> categorieEventoTemp = new ArrayList<CategoriaEvento>();
		for (CategoriaEvento categoriaEvento : categorieEvento) {
			categorieEventoTemp.add(categoriaEvento);
			
		}
		categorieEvento = null;
		categorieEvento = new ArrayList<CategoriaEvento>(categorieEventoTemp);
		// END
		
		List<Evento> eventi = eventoService.findByAttoId(attoId);
		
		//k = idCategoria, v = list<evento>
		Map<Long, List<Evento>> eventiMap = new HashMap<Long, List<Evento>>();
		
		if(categorieEvento!=null && categorieEvento.size() > 0){
			for(CategoriaEvento cat : categorieEvento){
				eventiMap.put(cat.getId(), new ArrayList<Evento>());
			}
			if(eventi!=null){
				for(Evento evento : eventi){
					if(evento!=null && evento.getTipoEvento()!=null){
						eventiMap.get(evento.getTipoEvento().getCategoriaEvento().getId()).add(evento);
					}
				}
			}
		}
		
		List<Avanzamento> allAvanzamenti = avanzamentoService.findByAtto_id(attoId);
		List<AvanzamentoDTO> avanzamenti = new ArrayList<AvanzamentoDTO>();
		String tempStato = null;
		String tempEsecutore = null;
		String tempAttivita = null;
		for(Avanzamento av : allAvanzamenti){
			String att = avanzamentoRepository.findAttivitaByAvId(av.getId());
			av.setAttivita(att);
			if(av.getStato().equalsIgnoreCase(tempStato) && av.getCreatedBy().equalsIgnoreCase(tempEsecutore)){
				avanzamenti.get(avanzamenti.size() - 1).setDataAttivita(av.getDataAttivita());
				if(av.getAttivita()!=null && av.getAttivita().equalsIgnoreCase(tempAttivita)){
					avanzamenti.get(avanzamenti.size() - 1).getListaAttivita().remove(avanzamenti.get(avanzamenti.size() - 1).getListaAttivita().size() - 1);
				}
				AttivitaDTO attivita = new AttivitaDTO();
				attivita.setData(av.getDataAttivita());
				attivita.setNome(av.getAttivita());
				if(avanzamenti.get(avanzamenti.size() - 1).getListaAttivita()!=null){
					avanzamenti.get(avanzamenti.size() - 1).getListaAttivita().add(attivita);
				}
				tempAttivita = attivita.getNome();
			}else{
				AvanzamentoDTO avDto = new AvanzamentoDTO();
				avDto.setAttivita(av.getAttivita());
				avDto.setDataAttivita(av.getDataAttivita());
				avDto.setId(av.getId());
				avDto.setNote(av.getNote());
				avDto.setSistemaAccreditato(av.getSistemaAccreditato());
				avDto.setStato(av.getStato());
				if(av.getCreatedBy()!=null && !av.getCreatedBy().equalsIgnoreCase("system")) {
					UtenteDto utente = utenteService.getUtenteBasicByUsername(av.getCreatedBy());
					avDto.setCreatedBy(utente.getName());
				}
				tempAttivita = av.getAttivita();
				tempStato = av.getStato();
				tempEsecutore = av.getCreatedBy();
				avDto.setListaAttivita(new ArrayList<AttivitaDTO>());
				AttivitaDTO attivita = new AttivitaDTO();
				attivita.setNome(av.getAttivita());
				attivita.setData(av.getDataAttivita());
				avDto.getListaAttivita().add(attivita);
				avanzamenti.add(avDto);
			}
		}
		String numero = "";
		
		if(atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty()){
			numero = "_"+atto.getNumeroAdozione();
		}
		
		String nome = atto.getCodiceCifra().replaceAll("\\/", "_") + (!numero.isEmpty() ? numero : "") + "_LOG.pdf";
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.log_iter_atto.name());
		
		java.io.File f = reportService.executeReportIter(idModelloHtml, eventiMap, categorieEvento, avanzamenti, atto);
		
		/*
		 * Salvo documento su repository documentale
		 */
		
		if(persisti) {
			String cmisObjectId = null;
			try {
				Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, false);
				String attoFolderPath = serviceUtil.buildDocumentPath(
						tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
				DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String prefixTimeStamp = dfmt.format(new Date()) + "_";
				cmisObjectId = dmsService.save(attoFolderPath, FileUtils.readFileToByteArray(f), prefixTimeStamp + nome, "application/pdf", createProps);
			} 
			catch (Exception e) {
				throw new RuntimeException(e);
			}
			DocumentoPdf doc = null;
			if(cmisObjectId!=null && !cmisObjectId.isEmpty()) {
				doc = new DocumentoPdf();
				File file = new File(nome, "application/pdf", f.length());
				file.setCmisObjectId(cmisObjectId);
				file.setSha256Checksum(FileChecksum.calcolaImpronta(FileUtils.readFileToByteArray(f)));
				file = fileRepository.save(file);
				doc.setFile(file);
				doc.setFirmato(false);
				doc.setFirmatodatutti(false);
				doc.setTipoDocumento(tipoDocumento);
				doc.setAttoReportIterId(atto.getId());
				documentoPdfRepository.save(doc);
				
				eventoService.saveEvento("LOG_ITER_GENERATO", atto);
			}else {
				throw new RuntimeException("Id documento cmis mancante");
			}
			return doc;
		}else {
			return f;
		}
	}
	
	@Transactional(readOnly=true)
	public DocumentoPdf getPropostaFirmataDaTuttiByAttoId(Long attoId){
		return documentoPdfRepository.findOne(QDocumentoPdf.documentoPdf.attoId.eq(attoId).and(QDocumentoPdf.documentoPdf.firmatodatutti.eq(true)));
	}
	
	@Transactional(readOnly=true)
	public DocumentoPdf getProvvedimentoFirmataDaTuttiByAttoId(Long attoId){
		return documentoPdfRepository.findOne(QDocumentoPdf.documentoPdf.attoAdozioneId.eq(attoId).and(QDocumentoPdf.documentoPdf.firmatodatutti.eq(true)));
	}
	
	@Transactional(readOnly=true)
	public DocumentoPdf getProvvedimentoOmissisFirmataDaTuttiByAttoId(Long attoId){
		return documentoPdfRepository.findOne(QDocumentoPdf.documentoPdf.attoAdozioneOmissisId.eq(attoId).and(QDocumentoPdf.documentoPdf.firmatodatutti.eq(true)));
	}
	
	@Transactional
	public String getCodiceCifraBySchedaAnagraficaContabile(Long documentoPdfId){
		return documentoPdfRepository.getCodiceCifraBySchedaAnagraficaContabile(documentoPdfId);
	}
	
	/**
	 * Se il documento è una scheda anagrafico contabile restituisce l'id dell'atto di riferimento, altrimenti restituisce null 
	 * @param idDocumentoPdf
	 * @return
	 */
	@Transactional(readOnly=true)
	public Long checkDocumentoSchedaAnagraficaContabile(Long idDocumentoPdf){
		Long attoId = null;
		if(idDocumentoPdf!=null){
			attoId = documentoPdfRepository.checkIfDocumentoIsSchedaAnagraficoContabile(idDocumentoPdf);
		}
		return attoId;
	}
	
	/**
	 * Se il documento è un parere restituisce l'id del parere di riferimento, altrimenti restituisce null 
	 * @param idDocumentoPdf
	 * @return
	 */
	@Transactional(readOnly=true)
	public Long checkIfDocumentoIsParere(Long idDocumentoPdf){
		Long attoId = null;
		if(idDocumentoPdf!=null){
			attoId = documentoPdfRepository.checkIfDocumentoIsParere(idDocumentoPdf);
		}
		return attoId;
	}
	
	@Transactional
	public void insertSchedaAnagraficoContabileDaRiversare(Long idDocumentoPdf, boolean pubblicazioneRup){
		List<BigInteger> documentiPrincipaliIds = this.findDocumentiPrincipaliOfSchedaAnagraficaContabile(idDocumentoPdf);
		for(BigInteger docPadre : documentiPrincipaliIds){
			documentoPdfRepository.insertSchedaAnagraficoContabileDaRiversare(idDocumentoPdf, docPadre, pubblicazioneRup);
		}
	}
	
	@Transactional
	public void deleteSchedaAnagraficoContabileDaRiversare(Long idDocumentoPdf){
		documentoPdfRepository.deleteSchedaAnagraficoContabileDaRiversare(idDocumentoPdf);
	}
	
	@Transactional(readOnly=true)
	public List<Object[]> getAllSchedeAnagraficheContabiliDaRiversareRup(){
		return documentoPdfRepository.getAllSchedeAnagraficheContabiliDaRiversareRup();
	}
	
	@Transactional
	public void setSchedaAnagraficaContabileRiversataRup(Long sacId, Long docPadreId){
		documentoPdfRepository.setSchedaAnagraficaContabileRiversataRup(sacId, docPadreId);
	}
	
	@Transactional(readOnly=true)
	public List<BigInteger> findDocumentiPrincipaliOfSchedaAnagraficaContabile(Long idDocSchedaAnagraficoContabile){
		return documentoPdfRepository.findDocumentiPrincipaliOfSchedaAnagraficaContabile(idDocSchedaAnagraficoContabile);
	}
	
	@Transactional(readOnly = true)
	private long contaFirmeProposta(Long attoId, boolean omissis){
		BooleanExpression exp = null;
		if(!omissis){
			exp = QDocumentoPdf.documentoPdf.attoId.eq(attoId).and(QDocumentoPdf.documentoPdf.firmato.eq(true));
		}else{
			exp = QDocumentoPdf.documentoPdf.attoOmissisId.eq(attoId).and(QDocumentoPdf.documentoPdf.firmato.eq(true));
		}
		return documentoPdfRepository.count(exp);
	}

	@Transactional
	public DocumentoPdf saveFileFirmato(Atto atto, MultipartFile multipartFile, long profiloId, String codiceTipoDocumento) 
			throws IOException, GestattiCatchedException {
		log.debug("save atto" + atto);
		
		Profilo utenteLoggato = profiloRepository.findOne(profiloId);
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(codiceTipoDocumento);
		
		String nome = this.getNomeFileFirmato(atto, false, false, multipartFile, false, false, null, tipoDocumento);
		
		/*
		 * Salvo documento su repository documentale
		 */
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(
					tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
			
			Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, false);
			
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), 
					nome, multipartFile.getContentType(), createProps);
		} 
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}
		
		File file = new File(nome, multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		file = fileRepository.save(file);
		
		return executeSaveFileFirmatoAtto(atto, file, utenteLoggato, tipoDocumento,null);
	}
	
	/**
	 * Genera il nome del file
	 * 
	 * @param atto
	 * @param omissis
	 * @param adozione
	 * @param multipartFile
	 * @param schedaAnagraficoContabile
	 * @return
	 */
	protected String getNomeFileFirmato(Atto atto, Boolean omissis, Boolean adozione,MultipartFile multipartFile, Boolean schedaAnagraficoContabile, Boolean attoInesistente, Parere parere, TipoDocumento tipoDocumento){
		String originalName = multipartFile.getOriginalFilename();
		return getNomeFileFirmato(atto, omissis, adozione, originalName, tipoDocumento);
	}
	
	/**
	 * Genera il nome del file
	 * 
	 * @param atto
	 * @param omissis
	 * @param adozione
	 * @param originalName
	 * @param tipoDocumento
	 * @return
	 */
	private String getNomeFileFirmato(Atto atto, Boolean omissis, Boolean adozione,String originalName, TipoDocumento tipoDocumento){
		
		String strOmissis = "";
		if(omissis) {
			strOmissis = "omissis";
		}
		
		String filenamePrefix = tipoDocumento.getDescrizione().replaceAll(" ", "_");
		
		String numero = "";
		
		if(atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty()){
			numero = "_"+atto.getNumeroAdozione();
		}
		
		String nome = "";
		if(tipoDocumento.getSpecificaFilename()==null || tipoDocumento.getSpecificaFilename().trim().isEmpty()) {
			nome = filenamePrefix+"-"+strOmissis+"-" + atto.getCodiceCifra().replace("/", "_") + numero + ".pdf"; // XXX delegherei la composizione del nome file all'utilizzatore di questo metodo
		}else {
			nome = tipoDocumento.getSpecificaFilename().replaceAll("\\$datiatto", strOmissis + "-" + atto.getCodiceCifra().replace("/", "_") + numero) + ".pdf";
		}
		nome = nome.replace("--", "-");
				
		return nome;
	}
	
	@Transactional(readOnly = true)
	private long contaFirmeParere(Long parereId){
		BooleanExpression exp = QDocumentoPdf.documentoPdf.parereId.eq(parereId).and(QDocumentoPdf.documentoPdf.firmato.eq(true)).and(QDocumentoPdf.documentoPdf.pareremodificato.isNull().or(QDocumentoPdf.documentoPdf.pareremodificato.eq(false)));
		return documentoPdfRepository.count(exp);
	}
	
	/*
	 * TODO: In ATTICO non previsti
	 * 
	@Transactional
	public DocumentoPdf saveParereFirmato(Atto atto, Parere parere,  Boolean omissis, Boolean adozione, MultipartFile multipartFile, Utente utenteLoggato ) throws IOException, CifraCatchedException {
		log.debug("save atto" + atto);
		String nome = this.getNomeFileFirmato(atto, omissis, adozione, multipartFile, false, false, parere);
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.allegato.name());
		
		// Salvo documento su repository documentale
		
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(
					tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), nome, multipartFile.getContentType(), null);
		}
		catch (Exception e) {
			throw new CifraCatchedException(e, e.getMessage());
		}
		
		File file = new File(nome,
				multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		file = fileRepository.save(file);
		DocumentoPdf docPdf = new DocumentoPdf();
		
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		if(parere!=null && parere.getSottoscrittori()!=null && parere.getSottoscrittori().size() > 0){
			if((new Long(this.contaFirmeParere(parere.getId())).intValue()) + 1 == parere.getSottoscrittori().size()){
				docPdf.setFirmatodatutti(true);
			}else{
				docPdf.setFirmatodatutti(false);
			}
		}else{
			if(adozione){
				docPdf.setFirmatodatutti(true);
			}else{
				throw new CifraCatchedException("Sottoscrittori Parere non Disponibili");
			}
		}
		docPdf.setFirmatario(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
		docPdf.setParere(parere);
		docPdf.setAooSerie(atto.getAoo());
		if(parere.getTipoParere().equalsIgnoreCase("controdeduzioni")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.controdeduzione.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else if(parere.getTipoParere().equalsIgnoreCase("ritiro")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.ritiro_in_giunta.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else if(parere.getParereSintetico()!=null && parere.getParereSintetico().equals("ritirato_proponente")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.restituzione_su_istanza_ufficio_proponente.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else{
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.parere.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}
		return docPdf;
	}
	
	@Transactional
	public void setParereModificato(Long parereId){
		documentoPdfRepository.setParereModificato(parereId);
	}
	
	@Transactional
	public DocumentoPdf saveParereFirmato(Atto atto, Parere parere,  Boolean omissis, Boolean adozione, File file, Utente utenteLoggato ) throws IOException, CifraCatchedException {
		
		String nome = this.getNomeFileFirmato(atto, omissis, adozione, file.getNomeFile());
		file.setNomeFile(nome);
		
		file = fileRepository.save(file);
		DocumentoPdf docPdf = new DocumentoPdf();
		
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		if(parere!=null && parere.getSottoscrittori()!=null && parere.getSottoscrittori().size() > 0){
			if((new Long(this.contaFirmeParere(parere.getId())).intValue()) + 1 == parere.getSottoscrittori().size()){
				docPdf.setFirmatodatutti(true);
			}else{
				docPdf.setFirmatodatutti(false);
			}
		}else{
			if(adozione){
				docPdf.setFirmatodatutti(true);
			}else{
				throw new CifraCatchedException("Sottoscrittori Parere non Disponibili");
			}
		}
		docPdf.setFirmatario(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
		docPdf.setParere(parere);
		docPdf.setAooSerie(atto.getAoo());
		if(parere.getTipoParere().equalsIgnoreCase("controdeduzioni")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.controdeduzione.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else if(parere.getTipoParere().equalsIgnoreCase("ritiro")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.ritiro_in_giunta.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else if(parere.getParereSintetico()!=null && parere.getParereSintetico().equals("ritirato_proponente")){
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.restituzione_su_istanza_ufficio_proponente.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}else{
			docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.parere.name()));
			docPdf = documentoPdfRepository.save(docPdf);
			if(docPdf.getFirmatodatutti()!=null && docPdf.getFirmatodatutti().equals(true)){
				riversamentoPoolService.aggiungiRiversamentoParere(docPdf, parere.getAoo().getId(), atto.getTipoAtto().getId(), atto.getAoo().getId());
			}
		}
		return docPdf;
	}
	
	
	@Transactional
	public DocumentoPdf saveFileFirmatoOfOdg(
			OrdineGiorno odg,
			Utente utenteLoggato,
			TipoDocumento tipo,
			byte[] fileContent, long size, String contentType) 
					throws IOException, CifraCatchedException {
		log.debug("save documento firmato da multipart file per ordine giorno" + odg);
		
		String nome = generaNomeDocumentoOdg(odg,true);
		
		// Salvo documento su repository documentale
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(tipo, null, null, null);
			cmisObjectId = dmsService.save(attoFolderPath, fileContent, nome, contentType, null);
		} catch (Exception e) {
			throw new CifraCatchedException(e, e.getMessage());
		}
		
		File file = new File(
				nome, contentType, size);
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(fileContent));
		file = fileRepository.save(file);

		DocumentoPdf docPdf = new DocumentoPdf();
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		docPdf.setFirmatodatutti(true);
		docPdf.setFirmatario(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
//		TODO: ???
//		docPdf.setCreatedBy(createdBy);
		docPdf.setOrdineGiorno(odg);
		docPdf.setTipoDocumento(tipo);
		
		return documentoPdfRepository.save(docPdf);
	}
	*/
	
	@Transactional
	public DocumentoPdf saveFileFirmato(Atto atto, File file, 
			long profiloId, TipoDocumento tipoDocumento, 
			boolean calcolaNomeFile) throws IOException, GestattiCatchedException {
		
		String nome = file.getNomeFile();
		if(calcolaNomeFile || nome.isEmpty()){
			nome = this.getNomeFileFirmato(atto, !atto.getPubblicazioneIntegrale(), atto.getDataAdozione() != null, file.getNomeFile(),tipoDocumento);
		}
		file.setNomeFile(nome);
		file = fileRepository.save(file);
		
		Profilo utenteLoggato = profiloRepository.findOne(profiloId);
		return executeSaveFileFirmatoAtto(atto, file, utenteLoggato, tipoDocumento, nome.contains("omissis"));
	}
	
	private DocumentoPdf executeSaveFileFirmatoAtto(Atto atto, File file, 
			Profilo utenteLoggato, TipoDocumento tipoDocumento, Boolean nomeContieneOmissis ) 
					throws GestattiCatchedException{
		
		DocumentoPdf docPdf = new DocumentoPdf();
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		docPdf.setFirmatario(utenteLoggato.getUtente().getNome() + " " + utenteLoggato.getUtente().getCognome());
		long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
		if(idProfDelega < 0) {
			idProfDelega = BpmThreadLocalUtil.getProfiloOriginarioId();
		}
		if (idProfDelega > 0) {
			Profilo profiloDelegante = profiloRepository.findOne(idProfDelega);
			
			if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
				Utente utDelegante = profiloDelegante.getUtente();
				docPdf.setFirmatarioDelegante(utDelegante.getNome() + " " + utDelegante.getCognome());
			}
		}
		//docPdf.setFirmatodatutti(verificaFirmatoDaTutti(atto, adozione, schedaAnagraficoContabile, omissis, attoInesistente));
		docPdf.setFirmatodatutti(true);
		docPdf.setAooSerieId(atto.getAoo().getId());
		
		Boolean adozione = null;
		if(tipoDocumento.getType()==null || (!tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString()) && !tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROVVEDIMENTO.toString()))) {
			adozione = atto.getDataAdozione() != null;
		}else if(tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString())){
			adozione = false;
		}else if(tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROVVEDIMENTO.toString())) {
			adozione = true;
		}else {
			throw new GestattiCatchedException("Errore tipo documento");
		}
		
		
		docPdf.setTipoDocumento(tipoDocumento);
		
		boolean pubblicazioneIntegrale = true;
		if(atto.getPubblicazioneIntegrale() != null){
			pubblicazioneIntegrale = atto.getPubblicazioneIntegrale().booleanValue();
		}
		
		boolean omissis = nomeContieneOmissis!=null? nomeContieneOmissis.booleanValue() : !pubblicazioneIntegrale;
		
		if(adozione){
			if(!omissis){
				docPdf.setAttoAdozioneId( atto.getId());
			}
			else{
				docPdf.setAttoAdozioneOmissisId( atto.getId());
			}
			docPdf = documentoPdfRepository.save(docPdf);
		}
		else {
			if(!omissis){
				docPdf.setAttoId(atto.getId());
			}
			else{
				docPdf.setAttoOmissisId(atto.getId());
			}
			docPdf = documentoPdfRepository.save(docPdf);
		}
		
		SottoscrittoreAtto sa = new SottoscrittoreAtto();
		sa.setAtto(atto);
		sa.setProfilo(utenteLoggato);
		sa.setDocumentoPdf(docPdf);
		sa.setDataFirma(new DateTime());
		
		// Lettura qualifica professionale
		String taskBpmId = workflowService.getMyNextTaskId(atto.getId().longValue(), utenteLoggato.getId().longValue());
		ProfiloQualificaBean profQualifica = workflowService.getProfiloQualificaEsecutore(taskBpmId);
		if (profQualifica != null) {
			sa.setDescrizioneQualifica(profQualifica.getDescrizioneQualifica());
		}
		
		sa.setEnabled(true);
		sottoscrittoreAttoRepository.save(sa);
		
		return docPdf;
	}
	
	/*
	 * TODO: In ATTICO modificata gestione sottoscrittori
	 * 
	public boolean verificaFirmatoDaTutti(
			Atto atto, 
			Boolean omissis) throws CifraCatchedException{
		boolean retValue = false; 
		
		log.debug(String.format("verificaFirmatoDaTutti :: atto_id:%s; stato:%s; omissis=%s", atto.getId(), atto.getStato(),  omissis));
		
		if(atto.getSottoscrittori()!=null && atto.getSottoscrittori().size() > 0){
				if((new Long(this.contaFirmeProposta(atto.getId(), omissis != null ? omissis : false)).intValue()) == atto.getSottoscrittori().size()){
					retValue = true;
				}
			}else{
				throw new CifraCatchedException("Sottoscrittori Proposta non Disponibili");
			}
		
		log.debug(String.format("verificaFirmatoDaTutti :: atto_id:%s; firmatodatutti:%s", atto.getId(), retValue));
		
		return retValue;
	}
	*/
	
	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public DocumentoPdf saveParerePdf(Parere parere, java.io.File content) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveParere(parere, content, Boolean.FALSE);
		}
		throw new DocumentoPdfException("Pdf non presente.");

	}
	
	private DocumentoPdf executeSaveParere(Parere parere, java.io.File content, Boolean omissis)
			throws FileNotFoundException, IOException, CifraCatchedException {
		String nome = this.getFileNameParere(parere, false);

		TipoDocumento tipoDocumento = null;
		
		if(parere.getTipoParere().equalsIgnoreCase("controdeduzioni")) {
			tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.controdeduzione.name());
		} else if(parere.getTipoParere().equalsIgnoreCase("ritiro")) {
			tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.ritiro_in_giunta.name());
		} else if(parere.getParereSintetico()!=null && parere.getParereSintetico().equals("ritirato_proponente")) {
			tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.restituzione_su_istanza_ufficio_proponente.name());
		} else {
			tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.parere.name());
		}
		
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf.setParere(parere);
		
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}
	
	private String getFileNameParere(Parere parere, boolean isFirmato){
		String suffisso = parere.getAtto().getCodiceCifra().split("/")[2] + "_" + parere.getAtto().getCodiceCifra().split("/")[3];
		String firmato = "";
		if(isFirmato){
			firmato = "-firmato";
		}
		String nome = null;
		if(parere.getTipoParere()!=null && !"".equals(parere.getTipoParere())){
			nome = "Parere_" + parere.getTipoParere().replace(" Organo di ", "").replace(" ", "") + firmato + "-"+parere.getAtto().getAoo().getCodice()+"-" + parere.getAtto().getTipoAtto().getCodice()+"-"+ suffisso + ".pdf";
		}else{
			nome = "Parere-" + firmato + parere.getAtto().getAoo().getCodice() + "-" + parere.getAtto().getTipoAtto().getCodice() + "-" + suffisso + ".pdf";
		}
		return nome;
	}
	*/
	
	/**
	 * TODO: rivedere la logica di generazione del nome del Verbale...
	 * @param verbale
	 * @param isFirmato
	 * @return
	 */
	private String getFileNameVerbale(Verbale verbale, boolean isFirmato){
		String suffisso = "";

		if (verbale.getSedutaGiunta() != null){
			String tipoSeduta = "";
			if (verbale.getSedutaGiunta().getTipoSeduta()!= null){
				if (verbale.getSedutaGiunta().getTipoSeduta() == 1 )
					tipoSeduta = "_Ordinaria";
				else
					tipoSeduta = "_Straordinaria";
			}

			String strDataOra = "";
			DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy_HH-mm");
			if(verbale.getSedutaGiunta().getSecondaConvocazioneInizio() != null){
				strDataOra = sdf.format(verbale.getSedutaGiunta().getSecondaConvocazioneInizio().toDate());
			}
			else{
				strDataOra = sdf.format(verbale.getSedutaGiunta().getPrimaConvocazioneInizio().toDate());
			}
			suffisso = String.format("seduta%s_del_%s", tipoSeduta, strDataOra);
		}
		
		
		String firmato = "";
		if(isFirmato){
			firmato = "-firmato";
		}
		String nome = "Verbale_" + suffisso + firmato + ".pdf";
		return nome;
	}
	
	private String getAllegatiParteIntegranteString(Collection<DocumentoInformatico> allegati) {
		String allegatiParteIntegrante = "";
		if(allegati!=null) {
			for (DocumentoInformatico d : allegati) {
				if(d.getTipoAllegato().getCodice().equalsIgnoreCase("PARTE_INTEGRANTE")) {
					String titolo = d.getTitolo();
	
					if(d.getFile() != null) {
						titolo += " - " + d.getFile().getSha256Checksum();
					}
	
					allegatiParteIntegrante += titolo;
				}
			}
		}
		return allegatiParteIntegrante;
	}
	
	/**
	 * Salva una riga nella tabella file ed una in documentoPdf
	 * 
	 * NB: La classe DocumentoPdfService non è ben parametrizzata (troppi valori cablati nei metodi),
	 * include logiche che non le sono di competenza ed è pessimamente progettata e implementata.
	 * Andrebbe realizzato uno strato di service apposito (basta una sola classe) per il salvataggio
	 * dei documenti (atti o allegati) e il metodo seguente dovrebbe far parte di tale classe.
	 * 
	 * NB: il codice di questo metodo è parzialmente ripreso da altri metodi di questa classe,
	 * cercando per quanto possibile e pertinente, di mantenere le logiche pre-esistenti.
	 * 
	 * @author marco ingrosso
	 * @date 2018-07-06
	 * 
	 * @param atto
	 * @param content
	 * @return
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws GestattiCatchedException 
	 */
	@Transactional
	public DocumentoPdf salvaDocumentoPdf(Atto atto, java.io.File pdfAtto, String codiceTipoDocumento,
			boolean omissis, boolean firmato, boolean firmatoDatutti, boolean completo) throws FileNotFoundException, IOException, GestattiCatchedException {
		
		if(atto==null) {
			throw new DocumentoPdfException("DocumentoPdfService :: salvaDocumentoPdf() :: Atto non specificato.");
		}
		if(pdfAtto==null) {
			throw new DocumentoPdfException("DocumentoPdfService :: salvaDocumentoPdf() :: File pdfAtto non specificato.");
		}
		if(codiceTipoDocumento==null) {
			throw new DocumentoPdfException("DocumentoPdfService :: salvaDocumentoPdf() :: codiceTipoDocumento non specificato.");
		}

		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(codiceTipoDocumento);
		if(tipoDocumento==null) {
			throw new DocumentoPdfException("DocumentoPdfService :: salvaDocumentoPdf() :: codiceTipoDocumento non valido o non esistente.");
		}

		String strOmissis = "";
		if(omissis) {
			strOmissis = "omissis";
		}
		
		String filenamePrefix = tipoDocumento.getDescrizione().replaceAll(" ", "_");
		
		
		String numero = "";
		
		if(atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty()){
			numero = "_"+atto.getNumeroAdozione();
		}
		
		String nome = "";
		if(tipoDocumento.getSpecificaFilename()==null || tipoDocumento.getSpecificaFilename().trim().isEmpty()) {
			nome = filenamePrefix+"-"+strOmissis+"-" + atto.getCodiceCifra().replace("/", "_") + numero + ".pdf"; // XXX delegherei la composizione del nome file all'utilizzatore di questo metodo
		}else {
			nome = tipoDocumento.getSpecificaFilename().replaceAll("\\$datiatto", strOmissis + "-" + atto.getCodiceCifra().replace("/", "_") + numero) + ".pdf";
		}
		nome = nome.replace("--", "-");
		
		FileInputStream in = new FileInputStream(pdfAtto);
		byte[] contenuto = IOUtils.toByteArray(in);
		IOUtils.closeQuietly(in);
		
		DocumentoPdf docPdf = new DocumentoPdf();
		boolean documentoDaSalvare = true;
		
		if(tipoDocumento.getType()!= null && tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString()) && Lists.newArrayList("DD", "DL").contains(atto.getTipoAtto().getCodice())){
			String allegatiParteIntegrante = this.getAllegatiParteIntegranteString(atto.getAllegati());
			
			String datiContabili = "";
			if(atto.getDatiContabili()!=null) {
				boolean isDD = Lists.newArrayList("DD").contains(atto.getTipoAtto().getCodice());
				boolean isDL = Lists.newArrayList("DL").contains(atto.getTipoAtto().getCodice());
				
				Set<MovimentoContabileDto> sortedSetMovimentiContabili = null;
				List<MovimentoContabileDto> l = datiContabiliService.elencoMovimento(atto.getId());
				if(l!=null) {
					sortedSetMovimentiContabili = new TreeSet<MovimentoContabileDto>(l);
				}
				
				List<DettaglioLiquidazioneDto> listDettLiquidazione = new ArrayList<DettaglioLiquidazioneDto>();
				List<MovimentoLiquidazioneDto> listLiquidazione = new ArrayList<MovimentoLiquidazioneDto>();
				List<ImpAccertamentoDto> listMovImpAcce = new ArrayList<ImpAccertamentoDto>();
				
				if(sortedSetMovimentiContabili!=null) {
					for (MovimentoContabileDto movimentoContabileDto : sortedSetMovimentiContabili) {
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
					datiContabili += atto.getDatiContabili().getIncludiMovimentiAtto() != null ? atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue() : "";
					datiContabili += atto.getDatiContabili().getNascondiBeneficiariMovimentiAtto() != null ? atto.getDatiContabili().getNascondiBeneficiariMovimentiAtto().booleanValue() : "";
					
					for (ImpAccertamentoDto impAccertamentoDto : listMovImpAcce) {
						datiContabili += impAccertamentoDto.getEu()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEu();
						datiContabili += impAccertamentoDto.getEsercizio()!=null?impAccertamentoDto.getEu():impAccertamentoDto.getEsercizio();
						datiContabili += impAccertamentoDto.getCapitolo()!=null?impAccertamentoDto.getCapitolo():impAccertamentoDto.getCapitolo();
						datiContabili += impAccertamentoDto.getArticolo()!=null?impAccertamentoDto.getArticolo():impAccertamentoDto.getArticolo();
						datiContabili += impAccertamentoDto.getAnnoImpacc()!=null?impAccertamentoDto.getAnnoImpacc():impAccertamentoDto.getAnnoImpacc();
						datiContabili += impAccertamentoDto.getSubImpacc()!=null?impAccertamentoDto.getSubImpacc():impAccertamentoDto.getSubImpacc();
						datiContabili += impAccertamentoDto.getImportoImpacc()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getImportoImpacc();
						datiContabili += impAccertamentoDto.getCodDebBen()!=null?impAccertamentoDto.getCodDebBen():impAccertamentoDto.getCodDebBen();
						datiContabili += impAccertamentoDto.getDescCodDebBen()!=null?impAccertamentoDto.getImportoImpacc():impAccertamentoDto.getDescCodDebBen();
					}
					
					
					for (ImpegnoDaStampareDto impegnoDaStampareDto : listaImpegniDaStampare) {
						datiContabili += impegnoDaStampareDto.getCodiceCapitolo()!=null?impegnoDaStampareDto.getCodiceCapitolo():"";
						datiContabili += impegnoDaStampareDto.getMeccanograficoCapitolo()!=null?impegnoDaStampareDto.getMeccanograficoCapitolo():"";
						datiContabili += impegnoDaStampareDto.getDescrizioneCapitolo()!=null?impegnoDaStampareDto.getDescrizioneCapitolo():"";
						datiContabili += impegnoDaStampareDto.getImportoCapitolo()!=null?impegnoDaStampareDto.getImportoCapitolo():"";
						datiContabili += impegnoDaStampareDto.getImpegnoFormattato()!=null?impegnoDaStampareDto.getImpegnoFormattato():"";
						datiContabili += impegnoDaStampareDto.getDatiAtto()!=null?impegnoDaStampareDto.getDatiAtto():"";
						datiContabili += impegnoDaStampareDto.getDescrizioneImpegno()!=null?impegnoDaStampareDto.getDescrizioneImpegno():"";
						datiContabili += impegnoDaStampareDto.getImportoImpegno()!=null?impegnoDaStampareDto.getImportoImpegno():"";
						datiContabili += impegnoDaStampareDto.getCupImpegno()!=null?impegnoDaStampareDto.getCupImpegno():"";
						datiContabili += impegnoDaStampareDto.getCigImpegno()!=null?impegnoDaStampareDto.getCigImpegno():"";
						datiContabili += impegnoDaStampareDto.getAnnoLiq()!=null?impegnoDaStampareDto.getAnnoLiq():"";
						datiContabili += impegnoDaStampareDto.getNumeroLiq()!=null?impegnoDaStampareDto.getNumeroLiq():"";
						
						
						if(impegnoDaStampareDto.getListaSoggetti()!=null) {
							for (SoggettoDto soggettoDto : impegnoDaStampareDto.getListaSoggetti()) {
								datiContabili += soggettoDto.getSoggetto()!=null?soggettoDto.getSoggetto():"";
								datiContabili += soggettoDto.getDescr()!=null?soggettoDto.getDescr():"";
								datiContabili += soggettoDto.getNotePagamento()!=null?soggettoDto.getNotePagamento():"";
								datiContabili += soggettoDto.getModoPaga()!=null?soggettoDto.getModoPaga():"";
								for (DocumentoDto documentoDto : soggettoDto.getListaDocumenti()) {
									datiContabili += documentoDto.getTipoDoc()!=null?documentoDto.getTipoDoc():"";
									datiContabili += documentoDto.getNumeroDoc()!=null?documentoDto.getNumeroDoc():"";
									datiContabili += documentoDto.getDataDoc()!=null?documentoDto.getDataDoc().toString():"";
									datiContabili += documentoDto.getDataScadDoc()!=null?documentoDto.getDataScadDoc().toString():"";
									datiContabili += documentoDto.getImporto()!=null?documentoDto.getImporto():"";
									datiContabili += documentoDto.getCodiceCup()!=null?documentoDto.getCodiceCup():"";
									datiContabili += documentoDto.getCodiceCig()!=null?documentoDto.getCodiceCig():"";
								}
							}
						}
					}
				}
				
			}
			
			String shaProposta = FileChecksum.calcolaImprontaProposta(atto.getCodiceCifra(), atto.getDataCreazione(), atto.getOggetto(), atto.getAoo().getDescrizione(), atto.getDomanda().getTesto(), allegatiParteIntegrante, datiContabili);
			log.info("shaProposta:"+shaProposta);
			docPdf.setImpronta(shaProposta);
			
			String shaPropostaPrecedente = "";
			
			if(atto.getDocumentiPdf()!=null) {
				for(DocumentoPdf versioneProposta : atto.getDocumentiPdf()) {
					shaPropostaPrecedente = versioneProposta.getImpronta();
					break;
				}
			}
			
			documentoDaSalvare = !shaProposta.equalsIgnoreCase(shaPropostaPrecedente);
			if(documentoDaSalvare) {
				log.info("RISULTANO DATI CAMBIATI: SARà GENERATA UNA NUOVA VERSIONE DEL DOCUMENTO DI PROPOSTA PER"+atto.getCodiceCifra());
			}
		}else if(tipoDocumento.getType()!= null && tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString()) && Lists.newArrayList("DEC").contains(atto.getTipoAtto().getCodice())){
			String allegatiParteIntegrante = this.getAllegatiParteIntegranteString(atto.getAllegati());
			String shaProposta = FileChecksum.calcolaImprontaProposta(atto.getCodiceCifra(), atto.getDataCreazione(), atto.getOggetto(), atto.getAoo().getDescrizione(), atto.getDomanda().getTesto(), allegatiParteIntegrante, null);
			docPdf.setImpronta(shaProposta);
			
			String shaPropostaPrecedente = atto.getDocumentiPdf()!=null && atto.getDocumentiPdf().size() > 0 && atto.getDocumentiPdf().get(0)!=null && atto.getDocumentiPdf().get(0).getImpronta()!=null ? atto.getDocumentiPdf().get(0).getImpronta() : "";
			
			documentoDaSalvare = !shaProposta.equalsIgnoreCase(shaPropostaPrecedente);
			if(documentoDaSalvare) {
				log.info("RISULTANO DATI CAMBIATI: SARà GENERATA UNA NUOVA VERSIONE DEL DOCUMENTO DI PROPOSTA PER"+atto.getCodiceCifra());
			}
		}
		
		if(documentoDaSalvare) {
		
			
			/*
			 * Salvo documento su repository documentale
			 */
			String cmisObjectId = null;
			try {
				String attoFolderPath = serviceUtil.buildDocumentPath(
						tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
							
				Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(atto, tipoDocumento, omissis);
				
				cmisObjectId = dmsService.save(attoFolderPath,contenuto, nome, "application/pdf", createProps);
			}
			catch (Exception e) {
				throw new GestattiCatchedException(e, e.getMessage());
			}
					
			
			File filePdf = new File(nome, "application/pdf", null);
			filePdf.setCmisObjectId(cmisObjectId);
			filePdf.setSha256Checksum(FileChecksum.calcolaImpronta(contenuto));
			filePdf.setSize( new Long(contenuto.length) );
			filePdf = fileRepository.save(filePdf);
			
			docPdf.setFile(filePdf);
	
			docPdf.setCompleto(completo);
			docPdf.setFirmato(firmato);
			docPdf.setFirmatodatutti(firmatoDatutti);
			
			if(tipoDocumento.getType()==null || (!tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString()) && !tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROVVEDIMENTO.toString()))) {
				if(!omissis){
					
					if(atto.getDataAdozione()!=null){
						docPdf.setAttoId(null);
						docPdf.setAttoAdozioneId(atto.getId());
					}else{
						docPdf.setAttoId(atto.getId());
						docPdf.setAttoAdozioneId(null);
						
					}
				}else{
					
					if(atto.getDataAdozione()!=null){
						docPdf.setAttoOmissisId(null);
						docPdf.setAttoAdozioneOmissisId(atto.getId());
					}else{
						docPdf.setAttoOmissisId(atto.getId());
						docPdf.setAttoAdozioneOmissisId(null);
					}
				}
			}else if(tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROPOSTA.toString())){
				if(!omissis) {
					docPdf.setAttoId(atto.getId());
					docPdf.setAttoAdozioneId(null);
				}else {
					docPdf.setAttoOmissisId(atto.getId());
					docPdf.setAttoAdozioneOmissisId(null);
				}
			}else if(tipoDocumento.getType().toString().equals(DocumentoTypeEnum.PROVVEDIMENTO.toString())) {
				if(!omissis) {
					docPdf.setAttoId(null);
					docPdf.setAttoAdozioneId(atto.getId());
				}else {
					docPdf.setAttoOmissisId(null);
					docPdf.setAttoAdozioneOmissisId(atto.getId());
				}
			}else {
				throw new GestattiCatchedException("Errore tipo documento");
			}
		
		}else {
			log.info ("docPdf non creato");
			return null;
		}
		
		docPdf.setTipoDocumento(tipoDocumento);
		
		docPdf = documentoPdfRepository.save(docPdf);
		log.info("docPdf creato:"+docPdf!=null&&docPdf.getId()!=null?docPdf.getId().toString():"");
		return docPdf;
	}
	
	/*
	 * TODO: In ATTICO non UTILIZZATI
	 * 
	@Transactional
	public DocumentoPdf saveAttoPdf(Atto atto, java.io.File content, TipoDocumento tipoDocumento) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveAttoAdozione(atto, content, Boolean.FALSE, tipoDocumento);
		}
		throw new DocumentoPdfException("Pdf non presente.");

	}
	
	@Transactional
	public DocumentoPdf saveAttoOmissisPdf(Atto atto, java.io.File content, TipoDocumento tipoDocumento) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveAtto(atto, content, Boolean.TRUE, tipoDocumento);
		}
		throw new DocumentoPdfException("Pdf non presente.");

	}

	@Transactional
	public DocumentoPdf saveAttoAdozionePdf(Atto atto, java.io.File content, TipoDocumento tipoDocumento) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveAttoAdozione(atto, content, Boolean.FALSE, tipoDocumento);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	
	@Transactional
	public DocumentoPdf saveAttoAdozioneOmissisPdf(Atto atto, java.io.File content, TipoDocumento tipoDocumento) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveAttoAdozione(atto, content, Boolean.TRUE, tipoDocumento);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	*/

	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public DocumentoPdf saveAttoInesistentePdf(Atto atto, java.io.File content) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSaveAttoInesistente(atto, content);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	*/
	
	/*
	 * TODO: In ATTICO non UTILIZZATI
	 * 
	private DocumentoPdf executeSaveAtto(Atto atto, java.io.File content, Boolean omissis, TipoDocumento tipoDocumento, String... args)
			throws FileNotFoundException, IOException, CifraCatchedException { 
		String strOmissis = "";
		if(omissis.booleanValue())
			strOmissis = "omissis";
		
		String nome = "Proposta-"+strOmissis+"-" + atto.getCodiceCifra().replace("/", "_") + ".pdf";
		nome = nome.replace("--", "-");
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		if(!omissis.booleanValue()){
			docPdf.setAtto(atto);
		}else{
			docPdf.setAttoOmissis(atto);
		}
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}

	private DocumentoPdf executeSaveAttoAdozione(Atto atto, java.io.File content, Boolean omissis, TipoDocumento tipoDocumento)
			throws FileNotFoundException, IOException, CifraCatchedException {
		String strOmissis = "";
		if(omissis.booleanValue())
			strOmissis = "_omissis";
	
		String nome = "Provvedimento"+strOmissis+"_n_";
		if(atto.getNumeroAdozione()!= null && atto.getNumeroAdozione().split("/").length==4){
			nome += atto.getNumeroAdozione().split("/")[3];
		}else{
			nome += atto.getNumeroAdozione() != null ? atto.getNumeroAdozione() :"";
		}
		
		nome += "-"+atto.getCodiceCifra().replace("/", "_") + ".pdf";
		
		nome = nome.replace(" ", "_");
		nome = nome.replace("/", "_");
		
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		
		docPdf.setTipoDocumento(tipoDocumento);
		if(!omissis.booleanValue()){
			docPdf.setAttoAdozione(atto);
		}else{
			docPdf.setAttoAdozioneOmissis(atto);
		}
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}
	*/
	
	/*
	 * TODO: In ATTICO non previsti
	 * 
	private DocumentoPdf executeSaveAttoInesistente(Atto atto, java.io.File content)
			throws FileNotFoundException, IOException, CifraCatchedException {
	
		String nome = "Atto-Inesistente-" + atto.getCodiceCifra().replace("/", "_") + ".pdf";
		nome = nome.replace("--", "-");
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.atto_inesistente.name());
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		docPdf.setCreatedBy(atto.getCreatedBy());
		docPdf.setAtto(atto);
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf.setAooSerie(atto.getAoo());
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}
	
	@Transactional
	public DocumentoPdf saveFileFirmatoOfResoconto(Resoconto resoconto, OrdineGiorno odg, File file, Utente utenteLoggato, String createdBy, TipoDocumento tipo) throws IOException, RiversamentoPoolException {
		log.debug("save documento firmato per resoconto" + resoconto);
		String tipoResoconto = "";
		if(resoconto.getTipo() == TIPO_RESOCONTO_INTEGRALE){
			tipoResoconto = "resoconto-integrale";
		} else if(resoconto.getTipo() == TIPO_RESOCONTO_PRESS_ASS){
			tipoResoconto = "presenze-assenze";
		}
		String nome = generaNomeDocumentoResoconto(odg, tipoResoconto);
		
		file.setNomeFile(nome);
		file = fileRepository.save(file);
		
		return executeSaveFileFirmatoResoconto(resoconto, file, utenteLoggato, createdBy, tipo);
	}
	
	@Transactional
	public DocumentoPdf saveFileFirmatoOfResoconto(Resoconto resoconto, OrdineGiorno odg, MultipartFile multipartFile, Utente utenteLoggato, String createdBy, TipoDocumento tipo) throws IOException, CifraCatchedException {
		log.debug("save documento firmato per resoconto" + resoconto);
		String tipoResoconto = "";
		if(resoconto.getTipo() == TIPO_RESOCONTO_INTEGRALE){
			tipoResoconto = "resoconto-integrale";
		} else if(resoconto.getTipo() == TIPO_RESOCONTO_PRESS_ASS){
			tipoResoconto = "presenze-assenze";
		}
		String nome = generaNomeDocumentoResoconto(odg, tipoResoconto);
		
		// Salvo documento su repository documentale
		
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(tipo, null, null, null);
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), nome, multipartFile.getContentType(), null);
		} catch (Exception e) {
			throw new CifraCatchedException(e, e.getMessage());
		}
		
		File file = new File(
				nome,
				multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		file = fileRepository.save(file);
		return executeSaveFileFirmatoResoconto(resoconto, file, utenteLoggato, createdBy, tipo);
	}
	
	private DocumentoPdf executeSaveFileFirmatoResoconto(Resoconto resoconto, File file, Utente utenteLoggato, String createdBy, TipoDocumento tipo) throws RiversamentoPoolException{
		DocumentoPdf docPdf = new DocumentoPdf();
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		docPdf.setFirmatodatutti(true);
		docPdf.setFirmatario(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
		docPdf.setCreatedBy(createdBy);
		docPdf.setResoconto(resoconto);
		docPdf.setTipoDocumento(tipo);
		
		docPdf = documentoPdfRepository.save(docPdf);
		
//		riversamentoPoolService.aggiungiRiversamentoResoconto(docPdf);
		return docPdf;
	}
	*/
	
	@Transactional
	public DocumentoPdf saveFileFirmato(Verbale verbale, MultipartFile multipartFile, Utente utenteLoggato ) throws IOException, GestattiCatchedException {
		log.debug("save documento firmato da multipart file per il :" + verbale);
		
		String nome = getFileNameVerbale(verbale,true);
		
		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				verbale.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.verbalegiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.verbaleconsiglio.name();
		}
		
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipoDocCodice);
		/*
		 * Salvo documento su repository documentale
		 */
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, null);
			cmisObjectId = dmsService.save(attoFolderPath, multipartFile.getBytes(), nome, multipartFile.getContentType(), null);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e, e.getMessage());
		}
		
		File file = new File(
				nome,
				multipartFile.getContentType(), multipartFile.getSize());
		file.setCmisObjectId(cmisObjectId);
		file.setSha256Checksum(FileChecksum.calcolaImpronta(multipartFile.getBytes()));
		file = fileRepository.save(file);

		return executeSaveFileFirmatoVerbale(verbale, file, utenteLoggato, "");
	}
	
	@Transactional
	public DocumentoPdf saveFileFirmato(Verbale verbale, File file, Utente utenteLoggato, String createdBy) throws IOException, GestattiCatchedException {
		log.debug("save documento firmato per verbale" + verbale);
		
		String nome = getFileNameVerbale(verbale,true);
		
		file.setNomeFile(nome);
		file = fileRepository.save(file);
		
		return executeSaveFileFirmatoVerbale(verbale, file, utenteLoggato, createdBy);
	}
	
	private DocumentoPdf executeSaveFileFirmatoVerbale(Verbale verbale, File file, Utente utenteLoggato, String createdBy){
		DocumentoPdf docPdf = new DocumentoPdf();
		docPdf.setFile(file);
		docPdf.setFirmato(true);
		docPdf.setFirmatario(utenteLoggato.getNome() + " " + utenteLoggato.getCognome());
		long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
		if(idProfDelega < 0) {
			idProfDelega = BpmThreadLocalUtil.getProfiloOriginarioId();
		}
		if (idProfDelega > 0) {
			Profilo profiloDelegante = profiloRepository.findOne(idProfDelega);
			
			if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
				Utente utDelegante = profiloDelegante.getUtente();
				docPdf.setFirmatarioDelegante(utDelegante.getNome() + " " + utDelegante.getCognome());
			}
		}
		docPdf.setCreatedBy(createdBy);
		docPdf.setVerbaleId(verbale.getId());
		
		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				verbale.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.verbalegiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.verbaleconsiglio.name();
		}
		
		docPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(tipoDocCodice));
		
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}
	
	@Transactional
	public DocumentoPdf saveOdGPdf(OrdineGiorno odg, java.io.File content, 
			Boolean firmato, boolean variazione, boolean annullamento, 
			String firmatario, String createdBy) throws FileNotFoundException, IOException, GestattiCatchedException {
		if (content != null) {
			return executeOdGPdf(odg, content,  firmato, variazione, annullamento, firmatario, createdBy);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	
	
	@Transactional
	public DocumentoPdf saveResocontoPdf(
			SedutaGiunta seduta, Resoconto resoconto, 
			java.io.File content, TipoDocumentoEnum tipo, String createdBy) 
					throws FileNotFoundException, IOException, GestattiCatchedException {
		if (content != null) {
			return executeResocontoPdf(seduta, resoconto, content,  tipo, createdBy);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	
	private String generaNomeDocumentoResoconto(SedutaGiunta seduta, TipoDocumentoEnum tipo){
		String nome = StringUtils.capitalize(tipo.name()) + "_";
		
		/*
		 * Vecchia versione CIFRA 
		 *
		if(tipo.equals("resoconto-integrale")){
			nome = nome + "Integrale_";
		}
		else if(tipo.equals("resoconto-parziale")){
			nome = nome + "Parziale_";
		}
		else if(tipo.equals("presenze-assenze")){
			nome = nome + "Presenze_Assenze_";
		}
		*/
		
		nome = nome + "seduta_n_" + seduta.getNumero();
		// DateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		// if(seduta.getSecondaConvocazioneInizio() != null){
		//	nome = nome + sdf.format(seduta.getSecondaConvocazioneInizio().toDate());
		// }
		// else{
		//	nome = nome + sdf.format(seduta.getPrimaConvocazioneInizio().toDate());
		//}
		
		nome = nome + ".pdf";
		
		return nome;
	}
	
	private String generaNomeDocumentoOdg(OrdineGiorno odg, Boolean firmato, boolean variazione, boolean annullamento){
		
		String strFirmato = "";
		if(firmato!=null && firmato.booleanValue()){
			strFirmato = "-Firmato";
		}
		
		String BASE = "Base";
		String dateTime = new SimpleDateFormat("yyyyMMddHHmm").format(new Date());
		String odgBaseSuffix = BASE +  "_seduta_n_" + odg.getSedutaGiunta().getNumero() + strFirmato + "_" + dateTime + ".pdf";
		String odgBaseSuffixVariazione = "_n_" + odg.getSedutaGiunta().getNumero() + strFirmato + "_" + dateTime + ".pdf";
		
		
		String ordineGiornoLavoro="Ordine_Giorno";
		
		if(odg.getSedutaGiunta()!=null) {
			SedutaGiunta seduta = odg.getSedutaGiunta();
			if (SedutaGiuntaConstants.organoSeduta.C.name().equalsIgnoreCase(
					seduta.getOrgano())) {
				ordineGiornoLavoro = "Ordine_Lavori";
			}
		}
		
		
		String nome = "";
		if(annullamento){
			nome = "Annullamento_seduta-odg-"+odgBaseSuffix;
		}
		else if((odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2) && !variazione)
			nome = ordineGiornoLavoro+"_"+odgBaseSuffix;
		else if((odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2) && variazione)
			nome = "Variazione_estremi_seduta"+odgBaseSuffixVariazione;
		else
			nome = ordineGiornoLavoro+"_"+odg.getTipoOdg().getDescrizione() + "-seduta_n_" + odg.getSedutaGiunta().getNumero() + strFirmato + "_" + dateTime + ".pdf";
		
		// n_" + odg.getProgressivoOdgSeduta() + "_
		
		nome = nome.replace(" ", "_");
		nome = nome.replace("/", "_");
		
		return nome;
	}
	
	
	private DocumentoPdf executeResocontoPdf(SedutaGiunta seduta, Resoconto resoconto, java.io.File content, TipoDocumentoEnum tipo, String createdBy)
			throws FileNotFoundException, IOException, GestattiCatchedException {
		
		String nomeDoc = generaNomeDocumentoResoconto(seduta,tipo);
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipo.name());
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nomeDoc);
		docPdf.setFirmato(false);
		docPdf.setFirmatodatutti(false);
		docPdf.setFirmatario("");
		docPdf.setCreatedBy(createdBy);
		docPdf.setResocontoId(resoconto.getId());
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}

	
	private DocumentoPdf executeOdGPdf(OrdineGiorno odg, java.io.File content, 
			Boolean firmato, boolean variazione, boolean annullamento, 
			String firmatario, String createdBy)
			throws FileNotFoundException, IOException, GestattiCatchedException {
		
		String nomeDoc = generaNomeDocumentoOdg(odg,firmato, variazione, annullamento);
		
		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odg.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.ordinegiornogiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.ordinegiornoconsiglio.name();
		}
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipoDocCodice);
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nomeDoc);
		docPdf.setFirmato(firmato);
		
		if(firmato!=null && firmato.booleanValue() == true){
			docPdf.setFirmatodatutti(true);
		}
		else{
			docPdf.setFirmatodatutti(false);
		}
		docPdf.setFirmatario(firmatario);
		long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
		if(idProfDelega < 0) {
			idProfDelega = BpmThreadLocalUtil.getProfiloOriginarioId();
		}
		if (idProfDelega > 0) {
			Profilo profiloDelegante = profiloRepository.findOne(idProfDelega);
			
			if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
				Utente utDelegante = profiloDelegante.getUtente();
				docPdf.setFirmatarioDelegante(utDelegante.getNome() + " " + utDelegante.getCognome());
			}
		}
		docPdf.setCreatedBy(createdBy);
		
		docPdf.setOrdineGiornoId(odg.getId());
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf = documentoPdfRepository.save(docPdf);
		
//		In ATTICO non previsti
//		if(docPdf.getFirmatodatutti().equals(true)){
//			riversamentoPoolService.aggiungiRiversamentoOdg(docPdf);
//		}
		return docPdf;
	}
	
	/*
	 * 
	@Transactional
	public DocumentoPdf saveSchedaAnagraficoContabile(Atto atto, java.io.File content, Boolean firmato, String firmatario, String createdBy, Boolean omissis) throws FileNotFoundException, IOException, CifraCatchedException {
		if (content != null) {
			return executeSchedaAnagraficoContabile(atto, content, omissis, firmato, firmatario, createdBy);
		}
		throw new DocumentoPdfException("Pdf non presente.");
	}
	
	private DocumentoPdf executeSchedaAnagraficoContabile(Atto atto, java.io.File content, Boolean omissis, Boolean firmato, String firmatario, String createdBy)
			throws FileNotFoundException, IOException, CifraCatchedException {
		String strOmissis = "";
		if(omissis.booleanValue()){
			strOmissis = "_omissis";
		}
		String strFirmato = "";
		if(firmato!=null && firmato.booleanValue()){
			strFirmato = "-Firmato";
		}
	
		String nome = "Scheda_Anagrafica"+strOmissis+strFirmato+"-" + atto.getCodiceCifra().replace("/", "_") + ".pdf";
		nome = nome.replace(" ", "_");
		nome = nome.replace("/", "_");
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.scheda_anagrafico_contabile.name());
		
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		docPdf.setFirmato(firmato);
		if(firmato!=null && firmato.booleanValue() == true){
			docPdf.setFirmatodatutti(true);
		}else{
			docPdf.setFirmatodatutti(false);
		}
		docPdf.setFirmatario(firmatario);
		docPdf.setCreatedBy(createdBy);
		docPdf.setTipoDocumento(tipoDocumentoService.findByCodice(TipoDocumentoEnum.scheda_anagrafico_contabile.name()));
		if(!omissis.booleanValue()){
			docPdf.setAttoSchedaAnagraficoContabile(atto);
		}
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}
	*/
	
	private DocumentoPdf saveFile(TipoDocumento tipoDocumento, java.io.File content, String nome) 
			throws FileNotFoundException, IOException, GestattiCatchedException {
		
		File filePdf = new File(nome, "application/pdf", null );
		FileInputStream in = new FileInputStream(content);
		byte []  contenuto = IOUtils.toByteArray(in);
		IOUtils.closeQuietly(in);
		
		/*
		 * Salvo documento su repository documentale
		 */
		String cmisObjectId = null;
		try {
			String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, null);
			cmisObjectId = dmsService.save(attoFolderPath, contenuto, nome, "application/pdf", null);
		}
		catch (Exception e) {
			// TODO: gestire meglio le eccezioni, viene sollevata RuntimeException in modo che Spring faccia Rollback
			throw new RuntimeException(e.getMessage(), e);
			// throw new CifraCatchedException(e, e.getMessage());
		}
		
		filePdf.setSize( new Long(contenuto.length) );
		filePdf.setCmisObjectId(cmisObjectId);
		filePdf.setSha256Checksum(FileChecksum.calcolaImpronta(contenuto));
		filePdf = fileRepository.save(filePdf);
		DocumentoPdf docPdf = new DocumentoPdf();
		docPdf.setFile(filePdf);

		docPdf.setFirmato(false);
		docPdf.setFirmatodatutti(false);
		return docPdf;
	}

	@Transactional(readOnly = true)
	public File download(Long idDocumento) throws Exception {
		DocumentoPdf  doc = documentoPdfRepository.findOne(idDocumento);
		File file = null;
		if (doc != null && doc.getFile()!=null) {
			file = doc.getFile();
		}
		return file;
	}
	
	@Transactional
	public DocumentoPdf saveVerbalePdf(Verbale verbale, java.io.File content, Boolean firmato, String firmatario, String createdBy) 
			throws FileNotFoundException, IOException, GestattiCatchedException {
		if (content != null) {
			return executeSaveVerbale(verbale, content, firmato, firmatario, createdBy);
		}
		throw new DocumentoPdfException("Pdf non presente.");

	}
	
	@Transactional(readOnly = true)
	private long contaFirmeVerbale(Long verbaleId){
		BooleanExpression exp = QDocumentoPdf.documentoPdf.verbaleId.eq(verbaleId).and(QDocumentoPdf.documentoPdf.firmato.eq(true));
		return documentoPdfRepository.count(exp);
	}
	
	private DocumentoPdf executeSaveVerbale(Verbale verbale, java.io.File content, Boolean firmato, String firmatario, String createdBy)
			throws FileNotFoundException, IOException, GestattiCatchedException {
		String nome = this.getFileNameVerbale(verbale, false);
		
		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				verbale.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.verbalegiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.verbaleconsiglio.name();
		}
		
		TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(tipoDocCodice);
		
		DocumentoPdf docPdf = saveFile(tipoDocumento, content, nome);
		log.debug(String.format("Doc pdf creato!! id=%s - firmato da tutti = %s", docPdf.getId(), docPdf.getFirmatodatutti()));
		docPdf.setFirmato(firmato);
		docPdf.setFirmatodatutti(false);
		docPdf.setFirmatario(firmatario);
		long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
		if(idProfDelega < 0) {
			idProfDelega = BpmThreadLocalUtil.getProfiloOriginarioId();
		}
		if (idProfDelega > 0) {
			Profilo profiloDelegante = profiloRepository.findOne(idProfDelega);
			
			if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
				Utente utDelegante = profiloDelegante.getUtente();
				docPdf.setFirmatarioDelegante(utDelegante.getNome() + " " + utDelegante.getCognome());
			}
		}
		docPdf.setCreatedBy(createdBy);
		docPdf.setVerbaleId(verbale.getId());
		docPdf.setTipoDocumento(tipoDocumento);
		docPdf = documentoPdfRepository.save(docPdf);
		return docPdf;
	}

	@Transactional(readOnly=true)
	public DocumentoPdf findById(BigInteger id) {
		return findById(id.longValue());
	}
	
	@Transactional(readOnly=true)
	public DocumentoPdf findOne(Long id){
		DocumentoPdf doc = documentoPdfRepository.findOne(id);
		return doc;
	}
	
	@Transactional(readOnly=true)
	public DocumentoPdf findById(Long id) {
		return documentoPdfRepository.getOne(id.longValue());
	}

	public List<Object[]> findDocumentiPdfDaRiversare(boolean attiGiunta) {
		if(!attiGiunta) {
			return documentoPdfRepository.findByFirmatoAndNotRiversatoInSerie();			
		} else {
			return documentoPdfRepository.findByFirmatoAndNotRiversatoInSerieAttiGiunta();			
		}
	}
	
	public BigInteger findByAttoFirmato(BigInteger attoId) {
		return documentoPdfRepository.findByAttoAndFirmatoTrue(attoId);
	}

	public Long findByAttoAdozioneOmissisFirmato(Long attoId) {
		return documentoPdfRepository.findByAttoAdozioneOmissisAndFirmatoTrue(attoId);
	}
	
	public Long findByAttoAdozioneFirmato(Long attoId) {
		return documentoPdfRepository.findByAttoAdozioneAndFirmatoTrue(attoId);
	}
	
	public List<Object[]> findAllDaRiversareInAlbo() {
		return documentoPdfRepository.findAllDaRiversareInAlbo();
	}

	public BigInteger findByParereFirmato(BigInteger parereId) {
		return documentoPdfRepository.findByParereAndFirmatoTrue(parereId);
	}
	
	public BigInteger findByLetteraFirmato(BigInteger letteraId) {
		return documentoPdfRepository.findByLetteraAndFirmatoTrue(letteraId);
	}
	
	public BigInteger findByResocontoFirmato(BigInteger resocontoId) {
		return documentoPdfRepository.findByResocontoAndFirmatoTrue(resocontoId);
	}

	public BigInteger findByVerbaleFirmato(BigInteger verbaleId) {
		return documentoPdfRepository.findByVerbaleAndFirmatoTrue(verbaleId);
	}

	public List<Object[]> relatedToAttoAndFirmato(Long attoId) {
		return documentoPdfRepository.relatedToAttoAndFirmatoTrue(attoId);
	}
	
	public List<Object[]> relatedToResoconto() {
		return documentoPdfRepository.relatedToResoconto();
	}
	
	public DocumentoPdf findSchedaContabileByAttoMain(Long attoId) {
		return documentoPdfRepository.findSchedaContabileByAtto(attoId);
	}

	@Transactional
	public void deleteDocumentoPdfAndFile(Long documentoPdfId){
		if(documentoPdfId!=null) {
			boolean exists = documentoPdfRepository.exists(documentoPdfId);
			if(!exists) {
				return;
			}
		}else {
			return;
		}
		
		Long fileId = documentoPdfRepository.getFileIdByDocumentoPdfId(documentoPdfId);
		sottoscrittoreAttoRepository.deleteByDocumentoPdfId(documentoPdfId);
		documentoPdfRepository.delete(documentoPdfId);
		File file = fileRepository.findOne(fileId);
		/*
		if(file!=null && file.getCmisObjectId()!=null) {
			dmsService.remove(file.getCmisObjectId());
		}
		*/
		if(fileId != null){
			fileRepository.delete(fileId);
		}
	}

}
