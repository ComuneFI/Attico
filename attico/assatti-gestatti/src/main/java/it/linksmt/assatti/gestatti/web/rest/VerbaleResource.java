package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.repository.SedutaGiuntaRepository;
import it.linksmt.assatti.datalayer.repository.VerbaleRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoInformaticoService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.FileService;
import it.linksmt.assatti.service.VerbaleService;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Verbale.
 */
@RestController
@RequestMapping("/api")
public class VerbaleResource {

    private final Logger log = LoggerFactory.getLogger(VerbaleResource.class);

    @Inject
    private VerbaleRepository verbaleRepository;
    
    @Inject
    private VerbaleService verbaleService;
    
    @Inject
	private DocumentoInformaticoService documentoInformaticoService;
    
//    @Inject
//    private UtenteService utenteService;
    
    @Inject
    private SedutaGiuntaRepository sedutaGiuntaRepository;
    
//    @Inject
//    private SottoscrittoreSedutaGiuntaService sottoscrittoreService;
    
    @Inject
    private DmsService dmsService;
    
    @Inject
    private FileService fileService;
    
    @Inject
	private DocumentoPdfService documentoPdfService;
    
    
    /**
     * POST  /verbales -> Create a new verbale.
     */
    @RequestMapping(value = "/verbales",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody Verbale verbale) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Verbale : {}", verbale);
	        if (verbale.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new verbale cannot already have an ID").build();
	        }
	        
	        SedutaGiunta seduta = sedutaGiuntaRepository.findOne(verbale.getSedutaGiunta().getId());
	        if (seduta == null) {
	        	log.error(String.format("SedutaGiunta con ID:%s non esiste!!", verbale.getSedutaGiunta().getId()));
	        	return ResponseEntity.badRequest().header("Failure", "SedutaGiunta non trovata").build();
	        }
	        verbale.setSedutaGiunta(seduta);

	        if (verbale.getSottoscrittori() != null){
	        	for (SottoscrittoreSedutaGiunta ssv : verbale.getSottoscrittori()){
	        		ssv.setVerbale(verbale);
	        	}
	        }
	        Verbale verbaleSaved = verbaleRepository.save(verbale);
	        
	        seduta.setVerbale(verbaleSaved);
	        SedutaGiunta sedutaSaved = sedutaGiuntaRepository.save(seduta);
	        
	        return ResponseEntity.created(new URI("/api/verbales/" + verbale.getId())).header("idSeduta", sedutaSaved.getId().toString()).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /verbales -> Updates an existing verbale.
     */
    @RequestMapping(value = "/verbales",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody Verbale verbale) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Verbale : {}", verbale);
	        if (verbale.getId() == null) {
	            return create(verbale);
	        } 
	        else {
	        	if (verbale.getDocumentiPdf() != null && verbale.getDocumentiPdf().size() > 0){
	        		for (DocumentoPdf docPDF : verbale.getDocumentiPdf()){
	        			docPDF.setVerbaleId(verbale.getId());
	        		}
	        	}
	        	
	        	if (verbale.getAllegati() != null && verbale.getAllegati().size() > 0){
	        		for (DocumentoInformatico allegato : verbale.getAllegati()){
	        			allegato.setVerbale(verbale);
	        		}
	        	}
	        	
	        	if (verbale.getSottoscrittori() != null && verbale.getSottoscrittori().size() > 0){
		        	for (SottoscrittoreSedutaGiunta ssv : verbale.getSottoscrittori()){
		        		if (ssv.getVerbale() == null || ssv.getVerbale().getId() == null || 
		        				(ssv.getVerbale().getId() != null && ssv.getVerbale().getId() != verbale.getId()))
		        		ssv.setVerbale(verbale);
		        	}
		        }
	        	
	        }
	        
	        Verbale verbaleSaved = verbaleRepository.save(verbale);
	        SedutaGiunta seduta = verbaleSaved.getSedutaGiunta();
	        if (verbaleSaved.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiVerbale.verbaleInPredisposizione.toString())){
	        	seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleInPredisposizione.toString());
	        	sedutaGiuntaRepository.save(seduta);
	        }
	        else if (verbaleSaved.getStato().equalsIgnoreCase(SedutaGiuntaConstants.statiVerbale.verbaleRifiutato.toString())){
	        	seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleNonRichiesto.toString());
	        	seduta.setSottoscittoreDocumento(null);
	        	sedutaGiuntaRepository.save(seduta);
	        	
	        	// In ATTICO la seduta non cambia di stato
	        	// terminaSeduta(seduta);
	        }
	        else if(seduta.getStatoDocumento().equals(SedutaGiuntaConstants.statiDocSeduta.verbaleInAttesa.toString())){
	        	seduta.setStatoDocumento(SedutaGiuntaConstants.statiDocSeduta.verbaleInPredisposizione.toString());
	        	seduta.setSottoscittoreDocumento(null);
	        	sedutaGiuntaRepository.save(seduta);
	        }
	        
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /verbales -> get all the verbales.
     */
    @RequestMapping(value = "/verbales",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Verbale>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Verbale> page = verbaleRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/verbales", offset, limit);
	        return new ResponseEntity<List<Verbale>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /verbales/:id -> get the "id" verbale.
     */
    @RequestMapping(value = "/verbales/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Verbale> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get Verbale : {}", id);
	        Verbale verbale = verbaleService.findOne(id);
	        if (verbale == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(verbaleService.svuotaRiferimenti(verbale), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /verbales/:id -> delete the "id" verbale.
     */
    @RequestMapping(value = "/verbales/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete Verbale : {}", id);
	        verbaleRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
  	 * GET/verbales/{id}/allegato/{idAllegato}-> download documento allegato
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/verbales/{id}/allegato/{idAllegato}", 
  			method = RequestMethod.GET  )
  	@Timed
  	public ResponseEntity<ByteArrayResource> downloadAllegato(
  			@PathVariable final Long id,
  			@PathVariable final Long idAllegato ) throws GestattiCatchedException {
  		try{
	  		log.debug(String.format("REST request to get download documento allegato : {%s} from verbale : {%s}", idAllegato, id));
	        Verbale verbale = verbaleService.findOne(id);
	        
			if (verbale == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			DocumentoInformatico allegato = documentoInformaticoService.findOne( idAllegato );
	
//			File result = documentoInformaticoService.downloadAllegato(allegato.getFile().getId(), allegato.getId());
//	
//			return DownloadFileUtil.responseStream(result);
			
			byte[] content = dmsService.getContent(allegato.getFile().getCmisObjectId());
			
			ByteArrayResource byteArrayResource = new ByteArrayResource(content);
			
//			return new ResponseEntity<ByteArrayResource>(byteArrayResource, HttpStatus.OK);
		 	return ResponseEntity.ok()
		 			.header("Content-Disposition", "attachment;filename=\""+ byteArrayResource.getFilename() + "\"" )
		 			.contentLength(byteArrayResource.contentLength() )
		            .contentType(MediaType.parseMediaType(allegato.getFile().getContentType()) )
		            .body( byteArrayResource );
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	@RequestMapping(value = "/verbales/{id}/allegato", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public @ResponseBody ResponseEntity<Set<DocumentoInformatico>> uploadAllegati(@PathVariable final Long id,
			@RequestParam("file") final MultipartFile[] files) throws GestattiCatchedException{
		try{
			log.debug("REST request to upload allegato for verbale id : {}", id);
			Verbale verbale = verbaleService.findOne(id);
			if (verbale == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			SedutaGiunta seduta = verbale.getSedutaGiunta();
			if (seduta != null && seduta.getId()!= null){
				log.debug(String.format("Seduta giunta id:%s", seduta.getId()));
			}
			documentoInformaticoService.save(verbale, files);
			
			// pulizia per evitare ERR_INCOMPLETE_CHUNKED_ENCODING...
			Verbale v = verbaleService.svuotaRiferimenti(verbale);
			
			return new ResponseEntity<Set<DocumentoInformatico>>(v.getAllegati(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
  	
  	 /**
     * GENERA DOC VERBALE  /verbales/:id/generadocperfirma -> 
     */
    @RequestMapping(value = "/verbales/generadocperfirma",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void generaDocPerFirma(
    		@RequestParam(value = "verbaleId", required=true ) final Long verbaleId,
    		@RequestParam(value = "modelloId", required=true ) final Long modelloId) throws GestattiCatchedException{
    	try{
	    	log.debug(String.format("REST request to generaDocPerFirma : {id:%s, idModello:%s}", verbaleId, modelloId));
	        verbaleRepository.findOne(verbaleId);
	        
	    	ReportDTO rep = new ReportDTO();
	    	rep.setIdAtto(verbaleId);
	    	rep.setIdModelloHtml(modelloId);
	        verbaleService.generaDocumentoPerFirma(rep, false, "", "");
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
  	 * GET /verbales/{id}/documento/{idDocumento} -> download documento versione pdf
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/verbales/{id}/documento/{idDocumento}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			@PathVariable final Long idDocumento,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download documento versione pdf : {}", id);
	
			Verbale verbale = verbaleRepository.findOne(id);
			if (verbale == null) {
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
     * Service per la firma di un documento (odg base, odg suppletivo,  variazione)
  	 * GET verbales/{id}/firmadocumento
     * @param id
     * @param dto
     * @param idProfilo
     * @param response
     * @return
     * @throws CifraCatchedException
     */
  	/*
  	 *	FIRMA NON PREVISTA 
  	 *
  	@RequestMapping(value = "/verbales/{id}/firmadocumento", method = RequestMethod.POST, produces={ MediaType.APPLICATION_JSON_VALUE } )
  	@Timed
  	public @ResponseBody ResponseEntity<String> firmaDocumento(
  			@PathVariable final Long id,
  			@RequestParam final Long idProfilo,
  			@Valid @RequestBody FirmaRemotaDTO dto,
  			final HttpServletResponse response) throws CifraCatchedException {
  		
  		log.debug("REST request to firmaverbale : {}", id);
		HttpStatus httpStatus = HttpStatus.OK;
		JsonObject json = new JsonObject();
		
  		try{
  			Long idDocumento = dto.getFilesId().get(0);
	  		log.debug("REST request to firmaverbale : {}", id);
	  		
	  		Verbale verbale = verbaleService.findOne(id);
			if (verbale == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			
			verbaleService.firmaDocumento(idProfilo, dto, idDocumento, verbale);
  			
  		} catch (FirmaRemotaException | NotFoundException e) {
			log.error(e.getMessage(), e);
 			json.addProperty("stato", e.getMessage());
 			httpStatus = HttpStatus.BAD_REQUEST;
  		}catch(Exception e){
  			log.error(e.getMessage(), e);
 			json.addProperty("stato", e.getMessage());
 			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
    	}
  		
  		return new ResponseEntity<String>(json.toString(), httpStatus);
  	}

  	private void terminaSeduta(SedutaGiunta seduta){
  		seduta.setStato(SedutaGiuntaConstants.statiSeduta.sedutaTerminata.toString());
  		sedutaGiuntaRepository.save(seduta);
  	}
  	*/  	
  	
  	/**
	 *
	 * 	 * POST /ordineGiornos/{id}/trasparenza/allegato/{schemaDatoId}-> upload documento trasparenza
	 * @param id
	 * @param schemaDatoId
	 * @param file
	 * @return
	 * @throws IOException
	 */
  	/*
  	 *	FIRMA NON PREVISTA 
  	 *
 	@RequestMapping(value = "/verbales/{id}/firmato/allegato", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
 	@Timed
 	public @ResponseBody ResponseEntity<String> uploadfirmato(
 			@PathVariable final Long id,
 			@RequestParam("idProfilo") final Long idProfilo,
 			@RequestParam("file") final MultipartFile  file) throws CifraCatchedException {
 		
 		log.debug("REST request to upload verbale firmato : {}", id);
		HttpStatus httpStatus = HttpStatus.OK;
		JsonObject json = new JsonObject();
	  		
 		try{
 			Utente utente = utenteService.findByUsername(SecurityUtils.getCurrentLogin());

			Verbale verbale = verbaleService.findOne(id);
			if (verbale == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			
 			verbaleService.uploadFirmato(idProfilo, file, utente, verbale);
 			
 		}catch (Exception exp){
 			log.error(exp.getMessage(), exp);
 			json.addProperty("stato", exp.getMessage());
 			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
 		}
 		
 		return new ResponseEntity<String>(json.toString(), httpStatus);
 	}
	*/
  	
  	/**
     * GET  /verbales/{id}/nextsottoscrittore -> restituisce l'idProfilo del prossimo sottoscrittore.
     */
 	/*
  	 *	NON PREVISTO IN ATTICO 
  	 *
    @RequestMapping(value = "/verbales/{id}/nextsottoscrittore",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> getNextSottoscrittore(
    		@PathVariable Long id, HttpServletResponse response) throws CifraCatchedException {
    	
    	JsonObject json = new JsonObject();
    	try{
	    	log.debug("REST request to getNextSottoscrittore : {}", id);
	        Verbale verbale = verbaleRepository.findOne(id);
	        if (verbale == null) {
	            return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
	        }
	        
	        SottoscrittoreSedutaGiunta next = sottoscrittoreService.getNextFirmatarioVerbale(id);
	        
	        if (next != null && next.getProfilo() != null){
	        	json.addProperty("value", next.getProfilo().getId());
	        	return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	        }
	        else{
	        	log.debug(String.format("getNextSottoscrittore - nessun next firmatario presente per il verbale id:%s", id));
	        	return new ResponseEntity<String>("", HttpStatus.NOT_FOUND);
	        }
    	}catch(Exception e){
    		throw new CifraCatchedException(e);
    	}
    }
    */
}
