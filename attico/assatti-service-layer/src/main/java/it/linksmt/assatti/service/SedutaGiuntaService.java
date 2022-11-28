package it.linksmt.assatti.service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import javax.inject.Inject;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricIdentityLinkLog;
import org.camunda.bpm.engine.task.Task;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.itextpdf.text.DocumentException;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.domain.InsediamentoConsiglio;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.ProgressivoSeduta;
import it.linksmt.assatti.datalayer.domain.QComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.QInsediamentoConsiglio;
import it.linksmt.assatti.datalayer.domain.QSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.Resoconto;
import it.linksmt.assatti.datalayer.domain.RubricaDestinatarioEsterno;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.TipoResocontoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.DocumentoPdfRepository;
import it.linksmt.assatti.datalayer.repository.EsitoRepository;
import it.linksmt.assatti.datalayer.repository.InsediamentoConsiglioRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.ProgressivoSedutaRepository;
import it.linksmt.assatti.datalayer.repository.ResocontoRepository;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoRepository;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.SedutaAnnullaDTO;
import it.linksmt.assatti.service.dto.SedutaCriteriaDTO;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * Service class for managing profile.
 */
@Service
public class SedutaGiuntaService {
	
	private final Logger log = LoggerFactory
			.getLogger(SedutaGiuntaService.class);

	@Inject
	private SedutaGiuntaRepository sedutaGiuntaRepository;
	
	@Inject
	private OrdineGiornoService ordineGiornoService;
	
	@Inject
	private ResocontoRepository resocontoRepository;
	
	@Inject
	private SottoscrittoreSedutaGiuntaService sottoscrittoreService;
	
	@Inject
	private AttiOdgRepository attiOdgRepository;
	
	@Inject
	private EsitoRepository esitoRepository;
	
	@Inject 
	private VerbaleService verbaleService;
	
	@Inject
	private ProgressivoSedutaRepository progressivoSedutaRepository;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private DestinatarioInternoService destinatarioInternoService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private ReportService reportService;
	
	@Inject
    private TipoDocumentoRepository tipoDocumentoRepository;
	
	@Inject
	private DocumentoPdfRepository documentoPdfRepository;
	
	@Inject
	private InsediamentoConsiglioRepository insediamentoConsiglioRepository;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private BpmWrapperUtil camundaUtil;
	
	@Inject
	private HistoryService historyService;
	
	@Transactional
	public void aggiornaStatoSeduta(Long sedutaId, int direzione, SedutaGiuntaConstants.statiSeduta statoDaAggiornare, Long profiloId) {
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaId);
		if(seduta!=null) {
			SedutaGiuntaConstants.statiSeduta statoAttuale = SedutaGiuntaConstants.getStatoSedutaByDescrizione(seduta.getStato(), seduta.getOrgano());
			if(statoAttuale!=null && statoDaAggiornare != null && statoDaAggiornare.getOrdine(seduta.getOrgano()) == (statoAttuale.getOrdine(seduta.getOrgano()) + direzione)) {
				if(SedutaGiuntaConstants.statiSeduta.sedutaConclusaNumerazioneConfermata.name().equalsIgnoreCase(statoDaAggiornare.name())) {
					seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResInPredisposizione.toString());
					seduta.setSottoscittoreDocumento(null);
				}
				
				seduta.setStato(statoDaAggiornare.toStringByOrgano(seduta.getOrgano()));
				sedutaGiuntaRepository.save(seduta);
			}else {
				throw new RuntimeException("Impossibile aggiornare lo stato della seduta. Si prega riprovare.");
			}
		}
	}

	@Transactional(readOnly = true)
	public Iterable<SedutaGiunta> findAllRiferimentoConsentiti(Long id) {
		Set<Long> ids = new HashSet<Long>();
		Iterable<SedutaGiunta> l = null;
		if (id != null) {
			sedutaGiuntaRepository.findOne(id);

			ids.add(id);
//			if (padre != null) {
//				ids.add(padre.getId());
//				ricorsivaControlloPadri(padre.getSedutariferimento(), ids);
//			}
			
			l = sedutaGiuntaRepository.findAll(QSedutaGiunta.sedutaGiunta.id.notIn(ids));
		} else {
			l = sedutaGiuntaRepository.findAll();
		}

		for (SedutaGiunta s : l) {
			s.setPresidente(null);
			s.setSedutaGiuntas(null);
			s.setSedutariferimento(null);
			s.setSegretario(null);

		}

		return l;
	}
	
	private BooleanExpression buildSottoscrittoreCriteria(final Long sottoscrittoreProfiloId){
		BooleanExpression predicate = QSedutaGiunta.sedutaGiunta.presidente.isNotNull();
		
		predicate = predicate.and(QSedutaGiunta.sedutaGiunta.presidente.id.eq(sottoscrittoreProfiloId))
				             .or(QSedutaGiunta.sedutaGiunta.segretario.id.eq(sottoscrittoreProfiloId))
				             .or(QSedutaGiunta.sedutaGiunta.sottoscrittoreDocVariazione.id.eq(sottoscrittoreProfiloId))
				             .or(QSedutaGiunta.sedutaGiunta.sottoscrittoreDocAnnullamento.id.eq(sottoscrittoreProfiloId))
				             .or(QSedutaGiunta.sedutaGiunta.odgs.any().sottoscrittore.id.eq(sottoscrittoreProfiloId))
				             .or(QSedutaGiunta.sedutaGiunta.verbale.sottoscrittori.any().profilo.id.eq(sottoscrittoreProfiloId));
		
		BooleanExpression sottoscrittoreGiuntaPresidente = QComponentiGiunta.componentiGiunta.id.isNotNull()
				.and(QComponentiGiunta.componentiGiunta.profilo.id.eq(sottoscrittoreProfiloId))
				.and(QComponentiGiunta.componentiGiunta.isPresidenteFine.eq(true));
		BooleanExpression sottoscrittoreGiuntaSegretario = QComponentiGiunta.componentiGiunta.id.isNotNull()
				.and(QComponentiGiunta.componentiGiunta.profilo.id.eq(sottoscrittoreProfiloId))
				.and(QComponentiGiunta.componentiGiunta.isSegretarioFine.eq(true));
				
		predicate = predicate.or(sottoscrittoreGiuntaPresidente.or(sottoscrittoreGiuntaSegretario));
		
		return predicate;
	}
	
	@Transactional(readOnly=true)
	public Page<SedutaGiunta> findAll(final Pageable generatePageRequest, final SedutaCriteriaDTO criteria){
		
		BooleanExpression expression = QSedutaGiunta.sedutaGiunta.id.isNotNull();
		
		// IN ATTICO NON PREVISTA FIRMA
		/*
		if(criteria.getType() != null && criteria.getType().equals("sedutafirma")){
			List<String> stati = new ArrayList<String>();
			
			stati.add(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgOdlBase.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.G.toString()));
			stati.add(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgOdlBase.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.C.toString()));
			stati.add(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione.toString());
			stati.add(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString());
			stati.add(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.toString());
			
			
			BooleanExpression statoOdgExpression = QSedutaGiunta.sedutaGiunta.odgs.any().oggetto.equalsIgnoreCase(
					SedutaGiuntaConstants.statiOdgOdl.odgOdlInAttesaDiFirma.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.G.toString()))
					.or(
					QSedutaGiunta.sedutaGiunta.odgs.any().oggetto.equalsIgnoreCase(
					SedutaGiuntaConstants.statiOdgOdl.odgOdlInAttesaDiFirma.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.C.toString())));
			
			expression = expression.and(QSedutaGiunta.sedutaGiunta.stato.in(stati));
			// .or(statoOdgExpression));
		}
		else{
		*/
			if(criteria.getStato() != null && !criteria.getStato().isEmpty()){
				expression = expression.and(QSedutaGiunta.sedutaGiunta.stato.equalsIgnoreCase(criteria.getStato()));
			}
//		}
		
		if(criteria.getProfiloId() != null && criteria.getProfiloId() != 0){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.presidente.id.eq(criteria.getProfiloId()));
		}
		
		if(criteria.getNumOdg() != null && !criteria.getNumOdg().equals("")){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.numero.equalsIgnoreCase(criteria.getNumOdg()));
		}
		if(criteria.getTipoSeduta() != null && !criteria.getTipoSeduta().equals("")){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.tipoSeduta.eq(Integer.valueOf(criteria.getTipoSeduta())));
		}
		if(criteria.getPresidente() != null && !criteria.getPresidente().equals("")){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.presidente.utente.username.containsIgnoreCase(criteria.getPresidente()));
		}
		if(criteria.getEstremiVariati() != null){
			BooleanExpression internalPredicate;
			if (criteria.getEstremiVariati()){
				internalPredicate = QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNotNull()
								.or(QSedutaGiunta.sedutaGiunta.secondaConvocazioneLuogo.isNotNull());				
			} else {
				internalPredicate = QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNull()
						.and(QSedutaGiunta.sedutaGiunta.secondaConvocazioneLuogo.isNull());
			}
			
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}
		if(criteria.getDecorsiTermini() != null){
			BooleanExpression internalPredicate = QSedutaGiunta.sedutaGiunta.fase.equalsIgnoreCase(SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA.toString());
			DateTime now = DateTime.now();
			if (criteria.getDecorsiTermini()){
				// Recupero solo le sedute per le quali sono decorsi i termini, ovvero quelle per le quali 
				// la data di seconda convocazione Ã¨ valorizzata ed Ã¨ minore della data odierna,
				// oppure se la data di prima convocazione Ã¨ minore della data odierna
				internalPredicate = internalPredicate.and((QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNotNull().and(QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.lt(now)))
						.or(QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNull().and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.lt(now))));
			} else {
				// Recupero solo le sedute per le quali NON sono decorsi i termini, ovvero quelle per le quali 
				// la data di seconda convocazione Ã¨ valorizzata ed Ã¨ maggiore della data odierna,
				// oppure se la data di prima convocazione Ã¨ maggiore della data odierna
				internalPredicate = internalPredicate.and((QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNotNull().and(QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.gt(now)))
						.or(QSedutaGiunta.sedutaGiunta.secondaConvocazioneInizio.isNull().and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.gt(now))));
			}
			
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}
		
		if(criteria.getPrimaConvInizioDa() != null) {  //  && !"".equals(criteria.getPrimaConvInizioDa())
			expression = expression.and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.goe(criteria.getPrimaConvInizioDa()));
		}
		if(criteria.getPrimaConvInizioA() != null) {  //  && !"".equals(criteria.getPrimaConvInizioA())
			expression = expression.and(QSedutaGiunta.sedutaGiunta.primaConvocazioneInizio.loe(criteria.getPrimaConvInizioA()));
		}
		
		if(criteria.getSottoscrittoreProfiloId() != null){
			BooleanExpression internalPredicate = buildSottoscrittoreCriteria(criteria.getSottoscrittoreProfiloId());
			
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}
		
		if(criteria.getStatoDocumento() != null && !criteria.getStatoDocumento().equals("")){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.statoDocumento.eq(criteria.getStatoDocumento()));
		}
		
		if(criteria.getSottoscrittoreDocumento() != null && !criteria.getSottoscrittoreDocumento().equals("")){
			expression = expression.and(QSedutaGiunta.sedutaGiunta.sottoscittoreDocumento.utente.username.containsIgnoreCase(criteria.getSottoscrittoreDocumento()));
		}
		
		if (!StringUtil.isNull(criteria.getOrgano())) {
			expression = expression.and(QSedutaGiunta.sedutaGiunta.organo.equalsIgnoreCase(criteria.getOrgano()));
		}
		
		Page<SedutaGiunta> listaSedute = null;
		
		if(criteria.getEcludiInfoAggiuntive()==null || !criteria.getEcludiInfoAggiuntive()) {
			listaSedute = sedutaGiuntaRepository.findAllInnerJoinAtto(expression, generatePageRequest);
			
			for (SedutaGiunta s : listaSedute) {
				minimalSedutaInfoLoad(s, false);
			}
			
		}else {
			listaSedute = sedutaGiuntaRepository.findAll(expression, generatePageRequest);
			for (SedutaGiunta s : listaSedute) {
				minimalSedutaInfoLoad(s, true);
			}
		}
		
		return listaSedute;
	}
	
	private void minimalSedutaInfoLoad(final SedutaGiunta seduta, final boolean ecludiInfoAggiuntive){

		
		if (seduta.getPresidente() != null) {
			serializeProfilo(seduta.getPresidente());
		}
		if (seduta.getSegretario() != null) {
			serializeProfilo(seduta.getSegretario());
		}
		if (seduta.getVicepresidente() != null) {
			serializeProfilo(seduta.getVicepresidente());
		}
		if (seduta.getSottoscittoreDocumento() != null) {
			serializeProfilo(seduta.getSottoscittoreDocumento());
		}
//		String statoDocumento = new String();
//		String sottoscrittoreDoc = new String();
		if(!ecludiInfoAggiuntive) {
			Set<OrdineGiorno> odgs = seduta.getOdgs();
			for (OrdineGiorno og : odgs) {
				List<AttiOdg> attos = og.getAttos();
				og.setSedutaGiunta(null);
				og.setDocumentiPdf(null);
				
				for (AttiOdg atto : attos) {
					atto.setOrdineGiorno(null);
					atto.setComponenti(null);
					minimalInfoLoad(atto.getAtto(), false, false, false);
				}
	//			if(og.getOggetto().equals(SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma.toString())){
	//				statoDocumento = SedutaGiuntaConstants.statiOdg.odgInAttesaDiFirma.toString() + " ("+ og.getTipoOdg().getDescrizione() +" )";
	//				sottoscrittoreDoc = og.getSottoscrittore().getUtente().getCognome()+" "+og.getSottoscrittore().getUtente().getNome();
	//			}
			}
		}else {
			seduta.setOdgs(null);
		}
		
//		if(seduta.getResoconto() != null && seduta.getResoconto().size() > 0){
//			for(Resoconto res:seduta.getResoconto()){
//				if(res.getStato() != null && res.getStato().equals(SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma.toString())){
//					statoDocumento = SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma.toString();
//					sottoscrittoreDoc = res.getSottoscrittore().getUtente().getCognome()+" "+res.getSottoscrittore().getUtente().getNome();
//				}
//				if(res.getStato() != null && res.getStato().equals(SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma.toString())){
//					statoDocumento = SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma.toString();
//					sottoscrittoreDoc = res.getSottoscrittore().getUtente().getCognome()+" "+res.getSottoscrittore().getUtente().getNome();
//				}
//			}
//		}
		
//		if(seduta.getVerbale() != null && seduta.getVerbale().getStato().equals(SedutaGiuntaConstants.statiVerbale.verbaleInAttesaDiFirma.toString())){
//			statoDocumento = SedutaGiuntaConstants.statiVerbale.verbaleInAttesaDiFirma.toString();
//		//	SottoscrittoreSedutaGiunta so = sottoscrittoreService.getNextFirmatarioVerbale(seduta.getVerbale().getId());
//		//	sottoscrittoreDoc = so.getProfilo().getUtente().getCognome()+" "+so.getProfilo().getUtente().getNome();
//			for(SottoscrittoreSedutaGiunta so:seduta.getVerbale().getSottoscrittori()){
//				if(!so.getFirmato()){
//					sottoscrittoreDoc = so.getProfilo().getUtente().getCognome()+" "+so.getProfilo().getUtente().getNome();
//				}
//			}
//		}
		
//		if(seduta.getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString())){
//			statoDocumento = SedutaGiuntaConstants.statiDocSeduta.docAnnInAttesaDiFirma.toString();
//			sottoscrittoreDoc = seduta.getSottoscrittoreDocAnnullamento().getUtente().getCognome()+" "+seduta.getSottoscrittoreDocAnnullamento().getUtente().getNome();
//		}
//		if(seduta.getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione)){
//			statoDocumento = SedutaGiuntaConstants.statiDocSeduta.docVarInAttesaDiFirma.toString();
//			sottoscrittoreDoc = seduta.getSottoscrittoreDocVariazione().getUtente().getCognome()+" "+seduta.getSottoscrittoreDocVariazione().getUtente().getNome();
//		}
		
//		seduta.setStatoDocumento(statoDocumento);
//		seduta.setSottoscittoreDocumento(sottoscrittoreDoc);
			
		seduta.setVerbale(null);
		seduta.setSedutariferimento(null);
		seduta.setSedutaGiuntas(null);
		seduta.setDestinatariInterni(null);
		seduta.setResoconto(null);
		seduta.setSottoscrittoreDocAnnullamento(null);
		seduta.setSottoscrittoreDocVariazione(null);
	}
	
	private void minimalInfoLoad(final Atto atto, boolean includiTaskAttivi, boolean includiAvanzamento, boolean escludiPareri) {
		atto.setSedutaGiunta(null);
		atto.setMotivazione(null);
		atto.setPreambolo( null);
		atto.setDispositivo( null);
		atto.setDomanda(null);
		atto.setDichiarazioni( null);
		atto.setAdempimentiContabili( null);
		atto.setInformazioniAnagraficoContabili(null);
		atto.setGaranzieRiservatezza(null);
		atto.setSottoscrittori(null);
		if(atto.getProfilo()!=null) {
			atto.setProfilo(DomainUtil.minimalProfilo(atto.getProfilo()));
		}
		if(atto.getEmananteProfilo()!=null) {
			atto.setEmananteProfilo(DomainUtil.minimalProfilo(atto.getEmananteProfilo()));
		}
		if(atto.getRupProfilo()!=null) {
			atto.setRupProfilo(DomainUtil.minimalProfilo(atto.getRupProfilo()));
		}
		
		if(includiAvanzamento) {
			if(atto.getAvanzamento() != null){
				for(Avanzamento av : atto.getAvanzamento()){
					av.setAtto(null);
				}
			}
		}else {
			atto.setAvanzamento(null);
		}
		// ATTICO: l'area corrisponde con l'Aoo padre, non viene sovrascritta
//		if(atto.getAoo() != null){
//			atto.setCodiceArea(atto.getAoo().getCodice());
//			atto.setDescrizioneArea(atto.getAoo().getDescrizione());
//		}
		
		if (includiTaskAttivi) {
			workflowService.loadAllTasks(atto);
		}
		
		atto.setAoo(null);
		atto.setProfilo(null);
		atto.setTipoMateria(null);
		atto.setOrdineGiornos(null);
		atto.setAllegati(null);
		atto.setDocumentiPdf(null);
		atto.setDocumentiPdfAdozione(null);
		atto.setDocumentiPdfAdozioneOmissis(null);
		atto.setDocumentiPdfOmissis(null);
		atto.setDatiContabili(null);
		
		if(!escludiPareri) {
			atto.setPareri(null);
		}
		
	}
	
	@Transactional(readOnly = true)
	public SedutaGiunta findOne(Long id,Long profiloId) throws ServiceException {
		log.debug("findOne:" + id);
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(id);
		if (seduta != null) {

//			if (prof.getComponentiGiunta() != null) {
//				for (Profilo profilo : prof.getComponentiGiunta()) {
//					serializeProfilo(profilo);
//				}
//			}
			seduta.setDestinatariInterni(destinatarioInternoService.getDestinatariInterniByAttoId(seduta.getId(),false));
			
			if (seduta.getPresidente() != null) {
				serializeProfilo(seduta.getPresidente());
			}
			
			if (seduta.getVicepresidente() != null) {
				serializeProfilo(seduta.getVicepresidente());
			}

			if (seduta.getSegretario() != null) {
				serializeProfilo(seduta.getSegretario());
			}

			if (seduta.getSedutariferimento() != null) {
				seduta.setSedutariferimento(new SedutaGiunta(seduta
						.getSedutariferimento().getId(), seduta
						.getSedutariferimento().getLuogo(), seduta
						.getSedutariferimento().getPrimaConvocazioneInizio()));
			}
			
			Set<RubricaDestinatarioEsterno> rubriche = seduta.getRubricaSeduta();
			
			for(RubricaDestinatarioEsterno rubrica : rubriche) {
				rubrica.setAoo(null);
			}
			
			if(seduta.getResoconto() != null){
				for (Resoconto r : seduta.getResoconto()) {
					r.setSedutaGiunta(null);
					log.debug("Documenti Resoconto:" + r.getDocumentiPdf().size());
					
					if(r.getDocumentiPdf()  != null  ){
						for (DocumentoPdf documentoPdf : r.getDocumentiPdf() ) {
							documentoPdf = getMinimalDocumentoPdf(documentoPdf);
						}
					}
				}
			}
			
			Set<OrdineGiorno> odgs = seduta.getOdgs();
			log.debug("Lista ODGS:" + odgs.size());
			
			boolean setNumeroArgomento = false;
			boolean setNumeroDiscussione = SedutaGiuntaConstants.statiSeduta.sedutaConclusaEsitiConfermati
												.toString().equalsIgnoreCase(StringUtil.trimStr(seduta.getStato()));
			
//			int valNumeroArgomento = 1;
			int valNumeroDiscussione = 1;
			
			
			if( SedutaGiuntaConstants.organoSeduta.C.name().equalsIgnoreCase(seduta.getOrgano()) &&
				SedutaGiuntaConstants.statiSeduta.sedutaConsolidata.toString()
					.equalsIgnoreCase(StringUtil.trimStr(seduta.getStato()))) {
				setNumeroArgomento = true;
				/* CDFATTICOMEV-55
				List<SedutaGiunta> ultimeSedute = sedutaGiuntaRepository.getLastSeduteByFase(
						SedutaGiuntaConstants.fasiSeduta.CONCLUSA.toString(), 
						SedutaGiuntaConstants.organoSeduta.C.name());
				
				if ( (ultimeSedute != null) && (ultimeSedute.size() > 0) ) {
					Integer maxNumeroArgomento = sedutaGiuntaRepository
							.getMaxNumeroArgomentoByIdSeduta(ultimeSedute.get(0).getId().longValue());
					
					if (maxNumeroArgomento != null && maxNumeroArgomento.intValue() > 1) {
						valNumeroArgomento = maxNumeroArgomento.intValue() + 1;
					}
				}
				*/
			}
			
			
			String codiceTaskAssessore = WebApplicationProps.getProperty(ConfigPropNames.VISTO_ASSESSORE_CODICE_CONFIGURAZIONE);
			boolean isSedutaGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(seduta.getOrgano());
			
			for (OrdineGiorno odg : odgs) {
				
				if(odg!=null && odg.getPreambolo()!=null) {
					odg.getPreambolo().getTesto();
				}
				
				//Documenti
				log.debug("Lista docs:" + odg.getDocumentiPdf().size());
				if(odg.getDocumentiPdf()  != null  ){
					for (DocumentoPdf documentoPdf : odg.getDocumentiPdf() ) {
						documentoPdf = getMinimalDocumentoPdf(documentoPdf);
					}

				}
				odg.setSedutaGiunta(null);
				
				List<AttiOdg> attos = new ArrayList<AttiOdg>();
				List<AttiOdg> attiUnordered = odg.getAttos();
				
				//se il numeroDiscussione non presente allora ordino in base all'inserimento in odg
				boolean hasNumeroDiscussione = true;
				for (AttiOdg attoOdg : attiUnordered) {
					if(attoOdg.getNumeroDiscussione()==null) {
						hasNumeroDiscussione = false;
						break;
					}
				}
				
				if(!hasNumeroDiscussione) {
					//Add Atti OdgBase
					List<AttiOdg> attiBase = new ArrayList<AttiOdg>();
					for (AttiOdg attoOdg : attiUnordered) {
						if(attoOdg.getOrdineGiorno().getTipoOdg().getId() == 1 ||
						   attoOdg.getOrdineGiorno().getTipoOdg().getId() == 2) {
							attiBase.add(attoOdg);
						}
					}
					//Order by ordine odg
					Collections.sort(attiBase,new Comparator<AttiOdg>(){
						@Override
						public int compare(AttiOdg atto1, AttiOdg atto2) {
							return atto1.getOrdineOdg().compareTo(atto2.getOrdineOdg());
						}
					});
					
					//Add Atti Suppletivo
					List<AttiOdg> attiSuppletivo = new ArrayList<AttiOdg>();
					for (AttiOdg attoOdg : attiUnordered) {
						if(attoOdg.getOrdineGiorno().getTipoOdg().getId() == 3) {
							attiSuppletivo.add(attoOdg);
						}
					}
					//Order by ordine odg
					Collections.sort(attiSuppletivo,new Comparator<AttiOdg>(){
						@Override
						public int compare(AttiOdg atto1, AttiOdg atto2) {
							return atto1.getOrdineOdg().compareTo(atto2.getOrdineOdg());
						}
					});
					
					//Add Atti Suppletivo
					List<AttiOdg> attiFuoriSacco = new ArrayList<AttiOdg>();
					for (AttiOdg attoOdg : attiUnordered) {
						if(attoOdg.getOrdineGiorno().getTipoOdg().getId() == 4) {
							attiFuoriSacco.add(attoOdg);
						}
					}
					//Order by ordine odg
					Collections.sort(attiFuoriSacco,new Comparator<AttiOdg>(){
						@Override
						public int compare(AttiOdg atto1, AttiOdg atto2) {
							return atto1.getOrdineOdg().compareTo(atto2.getOrdineOdg());
						}
					});
					
					attos.addAll(attiBase);
					attos.addAll(attiSuppletivo);
					attos.addAll(attiFuoriSacco);
				}
				else {
					attos.addAll(attiUnordered);
				}
				
				//per filtro odl pareri commissione
				String codiceTipoParereCommCons = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO);
				String codiceTipoParereQuartRev = WebApplicationProps.getProperty(ConfigPropNames.PARERE_QUARTIERE_REVISORE_CODICE_TIPO);

				for (AttiOdg atto : attos) {
					
					// Per filtro assessore proponente
					if (isSedutaGiunta) {
						// Per il filtro su assessore proponente
						@SuppressWarnings("rawtypes")
						List list = configurazioneIncaricoService.getMinimalProfiliIncaricati(codiceTaskAssessore, atto.getAtto().getId().longValue());
						if(list!= null) {
							atto.getAtto().setObjs(list);
							String nomiProponenti = "";
							for (Object prof : atto.getAtto().getObjs()) {
								String valAssessore = ((Profilo) prof).getUtente().getCognome() + " " + ((Profilo) prof).getUtente().getNome();
								if (nomiProponenti.length() > 0) {
									nomiProponenti += ", ";
								}
								nomiProponenti += valAssessore;
							}
							atto.getAtto().setAssessoreProponente(nomiProponenti);
						}
					}else {
						@SuppressWarnings("rawtypes")
						List list = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getAtto().getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
						if(list!= null) {
							atto.getAtto().setObjs(list);
						}
					}
					
					long ultimoIdOdg = atto.getId().longValue();
					for(AttiOdg checkOdg : atto.getAtto().getOrdineGiornos()) {
						if (checkOdg.getId().longValue() > ultimoIdOdg) {
							ultimoIdOdg = checkOdg.getId().longValue();
						}
					}
					
					atto.setAssenti(this.getComponentiAssenti(atto.getComponenti()));
					this.setPresidenteSegretario(atto);
					
					atto.setComponenti(null);
					atto.setOrdineGiorno(null);
					
					minimalInfoLoad(atto.getAtto(), true, false, true);
					if(atto.getAtto() != null && atto.getAtto().getPareri()!= null && !atto.getAtto().getPareri().isEmpty()) {
						Set<Parere> pareri = new HashSet<Parere>();
						for (Parere par : atto.getAtto().getPareri()) {
							Parere newPar = new Parere();
							newPar.setId(par.getId());
							newPar.setAoo(par.getAoo());
							newPar.setAnnullato(par.getAnnullato());
							newPar.setTipoAzione(par.getTipoAzione());
							newPar.setParerePersonalizzato(par.getParerePersonalizzato());
							newPar.setParereSintetico(par.getParereSintetico());
							newPar.setDataScadenza(par.getDataScadenza());
							newPar.setData(par.getData());
							pareri.add(newPar);
						}
						atto.getAtto().setPareri(pareri);
					}
					
					//PARERI COMMISSIONE 
					if(!isSedutaGiunta) {
						// resetto le liste 
						boolean aggiunto = false;
						boolean parQuarNotReq = true;
						boolean parQuarAllEspr = true;
						Boolean tuttiIPareriQuartieriScaduti = null;
						boolean almenoUnParereEspresso = false;
						List<Long>idsAooPareri= new ArrayList<Long>();
						List<Long>idsAooPareriEspressi= new ArrayList<Long>();
						List<Long>idsAooTask= new ArrayList<Long>();
						List<Long>idsAooInfo= new ArrayList<Long>();
						
						if(atto.getAtto().getPareri()!= null && !atto.getAtto().getPareri().isEmpty()) {
							for (Parere parere : atto.getAtto().getPareri()) {
								if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null) {
									if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
										idsAooPareri.add(parere.getAoo().getId());
										if(!StringUtils.isEmpty(parere.getParereSintetico())) {
											almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
									}
									else if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereQuartRev)) {
										if(!StringUtils.isEmpty(parere.getParereSintetico())) {
											almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
										parQuarNotReq = false;
										if(StringUtils.isEmpty(parere.getParereSintetico())){
											parQuarAllEspr = false;
										}
										if((tuttiIPareriQuartieriScaduti == null || tuttiIPareriQuartieriScaduti == true) && parere.getDataScadenza()!= null && parere.getDataScadenza().isBeforeNow()) {
											tuttiIPareriQuartieriScaduti = true;
										}else {
											tuttiIPareriQuartieriScaduti = false;
										}
									}
								}
							}
						}
						List<Task> listTask = workflowService.getAllTasks(atto.getAtto().getId());
						if(listTask!= null && !listTask.isEmpty()) {
							for (Task task : listTask) {
								if(task.getName() != null && task.getName().toLowerCase().contains("commissione")) {
									Aoo aoo = null;
									if (!StringUtil.isNull(task.getAssignee())) {
										aoo = camundaUtil.getAoo(task.getAssignee());
									}
									else{
										List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
												.taskId(task.getId()).orderByTime().desc().list();
										for (HistoricIdentityLinkLog identityLink : identityLinks) {
											if ( "candidate".equalsIgnoreCase(identityLink.getType()) &&
													!StringUtil.isNull(identityLink.getGroupId())) {
												
												List<Aoo> candidates = camundaUtil.getAooByCandidate(identityLink.getGroupId());
												if (candidates != null && candidates.size() > 0) {
													aoo = candidates.get(0);
													break;
												}
											}
										}
									}
									if(aoo!= null && aoo.getId() != null) {
										idsAooTask.add(aoo.getId());
									}
								}
							}
						}
						List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(atto.getAtto().getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
						
						Boolean tuttiScaduti = null;
						if(listInc != null) {
							for (ConfigurazioneIncaricoDto inc : listInc) {
								if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
									for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
											idsAooInfo.add(incAoo.getIdAoo());
											if(incAoo.getDataManuale()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataManuale().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScaduti = true;
												}else {
													tuttiScaduti = false;
												}
											}
											else if(incAoo.getDataCreazione()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScaduti == null || tuttiScaduti == true) && incAoo.getDataCreazione().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScaduti = true;
												}else {
													tuttiScaduti = idsAooPareriEspressi.contains(incAoo.getIdAoo());
												}
											}
									}
								}
							}
						}
						
						//Setto i filtri per i flag frontend 
						atto.getAtto().setParComAll(false);
						atto.getAtto().setParComNotReq(false);
						atto.getAtto().setParComExpired(false);
						
						if(idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty() && parQuarNotReq) {
							atto.getAtto().setParComNotReq(true);
						}
						
						if(!almenoUnParereEspresso) {
							//parComNotReq: Parere Comm. \ Cons. Quart. \ Rev. Cont. non richiesto
							boolean controlloPareriNonRichiesti = false;
							
							if(idsAooPareri.isEmpty() && idsAooTask.isEmpty() && idsAooInfo.isEmpty() && parQuarNotReq) {
								controlloPareriNonRichiesti = true;
							}
							
							if(controlloPareriNonRichiesti) {
								//Setto il filtro per i flag frontend
								atto.getAtto().setParComNotReq(true);
							}
						}
						
						if(almenoUnParereEspresso) {//parComAll: Parere espresso da tutte le Comm. \ Cons. Quart. \ Rev. Cont. previsti
							//si entra qui con il presupposto che sia stato espresso almenoUnParere
							
							boolean controlloTuttiIParereEspressi = parQuarNotReq || (!parQuarNotReq && parQuarAllEspr);
							
							//arrivati a questo punto controlloparComAll Ã¨ true se i pareri Quart non erano richiesti o se erano richiesti e sono stati tutti inseriti
							//controlloparComAll == false quando pareri Quart erano richiesti ma non sono stati inseriti tutti
							if(controlloTuttiIParereEspressi) {
							
								//inizio il controllo sui pareri commissione
								if(!idsAooPareri.isEmpty() && idsAooTask.isEmpty() && !idsAooInfo.isEmpty()) {
									if(!idsAooPareri.isEmpty() && !idsAooInfo.isEmpty()) {
										for (Long idAooInfo : idsAooInfo) {
											if(!idsAooPareri.contains(idAooInfo)) {
												controlloTuttiIParereEspressi = false ;
												break;
											}
										}
									}
								}
							
							}
							if(controlloTuttiIParereEspressi) {
								//Setto il filtro per i flag frontend 
								atto.getAtto().setParComAll(true);
							}
						}
						
						boolean controlloTerminiScadutiPerTuttiIPareriPrevisti = false;
						
						if( (tuttiScaduti!=null || tuttiIPareriQuartieriScaduti !=null) &&   (tuttiScaduti== null || (tuttiScaduti!= null && tuttiScaduti.booleanValue())) && (tuttiIPareriQuartieriScaduti == null || (tuttiIPareriQuartieriScaduti!= null && tuttiIPareriQuartieriScaduti.booleanValue()))) {
							controlloTerminiScadutiPerTuttiIPareriPrevisti = true;
						} 
						if(controlloTerminiScadutiPerTuttiIPareriPrevisti) {
							
							//Setto il filtro per i flag frontend
							atto.getAtto().setParComExpired(true);
						}

					}
					
					if (profiloId != null) {
						atto.setNextTaskId(workflowService.getMyNextTaskId(
								atto.getAtto().getId(), profiloId));
					}
					
					//  CASISTICA ELIMINATA
					// atto.setNumerabile(workflowService.isAttoNumerabile(atto.getAtto().getId()));
					atto.setUltimoOdg(ultimoIdOdg == atto.getId().longValue());
					
					if (!SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice().equalsIgnoreCase(atto.getEsito())) {
						if (setNumeroArgomento) {
							List<Integer> valNumeroArgomentoAtto = attiOdgRepository.getLastNumeriArgomentoAtto(atto.getAtto().getId().longValue(), atto.getId());
							if ((valNumeroArgomentoAtto != null) && (valNumeroArgomentoAtto.size() > 0)) {
								atto.setNumeroArgomento(valNumeroArgomentoAtto.get(0).intValue());
								atto.setArgomentoExSeduta(true);
							}
							else {
								/* CDFATTICOMEV-55 La numerazione avviene tramite apposita funzionalita
								atto.setNumeroArgomento(valNumeroArgomento);
								valNumeroArgomento++;
								*/
							}
						}
						if (setNumeroDiscussione && ((atto.getNumeroDiscussione() == null) || (atto.getNumeroDiscussione().intValue() < 1))) {
							atto.setNumeroDiscussione(valNumeroDiscussione);
							valNumeroDiscussione++;
						}
					}
					
					/*Atto coll = attoRepository.findOne(QAtto.atto.codicecifraAttoCollegato.eq(atto.getAtto().getCodiceCifra()));
					if(coll != null){
						atto.getAtto().setCodicecifraAttoCollegato(coll.getId().toString());
					}
					else{
						atto.getAtto().setCodicecifraAttoCollegato(null);
					}*/
				}
				odg.setAttos(attos);
			}
			
			if(seduta.getVerbale() != null){
				seduta.setVerbale(verbaleService.svuotaRiferimenti(seduta.getVerbale()));
			}
			
			if(seduta.getSottoscrittoriresoconto() != null){
				seduta.setSottoscrittoriresoconto(sottoscrittoreService.svuotaRiferimenti(seduta.getSottoscrittoriresoconto()));
			}
		}
		return seduta;
	}
	
	public DateTime getPrimaConvocazioneFine(Long idSeduta) {
		DateTime primaConvocazioneFine = sedutaGiuntaRepository.getPrimaConvocazioneFine(idSeduta);
		return primaConvocazioneFine;
	}
	
	public Integer getNextNumeroArgomento(Long idSeduta) {
		int valNumeroArgomento = 1;
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(idSeduta);
		
		BooleanExpression predicate = QInsediamentoConsiglio.insediamentoConsiglio.inizioInsediamento.loe(seduta.getInizioLavoriEffettiva()).and(QInsediamentoConsiglio.insediamentoConsiglio.fineInsediamento.goe(seduta.getInizioLavoriEffettiva()));
		InsediamentoConsiglio insCons = insediamentoConsiglioRepository.findOne(predicate);
		//si recupera il numero di argomento in base al consiglio che l'ha lavorata
		
		Integer maxNumeroArgomento = sedutaGiuntaRepository.getMaxNumeroArgomentoInsediamento(insCons.getInizioInsediamento(), insCons.getFineInsediamento(), SedutaGiuntaConstants.organoSeduta.C.name());
		
		if (maxNumeroArgomento != null && maxNumeroArgomento.intValue() > 1) {
			valNumeroArgomento = maxNumeroArgomento.intValue() + 1;
		}
		
		return valNumeroArgomento;
	}
	
	public Boolean existsNumeroArgomentoInInsediamentoConsiglioOutOfSeduta(Long idSeduta, Integer numeroArgomento, String opNumArg) {
		Boolean exists = false;
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(idSeduta);
		
		BooleanExpression predicate = QInsediamentoConsiglio.insediamentoConsiglio.inizioInsediamento.loe(seduta.getInizioLavoriEffettiva()).and(QInsediamentoConsiglio.insediamentoConsiglio.fineInsediamento.goe(seduta.getInizioLavoriEffettiva()));
		InsediamentoConsiglio insCons = insediamentoConsiglioRepository.findOne(predicate);
		//si recupera il numero di argomento in base al consiglio che l'ha lavorata
		
		Integer num = null;
		if(opNumArg!=null && opNumArg.equalsIgnoreCase("eq")) {
			num = sedutaGiuntaRepository.getNumeroArgomentoInsediamentoEq(insCons.getInizioInsediamento(), insCons.getFineInsediamento(), SedutaGiuntaConstants.organoSeduta.C.name(), idSeduta, numeroArgomento);
		}else if(opNumArg!=null && opNumArg.equalsIgnoreCase("bt")) {
			num = sedutaGiuntaRepository.getNumeroArgomentoInsediamentoBt(insCons.getInizioInsediamento(), insCons.getFineInsediamento(), SedutaGiuntaConstants.organoSeduta.C.name(), idSeduta, numeroArgomento);
		}else if(opNumArg!=null && opNumArg.equalsIgnoreCase("lt")) {
			num = sedutaGiuntaRepository.getNumeroArgomentoInsediamentoLt(insCons.getInizioInsediamento(), insCons.getFineInsediamento(), SedutaGiuntaConstants.organoSeduta.C.name(), idSeduta, numeroArgomento);
		}else if(opNumArg!=null && opNumArg.equalsIgnoreCase("gt")) {
			num = sedutaGiuntaRepository.getNumeroArgomentoInsediamentoGt(insCons.getInizioInsediamento(), insCons.getFineInsediamento(), SedutaGiuntaConstants.organoSeduta.C.name(), idSeduta, numeroArgomento);
		}
		
		if(num!=null) {
			exists = true;
		}
		return exists;
	}
	
	@Transactional
	public void setStessoNumeroArgomento(Long sedutaId, List<Long> attiOdgIds, Long profiloId) {
		Integer nextNumber = this.getNextNumeroArgomento(sedutaId);
		if(nextNumber != null && nextNumber > 0) {
			if(attiOdgIds!=null && attiOdgIds.size() > 0) {
				Map<Long, Integer> attiOdgNumArgMap = new HashMap<Long, Integer>();
				for(Long id : attiOdgIds) {
					if(id!=null && id > 0L) {
						attiOdgNumArgMap.put(id, nextNumber);
					}
				}
				this.updateNumeriArgomento(attiOdgNumArgMap, profiloId);
			}
		}else {
			throw new GestattiCatchedException("Si Ã¨ verificato un errore nella numerazione. Si prega di riprovare.");
		}
	}
	
	public void setArgomentiProgressivi(Long sedutaId, List<Long> attiOdgIds, Long profiloId) {
		Integer nextNumber = this.getNextNumeroArgomento(sedutaId);
		if(nextNumber != null && nextNumber > 0) {
			if(attiOdgIds!=null && attiOdgIds.size() > 0) {
				
				Iterable<AttiOdg> it = attiOdgRepository.findAll(attiOdgIds);
				//TreeMap <numero discussione, Map<attoOdgId, numero argomento>
				TreeMap<Integer, Map<Long, Integer>> map = new TreeMap<Integer, Map<Long, Integer>>();
				for(AttiOdg aog : it) {
					map.put(aog.getNumeroDiscussione(), new HashMap<Long, Integer>());
					map.get(aog.getNumeroDiscussione()).put(aog.getId(), null);
				}
				for(Integer nDiscussione : map.keySet()) {
					Long aOdgId = map.get(nDiscussione).keySet().iterator().next();
					map.get(nDiscussione).put(aOdgId, new Integer(nextNumber));
					nextNumber++;
				}
				this.updateNumeriArgomentoSorted(map, profiloId);
			}
		}else {
			throw new GestattiCatchedException("Si Ã¨ verificato un errore nella numerazione. Si prega di riprovare.");
		}
	}
	
	private List<String> getComponentiAssenti(List<ComponentiGiunta> componenti) {
		List<String> assenti = new ArrayList<String>();
		if(componenti != null){
			for(ComponentiGiunta componente : componenti){
				if(Boolean.FALSE.equals(componente.getPresente()) && 
				   Boolean.FALSE.equals(componente.getIsPresidenteFine()) && 
				   Boolean.FALSE.equals(componente.getIsSegretarioFine())){
					assenti.add(componente.getProfilo().getUtente().getNome() + " " + componente.getProfilo().getUtente().getCognome());
				}
			}
		}
		
		return assenti;
	}
	
	private void setPresidenteSegretario(AttiOdg atto) {
		List<ComponentiGiunta> componenti = atto.getComponenti();
		
		if(componenti != null){
			for(ComponentiGiunta componente : componenti){
				if(Boolean.TRUE.equals(componente.getIsPresidenteFine())){
					atto.setPresidente(componente.getProfilo().getUtente().getNome() + " " + componente.getProfilo().getUtente().getCognome());
					atto.setPresidenteid(componente.getProfilo().getId());
				}
				else if(Boolean.TRUE.equals(componente.getIsSegretarioFine())){
					atto.setSegretario(componente.getProfilo().getUtente().getNome() + " " + componente.getProfilo().getUtente().getCognome());
					atto.setSegretarioid(componente.getProfilo().getId());
				}
			}
		}
	}
	
	//@Transactional
	public void chiusuraResoconto(Long sedutaId, Long profiloId) throws Exception {
		
		ArrayList<String> esitoNT = new ArrayList<String>();
		esitoNT.add(SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice());
		
		List<Atto> attiConfermati = attiOdgRepository
				.findAttoBySedutaGiuntaAndEsitoNotInOrderByDiscussione(sedutaId, esitoNT);
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaId);
		boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name()
				.equalsIgnoreCase(seduta.getOrgano());
		
		for (Atto atto : attiConfermati) {
			log.info("Atto Confermato. Codice Cifra: " + atto.getCodiceCifra() + " Esito: " + atto.getEsito());
			if (!StringUtil.isNull(atto.getEsito())) {
				workflowService.confermaEsitoSeduta(isOdgGiunta, 
						atto.getId().longValue(), profiloId.longValue(), null);
			}
			else throw new RuntimeException("Esito dell'atto non impostato!");
		}
		
		this.aggiornaStatoSeduta(sedutaId, 1, SedutaGiuntaConstants.statiSeduta.sedutaConclusaNumerazioneConfermata, profiloId);
	}
	
	
	@Transactional
	public Resoconto createResoconto(
			final long sedutaId, final TipoResocontoEnum tipo, Long idModelloHtml,
			final Long profiloId, boolean chiusuraResoconto) {
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaId);
		
		Resoconto resoconto = resocontoRepository.findBySedutagiuntaIdAndTipo(sedutaId, tipo.getId());		
		if(resoconto == null){
			resoconto = new Resoconto();
			resoconto.setTipo(tipo.getId());
			
			resoconto.setSedutaGiunta(seduta);
			resocontoRepository.save(resoconto);
		}
    	
		try {
			ReportDTO reportDto = new ReportDTO();
			reportDto.setIdModelloHtml(idModelloHtml);
			reportDto.setTipoDoc(tipo.name());
			reportDto.setIdSeduta(sedutaId);
	    	//List<OrdineGiorno> odgs = new ArrayList<OrdineGiorno>(seduta.getOdgs());
			File result = reportService.previewResoconto(reportDto);
			
			String createdBy = "";
			Profilo prof = profiloRepository.findOne(profiloId);
			if (prof != null) {
				createdBy = prof.getUtente().getCognome() + " " + prof.getUtente().getNome();
			}
			
			DocumentoPdf savedDocPdf = documentoPdfService.saveResocontoPdf(
					seduta, resoconto, result, tipo.getTipoDocumento(), createdBy);
			savedDocPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(tipo.getTipoDocumento().name()));
			documentoPdfRepository.save(savedDocPdf);
			
			if (chiusuraResoconto) {
				seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaConsolidata.toStringByOrgano(seduta.getOrgano()));
				seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaConsolidata.getFase());
				sedutaGiuntaRepository.save(seduta);
			}
		}
		catch (Exception e) {
			// Sollevo RuntimeException altrimenti Spring non annulla la transazione e gli stati diventano disallineati
			throw new RuntimeException("Errore durante la generazione del Resoconto.", e);
		}
		
		return resoconto;
	}
	
	
	protected DocumentoPdf getMinimalDocumentoPdf(DocumentoPdf documentoPdf){
		return documentoPdf;
	}

	@Transactional(readOnly = true)
	public Set<RubricaDestinatarioEsterno> getRubricaDestinatariEsterni(Long idSeduta){
		log.debug("getRubricaDestinatariEsterni - idSeduta:" + idSeduta);
		
		Set<RubricaDestinatarioEsterno> result = null;
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(idSeduta);
		if (seduta != null) {
			result = seduta.getRubricaSeduta();
		} else {
			log.error("Nessuna seduta trovata con id: " + idSeduta);
		}
		
		return result;
	}

	private void serializeProfilo(Profilo profilo) {
		profilo.setHasQualifica(null);
		profilo.setGrupporuolo(null);
		profilo.setTipiAtto(null);
		if (profilo.getUtente() != null)
			profilo.setUtente(new Utente(profilo.getUtente().getId(), profilo
					.getUtente().getCodicefiscale(), profilo.getUtente()
					.getUsername(),profilo.getUtente().getCognome(),profilo.getUtente().getNome()));
	}
	
	@Transactional
	public void annulla(SedutaAnnullaDTO sedutaGiunta,Long profiloId) throws GestattiCatchedException, IOException, DocumentException,Exception{
		
		log.debug("SedutaId: {}" + sedutaGiunta.getSedutaId());
		log.debug("ModelloId: {}" + sedutaGiunta.getModelloId());
		log.debug("ProfiloId: {}" + sedutaGiunta.getProfiloId());
		log.debug("ProfiloSottoscrittoreId: {}" + sedutaGiunta.getProfiloSottoscrittoreId());
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaGiunta.getSedutaId()); 
		seduta.setSottoscittoreDocumento(null);
		Profilo sottoscrittore = profiloRepository.findOne(sedutaGiunta.getProfiloSottoscrittoreId());
		if (sottoscrittore!=null){
			seduta.setSottoscrittoreDocAnnullamento(sottoscrittore);
			seduta.setSottoscittoreDocumento(sottoscrittore);
		}
		
		for(OrdineGiorno odg : seduta.getOdgs()){
			odg.setOggetto(SedutaGiuntaConstants.statiOdgOdl.odgOdlAnnullato.toStringByOrgano(odg.getSedutaGiunta().getOrgano()));
			ordineGiornoService.cancelArguments(odg.getId(),profiloId);
		}
		
		if(sedutaGiunta.getModelloId() != null){
			seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString());
			seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.getFase());
			
			// In ATTICO NON PREVISTA FIRMA
			// seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docAnnInAttesaDiFirma.toString());
		}
		else {
			seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.toString());
			seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.getFase());
			
			seduta.setStatoDocumento(null);
			seduta.setSottoscittoreDocumento(null);
		}
		
		sedutaGiuntaRepository.save(seduta);
		
		if(sedutaGiunta.getModelloId() != null){
			ReportDTO rep = new ReportDTO();
			for(OrdineGiorno odg : seduta.getOdgs()){
				if(odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2){
					rep.setIdAtto(odg.getId());
					break;
				}
			}
	    	
	    	rep.setIdModelloHtml(sedutaGiunta.getModelloId());
	    	
	    	Profilo profilo = profiloRepository.findOne(sedutaGiunta.getProfiloId());
	    	
	    	ordineGiornoService.generaDocumentoAnnullamento(rep,profilo, false, "", "");
		}
	}
	
	@Transactional
	public void variazione(SedutaAnnullaDTO sedutaGiunta) throws GestattiCatchedException, IOException, DocumentException, DmsException {
		
		log.debug("SedutaId: {}" + sedutaGiunta.getSedutaId());
		log.debug("ModelloId: {}" + sedutaGiunta.getModelloId());
		log.debug("ProfiloId: {}" + sedutaGiunta.getProfiloId());
		log.debug("ProfiloSottoscrittoreId: {}" + sedutaGiunta.getProfiloSottoscrittoreId());
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaGiunta.getSedutaId()); 
		seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docVarGenerato.toString());
		seduta.setSottoscittoreDocumento(null);
		Profilo sottoscrittore = profiloRepository.findOne(sedutaGiunta.getProfiloSottoscrittoreId());
		if (sottoscrittore!=null){
			seduta.setSottoscrittoreDocVariazione(sottoscrittore);
			seduta.setSottoscittoreDocumento(sottoscrittore);
		}
		sedutaGiuntaRepository.save(seduta);
		
		ReportDTO rep = new ReportDTO();
		for(OrdineGiorno odg : seduta.getOdgs()){
			if(odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2){
				rep.setIdAtto(odg.getId());
				break;
			}
		}
    	
    	rep.setIdModelloHtml(sedutaGiunta.getModelloId());
    	
    	Profilo profilo = profiloRepository.findOne(sedutaGiunta.getProfiloId());
    	
    	ordineGiornoService.generaDocumentoVariazione(rep,profilo, false, "", "");
		
	}
	
	/**
	 * Ritorna 'ok' se il check non ha rilevato problemi
	 * Ritorna i numero di argomento incriminati qualora sia stato rilevate possibilitÃ  di buchi
	 * @param attiOdgNumeroJsonArray
	 * @param sedutaId
	 * @return
	 */
	@Transactional
	public String checkResetNumeriArgomento(JsonArray attiOdgNumeroJsonArray, Long sedutaId) {
		String notCheck = "";
		TreeMap<Integer, List<Long>> toDeleteMap = new TreeMap<Integer, List<Long>>();
		TreeMap<Integer, List<Long>> notToDeleteMap = new TreeMap<Integer, List<Long>>();
		if(attiOdgNumeroJsonArray!=null) {
			for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
				Long id = jsonEl.getAsJsonObject().get("id").getAsLong();
				Integer numArg = null;
				if(jsonEl!=null && jsonEl.getAsJsonObject() !=null && jsonEl.getAsJsonObject().has("numArg") && jsonEl.getAsJsonObject().get("numArg") !=null && !jsonEl.getAsJsonObject().get("numArg").isJsonNull()) {
					numArg = jsonEl.getAsJsonObject().get("numArg").getAsInt();
				}
				Boolean toDelete = null;
				if(jsonEl!=null && jsonEl.getAsJsonObject() !=null && jsonEl.getAsJsonObject().has("toDelete") && jsonEl.getAsJsonObject().get("toDelete") !=null && !jsonEl.getAsJsonObject().get("toDelete").isJsonNull()) {
					toDelete = jsonEl.getAsJsonObject().get("toDelete").getAsBoolean();
				}
				if(toDelete != null && toDelete && numArg != null && numArg > 0) {
					if(!toDeleteMap.containsKey(numArg)) {
						toDeleteMap.put(numArg, new ArrayList<Long>());
					}
					toDeleteMap.get(numArg).add(id);
				}else if((toDelete==null || !toDelete) &&  numArg != null && numArg > 0) {
					if(!notToDeleteMap.containsKey(numArg)) {
						notToDeleteMap.put(numArg, new ArrayList<Long>());
					}
					notToDeleteMap.get(numArg).add(id);
				}
			}
		}
		
		int maxNumInSeduta = 0;
//		int minNumInSeduta = Integer.MAX_VALUE;
		if(notToDeleteMap!=null && notToDeleteMap.size() > 0) {
			maxNumInSeduta = notToDeleteMap.lastKey();
//			minNumInSeduta = notToDeleteMap.firstKey();
		}
		
		for(Integer numArgToDelete : toDeleteMap.descendingKeySet()) {
			//verifica se il numero di argomento Ã¨ replicato su un altro odg in questa seduta in tal caso possiamo cancellarlo
			if(!notToDeleteMap.containsKey(numArgToDelete)) {
				Boolean existsInOtherSedute = this.existsNumeroArgomentoInInsediamentoConsiglioOutOfSeduta(sedutaId, numArgToDelete, "eq");
				//verifica se il numero di argomento Ã¨ replicato su un altro odg di altra seduta in tal caso possiamo cancellarlo
				if(existsInOtherSedute==null || !existsInOtherSedute) {
					//verifica se in questa seduta c'Ã¨ un numero di argomento piÃ¹ grande, per cui cancellando questo si crea un buco
					if(maxNumInSeduta > numArgToDelete) {
						if(notCheck.length() > 0) {
							notCheck += ", ";
						}
						notCheck += numArgToDelete + "";
					}else {
						//verifica se in altre sedute ci sono numeri di argomento piÃ¹ grandi, per cui cancellando questo si crea un buco
						Boolean existsGreaterThanThis = this.existsNumeroArgomentoInInsediamentoConsiglioOutOfSeduta(sedutaId, numArgToDelete, "gt");
						if(existsGreaterThanThis!=null && existsGreaterThanThis) {
							if(notCheck.length() > 0) {
								notCheck += ", ";
							}
							notCheck += numArgToDelete + "";
						}
					}
				}
			}
		}
		
		if(notCheck.length() > 0) {
			return notCheck;
		}else {
			return "ok";
		}
	}
	
	@Transactional
	public void resetNumeriArgomento(JsonArray attiOdgNumeroJsonArray, Long profiloId) {
		if(attiOdgNumeroJsonArray!=null) {
			for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
				if(jsonEl!=null) {
					Boolean toDelete = jsonEl.getAsJsonObject().get("toDelete").getAsBoolean();
					if(toDelete!=null && toDelete) {
						Long id = jsonEl.getAsJsonObject().get("id").getAsLong();
						AttiOdg attiOdg = attiOdgRepository.findOne(id);
						Atto atto = null;
						Integer oldNumArg = attiOdg.getNumeroArgomento();
						if(attiOdg!=null) {
							atto = attiOdg.getAtto();
							attiOdg.setNumeroArgomento(null);
							attiOdgRepository.save(attiOdg);
							String note = "";
							if(oldNumArg!=null && oldNumArg.intValue() > 0) {
								note += "Il numero di argomento " + oldNumArg + " Ã¨ stato rimosso da questo atto.";
							}else {
								note += "Il numero di argomento Ã¨ stato rimosso.";
							}
							registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), profiloId, atto.getStato(), NomiAttivitaAtto.RIMOZIONE_NUMERO_ARGOMENTO, note);
						}
					}
				}
			}
		}
	}
	
	@Transactional
	public void updateNumeriArgomento(Map<Long, Integer> attiOdgNumArgMap, Long profiloId) {
		if(attiOdgNumArgMap!=null) {
			for(Long attiOdgId : attiOdgNumArgMap.keySet()) {
				if(attiOdgId!=null) {
					AttiOdg attiOdg = attiOdgRepository.findOne(attiOdgId);
					Atto atto = null;
					Integer oldNumArg = attiOdg.getNumeroArgomento();
					if(attiOdg!=null && attiOdgNumArgMap.get(attiOdgId)!=null && attiOdgNumArgMap.get(attiOdgId) > 0) {
						atto = attiOdg.getAtto();
						attiOdg.setNumeroArgomento(attiOdgNumArgMap.get(attiOdgId));
						attiOdgRepository.save(attiOdg);
						String note = "";
						if(oldNumArg!=null && oldNumArg.intValue() > 0) {
							note += "Il numero di argomento Ã¨ stato modificato da " + oldNumArg + " al nuovo numero " + attiOdgNumArgMap.get(attiOdgId);
						}else {
							note += "E' stato assegnato il numero di argomento " + attiOdgNumArgMap.get(attiOdgId);
						}
						registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), profiloId, atto.getStato(), NomiAttivitaAtto.ASSEGNAZIONE_NUMERO_ARGOMENTO, note);
					}
				}
			}
		}
	}
	
	@Transactional
	public void updateNumeriArgomentoSorted(TreeMap<Integer, Map<Long, Integer>> map, Long profiloId) {
		if(map!=null) {
			for(Integer numeroDiscussione : map.keySet()) {
				if(numeroDiscussione!=null) {
					Long attiOdgId = map.get(numeroDiscussione).keySet().iterator().next();
					AttiOdg attiOdg = attiOdgRepository.findOne(attiOdgId);
					Atto atto = null;
					Integer oldNumArg = attiOdg.getNumeroArgomento();
					if(attiOdg!=null && map.get(numeroDiscussione).get(attiOdgId)!=null && map.get(numeroDiscussione).get(attiOdgId) > 0) {
						atto = attiOdg.getAtto();
						attiOdg.setNumeroArgomento(map.get(numeroDiscussione).get(attiOdgId));
						attiOdgRepository.save(attiOdg);
						String note = "";
						if(oldNumArg!=null && oldNumArg.intValue() > 0) {
							note += "Il numero di argomento Ã¨ stato modificato da " + oldNumArg + " al nuovo numero " + map.get(numeroDiscussione).get(attiOdgId);
						}else {
							note += "E' stato assegnato il numero di argomento " + map.get(numeroDiscussione).get(attiOdgId);
						}
						registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), profiloId, atto.getStato(), NomiAttivitaAtto.ASSEGNAZIONE_NUMERO_ARGOMENTO, note);
					}
				}
			}
		}
	}
	
	@Transactional
	public void updateNumeriArgomento(JsonArray attiOdgNumeroJsonArray, Long profiloId) {
		for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
			Long attiOdgId = null;
			if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("id") && jsonEl.getAsJsonObject().get("id")!=null && !jsonEl.getAsJsonObject().get("id").isJsonNull()) {
				attiOdgId = jsonEl.getAsJsonObject().get("id").getAsLong();
			}
			
			Integer newNumArg = null;
			if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("newNumArg") && jsonEl.getAsJsonObject().get("newNumArg")!=null && !jsonEl.getAsJsonObject().get("newNumArg").isJsonNull()) {
				newNumArg = jsonEl.getAsJsonObject().get("newNumArg").getAsInt();
			}
			
			Integer oldNumArg = null;
			if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("numArg") && jsonEl.getAsJsonObject().get("numArg")!=null && !jsonEl.getAsJsonObject().get("numArg").isJsonNull()) {
				oldNumArg = jsonEl.getAsJsonObject().get("numArg").getAsInt();
			}
			
			Boolean isOde = null;
			if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("ode") && jsonEl.getAsJsonObject().get("ode")!=null && !jsonEl.getAsJsonObject().get("ode").isJsonNull()) {
				isOde = jsonEl.getAsJsonObject().get("ode").getAsBoolean();
			}
			
			Atto atto = null;
			if(attiOdgId!=null && attiOdgId > 0 && (newNumArg==null || newNumArg > 0)) {
				AttiOdg attiOdg = attiOdgRepository.findOne(attiOdgId);
				if(attiOdg!=null) {
					atto = attiOdg.getAtto();
					attiOdg.setNumeroArgomento(newNumArg);
					attiOdg.setNargOde(isOde);
					attiOdgRepository.save(attiOdg);
					String note = "";
					if(oldNumArg!=null && oldNumArg.intValue() > 0 && newNumArg != null) {
						note += "Il numero di argomento \u00E8 stato modificato da " + oldNumArg + " al nuovo numero " + newNumArg;
					}else if(oldNumArg!=null && oldNumArg.intValue() > 0 && newNumArg == null) {
						note += "Il numero di argomento " + oldNumArg + " \u00E8 stato rimosso";
					}else {
						note += "\u00C8 stato assegnato il numero di argomento " + newNumArg;
					}
					registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), profiloId, atto.getStato(), NomiAttivitaAtto.ASSEGNAZIONE_NUMERO_ARGOMENTO, note);
				}else {
					throw new GestattiCatchedException("Errore. Atto da aggiornare non trovato.");
				}
			}
		}
	}

	@Transactional
	public void save(SedutaGiunta sedutaGiunta, Long profiloId) throws Exception{
		
		Set<Long> ids = new HashSet<Long>();
		if(sedutaGiunta.getSedutariferimento() != null ){
			SedutaGiunta padre = sedutaGiuntaRepository.findOne(sedutaGiunta.getSedutariferimento().getId());

			ids.add(padre.getId());
			ricorsivaControlloPadri(padre, ids);
		}

		if (ids.contains(sedutaGiunta.getId())) {
			throw new RuntimeException("Padre non permesso, problema di ciclicita");
		}
		
		if (sedutaGiunta.getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.toString())) {
			if (sedutaGiunta.getSecondaConvocazioneInizio() == null && sedutaGiunta.getSecondaConvocazioneLuogo() == null) {
				sedutaGiunta.setStato(SedutaGiuntaConstants.statiSeduta.odgOdlBaseConsolidato.toString());	
				sedutaGiunta.setFase(SedutaGiuntaConstants.statiSeduta.odgOdlBaseConsolidato.getFase());
			}
			else {
				sedutaGiunta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.getFase());
			}
		}
		
		if (sedutaGiunta.getFase().equalsIgnoreCase(SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA.toString()) &&
				sedutaGiunta.getPrimaConvocazioneFine() != null) {
			sedutaGiunta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneAttiTrattati.toString());	
			sedutaGiunta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneAttiTrattati.getFase());
		}
				
		for(OrdineGiorno odg : sedutaGiunta.getOdgs()){
			if(odg.getId() != null && odg.getOggetto().equals(SedutaGiuntaConstants.statiOdgOdl.odgOdlInPredisposizione.toStringByOrgano(sedutaGiunta.getOrgano())) || 
					odg.getOggetto().equals(SedutaGiuntaConstants.statiOdgOdl.odgOdlConsolidato.toStringByOrgano(sedutaGiunta.getOrgano()))) { // ODG Exist
				List<AttiOdg> list = new ArrayList<AttiOdg>();
				list.addAll(odg.getAttos());
				log.debug("Atti ODG list size:" + list.size());
				ordineGiornoService.deleteArguments(list,odg.getId(),profiloId);
				ordineGiornoService.saveArguments(list, odg.getId(),profiloId);
			}
		}
		
		if(sedutaGiunta.getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.toString())) {
			sedutaGiunta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docVarInPredisposizione.toString());
			sedutaGiunta.setSottoscittoreDocumento(null);
		}
		
		sedutaGiuntaRepository.save(sedutaGiunta);
		
		destinatarioInternoService.salvaDestinatariInterni(sedutaGiunta.getDestinatariInterni(), sedutaGiunta.getId(),false);
	}
	
	@Transactional
	public void saveSottoscrittori(SedutaGiunta sedutaGiunta) throws Exception{
		if (sedutaGiunta.getId() != null) {
			SedutaGiunta seduta = sedutaGiuntaRepository.findOne(sedutaGiunta.getId());
			seduta.setSottoscrittoriresoconto(sedutaGiunta.getSottoscrittoriresoconto());
			
			sedutaGiuntaRepository.save(seduta);
		}
	}
	
	@Transactional(readOnly = true)
	public OrdineGiorno getOdgBase(SedutaGiunta seduta){
		OrdineGiorno result = null;
		
		if (seduta != null) {
			log.debug("getOdgBase - idSeduta:" + seduta.getId());
			if (seduta.getOdgs() != null && seduta.getOdgs().size() > 0){
				for (OrdineGiorno odg : seduta.getOdgs()){
					if (ordineGiornoService.isOdGBase(odg)){
						result = odg;
						break;
					}
				}
				
				if (result != null){
					log.debug(String.format("Seduta Giunta id:%s - OdG Base con id:%s recuperato", 
							seduta.getId(), result.getId()));
				} else {
					log.debug(String.format("Seduta Giunta id:%s - Nessun OdG Base presente.", seduta.getId()));
				}
			} else {
				log.error(String.format("La seduta di giunta con id:%s non ha nessun OrdineGiorno", seduta.getId()));
			}
		} else {
			log.error("Impossibile recuperare l'ordinegiorno base: seduta is NULL!! ");
		}
		
		return result;
	}
	
	@Transactional(readOnly = true)
	public OrdineGiorno getOdgFuoriSacco(Long idSedutaGiunta){
		log.debug("getOdgFuoriSacco - idSeduta:" + idSedutaGiunta);
		OrdineGiorno result = null;
		
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(idSedutaGiunta);
		if (seduta != null) {
			if (seduta.getOdgs() != null && seduta.getOdgs().size() > 0){
				for (OrdineGiorno odg : seduta.getOdgs()){
					if (ordineGiornoService.isOdGFuoriSacco(odg)){
						result = odg;
						break;
					}
				}
				
				if (result != null){
					log.debug(String.format("Seduta Giunta id:%s - OdG FuoriSacco con id:%s recuperato", 
							idSedutaGiunta, result.getId()));
				} else {
					log.debug(String.format("Seduta Giunta id:%s - Nessun OdG FuoriSacco presente.", idSedutaGiunta));
				}
			} else {
				log.error(String.format("La seduta di giunta con id:%s non ha nessun OrdineGiorno", idSedutaGiunta));
			}
		} else {
			log.error("Nessuna seduta trovata con id: " + idSedutaGiunta);
		}
		
		return result;
	}

	private void ricorsivaControlloPadri(SedutaGiunta sedutaGiunta,
			Set<Long> ids) {
		if (sedutaGiunta != null) {
			ids.add(sedutaGiunta.getId());
			if (sedutaGiunta.getSedutariferimento() != null) {
				ids.add(sedutaGiunta.getSedutariferimento().getId());
				ricorsivaControlloPadri(sedutaGiunta.getSedutariferimento(),
						ids);
			}
		}

	}
	
	@Transactional(readOnly = true)
	public boolean abilitatoPresenzeAssenze(SedutaGiunta seduta){
		
		for(OrdineGiorno odg : seduta.getOdgs()) {			
			for(AttiOdg attiOdg : odg.getAttos()) {
				
				if (!StringUtil.isNull(attiOdg.getEsito())) {
					Esito esito = esitoRepository.findById(attiOdg.getEsito());
					
					if(esito != null && Boolean.TRUE.equals(esito.getRegistraVotazione())) {
						return true;
					}
				}
			}
		}
		
		return false;
	}

	@Transactional
	public void editNumeroSeduta(Long id, Long newNumeroSeduta) {
		SedutaGiunta seduta = sedutaGiuntaRepository.findOne(id);
		if(seduta!= null && seduta.getPrimaConvocazioneInizio()!= null && seduta.getOrgano()!= null && !seduta.getOrgano().isEmpty() && 
				newNumeroSeduta!= null && newNumeroSeduta.longValue() > 0) {
			DateTime startDateCheck = new DateTime(seduta.getPrimaConvocazioneInizio().getYear(),1,1,0,0);
			DateTime endDateCheck = new DateTime(seduta.getPrimaConvocazioneInizio().getYear()+1,1,1,0,0);
			Long idCheck = sedutaGiuntaRepository.getIdByDateAndNumero(startDateCheck,endDateCheck, String.valueOf(newNumeroSeduta), seduta.getOrgano());
			if(idCheck != null && idCheck > 0) {
				throw new GestattiCatchedException("Numero seduta presente.");
			}
			seduta.setNumero(String.valueOf(newNumeroSeduta));
			seduta = sedutaGiuntaRepository.save(seduta);
			ProgressivoSeduta progressivo = progressivoSedutaRepository.getByAnnoAndOrgano(seduta.getPrimaConvocazioneInizio().getYear(), seduta.getOrgano());
			if(newNumeroSeduta.longValue() == progressivo.getProgressivo().longValue()) {
				throw new GestattiCatchedException("Numero seduta presente.");
			}
			if(newNumeroSeduta.longValue() > progressivo.getProgressivo().longValue()) {
				progressivo.setProgressivo(newNumeroSeduta.intValue());
				progressivo = progressivoSedutaRepository.save(progressivo);
			}
		}
		else {
			throw new GestattiCatchedException("Si prega di riprovare.");
		}
	}
}
