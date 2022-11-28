package it.linksmt.assatti.gestatti.web.rest;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.pdfbox.util.PDFMergerUtility;
import org.camunda.bpm.engine.TaskService;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.io.RandomAccessSourceFactory;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.RandomAccessFileOrArray;

import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.StatoAttoDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.contabilita.config.JenteProps;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceException;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.AttoSchedaDato;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.EventoEnum;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.SezioneTipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.dto.DocumentoPdfDto;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.fdr.exception.FirmaRemotaException;
import it.linksmt.assatti.fdr.service.FdrWsUtil;
import it.linksmt.assatti.fdr.service.FirmaRemotaService;
import it.linksmt.assatti.gestatti.web.rest.util.AllegatoAttoUtil;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.gestatti.web.rest.util.ReportPdfCriteriTrasformer;
import it.linksmt.assatti.security.IAttoRetrievalSavingAccessControl;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.AttoSchedaDatoService;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoInformaticoService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.EventoService;
import it.linksmt.assatti.service.ExcelService;
import it.linksmt.assatti.service.FileService;
import it.linksmt.assatti.service.JobTrasparenzaService;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.SezioneTipoAttoService;
import it.linksmt.assatti.service.TipoAttoService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.XmlBuilderService;
import it.linksmt.assatti.service.dto.AttoCriteriaDTO;
import it.linksmt.assatti.service.dto.AttoWorkflowDTO;
import it.linksmt.assatti.service.dto.FirmaRemotaDTO;
import it.linksmt.assatti.service.dto.ModelloHtmlDto;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.RicercaLiberaDTO;
import it.linksmt.assatti.service.dto.SchedaValoriDlg33DTO;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;
import it.linksmt.assatti.service.dto.TipoDocumentoDto;
import it.linksmt.assatti.service.exception.AllegatoNonPermessoException;
import it.linksmt.assatti.service.exception.ExceptionUtil;
import it.linksmt.assatti.service.exception.GeneraFirmaDocumentoException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.GestattiEsecuzioneLavorazioneProcessoException;
import it.linksmt.assatti.service.util.FestiviUtils;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.FileChecksum;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller for managing Atto.
 */
@RestController
@RequestMapping("/api")
public class AttoResource {

	private final Logger log = LoggerFactory.getLogger(AttoResource.class);
	
	@Autowired
	ApplicationContext applicationContext;
	
//TODO integrazione 
//	@Inject
//	private ServiceUtil serviceUtil;

//	@Inject
//	private DestinatarioInternoService destinatarioInternoService;
	
	@Inject
	private TipoAttoService tipoAttoService;
	
	@Inject
	private ReportService reportService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private AooService aooService;

	@Inject
	private AttoSchedaDatoService attoSchedaDatoService;
	
	@Inject
	private ProfiloService profiloService;

	@Inject
	private DocumentoInformaticoService documentoInformaticoService;

//	@Inject
//	private SottoScrittoreAttoService sottoScrittoreAttoService;

	@Inject
	private ObjectMapper objectMapper;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;

	@Inject
	private DocumentoPdfService documentoPdfService;

	@Inject
	private WorkflowServiceWrapper workflowService;
	
//	@Inject
//	private ParereService parereService;

	@Inject
	UtenteService utenteService;
	
	@Inject
	private ReportPdfCriteriTrasformer reportPdfCriteriTrasformer;
	
	@Inject
	private EventoService eventoService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private ExcelService excelService;
	
	@Inject
	private XmlBuilderService xmlBuilderService;

	@Inject
	private TaskService taskService;
	
//	@Inject
//	private CodiceProgressivoService codiceProgressivoService;
	
	//TODO integrazione @Inject
	//TODO integrazione private ProtocollazioneDAO protocollazione;

	@Inject
	private FileService fileService;
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
//	@Inject
//	private ModelloHtmlService modelloHtmlService;
//	
//	@Inject
//	private SedutaGiuntaRepository sedutaRepository;
	
	@Inject
	private ContabilitaService contabilitaService;
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject JobTrasparenzaService jobTrasparenzaService;
	
	@Autowired
	private SezioneTipoAttoService sezioneTipoAttoService;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	private IAttoRetrievalSavingAccessControl attoRetrievalSavingAccessControl;
	
	
	
	@PostConstruct
	private void init() throws Exception {
		String className = WebApplicationProps.getProperty(ConfigPropNames.ATTO_RETRIEVAL_ACCESS_CONTROL_CLASS);
		attoRetrievalSavingAccessControl = (IAttoRetrievalSavingAccessControl)applicationContext.getBean(Class.forName(className));
	}
	
//	@Autowired
//	private TipoDocumentoService tipoDocumentoService;
	
	/**
     * GET  /attos/search -> get attos by criterias.
     *
	 * In ATTICO SEMBRA NON UTILIZZATO
	 * 
    @RequestMapping(value = "/attos/searchRevocato",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Atto>> searchRevocato(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
            @RequestBody String searchStr) throws CifraCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		// TODO: ???
	  		// search.addProperty("codiceAtto", "DIR");
	  		
	  		Sort sort = new Sort(new Order(search.get("direction").getAsString() == null || search.get("direction").getAsString().equalsIgnoreCase("asc") ? Direction.ASC : Direction.DESC, search.get("ordinamento").getAsString()), new Order(Direction.DESC, "id" ));
	  		Page<Atto> page = attoService.findAllAdottati(PaginationUtil.generatePageRequest(offset, limit, sort), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "", offset, limit);
	        
	        List<Atto> attiFE = new ArrayList<Atto>();
	    	for(Atto atto : page){
	    		attiFE.add(this.filtraAttoPerFrontEnd(atto));
	    	}
	    	
	        return new ResponseEntity<List<Atto>>(attiFE, headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }

    private Atto filtraAttoPerFrontEnd(Atto atto){
		Atto nuovo = new Atto();
		nuovo.setCodiceCifra(atto.getCodiceCifra());
		nuovo.setNumeroAdozione(atto.getNumeroAdozione());
		nuovo.setDataAdozione(atto.getDataAdozione());
		nuovo.setId(atto.getId());
		return nuovo;
	}
	*/
    
	/**
  	 * GET /attos/{id}/documento/{idDocumento}-> download documento versione pdf
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/attos/{id}/documento/{idDocumento}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			@PathVariable final Long idDocumento, // non è cmisObjectId!!! è idDocumentoPdf
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download documento versione pdf : {}", id);
	
//			Atto atto = attoService.findOne(id);
//			if (atto == null) {
//				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
//	  		}
	  		
	  		DocumentoPdf documentoPdf = documentoPdfService.findOne(idDocumento);

	  		Long idFile = documentoPdf.getFile().getId();
	  		
			File file = fileService.findByFileId(idFile); // documentoPdfService.download( idDocumento);
			if (file == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			byte[] content = dmsService.getContent(file.getCmisObjectId());
			return DownloadFileUtil.responseStream(content, file.getNomeFile(), file.getSize(), file.getContentType());
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	
  	}
  	
  	/**
  	 * GET /attos/open/{id}/documento/{idDocumento} -> download documento versione pdf
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/attos/open/{id}/documento/{idDocumento}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumentoOpen(
  			@PathVariable final Long id,
  			@PathVariable final Long idDocumento,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download documento versione pdf : {}", id);
	
//			File file = documentoPdfService.download( idDocumento);
			File file = fileService.findByFileId(idDocumento);
			if (file == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			byte[] content = dmsService.getContent(file.getCmisObjectId());
			return DownloadFileUtil.responseStream(content, file.getNomeFile(), file.getSize(), file.getContentType());
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	
  	}



  	/**
  	 * GET /attos/{id}/allegato/{idAllegato} --> download documento allegato
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/attos/{id}/allegato/{idAllegato}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<ByteArrayResource> downloadAllegato(
  			@PathVariable final Long id,
  			@PathVariable final Long idAllegato ) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download documento allegato : {}", id);
	
			DocumentoInformatico allegato = documentoInformaticoService.findOne( idAllegato );
	
//			File result = documentoInformaticoService.downloadAllegato(  allegato.getFile().getId(), allegato.getId() );
			
			byte[] content = dmsService.getContent(allegato.getFile().getCmisObjectId());
			
			ByteArrayResource byteArrayResource = new ByteArrayResource(content);
			
//			return DownloadFileUtil.responseStream(result);
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(allegato.getFile().getNomeFile().replaceAll(" ", "_"), StandardCharsets.UTF_8.toString()));
			
			ResponseEntity<ByteArrayResource> result = new ResponseEntity<ByteArrayResource>(byteArrayResource, headers, HttpStatus.OK);
			return result;
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}

  	 /**
  	 * GET /attos/{id}/allegato/{idAllegato}/omissis-> download documento allegato omissis
  	 * @throws IOException
  	 */
  	@RequestMapping(value = "/attos/{id}/allegato/{idAllegato}/omissis", method = RequestMethod.GET , produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<ByteArrayResource> downloadAllegatoOmissis(
  			@PathVariable final Long id,
  			@PathVariable final Long idAllegato,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get Face : {}", id);
	
//	  		File result = null;
			DocumentoInformatico allegato = documentoInformaticoService.findOne( idAllegato);
			String cmisObjectId = null;
			String filename = null;
			
			if(allegato.getFileomissis()!=null){
//				result = documentoInformaticoService.downloadAllegato(  allegato.getFileomissis().getId(), allegato.getId() );
				cmisObjectId = allegato.getFileomissis().getCmisObjectId();
				filename = allegato.getFileomissis().getNomeFile();
			}
			else{
//				result = documentoInformaticoService.downloadAllegato(  allegato.getFile().getId(), allegato.getId());
				cmisObjectId = allegato.getFile().getCmisObjectId();
				filename = allegato.getFile().getNomeFile();
			}
			
			byte[] content = dmsService.getContent(cmisObjectId);
			
			ByteArrayResource byteArrayResource = new ByteArrayResource(content);
			
//			return DownloadFileUtil.responseStream(result);
			
			MultiValueMap<String, String> headers = new LinkedMultiValueMap<String, String>();
			headers.add("Content-Disposition", "attachment; filename=" + URLEncoder.encode(filename.replaceAll(" ", "_"), StandardCharsets.UTF_8.toString()));
			
			ResponseEntity<ByteArrayResource> result = new ResponseEntity<ByteArrayResource>(byteArrayResource, headers, HttpStatus.OK);
			return result;
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}


  	 /**
  	 * GET /attos/{id}/trasparenza/allegato/{schemaDatoId}/{progressivoElemento} -> download documento trasparenza
  	 * @throws IOException
  	 */
  	@RequestMapping(value = "/attos/{id}/trasparenza/allegato/{schemaDatoId}/{progressivoElemento}", method = RequestMethod.GET , produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<ByteArrayResource> downloadAllegatoTrasparenza(
  			@PathVariable final Long id,
			@PathVariable final Long schemaDatoId,
			@PathVariable final Integer progressivoElemento,
	  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get id : {}", id);
	  		log.debug("REST request to get schemaDatoId : {}", schemaDatoId);
	  		log.debug("REST request to get progressivoElemento : {}", progressivoElemento);
	
	  		Atto atto = attoService.findOne(id);
	  		if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
	  		AttoSchedaDato attoSchedaDato = attoSchedaDatoService.getAttoSchedaDato(id, schemaDatoId, progressivoElemento);
	  		if (attoSchedaDato == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
//	  		File result = documentoInformaticoService.downloadAllegato( attoSchedaDato.getFileValore().getId(), null );
//			return DownloadFileUtil.responseStream(result);
			
			byte[] content = dmsService.getContent(attoSchedaDato.getFileValore().getCmisObjectId());
			
			ByteArrayResource byteArrayResource = new ByteArrayResource(content);
			
			return new ResponseEntity<ByteArrayResource>(byteArrayResource, HttpStatus.OK);
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}



	/**
	 *
	 * 	 * POST /attos/{id}/trasparenza/allegato/{schemaDatoId}-> upload documento trasparenza
	 * @param id
	 * @param schemaDatoId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos/{id}/trasparenza/allegato/{schemaDatoId}/{elemIndex}", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<File> uploadAllegatoTrasparenza(@PathVariable final Long id, @PathVariable final Long schemaDatoId,
			@RequestParam("file") final MultipartFile  file) throws GestattiCatchedException {
		try{
			log.debug("REST request to allegato id : {}", id);
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			File fileSaved = documentoInformaticoService.saveFile(atto, file);
			return new ResponseEntity<File>(fileSaved,HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 *
	 * 	 * POST /attos/{id}/trasparenza/allegato/{schemaDatoId}-> upload documento trasparenza
	 * @param id
	 * @param schemaDatoId
	 * @param file
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos/{id}/firmato/allegato", method = RequestMethod.POST, 
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<DocumentoPdf> uploadfirmato(
			@PathVariable final Long id,
			@RequestParam final  Boolean omissis,
			@RequestParam final   Boolean adozione,
			@RequestParam(required=false) final Boolean schedaAnagraficoContabile,
			@RequestParam(required=false) final Boolean relataPubblicazione,
			@RequestParam(required=false) final Boolean attoInesistente,
			@RequestParam(value="parereId",required=false) final Long parereId,
			@RequestParam(required=false) final String codiceTipoDocumento,
			@RequestParam("file") final MultipartFile  file,
			@RequestHeader(value="taskBpmId" ,required=false) final String taskBpmId,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
		try{
			log.debug("REST request to allegato id : {}", id);
	
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			//eventuale delega
			String profDelegante = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
			if (!StringUtil.isNull(profDelegante)) {
				Profilo profiloDelegante = bpmWrapperUtil.getProfiloByUsernameBpm(profDelegante);
				if (profiloDelegante != null) {
					BpmThreadLocalUtil.setProfiloDeleganteId(profiloDelegante.getId());
				}
			}

			//eventuale riassegnazione
			String profOriginarioStr = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
			if (!StringUtil.isNull(profOriginarioStr)) {
				Profilo profOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioStr);
				if (profOriginario != null) {
					BpmThreadLocalUtil.setProfiloOriginarioId(profOriginario.getId());
				}
			}
			
			DocumentoPdf doc =   documentoPdfService.saveFileFirmato(atto, file, profiloId, codiceTipoDocumento);
	
			return new ResponseEntity<DocumentoPdf>(doc,HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}


	@RequestMapping(value = "/attos/{id}/schedatrasparenza", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Atto> saveSchedaTrasparenza(
			@PathVariable final Long id,
			@RequestBody final SchedaValoriDlg33DTO schedaValori,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException  {
		try{
			log.debug("REST request to save schedatrasparenza id : {}", id);
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			log.debug("schedaValori  :  "+ schedaValori);
	
			attoService.saveSchedaTrasparenza(atto,schedaValori,profiloId);
	
			return new ResponseEntity<Atto>(atto, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	@RequestMapping(value = "/attos/{id}/schedatrasparenza", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<SchedaValoriDlg33DTO> caricaSchedaTrasparenza(
			@PathVariable final Long id ) throws GestattiCatchedException{
		try{
			log.debug("REST request to save schedatrasparenza id : {}", id);
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			SchedaValoriDlg33DTO dto = attoService.caricaSchedaTrasparenza( atto );
	
			return new ResponseEntity<SchedaValoriDlg33DTO>(dto, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/*
	 * Per ATTICO esclusa gestione sottoscrittori
	 *
	@RequestMapping(value = "/attos/{id}/sottoscrittoreatto", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Void> sottoscrittoreatto(
			@PathVariable final Long id,
			@RequestBody final SottoscrittoreAtto sottoscrittoreAtto) throws CifraCatchedException{
		try{
			log.debug("REST request to sottoscrittoreatto id : {}", id);
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			sottoscrittoreAtto.setAtto( DomainUtil.minimalAtto(atto) );
			sottoScrittoreAttoService.save(atto, sottoscrittoreAtto);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}
		catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/

	@RequestMapping(value = "/attos/{id}/allegato/{idAllegato}/omissis", method = RequestMethod.POST,
			consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
			produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Void> omissis(
			@PathVariable final Long id,
			@PathVariable final  Long  idAllegato,
			@RequestParam(value="allegato",required=true) final String allegato,
			@RequestParam(value="file",required=true) final  MultipartFile file) throws GestattiCatchedException {
		try{
			log.debug("REST request to omissis id : {}", id);
			log.debug("REST request to omissis allegato : {}", allegato);
			log.debug("REST request to omissis idAllegato : {}", idAllegato);
			log.debug("REST request to omissis file : {}", file);
	
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			DocumentoInformatico  allegatoO = objectMapper.readValue(allegato, DocumentoInformatico.class);
	
			DocumentoInformatico doc = documentoInformaticoService.saveOmissis(atto, allegatoO, file);
			log.info("Salvato file Omissis: " + doc.getId());
			
			return new ResponseEntity<>( HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/attos/{id}/allegato/{idAllegato}/omissis/{idOmissis}", method = RequestMethod.DELETE)
	@Timed
	public @ResponseBody ResponseEntity<Void> deleteOmissis(
			@PathVariable final Long id,
			@PathVariable final Long idAllegato,
			@PathVariable final Long idOmissis) throws GestattiCatchedException {
		try{
			log.debug("REST request to delete omissis id : {}", id);
			log.debug("REST request to delete omissis idAllegato : {}", idAllegato);
			log.debug("REST request to delete omissis idOmissis : {}", idOmissis);
	
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			Set<DocumentoInformatico> allegati = atto.getAllegati();
			for (DocumentoInformatico allegato : allegati) {
				if(allegato.getId().equals(idAllegato)){
					log.info("Found the allegato with id " + allegato.getId());
					documentoInformaticoService.deleteOmissis(allegato.getId());
				}
			}
	
			return new ResponseEntity<>(HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	@RequestMapping(value = "/attos/{id}/allegato", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Set<DocumentoInformatico>> allegato(@PathVariable final Long id,
			@RequestParam("file") final MultipartFile[] files, 
			@RequestParam(value="data", required=false) String infoAggiuntiveStr) throws GestattiCatchedException{
		try{
			log.debug("REST request to allegato id : {}", id);
			
	  		Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			boolean isProposta = attoService.isProposta(atto.getId());
			
			if(!AllegatoAttoUtil.isEstensionePermessa(files)) {
				throw new AllegatoNonPermessoException("Estensione non consentita: l'allegato non e' stato caricato.");
			}
			if(AllegatoAttoUtil.isAFileZipWithZipInside(files)) {
				throw new AllegatoNonPermessoException("Allegato non consentito: assicurarsi che il file zip non contenga altri file zip al suo interno o che i file contenuti abbiano un'estensione consentita.");
			}
			Set<DocumentoInformatico> allegati = null;
			
			if(infoAggiuntiveStr!=null) {
				JsonParser parser = new JsonParser();
		  		JsonObject infoAggiuntive = parser.parse(infoAggiuntiveStr).getAsJsonObject();
		  		String tipoAllegato = null;
		  		Boolean pubblicabile = null;
		  		if(infoAggiuntive!=null && !infoAggiuntive.isJsonNull()) {
			  		try {
			  			if(infoAggiuntive.has("tipoAllegato") && infoAggiuntive.get("tipoAllegato")!=null && !infoAggiuntive.get("tipoAllegato").isJsonNull()) {
			  				tipoAllegato = infoAggiuntive.get("tipoAllegato").getAsString();
			  			}
			  			if(infoAggiuntive.has("pubblicabile") && infoAggiuntive.get("pubblicabile")!=null && !infoAggiuntive.get("pubblicabile").isJsonNull()) {
			  				pubblicabile = infoAggiuntive.get("pubblicabile").getAsBoolean();
			  			}
			  		}catch(Exception e) {};
		  		}
	  			allegati = documentoInformaticoService.save(atto, files, isProposta, tipoAllegato, pubblicabile);
			}else {
				allegati = documentoInformaticoService.save(atto, files, isProposta, null, null);
			}
			
			return new ResponseEntity<Set<DocumentoInformatico>>(allegati, HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/attos/{id}/allegati", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<List<DocumentoInformatico>> allegati(@PathVariable final Long id) throws GestattiCatchedException{
		try{
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			List<DocumentoInformatico> allegati = documentoInformaticoService.getAllegatiByAtto(atto.getId());
			return new ResponseEntity<List<DocumentoInformatico>>(allegati, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos -> get all the attos.
	 */
	@RequestMapping(value = "/attos/{id}/copia", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Atto> copia(
			@PathVariable final Long id,
			@RequestParam(value = "tipoCopia", required = true) final String tipoCopia) throws GestattiCatchedException{
		try{
			log.debug("REST request copia : {}", id);
			log.debug("REST request tipoCopia : {}", tipoCopia);
				Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			Atto nuovoAtto  = attoService.copia( atto ,tipoCopia);
			return new ResponseEntity<Atto >(nuovoAtto,	HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * POST /attos -> Create a new atto.
	 * @throws DocumentException
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> create(
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@RequestBody Atto atto)
			throws GestattiCatchedException {
		try{
			log.debug("REST request to save Atto : {}", atto);
			JsonObject json = new JsonObject();
			if (atto.getId() != null && atto.getId()>0) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			TipoAtto tipoAtto = tipoAttoService.findOneSimple(atto.getTipoAtto().getId());
			if(tipoAtto==null || tipoAtto.getEnabled()==null || !tipoAtto.getEnabled()) {
				throw new GestattiCatchedException("Tipo atto non abilitato");
			}
			
			Profilo p = profiloService.findOneBase(profiloId);
			if(p.getFutureEnabled()==null || p.getFutureEnabled().equals(false)) {
				throw new GestattiCatchedException("Impossibile istruire un nuovo atto in quanto il profilo risulta disabilitato");
			}
			
			atto = attoService.save(atto, profiloId, null, null, null);
			log.info("Atto Salvato. Id: " + atto.getId().longValue());
			log.debug("atto:"+ atto);
			
			// Integrazione con BPMN per avvio processo
			String idIstanzaProcesso = null;
			
			try {
				idIstanzaProcesso = workflowService.avviaProcessoBpm(atto, profiloId);
			}
			catch (Exception e) {
				attoService.deleteOnCreate(atto, profiloId);
				throw new GestattiCatchedException(e);
			}
			
			// Ricarico le info con il Codice Atto
			atto = attoRepository.findOne(atto.getId().longValue());
			atto.setProcessoBpmId(idIstanzaProcesso);
			
			//CDFATTICOMEV-3
			if(!atto.getTipoIter().getCodice().equalsIgnoreCase("SENZA_VERIFICA_CONTABILE")) {
				List<SezioneTipoAttoDto> listSezionetipoAttoDto = sezioneTipoAttoService.findByTipoAtto(atto.getTipoAtto().getId());
				if(listSezionetipoAttoDto!=null && listSezionetipoAttoDto.size() > 0) {
					for(SezioneTipoAttoDto sezione : listSezionetipoAttoDto) {
						if(sezione!=null && sezione.getVisibile() && sezione.getCodice().equalsIgnoreCase("dichiarazioni")) {
							atto.setContabileOggetto(atto.getOggetto());
						}
					}
				}
			}
			
			atto = attoRepository.save(atto);	
			
			String nextTaskId = workflowService.getMyNextTaskId(atto.getId(), profiloId);
			
			json.addProperty("taskId", nextTaskId);
			json.addProperty("id", atto.getId());
			
			
			return new ResponseEntity<>(json.toString(), HttpStatus.CREATED);
		}
		catch(Exception e){
			throw new GestattiEsecuzioneLavorazioneProcessoException(e, "Crea Proposta");
    	}
	}

	/**
	 * POST /attos -> Updates an existing atto.
	 * @throws DocumentException
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> update(
			@RequestParam(value="taskId" ) final String taskId,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@RequestHeader(value="taskBpmId" ,required=false) final String taskBpmId,
			@RequestHeader(value="sedutaId" ,required=false) final Long sedutaId,
			@RequestBody final AttoWorkflowDTO dto)
			throws GestattiCatchedException {
		DecisioneWorkflowDTO decisione = null;
		try{
			log.debug("REST request to update dto : {}", dto);
			
			JsonObject json = new JsonObject();
			if (dto.getAtto().getId() == null) {
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
				
			decisione = dto.getDecisione();
			if (decisione == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			if(dto.getAtto()!= null && dto.getAtto().getDomanda() != null && dto.getAtto().getDomanda().getTesto() != null &&
					dto.getAtto().getDomanda().getTesto().trim().replaceAll("&nbsp;", "").replaceAll(" ", "").isEmpty()) {
				dto.getAtto().getDomanda().setTesto("");
			}
			
			boolean checkTesto = false;
            try {
            	checkTesto = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_TESTO_CHECK_NULL, "false"));
            }catch(Exception e) {
            	log.error("Errore nel parsing della property checkTesto");
            }
            
            boolean isRitiro = decisione.getCodiceAzioneUtente()!=null && decisione.getCodiceAzioneUtente().equalsIgnoreCase("RITIRO_FASE_ISTRUTTORIA");
           
            if(checkTesto && !isRitiro) {
               if(decisione.isCompletaTask() || taskBpmId == null|| StringUtil.isNull(taskBpmId)) {
            	   Atto atto = attoService.findOne(dto.getAtto().getId());
            	   List<SezioneTipoAtto> listSezionetipoAttoDto = sezioneTipoAttoService.findSezioniTipoAttoByTipoAtto(atto.getTipoAtto().getId());
            	   if(listSezionetipoAttoDto != null) {
            		   for (SezioneTipoAtto sezioneTipoAtto : listSezionetipoAttoDto) {
						if(sezioneTipoAtto.isVisibile() && sezioneTipoAtto.getSezione() != null &&sezioneTipoAtto.getSezione().getCodice()!= null &&
								sezioneTipoAtto.getSezione().getCodice().equalsIgnoreCase("domanda")) {
							if(dto.getAtto() != null && (dto.getAtto().getDomanda() == null || StringUtil.isNull(dto.getAtto().getDomanda().getTesto()))) {
								throw new GestattiCatchedException("Testo dell'atto non presente");
							}
						}
					}
            	   }
               }
            }
            
            
            if(Lists.newArrayList("DG", "DPC", "DC").contains(dto.getAtto().getTipoAtto().getCodice()) && dto.getAtto().getDataAdozione()!=null && dto.getAtto().getNumeroAdozione()!=null && dto.getAtto().getIe()!=null) {
            	Atto attoDB = attoService.findOne(dto.getAtto().getId());
            	Atto attoDto = dto.getAtto();
            	if((attoDB.getIe()==null) || (attoDB.getIe().booleanValue()!= attoDto.getIe().booleanValue())){
            		if(contabilitaService.esisteBozzaAtto(attoDto.getTipoAtto().getCodice(),  attoDto.getNumeroAdozione(),  attoDto.getDataAdozione().getYear(), false)) {
						Date dataEsecutivita = attoDto.getIe()!=null && attoDto.getIe().booleanValue()?attoDto.getDataAdozione().toDate():null;
						contabilitaService.dataEsecutivitaAtto(
								attoDto.getTipoAtto().getCodice(), 
								attoDto.getNumeroAdozione(), 
								attoDto.getDataAdozione().getYear(), 
								dataEsecutivita);
						if(attoDto.getIe()!=null && attoDto.getIe().booleanValue()) {
							dto.getAtto().setDataEsecutivita(dto.getAtto().getDataAdozione());
						}else {
							dto.getAtto().setDataEsecutivita(null);
						}
					}
            	}
            }
			
			workflowService.verifichePreliminariPreCompleteTask(taskId, profiloId, taskBpmId, sedutaId, dto);
			
			if(!Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_RETRIEVAL_ACCESS_CONTROL_ENABLED)) || attoRetrievalSavingAccessControl.canSaveAtto(dto.getAtto().getId(), profiloId, taskBpmId, sedutaId)) {
				log.debug("richiamo il save dell'atto {}", dto.getAtto().getId());
				
				//eventuale delega
				if(taskBpmId!=null && !taskBpmId.trim().isEmpty()) {
					String profDelegante = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
					if (!StringUtil.isNull(profDelegante)) {
						Profilo profiloDelegante = bpmWrapperUtil.getProfiloByUsernameBpm(profDelegante);
						if (profiloDelegante != null) {
							BpmThreadLocalUtil.setProfiloDeleganteId(profiloDelegante.getId());
						}
					}
				}
				
				//eventuale riassegnazione
				if(taskBpmId!=null && !taskBpmId.trim().isEmpty()) {
					String profOriginarioStr = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
					if (!StringUtil.isNull(profOriginarioStr)) {
						Profilo profOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioStr);
						if (profOriginario != null) {
							BpmThreadLocalUtil.setProfiloOriginarioId(profOriginario.getId());
						}
					}
				}
				if(dto.getAtto()!=null && dto.getAtto().getDataInizioPubblicazionePresunta()!=null) {
					if(dto.getDecisione().isCompletaTask() && (dto.getAtto().getDurataGiorni()==null || dto.getAtto().getDurataGiorni() < 5)) {
						throw new GestattiCatchedException("Occorre inserire una durata di pubblicazione, che sia di almeno 5 giorni.");
					}
					LocalDate endDate = FestiviUtils.aggiungiGiorniConUltimoNonLavorativo(dto.getAtto().getDataInizioPubblicazionePresunta().toDate(), dto.getAtto().getDurataGiorni() - 1);
					if(dto.getAtto().getDataFinePubblicazionePresunta()==null || !dto.getAtto().getDataFinePubblicazionePresunta().equals(endDate)) {
						dto.getAtto().setDataFinePubblicazionePresunta(endDate);
					}
				}
				attoService.save(dto.getAtto(), profiloId, taskId, dto, decisione);
				
				if(decisione!=null && decisione.getCodiceAzioneUtente()!=null && decisione.getCodiceAzioneUtente().equalsIgnoreCase("AGGIORNA_TRASPARENZA")) {
					jobTrasparenzaService.richiediNuovaPubblicazione(dto.getAtto().getId());
					
					eventoService.saveEvento(EventoEnum.EVENTO_ATTESA_PUBBLICAZIONE_TRASPARENZA.getDescrizione(), dto.getAtto());
					
					registrazioneAvanzamentoService.registraAvanzamento(dto.getAtto().getId(), profiloId, decisione.getDescrizioneAlternativa() != null && !decisione.getDescrizioneAlternativa().trim().isEmpty() ? decisione.getDescrizioneAlternativa() : decisione.getDescrizione() , null);
				}
				
				String nextTaskId = workflowService.getMyNextTaskId(dto.getAtto().getId(), profiloId);
				
				json.addProperty("taskId", nextTaskId);
				json.addProperty("id", dto.getAtto().getId());
				
				return new ResponseEntity<>(json.toString(), HttpStatus.OK);
			}else {
				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
			}
		}
		catch (FirmaRemotaException e) {
			if(e.getErrorCode()!=null) {
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(e.getErrorCode(), e.getMessage()),	HttpStatus.BAD_REQUEST);
			}else {
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC), HttpStatus.BAD_REQUEST);
			}
		}
		catch (GeneraFirmaDocumentoException e) {
			log.error("GeneraFirmaDocumento: Errore:"+ e.getMessage());
			if(e.getMessage()!=null && !e.getMessage().isEmpty()) {
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(
						FdrWsUtil.ERR_CODE_GENERIC, e.getMessage()), HttpStatus.BAD_REQUEST);
			}
			else {
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(FdrWsUtil.ERR_CODE_GENERIC,
						"Errore in fase di generazione e firma del documento. Ricaricare la pagina ed effettuare un nuovo tenativo."), HttpStatus.BAD_REQUEST);
			}
		}
		catch(Exception e){
			e.printStackTrace();
			if (e instanceof GestattiCatchedException) {
				throw (GestattiCatchedException)e;
			}
			else if(e instanceof ContabilitaServiceException || (e.getCause()!=null && e.getCause() instanceof ContabilitaServiceException)) {
				String baseError = "Errore in fase di interazione con il sistema contabile.\u003Cbr\u003EIn dettaglio: ";
				Throwable lastCause = ExceptionUtil.getLastExceptionCause(e);
				if(lastCause!=null) {
					String message = lastCause.getMessage();
					if(message!=null && !message.trim().isEmpty()) {
						if(message.contains("message: ") && message.split("message: ").length > 1) {
							throw new GestattiCatchedException(e, baseError + message.split("message: ")[1]);
						}else {
							throw new GestattiCatchedException(e, baseError + lastCause.getMessage());
						}
					}else {
						throw new GestattiCatchedException(e, baseError + lastCause.getMessage());
					}
				}else {
					throw new GestattiCatchedException(e, baseError + e.getMessage());
				}
			}
			
			String exceptionAsString = "";
			try {
				StringWriter sw = new StringWriter();
	            e.printStackTrace(new PrintWriter(sw));
	            exceptionAsString = sw.toString();
			}catch(Exception ex) {}
			
            if(exceptionAsString.contains("FNIMV_FNPRO_FK")) {
            	throw new GestattiCatchedException("Attenzione. Occorre rimuovere i movimenti da JEnte prima di poter ritirare l'atto.");
            }else if(decisione!=null && decisione.getDescrizione()!=null && !decisione.getDescrizione().isEmpty()){
				throw new GestattiEsecuzioneLavorazioneProcessoException(e, decisione.getDescrizione());
			}
			else{
				throw new GestattiCatchedException(e);
			}
    	}
	}

	/**
	 * GET /attos -> get all the attos.
	 */
	@RequestMapping(value = "/attos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Atto>> getAll(
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			Page<Atto> page = attoService.findAll(PaginationUtil
					.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, "codiceCifra")));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/attos", offset, limit);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers,
					HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/attos/xls", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Timed
	public ResponseEntity<FileSystemResource> searchXls(
			@RequestParam final String attoCriteriaStr)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to xls search Attos");
			ObjectMapper mapper = new ObjectMapper();
			AttoCriteriaDTO criteria = mapper.readValue(attoCriteriaStr, AttoCriteriaDTO.class);
			List atti = null;
			
			criteria.setType("xls");
			
			Object search = search(criteria, 0, Integer.MAX_VALUE, false);
			if(search != null && search instanceof Page){
				atti = ((Page<?>)search).getContent();
			}
			
			
			java.io.File file = excelService.createExcel(atti, criteria.getColnames());
			file.deleteOnExit();
			return DownloadFileUtil.responseStream(file, "atti.xls");
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /report -> Create search pdf report.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/reportpdf/{idModelloHtml}/{criteriaStr}/endSearch", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<FileSystemResource> report(@PathVariable("criteriaStr") String criteriaStr,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			AttoCriteriaDTO criteria = null;
			List atti = null;
			Map<String, String> criteriMap = new HashMap<String, String>();
			criteriaStr = criteriaStr.replaceAll("slash", "/");
			if(!criteriaStr.equalsIgnoreCase("test")){
				criteria = mapper.readValue(criteriaStr, AttoCriteriaDTO.class);
				criteria.setType("report");
				Properties prop = new Properties();
				ClassPathResource res = new ClassPathResource("xls.properties");
				prop.load(res.getInputStream());
				criteriMap = reportPdfCriteriTrasformer.generateMap(criteria, prop);
				
				Object search = search(criteria, 0, Integer.MAX_VALUE, false);
				if(search != null && search instanceof Page){
					atti = ((Page<?>)search).getContent();
				}
			}
			else{
				criteriaStr = "{\"tipiAttoIds\":[1,2,3,4,5],\"viewtype\":\"determine\",\"colnames\":[\"1-codiceCifra\",\"2-dataCreazione\",\"3-oggetto\",\"4-tipoIter\",\"5-stato\"]}";
				criteria = mapper.readValue(criteriaStr, AttoCriteriaDTO.class);
				criteria.setType("report");
				Object search = search(criteria, 0, 5, false);
				if(search != null && search instanceof Page){
					atti = ((Page<?>)search).getContent();
				}
			}
			
			Utente user = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put("nome", user.getNome());
			userMap.put("cognome", user.getCognome());
			List<Aoo> aoos = null;
			if(criteria.getAooIdGroup()!=null && criteria.getAooIdGroup().longValue() > 0) {
				aoos = aooService.findGerarchiaOfAoo(criteria.getAooIdGroup());
			}else {
				aoos = aooService.findGerarchiaAooByProfiloId(criteria.getProfiloId());
			}
			java.io.File file = reportService.executeReportRicerca(idModelloHtml, criteria.getColnames(), atti, userMap, aoos, criteriMap, reportPdfCriteriTrasformer.toPdfDto(criteria));
			return DownloadFileUtil.responseStream(file, "report.pdf");
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/xml", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Timed
	public ResponseEntity<FileSystemResource> searchXml(
			@RequestParam final String attoCriteriaStr,
			HttpServletRequest request)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to xml search Attos");
			ObjectMapper mapper = new ObjectMapper();
			AttoCriteriaDTO criteria = mapper.readValue(attoCriteriaStr, AttoCriteriaDTO.class);
			criteria.setType("xml");
			List atti = null;
			
			Object search = search(criteria, 0, Integer.MAX_VALUE, false);
			if(search != null && search instanceof Page){
				atti = ((Page<?>)search).getContent();
			}
			
			java.io.File file = xmlBuilderService.creaXml(atti);
			file.deleteOnExit();
			return DownloadFileUtil.responseStream(file, "atti.xml");
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/attos/xml/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Timed
	public ResponseEntity<FileSystemResource> oneXml(
			@PathVariable final Long id,
			HttpServletRequest request)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to xml search Attos");
			java.io.File file = xmlBuilderService.creaXml(attoService.findOne(id));
			file.deleteOnExit();
			return DownloadFileUtil.responseStream(file, "atto_"+id+".xml");
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /attos -> get all the attos.
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/avcanac", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
	@Timed
	public ResponseEntity<FileSystemResource> reportXlsAVCP_ANAC(
			@RequestParam final String attoCriteriaStr)
					throws GestattiCatchedException {
		try{
			log.debug("GET request to xls search Attos");
			AttoCriteriaDTO criteria = new Gson().fromJson(attoCriteriaStr, AttoCriteriaDTO.class);
			criteria.setType("avcanac");
			List atti = null;
			List<Atto> attiWithCig = new ArrayList<Atto>();
			
			Object search = search(criteria, 0, Integer.MAX_VALUE, false);
			if(search != null && search instanceof Page){
				atti = ((Page<?>)search).getContent();
			}
			
			for(Object atto: atti) {
				if(((Atto)atto).getCodiceCig() != null && !((Atto)atto).getCodiceCig().isEmpty()) {
					attiWithCig.add((Atto)atto);					
				}
			}
			
			java.io.File file = excelService.createExcel(attiWithCig, criteria.getColnames());
			file.deleteOnExit();
			return DownloadFileUtil.responseStream(file, "report_AVCPANAC.xls");
		}
		catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}

	/**
	 * GET /attos -> get all the attos.
	 */
	@RequestMapping(value = "/attos/search", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> getAll(
			@Valid @RequestBody final AttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestHeader(value="profiloId" ,required=false) final Long profiloId)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Attos : {}", criteria);
			if(criteria!=null && criteria.getProfiloId()==null && profiloId!=null) {
				criteria.setProfiloId(profiloId);
			}
			
			Object search = search(criteria, offset, limit, true);
			Object result = null;
			Page<?> page = null;
			if(search != null && search instanceof Page){
				page = ((Page<?>)search);
				result = page.getContent();
			}
			 
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/attos/search", offset, limit);
			return new ResponseEntity<>(result, headers,
					HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos -> get all the attos.
	 */
	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/attos/searchGrouped", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<?> searchGruped(
			@Valid @RequestBody final AttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestHeader(value="profiloId" ,required=false) final Long profiloId)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Attos : {}", criteria);
			if(criteria!=null && criteria.getProfiloId()==null && profiloId!=null) {
				criteria.setProfiloId(profiloId);
			}
			
			Object search = search(criteria, offset, limit, true);
			Page<?> page = null;
			
			if(search instanceof Map && ((Map<?,?>) search).get("attos") != null && ((Map<?,?>) search).get("attos") instanceof Page) {
				page = ((Page<?>)((Map<?,?>) search).get("attos"));
				((Map) search).put("attos", page.getContent());
			}
			 
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/attos/searchGruped", offset, limit);
			return new ResponseEntity<>(search, headers,
					HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	private Object search(AttoCriteriaDTO criteria, Integer offset, Integer limit, boolean dtoResult) throws RuntimeException, DatatypeConfigurationException{
		String ordinamento = "codiceCifra";
		if(!StringUtil.isNull(ordinamento)){
			ordinamento = criteria.getOrdinamento();
		}
		
		if(StringUtil.isNull(criteria.getTipoOrinamento())){
			criteria.setTipoOrinamento("desc");
		}
		
		Pageable pageable = PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.ASC, ordinamento));
		if(criteria.getTipoOrinamento().equalsIgnoreCase("desc")) {
			pageable = PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, ordinamento));
		}
		
		log.debug("viewtype: "+criteria.getViewtype());
		if(criteria.getViewtype().equals("elencopersedute")){
			log.debug("sono in elencopersedute");
			
			criteria.setAooId(null);
			ordinamento = "sedutaGiunta.primaConvocazioneInizio";
			
			List<Sort.Order> orders = new ArrayList<Sort.Order>();
			orders.add(new Order(Sort.Direction.DESC, "sedutaGiunta.primaConvocazioneInizio"));
			orders.add(new Order(Sort.Direction.ASC, "esito"));
			orders.add(new Order(Sort.Direction.DESC, "numeroAdozione"));
		}
		
		Object result = attoService.findAll(pageable, criteria, dtoResult);
		
		return result;
	}

	/**
	 * GET /attos -> get all the attos.
	 */
	@RequestMapping(value = "/attos/searchlibera", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Atto>> getAllLibera(
			@Valid @RequestBody final RicercaLiberaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to getAllLibera Attos : {}", criteria);
			String ordinamento = "codiceCifra";
			if(criteria.getOrdinamento()!=null && !"".equals(criteria.getOrdinamento().trim())){
				ordinamento = criteria.getOrdinamento();
				String[] splitOrd = ordinamento.split("\\.");
				String[] newArray = Arrays.copyOfRange(splitOrd, 1, splitOrd.length);
				ordinamento = StringUtils.join(newArray, ".");
			}
			log.debug("POST request to getAllLibera Attos ordinamento: {}", ordinamento);
			log.debug("POST request to getAllLibera Attos offset: {}", offset);
			log.debug("POST request to getAllLibera Attos limit: {}", limit);
			Page<Atto> page = null;
	
			if(criteria.getTipoOrdinamento().equalsIgnoreCase("desc")){
				page= attoService.findAllLibera(PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, ordinamento)), criteria);
				log.debug("POST request to getAllLibera Attos Desc order: {}", page);
			}else{
				page = attoService.findAllLibera(PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.ASC, ordinamento)), criteria);
				log.debug("POST request to getAllLibera Attos Asc order: {}", page);
			}
	
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/attos/searchlibera", offset, limit);
			
			log.debug("POST request to getAllLibera Attos Headers: {}", headers);
			return new ResponseEntity<List<Atto>>(page.getContent(), headers,
					HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}


	/**
	 * GET /attos/:id -> get the "id" atto.
	 */
	@RequestMapping(value = "/attos/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Atto> get(@PathVariable final Long id,
			final HttpServletResponse response,
			final HttpServletRequest request,
			@RequestHeader(value="profiloId" ,required=false) final Long profiloId,
			@RequestHeader(value="taskBpmId" ,required=false) final String taskBpmId,
			@RequestHeader(value="sedutaId" ,required=false) final Long sedutaId,
			@RequestParam(value = "securitykey", required = false) final String securitykey
//			@RequestHeader(value="securitykey" ,required=false) final String securitykey
			) throws GestattiCatchedException{
		try{
//			if(!Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_RETRIEVAL_ACCESS_CONTROL_ENABLED)) || attoRetrievalSavingAccessControl.canReadAtto(id, profiloId, taskBpmId, sedutaId)) {
				log.debug("REST request to get Atto : {}", id);
				Atto atto = attoService.findOne(id);
				if (atto == null) {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}else {
					if(taskBpmId!=null) {
						String highLightedInfo = workflowService.getHighLightedInfo(taskBpmId, atto);
						if(highLightedInfo!=null && !highLightedInfo.isEmpty()) {
							atto.setHighLightedInfo(highLightedInfo);
						}else {
							atto.setHighLightedInfo(null);
						}
						atto.setFullAccess(true);
					}else {
						atto.setFullAccess(attoService.hasAccessGrant(profiloId, id, true));
					}
				}
				atto.getTipoAtto().setFasiRicerca(null);
				this.aggiungiImpronte(atto);
				
				return new ResponseEntity<>(atto, HttpStatus.OK);
//			}else {
//				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//			}
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	
	/**
	 * GET /attos/:id -> get the "id" atto.
	 */
	@RequestMapping(value = "/attos/{id}/getAttoConTaskAttivi", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Atto> getAttoConTaskAttivi(@PathVariable final Long id,
			final HttpServletResponse response,
			final HttpServletRequest request,
			@RequestHeader(value="profiloId" ,required=false) final Long profiloId,
			@RequestHeader(value="taskBpmId" ,required=false) final String taskBpmId,
			@RequestHeader(value="sedutaId" ,required=false) final Long sedutaId,
			@RequestParam(value = "securitykey", required = false) final String securitykey
//			@RequestHeader(value="securitykey" ,required=false) final String securitykey
			) throws GestattiCatchedException{
		try{
//			if(!Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.ATTO_RETRIEVAL_ACCESS_CONTROL_ENABLED)) || attoRetrievalSavingAccessControl.canReadAtto(id, profiloId, taskBpmId, sedutaId)) {
				log.debug("REST request to get Atto : {}", id);
				Atto atto = attoService.findOne(id);
				if (atto == null) {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}else {
					if(taskBpmId!=null) {
						String highLightedInfo = workflowService.getHighLightedInfo(taskBpmId, atto);
						if(highLightedInfo!=null && !highLightedInfo.isEmpty()) {
							atto.setHighLightedInfo(highLightedInfo);
						}else {
							atto.setHighLightedInfo(null);
						}
						atto.setFullAccess(true);
					}else {
						atto.setFullAccess(attoService.hasAccessGrant(profiloId, id, true));
					}
					workflowService.loadAllTasks(atto);
				}
				atto.getTipoAtto().setFasiRicerca(null);
				this.aggiungiImpronte(atto);
				
				return new ResponseEntity<>(atto, HttpStatus.OK);
//			}else {
//				return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//			}
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	private void aggiungiImpronte(Atto atto) {
		boolean generaImprontaBozza = true;
		try {
			generaImprontaBozza = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.GENERAZIONE_IMPRONTA_BOZZA_ENABLED, "true"));
		}catch(Exception e) {
			generaImprontaBozza = true;
		}
		if(generaImprontaBozza){
			atto.setImprontaBozza(FileChecksum.calcolaImprontaBozza(atto.getCodiceCifra(), JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore." + atto.getTipoAtto().getCodice())));
		}
		
		boolean generaImprontaAtto = false;
		try {
			generaImprontaAtto = Boolean.parseBoolean(WebApplicationProps.getProperty(ConfigPropNames.GENERAZIONE_IMPRONTA_ATTO_ENABLED, "false"));
		}catch(Exception e) {
			generaImprontaAtto = false;
		}
		if(generaImprontaAtto){
			atto.setImprontaAtto(FileChecksum.calcolaImprontaAtto(atto.getNumeroAdozione(), atto.getDataAdozione(), JenteProps.getProperty("contabilita.comunefirenze.jente.mapping.organosettore." + atto.getTipoAtto().getCodice())));
		}
	}

	/**
	 * GET /attos/:id -> get the "id" atto.
	 * @throws DocumentException
	 * @throws IOException
	 */
	/* 
	 * IN ATTICO L'INTEGRAZIONE CON MOTORE WORKFLOW GESTITA DIVERSAMENTE
	 *
	@RequestMapping(value = "/attos/{id}/preworkflow", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<AttoWorkflowDTO> get(@PathVariable final Long id,
			@RequestParam(value="codiceDecisione") final String codiceDecisione,
			@RequestParam(value="taskId" ,required=false) final String taskId,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId ) throws CifraCatchedException {
		try{
			log.debug("REST request to get Atto : {}", id);
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			DecisioneWorkflowDTO decisione = null; //TODO integrazione workflowService.getDecisioneByTaskIdAndCodice(profiloId, taskId, codiceDecisione);
	
			if (decisione == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			AttoWorkflowDTO dto  = null;//TODO integrazione workflowService.preExecute(decisione, atto, taskId, profiloId);
			return new ResponseEntity<>(dto, HttpStatus.OK);
		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	*/

	/**
	 * GET /attos/states -> get distinct states
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos/stati", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> caricaStati(@RequestParam(value="tipoAtto", required=false) final String tipoAtto) throws GestattiCatchedException {
		try{
			log.debug("REST request to get distinct Atto.stato");
			List<String> states = null;
			if(tipoAtto!=null){
				states = attoService.findAllStates(tipoAtto);
			}else{
				states = attoService.findAllDistinctStato();
			}
			return new ResponseEntity<>(states!=null ? states : new ArrayList<String>(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@RequestMapping(value = "/attos/{id}/firma", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<FirmaRemotaDTO> firmaDocumenti(
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@RequestHeader(value="taskBpmId" ,required=true) final String taskBpmId,
			@PathVariable final Long id, 
			@Valid @RequestBody FirmaRemotaDTO dto)  throws IOException, GestattiCatchedException {
		
		try{
			log.debug("REST request firma : {}", id);
			Atto atto = attoRepository.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			//eventuale delega
			String profDelegante = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
			if (!StringUtil.isNull(profDelegante)) {
				Profilo profiloDelegante = bpmWrapperUtil.getProfiloByUsernameBpm(profDelegante);
				if (profiloDelegante != null) {
					BpmThreadLocalUtil.setProfiloDeleganteId(profiloDelegante.getId());
				}
			}
			
			//eventuale riassegnazione
			String profOriginarioStr = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
			if (!StringUtil.isNull(profOriginarioStr)) {
				Profilo profOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioStr);
				if (profOriginario != null) {
					BpmThreadLocalUtil.setProfiloOriginarioId(profOriginario.getId());
				}
			}
			
			List<File> pdfDaFirmare = new ArrayList<File>();
			List<byte[]> filesDaFirmare = new ArrayList<byte[]>();
			List<TipoDocumento> tipiDocumento = new ArrayList<TipoDocumento>();
			
			List<DocumentoPdf> docFirma = new ArrayList<DocumentoPdf>();
			if(dto.getFilesId()!=null){
				for (int i = 0; i < dto.getFilesId().size(); i++) {
					Long idDocumentoPdf = new Long(dto.getFilesId().get(i));
					DocumentoPdf documentoPdfDaFirmare = documentoPdfService.findById(idDocumentoPdf);
					docFirma.add(documentoPdfDaFirmare);
				}
			}
				
			if (docFirma == null || docFirma.size() < 1) {
				docFirma = atto.getDocumentiPdfAdozione();
			}
			
			if (docFirma == null || docFirma.size() < 1) {
				docFirma = atto.getDocumentiPdfAdozioneOmissis();
			}
			
			for (DocumentoPdf documentoPdf : docFirma) {
				File pdf = documentoPdf.getFile();
				if (pdf == null) {
					return new ResponseEntity<>(HttpStatus.NOT_FOUND);
				}
				pdfDaFirmare.add(pdf);
				filesDaFirmare.add(dmsService.getContent(pdf.getCmisObjectId()));
				tipiDocumento.add(documentoPdf.getTipoDocumento());
			}
			
			try {
				attoService.firmaSalvaDocumenti(dto, atto, profiloId, pdfDaFirmare, filesDaFirmare, tipiDocumento);
			}
			catch (FirmaRemotaException e) {
				return new ResponseEntity<FirmaRemotaDTO>(
						new FirmaRemotaDTO(e.getErrorCode(), e.getMessage()),	HttpStatus.BAD_REQUEST);
			}
			catch (Exception e) {
				log.error("FirmaRemotaService: WS firmaPades: Errore Generico:"+ e.getMessage());
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(
						FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC), HttpStatus.BAD_REQUEST);
			}
			
			return new ResponseEntity<FirmaRemotaDTO>(dto,	HttpStatus.OK);
			
		}catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}
	
	@RequestMapping(value = "/attos/{id}/sendOTP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<FirmaRemotaDTO> sendOTP(
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@PathVariable final Long id, 
			@Valid @RequestBody FirmaRemotaDTO dto)  throws IOException, GestattiCatchedException {
		
		log.debug("REST request OTP");
		try {
			FirmaRemotaService.sendCredential(dto.getCodiceFiscale(), dto.getPassword());
		}
		catch (FirmaRemotaException e) {
			return new ResponseEntity<FirmaRemotaDTO>(
					new FirmaRemotaDTO(e.getErrorCode(), e.getMessage()), HttpStatus.BAD_REQUEST);
		}
		catch (Exception e) {
			log.error("FirmaRemotaService: Test call ws firmaPades: Errore Generico:"+ e.getMessage());
			return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(
					FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC), HttpStatus.BAD_REQUEST);
		}
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
	/*
	 * NON DOVREBBE ESSERE UTILIZZATO IN ATTICO
	 *
	@RequestMapping(value = "/attos/{id}/generadocumentifirmati", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<FirmaRemotaDTO> generaDocumentiFirmati(
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@PathVariable final Long id, @Valid @RequestBody FirmaRemotaDTO dto)  
					throws IOException, CifraCatchedException {
			
		log.debug("REST request firma : {}", id);

		try {
			Atto atto = attoService.findOne(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			try {
				synchronized (AttoResource.class) {
					doGeneraFirmaDocumentiLocked(atto, profiloId, dto);	
					return new ResponseEntity<FirmaRemotaDTO>(dto, HttpStatus.OK);
				}
			}
			catch (Exception e) {
				return new ResponseEntity<FirmaRemotaDTO>(new FirmaRemotaDTO(e.getMessage()),	HttpStatus.BAD_REQUEST);
			}
		}
		catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
	}
	
	
	private void doGeneraFirmaDocumentiLocked(
			Atto atto, long profiloId, FirmaRemotaDTO dto) throws Exception {
		
		//genera numero adozione
//		LocalDate dataAdozione = new LocalDate();
//		String numeroAdozione = codiceProgressivoService.generaCodiceCifraAdozione( atto.getAoo(), dataAdozione.getYear() , atto.getTipoAtto() );
//		atto.setNumeroAdozione(  numeroAdozione);
//		atto.setDataAdozione( dataAdozione );
//		
//		DocumentoPdf attoAdozione = null;
//		DocumentoPdf attoAdozioneOmissis = null;
//		DocumentoPdf schedaAnagraficoContabile = null;
		
		List<File> pdfDaFirmare = new ArrayList<File>();
		List<byte[]> filesDaFirmare = new ArrayList<byte[]>();
		List<TipoDocumento> tipiDocumento = new ArrayList<TipoDocumento>();
		try {
			
			if(dto.getFilesId()!=null){
				for (int i = 0; i < dto.getFilesId().size(); i++) {
					Long idDocumentoPdf = new Long(dto.getFilesId().get(i));
					DocumentoPdf documentoPdfDaFirmare = documentoPdfService.findById(idDocumentoPdf);
					if(documentoPdfDaFirmare!=null && documentoPdfDaFirmare.getFile()!=null){
						pdfDaFirmare.add(documentoPdfDaFirmare.getFile());
						filesDaFirmare.add(dmsService.getContent(documentoPdfDaFirmare.getFile().getCmisObjectId()));
						tipiDocumento.add(documentoPdfDaFirmare.getTipoDocumento());
					}
				}
			}
			
			
//			//genera documenti
//			Long modelloHtmlId = dto.getModelloHtmlId();
//			{
//				ReportDTO reportDto = new ReportDTO();
//				reportDto.setIdAtto(atto.getId());
//				reportDto.setIdModelloHtml(modelloHtmlId);
//				reportDto.setOmissis(false);
//				java.io.File result = reportService.previewAtto(atto, reportDto);
//				attoAdozione = documentoPdfService.saveAttoAdozionePdf(atto, result);
//				documenti.add(attoAdozione);
//				pdfDaFirmare.add(attoAdozione.getFile());
//				filesDaFirmare.add(dmsService.getContent(attoAdozione.getFile().getCmisObjectId()));
//			}
//
//			if((atto.getRiservato()==null || atto.getRiservato()==false) && atto.getPubblicazioneIntegrale()!=null && atto.getPubblicazioneIntegrale() == false){
//				ReportDTO reportDto = new ReportDTO();
//				reportDto.setIdAtto(atto.getId());
//				reportDto.setIdModelloHtml(modelloHtmlId);
//				reportDto.setOmissis(true);
//				java.io.File result = reportService.previewAtto(atto, reportDto);
//				attoAdozioneOmissis = documentoPdfService.saveAttoAdozioneOmissisPdf(atto, result);
//				documenti.add(attoAdozioneOmissis);
//				pdfDaFirmare.add(attoAdozioneOmissis.getFile());
//				filesDaFirmare.add(dmsService.getContent(attoAdozioneOmissis.getFile().getCmisObjectId()));
//			}
//			
//			//SE ATTO NON E' DIRETTAMENTE ESECUTIVO GENERO SCHEDA ANAGRAFICO CONTABILE
//			if(  atto.getTipoIter()!=null && (!atto.getTipoIter().getCodice().equals("SENZA_VERIFICA_CONTABILE") || 
//				(atto.getTipoAdempimento() != null && atto.getTipoAdempimento().isGenerazioneSchedaAnagraficoContabile()))){
//				
//				Long modelloHtmlIdSchedaAnagraficoContabile = dto.getModelloHtmlIdSchedaAnagraficoContabile();
//				ReportDTO reportDto = new ReportDTO();
//				reportDto.setTipoDoc("scheda_anagrafico_contabile");
//				reportDto.setIdModelloHtml(modelloHtmlIdSchedaAnagraficoContabile);
//				java.io.File pdf = reportService.previewSchedaAnagraficoContabile(reportDto, atto);
//				if(pdf!=null && pdf.length() > 0L){
//					schedaAnagraficoContabile = documentoPdfService.saveSchedaAnagraficoContabile(atto, pdf, false, null, SecurityUtils.getCurrentLogin(), false);
//					documenti.add(schedaAnagraficoContabile);
//					pdfDaFirmare.add(schedaAnagraficoContabile.getFile());
//					filesDaFirmare.add(dmsService.getContent(schedaAnagraficoContabile.getFile().getCmisObjectId()));
//				}
//			}
			
		} catch (Exception e) {
			//rollback documenti non firmati e il num.adozione	
			String context = "Errore Generico in fase di creazione e salvataggio documenti";
			manageException(atto, null, null, context, e);
			throw new Exception(context);
		}
		
		try {
			dto = firmaSalvaDocumenti(dto, atto, profiloId, pdfDaFirmare, filesDaFirmare, tipiDocumento);	
		} 
		catch (Exception e) {
			//rollback documenti non firmati e il num.adozione
			//doc firmati e marcature sono rollbackati internamente in firmaSalvaDocumenti		
			String context = "Errore Generico in fase di firma e salvataggio documenti";
			manageException(atto, null, null, context, e);
			throw new Exception(e.getMessage());
		}
		
		// documenti.addAll(dto.getDocumentiFirmati());
		// allegati.addAll(dto.getMarcature());
		
		/*try {
			String env_protocollo = getProtocolloEnvironment();
			if(!env_protocollo.isEmpty()) {
				if(attoAdozione.getFile().getContenuto()==null) {
					attoAdozione.setFile(fileService.findByFileId(attoAdozione.getFile().getId()));
				}
				log.info("call eseguiProtocollazioneCifraDetermina <"+atto.getAoo().getCodice()+","+atto.getAoo().getDescrizione()+","+numeroAdozione+">");
				RispostaProtocollo r = protocollazione.eseguiProtocollazioneCifraDetermina(atto.getAoo().getCodice(), atto.getAoo().getDescrizione(), atto.getCodiceCifra(), 
																			numeroAdozione, dataAdozione.toString(), attoAdozione.getFile().getContenuto());	
				documentoPdfService.setNumeroDataProtocollo(attoAdozione.getId(), 
						serviceUtil.getStringSegnaturaProtocollo(r.getSegnaturaProtocollo()), 
						r.getSegnaturaProtocollo().getDataRegistrazione());
			}
		} catch (ProtocolloCatchedException e) {
			//rollback documenti tutti documenti e marcature e il num.adozione		
			String context = "Errore in fase di chiamata al Repertorio";
			manageException(atto, dataAdozione, numeroAdozione, documenti, allegati, context, e);
			throw new Exception(context);
		}*/
/*
	}

	private String getProtocolloEnvironment() {
		String env = System.getProperty("env_protocollo", "");
		return env.equalsIgnoreCase("demo") || env.equalsIgnoreCase("test") || env.equalsIgnoreCase("prod") ? env : "";
	}
*/
	

	/**
	 * GET /attos/events, colleziona gli eventi collegati all'atto corrente
	 * @throws GestattiCatchedException
	 */
	@RequestMapping(value = "/attos/{id}/eventi", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Evento>> eventi(@PathVariable final Long id) throws GestattiCatchedException {
		try{
			log.debug("REST request eventi : {}", id);
			Atto atto = attoService.findOne(id);
			List<Evento> eventi = eventoService.findByAtto(atto);
			return new ResponseEntity<>(eventi, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos/ultimostato, ultimo stato dell'atto corrente
	 * @throws GestattiCatchedException
	 */
	@RequestMapping(value = "/attos/{id}/ultimostato", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StatoAttoDTO>> ultimostato(
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@PathVariable final Long id
			) throws GestattiCatchedException {
		try{
			
			Atto atto = attoRepository.findOne(id);
			List<StatoAttoDTO> stato = workflowService.getStatoAtto(atto);
			
			return new ResponseEntity<>(stato, HttpStatus.OK);
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos/revocato/{codice} -> get atto revocato
	 */
	@RequestMapping(value = "/attos/{id}/revocato", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Atto> revocato(
			@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request codice : {}", id);
			Atto atto = attoService.findByCodiceCifraRevocato(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			
			return new ResponseEntity<>(atto, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos/{id}/relataGestibile -> get atto revocato
	 * @throws DocumentException
	 * @throws IOException
	 */
	@RequestMapping(value = "/attos/{id}/relataGestibile", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<String> relataGestibile(
			@PathVariable final Long id)
			throws GestattiCatchedException {
		try{
			log.debug("REST request codice : {}", id);
			Atto atto = attoService.findOneSimple(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			JsonObject json = new JsonObject();
			boolean gestibile = false;
			if(atto.getFinePubblicazioneAlbo()!=null){
				Calendar cal = new GregorianCalendar();
				cal.setTime(atto.getFinePubblicazioneAlbo().toDate());
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				cal.set(Calendar.SECOND, 59);
				cal.set(Calendar.MILLISECOND, 999);
				if(new Date().after(cal.getTime())){
//TO-DO Commentato per Firenze dove non viene generata la relata. Occorre ripensare la sezione delle pubblicazioni in base alle possibile casistiche
//					gestibile = true;
				}
			}
			json.addProperty("info", gestibile);
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/elencotipodocumento/{taskBpmId}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity getElencoTipoDocumentoPreview(
			@PathVariable final String taskBpmId,
			final HttpServletResponse response) throws GestattiCatchedException {
		try {
			log.debug("REST request to get tipoDocumento list : {}", taskBpmId);

			List<TipoDocumentoDto> listTipoDocumento = workflowService.getElencoTipoDocumentoDaGenerare(taskBpmId);

			if (listTipoDocumento == null) {
				return new ResponseEntity<>(new ArrayList<TipoDocumentoDto>(), HttpStatus.OK);
			}
			return new ResponseEntity<>(listTipoDocumento, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
		
		
	}
	
//	@SuppressWarnings("rawtypes")
//	@RequestMapping(value = "/attos/pcfEnabled/{taskBpmId}", method = RequestMethod.GET)
//	@Timed
//	public ResponseEntity getPCFPreview(
//			@PathVariable final String taskBpmId,
//			final HttpServletResponse response) throws GestattiCatchedException {
//		try {
//			log.debug("REST request to get tipoDocumento list : {}", taskBpmId);
//
//			TipoTaskDTO task = workflowService.getDettaglioTask(taskBpmId);
//			
//			TipoAtto tipoAtto = tipoAttoService.findByCodice(task.getTipoAtto());
//			
//			boolean abilitato = isTipoAttoAbilitatoPerPFC(tipoAtto);
//			if(abilitato) {
//				return new ResponseEntity<>(Boolean.TRUE, HttpStatus.OK);
//			}
//
//			return new ResponseEntity<>(Boolean.FALSE, HttpStatus.OK);
//		} catch (Exception e) {
//			throw new GestattiCatchedException(e);
//		}
//	}
//	
//	private boolean isTipoAttoAbilitatoPerPFC(TipoAtto tipoAtto) {
//		List<Object> coppieTipoAttoTitoloModello = WebApplicationProps.getPropertyList(ConfigPropNames.PROPOSTA_COMPLETO_FRONTESPIZIO_TIPOATTO_MODELLO);
//		if(tipoAtto!=null && tipoAtto.getCodice()!=null) {
//			for (Object coppia : coppieTipoAttoTitoloModello) {
//				if(coppia!=null) {
//					String coppiaS = coppia.toString();
//					if(coppiaS.startsWith(tipoAtto.getCodice()+"#")){
//						return true;
//					}
//				}
//			}
//		}
//		return false;
//	}

	@RequestMapping(value = "/attos/copiaNonConforme/{idModelloHtml}/{idProfiloAttivo}/atto{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> copiaNonConforme(
			@PathVariable("id") Long idAtto,
			@PathVariable("idModelloHtml") Long idModelloHtml,
			@PathVariable("idProfiloAttivo") Long idProfiloAttivo,
			@RequestParam("omissis") Boolean omissis
			) throws GestattiCatchedException {
		try{
			log.debug("preview:" + idAtto);
			
			
			Atto atto = attoService.findOne(idAtto);
			
			Avanzamento avanzamento = new Avanzamento();
			avanzamento.setAtto(atto);
			avanzamento.setDataAttivita(new DateTime());
			
			String denominazioneAttivita = "Generazione Copia Non Conforme";
			if(omissis!=null && omissis.booleanValue()) {
				denominazioneAttivita = "Generazione Copia Non Conforme Con Omissis";
			}
			avanzamento.setAttivita(denominazioneAttivita );
			
			Profilo profilo = profiloService.findOne(idProfiloAttivo);
			avanzamento.setProfilo(profilo);
			
			Set<Avanzamento> lavorazioni = atto.getAvanzamento();
			String stato = "";
			if ( (lavorazioni != null) && (lavorazioni.size() > 0) ) {
				for (Iterator<Avanzamento> iterator = lavorazioni.iterator(); iterator.hasNext() && stato.equals("");) {
					Avanzamento ultimaAzione = (Avanzamento)iterator.next();
					if(ultimaAzione.getStato()!=null) {
						stato = ultimaAzione.getStato();
					}
				}
			}
			avanzamento.setStato(stato);
			
			avanzamentoRepository.save(avanzamento);
			
			return executeAttoPreview(idAtto, idModelloHtml, omissis, Boolean.FALSE);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	@RequestMapping(value = "/attos/propostaCompletaFrontespizio/{idProfiloAttivo}/{id}/Frontespizio_e_Proposta.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> propostaCompletaFrontespizio(
			@PathVariable("id") Long idAtto,
			@PathVariable("idProfiloAttivo") String idProfiloAttivo,
			@RequestParam("omissis") Boolean omissis
			) throws GestattiCatchedException {
		try{
			log.debug("preview:" + idAtto);
			
			Atto atto = attoService.findOne(idAtto);
			Calendar c = Calendar.getInstance();
			int yyyy= c.get(Calendar.YEAR);
			String MM= StringUtil.riempiASinistra(String.valueOf(c.get(Calendar.MONTH)), "0", 2);
			String dd= StringUtil.riempiASinistra(String.valueOf(c.get(Calendar.DAY_OF_MONTH)), "0", 2);
			String hh= StringUtil.riempiASinistra(String.valueOf(c.get(Calendar.HOUR_OF_DAY)), "0", 2);
			String mm= StringUtil.riempiASinistra(String.valueOf(c.get(Calendar.MINUTE)), "0", 2);
			
			
			
			String nomeFile = "Frontespizio_e_Proposta_"+
			atto.getTipoAtto().getCodice()+"_"+
					atto.getCodiceCifra().substring(atto.getCodiceCifra().lastIndexOf("/")+1)+"_"+yyyy+"_"+MM+"_"+dd+"_"+hh+"_"+mm+".pdf ";
			
			List<ModelloHtml> modelli = modelloHtmlService.findByTipoDocumento(TipoDocumentoEnum.frontespizio_proposta.name());
			String titoloModello = getTitoloModelloPCF(atto.getTipoAtto().getCodice());
			if(titoloModello!=null) {
				List<ModelloHtml> modelliHtmlProposta = modelloHtmlService.findByTitolo(titoloModello);
				if(modelli!=null && modelli.size()>0 && modelliHtmlProposta != null && modelliHtmlProposta.size()>0)
				{
					ModelloHtml modelloHtmlFrontespizio = modelli.get(0);
					ReportDTO reportFrontespizioDto = new ReportDTO();
					reportFrontespizioDto.setIdAtto(idAtto);
					reportFrontespizioDto.setIdModelloHtml(modelloHtmlFrontespizio.getId());
					reportFrontespizioDto.setOmissis(omissis);
				
					java.io.File frontespizio = reportService.previewAtto(atto, reportFrontespizioDto);
					
					ReportDTO reportPropostaDto = new ReportDTO();
					reportPropostaDto.setIdAtto(idAtto);
					ModelloHtml modelloHtmlProposta = modelliHtmlProposta.get(0);
					reportPropostaDto.setIdModelloHtml(modelloHtmlProposta.getId());
					reportPropostaDto.setOmissis(omissis);
					java.io.File pdfProposta = reportService.previewAtto(atto, reportPropostaDto);
					
					

			        PDFMergerUtility mergePdf = new PDFMergerUtility();
		            mergePdf.addSource(frontespizio); 
		            mergePdf.addSource(pdfProposta); 

			        mergePdf.setDestinationFileName(nomeFile);
			        mergePdf.mergeDocuments();
			        java.io.File resultFile = new java.io.File(nomeFile);
			        return returnResponseEntity(resultFile);
					

				}
				
				
			}
			return returnResponseEntity(new java.io.File(""));
			
			 
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
		
	}

	private String getTitoloModelloPCF(String codice) {
		List<Object> coppieTipoAttoTitoloModello = WebApplicationProps.getPropertyList(ConfigPropNames.PROPOSTA_COMPLETO_FRONTESPIZIO_TIPOATTO_MODELLO);
		
		for (Object coppia : coppieTipoAttoTitoloModello) {
			if(coppia!=null) {
				String coppiaS = coppia.toString();
				if(coppiaS.startsWith(codice+"#")){
					return coppiaS.substring(coppiaS.indexOf("#")+1);
				}
			}
		}
		
		return null;
	}

	@RequestMapping(value = "/attos/copiaConforme/{idProfiloAttivo}/atto{id}.pdf", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> copiaConforme(
			@PathVariable("id") Long idAtto,
			@PathVariable("idProfiloAttivo") Long idProfiloAttivo,
			@RequestParam("omissis") Boolean omissis
			) throws GestattiCatchedException {
		try{
			log.debug("preview:" + idAtto);
			
			Atto atto = attoService.findOne(idAtto);
			
			Avanzamento avanzamento = new Avanzamento();
			avanzamento.setAtto(atto);
			avanzamento.setDataAttivita(new DateTime());
			
			String denominazioneAttivita = "Generazione Copia Conforme";
			if(omissis!=null && omissis.booleanValue()) {
				denominazioneAttivita = "Generazione Copia Conforme Con Omissis";
			}
			avanzamento.setAttivita(denominazioneAttivita );
			
			Profilo profilo = profiloService.findOne(idProfiloAttivo);
			avanzamento.setProfilo(profilo);
			
			Set<Avanzamento> lavorazioni = atto.getAvanzamento();
			String stato = "";
			if ( (lavorazioni != null) && (lavorazioni.size() > 0) ) {
				for (Iterator<Avanzamento> iterator = lavorazioni.iterator(); iterator.hasNext() && stato.equals("");) {
					Avanzamento ultimaAzione = (Avanzamento)iterator.next();
					if(ultimaAzione.getStato()!=null) {
						stato = ultimaAzione.getStato();
					}
				}
			}
			avanzamento.setStato(stato);
			
			avanzamentoRepository.save(avanzamento);
			
			java.io.File pdfADozione = null;
			byte[] contentPdfAdozione;
			if(!omissis) {
                if(atto.getDocumentiPdfAdozione()!=null) {
                    for (DocumentoPdf doc : atto.getDocumentiPdfAdozione()) {
                        if(doc.getFirmato()==null || doc.getFirmato().equals(false)) {
                            contentPdfAdozione = dmsService.getContent(doc.getFile().getCmisObjectId());
                            pdfADozione = java.io.File.createTempFile("tmp_", "pdf");
                            FileUtils.writeByteArrayToFile(pdfADozione, contentPdfAdozione);
                            break;
                        }
                    }
                   
                }
            }else {
                if(atto.getDocumentiPdfAdozioneOmissis()!=null) {
                    for (int i = 0; i < atto.getDocumentiPdfAdozioneOmissis().size(); i++) {
                        for (DocumentoPdf doc : atto.getDocumentiPdfAdozioneOmissis()) {
                            if(doc.getFirmato()==null || doc.getFirmato().equals(false)) {
                                contentPdfAdozione = dmsService.getContent(atto.getDocumentiPdfAdozioneOmissis().get(i).getFile().getCmisObjectId());
                                pdfADozione = java.io.File.createTempFile("tmp_", "pdf");
                                FileUtils.writeByteArrayToFile(pdfADozione, contentPdfAdozione);
                                break;
                            }
                        }
                    }
                   
                }
            }

			java.io.File resultFile = java.io.File.createTempFile("copiaConforme", ".pdf");
		
			if(pdfADozione!=null) {
				
				 RandomAccessFile raf = new RandomAccessFile(pdfADozione, "r");
			     RandomAccessFileOrArray pdfFile = new RandomAccessFileOrArray(
			          new RandomAccessSourceFactory().createSource(raf));
			     PdfReader reader = new PdfReader(pdfFile, new byte[0]);
			     int contaPagine = reader.getNumberOfPages() + 1; //aggiunto 1 perchè nel totale va incluso la pagina stessa della copia conforme
			     reader.close();
				  
				List<ModelloHtml> modelli = modelloHtmlService.findByTipoDocumento(TipoDocumentoEnum.ultima_pagina_copia_conforme.name());
				if(modelli!=null && modelli.size()>0)
				{
					ModelloHtml modelloHtmlUltimaPaginaCopiaConforme = modelli.get(0);
					ReportDTO reportDto = new ReportDTO();
					reportDto.setIdAtto(idAtto);
					reportDto.setIdModelloHtml(modelloHtmlUltimaPaginaCopiaConforme.getId());
					reportDto.setOmissis(omissis);
				
					java.io.File ultimaPagina = reportService.previewAtto(atto, reportDto, contaPagine);
					
					
					
					

					PdfReader pdfReader = new PdfReader(new FileInputStream(pdfADozione));
					PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
					Integer numeroDiPagineTotaleAllegati = 0;
					PdfReader readerUltPag = new PdfReader(new FileInputStream(ultimaPagina));

					for (int npage = 1; npage <= readerUltPag.getNumberOfPages(); npage++) {
						numeroDiPagineTotaleAllegati++;
						int pageNumber = pdfReader.getNumberOfPages() + 1;
						pdfStamper.insertPage(pageNumber, readerUltPag.getPageSize(npage));
						pdfStamper.replacePage(readerUltPag, npage, pageNumber);
					}

					pdfStamper.close();
					pdfReader.close();

					
				}
			    

			}
			
					
			
			return returnResponseEntity(resultFile);
			 
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	
	
	private ResponseEntity<ByteArrayResource> executeAttoPreview(Long idAtto, Long idModelloHtml, Boolean omissis, Boolean adottato) throws GestattiCatchedException, FileNotFoundException {
		try{
			Atto atto = attoService.findOneWithOrdineGiorno(idAtto);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
		
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(idAtto);
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setOmissis(omissis);
		
			java.io.File result = reportService.previewAtto(atto, reportDto);
			
			return returnResponseEntity(result);
		}catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}
	
	private ResponseEntity<ByteArrayResource> returnResponseEntity(java.io.File result)	throws GestattiCatchedException {
		try{
			FileInputStream inputend = new FileInputStream(result);
			ByteArrayResource resultByte = new ByteArrayResource(IOUtils.toByteArray(inputend));
			IOUtils.closeQuietly(inputend);
			ResponseEntity<ByteArrayResource> responseEntity = new ResponseEntity<ByteArrayResource>(resultByte,
					HttpStatus.OK);
			
			result.deleteOnExit();
			return responseEntity;
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	

			
	/**
	 * Restituisce solo quelli per cui esiste già il doc non firmato
	 * @param taskBpmId
	 * @param response
	 * @return
	 * @throws GestattiCatchedException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/elencodocumentidafirmare/{taskBpmId}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity getElencoDocumentiDaFirmare(
			@PathVariable final String taskBpmId,
			final HttpServletResponse response) throws GestattiCatchedException {
		try {
			log.debug("REST request to get tipoDocumento list : {}", taskBpmId);

			List<DocumentoPdfDto> listDocumentoPdfDto = workflowService.getElencoDocumentiDaFirmare(taskBpmId);

			if (listDocumentoPdfDto == null) {
				return new ResponseEntity<>(new ArrayList<DocumentoPdfDto>(), HttpStatus.OK);
			}
			return new ResponseEntity<>(listDocumentoPdfDto, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
		
		
	}
	
	/**
	 * Restituisce anche quelli per cui ancora non esiste il doc non firmato
	 * @param taskBpmId
	 * @param response
	 * @return
	 * @throws GestattiCatchedException
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping(value = "/attos/getCodiciTipiDocumentoDaFirmare/{taskBpmId}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity getCodiciTipiDocumentoDaFirmare(
			@PathVariable final String taskBpmId,
			final HttpServletResponse response) throws GestattiCatchedException {
		try {
			log.debug("REST request to get tipoDocumento list : {}", taskBpmId);

			List<String> listo = workflowService.getCodiciTipiDocumentoDaFirmare(taskBpmId);

			if (listo == null) {
				return new ResponseEntity<>(new ArrayList<String>(), HttpStatus.OK);
			}
			return new ResponseEntity<>(listo, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
		
		
	}
	
}
