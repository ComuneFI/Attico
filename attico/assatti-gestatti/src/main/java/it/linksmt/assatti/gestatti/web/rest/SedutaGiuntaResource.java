package it.linksmt.assatti.gestatti.web.rest;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
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
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.TipoOdg;
import it.linksmt.assatti.datalayer.domain.TipoResocontoEnum;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.repository.OrdineGiornoRepository;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreSedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.TipoOdgRepository;
import it.linksmt.assatti.datalayer.repository.VerbaleRepository;
import it.linksmt.assatti.service.ExcelService;
import it.linksmt.assatti.service.OrdineGiornoService;
import it.linksmt.assatti.service.SedutaGiuntaService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.VerbaleService;
import it.linksmt.assatti.service.dto.SedutaAnnullaDTO;
import it.linksmt.assatti.service.dto.SedutaCriteriaDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing SedutaGiunta.
 */
@RestController
@RequestMapping("/api")
public class SedutaGiuntaResource {

    private final Logger log = LoggerFactory.getLogger(SedutaGiuntaResource.class);

    @Inject
    private SedutaGiuntaRepository sedutaGiuntaRepository;
    
    @Inject
    private SottoscrittoreSedutaGiuntaRepository sottoscrittoreSedutaGiuntaRepository;

    @Inject
    private SedutaGiuntaService sedutaGiuntaService;

    @Inject 
    private CodiceProgressivoService codiceProgressivoService;
    
    @Inject
    private TipoOdgRepository tipoOdgRepository;
    
    @Inject
    private OrdineGiornoRepository ordineGiornoRepository;
    
    @Inject
    private VerbaleRepository verbaleRepository;
    
    @Inject
    private VerbaleService verbaleService;
    
    @Inject
    private OrdineGiornoService ordineGiornoService;
    
    @Inject
	UtenteService utenteService;
    
    @Inject
    ExcelService excelService;
    
    /**
     * POST  /sedutaGiuntas -> Create a new sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody SedutaGiunta sedutaGiunta) throws GestattiCatchedException {
    	try{
    		if(sedutaGiunta.getDataOra() == null){
    			sedutaGiunta.setDataOra(new DateTime());
    		}
	    	log.debug("REST request to save SedutaGiunta : {}", sedutaGiunta);
	        if (sedutaGiunta.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new sedutaGiunta cannot already have an ID").build();
	        }
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        Date toDayMidnight = cal.getTime();
	        
	        cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 23);
	        cal.set(Calendar.MINUTE, 59);
	        cal.set(Calendar.SECOND, 59);
	        cal.set(Calendar.MILLISECOND, 999);
	        
	        Date endOfToday = cal.getTime();
//	        CDFATTICOASM-159
//	        Rilassare vincoli data seduta
//	        if(
//	        	
//	        	(sedutaGiunta.getInizioLavoriEffettiva()!=null && sedutaGiunta.getInizioLavoriEffettiva().isBefore(toDayMidnight.getTime())) ||
//	        	(sedutaGiunta.getSecondaConvocazioneInizio()!=null && sedutaGiunta.getSecondaConvocazioneInizio().isBefore(toDayMidnight.getTime())) ||
////	        	(sedutaGiunta.getPrimaConvocazioneFine()!=null && sedutaGiunta.getPrimaConvocazioneFine().isAfter(endOfToday.getTime())) ||
//	        	(sedutaGiunta.getPrimaConvocazioneInizio()!=null && sedutaGiunta.getPrimaConvocazioneInizio().isBefore(toDayMidnight.getTime()))
//	          ) {
//	        	throw new GestattiCatchedException("ATTENZIONE\u0021 Non \u00E8 possibile selezionare una data di inizio precedente a quella corrente o una di fine successiva a quella corrente");
//	        }
	        
	        sedutaGiunta.setStato(SedutaGiuntaConstants.statiSeduta.odgOdlBaseInPredisposizione
	        		.toStringByOrgano(sedutaGiunta.getOrgano()));
	        sedutaGiunta.setFase(SedutaGiuntaConstants.statiSeduta.odgOdlBaseInPredisposizione.getFase());
	        
	        sedutaGiunta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.odgOdlBaseInPredisposizione
	        		.toStringByOrgano(sedutaGiunta.getOrgano()));
	        sedutaGiunta.setNotificaTuttiAssessori(true);
	        sedutaGiunta.setNotificaTuttiConsiglieri(false);
	        sedutaGiunta.setNotificaTuttiAltreStrutture(false);
	        SedutaGiunta sedutaGiuntaSaved = sedutaGiuntaRepository.save(sedutaGiunta);
	        
	        OrdineGiorno odg = new OrdineGiorno();
	        odg.setSedutaGiunta(sedutaGiuntaSaved);
	        odg.setOggetto(SedutaGiuntaConstants.statiOdgOdl.odgOdlInPredisposizione
	        		.toStringByOrgano(sedutaGiunta.getOrgano()));
	        TipoOdg tipoOdg = tipoOdgRepository.findOne(new Long(sedutaGiuntaSaved.getTipoSeduta()));
	        
	        odg.setTipoOdg(tipoOdg);
	        
	        String progressivo = codiceProgressivoService.generaProgressivoSeduta(
	        		sedutaGiuntaSaved.getPrimaConvocazioneInizio().getYear(), sedutaGiuntaSaved.getOrgano());
	        // .generaCodiceOrdineGiorno(sedutaGiuntaSaved.getDataOra().getYear());//,tipoOdg);
	        
	        if(progressivo != null){
	        	//odg.setNumeroOdg(progressivo);
	        	sedutaGiuntaSaved.setNumero(progressivo);
	        }
	        
	        ordineGiornoRepository.save(odg);
	        
	        // creo qui anche il verbale vuoto...
	        Verbale verbale = new Verbale();
	        verbale.setStato(SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione.toString());
	        verbale.getNarrativa().setTesto("");
	        verbale.getNoteFinali().setTesto("");
	        verbale.setSedutaGiunta(sedutaGiuntaSaved);
	        // aggiungo i sottoscrittori di default...
	        verbale.setSottoscrittori(new TreeSet<SottoscrittoreSedutaGiunta>());
	        verbaleService.addSottoscrittoreVerbale(verbale, sedutaGiuntaSaved.getSegretario(), 1);
	        verbaleService.addSottoscrittoreVerbale(verbale, sedutaGiuntaSaved.getPresidente(), 2);
	        Verbale verbaleSaved = verbaleRepository.save(verbale);
	        // Aggiorno la seduta agganciando il verbale...
	        sedutaGiuntaSaved.setVerbale(verbaleSaved);
	        sedutaGiuntaSaved.setSottoscrittoriresoconto(new TreeSet<SottoscrittoreSedutaGiunta>());
	        sedutaGiuntaSaved = sedutaGiuntaRepository.save(sedutaGiuntaSaved);
	        
	        return ResponseEntity.created(new URI("/api/sedutaGiuntas/" + sedutaGiuntaSaved.getId())).header("id", sedutaGiuntaSaved.getId().toString()).build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /sedutaGiuntas -> Updates an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody SedutaGiunta sedutaGiunta,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update SedutaGiunta : {}", sedutaGiunta);
	        if (sedutaGiunta.getId() == null) {
	            return create(sedutaGiunta);
	        }
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 0);
	        cal.set(Calendar.MINUTE, 0);
	        cal.set(Calendar.SECOND, 0);
	        cal.set(Calendar.MILLISECOND, 0);
	        Date toDayMidnight = cal.getTime();
	        
	        cal = Calendar.getInstance();
	        cal.set(Calendar.HOUR_OF_DAY, 23);
	        cal.set(Calendar.MINUTE, 59);
	        cal.set(Calendar.SECOND, 59);
	        cal.set(Calendar.MILLISECOND, 999);
	        
	        Date endOfToday = cal.getTime();
	        
	        SedutaGiunta sedutaDB = sedutaGiuntaRepository.findOne(sedutaGiunta.getId());
	        if(sedutaDB!= null && sedutaDB.getPrimaConvocazioneInizio() != null && sedutaGiunta.getPrimaConvocazioneInizio() != null&&
	        		sedutaDB.getPrimaConvocazioneInizio().getYear() != sedutaGiunta.getPrimaConvocazioneInizio().getYear()) {
	        	throw new GestattiCatchedException("Non è possibile cambiare l'anno della data convocazione perché è stato generato il numero seduta.");
	        }
	        // CDFATTICOASM-159
	        // Rilassare vincoli data seduta
//	        if(
//	        	(sedutaGiunta.getInizioLavoriEffettiva()!=null && sedutaDB.getInizioLavoriEffettiva() == null && sedutaGiunta.getInizioLavoriEffettiva().isBefore(toDayMidnight.getTime())) ||
//	        	(sedutaGiunta.getSecondaConvocazioneInizio()!=null && sedutaDB.getSecondaConvocazioneInizio() == null && sedutaGiunta.getSecondaConvocazioneInizio().isBefore(toDayMidnight.getTime())) ||
////	        	(sedutaGiunta.getPrimaConvocazioneFine()!=null && sedutaDB.getPrimaConvocazioneFine() == null && sedutaGiunta.getPrimaConvocazioneFine().isAfter(endOfToday.getTime())) ||
//	        	(sedutaGiunta.getPrimaConvocazioneInizio()!=null && sedutaDB.getPrimaConvocazioneInizio() == null && sedutaGiunta.getPrimaConvocazioneInizio().isBefore(toDayMidnight.getTime()))
//	          ) {
//	        	throw new GestattiCatchedException("ATTENZIONE\u0021 Non \u00E8 possibile selezionare una data di inizio precedente a quella corrente o una di fine successiva a quella corrente");
//	        }
	        sedutaGiuntaService.save(sedutaGiunta,profiloId);
	        return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /sedutaGiuntas -> Updates an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}/aggiornaStatoSeduta",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> aggiornaStatoSeduta(
    		@PathVariable final Long id,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestParam(value = "stato" , required = true) String stato,
    		@RequestParam(value = "direzione" , required = true) String direzione,
    		@RequestBody SedutaGiunta sedutaGiunta) throws GestattiCatchedException {
    	try{
	        if (id == null) {
	            return ResponseEntity.notFound().build();
	        }
	        Integer dir = 0;
	        if(direzione.equalsIgnoreCase("up")) {
	        	dir = 1;
	        }else if(direzione.equalsIgnoreCase("down")) {
	        	dir = -1;
	        }
	        if(dir.equals(0)) {
	        	throw new GestattiCatchedException("Si prega di riprovare.");
	        }
	        SedutaGiuntaConstants.statiSeduta statoDaAggiornare = SedutaGiuntaConstants.getStatoSedutaByCodice(stato);
	        if(
	           (sedutaGiunta.getOrgano().equalsIgnoreCase("C") && statoDaAggiornare.name() == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneNumeroArgomento.name()) ||
	           (sedutaGiunta.getOrgano().equalsIgnoreCase("G") && statoDaAggiornare.name() == SedutaGiuntaConstants.statiSeduta.sedutaConclusaRegistrazioneEsiti.name())
	           ) {
	        	sedutaGiuntaService.save(sedutaGiunta,profiloId);
	        }else if(sedutaGiunta.getOrgano().equalsIgnoreCase("C") && statoDaAggiornare.name() == SedutaGiuntaConstants.statiSeduta.sedutaConclusaIndicazioneOrdineDiscussione.name()) {
	        	//rimuovo i numeri di argomento precedentemente inseriti
	        	if(sedutaGiunta.getOdgs()!=null && sedutaGiunta.getOdgs().size() > 0) {
		        	List<AttiOdg> list = new ArrayList<AttiOdg>();
		        	for(OrdineGiorno odg : sedutaGiunta.getOdgs()){
		    			if(odg.getId() != null && odg.getOggetto().equals(SedutaGiuntaConstants.statiOdgOdl.odgOdlInPredisposizione.toStringByOrgano(sedutaGiunta.getOrgano())) || 
		    					odg.getOggetto().equals(SedutaGiuntaConstants.statiOdgOdl.odgOdlConsolidato.toStringByOrgano(sedutaGiunta.getOrgano()))) { // ODG Exist
		    				list.addAll(odg.getAttos());
		    			}
		    		}
		        	ordineGiornoService.rimuoviNumeriArgomento(list);
	        	}
	        }
	        sedutaGiuntaService.aggiornaStatoSeduta(id, dir, statoDaAggiornare, profiloId);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * PUT  /sedutaGiuntas -> Updates numero sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}/editNumeroSeduta",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> editNumeroSeduta(
    		@PathVariable final Long id,
    		@RequestParam(value="newNumeroSeduta" ,required=true) Long newNumeroSeduta
    		) throws GestattiCatchedException {
    	try{
	        if (id == null) {
	            return ResponseEntity.notFound().build();
	        }
	        sedutaGiuntaService.editNumeroSeduta(id, newNumeroSeduta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /sedutaGiuntas -> Updates an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/sottoscrittoriresoconto",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updatesottoscrittori(@RequestBody SedutaGiunta sedutaGiunta,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update SedutaGiunta : {}", sedutaGiunta);
	        if (sedutaGiunta.getId() == null) {
	            return ResponseEntity.notFound().build();
	        }
	        sedutaGiuntaService.saveSottoscrittori(sedutaGiunta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * PUT  /sedutaGiuntas/annulla -> Annulla an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/annulla",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> annulla(@RequestBody SedutaAnnullaDTO param,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to cancel SedutaGiunta : {}");
	        if (param.getSedutaId() == null) {
	            return ResponseEntity.notFound().build();
	        }
	        sedutaGiuntaService.annulla(param,profiloId);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /sedutaGiuntas/annulla -> Annulla an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}/deletesottoscrittore",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> deletesottoscrittore(
    		@PathVariable final Long id,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId
    		) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete SottoscrittoreSedutaGiunta of Resoconto : {}");
	    	SottoscrittoreSedutaGiunta sottoscrittore = sottoscrittoreSedutaGiuntaRepository.findOne(id);
	        if (sottoscrittore == null) {
	            return ResponseEntity.notFound().build();
	        }
	        sottoscrittoreSedutaGiuntaRepository.delete(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /sedutaGiuntas/variazione -> Variazione an existing sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/variazione",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> variazione(@RequestBody SedutaAnnullaDTO param) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to cancel SedutaGiunta : {}");
	        if (param.getSedutaId() == null) {
	            return ResponseEntity.notFound().build();
	        }
	        sedutaGiuntaService.variazione(param);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sedutaGiuntas -> get all the sedutaGiuntas.
     */
    @RequestMapping(value = "/sedutaGiuntas",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SedutaGiunta>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
    		@RequestParam(value = "per_page", required = false) Integer limit)
    			throws GestattiCatchedException{
    	try{
	    	Page<SedutaGiunta> page = sedutaGiuntaRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        
	    	HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sedutaGiuntas", offset, limit);
	        return new ResponseEntity<List<SedutaGiunta>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /sedutaGiuntas -> get all the sedutaGiuntas.
     */
    @RequestMapping(value = "/sedutaGiuntas/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<SedutaGiunta>> getAll(
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit,
    		@Valid @RequestBody final SedutaCriteriaDTO criteria) throws GestattiCatchedException{
    	try{
    		log.debug("POST request to get SedutaGiunta : {}", criteria);
    		String ordinamento = "primaConvocazioneInizio";
    		//String tipoOrdinamento = "desc";
    		Page<SedutaGiunta> page = sedutaGiuntaService.findAll(PaginationUtil.generatePageRequest(
    				offset, limit, new Sort(Sort.Direction.DESC, ordinamento)), criteria);
    		
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/sedutaGiuntas/search", offset, limit);
	        return new ResponseEntity<List<SedutaGiunta>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /sedutaGiuntas -> get all the riferimentoconsentiti.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}/riferimentoconsentiti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<SedutaGiunta>> findAllRiferimentoConsentiti( @PathVariable Long id )
    		throws GestattiCatchedException{
    	try{
	    	Iterable<SedutaGiunta> page = sedutaGiuntaService.findAllRiferimentoConsentiti( id );
	        return new ResponseEntity<Iterable<SedutaGiunta>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /sedutaGiuntas -> get all the riferimentoconsentiti.
     */
    @RequestMapping(value = "/sedutaGiuntas/statidocumenti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<String>> getStatiDocumenti()
    		throws GestattiCatchedException{
    	try{
    		
    		ArrayList<String> stati = new ArrayList<String>();
    		for (SedutaGiuntaConstants.statiDocSeduta stato : SedutaGiuntaConstants.statiDocSeduta.values()) {
    			try {
    				stati.add(stato.toString());
    			}
    			catch(Exception ex) {
    				stati.add(stato.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.G.name()));
    				stati.add(stato.toStringByOrgano(SedutaGiuntaConstants.organoSeduta.C.name()));
    			}
    		}
    		
	        return new ResponseEntity<List<String>>(stati , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    
    /**
     * GET  /sedutaGiuntas -> get all the riferimentoconsentiti.
     */
    @RequestMapping(value = "/sedutaGiuntas/riferimentoconsentiti",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Iterable<SedutaGiunta>> findAllRiferimentoConsentiti( )
    		throws GestattiCatchedException{
    	try{
	    	Iterable<SedutaGiunta> page = sedutaGiuntaService.findAllRiferimentoConsentiti( null );
	        return new ResponseEntity<Iterable<SedutaGiunta>>(page , HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    
    
    /**
     * GET  /sedutaGiuntas/:id -> get the "id" sedutaGiunta.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<SedutaGiunta> get(@PathVariable Long id,@RequestHeader(value="profiloId" ,required=true) final Long profiloId, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get SedutaGiunta : {}", id);
	        SedutaGiunta sedutaGiunta = sedutaGiuntaService.findOne(id,profiloId);
	        if (sedutaGiunta == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(sedutaGiunta, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /sedutaGiuntas/:id -> delete the "id" sedutaGiunta.
     *
     *Le sedute vanno Annullate, non semplicemente eliminate
     *
    @RequestMapping(value = "/sedutaGiuntas/{id}",  
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws CifraCatchedException{
    	try{
	    	log.debug("REST request to delete SedutaGiunta : {}", id);
	        sedutaGiuntaRepository.delete(id);
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
     */
    
    /**
     * GET  /sedutaGiuntas/:id/sottoscrittoripossibili -> get all possibili sottoscrittori del verbale.
     */
    @RequestMapping(value = "/sedutaGiuntas/{id}/sottoscrittoriverbalepossibili",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Profilo>> getSottoscrittoriVerbalePossibili(
    		@PathVariable Long id, HttpServletResponse response,@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException {
    	try {
    		log.debug("REST request to getSottoscrittoriVerbalePossibili : {}", id);
    		SedutaGiunta seduta = sedutaGiuntaService.findOne(id,profiloId);
	        if (seduta == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        
	        List<Profilo> sottoscrittoriPossibili = new ArrayList<Profilo>();
	        
	        if (seduta.getSegretario() != null && seduta.getSegretario().getHasQualifica() != null)
	        	log.debug(String.format("Segretario hasQualifica length:%s", seduta.getSegretario().getHasQualifica().size()));
	        else
	        	log.debug("Segretario hasQualifica is null");
	        if (seduta.getPresidente() != null && seduta.getPresidente().getHasQualifica() != null)
	        	log.debug(String.format("Presidente hasQualifica length:%s", seduta.getPresidente().getHasQualifica().size()));
	        else
	        	log.debug("Presidente hasQualifica is null");
	        
	        sottoscrittoriPossibili.add(seduta.getSegretario());
	        sottoscrittoriPossibili.add(seduta.getPresidente());
	        
	        return new ResponseEntity<List<Profilo>>(sottoscrittoriPossibili, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/chiusuraResoconto",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void chiusuraResoconto(
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to confermaOrdineDiscussione : {}", sedutaId);
    		
	    	sedutaGiuntaService.chiusuraResoconto(sedutaId, profiloId);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    @RequestMapping(value = "/sedutaGiuntas/generadocresoconto",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void generaDocResoconto(
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestParam(value = "tipo"  ,required=true ) final String tipo,
    		@RequestParam(value = "modelloId"  ,required=true ) final Long modelloId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to generadocresoconto : {}", sedutaId);
	    	
	    	if ("doc-definitivo-esito".equalsIgnoreCase(tipo)) {
	    		sedutaGiuntaService.createResoconto(
	    				sedutaId, TipoResocontoEnum.DOCUMENTO_DEF_ESITO, 
	    				modelloId, profiloId, false);
	    	}
	    	else if ("doc-definitivo-elenco-verbali".equalsIgnoreCase(tipo)) {
	    		sedutaGiuntaService.createResoconto( 
	    				sedutaId, TipoResocontoEnum.DOCUMENTO_DEF_ELENCO_VERBALI, 
	    				modelloId, profiloId, false);
	    	}
	    	else {
	    		throw new GestattiCatchedException("Occorre specificare il tipo di Documento Resoconto:"
	    				+ " doc-definitivo-esito oppure doc-definitivo-elenco-verbali");
	    	}
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/getNextNumeroArgomento",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getNextNumeroArgomento(
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
	    	int nextNumArg = sedutaGiuntaService.getNextNumeroArgomento(sedutaId);
	    	JsonObject json = new JsonObject();
			json.addProperty("nextNumArg", nextNumArg);
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/getPrimaConvocazioneFine",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getPrimaConvocazioneFine(
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId) throws GestattiCatchedException{
    	try{
    		DateTime primaConvocazioneFine = sedutaGiuntaService.getPrimaConvocazioneFine(sedutaId);
    		String pcfString = null;
    		if(primaConvocazioneFine!=null) {
    			pcfString = primaConvocazioneFine.toString("yyyy-MM-dd HH:mm:ss");
    		}
	    	JsonObject json = new JsonObject();
			json.addProperty("primaConvocazioneFine", pcfString);
			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/salvaNumeriArgomento",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> salvaNumeriArgomento(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestParam(value = "confermed"  ,required=false ) final Boolean confermed,
    		@RequestBody String attiOdgNumeroArg) throws GestattiCatchedException{
    	try{
    		JsonObject resJson = new JsonObject();
    		JsonParser parser = new JsonParser();
	  		JsonArray attiOdgNumeroJsonArray = parser.parse(attiOdgNumeroArg).getAsJsonArray();
	  		if(attiOdgNumeroJsonArray!=null && !attiOdgNumeroJsonArray.isJsonNull() && attiOdgNumeroJsonArray.iterator().hasNext()) {
				if(confermed!=null && confermed) {
					sedutaGiuntaService.updateNumeriArgomento(attiOdgNumeroJsonArray, profiloId);
					resJson.addProperty("done", true);
				}else {
					Integer nextNum = sedutaGiuntaService.getNextNumeroArgomento(sedutaId);
					resJson.addProperty("nextNum", nextNum);
					Integer numArg = null;
					Integer newNumArg = null;
					List<Integer> numeriInSeduta = new ArrayList<Integer>();
					for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
						if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("newNumArg") && jsonEl.getAsJsonObject().get("newNumArg")!=null && !jsonEl.getAsJsonObject().get("newNumArg").isJsonNull()) {
							if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("numArg") && jsonEl.getAsJsonObject().get("numArg")!=null && !jsonEl.getAsJsonObject().get("numArg").isJsonNull()) {
								numArg = jsonEl.getAsJsonObject().get("numArg").getAsInt();
							}
							newNumArg = jsonEl.getAsJsonObject().get("newNumArg").getAsInt();
							break;
						}else if(jsonEl!=null && !jsonEl.isJsonNull() && jsonEl.getAsJsonObject().has("numArg") && jsonEl.getAsJsonObject().get("numArg")!=null && !jsonEl.getAsJsonObject().get("numArg").isJsonNull()) {
							numeriInSeduta.add(jsonEl.getAsJsonObject().get("numArg").getAsInt());
						}
					}
					String check = "";
					if(numArg!=null && numArg > 0) {
						boolean esisteDuplicatoInQuestaSeduta = numeriInSeduta.contains(numArg);
						if(!esisteDuplicatoInQuestaSeduta) {
							Boolean esisteInAltraSeduta = sedutaGiuntaService.existsNumeroArgomentoInInsediamentoConsiglioOutOfSeduta(sedutaId, numArg, "eq");
							if((esisteInAltraSeduta == null || !esisteInAltraSeduta) && numArg.intValue() == (nextNum.intValue() - 1)) {
								check = "" + numArg.intValue();
							}
						}
					}
					if(check.isEmpty()) {
						check = sedutaGiuntaService.checkResetNumeriArgomento(attiOdgNumeroJsonArray, sedutaId);
					}
					if(check.equalsIgnoreCase("ok")) {
						if(newNumArg.intValue() > nextNum.intValue()) {
							check = "errMax";
						}
					}
					if(check!=null && check.equalsIgnoreCase("ok")) {
						sedutaGiuntaService.updateNumeriArgomento(attiOdgNumeroJsonArray, profiloId);
						resJson.addProperty("done", true);
					}else if(check!=null && check.equalsIgnoreCase("errMax")){
						resJson.addProperty("errMax", true);
					}else if(check!=null){
						resJson.addProperty("toConferm", check);
					}
				}
	  		}
			return new ResponseEntity<>(resJson.toString(), HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/resetNumeriArgomento",
	    method = RequestMethod.PUT,
	    produces = MediaType.APPLICATION_JSON_VALUE)
		@Timed
		public ResponseEntity<String> resetNumeriArgomento(
			@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
			@RequestParam(value = "confermed"  ,required=false ) final Boolean confermed,
			@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
			@RequestBody String attiOdgNumeroArg) throws GestattiCatchedException{
		try{
			JsonParser parser = new JsonParser();
			JsonArray attiOdgNumeroJsonArray = parser.parse(attiOdgNumeroArg).getAsJsonArray();
			
			JsonObject resJson = new JsonObject();
			if(confermed!=null && confermed) {
				sedutaGiuntaService.resetNumeriArgomento(attiOdgNumeroJsonArray, profiloId);
				resJson.addProperty("done", true);
			}else {
				if(attiOdgNumeroJsonArray!=null && !attiOdgNumeroJsonArray.isJsonNull() && attiOdgNumeroJsonArray.iterator().hasNext()) {
					String check = sedutaGiuntaService.checkResetNumeriArgomento(attiOdgNumeroJsonArray, sedutaId);
					if(check!=null && check.equalsIgnoreCase("ok")) {
						sedutaGiuntaService.resetNumeriArgomento(attiOdgNumeroJsonArray, profiloId);
						resJson.addProperty("done", true);
					}else if(check!=null){
						resJson.addProperty("toConferm", check);
					}else {
						resJson.addProperty("error", "Si è verificato un errore. Si prega di riprovare.");
					}
				}else {
					resJson.addProperty("error", "Nessun numero di argomento da resettare.");
				}
			}
			return new ResponseEntity<>(resJson.toString(), HttpStatus.OK);
		}
		catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}
    
    @RequestMapping(value = "/sedutaGiuntas/setStessoNumeroArgomento",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> setStessoNumeroArgomento(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestBody String attiOdgIds) throws GestattiCatchedException{
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonArray attiOdgNumeroJsonArray = parser.parse(attiOdgIds).getAsJsonArray();
	  		if(attiOdgNumeroJsonArray!=null && !attiOdgNumeroJsonArray.isJsonNull() && attiOdgNumeroJsonArray.iterator().hasNext()) {
	  			List<Long> attiOdgIdsList = new ArrayList<Long>();
	  			for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
	  				Long attiOdgId = jsonEl.getAsJsonPrimitive().getAsLong();
	  				if(attiOdgId!=null && attiOdgId > 0) {
	  					attiOdgIdsList.add(attiOdgId);
	  				}
	  			}
	  			sedutaGiuntaService.setStessoNumeroArgomento(sedutaId, attiOdgIdsList, profiloId);
	  		}
			return new ResponseEntity<>(HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/setArgomentiProgressivi",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> setArgomentiProgressivi(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestParam(value = "sedutaId"  ,required=true ) final Long sedutaId,
    		@RequestBody String attiOdgIds) throws GestattiCatchedException{
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonArray attiOdgNumeroJsonArray = parser.parse(attiOdgIds).getAsJsonArray();
	  		if(attiOdgNumeroJsonArray!=null && !attiOdgNumeroJsonArray.isJsonNull() && attiOdgNumeroJsonArray.iterator().hasNext()) {
	  			List<Long> attiOdgIdsList = new ArrayList<Long>();
	  			for(JsonElement jsonEl : attiOdgNumeroJsonArray) {
	  				Long attiOdgId = jsonEl.getAsJsonPrimitive().getAsLong();
	  				if(attiOdgId!=null && attiOdgId > 0) {
	  					attiOdgIdsList.add(attiOdgId);
	  				}
	  			}
	  			sedutaGiuntaService.setArgomentiProgressivi(sedutaId, attiOdgIdsList, profiloId);
	  		}
			return new ResponseEntity<>(HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/sedutaGiuntas/generareport/{idProfilo}/{idSeduta}/{nomeFile}", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<ByteArrayResource> generareport(@PathVariable("idProfilo") Long profiloId,
			 									  @PathVariable("idSeduta") Long sedutaId,
			 									 @PathVariable("nomeFile") String nomeFile) throws GestattiCatchedException {
		try{
			SedutaGiunta seduta = sedutaGiuntaService.findOne(sedutaId,profiloId);
			File result = excelService.createReportSeduta(seduta);
			return returnResponseEntity(result);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
    
    private ResponseEntity<ByteArrayResource> returnResponseEntity(File result)	throws GestattiCatchedException {
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
    
}
