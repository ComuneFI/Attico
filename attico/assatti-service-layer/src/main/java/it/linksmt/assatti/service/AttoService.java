package it.linksmt.assatti.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.xml.datatype.DatatypeConfigurationException;

import org.apache.commons.codec.binary.Base64;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.itextpdf.text.DocumentException;
import com.mysema.query.types.expr.BooleanExpression;
import com.mysema.query.types.expr.DateExpression;
import com.mysema.query.types.expr.NumberExpression;
import com.mysema.query.types.expr.SimpleExpression;
import com.mysema.query.types.expr.StringExpression;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;
import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.CodiciAzioniUtente;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.ProfiloQualificaBean;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.dto.RelataAlboDto;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.AttoHasAmbitoMateria;
import it.linksmt.assatti.datalayer.domain.AttoHasDestinatario;
import it.linksmt.assatti.datalayer.domain.AttoSchedaDato;
import it.linksmt.assatti.datalayer.domain.AttoSchedaDatoId;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.Beneficiario;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.CriterioRicerca;
import it.linksmt.assatti.datalayer.domain.CriterioRicercaEnum;
import it.linksmt.assatti.datalayer.domain.DatiContabili;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.FaseRicercaEnum;
import it.linksmt.assatti.datalayer.domain.FaseRicercaHasCriterio;
import it.linksmt.assatti.datalayer.domain.Fattura;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.MotivoClonazioneEnum;
import it.linksmt.assatti.datalayer.domain.Nota;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAtto;
import it.linksmt.assatti.datalayer.domain.QAvanzamento;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.Scheda;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants.statiAtto;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreAtto;
import it.linksmt.assatti.datalayer.domain.StatoAttoEnum;
import it.linksmt.assatti.datalayer.domain.StatoConclusoEnum;
import it.linksmt.assatti.datalayer.domain.StatoProceduraPubblicazioneEnum;
import it.linksmt.assatti.datalayer.domain.StatoRelataEnum;
import it.linksmt.assatti.datalayer.domain.TipoAllegato;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoAttoHasFaseRicerca;
import it.linksmt.assatti.datalayer.domain.TipoDatoEnum;
import it.linksmt.assatti.datalayer.domain.TipoDeterminazione;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.domain.TipoRicercaCriterioEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.dto.AooBasicDto;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.DatiContabiliRepository;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.datalayer.repository.SchedaRepository;
import it.linksmt.assatti.datalayer.repository.TipoAllegatoRepository;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.fdr.config.FdrClientProps;
import it.linksmt.assatti.fdr.dto.FirmaRemotaRequestDTO;
import it.linksmt.assatti.fdr.dto.FirmaRemotaResponseDTO;
import it.linksmt.assatti.fdr.exception.FirmaRemotaException;
import it.linksmt.assatti.fdr.service.FdrWsUtil;
import it.linksmt.assatti.fdr.service.FirmaRemotaService;
import it.linksmt.assatti.service.converter.AttoSearchConverter;
import it.linksmt.assatti.service.converter.DmsMetadataConverter;
import it.linksmt.assatti.service.dto.AllegatoSearchDTO;
import it.linksmt.assatti.service.dto.AooGroupDto;
import it.linksmt.assatti.service.dto.AttoCriteriaDTO;
import it.linksmt.assatti.service.dto.AttoSearchDTO;
import it.linksmt.assatti.service.dto.AttoWorkflowDTO;
import it.linksmt.assatti.service.dto.CondizioneRicercaLiberaDTO;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;
import it.linksmt.assatti.service.dto.FirmaRemotaDTO;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.RicercaLiberaDTO;
import it.linksmt.assatti.service.dto.SchedaValoriDlg33DTO;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;
import it.linksmt.assatti.service.exception.GeneraFirmaDocumentoException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.FileChecksum;
import it.linksmt.assatti.utility.RoleCodes;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.SectionsVisibilityProps;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * Service class for managing atti.
 */
@Service
@Transactional
public class AttoService {
	private final Logger log = LoggerFactory.getLogger(AttoService.class);

	private static DateFormat MYSQL_DATEFORMAT = new SimpleDateFormat("yyyy-MM-dd");
	
	@Inject
	private ConfigurazioneTaskService configurazioneTaskService;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private TipoIterService tipoIterService;
	
	@Inject
	private DatiContabiliService datiContabiliService;

	@Inject
	private DocumentoInformaticoService documentoInformaticoService;

	@Inject
	private RubricaDestinatarioEsternoService rubricaDestinatarioEsternoService;

	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private TipoAttoRepository tipoAttoRepository;
	
	@Inject
	private DatiContabiliRepository datiContabiliRepository;

	@Inject
	private UtenteService utenteService;

	@Inject
	private AooRepository aooRepository;

	@Inject
	private SchedaRepository schedaRepository;

	@Inject
	private AttoSchedaDatoService attoSchedaDatoService;

	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private ProfiloService profiloService;

	@Inject
	private EventoService eventoService;

	@Inject
	private SottoScrittoreAttoService sottoScrittoreAttoService;

	@Inject
	private DestinatarioInternoService destinatarioInternoService;

	@Inject
	private ReportService reportService;

	@Inject
	private DocumentoPdfService documentoPdfService;

	@Inject
	private ModelloHtmlService modelloHtmlService;

	@Inject
	private ServiceUtil serviceUtil;

	@Autowired
	private DmsMetadataConverter dmsMetadataConverter;
	
	@Autowired
	private DmsService dmsService;

	@Autowired
	private ConfigurazioneIncaricoService configurazioneIncaricoService;

	@Autowired
	private TipoAllegatoRepository tipoAllegatoRepository;

	@Inject
	private FileRepository fileRepository;
	
	@Inject
	private CodiceProgressivoService codiceProgressivoService;
	
	@Inject
	private JobPubblicazioneService jobPubblicazioneService;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Autowired
	private SezioneTipoAttoService sezioneTipoAttoService;

	// @Inject
	// private JobPubblicazioneRepository jobPubblicazioneRepository;

	// @Inject
	// private ProtocollazioneDAO wsProtocollazione;

	// @Inject
	// private WSPubblicazioneService wsPubblicazione;

	@Inject
	private AttoWorkerService attoWorkerService;

	@Inject
	private ParereService parereService;

	@Inject
	private WorkflowServiceWrapper workflowService;

	@Inject
	private TipoDocumentoService tipoDocumentoService;
	
	@Inject
	private ContabilitaServiceWrapper contabilitaServiceWrapper;
	
	@Inject
	private ContabilitaService contabiliaService;
	
	@Inject
	private AttiOdgRepository attiOdgRepository;
	
	@Inject
	private AvanzamentoService avanzamentoService;
	
	private final String ATTO = "atto";
	
	private final String DISALLINEAMENTO_DATI_JENTE= "disallineamento_dati_jente";
	
	private final String DOCS = "documenti_generati";
	
	@Transactional(readOnly = true)
	public Page<Atto> findListAttoItinereByIstruttore(Long profiloIdIstruttore, Pageable paginazione, Collection<Long> attoIdsExclusion){
		BooleanExpression p = QAtto.atto.iterStatus.eq("I").and(QAtto.atto.profilo.id.eq(profiloIdIstruttore));
		if(attoIdsExclusion!=null && attoIdsExclusion.size() > 0) {
			p = p.and(QAtto.atto.id.notIn(attoIdsExclusion));
		}
				
		Page<Atto> page = attoRepository.findAll(p, paginazione);
		return page;
	}
	
	@Transactional(readOnly = true)
	public Atto findListAttoItinereByIstruttore(Long profiloId, Long attoId){
		BooleanExpression p = QAtto.atto.iterStatus.eq("I").and(QAtto.atto.profilo.id.eq(profiloId)).and(QAtto.atto.id.eq(attoId));
		Atto atto = attoRepository.findOne(p);
		return atto;
	}
	
	@Transactional
	public DocumentoPdf getDocumentoPrincipale(Atto atto) {
		return this.getDocumentoPrincipale(atto, false, null);
	}
	
	@Transactional
	public DocumentoPdf getDocumentoPrincipale(Atto atto, boolean documentoOmissis) {
		return this.getDocumentoPrincipale(atto, false, documentoOmissis);
	}
	
	@Transactional(readOnly = true)
	public boolean hasAccessGrant(Long profiloId, long attoId, boolean fullAccess) {
		boolean permission = false;
		
		if(profiloId==null) {
			return permission;
		}else if (serviceUtil.hasRuolo(profiloId, RoleCodes.ROLE_SUPERVISORE_CONSULTAZIONE_ATTI)) {
			permission = true;
			return permission;
		}
		
		Atto atto = attoRepository.findOne(attoId);
		Profilo profilo = profiloService.findOne(profiloId);
		
		String[] statiConclusi = null;
		if(atto.getTipoAtto().getStatoConclusoRicerca()!=null) {
			statiConclusi = atto.getTipoAtto().getStatoConclusoRicerca().split("\\|");
		}else {
			statiConclusi = new String[] {};
		}
		List<String> statiConclusiList = new ArrayList<String>();
		boolean conclusoConEsecutivita = false;
		boolean conclusoConAttesaRelata = false;
		for(int i = 0; i < statiConclusi.length; i++) {
			if(statiConclusi[i].equalsIgnoreCase(StatoConclusoEnum.DATA_ESECUTIVITA.getCodice())) {
				conclusoConEsecutivita = true;
			}else if(statiConclusi[i].equalsIgnoreCase(StatoConclusoEnum.ATTESA_RELATA.getCodice())) {
				conclusoConAttesaRelata = true;
				statiConclusiList.add(statiConclusi[i]);
			}else {
				statiConclusiList.add(statiConclusi[i]);
			}
		}
		QAtto qAtto = QAtto.atto;
		BooleanExpression predicate = qAtto.id.eq(attoId);
		if(conclusoConAttesaRelata || conclusoConEsecutivita) {
			if(conclusoConAttesaRelata && conclusoConEsecutivita) {
				predicate = qAtto.inizioPubblicazioneAlbo.isNotNull().or(qAtto.dataEsecutivita.isNotNull()).or(qAtto.fineiterTipo.in(statiConclusiList));
			}else {
				if(conclusoConEsecutivita) {
					predicate = qAtto.dataEsecutivita.isNotNull().or(qAtto.fineiterTipo.in(statiConclusiList));
				}else if(conclusoConAttesaRelata) {
					predicate = qAtto.inizioPubblicazioneAlbo.isNotNull().or(qAtto.fineiterTipo.in(statiConclusiList));
				}
			}
		}else {
			predicate = qAtto.fineiterTipo.in(statiConclusiList);
		}
		
		boolean isAttoConcluso = attoRepository.count(QAtto.atto.id.eq(attoId).and(predicate)) > 0L;
		AttoCriteriaDTO criteria = new AttoCriteriaDTO();
		criteria.setViewtype(isAttoConcluso ? "conclusi" : "itinere");
		Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri = this.ottieniCriteri(atto.getTipoAtto(), !isAttoConcluso);
		for(TipoAttoHasFaseRicerca fase : mappaCriteri.keySet()) {
			if(fase.getFase() == null) {
				continue;
			}
			BooleanExpression p = this.buildCriteriRicercaConfigurabiliByFase(fase, qAtto, mappaCriteri, criteria, profilo, fullAccess);
			permission = attoRepository.count(QAtto.atto.id.eq(attoId).and(p)) > 0L;
			if(permission) {
				break;
			}
		}
		return permission;
	}
	
	/**
	 * If omissis is null omissis will be computed based on pubblicazioneIntegrale
	 * @param atto
	 * @param completo
	 * @param omissis
	 * @return
	 */
	@Transactional
	public DocumentoPdf getDocumentoPrincipale(Atto atto, boolean completo, Boolean omissis) {
		DocumentoPdf docEntity = null;
		if ((omissis!=null && omissis.equals(true)) || (omissis==null && (atto.getPubblicazioneIntegrale() == null || atto.getPubblicazioneIntegrale() == false))) {
			// Caricamento ultimo file firmato (il primo essendoci order by DESC)
			for (DocumentoPdf d : atto.getDocumentiPdfAdozioneOmissis()) {
				if((completo && d.getCompleto()!=null && d.getCompleto().equals(true)) || (!completo && (d.getCompleto()==null || d.getCompleto().equals(false)))) {
					docEntity = d;
					break;
				}
			}
		} else {
			// Caricamento ultimo file firmato (il primo essendoci order by DESC)
			for (DocumentoPdf d : atto.getDocumentiPdfAdozione()) {
				if((completo && d.getCompleto()!=null && d.getCompleto().equals(true)) || (!completo && (d.getCompleto()==null || d.getCompleto().equals(false)))) {
					docEntity = d;
					break;
				}
			}
		}
		return docEntity;
	}
	
	@Transactional
	public void effettuaRevocaAtto(Atto attoDiRevoca, TipoDeterminazione tipoCollegamento) throws ParseException {
		Atto revocato = this.findByCodiceCifra(attoDiRevoca.getCodicecifraAttoRevocato());
		
		String statoAttoCollegato = tipoCollegamento!=null && tipoCollegamento.getStatoTrasparenza()!=null?tipoCollegamento.getStatoTrasparenza():"modificato";
		revocato.setStato("Atto "+statoAttoCollegato);
		revocato = this.save(revocato);
		
		eventoService.saveEventoAttoCollegato(statoAttoCollegato, revocato);
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(revocato);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(revocato.getStato());
		String descrizioneAttivita = tipoCollegamento!=null && tipoCollegamento.getDescrizione()!=null?tipoCollegamento.getDescrizione():"modifica";
		avanzamento.setAttivita("Esecutività atto di "+descrizioneAttivita);
		avanzamento.setProfilo(null);

		avanzamento.setNote("Attenzione! Questo atto \u00E8 stato "+statoAttoCollegato+" con atto " + attoDiRevoca.getCodiceCifra() + " - " + attoDiRevoca.getNumeroAdozione() 
		+ " del " + attoDiRevoca.getDataEsecutivita().toString("dd/MM/yyyy") + " con oggetto \"" + attoDiRevoca.getOggetto() + "\"");
		avanzamento.setWarningType(tipoCollegamento.getCodice());
		avanzamentoService.salva(avanzamento);
		
		if(attoDiRevoca.getTipoDeterminazione() !=null && attoDiRevoca.getTipoDeterminazione().getDescrizione()!= null && !attoDiRevoca.getTipoDeterminazione().getDescrizione().trim().isEmpty()) {
			Avanzamento lastAvanzamento = avanzamentoService.findLastByAtto_id(attoDiRevoca.getId());
			lastAvanzamento.setNote(attoDiRevoca.getTipoDeterminazione().getDescrizione()+" l'atto "+revocato.getCodiceCifra()+ " - " + revocato.getNumeroAdozione() );
			avanzamentoService.salva(lastAvanzamento);
		}
		
	}
	
//	@Transactional
//	public void effettuaModificaIntegrazioneAtto(Atto attoDiRevoca, TipoDeterminazione tipoCollegamento) throws ParseException {
//		Atto revocato = this.findByCodiceCifra(attoDiRevoca.getCodicecifraAttoRevocato());
//		revocato.setStato(StatoAttoEnum.MODIFICATO_INTEGRATO.getDescrizione());
//		revocato = this.save(revocato);
//		
//		eventoService.saveEvento(EventoEnum.EVENTO_MODIFICA_INTEGRAZIONE.getDescrizione(), revocato);
//		
//		Avanzamento avanzamento = new Avanzamento();
//		avanzamento.setAtto(revocato);
//		avanzamento.setDataAttivita(new DateTime());
//		avanzamento.setStato(revocato.getStato());
//		avanzamento.setAttivita("Esecutività atto di modifica integrazione");
//		avanzamento.setProfilo(null);
//		avanzamento.setNote("Attenzione! Questo atto \u00E8 stato modificato - integrato con atto " + attoDiRevoca.getCodiceCifra() + " - " + attoDiRevoca.getNumeroAdozione() 
//		+ " del " + attoDiRevoca.getDataEsecutivita().toString("dd/MM/yyyy") + " con oggetto \"" + attoDiRevoca.getOggetto() + "\"");
//		avanzamento.setWarningType("MOD-INT");
//		avanzamentoService.salva(avanzamento);
//		
//		if(attoDiRevoca.getTipoDeterminazione() !=null && attoDiRevoca.getTipoDeterminazione().getDescrizione()!= null && !attoDiRevoca.getTipoDeterminazione().getDescrizione().trim().isEmpty()) {
//			Avanzamento lastAvanzamento = avanzamentoService.findLastByAtto_id(attoDiRevoca.getId());
//			lastAvanzamento.setNote(attoDiRevoca.getTipoDeterminazione().getDescrizione()+" l'atto "+revocato.getCodiceCifra()+ " - " + revocato.getNumeroAdozione() );
//			avanzamentoService.salva(lastAvanzamento);
//		}
//	}
	
	@Transactional
	public void impostaFineIter(Long idAtto, StatoConclusoEnum tipoFineIter) {
		Atto atto = attoRepository.findOne(idAtto);
		atto.setFineIterDate(LocalDateTime.now());
		atto.setFineiterTipo(tipoFineIter.getCodice());
		attoRepository.save(atto);
	}
	
	@Transactional(readOnly=true)
	public Aoo findAooByAttoId(Long attoId) {
		Atto atto = attoRepository.findOne(attoId);
		return atto.getAoo();
	}

	@Transactional
	public boolean isProposta(Long attoId) {
		BigInteger count = attoRepository.countPropostaAttoFirmatadatutti(attoId);
		return count == null || count.intValue() == 0;
	}

	@Transactional
	public void updateDataPresuntaInizioFinePubblicazione(Date inizioPresunta, Date finePresunta, Long attoId) {

		String inizioPresStr = null;
		String finePresStr = null;

		if (inizioPresunta != null) {
			inizioPresStr = MYSQL_DATEFORMAT.format(inizioPresunta);
		}
		if (finePresunta != null) {
			finePresStr = MYSQL_DATEFORMAT.format(finePresunta);
		}

		attoRepository.updateDataPresuntaInizioFinePubblicazione(inizioPresStr, finePresStr, attoId);
	}

	@Transactional
	public void aggiornaPubblicazioneAlbo(
			Long jobId, Long attoId, LocalDate inizioPubblicazioneAlbo, LocalDate finePubblicazioneAlbo, 
			Long progressivo, RelataAlboDto relata) throws ServiceException {
		if(attoId!=null) {
			Atto attoDb = attoRepository.findOne(attoId);
			if(
					(attoDb.getInizioPubblicazioneAlbo() == null && inizioPubblicazioneAlbo != null) ||
					(attoDb.getFinePubblicazioneAlbo() == null && finePubblicazioneAlbo != null) ||
					((attoDb.getRelatePubblicazione()==null || attoDb.getRelatePubblicazione().size() == 0) && relata!=null)
			) {
				attoDb.setInizioPubblicazioneAlbo(inizioPubblicazioneAlbo);
				attoDb.setFinePubblicazioneAlbo(finePubblicazioneAlbo);
				if (progressivo != null && (progressivo.longValue() > 0)) {
					attoDb.setProgressivoPubblicazioneAlbo(progressivo);
				}
				
				if(inizioPubblicazioneAlbo!=null && finePubblicazioneAlbo!=null && relata!=null && relata.getContent()!=null && relata.getContent().length > 0) {
					DocumentoPdf relataDb = documentoPdfService.aggiungiRelataPubblicazioneAlbo(attoDb, relata);
					if(relataDb!=null && relataDb.getId()!=null) {
						eventoService.pubblicazioneTerminata(attoDb);
						
						List<DocumentoPdf> relataList = new ArrayList<DocumentoPdf>();
						relataList.add(relataDb);
						attoDb.setRelatePubblicazione(relataList);
						attoDb.setStatoProceduraPubblicazione(StatoProceduraPubblicazioneEnum.CONCLUSA.value());
						attoDb.setStatoRelata(StatoRelataEnum.GENERATA.value());
						attoDb.setStato(StatoAttoEnum.PUBBLICAZIONE_TERMINATA.getDescrizione());
						
						jobPubblicazioneService.done(jobId);
						workflowService.infoPubblicazioneAlboRicevute(attoDb.getId());
					}
				}else {
					jobPubblicazioneService.waitingInfo(jobId);
				}
				attoRepository.save(attoDb);
			}else {
				jobPubblicazioneService.waitingInfo(jobId);
			}
		}else {
			log.warn("attoId is null");
			jobPubblicazioneService.waitingInfo(jobId);
		}
	}

	/*
	 * In ATTICO SEMBRA NON UTILIZZATO
	 * 
	@Transactional(readOnly = true)
	public Page<Atto> findAllAdottati(Pageable generatePageRequest, JsonObject searchJson) {
		BooleanExpression predicate = null;
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		try {
			if (searchJson != null && !searchJson.isJsonNull() && searchJson.has("aooId")
					&& !searchJson.get("aooId").getAsString().isEmpty()) {
				predicate = QAtto.atto.aoo.id.eq(Long.parseLong(searchJson.get("aooId").getAsString()))
						.and(QAtto.atto.id.in(eventoService.getAttiEsecutivi()));
				predicate = predicate.and(QAtto.atto.dataAdozione.isNotNull());

				if (searchJson.has("codiceCifra") && !searchJson.get("codiceCifra").isJsonNull()) {
					BooleanExpression p = QAtto.atto.codiceCifra
							.containsIgnoreCase(searchJson.get("codiceCifra").getAsString().trim());
					predicate = predicate.and(p);
				}
				if (searchJson.has("oggetto") && !searchJson.get("oggetto").isJsonNull()) {
					BooleanExpression p = QAtto.atto.oggetto
							.containsIgnoreCase(searchJson.get("oggetto").getAsString().trim());
					predicate = predicate.and(p);
				}
				if (searchJson.has("numeroAdozione") && !searchJson.get("numeroAdozione").isJsonNull()) {
					BooleanExpression p = QAtto.atto.numeroAdozione
							.containsIgnoreCase(searchJson.get("numeroAdozione").getAsString().trim());
					predicate = predicate.and(p);
				}

				if (searchJson.has("dataAdozioneStart") && !searchJson.get("dataAdozioneStart").isJsonNull()) {
					LocalDate ld = new LocalDate(df.parse(searchJson.get("dataAdozioneStart").getAsString().trim()));
					BooleanExpression p = QAtto.atto.dataAdozione.goe(ld);
					predicate = predicate.and(p);
				}

				if (searchJson.has("dataAdozioneEnd") && !searchJson.get("dataAdozioneEnd").isJsonNull()) {
					LocalDate ld = new LocalDate(df.parse(searchJson.get("dataAdozioneEnd").getAsString().trim()));
					BooleanExpression p = QAtto.atto.dataAdozione.loe(ld);
					predicate = predicate.and(p);
				}
				if (searchJson.has("codiceAtto") && !searchJson.get("codiceAtto").isJsonNull()) {
					predicate = predicate.and(
							QAtto.atto.tipoAtto.codice.equalsIgnoreCase(searchJson.get("codiceAtto").getAsString()));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (predicate == null) {
			predicate = QAtto.atto.id.isNull();
		}
		Page<Atto> page = attoRepository.findAll(predicate, generatePageRequest);
		return page;
	}
	*/

	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public void generaRelataPubblicazioneAtto(Atto atto) throws IOException, DocumentException, CifraCatchedException {
		List<ModelloHtml> list = modelloHtmlService.findByTipoDocumento(TipoDocumentoEnum.relata_pubblicazione.name());
		ReportDTO reportDto = new ReportDTO();
		reportDto.setIdAtto(atto.getId());
		reportDto.setIdModelloHtml(list.get(0).getId());
		reportDto.setTipoDoc(TipoDocumentoEnum.relata_pubblicazione.name());
		reportDto.setOmissis(false);
		RelataDiPubblicazioneDto datiRelata = new RelataDiPubblicazioneDto();
		datiRelata.setCodiceCifra(atto.getCodiceCifra());
		datiRelata.setDataAdozione(atto.getDataAdozione());
		datiRelata.setFinePubblicazioneAlbo(atto.getFinePubblicazioneAlbo());
		datiRelata.setInizioPubblicazioneAlbo(atto.getInizioPubblicazioneAlbo());
		// Il responsabile di pubblicazione è l'utente loggato
		Utente currentLogin = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		String nominativoResponsabilePubblicazione = "";
		if (currentLogin != null) {
			if (currentLogin.getNome() != null) {
				nominativoResponsabilePubblicazione = currentLogin.getNome();
			}
			if (currentLogin.getCognome() != null) {
				nominativoResponsabilePubblicazione += (" " + currentLogin.getCognome());
			}
		}
		datiRelata.setNominativoResponsabilePubblicazione(nominativoResponsabilePubblicazione);
		datiRelata.setNumeroAdozione(atto.getNumeroAdozione());
		datiRelata.setOggetto(atto.getOggetto());
		datiRelata.setAooProponente(atto.getAoo());
		File result = reportService.executePreviewRelata(datiRelata, reportDto);
		documentoPdfService.saveRelataPubblicazione(result, atto);
	}

	@Transactional
	public void generaProvvedimentoPubblicazioneAtto(Atto atto)
			throws IOException, DocumentException, DmsException, CifraCatchedException {
		List<ModelloHtml> list = modelloHtmlService
				.findByTipoDocumento(TipoDocumentoEnum.tipo_provvedimento_.name() + atto.getEsito());
		{
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(atto.getId());
			reportDto.setIdModelloHtml(list.get(0).getId());
			reportDto.setTipoDoc(TipoDocumentoEnum.tipo_provvedimento_.name() + atto.getEsito());
			reportDto.setOmissis(false);
			
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_provvedimento_.name());

			File result = reportService.previewAtto(atto, reportDto);
			documentoPdfService.saveAttoAdozionePdf(atto, result, tipoDocumento);
		}
		if ((atto.getRiservato() == null || atto.getRiservato() == false) && atto.getPubblicazioneIntegrale() != null
				&& atto.getPubblicazioneIntegrale() == false) {
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdAtto(atto.getId());
			reportDto.setIdModelloHtml(list.get(0).getId());
			reportDto.setTipoDoc(TipoDocumentoEnum.tipo_provvedimento_.name() + atto.getEsito());
			reportDto.setOmissis(true);
			File result = reportService.previewAtto(atto, reportDto);
			TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.tipo_provvedimento_.name());
			documentoPdfService.saveAttoAdozioneOmissisPdf(atto, result,tipoDocumento);
		}
	}
	*/

	@Transactional
	public List<Atto> findByIdIn(Set<Long> ids) {
		return attoRepository.findByIdIn(ids);
	}

	/*
	 * TODO: In ATTICO NON UTILIZZATI
	 * 
	@Transactional
	public File getPdfRelataForPreview(Long idAtto) throws IOException, DocumentException {
		Atto atto = attoRepository.findOne(idAtto);
		List<ModelloHtml> list = modelloHtmlService.findByTipoDocumento(TipoDocumentoEnum.relata_pubblicazione.name());
		ReportDTO reportDto = new ReportDTO();
		reportDto.setIdAtto(atto.getId());
		reportDto.setIdModelloHtml(list.get(0).getId());
		reportDto.setTipoDoc(TipoDocumentoEnum.relata_pubblicazione.name());
		reportDto.setOmissis(false);
		RelataDiPubblicazioneDto datiRelata = new RelataDiPubblicazioneDto();
		datiRelata.setCodiceCifra(atto.getCodiceCifra());
		datiRelata.setDataAdozione(atto.getDataAdozione());
		datiRelata.setFinePubblicazioneAlbo(atto.getFinePubblicazioneAlbo());
		datiRelata.setInizioPubblicazioneAlbo(atto.getInizioPubblicazioneAlbo());
		// Il responsabile di pubblicazione è l'utente loggato
		Utente currentLogin = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		String nominativoResponsabilePubblicazione = "";
		if (currentLogin != null) {
			if (currentLogin.getNome() != null) {
				nominativoResponsabilePubblicazione = currentLogin.getNome();
			}
			if (currentLogin.getCognome() != null) {
				nominativoResponsabilePubblicazione += (" " + currentLogin.getCognome());
			}
		}
		datiRelata.setNominativoResponsabilePubblicazione(nominativoResponsabilePubblicazione);
		datiRelata.setNumeroAdozione(atto.getNumeroAdozione());
		datiRelata.setOggetto(atto.getOggetto());
		datiRelata.setAooProponente(atto.getAoo());
		return reportService.executePreviewRelata(datiRelata, reportDto);
	}
	
	@Transactional(readOnly = true)
	public List<Long> findAttoIdsByTipoAttoAndStatoIn(Long tipoAttoId, List<String> stati) {
		List<BigInteger> ids = attoRepository.findAttoIdsByTipoAttoAndStatoIn(tipoAttoId, stati);
		List<Long> idsLong = new ArrayList<Long>();
		if (ids != null) {
			for (BigInteger id : ids) {
				idsLong.add(id.longValue());
			}
		}
		return idsLong;
	}
	*/

	@Transactional(readOnly = true)
	public Boolean isAttoRiservato(Long attoId) {
		Byte r = attoRepository.isAttoRiservato(attoId);
		return r != null && r.intValue() == 1 ? true : false;
	}

	@Transactional(readOnly = true)
	public Atto getAttoTestByStato(String stato) {
		Long idAtto = attoRepository.getAttoTestByStato(stato);
		if (idAtto != null) {
			return attoRepository.findOne(idAtto);
		} else {
			return null;
		}
	}

	@Transactional(readOnly = true)
	public Atto findOneSimple(Long attoid) {
		return attoRepository.findOne(attoid);
	}
	
	@Transactional(readOnly = true)
	public Atto findOneScrivaniaBasicAttuazione(Long attoid) {
		Atto db = attoRepository.findOne(attoid);
		Atto scrivania = new Atto(db.getId());
		if(db.getAoo()!=null) {
			scrivania.setAoo(new Aoo(db.getAoo().getId(), db.getAoo().getDescrizione(), db.getAoo().getCodice(), null));
		}
		if(db.getCreatedBy()!=null) {
			Utente utente = utenteService.findByUsername(db.getCreatedBy());
			if(utente!=null) {
				scrivania.setCreatedBy(utente.getNome() + " " + utente.getCognome());
			}
		}
		Set<Avanzamento> ultimoAvanzamento = null;
		if(db.getAvanzamento()!=null) {
			ultimoAvanzamento = new HashSet<Avanzamento>();
			Avanzamento av = new Avanzamento();
			av.setDataAttivita(db.getAvanzamento().iterator().next().getDataAttivita());
			av.setAttivita(db.getAvanzamento().iterator().next().getAttivita());
			ultimoAvanzamento.add(av);
			scrivania.setAvanzamento(ultimoAvanzamento);
		}
		
		scrivania.setCreatedDate(db.getCreatedDate());
		scrivania.setCodiceCifra(db.getCodiceCifra());
		scrivania.setOggetto(db.getOggetto());
		scrivania.setDatiContabili(db.getDatiContabili());
		scrivania.setTipoAtto(new TipoAtto(null, db.getTipoAtto().getCodice(), db.getTipoAtto().getDescrizione()));
		scrivania.setCodiceServizio(null);
		scrivania.setNumeroAdozione(db.getNumeroAdozione()!=null?db.getNumeroAdozione():"");
		scrivania.setDataAdozione(db.getDataAdozione()!=null?db.getDataAdozione():null);
		if(db.getPareri()!=null && db.getPareri().size() > 0) {
			for(Parere p : db.getPareri()) {
				if(p!=null && (p.getTipoAzione().getCodice().equalsIgnoreCase("inserimento_risposta") || p.getTipoAzione().getCodice().equalsIgnoreCase("selezione_relatore"))) {
					if(p.getProfiloRelatore()!=null && p.getProfiloRelatore().getUtente()!=null) {
						//appoggio il dato nel campo codiceServizio
						scrivania.setCodiceServizio(p.getProfiloRelatore().getUtente().getNome() + " " + p.getProfiloRelatore().getUtente().getCognome());
					}
					break;
				}
			}
		}
		return scrivania;
	}
	
	@Transactional(readOnly = true)
	public Atto findOneScrivaniaBasic(Long attoid) {
		Atto db = attoRepository.findOne(attoid);
		Atto scrivania = new Atto(db.getId());
		if(db.getAoo()!=null) {
			scrivania.setAoo(new Aoo(db.getAoo().getId(), db.getAoo().getDescrizione(), db.getAoo().getCodice(), null));
		}
		scrivania.setCodiceCifra(db.getCodiceCifra());
		scrivania.setOggetto(db.getOggetto());
		scrivania.setDatiContabili(db.getDatiContabili());
		scrivania.setTipoAtto(new TipoAtto(null, db.getTipoAtto().getCodice(), db.getTipoAtto().getDescrizione()));
		scrivania.setNumeroAdozione(db.getNumeroAdozione()!=null?db.getNumeroAdozione():"");
		scrivania.setDataAdozione(db.getDataAdozione()!=null?db.getDataAdozione():null);
		scrivania.setDataEsecutivita(db.getDataEsecutivita()!=null?db.getDataEsecutivita():null);
		return scrivania;
	}

	@Transactional(readOnly = true)
	public Atto findOneWithSchedeObbligo(final Long id) {
		Atto atto = findOne(id);
		// Loading lazy informations
		if (atto.getValoriSchedeDati() != null) {
			for (AttoSchedaDato asd : atto.getValoriSchedeDati()) {
				if (asd.getScheda() != null) {
					asd.getScheda().getId();
				}
				if (asd.getSchedaDato() != null && asd.getSchedaDato().getDato() != null) {
					asd.getSchedaDato().getDato().getId();
				}
			}
		}
		return atto;
	}

	@Transactional(readOnly = true)
	public Atto findOneWithSchedeObbligoAndDocuments(final Long id) {
		Atto domain = attoRepository.findOne(id);
		if (domain != null) {
			Atto minAtto = DomainUtil.minimalAtto(domain);

			if (domain.getAoo() != null) {
				domain.getAoo().getHasAssessorati();
			}

			Aoo minAoo = DomainUtil.minimalAoo(domain.getAoo());
			domain.setDestinatariInterni(
					destinatarioInternoService.getDestinatariInterniByAttoId(domain.getId(), true));
			domain.setAoo(minAoo);

			if (domain.getMotivazione() != null) {
				domain.getMotivazione().getTesto();
			}
			if (domain.getPreambolo() != null) {
				domain.getPreambolo().getTesto();
			}
			if (domain.getNoteMotivazione() != null) {
				domain.getNoteMotivazione().getTesto();
			}
			if (domain.getAdempimentiContabili() != null) {
				domain.getAdempimentiContabili().getTesto();
			}
			if (domain.getNoteMotivazione() != null) {
				domain.getNoteMotivazione().getTesto();
			}
			if (domain.getAdempimentiContabili() != null) {
				domain.getAdempimentiContabili().getTesto();
			}
			if (domain.getDispositivo() != null) {
				domain.getDispositivo().getTesto();
			}
			if (domain.getDomanda() != null) {
				domain.getDomanda().getTesto();
			}
			if (domain.getInformazioniAnagraficoContabili() != null) {
				domain.getInformazioniAnagraficoContabili().getTesto();
			}
			if (domain.getDichiarazioni() != null) {
				domain.getDichiarazioni().getTesto();
			}
			if (domain.getGaranzieRiservatezza() != null) {
				domain.getGaranzieRiservatezza().getTesto();
			}
			if (domain.getEmananteProfilo() != null && domain.getEmananteProfilo().getHasQualifica() != null
					&& domain.getEmananteProfilo().getHasQualifica().size() > 0) {
				for (QualificaProfessionale qp : domain.getEmananteProfilo().getHasQualifica()) {
					qp.getDenominazione();
				}
			}
			if (domain.getRupProfilo() != null && domain.getRupProfilo().getHasQualifica() != null
					&& domain.getRupProfilo().getHasQualifica().size() > 0) {
				for (QualificaProfessionale qp : domain.getRupProfilo().getHasQualifica()) {
					qp.getDenominazione();
				}
			}

			if (domain.getAllegati() != null) {
				for (DocumentoInformatico allegato : domain.getAllegati()) {
					// if(allegato.getFile()!=null){
					// allegato.getFile().getContenuto();
					// }
					allegato.setAtto(minAtto);
				}
			}

			if (domain.getHasAmbitoMateriaDl() != null) {
				for (AttoHasAmbitoMateria hasAmbitoMateria : domain.getHasAmbitoMateriaDl()) {
					hasAmbitoMateria.setAtto(minAtto);
					hasAmbitoMateria.getMateriaDl().getDenominazione();
					hasAmbitoMateria.getAmbitoDl().getDenominazione();
				}
			}

			if (domain.getValoriSchedeDati() != null) {
				for (AttoSchedaDato attoSchedaDato : domain.getValoriSchedeDati()) {
					attoSchedaDato.setAtto(minAtto);
				}
			}

			if (domain.getListaNote() != null) {
				for (Nota nota : domain.getListaNote()) {
					nota.setAtto(minAtto);
				}
			}

			if (domain.getDestinatariEsterni() != null) {
				for (RubricaDestinatarioEsterno rubricaDestinatarioEsterno : domain.getDestinatariEsterni()) {
					rubricaDestinatarioEsterno.setAoo(minAoo);
					DomainUtil.nameCerca(rubricaDestinatarioEsterno);
				}

			}

			if (domain.getBeneficiari() != null) {
				for (Beneficiario beneficiario : domain.getBeneficiari()) {
					beneficiario.setAtto(minAtto);
					if (beneficiario.getFatture() != null) {
						for (Fattura fattura : beneficiario.getFatture()) {
							// loading lazy
							fattura.getId();
							fattura.setBeneficiario(new Beneficiario(beneficiario.getId()));
						}
					}
				}
			}

			for (SottoscrittoreAtto iter : domain.getSottoscrittori()) {
				if (iter.getAtto() != null) {
					iter.setAtto(minAtto);
				}

				if (iter.getProfilo() != null) {

					if (iter.getProfilo().getUtente() != null) {
						iter.getProfilo().getUtente().getUsername();
					}

					if (iter.getProfilo().getHasQualifica() != null) {
						for (QualificaProfessionale qua : iter.getProfilo().getHasQualifica()) {
							qua.getDenominazione();
						}
					}
					iter.getProfilo().getAoo();
					if (!domain.getAoo().getId().equals(iter.getProfilo().getAoo().getId())) {
						iter.setAooNonProponente(iter.getProfilo().getAoo());
					}
				}
			}

			// //Documenti
			if (domain.getDocumentiPdf() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdf()) {

				}

			}

			if (domain.getDocumentiPdfOmissis() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfOmissis()) {

				}
			}

			// Documenti Adozione Omissis
			if (domain.getDocumentiPdfAdozioneOmissis() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfAdozioneOmissis()) {
					
				}

			}

			// Documenti Adozione
			if (domain.getDocumentiPdfAdozione() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfAdozione()) {
					
				}

			}

			// Schede anagrafico contabili
			if (domain.getSchedeAnagraficoContabili() != null) {
				for (DocumentoPdf documentoPdf : domain.getSchedeAnagraficoContabili()) {
					
				}

			}

			// Schede anagrafico contabili
			if (domain.getReportsIter() != null) {
				for (DocumentoPdf documentoPdf : domain.getReportsIter()) {
					
				}

			}

			// Relate pubblicazione
			if (domain.getRelatePubblicazione() != null) {
				for (DocumentoPdf documentoPdf : domain.getRelatePubblicazione()) {
					
				}

			}

			if (domain.getAvanzamento() != null && !domain.getAvanzamento().isEmpty()) {
				for (Avanzamento avanzamento : domain.getAvanzamento()) {
					avanzamento.setAtto(null);
					avanzamento.setAttivita(null);
					avanzamento.setProfilo(null);
				}
			}

			if (domain.getPareri() != null) {
				for (Parere parere : domain.getPareri()) {
					Aoo aooMinParere = DomainUtil.minimalAoo(parere.getAoo());
					parere.setAoo(aooMinParere);
					parere.setAtto(minAtto);
					parere.getTipoAzione();

					if (parere.getDocumentiPdf() != null) {
						for (DocumentoPdf docParere : parere.getDocumentiPdf()) {
							if (docParere.getFile() != null) {
								docParere.getFile().getNomeFile();
							}
						}
					}

					if (parere.getAllegati() != null) {
						for (DocumentoInformatico allegato : parere.getAllegati()) {
							allegato.setAtto(null);
							allegato.setParere(null);
							if (allegato.getFile() != null) {
								allegato.getFile().getNomeFile();
							}
						}
					}

				}
			}

			if (domain.getAoo() != null && domain.getAoo().getProfiloResponsabile() != null) {
				domain.getAoo().getProfiloResponsabile().setAoo(null);
				if (domain.getAoo().getAooPadre() != null) {
					domain.getAoo().getAooPadre().setProfiloResponsabile(null);
				}
			}

			if (domain.getProfilo() != null && domain.getProfilo().getAoo() != null
					&& domain.getProfilo().getAoo().getProfiloResponsabile() != null) {
				domain.getProfilo().getAoo().getProfiloResponsabile().setAoo(null);
			}
			if (domain.getProfilo() != null && domain.getProfilo().getAoo() != null
					&& domain.getProfilo().getAoo().getAooPadre() != null
					&& domain.getProfilo().getAoo().getAooPadre().getSottoAoo() != null) {
				domain.getProfilo().getAoo().getAooPadre().setSottoAoo(null);
			}

			if (domain.getValoriSchedeDati() != null) {
				for (AttoSchedaDato asd : domain.getValoriSchedeDati()) {
					if (asd != null && asd.getBeneficiario() == null && asd.getTipoDato() != null
							&& TipoDatoEnum.beneficiario.name().equals(asd.getTipoDato().name())) {
						asd.setBeneficiario(new Beneficiario(-1L));
					}
				}
			}

			domain.setSedutaGiunta(DomainUtil.minimalSedutaGiunta(domain.getSedutaGiunta()));
			if (domain.getValoriSchedeDati() != null) {
				for (AttoSchedaDato asd : domain.getValoriSchedeDati()) {
					if (asd.getScheda() != null) {
						asd.getScheda().getId();
					}
					if (asd.getSchedaDato() != null && asd.getSchedaDato().getDato() != null) {
						asd.getSchedaDato().getDato().getId();
					}
				}
			}
		}
		return domain;
	}

	@Transactional(readOnly = true)
	public Atto findOneWithOrdineGiorno(final Long id) {
		Atto atto = this.findOne(id);
		if (atto.getOrdineGiornos() != null) {
			for (AttiOdg aOdg : atto.getOrdineGiornos()) {
				if (aOdg.getComponenti() != null) {
					// forse questo ciclo serve per il lazy loading.....
					for (@SuppressWarnings("unused")
					ComponentiGiunta c : aOdg.getComponenti()) {
					}
				}
				if (aOdg.getOrdineGiorno() != null) {
					aOdg.getOrdineGiorno().getSedutaGiunta();
					if (aOdg.getOrdineGiorno().getSedutaGiunta() != null
							&& aOdg.getOrdineGiorno().getSedutaGiunta().getVicepresidente() != null) {
						aOdg.getOrdineGiorno().getSedutaGiunta().getVicepresidente().getId();
					}
				}
			}
		}
		return atto;
	}

	@Transactional(readOnly = true)
	public Atto findOne(final Long id) {
		log.debug("findOne id" + id);
		Atto domain = attoRepository.findOne(id);
		if (domain != null) {
			Atto minAtto = DomainUtil.minimalAtto(domain);

			if (domain.getAoo() != null) {
				domain.getAoo().getHasAssessorati();
				log.debug("Assessorati:" + domain.getAoo().getHasAssessorati().size());
			}

			Aoo minAoo = DomainUtil.minimalAoo(domain.getAoo());
			domain.setDestinatariInterni(
					destinatarioInternoService.getDestinatariInterniByAttoId(domain.getId(), true));
			domain.setAoo(minAoo);

			if (domain.getMotivazione() != null) {
				domain.getMotivazione().getTesto();
			}
			if (domain.getPreambolo() != null) {
				domain.getPreambolo().getTesto();
			}
			if (domain.getDomanda() != null) {
				domain.getDomanda().getTesto();
			}
			if (domain.getNoteMotivazione() != null) {
				domain.getNoteMotivazione().getTesto();
			}
			if (domain.getAdempimentiContabili() != null) {
				domain.getAdempimentiContabili().getTesto();
			}
			if (domain.getNoteMotivazione() != null) {
				domain.getNoteMotivazione().getTesto();
			}
			if (domain.getAdempimentiContabili() != null) {
				domain.getAdempimentiContabili().getTesto();
			}
			if (domain.getDispositivo() != null) {
				domain.getDispositivo().getTesto();
			}
			if (domain.getDomanda() != null) {
				domain.getDomanda().getTesto();
			}
			if (domain.getInformazioniAnagraficoContabili() != null) {
				domain.getInformazioniAnagraficoContabili().getTesto();
			}
			if (domain.getDichiarazioni() != null) {
				domain.getDichiarazioni().getTesto();
			}
			if (domain.getGaranzieRiservatezza() != null) {
				domain.getGaranzieRiservatezza().getTesto();
			}
			if (domain.getEmananteProfilo() != null && domain.getEmananteProfilo().getHasQualifica() != null
					&& domain.getEmananteProfilo().getHasQualifica().size() > 0) {
				for (QualificaProfessionale qp : domain.getEmananteProfilo().getHasQualifica()) {
					qp.getDenominazione();
				}
			}
			if (domain.getRupProfilo() != null && domain.getRupProfilo().getHasQualifica() != null
					&& domain.getRupProfilo().getHasQualifica().size() > 0) {
				for (QualificaProfessionale qp : domain.getRupProfilo().getHasQualifica()) {
					qp.getDenominazione();
				}
			}

			if (domain.getAllegati() != null) {
				Set<DocumentoInformatico> allegati = domain.getAllegati();
				for (DocumentoInformatico allegato : allegati) {
					allegato.setAtto(minAtto);
				}
			}

			if (domain.getHasAmbitoMateriaDl() != null) {
				for (AttoHasAmbitoMateria hasAmbitoMateria : domain.getHasAmbitoMateriaDl()) {
					hasAmbitoMateria.setAtto(minAtto);
					hasAmbitoMateria.getMateriaDl().getDenominazione();
					hasAmbitoMateria.getAmbitoDl().getDenominazione();
				}
			}

			if (domain.getValoriSchedeDati() != null) {
				for (AttoSchedaDato attoSchedaDato : domain.getValoriSchedeDati()) {
					attoSchedaDato.setAtto(minAtto);
				}
			}

			if (domain.getListaNote() != null) {
				for (Nota nota : domain.getListaNote()) {
					nota.setAtto(minAtto);
				}
			}

			if (domain.getDestinatariEsterni() != null) {
				for (RubricaDestinatarioEsterno rubricaDestinatarioEsterno : domain.getDestinatariEsterni()) {
					rubricaDestinatarioEsterno.setAoo(minAoo);
					DomainUtil.nameCerca(rubricaDestinatarioEsterno);
				}

			}

			if (domain.getBeneficiari() != null) {
				for (Beneficiario beneficiario : domain.getBeneficiari()) {
					beneficiario.setAtto(minAtto);
					if (beneficiario.getFatture() != null) {
						for (Fattura fattura : beneficiario.getFatture()) {
							// loading lazy
							fattura.getId();
							fattura.setBeneficiario(new Beneficiario(beneficiario.getId()));
						}
					}
				}
			}

			for (SottoscrittoreAtto iter : domain.getSottoscrittori()) {
				if (iter.getAtto() != null) {
					iter.setAtto(minAtto);
				}

				if (iter.getProfilo() != null) {

					if (iter.getProfilo().getUtente() != null) {
						iter.getProfilo().getUtente().getUsername();
					}

					if (iter.getProfilo().getHasQualifica() != null) {
						for (QualificaProfessionale qua : iter.getProfilo().getHasQualifica()) {
							qua.getDenominazione();
						}
					}
					iter.getProfilo().getAoo();
					if (!domain.getAoo().getId().equals(iter.getProfilo().getAoo().getId())) {
						iter.setAooNonProponente(iter.getProfilo().getAoo());
					}
				}
			}
			
			// Proponenti
			for (Profilo propostoDa : domain.getProponenti()) {
				
				if (propostoDa.getUtente() != null) {
					propostoDa.getUtente().getUsername();
				}
				if (propostoDa.getHasQualifica() != null) {
					for (QualificaProfessionale qua : propostoDa.getHasQualifica()) {
						qua.getDenominazione();
					}
				}
				propostoDa.getAoo();
			}

			// Documenti
			if (domain.getDocumentiPdf() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdf()) {

				}
			}

			if (domain.getDocumentiPdfOmissis() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfOmissis()) {

				}
			}

			// Documenti Adozione Omissis
			if (domain.getDocumentiPdfAdozioneOmissis() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfAdozioneOmissis()) {

				}
			}

			// Documenti Adozione
			if (domain.getDocumentiPdfAdozione() != null) {
				for (DocumentoPdf documentoPdf : domain.getDocumentiPdfAdozione()) {

				}
			}

			// Schede anagrafico contabili
			if (domain.getSchedeAnagraficoContabili() != null) {
				for (DocumentoPdf documentoPdf : domain.getSchedeAnagraficoContabili()) {

				}
			}

			// Schede anagrafico contabili
			if (domain.getReportsIter() != null) {
				for (DocumentoPdf documentoPdf : domain.getReportsIter()) {

				}
			}

			// Relate pubblicazione
			if (domain.getRelatePubblicazione() != null) {
				for (DocumentoPdf documentoPdf : domain.getRelatePubblicazione()) {

				}
			}

			if (domain.getAvanzamento() != null && !domain.getAvanzamento().isEmpty()) {
				for (Avanzamento avanzamento : domain.getAvanzamento()) {
					avanzamento.setAtto(null);
					avanzamento.setAttivita(null);
					avanzamento.setProfilo(null);
				}
			}

			if (domain.getPareri() != null) {
				for (Parere parere : domain.getPareri()) {
					Aoo aooMinParere = DomainUtil.minimalAoo(parere.getAoo());
					parere.setAoo(aooMinParere);
					parere.setAtto(minAtto);
					parere.getTipoAzione();
					if(parere.getProfilo()!=null && parere.getProfilo().getAoo()!=null) {
						parere.getProfilo().setAoo(DomainUtil.minimalAoo(parere.getProfilo().getAoo()));
					}

					if (parere.getDocumentiPdf() != null) {
						for (DocumentoPdf docParere : parere.getDocumentiPdf()) {
							if (docParere.getFile() != null) {
								docParere.getFile().getNomeFile();
							}
						}
					}

					if (parere.getAllegati() != null) {
						for (DocumentoInformatico allegato : parere.getAllegati()) {
							allegato.setAtto(null);
							allegato.setParere(null);
							if (allegato.getFile() != null) {
								allegato.getFile().getNomeFile();
							}
						}
					}

				}
			}

			if (domain.getAoo() != null && domain.getAoo().getProfiloResponsabile() != null) {
				domain.getAoo().getProfiloResponsabile().setAoo(null);
				if (domain.getAoo().getAooPadre() != null) {
					domain.getAoo().getAooPadre().setProfiloResponsabile(null);
				}
			}

			if (domain.getProfilo() != null && domain.getProfilo().getAoo() != null
					&& domain.getProfilo().getAoo().getProfiloResponsabile() != null) {
				domain.getProfilo().getAoo().getProfiloResponsabile().setAoo(null);
				// domain.getProfilo().getAoo().getProfiloResponsabile().setUtente(null);
			}
			// INNOVCIFRA-187
			// if(domain.getProfilo() != null &&
			// domain.getProfilo().getHasQualifica() != null){
			// for (QualificaProfessionale qualifica :
			// domain.getProfilo().getHasQualifica()) {
			// if(qualifica.getAoo() != null) {
			// qualifica.setAoo(null);
			// }
			// }
			// }
			if (domain.getProfilo() != null && domain.getProfilo().getAoo() != null
					&& domain.getProfilo().getAoo().getAooPadre() != null
					&& domain.getProfilo().getAoo().getAooPadre().getSottoAoo() != null) {
				domain.getProfilo().getAoo().getAooPadre().setSottoAoo(null);
			}

			if (domain.getValoriSchedeDati() != null) {
				for (AttoSchedaDato asd : domain.getValoriSchedeDati()) {
					if (asd != null && asd.getBeneficiario() == null && asd.getTipoDato() != null
							&& TipoDatoEnum.beneficiario.name().equals(asd.getTipoDato().name())) {
						asd.setBeneficiario(new Beneficiario(-1L));
					}
				}
			}
			
			// Dati Seduta
			List<AttiOdg> ordineGiornos = attiOdgRepository.findByAtto(domain);
			
			AttiOdg aOdg = null;
			if (ordineGiornos != null) {
				for(AttiOdg tmp : ordineGiornos) {					
					if(tmp.getEsito()!=null && domain.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(domain.getEsito())){
						if (aOdg==null || (tmp.getId().longValue() > aOdg.getId().longValue())) {
							aOdg = tmp;
						}
					}
				}
			}
			
			if (aOdg != null) {
				SedutaGiunta seduta = aOdg.getOrdineGiorno().getSedutaGiunta();
				domain.setSedutaGiunta(new SedutaGiunta(seduta.getId(), seduta.getLuogo(), seduta.getDataOra()));
			}
		}
		
		return domain;
	}
	
	@Transactional(readOnly = true)
	public boolean isAttoInItinere(Long attoId) {
		return attoRepository.count(QAtto.atto.id.eq(attoId).and(QAtto.atto.iterStatus.eq("I"))) > 0L;
	}

	// private Set<SottoscrittoreAtto> preimpostaSottoscrittori(Atto atto){
	// TreeSet<SottoscrittoreAtto> sottoscrittoriInseriti = new
	// TreeSet<SottoscrittoreAtto>();
	// boolean isDirigente = false;
	// String ruolidisottoscriventi =
	// WebApplicationProps.getProperty(ConfigPropNames.SOTTOSCRIZIONE_RUOLI_DIRIGENZIALI);
	// log.debug("preimpostaSottoscrittori");
	// if(ruolidisottoscriventi!=null){
	// QualificaProfessionale qualificaDirigente =
	// this.getQualificaIfDigirente(atto.getProfilo());
	// if(qualificaDirigente!=null){
	// String [] ruolidirigenzialiCodiciArray =
	// ruolidisottoscriventi.split(",");
	// List<Ruolo> ruolisottoscriventiList = new ArrayList<Ruolo>();
	// for(int i = 0; i<ruolidirigenzialiCodiciArray.length; i++){
	// List<Ruolo> tmp =
	// ruoloService.findByCodice(ruolidirigenzialiCodiciArray[i]);
	// if(tmp!=null){
	// ruolisottoscriventiList.addAll(tmp);
	// }
	// }
	// List<Profilo> profiliUtente =
	// profiloService.findActiveByUtenteAooTipoatto(atto.getProfilo().getUtente().getId(),
	// atto.getAoo().getId(), atto.getTipoAtto().getId());
	// List<Profilo> profiliSottoscrizione = new ArrayList<Profilo>();
	// for(Profilo profilo : profiliUtente){
	// for(Ruolo ruolo : ruolisottoscriventiList){
	// if(profilo.getGrupporuolo().getHasRuoli().contains(ruolo)){
	// profiliSottoscrizione.add(profilo);
	// break;
	// }
	// }
	// }
	// int ordine = 1;
	// for(Profilo profilo : profiliSottoscrizione){
	// isDirigente = true;
	// SottoscrittoreAtto sottoscrittore = new SottoscrittoreAtto();
	// sottoscrittore.setAtto(atto);
	// sottoscrittore.setOrdineFirma(ordine);
	// sottoscrittore.setProfilo(profilo);
	// if(profilo.getHasQualifica().size() == 1){
	// sottoscrittore.setQualificaProfessionale(profilo.getHasQualifica().iterator().next());
	// }
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// ordine++;
	// }
	// }
	// }
	// if(!isDirigente){
	// SottoscrittoreAtto sottoscrittore = new SottoscrittoreAtto();
	// sottoscrittore.setAtto(atto);
	// sottoscrittore.setOrdineFirma(0);
	// sottoscrittore.setProfilo(atto.getProfilo());
	// if(atto.getProfilo().getHasQualifica().size() == 1){
	// sottoscrittore.setQualificaProfessionale(atto.getProfilo().getHasQualifica().iterator().next());
	// }
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// }
	//
	// if(!atto.getTipoAtto().getCodice().equals("DIR")){
	// log.debug("Aggiunta del direttore di dipartimento e/o di sezione e
	// assessore");
	// Aoo aooTemp = aooRepository.findOne(atto.getAoo().getId());
	// SottoscrittoreAtto sottoscrittore = new SottoscrittoreAtto();
	// sottoscrittore.setAtto(atto);
	// sottoscrittore.setOrdineFirma(1);
	// log.debug("TipologiaAoo ID" + aooTemp.getTipoAoo().getId());
	// if(aooTemp.getTipoAoo() != null && aooTemp.getTipoAoo().getId() == 1){
	// sottoscrittore.setTipologia(3);
	// }
	// else{
	// sottoscrittore.setTipologia(2);
	// }
	//
	// if(aooTemp.getProfiloResponsabileId() != null){
	// log.debug("ProfiloResponsabile
	// Id:{}",aooTemp.getProfiloResponsabileId());
	// sottoscrittore.setProfilo(profiloService.findOne(aooTemp.getProfiloResponsabileId()));
	// if(sottoscrittore.getProfilo().getHasQualifica().size() == 1){
	// sottoscrittore.setQualificaProfessionale(sottoscrittore.getProfilo().getHasQualifica().iterator().next());
	// }
	//
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// }
	// else{
	// log.debug("Profilo null");
	// }
	//
	// if(aooTemp.getAooPadre() != null) {
	// sottoscrittore = new SottoscrittoreAtto();
	// sottoscrittore.setAtto(atto);
	// sottoscrittore.setOrdineFirma(2);
	// log.debug("TipologiaAoo ID" +
	// aooTemp.getAooPadre().getTipoAoo().getId());
	// if(aooTemp.getAooPadre().getTipoAoo() != null &&
	// aooTemp.getAooPadre().getTipoAoo().getId() == 1){
	// sottoscrittore.setTipologia(3);
	// }
	// else{
	// sottoscrittore.setTipologia(2);
	// }
	// if(aooTemp.getAooPadre().getProfiloResponsabileId() != null){
	// log.debug("Padre ProfiloResponsabile
	// Id:{}",aooTemp.getAooPadre().getProfiloResponsabileId());
	// sottoscrittore.setProfilo(profiloService.findOne(aooTemp.getAooPadre().getProfiloResponsabileId()));
	// if(sottoscrittore.getProfilo().getHasQualifica().size() == 1){
	// sottoscrittore.setQualificaProfessionale(sottoscrittore.getProfilo().getHasQualifica().iterator().next());
	// }
	//
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// }
	// else{
	// log.debug("Profilo padre null");
	// }
	//
	//
	// }
	//
	// if(!aooTemp.getHasAssessorati().isEmpty()){
	// log.debug("Presenti assessori");
	// sottoscrittore = new SottoscrittoreAtto();
	// sottoscrittore.setAtto(atto);
	// sottoscrittore.setOrdineFirma(3);
	// sottoscrittore.setTipologia(1);
	// if(aooTemp.getHasAssessorati().size() > 1){
	// log.debug("Trovati n assessori");
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// }
	// else{
	// log.debug("trovato 1 assessore");
	// Assessorato assessorato = aooTemp.getHasAssessorati().iterator().next();
	// if(assessorato.getProfiloResponsabileId() != null){
	// log.debug("ProfiloResponsabile
	// Id:{}",assessorato.getProfiloResponsabileId());
	// sottoscrittore.setProfilo(profiloService.findOne(assessorato.getProfiloResponsabileId()));
	// if(sottoscrittore.getProfilo() != null &&
	// sottoscrittore.getProfilo().getHasQualifica().size() == 1){
	// sottoscrittore.setQualificaProfessionale(sottoscrittore.getProfilo().getHasQualifica().iterator().next());
	// }
	//
	// sottoScrittoreAttoService.save(atto, sottoscrittore);
	// sottoscrittoriInseriti.add(sottoscrittore);
	// }
	// else{
	// log.debug("Profilo null");
	// }
	// }
	// }
	// else{
	// log.debug("Non ci sono assessori");
	// }
	//
	// }
	//
	// return sottoscrittoriInseriti;
	// }

	@Transactional
	public Atto save(Atto atto) {
		return attoRepository.save(atto);
	}

	@Transactional
	public void deleteOnCreate(Atto atto, Long profiloId) {
		if (atto.getCodiceCifra() == null) {
			attoWorkerService.deleteByAttoIdAndProfiloId(atto.getId(), profiloId);
			sottoScrittoreAttoService.deleteByAttoId(atto.getId());
			attoRepository.delete(atto);
		}
	}

	@Transactional
	public void updateStato(StatoAttoEnum stato, Long attoId) {
		attoRepository.updateStatoAtto(stato.getDescrizione(), attoId);
	}

	@Transactional
	public void updateStato(String stato, Long attoId) {
		attoRepository.updateStatoAtto(stato, attoId);
	}

	@Transactional
	public void updateStatoPubblicazione(String statoPubblicazione, Long attoId) {
		attoRepository.updateStatoPubblicazioneAtto(statoPubblicazione, attoId);
	}

	@Transactional
	public void updateStatoProceduraPubblicazione(String statoProceduraPubblicazione, Long attoId) {
		attoRepository.updateStatoProceduraPubblicazioneAtto(statoProceduraPubblicazione, attoId);
	}

	@Transactional
	public void updateDatiAnnullamentoPubblicazione(String motivazioneRichiestaAnnullamento, boolean oscuramento,
			Long jobId) {

		/*
		 * DateFormat dfPerQuery = new SimpleDateFormat("yyyy-MM-dd");
		 * 
		 * DateFormat dfPerAnnullamentoWSProto = new
		 * SimpleDateFormat("dd/MM/yyyy"); DateFormat dfFromWSProto = new
		 * SimpleDateFormat("dd-MM-yyyy");
		 * 
		 * Utente richiedente =
		 * utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		 * 
		 * String nomeECognomeRichiedente = richiedente.getUsername();
		 * 
		 * JobPubblicazione jobPubblicazione =
		 * jobPubblicazioneRepository.findOne(jobId);
		 * 
		 * 
		 * String statoRichiestaAnnullamento =
		 * StatoRichiestaAnnullamentoEnum.INIZIALE.value();
		 * 
		 * if(jobPubblicazione.getAtto() != null &&
		 * jobPubblicazione.getAtto().getStatoRichiestaAnnullamento()!=null){
		 * statoRichiestaAnnullamento =
		 * jobPubblicazione.getAtto().getStatoRichiestaAnnullamento(); }
		 * 
		 * 
		 * 
		 * // TODO: non presente ? String provvedimentoAnnullamento = "";
		 * 
		 * // TODO: dati presi dalla Segnatura, verificare la correttezza
		 * String[] campiProtocollo =
		 * jobPubblicazione.getNumeroProtocollo().split("/");
		 * 
		 * String codiceAoo = campiProtocollo[1]; String numeroRegistrazione =
		 * campiProtocollo[4];
		 * 
		 * String dataRegistrazione = campiProtocollo[3];
		 * 
		 * Date dtReg = null; try { dtReg = new
		 * SimpleDateFormat("ddMMyyyy").parse(dataRegistrazione);
		 * 
		 * dataRegistrazione = dfPerAnnullamentoWSProto.format(dtReg); } catch
		 * (ParseException e1) { e1.printStackTrace(); }
		 * 
		 * 
		 * // TODO: integrazione con Protocollo
		 * 
		 * RispostaAnnullamentoProtocollo rispProto = null;
		 * 
		 * 
		 * //richiamo l'annullamento del protocollo solo se lo stato della
		 * richiesta è allo stato iniziale
		 * if(statoRichiestaAnnullamento.equals(StatoRichiestaAnnullamentoEnum.
		 * INIZIALE.value()) ) { try { rispProto =
		 * wsProtocollazione.annullaProtocolloAlbo( numeroRegistrazione,
		 * dataRegistrazione, motivazioneRichiestaAnnullamento,
		 * provvedimentoAnnullamento);
		 * 
		 * attoRepository.updateStatoRichiestaAnnullamentoAtto(
		 * StatoRichiestaAnnullamentoEnum.PROTOCOLLO.value(),
		 * jobPubblicazione.getAtto().getId()); } catch(Exception ex) {
		 * log.info("ERRORE IN FASE DI ANNULLAMENTO - PROTOCOLLO");
		 * log.error(ex.getMessage()); } }
		 * 
		 * //aggiungere in or lo stato richiesta annullata da protocollo if
		 * (rispProto != null ||
		 * statoRichiestaAnnullamento.equals(StatoRichiestaAnnullamentoEnum.
		 * PROTOCOLLO.value()) ) {
		 * 
		 * 
		 * 
		 * // TODO: capire come restituisce i dati il ws effettivo Date
		 * dataAnnullamento = null; String dataAnnullamentoStr =""; if(rispProto
		 * != null){ dataAnnullamentoStr = rispProto.getDataAnnullamento(); try{
		 * dataAnnullamento = dfFromWSProto.parse(dataAnnullamentoStr); } catch
		 * (Exception e) { dataAnnullamento = new Date(); } }else { // prendo i
		 * dati dalla vecchia richiesta dataAnnullamento =
		 * jobPubblicazione.getAtto().getDataRichiestaAnnullamento().toDate(); }
		 * 
		 * 
		 * // Salvare data annullamento
		 * 
		 * try {
		 * wsPubblicazione.annullaPubblicazione(jobPubblicazione.getAtto(),
		 * nomeECognomeRichiedente, motivazioneRichiestaAnnullamento,
		 * dataAnnullamento, oscuramento);
		 * 
		 * // Infine aggiornare i dati di CIFRA ? if(oscuramento) {
		 * attoRepository.updateDatiAnnullamentoAttoConOscuramento(
		 * motivazioneRichiestaAnnullamento,
		 * dfPerQuery.format(dataAnnullamento), nomeECognomeRichiedente,
		 * jobPubblicazione.getAtto().getId()); } else{
		 * attoRepository.updateDatiAnnullamentoAttoSenzaOscuramento(
		 * motivazioneRichiestaAnnullamento,
		 * dfPerQuery.format(dataAnnullamento), nomeECognomeRichiedente,
		 * jobPubblicazione.getAtto().getId()); }
		 * 
		 * attoRepository.updateStatoRichiestaAnnullamentoAtto(
		 * StatoRichiestaAnnullamentoEnum.CONCLUSA.value(),
		 * jobPubblicazione.getAtto().getId()); } catch(Exception ex) {
		 * log.info("ERRORE IN FASE DI ANNULLAMENTO - ALBO");
		 * log.error(ex.getMessage()); } }
		 */
	}

	/*
	 * TODO: capire come gestire in caso di creazione da WS se prevista
	 * 
	 * @Transactional public Atto saveWs(Atto atto, Long profiloId,
	 * Set<DocumentoInformatico> allegati, SistemaAccreditato sistema) throws
	 * IOException, DocumentException, CifraCatchedException{ Atto attoSalvato =
	 * this.save(atto, null, null, null, profiloId, sistema); if(allegati!=null
	 * && allegati.size() > 0){ for(DocumentoInformatico doc : allegati){
	 * documentoInformaticoService.save(doc); } } return attoSalvato; }
	 */

	@Transactional
	public Atto save(Atto atto, final Long profiloId, String taskBpmId, final AttoWorkflowDTO dto, final DecisioneWorkflowDTO decisione) throws Exception {

		LocalDate dataCreazione = new LocalDate();
		boolean nuovoOClonato = false;
		boolean parereAggiunto = false;
		Parere parereNuovo = null;
		
		DatiContabili dc = datiContabiliService.findOne(atto.getId());
		if(dc!=null && atto.getDatiContabili()!=null) {
			atto.getDatiContabili().setDaticontabili(dc.getDaticontabili());
		}

		if (atto.getId() == null || (atto.getAttoclonatoid() != null && atto.getAttoclonatoid() > 0L)) {
			nuovoOClonato = true;
			atto.setDataCreazione(dataCreazione);
		}

		if (atto.getAllegati() != null) {
			Iterator<DocumentoInformatico> it = atto.getAllegati().iterator();
			while (it.hasNext()) {
				DocumentoInformatico doc = it.next();
				if (doc.getTipoAllegato() != null && doc.getTipoAllegato().getCodice() != null) {
					TipoAllegato tipoAllegato = tipoAllegatoRepository.findByCodice(doc.getTipoAllegato().getCodice());
					doc.setTipoAllegato(tipoAllegato);
				}
			}
		}
		//scorro i pareri per aggiornare il flag pubblicabile di ogni allegato
		if(atto.getPareri()!=null) {
			for (Parere parere : atto.getPareri()) {
				if(parere.getAllegati()!=null) {
					Iterator<DocumentoInformatico> it = parere.getAllegati().iterator();
					while (it.hasNext()) { 
						DocumentoInformatico doc = it.next();
						if(doc.getPubblicabile()!=null) {
							DocumentoInformatico docDb = documentoInformaticoService.findOne(doc.getId());
							if(docDb!=null) {
								//aggiorno solo se il flag pubblicabile è cambiato di valore
								boolean aggiorna = docDb.getPubblicabile() == null && doc.getPubblicabile()!=null;
								if(!aggiorna) {
									if(doc.getPubblicabile()!=null && docDb.getPubblicabile()!=null) {
										aggiorna = doc.getPubblicabile().booleanValue() != docDb.getPubblicabile().booleanValue();
									}
								}
								if(aggiorna) {
									docDb.setPubblicabile(doc.getPubblicabile());
									documentoInformaticoService.save(docDb);
								}
							}
						}
					}
				}
			}
			
		}

		/*
		 * Salvataggio dell'elenco di ConfigurazioneIncarico
		 */
		if (dto != null && dto.getIncarichi() != null) {
			for (ConfigurazioneIncaricoDto configurazioneIncaricoDto : dto.getIncarichi()) {
				configurazioneIncaricoService.save(configurazioneIncaricoDto);
			}
		}

		log.debug("Atto save {}", atto.getId());
		// new atto
		if (atto.getId() == null) {


			if(atto.getAoo() != null) {
				// recupero AOO perche in atto non viene settata per intera
				Aoo aoo = aooRepository.findOne(atto.getAoo().getId());
	
				String codiceArea = aoo.getAooPadre() != null ? aoo.getAooPadre().getCodice() : aoo.getCodice();
				String descrizioneArea = aoo.getAooPadre() != null ? aoo.getAooPadre().getDescrizione()
						: aoo.getDescrizione();
				atto.setCodiceArea(codiceArea);
				atto.setDescrizioneArea(descrizioneArea);
	
				String codiceServizio = aoo.getAooPadre() != null ? aoo.getCodice() : "";
				String descrizioneServizio = aoo.getAooPadre() != null ? aoo.getDescrizione() : "";
				atto.setCodiceServizio(codiceServizio);
				atto.setDescrizioneServizio(descrizioneServizio);
			}

			// Preimposta RUP e sottoscrittori
			if ((atto.getRupProfilo() == null) || (atto.getRupProfilo().getId() == null)
					|| (atto.getRupProfilo().getId().longValue() < 1)) {
				atto.setRupProfilo(atto.getProfilo());
			}
		}

		if (atto.getCodiceUfficio() == null && atto.getUfficio() != null) {
			String descrizioneUfficio = atto.getUfficio().getDescrizione();
			String codiceUfficio = atto.getUfficio().getCodice();
			atto.setDescrizioneUfficio(descrizioneUfficio);
			atto.setCodiceUfficio(codiceUfficio);
		}

		if (nuovoOClonato) {
			Set<AttoSchedaDato> valoriSchedeDatiClonazione = null;
			Set<AttoHasDestinatario> oldDestInterni = null;
			if ((atto.getAttoclonatoid() != null && atto.getAttoclonatoid() > 0L)) {
				if (atto.getValoriSchedeDati() != null && atto.getValoriSchedeDati().size() > 0) {
					valoriSchedeDatiClonazione = atto.getValoriSchedeDati();
					atto.setValoriSchedeDati(null);
				}
				oldDestInterni = destinatarioInternoService.getDestinatariInterniByAttoId(atto.getId(), true);
				this.configuraAttoPerClonazione(atto);
			}

			atto = attoRepository.save(atto);

			/*
			 * if(atto.getCodicecifraAttoCollegato()!=null &&
			 * !"".equals(atto.getCodicecifraAttoCollegato().trim()) &&
			 * atto.getTipoDeterminazione()!=null &&
			 * atto.getTipoDeterminazione().getDescrizione() != null &&
			 * (atto.getTipoDeterminazione().getDescrizione().toLowerCase().
			 * contains("revoca") ||
			 * atto.getTipoDeterminazione().getDescrizione().toLowerCase().
			 * contains("rettifica"))){ Atto daRevocare =
			 * attoRepository.findByCodiceCifra(atto.getCodicecifraAttoCollegato
			 * ()); Avanzamento avanzamento = new Avanzamento();
			 * avanzamento.setStato(StatoAttoEnum.IN_ATTESA_DI_REVOCA.
			 * getDescrizione()); avanzamento.setAtto(daRevocare);
			 * avanzamento.setDataAttivita(new DateTime());
			 * avanzamento.setAttivita("Clonazione con " +
			 * atto.getTipoDeterminazione().getDescrizione());
			 * avanzamentoRepository.save(avanzamento); }
			 */

			// START - completo inserimento dati clonazione (per cui si ha
			// bisogno dell'attoid)
			if ((atto.getAttoclonatoid() != null && atto.getAttoclonatoid() > 0L)) {
				if (oldDestInterni != null && oldDestInterni.size() > 0) {
					Set<AttoHasDestinatario> destinatariInteriNew = new HashSet<AttoHasDestinatario>();
					for (AttoHasDestinatario destInt : oldDestInterni) {
						destInt.setId(null);
						destInt.setDocumentoPdfId(null);
						destInt.setSedutaId(null);
						destInt.setAttoId(atto.getId());
						destinatariInteriNew.add(destInt);
					}
					destinatarioInternoService.salvaDestinatariInterni(destinatariInteriNew, atto.getId(), true);
				}
				if (valoriSchedeDatiClonazione != null) {
					atto.setValoriSchedeDati(valoriSchedeDatiClonazione);
					valoriSchedeDatiClonazione = null;
					for (AttoSchedaDato asd : atto.getValoriSchedeDati()) {
						if (asd != null && asd.getId() != null) {
							asd.getId().setAttoId(atto.getId());
							// attoSchedaDatoService.salva(asd);
						}
					}

					atto = attoRepository.save(atto);
				}
			}
			// END - completo inserimento dati clonazione (per cui si ha bisogno
			// dell'attoid)

			if ((atto.getAttoclonatoid() != null && atto.getAttoclonatoid() > 0L)) {
				Profilo profiloClonatore = profiloService.findOne(profiloId);
				Aoo aooAttoOriginale = attoRepository.getAooOfAtto(atto.getAttoclonatoid());
				if (profiloClonatore.getAoo().getId().equals(aooAttoOriginale.getId())) {
					/*
					 * TODO: in ATTICO i sottescrittori vengono inseriti quando viene effettivamente firmato 
					 *
					Integer ordineNextSottoscrittore = 1;
					Iterable<SottoscrittoreAtto> sottoscrittoriAttoOriginale = sottoScrittoreAttoService
							.getSottoscrittoriByAttoId(atto.getAttoclonatoid());
					boolean profiloClonatorePresente = false;
					
					for (SottoscrittoreAtto sottoscrittore : sottoscrittoriAttoOriginale) {
						SottoscrittoreAtto nuovo = new SottoscrittoreAtto();
						BeanUtils.copyProperties(sottoscrittore, nuovo);
						nuovo.setId(null);
						nuovo.setAtto(atto);
						nuovo.setOrdineFirma(ordineNextSottoscrittore);
						sottoScrittoreAttoService.addSottoscrittore(nuovo);
						ordineNextSottoscrittore++;
						if (!profiloClonatorePresente && profiloClonatore.getUtente() != null
								&& sottoscrittore.getProfilo() != null
								&& sottoscrittore.getProfilo().getUtente() != null && profiloClonatore.getUtente()
										.getId().equals(sottoscrittore.getProfilo().getUtente().getId())) {
							profiloClonatorePresente = true;
						}
					}
					if (!profiloClonatorePresente) {
						SottoscrittoreAtto sottoscrittoreClonatore = new SottoscrittoreAtto();
						sottoscrittoreClonatore.setAtto(atto);
						sottoscrittoreClonatore.setOrdineFirma(0);
						sottoscrittoreClonatore.setProfilo(profiloClonatore);
						if (profiloClonatore.getHasQualifica() != null
								&& profiloClonatore.getHasQualifica().size() > 0) {
							sottoscrittoreClonatore
									.setQualificaProfessionale(profiloClonatore.getHasQualifica().iterator().next());
						}
						sottoScrittoreAttoService.addSottoscrittore(sottoscrittoreClonatore);
					}
					*/
				} 
				else {
					atto.setEmananteProfilo(null);
					atto.setQualificaEmanante(null);
				}

				if (atto.getDestinatariInterni() != null) {
					for (AttoHasDestinatario destinatarioInterno : atto.getDestinatariInterni()) {
						destinatarioInterno.setAttoId(atto.getId());
						destinatarioInterno.setId(null);
					}
				}
			}
			else {
				for (AttoHasDestinatario dest : atto.getDestinatariInterni()) {
					dest.setAttoId(atto.getId());
				}
			}

			destinatarioInternoService.salvaDestinatariInterni(atto.getDestinatariInterni(), atto.getId(), true);
			log.info("Nuovo Atto salvato. Id: " + String.valueOf(atto.getId()));
		}
		else {
			if (atto.getObbligodlgs332013() == null || atto.getObbligodlgs332013() == false) {
				// Delete all AttoSchedaDato
				log.info("Deleting AttoSchedaDato");
				for (AttoSchedaDato attoSchedaDato : atto.getValoriSchedeDati()) {
					log.info("Deleting AttoSchedaDato " + attoSchedaDato.getDataValore());
					attoSchedaDatoService.delete(attoSchedaDato);
				}
				atto.getValoriSchedeDati().clear();
				atto.setHasAmbitoMateriaDl(new ArrayList<AttoHasAmbitoMateria>());
				atto.setMacroCategoriaObbligoDl33(null);
				atto.setCategoriaObbligoDl33(null);
				atto.setObbligoDl33(null);
			}
			if (atto.getBeneficiari() != null && atto.getBeneficiari().size() > 0) {
				for (Beneficiario b : atto.getBeneficiari()) {
					if (b != null && b.getFatture() != null && b.getFatture().size() > 0) {
						for (Fattura f : b.getFatture()) {
							f.setBeneficiario(b);
						}
					}
				}
			}

			atto = attoRepository.save(atto);
			log.debug("stato atto salvato {} ", atto.getStato());

			Long profResp = null;
			if(dto.getDecisione()!=null && dto.getDecisione().getTipoParere()!=null) {
				if(dto.getDecisione().getTipoParere().equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_CONTABILE.getCodice())) {
					VariableInstance vResp = workflowService.getVariabileTask(taskBpmId, ConfigurazioneTaskEnum.VERIFICA_RESPONSABILE_CONTABILE.getCodice());
					profResp = bpmWrapperUtil.getIdProfiloByUsernameBpm(vResp.toString());
				}else if(dto.getDecisione().getTipoParere().equalsIgnoreCase(TipiParereEnum.PARERE_REGOLARITA_TECNICA.getCodice())) {
					VariableInstance vResp = workflowService.getVariabileTask(taskBpmId, ConfigurazioneTaskEnum.VERIFICA_RESPONSABILE_TECNICO.getCodice());
					profResp = bpmWrapperUtil.getIdProfiloByUsernameBpm(vResp.toString());
				}
			}
			
			parereNuovo = parereService.save(taskBpmId, (profResp!=null ? profResp : profiloId), 
					dto.getParere(), dto.getAtto(), dto.getDecisione());
			if(parereNuovo!=null) {
				if(atto.getPareri()==null) {
					atto.setPareri(new HashSet<Parere>());
				}
				atto.getPareri().add(parereNuovo);
				parereAggiunto=true;
			}
		}
		attoWorkerService.checkAndInsertWorker(atto.getId(), profiloId);
		Map<String, Object> preparazione = null;
		try {
			boolean nonEseguireTask = false;
			
			// Gestione AZIONI UTENTE
			if ((dto != null) && (dto.getDecisione() != null)) {
				preparazione = this.preparaEsecuzioneAzione(atto, profiloId, taskBpmId, dto);
				if(preparazione!=null && preparazione.get(ATTO)!=null) {
					atto = (Atto)preparazione.get(ATTO);
				}
				if(preparazione!=null && preparazione.get(DISALLINEAMENTO_DATI_JENTE) != null) {
					nonEseguireTask = (boolean)preparazione.get(DISALLINEAMENTO_DATI_JENTE);
					if(nonEseguireTask && parereAggiunto && parereNuovo!=null) {
						parereService.rimuoviParere(parereNuovo.getId());
						
						atto.getPareri().remove(parereNuovo);
					}
				}
				
				atto = attoRepository.save(atto);
			}
	
			if(!nonEseguireTask && decisione!=null && 
				(decisione.getCodiceAzioneUtente()==null || 
					(!decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.EDIT_ITER_DD_TO_SENZA_VERIFICA)
					&& !decisione.getCodiceAzioneUtente().equalsIgnoreCase(CodiciAzioniUtente.EDIT_ITER_DD_TO_CON_VERIFICA)
					)
				)) {
				// Integrazione con BPM
				workflowService.eseguiAzione(taskBpmId, profiloId, dto.getDecisione(), null);
			}
		}catch(Exception e) {
			/*
			if(preparazione!=null && preparazione.containsKey(DOCS) && preparazione.get(DOCS) != null) {
				@SuppressWarnings("unchecked")
				List<DocumentoPdf> docs = (List<DocumentoPdf>)preparazione.get(DOCS);
				manageException(atto, docs, null, null, e);
			}
			*/
			if(e instanceof RuntimeException) {
				throw e;
			}else {
				throw new RuntimeException(e);
			}
		}
		return atto;
	}

	private Map<String, Object> preparaEsecuzioneAzione(Atto atto, final Long profiloId, 
			String taskBpmId, final AttoWorkflowDTO dto) throws Exception {
		
		List<DocumentoPdf> docGenerati = new ArrayList<DocumentoPdf>();
		Map<String, Object> map = new HashMap<String, Object>();
		
		String codiceDecisione = StringUtil.trimStr(dto.getDecisione().getCodiceAzioneUtente()).toUpperCase();
		if (StringUtil.isNull(codiceDecisione)) {
			map.put(ATTO, atto);
			return map;
		}
		
		boolean loggaAzione = dto.getDecisione().isLoggaAzione();
		String noteLog = "";
		
		switch (codiceDecisione) {
		
		case CodiciAzioniUtente.GENERA_ATTO_DL:
			
			// Per DL verifica la presenza di movimenti contabili
			/*
			 * 2019-10-28 Rimosso il controllo su presenza movimenti (FISISSCOL-172 - Mantis 7934)
			 * 
			List<MovimentoContabileDto> listMov = datiContabiliService.elencoMovimento(atto.getId());
			if (listMov == null || listMov.isEmpty()) {
				throw new ContabilitaServiceException("Al presente Atto non risulta associato nessun movimento contabile." + StringUtil.USER_MESSAGE_NEW_LINE
						+"Occorre inserire i movimenti prima di proseguire.");
			}
			*/
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
			break;
		case CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO_DL:

			boolean datiContabiliCambiati = false;
			//fix problema timeout 20220331 
			if(atto.getDatiContabili()!=null && atto.getDatiContabili().getIncludiMovimentiAtto()!=null && atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue()==true) {
				datiContabiliCambiati = contabilitaServiceWrapper.updateMovimentiContabiliAggiornati(atto, profiloId, true, true,CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO_DL);
			}
			if(datiContabiliCambiati) {
				atto = attoRepository.save(atto);
			}
			String rigenerazioneDocumentoDL = "";
    		VariableInstance vDL = workflowService.getVariabileTask(taskBpmId, AttoProcessVariables.VAR_RIGENERAZIONE_DOCUMENTO);
    		if(vDL!=null) {
    			rigenerazioneDocumentoDL = (String)vDL.getValue();
    		}
    		
    		if(rigenerazioneDocumentoDL.equalsIgnoreCase("SI")) {
    			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
    		}
			//nel caso in cui i dati contabili siano stati modificati su jente, aggiorno i dati su attico e lancio un'eccezione per avvisare l'utente dell'aggiornamento dei dati contabili
			if(datiContabiliCambiati) {
				map.put(DISALLINEAMENTO_DATI_JENTE, true);
			}
			
			
			break;
		case CodiciAzioniUtente.GENERA_ATTO:
			if(atto.getOrdineGiornos() == null) {
				atto = this.findOneWithOrdineGiorno(atto.getId());
			}
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
			break;
		case CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO:
			
			boolean datiContabiliCambiatiDD = false;
			if(atto.getTipoIter()!= null && atto.getTipoIter().getCodice().equalsIgnoreCase("CON_VERIFICA_CONTABILE")) {
				if(atto.getDatiContabili()!=null && atto.getDatiContabili().getIncludiMovimentiAtto()!=null && atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue()==true) {
					datiContabiliCambiatiDD = contabilitaServiceWrapper.updateMovimentiContabiliAggiornati(atto, profiloId, true, true,CodiciAzioniUtente.VISTO_POSITIVO_GENERA_ATTO);
				}
			}
			
			if(datiContabiliCambiatiDD) {
				atto = attoRepository.save(atto);
			}
			String rigenerazioneDocumento = "";
    		VariableInstance v = workflowService.getVariabileTask(taskBpmId, AttoProcessVariables.VAR_RIGENERAZIONE_DOCUMENTO);
    		if(v!=null) {
    			rigenerazioneDocumento = (String)v.getValue();
    		}
    		
    		if(rigenerazioneDocumento.equalsIgnoreCase("SI")) {
    			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
    		}
    		//nel caso in cui i dati contabili siano stati modificati su jente, aggiorno i dati su attico e lancio un'eccezione per avvisare l'utente dell'aggiornamento dei dati contabili
			if(datiContabiliCambiatiDD) {
				map.put(DISALLINEAMENTO_DATI_JENTE, true);
			}
			break;
			
		case CodiciAzioniUtente.GENERA_PAR_REG:
			if(atto.getOrdineGiornos() == null) {
				atto = this.findOneWithOrdineGiorno(atto.getId());
			}
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
			break;
		
		case CodiciAzioniUtente.GENERA_FIRMA_ATTO:
			if(atto.getOrdineGiornos() == null) {
				atto = this.findOneWithOrdineGiorno(atto.getId());
			}
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), true, dto.getDtoFdr());
			break;
		
		case CodiciAzioniUtente.GENERA_FIRMA_REG_TECN:
			if(atto.getOrdineGiornos() == null) {
				atto = this.findOneWithOrdineGiorno(atto.getId());
			}
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), true, dto.getDtoFdr());
			break;
			
		case CodiciAzioniUtente.GENERA_FIRMA_REG_CONT:
			if(atto.getOrdineGiornos() == null) {
				atto = this.findOneWithOrdineGiorno(atto.getId());
			}
			docGenerati = generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), true, dto.getDtoFdr());
			break;
			
		case CodiciAzioniUtente.NUMERA_E_GENERA_ATTO:
			docGenerati = this.numeraGeneraFirma(atto, profiloId, taskBpmId, dto, false);
			break;
		
		case CodiciAzioniUtente.NUMERA_GENERA_FIRMA_ATTO:
			if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())) {
				if(atto.getDatiContabili()!=null && atto.getDatiContabili().getIncludiMovimentiAtto()!=null && atto.getDatiContabili().getIncludiMovimentiAtto().booleanValue()==true) {
					contabilitaServiceWrapper.updateMovimentiContabili(atto, profiloId, false, false);
				}
			}
			docGenerati = this.numeraGeneraFirma(atto, profiloId, taskBpmId, dto, true);
			break;
			
		case CodiciAzioniUtente.FIRMA_ATTO:
			break;
			
		case CodiciAzioniUtente.INVIA_DATI_CONTABILI:
			contabilitaServiceWrapper.sendBozza(atto, profiloId);
			break;
			
		case CodiciAzioniUtente.RECUPERA_DATI_CONTABILI:
			contabilitaServiceWrapper.updateMovimentiContabili(atto, profiloId, true, true);
			DatiContabili datiCont = datiContabiliRepository.findOne(atto.getId());
			if (datiCont == null) {
				datiCont = new DatiContabili(true);
				datiCont.setId(atto.getId());
			}else {
				//impostare a true solo se recupera davvero dati contabili
				datiCont.setIncludiMovimentiAtto(datiCont.getDatiRicevuti() != null?datiCont.getDatiRicevuti().booleanValue():false);
				
			}
			datiContabiliRepository.save(datiCont);
			break;
			
		case CodiciAzioniUtente.VERIFICA_DATI_CONTABILI:
			contabilitaServiceWrapper.verificaMovimentiContabili(atto);
			break;
		
		case CodiciAzioniUtente.EDIT_ITER_DD_TO_CON_VERIFICA:
		{
			noteLog = "L'iter burocratico dell'atto \u00E8 stato modificato da \"" + atto.getTipoIter().getDescrizione() + "\" a ";
			workflowService.eseguiAzione(taskBpmId, profiloId, dto.getDecisione(), "CON_VERIFICA_CONTABILE");
            String nextTaskId = workflowService.getMyNextTaskId(dto.getAtto().getId(), profiloId);
            if(nextTaskId==null || nextTaskId.isEmpty()) {
            	nextTaskId = workflowService.getMyNextTaskId(dto.getAtto().getId(), atto.getProfilo().getId());
            	workflowService.riassegnazioneTaskMyOwn(nextTaskId, profiloId);
            }
            atto = this.editIterDD(atto, nextTaskId, "CON_VERIFICA_CONTABILE");
			noteLog += "\"" +atto.getTipoIter().getDescrizione() + "\"";
			break;
		}
			
		case CodiciAzioniUtente.EDIT_ITER_DD_TO_SENZA_VERIFICA:
		{	
			noteLog = "L'iter burocratico dell'atto \u00E8 stato modificato da \"" + atto.getTipoIter().getDescrizione() + "\" a ";
			workflowService.eseguiAzione(taskBpmId, profiloId, dto.getDecisione(), "SENZA_VERIFICA_CONTABILE");			
			String nextTaskId = workflowService.getMyNextTaskId(dto.getAtto().getId(), profiloId);
			if(nextTaskId==null || nextTaskId.isEmpty()) {
				nextTaskId = workflowService.getMyNextTaskId(dto.getAtto().getId(), atto.getProfilo().getId());
				workflowService.riassegnazioneTaskMyOwn(nextTaskId, profiloId);
			}
			atto = this.editIterDD(atto, nextTaskId, "SENZA_VERIFICA_CONTABILE");
			noteLog += "\"" +atto.getTipoIter().getDescrizione() + "\"";
			String codiceTipoAtto = dto.getAtto().getTipoAtto().getCodice();
			String numeroRif = dto.getAtto().getCodiceCifra().substring(dto.getAtto().getCodiceCifra().length() - 5);
			int annoRif = dto.getAtto().getDataCreazione().getYear();
			contabiliaService.eliminaBozza(codiceTipoAtto, numeroRif, annoRif);
			break;
		}
		
		default:
			break;
		}

		if(loggaAzione) {
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), profiloId, atto.getStato(), 
					(dto.getDecisione().getDescrizioneAlternativa() != null && !dto.getDecisione().getDescrizioneAlternativa().trim().isEmpty() ? dto.getDecisione().getDescrizioneAlternativa() : dto.getDecisione().getDescrizione()),
					noteLog
			);	
		}
		map.put(ATTO, atto);
		map.put(DOCS, docGenerati);
		return map;
	}
	
//	private boolean aggiornamentoDatiContabili(Atto atto, Long profiloId, boolean isBozza, boolean verificaMovimenti) {
//		
//		//il metodo ritorno un boolean valorizzato:
//		//true : se i dati contabili sono cambiati
//		//false : se i dati contabili non sono cambiati
//		
//		
//		DatiContabili datiContabiliPreVisto = atto.getDatiContabili();
//		
//		BigDecimal impE1 = datiContabiliPreVisto.getImportoEntrata()!=null?datiContabiliPreVisto.getImportoEntrata():new BigDecimal(0);
//		BigDecimal impU1 = datiContabiliPreVisto.getImportoUscita()!=null?datiContabiliPreVisto.getImportoUscita():new BigDecimal(0);
//		String json1 = datiContabiliPreVisto.getDaticontabili()!=null?datiContabiliPreVisto.getDaticontabili():"";
//		log.info("impE1"+impE1);
//		log.info("impU1"+impU1);
//		log.info("json1"+json1);
//		
//		//aggiorno i dati da jente per controllare che su attico non siano allineati
//		contabilitaServiceWrapper.updateMovimentiContabili(atto, profiloId, isBozza, verificaMovimenti);
//		
//		DatiContabili datiContabiliAggiornati = datiContabiliRepository.findOne(atto.getId());
//		atto.setDatiContabili(datiContabiliAggiornati);
//		
//		if(
//				(datiContabiliPreVisto == null && datiContabiliAggiornati != null) ||
//				(datiContabiliPreVisto != null && datiContabiliAggiornati == null)
//				) {
//			return true;
//		}
//		
//		if(datiContabiliPreVisto != null && datiContabiliAggiornati != null) {
//			
//			//controllo l'importo entrata
//			BigDecimal impE2 = datiContabiliAggiornati.getImportoEntrata()!=null?datiContabiliAggiornati.getImportoEntrata():new BigDecimal(0);
//			BigDecimal impU2 = datiContabiliAggiornati.getImportoUscita()!=null?datiContabiliAggiornati.getImportoUscita():new BigDecimal(0);
//			String json2 = datiContabiliAggiornati.getDaticontabili()!=null?datiContabiliAggiornati.getDaticontabili():"";
//			log.info("impE1"+impE1);
//			log.info("impU1"+impU1);
//			log.info("json1"+json1);
//			log.info("impE2"+impE2);
//			log.info("impU2"+impU2);
//			log.info("json2"+json2);
//			
//			if(impE1.compareTo(impE2)!=0) {return true;}
//			
//			//controllo l'importo uscita
//			
//			if(impU1.compareTo(impU2)!=0) {return true;}
//			
//			//controllo il json dei dati contabili
//			
//			if(!json1.equalsIgnoreCase(json2)) {return true;}
//		}
//		
//		
//		
//		return false;
//	}

	private Atto editIterDD(Atto atto, String taskBpmId, String newCodiceIter) throws Exception {
		List<ConfigurazioneIncaricoDto> incarichiSalvati = configurazioneIncaricoService.findByAtto(atto.getId());
		if(incarichiSalvati!=null && incarichiSalvati.size() > 0) {
			List<ConfigurazioneIncaricoDto> incarichiToRemove = null;
			Iterable<AssegnazioneIncaricoDTO> incTaskIt = workflowService.getAssegnazioniIncarichi(taskBpmId);
			if(incTaskIt!=null) {
				incarichiToRemove = new ArrayList<ConfigurazioneIncaricoDto>();
				for(ConfigurazioneIncaricoDto incDb : incarichiSalvati) {
					if(incDb != null) {
						boolean toRemove = true;
						if(incDb.getConfigurazioneTaskCodice()!=null) {
							for(AssegnazioneIncaricoDTO incTask : incTaskIt) {
								if(incTask!=null && incTask.getCodiceConfigurazione()!=null && incTask.getCodiceConfigurazione().equalsIgnoreCase(incDb.getConfigurazioneTaskCodice())) {
									toRemove = false;
									break;
								}
							}
						}
						if(toRemove) {
							incarichiToRemove.add(incDb);
						}
					}
				}
			}else {
				incarichiToRemove = incarichiSalvati;
			}
			if(incarichiToRemove!=null && incarichiToRemove.size() > 0) {
				configurazioneIncaricoService.removeConfigurazioniIncarico(incarichiToRemove);
			}
		}
		TipoIter tipoIter = tipoIterService.findByCodiceAndTipoAttoId(newCodiceIter, atto.getTipoAtto().getId());
		if(tipoIter == null) {
			throw new GestattiCatchedException("Il tipo di iter " + newCodiceIter + " non esiste.");
		}
		atto.setTipoIter(tipoIter);
		boolean oggettoContabileSettato = false;
		//CDFATTICOMEV-3
		if(!atto.getTipoIter().getCodice().equalsIgnoreCase("SENZA_VERIFICA_CONTABILE")) {
			List<SezioneTipoAttoDto> listSezionetipoAttoDto = sezioneTipoAttoService.findByTipoAtto(atto.getTipoAtto().getId());
			if(listSezionetipoAttoDto!=null && listSezionetipoAttoDto.size() > 0) {
				for(SezioneTipoAttoDto sezione : listSezionetipoAttoDto) {
					if(sezione!=null && sezione.getVisibile() && sezione.getCodice().equalsIgnoreCase("dichiarazioni")) {
						atto.setContabileOggetto(atto.getOggetto());
						oggettoContabileSettato = true;
					}
				}
			}
		}
		if(!oggettoContabileSettato) {
			atto.setContabileOggetto(null);
		}
		atto.setDatiContabili(new DatiContabili(atto.getId()));
		return atto;
	}
	
	@Transactional
	public List<DocumentoPdf> numeraGeneraFirma(Atto atto, Long profiloId, String taskBpmId, AttoWorkflowDTO dto, boolean firma) throws Exception{
		if(dto.getModelliHtmlIds()==null || dto.getModelliHtmlIds().size() < 1) {
			throw new GestattiCatchedException("Nessun modello selezionato");
		}
		
		boolean aggiornaDataAdozione = WebApplicationProps.getPropertyList(ConfigPropNames.LIST_TIPO_ATTO_AGGIORNA_DATA_ADOZIONE, new ArrayList<Object>()).contains(atto.getTipoAtto().getCodice());
			
		LocalDate dataAdozione;
		if(aggiornaDataAdozione) {
			dataAdozione = LocalDate.now();
		}else {
			//workaroud DL - data adozione non si aggiorna se esiste la data di esecutivit�
			if(atto.getTipoAtto().getCodice().equalsIgnoreCase("DL") && atto.getDataEsecutivita() == null){
				dataAdozione = LocalDate.now();
			}else {
				dataAdozione = atto.getDataAdozione()!= null ? atto.getDataAdozione():LocalDate.now();
			}
				
		}

		//Numerazione
		if (StringUtil.isNull(atto.getNumeroAdozione())) {
			String numeroAdozione = null;
			if(atto.getTipoAtto().getProgressivoAdozioneAoo()) {
				numeroAdozione = codiceProgressivoService.generaCodiceCifraAdozione( atto.getAoo(), dataAdozione.getYear() ,  atto.getTipoAtto().getTipoProgressivo() );
			} else {
				numeroAdozione = codiceProgressivoService.generaCodiceCifraAdozione( null, dataAdozione.getYear() ,  atto.getTipoAtto().getTipoProgressivo() );
			}
			atto.setNumeroAdozione(  numeroAdozione);
			atto.setDataNumerazione(LocalDate.now());
		}
		
		atto.setDataAdozione( dataAdozione );
		atto = attoRepository.save(atto);
		
		return generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), firma, dto.getDtoFdr());
	}
	
	public FirmaRemotaDTO firmaSalvaDocumenti(FirmaRemotaDTO dto, Atto atto, long profiloId,
			List<it.linksmt.assatti.datalayer.domain.File> pdfDaFirmare, List<byte[]> filesDaFirmare, List<TipoDocumento> tipiDocumento) throws Exception {
		
		List<DocumentoPdf> documenti = new ArrayList<DocumentoPdf>();
		List<DocumentoInformatico> allegati = new ArrayList<DocumentoInformatico>();
		
		try {
			List<FirmaRemotaRequestDTO> reqFirma = new ArrayList<FirmaRemotaRequestDTO>();
			int [] firmeOrig = new int [filesDaFirmare.size()];
			for (int i=0; i< filesDaFirmare.size(); i++) {
				if(filesDaFirmare.get(i)==null || filesDaFirmare.get(i).length == 0L) {
					throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "content of file to be signed is empty");
				}
				PDDocument signedFile = PDDocument.load(new ByteArrayInputStream(filesDaFirmare.get(i)));
				int nFirme = signedFile.getSignatureFields().size();
				firmeOrig[i] = nFirme;
				FirmaRemotaRequestDTO req = new FirmaRemotaRequestDTO(pdfDaFirmare.get(i).getNomeFile(), filesDaFirmare.get(i));
				reqFirma.add(req);
			}			
			
			List<FirmaRemotaResponseDTO> listFirmaRemotaResponseDTO = FirmaRemotaService.firmaPades(
					dto.getCodiceFiscale(), dto.getPassword(), dto.getOtp(), reqFirma, null);
			
			for(int i=0; i<listFirmaRemotaResponseDTO.size(); i++) {
				
				FirmaRemotaResponseDTO firmaRemotaResponseDTO = listFirmaRemotaResponseDTO.get(i);
				if(firmaRemotaResponseDTO==null || firmaRemotaResponseDTO.getDocument()==null || firmaRemotaResponseDTO.getDocument().length == 0L) {
					throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "content of signed file is empty");
				}
				
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
				/*
				 * Salvo documento su repository documentale
				 */
				String cmisObjectId = null;
				try {
					String attoFolderPath = serviceUtil.buildDocumentPath(
							tipiDocumento.get(i), atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
					cmisObjectId = dmsService.save(
							attoFolderPath, firmaRemotaResponseDTO.getDocument(), 
							pdfDaFirmare.get(i).getNomeFile(), pdfDaFirmare.get(i).getContentType(), null);
				}
				catch (Exception e) {
					log.error("errore salvataggio cmis: " + e.getMessage());
					e.printStackTrace();
					throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, "Errore salvataggio documento su documentale.");
				}
				
				it.linksmt.assatti.datalayer.domain.File pdfFirmato = new it.linksmt.assatti.datalayer.domain.File();
				pdfFirmato.setContentType(pdfDaFirmare.get(i).getContentType());
				pdfFirmato.setNomeFile(pdfDaFirmare.get(i).getNomeFile());
				pdfFirmato.setCmisObjectId(cmisObjectId);
				pdfFirmato.setSha256Checksum(FileChecksum.calcolaImpronta(firmaRemotaResponseDTO.getDocument()));
				pdfFirmato.setSize(new Long(firmaRemotaResponseDTO.getDocument().length));

				log.debug("Allegato without parere");
				DocumentoPdf documentoRiferimento = documentoPdfService.saveFileFirmato(
						atto, pdfFirmato, profiloId, tipiDocumento.get(i), false);
				documenti.add(documentoRiferimento);
			}
			
			dto.setDocumentiFirmati(documenti);
			
			// TODO ???
//			dto.setMarcature(allegati);
			return dto;
		}
		catch(FirmaRemotaException e) {
//			String context = "Errore Generico in fase di firma e salvataggio documenti";
//			manageException(atto, documenti, allegati, context, e);
			throw e;
		}
		catch(Exception e) {
			if(atto!=null && atto.getId()!=null) {
				log.error("Errore in fase di firma per atto id " + atto.getId());
			}
			e.printStackTrace();
			String context = "Errore Generico in fase di firma e salvataggio documenti";
//			manageException(atto, documenti, allegati, context, e);
			throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, context);
		}
	}
	
	/*
	private void manageException(Atto atto, List<DocumentoPdf> documenti, List<DocumentoInformatico> allegati, String context, Exception e) {
		if(e!=null && context!=null) {
			log.error(context+": "+ e.getMessage(), e);
		}
		
		if(documenti!=null && documenti.size() > 0) {
			for(DocumentoPdf doc : documenti) {
				if(doc!=null && doc.getFile() != null && doc.getFile().getCmisObjectId()!=null) {
					try {
						dmsService.remove(doc.getFile().getCmisObjectId());
					}catch(Exception e2) {
	    				log.error("Error in removing doc " + doc.toString());
	    			}
				}
			}
		}
		
		if(allegati!=null && allegati.size() > 0) {
			for(DocumentoInformatico all : allegati) {
				if(all!=null && all.getFile() != null && all.getFile().getCmisObjectId()!=null) {
					try {
						dmsService.remove(all.getFile().getCmisObjectId());
					}catch(Exception e2) {
	    				log.error("Error in removing doc " + all.toString());
	    			}
				}
			}
		}
	}
	*/
	
	@Transactional
	public List<DocumentoPdf> generaDocumenti(Long profiloId, Atto atto, String taskBpmId, List<Long> modelloIdList, boolean numera) throws Exception{
		AttoWorkflowDTO dto = new AttoWorkflowDTO();
		dto.setModelliHtmlIds(modelloIdList);
		if(numera) {
			return this.numeraGeneraFirma(atto, profiloId, taskBpmId, dto, false);
		}else {
			return this.generaAtto(profiloId, atto, taskBpmId, dto.getModelliHtmlIds(), false, null);
		}
	}
	
	@Transactional
	public List<DocumentoPdf> generaDocumenti(Long profiloId, Atto atto, String taskBpmId, Long modelloId, boolean numera) throws Exception{
		List<Long> modelloIdList = new ArrayList<Long>();
		modelloIdList.add(modelloId);
		return this.generaDocumenti(profiloId, atto, taskBpmId, modelloIdList, numera);
	}
	
	@Transactional
	public List<DocumentoPdf> generaAtto(Long profiloId, Atto atto, String taskBpmId, List<Long> modelliHtmlIds, boolean firmaRemotaContestuale, FirmaRemotaDTO fdrDto) throws Exception {
		List<DocumentoPdf> documentiGenerati = new ArrayList<DocumentoPdf>();
		if (modelliHtmlIds != null && modelliHtmlIds.size() > 0) {
			List<it.linksmt.assatti.datalayer.domain.File> pdfDaFirmare = new ArrayList<it.linksmt.assatti.datalayer.domain.File>();
			List<byte[]> filesDaFirmare = new ArrayList<byte[]>();
			List<TipoDocumento> tipiDocumento = new ArrayList<TipoDocumento>();
			List<String> tipiDocDaFirmare = null;
			
			if(firmaRemotaContestuale) {
				tipiDocDaFirmare = workflowService.getCodiciTipiDocumentoDaFirmare(taskBpmId);
			}
			
			try {
				if(!atto.getTipoAtto().getCodice().equalsIgnoreCase("DEC") || (atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().trim().isEmpty())) {
					ProfiloQualificaBean profQualifica = workflowService.getProfiloQualificaEsecutore(taskBpmId);
					if (profQualifica != null) {
						if (profQualifica.getProfilo() != null) {
							atto.setEmananteProfilo(profQualifica.getProfilo());
						}
						if (!StringUtil.isNull(profQualifica.getDescrizioneQualifica())) {
							atto.setDescrizioneQualificaEmanante(profQualifica.getDescrizioneQualifica());
						}
						atto = attoRepository.save(atto);
					}
				}
				
				// richiamo la generazione degli atti
				for (int i = 0; i < modelliHtmlIds.size(); i++) {
	
					//Genero Atto
					ModelloHtml modelloHtml = modelloHtmlService.findOne(modelliHtmlIds.get(i));
					ReportDTO reportDto = new ReportDTO();
					reportDto.setIdAtto(atto.getId());
					reportDto.setIdModelloHtml(modelloHtml.getId());
					reportDto.setOmissis(false);
					java.io.File pdfAtto = reportService.previewAtto(atto, reportDto);
	
					// TODO: verificare se esiste la possibilità che arrivi null
					// e come agire in tal caso
					boolean omissis = false;
					if (atto.getPubblicazioneIntegrale() != null) {
						omissis = !atto.getPubblicazioneIntegrale().booleanValue();
					}
	
					DocumentoPdf documentoPdf = documentoPdfService.salvaDocumentoPdf(atto, pdfAtto,
							modelloHtml.getTipoDocumento().getCodice(), false, false, false, false);
					documentiGenerati.add(documentoPdf);
					
					if(firmaRemotaContestuale && tipiDocDaFirmare.contains(modelloHtml.getTipoDocumento().getCodice())) {
						it.linksmt.assatti.datalayer.domain.File pdf = documentoPdf.getFile();
						pdfDaFirmare.add(pdf);
						filesDaFirmare.add(dmsService.getContent(pdf.getCmisObjectId()));
						tipiDocumento.add(documentoPdf.getTipoDocumento());
					}
					
					if(omissis){
						reportDto.setOmissis(true);
						File pdfAttoOmissis = reportService.previewAtto(atto, reportDto);
						documentoPdf = documentoPdfService.salvaDocumentoPdf(atto, pdfAttoOmissis,
								modelloHtml.getTipoDocumento().getCodice(), true, false, false, false);
						documentiGenerati.add(documentoPdf);
						
						if(firmaRemotaContestuale && tipiDocDaFirmare.contains(modelloHtml.getTipoDocumento().getCodice())) {
							it.linksmt.assatti.datalayer.domain.File pdf = documentoPdf.getFile();
							pdfDaFirmare.add(pdf);
							filesDaFirmare.add(dmsService.getContent(pdf.getCmisObjectId()));
							tipiDocumento.add(documentoPdf.getTipoDocumento());
						}
					}
					if(firmaRemotaContestuale && tipiDocDaFirmare.contains(modelloHtml.getTipoDocumento().getCodice())) {
						String codiceFiscale = profiloService.getCodiceFiscaleByProfiloId(profiloId);
						fdrDto.setCodiceFiscale(codiceFiscale);
						FirmaRemotaDTO exec = this.firmaSalvaDocumenti(fdrDto, atto, profiloId, pdfDaFirmare, filesDaFirmare, tipiDocumento);
						documentiGenerati.addAll(exec.getDocumentiFirmati());
					}
				}
			}catch(FirmaRemotaException e) {
//				manageException(atto, documentiGenerati, null, null, e);
				throw e;
			
			}
			catch(Exception e) {
				if(atto!=null && atto.getId()!=null) {
                    log.error("Errore in fase di firma per atto id " + atto.getId());
                }
                e.printStackTrace();
				String context = "Errore Generico in fase di firma e salvataggio documenti";
//				manageException(atto, documentiGenerati, null, context, e);
				if(firmaRemotaContestuale) {
					throw new GeneraFirmaDocumentoException(context);
				}else {
					throw new RuntimeException(context);
				}
			}
		}else {
			throw new GestattiCatchedException("Nessun modello selezionato");
		}
		
		return documentiGenerati;
	}

	private void configuraAttoPerClonazione(Atto atto) throws GestattiCatchedException {
		if (atto.getMotivoClonazione() == null || "".equals(atto.getMotivoClonazione().trim())) {
			// if(atto.getTipoDeterminazione()!=null &&
			// atto.getTipoDeterminazione().getDescrizione() != null &&
			// atto.getTipoDeterminazione().getDescrizione().toLowerCase().contains("revoca")){
			// atto.setMotivoClonazione(MotivoClonazioneEnum.REVOCA.toString());
			// }else if(atto.getTipoDeterminazione()!=null &&
			// atto.getTipoDeterminazione().getDescrizione() != null &&
			// atto.getTipoDeterminazione().getDescrizione().toLowerCase().contains("rettifica")){
			// atto.setMotivoClonazione(MotivoClonazioneEnum.RETTIFICA.toString());
			// }else{
			atto.setMotivoClonazione(MotivoClonazioneEnum.CLONAZIONE.toString());
			// }
		}
		atto.setAvanzamento(null);
		atto.setId(null);
		atto.setProcessoBpmId(null);
		atto.setVersion(0);
		if (atto.getAdempimentiContabili() != null) {
			atto.getAdempimentiContabili().setId(null);
		}
		if (atto.getDichiarazioni() != null) {
			atto.getDichiarazioni().setId(null);
		}
		if (atto.getDispositivo() != null) {
			atto.getDispositivo().setId(null);
		}
		if (atto.getDomanda() != null) {
			atto.getDomanda().setId(null);
		}
		if (atto.getGaranzieRiservatezza() != null) {
			atto.getGaranzieRiservatezza().setId(null);
		}
		if (atto.getInformazioniAnagraficoContabili() != null) {
			atto.getInformazioniAnagraficoContabili().setId(null);
		}
		if (atto.getMotivazione() != null) {
			atto.getMotivazione().setId(null);
		}
		if (atto.getNoteMotivazione() != null) {
			atto.getNoteMotivazione().setId(null);
		}
		if (atto.getPreambolo() != null) {
			atto.getPreambolo().setId(null);
		}
		if (atto.getBeneficiari() != null && atto.getBeneficiari().size() > 0) {
			for (Beneficiario b : atto.getBeneficiari()) {
				if (b != null) {
					b.setAtto(atto);
					b.setId(null);
					if (b.getFatture() != null && b.getFatture().size() > 0) {
						b.setFatture(new HashSet<Fattura>(b.getFatture()));
						for (Fattura fattura : b.getFatture()) {
							if (fattura != null) {
								fattura.setId(null);
								fattura.setBeneficiario(b);
							}
						}
					}
				}
			}
		}
		if (atto.getHasAmbitoMateriaDl() != null && atto.getHasAmbitoMateriaDl().size() > 0) {
			for (AttoHasAmbitoMateria am : atto.getHasAmbitoMateriaDl()) {
				if (am != null) {
					am.setAtto(atto);
					am.setId(null);
				}
			}
		}
		atto.setDocumentiPdf(null);
		atto.setDocumentiPdfAdozione(null);
		atto.setDocumentiPdfAdozioneOmissis(null);
		atto.setDocumentiPdfOmissis(null);

		if (atto.getAllegati() != null && atto.getAllegati().size() > 0) {
			Set<DocumentoInformatico> allegatiNew = new HashSet<DocumentoInformatico>();
			for (DocumentoInformatico allegato : atto.getAllegati()) {
				// non deve trattarsi di marcatura temporale
				if (allegato.getDocumentoRiferimento() == null && (allegato.getAllegatoProvvedimento() == null
						|| allegato.getAllegatoProvvedimento() == false)) {
					DocumentoInformatico documentoCompletoDB = documentoInformaticoService.findOne(allegato.getId());
					allegato.setAtto(atto);
					allegato.setAvanzamento(null);
					allegato.setId(null);
					allegato.setDocumentoRiferimento(null);
					if (documentoCompletoDB.getFile() != null) {
						/*
						 * Salvo allegato su repository documentale
						 */
						String objectId = "";
						byte[] byteFile = null;
						try {
							it.linksmt.assatti.datalayer.domain.File df = documentoCompletoDB.getFile();
							TipoDocumento tipoDocumento = tipoDocumentoService
									.findByCodice(TipoDocumentoEnum.allegato.name());
							String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, atto.getAoo(),
									atto.getTipoAtto(), atto.getCodiceCifra());
							byteFile = dmsService.getContent(df.getCmisObjectId());
							
							Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(
									atto, tipoDocumento, false);
							DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
							String prefixTimeStamp = dfmt.format(new Date()) + "_";
							objectId = dmsService.save(attoFolderPath, byteFile, 
									prefixTimeStamp + df.getNomeFile(), df.getContentType(), createProps);

							it.linksmt.assatti.datalayer.domain.File file = new it.linksmt.assatti.datalayer.domain.File();
							file.setContentType(documentoCompletoDB.getFile().getContentType());
							// file.setContenuto(documentoCompletoDB.getFile().getContenuto());
							// // TODO non salvare contenuto su blob!
							file.setCmisObjectId(objectId);
							file.setNomeFile(documentoCompletoDB.getFile().getNomeFile());
							file.setSize(documentoCompletoDB.getFile().getSize());
							file.setSha256Checksum(FileChecksum.calcolaImpronta(byteFile));
							fileRepository.save(file);
							allegato.setFile(file);
						}catch (Exception e) {
							throw new GestattiCatchedException(e, e.getMessage());
						}
					}
					if (documentoCompletoDB.getFileomissis() != null) {
						/*
						 * Salvo allegato su repository documentale
						 */
						String objectId = "";
						byte[] bytes = null;
						try {
							it.linksmt.assatti.datalayer.domain.File df = documentoCompletoDB.getFile();
							bytes = dmsService.getContent(df.getCmisObjectId());
							TipoDocumento tipoDocumento = tipoDocumentoService
									.findByCodice(TipoDocumentoEnum.allegato.name());
							String attoFolderPath = serviceUtil.buildDocumentPath(
									tipoDocumento, atto.getAoo(), atto.getTipoAtto(), atto.getCodiceCifra());
							
							Map<String, Object> createProps = dmsMetadataConverter.getPropertiesForCreate(
									atto, tipoDocumento, false);
							DateFormat dfmt = new SimpleDateFormat("yyyyMMddHHmmssSSS");
							String prefixTimeStamp = dfmt.format(new Date()) + "_";
							objectId = dmsService.save(attoFolderPath, bytes, 
									prefixTimeStamp + df.getNomeFile(), df.getContentType(), createProps);

							it.linksmt.assatti.datalayer.domain.File file = new it.linksmt.assatti.datalayer.domain.File();
							file.setContentType(documentoCompletoDB.getFileomissis().getContentType());
							// file.setContenuto(documentoCompletoDB.getFileomissis().getContenuto());//
							// TODO non salvare contenuto su blob!
							file.setCmisObjectId(objectId);
							file.setSha256Checksum(FileChecksum.calcolaImpronta(bytes));
							file.setNomeFile(documentoCompletoDB.getFileomissis().getNomeFile());
							file.setSize(documentoCompletoDB.getFileomissis().getSize());
							fileRepository.save(file);
							allegato.setFileomissis(file);
						}catch (Exception e) {
							throw new GestattiCatchedException(e, e.getMessage());
						}
					}
					allegatiNew.add(allegato);
				}
			}
			atto.setAllegati(allegatiNew);
		}
		atto.setRelatePubblicazione(null);
		if (atto.getDestinatariEsterni() != null) {
			Set<Long> destinatariIds = new HashSet<Long>();
			for (RubricaDestinatarioEsterno destEsterno : atto.getDestinatariEsterni()) {
				destinatariIds.add(destEsterno.getId());
			}
			atto.setDestinatariEsterni(Sets.newHashSet(rubricaDestinatarioEsternoService.findByIds(destinatariIds)));
		}
		atto.setPareri(null);
		atto.setDataAdozione(null);
		atto.setNumeroAdozione(null);
		atto.setDataNumerazione(null);
		if (atto.getNumeroAdozioneAttoRevocato() == null || "".equals(atto.getNumeroAdozioneAttoRevocato())) {
			atto.setNumeroAdozioneAttoRevocato(null);
			atto.setDataAdozioneAttoRevocato(null);
		}
		if (atto.getCodicecifraAttoRevocato() == null || "".equals(atto.getCodicecifraAttoRevocato())) {
			atto.setCodicecifraAttoRevocato(null);
		}
		atto.setInizioPubblicazioneAlbo(null);
		atto.setDataInizioPubblicazionePresunta(null);
		atto.setFinePubblicazioneAlbo(null);
		atto.setDataFinePubblicazionePresunta(null);
		atto.setEsito(null);
		atto.setSedutaGiunta(null);
	}

	// private QualificaProfessionale getQualificaIfDigirente(final Profilo
	// profilo){
	// QualificaProfessionale result = null;
	// String qualifichedirigenziali =
	// WebApplicationProps.getProperty(ConfigPropNames.SOTTOSCRIZIONE_QUALIFICHE_DIRIGENZIALI);
	// if(qualifichedirigenziali!=null && profilo!=null){
	// String [] qualificheDirigenzialiArray =
	// qualifichedirigenziali.split(",");
	//
	// Set<QualificaProfessionale> qualifiche = profilo.getHasQualifica();
	// if(qualifiche!=null){
	// for(QualificaProfessionale qualifica : qualifiche){
	// for(int i = 0; i< qualificheDirigenzialiArray.length; i++){
	// if(qualifica.getDenominazione().equalsIgnoreCase(qualificheDirigenzialiArray[i])){
	// result = qualifica;
	// break;
	// }
	// }
	// if(result != null){
	// break;
	// }
	// }
	// }
	// }
	// return result;
	// }

	@Transactional(readOnly = true)
	public Page<Atto> findAll(final Pageable generatePageRequest) {
		Page<Atto> l = attoRepository.findAll(generatePageRequest);
		for (Atto atto : l) {
			minimalInfoLoad(atto);

		}
		return l;
	}

	private void minimalInfoLoad(final Atto atto) {
		atto.setMotivazione(null);
		atto.setPreambolo(null);
		atto.setDispositivo(null);
		atto.setDomanda(null);
		atto.setDichiarazioni(null);
		atto.setAdempimentiContabili(null);
		atto.setInformazioniAnagraficoContabili(null);
		atto.setGaranzieRiservatezza(null);
		atto.setSottoscrittori(null);
		if (atto.getAvanzamento() != null) {
			for (Avanzamento av : atto.getAvanzamento()) {
				av.setAtto(null);
			}
		}
		if (atto.getDocumentiPdf() != null && atto.getDocumentiPdf().size() > 0) {
			List<DocumentoPdf> docs = new ArrayList<DocumentoPdf>();
			for (DocumentoPdf doc : atto.getDocumentiPdf()) {
				docs.add(new DocumentoPdf(doc.getId()));
			}
			atto.setDocumentiPdf(docs);
		} else {
			atto.setDocumentiPdf(null);
		}
		if (atto.getDocumentiPdfAdozione() != null && atto.getDocumentiPdfAdozione().size() > 0) {
			List<DocumentoPdf> docs = new ArrayList<DocumentoPdf>();
			for (DocumentoPdf doc : atto.getDocumentiPdfAdozione()) {
				docs.add(new DocumentoPdf(doc.getId()));
			}
			atto.setDocumentiPdfAdozione(docs);
		} else {
			atto.setDocumentiPdfAdozione(null);
		}
		if (atto.getDocumentiPdfAdozioneOmissis() == null && atto.getDocumentiPdfAdozioneOmissis().size() > 0) {
			List<DocumentoPdf> docs = new ArrayList<DocumentoPdf>();
			for (DocumentoPdf doc : atto.getDocumentiPdfAdozioneOmissis()) {
				docs.add(new DocumentoPdf(doc.getId()));
			}
			atto.setDocumentiPdfAdozioneOmissis(docs);
		} else {
			atto.setDocumentiPdfAdozioneOmissis(null);
		}
		if (atto.getDocumentiPdfOmissis() == null && atto.getDocumentiPdfOmissis().size() > 0) {
			List<DocumentoPdf> docs = new ArrayList<DocumentoPdf>();
			for (DocumentoPdf doc : atto.getDocumentiPdfOmissis()) {
				docs.add(new DocumentoPdf(doc.getId()));
			}
			atto.setDocumentiPdfOmissis(docs);
		} else {
			atto.setDocumentiPdfOmissis(null);
		}
		// ATTICO: l'area corrisponde con l'Aoo padre, non viene sovrascritta
//		if (atto.getAoo() != null) {
//			atto.setCodiceArea(atto.getAoo().getCodice());
//			atto.setDescrizioneArea(atto.getAoo().getDescrizione());
//		}
		if (atto.getAoo() != null) {
			Aoo simpleAoo = new Aoo();
			simpleAoo.setId(atto.getAoo().getId());
			simpleAoo.setCodice(atto.getAoo().getCodice());
			simpleAoo.setDescrizione((atto.getAoo().getDescrizione()));
			atto.setAoo(simpleAoo);
		}
		atto.setProfilo(null);
		atto.setTipoMateria(null);
		if (atto.getSedutaGiunta() != null) {
			atto.getSedutaGiunta().setOdgs(null);

			atto.getSedutaGiunta().setSedutaGiuntas(null);
			atto.getSedutaGiunta().setSedutariferimento(null);
			if (atto.getSedutaGiunta().getPresidente() != null) {
				atto.getSedutaGiunta().getPresidente().setAoo(null);
			}
			if (atto.getSedutaGiunta().getSegretario() != null) {
				atto.getSedutaGiunta().getSegretario().setAoo(null);
			}

			if (atto.getSedutaGiunta().getVicepresidente() != null) {
				atto.getSedutaGiunta().getVicepresidente().setAoo(null);
			}

			atto.getSedutaGiunta().setDestinatariInterni(null);
			atto.getSedutaGiunta().setResoconto(null);
			atto.getSedutaGiunta().setRubricaSeduta(null);
			atto.getSedutaGiunta().setSottoscrittoreDocAnnullamento(null);
			atto.getSedutaGiunta().setSottoscrittoreDocVariazione(null);
			atto.getSedutaGiunta().setVerbale(null);
		}
	}

	@Transactional(readOnly = true)
	public Page<Atto> findAllLibera(final Pageable generatePageRequest, final RicercaLiberaDTO criteria) {

		System.out.println("Find All libera");
		BooleanExpression expression = null;
		// BooleanExpression tipiAttoExpression = null;
		for (CondizioneRicercaLiberaDTO condizione : criteria.getCampiWhere()) {
			log.debug("CONDIZIONE: {}", condizione);
			if (expression == null) {
				expression = generaExpression(condizione);
				log.debug("EXPRESSION 1: {}", expression);
			} else {
				if (condizione.getRelazioneAltroCampo().equals("and")) {
					expression = expression.and(generaExpression(condizione));
				}
				if (condizione.getRelazioneAltroCampo().equals("or")) {
					expression = expression.or(generaExpression(condizione));
				}
			}
		}

		if ((criteria.getProfiloId() != null) && (serviceUtil.hasRuolo(criteria.getProfiloId(),
				"ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA"))) {
			// Se entro qui ho il ruolo
			// ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA
			// per cui devo vedere per cui devo poter vedere tutti gli atti di
			// cui sono stato
			// sottoscrittore in aggiunta agli atti della mia Aoo.
			log.debug(String.format(
					"sono il profiloId: %s con ruolo: ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA",
					criteria.getProfiloId()));

			BooleanExpression sottoscrittorePredicate = QAtto.atto.sottoscrittori.any().profilo.id
					.eq(criteria.getProfiloId());
			if (criteria.getAooId() != null) {
				BooleanExpression aooPredicate = QAtto.atto.aoo.id.eq(Long.parseLong(criteria.getAooId(), 10));
				sottoscrittorePredicate = sottoscrittorePredicate.and(aooPredicate);
			}

			if (expression != null) {
				expression.and(sottoscrittorePredicate);
			} else {
				expression = sottoscrittorePredicate;
			}

		}

		log.debug("EXPRESSION {}", expression);
		Page<Atto> l = attoRepository.findAll(expression, generatePageRequest);
		log.debug("Lunghezza Pagina:" + l.getSize());
		for (Atto atto : l) {

			minimalInfoLoad(atto);
			log.debug("idAtto:" + atto.getCodiceCifra());
		}
		return l;
	}
	
	private AttoSearchDTO searchAttoToDto(AttoCriteriaDTO criteria, Profilo profilo, Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri, Atto atto, boolean dtoWithAvanz) {
		AttoSearchDTO dto = AttoSearchConverter.toDto(atto, dtoWithAvanz);
		if (!serviceUtil.hasRuolo(criteria.getProfiloId(), RoleCodes.ROLE_SUPERVISORE_CONSULTAZIONE_ATTI)) {
			try {
				for(TipoAttoHasFaseRicerca fase : mappaCriteri.keySet()) {
					if(fase.getFase() == null) {
						continue;
					}
					List<FaseRicercaHasCriterio> esclusivi = this.getCriteriAttivi(mappaCriteri.get(fase).get(TipoRicercaCriterioEnum.ESCLUSIVO));
					if(esclusivi.size() > 0) {
						BooleanExpression p = getBaseFaseCriteria(fase, QAtto.atto);
						p = p.and(QAtto.atto.id.eq(atto.getId()));
						//check fase coerente con l'atto
						if(attoRepository.count(p) > 0L) {
							BooleanExpression check = this.buildCriteriRicercaConfigurabiliByFase(fase, QAtto.atto, mappaCriteri, criteria, profilo, false);
							if(attoRepository.count(check.and(QAtto.atto.id.eq(atto.getId()))) < 1L) {
								for(FaseRicercaHasCriterio esclusivo : esclusivi) {
									if(esclusivo.getCriterio().getCodice().equalsIgnoreCase(CriterioRicercaEnum.ACCESSO_ATTO_NEGATO.name())) {
										dto.setAccessoNegato(true);
									}
									if(esclusivo.getCriterio().getCodice().equalsIgnoreCase(CriterioRicercaEnum.LINK_DOC_OMISSIS.name())) {
										boolean hasFullAccess = this.hasAccessGrant(profilo.getId(), atto.getId(), true);
										if(atto.getDocumentiPdfAdozioneOmissis()!=null && atto.getDocumentiPdfAdozioneOmissis().size() > 0) {
											it.linksmt.assatti.datalayer.domain.File file = atto.getDocumentiPdfAdozioneOmissis().iterator().next().getFile();
											if(file!=null) {
												dto.setOmissisLink("/api/files/" + Base64.encodeBase64String(file.getCmisObjectId().getBytes("UTF-8")).replaceAll("=", "") + "/download");
											}else {
												dto.setOmissisLink("");
											}
										}
										if(atto.getAllegati()!=null && atto.getAllegati().size() > 0) {
											for(DocumentoInformatico allegato : atto.getAllegati()) {
												if(allegato!=null) {
													if(allegato.getFile()!=null && allegato.getFile().getCmisObjectId()!=null && !allegato.getFile().getCmisObjectId().isEmpty()
													) {
														AllegatoSearchDTO allegatoDto = null;
														allegatoDto = new AllegatoSearchDTO();
														allegatoDto.setOmissis(false);
														if(hasFullAccess || (allegato.getPubblicabile()!=null && allegato.getPubblicabile())){
															allegatoDto.setLink("/api/files/" + Base64.encodeBase64String(allegato.getFile().getCmisObjectId().getBytes("UTF-8")).replaceAll("=", "") + "/download");
															allegatoDto.setTitolo(allegato.getTitolo());
															allegatoDto.setFileName(allegato.getFile().getNomeFile());
															allegatoDto.setId(allegato.getFile().getId());
														}else {
															allegatoDto.setTitolo(allegato.getTitolo());
														}
														if(dto.getAllegati()==null) {
															dto.setAllegati(new ArrayList<AllegatoSearchDTO>());
														}
														dto.getAllegati().add(allegatoDto);
													}
													if(allegato.getOmissis()!=null && allegato.getOmissis() && allegato.getFileomissis()!=null && allegato.getFileomissis().getCmisObjectId()!=null && !allegato.getFileomissis().getCmisObjectId().isEmpty()) {
														AllegatoSearchDTO allegatoDto = null;
														allegatoDto = new AllegatoSearchDTO();
														allegatoDto.setTitolo(allegato.getTitolo());
														allegatoDto.setFileName(allegato.getFileomissis().getNomeFile());
														allegatoDto.setId(allegato.getFileomissis().getId());
														allegatoDto.setOmissis(true);
														allegatoDto.setLink("/api/files/" + Base64.encodeBase64String(allegato.getFileomissis().getCmisObjectId().getBytes("UTF-8")).replaceAll("=", "") + "/download");
														if(dto.getAllegati()==null) {
															dto.setAllegati(new ArrayList<AllegatoSearchDTO>());
														}
														dto.getAllegati().add(allegatoDto);
													}
												}
											}
										}
									}
								}
								break;
							}
						}
					}
				}
			} catch (Exception e) {
				dto.setAccessoNegato(true);
				log.error("searchAttoToDto error - attoid " + atto.getId() + " detailerror: " + e.getMessage());
				e.printStackTrace();
			}
		}else {
			dto.setAccessoNegato(false);
			dto.setOmissisLink("");
		}
		return dto;
	}

	@Transactional(readOnly = true)
	public Object findAll(final Pageable generatePageRequest, final AttoCriteriaDTO criteria, boolean dtoResult) throws DatatypeConfigurationException {
		Profilo currentProfilo = profiloService.findOneRicerca(criteria.getProfiloId());
		
		BooleanExpression expression = buildFindAllCriteria(criteria, currentProfilo);
		if (expression == null) {
			return new PageImpl<Atto>(new ArrayList<Atto>(), generatePageRequest, 0);
		}
		BooleanExpression groupExpression = null;
		if(criteria.getAooIdGroup()!=null) {
			if(criteria.getType()!=null && criteria.getType().equalsIgnoreCase("grouped-search")) {
				if(criteria.getAooIdGroup().longValue() > 0L) {
					groupExpression = QAtto.atto.aoo.id.eq(criteria.getAooIdGroup());
				}else if(criteria.getAooIdGroup().equals(0L)) {
					groupExpression = QAtto.atto.aoo.isNull();
				}
			}else {
				if(criteria.getAooIdGroup().longValue() > 0L) {
					Set<Aoo> aooDiscendenti = aooService.findDiscendentiOfAoo(criteria.getAooIdGroup(), true);
					groupExpression = QAtto.atto.aoo.in(aooDiscendenti);
				}else if(criteria.getAooIdGroup().equals(0L)) {
					groupExpression = QAtto.atto.aoo.isNull();
				}
			}
		}

		Page<Atto> lProv = null;
		if (generatePageRequest.getSort().getOrderFor("dataAttivita") != null) {
			Pageable newGeneratePageRequest = PaginationUtil.generatePageRequest(generatePageRequest.getOffset(),
					generatePageRequest.getPageSize(), new QSort(serviceUtil.toOrderSpecifiers(Avanzamento.class,
							"avanzamento", generatePageRequest.getSort())));

			BooleanExpression avanzamentoExpr = 
						QAvanzamento.avanzamento.stato.equalsIgnoreCase(
								statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.C.name()))
							.or(
						QAvanzamento.avanzamento.stato.equalsIgnoreCase(
								statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.G.name())));
			
			expression = expression.and(avanzamentoExpr);
			if(groupExpression==null) {
				lProv = attoRepository.findAllInnerJoinAvanzamenti(expression, newGeneratePageRequest);
			}else {
				lProv = attoRepository.findAllInnerJoinAvanzamenti(expression.and(groupExpression), newGeneratePageRequest);
			}
		}
		else {
			if(groupExpression==null) {
				lProv = attoRepository.findAll(expression, generatePageRequest);
			}else {
				lProv = attoRepository.findAll(expression.and(groupExpression), generatePageRequest);
			}
		}

		log.debug("Lunghezza Pagina:" + lProv.getSize());
	
		if (dtoResult && (criteria.getViewtype().equals("itinere") || criteria.getViewtype().equals("conclusi")) && criteria.getCodiceTipoAtto()!=null && !criteria.getCodiceTipoAtto().isEmpty()) {
			TipoAtto tipoAtto = tipoAttoRepository.findByCodice(criteria.getCodiceTipoAtto());
			Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri = this.ottieniCriteri(tipoAtto, criteria.getViewtype().equals("itinere"));
			List<AttoSearchDTO> list = new ArrayList<AttoSearchDTO>();
			for(Atto atto : lProv) {
				AttoSearchDTO dto = this.searchAttoToDto(criteria, currentProfilo, mappaCriteri, atto, criteria.getViewtype().equals("itinere"));
				list.add(dto);
			}
			if(criteria.getType()!=null && criteria.getType().equalsIgnoreCase("grouped-search")) {
				if(criteria.getAooIdGroup()!=null) {
					Map<String, Object> map = new HashMap<String, Object>();
					List<AooGroupDto> aoos = new ArrayList<AooGroupDto>();
					map.put("aoos", aoos);
					if(criteria.getAooIdGroup().longValue() > 0L) {
						List<Aoo> figlieDirette = new ArrayList<Aoo>();
						if(criteria.getUfficiDisattivati()==null || !criteria.getUfficiDisattivati().booleanValue()) {
							figlieDirette = aooService.findFiglieDiretteOfAoo(criteria.getAooIdGroup());
						}else {
							figlieDirette = aooService.findAllFiglieDiretteOfAoo(criteria.getAooIdGroup());
						}
						for(Aoo aoo : figlieDirette) {
							Set<Aoo> aooDiscendenti;
							if(criteria.getUfficiDisattivati()==null || !criteria.getUfficiDisattivati().booleanValue()) {
								aooDiscendenti = aooService.findDiscendentiOfAoo(aoo.getId(), true);
							}else {
								aooDiscendenti = aooService.findAllDiscendentiOfAoo(aoo.getId(), true);
							}
							
							Long count = attoRepository.count(expression.and(QAtto.atto.aoo.in(aooDiscendenti)));
							if(count!=null && count.longValue() > 0) {
								AooGroupDto dto = new AooGroupDto(aoo.getId(), aoo.getCodice(), aoo.getDescrizione(), count);
								dto.setDisabilitata(aoo.getValidita()!=null && aoo.getValidita().getValidoal()!=null);
								aoos.add(dto);
							}
						}
						
						map.put("attos", new PageImpl<AttoSearchDTO>(list, generatePageRequest, lProv.getTotalElements()));
						map.put("ufficioId",  criteria.getAooIdGroup());
						AooBasicDto aooGroup = aooService.getBasicAoo(criteria.getAooIdGroup());
						map.put("descrizione", aooGroup.getCodice() + " - " + aooGroup.getDescrizione());
					}else {
						map.put("attos", new PageImpl<AttoSearchDTO>(list, generatePageRequest, lProv.getTotalElements()));
					}
					
					return map;
				}
				return new PageImpl<Atto>(new ArrayList<Atto>(), generatePageRequest, 0);
			}else {
				return new PageImpl<AttoSearchDTO>(list, generatePageRequest, lProv.getTotalElements());
			}
		}else {
			int eliminati = 0;	
			List<Atto> list = new ArrayList<Atto>();
			
			for (Atto atto : lProv) {
				if(atto.getTipoAtto() != null) {
					atto.setTipoAtto(new TipoAtto(atto.getTipoAtto().getId(), atto.getTipoAtto().getCodice(), atto.getTipoAtto().getDescrizione()));
				}
				if(atto.getProfilo()!=null) {
					atto.setProfilo(new Profilo(atto.getProfilo().getId()));
				}
				if(atto.getTipoIter()!=null && atto.getTipoIter().getTipoAtto()!=null) {
					atto.getTipoIter().setTipoAtto(null);
				}
				boolean okAssenti = true;
				boolean okNumAdozione = true;
				
				if (atto.getSedutaGiunta() != null) {
					SedutaGiunta seduta = new SedutaGiunta(atto.getSedutaGiunta().getId(),
							atto.getSedutaGiunta().getLuogo(), atto.getSedutaGiunta().getDataOra());
					seduta.setPrimaConvocazioneInizio(atto.getSedutaGiunta().getPrimaConvocazioneInizio());
					if (atto.getSedutaGiunta().getSecondaConvocazioneInizio() != null)
						seduta.setSecondaConvocazioneInizio(atto.getSedutaGiunta().getSecondaConvocazioneInizio());
					seduta.setTipoSeduta(atto.getSedutaGiunta().getTipoSeduta());
					seduta.setNumero(atto.getSedutaGiunta().getNumero());
					atto.setSedutaGiunta(seduta);
				}
				if (criteria.getViewtype().equals("elencopersedute")) {
	
					boolean daValorizzato = criteria.getNumeroAdozioneDa() != null
							&& !"".equals(criteria.getNumeroAdozioneDa());
					boolean aValorizzato = criteria.getNumeroAdozioneA() != null
							&& !"".equals(criteria.getNumeroAdozioneA());
					int numeroAdozioneDa = -1;
					int numeroAdozioneA = -1;
					if (daValorizzato) {
						try {
							numeroAdozioneDa = Integer.parseInt(criteria.getNumeroAdozioneDa());
						} catch (Exception e) {
	
						}
					}
	
					if (aValorizzato) {
						try {
							numeroAdozioneA = Integer.parseInt(criteria.getNumeroAdozioneA());
						} catch (Exception e) {
	
						}
					}
	
					String assenti = "";
					for (AttiOdg attoOdg : atto.getOrdineGiornos()) {
						if (attoOdg.getEsito() != null && atto.getEsito() != null
								&& attoOdg.getEsito().equals(atto.getEsito())) {
							for (ComponentiGiunta comp : attoOdg.getComponenti()) {
								if (comp.getPresente() == false) {
									if (!assenti.equals(""))
										assenti += ", ";
									assenti += comp.getProfilo().getUtente().getCognome() + " "
											+ comp.getProfilo().getUtente().getNome();
								}
							}
						}
					}
					atto.setAssenti(assenti);
					atto.setOrdineGiornos(null);
	
					if (criteria.getAssenti() != null) {
						okAssenti = assenti.toLowerCase().contains(criteria.getAssenti().toLowerCase());
					}
	
					int numeroAdozione = -1;
					try {
						if ((atto.getNumeroAdozione() == null || atto.getNumeroAdozione().isEmpty())
								&& (numeroAdozioneDa > -1 || numeroAdozioneA > -1)) {
							okNumAdozione = false;
						} 
						else {
							numeroAdozione = Integer.parseInt(atto.getNumeroAdozione());
							if (numeroAdozioneDa > -1 && numeroAdozioneA > -1) {
								okNumAdozione = numeroAdozione >= numeroAdozioneDa && numeroAdozione <= numeroAdozioneA;
							}
							else if (numeroAdozioneDa > -1) {
								okNumAdozione = numeroAdozione >= numeroAdozioneDa;
							} 
							else if (numeroAdozioneA > -1) {
								okNumAdozione = numeroAdozione <= numeroAdozioneA;
							}
						}
					}
					catch (Exception e) {
						// TODO: handle exception
					}
				}
				
				if (okAssenti && okNumAdozione) {
					list.add(atto);
				}
				else {
					eliminati++;
				}
				log.debug("Atto:" + atto.getId().toString() + " - " + atto.getId());
			}
			
			return new PageImpl<Atto>(list, generatePageRequest, lProv.getTotalElements() - eliminati);
		}
	}

	@SuppressWarnings({ "unlikely-arg-type", "unused" })
	private BooleanExpression buildFindAllCriteria(final AttoCriteriaDTO criteria, final Profilo currentProfilo) throws DatatypeConfigurationException {
		QAtto qAtto = QAtto.atto;
		BooleanExpression expression = qAtto.id.isNotNull();

		if(criteria.getProfiloId()==null) {
			return qAtto.id.isNull();
		}
		
		if (criteria.getIstruttore() != null && !criteria.getIstruttore().isEmpty()) {
			expression = expression.and(qAtto.createdBy.containsIgnoreCase(criteria.getIstruttore()));
		}

		if ((criteria.getLastModifiedDateDa() != null && !"".equals(criteria.getLastModifiedDateDa()))
				&& (criteria.getLastModifiedDateA() != null && !"".equals(criteria.getLastModifiedDateA()))) {
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(criteria.getLastModifiedDateDa().toDate());
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);
			expression = expression.and(qAtto.lastModifiedDate.goe(new DateTime(calStart.getTime())));

			Calendar calEnd = new GregorianCalendar();
			calEnd.setTime(criteria.getLastModifiedDateA().toDate());
			calEnd.set(Calendar.HOUR_OF_DAY, 23);
			calEnd.set(Calendar.MINUTE, 59);
			calEnd.set(Calendar.SECOND, 59);
			expression = expression.and(qAtto.lastModifiedDate.loe(new DateTime(calEnd.getTime())));
		}
		else {
			if (criteria.getLastModifiedDateDa() != null && !"".equals(criteria.getLastModifiedDateDa())) {
				Calendar calStart = new GregorianCalendar();
				calStart.setTime(criteria.getLastModifiedDateDa().toDate());
				calStart.set(Calendar.HOUR_OF_DAY, 0);
				calStart.set(Calendar.MINUTE, 0);
				calStart.set(Calendar.SECOND, 0);
				expression = expression.and(qAtto.lastModifiedDate.goe(new DateTime(calStart.getTime())));
			}
			if (criteria.getLastModifiedDateA() != null && !"".equals(criteria.getLastModifiedDateA())) {
				Calendar calEnd = new GregorianCalendar();
				calEnd.setTime(criteria.getLastModifiedDateA().toDate());
				calEnd.set(Calendar.HOUR_OF_DAY, 23);
				calEnd.set(Calendar.MINUTE, 59);
				calEnd.set(Calendar.SECOND, 59);
				expression = expression.and(qAtto.lastModifiedDate.loe(new DateTime(calEnd.getTime())));
			}
		}

		/*
		 * PER ATTICO NON DOVREBBE AVERE SENSO
		 *
		if ((criteria.getProfiloId() != null) && (serviceUtil.hasRuolo(criteria.getProfiloId(),
				"ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA"))) {
			// Se entro qui ho il ruolo
			// ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA
			// per cui devo vedere per cui devo poter vedere tutti gli atti di
			// cui sono stato
			// sottoscrittore in aggiunta agli atti della mia Aoo.
			log.debug(String.format(
					"sono il profiloId: %s con ruolo: ROLE_SOTTOSCRITTORE_PROPOSTA_CONSULTAZIONE_ATTI_GIUNTA con view type: %s",
					criteria.getProfiloId(), criteria.getViewtype()));

			BooleanExpression sottoscrittorePredicate = QAtto.atto.sottoscrittori.any().profilo.id
					.eq(criteria.getProfiloId());

			if (criteria.getAooId() != null) {
				BooleanExpression aooPredicate = QAtto.atto.aoo.id.eq(Long.parseLong(criteria.getAooId(), 10));

				if (criteria.getViewtype().equals("tutti")) {
					BooleanExpression statoPredicate = QAtto.atto.aoo.id.eq(Long.parseLong(criteria.getAooId(), 10));
					for (String stato : StatoAttoEnum.descrizioneConclusi()) {
						statoPredicate = statoPredicate.or(QAtto.atto.stato.equalsIgnoreCase(stato));
					}
					expression = expression.and(statoPredicate.or(sottoscrittorePredicate));
				} else if (!criteria.getViewtype().equals("pubblicati")) {
					expression = expression.and(aooPredicate.or(sottoscrittorePredicate));
				}
			} else {
				if (!criteria.getViewtype().equals("pubblicati")) {
					expression = expression.or(sottoscrittorePredicate);
				}
			}
		}
		*/		

		log.debug("criteria.getDataIncarico():{}", criteria.getDataIncarico());
		if ((criteria.getIncaricoa() != null && !"".equals(criteria.getIncaricoa())) || (criteria.getDataIncarico() != null)) {
			List<Utente> utenti = null;
			JsonObject search = null;
			boolean ricercaFallita = false;
			if (criteria.getIncaricoa() != null && !"".equals(criteria.getIncaricoa())) {
				try {
					utenti = utenteService.findByNomeCognome(criteria.getIncaricoa());
					if(utenti!=null && !utenti.isEmpty()) {
						search = new JsonObject();
						JsonArray ja = new JsonArray();
						for(Utente utente : utenti) {
							ja.add(new JsonPrimitive(utente.getUsername()));
						}
						search.add("assegnatari", ja);
					}else {
						ricercaFallita = true;
					}
				} catch (Exception exp) {
					log.error(exp.getMessage());
				}
			}
			if(!ricercaFallita) {
				if(criteria.getDataIncarico()!=null) {
					DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
					String data = df.format(criteria.getDataIncarico().toDate());
					if(search==null) {
						search = new JsonObject();
					}
					search.addProperty("data_task_start", data + " 00:00:00");
					search.addProperty("data_task_end", data + " 23:59:59");
				}
	
				Set<Long> attoIds = null;
				if(search!=null) {
					attoIds = workflowService.getAttoIdsTaskItinere(search);
					if(attoIds!=null && attoIds.size() > 0) {
						expression = expression.and(qAtto.id.in(attoIds));
					}else {
						expression = qAtto.id.isNull();
					}
				}else {
					expression = qAtto.id.isNull();
				}
			}else {
				expression = qAtto.id.isNull();
			}
		}

		BooleanExpression tipiAttoExpression = null;
		if(criteria.getTipiAttoIds()!=null) {
			for (Long idTipoAtto : criteria.getTipiAttoIds()) {
				if (tipiAttoExpression == null) {
					tipiAttoExpression = qAtto.tipoAtto.id.eq(idTipoAtto);
				} else {
					tipiAttoExpression = tipiAttoExpression.or(qAtto.tipoAtto.id.eq(idTipoAtto));
				}
			}
		}
		if (tipiAttoExpression != null) {
			expression = expression.and(tipiAttoExpression);
		}

		if (criteria.getArea() != null && !"".equals(criteria.getArea())) {
			BooleanExpression internalPredicate = ((qAtto.aoo.codice.concat(" - ")
					.concat(qAtto.aoo.descrizione)).containsIgnoreCase(criteria.getArea()))
							.or((qAtto.aoo.codice.concat("-").concat(qAtto.aoo.descrizione))
									.containsIgnoreCase(criteria.getArea()));
			if (internalPredicate != null) {
				expression = expression.and(internalPredicate);
			}
		}

		/*
		if (criteria.getViewtype().equals("elencopersedute")) {
			expression = expression.and(qAtto.sedutaGiunta.isNotNull());

			// MODIFICA ISSUE 22

			if ((criteria.getLastModifiedDateDa() != null && !"".equals(criteria.getLastModifiedDateDa()))
					&& (criteria.getLastModifiedDateA() != null && !"".equals(criteria.getLastModifiedDateA()))) {
				Calendar calStart = new GregorianCalendar();
				calStart.setTime(criteria.getLastModifiedDateDa().toDate());
				calStart.set(Calendar.HOUR_OF_DAY, 0);
				calStart.set(Calendar.MINUTE, 0);
				calStart.set(Calendar.SECOND, 0);
				expression = expression.and(qAtto.lastModifiedDate.goe(new DateTime(calStart.getTime())));

				Calendar calEnd = new GregorianCalendar();
				calEnd.setTime(criteria.getLastModifiedDateA().toDate());
				calEnd.set(Calendar.HOUR_OF_DAY, 23);
				calEnd.set(Calendar.MINUTE, 59);
				calEnd.set(Calendar.SECOND, 59);
				expression = expression.and(qAtto.lastModifiedDate.loe(new DateTime(calEnd.getTime())));
			}

			if (criteria.getInizioDataSeduta() != null && !"".equals(criteria.getInizioDataSeduta())) {
				Calendar calStart = new GregorianCalendar();
				calStart.setTime(criteria.getInizioDataSeduta().toDate());
				calStart.set(Calendar.HOUR_OF_DAY, 0);
				calStart.set(Calendar.MINUTE, 0);
				calStart.set(Calendar.SECOND, 0);
				expression = expression
						.and(qAtto.sedutaGiunta.primaConvocazioneInizio.goe(new DateTime(calStart.getTime())));
			}

			if (criteria.getFineDataSeduta() != null && !"".equals(criteria.getFineDataSeduta())) {
				Calendar calStart = new GregorianCalendar();
				calStart.setTime(criteria.getFineDataSeduta().toDate());
				calStart.set(Calendar.HOUR_OF_DAY, 23);
				calStart.set(Calendar.MINUTE, 59);
				calStart.set(Calendar.SECOND, 59);
				expression = expression
						.and(qAtto.sedutaGiunta.primaConvocazioneInizio.loe(new DateTime(calStart.getTime())));
			}

			if (criteria.getEsitoSeduta() != null && !"".equals(criteria.getEsitoSeduta())) {
				expression = expression.and(qAtto.esito
						.equalsIgnoreCase(criteria.getEsitoSeduta().replaceAll(" ", "_").replace("'", "_")));
			}

			if (criteria.getLuogoSeduta() != null && !"".equals(criteria.getLuogoSeduta())) {
				expression = expression
						.and(qAtto.sedutaGiunta.luogo.containsIgnoreCase(criteria.getLuogoSeduta()));
			}
			
			// if(criteria.getRelatore()!= null &&
			// !"".equals(criteria.getRelatore())){
			// expression =
			// expression.and(QAtto.atto.denominazioneRelatore.containsIgnoreCase(criteria.getRelatore()));
			// }
		}
		*/
		
		TipoAtto tipoAtto = null;
		if(criteria.getCodiceTipoAtto()!=null && !criteria.getCodiceTipoAtto().isEmpty()){
			tipoAtto = tipoAttoRepository.findByCodice(criteria.getCodiceTipoAtto());
			
			if(tipoAtto != null){
				expression = expression.and(qAtto.tipoAtto.id.eq(tipoAtto.getId()));
				
				if (criteria.getViewtype().equals("itinere") ||
//					criteria.getViewtype().equals("tutti")   ||
					criteria.getViewtype().equals("conclusi")) {
					
					String[] statiConclusi = null;
					if(tipoAtto.getStatoConclusoRicerca()!=null) {
						statiConclusi = tipoAtto.getStatoConclusoRicerca().split("\\|");
					}else {
						statiConclusi = new String[] {};
					}
					List<String> statiConclusiList = new ArrayList<String>();
					boolean conclusoConEsecutivita = false;
					boolean conclusoConAttesaRelata = false;
					for(int i = 0; i < statiConclusi.length; i++) {
						if(statiConclusi[i].equalsIgnoreCase(StatoConclusoEnum.DATA_ESECUTIVITA.getCodice())) {
							conclusoConEsecutivita = true;
						}else if(statiConclusi[i].equalsIgnoreCase(StatoConclusoEnum.ATTESA_RELATA.getCodice())) {
							conclusoConAttesaRelata = true;
							statiConclusiList.add(statiConclusi[i]);
						}else {
							statiConclusiList.add(statiConclusi[i]);
						}
					}
					
					if ((criteria.getProfiloId() == null) || (criteria.getProfiloId().longValue() < 1)) {
						return null;
					}
					BooleanExpression itinereExpress = null;
					BooleanExpression conclusiExpress = null;
//					List<Long> listItinere = workflowService.getIdsAttiItinere(criteria.getCodiceTipoAtto());
					if (criteria.getViewtype().equals("itinere") 
							// || criteria.getViewtype().equals("tutti")
					) {
						if(conclusoConAttesaRelata || conclusoConEsecutivita) {
							if(conclusoConAttesaRelata && conclusoConEsecutivita) {
								itinereExpress = qAtto.inizioPubblicazioneAlbo.isNull().and(qAtto.dataEsecutivita.isNull()).and(qAtto.fineIterDate.isNull());
							}else {
								if(conclusoConEsecutivita) {
									itinereExpress = qAtto.dataEsecutivita.isNull().and(qAtto.fineIterDate.isNull());
								}else if(conclusoConAttesaRelata) {
									itinereExpress = qAtto.inizioPubblicazioneAlbo.isNull().and(qAtto.fineIterDate.isNull());
								}
							}
						}else {
							itinereExpress = qAtto.fineIterDate.isNull();
						}
					}else if(criteria.getViewtype().equals("conclusi")) {
						if(conclusoConAttesaRelata || conclusoConEsecutivita) {
							if(conclusoConAttesaRelata && conclusoConEsecutivita) {
								conclusiExpress = qAtto.inizioPubblicazioneAlbo.isNotNull().or(qAtto.dataEsecutivita.isNotNull()).or(qAtto.fineiterTipo.in(statiConclusiList));
							}else {
								if(conclusoConEsecutivita) {
									conclusiExpress = qAtto.dataEsecutivita.isNotNull().or(qAtto.fineiterTipo.in(statiConclusiList));
								}else if(conclusoConAttesaRelata) {
									conclusiExpress = qAtto.inizioPubblicazioneAlbo.isNotNull().or(qAtto.fineiterTipo.in(statiConclusiList));
								}
							}
						}else {
							conclusiExpress = qAtto.fineiterTipo.in(statiConclusiList);
						}
					}
					
					if (criteria.getViewtype().equals("itinere")) {
						if(itinereExpress!=null) {
							expression = expression.and(itinereExpress);
						}
					}else if(criteria.getViewtype().equals("conclusi")) {
							expression = expression.and(conclusiExpress);
					}
					/*else if(criteria.getViewtype().equals("tutti")) {
						if ( (listItinere != null) && (!listItinere.isEmpty())) {
							expression = expression.and(QAtto.atto.id.notIn(listItinere).or(itinereExpress));
						}
					}*/
					
					
					if (!serviceUtil.hasRuolo(criteria.getProfiloId(), RoleCodes.ROLE_SUPERVISORE_CONSULTAZIONE_ATTI)) {
						Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri = ottieniCriteri(tipoAtto, criteria.getViewtype().equals("itinere"));
						BooleanExpression criteriByFaseExpression = this.buildCriteriRicercaConfigurabili(qAtto, mappaCriteri, criteria, currentProfilo);
						if(criteriByFaseExpression!=null) {
							if(expression!=null) {
								expression = expression.and(criteriByFaseExpression);
							}else {
								expression = criteriByFaseExpression;
							}
						}
					}
				}else {
					expression = qAtto.id.eq(-1L);
				}
			}
		}
		if(criteria.getIdAttoFilterType()!=null) {
			expression = expression.and(qAtto.tipoAtto.id.eq(attoRepository.getTipoAtto(criteria.getIdAttoFilterType())));
		}
		if(criteria.getEscludiRevocati()!=null && criteria.getEscludiRevocati().equals(true)) {
			expression = expression.and(qAtto.stato.notEqualsIgnoreCase(StatoAttoEnum.REVOCATO.getDescrizione()));
		}
		if(criteria.getFineIterType()!=null && !criteria.getFineIterType().trim().isEmpty()) {
			expression = expression.and(qAtto.fineiterTipo.equalsIgnoreCase(criteria.getFineIterType()));
		}
		if (criteria.getStato() != null && !"".equals(criteria.getStato())) {
			if (!criteria.getStato().contains(";") || criteria.getStato().split(";").length < 2) {
				expression = expression.and(qAtto.stato.equalsIgnoreCase(criteria.getStato().split(";")[0]));
			} else {
				String stati[] = criteria.getStato().split(";");
				BooleanExpression internalExp = null;
				for (int i = 0; i < stati.length; i++) {
					if (i == 0) {
						internalExp = qAtto.stato.equalsIgnoreCase(stati[i]);
					} else {
						internalExp = internalExp.or(qAtto.stato.equalsIgnoreCase(stati[i]));
					}
				}
				expression = expression.and(internalExp);
			}
		}

		if (criteria.getRegolamento() != null && !"".equals(criteria.getRegolamento())) {
			expression = expression.and(qAtto.regolamento.equalsIgnoreCase(criteria.getRegolamento()));
		}

		if (criteria.getAttoRevocato() != null && !"".equals(criteria.getAttoRevocato())) {
			expression = expression
					.and(qAtto.codicecifraAttoRevocato.containsIgnoreCase(criteria.getAttoRevocato()));
		}
		if (criteria.getEsitoSeduta() != null && !"".equals(criteria.getEsitoSeduta())) {
			expression = expression.and(qAtto.esito
					.equalsIgnoreCase(criteria.getEsitoSeduta().replaceAll(" ", "_").replace("'", "_")));
		}
		if (criteria.getEstremiSeduta() != null && !"".equals(criteria.getEstremiSeduta())) {
			// expression = expression.and(
			// QAtto.atto.esito.equalsIgnoreCase(criteria.getEsitoSeduta() ) );
		}

		/*
		if (criteria.getTipoSeduta() != null) {
			expression = expression.and(qAtto.sedutaGiunta.tipoSeduta.eq(criteria.getTipoSeduta().id));
		}

		if (criteria.getNumeroSeduta() != null && !"".equals(criteria.getNumeroSeduta())) {
			expression = expression.and(qAtto.sedutaGiunta.numero.eq(criteria.getNumeroSeduta()));
		}

		if (criteria.getInizioDataSeduta() != null && !"".equals(criteria.getInizioDataSeduta())) {
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(criteria.getInizioDataSeduta().toDate());
			calStart.set(Calendar.HOUR_OF_DAY, 0);
			calStart.set(Calendar.MINUTE, 0);
			calStart.set(Calendar.SECOND, 0);

			BooleanExpression temp = qAtto.sedutaGiunta.secondaConvocazioneInizio.isNotNull()
					.and(qAtto.sedutaGiunta.secondaConvocazioneInizio.goe(new DateTime(calStart.getTime())));
			BooleanExpression temp2 = qAtto.sedutaGiunta.secondaConvocazioneInizio.isNull()
					.and(qAtto.sedutaGiunta.primaConvocazioneInizio.goe(new DateTime(calStart.getTime())));

			expression = expression.and(temp.or(temp2));
		}

		if (criteria.getFineDataSeduta() != null && !"".equals(criteria.getFineDataSeduta())) {
			Calendar calStart = new GregorianCalendar();
			calStart.setTime(criteria.getFineDataSeduta().toDate());
			calStart.set(Calendar.HOUR_OF_DAY, 23);
			calStart.set(Calendar.MINUTE, 59);
			calStart.set(Calendar.SECOND, 59);
			BooleanExpression temp = qAtto.sedutaGiunta.secondaConvocazioneInizio.isNotNull()
					.and(qAtto.sedutaGiunta.secondaConvocazioneInizio.loe(new DateTime(calStart.getTime())));
			BooleanExpression temp2 = qAtto.sedutaGiunta.secondaConvocazioneInizio.isNull()
					.and(qAtto.sedutaGiunta.primaConvocazioneInizio.loe(new DateTime(calStart.getTime())));

			expression = expression.and(temp.or(temp2));
		}
		*/

		if (criteria.getNumeroAdozione() != null && !"".equals(criteria.getNumeroAdozione())) {
			expression = expression.and(qAtto.numeroAdozione.containsIgnoreCase(criteria.getNumeroAdozione()));
		}

		if (criteria.getCodiceCifra() != null && !"".equals(criteria.getCodiceCifra())) {
			expression = expression.and(qAtto.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()));
		}
		
		if (criteria.getCodiceCup() != null && !"".equals(criteria.getCodiceCup())) {
			expression = expression.and(qAtto.codiceCup.containsIgnoreCase(criteria.getCodiceCup()));
		}
		
		if (criteria.getCodiceCig() != null && !"".equals(criteria.getCodiceCig())) {
			expression = expression.and(qAtto.codiceCig.containsIgnoreCase(criteria.getCodiceCig()));
		}
		
		if (criteria.getAnno() != null && !"".equals(criteria.getAnno())) {
			expression = expression.and(qAtto.dataCreazione.year().eq(Integer.parseInt(criteria.getAnno())));
		}

		if (criteria.getDataCreazione() != null && !"".equals(criteria.getDataCreazione())) {
			expression = expression.and(qAtto.dataCreazione.goe(criteria.getDataCreazione()));
			expression = expression.and(qAtto.dataCreazione.loe(criteria.getDataCreazione()));
		}
		if (criteria.getDataCreazioneDa() != null && !"".equals(criteria.getDataCreazioneDa())) {
			expression = expression.and(qAtto.dataCreazione.goe(criteria.getDataCreazioneDa()));
		}
		if (criteria.getDataCreazioneA() != null && !"".equals(criteria.getDataCreazioneA())) {
			expression = expression.and(qAtto.dataCreazione.loe(criteria.getDataCreazioneA()));
		}

		if ((criteria.getDataAdozione() != null && !"".equals(criteria.getDataAdozione()))
				&& (criteria.getDataAdozioneA() != null && !"".equals(criteria.getDataAdozioneA()))) {
			expression = expression.and(qAtto.dataAdozione.goe(criteria.getDataAdozione()));
			expression = expression.and(qAtto.dataAdozione.loe(criteria.getDataAdozioneA()));
		} else {
			if (criteria.getDataAdozione() != null && !"".equals(criteria.getDataAdozione())) {
				expression = expression.and(qAtto.dataAdozione.goe(criteria.getDataAdozione()));
			}

			if (criteria.getDataAdozioneA() != null && !"".equals(criteria.getDataAdozioneA())) {
				expression = expression.and(qAtto.dataAdozione.loe(criteria.getDataAdozioneA()));
			}
		}

		if ((criteria.getDataEsecutiva() != null && !"".equals(criteria.getDataEsecutiva()))
				&& (criteria.getDataAdozione() == null || "".equals(criteria.getDataAdozione()))) {
			expression = expression.and(qAtto.dataEsecutivita.eq(criteria.getDataEsecutiva()));
		}

		if (criteria.getOggetto() != null && !"".equals(criteria.getOggetto())) {
			expression = expression.and(qAtto.oggetto.containsIgnoreCase(criteria.getOggetto()));
		}

		if (criteria.getTipoAtto() != null && !"".equals(criteria.getTipoAtto())) {
			expression = expression.and(qAtto.tipoAtto.codice.equalsIgnoreCase(criteria.getTipoAtto()));
		}

		if (criteria.getTipiIterForzati() != null && criteria.getTipiIterForzati().size() > 0) {
			BooleanExpression iterForzati = null;
			for (String tipoIter : criteria.getTipiIterForzati()) {
				if (iterForzati == null) {
					iterForzati = qAtto.tipoIter.descrizione.equalsIgnoreCase(tipoIter);
				} else {
					iterForzati = iterForzati.or(qAtto.tipoIter.descrizione.equalsIgnoreCase(tipoIter));
				}
			}
			expression = expression.and(iterForzati);
		}

		if (criteria.getTipoIter() != null && !"".equals(criteria.getTipoIter().trim())) {
			expression = expression.and(qAtto.tipoIter.descrizione.equalsIgnoreCase(criteria.getTipoIter()));
		}
		
		if (criteria.getTipoFinanziamento() != null && !"".equals(criteria.getTipoFinanziamento().trim())) {
			expression = expression.and(qAtto.hasTipoFinanziamenti.any().descrizione.equalsIgnoreCase(criteria.getTipoFinanziamento()));
		}

		if (criteria.getTaskStato() != null && !"".equals(criteria.getTaskStato())) {
			expression = expression.and(qAtto.stato.equalsIgnoreCase(criteria.getTaskStato()));
		}
		if (criteria.getIdOdg() != null) {
			expression = expression.and(qAtto.ordineGiornos.any().id.eq(criteria.getIdOdg()));
		}

		if ((criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo()))
				&& (criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo()))) {
			expression = expression.and(qAtto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo()));
			expression = expression.and(qAtto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()));
		} else {
			if (criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo())) {
				expression = expression
						.and(qAtto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo()));
			}

			if (criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo())) {
				expression = expression.and(qAtto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()));
			}
		}
		return expression;
	}
	
	private Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> ottieniCriteri(TipoAtto tipoAtto, boolean ricercaItinere){
		Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> map = new HashMap<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>>();
		
		if(tipoAtto.getFasiRicerca()!=null) {
			for(TipoAttoHasFaseRicerca fase : tipoAtto.getFasiRicerca()) {
				if(fase.getFase() == null 
						|| (fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.ITINERE.name()) && !ricercaItinere) 
						|| (!fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.ITINERE.name()) && ricercaItinere)) {
					continue;
				}
				if(!map.containsKey(fase)) {
					map.put(fase, new HashMap<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>());
				}
				for(FaseRicercaHasCriterio c : fase.getCriteri()) {
					CriterioRicerca criterio = c.getCriterio();
					if(criterio.getTipo().equalsIgnoreCase(TipoRicercaCriterioEnum.INCLUSIVO.getCodice())) {
						if(!map.get(fase).containsKey(TipoRicercaCriterioEnum.INCLUSIVO)) {
							map.get(fase).put(TipoRicercaCriterioEnum.INCLUSIVO, new ArrayList<FaseRicercaHasCriterio>());
						}
						map.get(fase).get(TipoRicercaCriterioEnum.INCLUSIVO).add(c);
					}else if(criterio.getTipo().equalsIgnoreCase(TipoRicercaCriterioEnum.ESCLUSIVO.getCodice())) {
						if(!map.get(fase).containsKey(TipoRicercaCriterioEnum.ESCLUSIVO)) {
							map.get(fase).put(TipoRicercaCriterioEnum.ESCLUSIVO, new ArrayList<FaseRicercaHasCriterio>());
						}
						map.get(fase).get(TipoRicercaCriterioEnum.ESCLUSIVO).add(c);
					}
				}
			}
		}
		return map;
	}
	
	private BooleanExpression getBaseFaseCriteria(TipoAttoHasFaseRicerca fase, QAtto qAtto) {
		BooleanExpression faseExp = null;
		if(fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.ITINERE.name())) {
			faseExp = qAtto.fineIterDate.isNull();
		}else if(fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.CONCLUSO_NO_RISERV_NO_PUB_INTEG.name())) {
			faseExp = qAtto.riservato.eq(false).and(qAtto.pubblicazioneIntegrale.eq(false));
		}else if(fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.CONCLUSO_RISERVATO.name())) {
			faseExp = qAtto.riservato.eq(true).or(qAtto.riservato.isNull());
		}else if(fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.CONCLUSO_NO_RISERV_SI_PUB_INTEG.name())) {
			faseExp = qAtto.riservato.eq(false).and(qAtto.pubblicazioneIntegrale.eq(true));
		}else {
			faseExp = qAtto.id.eq(-1L);
		}
		return faseExp;
	}
	
	private List<FaseRicercaHasCriterio> getCriteriAttivi(List<FaseRicercaHasCriterio> criteri){
		List<FaseRicercaHasCriterio> list = new ArrayList<FaseRicercaHasCriterio>();
		if(criteri!=null) {
			for(FaseRicercaHasCriterio c : criteri) {
				if(c.getValue()!=null && c.getValue().equals(true)) {
					list.add(c);
				}
			}
		}
		return list;
	}
	
	private BooleanExpression buildCriteriRicercaConfigurabili(QAtto qAtto, Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri, AttoCriteriaDTO criteria, Profilo profilo) {
		BooleanExpression returnedExpression = qAtto.id.eq(-1L);
		List<BooleanExpression> expressionFasi = new ArrayList<BooleanExpression>();
		for(TipoAttoHasFaseRicerca fase : mappaCriteri.keySet()) {
			if(fase.getFase() == null) {
				continue;
			}
			if(this.getCriteriAttivi(mappaCriteri.get(fase).get(TipoRicercaCriterioEnum.ESCLUSIVO)).size() < 1) {
				expressionFasi.add(this.buildCriteriRicercaConfigurabiliByFase(fase, qAtto, mappaCriteri, criteria, profilo, false));
			}else {
				expressionFasi.add(this.getBaseFaseCriteria(fase, qAtto));
			}
		}
		if(expressionFasi!=null && expressionFasi.size() > 0) {
			BooleanExpression toAddOnGlobal = null;
			for(BooleanExpression exp : expressionFasi) {
				if(toAddOnGlobal==null) {
					toAddOnGlobal = exp;
				}else {
					toAddOnGlobal = toAddOnGlobal.or(exp);
				}
			}
			returnedExpression = toAddOnGlobal;
		}
		return returnedExpression;
	}
	
	private BooleanExpression buildCriteriRicercaConfigurabiliByFase(TipoAttoHasFaseRicerca fase, QAtto qAtto, Map<TipoAttoHasFaseRicerca, Map<TipoRicercaCriterioEnum, List<FaseRicercaHasCriterio>>> mappaCriteri, AttoCriteriaDTO criteria, Profilo profilo, boolean checkingAttachmentAccess) {
		BooleanExpression returnedExpression = qAtto.id.eq(-1L);
		BooleanExpression faseExp = this.getBaseFaseCriteria(fase, qAtto);
		if (criteria.getViewtype().equals("itinere") && fase.getFase().getCodice().equalsIgnoreCase(FaseRicercaEnum.ITINERE.name())) {
			BooleanExpression criterioExp = null;
			boolean profiloCanRead = false;
			for(FaseRicercaHasCriterio c : mappaCriteri.get(fase).get(TipoRicercaCriterioEnum.INCLUSIVO)) {
				CriterioRicerca criterio = c.getCriterio();
				boolean skip = 
						(checkingAttachmentAccess && (c.getVisibilitaCompleta()==null || c.getVisibilitaCompleta().equals(false)) && !criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO_FULL_VISIB.name())) 
						||
						(checkingAttachmentAccess && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO.name()));
				if(skip) {
					continue;
				}
				if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.UFFICIO_PROPONENTE.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.aoo.id.eq(profilo.getAoo().getId());
					}else {
						criterioExp = criterioExp.or(qAtto.aoo.id.eq(profilo.getAoo().getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.ATTI_LAVORATI.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.avanzamento.any().profilo.id.eq(profilo.getId());
					}else {
						criterioExp = criterioExp.or(qAtto.avanzamento.any().profilo.id.eq(profilo.getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.UFFICI_ATTI_LAVORATI.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.avanzamento.any().profilo.aoo.id.eq(profilo.getAoo().getId());
					}else {
						criterioExp = criterioExp.or(qAtto.avanzamento.any().profilo.aoo.id.eq(profilo.getAoo().getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.RESP_UFFICI_ATTI_LAVORATI.name())) {
					List<Aoo> aooDiCuiProfiloCorrenteResponsabile = aooService.getAooByProfiloResponsabile(profilo.getId());
					if(aooDiCuiProfiloCorrenteResponsabile!=null && aooDiCuiProfiloCorrenteResponsabile.size() > 0) {
						BooleanExpression exp = qAtto.avanzamento.any().profilo.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDiCuiProfiloCorrenteResponsabile) {
							exp = exp.or(qAtto.avanzamento.any().profilo.aoo.superAooIds.like("%," + aoo.getId() + ",%"));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.VISIONE_ATTI_GERARCHICA.name())) {
					List<Aoo> aooDiCuiProfiloCorrenteResponsabile = aooService.getAooByProfiloResponsabile(profilo.getId());
					if(aooDiCuiProfiloCorrenteResponsabile!=null && aooDiCuiProfiloCorrenteResponsabile.size() > 0) {
						BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDiCuiProfiloCorrenteResponsabile) {
							exp = exp.or(qAtto.aoo.superAooIds.like("%," + aoo.getId() + ",%"));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
					criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_SOTTOSTRUTTURA_FULL_VISIB.name()) && 
					c.getAoos()!=null && c.getAoos().size() > 0) {
					
					Set<Aoo> aooDellaStessaSottostruttura = null;
					
					for(Aoo aoo : c.getAoos()) {
						if(aoo!=null && profilo.getAoo()!=null && profilo.getAoo().getId().equals(aoo.getId())) {
							Aoo aooDirezione = aooService.getAooDirezione(profilo.getAoo());
							if(aooDirezione!=null) {
								aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), profilo.getAoo(), true);
							}
							break;
						}
					}
					
					if(aooDellaStessaSottostruttura!=null && aooDellaStessaSottostruttura.size() > 0) {
						BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDellaStessaSottostruttura) {
							exp = exp.or(qAtto.aoo.id.eq(aoo.getId()));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
						 criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.PROFILI_SOTTOSTRUTTURA_FULL_VISIB.name()) && 
						c.getProfilos()!=null && c.getProfilos().size() > 0) {
					
					Set<Aoo> aooDellaStessaSottostruttura = null;
					
					for(Profilo profCriterio : c.getProfilos()) {
						if(profCriterio!=null && profilo.getId()!=null && profilo.getId().equals(profCriterio.getId())) {
							Aoo aooDirezione = aooService.getAooDirezione(profilo.getAoo());
							if(aooDirezione!=null) {
								aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), profilo.getAoo(), true);
							}
							break;
							
						}
					}
					
					if(aooDellaStessaSottostruttura!=null && aooDellaStessaSottostruttura.size() > 0) {
						BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDellaStessaSottostruttura) {
							exp = exp.or(qAtto.aoo.id.eq(aoo.getId()));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
						((!checkingAttachmentAccess && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO.name())) || criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO_FULL_VISIB.name())) && 
						c.getAoos()!=null && c.getAoos().size() > 0) {
					for(Aoo aoo : c.getAoos()) {
						if(aoo!=null && profilo.getAoo()!=null && profilo.getAoo().getId().equals(aoo.getId())) {
							profiloCanRead = true;
							break;
						}
					}
				}
				else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.TUTTI_GLI_UFFICI.name())) {
					profiloCanRead = true;
				}
			}
			if(profiloCanRead) {
				returnedExpression = faseExp;
			}else if(criterioExp!=null) {
				returnedExpression = criterioExp;
			}else{
				returnedExpression = qAtto.id.eq(-1L);
			}
		}else if(criteria.getViewtype().equals("conclusi")) {
			boolean profiloCanRead = false;
			BooleanExpression criterioExp = null;
			for(FaseRicercaHasCriterio c : mappaCriteri.get(fase).get(TipoRicercaCriterioEnum.INCLUSIVO)) {
				CriterioRicerca criterio = c.getCriterio();
				boolean skip = 
						(checkingAttachmentAccess && (c.getVisibilitaCompleta()==null || c.getVisibilitaCompleta().equals(false)) && !criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO_FULL_VISIB.name())) 
						||
						(checkingAttachmentAccess && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO.name()));
				if(skip) {
					continue;
				}
				if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.UFFICIO_PROPONENTE.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.aoo.id.eq(profilo.getAoo().getId());
					}else {
						criterioExp = criterioExp.or(qAtto.aoo.id.eq(profilo.getAoo().getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.ATTI_LAVORATI.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.avanzamento.any().profilo.id.eq(profilo.getId());
					}else {
						criterioExp = criterioExp.or(qAtto.avanzamento.any().profilo.id.eq(profilo.getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.UFFICI_ATTI_LAVORATI.name())) {
					if(criterioExp==null) {
						criterioExp = qAtto.avanzamento.any().profilo.aoo.id.eq(profilo.getAoo().getId());
					}else {
						criterioExp = criterioExp.or(qAtto.avanzamento.any().profilo.aoo.id.eq(profilo.getAoo().getId()));
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.RESP_UFFICI_ATTI_LAVORATI.name())) {
					List<Aoo> aooDiCuiProfiloCorrenteResponsabile = aooService.getAooByProfiloResponsabile(profilo.getId());
					if(aooDiCuiProfiloCorrenteResponsabile!=null && aooDiCuiProfiloCorrenteResponsabile.size() > 0) {
						BooleanExpression exp = qAtto.avanzamento.any().profilo.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDiCuiProfiloCorrenteResponsabile) {
							exp = exp.or(qAtto.avanzamento.any().profilo.aoo.superAooIds.like("%," + aoo.getId() + ",%"));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.VISIONE_ATTI_GERARCHICA.name())) {
					List<Aoo> aooDiCuiProfiloCorrenteResponsabile = aooService.getAooByProfiloResponsabile(profilo.getId());
					if(aooDiCuiProfiloCorrenteResponsabile!=null && aooDiCuiProfiloCorrenteResponsabile.size() > 0) {
						BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
						for(Aoo aoo : aooDiCuiProfiloCorrenteResponsabile) {
							exp = exp.or(qAtto.aoo.superAooIds.like("%," + aoo.getId() + ",%"));
						}
						if(criterioExp==null) {
							criterioExp = exp;
						}else {
							criterioExp = criterioExp.or(exp);
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
						criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_SOTTOSTRUTTURA_FULL_VISIB.name()) && 
						c.getAoos()!=null && c.getAoos().size() > 0) {
						
						Set<Aoo> aooDellaStessaSottostruttura = null;
						
						for(Aoo aoo : c.getAoos()) {
							if(aoo!=null && profilo.getAoo()!=null && profilo.getAoo().getId().equals(aoo.getId())) {
								Aoo aooDirezione = aooService.getAooDirezione(profilo.getAoo());
								if(aooDirezione!=null) {
									aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), profilo.getAoo(), true);
								}
								break;
							}
						}
						
						if(aooDellaStessaSottostruttura!=null && aooDellaStessaSottostruttura.size() > 0) {
							BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
							for(Aoo aoo : aooDellaStessaSottostruttura) {
								exp = exp.or(qAtto.aoo.id.eq(aoo.getId()));
							}
							if(criterioExp==null) {
								criterioExp = exp;
							}else {
								criterioExp = criterioExp.or(exp);
							}
						}
					}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
							 criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.PROFILI_SOTTOSTRUTTURA_FULL_VISIB.name()) && 
							c.getProfilos()!=null && c.getProfilos().size() > 0) {
						
						Set<Aoo> aooDellaStessaSottostruttura = null;
						
						for(Profilo profCriterio : c.getProfilos()) {
							if(profCriterio!=null && profilo.getId()!=null && profilo.getId().equals(profCriterio.getId())) {
								Aoo aooDirezione = aooService.getAooDirezione(profilo.getAoo());
								if(aooDirezione!=null) {
									aooDellaStessaSottostruttura = aooService.findDiscendentiOfAooNotDirezione(aooDirezione.getId(), profilo.getAoo(), true);
								}
								break;
								
							}
						}
						
						if(aooDellaStessaSottostruttura!=null && aooDellaStessaSottostruttura.size() > 0) {
							BooleanExpression exp = qAtto.aoo.profiloResponsabileId.eq(profilo.getId());
							for(Aoo aoo : aooDellaStessaSottostruttura) {
								exp = exp.or(qAtto.aoo.id.eq(aoo.getId()));
							}
							if(criterioExp==null) {
								criterioExp = exp;
							}else {
								criterioExp = criterioExp.or(exp);
							}
						}
					}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && 
						((!checkingAttachmentAccess && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO.name())) || criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.APPARTENENZA_UFFICIO_FULL_VISIB.name())) &&
						c.getAoos()!=null && c.getAoos().size() > 0) {
					for(Aoo aoo : c.getAoos()) {
						if(aoo!=null && profilo.getAoo()!=null && profilo.getAoo().getId().equals(aoo.getId())) {
							profiloCanRead = true;
							break;
						}
					}
				}else if(c.getValue()!=null && c.getValue().equals(true) && !profiloCanRead && criterio.getCodice().equalsIgnoreCase(CriterioRicercaEnum.TUTTI_GLI_UFFICI.name())) {
					profiloCanRead = true;
				}
			}
			if(profiloCanRead) {
				returnedExpression = faseExp;
			}else if(criterioExp!=null) {
				returnedExpression = faseExp.and(criterioExp);
			}else{
				returnedExpression = qAtto.id.eq(-1L);
			}
		}
		return returnedExpression;
	}
	
	/*
	 * IN ATTICO NON USATO 
	 *	
	@SuppressWarnings("unchecked")
	@Transactional(readOnly = true)
	public List<String> findByCriteria(JsonObject criteria) throws ParseException {
		String where = "1=1";
		if (criteria != null && !criteria.isJsonNull()) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			if (criteria.has("oggetto") && !criteria.get("oggetto").getAsString().equals("")) {
				where += " and atto.oggetto like '%" + criteria.get("oggetto").getAsString().trim() + "%'";
			}
			if (criteria.has("codiceAoo") && !criteria.get("codiceAoo").getAsString().equals("")) {
				where += " and aoo.codice like '%" + criteria.get("codiceAoo").getAsString().trim() + "%'";
			}
			if (criteria.has("dataAdozioneStart")
					&& !criteria.get("dataAdozioneStart").getAsString().trim().equalsIgnoreCase("")) {
				Date data = df.parse(criteria.get("dataAdozioneStart").getAsString().trim());
				where += " and atto.data_adozione >= '" + df.format(data) + "'";
			}
			if (criteria.has("dataAdozioneEnd")
					&& !criteria.get("dataAdozioneEnd").getAsString().trim().equalsIgnoreCase("")) {
				Date data = df.parse(criteria.get("dataAdozioneEnd").getAsString().trim());
				where += " and atto.data_adozione <= '" + df.format(data) + "'";
			}
			Query query = entityManager.createNativeQuery(
					"SELECT codice_cifra from atto inner join aoo on atto.aoo_id = aoo.id where " + where);
			return (List<String>) query.getResultList();
		} else {
			return null;
		}
	}
	*/

	@Transactional(readOnly = true)
	public Atto findByCodiceCifra(String codiceCifra) throws ParseException {
		if (codiceCifra != null && !codiceCifra.equals("")) {
			return attoRepository.findOne(QAtto.atto.codiceCifra.eq(codiceCifra));
		} else {
			return null;
		}

	}

	@Transactional(readOnly = true)
	public Atto findByCodiceCifraRevocato(Long id) throws ParseException {
		Atto atto = attoRepository.findOne(id);

		if (atto != null) {
			atto = attoRepository.findOne(QAtto.atto.codicecifraAttoRevocato.eq(atto.getCodiceCifra()));
			if (atto != null) {
				String cifra = atto.getCodiceCifra();
				atto = new Atto(atto.getId());
				atto.setCodiceCifra(cifra);
			}
			return atto;
		} else {
			return null;
		}

	}
	
	@Transactional(readOnly = true)
	public Atto findAttoDiRevoca(Long id) throws ParseException {
		Atto atto = attoRepository.findOne(id);

		if (atto != null) {
			Atto attoDiRevoca = attoRepository.findOne(QAtto.atto.codicecifraAttoRevocato.eq(atto.getCodiceCifra()));
			
			return attoDiRevoca;
		} else {
			return null;
		}

	}

	/*
	 * IN ATTICO SEMBRA NON USATO 
	 *	
	@SuppressWarnings("unlikely-arg-type")
	@Transactional(readOnly = true)
	public Iterable<Atto> findAll(final AttoCriteriaDTO criteria) {
		BooleanExpression expression = QAtto.atto.id.isNotNull();

		if (criteria.getAooId() != null) {
			if (criteria.getViewtype().equals("tutti")) {
				BooleanExpression internalPredicate = QAtto.atto.aoo.id.eq(Long.parseLong(criteria.getAooId(), 10));
				for (String stato : StatoAttoEnum.descrizioneConclusi()) {
					internalPredicate = internalPredicate.or(QAtto.atto.stato.equalsIgnoreCase(stato));
				}
				expression = expression.and(internalPredicate);
			} else if (!criteria.getViewtype().equals("pubblicati")) {
				expression = expression.and(QAtto.atto.aoo.id.eq(Long.parseLong(criteria.getAooId(), 10)));
			}
		}

		log.debug("criteria.getDataIncarico():{}", criteria.getDataIncarico());
		if ((criteria.getIncaricoa() != null && !"".equals(criteria.getIncaricoa()))
				|| (criteria.getDataIncarico() != null && !"".equals(criteria.getDataIncarico()))) {
			Page<TaskDesktopDTO> page = null;
			Utente utente = null;
			Iterable<Profilo> profili = null;
			if (criteria.getIncaricoa() != null && !"".equals(criteria.getIncaricoa())) {
				// Utente utente =
				// utenteService.findOne(criteria.getIncaricoa());
				utente = utenteService.findByUsername(criteria.getIncaricoa());
				if (utente == null) {
					utente = utenteService.findByCognomeNome(criteria.getIncaricoa());
				}

				if (utente != null) {
					profili = utenteService.activeprofilos(utente.getUsername());
				}
			}

			List<Long> ids = new ArrayList<Long>();

			Date incarico = null;
			if (criteria.getDataIncarico() != null && !"".equals(criteria.getDataIncarico())) {
				incarico = criteria.getDataIncarico().toDateTimeAtStartOfDay().toDate();
			}

			// TODO: integrazione con BPMN
			// page =
			// workflowService.getAll("carico",criteria.getProfiloId(),profili,null,null,incarico,null);

			for (TaskDesktopDTO task : page.getContent()) {
				ids.add(task.getAtto().getId());
			}

			Set<Long> hs = new HashSet<>();
			hs.addAll(ids);
			ids.clear();
			ids.addAll(hs);

			expression = expression.and(QAtto.atto.id.in(ids));

		}

		BooleanExpression tipiAttoExpression = null;
		if (criteria.getTipiAttoIds() != null) {
			for (Long idTipoAtto : criteria.getTipiAttoIds()) {
				if (tipiAttoExpression == null) {
					tipiAttoExpression = QAtto.atto.tipoAtto.id.eq(idTipoAtto);
				} else {
					tipiAttoExpression = tipiAttoExpression.or(QAtto.atto.tipoAtto.id.eq(idTipoAtto));
				}
			}
		}
		if (tipiAttoExpression != null) {
			expression = expression.and(tipiAttoExpression);
		}

		if (criteria.getArea() != null && !"".equals(criteria.getArea())) {
			BooleanExpression internalPredicate = ((QAtto.atto.aoo.codice.concat(" - ")
					.concat(QAtto.atto.aoo.descrizione)).containsIgnoreCase(criteria.getArea()))
							.or((QAtto.atto.aoo.codice.concat("-").concat(QAtto.atto.aoo.descrizione))
									.containsIgnoreCase(criteria.getArea()));
			if (internalPredicate != null) {
				expression = expression.and(internalPredicate);
			}
		}

		if (criteria.getViewtype().equals("itinere")) {

			List<TipoIter> tipi = new ArrayList<TipoIter>();

			tipi.add(tipoIterRepository.findOne((long) 2));
			tipi.add(tipoIterRepository.findOne((long) 3));

			// atti non pubblicati
			// atti tipo 2 e 3 con pareri empty

			BooleanExpression internalPredicate = null;
			for (String stato : StatoAttoEnum.descrizioneConclusi()) {
				if (internalPredicate == null) {
					internalPredicate = QAtto.atto.stato.notEqualsIgnoreCase(stato);
				} else {
					internalPredicate = internalPredicate.and(QAtto.atto.stato.notEqualsIgnoreCase(stato));
				}
			}
			BooleanExpression internalPredicate2 = QAtto.atto.tipoIter.in(tipi).and(QAtto.atto.pareri.isEmpty());

			expression = expression.and(internalPredicate.or(internalPredicate2));
		}

		if (criteria.getStato() != null && !"".equals(criteria.getStato())) {
			if (!criteria.getStato().contains(";") || criteria.getStato().split(";").length < 2) {
				expression = expression.and(QAtto.atto.stato.equalsIgnoreCase(criteria.getStato().split(";")[0]));
			} else {
				String stati[] = criteria.getStato().split(";");
				BooleanExpression internalExp = null;
				for (int i = 0; i < stati.length; i++) {
					if (i == 0) {
						internalExp = QAtto.atto.stato.equalsIgnoreCase(stati[i]);
					} else {
						internalExp = internalExp.or(QAtto.atto.stato.equalsIgnoreCase(stati[i]));
					}
				}
				expression = expression.and(internalExp);
			}
		}

		if (criteria.getRegolamento() != null && !"".equals(criteria.getRegolamento())) {
			expression = expression.and(QAtto.atto.regolamento.equalsIgnoreCase(criteria.getRegolamento()));
		}

		if (criteria.getNumeroAdozione() != null && !"".equals(criteria.getNumeroAdozione())) {
			expression = expression.and(QAtto.atto.numeroAdozione.containsIgnoreCase(criteria.getNumeroAdozione()));
		}

		if (criteria.getCodiceCifra() != null && !"".equals(criteria.getCodiceCifra())) {
			expression = expression.and(QAtto.atto.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()));
		}
		if (criteria.getAnno() != null && !"".equals(criteria.getAnno())) {
			expression = expression.and(QAtto.atto.dataCreazione.year().eq(Integer.parseInt(criteria.getAnno())));
		}

		if (criteria.getDataCreazione() != null && !"".equals(criteria.getDataCreazione())) {
			expression = expression.and(QAtto.atto.dataCreazione.goe(criteria.getDataCreazione()));
			expression = expression.and(QAtto.atto.dataCreazione.loe(criteria.getDataCreazione()));
		}
		if (criteria.getDataCreazioneDa() != null && !"".equals(criteria.getDataCreazioneDa())) {
			expression = expression.and(QAtto.atto.dataCreazione.goe(criteria.getDataCreazioneDa()));
		}
		if (criteria.getDataCreazioneA() != null && !"".equals(criteria.getDataCreazioneA())) {
			expression = expression.and(QAtto.atto.dataCreazione.loe(criteria.getDataCreazioneA()));
		}

		if ((criteria.getDataAdozione() != null && !"".equals(criteria.getDataAdozione()))
				&& (criteria.getDataAdozioneA() != null && !"".equals(criteria.getDataAdozioneA()))) {
			expression = expression.and(QAtto.atto.dataAdozione.goe(criteria.getDataAdozione()));
			expression = expression.and(QAtto.atto.dataAdozione.loe(criteria.getDataAdozioneA()));
		} else {
			if (criteria.getDataAdozione() != null && !"".equals(criteria.getDataAdozione())) {
				expression = expression.and(QAtto.atto.dataAdozione.goe(criteria.getDataAdozione()));
			}

			if (criteria.getDataAdozioneA() != null && !"".equals(criteria.getDataAdozioneA())) {
				expression = expression.and(QAtto.atto.dataAdozione.loe(criteria.getDataAdozioneA()));
			}
		}

		if ((criteria.getDataEsecutiva() != null && !"".equals(criteria.getDataEsecutiva()))
				&& (criteria.getDataAdozione() == null || "".equals(criteria.getDataAdozione()))) {
			expression = expression.and(QAtto.atto.id.in(eventoService
					.findAttosByEsecutivita(new DateTime(criteria.getDataEsecutiva().toDate().getTime()))));
		}

		if (criteria.getOggetto() != null && !"".equals(criteria.getOggetto())) {
			expression = expression.and(QAtto.atto.oggetto.containsIgnoreCase(criteria.getOggetto()));
		}

		if (criteria.getTipoAtto() != null && !"".equals(criteria.getTipoAtto())) {
			expression = expression.and(QAtto.atto.tipoAtto.descrizione.containsIgnoreCase(criteria.getTipoAtto()));
		}

		if (criteria.getTipoIter() != null && !"".equals(criteria.getTipoIter())) {
			expression = expression.and(QAtto.atto.tipoIter.descrizione.containsIgnoreCase(criteria.getTipoIter()));
		}

		if (criteria.getTaskStato() != null && !"".equals(criteria.getTaskStato())) {
			expression = expression.and(QAtto.atto.stato.equalsIgnoreCase(criteria.getTaskStato()));
		}
		if (criteria.getIdOdg() != null) {
			// expression = expression.and(
			// QAtto.atto.tipoAtto.codice.equalsIgnoreCase("DEL"));
			expression = expression.and(QAtto.atto.ordineGiornos.any().id.eq(criteria.getIdOdg()));
		}

		if ((criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo()))
				&& (criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo()))) {
			expression = expression.and(QAtto.atto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo()));
			expression = expression.and(QAtto.atto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()));
		} else {
			if (criteria.getInizioPubblicazioneAlbo() != null && !"".equals(criteria.getInizioPubblicazioneAlbo())) {
				expression = expression
						.and(QAtto.atto.inizioPubblicazioneAlbo.goe(criteria.getInizioPubblicazioneAlbo()));
			}

			if (criteria.getFinePubblicazioneAlbo() != null && !"".equals(criteria.getFinePubblicazioneAlbo())) {
				expression = expression.and(QAtto.atto.finePubblicazioneAlbo.loe(criteria.getFinePubblicazioneAlbo()));
			}
		}

		Iterable<Atto> l = attoRepository.findAll(expression);

		List<String> ids = new ArrayList<String>();

		for (Atto atto : l) {
			ids.add(atto.getId().toString());
			log.debug("Atto:" + atto.getId().toString() + " - " + atto.getId());
			minimalInfoLoad(atto);
			log.debug("idAtto:" + atto.getCodiceCifra());
		}

		return l;
	}
	*/

	/**
	 * Il metodo viene chiamato per la crezione da clone
	 * 
	 * @param atto
	 * @param tipoClone
	 * @return
	 */
	@Transactional(readOnly = true)
	public Atto copia(final Atto atto, final String tipoCopia) {
		Atto nuovoAtto = new Atto();

		BeanUtils.copyProperties(atto, nuovoAtto, "sottoscrittori", "allegati", "id", "codiceCifra", "lastModifiedDate",
				"version", "createdDate", "lastModifiedBy", "createdBy", "dataCreazione");
		if ("riferimento".equals(tipoCopia)) {
			nuovoAtto.setCodicecifraAttoRevocato(atto.getCodiceCifra());
		}

		return nuovoAtto;
	}

	@Transactional(readOnly = true)
	public SchedaValoriDlg33DTO caricaSchedaTrasparenza(final Atto atto) {
		SchedaValoriDlg33DTO dto = new SchedaValoriDlg33DTO();
		Map<Long, Object> valoriSchedaDati = null;
		Integer progressivoElemento = 0;

		for (AttoSchedaDato attoSchedaDato : atto.getValoriSchedeDati()) {

			if (!dto.getElementiSchede().containsKey(attoSchedaDato.getId().getSchedaId())) {
				dto.getElementiSchede().put(attoSchedaDato.getId().getSchedaId(), new ArrayList<Map<Long, Object>>());
				progressivoElemento = 0;
				valoriSchedaDati = new HashMap<Long, Object>();
				dto.getElementiSchede().get(attoSchedaDato.getId().getSchedaId()).add(valoriSchedaDati);
			}

			if (!attoSchedaDato.getId().getProgressivoElemento().equals(progressivoElemento)) {
				progressivoElemento = attoSchedaDato.getId().getProgressivoElemento();
				valoriSchedaDati = new HashMap<Long, Object>();
				dto.getElementiSchede().get(attoSchedaDato.getId().getSchedaId()).add(valoriSchedaDati);
			}

			Object object = attoSchedaDatoService.dbToVo(attoSchedaDato);
			log.debug("object:" + object);
			valoriSchedaDati.put(attoSchedaDato.getId().getSchedaDatoId(), object);

		}

		return dto;
	}

	@Transactional
	public void saveSchedaTrasparenza(final Atto atto, final SchedaValoriDlg33DTO schedaValori, final Long profiloId)
			throws IOException, DocumentException {
		log.debug("saveSchedaTrasparenza...");
		Map<AttoSchedaDatoId, AttoSchedaDato> valori = new HashMap<AttoSchedaDatoId, AttoSchedaDato>();

		// Delete all old AttoSchedaDato
		log.info("Deleting AttoSchedaDato");
		for (AttoSchedaDato attoSchedaDato : atto.getValoriSchedeDati()) {
			log.info("Deleting AttoSchedaDato " + attoSchedaDato.getDataValore());
			attoSchedaDatoService.delete(attoSchedaDato);
		}

		atto.getValoriSchedeDati().clear();

		for (Long schedaId : schedaValori.getElementiSchede().keySet()) {
			log.debug("schedaId:" + schedaId);
			Scheda scheda = schedaRepository.findOne(schedaId);

			Map<Long, SchedaDato> campiScheda = new HashMap<Long, SchedaDato>();
			for (SchedaDato schedaDato : scheda.getCampi()) {
				campiScheda.put(schedaDato.getId(), schedaDato);
			}

			List<Map<Long, Object>> elementi = schedaValori.getElementiSchede().get(schedaId);
			int progressivoElemento = 0;
			for (Map<Long, Object> schedaDatiValori : elementi) {
				for (Long schedaDatoId : schedaDatiValori.keySet()) {
					log.debug("schedaDatoId:" + schedaDatoId);
					Object valore = schedaDatiValori.get(schedaDatoId);
					log.debug("valore:" + valore);

					AttoSchedaDatoId id = new AttoSchedaDatoId();
					id.setAttoId(atto.getId());
					id.setSchedaDatoId(schedaDatoId);
					id.setSchedaId(schedaId);
					id.setProgressivoElemento(progressivoElemento);

					AttoSchedaDato attoSchedaDato = null;
					if (valori.containsKey(id)) {
						attoSchedaDato = valori.get(id);
					} else {
						attoSchedaDato = new AttoSchedaDato();
						attoSchedaDato.setId(id);
						atto.getValoriSchedeDati().add(attoSchedaDato);
					}

					TipoDatoEnum tipoDato = campiScheda.get(schedaDatoId).getDato().getTipoDato();
					log.debug("valore:" + valore);
					attoSchedaDato.setTipoDato(tipoDato);
					attoSchedaDatoService.voToDb(attoSchedaDato, valore);

				}
				progressivoElemento++;
			}
		}
	}

	@Transactional(readOnly = true)
	public List<String> findAllStates(String tipoAtto) {
		return attoRepository.findDistinctStato(tipoAtto);
	}

	@Transactional(readOnly = true)
	public List<String> findAllDistinctStato() {
		return attoRepository.findAllDistinctStato();
	}

	private Object getValue(String property) {
		log.debug("getValue PROPERTY: {}", property);
		Object result = null;
		String[] splitProp = property.split("\\.");
		for (String prop : splitProp) {
			log.debug("getValue splitProp: {}", prop);
			if (result == null) {
				log.debug("getValue RESULT: {}", result);
				try {
					Class.forName(QAtto.class.getName());
					result = QAtto.class.getField(prop).get(null);
					log.debug("getValue RESULT: {}", result);
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (NoSuchFieldException e) {
					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			} else {
				log.debug("getValue RESULT: {}", result);
				try {
					log.debug("getValue RESULT CLASS: {}", result.getClass());
					log.debug("getValue RESULT FIELD: {}", result.getClass().getField(prop));
					Class.forName(result.getClass().getName());
					result = result.getClass().getField(prop).get(result);
					log.debug("getValue RESULT: {}", result);
				} catch (IllegalArgumentException e) {

					e.printStackTrace();
				} catch (NoSuchFieldException e) {

					e.printStackTrace();
				} catch (SecurityException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}

		log.debug("getValue RETURN RESULT: {}", result);
		return result;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private BooleanExpression generaExpression(CondizioneRicercaLiberaDTO condizione) {

		log.debug("RICERCA LIBERA generaExpression CONDIZIONE: {}", condizione.getCondizione());

		if (condizione.getCondizione().equals("eq") && !condizione.getCampo().contains("data")) {
			return ((SimpleExpression) getValue(condizione.getCampo())).eq(condizione.getValore());
		}
		if (condizione.getCondizione().equals("eq") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.eq(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}
		if (condizione.getCondizione().equals("neq") && !condizione.getCampo().contains("data")) {
			return ((SimpleExpression) getValue(condizione.getCampo())).ne(condizione.getValore());
		}
		if (condizione.getCondizione().equals("neq") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.ne(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}
		if (condizione.getCondizione().equals("like") && !condizione.getCampo().contains("data")) {
			log.debug("RICERCA LIBERA generaExpression LIKE");
			log.debug("RICERCA LIBERA generaExpression CAMPO: {}", condizione.getCampo());
			log.debug("RICERCA LIBERA generaExpression VALORE: {}", condizione.getValore());
			return ((StringExpression) getValue(condizione.getCampo())).containsIgnoreCase(condizione.getValore());
		}
		if (condizione.getCondizione().equals("like") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.eq(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}

		if (condizione.getCondizione().equals("maggiore") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.gt(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));

		}
		if (condizione.getCondizione().equals("maggiore") && !condizione.getCampo().contains("data")) {
			return ((NumberExpression) getValue(condizione.getCampo())).gt(Integer.parseInt(condizione.getValore()));
		}

		if (condizione.getCondizione().equals("maggioreuguale") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.goe(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}
		if (condizione.getCondizione().equals("maggioreuguale") && !condizione.getCampo().contains("data")) {
			return ((NumberExpression) getValue(condizione.getCampo())).goe(Integer.parseInt(condizione.getValore()));
		}

		if (condizione.getCondizione().equals("minore") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.lt(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}
		if (condizione.getCondizione().equals("minore") && !condizione.getCampo().contains("data")) {
			return ((NumberExpression) getValue(condizione.getCampo())).lt(Integer.parseInt(condizione.getValore()));
		}

		if (condizione.getCondizione().equals("minoreuguale") && condizione.getCampo().contains("data")) {
			return ((DateExpression) getValue(condizione.getCampo()))
					.loe(ISODateTimeFormat.dateTimeParser().parseLocalDate(condizione.getValore()));
		}
		if (condizione.getCondizione().equals("minoreuguale") && !condizione.getCampo().contains("data")) {
			return ((NumberExpression) getValue(condizione.getCampo())).loe(Integer.parseInt(condizione.getValore()));
		}

		return null;
	}
	
	public boolean checkTestoAtto(Atto atto, String sezioneTesto) throws ServiceException {
		if(atto == null) {
			return false;
		}
		List<SezioneTipoAttoDto> listSezionetipoAttoDto = sezioneTipoAttoService.findByTipoAtto(atto.getTipoAtto().getId());
		if(listSezionetipoAttoDto != null) {
		   for (SezioneTipoAttoDto sezioneTipoAttoDto : listSezionetipoAttoDto) {
			   if(sezioneTipoAttoDto.getVisibile() && sezioneTipoAttoDto.getCodice()!= null &&
					sezioneTipoAttoDto.getCodice().equalsIgnoreCase(sezioneTesto)) {
					if(sezioneTesto.equals("domanda")) {
					    if(atto.getDomanda() == null || StringUtil.isNull(atto.getDomanda().getTesto()) || 
								atto.getDomanda().getTesto().trim().replaceAll("&nbsp;", "").replaceAll(" ", "").isEmpty()) {
							return false;
						}
					}else if(sezioneTesto.equals("preambolo")) {
						if(atto.getPreambolo() == null || StringUtil.isNull(atto.getPreambolo().getTesto()) || 
								atto.getPreambolo().getTesto().trim().replaceAll("&nbsp;", "").replaceAll(" ", "").isEmpty()) {
							return false;
						}
					}else if(sezioneTesto.equals("motivazione")) {
						if(atto.getMotivazione() == null || StringUtil.isNull(atto.getMotivazione().getTesto()) || 
								atto.getMotivazione().getTesto().trim().replaceAll("&nbsp;", "").replaceAll(" ", "").isEmpty()) {
							return false;
						}
					}else if(sezioneTesto.equals("dispositivo")) {
						if(atto.getDispositivo() == null || StringUtil.isNull(atto.getDispositivo().getTesto()) || 
								atto.getDispositivo().getTesto().trim().replaceAll("&nbsp;", "").replaceAll(" ", "").isEmpty()) {
							return false;
						}
					}
			   }
		  }
		}
		return true;
	}
	
	@Transactional(readOnly = true)
	public boolean hasRequiredFields(Long attoId, String taskId, Long profiloId) {
    	boolean ok = true;
    	try {
    		Atto atto = attoRepository.findOne(attoId);
    		Map<String, Boolean> enabledSectionMap = sezioneTipoAttoService.findByTipoAttoVisibilityMap(atto.getTipoAtto().getId());
	        
    		String SEZIONE = "domanda";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DOMANDA_VISIBILITY, "false"))) {
    			ok = this.checkTestoAtto(atto, "domanda");
    			loggaCheckErrato(ok, "check domanda");
	        }
    		
    		SEZIONE = "preambolo";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_PREAMBOLO_VISIBILITY, "false"))) {
    			ok = this.checkTestoAtto(atto, "preambolo");
    			loggaCheckErrato(ok, "check preambolo");
	        }
    		
    		SEZIONE = "motivazione";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_MOTIVAZIONE_VISIBILITY, "false"))) {
    			ok = this.checkTestoAtto(atto, "motivazione");
    			loggaCheckErrato(ok, "check motivazione");
    			
	        }
    		
    		SEZIONE = "dispositivo";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DISPOSITIVO_VISIBILITY, "false"))) {
    			ok = this.checkTestoAtto(atto, "dispositivo");
    			loggaCheckErrato(ok, "check dispositivo");
	        }
    		
    		SEZIONE = "datiidentificativi";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVI_VISIBILITY, "false"))) {
	        	ok = atto.getOggetto() != null && !atto.getOggetto().replaceAll("\r", "").replaceAll("\n", "").trim().isEmpty();
	        	if(ok) {
	        		ok = atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getId()!=null;
	        		loggaCheckErrato(ok, "check datiIdentificativi1");
	        	}
	        	if(ok && atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getCodice().equalsIgnoreCase("REVOCA")) {
	        		ok = atto.getCodicecifraAttoRevocato()!=null && !atto.getCodicecifraAttoRevocato().trim().isEmpty();
	        		loggaCheckErrato(ok, "check datiIdentificativi2 revoca");
	        	}
	        	if(ok) {
	        		ok = atto.getTipoIter()!=null && atto.getTipoIter().getId()!=null;
	        		loggaCheckErrato(ok, "check datiIdentificativi3");
	        	}
	        	if(ok) {
	        		ok = atto.getCodiceCup()==null || atto.getCodiceCup().isEmpty() || atto.getCodiceCup().trim().isEmpty() || atto.getCodiceCup().trim().length() == 15;
	        		loggaCheckErrato(ok, "check cup");
	        	}
	        	if(ok && atto.getId()!=null && atto.getId() > 0 && atto.getTipoDeterminazione()!=null && !atto.getTipoDeterminazione().getDescrizione().toLowerCase().equals("ordinario")) {
	        		ok = atto.getAttoRevocatoId()!=null;
	        		loggaCheckErrato(ok, "check datiIdentificativi4");
	        	}
	        }
    		
    		SEZIONE = "riferimentinormativi";
    		Boolean sectionRiferimentiNormativiEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_RIFERIMENTINORMATIVI_VISIBILITY, "false"))) {
				sectionRiferimentiNormativiEnabled = Boolean.TRUE;
			}
    		if(ok && sectionRiferimentiNormativiEnabled!=null && sectionRiferimentiNormativiEnabled && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE)) {
	        	if(atto.getRiservato()==null || (!atto.getRiservato() && atto.getPubblicazioneIntegrale() == null)) {
	        		ok = false;
	        		loggaCheckErrato(ok, "check riferimentinormativi");
	        	}
	        }
    		
    		SEZIONE = "schede"; //beneficiario
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SCHEDE_VISIBILITY, "false"))) {
	        	
	        }
    		
    		SEZIONE = "dichiarazioni";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && !atto.getTipoIter().getCodice().equalsIgnoreCase("SENZA_VERIFICA_CONTABILE") && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DICHIARAZIONI_VISIBILITY, "false"))) {
	        	if(atto.getContabileOggetto() == null || atto.getContabileOggetto().trim().isEmpty()) {
	        		ok = false;
	        		loggaCheckErrato(ok, "check dichiarazioni");
	        	}
	        }
    		
    		
    		SEZIONE = "allegati";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && atto.getAllegati()!=null && atto.getAllegati().size() > 0 && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_ALLEGATI_VISIBILITY, "false"))) {
	        	for(DocumentoInformatico allegato : atto.getAllegati()) {
	        		if(allegato != null && allegato.getFile() != null &&
							(allegato.getTitolo() == null || allegato.getTitolo().trim().isEmpty() ||
								allegato.getTipoAllegato() == null ||
								allegato.getPubblicabile() == null ||
								(atto.getRiservato() !=null && atto.getRiservato() && allegato.getPubblicabile()!=null && allegato.getPubblicabile()) ||
								(allegato.getPubblicabile()!=null && allegato.getPubblicabile() && allegato.getOmissis() == null) ||
								(allegato.getOmissis()!=null && allegato.getOmissis() && allegato.getFileomissis()==null))) {
	        			ok = false;
	        			loggaCheckErrato(ok, "check allegati ko per allegatoId:"+allegato.getId());
	        			break;
	        		}
	        	}
	        }
    		
    		SEZIONE = "trasparenza";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_TRASPARENZA_VISIBILITY, "false"))) {
	        	
	        }
    		
    		SEZIONE = "divulgazione";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DIVULGAZIONE_VISIBILITY, "false"))) {
    			if((atto.getRiservato()==null || atto.getRiservato().equals(false)) && atto.getDurataGiorni()==null){
    				ok = false;
    				loggaCheckErrato(ok, "check divulgazione");
    			}
    			if(ok) {
        			if(!(sectionRiferimentiNormativiEnabled && enabledSectionMap.containsKey("riferimentinormativi") && enabledSectionMap.get("riferimentinormativi"))
        				&& (atto.getPubblicazioneTrasparenzaNolimit() == null || atto.getRiservato()==null || (atto.getRiservato().equals(false) && atto.getPubblicazioneIntegrale()==null))){
        				ok = false;
        				loggaCheckErrato(ok, "check riferimentinormativi");
        			}
    			}
	        }
    		
    		SEZIONE = "datiidentificativiconsiglio";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVICONSIGLIO_VISIBILITY, "false"))) {
    			ok = atto.getOggetto() != null && !atto.getOggetto().replaceAll("\r", "").replaceAll("\n", "").trim().isEmpty();
	        	if(ok) {
	        		ok = atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiconsiglio1");
	        	}
	        	if(ok) {
	        		ok = atto.getTipoIter()!=null && atto.getTipoIter().getId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiconsiglio2");
	        	}
	        	if(ok) {
	        		ok = atto.getProponenti()!=null && atto.getProponenti().size() > 0;
	        		loggaCheckErrato(ok, "check datiidentificativiconsiglio3");
	        	}
	        	if(ok) {
	        		ok = atto.getDataRicevimento()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiconsiglio4");
	        	}
	        	if(ok && atto.getId()!=null && atto.getId() > 0 && atto.getTipoDeterminazione()!=null && !atto.getTipoDeterminazione().getDescrizione().toLowerCase().equals("ordinario")) {
	        		ok = atto.getAttoRevocatoId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiconsiglio5");
	        	}
	        }
    		
    		SEZIONE = "divulgazionesemplificata";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DIVULGAZIONESEMPLIFICATA_VISIBILITY, "false"))){
    			if(!(sectionRiferimentiNormativiEnabled && enabledSectionMap.containsKey("riferimentinormativi") && enabledSectionMap.get("riferimentinormativi"))
    				&& (atto.getRiservato()==null || (atto.getRiservato().equals(false) && atto.getPubblicazioneIntegrale()==null))){
    				ok = false;
    				loggaCheckErrato(ok, "check divulgazionesemplificata");
    			}
	        }
    		
    		SEZIONE = "datiidentificativiverbale";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_DATIIDENTIFICATIVIVERBALE_VISIBILITY, "false"))) {
    			ok = atto.getOggetto() != null && !atto.getOggetto().replaceAll("\r", "").replaceAll("\n", "").trim().isEmpty();
	        	if(ok) {
	        		ok = atto.getTipoDeterminazione()!=null && atto.getTipoDeterminazione().getId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiverbale1");
	        	}
	        	if(ok) {
	        		ok = atto.getTipoIter()!=null && atto.getTipoIter().getId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiverbale2");
	        	}
	        	if(ok && atto.getId()!=null && atto.getId() > 0 && atto.getTipoDeterminazione()!=null && !atto.getTipoDeterminazione().getDescrizione().toLowerCase().equals("ordinario")) {
	        		ok = atto.getAttoRevocatoId()!=null;
	        		loggaCheckErrato(ok, "check datiidentificativiverbale3");
	        	}
	        }
    		
    		SEZIONE = "sottoscrizioni";
    		Boolean sectionSottoscrizioniEnabled = Boolean.FALSE;
			if (Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_SOTTOSCRIZIONI_VISIBILITY, "false"))) {
				sectionSottoscrizioniEnabled = Boolean.TRUE;
			}
    		if(ok && sectionSottoscrizioniEnabled && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE)) {
	        	if(atto.getSottoscrittori()==null || atto.getSottoscrittori().size()==0) {
	        		ok = false;
	        		loggaCheckErrato(ok, "check sottoscrizioni1");
	        	}else if(atto.getEmananteProfilo()==null || atto.getEmananteProfilo().getId()==null || atto.getQualificaEmanante()==null || atto.getQualificaEmanante().getId()==null){
	        		ok = false;
	        		loggaCheckErrato(ok, "check sottoscrizioni2");
	        	}else {
	        		boolean almenoUnoAttivo = false;
	        		for(SottoscrittoreAtto sottoscrittore : atto.getSottoscrittori()) {
	        			if(sottoscrittore.getEnabled()!=null && sottoscrittore.getEnabled()) {
	        				almenoUnoAttivo = true;
	        			}
	        			if(sottoscrittore.getQualificaProfessionale()==null || sottoscrittore.getQualificaProfessionale().getId()==null) {
	        				ok = false;
	        				if(almenoUnoAttivo) {
	        					break;
	        				}
	        			}
	        		}
	        		if(ok) {
	        			ok = almenoUnoAttivo;
	        		}
	        		if(ok && atto.getCongiunto()!=null && atto.getCongiunto()) {
	        			boolean almenoUnSottoscrittoreAltraAOO = false;
	        			for(SottoscrittoreAtto sottoscrittore : atto.getSottoscrittori()) {
	        				if(sottoscrittore!=null && sottoscrittore.getAooNonProponente()!=null && !sottoscrittore.getAooNonProponente().getId().equals(atto.getAoo().getId())) {
	        					almenoUnSottoscrittoreAltraAOO = true;
	        					break;
	        				}
	        			}
	        			ok = almenoUnSottoscrittoreAltraAOO;
	        		}
	        	}
	        }
    		loggaCheckErrato(ok, "check sottoscrizioni3");
    		
    		SEZIONE = "assegnazioneincarichi";
    		if(ok && enabledSectionMap.containsKey(SEZIONE) && enabledSectionMap.get(SEZIONE) && Boolean.parseBoolean(SectionsVisibilityProps.getProperty(Constants.SECTION_ASSEGNAZIONEINCARICHI_VISIBILITY, "false"))) {
    			String tipoConfigurazioneTaskProfiloId = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_PROFILO_ID);
    			String tipoConfigurazioneTaskUfficioId = WebApplicationProps.getProperty(Constants.WEB_APPLICATION_TASK_TIPOCONFIGURAZIONE_UFFICIO_ID);
    			
        		List<ConfigurazioneIncaricoDto> listConfigurazioneIncarico = configurazioneIncaricoService.findByAtto(atto.getId());
        		Map<Long, ConfigurazioneIncaricoDto> confIncMap = new HashMap<Long, ConfigurazioneIncaricoDto>();
        		for(ConfigurazioneIncaricoDto dto : listConfigurazioneIncarico) {
        			if(dto!=null && dto.getEnabled()!=null && dto.getEnabled()) {
        				confIncMap.put(dto.getIdConfigurazioneTask(), dto);
        			}
        		}
        		Boolean assigneed = workflowService.isTaskAssigneedToProfile(taskId, profiloId);
    	    	if(assigneed!=null && assigneed) {
    		    	Iterable<AssegnazioneIncaricoDTO> listAssegnazioneIncaricoDTO = workflowService.getAssegnazioniIncarichi(taskId);
    		    	if(listAssegnazioneIncaricoDTO!=null) {
    			    	for(AssegnazioneIncaricoDTO a : listAssegnazioneIncaricoDTO) {
    			    		ConfigurazioneTaskDto configurazioneTaskDto = configurazioneTaskService.findByCodice(a.getCodiceConfigurazione());
    			    		//tipo profilo
    			    		if(configurazioneTaskDto.getTipoConfigurazioneTaskId().equals(Long.parseLong(tipoConfigurazioneTaskProfiloId))) {
    			    			if(configurazioneTaskDto.isObbligatoria()) {
    			    				if(!confIncMap.containsKey(configurazioneTaskDto.getIdConfigurazioneTask()) 
    			    				|| confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoProfiloDto()==null
    			    				|| confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoProfiloDto().size() < 1) {
    			    					loggaCheckErrato(ok, "check assegnazioneincarichi1");
    			    					ok = false;
    			    					break;
    			    				}
    			    			}
    			    			if(confIncMap.containsKey(configurazioneTaskDto.getIdConfigurazioneTask()) && confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoProfiloDto() != null) {
    			    				for(ConfigurazioneIncaricoProfiloDto confProf : confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoProfiloDto()) {
    			    					if(configurazioneTaskDto.isObbligatoria()) {
    				    					if(confProf.getIdProfilo()==null || confProf.getQualificaProfessionaleDto()==null || confProf.getQualificaProfessionaleDto().getId()==null) {
    				    						loggaCheckErrato(ok, "check assegnazioneincarichi2");
    				    						ok = false;
    					    					break;
    				    					}
    			    					}
    			    					if(confProf.getProfilo()!=null && confProf.getIdProfilo()!=null && confProf.getProfilo().getId().equals(confProf.getIdProfilo()) && confProf.getProfilo().getValidita()!=null && confProf.getProfilo().getValidita().getValidoal()!=null) {
    			    						loggaCheckErrato(ok, "check assegnazioneincarichi3");
    			    						ok = false;
    				    					break;
    			    					}
    			    					if(confProf.getIdProfilo()!=null && (confProf.getQualificaProfessionaleDto()==null || confProf.getQualificaProfessionaleDto().getId()==null)) {
    			    						loggaCheckErrato(ok, "check assegnazioneincarichi4");
    			    						ok = false;
    				    					break;
    			    					}
    			    				}
    			    			}
    			    			if(!ok) {
    			    				loggaCheckErrato(ok, "check assegnazioneincarichi5");
    			    				break;
    			    			}
    			    		}else if(configurazioneTaskDto.getTipoConfigurazioneTaskId().equals(Long.parseLong(tipoConfigurazioneTaskUfficioId))) {
    			    			if(configurazioneTaskDto.isObbligatoria()) {
    			    				if(!confIncMap.containsKey(configurazioneTaskDto.getIdConfigurazioneTask()) 
    				    				|| confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoAooDto()==null
    				    				|| confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoAooDto().size() < 1) {
    				    					ok = false;
    				    					loggaCheckErrato(ok, "check assegnazioneincarichi6");
    				    					break;
    				    			}
    			    				for(ConfigurazioneIncaricoAooDto confAoo : confIncMap.get(configurazioneTaskDto.getIdConfigurazioneTask()).getListConfigurazioneIncaricoAooDto()) {
    			    					if(configurazioneTaskDto.isObbligatoria()) {
    				    					if(confAoo.getIdAoo()==null) {
    				    						ok = false;
    				    						loggaCheckErrato(ok, "check assegnazioneincarichi7");
    					    					break;
    				    					}
    			    					}
    			    					if(configurazioneTaskDto.isImpostaScadenza() && configurazioneTaskDto.isScadenzaObbligatoria() && confAoo.getIdAoo()!=null && confAoo.getGiorniScadenza()==null) {
    			    						ok = false;
    			    						loggaCheckErrato(ok, "check assegnazioneincarichi8");
    				    					break;
    			    					}
    			    					if(configurazioneTaskDto.isDataManuale() && confAoo.getIdAoo()!=null && confAoo.getDataManuale()==null) {
    			    						ok = false;
    			    						loggaCheckErrato(ok, "check assegnazioneincarichi9");
    				    					break;
    			    					}
    			    				}
    			    				if(!ok) {
    			    					loggaCheckErrato(ok, "check assegnazioneincarichi10");
    				    				break;
    				    			}
    			    			}
    			    		}
    			    	}
    		    	}
    	    	}
	        }
    		
    	}catch(Exception e) {
    		e.printStackTrace();
    		loggaCheckErrato(ok, "check exception nel catch:"+e.toString());
    		ok = false;
    	}
    	loggaCheckErrato(ok, "check finale");
    	return ok;
    }
	
	private void loggaCheckErrato (boolean ok, String msg) {
		if(!ok) {
			log.info("****CONTROLLARE LA SEZIONE:"+msg+"**********" );
		}
	}
}
