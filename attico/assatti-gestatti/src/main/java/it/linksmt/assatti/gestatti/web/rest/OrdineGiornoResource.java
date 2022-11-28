package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.joda.time.DateTimeZone;
import org.joda.time.LocalDateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ComponentiGiunta;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SezioneTesto;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.datalayer.repository.ComponentiGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.OrdineGiornoRepository;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.gestatti.web.rest.dto.ResocontoMassivoDTO;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.FileService;
import it.linksmt.assatti.service.OrdineGiornoService;
import it.linksmt.assatti.service.dto.AttiOdgCriteriaDTO;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.ResocontoDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.utility.StringUtil;


/**
 * REST controller for managing OrdineGiorno.
 */
@RestController
@RequestMapping("/api")
public class OrdineGiornoResource {

    private final Logger log = LoggerFactory.getLogger(OrdineGiornoResource.class);
    
    /*
    private final Integer TIPO_RESOCONTO_INTEGRALE = 1;
	private final Integer TIPO_RESOCONTO_PARZIALE = 0;
	private final Integer TIPO_RESOCONTO_PRESS_ASS = 2;
    */
    
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
    private OrdineGiornoService odgService;

    @Inject
    private OrdineGiornoRepository ordineGiornoRepository;
    
    @Inject
    private SedutaGiuntaRepository sedutaGiuntaRepository;
    
    @Inject
	private OrdineGiornoService ordineGiornoService;
    
    @Inject
	private DocumentoPdfService documentoPdfService;
    
    @Inject 
    private AttiOdgRepository attiOdgRepository;
    
    @Inject 
    private WorkflowServiceWrapper workflowService;
    
    @Inject 
    private ComponentiGiuntaRepository componentiGiuntaRepository;
    
    @Inject
    private FileService fileService;
    
    @Inject
    private DmsService dmsService;
    
    //TODO Integrazione @Inject
	//TODO integrazione private WorkflowServiceWrapper workflowService;
    
    /**
     * POST  /ordineGiornos -> Create a new ordineGiorno.
     */
    @RequestMapping(value = "/ordineGiornos",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrdineGiorno> create(@RequestBody OrdineGiorno ordineGiorno) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save OrdineGiorno : {}", ordineGiorno);
	//        if (ordineGiorno.getId() != null) {
	//            return ResponseEntity.badRequest().header("Failure", "A new ordineGiorno cannot already have an ID").build();
	//        }
	    	
	    	SedutaGiunta temp = null;
	    	if(ordineGiorno.getSedutaGiunta() != null && ordineGiorno.getSedutaGiunta().getId() != null){
	    		temp = sedutaGiuntaRepository.findOne(ordineGiorno.getSedutaGiunta().getId());
	    	}
	    	
	    	if(ordineGiorno.getSedutaGiunta() != null && ordineGiorno.getSedutaGiunta().getDataOra() != null){
	    		ordineGiorno.getSedutaGiunta().getDataOra().getYear();
	    	}
	    	else if(ordineGiorno.getSedutaGiunta() != null && ordineGiorno.getSedutaGiunta().getId() != null){
	    		temp.getDataOra().getYear();
	    	}
	    	
	    	// IN ATTICO ELIMINATE QUESTO TIPO DI LOGICHE
	    	/*
	    	if(ordineGiornoService.isOdGBase(ordineGiorno)){
	    		String progressivo = codiceProgressivoService.generaCodiceOrdineGiorno(anno);//,ordineGiorno.getTipoOdg());
		        
		        if(progressivo != null){
		        	ordineGiorno.setNumeroOdg(progressivo);
		        }
	    	}else{
	    		// mi calcolo il progressivo per suppletivo e fuori sacco relativo alla seduta
	    		
	    		// Controllo se è possibile creare l'odg
	    		DateTime inizio = temp.getPrimaConvocazioneInizio();
	    		if(temp.getSecondaConvocazioneInizio() != null){
	    			inizio = temp.getSecondaConvocazioneInizio();
	    		}
	    		inizio = inizio.withZone(DateTimeZone.UTC);
	    		DateTime today = new DateTime(DateTimeZone.UTC);
	    			    		
	    		Duration duration = new Duration(inizio, today);
	    		
	    		long hours = duration.getStandardHours();
	    		
	    		log.debug("Differenza ore:" + hours);
	    		log.debug(inizio.toString());
	    		log.debug(today.toString());
	    		
	    		if(ordineGiornoService.isOdGSuppletivo(ordineGiorno) && hours > -1 || ordineGiornoService.isOdGFuoriSacco(ordineGiorno) && hours < 0){
	    			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	    		}
	    			    		
	    	}
	    	*/
	    	if(ordineGiorno.getPreambolo()==null) {
	    		ordineGiorno.setPreambolo(new SezioneTesto());
	    	}
	        ordineGiorno =  ordineGiornoRepository.save(ordineGiorno);
	        ordineGiornoService.setStatoDocumentoInPredisposizioneSottoscrittoreSeduta(ordineGiorno);
	        
	        return new ResponseEntity<OrdineGiorno>(ordineGiorno,HttpStatus.OK);
	        //return ResponseEntity.created(new URI("/api/ordineGiornos/" + ordineGiorno.getId())).build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /ordineGiornos -> Updates an existing ordineGiorno.
     */
    @RequestMapping(value = "/ordineGiornos",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody final OrdineGiorno ordineGiorno,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update OrdineGiorno : {}", ordineGiorno);
	        if (ordineGiorno.getId() == null) {
	            create(ordineGiorno);
	        }
	        //ordineGiornoRepository.save(ordineGiorno);
	        List<AttiOdg> list = new ArrayList<AttiOdg>();
			list.addAll(ordineGiorno.getAttos());
			log.debug("Atti ODG list size:" + list.size());
			
	        ordineGiornoService.save(ordineGiorno, list,profiloId);
			
			return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ordineGiornos -> get all the ordineGiornos.
     */
    @RequestMapping(value = "/ordineGiornos",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<OrdineGiorno>> getAll(@RequestParam(value = "page" , required = false) final Integer offset,
                                  @RequestParam(value = "per_page", required = false) final Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<OrdineGiorno> page = ordineGiornoRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ordineGiornos", offset, limit);
	        return new ResponseEntity<List<OrdineGiorno>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /ordineGiornos/:id -> get the "id" ordineGiorno.
     */
    @RequestMapping(value = "/ordineGiornos/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<OrdineGiorno> get(@PathVariable final Long id, final HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get OrdineGiorno : {}", id);
	        OrdineGiorno ordineGiorno = ordineGiornoRepository.findOne(id);
	        if (ordineGiorno == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(ordineGiorno, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /ordineGiornos/esito -> get the "id" ordineGiorno.
     */
    @RequestMapping(value = "/ordineGiornos/esito/{attiodg}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<ResocontoDTO> getEsito(
    		@PathVariable final Long attiodg, 
    		final HttpServletResponse response) throws GestattiCatchedException{
    	try{
    		log.debug("REST request to get Resoconto : {}", attiodg);
    		
    		ResocontoDTO resoconto = new ResocontoDTO();
    		
    		AttiOdg attoOdg = attiOdgRepository.findOne(attiodg);
    		if (attoOdg == null) {
 	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
 	        }
    		if(attoOdg.getComposizione()!=null) {
    			resoconto.setComposizione(attoOdg.getComposizione());
    		}
    		resoconto.setEsito(attoOdg.getEsito());
    		resoconto.setId(attoOdg.getId());
    		resoconto.setSedutaConvocata(attoOdg.getSedutaConvocata());
    		resoconto.setAttoPresentato(attoOdg.getAttoPresentato());
    		resoconto.setDichiarazioniVoto(attoOdg.getDichiarazioniVoto());
    		resoconto.setVotazioneSegreta(attoOdg.getVotazioneSegreta());
    		resoconto.setVotazioneIE(attoOdg.getVotazioneIE());
    		resoconto.setApprovataIE(attoOdg.getApprovataIE());
    		resoconto.setNumPresenti(attoOdg.getNumPresenti());
    		resoconto.setNumAssenti(attoOdg.getNumAssenti());
    		resoconto.setNumFavorevoli(attoOdg.getNumFavorevoli());
    		resoconto.setNumContrari(attoOdg.getNumContrari());
    		resoconto.setNumAstenuti(attoOdg.getNumAstenuti());
    		resoconto.setNumNpv(attoOdg.getNumNpv());
    		
    		if(attoOdg.getDataDiscussione()==null) {
    			resoconto.setDataDiscussione(attoOdg.getAtto().getDataAdozione());
    		} else {
    			resoconto.setDataDiscussione(attoOdg.getDataDiscussione());
    		}

    		if (attoOdg.getAtto().getIsModificatoInGiunta()!=null)
    			resoconto.setIsAttoModificatoInGiunta(attoOdg.getAtto().getIsModificatoInGiunta());
    		else
    			resoconto.setIsAttoModificatoInGiunta(false);

    		List<ComponentiGiunta> componenti = componentiGiuntaRepository.findByAtto(attoOdg);
    		for(ComponentiGiunta componente : componenti){
    			componente.getProfilo().setAoo(null);
    			componente.getOrdineGiorno().setAttos(null);
    			componente.getOrdineGiorno().setSedutaGiunta(null);
    			componente.getAtto().setOrdineGiorno(null);
    			componente.getAtto().setAtto(null);
    			
//    			if (Boolean.TRUE.equals(componente.getIsPresidenteInizio())){
//    				resoconto.setPresidenteInizio(componente);
//    			}
//    			if (Boolean.TRUE.equals(componente.getIsSegretarioInizio())){
//    				resoconto.setSegretarioInizio(componente);
//    			}
    			if (Boolean.TRUE.equals(componente.getIsPresidenteFine())){
    				resoconto.setPresidenteFine(componente);
    			}
    			if (Boolean.TRUE.equals(componente.getIsSegretarioFine())){
    				resoconto.setSegretarioFine(componente);
    			}
    			
    			if (Boolean.TRUE.equals(componente.getIsPresidenteIE())){
    				resoconto.setPresidenteIE(componente);
    			}
    			if (Boolean.TRUE.equals(componente.getIsSindaco())){
    				resoconto.setSindaco(componente);
    			}
    			
    			if (Boolean.TRUE.equals(componente.getIsSegretarioIE())){
    				resoconto.setSegretarioIE(componente);
    			}
    			
    			if (Boolean.TRUE.equals(componente.getIsScrutatore())) {
    				List<ComponentiGiunta> scrutatori = resoconto.getScrutatori();
    				if (scrutatori == null) {
    					scrutatori = new ArrayList<ComponentiGiunta>();
    				}
    				
    				scrutatori.add(componente);
    				resoconto.setScrutatori(scrutatori);
    			}
    			if (Boolean.TRUE.equals(componente.getIsScrutatoreIE())) {
    				List<ComponentiGiunta> scrutatoriIE = resoconto.getScrutatoriIE();
    				if (scrutatoriIE == null) {
    					scrutatoriIE = new ArrayList<ComponentiGiunta>();
    				}
    				
    				scrutatoriIE.add(componente);
    				resoconto.setScrutatoriIE(scrutatoriIE);
    			}
    		}
    		
    		resoconto.setComponenti(componenti);
	        
	        return new ResponseEntity<>(resoconto, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }



    /**
     * DELETE  /ordineGiornos/:id -> delete the "id" ordineGiorno.
     */
    @RequestMapping(value = "/ordineGiornos/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable final Long id,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to delete OrdineGiorno : {}", id);
	        odgService.delete(null,id,profiloId);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }


    /**
     * GENERA DOC ODG  /ordineGiornos/:id -> 
     */
    @RequestMapping(value = "/ordineGiornos/generadocodg",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void generaDocOdg(@RequestParam(value = "odgId"  ,required=true ) final Long odgId,
    		@RequestParam(value = "modelloId", required=true ) final Long modelloId,
    		@RequestParam(value = "sottoscrittoreId", required=false ) final Long profiloSottoscrittoreId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{

    	try{
	    	log.debug("REST request to delete generaDocOdg : {}", odgId);
	       
	    	ReportDTO rep = new ReportDTO();
	    	rep.setIdAtto(odgId);
	    	rep.setIdModelloHtml(modelloId);
	    	rep.setIdProfiloSottoscrittore(profiloSottoscrittoreId);
	        odgService.generaDocumentoOrdinegiorno(rep, false, "", profiloId);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GENERA DOC RESOCONTO  /ordineGiornos/:id -> 
     */
    /*
	 * TODO: In ATTICO non previsto
	 *
    @RequestMapping(value = "/ordineGiornos/generadocresoconto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void generaDocResoconto(
    		@RequestParam(value = "odgId"  ,required=true ) final Long odgId,
    		@RequestParam(value = "tipo"  ,required=true ) final String tipo,
    		@RequestParam(value = "modelloId"  ,required=true ) final Long modelloId,
    		@RequestParam(value = "sottoscrittoreId"  ,required=false ) final Long sottoscrittoreId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws CifraCatchedException{
    	try{
	    	log.debug("REST request to generadocresoconto : {}", odgId);
	       
	    	ReportDTO rep = new ReportDTO();
	    	rep.setIdAtto(odgId);
	    	rep.setIdModelloHtml(modelloId);
	        odgService.generaDocumentoResoconto(rep, tipo,profiloId, "", sottoscrittoreId);
	        if(tipo.equals("resoconto")){
	        	odgService.pubblicaDocumentoResocontoParziale(odgId);
	        	odgService.chiusuraResoconto(odgId, profiloId);
	        }
	        
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }

    @RequestMapping(value = "/ordineGiornos/pubblicadocresoconto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void pubblicaDocResoconto(@RequestParam(value = "odgId"  ,required=true ) final Long odgId) throws CifraCatchedException{
    	try{
	    	log.debug("REST request to public resoconto : {}", odgId);
	        
	    	odgService.pubblicaDocumentoResoconto(odgId);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/ordineGiornos/saveargumentsodg",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> saveargumentsodg(
    		@RequestParam(value = "odgId" , required=true) final Long odgId,
    		@RequestBody final  List <AttiOdg> listArgumentsOdg,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws CifraCatchedException{
    	try{
	    	log.debug("REST request saveargumentsodg listArgumentsOdg: {}", listArgumentsOdg);
	    	odgService.saveArguments(listArgumentsOdg, odgId,profiloId);
	    	
	    	// boolean rispostaTask = workflowService.prendiInCaricoTask( task.getId(),profiloId );
	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new CifraCatchedException(e);
    	} 
    }
    */
    
    @RequestMapping(value = "/ordineGiornos/salvaesiti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> salvaEsiti(
    		@Valid @RequestBody final ResocontoMassivoDTO resoconto, 
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		ResocontoDTO resocontoDTO = resoconto.getResoconto();
    		List<Long> atti = resoconto.getAtti();
    		
    		/* 
    		 *  La predisposizione del resocono avviene dopo la conferma di tutti gli esiti
    		 *
    		if(atti.size() > 0){
    			AttiOdg attoOdg = attiOdgRepository.findOne(atti.get(0));
    			SedutaGiunta seduta = sedutaGiuntaRepository.findOne(attoOdg.getOrdineGiorno().getSedutaGiunta().getId());
    			seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.docResInPredisposizione.toString());
        		seduta.setSottoscittoreDocumento(null);
        		sedutaGiuntaRepository.save(seduta);
    		}
    		*/
    		
    		for(Long attoOdgId : atti){
    			ordineGiornoService.salvaEsiti(profiloId, resocontoDTO, attoOdgId);
    		}
    		
    		
    		
    		
    		
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    @RequestMapping(value = "/ordineGiornos/confermaesito",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> confermaEsito(
    		@RequestParam( value = "attoOdgId", required=true) final Long attoOdgId,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		JsonObject json = new JsonObject();
    		ordineGiornoService.confermaEsito(profiloId, attoOdgId);
    		DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
    		String date = formatter.print(LocalDateTime.now().toDateTime(DateTimeZone.UTC));
    		json.addProperty("data", date);
    		return new ResponseEntity<String>(json.toString() , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    
    
    
    @RequestMapping(value = "/ordineGiornos/nontrattati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> nontrattati(
    		@RequestParam( value = "attoOdgId", required=true) final List<Long> attoOdgId,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		ordineGiornoService.impostaNonTrattati(attoOdgId, SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice());
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
    		
    		
	@RequestMapping(value = "/ordineGiornos/reimpostatrattati",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> reimpostatrattati(
    		@RequestParam( value = "attoOdgId", required=true) final List<Long> attoOdgId,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
		try{
    		ordineGiornoService.impostaNonTrattati(attoOdgId, null);
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }

	@RequestMapping(value = "/ordineGiornos/annullaesito",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> annullaEsito(
    		@RequestParam( value = "attoOdgId", required=true) final Long attoOdgId,
    		@RequestHeader(value="profiloId", required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		ordineGiornoService.annullaEsito(profiloId, attoOdgId);
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
	
	
    @RequestMapping(value = "/ordineGiornos/modificacomponenti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> modificaComponenti(
    		@Valid @RequestBody final ResocontoDTO resoconto, 
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		AttiOdg attoOdg = attiOdgRepository.findOne(resoconto.getId());
    		ordineGiornoService.salvaEsiti(profiloId, resoconto, attoOdg.getId());
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
	
    /* 
     * In attico non consentito -> da resoconto massivo
     * 
    @RequestMapping(value = "/ordineGiornos/salvaesito",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> salvaEsito(@Valid @RequestBody final ResocontoDTO resoconto,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws CifraCatchedException{
    	try{
    		log.debug("salvaResoconto - AttiOdg id:" + resoconto.getId());
    		log.debug("salvaResoconto - Esito:" + resoconto.getEsito());
    		log.debug("salvaResoconto - Componenti:"+ resoconto.getComponenti().size());
    		log.debug("salvaResoconto - AttoModificatoInGiunta:"+ resoconto.getIsAttoModificatoInGiunta());
    		
    		AttiOdg attoOdg = attiOdgRepository.findOne(resoconto.getId());
    		
    		if (attoOdg == null) {
  				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  			}
   			
    		ordineGiornoService.updateEsito(resoconto, attoOdg, profiloId);
    			
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new CifraCatchedException(e);
    	} 
    }
	*/
	
    /* 
     * Per ATTICO non consentita la cancellazione dell'esito
     * 
    @RequestMapping(value = "/ordineGiornos/cancellaesiti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> cancellaEsiti(@Valid @RequestBody final ResocontoMassivoDTO resoconto,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws CifraCatchedException{
    	try{
    		resoconto.getResoconto();
    		List<Long> atti = resoconto.getAtti();
    		for(Long attoOdgId : atti){
    			AttiOdg attoOdg = attiOdgRepository.findOne(attoOdgId);
    			if(attoOdg != null) {
    				ordineGiornoService.cancellaEsiti(profiloId, attoOdg);
        		}
    		}
    		
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	} catch(Exception e){
    		throw new CifraCatchedException(e);
    	} 
    }

	
    @RequestMapping(value = "/ordineGiornos/cancellaesito",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> cancellaEsito(@Valid @RequestBody final ResocontoDTO resoconto,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws CifraCatchedException{
    	try{
    		log.debug("cancellaEsito - AttiOdg id:" + resoconto.getId());
    		log.debug("cancellaEsito - Esito:" + resoconto.getEsito());
    		log.debug("cancellaEsito - Componenti:"+ resoconto.getComponenti().size());
    		
    		AttiOdg attoOdg = attiOdgRepository.findOne(resoconto.getId());
    		if (attoOdg == null) {
  				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  			}
    		
    		ordineGiornoService.cancellaEsito(resoconto, profiloId, attoOdg);
    		
    		return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	} 
    }
	** END -  Per ATTICO non consentita la cancellazione dell'esito */
	
    /*
     * CASISTICA ELIMINATA
     *
    @RequestMapping(value = "/ordineGiornos/numeraatti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> numeraAtti(@RequestBody final  List <Atto> listArgumentsOdg,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId)throws CifraCatchedException {
    	try{
	    	log.debug("REST request numeraAtti listArgumentsOdg: {}", listArgumentsOdg);
	
	    	ordineGiornoService.numeraAtti(listArgumentsOdg, profiloId);
	    	
	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    /* 
     * Il processo prevede la generazione del documento in una fase successiva 
	 *
    @RequestMapping(value = "/ordineGiornos/generadocprovv",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> generaDocProvvedimento(@RequestBody final  List <Atto> listArgumentsOdg,@RequestHeader(value="profiloId" ,required=true) final Long profiloId)throws CifraCatchedException {
    	try{
	    	log.debug("REST request generaDocProvvedimento listArgumentsOdg: {}", listArgumentsOdg);
	
	    	ordineGiornoService.generaDocProvvedimento(listArgumentsOdg, profiloId);
	    	
	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
    
    
    /* 
     * Il processo attico non consente l'annullamento della numerazione
	 *
    @RequestMapping(value = "/ordineGiornos/annullanumerazione",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> annullaNumerazione(@RequestBody final  List <Atto> listArgumentsOdg,@RequestHeader(value="profiloId" ,required=true) final Long profiloId)throws CifraCatchedException {
    	try{
	    	log.debug("REST request numeraAtti listArgumentsOdg: {}", listArgumentsOdg);
	
	    	ordineGiornoService.annullaNumerazione(listArgumentsOdg, profiloId);
	    	
	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */

    @RequestMapping(value = "/ordineGiornos/suspendatti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> suspendatti(
    		@RequestBody final  List <Atto> listArgumentsOdg,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId)
    				throws GestattiCatchedException {
    	try{
	    	log.debug("REST request suspendatti listArgumentsOdg: {}", listArgumentsOdg);
	
	    	for (Atto atto : listArgumentsOdg){
	    		
	    		registrazioneAvanzamentoService.impostaStatoAtto(
	    				atto.getId().longValue(), profiloId.longValue(), 
	    				SedutaGiuntaConstants.statiAtto.propostaSospesa.toString(), 
	    				NomiAttivitaAtto.PROPOSTA_SOSPENSIONE, atto.getNote());
	    	}

	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    @RequestMapping(value = "/ordineGiornos/activateatti",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Boolean> activateatti(@RequestBody final  List <Atto> listArgumentsOdg,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request activateatti listArgumentsOdg: {}", listArgumentsOdg);
	
	    	for (Atto atto : listArgumentsOdg) {
	    		
	    		String organo = null;
	    		if (workflowService.isAttesaEsitoGiunta(atto.getId().longValue())) {
	    			organo = SedutaGiuntaConstants.organoSeduta.G.name();
	    		}
	    		else if (workflowService.isAttesaEsitoConsiglio(atto.getId().longValue())) {
	    			organo = SedutaGiuntaConstants.organoSeduta.C.name();
	    		}
	    		
	    		if (StringUtil.isNull(organo)) {
	    			throw new GestattiCatchedException("Impossibile leggere l'organo della seduta se Giunta o Consiglio.");
	    		}
	    		
	    		registrazioneAvanzamentoService.impostaStatoAtto(
	    				atto.getId().longValue(), profiloId.longValue(), 
	    				SedutaGiuntaConstants.statiAtto.propostaInseribileInOdgOdl.toStringByOrgano(organo), 
	    				NomiAttivitaAtto.PROPOSTA_RIATTIVA, null);
	    	}
	    	
	    	return new ResponseEntity<Boolean>(true , HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
  	 * GET/ordineGiornos/{id}/allegato/{idAllegato}-> download documento versione pdf
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/ordineGiornos/{id}/documento/{idDocumento}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			@PathVariable final Long idDocumento,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download documento versione pdf : {}", id);
	
			OrdineGiorno odg = ordineGiornoRepository.findOne(id);
			if (odg == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
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
  	 * GET/ordineGiornos/stampagiacenza-> download documento versione pdf
	 * @throws IOException
  	 *
	 * IN ATTICO NON PREVISTA
	 *
  	@RequestMapping(value = "/ordineGiornos/stampagiacenza/{idModello}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> stampaGiacenza(
  			@PathVariable final Long idModello,
  			@RequestParam("includiSospese") final Boolean includiSospese,
  			final HttpServletResponse response) throws CifraCatchedException {
  		
  		try{
	  		log.debug("REST request to stampaGiacenza : {}", idModello, String.valueOf(includiSospese));
	
	  		ReportDTO rep = new ReportDTO();	    	
	    	rep.setIdModelloHtml(idModello);
	    	java.io.File file = odgService.generaDocumentoGiacenza(rep, includiSospese);
	    	
//	    	File filePdf = new File("Giacenza.pdf", "application/pdf", null ,null );
			FileInputStream in = new FileInputStream(file);
			byte []  contenuto = IOUtils.toByteArray(in);
			IOUtils.closeQuietly(in);
			
			return DownloadFileUtil.responseStream(contenuto, "Giacenza.pdf", new Long(contenuto.length), "application/pdf");
  		}
  		catch(Exception e){
    		throw new CifraCatchedException(e);
    	}	
  	}
  	
  	/**
	 *
	 * POST /ordineGiornos/{id}/firmato/allegato -> upload documento firmato
	 * @param id
	 * @param idProfilo
	 * @param file
	 * @return
	 * @throws IOException
	 *
	 * IN ATTICO NON PREVISTA
	 *
  	@RequestMapping(value = "/ordineGiornos/{id}/firmato/allegato", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
  	@Timed
  	public @ResponseBody ResponseEntity<String> uploadfirmato(
  			@PathVariable final Long id,
  			@RequestParam("idProfilo") final Long idProfilo,
  			@RequestParam("file") final MultipartFile file,
  			@RequestParam(value = "idResoconto", required=false) final Long idResoconto,
  			@RequestParam("signingDocId") final Long signingDocId) throws CifraCatchedException {
  		
  		try {
  			log.debug("REST request to upload documento firmato : {}", id);
  			
  			HttpStatus httpStatus = HttpStatus.OK;
  	  		JsonObject json = new JsonObject();

  			OrdineGiorno odg = ordineGiornoRepository.findOne(id);
  			if (odg == null) {
  				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
  			}
  			
  			// IN ATTICO NON PREVISTA GENERAZIONE RESOCONTO 
  			// if(idResoconto == null){
  			
  			ordineGiornoService.uploadFirmato(idProfilo, file, odg, signingDocId);
  			
  				// } else {
//  				Boolean last = ordineGiornoService.uploadResocontoFirmato(idProfilo, file, utente, odg,idResoconto, signingDocId);
//  				ordineGiornoService.pubblicaDocumentoResoconto(id);
//  				if(last == true){
//  	  		  		
//  	  		  		Resoconto resoconto = resocontoRepository.findOne(idResoconto);
//  	  		  		if(resoconto.getTipo() == TIPO_RESOCONTO_INTEGRALE){
//  	  		  			resoconto.setStato(SedutaGiuntaConstants.statiResoconto.resocontoConsolidato.toString());
//  	  		  		} else{
//  	  		  			resoconto.setStato(SedutaGiuntaConstants.statiPresenze.presenzeConsolidato.toString());
//  	  		  		}
//  	  		  		
//  	  		  		resocontoRepository.save(resoconto);
//  				}
  			// }
  			
  			return new ResponseEntity<String>(json.toString(), httpStatus);
  		}
  		catch(Exception e){
  			throw new CifraCatchedException(e);
  		}
  	}
  	*/
  	
  	/**
     * Service per la firma di un documento (odg base, odg suppletivo,  variazione)
  	 * GET ordineGiornos/{id}/firmadocumento
     * @param id
     * @param idProfilo
     * @param dto
     * @param response
     * @return
     * @throws CifraCatchedException
     */
  	/*
	 * TODO: In ATTICO non previsto 
	 *
  	@RequestMapping(value = "/ordineGiornos/{id}/firmadocumentoresoconto", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public @ResponseBody ResponseEntity<String> firmaDocumentoResoconto(
  			@PathVariable final Long id,
  			@RequestParam final Long idResoconto,
  			@RequestParam final String tipo,
  			@RequestParam final Long idProfilo,
  			@Valid @RequestBody FirmaRemotaDTO dto,
  			final HttpServletResponse response) throws CifraCatchedException {
  		
  		try{
  			Long idDocumento = dto.getFilesId().get(0);
	  		log.debug("REST request to firmaDocumentoResoconto : {}", id);
	  		HttpStatus httpStatus = HttpStatus.OK;
  	  		JsonObject json = new JsonObject();
  	  		OrdineGiorno odg = ordineGiornoRepository.findOne(id);
			if (resocontoRepository.findOne(idResoconto) == null || odg == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			
	  		Boolean last = ordineGiornoService.firmaDocumentoResoconto(idProfilo, dto, idDocumento, odg, idResoconto);
//	  		ordineGiornoService.pubblicaDocumentoResoconto(id);
	  		if(last == true){
	  			
	  			Resoconto resoconto = resocontoRepository.findOne(idResoconto);
		  		if(tipo.equals("resoconto")){
		  			resoconto.setStato(SedutaGiuntaConstants.statiResoconto.resocontoConsolidato.toString());
		  		} else{
		  			resoconto.setStato(SedutaGiuntaConstants.statiPresenze.presenzeConsolidato.toString());
		  		}
		  		
		  		resocontoRepository.save(resoconto);
		  		
	  		}
	  		
	  		return new ResponseEntity<String>(json.toString(), httpStatus);
  		} catch (FirmaRemotaException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
  		}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
  	}
  	*/
  	
  	/**
     * Service per la firma di un documento (odg base, odg suppletivo,  variazione)
  	 * GET ordineGiornos/{id}/firmadocumento
     * @param id
     * @param idProfilo
     * @param dto
     * @param response
     * @return
     * @throws CifraCatchedException
     * 
     * IN ATTICO NON PREVISTO
     * 
     *
  	@RequestMapping(value = "/ordineGiornos/{id}/firmadocumento", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public @ResponseBody ResponseEntity<String> firmaDocumento(
  			@PathVariable final Long id,
  			@RequestParam final Long idProfilo,
  			@Valid @RequestBody FirmaRemotaDTO dto,
  			final HttpServletResponse response) throws CifraCatchedException {
  		
  		try{
  			Long idDocumento = dto.getFilesId().get(0);
	  		log.debug("REST request to firmaDocumento : {}", id);
	  		HttpStatus httpStatus = HttpStatus.OK;
  	  		JsonObject json = new JsonObject();
	  		
			if (ordineGiornoRepository.findOne(id) == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}

	  		ordineGiornoService.firmaDocumento(idProfilo, dto, idDocumento);
  			
	  		return new ResponseEntity<String>(json.toString(), httpStatus);
  		}
  		catch (FirmaRemotaException e) {
			return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
  		}
  		catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
  	}
	*/
  	
  	
  	/**
     * POST  /ordineGiornos -> search .
     */
    @RequestMapping(value = "/ordineGiornos/searchAttiOdgs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<AttiOdg>> getAllAttiOdgs(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
    		@Valid @RequestBody final AttiOdgCriteriaDTO criteria)  throws GestattiCatchedException{
    	try{
	    	log.debug("POST request to get AttiOdg list : {}", criteria);
    		String ordinamento = "ordineGiorno.sedutaGiunta.dataOra";
    		//String tipoOrdinamento = "desc";
    		Page<AttiOdg> page = ordineGiornoService.findAllAttiOdgInSeduteNonAnnullate(
    				PaginationUtil.generatePageRequest(offset, limit,new Sort(Sort.Direction.DESC, ordinamento)), criteria);
    		
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/ordineGiornos/searchAttiOdgs", offset, limit);
	        return new ResponseEntity<List<AttiOdg>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
  	
}

