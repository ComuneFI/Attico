package it.linksmt.assatti.gestatti.web.rest;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.Authority;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.MonitoraggioAccesso;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoDocumentoEnum;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.dto.UserDTO;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.datalayer.repository.UserRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.DmsService;
//TODO integrazione
//import it.linksmt.assatti.service.MailService;
import it.linksmt.assatti.service.MonitoraggioAccessoService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.TipoDocumentoService;
import it.linksmt.assatti.service.UserService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.ServiceUtil;
import it.linksmt.assatti.utility.FileChecksum;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private final Logger log = LoggerFactory.getLogger(AccountResource.class);
    
    @Inject
	private FileRepository fileRepository;
    
    @Inject
    private UserRepository userRepository;

    @Inject
    private UtenteService utenteService;

    @Inject
    private ProfiloService profiloService;
    
    @Inject
    private UserService userService;
    
	@Autowired
	private ServiceUtil serviceUtil;

	@Autowired
	private DmsService dmsService;
	
	@Autowired
	private TipoDocumentoService tipoDocumentoService;

    //TODO integrazione @Inject
    //private MailService mailService;
    
    @Inject
    private MonitoraggioAccessoService monitoraggioAccessoService;

    @RequestMapping(value = "/uploadmoduloregistrazione",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    		)
    @Timed
    public ResponseEntity<String> uploadModuloRegistrazione(@RequestParam("file") MultipartFile  file, HttpServletRequest request) throws GestattiCatchedException {
    	try{
	    	log.info("Ricevuto file : " + file.getOriginalFilename());
	    	
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
			} catch (Exception e) {
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
     * POST  /register -> register the user.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/register",
            method = RequestMethod.POST,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> registerAccount(@Valid @RequestBody UserDTO userDTO, HttpServletRequest request) throws GestattiCatchedException {
    	try{
	        User user = userRepository.findOneByLogin(userDTO.getLogin());
	        if (user != null) {
	        	if(userDTO.getFileid()!=null) fileRepository.deleteById(userDTO.getFileid());
	            return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("login already in use");
	        } else {
	            if (userRepository.findOneByEmail(userDTO.getEmail()) != null) {
	            	if(userDTO.getFileid()!=null) fileRepository.deleteById(userDTO.getFileid());
	                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("e-mail address already in use");
	            }
	            if(userDTO.getUtente().getCodicefiscale()!=null && !utenteService.checkByCodicefiscale(userDTO.getUtente().getCodicefiscale()).equals("")){
	            	if(userDTO.getFileid()!=null) fileRepository.deleteById(userDTO.getFileid());
	                return ResponseEntity.badRequest().contentType(MediaType.TEXT_PLAIN).body("codice fiscale already in use");
	            }
	            user = userService.createUserInformation(userDTO.getLogin(), userDTO.getPassword(),
	            userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail().toLowerCase(),
	            userDTO.getLangKey());
	            
	            Utente utente = userDTO.getUtente();
	            log.debug("utente:"+utente);
	            if(userDTO.getFileid()!=null && userDTO.getFileid() > 0L) {
	            	utente.setModuloregistrazione(fileRepository.findOne(userDTO.getFileid()));
	            }
	            
	            if(utente != null ){
		            utente = utenteService.registrazione(userDTO);
	            }
	            
	            String baseUrl = request.getScheme() + // "http"
	            "://" +                            // "://"
	            request.getServerName() +          // "myhost"
	            ":" +                              // ":"
	            request.getServerPort();           // "80"
	
	//            mailService.sendActivationEmail(user, baseUrl);
	            return new ResponseEntity<>(HttpStatus.CREATED);
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /activate -> activate the registered user.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/activate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> activateAccount(@RequestParam(value = "key") String key) throws GestattiCatchedException {
    	try{
	    	User user = userService.activateRegistration(key);
	        if (user == null) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        return new ResponseEntity<String>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /authenticate -> check if the user is authenticated, and return its login.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/authenticate",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public String isAuthenticated(HttpServletRequest request) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to check if the current user is authenticated");
	        return request.getRemoteUser();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /account -> get the current user.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<UserDTO> getAccount() throws GestattiCatchedException {
    	try{
	    	User user = userService.getUserWithAuthorities();
	        if (user == null) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        Utente utente = utenteService.findByUsername(user.getLogin());
	        List<String> roles = new ArrayList<>();
	        for (Authority authority : user.getAuthorities()) {
	            roles.add(authority.getName());
	        }
	        
	        if(!roles.contains(AuthoritiesConstants.ADMIN) && !roles.contains(AuthoritiesConstants.AMMINISTRATORE_RP)){
	        	List<Profilo> profiliUtente = profiloService.findActiveByUsername(user.getLogin());
	        	if(profiliUtente == null || profiliUtente.size()==0){
	        		return new ResponseEntity<>(HttpStatus.LOCKED);
	        	}
	        }
	        
	        UserDTO userDTO = new UserDTO(
	                user.getLogin(),
	                null,
	                user.getFirstName(),
	                user.getLastName(),
	                user.getEmail(),
	                user.getLangKey(),
	                roles);
	        userDTO.setUtente(utente);
	        return new ResponseEntity<>(
	        	userDTO,
	            HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /account -> update the current user information.
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/account",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> saveAccount(@RequestBody UserDTO userDTO) throws GestattiCatchedException {
    	try{
	    	User userHavingThisLogin = userRepository.findOneByLogin(userDTO.getLogin());
	        if (userHavingThisLogin != null && !userHavingThisLogin.getLogin().equals(SecurityUtils.getCurrentLogin())) {
	            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
	        userService.updateUserInformation(userDTO.getFirstName(), userDTO.getLastName(), userDTO.getEmail());
	        return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * POST  /change_password -> changes the current user's password
     * @throws GestattiCatchedException 
     */
    @RequestMapping(value = "/account/change_password",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<?> changePassword(@RequestBody String password) throws GestattiCatchedException {
    	try{
	    	if (StringUtils.isEmpty(password) || password.length() < 5 || password.length() > 50) {
	            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	        }
	        userService.changePassword(password);
	        return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    @RequestMapping(value = "/account/reset_password/init",
        method = RequestMethod.POST,
        produces = MediaType.TEXT_PLAIN_VALUE)
    @Timed
    public ResponseEntity<?> requestPasswordReset(@RequestBody String mail, HttpServletRequest request) throws GestattiCatchedException {
    	try{
	        User user = userService.requestPasswordReset(mail);
	
	        if (user != null) {
//	          String baseUrl = request.getScheme() +
//	              "://" +
//	              request.getServerName() +
//	              ":" +
//	              request.getServerPort() +
//	              request.getContextPath();
	          
	          //TODO integrazione mailService.sendPasswordResetMail(user);
	          return new ResponseEntity<>("e-mail was sent", HttpStatus.OK);
	        }
	        else {
	          return new ResponseEntity<>("e-mail address not registered", HttpStatus.BAD_REQUEST);
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    @RequestMapping(value = "/account/reset_password/finish",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> finishPasswordReset(@RequestParam(value = "key") String key, @RequestParam(value = "newPassword") String newPassword) throws GestattiCatchedException {
    	try{
	    	User user = userService.completePasswordReset(newPassword, key);
	        if (user != null) {
	          return new ResponseEntity<String>(HttpStatus.OK);
	        } else {
	          return new ResponseEntity<String>(HttpStatus.INTERNAL_SERVER_ERROR);
	        }
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * Permette ad un eventuale filtro java di eseguire del codice custom in fase di logout.
     * Il metodo in se per se non fa nulla. Chiamato da auth.oauth2.service.js
     * 
     * @param request
     * @param response
     * @return
     * @throws GestattiCatchedException
     */
    @RequestMapping(value = "/account/logout/custom",
        method = RequestMethod.POST,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> logout(HttpServletRequest request, HttpServletResponse response) throws GestattiCatchedException {
    	try{
    		log.info("AccountResource :: logout");
    		return new ResponseEntity<>("", HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
	/**
	 * POST /account/logged -> verifica se l'account è già loggato.
	 * 
	 * NB: IL NOME DI QUESTO SERVIZIO NON HA SENSO! IN REALTA'IL METODO,
	 * FRA LE ALTRE COSE, EFFETTUA IL LOGIN
	 * 
	 * @param request
	 * @param response
	 * @throws GestattiCatchedException
	 */
	@RequestMapping(value = "/account/logged", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Boolean> isAccountLogged(
			@RequestParam(value = "username") String username,
			@RequestParam(value = "password") String password,
			@RequestParam(value = "logout") boolean logout,
			HttpServletRequest request, HttpServletResponse response) {
		HttpStatus status = null;
		MonitoraggioAccesso accesso = new MonitoraggioAccesso();
		accesso.setData(DateTime.now());
		String hostname = null;
		InetAddress inetAddress;
		Boolean loginConcurrent = Boolean.valueOf(WebApplicationProps.getProperty("login.concurrent"));
		try {
			inetAddress = InetAddress.getByName(request.getRemoteAddr());
			hostname = inetAddress.getHostName();
		} catch (UnknownHostException e1) {
		}
		accesso.setHostname(hostname != null ? hostname : request.getRemoteHost());
		accesso.setIpAddress(this.getClientIp(request));
		accesso.setUsername(username);
		
		boolean checkLogged = false;
		Utente utente = utenteService.findByUsername(username);
		if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
			if ((username == null || username.trim().isEmpty()) && (password == null || password.trim().isEmpty())) {
				accesso.setDettaglio("Username e password non inseriti");
				status = HttpStatus.UNAUTHORIZED;
			} else if (username == null || username.trim().isEmpty()) {
				accesso.setDettaglio("Username non inserita");
				status = HttpStatus.UNAUTHORIZED;
			} else {
				accesso.setDettaglio("Password non inserita");
				status = HttpStatus.UNAUTHORIZED;
			}
		} 
		else if (!userService.existUsername(username)) {
			accesso.setDettaglio("Lo username inserito non esiste nel sistema");
			status = HttpStatus.UNAUTHORIZED;
		}
		else if(utente.getValidita()!=null && utente.getValidita().getValidoal()!=null && !utente.getValidita().getValidoal().toDate().after(new Date())) {
			log.info("utente disabilitato");
			accesso.setDettaglio("L'utente non è abilitato all'uso del sistema ATTICO.");
			status = HttpStatus.UNAUTHORIZED;
		}
		else if (userService.isCredenzialiCorrette(username, password)) {
			try {
				checkLogged = userService.isAccountLogged(username, password, logout, request, response);
			}
			catch (Exception e) {
				return new ResponseEntity<>(false, HttpStatus.UNAUTHORIZED);
			}
			if (loginConcurrent || !checkLogged) {
				accesso.setDettaglio("Accesso effettuato con successo");
			} else {
				accesso.setDettaglio("Credenziali corrette ma utente gi\u00E0 loggato.");
			}
			status = HttpStatus.OK;
		}
		else {
			accesso.setDettaglio("Le credenziali inserite sono errate");
			status = HttpStatus.UNAUTHORIZED;
		}
		accesso.setStatus(status.getReasonPhrase());
		monitoraggioAccessoService.salvaNuovoAccesso(accesso);
		
		return new ResponseEntity<>(checkLogged, status);
	}
	
	private String getClientIp(HttpServletRequest request) {

        String remoteAddr = "";

        if (request != null) {
            remoteAddr = request.getHeader("X-FORWARDED-FOR");
            if (remoteAddr == null || "".equals(remoteAddr)) {
                remoteAddr = request.getRemoteAddr();
            }
        }

        return remoteAddr;
    }

}
