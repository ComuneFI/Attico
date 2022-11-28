package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.RichiestaHD;
import it.linksmt.assatti.datalayer.domain.RispostaHD;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.RichiestaHDService;
import it.linksmt.assatti.service.TipoDocumentoService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;

/**
 * REST controller for managing RichiestaHD.
 */
@RestController
@RequestMapping("/api")
public class RichiestaHDResource {

    private final Logger log = LoggerFactory.getLogger(RichiestaHDResource.class);

    @Inject
    private RichiestaHDService richiestaHDService; 
    
    @Inject
	private FileRepository fileRepository;
    
	@Autowired
	private ServiceUtil serviceUtil;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private TipoDocumentoService tipoDocumentoService;
    
    
    /**
  	 * GET /richiestaHDs/{id}/modulo -> download modulo richiesta
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/richiestaHDs/{id}/modulo", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download of moduloregistrazione with fileid : {}", id);
	
			RichiestaHD richiesta = richiestaHDService.findOne(id);
			if (richiesta == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			if(richiesta.getAllegatoId()==null){
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			File file = fileRepository.findOne(richiesta.getAllegatoId());
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
    
    @RequestMapping(value = "/richiestaHDs/uploadmodulo",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    		)
    @Timed
    public ResponseEntity<String> uploadModulo(@RequestParam("file") MultipartFile  file, HttpServletRequest request) throws GestattiCatchedException {
    	try{
	    	log.info("Ricevuto file : " + file.getOriginalFilename());
	    	
	    	TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.richiesta_help_desk.name());
			/*
			 * Salvo documento su repository documentale
			 */
			String cmisObjectId = null;
			try {
				String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, null);
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String prefixTimeStamp = df.format(new Date()) + "_";
				cmisObjectId = dmsService.save(attoFolderPath, file.getBytes(), prefixTimeStamp + file.getOriginalFilename(), file.getContentType(), null);
			}
			catch (Exception e) {
				throw new GestattiCatchedException(e, e.getMessage());
			}
	    	
	    	File fileEntity = new File(file.getOriginalFilename(),
					file.getContentType(), file.getSize());
	    	fileEntity.setCmisObjectId(cmisObjectId);
	    	fileEntity.setSha256Checksum(FileChecksum.calcolaImpronta(file.getBytes()));
	    	File fileSaved = fileRepository.save(fileEntity);
	    	Long id = fileSaved.getId();
	    	log.info("salvato il file, con id : " +  id);
	    	JsonObject json = new JsonObject();
			json.addProperty("fileid", id);
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.CREATED);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /richiestaHDs -> Create a new richiestaHD.
     */
    @RequestMapping(value = "/richiestaHDs",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@Valid @RequestBody RichiestaHD richiestaHD) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to save RichiestaHD : {}", richiestaHD);
	        if (richiestaHD.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new richiestaHD cannot already have an ID").build();
	        }
	        
	        richiestaHD = richiestaHDService.create(richiestaHD);
	        
	        return ResponseEntity.created(new URI("/api/richiestaHDs/" + richiestaHD.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs -> rispostaOperatore
     */
    @RequestMapping(value = "/richiestaHDs/rispostaOperatore",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> rispostaOperatore(@RequestBody RispostaHD risposta,
    		@RequestParam(value="statoRichiesta", required=false) String statoRichiesta) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to rispostaOperatore : {}", risposta);
	    	if (risposta == null || risposta.getRichiesta() == null || risposta.getRichiesta().getId() == null) {
	            return ResponseEntity.badRequest().build();
	        }
	        richiestaHDService.addRispostaOperatore(risposta, statoRichiesta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs -> rispostaUtente
     */
    @RequestMapping(value = "/richiestaHDs/rispostaUtente",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> rispostaUtente(@RequestBody RispostaHD risposta) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to rispostaUtente : {}", risposta);
	        if (risposta == null || risposta.getRichiesta() == null || risposta.getRichiesta().getId() == null) {
	            return ResponseEntity.badRequest().build();
	        }
	        richiestaHDService.addRispostaUtente(risposta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs -> editTestoRichiesta
     */
    @RequestMapping(value = "/richiestaHDs/editTestoRichiesta",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> editTestoRichiesta(@RequestBody RichiestaHD richiesta) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to editTestoRichiesta : {}", richiesta);
	        if (richiesta == null || richiesta.getId() == null || richiesta.getTestoRichiesta() == null || richiesta.getTestoRichiesta().trim().isEmpty()) {
	            return ResponseEntity.badRequest().build();
	        }
	        richiestaHDService.updateTestoRichiesta(richiesta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs -> editTestoRisposta
     */
    @RequestMapping(value = "/richiestaHDs/editTestoRisposta",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> editTestoRisposta(@RequestBody RispostaHD risposta) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to editTestoRisposta : {}", risposta);
	        if (risposta == null || risposta.getId() == null || risposta.getTestoRisposta() == null || risposta.getTestoRisposta().trim().isEmpty()) {
	            return ResponseEntity.badRequest().build();
	        }
	        richiestaHDService.updateTestoRisposta(risposta);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /richiestaHD/updateStato
     */
    @RequestMapping(value = "/richiestaHDs/{id}/updateStato",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured(AuthoritiesConstants.USER)
    public ResponseEntity<Void> update(
    		@PathVariable Long id,
    		@RequestParam(value="statoId", required=true) String statoId) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update stato RichiestaHD : {}", id);
	        if (statoId == null) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	        richiestaHDService.updateStatoRichiesta(id, Long.parseLong(statoId));
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /richiestaHD/presaVisione
     */
    @RequestMapping(value = "/richiestaHDs/{id}/presaVisione",
        method = RequestMethod.GET,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
    public ResponseEntity<Void> presaVisione(
    		@PathVariable Long id) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to presaVisione RichiestaHD : {}", id);
	        if (id == null || id<=0L) {
	            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
	        }
	        richiestaHDService.presaVisione(id);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /richiestaHDs/:id -> get the "id" richiestaHD.
     */
    @RequestMapping(value = "/richiestaHDs/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<RichiestaHD> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get RichiestaHD : {}", id);
	        RichiestaHD richiestaHD = richiestaHDService.findOne(id);
	        if (richiestaHD == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(richiestaHD, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /richiestaHDs/search -> get all the richiestaHDs search by criteria used by administratorIP to maintain hd request.
     */
    @RequestMapping(value = "/richiestaHDs/searchOperatore",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RichiestaHD>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                  
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Page<RichiestaHD> page = richiestaHDService.findAllNotDirigente(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/richiestaHDs", offset, limit);
	        return new ResponseEntity<List<RichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/richiestaHDs/uploadallegato",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    		)
    @Timed
    public ResponseEntity<String> uploadallegato(@RequestParam("file") MultipartFile  file, HttpServletRequest request) throws GestattiCatchedException {
    	try{
	    	log.info("Ricevuto file : " + file.getOriginalFilename());
	    	
	    	TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.richiesta_help_desk_allegato.name());
			/*
			 * Salvo documento su repository documentale
			 */
			String cmisObjectId = null;
			try {
				String attoFolderPath = serviceUtil.buildDocumentPath(tipoDocumento, null, null, null);
				DateFormat df = new SimpleDateFormat("yyyyMMddHHmmssSSS");
				String prefixTimeStamp = df.format(new Date()) + "_";
				cmisObjectId = dmsService.save(attoFolderPath, file.getBytes(), prefixTimeStamp + file.getOriginalFilename(), file.getContentType(), null);
			} catch (Exception e) {
				throw new GestattiCatchedException(e, e.getMessage());
			}
			
	    	File fileEntity = new File(file.getOriginalFilename(),
					file.getContentType(), file.getSize());
	    	File fileSaved = fileRepository.save(fileEntity);
	    	fileSaved.setCmisObjectId(cmisObjectId);
	    	fileSaved.setSha256Checksum(FileChecksum.calcolaImpronta(file.getBytes()));
	    	Long id = fileSaved.getId();
	    	log.info("salvato il file, con id : " +  id);
	    	JsonObject json = new JsonObject();
			json.addProperty("fileid", id);
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.CREATED);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs/search -> get all the richiestaHDs search by criteria used by administratorRP to maintain hd request.
     */
    @RequestMapping(value = "/richiestaHDs/searchRichiesteDirigente",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RichiestaHD>> searchDirigente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                  
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	    	Page<RichiestaHD> page = richiestaHDService.findAllDirigente(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/richiestaHDs", offset, limit);
	        return new ResponseEntity<List<RichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs/searchDirigente -> get all the richiestaHDs search by criteria used by dirigenti
     */
    @RequestMapping(value = "/richiestaHDs/searchDirigente",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RichiestaHD>> searchCurrentDirigente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                  
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		if(search==null){
	  			search = new JsonObject();
			}
	  		search.addProperty("dirigente", true);
	    	Page<RichiestaHD> page = richiestaHDService.findAllOfCurrentUser(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/richiestaHDs", offset, limit);
	        return new ResponseEntity<List<RichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /richiestaHDs/search -> get all the richiestaHDs search by criteria used by users
     */
    @RequestMapping(value = "/richiestaHDs/searchUtente",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<RichiestaHD>> searchUtente(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr )
                                  
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		if(search==null){
	  			search = new JsonObject();
			}
	  		search.addProperty("dirigente", false);
	    	Page<RichiestaHD> page = richiestaHDService.findAllOfCurrentUser(PaginationUtil.generatePageRequest(offset, limit), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/richiestaHDs", offset, limit);
	        return new ResponseEntity<List<RichiestaHD>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
