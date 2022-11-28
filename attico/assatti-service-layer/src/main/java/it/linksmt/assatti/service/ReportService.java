package it.linksmt.assatti.service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.inject.Inject;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.jempbox.xmp.XMPSchemaDublinCore;
import org.apache.jempbox.xmp.pdfa.XMPMetadataPDFA;
import org.apache.jempbox.xmp.pdfa.XMPSchemaPDFAId;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDDocumentOutline;
import org.apache.pdfbox.pdmodel.interactive.documentnavigation.outline.PDOutlineItem;
import org.joda.time.DateTime;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.google.common.collect.Lists;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfDocument;
import com.itextpdf.text.pdf.PdfImportedPage;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import it.linksmt.assatti.bpm.dto.StatoAttoDTO;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.dto.contabilita.DettaglioLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpAccertamentoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.ImpegnoDaStampareDto;
import it.linksmt.assatti.cooperation.dto.contabilita.LiquidazioneImpegnoDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoLiquidazioneDto;
import it.linksmt.assatti.cooperation.dto.contabilita.RigaLiquidazioneDto;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.CategoriaEvento;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.domain.EsitoPareri;
import it.linksmt.assatti.datalayer.domain.Evento;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.ReportRuntime;
import it.linksmt.assatti.datalayer.domain.Resoconto;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.TipoAoo;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.TipoResocontoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.domain.dto.AttiOdgWithIEDto;
import it.linksmt.assatti.datalayer.domain.dto.AvanzamentoDTO;
import it.linksmt.assatti.datalayer.domain.dto.RelataDiPubblicazioneDto;
import it.linksmt.assatti.datalayer.domain.util.AttiOdgWithIEDtoTransformer;
import it.linksmt.assatti.datalayer.domain.util.ReportServiceDataTest;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.EsitoRepository;
import it.linksmt.assatti.datalayer.repository.ModelloHtmlRepository;
import it.linksmt.assatti.datalayer.repository.OrdineGiornoRepository;
import it.linksmt.assatti.datalayer.repository.ReportRuntimeRepository;
import it.linksmt.assatti.datalayer.repository.ResocontoRepository;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.VerbaleRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.CartaStampataService.OdgPdfObject;
import it.linksmt.assatti.service.dto.AllegatoStampaDTO;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.dto.CriteriReportPdfRicercaDTO;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.TipoAttoReportDto;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.DateUtil;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class ReportService {

	private final Logger log = LoggerFactory.getLogger(ReportService.class);

	private static final String IT = "it";

	@Inject
	private UtenteService utenteService;
	@Inject
	private ModelloHtmlRepository modelloHtmlRepository;
	@Inject
	private ReportRuntimeRepository reportRuntimeRepository;
	@Inject
	private AttiOdgRepository attiOdgRepository;
	@Inject
	private VerbaleRepository verbaleRepository;
	@Inject
	private OrdineGiornoRepository ordineGiornoRepository;
	@Inject
	private SedutaGiuntaRepository sedutaGiuntaRepository;
	@Inject
	private EsitoRepository esitoRepository;
	@Inject
	private ResocontoRepository resocontoRepository;
	@Inject
	private SpringTemplateEngine templateEngine;
	@Inject
	private CartaStampataService cartaStampataService;
	@Inject
	private DocumentoPdfService documentoPdfService;
	@Inject
	private ServiceUtil serviceUtil;
	@Inject
	private AooService aooService;
	@Inject
	private SedutaGiuntaService sedutaGiuntaService;
	@Inject
	private OrdineGiornoService ordineGiornoService;
	@Inject
	private UtilityService utilityService;
	@Inject
	private EsitoPareriService esitoPareriService;
	
    @Inject
    private ConfigurazioneIncaricoService configurazioneIncaricoService;
    
    @Inject
    private DocumentoInformaticoService documentoInformaticoService;
    
    @Inject
    private DatiContabiliService datiContabiliService;
    
    @Inject
    private EsitoService esitoService;
    
    @Inject
    private DmsService dmsService;
    
    @Inject
	private WorkflowServiceWrapper workflowService;
    
	private String urlSitoEnte = WebApplicationProps.getProperty(ConfigPropNames.ENTE_URL_SITO);
	
	private String numeroColonneTabellaConsiglio = WebApplicationProps.getProperty(ConfigPropNames.NUMERO_COLONNE_TABELLE_CONSIGLIO);
	
	private String RUOLO_CAPO_ENTE = WebApplicationProps.getProperty(ConfigPropNames.RUOLO_CAPO_ENTE);
	private String RUOLO_VICE_CAPO_ENTE = WebApplicationProps.getProperty(ConfigPropNames.RUOLO_VICE_CAPO_ENTE);
	
	public final static String CONVERT_TO_PDF = ".pdf";
	public final static String CONVERT_TO_DOCX = ".docx";

	public static final String FONT = "/fonts/FreeSans.ttf";

	private static final String FORMATO_A4_PORTRAIT = "A4 portrait";
	private static final String FORMATO_A4_LANDSCAPE = "A4 landscape";
	
	public ReportService() {

	}
	
	private void aggiuntSegretarioPresidenteAlContext(Atto atto, Context contextRT){
		Profilo presidente = null;
		Profilo segretario = null;
		QualificaProfessionale qualificaProfessionaleSegretario = null;
		Profilo segretarioIE = null;
		QualificaProfessionale qualificaProfessionaleSegretarioIE = null;
		List<Profilo> scrutatori = new ArrayList<Profilo>();
		List<Profilo> scrutatoriIE = new ArrayList<Profilo>();
		if(atto!=null && atto.getOrdineGiornos()!=null && atto.getOrdineGiornos().size() > 0){
			AttiOdg aOdg = null;
			for(AttiOdg tmp : atto.getOrdineGiornos()){
				if(tmp.getEsito()!=null && atto.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(atto.getEsito())){
					aOdg = tmp;
					break;
				}
			}
			if(aOdg!=null && aOdg.getComponenti()!=null && aOdg.getComponenti().size() > 0){
				for(ComponentiGiunta cg : aOdg.getComponenti()){
					if(cg!=null && Boolean.TRUE.equals(cg.getIsScrutatore())) {
						scrutatori.add(cg.getProfilo());
					}
					if(cg!=null && Boolean.TRUE.equals(cg.getIsScrutatoreIE())) {
						scrutatoriIE.add(cg.getProfilo());
					}
					if(cg!=null && Boolean.TRUE.equals(cg.getIsPresidenteFine())) {
						presidente = cg.getProfilo();
					}
					if(cg!=null && Boolean.TRUE.equals(cg.getIsSegretarioFine())){
						segretario = cg.getProfilo();
						qualificaProfessionaleSegretario = cg.getQualificaProfessionale();
					}
					if(cg!=null && Boolean.TRUE.equals(cg.getIsSegretarioIE())){
						segretarioIE = cg.getProfilo();
						qualificaProfessionaleSegretarioIE = cg.getQualificaProfessionaleIE();
					}
				}
			}
		}
		contextRT.setVariable("presidente", presidente);
		contextRT.setVariable("segretario", segretario);
		contextRT.setVariable("qualificaProfessionaleSegretario", qualificaProfessionaleSegretario);
		contextRT.setVariable("segretarioIE", segretarioIE);
		contextRT.setVariable("qualificaProfessionaleSegretarioIE", qualificaProfessionaleSegretarioIE);
		contextRT.setVariable("scrutatori", scrutatori);
		contextRT.setVariable("scrutatoriIE", scrutatoriIE);
	}
	
	private SedutaGiunta getSedutaFromAtto(Atto atto, List<AttiOdg> odgs){
		SedutaGiunta seduta = null;
		if(odgs!=null && odgs.size() > 0){
			AttiOdg aOdg = null;
			for(AttiOdg tmp : odgs){
				if(tmp.getEsito()!=null && atto.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(atto.getEsito())){
					aOdg = tmp;
					break;
				}
			}
			if(aOdg!=null && aOdg.getOrdineGiorno() != null && aOdg.getOrdineGiorno().getSedutaGiunta() != null){
				seduta = aOdg.getOrdineGiorno().getSedutaGiunta();
			}
		} else {
			log.warn(String.format("getSedutaFromAtto :: Lista attiOdg null per l'atto con id=%s", atto.getId()));
		}
		
		return seduta;
	}
	
	/**
	 * 
	 * ATTENZIONE!!!
	 * In barba al suo nome, questo metodo viene usato ogni volta che viene generato il pdf di un atto,
	 * sia in caso di preview che di generazione del documento principale.
	 * 
	 * @param atto
	 * @param reportDto
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 * @throws DmsException 
	 * @throws ServiceException
	 */
	@Transactional
	public File previewAtto(Atto atto, ReportDTO reportDto) throws IOException, DocumentException, DmsException, ServiceException {
		return  previewAtto(atto, reportDto, 0);
	}

	/**
	 * 
	 * ATTENZIONE!!!
	 * In barba al suo nome, questo metodo viene usato ogni volta che viene generato il pdf di un atto,
	 * sia in caso di preview che di generazione del documento principale.
	 * 
	 * @param atto
	 * @param reportDto
	 * @return
	 * @throws IOException
	 * @throws DocumentException
	 * @throws DmsException 
	 * @throws ServiceException 
	 */
	@Transactional
	public File previewAtto(Atto atto, ReportDTO reportDto, int numeroPagineCopiaConforme) throws IOException, DocumentException, DmsException, ServiceException {
		log.debug("preview:atto:" + reportDto);
		Context contextRT = new Context(Locale.forLanguageTag(IT));
		if(reportDto.getDelegheFirme()!=null && reportDto.getDelegheFirme().size() > 0) {
			contextRT.setVariable("delegheFirme", reportDto.getDelegheFirme());
		}
		contextRT.setVariable("currentUser", utenteService.getUtenteBasicByUsername(SecurityUtils.getCurrentLogin()));
		Atto bean = new Atto();
		BeanUtils.copyProperties(atto, bean);
		
		if(atto.getOrdineGiornos()==null) {
			List<AttiOdg> odgs = attiOdgRepository.findByAtto(atto);
			atto.setOrdineGiornos(new HashSet<AttiOdg>(odgs));
			bean.setOrdineGiornos(new HashSet<AttiOdg>(odgs));
		}
		
		HashMap<Long, Long> hmProfiliSottoscrittoriDistinct = new HashMap<Long,Long>();
		Set<SottoscrittoreAtto> sottoscrittori = new TreeSet<SottoscrittoreAtto>();
		if(bean.getSottoscrittori()!=null){
			for(SottoscrittoreAtto s : bean.getSottoscrittori()){
				if(s.getEnabled()!=null && s.getEnabled()==true){
					if(!hmProfiliSottoscrittoriDistinct.containsKey(s.getProfilo().getId())) {
						sottoscrittori.add(s);
						hmProfiliSottoscrittoriDistinct.put(s.getProfilo().getId(), s.getProfilo().getId());
					}
					
				}
			}
		}
		bean.setSottoscrittori(sottoscrittori);
		
		
		/*
		 * Passo un oggetto con il visto di regolarità contabile e i pareri tecnico/contabile al modello html
		 */
		boolean parereContOk = false;
		boolean parereTecnicoOk = false;
		List<Parere> pareriQuartiereRevisore = null;
		List<Parere> pareriCommissione = null;
		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(atto.getId());
		
		
		
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		boolean isPropostaLicenziataDallaGiunta = modelloHtml!=null && modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null && "PROPOSTA_LICENZIATA_DALLA_GIUNTA".equalsIgnoreCase(modelloHtml.getTipoDocumento().getCodice());
		
		if(modelloHtml!=null && modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null && "DETERMINA_DIRIGENZIALE_COMPLETA".equalsIgnoreCase(modelloHtml.getTipoDocumento().getCodice())) {
			
			String firmatarioRagioneria = null;
			if(atto.getDocumentiPdfAdozioneOmissis()!=null && atto.getDocumentiPdfAdozioneOmissis().size()>0) {
				
				Long idDocPdf = documentoPdfService.findByAttoAdozioneOmissisFirmato(atto.getId());
			
				log.info("documentopdf preso in considerazione per il firmatarioRagioneria:"+idDocPdf);
				
				DocumentoPdf pdf = documentoPdfService.findOne(idDocPdf);				
				
//				log.info("atto.getDocumentiPdfAdozioneOmissis().size():"+atto.getDocumentiPdfAdozioneOmissis().size());
//				DocumentoPdf pdf = atto.getDocumentiPdfAdozioneOmissis().get(0);
//				
//				for (int i = 1; i < atto.getDocumentiPdfAdozioneOmissis().size(); i++) {
//					if(atto.getDocumentiPdfAdozioneOmissis().get(i).getCreatedDate().isAfter(pdf.getCreatedDate())) {
//						pdf = atto.getDocumentiPdfAdozioneOmissis().get(i);
//						log.info("cambio documentopdf per il firmatarioRagioneria:"+pdf.getId());
//					}
//				}
//				log.info("documentopdf preso in considerazione per il firmatarioRagioneria:"+pdf.getId());
				
				if(pdf!=null && pdf.getFirmatario()!=null && !pdf.getFirmatario().isEmpty()) {
					firmatarioRagioneria = pdf.getFirmatario();
				}
			} 
			
			if(firmatarioRagioneria==null && atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size()>0) {
				
//				log.info("atto.getDocumentiPdfAdozione().size():"+atto.getDocumentiPdfAdozione().size());
//				DocumentoPdf pdf = atto.getDocumentiPdfAdozione().get(0);
//				
//				for (int i = 1; i < atto.getDocumentiPdfAdozione().size(); i++) {
//					if(atto.getDocumentiPdfAdozione().get(i).getCreatedDate().isAfter(pdf.getCreatedDate())) {
//						pdf = atto.getDocumentiPdfAdozioneOmissis().get(i);
//						log.info("cambio documentopdf per il firmatarioRagioneria:"+pdf.getId());
//					}
//				}
				
				Long idDocPdf = documentoPdfService.findByAttoAdozioneFirmato(atto.getId());
				
				log.info("documentopdf preso in considerazione per il firmatarioRagioneria:"+idDocPdf);
				
				DocumentoPdf pdf = documentoPdfService.findOne(idDocPdf);	
				
				if(pdf!=null && pdf.getFirmatario()!=null && !pdf.getFirmatario().isEmpty()) {
					firmatarioRagioneria = pdf.getFirmatario();
				}
				
				log.info("documentopdf preso in considerazione per il firmatarioRagioneria:"+pdf.getId());
			}
			
			if(firmatarioRagioneria!=null) {
				contextRT.setVariable("firmatarioRagioneria", firmatarioRagioneria);
			}
		}else if(modelloHtml!=null && modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null && "frontespizio_proposta".equalsIgnoreCase(modelloHtml.getTipoDocumento().getCodice())) {
			if(atto.getTipoAtto()!=null && atto.getTipoAtto().getCodice()!=null && !atto.getTipoAtto().getCodice().isEmpty()) {
				String codiceTipoAtto = atto.getTipoAtto().getCodice();
				
				String titoloProposta = "Proposta";
				String livelloSuperiore = "";
				String responsabileTecnico = "";
				String responsabileContabile = "";
				String assessoreConsigliere = "";
				String estensoreProponente = "";
				String pareriIstruttori = "";
				String responsabileIstruttoria = "";
				String importoTotaleEntrata = "0";
				String importoTotaleUscita = "0";
				
				String dataDiStampa = "";
				
				//calcolo del responsabile
				HashMap<String, String> codiciIncaricoResponsabileTecnicoFirmatario = new HashMap<String, String>();
				codiciIncaricoResponsabileTecnicoFirmatario.put("DC_VERIFICA_RESPONSABILE_TECNICO_FIRMATARIO_DELIBERA","DC_VERIFICA_RESPONSABILE_TECNICO_FIRMATARIO_DELIBERA");
				codiciIncaricoResponsabileTecnicoFirmatario.put("DIR_VERIFICA_RESPONSABILE_TECNICO_FIRMATARIO_DELIBERA","DIR_VERIFICA_RESPONSABILE_TECNICO_FIRMATARIO_DELIBERA");
				
				if(listConfigurazioneIncarico!=null) {
					for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
						if(codiciIncaricoResponsabileTecnicoFirmatario.containsKey(c.getConfigurazioneTaskCodice())){
							List<ConfigurazioneIncaricoProfiloDto> incarichi = c.getListConfigurazioneIncaricoProfiloDto();
							if(incarichi!=null) {
								for(ConfigurazioneIncaricoProfiloDto incarico : incarichi) {
									
									if(incarico.getUtenteCognome()!=null && incarico.getUtenteNome()!=null) {
										if(!responsabileTecnico.isEmpty()) {
											responsabileTecnico += "; ";
										}
										responsabileTecnico += (incarico.getUtenteCognome() + " " + incarico.getUtenteNome());
									}
								}
							}
						}
					}
				}
				
				//calcolo del assessore/consigliere
				if(codiceTipoAtto.equalsIgnoreCase("DG") || codiceTipoAtto.equalsIgnoreCase("DPC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DC"))
				{
					if(listConfigurazioneIncarico!=null) {
						
						for(ConfigurazioneIncaricoDto c: listConfigurazioneIncarico) {
			    			if(c.getConfigurazioneTaskCodice().equals(TipiParereEnum.PARERE_ASSESORE.getCodice()) ||
		    				   c.getConfigurazioneTaskCodice().equals(TipiParereEnum.PARERE_CONSIGLIERE.getCodice())) {
			    				List<ConfigurazioneIncaricoProfiloDto> incarichi = c.getListConfigurazioneIncaricoProfiloDto();
								if(incarichi!=null) {
									for(ConfigurazioneIncaricoProfiloDto incarico : incarichi) {
										
										if(incarico.getUtenteCognome()!=null && incarico.getUtenteNome()!=null) {
											if(!assessoreConsigliere.isEmpty()) {
												assessoreConsigliere += "; ";
											}
											assessoreConsigliere += (incarico.getUtenteCognome() + " " + incarico.getUtenteNome());
										}
									}
								}
			    			}
			    		}
					}
					
					
					
				}else {
					if(atto.getProponenti()!=null) {
						for (Profilo profilo : atto.getProponenti()) {
							if(profilo!=null && profilo.getUtente()!=null) {
								assessoreConsigliere += profilo.getUtente().getCognome() + " " +  profilo.getUtente().getNome()+ "; ";
							}
						}
					}
				}
				
				//calcolo del estensoreProponente
				if(atto.getCreatedBy()!=null) {
					Utente estensore = utenteService.findByUsername(atto.getCreatedBy());
					if(estensore!=null) {
						estensoreProponente = estensore.getCognome()+ " "+estensore.getNome();
					}
				}
				
				NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"));
				if(atto.getDatiContabili()!=null){
					if(atto.getDatiContabili().getImportoEntrata()!=null && atto.getDatiContabili().getImportoEntrata().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						importoTotaleEntrata = currencyInstance.format(atto.getDatiContabili().getImportoEntrata());
					}
					if(atto.getDatiContabili().getImportoUscita()!=null && atto.getDatiContabili().getImportoUscita().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						importoTotaleUscita = currencyInstance.format(atto.getDatiContabili().getImportoUscita());
					}					
				}

				
				//calcolo del pareriIstruttori
				HashMap<String, String> codiciIncaricoPareriIstruttori = new HashMap<String, String>();
				
				codiciIncaricoPareriIstruttori.put("DIR_PARERE_ISTRUTTORIO_RESPONSABILE","DIR_PARERE_ISTRUTTORIO_RESPONSABILE");
				if(listConfigurazioneIncarico!=null) {
					
					for(ConfigurazioneIncaricoDto c: listConfigurazioneIncarico) {
		    			if(codiciIncaricoPareriIstruttori.containsKey(c.getConfigurazioneTaskCodice())) {
		    				List<ConfigurazioneIncaricoAooDto> incarichi = c.getListConfigurazioneIncaricoAooDto();
							if(incarichi!=null) {
								for(ConfigurazioneIncaricoAooDto incarico : incarichi) {
									if(atto.getPareri()!=null) {
										Set<Parere> listParere = atto.getPareri();
										for (Parere parere : listParere) {
											if (parere.getAoo()!=null && parere.getAoo().getId()!=null && parere.getAoo().getId().longValue() == incarico.getIdAoo().longValue() 
													&& parere.getTipoAzione()!=null && !StringUtil.isNull(parere.getTipoAzione().getCodice()) && parere.getTipoAzione().getCodice().equalsIgnoreCase(TipiParereEnum.VISTO_RESP_ISTRUTTORIA.getCodice()) && 
													(parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false)
												) {
												if(parere.getProfilo()!=null && parere.getProfilo().getUtente()!=null) {
												
													Utente utenteParere = parere.getProfilo().getUtente();
												
													if(!pareriIstruttori.isEmpty()) {
														pareriIstruttori+="; ";
													}
													pareriIstruttori += utenteParere.getCognome() + " " + utenteParere.getNome();
												}
												
											}
										}
									}
								}
							}
		    			}
		    		}
				}
				
				//calcolo del responsabile contabile
				HashMap<String, String> codiciIncaricoResponsabileContabile = new HashMap<String, String>();
				
				codiciIncaricoResponsabileContabile.put("DC_VERIFICA_RESPONSABILE_CONTABILE","DC_VERIFICA_RESPONSABILE_CONTABILE");
				codiciIncaricoResponsabileContabile.put("DIR_VERIFICA_RESPONSABILE_CONTABILE","DIR_VERIFICA_RESPONSABILE_CONTABILE");
				String responsabileContabileIncaricato = "";
				if(listConfigurazioneIncarico!=null) {
					for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
						if(codiciIncaricoResponsabileContabile.containsKey(c.getConfigurazioneTaskCodice())){
							List<ConfigurazioneIncaricoProfiloDto> incarichi = c.getListConfigurazioneIncaricoProfiloDto();
							if(incarichi!=null) {
								for(ConfigurazioneIncaricoProfiloDto incarico : incarichi) {
									
									if(incarico.getUtenteCognome()!=null && incarico.getUtenteNome()!=null) {
										if(!responsabileContabileIncaricato.isEmpty()) {
											responsabileContabileIncaricato += "; ";
										}
										responsabileContabileIncaricato += (incarico.getUtenteCognome() + " " + incarico.getUtenteNome());
									}
								}
							}
						}
					}
				}
				if(atto.getPareri()!=null) {
					Set<Parere> listParere = atto.getPareri();
					for (Parere parere : listParere) {
						if (parere.getTipoAzione()!=null && !StringUtil.isNull(parere.getTipoAzione().getCodice()) && parere.getTipoAzione().getCodice().equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice()) && 
								(parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false)
							) {
							if(parere.getProfilo()!=null && parere.getProfilo().getUtente()!=null) {
							
								Utente utenteParere = parere.getProfilo().getUtente();
							
								if(!responsabileContabile.isEmpty()) {
									pareriIstruttori+="; ";
								}
								responsabileContabile += utenteParere.getCognome() + " " + utenteParere.getNome();
							}
							
						}
					}
				}
				
				//calcolo del responsabileIstruttoria
				HashMap<String, String> codiciIncaricoRespIstruttoria = new HashMap<String, String>();
				codiciIncaricoRespIstruttoria.put("ORD_VISTO_RESPONSABILE_ISTRUTTORIA","ORD_VISTO_RESPONSABILE_ISTRUTTORIA");
				codiciIncaricoRespIstruttoria.put("DEC_VISTO_RESPONSABILE_ISTRUTTORIA","DEC_VISTO_RESPONSABILE_ISTRUTTORIA");
				codiciIncaricoRespIstruttoria.put("DIR_VISTO_RESPONSABILE_ISTRUTTORIA","DIR_VISTO_RESPONSABILE_ISTRUTTORIA");
				codiciIncaricoRespIstruttoria.put("DEC_VISTO_RESPONSABILE_ISTRUTTORIA_1","DEC_VISTO_RESPONSABILE_ISTRUTTORIA_1");
				
				if(listConfigurazioneIncarico!=null) {
					
					for(ConfigurazioneIncaricoDto c: listConfigurazioneIncarico) {
		    			if(codiciIncaricoRespIstruttoria.containsKey(c.getConfigurazioneTaskCodice())) {
		    				List<ConfigurazioneIncaricoProfiloDto> incarichi = c.getListConfigurazioneIncaricoProfiloDto();
							if(incarichi!=null) {
								for(ConfigurazioneIncaricoProfiloDto incarico : incarichi) {
									if(incarico.getUtenteCognome()!=null && incarico.getUtenteNome()!=null)
									{
										if(!responsabileIstruttoria.isEmpty()) {
											responsabileIstruttoria += "; ";
										}
										responsabileIstruttoria += incarico.getUtenteCognome() + " " + incarico.getUtenteNome();
									}
								}
							}
		    			}
		    		}
				}

				if(codiceTipoAtto.equalsIgnoreCase("DC")) {
					titoloProposta = "PROPOSTA DELIBERAZIONE CONSIGLIO COMUNALE";
					
				}else if(codiceTipoAtto.equalsIgnoreCase("DIC")){
					titoloProposta = "PROPOSTA DI DELIBERAZIONE DI INDIRIZZO DI CONSIGLIO COMUNALE";
					responsabileTecnico = "";
					//TODO CALCOLARE responsabile
					
				}else if(codiceTipoAtto.equalsIgnoreCase("DG")) {
					titoloProposta = "PROPOSTA DELIBERAZIONE GIUNTA";
				}else if(codiceTipoAtto.equalsIgnoreCase("DIG")){
					titoloProposta = "PROPOSTA DI DELIBERAZIONE DI INDIRIZZO DELLA GIUNTA COMUNALE";
					//TODO CALCOLARE responsabile
					
				}else if(codiceTipoAtto.equalsIgnoreCase("DPC")){
					titoloProposta = "PROPOSTA DI DELIBERAZIONE PER IL CONSIGLIO";
				}else if(codiceTipoAtto.equalsIgnoreCase("COM")){
					titoloProposta = "COMUNICAZIONE";
				}else if(codiceTipoAtto.equalsIgnoreCase("DEC")){
					titoloProposta = "PROPOSTA DI DECRETO DEL SINDACO";
				}else if(codiceTipoAtto.equalsIgnoreCase("DAT")){
					titoloProposta = "DOMANDA DI ATTUALITA'";
				}else if(codiceTipoAtto.equalsIgnoreCase("INT")){
					titoloProposta = "INTERROGAZIONE";
				}else if(codiceTipoAtto.equalsIgnoreCase("MOZ")){
					titoloProposta = "MOZIONE";
				}else if(codiceTipoAtto.equalsIgnoreCase("ORD")){
					titoloProposta = "ORDINANZA DEL SINDACO";
				}else if(codiceTipoAtto.equalsIgnoreCase("ODG")){
					titoloProposta = "ORDINE DEL GIORNO";
				}else if(codiceTipoAtto.equalsIgnoreCase("QT")){
					titoloProposta = "QUESTION TIME";
				}else if(codiceTipoAtto.equalsIgnoreCase("RIS")){
					titoloProposta = "RISOLUZIONE";
				}else if(codiceTipoAtto.equalsIgnoreCase("VERB")){
					titoloProposta = "VERBALE";
				}
				
				Aoo aooProponente = aooService.findOne(atto.getAoo().getId());
				if(aooProponente.getTipoAoo()!=null && aooProponente.getTipoAoo().getCodice()!=null && !aooProponente.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
					
					Aoo direzione = aooService.getAooDirezione(aooProponente);
					if(direzione != null) {
						livelloSuperiore = direzione.getDescrizione();
					}
				}

				SimpleDateFormat sdf=new SimpleDateFormat("dd/MM/yyyy hh:mm");
				dataDiStampa=sdf.format(new Date());
				
				contextRT.setVariable("titoloProposta", titoloProposta);
				contextRT.setVariable("livelloSuperiore", livelloSuperiore);
				contextRT.setVariable("responsabile", responsabileTecnico);
				contextRT.setVariable("responsabileContabile", responsabileContabile.isEmpty()?responsabileContabileIncaricato:responsabileContabile);
				contextRT.setVariable("assessoreConsigliere", assessoreConsigliere);
				contextRT.setVariable("estensoreProponente", estensoreProponente);
				contextRT.setVariable("pareriIstruttori", pareriIstruttori);
				contextRT.setVariable("responsabileIstruttoria", responsabileIstruttoria);
				contextRT.setVariable("importoTotaleEntrata", importoTotaleEntrata);
				contextRT.setVariable("importoTotaleUscita", importoTotaleUscita);
				contextRT.setVariable("dataDiStampa", dataDiStampa);
				
				
				
				
				
				List<DocumentoInformatico> allegati = documentoInformaticoService.findByAtto(atto.getId());
				
				List<AllegatoStampaDTO> allegatiDto = new ArrayList<AllegatoStampaDTO>();
				
				if(allegati!=null) {
					int progressivo = 1;
					for (DocumentoInformatico a : allegati) {
						AllegatoStampaDTO dto = new AllegatoStampaDTO();
						dto.setProgressivo(String.valueOf(progressivo));
						dto.setDescrizione(a.getTitolo()!=null?a.getTitolo():"");
						String tipologia = "Generico";
						if(a.getTipoAllegato()!=null && a.getTipoAllegato().getCodice()!= null && a.getTipoAllegato().getCodice().equalsIgnoreCase("PARTE_INTEGRANTE")) {
							tipologia = a.getPubblicabile() == null || !a.getPubblicabile()? "Riservato":"Integrante";
						}
						dto.setTipo(tipologia);
						dto.setAnnotazione(a.getNote()!=null?a.getNote():"");
						allegatiDto.add(dto);
						progressivo++;
					}
				}
				contextRT.setVariable("allegati", allegatiDto);
			}
		}
		
		
		
		
		if(atto.getPareri()!=null) {
			Set<Parere> listParere = atto.getPareri();
			DateTime dataParereTecnico = null;
			DateTime dataParereContabile = null;
			ArrayList<Parere> pareriList = Lists.newArrayList(listParere.iterator());
			for (int i = 0; i < pareriList.size(); i++) {
				
				Parere parere = pareriList.get(i);
				if ((parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) &&
					(parere.getTipoAzione() != null) && (!StringUtil.isNull(parere.getTipoAzione().getCodice()))) {
					
					// i pareri sono ordinati per data dal più recente, viene inserito il primo per ogni tipologia
					String tipoPar = StringUtil.trimStr(parere.getTipoAzione().getCodice());
					
					if(!parereContOk && ((!Lists.newArrayList("DG", "DC", "DPC").contains(atto.getTipoAtto().getCodice()) && tipoPar.equalsIgnoreCase(TipiParereEnum.VISTO_CONTABILE.getCodice())) || tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_CONTABILE.getCodice())) ) {
						if(parere.getCreatedBy()!=null) {
							parere.setCreateUser(utenteService.findByUsername(parere.getCreatedBy()));
						}
						String parereContabileArticolato =  parere.getParerePersonalizzato();
						if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_CONTABILE.getCodice())) {
							int succ = i+1;
							boolean parerePersonalizzatoValorizzato = !StringUtil.isNull(parereContabileArticolato);
							boolean esci = false;
							while (!esci && !parerePersonalizzatoValorizzato && succ < pareriList.size()) {
								Parere parereSucc = pareriList.get(succ);
								String tipoParSucc = StringUtil.trimStr(parereSucc.getTipoAzione().getCodice());
								if(tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE_REGOLARITA_MODIFICHE.getCodice()) || tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice())) {
									if(!StringUtil.isNull(parereSucc.getParerePersonalizzato())) {
											parereContabileArticolato =  parereSucc.getParerePersonalizzato();
									}
								}
								parerePersonalizzatoValorizzato = !StringUtil.isNull(parereContabileArticolato);
								esci = !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE_REGOLARITA_MODIFICHE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.VISTO_CONTABILE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice());
								succ=succ+1;
							}
						}
						contextRT.setVariable("parereContabileArticolato", parereContabileArticolato);
						contextRT.setVariable("parereContabile", parere);
						parereContOk = true;
						dataParereContabile = parere.getData();
					} 
					else if(!parereTecnicoOk && ((!Lists.newArrayList("DG", "DC", "DPC").contains(atto.getTipoAtto().getCodice()) && tipoPar.equalsIgnoreCase(TipiParereEnum.VISTO_TECNICO.getCodice())) || tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_TECNICA.getCodice())) ) {
						if(parere.getCreatedBy()!=null) {
							parere.setCreateUser(utenteService.findByUsername(parere.getCreatedBy()));
						}
						
						String parereTecnicoArticolato =  parere.getParerePersonalizzato();
						if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_TECNICA.getCodice())) {
							int succ = i+1;
							boolean parerePersonalizzatoValorizzato = !StringUtil.isNull(parereTecnicoArticolato);
							boolean esci = false;
							while (!esci && !parerePersonalizzatoValorizzato && succ < pareriList.size()) {
								Parere parereSucc = pareriList.get(succ);
								String tipoParSucc = StringUtil.trimStr(parereSucc.getTipoAzione().getCodice());
								if(tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO_REGOLARITA_MODIFICHE.getCodice()) || tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice())) {
									if(!StringUtil.isNull(parereSucc.getParerePersonalizzato())) {
										parereTecnicoArticolato =  parereSucc.getParerePersonalizzato();
									}
								}
								parerePersonalizzatoValorizzato = !StringUtil.isNull(parereTecnicoArticolato);
								esci = !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO_REGOLARITA_MODIFICHE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.VISTO_TECNICO.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice());;
								succ=succ+1;
							}
						}
						contextRT.setVariable("parereTecnicoArticolato", parereTecnicoArticolato);
						contextRT.setVariable("parereTecnico", parere);
						dataParereTecnico = parere.getData();
						parereTecnicoOk = true;
					}
					if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_COMMISSIONE.getCodice())) {
						if(pareriCommissione==null) {
							pareriCommissione = new ArrayList<Parere>();
						}
						String decodificaParere = getDecodificaParere(parere);
						parere.setParere(decodificaParere);
						
						if(listConfigurazioneIncarico!=null) {
							
							for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
								if(c.getConfigurazioneTaskCodice()!=null && c.getConfigurazioneTaskCodice().equals(ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE.getCodice())) {
									List<ConfigurazioneIncaricoAooDto> incaricoaoos = c.getListConfigurazioneIncaricoAooDto();
									if(incaricoaoos!=null) {
										for(ConfigurazioneIncaricoAooDto incarico : incaricoaoos) {
											
											if(incarico.getIdAoo().longValue() == parere.getAoo().getId().longValue()) {
												parere.setDataInvio(incarico.getDataManuale());
												parere.setDataScadenza(new DateTime(incarico.getDataManuale()).plusDays(incarico.getGiorniScadenza()));
											}
											
										}
									}
								}
							}
							
						}
						
						pareriCommissione.add(parere);
					}else if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_QUARTIERE_REVISORI.getCodice())) {
						if(pareriQuartiereRevisore==null) {
							pareriQuartiereRevisore = new ArrayList<Parere>();
						}
						String decodificaParere = getDecodificaParere(parere);
						parere.setParere(decodificaParere);
						
						
						pareriQuartiereRevisore.add(parere);
					}else if( (parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) && Lists.newArrayList("DG","DC","DPC").contains(atto.getTipoAtto().getCodice()) && (tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice()) || tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO_REGOLARITA_MODIFICHE.getCodice())) ) { 
						if(dataParereTecnico==null || parere.getData().isAfter(dataParereTecnico)) {
							if(parere.getCreatedBy()!=null) {
								parere.setCreateUser(utenteService.findByUsername(parere.getCreatedBy()));
							}
							
							String parereTecnicoArticolato =  parere.getParerePersonalizzato();
							
							if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice())) {
								int succ = i+1;
								boolean parerePersonalizzatoValorizzato = !StringUtil.isNull(parereTecnicoArticolato);
								boolean esci = false;
								while (!esci && !parerePersonalizzatoValorizzato && succ < pareriList.size()) {
									Parere parereSucc = pareriList.get(succ);
									String tipoParSucc = StringUtil.trimStr(parereSucc.getTipoAzione().getCodice());
									if(tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO_REGOLARITA_MODIFICHE.getCodice()) || tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice())) {
										if(!StringUtil.isNull(parereSucc.getParerePersonalizzato())) {
											parereTecnicoArticolato =  parereSucc.getParerePersonalizzato();
										}
									}
									parerePersonalizzatoValorizzato = !StringUtil.isNull(parereTecnicoArticolato);
									esci = !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO_REGOLARITA_MODIFICHE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.VISTO_TECNICO.getCodice())  && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_TECNICO.getCodice());
									succ=succ+1;
								}
							}
							
							
							contextRT.setVariable("parereTecnicoArticolato", parereTecnicoArticolato);
							contextRT.setVariable("parereTecnico", parere);
							parereTecnicoOk = true;
							dataParereTecnico = parere.getData();
						}
					}
					else if((parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) && Lists.newArrayList("DG","DC","DPC").contains(atto.getTipoAtto().getCodice()) && (tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice()) || tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE_REGOLARITA_MODIFICHE.getCodice())) ) {
						if(dataParereContabile==null || parere.getData().isAfter(dataParereContabile)) {
							String parereContabileArticolato =  parere.getParerePersonalizzato();
							if(parere.getCreatedBy()!=null) {
								parere.setCreateUser(utenteService.findByUsername(parere.getCreatedBy()));
							}
							if(tipoPar.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice())) {
								int succ = i+1;
								boolean parerePersonalizzatoValorizzato = !StringUtil.isNull(parereContabileArticolato);
								boolean esci = false;
								while (!esci && !parerePersonalizzatoValorizzato && succ < pareriList.size()) {
									Parere parereSucc = pareriList.get(succ);
									String tipoParSucc = StringUtil.trimStr(parereSucc.getTipoAzione().getCodice());
									if(tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE_REGOLARITA_MODIFICHE.getCodice()) || tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice())) {
										if(!StringUtil.isNull(parereSucc.getParerePersonalizzato())) {
												parereContabileArticolato =  parereSucc.getParerePersonalizzato();
										}
									}
									parerePersonalizzatoValorizzato = !StringUtil.isNull(parereContabileArticolato);
									esci = !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE_REGOLARITA_MODIFICHE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.VISTO_CONTABILE.getCodice()) && !tipoParSucc.equalsIgnoreCase(TipiParereEnum.PARERE_CONTABILE.getCodice());
									succ=succ+1;
								}
							}
							contextRT.setVariable("parereContabileArticolato", parereContabileArticolato);
							dataParereContabile = parere.getData();
							contextRT.setVariable("parereContabile", parere);
							parereContOk = true;
						}
					}
				}
			}
		}
		
		//gestisco i pareri commissione non inseriti con termini scaduti che devono essere riportati in stampa con data parere e parere non inserito
		if(listConfigurazioneIncarico!=null) {
			
			for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
				if(c.getConfigurazioneTaskCodice()!=null && c.getConfigurazioneTaskCodice().equals(ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE.getCodice())) {
					List<ConfigurazioneIncaricoAooDto> incaricoaoos = c.getListConfigurazioneIncaricoAooDto();
					if(incaricoaoos!=null) {
						for(ConfigurazioneIncaricoAooDto incarico : incaricoaoos) {
							
							boolean parereGiaInseritoInLista = false;
							
							if(pareriCommissione!=null) {
								for (Parere parereCommissione : pareriCommissione) {
									if(incarico.getIdAoo().longValue() == parereCommissione.getAoo().getId().longValue()) {
										parereGiaInseritoInLista = true;
										break;
									}
								}
							}else {
								pareriCommissione = new ArrayList<Parere>();
							}
							
							if(!parereGiaInseritoInLista) {
								Parere parereConTerminiScadutiNonIserito = new Parere();
								parereConTerminiScadutiNonIserito.setAoo(aooService.findMinimalAooById(incarico.getIdAoo()));
								parereConTerminiScadutiNonIserito.setDataInvio(incarico.getDataManuale());
								parereConTerminiScadutiNonIserito.setDataScadenza(new DateTime(incarico.getDataManuale()).plusDays(incarico.getGiorniScadenza()));
								pareriCommissione.add(parereConTerminiScadutiNonIserito);
							}
						}
					}
				}
			}
			
		}
		
		if(pareriQuartiereRevisore!=null) {
			List<Parere> pareriQuartiereRevisoreConTipoAoo = new ArrayList<Parere>();
			for (int i = 0; i < pareriQuartiereRevisore.size(); i++) {
				Aoo aoo = aooService.getAooConTipoAoo(pareriQuartiereRevisore.get(i).getAoo().getId());
				
				TipoAoo tipoAoo = aoo.getTipoAoo();
				aoo.setTipoAoo(tipoAoo);
				pareriQuartiereRevisore.get(i).setAoo(aoo);
				pareriQuartiereRevisoreConTipoAoo.add(pareriQuartiereRevisore.get(i));
			}
			Collections.sort(pareriQuartiereRevisoreConTipoAoo,new Comparator<Parere>(){
				@Override
			    public int compare(Parere parere1, Parere parere2){
					return parere1.getAoo().getDescrizioneAsDestinatario().compareTo(parere2.getAoo().getDescrizioneAsDestinatario());
				}
			});
			contextRT.setVariable("pareriQuartiereRevisore", pareriQuartiereRevisoreConTipoAoo);
		}
		if(pareriCommissione!=null) {
			//Ordino i pareriCommissione in ordine alfabetico in base al nome della Commissione
			Collections.sort(pareriCommissione,new Comparator<Parere>(){
				@Override
			    public int compare(Parere parere1, Parere parere2){
					return parere1.getAoo().getDescrizioneAsDestinatario().compareTo(parere2.getAoo().getDescrizioneAsDestinatario());
				}
			});
			contextRT.setVariable("pareriCommissioni", pareriCommissione);
		}
		
		contextRT.setVariable("atto", bean);
		this.aggiuntSegretarioPresidenteAlContext(atto, contextRT);
		
		/*
		 * Passo un oggetto con l'elenco degli incarichi al modello html
		 */
		try {
			List<String> responsabili = null;
			if(listConfigurazioneIncarico == null) {
				listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(atto.getId());
			}
			if(listConfigurazioneIncarico!=null) {
				responsabili = new ArrayList<>();
				
				for(ConfigurazioneIncaricoDto c : listConfigurazioneIncarico) {
					if(c.getConfigurazioneTaskCodice()!=null && c.getConfigurazioneTaskCodice().equals(ConfigurazioneTaskEnum.RESPONSABILE_TECNICO.getCodice())) {
						List<ConfigurazioneIncaricoProfiloDto> profili = c.getListConfigurazioneIncaricoProfiloDto();
						if(profili!=null) {
							for(ConfigurazioneIncaricoProfiloDto p : profili) {
								String responsabileTecnico = p.getUtenteCognome() + " " + p.getUtenteNome();
								responsabili.add(responsabileTecnico);
							}
						}
					}
				}
				
				contextRT.setVariable("responsabili", responsabili);
			}
		}
		catch (ServiceException e) {
			log.error("Errore durante la lettura dei dati degli incarichi.", e);
		}
		
		/*
		 * Passo un oggetto con l'elenco degli allegati al modello html
		 */
		try {
			List<String> elencoTitoloAllegato = null;
			List<DocumentoInformatico> listDocumentoInformatico = documentoInformaticoService.findByAttoCodiceTipoAllegato(atto.getId(), "PARTE_INTEGRANTE");
			if(listDocumentoInformatico!=null && !listDocumentoInformatico.isEmpty()) {
				elencoTitoloAllegato = new ArrayList<>();

				for(DocumentoInformatico d : listDocumentoInformatico) {
					String titolo = d.getTitolo();

					if(d.getFile() != null) {
						titolo += " - " + d.getFile().getSha256Checksum();
					}

					elencoTitoloAllegato.add(titolo);
				}

				contextRT.setVariable("elencoTitoloAllegato", elencoTitoloAllegato);
			}
		}
		catch (ServiceException e) {
			log.error("Errore durante l'aggiunta degli allegati", e);
		}
				
		/*
		 * Passo un oggetto con l'elenco dei movimenti contabili al modello html
		 */
		List<MovimentoContabileDto> listMovimentiContabili = null;
		listMovimentiContabili = datiContabiliService.elencoMovimento(atto.getId());
		
		List<DettaglioLiquidazioneDto> listDettLiquidazione = new ArrayList<DettaglioLiquidazioneDto>();
		List<MovimentoLiquidazioneDto> listLiquidazione = new ArrayList<MovimentoLiquidazioneDto>();
		List<ImpAccertamentoDto> listMovImpAcce = new ArrayList<ImpAccertamentoDto>();
		
		boolean nascondiBen = false;
		if(atto.getDatiContabili() != null && atto.getDatiContabili().getNascondiBeneficiariMovimentiAtto() != null 
				&& atto.getDatiContabili().getNascondiBeneficiariMovimentiAtto().booleanValue()) {
			nascondiBen = true;
		}
		
		if(listMovimentiContabili!=null) {
			for (MovimentoContabileDto movimentoContabileDto : listMovimentiContabili) {
				if (movimentoContabileDto.getDettaglioLiquidazione()!=null) {
					listDettLiquidazione.add(movimentoContabileDto.getDettaglioLiquidazione());
				}
				if(movimentoContabileDto.getLiquidazione()!=null) {
					if(movimentoContabileDto.getLiquidazione() != null && movimentoContabileDto.getLiquidazione().getDocumento() != null && movimentoContabileDto.getLiquidazione().getDocumento().size()>0 &&
							movimentoContabileDto.getLiquidazione().getDocumento().get(0) != null && nascondiBen) {
						movimentoContabileDto.getLiquidazione().getDocumento().get(0).setSoggettoNome(
								movimentoContabileDto.getLiquidazione().getDocumento().get(0).getSoggettoCod()!=null && !movimentoContabileDto.getLiquidazione().getDocumento().get(0).getSoggettoCod().isEmpty()
								?"*****"
										:"");
					}
					listLiquidazione.add(movimentoContabileDto.getLiquidazione());
				} 
				if(movimentoContabileDto.getMovImpAcce()!=null) {
					if(movimentoContabileDto.getMovImpAcce() != null && nascondiBen) {
						movimentoContabileDto.getMovImpAcce().setDescCodDebBen(
								movimentoContabileDto.getMovImpAcce().getCodDebBen()!=null && !movimentoContabileDto.getMovImpAcce().getCodDebBen().isEmpty()
								?"*****"
										:"");
					}
					listMovImpAcce.add(movimentoContabileDto.getMovImpAcce());
				}
			}
		}
		
		contextRT.setVariable("dettaglioMovimentiLiquidazione", listDettLiquidazione);
		contextRT.setVariable("movimentiContabiliLiquidazione", listLiquidazione);
		
		
		contextRT.setVariable("movimentiContabiliImpAcc", listMovImpAcce);
		
		
		List<ImpegnoDaStampareDto> listaImpegniDaStampare = new ArrayList<ImpegnoDaStampareDto>();
		listaImpegniDaStampare = getlistaImpegniDaStampare(listDettLiquidazione);
		contextRT.setVariable("listaImpegniDaStampare", listaImpegniDaStampare);
		
		/*
		 * Passo un oggetto con l'elenco degli esiti della seduta al modello html
		 */
		List<Esito> listEsitiSeduta = null;
		listEsitiSeduta = esitoService.findAll();
		contextRT.setVariable("esitiSeduta", listEsitiSeduta);
		
			
		// String htmlRT = creaTemplatePreview(reportDto, contextRT);
		// log.debug( "preview:atto:HTML"+ htmlRT);
		// ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		
		/*
		 * TODO: valutare come vanno gestiti i codici in ATTICO
		 * 
		if(!atto.getTipoAtto().getCodice().equalsIgnoreCase("del") || (atto.getTipoAtto().getCodice().equalsIgnoreCase("del") && modelloHtml.getTitolo().toUpperCase().contains("PROPOSTA"))){
//			return executePreviewAtto(htmlRT, atto, reportDto, getNascondiUrlSitoESpostaDatiIntestazione(modelloHtml),getStampaIntestazioneSoloFrontespizio(modelloHtml), getSoloLogoCentrale(modelloHtml), getAggiungiRefertoTecnico(modelloHtml));
			// FIXME modificato per demo del 10 luglio 2018
			return executePreviewAtto(htmlRT, atto, reportDto, 
					getNascondiUrlSitoESpostaDatiIntestazione(modelloHtml), true, true);
			
			// , getAggiungiRefertoTecnico(modelloHtml)
		}
		else{
		*/
		
			// Considero l'odg e la seduta più recenti per i quali l'esito dell'atto corrisponde
			AttiOdg aOdg = null;
			
			
			if (atto.getOrdineGiornos() != null) {
				for(AttiOdg tmp : atto.getOrdineGiornos()) {					
					if(tmp.getEsito()!=null && atto.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(atto.getEsito())){
						if (aOdg==null || (tmp.getId().longValue() > aOdg.getId().longValue())) {
							aOdg = tmp;
						}
					}
				}
			}else {
				List<AttiOdg> ordineGiornoSet = attiOdgRepository.findByAtto(atto);
				if(ordineGiornoSet != null) 
				{
					for(AttiOdg tmp : ordineGiornoSet) {					
						if(tmp.getEsito()!=null && atto.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(atto.getEsito())){
							if (aOdg==null || (tmp.getId().longValue() > aOdg.getId().longValue())) {
								aOdg = tmp;
							}
						}
					}
				}
			}
			if ( (aOdg != null) && !StringUtil.isNull(aOdg.getEsito())) {
				Esito esito = this.esitoRepository.findOne(StringUtil.trimStr(aOdg.getEsito()));
				if (esito != null) {
					aOdg.setEsitoLabel(esito.getLabelDocument());
				}
			}
			
			final SedutaGiunta seduta = aOdg!=null && aOdg.getOrdineGiorno()!=null ? aOdg.getOrdineGiorno().getSedutaGiunta() : new SedutaGiunta();
			List<ComponentiGiunta> componenti = aOdg!=null && aOdg.getComponenti()!=null ? aOdg.getComponenti() : new ArrayList<ComponentiGiunta>();
			
			Set<Utente> presidentiRegione = utenteService.findUtentiByCodiceRuolo(RUOLO_CAPO_ENTE);
			Set<Utente> vicePresidentiRegione = utenteService.findUtentiByCodiceRuolo(RUOLO_VICE_CAPO_ENTE);
			
			Profilo presidenteRegione = null;
			Profilo segretario = null;
			Profilo vicePresidenteRegione = null;
			Profilo presidenteProvvedimento = null;
			
//			List<ComponentiGiunta> componentiFiltrati = new ArrayList<ComponentiGiunta>();
			for(ComponentiGiunta componente : componenti){
				if(presidenteRegione==null && presidentiRegione.contains(componente.getProfilo().getUtente())){
					presidenteRegione = componente.getProfilo();
				}
				if(segretario==null && Boolean.TRUE.equals(componente.getIsSegretarioFine()) ) {
					segretario = componente.getProfilo();
				}
				if(presidenteProvvedimento == null && Boolean.TRUE.equals(componente.getIsPresidenteFine())){
					presidenteProvvedimento = componente.getProfilo();
				}
				if(vicePresidenteRegione==null && vicePresidentiRegione.contains(componente.getProfilo().getUtente())){
					if(presidenteRegione==null ||  !componente.getProfilo().getUtente().getId().equals(presidenteRegione.getUtente().getId())){
						vicePresidenteRegione = componente.getProfilo();
					}
				}
			}
			
//			Set<ComponentiGiunta> ordinati = new TreeSet<ComponentiGiunta>(new Comparator<ComponentiGiunta>() {
//				@Override
//				public int compare(ComponentiGiunta o1, ComponentiGiunta o2) {
//					//return (o1.getProfilo().getUtente().getNome() + o1.getProfilo().getUtente().getCognome() + o1.getProfilo().getId()).compareTo(o2.getProfilo().getUtente().getNome() + o2.getProfilo().getUtente().getCognome() + o2.getProfilo().getId());
//					if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(seduta.getOrgano())) {
//						Integer ordineGiunta1 = o1.getProfilo()!=null && o1.getProfilo().getOrdineGiunta()!=null?o1.getProfilo().getOrdineGiunta():0;
//						Integer ordineGiunta2 = o2.getProfilo()!=null && o2.getProfilo().getOrdineGiunta()!=null?o2.getProfilo().getOrdineGiunta():0;
//						
//						return ordineGiunta1.compareTo(ordineGiunta2);
//					}else {
//						Integer ordineCons1 = o1.getProfilo()!=null && o1.getProfilo().getOrdineConsiglio()!=null?o1.getProfilo().getOrdineConsiglio():0;
//						Integer ordineCons2 = o2.getProfilo()!=null && o2.getProfilo().getOrdineConsiglio()!=null?o2.getProfilo().getOrdineConsiglio():0;
//						
//						return ordineCons1.compareTo(ordineCons2);
//					}
//					}
//			});
//			ordinati.addAll(componenti);
//			componenti = Lists.newArrayList(ordinati);
			
			/* CIFRA-21 Rimozione di tutti i filtri sugli assessori
			boolean escludi = false;
			
			//si escludono segretario e presidente della seduta/provvedimento
			for(ComponentiGiunta componente : componenti){
				if(!escludi && segretario!=null && componente.getProfilo().getUtente().getId().equals(segretario.getUtente().getId())){
					escludi = true;
				}
				if(!escludi && presidenteProvvedimento!=null && componente.getProfilo().getUtente().getId().equals(presidenteProvvedimento.getUtente().getId())){
					escludi = true;
				}
				if(!escludi){
					componentiFiltrati.add(componente);
				}
				escludi = false;
			}
			*/
			
			//setto il flag per presidente regione e vicepresidente regione
			for(ComponentiGiunta componente : componenti){
				if(presidenteRegione!=null && presidenteRegione.getUtente().getId().equals(componente.getProfilo().getUtente().getId())){
					componente.setIsPresidenteRegione(true);
				}
				else if(vicePresidenteRegione!=null && vicePresidenteRegione.getUtente().getId().equals(componente.getProfilo().getUtente().getId())){
					componente.setIsVicePresidenteRegione(true);
				}
			}
			
			OrdineGiorno odg = aOdg!=null && aOdg.getOrdineGiorno()!=null ? aOdg.getOrdineGiorno() : new OrdineGiorno();

			
			
//			if(componenti!=null) {
//				for(ComponentiGiunta componente : componenti){
//					if(!stampaAssenti && (componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && (componente.getPresente()!=null && !componente.getPresente().booleanValue())) {
//						stampaAssenti=true;
//					}
//					if(!stampaAssentiIE && (componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && (componente.getPresenteIE()!=null && !componente.getPresenteIE().booleanValue())) {
//						stampaAssentiIE=true;
//					}
//				}
//			}
			
			
			
			
			
			contextRT.setVariable("componentiGiunta", componenti);
			
			List<ComponentiGiunta> assenti = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presenti = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> assentiDispari = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assentiPari = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> assenti1 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assenti2 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assenti3 = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> presentiDispari = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presentiPari = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> presenti1 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presenti2 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presenti3 = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> assentiIE = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presentiIE = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> assentiDispariIE = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assentiPariIE = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> assentiIE1 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assentiIE2 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> assentiIE3 = new ArrayList<ComponentiGiunta>();
			
			
			List<ComponentiGiunta> presentiDispariIE = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presentiPariIE = new ArrayList<ComponentiGiunta>();
			
			List<ComponentiGiunta> presentiIE1 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presentiIE2 = new ArrayList<ComponentiGiunta>();
			List<ComponentiGiunta> presentiIE3 = new ArrayList<ComponentiGiunta>();
			
//			boolean pariAssente = false;
//			boolean pariPresente = false;
//			
//			if(componenti!=null) {
//				for (int i = 0; i < componenti.size(); i++) {
//					ComponentiGiunta componente = componenti.get(i);
//						if(
//								(componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && 
//								(componente.getIsSegretarioInizio()==null || !componente.getIsSegretarioInizio().booleanValue()) &&
//								(componente.getIsSegretarioFine()==null || !componente.getIsSegretarioFine().booleanValue()) &&
//								(componente.getPresente()!=null && !componente.getPresente().booleanValue())
//							) {
//							if(pariAssente) {
//								assentiPari.add(componente);
//								pariAssente = false;
//							}else {
//								assentiDispari.add(componente);
//								pariAssente = true;
//							}
//							
//							
//						}else {
//							if(pariPresente) {
//								presentiPari.add(componente);
//								pariPresente = false;
//							}else {
//								presentiDispari.add(componente);
//								pariPresente = true;
//							}
//							
//						}
//					
//					
//					
//				}
//			}
			
			//conto gli assenti e i presenti
			int contaPresenti = 0;
			int contaPresentiIE = 0;
			int contaAssenti = 0;
			int contaAssentiIE = 0;
			
			boolean stampaAssenti = false;
			boolean stampaAssentiIE = false;
			
			boolean stampaSu3colonne = false;
			if(numeroColonneTabellaConsiglio!=null) {
				try {
					int colonne = Integer.parseInt(numeroColonneTabellaConsiglio);
					stampaSu3colonne = colonne == 3;
				} catch (Exception e) {
					
				}
			}
			contextRT.setVariable("stampaSu3colonne", stampaSu3colonne);
			
			if(componenti!=null) {
				for (int i = 0; i < componenti.size(); i++) {
					ComponentiGiunta componente = componenti.get(i);
					if(
							(componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && 
							(componente.getIsSegretarioInizio()==null || !componente.getIsSegretarioInizio().booleanValue()) &&
							(componente.getIsSegretarioFine()==null || !componente.getIsSegretarioFine().booleanValue()) &&
							(componente.getPresente()!=null && !componente.getPresente().booleanValue())
						) {
						contaAssenti=contaAssenti+1;
						assenti.add(componente);
					}else if(
							(componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && 
							(componente.getIsSegretarioInizio()==null || !componente.getIsSegretarioInizio().booleanValue()) &&
							(componente.getIsSegretarioFine()==null || !componente.getIsSegretarioFine().booleanValue()) &&
							(componente.getPresente()!=null && componente.getPresente().booleanValue())
						) {
						contaPresenti=contaPresenti+1;
						presenti.add(componente);
					}
					
					if(
							(componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && 
							(componente.getIsSegretarioInizio()==null || !componente.getIsSegretarioInizio().booleanValue()) &&
							(componente.getIsSegretarioFine()==null || !componente.getIsSegretarioFine().booleanValue()) &&
							(componente.getIsSegretarioIE()==null || !componente.getIsSegretarioIE().booleanValue()) &&
							(componente.getPresenteIE()!=null && !componente.getPresenteIE().booleanValue())
						) {
						contaAssentiIE=contaAssentiIE+1;
						assentiIE.add(componente);
					}else if(
							(componente.getIsSindaco()==null || !componente.getIsSindaco().booleanValue()) && 
							(componente.getIsSegretarioInizio()==null || !componente.getIsSegretarioInizio().booleanValue()) &&
							(componente.getIsSegretarioFine()==null || !componente.getIsSegretarioFine().booleanValue()) &&
							(componente.getIsSegretarioIE()==null || !componente.getIsSegretarioIE().booleanValue()) &&
							(componente.getPresenteIE()!=null && componente.getPresenteIE().booleanValue())
						) {
						contaPresentiIE=contaPresentiIE+1;
						presentiIE.add(componente);
					}
				}
			}
			
			if(contaAssenti>0 && assenti.size()>0) {
				stampaAssenti=true;
				
				if(!stampaSu3colonne) {
					int componentiPrimaColonna = contaAssenti%2==0?contaAssenti/2:(contaAssenti+1)/2;
					for (int i = 0; i < assenti.size(); i++) {
						if(i+1<=componentiPrimaColonna) {
							assentiDispari.add(assenti.get(i));
						}else {
							assentiPari.add(assenti.get(i));
						}
					}
				}else {
				
					int componentiColonna1di3 = contaAssenti%3==0?contaAssenti/3:(contaAssenti+2)/3;
					int restanti = contaAssenti - componentiColonna1di3;
					int componentiColonna2di3 = restanti%2==0?restanti/2:(restanti+1)/2;
					
					for (int i = 0; i < assenti.size(); i++) {
						if(i+1<=componentiColonna1di3) {
							assenti1.add(assenti.get(i));
						}else if(i+1<=componentiColonna1di3+componentiColonna2di3) {
							assenti2.add(assenti.get(i));
						}else {
							assenti3.add(assenti.get(i));
						}
					}
				}
				
				
				
			}
			
			if(contaPresenti>0 && presenti.size()>0) {
				
				if(!stampaSu3colonne) {
					int componentiPrimaColonna = contaPresenti%2==0?contaPresenti/2:(contaPresenti+1)/2;
					for (int i = 0; i < presenti.size(); i++) {
						if(i+1<=componentiPrimaColonna) {
							presentiDispari.add(presenti.get(i));
						}else {
							presentiPari.add(presenti.get(i));
						}
					}
				}else {
					int componentiColonna1di3 = contaPresenti%3==0?contaPresenti/3:(contaPresenti+2)/3;
					int restanti = contaPresenti - componentiColonna1di3;
					int componentiColonna2di3 = restanti%2==0?restanti/2:(restanti+1)/2;
					
					for (int i = 0; i < presenti.size(); i++) {
						if(i+1<=componentiColonna1di3) {
							presenti1.add(presenti.get(i));
						}else if(i+1<=componentiColonna1di3+componentiColonna2di3) {
							presenti2.add(presenti.get(i));
						}else {
							presenti3.add(presenti.get(i));
						}
					}
				}
			}
			
			if(contaAssentiIE>0 && assentiIE.size()>0) {
				stampaAssentiIE=false;//per adesso non visualizziamo gli assenti per la IE
				if(!stampaSu3colonne) {
					int componentiPrimaColonna = contaAssentiIE%2==0?contaAssentiIE/2:(contaAssentiIE+1)/2;
					for (int i = 0; i < assentiIE.size(); i++) {
						if(i+1<=componentiPrimaColonna) {
							assentiDispariIE.add(assentiIE.get(i));
						}else {
							assentiPariIE.add(assentiIE.get(i));
						}
					}
				}else {
					int componentiColonna1di3 = contaAssentiIE%3==0?contaAssentiIE/3:(contaAssentiIE+2)/3;
					int restanti = contaAssentiIE - componentiColonna1di3;
					int componentiColonna2di3 = restanti%2==0?restanti/2:(restanti+1)/2;
					
					for (int i = 0; i < assentiIE.size(); i++) {
						if(i+1<=componentiColonna1di3) {
							assentiIE1.add(assentiIE.get(i));
						}else if(i+1<=componentiColonna1di3+componentiColonna2di3) {
							assentiIE2.add(assentiIE.get(i));
						}else {
							assentiIE3.add(assentiIE.get(i));
						}
					}
				}
			}
			
			if(contaPresentiIE>0 && presentiIE.size()>0) {
				
				if(!stampaSu3colonne) {
					int componentiPrimaColonna = contaPresentiIE%2==0?contaPresentiIE/2:(contaPresentiIE+1)/2;
					for (int i = 0; i < presentiIE.size(); i++) {
						if(i+1<=componentiPrimaColonna) {
							presentiDispariIE.add(presentiIE.get(i));
						}else {
							presentiPariIE.add(presentiIE.get(i));
						}
					}
				}else {
					int componentiColonna1di3 = contaPresentiIE%3==0?contaPresentiIE/3:(contaPresentiIE+2)/3;
					int restanti = contaPresentiIE - componentiColonna1di3;
					int componentiColonna2di3 = restanti%2==0?restanti/2:(restanti+1)/2;
					
					for (int i = 0; i < presentiIE.size(); i++) {
						if(i+1<=componentiColonna1di3) {
							presentiIE1.add(presentiIE.get(i));
						}else if(i+1<=componentiColonna1di3+componentiColonna2di3) {
							presentiIE2.add(presentiIE.get(i));
						}else {
							presentiIE3.add(presentiIE.get(i));
						}
					}
				}
			}
			
			contextRT.setVariable("stampaAssenti", stampaAssenti);
			contextRT.setVariable("stampaAssentiIE", stampaAssentiIE);
			
			contextRT.setVariable("assentiDispari", assentiDispari);
			contextRT.setVariable("assentiPari", assentiPari);
			contextRT.setVariable("assenti1", assenti1);
			contextRT.setVariable("assenti2", assenti2);
			contextRT.setVariable("assenti3", assenti3);
			contextRT.setVariable("presentiDispari", presentiDispari);
			contextRT.setVariable("presentiPari", presentiPari);
			contextRT.setVariable("presenti1", presenti1);
			contextRT.setVariable("presenti2", presenti2);
			contextRT.setVariable("presenti3", presenti3);
			
			contextRT.setVariable("assentiDispariIE", assentiDispariIE);
			contextRT.setVariable("assentiPariIE", assentiPariIE);
			
			contextRT.setVariable("assentiIE1", assentiIE1);
			contextRT.setVariable("assentiIE2", assentiIE2);
			contextRT.setVariable("assentiIE3", assentiIE3);
			
			contextRT.setVariable("presentiDispariIE", presentiDispariIE);
			contextRT.setVariable("presentiPariIE", presentiPariIE);
			
			contextRT.setVariable("presentiIE1", presentiIE1);
			contextRT.setVariable("presentiIE2", presentiIE2);
			contextRT.setVariable("presentiIE3", presentiIE3);
			
			
		
			contextRT.setVariable("seduta", seduta);
			
			if(seduta.getInizioLavoriEffettiva()!=null) {
				String formatoData = WebApplicationProps.getProperty(ConfigPropNames.FORMATO_DATA_SEDUTA_TESTUALE);
				if(formatoData!=null && formatoData.contains("#anno#") && formatoData.contains("#mese#") && formatoData.contains("#giorno#")) {
					
					try {
						String dataInFormatoTestuale = DateUtil.conversioneDataInFormatoTestuale(seduta.getInizioLavoriEffettiva(), formatoData);
						contextRT.setVariable("dataInFormatoTestuale", dataInFormatoTestuale);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
				}
			}
			
			
			
			
			if(aOdg!=null && aOdg.getVotazioneIE() !=null && aOdg.getVotazioneIE()) {
				AttiOdgWithIEDto aOdgWithIE = AttiOdgWithIEDtoTransformer.toAttiOdgWithIEDto(aOdg);
				contextRT.setVariable("attiOdg", aOdgWithIE);
			}else {
				contextRT.setVariable("attiOdg", aOdg);
			}
			contextRT.setVariable("odg", odg);
			
			if(numeroPagineCopiaConforme>0) {
				contextRT.setVariable("numeroPagineCopiaConforme", numeroPagineCopiaConforme);
			}
			
			
			
			
			String htmlRT = creaTemplatePreview(reportDto, contextRT);
			
			return executePreviewAtto(htmlRT, (Atto) atto, reportDto, 
					false, true, true);
			
			// TODO: verificare 
			// return executePreviewDeliberaGiunta(atto, componenti, odg, seduta, reportDto);
		// }

	}
	
	private String getDecodificaParere(Parere parere) {
		
		
		
		if(parere!=null) {
			if(parere.getParereSintetico()!=null && parere.getParereSintetico().toLowerCase().contains("personalizzato")) {
				return parere.getParerePersonalizzato();
			}
			
			List<EsitoPareri> lst = esitoPareriService.findByCodice(parere.getParereSintetico());
			
			if(lst!=null && lst.size()>0) {
				EsitoPareri esitoPareri = lst.get(0);
				return esitoPareri.getValore();
			}
			return parere.getParereSintetico();
		}
		return "";
	}

	public List<ImpegnoDaStampareDto> getlistaImpegniDaStampare(List<DettaglioLiquidazioneDto> listDettLiquidazione) {
		List<ImpegnoDaStampareDto> listaImpegniDaStampare = new ArrayList<ImpegnoDaStampareDto>();
		
		if(listDettLiquidazione != null) {
			for (DettaglioLiquidazioneDto dettaglioLiquidazioneDto: listDettLiquidazione) {
				
				if(dettaglioLiquidazioneDto!=null && dettaglioLiquidazioneDto.getListaCapitoli()!=null && dettaglioLiquidazioneDto.getListaCapitoli().size() >0) {
					
					for (RigaLiquidazioneDto rigaLiquidazioneDto :  dettaglioLiquidazioneDto.getListaCapitoli()) {
					
						
						for (LiquidazioneImpegnoDto liquidazioneImpegnoDto :  rigaLiquidazioneDto.getListaImpegni()) {
						
							ImpegnoDaStampareDto riga = new ImpegnoDaStampareDto();
							riga.setCodiceCapitolo(rigaLiquidazioneDto.getCapitolo());
							riga.setMeccanograficoCapitolo(rigaLiquidazioneDto.getMeccanografico());
							riga.setDescrizioneCapitolo(rigaLiquidazioneDto.getDescr());
							riga.setImportoCapitolo(rigaLiquidazioneDto.getImportoTotaleCapitolo());
							riga.setCodiceImpegno(liquidazioneImpegnoDto.getNumero());
							riga.setImpegnoFormattato(liquidazioneImpegnoDto.getImpFormattato());
							String datiAtto = "Atto "+liquidazioneImpegnoDto.getTipoProvImp()+"/"+liquidazioneImpegnoDto.getOrganoProvImp()+"/"+liquidazioneImpegnoDto.getAnnoProvImp()+"/"+liquidazioneImpegnoDto.getNumeroProvImp() +(liquidazioneImpegnoDto.getDataEsecProvImp()!=null && !liquidazioneImpegnoDto.getDataEsecProvImp().trim().isEmpty() ? " efficace dal "+liquidazioneImpegnoDto.getDataEsecProvImp() : "");
							riga.setDatiAtto(datiAtto);
							riga.setTipoProvImp(liquidazioneImpegnoDto.getTipoProvImp());
							riga.setOrganoProvImp(liquidazioneImpegnoDto.getOrganoProvImp());
							riga.setAnnoProvImp(liquidazioneImpegnoDto.getAnnoProvImp());
							riga.setNumeroProvImp(liquidazioneImpegnoDto.getNumeroProvImp());
							riga.setDataEsecProvImp(liquidazioneImpegnoDto.getDataEsecProvImp());
							riga.setDescrizioneImpegno(liquidazioneImpegnoDto.getDescr());
							riga.setImportoImpegno(liquidazioneImpegnoDto.getImportoTotaleImpegno());
							riga.setCupImpegno(liquidazioneImpegnoDto.getCodiceCUP());
							riga.setCigImpegno(liquidazioneImpegnoDto.getCodiceCIG());
							riga.setListaSoggetti(liquidazioneImpegnoDto.getListaSogg());
							riga.setAnnoLiq(dettaglioLiquidazioneDto.getAnnoLiq());
							riga.setNumeroLiq(dettaglioLiquidazioneDto.getNumeroLiq());
							listaImpegniDaStampare.add(riga);

						}
					}
				}
				
				
			}
		}
		
		return listaImpegniDaStampare;
	}

	/*
	private boolean getNascondiUrlSitoESpostaDatiIntestazione(
			ModelloHtml modelloHtml) {
		boolean out = false;
		if(modelloHtml!=null){
			String codiceTipoDocumento = "";
			if(modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null){
				codiceTipoDocumento = modelloHtml.getTipoDocumento().getCodice();
			}
			if(!codiceTipoDocumento.equals("")){
				out = codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_schema_disegno_legge.name()) 
						|| codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_presa_d_atto.name()) 
						|| codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_comunicazione.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_verbalizzato.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_disegno_legge.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_approvato.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_delibera_giunta.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_delibera_consiglio.name());
			}
		}
		return out;
	}
	*/
	
	/*
	private boolean getAggiungiRefertoTecnico(ModelloHtml modelloHtml) {
		boolean out = false;
		if(modelloHtml!=null){
			String codiceTipoDocumento = "";
			if(modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null){
				codiceTipoDocumento = modelloHtml.getTipoDocumento().getCodice();
			}
			if(!codiceTipoDocumento.equals("")){
				out = codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_schema_disegno_legge.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_presa_d_atto.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_disegno_legge.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_approvato.name());
			}
		}
		return out;
	}
	*/
	
//	private boolean getStampaIntestazioneSoloFrontespizio_OLD(
//			ModelloHtml modelloHtml) {
//		boolean out = false;
//		if(modelloHtml!=null){
//			if(modelloHtml.getTipoAtto()!=null && modelloHtml.getTipoAtto().getCodice()!=null){
//				String cod = modelloHtml.getTipoAtto().getCodice();
//				out = cod.equals("SDL") || cod.equals("SDL_A") || cod.equals("COM") ||
//						cod.equals("COM_A") || cod.equals("DDL") || cod.equals("DDL_A") || cod.equals("DEL") || cod.equals("DEL_A");
//			}
//		}
//		return out;
//	}
	
	/*
	private boolean getStampaIntestazioneSoloFrontespizio(
			ModelloHtml modelloHtml) {
		boolean out = false;
		if(modelloHtml!=null){
			String codiceTipoDocumento = "";
			if(modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null){
				codiceTipoDocumento = modelloHtml.getTipoDocumento().getCodice();
			}
			
			if(!codiceTipoDocumento.equals("")){
				out = codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_schema_disegno_legge.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_presa_d_atto.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_comunicazione.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_verbalizzato.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_disegno_legge.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_approvato.name()) || 
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_delibera_giunta.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_proposta_delibera_consiglio.name()) ||
						codiceTipoDocumento.equals(TipoDocumentoEnum.tipo_provvedimento_adottato.name());
			}
		}
		return out;
	}
	*/
	
//	private boolean getSoloLogoCentrale_old(ModelloHtml modelloHtml){
//		boolean out = false;
//		if(modelloHtml!=null){
//			if(modelloHtml.getTipoAtto()!=null && modelloHtml.getTipoAtto().getCodice()!=null){
//				String cod = modelloHtml.getTipoAtto().getCodice();
//				out =cod.equals("DDL_A") || cod.equals("DEL_A");
//			}
//		}
//		return out;
//	}
	
	/*
	private boolean getSoloLogoCentrale(ModelloHtml modelloHtml){
		boolean out = false;
		if(modelloHtml!=null){
			if(modelloHtml.getTipoDocumento()!=null && modelloHtml.getTipoDocumento().getCodice()!=null){
				String cod = modelloHtml.getTipoDocumento().getCodice();
				
				out =cod.equals(TipoDocumentoEnum.tipo_provvedimento_approvato.name()) || cod.equals(TipoDocumentoEnum.tipo_provvedimento_adottato.name());
			}
		}
		return out;
	}
	*/

//	private boolean stampaLogoConDicitura(
//			ModelloHtml modelloHtml) {
//		boolean out = false;
//		if(modelloHtml!=null){
//			if(modelloHtml.getTipoAtto()!=null && modelloHtml.getTipoAtto().getCodice()!=null){
//				String cod = modelloHtml.getTipoAtto().getCodice();
//				out = cod.equals("COM_A") || cod.equals("DIR") || cod.equals("DIR");
//			}
//		}
//		return out;
//	} 
	
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	@Transactional(readOnly = true)
	public File previewParere(Parere object, ReportDTO reportDto, Atto atto) throws IOException, DocumentException, DmsException {
		log.debug("preview:parere:" + reportDto);
		String tag = "parere";
//		Object a = atto.getOrdineGiornos() != null ? atto.getOrdineGiornos().size() : null;
		
		if (object == null) {
			throw new NullPointerException("Si sta tentando di effettuare la preview di un parere ma il parere risulta null");
		}
		File inputFile = executePreviewParere(tag, object, reportDto, atto, null);
		return inputFile;
	}
	
	@Transactional(readOnly = true)
	public File previewParere(Parere object, ReportDTO reportDto, Atto atto, List<AttiOdg> odgs) throws IOException, DocumentException, DmsException {
		log.debug("preview:parere:" + reportDto);
		String tag = "parere";
		SedutaGiunta seduta = this.getSedutaFromAtto(atto, odgs);
		if (seduta == null)
			log.warn(String.format("previewParere :: seduta is null per l'atto con id=%s", atto.getId()));
		
		if (object == null) {
			throw new NullPointerException("Si sta tentando di effettuare la preview di un parere ma il parere risulta null");
		}
		File inputFile = executePreviewParere(tag, object, reportDto, atto, seduta);
		return inputFile;
	}
	

	public File previewAttoInesistente(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		log.debug("preview:atto:" + reportDto);
		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, contextRT);
		return executePreviewAttoInesistente(htmlRT, atto, reportDto);
	}

	@Transactional(readOnly = true)
	private File executePreviewAttoInesistente(String htmlRT, Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		
		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();
		
		String banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo,null, urlSitoEnte, false, false, false);
		
		IOUtils.closeQuietly(input);
		
		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));
		
		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;
		
		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);
			
			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}
		
		pdfStamper.close();
		pdfReader.close();
		
		return inputFile;
	}
	
	@Transactional(readOnly = true)
	private File executePreviewRestituzioneSuIstanzaUfficioProponente(String htmlRT, Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		
		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();
		
		String banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo ,null, urlSitoEnte, false, false, false);
		
		IOUtils.closeQuietly(input);
		
		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));
		
		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;
		
		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);
			
			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}
		
		pdfStamper.close();
		pdfReader.close();
		
		return inputFile;
	}
	
	public File previewSchedaAnagraficoContabile(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewschedaAnagraficoContabile:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewSchedaAnagraficoContabile(atto, reportDto);
		return inputFile;
	}
	

	@Transactional(readOnly = true)
	private File executePreviewSchedaAnagraficoContabile(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();
		
		String banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo, atto.getUfficio(), urlSitoEnte, false, false, false);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();

		// FileUtils.copyInputStreamToFile(input, inputFile);
		// IOUtils.closeQuietly(input);
		return inputFile;
	}
	@Transactional
	public File previewParere(ReportDTO reportDto, Parere parere) throws IOException, DocumentException, DmsException {
		return previewParere(parere, reportDto, parere.getAtto());
	}
	
	@Transactional
	public File previewParereRitiro(ReportDTO reportDto, Parere parere, Atto atto, List<AttiOdg> odgs) throws IOException, DocumentException, DmsException {
		return previewParere(parere, reportDto, atto, odgs);
	}
	
	public File previewPresenzeAssenze(Resoconto object,Profilo profilo, SedutaGiunta seduta, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		log.debug("preview:presenzeassenze:" + reportDto);
		if (object == null) {
			throw new DocumentException();
		}
		
//		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
//		if (!modelloHtml.getTipoDocumento().getCodice().equalsIgnoreCase(reportDto.getTipoDoc())) {
//			throw new DocumentException("Object Not Found");
//		}

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("sedutaGiunta", seduta);
		RilevazioneAssenzaDto rilevazioneAssenzaDto = new RilevazioneAssenzaDto();
		List<ComponentiGiunta> componentiGiunta = new ArrayList<ComponentiGiunta>();
		log.debug("Ordini del Giorno length:" + seduta.getOdgs().size());
		for(OrdineGiorno odg : seduta.getOdgs()) {
			List<ComponentiGiunta> tempC = componentiGiuntaRepository.findByOrdineGiorno(odg);
			log.debug("Componenti Giunta length:" + tempC.size());
			for(ComponentiGiunta tempA : tempC){
				if(Boolean.TRUE.equals(tempA.getIsPresidenteFine()) && 
				   Boolean.TRUE.equals(tempA.getIsSegretarioFine())) {
					componentiGiunta.add(tempA);
				}
			}
			//componentiGiunta.addAll(tempC);
		}
		
		log.debug("Componenti Giunta final length:" + componentiGiunta.size());
		rilevazioneAssenzaDto.setProgressivoDiInizio(utilityService.getProgressivoInizio(componentiGiunta));
		rilevazioneAssenzaDto.setProgressivoDiFine(utilityService.getProgressivoFine(componentiGiunta));
		rilevazioneAssenzaDto.setRigheAssenti(utilityService.getRigheAssentiDto(componentiGiunta));
		contextRT.setVariable("rilevazioneAssenzaDto", rilevazioneAssenzaDto);
		
		String htmlRT = creaTemplatePreview(reportDto, contextRT);
				
		File inputFile = executePreviewComunicazioneSeduta(htmlRT, seduta, profilo, reportDto);
		return inputFile;
	}
	*/
	
	@Transactional
	public File previewAnnullamentoSeduta(SedutaGiunta seduta,Profilo profilo, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		log.debug("preview:seduta:annulla:" + reportDto);
		if (seduta == null) {
			throw new DocumentException();
		}

//		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("sedutaGiunta", seduta);
		String htmlRT = creaTemplatePreview(reportDto, contextRT);
		
		File inputFile = executePreviewComunicazioneSeduta(htmlRT, seduta, profilo, reportDto);
		return inputFile;
	}
	
	@Transactional
	public File previewAnnullamentoSeduta(long idSeduta,Profilo profilo, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(idSeduta);
		return previewVariazioneSeduta(seduta, profilo, reportDto);
	}
	
	@Transactional
	public File previewVariazioneSeduta(SedutaGiunta seduta, Profilo profilo, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		log.info("previewVariazioneSeduta start:" + reportDto);
		if (seduta == null) {
			throw new DocumentException();
		}

//		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("sedutaGiunta", seduta);
		log.info("previewVariazioneSeduta-creaTemplatePreview:starts");
		String htmlRT = creaTemplatePreview(reportDto, contextRT);
		log.info("previewVariazioneSeduta-creaTemplatePreview:ends");
		log.info("previewVariazioneSeduta-executePreviewComunicazioneSeduta:starts");
		File inputFile = executePreviewComunicazioneSeduta(htmlRT, seduta, profilo, reportDto);
		log.info("previewVariazioneSeduta-executePreviewComunicazioneSeduta:ends");
		return inputFile;
	}
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	public File previewPresenzeAssenze(ReportDTO reportDto, final Long idProfilo) throws IOException, DocumentException, DmsException {
		Profilo profilo = profiloRepository.findOne(idProfilo);
		OrdineGiorno odg = ordineGiornoRepository.getOne(reportDto.getIdAtto());
		Resoconto resoconto = getResoconto(odg.getSedutaGiunta(), 2, true);

		return previewPresenzeAssenze(resoconto, profilo, odg.getSedutaGiunta(), reportDto);
	}

	public File previewResoconto(ReportDTO reportDto) throws IOException, DocumentException {
		OrdineGiorno object = ordineGiornoRepository.getOne(reportDto.getIdAtto());
		Resoconto resoconto = getResoconto(object.getSedutaGiunta(), 1, true);
		List<OrdineGiorno> odgs = new ArrayList<OrdineGiorno>(object.getSedutaGiunta().getOdgs());

		return previewResoconto(resoconto, odgs, reportDto);
	}
	*/

//	public File previewVerbale(ReportDTO reportDto) throws IOException, DocumentException, DmsException {
//		return previewVerbale(null, null, reportDto);
//	}
	
	
	public File previewVerbale(ReportDTO reportDto) throws IOException, DocumentException {
		
		try {
//			Resoconto resoconto;
//			if (TipoResocontoEnum.DOCUMENTO_DEF_ESITO.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
//				resoconto = createResoconto(
//						reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ESITO, 
//	    				reportDto.getIdModelloHtml());
//	    	}
//	    	else if (TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
//	    		resoconto = createResoconto( 
//	    				reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI, 
//	    				reportDto.getIdModelloHtml());
//	    	}
//	    	else {
//	    		throw new CifraCatchedException("Occorre specificare il tipo di Documento Resoconto:"
//	    				+ " doc-definitivo-esito oppure doc-definitivo-elenco-verbali");
//	    	}
//			
	    	
			SedutaGiunta sedutaGiunta = sedutaGiuntaRepository.findOne(reportDto.getIdSeduta());
			List<OrdineGiorno> odgs =  ordineGiornoRepository.findBySeduta(reportDto.getIdSeduta()); //   new ArrayList<OrdineGiorno>(resoconto.getSedutaGiunta().getOdgs());
			List<AttiOdg> attiOdgList = new ArrayList<AttiOdg>();
			OrdineGiorno odgBase = null;
			Map<String, String> votazioneLabels = new HashMap<String, String>();
			
			for (OrdineGiorno ordineGiorno : odgs) {
				if (ordineGiorno.getTipoOdg().getId() == 1 || ordineGiorno.getTipoOdg().getId() == 2) {
					odgBase = ordineGiorno;
				}
				//Add Odg Base
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getAtto().getEsito() != null) {
						if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 1 || 
								   attiOdg.getOrdineGiorno().getTipoOdg().getId() == 2 ) {
									attiOdgList.add(attiOdg);
								}
					}
					
				}
				//Add Odg Suppletivo
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getAtto().getEsito() != null) {
						if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 3 ) {
							attiOdgList.add(attiOdg);
						}
					}
				}
				//Add Odg Fuori Sacco
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getAtto().getEsito() != null) {
						if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 4) {
							attiOdgList.add(attiOdg);
						}
					}
				}
			}
			
			boolean isAttiOdgOrdered = true;
			for(AttiOdg attoOdg: attiOdgList) {
				if (((attoOdg.getNumeroDiscussione() == null) || (attoOdg.getNumeroDiscussione().intValue() < 1))) {
					isAttiOdgOrdered = false;
					break;
				}
			}
			
			if(isAttiOdgOrdered) {
				//ordino atti in base al numero discussione
				Collections.sort(attiOdgList,new Comparator<AttiOdg>(){
					@Override
				    public int compare(AttiOdg atto1, AttiOdg atto2){
						return atto1.getNumeroDiscussione().compareTo(atto2.getNumeroDiscussione());
					}
				});
			}else {
				int valNumeroDiscussione = 1;
				for(AttiOdg attoOdg: attiOdgList) {
					if (((attoOdg.getNumeroDiscussione() == null) || (attoOdg.getNumeroDiscussione().intValue() < 1))) {
						attoOdg.setNumeroDiscussione(valNumeroDiscussione);
						valNumeroDiscussione++;
					}
				}
			}
			
			if(attiOdgList!=null && attiOdgList.size() > 0) {
				for(AttiOdg attiOdg : attiOdgList) {
					if(attiOdg.getComponenti()!=null && attiOdg.getComponenti().size() > 0) {
						for(ComponentiGiunta c : attiOdg.getComponenti()) {
							if(c!=null && c.getVotazione() != null && !c.getVotazione().trim().isEmpty() && !votazioneLabels.containsKey(c.getVotazione().trim())) {
								votazioneLabels.put(c.getVotazione().trim(), WebApplicationProps.getProperty("message.votazione." + c.getVotazione().trim(), c.getVotazione().trim()));
							}
							if(c!=null && c.getVotazioneIE() != null && !c.getVotazioneIE().trim().isEmpty() && !votazioneLabels.containsKey(c.getVotazioneIE().trim())) {
								votazioneLabels.put(c.getVotazioneIE().trim(), WebApplicationProps.getProperty("message.votazione." + c.getVotazioneIE().trim(), c.getVotazioneIE().trim()));
							}
						}
					}
				}
			}
			
			List<Esito> esiti = esitoRepository.findAll();
			
			Context context = new Context(Locale.forLanguageTag(IT));
			context.setVariable("votazioneLabels", votazioneLabels);
			context.setVariable("odg", odgBase);
			context.setVariable("attiOdg", attiOdgList);
			context.setVariable("sedutaGiunta", sedutaGiunta);
			context.setVariable("esiti", esiti);
			Verbale verbale = verbaleRepository.getOne(reportDto.getIdSeduta());
			context.setVariable("verbale", verbale);
			
			//TODO:Gestione fuso orario da centralizzare con custom tag su thymeleaf
			DateTime inizioLavoriEffettiva = formatTime(odgBase.getSedutaGiunta().getInizioLavoriEffettiva());
			context.setVariable("inizioLavoriEffettiva", inizioLavoriEffettiva);
			
			
			String htmlRT = creaTemplatePreview(reportDto, context);

			ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + sedutaGiunta.getNumero()), null);
//			InputStream inputHtml = result.getInputStream();
			File file = File.createTempFile("withfiligrana_", ".pdf");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
			
		}
		catch (Exception e) {
			// Sollevo RuntimeException altrimenti Spring non annulla la transazione e gli stati diventano disallineati
			throw new RuntimeException("Errore durante la generazione anteprima Resoconto.", e);
		}
	}
	
public File previewVariazioneEstremiSeduta(ReportDTO reportDto) throws IOException, DocumentException {
		
		try {
//			Resoconto resoconto;
//			if (TipoResocontoEnum.DOCUMENTO_DEF_ESITO.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
//				resoconto = createResoconto(
//						reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ESITO, 
//	    				reportDto.getIdModelloHtml());
//	    	}
//	    	else if (TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
//	    		resoconto = createResoconto( 
//	    				reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI, 
//	    				reportDto.getIdModelloHtml());
//	    	}
//	    	else {
//	    		throw new CifraCatchedException("Occorre specificare il tipo di Documento Resoconto:"
//	    				+ " doc-definitivo-esito oppure doc-definitivo-elenco-verbali");
//	    	}
//			
	    	
			SedutaGiunta sedutaGiunta = sedutaGiuntaRepository.findOne(reportDto.getIdSeduta());
			
			
			Context context = new Context(Locale.forLanguageTag(IT));
			context.setVariable("sedutaGiunta", sedutaGiunta);
			
			//TODO:Gestione fuso orario da centralizzare con custom tag su thymeleaf
			
			String htmlRT = creaTemplatePreview(reportDto, context);

			ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + sedutaGiunta.getNumero()), null);
			
			byte[] b = serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner());
			InputStream inputBanner = new ByteArrayInputStream(b);
			InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
			InputStream input = result.getInputStream();
			log.info("executePreviewComunicazioneSeduta-addFiligrana");
			File filefiligrana = cartaStampataService.addFiligrana(
					input, inputBanner, inputLogo, 
					null, null, urlSitoEnte, false, false, false);
			log.info("executePreviewComunicazioneSeduta-addFiligrana ends");
			IOUtils.closeQuietly(input);
			return aggiungiAllegati(filefiligrana, null, reportDto.getOmissis());
			
			
			
//			InputStream inputHtml = result.getInputStream();
//			File file = File.createTempFile("withfiligrana_", ".pdf");
//			FileOutputStream fop = new FileOutputStream(file);
//			fop.write(result.getByteArray());
//			fop.flush();
//			fop.close();
//			return file;
			
		}
		catch (Exception e) {
			// Sollevo RuntimeException altrimenti Spring non annulla la transazione e gli stati diventano disallineati
			throw new RuntimeException("Errore durante la generazione anteprima Resoconto.", e);
		}
	}
	

	public File previewVerbale(Verbale verbale, OrdineGiorno odgFuoriSacco, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		log.debug("preview:verbale:" + reportDto);
		
		File inputFile = executePreviewVerbale(verbale, odgFuoriSacco, reportDto);
		return inputFile;
	}

	/*
	 * TODO: In ATTICO non previsti
	 *
	public File previewLettera(ReportDTO reportDto) throws IOException, DocumentException {
		return previewLettera(null, reportDto);
	}

	public File previewLettera(Object object, ReportDTO reportDto) throws IOException, DocumentException {
		log.debug("preview:lettera:" + reportDto);
		String tag = "lettera";
		if (object == null) {
			object = letteraRepository.getOne(reportDto.getIdAtto());
		}
		File inputFile = executeGenericPreview(tag, object, reportDto);
		return inputFile;
	}
	*/

	public File previewOrdinegiorno(ReportDTO reportDto) throws IOException, DocumentException {
		return previewOrdinegiorno(null, reportDto);
	}

	// public File previewOrdinegiorno(Object object, ReportDTO reportDto) throws IOException,
	// DocumentException {
	// log.debug( "preview:lettera:"+ reportDto);
	// String tag = "ordinegiorno";
	// if(object== null){
	// object = ordineGiornoRepository.getOne(reportDto.getIdAtto());
	// }
	// File inputFile = executeGenericPreview(tag , object, reportDto);
	// return inputFile;
	// }
	public File previewOrdinegiorno(Object object, ReportDTO reportDto) throws IOException, DocumentException {
		log.debug("preview:ordinegiorno:" + reportDto);
		if (object == null) {
			object = ordineGiornoRepository.findOne(reportDto.getIdAtto());
		}

		OrdineGiorno odg = (OrdineGiorno) object;
		
		
		//per ogni atto all'interno dell'odg carico i task attivi che servono per la lista delle commmissioni che non esprimo il parere
		if(odg!=null && odg.getAttos()!=null) {
			for (int i = 0; i < odg.getAttos().size(); i++) {
				workflowService.loadAllTasks(odg.getAttos().get(i).getAtto());
				if(odg.getAttos().get(i).getAtto().getTaskAttivi()!=null) {
					List l = odg.getAttos().get(i).getAtto().getTaskAttivi();
					for(Object dto : odg.getAttos().get(i).getAtto().getTaskAttivi()) {
						if(dto!=null && dto instanceof StatoAttoDTO) {
							if(((StatoAttoDTO)dto).getAoo()!=null && ((StatoAttoDTO)dto).getAoo().getCodice()!=null) {
								for(Parere p : odg.getAttos().get(i).getAtto().getPareri()) {
									if(p.getAoo().getCodice().equals(((StatoAttoDTO)dto).getAoo().getCodice())) {
										l.remove(dto);
										break;
									}
								}
								if(!l.contains(dto)) {
									break;
								}
							}
						}
					}
					odg.getAttos().get(i).getAtto().setTaskAttivi(l);
				}
			}
		}
		
		
		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odg.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.ordinegiornogiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.ordinegiornoconsiglio.name();
		}
		reportDto.setTipoDoc(tipoDocCodice);

		File inputFile = null;
		if (ordineGiornoService.isOdGBase(odg)) {
			inputFile = executePreviewOrdinegiornoBase(odg.getSedutaGiunta(), odg, reportDto);
		}
		else {
			SedutaGiunta seduta = odg.getSedutaGiunta();
			OrdineGiorno odgBase = sedutaGiuntaService.getOdgBase(seduta);
			//File docOdgBase = ordineGiornoService.recuperaDocumentoOdg(odgBase, false);
			File docOdgBase = executePreviewOrdinegiornoBase(seduta, odgBase, reportDto);

			if (ordineGiornoService.isOdGSuppletivo(odg)) {
				File docSuppletivoPrecedente = null;
				
				//scorro la lista degli odg per vedere se ci sono suppletivi precedenti a quello in corso
				for (Iterator<OrdineGiorno> iterator = seduta.getOdgs().iterator(); iterator.hasNext();) {
					OrdineGiorno ordineGiorno = (OrdineGiorno) iterator.next();
					if(ordineGiorno.getId().longValue() != odg.getId().longValue() && ordineGiornoService.isOdGSuppletivo(ordineGiorno)){
						docSuppletivoPrecedente = executePreviewOrdinegiornoSuppletivo(seduta, docOdgBase, docSuppletivoPrecedente, odg, reportDto);
					}else if(ordineGiorno.getId().longValue() == odg.getId().longValue()){
						break;
					}
					
				}
				
				inputFile = executePreviewOrdinegiornoSuppletivo(seduta, docOdgBase, docSuppletivoPrecedente, odg, reportDto);
			}
			else if (ordineGiornoService.isOdGFuoriSacco(odg)) {
				
				inputFile = executePreviewOrdinegiornoFuoriSacco(seduta, docOdgBase, odg, reportDto);
			}
		}

		return inputFile;
	}

	public File previewGiacenza(List<Atto> listAtto, ReportDTO reportDto) throws IOException, DocumentException {
		log.debug("previewGiacenza:" + reportDto);
		OrdineGiorno odg = new OrdineGiorno();
		//odg.setNumeroOdg("");
		List<AttiOdg> attiOdg = new ArrayList<AttiOdg>();
		for (Atto a : listAtto) {
			AttiOdg aO = new AttiOdg();
			
			String esitoAtto = a.getEsito();
			
			// TODO: modificare in base a esito seduta
			if(!StringUtil.isNull(esitoAtto)) { // && (tipoProvv == null)){
				aO.setSezione(1);
				if(a.getUsoEsclusivo() != null && a.getUsoEsclusivo().equals("sanita")){
					aO.setParte(2);
				} else {
					aO.setParte(1);
				}
			} 
			else{
				aO.setSezione(2);
				if(a.getUsoEsclusivo() != null && a.getUsoEsclusivo().equals("sanita")){
					aO.setParte(2);
				} else {
					aO.setParte(1);
				}
			}
			
			aO.setAtto(a);
			attiOdg.add(aO);
		}
		odg.setAttos(attiOdg);
		File inputFile = executePreviewGiacenza(odg, reportDto);
		return inputFile;
	}

	/*
	 * TODO: In ATTICO i tipi documento saranno gestiti in maniera dinamica 
	 *
	public File previewPropostaDeliberaGiunta(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewPropostaDeliberaGiunta:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewPropostaDeliberaGiunta(atto, reportDto);
		return inputFile;
	}
	
	public File previewPropostaDeliberaConsiglio(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewPropostaDeliberaConsiglio:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewPropostaDeliberaConsiglio(atto, reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewPropostaDeliberaGiunta(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		//File filefiligrana = cartaStampataService.addFiligrana(input, inputLogo, denominazioneRegione, aooService.getGerarchiaAoo(atto.getAoo()) );
		String banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo, atto.getUfficio(), urlSitoEnte, getNascondiUrlSitoESpostaDatiIntestazione(modelloHtml), getStampaIntestazioneSoloFrontespizio(modelloHtml), false);
		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}
	
	@Transactional(readOnly = true)
	private File executePreviewPropostaDeliberaConsiglio(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		//File filefiligrana = cartaStampataService.addFiligrana(input, inputLogo, denominazioneRegione, aooService.getGerarchiaAoo(atto.getAoo()) );
		String banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo, atto.getUfficio(), urlSitoEnte, 
				getNascondiUrlSitoESpostaDatiIntestazione(modelloHtml), getStampaIntestazioneSoloFrontespizio(modelloHtml), false);
		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}

	public File previewDeliberaConsiglio(ReportDTO reportDto, Atto atto, List<ComponentiGiunta> componentiGiunta, OrdineGiorno odg) throws IOException, DocumentException, DmsException {
		log.debug("previewDeliberaConsiglio:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewDeliberaConsiglio(atto, componentiGiunta, odg, atto.getSedutaGiunta(), reportDto);
		return inputFile;
	}
	
	public File previewDeliberaGiunta(ReportDTO reportDto, Atto atto, List<ComponentiGiunta> componentiGiunta, OrdineGiorno odg) throws IOException, DocumentException, DmsException {
		log.debug("previewDeliberaConsiglio:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewDeliberaGiunta(atto, componentiGiunta, odg, atto.getSedutaGiunta(), reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewDeliberaGiunta(Atto atto, List<ComponentiGiunta> componentiGiunta, OrdineGiorno odg, SedutaGiunta seduta, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		context.setVariable("componentiGiunta", componentiGiunta);
		context.setVariable("seduta", seduta);
		context.setVariable("odg", odg);
		//INIZIO - INSERISCO EVENTUALE SEGRETARIO / PRESIDENTE NEL CONTEXT DEL MODELLO
		this.aggiuntSegretarioPresidenteAlContext(atto, context);
		//FINE - INSERISCO EVENTUALE SEGRETARIO / PRESIDENTE NEL CONTEXT DEL MODELLO
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaDelibera(input, inputLogo);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc()!=null && !reportDto.getTipoDoc().trim().isEmpty() ? reportDto.getTipoDoc().trim() : "doc", ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		
		//return inputFile;
		return aggiungiAllegati(inputFile, atto.getId().longValue(), reportDto.getOmissis());
		
	}
	
	@Transactional(readOnly = true)
	private File executePreviewDeliberaConsiglio(Atto atto, List<ComponentiGiunta> componentiGiunta, OrdineGiorno odg, SedutaGiunta seduta, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		context.setVariable("componentiGiunta", componentiGiunta);
		context.setVariable("seduta", seduta);
		context.setVariable("odg", odg);
		//INIZIO - INSERISCO EVENTUALE SEGRETARIO / PRESIDENTE NEL CONTEXT DEL MODELLO
		this.aggiuntSegretarioPresidenteAlContext(atto, context);
		//FINE - INSERISCO EVENTUALE SEGRETARIO / PRESIDENTE NEL CONTEXT DEL MODELLO
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaDelibera(input, inputLogo);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc()!=null && !reportDto.getTipoDoc().trim().isEmpty() ? reportDto.getTipoDoc().trim() : "doc", ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		
		//return inputFile;
		return aggiungiAllegati(inputFile, atto.getId().longValue(), reportDto.getOmissis());
		
	}
	*/

	/*
	 * TODO: In ATTICO non previsto
	 * 
	public File previewSchemaDisegnoLegge(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewSchemaDisegnoLegge:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewSchemaDisegnoLegge(atto, reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewSchemaDisegnoLegge(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		Aoo aoo = aooService.findOne(atto.getAoo().getId());
		File filefiligrana = cartaStampataService.addFiligranaFrontespizio(input, inputLogo, denominazioneEnte, aoo, atto.getUfficio());

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();

		return inputFile;
	}

	public File previewDisegnoLegge(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewDisegnoLegge:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewDisegnoLegge(atto, reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewDisegnoLegge(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaLogoCentrale(input, inputLogo);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}

	public File previewComunicazione(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewComunicazione:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewComunicazione(atto, reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewComunicazione(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaFrontespizioLogoCentrale(input, inputLogo, denominazioneEnte, atto.getDescrizioneArea() != null ? atto.getDescrizioneArea().toUpperCase() : "",
				atto.getDescrizioneServizio() != null ? atto.getDescrizioneServizio().toUpperCase() : "");

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}

	public File previewRefertoTecnico(ReportDTO reportDto, Atto atto) throws IOException, DocumentException {
		log.debug("previewRefertoTecnico:" + reportDto);
		reportDto.setOmissis(false);
		File inputFile = executePreviewRefertoTecnico(atto, reportDto);
		return inputFile;
	}

	@Transactional(readOnly = true)
	private File executePreviewRefertoTecnico(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		this.aggiuntSegretarioPresidenteAlContext(atto, context);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaLogoCentrale(input, inputLogo);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		FileInputStream fis = new FileInputStream(filefiligrana);
		PdfReader pdfReader = new PdfReader(fis);
		FileOutputStream fos = new FileOutputStream(inputFile);
		PdfStamper pdfStamper = new PdfStamper(pdfReader, fos);

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

//		for (int i = 0; i < total;) {
//			pagecontent = pdfStamper.getOverContent(++i);
//			Rectangle pageSize = pdfReader.getPageSize(i);
//
//			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), xNumberPage,
//					15, 0);
//		}
		
		pdfStamper.close();
		pdfReader.close();
		fis.close();
		fos.close();
		return inputFile;
	}
	*/

	@Transactional(readOnly = true)
	private File executePreviewGiacenza(OrdineGiorno odgBase, ReportDTO reportDto) throws IOException, DocumentException {

		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("odg", odgBase);
		String htmlRT = creaTemplatePreview(reportDto, context);

		String dataSeduta = new DateTime().toString("dd/MM/yyyy");
		
		ByteArrayResource result = invokeHtmlToPDF(htmlRT,reportDto.getIdModelloHtml(), "", null);
		InputStream inputHtml = result.getInputStream();

		return createOdgBase(true, true, dataSeduta, odgBase, inputHtml);
	}

	@Transactional(readOnly = true)
	private File executePreviewOrdinegiornoBase(SedutaGiunta seduta, OrdineGiorno odgBase, ReportDTO reportDto) throws IOException, DocumentException {

//		boolean sedutaOrdinaria = seduta.getTipoSeduta() == 1;
//		String dataSeduta = seduta.getPrimaConvocazioneInizio().toString("dd/MM/yyyy");
		
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("seduta", seduta);
		
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"));
		
		if(odgBase!=null && odgBase.getAttos()!=null) {
			for (int i = 0; i < odgBase.getAttos().size(); i++) {
				if(odgBase.getAttos().get(i).getAtto()!=null && odgBase.getAttos().get(i).getAtto().getDatiContabili()!=null){
					if(odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()!=null && odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgBase.getAttos().get(i).getAtto().getDatiContabili().setImportoEntrataFormattato(currencyInstance.format(odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()));
					}
					if(odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()!=null && odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgBase.getAttos().get(i).getAtto().getDatiContabili().setImportoUscitaFormattato(currencyInstance.format(odgBase.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()));
					}					
				}
			}
		}
		
		context.setVariable("odg", odgBase);
		Map<String, String> pareriSinteticiMap = esitoPareriService.findAllMap();
		context.setVariable("pareriSinteticiMap", pareriSinteticiMap);
		context.setVariable("componenteAttiTrasparenzaBaseUrl", WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TRASPARENZA_BASE_URL));
		
		boolean stampaQT=false;
		boolean stampaVERB=false;
		boolean stampaCOM=false;
		boolean stampaODG=false;
		boolean stampaDEL=false;
		boolean stampaINT=false;
		boolean stampaMZ=false;
		boolean stampaRIS=false;
		
		if(odgBase!=null && odgBase.getAttos()!=null) {
			
			for (int i = 0; i < odgBase.getAttos().size(); i++) {
				String codiceTipoAtto = odgBase.getAttos().get(i).getAtto().getTipoAtto().getCodice();
				
				if(codiceTipoAtto.equalsIgnoreCase("QT")) {
					stampaQT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("VERB")) {
					stampaVERB = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("COM")) {
					stampaCOM = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("ODG")) {
					stampaODG = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("DC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DPC") ) {
					stampaDEL = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("INT")) {
					stampaINT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("MZ")) {
					stampaMZ = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("RIS")) {
					stampaRIS = true;
				}
			}
			
		}
		context.setVariable("stampaQT",stampaQT);
		context.setVariable("stampaVERB",stampaVERB);
		context.setVariable("stampaCOM",stampaCOM);
		context.setVariable("stampaODG",stampaODG);
		context.setVariable("stampaDEL",stampaDEL);
		context.setVariable("stampaINT",stampaINT);
		context.setVariable("stampaMZ",stampaMZ);
		context.setVariable("stampaRIS",stampaRIS);
		
		if(seduta != null && seduta.getOrgano() != null && seduta.getOrgano().equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.C.name()) &&
			odgBase!=null && odgBase.getAttos()!=null) {
			//ODL setto l'ordine atti
			LinkedHashMap<TipoAttoReportDto, List<AttiOdg>> attiReport = new LinkedHashMap<TipoAttoReportDto, List<AttiOdg>>();
			Collections.sort(odgBase.getAttos(), new Comparator<AttiOdg>() {
				  @Override
				  public int compare(AttiOdg a, AttiOdg b) {
				    return a.getOrdineOdg().compareTo(b.getOrdineOdg());
				  }
				});
			long idTipo = 1;
			TipoAttoReportDto tipoAttoRep = null;
			List<AttiOdg> actLst = null;
			for (AttiOdg atto : odgBase.getAttos()) {
				if(atto != null && atto.getAtto() != null && atto.getAtto().getTipoAtto() != null && atto.getAtto().getTipoAtto().getCodice() != null) {
					String codice = atto.getAtto().getTipoAtto().getCodice();
					if(codice.equalsIgnoreCase("DC") || codice.equalsIgnoreCase("DIC") || codice.equalsIgnoreCase("DPC") ) {
						codice = "DC-DPC-DIC";
					}
					if(tipoAttoRep != null && tipoAttoRep.getCodice() != null && !tipoAttoRep.getCodice().equalsIgnoreCase(codice)) {
						if(actLst != null && !actLst.isEmpty()) {
							attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
						}
						tipoAttoRep = null;
						actLst = null;
					}
					if(tipoAttoRep == null) {
						tipoAttoRep = new TipoAttoReportDto();
						tipoAttoRep.setCodice(codice);
						if(codice.equalsIgnoreCase("QT")) {
							tipoAttoRep.setDescrizione("QUESTIONTIME DOMANDE A RISPOSTA IMMEDIATA ( art. 41 del Regolamento del Consiglio Comunale)");
						}else if(codice.equalsIgnoreCase("DC-DPC-DIC")) {
							tipoAttoRep.setDescrizione("DELIBERAZIONI");
						}
						else if(codice.equalsIgnoreCase("VERB")) {
							tipoAttoRep.setDescrizione("VERBALI");
						}
						else if(codice.equalsIgnoreCase("COM")) {
							tipoAttoRep.setDescrizione("COMUNICAZIONI");
						}
						else if(codice.equalsIgnoreCase("DAT")) {
							tipoAttoRep.setDescrizione("Domande di Attualità".toUpperCase());
						}
						else if(codice.equalsIgnoreCase("INT")) {
							tipoAttoRep.setDescrizione("INTERROGAZIONI");
						}
						else if(codice.equalsIgnoreCase("ODG")) {
							tipoAttoRep.setDescrizione("ORDINI DEL GIORNO");
						}
						else if(codice.equalsIgnoreCase("MZ")) {
							tipoAttoRep.setDescrizione("MOZIONI");
						}
						else if(codice.equalsIgnoreCase("RIS")) {
							tipoAttoRep.setDescrizione("RISOLUZIONI");
						}
						else {
							tipoAttoRep.setDescrizione(atto.getAtto().getTipoAtto().getDescrizione().toUpperCase());	
						}
						tipoAttoRep.setId(idTipo);
						idTipo++;
					}
					if(actLst == null) {
						actLst = new ArrayList<AttiOdg>();
					}
					actLst.add(atto);
				}
				
			}
			if(actLst!= null && !actLst.isEmpty() && tipoAttoRep != null) {
				attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
			}
			if(!attiReport.isEmpty()) {
				context.setVariable("attiReport", attiReport);	
			}
		}
		
		HashMap<Long, List<ConfigurazioneIncaricoProfiloDto>> listaRelatoriAtto = getRelatoriAtto(odgBase);
		context.setVariable("listaRelatoriAtto", listaRelatoriAtto);	

		HashMap<Long, Aoo> direzioneAtto = getDirezioneAtto(odgBase);
		context.setVariable("direzioneAtto", direzioneAtto);	
		
		//TODO:Gestione fuso orario da centralizzare con custom tag su thymeleaf
		DateTime primaConvocazioneInizio = formatTime(odgBase.getSedutaGiunta().getPrimaConvocazioneInizio());
		context.setVariable("primaConvocazioneInizio", primaConvocazioneInizio);
		
		String htmlRT = creaTemplatePreview(reportDto, context);
		//ByteArrayResource result = invokeHtmlToPDF(htmlRT,reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + seduta.getNumero()));
		
		
		if(reportDto.getIsDoc()!=null&&reportDto.getIsDoc().booleanValue()) {
			ByteArrayResource result = invokeHtmlToDocx(htmlRT,reportDto.getIdModelloHtml());
			File file = File.createTempFile("withfiligrana_", ".docx");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
		}else {
			ByteArrayResource result = invokeHtmlToPDF(htmlRT,reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + seduta.getNumero()), null);
//			InputStream inputHtml = result.getInputStream();
			File file = File.createTempFile("withfiligrana_", ".pdf");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
		}
		
		

//		return createOdgBase(false, sedutaOrdinaria, dataSeduta, odgBase, inputHtml);
		
	}

	@Transactional(readOnly = true)
	private File executePreviewOrdinegiornoSuppletivo(SedutaGiunta seduta, File docOdgbase, File docSuppletivoPrecedente, OrdineGiorno odgSuppletivo, ReportDTO reportDto) throws IOException, DocumentException {

		String dataSeduta = seduta.getPrimaConvocazioneInizio().toString("dd/MM/yyyy");

		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("seduta", seduta);
		
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"));
		
		if(odgSuppletivo!=null && odgSuppletivo.getAttos()!=null) {
			for (int i = 0; i < odgSuppletivo.getAttos().size(); i++) {
				if(odgSuppletivo.getAttos().get(i).getAtto()!=null && odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili()!=null){
					if(odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()!=null && odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().setImportoEntrataFormattato(currencyInstance.format(odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()));
					}
					if(odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()!=null && odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().setImportoUscitaFormattato(currencyInstance.format(odgSuppletivo.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()));
					}					
				}
			}
		}
		
		context.setVariable("odg", odgSuppletivo);
		Map<String, String> pareriSinteticiMap = esitoPareriService.findAllMap();
		context.setVariable("pareriSinteticiMap", pareriSinteticiMap);
		context.setVariable("componenteAttiTrasparenzaBaseUrl", WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TRASPARENZA_BASE_URL));
		
		boolean stampaQT=false;
		boolean stampaVERB=false;
		boolean stampaCOM=false;
		boolean stampaODG=false;
		boolean stampaDEL=false;
		boolean stampaINT=false;
		boolean stampaMZ=false;
		boolean stampaRIS=false;
		
		if(odgSuppletivo!=null && odgSuppletivo.getAttos()!=null) {
			
			for (int i = 0; i < odgSuppletivo.getAttos().size(); i++) {
				String codiceTipoAtto = odgSuppletivo.getAttos().get(i).getAtto().getTipoAtto().getCodice();
				
				if(codiceTipoAtto.equalsIgnoreCase("QT")) {
					stampaQT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("VERB")) {
					stampaVERB = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("COM")) {
					stampaCOM = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("ODG")) {
					stampaODG = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("DC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DPC") ) {
					stampaDEL = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("INT")) {
					stampaINT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("MZ")) {
					stampaMZ = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("RIS")) {
					stampaRIS = true;
				}
			}
			
		}
		context.setVariable("stampaQT",stampaQT);
		context.setVariable("stampaVERB",stampaVERB);
		context.setVariable("stampaCOM",stampaCOM);
		context.setVariable("stampaODG",stampaODG);
		context.setVariable("stampaDEL",stampaDEL);
		context.setVariable("stampaINT",stampaINT);
		context.setVariable("stampaMZ",stampaMZ);
		context.setVariable("stampaRIS",stampaRIS);
		
		if(seduta != null && seduta.getOrgano() != null && seduta.getOrgano().equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.C.name()) &&
				odgSuppletivo!=null && odgSuppletivo.getAttos()!=null) {
				//ODL setto l'ordine atti
				LinkedHashMap<TipoAttoReportDto, List<AttiOdg>> attiReport = new LinkedHashMap<TipoAttoReportDto, List<AttiOdg>>();
				Collections.sort(odgSuppletivo.getAttos(), new Comparator<AttiOdg>() {
					  @Override
					  public int compare(AttiOdg a, AttiOdg b) {
					    return a.getOrdineOdg().compareTo(b.getOrdineOdg());
					  }
					});
				long idTipo = 1;
				TipoAttoReportDto tipoAttoRep = null;
				List<AttiOdg> actLst = null;
				for (AttiOdg atto : odgSuppletivo.getAttos()) {
					if(atto != null && atto.getAtto() != null && atto.getAtto().getTipoAtto() != null && atto.getAtto().getTipoAtto().getCodice() != null) {
						String codice = atto.getAtto().getTipoAtto().getCodice();
						if(codice.equalsIgnoreCase("DC") || codice.equalsIgnoreCase("DIC") || codice.equalsIgnoreCase("DPC") ) {
							codice = "DC-DPC-DIC";
						}
						if(tipoAttoRep != null && tipoAttoRep.getCodice() != null && !tipoAttoRep.getCodice().equalsIgnoreCase(codice)) {
							if(actLst != null && !actLst.isEmpty()) {
								attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
							}
							tipoAttoRep = null;
							actLst = null;
						}
						if(tipoAttoRep == null) {
							tipoAttoRep = new TipoAttoReportDto();
							tipoAttoRep.setCodice(codice);
							if(codice.equalsIgnoreCase("QT")) {
								tipoAttoRep.setDescrizione("QUESTIONTIME DOMANDE A RISPOSTA IMMEDIATA ( art. 41 del Regolamento del Consiglio Comunale)");
							}else if(codice.equalsIgnoreCase("DC-DPC-DIC")) {
								tipoAttoRep.setDescrizione("DELIBERAZIONI");
							}
							else if(codice.equalsIgnoreCase("VERB")) {
								tipoAttoRep.setDescrizione("VERBALI");
							}
							else if(codice.equalsIgnoreCase("COM")) {
								tipoAttoRep.setDescrizione("COMUNICAZIONI");
							}
							else if(codice.equalsIgnoreCase("DAT")) {
								tipoAttoRep.setDescrizione("Domande di Attualità".toUpperCase());
							}
							else if(codice.equalsIgnoreCase("INT")) {
								tipoAttoRep.setDescrizione("INTERROGAZIONI");
							}
							else if(codice.equalsIgnoreCase("ODG")) {
								tipoAttoRep.setDescrizione("ORDINI DEL GIORNO");
							}
							else if(codice.equalsIgnoreCase("MZ")) {
								tipoAttoRep.setDescrizione("MOZIONI");
							}
							else if(codice.equalsIgnoreCase("RIS")) {
								tipoAttoRep.setDescrizione("RISOLUZIONI");
							}
							else {
								tipoAttoRep.setDescrizione(atto.getAtto().getTipoAtto().getDescrizione().toUpperCase());	
							}
							tipoAttoRep.setId(idTipo);
							idTipo++;
						}
						if(actLst == null) {
							actLst = new ArrayList<AttiOdg>();
						}
						actLst.add(atto);
					}
					
				}
				if(actLst!= null && !actLst.isEmpty() && tipoAttoRep != null) {
					attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
				}
				if(!attiReport.isEmpty()) {
					context.setVariable("attiReport", attiReport);	
				}
			}
		
		//TODO:Gestione fuso orario da centralizzare con custom tag su thymeleaf
		DateTime primaConvocazioneInizio = formatTime(odgSuppletivo.getSedutaGiunta().getPrimaConvocazioneInizio());
		context.setVariable("primaConvocazioneInizio", primaConvocazioneInizio);
		
		HashMap<Long, List<ConfigurazioneIncaricoProfiloDto>> listaRelatoriAtto = getRelatoriAtto(odgSuppletivo);
		context.setVariable("listaRelatoriAtto", listaRelatoriAtto);
		
		HashMap<Long, Aoo> direzioneAtto = getDirezioneAtto(odgSuppletivo);
		context.setVariable("direzioneAtto", direzioneAtto);
		
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = reportDto.getIsDoc()!=null&&reportDto.getIsDoc().booleanValue()?
				invokeHtmlToDocx(htmlRT, reportDto.getIdModelloHtml()):
				invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + seduta.getNumero()), null);
		File file = File.createTempFile("withfiligrana_", ".pdf");
		FileOutputStream fop = new FileOutputStream(file);
		fop.write(result.getByteArray());
		fop.flush();
		fop.close();
		return file;
		//return createOdgSuppletivo(docOdgbase, docSuppletivoPrecedente, dataSeduta, odgSuppletivo, inputHtml);
	}

	@Transactional(readOnly = true)
	private File executePreviewOrdinegiornoFuoriSacco(SedutaGiunta seduta, File docOdgbase, OrdineGiorno odgFuoriSacco, ReportDTO reportDto) throws IOException, DocumentException {

		String dataSeduta = seduta.getPrimaConvocazioneInizio().toString("dd/MM/yyyy");

		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("seduta", seduta);
		
		NumberFormat currencyInstance = NumberFormat.getCurrencyInstance(getLocalFromISO("EUR"));
		
		if(odgFuoriSacco!=null && odgFuoriSacco.getAttos()!=null) {
			for (int i = 0; i < odgFuoriSacco.getAttos().size(); i++) {
				if(odgFuoriSacco.getAttos().get(i).getAtto()!=null && odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili()!=null){
					if(odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()!=null && odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().setImportoEntrataFormattato(currencyInstance.format(odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoEntrata()));
					}
					if(odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()!=null && odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita().doubleValue() != BigDecimal.ZERO.doubleValue()) {
						odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().setImportoUscitaFormattato(currencyInstance.format(odgFuoriSacco.getAttos().get(i).getAtto().getDatiContabili().getImportoUscita()));
					}
				}
			}
		}
		
		context.setVariable("odg", odgFuoriSacco);
		Map<String, String> pareriSinteticiMap = esitoPareriService.findAllMap();
		context.setVariable("pareriSinteticiMap", pareriSinteticiMap);
		context.setVariable("componenteAttiTrasparenzaBaseUrl", WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TRASPARENZA_BASE_URL));
		
		boolean stampaQT=false;
		boolean stampaVERB=false;
		boolean stampaCOM=false;
		boolean stampaODG=false;
		boolean stampaDEL=false;
		boolean stampaINT=false;
		boolean stampaMZ=false;
		boolean stampaRIS=false;
		
		if(odgFuoriSacco!=null && odgFuoriSacco.getAttos()!=null) {
			
			for (int i = 0; i < odgFuoriSacco.getAttos().size(); i++) {
				String codiceTipoAtto = odgFuoriSacco.getAttos().get(i).getAtto().getTipoAtto().getCodice();
				
				if(codiceTipoAtto.equalsIgnoreCase("QT")) {
					stampaQT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("VERB")) {
					stampaVERB = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("COM")) {
					stampaCOM = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("ODG")) {
					stampaODG = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("DC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DPC") ) {
					stampaDEL = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("INT")) {
					stampaINT = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("MZ")) {
					stampaMZ = true;
				}else if(codiceTipoAtto.equalsIgnoreCase("RIS")) {
					stampaRIS = true;
				}
			}
			
		}
		context.setVariable("stampaQT",stampaQT);
		context.setVariable("stampaVERB",stampaVERB);
		context.setVariable("stampaCOM",stampaCOM);
		context.setVariable("stampaODG",stampaODG);
		context.setVariable("stampaDEL",stampaDEL);
		context.setVariable("stampaINT",stampaINT);
		context.setVariable("stampaMZ",stampaMZ);
		context.setVariable("stampaRIS",stampaRIS);
		
		if(seduta != null && seduta.getOrgano() != null && seduta.getOrgano().equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.C.name()) &&
				odgFuoriSacco!=null && odgFuoriSacco.getAttos()!=null) {
				//ODL setto l'ordine atti
				LinkedHashMap<TipoAttoReportDto, List<AttiOdg>> attiReport = new LinkedHashMap<TipoAttoReportDto, List<AttiOdg>>();
				Collections.sort(odgFuoriSacco.getAttos(), new Comparator<AttiOdg>() {
					  @Override
					  public int compare(AttiOdg a, AttiOdg b) {
					    return a.getOrdineOdg().compareTo(b.getOrdineOdg());
					  }
					});
				long idTipo = 1;
				TipoAttoReportDto tipoAttoRep = null;
				List<AttiOdg> actLst = null;
				for (AttiOdg atto : odgFuoriSacco.getAttos()) {
					if(atto != null && atto.getAtto() != null && atto.getAtto().getTipoAtto() != null && atto.getAtto().getTipoAtto().getCodice() != null) {
						String codice = atto.getAtto().getTipoAtto().getCodice();
						if(codice.equalsIgnoreCase("DC") || codice.equalsIgnoreCase("DIC") || codice.equalsIgnoreCase("DPC") ) {
							codice = "DC-DPC-DIC";
						}
						if(tipoAttoRep != null && tipoAttoRep.getCodice() != null && !tipoAttoRep.getCodice().equalsIgnoreCase(codice)) {
							if(actLst != null && !actLst.isEmpty()) {
								attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
							}
							tipoAttoRep = null;
							actLst = null;
						}
						if(tipoAttoRep == null) {
							tipoAttoRep = new TipoAttoReportDto();
							tipoAttoRep.setCodice(codice);
							if(codice.equalsIgnoreCase("QT")) {
								tipoAttoRep.setDescrizione("QUESTIONTIME DOMANDE A RISPOSTA IMMEDIATA ( art. 41 del Regolamento del Consiglio Comunale)");
							}else if(codice.equalsIgnoreCase("DC-DPC-DIC")) {
								tipoAttoRep.setDescrizione("DELIBERAZIONI");
							}
							else if(codice.equalsIgnoreCase("VERB")) {
								tipoAttoRep.setDescrizione("VERBALI");
							}
							else if(codice.equalsIgnoreCase("COM")) {
								tipoAttoRep.setDescrizione("COMUNICAZIONI");
							}
							else if(codice.equalsIgnoreCase("DAT")) {
								tipoAttoRep.setDescrizione("Domande di Attualità".toUpperCase());
							}
							else if(codice.equalsIgnoreCase("INT")) {
								tipoAttoRep.setDescrizione("INTERROGAZIONI");
							}
							else if(codice.equalsIgnoreCase("ODG")) {
								tipoAttoRep.setDescrizione("ORDINI DEL GIORNO");
							}
							else if(codice.equalsIgnoreCase("MZ")) {
								tipoAttoRep.setDescrizione("MOZIONI");
							}
							else if(codice.equalsIgnoreCase("RIS")) {
								tipoAttoRep.setDescrizione("RISOLUZIONI");
							}
							else {
								tipoAttoRep.setDescrizione(atto.getAtto().getTipoAtto().getDescrizione().toUpperCase());	
							}
							tipoAttoRep.setId(idTipo);
							idTipo++;
						}
						if(actLst == null) {
							actLst = new ArrayList<AttiOdg>();
						}
						actLst.add(atto);
					}
					
				}
				if(actLst!= null && !actLst.isEmpty() && tipoAttoRep != null) {
					attiReport.put(tipoAttoRep, new ArrayList<AttiOdg>(actLst));
				}
				if(!attiReport.isEmpty()) {
					context.setVariable("attiReport", attiReport);	
				}
			}
		
		DateTime primaConvocazioneInizio = formatTime(odgFuoriSacco.getSedutaGiunta().getPrimaConvocazioneInizio());
		context.setVariable("primaConvocazioneInizio", primaConvocazioneInizio);
		
		HashMap<Long, List<ConfigurazioneIncaricoProfiloDto>> listaRelatoriAtto = getRelatoriAtto(odgFuoriSacco);
		context.setVariable("listaRelatoriAtto", listaRelatoriAtto);	
		
		HashMap<Long, Aoo> direzioneAtto = getDirezioneAtto(odgFuoriSacco);
		context.setVariable("direzioneAtto", direzioneAtto);
		
		String htmlRT = creaTemplatePreview(reportDto, context);

		if(reportDto.getIsDoc()!=null&&reportDto.getIsDoc().booleanValue()) {
			ByteArrayResource result = invokeHtmlToDocx(htmlRT,reportDto.getIdModelloHtml());
			File file = File.createTempFile("withfiligrana_", ".docx");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
		}else {
			ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + seduta.getNumero()), null);
			InputStream inputHtml = result.getInputStream();
	
			boolean sedutaOrdinaria = seduta.getTipoSeduta() == 1;
			
			//return createOdgFuoriSacco(docOdgbase, sedutaOrdinaria, dataSeduta, odgFuoriSacco, inputHtml);
			File file = File.createTempFile("withfiligrana_", ".pdf");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
		}
	}

	private File createOdgBase(boolean giacenza, boolean sedutaOrdinaria, 
			String dataSeduta, OrdineGiorno odgBase, InputStream inputHtml) throws IOException, DocumentException {

		List<AttiOdg> attiOdg = odgBase.getAttos();

		String prefixToc = "";
		String prefixOdg = "";

		if (giacenza) {
			prefixToc = "Giacenza del  " + dataSeduta + " - Indice Analitico";
			prefixOdg = "Giacenza del  " + dataSeduta + " - pag. ";
		}
		else {
			String numOdg = odgBase.getSedutaGiunta().getNumero();
			//TODO verificare come modificare questa riga
			prefixToc = "OdG Seduta n." + numOdg + " del " + dataSeduta + " - Indice Analitico";
			//prefixOdg = sedutaOrdinaria ? "OdG n." + numOdg + "  del  " + dataSeduta + " - pag. " : "Seduta Straordinaria n. " + numOdg + " S del " + dataSeduta + " - pag. ";
			String tipoSeduta = sedutaOrdinaria? "Ordinaria" : "Straordinaria";
			//   OdG Seduta   Ordinaria/Straordinaria N.7 del 13/12/2016 - pag. 1
			prefixOdg = "OdG Seduta "+tipoSeduta+" N." + numOdg + " del " + dataSeduta + " - pag. ";
		}

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		InputStream inputLogoOdg = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoPalazzoEnte()));
		InputStream inputLogoIntestazione = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		boolean isBase = !giacenza;
		
		String organo = "Segreteria Generale ";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odgBase.getSedutaGiunta().getOrgano())) {
			organo += "della Giunta Comunale";
		}
		else {
			organo += "del Consiglio Comunale";
		}
		
		File fileOdgBase = cartaStampataService.addFiligranaOrdinegiorno(isBase, 
				inputHtml, inputLogo, inputLogoOdg, inputLogoIntestazione, organo);
		
		IOUtils.closeQuietly(inputHtml);
		IOUtils.closeQuietly(inputLogo);
		IOUtils.closeQuietly(inputLogoOdg);
		PdfReader pdfReaderOdgBase = new PdfReader(new FileInputStream(fileOdgBase));
		int numPagesOdgBase = pdfReaderOdgBase.getNumberOfPages();

		OdgPdfObject odg = cartaStampataService.addListaOdg(giacenza, attiOdg, 1, giacenza?1:3);
		File fileOdgs = odg.getBody();

		int numPagesToc = 0;
		PdfReader pdfReaderToc = null;
		
		// TODO : problema con relatori
		// if (sedutaOrdinaria) {
		//	File filetoc = cartaStampataService.addIndiceAnalitico(odg);
		//	pdfReaderToc = new PdfReader(new FileInputStream(filetoc));
		//	numPagesToc = pdfReaderToc.getNumberOfPages();
		// }

		PdfReader pdfReaderListaOdgs = new PdfReader(new FileInputStream(fileOdgs));

		int startPaginationToc = numPagesOdgBase + 1;
		int startPaginationOdg = numPagesOdgBase + numPagesToc + 1;

		// File fileOdgBase = File.createTempFile("odgbase_", ".pdf"); test ottimizzazione
		PdfStamper pdfStamperUnion = new PdfStamper(pdfReaderOdgBase, new FileOutputStream(fileOdgBase));
		for (int i = 1; i <= numPagesToc; i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderToc, i);
			pdfStamperUnion.insertPage(pdfReaderOdgBase.getNumberOfPages() + i, pdfReaderOdgBase.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgBase.getNumberOfPages()).addTemplate(page, 0, 0);
		}
		for (int i = 1; i <= pdfReaderListaOdgs.getNumberOfPages(); i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderListaOdgs, i);
			pdfStamperUnion.insertPage(pdfReaderOdgBase.getNumberOfPages() + i, pdfReaderOdgBase.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgBase.getNumberOfPages()).addTemplate(page, 0, 0);
		}

		for (int i = startPaginationToc; i < startPaginationToc + numPagesToc; i++) {
			Rectangle pageSize = pdfReaderOdgBase.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT, new Phrase(prefixToc, FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)),
					pageSize.getWidth() - 60, pageSize.getHeight() - 30, 0);
		}
		for (int i = startPaginationOdg; i <= pdfReaderOdgBase.getNumberOfPages(); i++) {
			Rectangle pageSize = pdfReaderOdgBase.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT,
					new Phrase(String.format(prefixOdg + "%s", i - startPaginationOdg + 1), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)), pageSize.getWidth() - 60,
					pageSize.getHeight() - 30, 0);
		}

		Map<String, String> info = pdfReaderOdgBase.getInfo();
		info.put("OdgBaseNumerazioneArgomenti", String.valueOf(attiOdg.size() + 2));
		info.put("OdgBaseNumerazionePagine", String.valueOf(pdfReaderOdgBase.getNumberOfPages() - startPaginationOdg + 1));
		pdfStamperUnion.setMoreInfo(info);

		pdfStamperUnion.close();
		pdfReaderListaOdgs.close();
		if (pdfReaderToc != null)
			pdfReaderToc.close();
		pdfReaderOdgBase.close();

		return fileOdgBase;
	}

	private File createOdgSuppletivo(File docOdgBase, File docSuppletivoPrecedente, String dataSeduta, OrdineGiorno odgSuppletivo, InputStream inputHtml) throws IOException, DocumentException {

		//String numOdg = odgSuppletivo.getNumeroOdg();
		List<AttiOdg> attiOdg = odgSuppletivo.getAttos();

		// int odgNumerazioneArgomenti = 0;
		// int odgNumerazionePagine = 0;
		if(docSuppletivoPrecedente!=null){
			PdfReader pdfReaderOdgSuppletivoPrecedente = new PdfReader(new FileInputStream(docSuppletivoPrecedente));
			// odgNumerazioneArgomenti = Integer.parseInt(pdfReaderOdgSuppletivoPrecedente.getInfo().get("LastNumerazioneArgomenti"));
			// odgNumerazionePagine = Integer.parseInt(pdfReaderOdgSuppletivoPrecedente.getInfo().get("LastNumerazionePagine"));
			pdfReaderOdgSuppletivoPrecedente.close();
		}else{
			PdfReader pdfReaderOdgBase = new PdfReader(new FileInputStream(docOdgBase));
			// odgNumerazioneArgomenti = Integer.parseInt(pdfReaderOdgBase.getInfo().get("OdgBaseNumerazioneArgomenti"));
			// odgNumerazionePagine = Integer.parseInt(pdfReaderOdgBase.getInfo().get("OdgBaseNumerazionePagine"));
			pdfReaderOdgBase.close();
		}
		
		// int startPaginationOdg = odgNumerazionePagine;
		

		String prefixOdg = "Suppletivo all'OdG " + "  del  " + dataSeduta + " - pag. ";
		
		try {
			String numOdg = odgSuppletivo.getProgressivoOdgSeduta();
			
			prefixOdg = "Suppletivo N." + numOdg + " all'OdG " + "  del  " + dataSeduta + " - pag. ";
		} catch (Exception e) {
			// TODO: handle exception
		}

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		InputStream inputLogoOdg = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoPalazzoEnte()));
		InputStream inputLogoIntestazione = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		
		String organo = "Segreteria Generale ";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odgSuppletivo.getSedutaGiunta().getOrgano())) {
			organo += "della Giunta Comunale";
		}
		else {
			organo += "del Consiglio Comunale";
		}
		
		File fileOdgSuppletivo = cartaStampataService.addFiligranaOrdinegiorno(false, 
				inputHtml, inputLogo, inputLogoOdg, inputLogoIntestazione, organo);
		
		IOUtils.closeQuietly(inputHtml);
		IOUtils.closeQuietly(inputLogo);
		IOUtils.closeQuietly(inputLogoOdg);

		PdfReader pdfReaderOdgSuppletivo = new PdfReader(new FileInputStream(fileOdgSuppletivo));

		OdgPdfObject odg = cartaStampataService.addListaOdg(false, attiOdg, 3, /* odgNumerazioneArgomenti + */ 1);
		File fileOdgs = odg.getBody();

		PdfReader pdfReaderListaOdgs = new PdfReader(new FileInputStream(fileOdgs));

		PdfStamper pdfStamperUnion = new PdfStamper(pdfReaderOdgSuppletivo, new FileOutputStream(fileOdgSuppletivo));
		for (int i = 1; i <= pdfReaderListaOdgs.getNumberOfPages(); i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderListaOdgs, i);
			pdfStamperUnion.insertPage(pdfReaderOdgSuppletivo.getNumberOfPages() + i, pdfReaderOdgSuppletivo.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgSuppletivo.getNumberOfPages()).addTemplate(page, 0, 0);
		}

		for (int i = 1; i <= pdfReaderOdgSuppletivo.getNumberOfPages(); i++) {
			Rectangle pageSize = pdfReaderOdgSuppletivo.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT,
					new Phrase(String.format(prefixOdg + "%s", i /* + odgNumerazionePagine */), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)), pageSize.getWidth() - 60,
					pageSize.getHeight() - 30, 0);
		}

		Map<String, String> info = pdfReaderOdgSuppletivo.getInfo();
		// info.put("LastNumerazioneArgomenti", String.valueOf(attiOdg.size()+odgNumerazioneArgomenti));
		// info.put("LastNumerazionePagine", String.valueOf(pdfReaderOdgSuppletivo.getNumberOfPages() + startPaginationOdg));
		pdfStamperUnion.setMoreInfo(info);

		pdfStamperUnion.close();
		
		
		pdfStamperUnion.close();
		pdfReaderListaOdgs.close();
		pdfReaderOdgSuppletivo.close();

		return fileOdgSuppletivo;
	}

	private File createOdgFuoriSacco(File docOdgBase, boolean sedutaOrdinaria, 
			String dataSeduta, OrdineGiorno odgFuoriSacco, InputStream inputHtml) throws IOException, DocumentException {

		String numOdg = odgFuoriSacco.getSedutaGiunta().getNumero();
		List<AttiOdg> attiOdg = odgFuoriSacco.getAttos();

		
		//int odgNumerazioneArgomenti = 0;
		//int odgNumerazionePagine = 0;
		
		PdfReader pdfReaderOdgBase = new PdfReader(new FileInputStream(docOdgBase));
		//odgNumerazioneArgomenti = Integer.parseInt(pdfReaderOdgBase.getInfo().get("OdgBaseNumerazioneArgomenti"));
		//odgNumerazionePagine = Integer.parseInt(pdfReaderOdgBase.getInfo().get("OdgBaseNumerazionePagine"));
		pdfReaderOdgBase.close();
		

		//String prefixOdg = "Elenco Provvedimenti Fuori Sacco - pag. ";
		String tipoSeduta = sedutaOrdinaria? "Ordinaria" : "Straordinaria";
		//   Fuori Sacco Seduta Ordinaria N.7 del 13/12/2016 - pag. 4
		String prefixOdg = "Fuori Sacco Seduta "+tipoSeduta+" N." + numOdg + "  del  " + dataSeduta + " - pag. ";

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		InputStream inputLogoOdg = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoPalazzoEnte()));
		InputStream inputLogoIntestazione = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		IOUtils.closeQuietly(inputHtml);
		IOUtils.closeQuietly(inputLogo);
		IOUtils.closeQuietly(inputLogoOdg);

		String organo = "Segreteria Generale ";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odgFuoriSacco.getSedutaGiunta().getOrgano())) {
			organo += "della Giunta Comunale";
		}
		else {
			organo += "del Consiglio Comunale";
		}

		OdgPdfObject odg = cartaStampataService.addListaOdgFuoriSacco(false, 
				attiOdg, 4, 1,inputLogo, dataSeduta, sedutaOrdinaria, organo);
		File fileOdgs = odg.getBody();

		PdfReader pdfReaderListaOdgs = new PdfReader(new FileInputStream(fileOdgs));

		PdfStamper pdfStamperUnion = new PdfStamper(pdfReaderListaOdgs, new FileOutputStream(fileOdgs));

		for (int i = 1; i <= pdfReaderListaOdgs.getNumberOfPages(); i++) {
			Rectangle pageSize = pdfReaderListaOdgs.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT,
					new Phrase(String.format(prefixOdg + "%s", i /*+ odgNumerazionePagine*/), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)), pageSize.getWidth() - 60,
					pageSize.getHeight() - 30, 0);
		}

		pdfStamperUnion.close();
		pdfReaderListaOdgs.close();

		return fileOdgs;
	}
	
	private static class SezioneEnum {
	    public enum SEZIONI {
	    	SVOLTI, APPROVATI, RESPINTI, RINVIATI, RITIRATI;
	    } 
	}
	
	private void insertIntoMap(AttiOdg attoOdg, String codiceTipoAtto, Map<String, Map<String, List<AttiOdg>>> map, SezioneEnum.SEZIONI sezione) {
		if(!map.containsKey(sezione.name())) {
			map.put(sezione.name(), new HashMap<String, List<AttiOdg>>());
		}
		if(!map.get(sezione.name()).containsKey(codiceTipoAtto)) {
			map.get(sezione.name()).put(codiceTipoAtto, new ArrayList<AttiOdg>());
		}
		map.get(sezione.name()).get(codiceTipoAtto).add(attoOdg);
	}
 
	public File previewResoconto(ReportDTO reportDto) throws IOException, DocumentException {
		
		try {
			Resoconto resoconto;
			if (TipoResocontoEnum.DOCUMENTO_DEF_ESITO.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
				resoconto = createResoconto(
						reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ESITO, 
	    				reportDto.getIdModelloHtml());
	    	}
	    	else if (TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI.name().equalsIgnoreCase(reportDto.getTipoDoc())) {
	    		resoconto = createResoconto( 
	    				reportDto.getIdSeduta(), TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI, 
	    				reportDto.getIdModelloHtml());
	    	}
	    	else {
	    		throw new GestattiCatchedException("Occorre specificare il tipo di Documento Resoconto:"
	    				+ " doc-definitivo-esito oppure doc-definitivo-elenco-verbali");
	    	}
			
	    	List<OrdineGiorno> odgs =  ordineGiornoRepository.findBySeduta(resoconto.getSedutaGiunta().getId()); //   new ArrayList<OrdineGiorno>(resoconto.getSedutaGiunta().getOdgs());
			List<AttiOdg> attiOdgList = new ArrayList<AttiOdg>();
			OrdineGiorno odgBase = null;
			for (OrdineGiorno ordineGiorno : odgs) {
				if (ordineGiorno.getTipoOdg().getId() == 1 || ordineGiorno.getTipoOdg().getId() == 2) {
					odgBase = ordineGiorno;
				}
				//Add Odg Base
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 1 || 
					   attiOdg.getOrdineGiorno().getTipoOdg().getId() == 2 ) {
						if(attiOdg.getEsito()==null || !attiOdg.getEsito().equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice())) {
							attiOdgList.add(attiOdg);
						}
					}
				}
				//Add Odg Suppletivo
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 3 ) {
						if(attiOdg.getEsito()==null || !attiOdg.getEsito().equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice())) {
							attiOdgList.add(attiOdg);
						}
					}
				}
				//Add Odg Fuori Sacco
				for(AttiOdg attiOdg :ordineGiorno.getAttos()) {
					if(attiOdg.getOrdineGiorno().getTipoOdg().getId() == 4) {
						if(attiOdg.getEsito()==null || !attiOdg.getEsito().equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice())) {
							attiOdgList.add(attiOdg);
						}
					}
				}
			}
			
			boolean isAttiOdgOrdered = true;
			for(AttiOdg attoOdg: attiOdgList) {
				if (((attoOdg.getNumeroDiscussione() == null) || (attoOdg.getNumeroDiscussione().intValue() < 1))) {
					isAttiOdgOrdered = false;
					break;
				}
			}
			
			if(isAttiOdgOrdered) {
				//ordino atti in base al numero discussione
				Collections.sort(attiOdgList,new Comparator<AttiOdg>(){
					@Override
				    public int compare(AttiOdg atto1, AttiOdg atto2){
						return atto1.getNumeroDiscussione().compareTo(atto2.getNumeroDiscussione());
					}
				});
			}else {
				int valNumeroDiscussione = 1;
				for(AttiOdg attoOdg: attiOdgList) {
					if (((attoOdg.getNumeroDiscussione() == null) || (attoOdg.getNumeroDiscussione().intValue() < 1))) {
						attoOdg.setNumeroDiscussione(valNumeroDiscussione);
						valNumeroDiscussione++;
					}
				}
			}
			
			
			List<Esito> esiti = esitoRepository.findAll();
			
			boolean stampaSvolti = false;
			boolean stampaApprovati = false;
			boolean stampaRespinti = false;
			boolean stampaRinviati = false;
			boolean stampaRitirati = false;
			
			
			
			Map<String, Map<String, List<AttiOdg>>> map = new HashMap<String, Map<String, List<AttiOdg>>>();
			
			if(attiOdgList != null && attiOdgList.size() > 0) {
				
				for (int i = 0; i < attiOdgList.size(); i++) {
					
					Atto atto = attiOdgList.get(i).getAtto();
					String codiceTipoAtto = atto.getTipoAtto().getCodice();
					String esito = attiOdgList.get(i).getEsito() != null ? attiOdgList.get(i).getEsito() : "";
					
					if(codiceTipoAtto.equalsIgnoreCase("QT")) {
						if(esito.equalsIgnoreCase("risposta_in_aula")) {
							stampaSvolti = true;
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.SVOLTI);
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("VERB")) {
						if(esito.equalsIgnoreCase("approvato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.APPROVATI);
							stampaApprovati = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("COM")) {
						if(esito.equalsIgnoreCase("svolta")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.SVOLTI);
							stampaSvolti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
						
					}else if(codiceTipoAtto.equalsIgnoreCase("ODG")) {
						if(esito.equalsIgnoreCase("approvata") || esito.equalsIgnoreCase("approvata_del") || esito.equalsIgnoreCase("approvata_modifiche") || esito.equalsIgnoreCase("approvata_modifiche_del")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.APPROVATI);
							stampaApprovati = true;
						}else if(esito.equalsIgnoreCase("respinto") || esito.equalsIgnoreCase("respinto_emendato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RESPINTI);
							stampaRespinti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("DC") || codiceTipoAtto.equalsIgnoreCase("DIC") || codiceTipoAtto.equalsIgnoreCase("DPC") ) {
						if(esito.equalsIgnoreCase("approvata") || esito.equalsIgnoreCase("approvata_del") || esito.equalsIgnoreCase("approvata_modifiche") || esito.equalsIgnoreCase("approvata_modifiche_del")) {
							this.insertIntoMap(attiOdgList.get(i), "DELIBERA", map, SezioneEnum.SEZIONI.APPROVATI);
							stampaApprovati = true;
						}else if(esito.equalsIgnoreCase("respinto") || esito.equalsIgnoreCase("respinto_emendato")) {
							this.insertIntoMap(attiOdgList.get(i), "DELIBERA", map, SezioneEnum.SEZIONI.RESPINTI);
							stampaRespinti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), "DELIBERA", map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), "DELIBERA", map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("INT")) {
						if(esito.equalsIgnoreCase("risposta_in_aula")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.SVOLTI);
							stampaSvolti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("MZ")) {
						if(esito.equalsIgnoreCase("approvata") || esito.equalsIgnoreCase("approvata_del") || esito.equalsIgnoreCase("approvata_modifiche") || esito.equalsIgnoreCase("approvata_modifiche_del")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.APPROVATI);
							stampaApprovati = true;
						}else if(esito.equalsIgnoreCase("respinto") || esito.equalsIgnoreCase("respinto_emendato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RESPINTI);
							stampaRespinti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("RIS")) {
						if(esito.equalsIgnoreCase("approvata") || esito.equalsIgnoreCase("approvata_del") || esito.equalsIgnoreCase("approvata_modifiche") || esito.equalsIgnoreCase("approvata_modifiche_del")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.APPROVATI);
							stampaApprovati = true;
						}else if(esito.equalsIgnoreCase("respinto") || esito.equalsIgnoreCase("respinto_emendato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RESPINTI);
							stampaRespinti = true;
						}else if(esito.equalsIgnoreCase("rinviato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RINVIATI);
							stampaRinviati = true;
						}else if(esito.equalsIgnoreCase("ritirato")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.RITIRATI);
							stampaRitirati = true;
						}
					}else if(codiceTipoAtto.equalsIgnoreCase("DAT")) {
						if(esito.equalsIgnoreCase("risposta_in_aula")) {
							this.insertIntoMap(attiOdgList.get(i), codiceTipoAtto, map, SezioneEnum.SEZIONI.SVOLTI);
							stampaSvolti = true;
						}
					}
				}
				
			}
			
			
			
			
			
			Context context = new Context(Locale.forLanguageTag(IT));
			context.setVariable("resoconto", resoconto);
			
			
			context.setVariable("odg", odgBase);
			context.setVariable("attiOdg", attiOdgList);
			context.setVariable("attiOdgMap", map);
			context.setVariable("sedutaGiunta", resoconto.getSedutaGiunta());
			context.setVariable("esiti", esiti);
			
			context.setVariable("stampaSvolti",stampaSvolti);
			context.setVariable("stampaApprovati",stampaApprovati);
			context.setVariable("stampaRespinti",stampaRespinti);
			context.setVariable("stampaRinviati",stampaRinviati);
			context.setVariable("stampaRitirati",stampaRitirati);
			
			//TODO:Gestione fuso orario da centralizzare con custom tag su thymeleaf
			DateTime inizioLavoriEffettiva = formatTime(odgBase.getSedutaGiunta().getInizioLavoriEffettiva());
			context.setVariable("inizioLavoriEffettiva", inizioLavoriEffettiva);
			
			
			String htmlRT = creaTemplatePreview(reportDto, context);

			ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + resoconto.getSedutaGiunta().getNumero()), null);
//			InputStream inputHtml = result.getInputStream();
			File file = File.createTempFile("withfiligrana_", ".pdf");
			FileOutputStream fop = new FileOutputStream(file);
			fop.write(result.getByteArray());
			fop.flush();
			fop.close();
			return file;
			
		}
		catch (Exception e) {
			// Sollevo RuntimeException altrimenti Spring non annulla la transazione e gli stati diventano disallineati
			throw new RuntimeException("Errore durante la generazione anteprima Resoconto.", e);
		}
	}
	
	public Resoconto createResoconto(
			final long sedutaId, final TipoResocontoEnum tipo, Long idModelloHtml) {
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaId);
		Resoconto resoconto = resocontoRepository.findBySedutagiuntaIdAndTipo(sedutaId, tipo.getId());		
		if(resoconto == null){
			resoconto = new Resoconto();
			resoconto.setTipo(tipo.getId());
			
			resoconto.setSedutaGiunta(seduta);
			//resocontoRepository.save(resoconto);
		}
		
		
		return resoconto;
	}
	/*
	 * TODO: In ATTICO non previsto
	 *
	@Transactional(readOnly = true)
	private File executePreviewPresenzeAssenze(Resoconto resoconto, List<OrdineGiorno> odgs, ReportDTO reportDto) throws IOException, DocumentException {

		List<AttiOdg> attiOdg = new ArrayList<AttiOdg>();
		OrdineGiorno odgBase = null;
		for (OrdineGiorno ordineGiorno : odgs) {
			if (ordineGiorno.getTipoOdg().getId() == 1 || ordineGiorno.getTipoOdg().getId() == 2) {
				odgBase = ordineGiorno;
			}
			attiOdg.addAll(ordineGiorno.getAttos());
		}

		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("resoconto", resoconto);
		context.setVariable("odg", odgBase);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream inputHtml = result.getInputStream();

		return createResoconto(resoconto, odgBase, attiOdg, inputHtml);
	}
	*/
	/*
	private File createResoconto(Resoconto resoconto, 
			OrdineGiorno odgBase, List<AttiOdg> attiOdg, InputStream inputHtml) throws IOException, DocumentException {

		SedutaGiunta seduta = resoconto.getSedutaGiunta();
		String dataSeduta = seduta.getPrimaConvocazioneInizio().toString("dd/MM/yyyy");

		String numOdg = seduta.getNumero();
		//TODO VERIFICARE CHE VANNO BENE LE SEGUENTI RIGHE
		String prefixToc = "Resoconto Seduta n." + numOdg + "  del  " + dataSeduta + " - Indice Analitico";
		String prefixOdg = "Resoconto Seduta n." + numOdg + "  del  " + dataSeduta + " - pag. ";

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		InputStream inputLogoOdg = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoPalazzoEnte()));
		InputStream inputLogoIntestazione = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		
		String organo = "Segreteria Generale ";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				odgBase.getSedutaGiunta().getOrgano())) {
			organo += "della Giunta Comunale";
		}
		else {
			organo += "del Consiglio Comunale";
		}
		File fileOdgBase = cartaStampataService.addFiligranaOrdinegiorno(true, 
				inputHtml, inputLogo, inputLogoOdg, inputLogoIntestazione, organo);
		
		IOUtils.closeQuietly(inputHtml);
		IOUtils.closeQuietly(inputLogo);
		IOUtils.closeQuietly(inputLogoOdg);
		
		PdfReader pdfReaderOdgBase = new PdfReader(new FileInputStream(fileOdgBase));
		int numPagesOdgBase = pdfReaderOdgBase.getNumberOfPages();

		OdgPdfObject odg = cartaStampataService.addListaResocontoOdg(resoconto.getTipo(), attiOdg);
		File fileOdgs = odg.getBody();

		//  IN ATTICO NON PREVISTO
		// File fileSottoscrittoriResoconto = cartaStampataService.addListaSottoscrittoriResoconto(seduta);
		
		int numPagesToc = 0;
		
		/*
		 * IN ATTICO NON PREVISTO
		 *
		PdfReader pdfReaderToc = null;
		File filetoc = cartaStampataService.addIndiceAnaliticoResoconto(odg);
		pdfReaderToc = new PdfReader(new FileInputStream(filetoc));
		numPagesToc = pdfReaderToc.getNumberOfPages();
		 

		PdfReader pdfReaderListaOdgs = new PdfReader(new FileInputStream(fileOdgs));

		//  IN ATTICO NON PREVISTO
		// PdfReader pdfReaderSottoscrittoriResoconto = new PdfReader(new FileInputStream(fileSottoscrittoriResoconto));

		int startPaginationToc = numPagesOdgBase + 1;
		int startPaginationOdg = numPagesOdgBase + numPagesToc + 1;

		// File fileOdgBase = File.createTempFile("odgbase_", ".pdf"); test ottimizzazione
		PdfStamper pdfStamperUnion = new PdfStamper(pdfReaderOdgBase, new FileOutputStream(fileOdgBase));
		
		/*
		 * IN ATTICO NON PREVISTO
		 *
		for (int i = 1; i <= numPagesToc; i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderToc, i);
			pdfStamperUnion.insertPage(pdfReaderOdgBase.getNumberOfPages() + i, pdfReaderOdgBase.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgBase.getNumberOfPages()).addTemplate(page, 0, 0);
		}
		 
		for (int i = 1; i <= pdfReaderListaOdgs.getNumberOfPages(); i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderListaOdgs, i);
			pdfStamperUnion.insertPage(pdfReaderOdgBase.getNumberOfPages() + i, pdfReaderOdgBase.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgBase.getNumberOfPages()).addTemplate(page, 0, 0);
		}

		/*
		 * IN ATTICO NON PREVISTO
		 *
		for (int i = 1; i <= pdfReaderSottoscrittoriResoconto.getNumberOfPages(); i++) {
			PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderSottoscrittoriResoconto, i);
			pdfStamperUnion.insertPage(pdfReaderOdgBase.getNumberOfPages() + i, pdfReaderOdgBase.getPageSize(1));
			pdfStamperUnion.getOverContent(pdfReaderOdgBase.getNumberOfPages()).addTemplate(page, 0, 0);
		}
		 
		
		for (int i = startPaginationToc; i < startPaginationToc + numPagesToc; i++) {
			Rectangle pageSize = pdfReaderOdgBase.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT, new Phrase(prefixToc, FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)),
					pageSize.getWidth() - 60, pageSize.getHeight() - 30, 0);
		}
		for (int i = startPaginationOdg; i <= pdfReaderOdgBase.getNumberOfPages(); i++) {
			Rectangle pageSize = pdfReaderOdgBase.getPageSize(i);
			ColumnText.showTextAligned(pdfStamperUnion.getOverContent(i), Element.ALIGN_RIGHT,
					new Phrase(String.format(prefixOdg + "%s", i - startPaginationOdg + 1), FontFactory.getFont(FontFactory.TIMES_ROMAN, 8, Font.NORMAL)), pageSize.getWidth() - 60,
					pageSize.getHeight() - 30, 0);
		}

		pdfStamperUnion.close();
		pdfReaderListaOdgs.close();
		//  IN ATTICO NON PREVISTI
		// pdfReaderSottoscrittoriResoconto.close();
		// if (pdfReaderToc != null)
		//	pdfReaderToc.close();
		pdfReaderOdgBase.close();

		return fileOdgBase;
	} 
   */
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	@Transactional(readOnly = true)
	private File executePreviewLettera(Atto atto, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("atto", atto);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File filefiligrana = cartaStampataService.addFiligranaLogoCentrale(input, inputLogo);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), pageSize.getWidth() - 15,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}
	@Transactional
	public File executePreviewRelata(RelataDiPubblicazioneDto datiRelata, ReportDTO reportDto) throws IOException, DocumentException {
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("datiRelata", datiRelata);
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		//File filefiligrana = cartaStampataService.addFiligrana(input, inputLogo);
		String banner = datiRelata.getAooProponente().getAooPadre() != null ? datiRelata.getAooProponente().getAooPadre().getLogo() : datiRelata.getAooProponente().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, datiRelata.getAooProponente() , null, urlSitoEnte, false, false, false);

		IOUtils.closeQuietly(input);

		File inputFile = File.createTempFile(reportDto.getTipoDoc(), ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(inputFile));

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", i), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)), pageSize.getWidth() - 15,
					15, 0);
		}

		pdfStamper.close();
		pdfReader.close();
		return inputFile;
	}
	*/

	@Transactional(readOnly = true)
	private File executePreviewVerbale(Verbale verbale, OrdineGiorno odgFuorisacco, ReportDTO reportDto) throws IOException, DocumentException, DmsException {

		String tipoDocCodice = "";
		if (SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(
				verbale.getSedutaGiunta().getOrgano())) {
			tipoDocCodice = TipoDocumentoEnum.verbalegiunta.name();
		}
		else {
			tipoDocCodice = TipoDocumentoEnum.verbaleconsiglio.name();
		}
		reportDto.setTipoDoc(tipoDocCodice);
		
		Context context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("verbale", verbale);
		context.setVariable("fuorisacco", odgFuorisacco != null);
		//odg.sedutaGiunta.inizioLavoriEffettiva
		
		
		
		String htmlRT = creaTemplatePreview(reportDto, context);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), ""), null);
		InputStream inputHtml = result.getInputStream();

		File fileVerbale = createVerbale(odgFuorisacco, inputHtml);

		context = new Context(Locale.forLanguageTag(IT));
		context.setVariable("noteFinali", verbale.getNoteFinali());
		context.setVariable("sottoscrittori", verbale.getSottoscrittori());
		ClassPathResource resource = new ClassPathResource("/modelli/areaSottoscrittori.html");
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		htmlRT = creaTemplatePreview(FileUtils.readFileToString(resource.getFile()), context, modelloHtml);
		result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), ""), null);
		inputHtml = result.getInputStream();
		fileVerbale = aggiungiSottoscrittoriVerbale(fileVerbale, inputHtml);

		return aggiungiAllegatiVerbale(fileVerbale, verbale.getAllegati());
	}

	private File createVerbale(OrdineGiorno odgFuoriSacco, InputStream inputHtml) throws IOException, DocumentException {

		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnte()));
		File fileVerbale = cartaStampataService.addFiligranaLogoCentrale(inputHtml, inputLogo);
		IOUtils.closeQuietly(inputHtml);
		IOUtils.closeQuietly(inputLogo);
		PdfReader pdfReaderVerbale = new PdfReader(new FileInputStream(fileVerbale));

		PdfStamper pdfStamperUnion = new PdfStamper(pdfReaderVerbale, new FileOutputStream(fileVerbale));
		PdfReader pdfReaderListaOdgs = null;
		if (odgFuoriSacco != null) {
			List<AttiOdg> attiOdg = odgFuoriSacco.getAttos();
			OdgPdfObject odg = cartaStampataService.addListaOdg(false, attiOdg, 4, 1);
			File fileOdgs = odg.getBody();
			pdfReaderListaOdgs = new PdfReader(new FileInputStream(fileOdgs));
			for (int i = 1; i <= pdfReaderListaOdgs.getNumberOfPages(); i++) {
				PdfImportedPage page = pdfStamperUnion.getImportedPage(pdfReaderListaOdgs, i);
				pdfStamperUnion.insertPage(pdfReaderVerbale.getNumberOfPages() + i, pdfReaderVerbale.getPageSize(1));
				pdfStamperUnion.getOverContent(pdfReaderVerbale.getNumberOfPages()).addTemplate(page, 0, 0);
			}
		}
		pdfStamperUnion.close();
		if (pdfReaderListaOdgs != null)
			pdfReaderListaOdgs.close();
		pdfReaderVerbale.close();
		return fileVerbale;
	}

	private File aggiungiSottoscrittoriVerbale(File fileVerbale, InputStream input) throws IOException, DocumentException {
		File resultFile = File.createTempFile("withSottoscrittori_", ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(fileVerbale));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		PdfReader reader = new PdfReader(input);
		for (int npage = 1; npage <= reader.getNumberOfPages(); npage++) {
			int pageNumber = pdfReader.getNumberOfPages() + 1;
			pdfStamper.insertPage(pageNumber, reader.getPageSize(npage));
			pdfStamper.replacePage(reader, npage, pageNumber);
		}
		pdfStamper.close();
		pdfReader.close();
		return resultFile;
	}

	private File aggiungiAllegatiVerbale(File fileVerbale, Set<DocumentoInformatico> allegati) throws IOException, DocumentException, DmsException {
		List<File> listFile = new ArrayList<File>();
		if (allegati != null) {
			for (DocumentoInformatico docInfo : allegati) {
				if (docInfo.getParteIntegrante() != null && docInfo.getParteIntegrante() == true) {
					it.linksmt.assatti.datalayer.domain.File fileD = docInfo.getFile();
					
//					byte[] b = fileD.getContenuto();
					byte[] b = dmsService.getContent(fileD.getCmisObjectId());
	
					File tmp = null;
					String tmpExt = FilenameUtils.getExtension(fileD.getNomeFile());
	
					tmp = File.createTempFile("tmp_", tmpExt);
					FileUtils.writeByteArrayToFile(tmp, b);
	
					InputStream inputAllegato = null;
					if ("pdf".equalsIgnoreCase(tmpExt)) {
						inputAllegato = new FileInputStream(tmp);
					}
					else {
						ByteArrayResource result = invokeConvertToXXX(tmp, CONVERT_TO_PDF,null, null);
						inputAllegato = result.getInputStream();
					}
	
					File tmpPDF = File.createTempFile("tmp_", ".pdf");
					FileUtils.copyInputStreamToFile(inputAllegato, tmpPDF);
	
					listFile.add(tmpPDF);
	
					tmp.delete();
				}
			}
		}
		File resultFile = File.createTempFile("verbale", ".pdf");

		PdfReader pdfReader = new PdfReader(new FileInputStream(fileVerbale));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		Integer numeroDiPagineTotaleAllegati = 0;
		for (File file : listFile) {
			PdfReader reader = new PdfReader(new FileInputStream(file));

			for (int npage = 1; npage <= reader.getNumberOfPages(); npage++) {
				numeroDiPagineTotaleAllegati++;
				int pageNumber = pdfReader.getNumberOfPages() + 1;
				pdfStamper.insertPage(pageNumber, reader.getPageSize(npage));
				pdfStamper.replacePage(reader, npage, pageNumber);
			}
		}

		pdfStamper.close();
		pdfReader.close();

		return resultFile;
	}

	public Atto getAttoTest() {
		return ReportServiceDataTest.getAttoTest();
	}
	public Atto getSottoscrittoriAttoTest() {
		return ReportServiceDataTest.getSottoscrittoriAttoTest();
	}
	public SedutaGiunta getSedutaTest() {
		return ReportServiceDataTest.getSedutaGiuntaTest(null);
	}
	public Profilo getProfiloTest() {
		return ReportServiceDataTest.getProfiloTest(0);
	}

	private RelataDiPubblicazioneDto getDatiRelataPubblicazioneTest() {
		return ReportServiceDataTest.getDatiRelataPubblicazioneTest();
	}
	
	public File test(ReportDTO reportDto) throws IOException, DocumentException, DmsException {

		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		if (!modelloHtml.getTipoDocumento().getCodice().equalsIgnoreCase(reportDto.getTipoDoc())) {
			throw new DocumentException("Object Not Found");
		}

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		
		File rtFile = null;
		String rtHTML = null;
		
		if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.variazione_estremi_seduta_giunta.name())
				|| reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.annullamento_seduta_giunta.name())
				|| reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.variazione_estremi_seduta_consiglio.name())
				|| reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.annullamento_seduta_consiglio.name()) ) {
			/*
			 * TODO: In ATTICO non previsto
			 * 
				|| reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.foglio_assenti_riunione_giunta.name())
			*/
			
			SedutaGiunta object = getSedutaTest();
			Profilo object2 = getProfiloTest();
			contextRT.setVariable("sedutaGiunta", object);
			contextRT.setVariable("atto", getSottoscrittoriAttoTest());
			
			/*
			 * TODO: In ATTICO non previsto
			 * 
			if(reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.foglio_assenti_riunione_giunta.name())){
				RilevazioneAssenzaDto rilevazioneAssenzaDto = new RilevazioneAssenzaDto();
				List<ComponentiGiunta> componentiGiunta = ReportServiceDataTest.getComponentiGiuntaTest();
				componentiGiunta.addAll(ReportServiceDataTest.getComponentiGiuntaTestConsecutivi());
				rilevazioneAssenzaDto.setProgressivoDiInizio(utilityService.getProgressivoInizio(componentiGiunta));
				rilevazioneAssenzaDto.setProgressivoDiFine(utilityService.getProgressivoFine(componentiGiunta));
				rilevazioneAssenzaDto.setRigheAssenti(utilityService.getRigheAssentiDto(componentiGiunta));
				contextRT.setVariable("rilevazioneAssenzaDto", rilevazioneAssenzaDto);
			}
			*/
			
			rtHTML = creaTemplatePreview(reportDto, contextRT);
			rtFile = executePreviewComunicazioneSeduta(rtHTML, object, object2, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.verbalegiunta.name()) ||
				 reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.verbaleconsiglio.name())) {
			Verbale object = ReportServiceDataTest.getVerbaleTest();
			SedutaGiunta object2 = getSedutaTest();
			object.setSedutaGiunta(object2);
			rtFile = executePreviewVerbale(object, ReportServiceDataTest.getOrdineGiornoTest(4L, object2), reportDto);
		}
		
		/*
		 * TODO: In ATTICO non previsti
		 * 
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.parere.name())) {
			object = ReportServiceDataTest.getParereTest();
			rtFile = executePreviewParere(reportDto.getTipoDoc(), (Parere) object, reportDto, getAttoTest());
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.scheda_anagrafico_contabile.name())) {
			object = getAttoTest();
			rtFile = executePreviewSchedaAnagraficoContabile((Atto) object, reportDto);

		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.atto_inesistente.name())) {
			object = getAttoTest();
			contextRT.setVariable("atto", object);
			String htmlRT = creaTemplatePreview(reportDto, contextRT);
			rtFile = executePreviewAttoInesistente(htmlRT, (Atto) object, reportDto);
			
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.restituzione_su_istanza_ufficio_proponente.name())) {
			object = getAttoTest();
			contextRT.setVariable("atto", object);
			String htmlRT = creaTemplatePreview(reportDto, contextRT);
			rtFile = executePreviewRestituzioneSuIstanzaUfficioProponente(htmlRT, (Atto) object, reportDto);
			
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.tipo_proposta_delibera_consiglio.name())) {
			object = getAttoTest();
			rtFile = executePreviewPropostaDeliberaConsiglio((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.tipo_proposta_delibera_giunta.name())) {
			object = getAttoTest();
			rtFile = executePreviewPropostaDeliberaGiunta((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.schema_disegno_legge.name())) {
			object = getAttoTest();
			rtFile = executePreviewSchemaDisegnoLegge((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.disegno_legge.name())) {
			object = getAttoTest();
			rtFile = executePreviewDisegnoLegge((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.comunicazione.name())) {
			object = getAttoTest();
			rtFile = executePreviewComunicazione((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.referto_tecnico.name())) {
			object = getAttoTest();
			rtFile = executePreviewRefertoTecnico((Atto) object, reportDto);

		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.delibera.name())) {
			object = getAttoTest();
			object2 = ReportServiceDataTest.getComponentiGiuntaTest();
			OrdineGiorno odgTest = ReportServiceDataTest.getOrdineGiornoTest(1l);
			rtFile = executePreviewDeliberaGiunta((Atto) object, (List<ComponentiGiunta>) object2, odgTest,  ((Atto) object).getSedutaGiunta(), reportDto);

		}
		*/
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.giacenza.name())) {
			SedutaGiunta object2 = getSedutaTest();
			OrdineGiorno object = ReportServiceDataTest.getOrdineGiornoTest(1L, object2);
			rtFile = executePreviewGiacenza(object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.ordinegiornogiunta.name()) ||
				 reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.ordinegiornoconsiglio.name())) {
			SedutaGiunta object = ReportServiceDataTest.getSedutaGiuntaTest(null);
			long tipoOdg = (long) (Math.random() * 4) + 1; // 1 ord, 2 str, 3 suppl, 4 fuorisacco
			if (tipoOdg == 1 | tipoOdg == 2)
				rtFile = executePreviewOrdinegiornoBase((SedutaGiunta) object, ReportServiceDataTest.getOrdineGiornoTest(tipoOdg, object), reportDto);
			else if (tipoOdg == 3) {
				rtFile = executePreviewOrdinegiornoBase((SedutaGiunta) object, ReportServiceDataTest.getOrdineGiornoTest(1L, object), reportDto);
				rtFile = executePreviewOrdinegiornoSuppletivo((SedutaGiunta) object, rtFile, null, ReportServiceDataTest.getOrdineGiornoTest(3L, object), reportDto);
			}
			else if (tipoOdg == 4) {
				rtFile = executePreviewOrdinegiornoBase((SedutaGiunta) object, ReportServiceDataTest.getOrdineGiornoTest(1L, object), reportDto);
				rtFile = executePreviewOrdinegiornoFuoriSacco((SedutaGiunta) object, rtFile, ReportServiceDataTest.getOrdineGiornoTest(4L, object), reportDto);
			}

		}
		/*
		 * TODO: In ATTICO non previsti
		 * 
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.resoconto.name())) {
			object = ReportServiceDataTest.getResocontoTest();

			List<OrdineGiorno> odgs = new ArrayList<OrdineGiorno>();
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(1L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(3L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(3L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(3L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(3L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(3L));
			odgs.add(ReportServiceDataTest.getOrdineGiornoTest(4L));
			rtFile = executePreviewResoconto((Resoconto) object, odgs, reportDto);

		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.lettera.name())) {
			// object = ReportServiceDataTest.getLetteraTest();
			object = getAttoTest();
			rtFile = executePreviewLettera((Atto) object, reportDto);
		}
		else if (reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.relata_pubblicazione.name())) {
			// object = ReportServiceDataTest.getLetteraTest();
			object = getDatiRelataPubblicazioneTest();
			rtFile = executePreviewRelata((RelataDiPubblicazioneDto) object, reportDto);
		}else if(reportDto.getTipoDoc().equalsIgnoreCase(TipoDocumentoEnum.ritiro_in_giunta.name())){
			object = ReportServiceDataTest.getParereTest();
			
			rtFile = executePreviewParere(reportDto.getTipoDoc(), (Parere) object, reportDto, getAttoTest());
		}
		*/
		
		else {
			Atto object = getAttoTest();
			contextRT.setVariable("atto", object);
			
			// Movimenti contabili
			/*
			 * Passo un oggetto con l'elenco dei movimenti contabili al modello html
			 */
			List<MovimentoContabileDto> listMovimentiContabili = new Gson().fromJson(
					"[{\"movImpAcce\":{\"archivio\":\"E\",\"tipoMovimento\":\"INS_IMP\",\"esercizio\":\"2019\",\"eu\":\"U\",\"capitolo\":\"25\",\"articolo\":\"\",\"descCapitolo\":\"RETRIBUZIONE PER INCARICHI FUORI ORGANICO UFFICIO DEL SINDACO\",\"annoImpacc\":\"2019\",\"numeroImpacc\":\"2596\",\"subImpacc\":\"\",\"descImpacc\":\"wer\",\"importoImpacc\":\"23,00\",\"liquidatoImpacc\":\"0,00\",\"ordinatoImpacc\":\"0,00\",\"oggetto\":\"wer\",\"annoFinanziamento\":\"2019\",\"respProc\":\"B3\",\"descRespProc\":\"SERVIZIO AMMINISTRAZIONE DEL PERSONALE\",\"importo\":\"23,00\",\"voceEconomica\":\"20\",\"descVoceEconomica\":\"RETRIBUZIONI LORDE\",\"centroCosto\":\"**NO\",\"descCentroCosto\":\"**NO  *** NO ANALITICA NON RIBALTARE\",\"codDebBen\":\"\",\"descCodDebBen\":\"\",\"naturaCoge\":\"\",\"descNaturaCoge\":\"\",\"codiceCup\":\"\",\"codiceCig\":\"\",\"codFondo\":\"\",\"descFondo\":\"\",\"codTipoFinanz\":\"00\",\"descTipoFinanz\":\"ENTRATE PROPRIE\",\"siope\":\"1104\",\"descSiope\":\"Competenze fisse ed accessorie per il personale a tempo determinato\",\"mutuo\":\"\",\"descMutuo\":\"\",\"perfezionamento\":\"\",\"descPerfezionamento\":\"\",\"vincolo\":\"\",\"descVincolo\":\"\",\"programma\":\"10RPP\",\"descProgramma\":\"Un Comune efficiente\",\"progetto\":\"^^^^^\",\"descProgetto\":\"Un Comune efficiente\",\"codMeccanografico\":\"1.01.01.01\",\"codArmonizzato\":\"01.01.1\",\"codLibroIva\":\"\",\"descLibroIva\":\"\",\"pianoFinanziario\":\"1.01.01.01.006\",\"pianoFinanziarioDesc\":\"Voci stipendiali corrisposte al personale a tempo determinato\",\"missioneCapitolo\":\"01\",\"programmaMissioneCapitolo\":\"01\"}},{\"movImpAcce\":{\"archivio\":\"E\",\"tipoMovimento\":\"INS_IMP\",\"esercizio\":\"2019\",\"eu\":\"U\",\"capitolo\":\"25\",\"articolo\":\"\",\"descCapitolo\":\"RETRIBUZIONE PER INCARICHI FUORI ORGANICO UFFICIO DEL SINDACO\",\"annoImpacc\":\"2019\",\"numeroImpacc\":\"2604\",\"subImpacc\":\"\",\"descImpacc\":\"deqe\",\"importoImpacc\":null,\"liquidatoImpacc\":\"0,00\",\"ordinatoImpacc\":\"0,00\",\"oggetto\":\"deqe\",\"annoFinanziamento\":\"2019\",\"respProc\":\"B3\",\"descRespProc\":\"SERVIZIO AMMINISTRAZIONE DEL PERSONALE\",\"importo\":\"77,70\",\"voceEconomica\":\"20\",\"descVoceEconomica\":\"RETRIBUZIONI LORDE\",\"centroCosto\":\"**NO\",\"descCentroCosto\":\"**NO  *** NO ANALITICA NON RIBALTARE\",\"codDebBen\":\"\",\"descCodDebBen\":\"\",\"naturaCoge\":\"\",\"descNaturaCoge\":\"\",\"codiceCup\":\"\",\"codiceCig\":\"\",\"codFondo\":\"\",\"descFondo\":\"\",\"codTipoFinanz\":\"00\",\"descTipoFinanz\":\"ENTRATE PROPRIE\",\"siope\":\"1104\",\"descSiope\":\"Competenze fisse ed accessorie per il personale a tempo determinato\",\"mutuo\":\"\",\"descMutuo\":\"\",\"perfezionamento\":\"\",\"descPerfezionamento\":\"\",\"vincolo\":\"\",\"descVincolo\":\"\",\"programma\":\"10RPP\",\"descProgramma\":\"Un Comune efficiente\",\"progetto\":\"^^^^^\",\"descProgetto\":\"Un Comune efficiente\",\"codMeccanografico\":\"1.01.01.01\",\"codArmonizzato\":\"01.01.1\",\"codLibroIva\":\"\",\"descLibroIva\":\"\",\"pianoFinanziario\":\"1.01.01.01.006\",\"pianoFinanziarioDesc\":\"Voci stipendiali corrisposte al personale a tempo determinato\",\"missioneCapitolo\":\"01\",\"programmaMissioneCapitolo\":\"01\"}},{\"liquidazione\":{\"anno\":\"2019\",\"numero\":\"0000813\",\"data\":\"20190903\",\"oggetto\":\"test liquidazione 0309 vm\",\"importo\":\"22,00\",\"documento\":[{\"id\":\"204078\",\"soggettoCod\":\"46792\",\"soggettoNome\":\"ALCOOLTEST MARKETING ITALY SRL\",\"anno\":\"2019\",\"numero\":\"171\",\"tipo\":\"HF\",\"data\":\"03/09/2019\",\"oggetto\":\"Liq.Rapida serviziofn\",\"importo\":null,\"impegno\":{\"anno\":\"2014\",\"numero\":\"7003\",\"sub\":\"\",\"oggetto\":\"NARDELLA,BETTINI E BALLI QUOTA TFR 2014\"}}]}},{\"dettaglioLiquidazione\":{\"annoLiq\":\"2019\",\"numeroLiq\":\"813\",\"dataLiq\":\"03/09/2019\",\"esercizio\":\"2019\",\"codRproc\":\"B3\",\"descRProc\":\"SERVIZIO AMMINISTRAZIONE DEL PERSONALE\",\"nomeRProc\":\"\",\"nomeRProc2\":\"\",\"dirigente\":\" nome del dirigente inserito a mano da codice\",\"tipoAttoLiq\":\"A\",\"organoAttoLiq\":\"SVI-DL\",\"annoAttoLiq\":\"2019\",\"numeroAttoLiq\":\"34\",\"dataAttoLiq\":\"03/09/2019\",\"dataEsecAttoLiq\":\"\",\"descLiquidazione\":\"test liquidazione 0309 vm\",\"totaleLiq\":\"22,00\",\"totaleImponibileLiq\":\"22,00\",\"totaleRitenutaLiq\":\"0,00\",\"numeroRighe\":\"1\",\"listaCapitoli\":[{\"capitolo\":\"952\",\"articolo\":\"\",\"descr\":\"CONTRIBUTI PER AMMINISTRATORI COMUNALI\",\"meccanografico\":\"01.01.1\",\"importoTotaleCapitolo\":\"22,00\",\"assestatoCapit\":\"0,00\",\"listaImpegni\":[{\"anno\":\"2014\",\"numero\":\"7003\",\"sub\":\"\",\"impFormattato\":\"2014/7003\",\"descr\":\"NARDELLA,BETTINI E BALLI QUOTA TFR 2014\",\"tipoProvImp\":\"A\",\"organoProvImp\":\"FFT\",\"annoProvImp\":\"2014\",\"numeroProvImp\":\"2\",\"importoTotaleImpegno\":\"22,00\",\"importoDisponibileImp\":\"1.790,20\",\"importoAssestatoImp\":\"4.530,00\",\"codiceCUP\":\"\",\"codiceCIG\":\"\",\"codiceCGU\":\"1325\",\"fondo\":\"\",\"tipoFinanziamento\":\"00\",\"pianoFinanziario\":\"1.01.02.01.999\",\"descPianoFinanziario\":\"Altri contributi sociali effettivi n.a.c.\",\"listaSogg\":[{\"soggetto\":\"46792\",\"descr\":\"ALCOOLTEST MARKETING ITALY SRL \",\"codiceFisc\":\"\",\"pIva\":\"03198140547\",\"modoPaga\":\"CCB - IT69O0539038270000000000159\",\"importoTotSogg\":\"22,00\",\"notePagamento\":\"\",\"listaDocumenti\":[{\"annoDoc\":\"2019\",\"numeroDoc\":\"171\",\"tipoDoc\":\"HF\",\"descrTipoDoc\":\"Documento Generato Automaticamente\",\"dataDoc\":\"03/09/2019\",\"oggetto\":\"Liq.Rapida serviziofn\",\"importo\":\"22,00\",\"dataRegistrDoc\":\"03/09/2019\",\"dataScadDoc\":\"03/09/2019\",\"importoTotaleDoc\":\"22,00\",\"numeroRata\":\"1\",\"numeroRegistrazione\":\"0\",\"ritenute\":\"\",\"codiceCig\":\"\",\"codiceCup\":\"\"}]}]}]}]}},{\"liquidazione\":{\"anno\":\"2019\",\"numero\":\"0000813\",\"data\":\"20190903\",\"oggetto\":\"test liquidazione 0309 vm\",\"importo\":\"22,00\",\"documento\":[{\"id\":\"204078\",\"soggettoCod\":\"46792\",\"soggettoNome\":\"ALCOOLTEST MARKETING ITALY SRL\",\"anno\":\"2019\",\"numero\":\"171\",\"tipo\":\"HF\",\"data\":\"03/09/2019\",\"oggetto\":\"Liq.Rapida serviziofn\",\"importo\":\"22,00\",\"impegno\":{\"anno\":\"2014\",\"numero\":\"7003\",\"sub\":\"\",\"oggetto\":\"NARDELLA,BETTINI E BALLI QUOTA TFR 2014\"}}]}},{\"dettaglioLiquidazione\":{\"annoLiq\":\"2019\",\"numeroLiq\":\"813\",\"dataLiq\":\"03/09/2019\",\"esercizio\":\"2019\",\"codRproc\":\"B3\",\"descRProc\":\"SERVIZIO AMMINISTRAZIONE DEL PERSONALE\",\"nomeRProc\":\"\",\"nomeRProc2\":\"\",\"dirigente\":\" nome del dirigente inserito a mano da codice\",\"tipoAttoLiq\":\"A\",\"organoAttoLiq\":\"SVI-DL\",\"annoAttoLiq\":\"2019\",\"numeroAttoLiq\":\"34\",\"dataAttoLiq\":\"03/09/2019\",\"dataEsecAttoLiq\":\"\",\"descLiquidazione\":\"test liquidazione 0309 vm\",\"totaleLiq\":\"22,00\",\"totaleImponibileLiq\":\"22,00\",\"totaleRitenutaLiq\":\"0,00\",\"numeroRighe\":\"1\",\"listaCapitoli\":[{\"capitolo\":\"952\",\"articolo\":\"\",\"descr\":\"CONTRIBUTI PER AMMINISTRATORI COMUNALI\",\"meccanografico\":\"01.01.1\",\"importoTotaleCapitolo\":\"22,00\",\"assestatoCapit\":\"0,00\",\"listaImpegni\":[{\"anno\":\"2014\",\"numero\":\"7003\",\"sub\":\"\",\"impFormattato\":\"2014/7003\",\"descr\":\"NARDELLA,BETTINI E BALLI QUOTA TFR 2014\",\"tipoProvImp\":\"A\",\"organoProvImp\":\"FFT\",\"annoProvImp\":\"2014\",\"numeroProvImp\":\"2\",\"dataEsecProvImp\":\"01/01/2014\",\"importoTotaleImpegno\":\"22,00\",\"importoDisponibileImp\":\"1.790,20\",\"importoAssestatoImp\":\"4.530,00\",\"codiceCUP\":\"\",\"codiceCIG\":\"\",\"codiceCGU\":\"1325\",\"fondo\":\"\",\"tipoFinanziamento\":\"00\",\"pianoFinanziario\":\"1.01.02.01.999\",\"descPianoFinanziario\":\"Altri contributi sociali effettivi n.a.c.\",\"listaSogg\":[{\"soggetto\":\"46792\",\"descr\":\"ALCOOLTEST MARKETING ITALY SRL \",\"codiceFisc\":\"\",\"pIva\":\"03198140547\",\"modoPaga\":\"CCB - IT69O0539038270000000000159\",\"importoTotSogg\":\"22,00\",\"notePagamento\":\"\",\"listaDocumenti\":[{\"annoDoc\":\"2019\",\"numeroDoc\":\"171\",\"tipoDoc\":\"HF\",\"descrTipoDoc\":\"Documento Generato Automaticamente\",\"dataDoc\":\"03/09/2019\",\"oggetto\":\"Liq.Rapida serviziofn\",\"importo\":\"22,00\",\"dataRegistrDoc\":\"03/09/2019\",\"dataScadDoc\":\"03/09/2019\",\"importoTotaleDoc\":\"22,00\",\"numeroRata\":\"1\",\"numeroRegistrazione\":\"0\",\"ritenute\":\"\",\"codiceCig\":\"\",\"codiceCup\":\"\"}]}]}]}]}}]",
					new TypeToken<ArrayList<MovimentoContabileDto>>(){}.getType());
			
			List<DettaglioLiquidazioneDto> listDettLiquidazione = new ArrayList<DettaglioLiquidazioneDto>();
			List<MovimentoLiquidazioneDto> listLiquidazione = new ArrayList<MovimentoLiquidazioneDto>();
			List<ImpAccertamentoDto> listMovImpAcce = new ArrayList<ImpAccertamentoDto>();
			
			boolean nascondiBen = false;
			if(object.getDatiContabili() != null && object.getDatiContabili().getNascondiBeneficiariMovimentiAtto() != null 
					&& object.getDatiContabili().getNascondiBeneficiariMovimentiAtto().booleanValue()) {
				nascondiBen = true;
			}
			
			if(listMovimentiContabili!=null) {
				for (MovimentoContabileDto movimentoContabileDto : listMovimentiContabili) {
					if (movimentoContabileDto.getDettaglioLiquidazione()!=null) {
						listDettLiquidazione.add(movimentoContabileDto.getDettaglioLiquidazione());
					}
					if(movimentoContabileDto.getLiquidazione()!=null) {
						if(movimentoContabileDto.getLiquidazione() != null && movimentoContabileDto.getLiquidazione().getDocumento() != null &&
								movimentoContabileDto.getLiquidazione().getDocumento().get(0) != null && nascondiBen) {
							movimentoContabileDto.getLiquidazione().getDocumento().get(0).setSoggettoNome(
									movimentoContabileDto.getLiquidazione().getDocumento().get(0).getSoggettoCod()!=null && !movimentoContabileDto.getLiquidazione().getDocumento().get(0).getSoggettoCod().isEmpty()
									?"*****"
											:"");
						}
						listLiquidazione.add(movimentoContabileDto.getLiquidazione());
					} 
					if(movimentoContabileDto.getMovImpAcce()!=null) {
						if(movimentoContabileDto.getMovImpAcce() != null && nascondiBen) {
							movimentoContabileDto.getMovImpAcce().setDescCodDebBen(
									movimentoContabileDto.getMovImpAcce().getCodDebBen()!=null && !movimentoContabileDto.getMovImpAcce().getCodDebBen().isEmpty()
									?"*****"
											:"");
						}
						listMovImpAcce.add(movimentoContabileDto.getMovImpAcce());
					}
				}
			}
			
			contextRT.setVariable("dettaglioMovimentiLiquidazione", listDettLiquidazione);
			contextRT.setVariable("movimentiContabiliLiquidazione", listLiquidazione);
			
			
			contextRT.setVariable("movimentiContabiliImpAcc", listMovImpAcce);
			
			
			List<ImpegnoDaStampareDto> listaImpegniDaStampare = new ArrayList<ImpegnoDaStampareDto>();
			listaImpegniDaStampare = getlistaImpegniDaStampare(listDettLiquidazione);
			contextRT.setVariable("listaImpegniDaStampare", listaImpegniDaStampare);
			
			
			
			rtHTML = creaTemplatePreview(reportDto, contextRT);
			
			// TODO: verificare se il metodo per generare l'atto è corretto
			
			rtFile = executePreviewAtto(rtHTML, (Atto) object, reportDto, 
					false, true, true);
			
//			rtFile = executePreviewAtto(rtHTML, (Atto) object, reportDto, 
//					getNascondiUrlSitoESpostaDatiIntestazione(modelloHtml), 
//					getStampaIntestazioneSoloFrontespizio(modelloHtml), 
//					getSoloLogoCentrale(modelloHtml));
			
			// getAggiungiRefertoTecnico(modelloHtml)
		}

//		if (object == null) {
//			throw new DocumentException("Object Not Found");
//		}

		return rtFile;
	}

	

	public File testOmissis(ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		reportDto.setOmissis(Boolean.TRUE);
		return test(reportDto);
	}

	/*
	@Transactional(readOnly = true)
	private File executeGenericPreview(String tag, Object object, ReportDTO reportDto) throws IOException, DocumentException, FileNotFoundException {

		if (object == null) {
			throw new DocumentException("Object Not Found");
		}

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable(tag, object);

		String htmlRT = creaTemplatePreview(reportDto, contextRT);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), "");
		InputStream input = result.getInputStream();

		File inputFile = File.createTempFile(tag, ".pdf");
		FileUtils.copyInputStreamToFile(input, inputFile);
		IOUtils.closeQuietly(input);
		return inputFile;
	}
	*/
	
	private String creaTemplatePreview(ReportDTO reportDto, Context contextRT) throws IOException, DocumentException, FileNotFoundException {

		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(reportDto.getIdModelloHtml());
		List<ReportRuntime> ids = new ArrayList<ReportRuntime>();
		String htmlTemplate = 
				reportDto.getIsDoc()!=null&&reportDto.getIsDoc().booleanValue()?modelloHtml.getHtml():			
				"<html><head><style>html,body{padding:0;margin:0}body{font:11pt Calibri,Sans-Serif;line-height:1.3;background:#fff!important;color:#000}</style></head><body>"+modelloHtml.getHtml()+"</body></html>";
		ReportRuntime reportRuntime = new ReportRuntime();
		reportRuntime.setHtml(htmlTemplate);

		reportRuntime = reportRuntimeRepository.save(reportRuntime);

		ids.add(reportRuntime);
		String keyRT = "reportId:" + String.valueOf(reportRuntime.getId());
		String htmlRT = templateEngine.process(keyRT, contextRT);

		reportRuntime = new ReportRuntime();
		reportRuntime.setHtml(htmlRT);

		reportRuntime = reportRuntimeRepository.save(reportRuntime);

		ids.add(reportRuntime);
		keyRT = "reportId:" + String.valueOf(reportRuntime.getId());
		htmlRT = templateEngine.process(keyRT, contextRT);
		
		reportRuntimeRepository.delete(ids);

		if (reportDto!=null && reportDto.getOmissis()!=null && reportDto.getOmissis().booleanValue()) {
			htmlRT = htmlRT.replaceAll("(?s)<omissis.*?omissis>", "<span style='font-weight: bold;'>...omissis...</span>");
		}
		htmlRT = htmlRT.replace("[[${formato}]]", modelloHtml.getPageOrientation()!=null && modelloHtml.getPageOrientation() ? FORMATO_A4_LANDSCAPE : FORMATO_A4_PORTRAIT);
		return htmlRT;
	}

	
	private String creaTemplatePreview(String modelloHtml, Context contextRT, ModelloHtml modelloHtmlObj) {
		List<ReportRuntime> ids = new ArrayList<ReportRuntime>();
		ReportRuntime reportRuntime = new ReportRuntime();
		reportRuntime.setHtml(modelloHtml);

		reportRuntime = reportRuntimeRepository.save(reportRuntime);

		ids.add(reportRuntime);
		String keyRT = "reportId:" + String.valueOf(reportRuntime.getId());
		String htmlRT = templateEngine.process(keyRT, contextRT);

		reportRuntime = new ReportRuntime();
		reportRuntime.setHtml(htmlRT);

		reportRuntime = reportRuntimeRepository.save(reportRuntime);

		ids.add(reportRuntime);
		keyRT = "reportId:" + String.valueOf(reportRuntime.getId());
		htmlRT = templateEngine.process(keyRT, contextRT);

		reportRuntimeRepository.delete(ids);
		htmlRT = htmlRT.replace("[[${formato}]]", modelloHtmlObj.getPageOrientation()!=null && modelloHtmlObj.getPageOrientation() ? FORMATO_A4_LANDSCAPE : FORMATO_A4_PORTRAIT);
		return htmlRT;
	}

	/*
	 * TODO: In ATTICO non previsti
	 *
	@Transactional(readOnly = true)
	private File executePreviewParere(String tag, Parere parere, ReportDTO reportDto, Atto atto) throws IOException, DocumentException, FileNotFoundException, DmsException {

		if (parere == null) {
			throw new DocumentException("Object Not Found");
		}

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable(tag, parere);
		String htmlRT = creaTemplatePreview(reportDto, contextRT);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();
		
		String banner = parere.getAoo().getAooPadre() != null ? parere.getAoo().getAooPadre().getLogo() : parere.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		if (parere.getAoo() != null) {
			parere.setAoo(aooService.findOne(parere.getAoo().getId()));
		} 
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, parere.getAoo() , null, urlSitoEnte, false, false, false);

		IOUtils.closeQuietly(input);
		return aggiungiAllegatiParere(filefiligrana, parere.getId().longValue());
	}
	
	@Transactional(readOnly = true)
	private File executePreviewParere(String tag, Parere parere, ReportDTO reportDto, Atto atto, SedutaGiunta seduta) throws IOException, DocumentException, FileNotFoundException, DmsException {

		if (parere == null) {
			throw new DocumentException("Object Not Found");
		}

		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable(tag, parere);
		if(seduta!=null){
			contextRT.setVariable("seduta", seduta);
		}
		String htmlRT = creaTemplatePreview(reportDto, contextRT);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT);
		InputStream input = result.getInputStream();
		
		String banner = parere.getAoo().getAooPadre() != null ? parere.getAoo().getAooPadre().getLogo() : parere.getAoo().getLogo();
		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		if (parere.getAoo() != null) {
			parere.setAoo(aooService.findOne(parere.getAoo().getId()));
		} 
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, parere.getAoo() , null, urlSitoEnte, false, false, false);

		IOUtils.closeQuietly(input);
		return aggiungiAllegatiParere(filefiligrana, parere.getId().longValue());
	}
	*/
	
	@Transactional
	public File executeReportIter(final Long modelloId, final Map<Long, List<Evento>> eventiMap, final List<CategoriaEvento> categorieEvento, final List<AvanzamentoDTO> avanzamenti, Atto atto) throws IOException, DocumentException, FileNotFoundException {
		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("eventiMap", eventiMap);
		contextRT.setVariable("categorieEvento", categorieEvento);
		contextRT.setVariable("avanzamenti", avanzamenti);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		contextRT.setVariable("data", dateFormat.format(atto.getCreatedDate().toDate()));
		contextRT.setVariable("ora", timeFormat.format(atto.getCreatedDate().toDate()));
		contextRT.setVariable("atto", atto);
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(modelloId);
		String htmlRT = creaTemplatePreview(modelloHtml.getHtml(), contextRT, modelloHtml);
		ByteArrayResource result = invokeHtmlToPDF(htmlRT, modelloId, this.getTitoloDocumento(modelloId, atto.getCodiceCifra()), atto);
		InputStream input = result.getInputStream();

		byte[] b = serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner());
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		File filefiligrana = cartaStampataService.addFiligrana(input, inputBanner, inputLogo, null, null, urlSitoEnte, false, true, false);
		File resultFile = File.createTempFile("report_iter_", ".pdf");
		
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		
		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);
			Integer pagina = i;
			
			Document pdfDoc = new PdfDocument();
			pdfDoc.setPageSize(pageSize);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_LEFT, new Phrase(String.format("Pag. %s di %s", pagina, total), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED, 8f)),
					pageSize.getLeft() + pdfDoc.leftMargin(), pageSize.getBottom() + pdfDoc.bottomMargin(), 0);
			if(atto!=null) {
				String codiceAtto = "";
				if(atto.getNumeroAdozione()!=null && atto.getDataAdozione()!=null) {
					codiceAtto = atto.getTipoAtto().getCodice() + "/" + atto.getDataAdozione().getYear() + "/" + atto.getNumeroAdozione();
				}else {
					codiceAtto = atto.getCodiceCifra();
				}
				ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", codiceAtto ), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED, 8f)),
						pageSize.getRight() - pdfDoc.rightMargin(), pageSize.getBottom() + pdfDoc.bottomMargin(), 0);
			}
		}
		pdfStamper.close();
		pdfReader.close();

		return resultFile;
	}
	
	@Transactional
	public File executeReportRicerca(final Long modelloId, final Set<String> colNamesSorted, final List<Atto> atti, Map<String, String> user, List<Aoo> aoos, Map<String, String> userCriteria, CriteriReportPdfRicercaDTO fixedCriteria) throws IOException, DocumentException, FileNotFoundException {
		Properties prop = new Properties();
		ClassPathResource res = new ClassPathResource("xls.properties");
		prop.load(res.getInputStream());
		
		List<List<String>> obj = new ArrayList<List<String>>();
		List<String> cols = new ArrayList<String>();
		for(String c : colNamesSorted){
			if(c.contains("-") && c.split("-").length > 1){
				cols.add(c.split("-")[1]);
			}
		}
		List<Map<String, String>> listMapAtti = utilityService.listMapAtti(atti, cols);
		
		List<String> intestazione = new ArrayList<String>();
		
		for(String col : cols){
			intestazione.add(prop.getProperty(col));
		}
		obj.add(intestazione);
		
		for(Map<String, String> mapAtto : listMapAtti){
			List<String> row = new ArrayList<String>();
			for(String col : cols){
				row.add(mapAtto.get(col) != null ? mapAtto.get(col) : "");
			}
			obj.add(row);
		}
		
		Context contextRT = new Context(Locale.forLanguageTag(IT));
		contextRT.setVariable("list", obj);
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat timeFormat = new SimpleDateFormat("HH:mm");
		contextRT.setVariable("fixedCriteri", fixedCriteria);
		contextRT.setVariable("userCriteri", userCriteria);
		contextRT.setVariable("aoos", Lists.reverse(aoos));
		contextRT.setVariable("data", dateFormat.format(new Date()));
		contextRT.setVariable("ora", timeFormat.format(new Date()));
		contextRT.setVariable("user", user);
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(modelloId);
		String htmlRT = creaTemplatePreview(modelloHtml.getHtml(), contextRT, modelloHtml);

		ByteArrayResource result = invokeHtmlToPDF(htmlRT, modelloId, this.getTitoloDocumento(modelloId, ""), null);
		InputStream input = result.getInputStream();

		byte[] b = serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner());
		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		return cartaStampataService.addFiligrana(input, inputBanner, inputLogo, null, null, urlSitoEnte, false, true, false);
	}
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	private File aggiungiAllegatiParere(File filefiligrana, long idParere) throws IOException, DocumentException, DmsException {
		List<File> listFile = new ArrayList<File>();
		if (idParere > 0L) {
			Parere addoDoc = parereService.findOne(idParere);
			for (DocumentoInformatico docInfo : addoDoc.getAllegati()) {

				it.linksmt.assatti.datalayer.domain.File fileD = docInfo.getFile();
				
//				byte[] b = fileD.getContenuto();
				byte[] b = dmsService.getContent(fileD.getCmisObjectId());

				File tmp = null;
				String tmpExt = FilenameUtils.getExtension(fileD.getNomeFile());

				tmp = File.createTempFile("tmp_", tmpExt);
				FileUtils.writeByteArrayToFile(tmp, b);

				InputStream inputAllegato = null;
				if ("pdf".equalsIgnoreCase(tmpExt)) {
					inputAllegato = new FileInputStream(tmp);
				}
				else {
					ByteArrayResource result = invokeConvertToXXX(tmp, CONVERT_TO_PDF);
					inputAllegato = result.getInputStream();
				}

				File tmpPDF = File.createTempFile("tmp_", ".pdf");
				FileUtils.copyInputStreamToFile(inputAllegato, tmpPDF);

				listFile.add(tmpPDF);

				tmp.delete();
			}
		}
		File resultFile = File.createTempFile("parere", ".pdf");

		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		Integer numeroDiPagineTotaleAllegati = 0;
		for (File file : listFile) {
			PdfReader reader = new PdfReader(new FileInputStream(file));

			for (int npage = 1; npage <= reader.getNumberOfPages(); npage++) {
				numeroDiPagineTotaleAllegati++;
				int pageNumber = pdfReader.getNumberOfPages() + 1;
				pdfStamper.insertPage(pageNumber, reader.getPageSize(npage));
				pdfStamper.replacePage(reader, npage, pageNumber);
			}
		}

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);
			Integer pagina = i;
			if (i > (total - numeroDiPagineTotaleAllegati)) {
				pagina = i - (total - numeroDiPagineTotaleAllegati);
			}
			ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", pagina), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED)),
					pageSize.getWidth() - 15, 15, 0);

		}

		pdfStamper.close();
		pdfReader.close();

		return resultFile;
	}
	*/

	@Transactional(readOnly = true)
	private File executePreviewAtto(String htmlRT, Atto atto, ReportDTO reportDto, 
			boolean nascondiUrSitoESpostaDatiAoo, boolean soloFrontespizio, boolean soloLogoCentrale) 
					throws IOException, DocumentException, DmsException {
		
		/*
		 * TODO: In ATTICO non previsto
		 * 
		, boolean aggiungiReferto
		*/
		InputStream inputLogo = null;
		String banner = null;
		if(atto.getAoo() !=null) {
			banner = atto.getAoo().getAooPadre() != null ? atto.getAoo().getAooPadre().getLogo() : atto.getAoo().getLogo();
		}

		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);

		InputStream inputBanner = new ByteArrayInputStream(b);
		
		if(soloLogoCentrale){
			inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		}
		else{
			inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		}
		
		String titoloSuffix = atto.getCodiceCifra();
		if(atto.getDataAdozione()!=null && atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty()) {
			titoloSuffix = atto.getTipoAtto().getCodice() + "/" + atto.getDataAdozione().toString("yyyy") + "/" + atto.getNumeroAdozione();
		}
		ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), titoloSuffix), atto);
		InputStream input = result.getInputStream();

		Aoo aoo = null;
		if(atto.getAoo()!=null && atto.getAoo().getId()!=null) {
			aoo = aooService.findOne(atto.getAoo().getId());
		}
		

		File filefiligrana = Lists.newArrayList("DC", "DPC", "DIC", "DIG","DG").contains(atto.getTipoAtto().getCodice())?
				cartaStampataService.addFiligranaDeliberaConsiglio(input, inputBanner, inputLogo, aoo, atto.getUfficio(), urlSitoEnte, nascondiUrSitoESpostaDatiAoo, soloFrontespizio, soloLogoCentrale):
					cartaStampataService.addFiligrana(input, inputBanner, inputLogo, aoo, atto.getUfficio(), urlSitoEnte, nascondiUrSitoESpostaDatiAoo, soloFrontespizio, soloLogoCentrale);

		File fileReferto = null;
		
		/*
		 * TODO: In ATTICO non previsto
		 * 
		if(aggiungiReferto){
			ReportDTO reportDTOReferto = new ReportDTO();
			reportDTOReferto.setIdAtto(atto.getId());
			ModelloHtml modelloHtml = modelloHtmlService.findByTipoDocumento(TipoDocumentoEnum.referto_tecnico.name()).get(0);
			reportDTOReferto.setIdModelloHtml(modelloHtml.getId());
			reportDTOReferto.setTipoDoc(TipoDocumentoEnum.referto_tecnico.name());
			fileReferto = executePreviewRefertoTecnico(atto, reportDTOReferto);
			IOUtils.closeQuietly(input);
			File filefiligranaSenzaAllegati = aggiungiReferto(filefiligrana, fileReferto);
			return aggiungiAllegati(filefiligranaSenzaAllegati, atto.getId().longValue(), reportDto.getOmissis());
			
		}
		*/
		
		IOUtils.closeQuietly(input);
		return aggiungiAllegati(filefiligrana, atto, reportDto.getOmissis());
	}
	
	private File executePreviewComunicazioneSeduta(String htmlRT, SedutaGiunta seduta, Profilo profilo, ReportDTO reportDto) throws IOException, DocumentException, DmsException {
		log.info("START executePreviewComunicazioneSeduta");
		String banner = profilo.getAoo().getAooPadre() != null ? profilo.getAoo().getAooPadre().getLogo() : profilo.getAoo().getLogo();

		byte[] b = banner == null ? serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringDefaultBanner()) : serviceUtil.getByteDecodeBase64DataImg(banner);

		InputStream inputBanner = new ByteArrayInputStream(b);
		InputStream inputLogo = new ByteArrayInputStream(serviceUtil.getByteDecodeBase64DataImg(serviceUtil.getStringLogoEnteIntestazioneConDicitura()));
		log.info("executePreviewComunicazioneSeduta-invokeHtmlToPDF");
		ByteArrayResource result = invokeHtmlToPDF(htmlRT, reportDto.getIdModelloHtml(), this.getTitoloDocumento(reportDto.getIdModelloHtml(), "Seduta n. " + seduta.getNumero()), null);
		log.info("executePreviewComunicazioneSeduta-invokeHtmlToPDF ends");
		InputStream input = result.getInputStream();
		log.info("executePreviewComunicazioneSeduta-addFiligrana");
		File filefiligrana = cartaStampataService.addFiligrana(
				input, inputBanner, inputLogo, 
				profilo.getAoo(), null, urlSitoEnte, false, false, false);
		log.info("executePreviewComunicazioneSeduta-addFiligrana ends");
		IOUtils.closeQuietly(input);
		return aggiungiAllegati(filefiligrana, null, reportDto.getOmissis());
	}

	private File aggiungiAllegati(File filefiligrana, Atto atto, Boolean omissis) throws IOException, DocumentException, DmsException {
		log.info("aggiungiAllegati starts");
		List<File> listFile = new ArrayList<File>();
		// todo da iterare
		if (atto != null) {
			// Atto addoDoc = attoService.findOne(idAtto);
			for (DocumentoInformatico docInfo : atto.getAllegati()) {
				if (docInfo.getParteIntegrante() != null && docInfo.getParteIntegrante() == true) {

					it.linksmt.assatti.datalayer.domain.File fileD = null;
					if (docInfo.getPubblicabile() == null || docInfo.getPubblicabile() == false) {
						if (omissis == null || omissis == false) {
							fileD = docInfo.getFile();
						}
						else {
							continue;
						}
					}
					else if (docInfo.getOmissis() != null && docInfo.getOmissis() == false) {
						fileD = docInfo.getFile();
					}
					else {
						if (docInfo.getOmissis() != null && docInfo.getOmissis() == true) {
							if (omissis == null || omissis == false) {
								fileD = docInfo.getFile();
							}
							else if (docInfo.getFileomissis() != null) {
								fileD = docInfo.getFileomissis();
							}
							else {
								continue;
							}
						}
						else {
							continue;
						}
					}
//					byte[] b = fileD.getContenuto();
					byte[] b = dmsService.getContent(fileD.getCmisObjectId());

					File tmp = null;
					String tmpExt = FilenameUtils.getExtension(fileD.getNomeFile());

					tmp = File.createTempFile("tmp_", tmpExt);
					FileUtils.writeByteArrayToFile(tmp, b);

					InputStream inputAllegato = null;
					if ("pdf".equalsIgnoreCase(tmpExt)) {
						inputAllegato = new FileInputStream(tmp);
					}
					else {
						ByteArrayResource result = invokeConvertToXXX(tmp, CONVERT_TO_PDF, null, null);
						inputAllegato = result.getInputStream();
					}

					File tmpPDF = File.createTempFile("tmp_", ".pdf");
					FileUtils.copyInputStreamToFile(inputAllegato, tmpPDF);

					listFile.add(tmpPDF);

					tmp.delete();
				}
			}
		}
		File resultFile = File.createTempFile("result_", ".pdf");

		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		Integer numeroDiPagineTotaleAllegati = 0;
		for (File file : listFile) {
			PdfReader reader = new PdfReader(new FileInputStream(file));

			for (int npage = 1; npage <= reader.getNumberOfPages(); npage++) {
				numeroDiPagineTotaleAllegati++;
				int pageNumber = pdfReader.getNumberOfPages() + 1;
				pdfStamper.insertPage(pageNumber, reader.getPageSize(npage));
				pdfStamper.replacePage(reader, npage, pageNumber);
			}

			// reader.close();
		}

		int total = pdfReader.getNumberOfPages();
		PdfContentByte pagecontent;

		for (int i = 0; i < total;) {
			pagecontent = pdfStamper.getOverContent(++i);
			Rectangle pageSize = pdfReader.getPageSize(i);
			Integer pagina = i;
			if (i > (total - numeroDiPagineTotaleAllegati)) {
				pagina = i - (total - numeroDiPagineTotaleAllegati);
			}
			
			Document pdfDoc = new PdfDocument();
			pdfDoc.setPageSize(pageSize);

			ColumnText.showTextAligned(pagecontent, Element.ALIGN_LEFT, new Phrase(String.format("Pag. %s di %s", pagina, total), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED, 8f)),
					pageSize.getLeft() + pdfDoc.leftMargin(), pageSize.getBottom() + pdfDoc.bottomMargin(), 0);
			if(atto!=null) {
				String codiceAtto = "";
				if(atto.getNumeroAdozione()!=null && atto.getDataAdozione()!=null) {
					
					
					if(Lists.newArrayList("DG", "DC", "DPC", "DIG", "DIC").contains(atto.getTipoAtto().getCodice())) {
						codiceAtto = atto.getTipoAtto().getCodice() + "/" + atto.getDataAdozione().getYear() + "/" + atto.getNumeroAdozione() + " - " + atto.getCodiceCifra().substring(atto.getCodiceCifra().length() - 10);
					}else {
						codiceAtto = atto.getTipoAtto().getCodice() + "/" + atto.getDataAdozione().getYear() + "/" + atto.getNumeroAdozione();
					}
				}else {
					codiceAtto = atto.getCodiceCifra();
				}
				ColumnText.showTextAligned(pagecontent, Element.ALIGN_RIGHT, new Phrase(String.format("%s", codiceAtto ), FontFactory.getFont(FONT, BaseFont.WINANSI, BaseFont.EMBEDDED, 8f)),
						pageSize.getRight() - pdfDoc.rightMargin(), pageSize.getBottom() + pdfDoc.bottomMargin(), 0);
			}

		}

		pdfStamper.close();
		pdfReader.close();
		log.info("aggiungiAllegati ends");
		return resultFile;
		
	}
	
	/*
	 * TODO: In ATTICO non previsti
	 *
	private File aggiungiReferto(File filefiligrana, File fileReferto) throws IOException, DocumentException {
		File resultFile = File.createTempFile("withReferto_", ".pdf");
		PdfReader pdfReader = new PdfReader(new FileInputStream(filefiligrana));
		PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(resultFile));
		Integer numeroDiPagineTotaleAllegati = 0;
		
	
		PdfReader reader = new PdfReader(new FileInputStream(fileReferto));

		for (int npage = 1; npage <= reader.getNumberOfPages(); npage++) {
			numeroDiPagineTotaleAllegati++;
			int pageNumber = pdfReader.getNumberOfPages() + 1;
			pdfStamper.insertPage(pageNumber, reader.getPageSize(npage));
			pdfStamper.replacePage(reader, npage, pageNumber);
		}

		pdfStamper.close();
		pdfReader.close();
		return resultFile;
	}
	*/
		
	public ByteArrayResource invokeHtmlToPDF(String html, Long idModelloHtml, String titolo, Atto atto) throws IOException {

		html = html.replaceAll("(?s)<!--.*?-->", "");
		
		boolean newVersion = false;
		String enableNewConvertMode = WebApplicationProps.getProperty("enableNewConvertMode");
		if(enableNewConvertMode!=null && !enableNewConvertMode.trim().isEmpty() && enableNewConvertMode.trim().equalsIgnoreCase("full")) {
			newVersion = true;
		}else if(enableNewConvertMode!=null && !enableNewConvertMode.trim().isEmpty() && enableNewConvertMode.trim().equalsIgnoreCase("true")) {
			List<Object> fromDateList = WebApplicationProps.getPropertyList("enableNewConvertModeFromDateList");
			
			String codiceAooPredisposizioneAtto = atto !=null && atto.getAoo()!=null && atto.getAoo().getCodice()!= null ? atto.getAoo().getCodice() : null;
			Long timeAttoCreation = atto!=null && atto.getCreatedDate()!=null ? atto.getCreatedDate().getMillis() : null;
			
			List<Object> enableNewConvertModeAooList = WebApplicationProps.getPropertyList("enableNewConvertModeAooList");
			for(int i = 0; i < enableNewConvertModeAooList.size(); i++) {
				List<String> aoos = Lists.newArrayList(enableNewConvertModeAooList.get(i).toString().split("-"));
				if(aoos.contains(codiceAooPredisposizioneAtto)) {
					LocalDateTime dt = LocalDateTime.parse(fromDateList.get(i).toString(), new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd HH:mm").toFormatter());
					Long timeFrom = dt.toDateTime().getMillis();
					if(timeAttoCreation.longValue() > timeFrom.longValue()) {
						newVersion = true;
					}
					break;
				}
			}
		}
		
		if(!newVersion) {
			File tmp = null;
			tmp = File.createTempFile("tmp_", ".html");
			FileUtils.writeByteArrayToFile(tmp, html.getBytes());
			return invokeConvertToXXX(tmp, CONVERT_TO_PDF, idModelloHtml, titolo);
		}else {
			return invokeConvertToPdf(html, idModelloHtml, WebApplicationProps.getProperty("convert-pdf-endpoint-v2"), titolo);
		}
	}
	
	public ByteArrayResource invokeHtmlToDocx(String html, Long idModelloHtml) throws IOException {
		ByteArrayResource risultato = null;
		//log.info(html);
		//log.info("*******************unescapeHtml**********************");
		html = html.replaceAll("(?s)<!--.*?-->", "");
		html = html.replaceAll("(?s)<style.*?style>", "");
		html = html.replaceAll("&nbsp;"," ");
		html = html.replaceAll("&ensp;"," ");
		html = html.replaceAll("&thinsp;"," ");
		html = html.replaceAll("&emsp;","  ");
		//html = Normalizer.normalize(html, Normalizer.Form.NFKD);
//		html = StringEscapeUtils.unescapeHtml(html);
		//log.info(html);
		//usando l'unescape html non va in errore la costruzione del link alla trasparenza: per questo motivo ci sono i replce degli spazi nell righe precendenti
//		//html = StringEscapeUtils.unescapeHtml(html);
//		log.info("*******************unescapeHtml**********************");
//		log.info(html);
		String endpointConverter = WebApplicationProps.getProperty(ConfigPropNames.CONVERT_DOCX);
		if(idModelloHtml != null) {
			ModelloHtml modelloHtml =  modelloHtmlRepository.findOne(idModelloHtml);
			if(modelloHtml != null && modelloHtml.getPageOrientation()) {
				endpointConverter = WebApplicationProps.getProperty(ConfigPropNames.CONVERT_DOCX_LANDSCAPE);
			}
		}
		
    	try {
		    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(new StringHttpMessageConverter());
			messageConverters.add(new ResourceHttpMessageConverter());
			messageConverters.add(new ByteArrayHttpMessageConverter());

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setMessageConverters(messageConverters);
			
			HttpHeaders headers = new HttpHeaders();
			//headers.setContentType(MediaType.TEXT_HTML);

			
			MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			
			HttpEntity<String> request = new HttpEntity<String>(html, headers);

			log.info("EndpointConverter:"+endpointConverter);
		    ResponseEntity<ByteArrayResource> result = restTemplate.exchange(endpointConverter, HttpMethod.POST, request, ByteArrayResource.class);
		    
		    risultato = result.getBody();
		}catch(Exception e) {
			e.printStackTrace();
		}
    	return risultato;
	}
	
	private ByteArrayResource invokeConvertToXXX(File tmp, String convertType, Long idModelloHtml, String titolo) throws IOException {
		
		
		String convertPdf = WebApplicationProps.getProperty(ConfigPropNames.CONVERT_PDF);
		
		//Cambia l'orientamento della pagina in base al valore salvato nel templete se presente
		if(idModelloHtml != null) {
			ModelloHtml modelloHtml =  modelloHtmlRepository.findOne(idModelloHtml);
			if(modelloHtml != null && modelloHtml.getPageOrientation()) {
				convertPdf = new String(WebApplicationProps.getProperty(ConfigPropNames.CONVERT_PDF_LANDSCAPE));
			}
		}
		
		List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
		messageConverters.add(new FormHttpMessageConverter());
		messageConverters.add(new ResourceHttpMessageConverter());
		messageConverters.add(new ByteArrayHttpMessageConverter());

		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setMessageConverters(messageConverters);

		MultiValueMap<String, Object> multipartMap = new LinkedMultiValueMap<>();
		multipartMap.add("file", new FileSystemResource(tmp));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);

		HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<MultiValueMap<String, Object>>(multipartMap, headers);

		log.debug("convertPdf:" + convertPdf);
		ResponseEntity<ByteArrayResource> result = restTemplate.exchange(convertPdf, HttpMethod.POST, request, ByteArrayResource.class);
		
		ByteArrayResource ret = result.getBody();
		
		boolean enablePdfBookmarksAndMetadata = false;
		try {
			enablePdfBookmarksAndMetadata = Boolean.parseBoolean(WebApplicationProps.getProperty("enablePdfBookmarksAndMetadata", "false"));
		}catch(Exception e) {
			enablePdfBookmarksAndMetadata = false;
		}
		if(!enablePdfBookmarksAndMetadata) {
			return ret;
		}else {
			ret = this.addBookmarskAndMetadata(result.getBody().getByteArray(), titolo);
		}

		return ret;
	}
	
	private ByteArrayResource addBookmarskAndMetadata(byte[] originalPdf, String titolo) {
		ByteArrayResource ret = null;
		ByteArrayOutputStream baos = null;
		try {
			String descrizione = titolo != null && !titolo.isEmpty() ? titolo : "Documento";
			
			PDDocument doc = PDDocument.load(new ByteArrayInputStream(originalPdf));
			
			 //adding info
			{
				PDDocumentInformation info = new PDDocumentInformation();
			    info.setTitle(descrizione);
			    doc.setDocumentInformation(info);
			}
			
			//adding metadata
			{
				XMPMetadataPDFA xmpPdfa = new XMPMetadataPDFA();
				
				XMPSchemaDublinCore dc = xmpPdfa.getDublinCoreSchema();
				if(dc==null) {
					dc = xmpPdfa.addDublinCoreSchema();
				}
				dc.setTitle(descrizione);
				
				XMPSchemaPDFAId pdfaId = xmpPdfa.getPDFAIdSchema();
				if(pdfaId==null) {
					pdfaId = xmpPdfa.addPDFAIdSchema();
					pdfaId.setConformance("A");
					pdfaId.setPart(1);
					pdfaId.setAbout("");
				}
				
				PDMetadata metadata = new PDMetadata(doc);
				metadata.importXMPMetadata(xmpPdfa.asByteArray());
				
				doc.getDocumentCatalog().setMetadata(metadata);
				doc.getDocumentCatalog().setLanguage("it-IT");
			}
			
			//adding bookmark
			{
				PDDocumentOutline outline = new PDDocumentOutline();
			    PDPage firstPage = (PDPage)doc.getDocumentCatalog().getAllPages().get(0);
			    PDOutlineItem firstPageItem = new PDOutlineItem();
			    firstPageItem.setTitle(descrizione);
			    firstPageItem.setDestination(firstPage);
			    outline.appendChild(firstPageItem);
			    doc.getDocumentCatalog().setDocumentOutline( outline );
			}
			
		    //saving
			baos = new ByteArrayOutputStream();
			doc.save(baos);
			doc.close();
			
			ret = new ByteArrayResource(baos.toByteArray());
		}catch(Exception e) {
			e.printStackTrace();
		}finally {
			try {
				IOUtils.closeQuietly(baos);
			}catch(Exception e) {
			}
		}
		return ret;
	}
	
	private ByteArrayResource invokeConvertToPdf(String html, Long idModelloHtml, String endpointConverter, String titolo) throws IOException {
		ByteArrayResource risultato = null;
		boolean landScape = false;
		if(idModelloHtml != null) {
			ModelloHtml modelloHtml =  modelloHtmlRepository.findOne(idModelloHtml);
			if(modelloHtml != null && modelloHtml.getPageOrientation()) {
				landScape = true;
			}
		}
		
		try {
		    List<HttpMessageConverter<?>> messageConverters = new ArrayList<HttpMessageConverter<?>>();
			messageConverters.add(new StringHttpMessageConverter());
			messageConverters.add(new ResourceHttpMessageConverter());
			messageConverters.add(new ByteArrayHttpMessageConverter());
	
			RestTemplate restTemplate = new RestTemplate();
			restTemplate.setMessageConverters(messageConverters);
			
			HttpHeaders headers = new HttpHeaders();
			
			MediaType mediaType = new MediaType("application", "json", StandardCharsets.UTF_8);
			headers.setContentType(mediaType);
			
			HttpEntity<String> request = new HttpEntity<String>(html, headers);
	
		    ResponseEntity<ByteArrayResource> result = restTemplate.exchange(endpointConverter + "?landscape="+landScape, HttpMethod.POST, request, ByteArrayResource.class);
		    
		    risultato = result.getBody();
		    
		    boolean enablePdfBookmarksAndMetadata = false;
			try {
				enablePdfBookmarksAndMetadata = Boolean.parseBoolean(WebApplicationProps.getProperty("enablePdfBookmarksAndMetadata", "false"));
			}catch(Exception e) {
				enablePdfBookmarksAndMetadata = false;
			}
			if(!enablePdfBookmarksAndMetadata) {
				return risultato;
			}else {
				risultato = this.addBookmarskAndMetadata(result.getBody().getByteArray(), titolo);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		return risultato;
	}


	/*
	 * TODO: In ATTICO non previsti
	 *
	protected Resoconto getResoconto(SedutaGiunta seduta, Integer tipo, boolean res) {
		Resoconto resoconto = null;
		if (res == true) {
			resoconto = resocontoRepository.findBySedutagiuntaIdAndTipo(seduta.getId(), tipo);
		}
		else {
			resoconto = resocontoRepository.findBySedutagiuntaIdAndTipoNull(seduta.getId());
		}

		if (resoconto == null) {
			resoconto = new Resoconto();
			if (res == true) {
				resoconto.setTipo(tipo);
			}
			else {
				resoconto.setTipo(2);// TODO demo in assenza del modello
			}

			resoconto.setSedutaGiunta(seduta);
		}

		return resoconto;
	}
	*/
	
	private HashMap<Long, List<ConfigurazioneIncaricoProfiloDto>> getRelatoriAtto(OrdineGiorno odgBase) {
		//map Atto - ConfigurazioneIncaricoProfiloDto
		HashMap<Long,List<ConfigurazioneIncaricoProfiloDto>> listaRelatoriAtto = new HashMap<>();
		for(AttiOdg attoOdg :odgBase.getAttos()) {
			try {
				if(attoOdg.getAtto().getId()!=null && attoOdg.getAtto().getId()>0L) {
		    		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(attoOdg.getAtto().getId());
		    		for(ConfigurazioneIncaricoDto confIncarico: listConfigurazioneIncarico) {
		    			if(confIncarico.getConfigurazioneTaskCodice().equals(TipiParereEnum.PARERE_ASSESORE.getCodice()) ||
	    				   confIncarico.getConfigurazioneTaskCodice().equals(TipiParereEnum.PARERE_CONSIGLIERE.getCodice())) {
		    				listaRelatoriAtto.put(attoOdg.getAtto().getId(), confIncarico.getListConfigurazioneIncaricoProfiloDto());
		    			}
		    		}
		    		
				}
			} catch (ServiceException e) {
				log.error("executePreviewOrdinegiornoBase - Errore durante il recupero dei proponenti",e.getMessage());
			}
			
		}
		return listaRelatoriAtto;
	}
	
	private HashMap<Long, Aoo> getDirezioneAtto(OrdineGiorno odgBase) {
		//map Atto - ConfigurazioneIncaricoProfiloDto
		HashMap<Long,Aoo> direzioneAtto = new HashMap<>();
		for(AttiOdg attoOdg :odgBase.getAttos()) {
			try {
				Aoo aooProponente = attoOdg.getAtto().getAoo();
				if(aooProponente!=null) {
					if(aooProponente.getTipoAoo()!=null && aooProponente.getTipoAoo().getCodice()!=null && aooProponente.getTipoAoo().getCodice().equalsIgnoreCase("DIREZIONE")) {
						direzioneAtto.put(attoOdg.getAtto().getId(), aooProponente);
					}else {
						Aoo direzione = aooService.getAooDirezione(aooProponente);
						if(direzione != null) {
							direzioneAtto.put(attoOdg.getAtto().getId(), direzione);
						}
					}
				}
			} catch (Exception e) {
				log.error("executePreviewOrdinegiornoBase - Errore durante il recupero della aoo",e.getMessage());
			}
		}
		return direzioneAtto;
	}
	
	private DateTime formatTime(DateTime dataOra) {
		TimeZone tz = TimeZone.getTimeZone("Europe/Rome");
		TimeZone.setDefault(tz);
		String pattern ="yyyy-MM-dd HH:mm";
		String dt = DateFormatUtils.format(dataOra.getMillis(), pattern, tz);
		DateTimeFormatter formatter = DateTimeFormat.forPattern(pattern);
		DateTime primaConvocazioneInizio = formatter.parseDateTime(dt);
		return primaConvocazioneInizio;
	}
	
	private String getTitoloDocumento(Long modelloHtmlId, String suffix) {
		String titolo = "";
		
		ModelloHtml mod = modelloHtmlRepository.findOne(modelloHtmlId);
		if(mod!=null && mod.getTitolo()!=null && !mod.getTitolo().isEmpty()) {
			titolo = mod.getTitolo().substring(0, 1).toUpperCase() + mod.getTitolo().substring(1, mod.getTitolo().length()).toLowerCase();
		}
		if(suffix!=null && !suffix.isEmpty()) {
			if(!titolo.isEmpty()) {
				titolo += " ";
			}
			titolo += suffix;
		}
		return titolo;
	}
	
	private Locale getLocalFromISO(String iso4217code){
	    Locale toReturn = null;
	    for (Locale locale : NumberFormat.getAvailableLocales()) {
	        String code = NumberFormat.getCurrencyInstance(locale).
	                getCurrency().getCurrencyCode();
	        if (iso4217code.equals(code)) {
	            toReturn = locale;
	            break;
	        }
	    }
	    return toReturn;
	}
}