package it.linksmt.assatti.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.persistence.EntityManager;

import org.apache.commons.io.FileUtils;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.collect.Lists;
import com.itextpdf.text.DocumentException;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.CampoTipoAtto;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Esito;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAttiOdg;
import it.linksmt.assatti.datalayer.domain.QComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.CampoTipoAttoRepository;
import it.linksmt.assatti.datalayer.repository.ComponentiGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.DocumentoPdfRepository;
import it.linksmt.assatti.datalayer.repository.EsitoRepository;
import it.linksmt.assatti.datalayer.repository.OrdineGiornoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.TipoDocumentoRepository;
import it.linksmt.assatti.service.dto.AttiOdgCriteriaDTO;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.ResocontoDTO;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class OrdineGiornoService {
	private final Logger log = LoggerFactory.getLogger(OrdineGiornoService.class);
	
	/*
	private final Integer TIPO_RESOCONTO_INTEGRALE	= 1;
	private final Integer TIPO_RESOCONTO_PARZIALE	= 0;
	private final Integer TIPO_RESOCONTO_PRESS_ASS	= 2;
	*/	
	
	@Autowired
	private EntityManager em;

	@Inject
	private AttoRepository attoRepository; 

	@Inject
	private EsitoRepository esitoRepository;
	
	@Inject
	private AttiOdgRepository attoOdgRepository; 
	
	@Inject
	private OrdineGiornoRepository odgRepository; 
	
	@Inject
	private SedutaGiuntaRepository sedutaRepository; 
	
	@Inject
	private CampoTipoAttoRepository campoTipoAttoRepository;
	
	@Inject
	private ReportService reportService;
	
	@Inject
    private TipoDocumentoRepository tipoDocumentoRepository;
	
	@Inject
	private DocumentoPdfRepository documentoPdfRepository;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private AttiOdgRepository attiOdgRepository;
	
	@Inject
	private AttiOdgService attiOdgService;

	@Inject 
	private ComponentiGiuntaRepository componentiGiuntaRepository;
	
	@Inject
	private AttoWorkerService attoWorkerService;
	
	@Inject
	private FileService fileService;
	
	@Inject
	private DmsService dmsService;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private ContabilitaService contabilitaService;
	
	@Transactional
	public OrdineGiorno findOne(Long ordineGiornoId) {
		if(ordineGiornoId!=null) {
			return odgRepository.findOne(ordineGiornoId);
		}else {
			return null;
		}
	}
	
	@Transactional
	public void save(OrdineGiorno ordineGiorno, List<AttiOdg> list, Long profiloId) throws Exception {
		this.deleteArguments(list,ordineGiorno.getId(),profiloId);
		this.saveArguments(list, ordineGiorno.getId(),profiloId);
		odgRepository.save(ordineGiorno);
	}
	
	@Transactional
	public void deleteArguments(List<AttiOdg> list,Long idOdg,Long profiloId) throws Exception {
		log.info("delete arguments for id odg: "+idOdg);

		OrdineGiorno odg = odgRepository.findOne(idOdg);		
		Iterable<AttiOdg> l = attoOdgRepository.findByOrdineGiorno(odg);
		for (AttiOdg attoOdg : l) {
			log.info("attoOdg id: "+ attoOdg.getId());
			boolean attoDaCancellare = true;
			if(list != null){
				for (AttiOdg attoOdgNew : list){
					if(attoOdg.getId().equals(attoOdgNew.getId())){
						attoDaCancellare = false;
					}
				}
			}
			if(attoDaCancellare){
				log.info("attoOdg atto id check");
				log.info("attoOdg atto id: "+ attoOdg.getAtto().getId());
				
				Atto atto = attoRepository.findOne(attoOdg.getAtto().getId());
				String organo = attoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano();
				
				
				boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
				
				attoOdgRepository.delete(attoOdg);
						
				registrazioneAvanzamentoService.impostaStatoAtto(
	    				atto.getId().longValue(), profiloId.longValue(), 
	    				SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(organo), 
	    				isOdgGiunta?NomiAttivitaAtto.RIMUOVI_DA_ODG:NomiAttivitaAtto.RIMUOVI_DA_ODL, null);
											
				log.info("profilo id: "+ profiloId);
				log.info("atto id: "+ atto.getId());
				//TODO workflowService.rimuoviAttoDaOdg(atto, profiloId);
			}
		}
	}
	
	@Transactional
	public void delete(List<AttiOdg> list,Long idOdg,Long profiloId) throws Exception {
		log.info("delete arguments for id odg: "+idOdg);

		OrdineGiorno odg = odgRepository.findOne(idOdg);		
		Iterable<AttiOdg> l = attoOdgRepository.findByOrdineGiorno(odg);
		for (AttiOdg attoOdg : l) {
			log.info("attoOdg id: "+ attoOdg.getId());
			boolean attoDaCancellare = true;
			if(list != null){
				for (AttiOdg attoOdgNew : list){
					if(attoOdg.getId().equals(attoOdgNew.getId())){
						attoDaCancellare = false;
					}
				}
			}
			if(attoDaCancellare){
				log.info("attoOdg atto id check");
				log.info("attoOdg atto id: "+ attoOdg.getAtto().getId());
				
				Atto atto = attoRepository.findOne(attoOdg.getAtto().getId());
				String organo = attoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano();
				
				attoOdgRepository.delete(attoOdg);
				boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);	
				registrazioneAvanzamentoService.impostaStatoAtto(
	    				atto.getId().longValue(), profiloId.longValue(), 
	    				SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(organo), 
	    				isOdgGiunta?NomiAttivitaAtto.RIMUOVI_DA_ODG:NomiAttivitaAtto.RIMUOVI_DA_ODL, null);
											
				log.info("profilo id: "+ profiloId);
				log.info("atto id: "+ atto.getId());
				//TODO workflowService.rimuoviAttoDaOdg(atto, profiloId);
			}
		}
		odgRepository.delete(idOdg);
	}
	
	@Transactional
	public void cancelArguments(Long idOdg,Long profiloId) throws Exception {
		log.info("cancel arguments for id odg: "+idOdg);

		OrdineGiorno odg = odgRepository.findOne(idOdg);		
		Iterable<AttiOdg> l = attoOdgRepository.findByOrdineGiorno(odg);
		for (AttiOdg attoOdg : l) {
			Atto atto = attoRepository.findOne(attoOdg.getAtto().getId());	
			
			String organo = attoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano();
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name().equalsIgnoreCase(organo);
			
			registrazioneAvanzamentoService.impostaStatoAtto(
    				atto.getId().longValue(), profiloId.longValue(), 
    				SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl
    					.toStringByOrgano(odg.getSedutaGiunta().getOrgano()),
    					isOdgGiunta?NomiAttivitaAtto.RIMUOVI_DA_ODG:NomiAttivitaAtto.RIMUOVI_DA_ODL, null);
			
			//TODO workflowService.rimuoviAttoDaOdg(atto, profiloId);
		}
	}
	
	/* CASISTICA ELIMINATA
	@Transactional
	public void numeraAtti(List <Atto> listArgumentsOdg, Long profiloId) throws Exception {
		log.info("Numerazione atti");
		
		for (Atto atto : listArgumentsOdg){
			workflowService.sendNumerazioneAtto(atto.getId(), profiloId.longValue());
			
			//TODO workflowService.procediSenzaAnnullaEsito(atto, profiloId,null,null);
			attoWorkerService.checkAndInsertWorker(atto.getId(), profiloId);
    	}
	}
	*/
	
    /* 
     * Per ATTICO la generazione del documento avviene in una fase successiva
     * 
	@Transactional
	public void generaDocProvvedimento(List <Atto> listArgumentsOdg,Long profiloId) throws Exception {
		log.info("generaDocProvvedimento: ");
		if(listArgumentsOdg.size() > 0){
			Atto attor = attoRepository.findOne(listArgumentsOdg.get(0).getId());
			SedutaGiunta seduta = sedutaRepository.findOne(attor.getSedutaGiunta().getId());
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.provvedimentiInAttesaDiFirma.toString());
			seduta.setSottoscittoreDocumento(null);
			
			sedutaRepository.save(seduta);
		}
		
		for (Atto atto : listArgumentsOdg){   		
			Atto attor = attoRepository.findOne(atto.getId());
			
    		attoService.generaProvvedimentoPubblicazioneAtto(attor);
    		
    		Avanzamento avanzamento = new Avanzamento();
			avanzamento.setAtto(attor);
			avanzamento.setDataAttivita(new DateTime());

			avanzamento.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiFirmaSegretario.toString());
			avanzamento.setAttivita("Genera Documento di Provvedimento ");
			avanzamento = avanzamentoRepository.save(avanzamento);

			
						
			attor.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiFirmaSegretario.toString());
			attor = attoRepository.save(attor);
			
			//TODO workflowService.procediSenzaAnnullaNumerazione(attor, profiloId,null,null);
			attoWorkerService.checkAndInsertWorker(atto.getId(), profiloId);
    		
    	}	
	}
	*/
	
	
	/* Il processo attico non consente l'annullamento della numerazione
	 * 
	 *
	@Transactional
	public void annullaNumerazione(List <Atto> listArgumentsOdg,Long profiloId) throws Exception {
		log.info("annullaNumerazione: ");
		for (Atto atto : listArgumentsOdg){    		
    		
    		Avanzamento avanzamento = new Avanzamento();
			avanzamento.setAtto(atto);
			avanzamento.setDataAttivita(new DateTime());

			avanzamento.setStato(SedutaGiuntaConstants.statiAtto.propostaConEsito.toString());
			avanzamento.setAttivita("Annulla Numerazione");
			avanzamento = avanzamentoRepository.save(avanzamento);

			Atto attor = attoRepository.findOne(atto.getId());
			//LocalDate dataAdozione = new LocalDate();
			LocalDate dataAdozione =  attor.getDataAdozione();
			codiceProgressivoService.annullaCodiceCifraAdozioneAttiGiunta( dataAdozione.getYear() , attor.getTipoAtto(),attor.getNumeroAdozione() );
			attor.setNumeroAdozione( null );
			//atto.setDataAdozione( null );
			
			attor.setStato(SedutaGiuntaConstants.statiAtto.propostaConEsito.toString());
			attor = attoRepository.save(attor);
			
			//TODO workflowService.annullaNumerazione(atto, profiloId);
			attoWorkerService.checkAndInsertWorker(atto.getId(), profiloId);
    		
    	}
	}
	*/
	
	/*
	 * IN ATTICO NON PREVISTO 
	 *
	@Transactional
	public void pubblicaDocumentoResoconto(Long odgId) throws RiversamentoPoolException{
		
		OrdineGiorno object = odgRepository.getOne(odgId);
		List<Resoconto> resoconti = resocontoRepository.findBySedutagiunta(object.getSedutaGiunta().getId());
		for(Resoconto res : resoconti){
			if(res.getTipo() != null){
				res.setDataPubblicazioneSito(new LocalDate());
				resocontoRepository.save(res);
			}
			if(res.getDocumentiPdf()!=null){
				for(DocumentoPdf doc : res.getDocumentiPdf()){
					documentoPdfService.aggiungiRiversamentoResoconto(doc);
				}
			}
		}
		
	}
	
	@Transactional
	public void pubblicaDocumentoResocontoParziale(Long odgId) throws RiversamentoPoolException{
		
		OrdineGiorno object = odgRepository.getOne(odgId);
		List<Resoconto> resoconti = resocontoRepository.findBySedutagiunta(object.getSedutaGiunta().getId());
		for(Resoconto res : resoconti){
			if(res.getTipo() != null && res.getTipo() == TIPO_RESOCONTO_PARZIALE){
				res.setDataPubblicazioneSito(new LocalDate());
				resocontoRepository.save(res);
			}
//			if(res.getDocumentiPdf()!=null){
//				for(DocumentoPdf doc : res.getDocumentiPdf()){
//					documentoPdfService.aggiungiRiversamentoResoconto(doc);
//				}
//			}
		}
		
	}
	
	@Transactional
	public void generaDocumentoResoconto(ReportDTO reportDto,String tipo,Long profiloId,String createdBy, Long sottoscrittoreId) throws IOException, DocumentException, DmsException, CifraCatchedException  {
		log.debug( "generaDocumentoResoconto:"+ reportDto);
		OrdineGiorno object = odgRepository.getOne(reportDto.getIdAtto());
		
		SottoscrittoreSedutaGiunta sottoscrittoreResoconto = sottoscrittoresedutaRepository.findFirstBySedutaResoconto(object.getSedutaGiunta().getId());
		
		if(tipo.equals("presenze-assenze")){
			Resoconto resoconto = getResoconto(object.getSedutaGiunta(), 2,false);
			SedutaGiunta seduta = sedutaRepository.findOne(resoconto.getSedutaGiunta().getId());
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docPresInAttesaDiFirma.toString());
			seduta.setSottoscittoreDocumento(null);
			if(sottoscrittoreResoconto != null){
				seduta.setSottoscittoreDocumento(sottoscrittoreResoconto.getProfilo());
				resoconto.setSottoscrittore(sottoscrittoreResoconto.getProfilo());
			}
			sedutaRepository.save(seduta);
			Profilo profilo = profiloRepository.findOne(profiloId);
			resoconto.setStato(SedutaGiuntaConstants.statiPresenze.presenzeInAttesaDiFirma.toString());
			
			File result = reportService.previewPresenzeAssenze(resoconto,profilo,object.getSedutaGiunta(),reportDto);
			DocumentoPdf savedDocPdf = documentoPdfService.saveResocontoPdf(object,resoconto, result, tipo, createdBy);
			savedDocPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.foglio_assenti_riunione_giunta.name()));
			documentoPdfRepository.save(savedDocPdf);
			documentoPdfService.aggiungiRiversamentoResoconto(savedDocPdf);
//	        TODO Spostare in firma
//			protocollaResoconto(resoconto, savedDocPdf);
		}
		else if(tipo.equals("resoconto")){
			// integrale
			Resoconto resoconto = getResoconto(object.getSedutaGiunta(), TIPO_RESOCONTO_INTEGRALE, true);

			SedutaGiunta seduta = sedutaRepository.findOne(resoconto.getSedutaGiunta().getId());
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResInAttesaDiFirma.toString());
			seduta.setSottoscittoreDocumento(null);
			if(sottoscrittoreResoconto != null){
				seduta.setSottoscittoreDocumento(sottoscrittoreResoconto.getProfilo());
				resoconto.setSottoscrittore(sottoscrittoreResoconto.getProfilo());
			}
			sedutaRepository.save(seduta);
			resoconto.setStato(SedutaGiuntaConstants.statiResoconto.resocontoInAttesaDiFirma.toString());
			List<OrdineGiorno> odgs = new ArrayList<OrdineGiorno>(object.getSedutaGiunta().getOdgs());
			File result = reportService.previewResoconto(resoconto,odgs,reportDto);
			DocumentoPdf savedDocPdf = documentoPdfService.saveResocontoPdf(object,resoconto, result, "resoconto-integrale", createdBy);
			savedDocPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.resoconto.name()));
			documentoPdfRepository.save(savedDocPdf);
			
//			protocollaResoconto(resoconto, savedDocPdf);
			
			// parziale
			resoconto = getResoconto(object.getSedutaGiunta(), TIPO_RESOCONTO_PARZIALE, true);
			odgs = new ArrayList<OrdineGiorno>(object.getSedutaGiunta().getOdgs());
			result = reportService.previewResoconto(resoconto,odgs,reportDto);
			savedDocPdf = documentoPdfService.saveResocontoPdf(object,resoconto, result, "resoconto-parziale", createdBy);
			savedDocPdf.setTipoDocumento(tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.resoconto.name()));
			documentoPdfRepository.save(savedDocPdf);
//			protocollaResoconto(resoconto, savedDocPdf);
		}
		
	}
	*/
	
	
	/**
	 * @param profiloId
	 * @param resocontoDTO
	 * @param attoOdgId
	 * @throws UnsupportedEncodingException
	 */
	@Transactional
	public void salvaEsiti(final Long profiloId, ResocontoDTO resocontoDTO, Long attoOdgId)
			throws UnsupportedEncodingException {
		AttiOdg attoOdg = attiOdgRepository.findOne(attoOdgId);
		if(attoOdg != null) {
			Atto atto = modificaEsito(resocontoDTO, attoOdg, profiloId);
			
			boolean isOdgGiunta = attoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano()
					.equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.G.name());
			
			workflowService.registrazioneEsitoSeduta(isOdgGiunta, 
					atto.getId().longValue(), resocontoDTO);
		}
	}
	
	@Transactional
	public void confermaEsito(final Long profiloId, Long attoOdgId)
			throws UnsupportedEncodingException, GestattiCatchedException, ServiceException {
		AttiOdg attoOdg = attiOdgRepository.findOne(attoOdgId);
		attoOdg.setDataConfermaEsito(LocalDateTime.now());
		Esito esito = esitoRepository.findOne(attoOdg.getEsito());
		if(attoOdg.getAtto().getIe() != null && esito!=null && (esito.getAmmetteIE()==null || !esito.getAmmetteIE()) && esito.getId().toLowerCase().contains("approvat")) {
			attoOdg.setApprovataIE(attoOdg.getAtto().getIe());
		}
		
		attoOdg = attiOdgRepository.save(attoOdg);
		if(attoOdg != null) {			
			boolean isOdgGiunta = SedutaGiuntaConstants.organoSeduta.G.name()
					.equalsIgnoreCase(attoOdg.getOrdineGiorno().getSedutaGiunta().getOrgano());
			
			// Quando si conferma "Non trattato" ritorna in inseribile OdG
			if (SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice()
					.equalsIgnoreCase(StringUtil.trimStr(attoOdg.getEsito()))) {
				//attiOdgRepository.bloccoModificaVotazioni(attoOdg.getAtto().getId().longValue());
				attoOdg = attiOdgService.bloccoModificaVotazioni(attoOdg);
				Map<String, Object> esitoAtto = new HashMap<String, Object>();
				esitoAtto.put(AttoProcessVariables.ESITO_PROPOSTA_ODG, 
						SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice());
				
				workflowService.confermaEsitoSeduta(isOdgGiunta, 
						attoOdg.getAtto().getId().longValue(), profiloId.longValue(), esitoAtto);
			}
			else {
				
				// Se la seduta è di Consiglio, verifica obbligatorietà parere Commissione Consigliare
				String valObbligatorio = "false";
				if (isOdgGiunta) {
					valObbligatorio = StringUtil.trimStr(WebApplicationProps.getProperty(
							ConfigPropNames.PARERE_ASSESSORE_OBBLIGATORIO));
				}
				else {
					valObbligatorio = StringUtil.trimStr(WebApplicationProps.getProperty(
							ConfigPropNames.PARERE_COMMISSIONE_CONSIGLIARE_OBBLIGATORIO));
				}
				
				if (valObbligatorio.equalsIgnoreCase("true") || valObbligatorio.equalsIgnoreCase("si")) {
					Atto check = attoOdg.getAtto();
					workflowService.loadAllTasks(check);
					String codiceTipoParereCommCons = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO);
					String codiceTipoParereQuartRev = WebApplicationProps.getProperty(ConfigPropNames.PARERE_QUARTIERE_REVISORE_CODICE_TIPO);
//					if (check.getTaskAttivi() != null && check.getTaskAttivi().size() > 0) {
//						
//						List taskAttivi = check.getTaskAttivi();
						
						Boolean tuttiIPareriQuartieriScaduti = null;
						List<ConfigurazioneIncaricoDto> listInc = configurazioneIncaricoService.getConfIncaricoByConfTask(check.getId(), ConfigurazioneTaskEnum.DIR_SEGRETARIO_COMMISSIONE);
						List<Long>idsAooPareriEspressi= new ArrayList<Long>();
						if(check.getPareri()!= null && !check.getPareri().isEmpty()) {
							for (Parere parere : check.getPareri()) {
								if((parere.getAnnullato() == null || !parere.getAnnullato()) && parere.getAoo() !=null && parere.getTipoAzione()!= null && parere.getTipoAzione().getCodice()!= null) {
									if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereCommCons)) {
										//idsAooPareri.add(parere.getAoo().getId());
										if(!StringUtils.isEmpty(parere.getParereSintetico())) {
											//almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
									}
									else if(parere.getTipoAzione().getCodice().equalsIgnoreCase(codiceTipoParereQuartRev)) {
										if(!StringUtils.isEmpty(parere.getParereSintetico()) || parere.getData()!=null) {
											//almenoUnParereEspresso = true;
											if(parere.getAoo()!=null && parere.getAoo().getId() != null) {
												idsAooPareriEspressi.add(parere.getAoo().getId());
											}
										}
										//parQuarNotReq = false;
										if(StringUtils.isEmpty(parere.getParereSintetico())){
											//parQuarAllEspr = false;
										}
										if((tuttiIPareriQuartieriScaduti == null || tuttiIPareriQuartieriScaduti == true) && parere.getDataScadenza()!= null && parere.getDataScadenza().isBeforeNow()) {
											tuttiIPareriQuartieriScaduti = true;
										}else {
											if(parere.getAoo()!= null && !idsAooPareriEspressi.contains(parere.getAoo().getId())) {
												tuttiIPareriQuartieriScaduti = false;
											}
											
										}
									}
								}
							}
						}
						
						Boolean tuttiScadutiCommissioni = null;
						if(listInc != null) {
							for (ConfigurazioneIncaricoDto inc : listInc) {
								if(inc.getListConfigurazioneIncaricoAooDto()!= null && !inc.getListConfigurazioneIncaricoAooDto().isEmpty()) {
									for (ConfigurazioneIncaricoAooDto incAoo : inc.getListConfigurazioneIncaricoAooDto()) {
										
										if(!idsAooPareriEspressi.contains(incAoo.getIdAoo())) {
											if(incAoo.getDataManuale()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScadutiCommissioni == null || tuttiScadutiCommissioni == true) && incAoo.getDataManuale().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScadutiCommissioni = true;
												}else {
													tuttiScadutiCommissioni = false;
												}
											}
											else if(incAoo.getDataCreazione()!= null && incAoo.getGiorniScadenza()!= null) {
												if((tuttiScadutiCommissioni == null || tuttiScadutiCommissioni == true) && incAoo.getDataCreazione().plusDays(incAoo.getGiorniScadenza()).isBeforeNow()) {
													tuttiScadutiCommissioni = true;
												}else {
													tuttiScadutiCommissioni = idsAooPareriEspressi.contains(incAoo.getIdAoo());
												}
											}
										}
										
										
											
									}
								}
							}
						}
						boolean propagaErrore = false;
						
						if(tuttiScadutiCommissioni == null && tuttiIPareriQuartieriScaduti == null) {
							propagaErrore = false;
						}else {
							if((tuttiScadutiCommissioni!=null && !tuttiScadutiCommissioni) || (tuttiIPareriQuartieriScaduti!=null && !tuttiIPareriQuartieriScaduti)) {
								propagaErrore = true;
							}
						}
						
						if(propagaErrore) {
						
							throw new GestattiCatchedException("Non \u00E8 stato possibile effettuare l'operazione richiesta " + GestattiCatchedException.NEW_LINE + 
								"L'atto potrebbe essere in attesa di Pareri da Parte di Assessori o Commmisioni Consiliari.");
						}
					//}
				}
					
//				attiOdgRepository.bloccoModificaVotazioni(attoOdg.getAtto().getId().longValue());
				attoOdg = attiOdgService.bloccoModificaVotazioni(attoOdg);	
			}
			
			// Controllo se tutti esiti confermati e cambio stato seduta
			SedutaGiunta seduta = sedutaRepository.findOne(
					attoOdg.getOrdineGiorno().getSedutaGiunta().getId().longValue());
			
			Set<OrdineGiorno> allOdg = seduta.getOdgs();
			if (allOdg != null) {
				boolean confermati = true;
				for (OrdineGiorno ordineGiorno : allOdg) {
					List<AttiOdg> attoConf = ordineGiorno.getAttos();
					if (attoConf != null) {
						for (AttiOdg curCheck : attoConf) {
							if ( curCheck.getId().longValue() != attoOdgId.longValue() && 
								(curCheck.getBloccoModifica() == null || curCheck.getBloccoModifica().booleanValue() == false)) {
								confermati = false;
							}
						}
					}	// End attiOdg
				}
				if (confermati) {
					seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaConclusaEsitiConfermati.toString());
					seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaConclusaEsitiConfermati.getFase());
					
					seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResInAttesaDiPredisposizione.toString());
					seduta.setSottoscittoreDocumento(null);
					sedutaRepository.save(seduta);
				}
			}	// End all odg
		}
	}
	
	@Transactional
	public void annullaEsito(final Long profiloId, Long attoOdgId)
			throws UnsupportedEncodingException {
		
		// Elimino tutte i dati dell'esito
		AttiOdg attoOdg = attiOdgRepository.findOne(attoOdgId);
		if(attoOdg != null) {
			List<ComponentiGiunta> presList = attoOdg.getComponenti();
			for (ComponentiGiunta componente : presList) {
				componentiGiuntaRepository.delete(componente);
			}
			attoOdg.setComponenti(new ArrayList<ComponentiGiunta>());
			
			attoOdg.setEsito(null);
			attoOdg.setApprovataIE(null);
			attoOdg.setVotazioneSegreta(null);
			attoOdg.setVotazioneIE(null);
			attoOdg.setDichiarazioniVoto(null);
			attoOdg.setNumPresenti(null);
			attoOdg.setNumAssenti(null);
			attoOdg.setNumAstenuti(null);
			attoOdg.setNumNpv(null);
			attoOdg.setNumContrari(null);
			attoOdg.setNumFavorevoli(null);
			attoOdg.setSedutaConvocata(null);
			attoOdg.setAttoPresentato(null);
			attoOdg.setDataDiscussione(null);
			
			attiOdgRepository.save(attoOdg);
		}
	}
	
	
	@Transactional
	public void impostaNonTrattati(List<Long> attoOdgId, String codiceEsito) {
		for (Long curID : attoOdgId) {
			AttiOdg attoOdg = attiOdgRepository.findOne(curID);
			attoOdg.setEsito(codiceEsito);
			
			attiOdgRepository.save(attoOdg);
		}
	}
	
	
	/**
	 * @param profiloId
	 * @param resocontoDTO
	 * @param attoOdgId
	 * @throws UnsupportedEncodingException
	 */
	/* 
	 * NON PIU' USATO
	 *
	@Transactional
	public void modificaComponenti(final Long profiloId, ResocontoDTO resocontoDTO, Long attoOdgId)
			throws UnsupportedEncodingException {
		AttiOdg attoOdg = attiOdgRepository.findOne(attoOdgId);
		if(attoOdg != null) {
			Atto atto = modificaComponenti(resocontoDTO, attoOdg, profiloId);
			
			workflowService.modificaComponentiSeduta(atto.getId(), resocontoDTO);
		}
	}
	*/
	
	/**
	 * @param resoconto
	 * @param attoOdg
	 */
	/*
	 *  In attico non consentito -> da resoconto massivo
	 *
	@Transactional
	public void updateEsito(final ResocontoDTO resoconto, AttiOdg attoOdg, Long profiloId) {
		modificaEsito(resoconto, attoOdg, false, profiloId);
	}
	*/

	/**
	 * @param resocontoDTO
	 * @param attoOdg
	 * @return
	 */
	protected Atto modificaEsito(ResocontoDTO resocontoDTO, AttiOdg attoOdg, Long profiloId) {
		
		Long attoId = attoOdg.getAtto().getId();
		Atto attoDB = attoRepository.findOne(attoOdg.getAtto().getId());
		boolean ieDB = attoDB.getIe()!=null?attoDB.getIe().booleanValue():false;
		boolean ieResoconto = resocontoDTO.getIe()!=null?resocontoDTO.getIe().booleanValue():false;
		if(Lists.newArrayList("DPC", "DC").contains(attoOdg.getAtto().getTipoAtto().getCodice())) {
			if(resocontoDTO.getVotazioneIE()!=null && resocontoDTO.getVotazioneIE().booleanValue()) {
				ieResoconto = resocontoDTO.getApprovataIE()!=null?resocontoDTO.getApprovataIE().booleanValue():false;
			}else {
				ieResoconto = false;
			}
		}
		log.debug("salvaEsito - Atto id:" + attoId);
		
		attoOdg.setEsito(resocontoDTO.getEsito());
		attoOdg.setNumPresenti(resocontoDTO.getNumPresenti());
		attoOdg.setNumAssenti(resocontoDTO.getNumAssenti());
		attoOdg.setNumFavorevoli(resocontoDTO.getNumFavorevoli());
		attoOdg.setNumContrari(resocontoDTO.getNumContrari());
		attoOdg.setNumAstenuti(resocontoDTO.getNumAstenuti());
		attoOdg.setNumNpv(resocontoDTO.getNumNpv());
		attoOdg.setDichiarazioniVoto(resocontoDTO.getDichiarazioniVoto());
		attoOdg.setSedutaConvocata(resocontoDTO.getSedutaConvocata());
		attoOdg.setAttoPresentato(resocontoDTO.getAttoPresentato());
		attoOdg.setVotazioneSegreta(resocontoDTO.getVotazioneSegreta());
		attoOdg.setApprovataIE(resocontoDTO.getApprovataIE());
		attoOdg.setVotazioneIE(resocontoDTO.getVotazioneIE());
		attoOdg.setDataDiscussione(resocontoDTO.getDataDiscussione());
		if(resocontoDTO.getComposizione()!=null) {
			attoOdg.setComposizione(resocontoDTO.getComposizione());
		}
		attiOdgRepository.save(attoOdg);
		
		Atto atto = attoRepository.findOne(attoId);
		
		atto.setEsito(resocontoDTO.getEsito());
		atto.setIsModificatoInGiunta(resocontoDTO.getIsAttoModificatoInGiunta());
		
		
		//if(checkIE(atto.getTipoAtto().getId()) && resocontoDTO.getIe() != null) {
			atto.setIe(ieResoconto);
		//}
		
		//aggiorna dataEsecuzione su Jente
		if(Lists.newArrayList("DG", "DPC", "DC").contains(attoOdg.getAtto().getTipoAtto().getCodice()) && attoOdg.getAtto().getDataAdozione()!=null && attoOdg.getAtto().getNumeroAdozione()!=null && ieDB!=ieResoconto) {
        	
        	Atto attoDto = attoOdg.getAtto();
    		if(contabilitaService.esisteBozzaAtto(attoDto.getTipoAtto().getCodice(),  attoDto.getNumeroAdozione(),  attoDto.getDataAdozione().getYear(), false)) {
				Date dataEsecutivitaDaComunicareAJente = ieResoconto?attoDto.getDataAdozione().toDate():null;
				contabilitaService.dataEsecutivitaAtto(
						attoDto.getTipoAtto().getCodice(), 
						attoDto.getNumeroAdozione(), 
						attoDto.getDataAdozione().getYear(), 
						dataEsecutivitaDaComunicareAJente);
//				if(ieResoconto) {
//					atto.setDataEsecutivita(atto.getDataAdozione());
//				}else {
//					atto.setDataEsecutivita(null);
//				}
			}
        }
		
		
		Atto temp = attoRepository.save(atto);
		log.debug("Esito atto:" + temp.getEsito());
		
		Esito esito = esitoRepository.findById(atto.getEsito());
		
		List<ComponentiGiunta> listaComponentiDaCancellare = componentiGiuntaRepository.findByAtto(attoOdg);
		componentiGiuntaRepository.delete(listaComponentiDaCancellare);
		
		if(esito != null && Boolean.TRUE.equals(esito.getRegistraVotazione())) {
			List<ComponentiGiunta> componenti = this.getComponentiResoconto(resocontoDTO.getComponenti(), attoOdg);
			componentiGiuntaRepository.save(componenti);
		}
		
		attoWorkerService.checkAndInsertWorker(attoId, profiloId);
		return atto;
	}
	
	private boolean checkIE(Long idTipoAtto) {
		
		List<CampoTipoAtto> listCampotipoAtto = campoTipoAttoRepository.findByTipoAtto(idTipoAtto);
		if(listCampotipoAtto != null && !listCampotipoAtto.isEmpty()){
			for (CampoTipoAtto campTipo : listCampotipoAtto) {
				if(campTipo.getCampo()!= null && !StringUtil.isNull(campTipo.getCampo().getCodice()) && campTipo.getCampo().getCodice().equalsIgnoreCase("ie")) {
					return campTipo.isVisibile();
				}
			}
		}
		return false;
	}
	
	/**
	 * @param resocontoDTO
	 * @param attoOdg
	 * @return
	 *
	 * NON PIU' UTILIZZATO
	 *
	@Transactional
	protected Atto modificaComponenti(ResocontoDTO resocontoDTO, AttiOdg attoOdg, Long profiloId) {
		
		Long attoId = attoOdg.getAtto().getId();
		log.debug("modificaComponenti - Atto id:" + attoId);
		
		attoOdg.setNumPresenti(resocontoDTO.getNumPresenti());
		attoOdg.setNumAssenti(resocontoDTO.getNumAssenti());
		attoOdg.setNumFavorevoli(resocontoDTO.getNumFavorevoli());
		attoOdg.setNumContrari(resocontoDTO.getNumContrari());
		attoOdg.setNumAstenuti(resocontoDTO.getNumAstenuti());
		attoOdg.setNumNpv(resocontoDTO.getNumNpv());
		//attoOdg.setDichiarazioniVoto(resocontoDTO.getDichiarazioniVoto());
		//attoOdg.setSedutaConvocata(resocontoDTO.getSedutaConvocata());
		//attoOdg.setVotazioneSegreta(resocontoDTO.getVotazioneSegreta());
		//attoOdg.setDataDiscussione(resocontoDTO.getDataDiscussione());
		attiOdgRepository.save(attoOdg);
		
		Atto atto = attoRepository.findOne(attoId);
		
		//atto.setEsito(resocontoDTO.getEsito());
		//atto.setIsModificatoInGiunta(resocontoDTO.getIsAttoModificatoInGiunta());
		
		//Atto temp = attoRepository.save(atto);
		//log.debug("Esito atto:" + temp.getEsito());
		
		Esito esito = esitoRepository.findById(atto.getEsito());
		
		List<ComponentiGiunta> listaComponentiDaCancellare = componentiGiuntaRepository.findByAtto(attoOdg);
		componentiGiuntaRepository.delete(listaComponentiDaCancellare);
		
		if(esito != null && Boolean.TRUE.equals(esito.getRegistraVotazione())) {
			// Eventuale eliminazione e scrittura dei presenti/votazioni
			List<ComponentiGiunta> componenti = this.getComponentiResoconto(resocontoDTO.getComponenti(), attoOdg);
			componentiGiuntaRepository.save(componenti);
		}
		
		attoWorkerService.checkAndInsertWorker(attoId, profiloId);
		return atto;
	}
	*/
	
	private List<ComponentiGiunta> getComponentiResoconto(List<ComponentiGiunta> comps, AttiOdg attoOdg){
    	List<ComponentiGiunta> componenti = new ArrayList<ComponentiGiunta>();
    	for(ComponentiGiunta componente : comps){
    		// if(resetId) {
    			em.detach(componente);
    			componente.setId(null);
    		// }
			componente.setAtto(attoOdg);
//TODO disabilitato per permettere una maggiore flessibilità per la votazione
//			if (Boolean.TRUE.equals(componente.getIsPresidenteFine()) || 
//				Boolean.TRUE.equals(componente.getIsSegretarioFine())) {
//				componente.setPresente(true);
//			}
			
			componenti.add(componente);
		}
    	
    	return componenti;
    }
	
	/**
	 * @param profiloId
	 * @param attoOdg
	 * @throws UnsupportedEncodingException
	 */
	/* Funzione non prevista in ATTICO
	 * 
	@Transactional
	public void cancellaEsiti(final Long profiloId, AttiOdg attoOdg) throws UnsupportedEncodingException {
		attoOdg.setEsito(null);
		Long attoId = attoOdg.getAtto().getId();
		log.debug("cancellaEsito - Atto id:" + attoId);
		
		Atto atto = attoRepository.findOne(attoId);
		atto.setEsito(null);
		atto.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString());
		atto.setDataAdozione(null);
		attiOdgRepository.save(attoOdg);
		
		Atto temp = attoRepository.save(atto);
		log.debug("Esito atto:" + temp.getEsito());
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(atto);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString());
		avanzamento.setAttivita("Annulla esito");
		avanzamento = avanzamentoRepository.save(avanzamento);
		        			
		List<ComponentiGiunta> componenti = componentiGiuntaRepository.findByAtto(attoOdg);
		componentiGiuntaRepository.delete(componenti);
		
		//TODO workflowService.annullaEsito(atto, profiloId);
		attoWorkerService.checkAndInsertWorker(attoId, profiloId);
	}
	*/
    
	/**
	 * @param resoconto
	 * @param profiloId
	 * @param attoOdg
	 * @throws UnsupportedEncodingException
	 */
	/*
	@Transactional
	public void cancellaEsito(final ResocontoDTO resoconto, final Long profiloId, AttiOdg attoOdg)
			throws UnsupportedEncodingException {
		attoOdg.setEsito(null);
		Long attoId = attoOdg.getAtto().getId();
		log.debug("cancellaEsito - Atto id:" + attoId);
		
		Atto atto = attoRepository.findOne(attoId);
		atto.setEsito(resoconto.getEsito());
		atto.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString());
		atto.setDataAdozione(null);
		attiOdgRepository.save(attoOdg);
		
		Atto temp = attoRepository.save(atto);
		log.debug("Esito atto:" + temp.getEsito());
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(atto);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString());
		avanzamento.setAttivita("Annulla esito");
		avanzamento = avanzamentoRepository.save(avanzamento);
		
		//TODO workflowService.annullaEsito(atto, profiloId);
 		componentiGiuntaRepository.delete(resoconto.getComponenti());
	}
	*/

	
	
	/* 
	 * Funzione non utilizzata
	 * 
	protected Resoconto getResoconto(SedutaGiunta seduta,Integer tipo,boolean res){

		Resoconto resoconto = null;
		if(res == true){
			resoconto = resocontoRepository.findBySedutagiuntaIdAndTipo(seduta.getId(),tipo);
		}
		else{
			resoconto = resocontoRepository.findBySedutagiuntaIdAndTipoNull(seduta.getId());
		}
		
		if(resoconto == null){
			resoconto = new Resoconto();
			if(res == true){
				resoconto.setTipo(tipo);
			}
			else{
				resoconto.setTipo(2);// TODO demo in assenza del modello 
			}
			
			resoconto.setSedutaGiunta(seduta);
			resocontoRepository.save(resoconto);
		}
		
		return resoconto;
	}
	*/
	
	@Transactional
	public void generaDocumentoOrdinegiorno(ReportDTO reportDto,Boolean firmato,String firmatario, long createdBy) 
			throws IOException, DocumentException, GestattiCatchedException  {
		log.debug( "generaDocumentoOrdinegiorno:"+ reportDto);
		
		OrdineGiorno odg = odgRepository.getOne(reportDto.getIdAtto());
		
		if(reportDto.getIdProfiloSottoscrittore()!=null) {
			Profilo sottoscrittore = profiloRepository.findOne(reportDto.getIdProfiloSottoscrittore());
			if (sottoscrittore!=null){
				odg.setSottoscrittore(sottoscrittore);
			}
		}
		
		File result = reportService.previewOrdinegiorno(odg,reportDto);
		
		// SedutaGiunta seduta = setStatoDocumentoInAttesaDiFirmaSottoscrittoreSeduta(odg, sottoscrittore);
		
		SedutaGiunta seduta = setStatoDocumentoConsolidatoSottoscrittoreSeduta(odg);
		log.debug( "seduta.getStato:"+ seduta.getStato());
		
		DocumentoPdf savedDocPdf = documentoPdfService.saveOdGPdf(odg, result, firmato, false, false, firmatario, String.valueOf(createdBy));
		 	
		// IN ATTICO NON PREVISTA LA FIRMA, AGGIORNO LO STATO CHE SI TROVA NELLA COLONNA "OGGETTO"
		odg.setOggetto(SedutaGiuntaConstants.statiOdgOdl.odgOdlConsolidato
  				.toStringByOrgano(odg.getSedutaGiunta().getOrgano()));
		odgRepository.save(odg);
		
		// TODO: Generazione documento variazione per ora non vincolante
		// if(!(seduta.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.toString()))){
			for(AttiOdg atto :odg.getAttos()){
				
				boolean isOdgGiunta = odg.getSedutaGiunta().getOrgano()
						.equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.G.name());
				
				String nomeAttivitaAtto = isOdgGiunta?NomiAttivitaAtto.CONSOLIDA_ODG:NomiAttivitaAtto.CONSOLIDA_ODL;
				
				registrazioneAvanzamentoService.impostaStatoAtto(
	    				atto.getAtto().getId().longValue(), createdBy, 
	    				SedutaGiuntaConstants.statiAtto.propostaInAttesaDiEsito.toString(), 
	    				nomeAttivitaAtto, null);
			}
		// }
			
		if(isOdGBase(odg) && seduta.getFase().equalsIgnoreCase(SedutaGiuntaConstants.fasiSeduta.PREDISPOSIZIONE.toString())) {
			seduta.setStato(SedutaGiuntaConstants.statiSeduta.odgOdlBaseConsolidato.toStringByOrgano(seduta.getOrgano()));
			seduta.setFase(SedutaGiuntaConstants.statiSeduta.odgOdlBaseConsolidato.getFase());
			sedutaRepository.save(seduta);
		}
		else if (seduta.getFase().equalsIgnoreCase(SedutaGiuntaConstants.fasiSeduta.PREDISPOSTA.toString())) {
			if (isOdGSuppletivo(odg)) {
				seduta.setStato(SedutaGiuntaConstants.statiSeduta.odgOdlSuppletivoConsolidato.toStringByOrgano(seduta.getOrgano()));
				seduta.setFase(SedutaGiuntaConstants.statiSeduta.odgOdlSuppletivoConsolidato.getFase());
				sedutaRepository.save(seduta);
			}
			else if (isOdGFuoriSacco(odg)) {
				seduta.setStato(SedutaGiuntaConstants.statiSeduta.odgOdlFuoriSaccoConsolidato.toStringByOrgano(seduta.getOrgano()));
				seduta.setFase(SedutaGiuntaConstants.statiSeduta.odgOdlFuoriSaccoConsolidato.getFase());
				sedutaRepository.save(seduta);
			}
		}
		 
		//2019-02-15 commentiamo questa sezione in seguito al fatto che FI chiede che il segretario non sia piÃ¹ obbligatorio e perchÃ¨ il resoconto non deve essere piÃ¹ firmato
//		if(isOdGBase(odg) && (seduta.getSottoscrittoriresoconto() == null || (seduta.getSottoscrittoriresoconto() != null && seduta.getSottoscrittoriresoconto().size() < 1) )){
//			SottoscrittoreSedutaGiunta sottoscrittoreresoconto = new SottoscrittoreSedutaGiunta();
//			sottoscrittoreresoconto.setOrdineFirma(1);
//			sottoscrittoreresoconto.setSedutaresoconto(seduta);
//			sottoscrittoreresoconto.setProfilo(seduta.getSegretario());
//			if (seduta.getSegretario().getHasQualifica()!=null && seduta.getSegretario().getHasQualifica().size() == 1){
//				sottoscrittoreresoconto.setQualificaProfessionale(seduta.getSegretario().getHasQualifica().iterator().next());
//			}
//						
//			sottoscrittoresedutaRepository.save(sottoscrittoreresoconto);
//		}

//		if( (isOdGBase(odg) && seduta.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiSeduta.sedutaInPredisposizione.toString())) || 
//				isOdGSuppletivo(odg) || isOdGFuoriSacco(odg)){
//			protocollaOdg(savedDocPdf);
//		}
		
		
		/*
		 *  IN ATTICO NON PREVISTA LA FIRMA DI ODG/ODL
		 *
		if(isOdGBase(odg) && seduta.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDocumentoVariazione.toString())) {
			seduta.setStato__(SedutaGiuntaConstants.statiSeduta.sedutaDocumentoVariazioneGenerato.toString());
			sedutaRepository.save(seduta);
		}
		else {
			
			if(isOdGBase(odg) && seduta.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiSeduta.sedutaInPredisposizione.toString())) {
				seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaOdgOdlBase
						.toStringByOrgano(seduta.getOrgano()));
				sedutaRepository.save(seduta);
			}
			odg.setOggetto(""+SedutaGiuntaConstants.statiOdgOdl.odgOdlInAttesaDiFirma
						.toStringByOrgano(seduta.getOrgano()));
			odgRepository.save(odg);

			updateStatoOdGFirmato(odg);
		}
		*/
	}

	/** TODO
	 * Vengono protocollati :
	 * 		- Odg Base
	 * 		- Odg Suppletivo
	 * 	 	- Odg Fuorisacco
	 * 		- Doc Variazione
	 * 		- Doc Annullamento
	 * @param odg
	 * @param savedDocPdf
	 */
//	private void protocollaOdg(DocumentoPdf savedDocPdf) {
//		OrdineGiorno odg = savedDocPdf.getOrdineGiorno();
//		SedutaGiunta seduta = odg.getSedutaGiunta();
//		
//		String dataSeduta = "";
//		if (seduta.getSecondaConvocazioneInizio()!= null){
//			dataSeduta = formatter.print(seduta.getSecondaConvocazioneInizio().toDateTime(DateTimeZone.UTC));
//		} else {
//			dataSeduta = formatter.print(seduta.getPrimaConvocazioneInizio().toDateTime(DateTimeZone.UTC));
//		}
//		String oggetto = String.format("Seduta di Giunta n. %s del %s :: ",	seduta.getNumero(), dataSeduta);
//
//		String tipoDocumento = "";
//		if (isOdGBase(odg)){
//			if (isVariazioneSeduta(odg)){
//				tipoDocumento = "Ordine del Giorno - Variazione";
//				oggetto += "Documento di Variazione Estremi della Seduta di Giunta";
//			} else if (isAnnullamentoSeduta(odg)){
//				tipoDocumento = "Ordine del Giorno - Annullamento";
//				oggetto += "Documento di Annullamento Seduta di Giunta";
//			} else {
//				tipoDocumento = "Ordine del Giorno";
//				oggetto += "Documento Ordine del Giorno";
//			}
//		} else if (isOdGSuppletivo(odg)){
//			tipoDocumento = "Ordine del Giorno - Suppletivo";
//			oggetto += "Documento Ordine del Giorno Suppletivo n. " + odg.getProgressivoOdgSeduta();
//		} else if (isOdGFuoriSacco(odg)){
//			tipoDocumento = "Ordine del Giorno - Fuori Sacco";
//			oggetto += "Documento Ordine del Giorno Fuori Sacco";
//		}
//		List<String> destinatari = notificheService.getDestinatariNotificheSeduta(seduta);
//		
//		// TODO IMPOSTARE I RUOLI, CAPIRE COME DETERMINARE CHI E' IL MITENTE
//		List<String> ruoli_ = new ArrayList<String>();
//		ruoli_.add("Assessore");
//		//ruoli_.add("Consigliere");
//		String ruoli[] = ruoli_.toArray(new String[ruoli_.size()]);
//		
//		try {
//			RispostaProtocollo response = protocollazione.eseguiProtocollazione(env, savedDocPdf.getFile().getContenuto(), 
//					savedDocPdf.getFile().getNomeFile(), tipoDocumento, true, destinatari, ruoli, oggetto);
//			String segnaturaProtocollo = serviceUtil.getStringSegnaturaProtocollo(response.getSegnaturaProtocollo());
//			if (isOdGBase(odg) || isOdGSuppletivo(odg) || isOdGFuoriSacco(odg)){
//				odg.setProtocollo(segnaturaProtocollo);
//			}
//			
//			documentoPdfService.setNumeroDataProtocollo(savedDocPdf.getId(), segnaturaProtocollo, response.getSegnaturaProtocollo().getDataRegistrazione());
//			
//		} catch (ProtocolloCatchedException e) {
//			log.error("Errore protocollazione:" + e.getMessage());
//		}
//	}
	
	/**TODO
	 * Vengono protocollati :
	 * 		- Odg Base
	 * 		- Odg Suppletivo
	 * 		- Doc. Variazione
	 * 		- Doc Annullamento
	 * @param res
	 * @param savedDocPdf
	 */
	/*
	 * NON PREVISTO IN ATTICO
	 *
	private void protocollaResoconto(Resoconto res, DocumentoPdf savedDocPdf) {
		String oggetto = "Generazione Documento di Resoconto";
		String tipoDocumento = "";
		
		if (res.getTipo() == TIPO_RESOCONTO_INTEGRALE){
			tipoDocumento = "Resoconto - Integrale";
		} else if (res.getTipo() == TIPO_RESOCONTO_PARZIALE){
			tipoDocumento = "Resoconto - Parziale";
		} else if (res.getTipo() == TIPO_RESOCONTO_PRESS_ASS){
			tipoDocumento = "Resoconto - Presenze/Assenze";
		}
		
		List<String> destinatari = notificheService.getDestinatariNotificheSeduta(res.getSedutaGiunta());
		
		// TODO IMPOSTARE I RUOLI, CAPIRE COME DETERMINARE CHI E' IL MITENTE
		List<String> ruoli_ = new ArrayList<String>();
		ruoli_.add("Assessore");
		//ruoli_.add("Consigliere");
		String ruoli[] = ruoli_.toArray(new String[ruoli_.size()]);
		
		try {
			TODO RispostaProtocollo response = protocollazione.eseguiProtocollazione(env, savedDocPdf.getFile().getContenuto(), 
					savedDocPdf.getFile().getNomeFile(), tipoDocumento, true, destinatari, ruoli, oggetto);

			documentoPdfService.setNumeroDataProtocollo(
					savedDocPdf.getId(), 
					serviceUtil.getStringSegnaturaProtocollo(response.getSegnaturaProtocollo()), 
					response.getSegnaturaProtocollo().getDataRegistrazione());
			
		} catch (Exception e) {
			log.error("Errore protocollazione:" + e.getMessage());
		}
	}
	*/
	
	@Transactional
	public void generaDocumentoAnnullamento(ReportDTO reportDto,Profilo profilo,Boolean firmato,String firmatario,String createdBy) throws IOException, DocumentException, DmsException, GestattiCatchedException  {
		log.debug( "generaDocumentoAnnullamento:"+ reportDto);
		
		OrdineGiorno odg = odgRepository.getOne(reportDto.getIdAtto());
		SedutaGiunta seduta = odg.getSedutaGiunta();
//		
//		seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString());
//		sedutaRepository.save(seduta);
		reportDto.setIdSeduta(seduta.getId());
		File result = reportService.previewVariazioneEstremiSeduta(reportDto);
		DocumentoPdf savedDocPdf = documentoPdfService.saveOdGPdf(
				odg, result, firmato, false, true, firmatario, createdBy);
		
		// PROTOCOLLA
//		protocollaOdg(savedDocPdf);
		TipoDocumento tipoDoc = null;
		if (seduta.getOrgano().equalsIgnoreCase(
				SedutaGiuntaConstants.organoSeduta.G.name())) {
			tipoDoc = tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.annullamento_seduta_giunta.name());
		}
		else {
			tipoDoc = tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.annullamento_seduta_consiglio.name());
		}
		
		savedDocPdf.setTipoDocumento(tipoDoc);
		documentoPdfRepository.save(savedDocPdf);
	}
	
	@Transactional
	public void generaDocumentoVariazione(ReportDTO reportDto,Profilo profilo,Boolean firmato,String firmatario,String createdBy) throws IOException, DocumentException, DmsException, GestattiCatchedException  {
		log.debug( "generaDocumentoVariazione:"+ reportDto);
		
		OrdineGiorno odg = odgRepository.getOne(reportDto.getIdAtto());
		SedutaGiunta seduta = sedutaRepository.findOne(odg.getSedutaGiunta().getId());
		
		File result = reportService.previewVariazioneSeduta(seduta, profilo, reportDto);
		DocumentoPdf savedDocPdf = documentoPdfService.saveOdGPdf(odg, result, firmato, true, false, firmatario, createdBy);
		
		seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaDocumentoVariazioneGenerato.toString());
		seduta.setFase(SedutaGiuntaConstants.statiSeduta.sedutaDocumentoVariazioneGenerato.getFase());
		sedutaRepository.save(seduta);
		
		// PROTOCOLLA
//		protocollaOdg(savedDocPdf);
		
		TipoDocumento tipoDoc = null;
		if (seduta.getOrgano().equalsIgnoreCase(
				SedutaGiuntaConstants.organoSeduta.G.name())) {
			tipoDoc = tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.variazione_estremi_seduta_giunta.name());
		}
		else {
			tipoDoc = tipoDocumentoRepository.findByCodice(TipoDocumentoEnum.variazione_estremi_seduta_consiglio.name());
		}
		
		savedDocPdf.setTipoDocumento(tipoDoc);
		documentoPdfRepository.save(savedDocPdf);
	}
	
	/*
	 * IN ATTICO NON PREVISTA
	 *
	@Transactional
	public File generaDocumentoGiacenza(ReportDTO reportDto, Boolean includiSospesi) throws IOException, DocumentException  {
		log.debug( "generaDocumentoGiacenza:"+ reportDto);
		
		
		// TODO !!!!
		AttoCriteriaDTO criteria = new AttoCriteriaDTO();
		String stato = SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl
				.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.G.name());
		
		stato += ";" + SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl
				.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.C.name());
		
		if (includiSospesi)
			stato += ";"+SedutaGiuntaConstants.statiAtto.propostaSospesa.toString();
		
		criteria.setStato(stato);
		criteria.setViewtype("tutti");
		Iterable<Atto> iterableAtto = attoService.findAll(criteria);
		
		// TODO ordinamento atti
		List<Atto> orderedList = IteratorUtils.toList(iterableAtto.iterator());
		Collections.sort(orderedList,new Comparator<Atto>(){
			@Override
		    public int compare(Atto atto1, Atto atto2){
				return atto1.getCodiceArea().compareTo(atto2.getCodiceArea());
			}
		});
		
		List<Atto> sezioneUnoUno = new ArrayList<Atto>();
		List<Atto> sezioneUnoDue = new ArrayList<Atto>();
		List<Atto> sezioneDueUno = new ArrayList<Atto>();
		List<Atto> sezioneDueDue = new ArrayList<Atto>();
		for(Atto atto : orderedList){
			if(atto.getEsito() != null && (atto.getEsito().equals("ritirato") || atto.getEsito().equals("non_trattato") || atto.getEsito().equals("rinviato"))){
				if(atto.getUsoEsclusivo() != null && atto.getUsoEsclusivo().equals("sanita")){
					sezioneUnoDue.add(atto);
				}
				else{
					sezioneUnoUno.add(atto);
				}
			}
			else{
				if(atto.getUsoEsclusivo() != null && atto.getUsoEsclusivo().equals("sanita")){
					sezioneDueDue.add(atto);
				}
				else{
					sezioneDueUno.add(atto);
				}
			}
		}
		
		//---------Ordinamento sezione uno - atti rinviati------------------------------
		sezioneUnoUno = this.ordinaParti(sezioneUnoUno); // atti non sanitari
		sezioneUnoDue = this.ordinaParti(sezioneUnoDue); // atti sanitari    		
		
		//---------Ordinamento sezione due - atti nuovi---------------------------------
		sezioneDueUno = this.ordinaParti(sezioneDueUno); // atti non sanitari
		sezioneDueDue = this.ordinaParti(sezioneDueDue); // atti sanitari
		
		orderedList = new ArrayList<Atto>(sezioneUnoUno);
		orderedList.addAll(sezioneUnoDue);
		orderedList.addAll(sezioneDueUno);
		orderedList.addAll(sezioneDueDue);
		
		File result = reportService.previewGiacenza(orderedList,reportDto);
		
		return result;
	}
	*/
	
	/**
	 * Ordinamento prima per tipologia di atto poi per relatore
	 * @param sezione
	 * @return
	 *
	 * IN ATTICO NON PREVISTA
	 *
	private List<Atto> ordinaParti(List<Atto> sezione) {
		Map<String, List<Atto>> sezione_divisa = new HashMap<String,List<Atto>>();
		sezione_divisa.put("SDL", new ArrayList<Atto>());
		sezione_divisa.put("DDL", new ArrayList<Atto>());
		sezione_divisa.put("COM", new ArrayList<Atto>());
//		sezione_divisa.put("DEL", new ArrayList<Atto>());
//		sezione_divisa.put("DELA", new ArrayList<Atto>());
		
		// FIXME ha senso questo codice senza atto.denominazioneRelatore?
//		for(Atto atto : sezione){
//			for(String relatore : this.relatori) {
//				if(atto.getDenominazioneRelatore().equals(relatore)){
//					if(atto.getTipoAtto().getCodice().equals("DEL")){
//						sezione_divisa.get("COM").add(atto);
//					}
//					else{
//						sezione_divisa.get(atto.getTipoAtto().getCodice()).add(atto);
//					}
//				}
//			}
//			
//		}
		
		sezione = new ArrayList<Atto>(sezione_divisa.get("SDL"));
		sezione.addAll(sezione_divisa.get("DDL"));
		sezione.addAll(sezione_divisa.get("COM"));
		
		return sezione;
	}
	*/
	
	@Transactional
	public void rimuoviNumeriArgomento(List <AttiOdg> listArgumentsOdg) {
		if(listArgumentsOdg!=null) {
			List<AttiOdg> toUpts = new ArrayList<AttiOdg>();
			for(AttiOdg aOdg : listArgumentsOdg) {
				if(aOdg != null && aOdg.getId() != null) {
					AttiOdg toUpd = attoOdgRepository.findOne(aOdg.getId());
					toUpd.setNumeroArgomento(null);
					toUpd.setNargOde(null);
					toUpts.add(toUpd);
				}
			}
			if(toUpts.size() > 0) {
				attoOdgRepository.save(toUpts);
			}
		}
	}
	
	@Transactional
	public void saveArguments(List <AttiOdg> listArgumentsOdg,Long idOdg,Long profiloId) throws Exception{
		log.info("save arguments for id odg: "+idOdg);
		
		for (AttiOdg attoOdg : listArgumentsOdg) {
			log.info("save arguments for attoOdg: "+attoOdg.getId());
			if(attoOdg.getAtto()==null) {
				continue;
			}
			
			boolean regAvanzamento = false;
			OrdineGiorno ordineGiorno = null;
			Atto atto1 = null;
			
			if (attoOdg.getId() == null) {
				regAvanzamento = true;
				
				atto1 = attoRepository.findOne(attoOdg.getAtto().getId());
	    		log.debug("Atto1:"+atto1.getId());
	    		
	        	ordineGiorno = odgRepository.findOne(idOdg);
	        	log.debug("OrdineGiornoId:" + ordineGiorno.getId());
	        		        	
	        	attoOdg.setAtto(atto1);
	        	// attoOdg.setId(null);
	        	attoOdg.setOrdineGiorno(ordineGiorno);
			}
			else {
				// Aggiorno ordineOdg - numeroDiscussione - numeroArgomento
				Integer ordineOdg = attoOdg.getOrdineOdg();
				Integer numeroDiscussione = attoOdg.getNumeroDiscussione();
				Integer numeroArgomento = attoOdg.getNumeroArgomento();
				Boolean nArtOde = attoOdg.getNargOde();
				attoOdg = attoOdgRepository.findOne(attoOdg.getId());
				attoOdg.setOrdineOdg(ordineOdg);
				attoOdg.setNumeroDiscussione(numeroDiscussione);
				attoOdg.setNumeroArgomento(numeroArgomento);
				attoOdg.setNargOde(nArtOde);
			}
			
			attoOdgRepository.save(attoOdg);
				
        	if (regAvanzamento) {
	        	String note = "";
				try{
					String dataSeduta = "";
					if (ordineGiorno.getSedutaGiunta().getSecondaConvocazioneInizio() != null){
						dataSeduta = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
								.print(ordineGiorno.getSedutaGiunta().getSecondaConvocazioneInizio());  //.toDateTime(DateTimeZone.UTC));
					}
					else {
						dataSeduta = DateTimeFormat.forPattern("dd/MM/yyyy HH:mm")
								.print(ordineGiorno.getSedutaGiunta().getPrimaConvocazioneInizio());  //.toDateTime(DateTimeZone.UTC));
					}
					String tipoSeduta = "";
					if (ordineGiorno.getSedutaGiunta().getTipoSeduta() == 1)
						tipoSeduta = "Ordinaria";
					else
						tipoSeduta = "Straordinaria";

					String tipoOdg = ordineGiorno.getTipoOdg().getDescrizione();
					if (ordineGiorno.getTipoOdg().getId() == 3)
						tipoOdg += " nr. " + ordineGiorno.getProgressivoOdgSeduta();

					String numeroSeduta = "";
					if (ordineGiorno.getSedutaGiunta().getNumero() != null){
						numeroSeduta = ordineGiorno.getSedutaGiunta().getNumero();
					}
					boolean isOdgGiunta = ordineGiorno.getSedutaGiunta().getOrgano()
							.equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.G.name());
					String OdGOdL = isOdgGiunta?"OdG":"OdL";
					
					note = String.format("Seduta %s nr. %s del %s "+OdGOdL+" %s", tipoSeduta, numeroSeduta, dataSeduta, tipoOdg);
				} 
				catch (Exception exp){
					log.error(exp.getMessage(), exp);
				}
				
				boolean isOdgGiunta = ordineGiorno.getSedutaGiunta().getOrgano()
						.equalsIgnoreCase(SedutaGiuntaConstants.organoSeduta.G.name());
				
				String nomeAttivitaAtto = isOdgGiunta?NomiAttivitaAtto.INSERISCI_IN_ODG:NomiAttivitaAtto.INSERISCI_IN_ODL;
				
				registrazioneAvanzamentoService.impostaStatoAtto(
						atto1.getId().longValue(), profiloId.longValue(), 
	    				SedutaGiuntaConstants.statiAtto.propostaInseritaInOdgOdl
	    					.toStringByOrgano(ordineGiorno.getSedutaGiunta().getOrgano()), 
	    				nomeAttivitaAtto, note);
				
				//TODO workflowService.riattivaAtto(atto1, profiloId);
			}
        	log.debug("Atto post save (attoOdg id):" + attoOdg.getId());
		}
	}
	
	/**
	 * notifica OdG/Variazione <UC_DEL_041>.... 
	 * @param docDaNotificare
	 * @param autore
	 *
	 *  IN ATTICO NON UTILIZZATO
	 *
	public void notificaDocumentoPdf(DocumentoPdf docDaNotificare, Long idProfiloAutore){
		log.info(String.format("Invio notifiche per il DocumentoPdf: %s relativo all'OdG: %s",
				docDaNotificare.getId(), docDaNotificare.getOrdineGiorno().getId()));
		
		if (isOdGBase(docDaNotificare.getOrdineGiorno())){
			if (isVariazioneSeduta(docDaNotificare.getOrdineGiorno())){
				notificheService.notificaDocumentoOdG(docDaNotificare, TipoDocumentoNotificaEnum.VARIAZIONE, idProfiloAutore);
			} else if (isAnnullamentoSeduta(docDaNotificare.getOrdineGiorno())){
				notificheService.notificaDocumentoOdG(docDaNotificare, TipoDocumentoNotificaEnum.ANNULLAMENTO, idProfiloAutore);
			} else {
				notificheService.notificaDocumentoOdG(docDaNotificare, TipoDocumentoNotificaEnum.ODG_BASE, idProfiloAutore);
			}
		} else if (isOdGSuppletivo(docDaNotificare.getOrdineGiorno())){
			notificheService.notificaDocumentoOdG(docDaNotificare, TipoDocumentoNotificaEnum.ODG_SUPPLETIVO, idProfiloAutore);
		} else if (isOdGFuoriSacco(docDaNotificare.getOrdineGiorno())){
			notificheService.notificaDocumentoOdG(docDaNotificare, TipoDocumentoNotificaEnum.ODG_FUORISACCO, idProfiloAutore);
		}
	}
	*/
	
	public boolean isOdGBase(OrdineGiorno odg){
		boolean retValue = false;
		
		if (odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2)
			retValue = true;
		
		return retValue;
	}
	
	public boolean isOdGSuppletivo(OrdineGiorno odg){
		boolean retValue = false;
		
		if (odg.getTipoOdg().getId() == 3)
			retValue = true;
		
		return retValue;
	}
	
	public boolean isOdGFuoriSacco(OrdineGiorno odg){
		boolean retValue = false;
		
		if (odg.getTipoOdg().getId() == 4)
			retValue = true;
		
		return retValue;
	}
	
	/*
	 *  IN ATTICO NON UTILIZZATI
	 *
	public boolean isVariazioneSeduta(OrdineGiorno odg){
		boolean retValue = false;
		
		if (isOdGBase(odg))
			if (odg.getSedutaGiunta().getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaVariazione.toString()))
				retValue = true;
		
		return retValue;
	}
	
	public boolean isAnnullamentoSeduta(OrdineGiorno odg){
		boolean retValue = false;
		
		if (isOdGBase(odg))
			if (odg.getSedutaGiunta().getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString()))
				retValue = true;
		
		return retValue;
	}
	*/
	
	@Transactional(readOnly=true)
	public File recuperaDocumentoOdg(OrdineGiorno odg, boolean firmato){
		File retValue = null;
		
		if (odg != null && odg.getDocumentiPdf() != null){
			long idDocPdf = 0;
			for (DocumentoPdf pdf : odg.getDocumentiPdf()){
				if (pdf.getFirmato() == firmato){
					idDocPdf = pdf.getId();
					break;
				}
			}
			
			if (idDocPdf != 0){
				try{
					it.linksmt.assatti.datalayer.domain.File fileCifra = fileService.getByFileId(idDocPdf); // documentoPdfService.download(idDocPdf);
					retValue = java.io.File.createTempFile("downloadcifra" ,".tmp");
					
					byte[] content = dmsService.getContent(fileCifra.getCmisObjectId());
					
					FileUtils.writeByteArrayToFile(retValue, content);
					retValue.deleteOnExit();
				}
				catch (Exception exp){
					log.error(exp.getMessage(), exp);
				}
			}
			else 
				log.error(String.format("Nessun documento trovato per l'odg con id:%s con firmato = %s", odg.getId(), firmato));
		}
		else {
			if (odg != null)
				log.error(String.format("L'ordine giorno con id:%s non ha documenti associati...", odg.getId()));
			else
				log.error("Impossibile recuperare il documento dell'ordine del giorno! ordinegiorno IS NULL!!");
		}
		
		return retValue;
	}
	
	@Transactional(readOnly=true)
	public Page<AttiOdg> findAllAttiOdg(final Pageable generatePageRequest, final AttiOdgCriteriaDTO criteria){

		BooleanExpression expression = buildAttiOdgSearchExpression(criteria);
		
		Page<AttiOdg> listaAttiOdg = attiOdgRepository.findAllInnerJoinComponentiGiuta(expression, generatePageRequest);
		
		for (AttiOdg ao : listaAttiOdg){
			minimalAttoOdg(ao);
		}
		
		return listaAttiOdg;
	}
	
	@Transactional(readOnly=true)
	public Page<AttiOdg> findAllAttiOdgInSeduteNonAnnullate(final Pageable generatePageRequest, final AttiOdgCriteriaDTO criteria){

		BooleanExpression expression = buildAttiOdgSearchExpression(criteria);
		expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.stato.notIn(SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString(),SedutaGiuntaConstants.statiSeduta.sedutaProvvisoriaAnnullata.toString()));
		
		Page<AttiOdg> listaAttiOdg = attiOdgRepository.findAllInnerJoinComponentiGiuta(expression, generatePageRequest);
		
		for (AttiOdg ao : listaAttiOdg){
			minimalAttoOdg(ao);
			if(ao!=null && ao.getEsito()!=null && 
				(ao.getEsito().equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.rinviatoConsiglio.getCodice()) || ao.getEsito().equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.rinviatoGiunta.getCodice()))
			) {
				Atto atto = new Atto();
				BeanUtils.copyProperties(ao.getAtto(), atto);
				atto.setStato("");
				atto.setNumeroAdozione("");
				atto.setDataAdozione(null);
				ao.setAtto(atto);
			}
		}
		
		return listaAttiOdg;
	}
	
	/**
	 * @param idProfilo
	 * @param dto
	 * @param idDocumento
	 * @return
	 * @throws FirmaRemotaException
	 * @throws Exception
	 *
	 * IN ATTICO NON PREVISTO
	 *
	@Transactional
	public void firmaDocumento(final Long idProfilo, FirmaRemotaDTO dto, Long idDocumento)
			throws FirmaRemotaException, Exception {
		// Firma digitale...
		DocumentoPdf docDaNotificare = firmaRemota(
				idDocumento, 
				dto.getCodiceFiscale(), 
				dto.getPassword(), 
				dto.getOtp());
		
		//TODO protocollaOdg(docDaNotificare);
		uploadDiogAndNotificaAndPubblica(docDaNotificare, idProfilo);
		OrdineGiorno odg = docDaNotificare.getOrdineGiorno();
		setStatoDocumentoConsolidatoSottoscrittoreSeduta(odg);
	}
	*/
	
	/**
	 * @param idProfilo
	 * @param dto
	 * @param idDocumento
	 * @return
	 * @throws FirmaRemotaException
	 * @throws Exception
	 */
	
	/*
	 * IN ATTICO NON PREVISTI 
	 *
	@Transactional
	public Boolean firmaDocumentoResoconto(final Long idProfilo, FirmaRemotaDTO dto, Long idDocumento, OrdineGiorno odg, Long resId)
			throws FirmaRemotaException, Exception {
		// Firma digitale...
		DocumentoPdf docDaNotificare = firmaRemotaResoconto(
				idDocumento, 
				dto.getCodiceFiscale(), 
				dto.getPassword(), 
				dto.getOtp());
		
		List<Resoconto> resoconti = resocontoRepository.findBySedutagiunta(odg.getSedutaGiunta().getId());
		Boolean last = false;
		Resoconto res = resocontoRepository.findOne(resId);
		Long sottoscrittoreId = res.getSottoscrittore().getId();
		SottoscrittoreSedutaGiunta sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndProfilo(res.getSedutaGiunta().getId(), sottoscrittoreId);
		Integer ordine = sottSed.getOrdineFirma() + 1;
		sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndOrdine(res.getSedutaGiunta().getId(), ordine);
		SedutaGiunta seduta = sedutaRepository.findOne(res.getSedutaGiunta().getId());
		if(sottSed == null){
			protocollaResoconto(res, docDaNotificare);
//			IN ATTICO NON PREVISTO
//			riversamentoPoolService.aggiungiRiversamentoResoconto(docDaNotificare);
			res.setDataPubblicazioneSito(new LocalDate());
			resocontoRepository.save(res);
			last = true;
			
			if(res.getTipo() == TIPO_RESOCONTO_INTEGRALE){
				boolean abilitato = sedutaService.abilitatoPresenzeAssenze(seduta);
				seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResFirmato.toString());
				if(!abilitato) {
					seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleInAttesa.toString());
				}
			} else if(res.getTipo() == TIPO_RESOCONTO_PRESS_ASS){
				seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docPresFirmato.toString());
			}
			
			seduta.setSottoscittoreDocumento(null);
		} else {
			res.setSottoscrittore(sottSed.getProfilo());
			resocontoRepository.save(res);
			seduta.setSottoscittoreDocumento(sottSed.getProfilo());
		}
		
		sedutaRepository.save(seduta);
		
//		for(Resoconto res : resoconti){
//			
//			if(res.getId() == resId){
//				
//				Long sottoscrittoreId = res.getSottoscrittore().getId();
//				SottoscrittoreSedutaGiunta sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndProfilo(res.getSedutaGiunta().getId(), sottoscrittoreId);
//				Integer ordine = sottSed.getOrdineFirma() + 1;
//				sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndOrdine(res.getSedutaGiunta().getId(), ordine);
//				if(sottSed == null){
//					protocollaResoconto(res, docDaNotificare);
//					riversamentoPoolService.aggiungiRiversamentoResoconto(docDaNotificare);
//					res.setDataPubblicazioneSito(new LocalDate());
//					resocontoRepository.save(res);
//					last = true;
//				} else {
//					res.setSottoscrittore(sottSed.getProfilo());
//					resocontoRepository.save(res);
//				}
//				
//			} 
//		}
		
		return last;
	}
	
	private DocumentoPdf firmaRemota(long docDaFirmareId, String codFiscale, String password, String otp) 
  			throws FirmaRemotaException, Exception {
		
  		DocumentoPdf docDaFirmare = documentoPdfRepository.findOne(docDaFirmareId);
  		if (docDaFirmare == null) {
  			throw new CifraCatchedException(String.format("DocumentoPDF con id:%s non trovato!!", docDaFirmareId));
  		}
  		
  		it.linksmt.assatti.datalayer.domain.File pdfDaFirmare = docDaFirmare.getFile();
  		byte[] fileContent = dmsService.getContent(pdfDaFirmare.getCmisObjectId());
  		
  		List<FirmaRemotaRequestDTO> reqFirma = new ArrayList<FirmaRemotaRequestDTO>();
  		FirmaRemotaRequestDTO req = new FirmaRemotaRequestDTO(
  							pdfDaFirmare.getNomeFile(), fileContent);
		reqFirma.add(req);
  		
		//firmo docs presi da db
		List<FirmaRemotaResponseDTO> listFirmaRemotaResponseDTO = FirmaRemotaService.firmaPades(
				codFiscale, password, otp, reqFirma);
		
		FirmaRemotaResponseDTO resFirmato = listFirmaRemotaResponseDTO.get(0);
		byte[] signedContent = resFirmato.getDocument();
		
		Utente utente = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		
		DocumentoPdf docFirmatoSalvato = documentoPdfService.saveFileFirmatoOfOdg(
				docDaFirmare.getOrdineGiorno(), utente, 
				docDaFirmare.getTipoDocumento(),
				signedContent, signedContent.length,
				pdfDaFirmare.getContentType());
		
		// TODO: marcatura ???
		//  
//		if(listFirmaRemotaResponseDTO.get(0).getAttachment() != null) {
//			it.linksmt.assatti.datalayer.domain.File pdfFirmatoMarcato = new it.linksmt.assatti.datalayer.domain.File();
//			pdfFirmatoMarcato.setContentType("application/timestamp-query");
//			pdfFirmatoMarcato.setNomeFile(pdfToSign.getNomeFile()+".tsd");
//			pdfFirmatoMarcato.setContenuto(listFirmaRemotaResponseDTO.get(0).getAttachment());
//			pdfFirmatoMarcato.setSize(new Long(listFirmaRemotaResponseDTO.get(0).getAttachment().length));
//			
//			DocumentoInformatico marcatura = new DocumentoInformatico();
//			marcatura.setTitolo("Marcatura temporale");
//			marcatura.setFile(pdfFirmatoMarcato);
//			marcatura.setNomeFile(pdfFirmatoMarcato.getNomeFile());
//			marcatura.setParteIntegrante(false);
//			marcatura.setOmissis(false);
//			marcatura.setDocumentoRiferimento(docFirmatoSalvato);
//			documentoInformaticoService.save(marcatura);
//		}
  		
  		return docFirmatoSalvato;
  	}
	
	
	
	/* private DocumentoPdf firmaRemotaResoconto(long docDaFirmareId, String codFiscale, String password, String otp) 
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
		OrdineGiorno odg = null;
		for(OrdineGiorno temp : docDaFirmare.getResoconto().getSedutaGiunta().getOdgs()){
			odg = temp;
			break;
		}
		
		DocumentoPdf docFirmatoSalvato = documentoPdfService.saveFileFirmatoOfResoconto(
				docDaFirmare.getResoconto(),
				odg, 
				signed, 
				firmatario, 
				"",
				docDaFirmare.getTipoDocumento());
		
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
			marcatura.setDocumentoRiferimento(docFirmatoSalvato);
			documentoInformaticoService.save(marcatura);
		}
  		
  		return docFirmatoSalvato;
  	}
	
	private DocumentoPdf firmaRemotaResoconto(long docDaFirmareId, String codFiscale, String password, String otp) 
  			throws FirmaRemotaException, Exception {
  		
  		List<byte[]> files = new ArrayList<byte[]>();
		
  		DocumentoPdf docDaFirmare = documentoPdfRepository.findOne(docDaFirmareId);
  		if (docDaFirmare == null) {
  			throw new CifraCatchedException(String.format("DocumentoPDF con id:%s non trovato!!", docDaFirmareId));
  		}
  		
  		
  		return docDaFirmare;
  	}
  	*/
	
	/**
	 * @param idProfilo
	 * @param file
	 * @param utente
	 * @param odg
	 * @return
	 * @throws IOException
	 * @throws CifraCatchedException 
	 *
	 *IN ATTICO NON PREVISTO
	 *
	@Transactional
	public void uploadFirmato(
			final Long idProfilo, 
			final MultipartFile file,
			OrdineGiorno odg,
			Long signingDocId) throws IOException, CifraCatchedException {
		//documento non firmato
		DocumentoPdf signingDoc = documentoPdfService.findById(signingDocId);
		
		Utente utente = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
		
		DocumentoPdf doc =  documentoPdfService.saveFileFirmatoOfOdg(
				odg, utente, signingDoc.getTipoDocumento(),
				file.getBytes(), file.getSize(), file.getContentType());
		
		//TODO protocollaOdg(doc);
		uploadDiogAndNotificaAndPubblica(doc, idProfilo);
		setStatoDocumentoConsolidatoSottoscrittoreSeduta(odg);
	}
	*/
	
	/**
	 * @param idProfilo
	 * @param file
	 * @param utente
	 * @param odg
	 * @return
	 * @throws IOException
	 * @throws CifraCatchedException 
	 */
	
	/*
	 * TODO: In ATTICO non previsto
	 * 
	@Transactional
	public Boolean uploadResocontoFirmato(
			final Long idProfilo, 
			final MultipartFile file, 
			Utente utente,
			OrdineGiorno odg,
			Long idResoconto,
			Long signingDocId) throws IOException, CifraCatchedException {
		//documento non firmato
		DocumentoPdf signingDoc = documentoPdfService.findById(signingDocId);
		Resoconto resoconto = resocontoRepository.findOne(idResoconto);
		SedutaGiunta seduta = sedutaRepository.findOne(resoconto.getSedutaGiunta().getId());
		DocumentoPdf doc =  documentoPdfService.saveFileFirmatoOfResoconto(resoconto, odg, file, utente,"", signingDoc.getTipoDocumento());
		List<Resoconto> resoconti = resocontoRepository.findBySedutagiunta(odg.getSedutaGiunta().getId());
		Boolean last = false;
		
		Long sottoscrittoreId = resoconto.getSottoscrittore().getId();
		SottoscrittoreSedutaGiunta sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndProfilo(resoconto.getSedutaGiunta().getId(), sottoscrittoreId);
		Integer ordine = sottSed.getOrdineFirma() + 1;
		sottSed = sottoscrittoresedutaRepository.findBySedutaResocontoAndOrdine(resoconto.getSedutaGiunta().getId(), ordine);
		if(sottSed == null){
			protocollaResoconto(resoconto, doc);
			riversamentoPoolService.aggiungiRiversamentoResoconto(doc);
			resoconto.setDataPubblicazioneSito(new LocalDate());
			resocontoRepository.save(resoconto);
			last = true;
			
			if(resoconto.getTipo() == TIPO_RESOCONTO_INTEGRALE){
				boolean abilitato = sedutaService.abilitatoPresenzeAssenze(seduta);
				seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResFirmato.toString());
				if(!abilitato) {
					seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleInAttesa.toString());
				}
			} else if(resoconto.getTipo() == TIPO_RESOCONTO_PRESS_ASS){
				seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docPresFirmato.toString());
			}
			
			seduta.setSottoscittoreDocumento(null);
		} else {
			resoconto.setSottoscrittore(sottSed.getProfilo());
			resocontoRepository.save(resoconto);
			seduta.setSottoscittoreDocumento(sottSed.getProfilo());
		}
		
		sedutaRepository.save(seduta);
//		for(Resoconto res : resoconti){
//			
//			if(res.getId() == idResoconto){
//				
//			}
//			
//		}
		
		return last;
	}
	*/
	
	
	/*
	 * In ATTICO non previsto
	 * 
	private void uploadDiogAndNotificaAndPubblica(DocumentoPdf docDaNotificare, Long idProfiloAutore){
		// notifica OdG/Variazione <UC_DEL_041>....
		notificaDocumentoPdf(docDaNotificare, idProfiloAutore);

		// Pubblicazione <UC_DEL_042>
		log.debug("pubblicazione <UC_DEL_042>");
		// aggiorno la data di pubblicazione...
		OrdineGiorno odg = docDaNotificare.getOrdineGiorno();
		if (isOdGBase(odg) || isOdGSuppletivo(odg) || isOdGFuoriSacco(odg)){
			odg.setDataPubblicazioneSito(new LocalDate());
			odgRepository.save(odg);
		}
		
		// Aggiorno lo stato dela Seduta...
		SedutaGiunta sedutaSalvata = updateStatoOdGFirmato(docDaNotificare.getOrdineGiorno());
	}
	
	private SedutaGiunta updateStatoOdGFirmato(OrdineGiorno odg){
  		SedutaGiunta sedutaSalvata = null;
  		odg.setOggetto(SedutaGiuntaConstants.statiOdgOdl.odgOdlConsolidato
  				.toStringByOrgano(odg.getSedutaGiunta().getOrgano()));
  		OrdineGiorno odgSalvato = odgRepository.save(odg);
  		
  		if (isOdGBase(odgSalvato)){
  			SedutaGiunta seduta = odgSalvato.getSedutaGiunta();
  			if(seduta.getStato().equals(SedutaGiuntaConstants.statiSeduta.sedutaInAttesaDiFirmaAnnullamento.toString())){
  				seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaAnnullata.toString());
  			}
  			else{
  				seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaConsolidata.toString());
  			}
  			
  			log.debug("seduta.getStato:" + seduta.getStato());
  			sedutaSalvata = sedutaRepository.save(seduta);
  		}
  		
  		return sedutaSalvata; 
  	}
  	*/
	
	/**
	 * Costruisce la Boolean expression da utilizzare come predicate per la successiva findAll.
	 * 
	 * @param criteria
	 * @return 
	 */
	private BooleanExpression buildAttiOdgSearchExpression(final AttiOdgCriteriaDTO criteria) {

		BooleanExpression expression = QAttiOdg.attiOdg.id.isNotNull();
		
		if(criteria!=null && (
				(criteria.getStatoProposta()!=null && !criteria.getStatoProposta().trim().isEmpty()) ||
				(criteria.getNumeroAdozione()!=null && !criteria.getNumeroAdozione().trim().isEmpty()) ||
				(criteria.getDataAdozione()!=null)
		  )
		) {
			expression = expression.and(QAttiOdg.attiOdg.esito.notEqualsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.rinviatoConsiglio.getCodice()).and(QAttiOdg.attiOdg.esito.notEqualsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.rinviatoGiunta.getCodice())));
		}
		
		if(criteria.getStatoProposta() != null && !criteria.getStatoProposta().isEmpty()){
			expression = expression.and(QAttiOdg.attiOdg.atto.stato.equalsIgnoreCase(criteria.getStatoProposta()));
		}
		if(criteria.getCodiceCifra() != null && !criteria.getCodiceCifra().isEmpty()){
			expression = expression.and(QAttiOdg.attiOdg.atto.codiceCifra.containsIgnoreCase(criteria.getCodiceCifra()));
		}
		if(criteria.getNumSeduta() != null && !criteria.getNumSeduta().isEmpty()){
			expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.numero.containsIgnoreCase(criteria.getNumSeduta()));
		}
		if(criteria.getOggetto() != null && !criteria.getOggetto().isEmpty()){
			expression = expression.and(QAttiOdg.attiOdg.atto.oggetto.containsIgnoreCase(criteria.getOggetto()));
		}

		if(criteria.getDataOraSedutaDa() != null && !"".equals(criteria.getDataOraSedutaDa())){
			BooleanExpression secondaConvPredicate = QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.isNotNull()
					.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.goe(criteria.getDataOraSedutaDa()));
			BooleanExpression primaConvPredicate = QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.isNull()
					.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.primaConvocazioneInizio.goe(criteria.getDataOraSedutaDa()));

			expression = expression.and(secondaConvPredicate.or(primaConvPredicate));
		}
		if(criteria.getDataOraSedutaA() != null && !"".equals(criteria.getDataOraSedutaA())){
			BooleanExpression secondaConvPredicate = QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.isNotNull()
					.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.loe(criteria.getDataOraSedutaA()));
			BooleanExpression primaConvPredicate = QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.secondaConvocazioneInizio.isNull()
					.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.primaConvocazioneInizio.loe(criteria.getDataOraSedutaA()));

			expression = expression.and(secondaConvPredicate.or(primaConvPredicate));
		}
		
		if(criteria.getDataAdozione() != null && !"".equals(criteria.getDataAdozione())){
			BooleanExpression adozionePredicate = QAttiOdg.attiOdg.atto.dataAdozione.isNotNull()
					.and(QAttiOdg.attiOdg.atto.dataAdozione.eq(criteria.getDataAdozione()));			

			expression = expression.and(adozionePredicate);
		}
		if(criteria.getNumeroAdozione() != null && !criteria.getNumeroAdozione().isEmpty()){
			expression = expression.and(QAttiOdg.attiOdg.atto.numeroAdozione.eq(criteria.getNumeroAdozione()));
		}
		
		if(criteria.getDataEsecutivitaDa() != null && !"".equals(criteria.getDataEsecutivitaDa())){
			BooleanExpression be = QAttiOdg.attiOdg.atto.dataEsecutivita.isNotNull()
					.and(QAttiOdg.attiOdg.atto.dataEsecutivita.goe(criteria.getDataEsecutivitaDa()));

			expression = expression.and(be);
		}
		if(criteria.getDataEsecutivitaA() != null && !"".equals(criteria.getDataEsecutivitaA())){
			BooleanExpression be = QAttiOdg.attiOdg.atto.dataEsecutivita.isNotNull()
					.and(QAttiOdg.attiOdg.atto.dataEsecutivita.loe(criteria.getDataEsecutivitaA()));
			
			expression = expression.and(be);
		}
		
		
		
		if(criteria.getTipoSeduta() != null && !criteria.getTipoSeduta().equals("")){
			expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.tipoSeduta.eq(Integer.valueOf(criteria.getTipoSeduta())));
		}
		if (!StringUtil.isNull(criteria.getOrgano())) {
			expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.sedutaGiunta.organo.equalsIgnoreCase(criteria.getOrgano()));
		}
				
		if(criteria.getTipoOdg() != null && !criteria.getTipoOdg().equals("")){
			long tipo = Long.valueOf(criteria.getTipoOdg());

			if (tipo == 1)
				expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.tipoOdg.id.loe(2L));
			else
				expression = expression.and(QAttiOdg.attiOdg.ordineGiorno.tipoOdg.id.eq(tipo));
		}
		
		if(criteria.getSottoscrittoreNome() != null && !criteria.getSottoscrittoreNome().equals("")){
			BooleanExpression cgExpr = QComponentiGiunta.componentiGiunta.id.isNotNull();

			cgExpr = cgExpr.and(QComponentiGiunta.componentiGiunta.profilo.utente.username.containsIgnoreCase(criteria.getSottoscrittoreNome()));
			cgExpr = cgExpr.and(QComponentiGiunta.componentiGiunta.isPresidenteFine.eq(true).or(QComponentiGiunta.componentiGiunta.isSegretarioFine.eq(true)));

			expression = expression.and(cgExpr);
		}

		if(criteria.getEsito() != null && !criteria.getEsito().equals("")){
			expression = expression.and(QAttiOdg.attiOdg.esito.equalsIgnoreCase(criteria.getEsito()));
		}
		if (criteria.getModificatoInGiunta() != null){
			expression = expression.and(QAttiOdg.attiOdg.atto.isModificatoInGiunta.eq(criteria.getModificatoInGiunta()));
		}
		
		if(criteria.getSottoscrittoreProfiloId() != null){
			BooleanExpression internalPredicate = buildSottoscrittoreCriteria(criteria.getSottoscrittoreProfiloId());
			
			if(internalPredicate!=null){
				expression = expression.and(internalPredicate);
			}
		}

		return expression;
	}
	
	private BooleanExpression buildSottoscrittoreCriteria(final Long sottoscrittoreProfiloId){
		
		BooleanExpression sottoscrittoreGiuntaPresidente = QComponentiGiunta.componentiGiunta.id.isNotNull()
				.and(QComponentiGiunta.componentiGiunta.profilo.id.eq(sottoscrittoreProfiloId))
				.and(QComponentiGiunta.componentiGiunta.isPresidenteFine.eq(true));
		BooleanExpression sottoscrittoreGiuntaSegretario = QComponentiGiunta.componentiGiunta.id.isNotNull()
				.and(QComponentiGiunta.componentiGiunta.profilo.id.eq(sottoscrittoreProfiloId))
				.and(QComponentiGiunta.componentiGiunta.isSegretarioFine.eq(true));
				
		BooleanExpression predicate = sottoscrittoreGiuntaPresidente.or(sottoscrittoreGiuntaSegretario);
		
		return predicate;
	}
	
	private void minimalAttoOdg(final AttiOdg attoOdg) {
		if (attoOdg.getAtto()!=null)
			minimalAtto(attoOdg.getAtto());
		
		if (attoOdg.getOrdineGiorno()!=null)
			minimalOdg(attoOdg.getOrdineGiorno());
		
		for (ComponentiGiunta comp : attoOdg.getComponenti()){
			comp.setAtto(null);
			comp.setOrdineGiorno(null);
			if (comp.getProfilo()!= null)
				serializeProfilo(comp.getProfilo());
		}
		
	}
	
	private void minimalAtto(final Atto atto) {
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
		atto.setAvanzamento(null);
		atto.setAoo(null);
		atto.setAoo(null);
		atto.setProfilo(null);
		atto.setTipoMateria(null);
		atto.setOrdineGiornos(null);
		atto.setAllegati(null);
		atto.setEmananteProfilo(null);
		atto.setRupProfilo(null);
		atto.setTipoIter(null);
		
		atto.setDocumentiPdf(null);	
		atto.setDocumentiPdfOmissis(null);
		atto.setDocumentiPdfAdozioneOmissis(null);
		atto.setDocumentiPdfAdozione(null);
	}
	
	private void minimalOdg(final OrdineGiorno odg) {
		odg.setAttos(null);
		odg.setDocumentiPdf(null);
		odg.setSottoscrittore(null);
		
		// Dati minimi della seduta...
		odg.getSedutaGiunta().setOdgs(null);
		odg.getSedutaGiunta().setSedutariferimento(null);
		odg.getSedutaGiunta().setSedutaGiuntas(null);
		odg.getSedutaGiunta().setDestinatariInterni(null);
		odg.getSedutaGiunta().setVerbale(null);
		odg.getSedutaGiunta().setResoconto(null);
		odg.getSedutaGiunta().setPresidente(null);
		odg.getSedutaGiunta().setSegretario(null);
		odg.getSedutaGiunta().setVicepresidente(null);
		
	}
	
	private void serializeProfilo(final Profilo profilo) {
		profilo.setHasQualifica(null);
		profilo.setGrupporuolo(null);
		profilo.setTipiAtto(null);
		if (profilo.getUtente() != null)
			profilo.setUtente(new Utente(profilo.getUtente().getId(), profilo
					.getUtente().getCodicefiscale(), profilo.getUtente()
					.getUsername(),profilo.getUtente().getCognome(),profilo.getUtente().getNome()));
		
	}
	
	public SedutaGiunta setStatoDocumentoConsolidatoSottoscrittoreSeduta(OrdineGiorno odg){
		SedutaGiunta seduta = sedutaRepository.findOne(odg.getSedutaGiunta().getId());
		seduta.setSottoscittoreDocumento(null);
		log.debug("Stato seduta:" + seduta.getStato());
		log.debug("Stato doc seduta:" + seduta.getStatoDocumento());
		
		String organo = seduta.getOrgano();
		
		/*
		if(seduta.getStatoDocumento().equals(SedutaGiuntaConstants.statiDocSeduta.docAnnInAttesaDiFirma.toString())){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docAnnFirmato.toString());
		} else
		*/
		if(seduta.getStatoDocumento().equals(SedutaGiuntaConstants.statiDocSeduta.docVarInPredisposizione.toString())){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docVarGenerato.toString());
		} else if(isOdGBase(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlBaseConsolidato.toStringByOrgano(organo));
		} else if(isOdGSuppletivo(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlSuppletivoConsolidato.toStringByOrgano(organo));
		} else if(isOdGFuoriSacco(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlFuoriSaccoConsolidato.toStringByOrgano(organo));
		}
				
		return sedutaRepository.save(seduta);
	}
	
	public void setStatoDocumentoInPredisposizioneSottoscrittoreSeduta(OrdineGiorno odg){
		SedutaGiunta seduta = sedutaRepository.findOne(odg.getSedutaGiunta().getId());
		seduta.setSottoscittoreDocumento(null);
		
		String organo = seduta.getOrgano();
		
		if(isOdGBase(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlBaseInPredisposizione.toStringByOrgano(organo));
		} else if(isOdGSuppletivo(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlSuppletivoInPredisposizione.toStringByOrgano(organo));
		} else if(isOdGFuoriSacco(odg)){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlFuoriSaccoInPredisposizione.toStringByOrgano(organo));
		}
		sedutaRepository.save(seduta);
	}
	
	/*
	 * IN ATTICO NON PREVISTO
	 * 
	public SedutaGiunta setStatoDocumentoInAttesaDiFirmaSottoscrittoreSeduta(OrdineGiorno odg, Profilo sottoscrittore){
		SedutaGiunta seduta = sedutaRepository.findOne(odg.getSedutaGiunta().getId());
		String organo = seduta.getOrgano();
		
		if (sottoscrittore!=null){
			seduta.setSottoscittoreDocumento(sottoscrittore);
		}
		if(odg.getTipoOdg().getId() == 1 || odg.getTipoOdg().getId() == 2){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlBaseInAttesaDiFirma.toStringByOrgano(organo));
		} else if(odg.getTipoOdg().getId() == 3){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlSuppletivoInAttesaDiFirma.toStringByOrgano(organo));
		} else if(odg.getTipoOdg().getId() == 4){
			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlFuoriSaccoInAttesaDiFirma.toStringByOrgano(organo));
		}
		sedutaRepository.save(seduta);
		
		return seduta;
	}
	*/
}
