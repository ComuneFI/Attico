package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.springframework.data.domain.Sort;
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

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.UtenteStato;
import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.TipoDocumentoService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.dto.UtenteSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;

/**
 * REST controller for managing Utente.
 */
@RestController
@RequestMapping("/api")
public class UtenteResource {

	private final Logger log = LoggerFactory.getLogger(UtenteResource.class);

	@Inject
	private UtenteService utenteService;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private UserService userService;
	
	@Inject
	private FileRepository fileRepository;
	
	@Autowired
	private ServiceUtil serviceUtil;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private TipoDocumentoService tipoDocumentoService;


	/**
  	 * GET/utente/{id}/moduloregistrazione-> download modulo di registrazione caricato dall'utente
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/utentes/{id}/moduloregistrazione", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to get download of moduloregistrazione with fileid : {}", id);
	
			Utente utente = utenteService.findOne(id);
			if (utente == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
	
			File file = utente.getModuloregistrazione();
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
  	 * POST/utentes/{id}/updatemoduloregistrazione-> sostituisce il modulo di registrazione caricato dall'utente
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/utentes/{id}/updatemoduloregistrazione",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    		)
    @Timed
    public ResponseEntity<String> updateModuloRegistrazione(
    		@PathVariable final Long id,
    		@RequestParam("file") MultipartFile  file, 
    		HttpServletRequest request) throws GestattiCatchedException {
    	try{
	    	log.info("Ricevuto file : " + file.getOriginalFilename());
	    	
	    	Utente utente = utenteService.findOne(id);
	    	File fileEntity = utente.getModuloregistrazione();
			
	    	// TODO: salvare solo se lo stato è REGISTRATO ?
	    	
	    	TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(TipoDocumentoEnum.modulo_registrazione.name());
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
			
	    	fileEntity.setNomeFile(file.getOriginalFilename());
	    	fileEntity.setContentType(file.getContentType());
	    	fileEntity.setSize(file.getSize());
	    	fileEntity.setSha256Checksum(FileChecksum.calcolaImpronta(file.getBytes()));
	    	fileEntity.setCmisObjectId(cmisObjectId);
	    	
	    	fileRepository.save(fileEntity);
	    	
	    	log.info("salvato il file, con id : " +  fileEntity.getId());
	    	JsonObject json = new JsonObject();
			json.addProperty("fileid", id);
	    	return new ResponseEntity<String>(json.toString(), HttpStatus.CREATED);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

	/**
	 * POST /utentes -> Create a new utente.
	 */
	@RequestMapping(value = "/utentes", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody final Utente utente)
			throws GestattiCatchedException {
		try{
			log.debug("REST request to save Utente : {}", utente);
			if (utente.getId() != null) {
				return ResponseEntity
						.badRequest()
						.header("Failure", "A new utente cannot already have an ID")
						.build();
			}
			utenteService.save(utente);
			return ResponseEntity
					.created(new URI("/api/utentes/" + utente.getId())).build();
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * POST /utentes -> Create a new utente.
	 */
	@RequestMapping(value = "/utentes/activeAmministratoreIP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> activeAmministratoreIP(
			@Valid @RequestBody final Utente utente) throws GestattiCatchedException {
		try{
			log.debug("REST request to activeAmministratoreIP Utente : {}", utente);
			utenteService.activeAmministratoreIP(utente);
			return ResponseEntity
					.created(new URI("/api/utentes/" + utente.getId())).build();
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * POST /utentes -> Create a new utente.
	 */
	@RequestMapping(value = "/utentes/activeAmministratoreRP", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<Void> activeAmministratoreRP(
			@Valid @RequestBody final Utente utente) throws GestattiCatchedException {
		try{
			log.debug("REST request to activeAmministratoreRP Utente : {}", utente);
			utenteService.activeAmministratoreRP(utente);
			return ResponseEntity
					.created(new URI("/api/utentes/" + utente.getId())).build();
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * POST /utentes -> Create a new utente.
	 */
	@RequestMapping(value = "/utentes/attiva", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	@Secured({ AuthoritiesConstants.ADMIN,
			AuthoritiesConstants.AMMINISTRATORE_RP })
	public ResponseEntity<Void> attiva(@Valid @RequestBody final Utente utente)
			throws GestattiCatchedException {
		try{
			log.debug("REST request to attiva Utente : {}", utente);
			utenteService.attiva(utente);
			return ResponseEntity
					.created(new URI("/api/utentes/" + utente.getId())).build();
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * PUT /utentes -> Updates an existing utente.
	 */
	@RequestMapping(value = "/utentes", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> update(@Valid @RequestBody final Utente utente)
			throws GestattiCatchedException{
		try{
			log.debug("REST request to update Utente : {}", utente);
			if (utente.getId() == null) {
				return create(utente);
			}
			if(utente.getUser()!=null && utente.getUser().getPassword()!=null && !utente.getUser().getPassword().isEmpty()){
				userService.changePassword(utente.getUsername(), utente.getUser().getPassword());
			}
			utenteService.save(utente);
			return ResponseEntity.ok().build();
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	private UtenteSearchDTO buildUtenteSearchDTO(final String id, final String cognome, final String nome, final String username, final String stato, final String aoo) throws GestattiCatchedException{
		try{
			UtenteSearchDTO search = new UtenteSearchDTO();
	    	search.setId(id);
	    	search.setAoo(aoo);
	    	search.setCognome(cognome);
	    	search.setNome(nome);
	    	search.setStato(stato);
	    	search.setUsername(username);
	    	return search;
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
	
	/**
	 * GET /utentes/getAllActive -> return only active users
	 */
	@RequestMapping(value = "/utentes/getAllActive", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Utente>> getAllActive()
			throws GestattiCatchedException {
		try{
			UtenteSearchDTO search = this.buildUtenteSearchDTO(null, null, null, null, UtenteStato.ATTIVO.toString(), null);
			Integer offset = null;
			Integer limit = null;
			Page<Utente> page = utenteService.findAll(search, PaginationUtil
					.generatePageRequest(offset, limit, new Sort(Sort.Direction.ASC, "username")));
	
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/utentes/getAllActive", offset, limit);
			return new ResponseEntity<List<Utente>>(page.getContent(), headers,
					HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utentes/getAllBasic
	 */
	@RequestMapping(value = "/utentes/getAllBasic", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<UtenteDto>> getAllBasic()
			throws GestattiCatchedException {
		try{
			List<UtenteDto> list = utenteService.findAllUtentiBasic(false);
			return new ResponseEntity<List<UtenteDto>>(list, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utentes/getAllDirigenti -> return only active dirigenti
	 */
	@RequestMapping(value = "/utentes/getAllDirigenti", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Utente>> getAllDirigenti()
			throws GestattiCatchedException {
		try{
			List<Utente> dirigenti = utenteService.findDirigentiAttivi();
			return new ResponseEntity<List<Utente>>(dirigenti, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utentes/:id/getAoosDirigente -> return aooIds di cui risulta dirigente
	 */
	@RequestMapping(value = "/utentes/{id}/getAoosDirigente", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Aoo>> getAoosDirigente(@PathVariable final Long id)
			throws GestattiCatchedException {
		try{
			List<Aoo> aoos = aooService.findAoosDirigente(id);
			return new ResponseEntity<List<Aoo>>(aoos, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utentes/utentiLoggati -> return only logged users
	 */
	@RequestMapping(value = "/utentes/utentiLoggati", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.AMMINISTRATORE_RP })
	@Timed
	public ResponseEntity<List<Utente>> utentiLoggati()
			throws GestattiCatchedException {
		try{
			List<Utente> utentiLoggati = utenteService.findUtentiLoggati();
			return new ResponseEntity<List<Utente>>(utentiLoggati, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utentes -> get all the utentes.
	 */
	@RequestMapping(value = "/utentes", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Utente>> getAll(
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit,
			@RequestParam(value = "idUtente" , required = false) final String id,
			@RequestParam(value = "cognome" , required = false) final String cognome,
			@RequestParam(value = "nome" , required = false) final String nome,
			@RequestParam(value = "username" , required = false) final String username,
			@RequestParam(value = "stato" , required = false) final String stato,
			@RequestParam(value = "aoo" , required = false) final String aoo)
					throws GestattiCatchedException {
		try{
			UtenteSearchDTO search = this.buildUtenteSearchDTO(id, cognome, nome, username, stato, aoo);
			Page<Utente> page = utenteService.findAll(search, PaginationUtil
					.generatePageRequest(offset, limit, new Sort(Sort.Direction.ASC, "username")));
	
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/api/utentes", offset, limit);
			return new ResponseEntity<List<Utente>>(page.getContent(), headers,
					HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utentes -> get all the utentestato values.
	 */
	@RequestMapping(value = "/utentes/statos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getUtenteStatoValues()
			throws GestattiCatchedException {
		try{
			List<String> statiStr = new ArrayList<String>();
			for(UtenteStato stato : UtenteStato.values()){
				if(!stato.toString().equalsIgnoreCase(UtenteStato.CANCELLATO.toString())) {
					statiStr.add(stato.toString());
				}
			}
			return new ResponseEntity<List<String>>(statiStr,
					HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utentes/:id -> get the "id" utente.
	 */
	@RequestMapping(value = "/utentes/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Utente> get(@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException {
		try{
			log.debug("REST request to get Utente : {}", id);
			Utente utente = utenteService.findOne(id);
			if (utente == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(utente, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utentes/:id -> get the profile of "id" utente and aoo
	 */
	@RequestMapping(value = "/utentes/{id}/profilos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Iterable<Profilo>> findProfiliAoo(
			@PathVariable final Long id,
			@RequestParam(value = "aooId", required = false) final Long aooId,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to get Utente : {}", id);
			Iterable<Profilo> profili = utenteService.findProfiliAoo(id, aooId);
			return new ResponseEntity<>(profili, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utente/:cf -> get the stato of utente by "codicefiscale"
	 */

	@RequestMapping(value = "/utentes/checkstato/{codicefiscale}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> checkCodiceFiscale(
			@PathVariable final String codicefiscale,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to check CodiceFiscale : {}", codicefiscale);
			String stato = utenteService.checkByCodicefiscale(codicefiscale);
			JsonObject json = new JsonObject();
			json.addProperty("stato", stato);
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * PUT /utente/:id/disable -> disable utente with id
	 */
	@RequestMapping(value = "/utentes/{id}/disable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	@Secured(AuthoritiesConstants.ADMIN)
	public ResponseEntity<String> disableUtente(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to disable user : {}", id);
			JsonObject json = new JsonObject();
			try{
				utenteService.disableUtente(Long.parseLong(id));
				json.addProperty("stato", UtenteStato.DISABILITATO.toString());
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * PUT /utente/:id/enable -> enable utente with id
	 */
	@RequestMapping(value = "/utentes/{id}/enable", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> enableUtente(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to enable user : {}", id);
			JsonObject json = new JsonObject();
			try{
				Utente utente = utenteService.findOne(Long.parseLong(id));
				utenteService.attiva(utente);
				json.addProperty("stato", UtenteStato.ATTIVO.toString());
			}catch(Exception e){
				json.addProperty("stato", "errore");
				json.addProperty("dettaglioErrore", e.getMessage());
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * PUT /utente/:id/lastprofile/{profiloId} -> update last used profile id
	 */
	@RequestMapping(value = "/utentes/{id}/lastprofile/{profiloId}", method = RequestMethod.PUT, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Void> updatelastprofile(
			@PathVariable final String id,
			@PathVariable final Long profiloId,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to set last used profile for user : {}", id);
			utenteService.lastProfile(Long.parseLong(id), profiloId);
			return new ResponseEntity<Void>(HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utente/:id/lastprofile -> check last used profile id
	 */
	@RequestMapping(value = "/utentes/{id}/lastprofile", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> lastprofile(
			@PathVariable final String id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST to check last used profile for user : {}", id);
			JsonObject json = new JsonObject();
			try{
				Utente utente = utenteService.findOne(Long.parseLong(id));
				Long profiloId = null;
				if(utente.getLastProfileId()!=null && !utente.getLastProfileId().equals(0L)){
					Profilo p = profiloService.findOne(utente.getLastProfileId());
					if(p!=null && p.getValidita()!=null && (p.getValidita().getValidoal()==null || p.getValidita().getValidoal().toDate().after(new Date()))){
						profiloId = p.getId();
					}
				}
				json.addProperty("id", profiloId!=null ? profiloId + "" : "0");
			}catch(Exception e){
				json.addProperty("id", "0");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * GET /utentes/:id -> get the profile of "login" utente and aoo
	 */
	@RequestMapping(value = "/utentes/{username}/activeprofilos", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Iterable<Profilo>> activeprofilos(
			@PathVariable final String username, final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to get activeprofilos Utente : {}", username);
			Iterable<Profilo> profili = utenteService.activeprofilos(username);
			return new ResponseEntity<>(profili, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /utentes/:id -> get the utente from username
	 */
	@RequestMapping(value = "/utentes/{username}/getbyusername", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Utente> getbyusername(
			@PathVariable final String username, final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to get Utente by username : {}", username);
			Utente utente = utenteService.findByUsername(username);
			if (utente == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
			return new ResponseEntity<>(utente, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}

	/**
	 * DELETE /utentes/:id -> delete the "id" utente.
	 */
	@RequestMapping(value = "/utentes/{id}", method = RequestMethod.DELETE, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void delete(@PathVariable final Long id) throws GestattiCatchedException{
		try{
			log.debug("REST request to delete Utente : {}", id);
			utenteService.delete(id);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
}
