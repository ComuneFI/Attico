package it.linksmt.assatti.service;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Authority;
import it.linksmt.assatti.datalayer.domain.GruppoRuolo;
import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QAoo;
import it.linksmt.assatti.datalayer.domain.QGruppoRuolo;
import it.linksmt.assatti.datalayer.domain.QProfilo;
import it.linksmt.assatti.datalayer.domain.QRuolo;
import it.linksmt.assatti.datalayer.domain.QUtente;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.User;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.domain.UtenteStato;
import it.linksmt.assatti.datalayer.domain.Validita;
import it.linksmt.assatti.datalayer.domain.dto.UserDTO;
import it.linksmt.assatti.datalayer.domain.dto.UtenteDto;
import it.linksmt.assatti.datalayer.repository.GruppoRuoloRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.datalayer.repository.UtenteBasicRepository;
import it.linksmt.assatti.datalayer.repository.UtenteRepository;
import it.linksmt.assatti.security.AuthoritiesConstants;
import it.linksmt.assatti.service.dto.UtenteSearchDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;


/**
 * Service class for managing users.
 */
@Service
@Transactional
public class UtenteService {

	private static final String IT = "it";

	private final Logger log = LoggerFactory.getLogger(UtenteService.class);

	@Inject
	private UtenteRepository utenteRepository;
	
	@Inject
	private UtenteBasicRepository utenteBasicRepository;

	@Inject
	private ProfiloRepository profiloRepository;

	@Inject
	private UserService userService;
	
	@Inject
	private IndirizzoService indirizzoService;

	@Inject
	private ValiditaService validitaService;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private SessionRegistry sessionRegistry;
	
	@Inject
	private RuoloRepository ruoloRepository;
	
	@Inject
	private GruppoRuoloRepository gruppoRuoloRepository;
	
	@Transactional(readOnly = true)
	public String getNameByUser(String username) {
		//return utenteRepository.getNameByUser(username);
		String name = "";
		UtenteDto dto = getUtenteBasicByUsername(username);
		if(dto!=null) {
			name = dto.getName();
		}
		return name;
	}
	
	@Transactional(readOnly = true)
	public List<UtenteDto> findAllUtentiBasic(boolean includiNonAttivi){
		if(includiNonAttivi) {
			return utenteBasicRepository.getUtentiBasic();
		}else {
			return utenteBasicRepository.getUtentiBasicAttivi();
		}
	}
	
	@Transactional(readOnly = true)
	public UtenteDto getUtenteBasicByUsername(String username){
		if(username!=null && !username.trim().isEmpty()) {
			return utenteBasicRepository.getUtenteBasicByUsername(username);
		}else{
			return null;
		}
	}
	
	@Transactional(readOnly = true)
	public Set<Utente> findUtentiByCodiceRuolo(String codiceRuolo){
		Set<Utente> utenti = new HashSet<Utente>();
		BooleanExpression pR = QRuolo.ruolo.codice.equalsIgnoreCase(codiceRuolo).and(QRuolo.ruolo.enabled.eq(true));
		Iterable<Ruolo> ruoli = ruoloRepository.findAll(pR);
		BooleanExpression pG = null;
		if(ruoli!=null && ruoli.iterator().hasNext()){
			for(Ruolo ruolo : ruoli){
				if(pG==null){
					pG = QGruppoRuolo.gruppoRuolo.hasRuoli.contains(ruolo);
				}else{
					pG = pG.or(QGruppoRuolo.gruppoRuolo.hasRuoli.contains(ruolo));
				}
			}
			Iterable<GruppoRuolo> grs = gruppoRuoloRepository.findAll(pG);
			if(grs!=null && grs.iterator().hasNext()){
				BooleanExpression pP = QProfilo.profilo.grupporuolo.in(Lists.newArrayList(grs)).and(QProfilo.profilo.validita.validoal.isNull());
				Iterable<Profilo> profili = profiloRepository.findAll(pP);
				if(profili!=null && profili.iterator().hasNext()){
					for(Profilo profilo : profili){
						utenti.add(profilo.getUtente());
					}
				}
			}
		}				
		return utenti;
	}
	
	@Transactional(readOnly = true)
	public Set<Utente> findLoggatiByAoosId(List<Long> aooIds){
		HashSet<Utente> utenti = new HashSet<Utente>();
		for(Profilo profilo : profiloService.findActiveByAooIds(aooIds)){
			utenti.add(profilo.getUtente());
		}
		utenti.retainAll(findUtentiLoggati());
		return utenti;
	}
	
	@Transactional(readOnly = true)
	public List<Utente> findUtentiLoggati(){
		Set<String> loggedUsernames = new HashSet<String>();
		for(Object principal : sessionRegistry.getAllPrincipals()) {
    		List<SessionInformation> sessions = sessionRegistry.getAllSessions(principal, false);
    		if(sessions.size()>0) {        		
    			String username = ((org.springframework.security.core.userdetails.User)principal).getUsername();
    			loggedUsernames.add(username);
    		}        		
    	}   
		BooleanExpression predicate = null;
		if(loggedUsernames.size() > 0){
			predicate = QUtente.utente.username.in(loggedUsernames);
		}else{
			predicate = QUtente.utente.id.isNull();
		}
		return Lists.newArrayList(utenteRepository.findAll(predicate));
	}
	
	@Transactional(readOnly = true)
	public Iterable<Profilo> findProfiliAoo(Long id, Long aooId) {
		BooleanExpression predicateProfilo = QProfilo.profilo.utente.id.eq(id);
		if (aooId != null) {
			predicateProfilo = predicateProfilo.and(QProfilo.profilo.aoo.id
					.eq(aooId));
		}
		return profiloRepository.findAll(predicateProfilo);
	}
	
	@Transactional(readOnly=true)
	public String checkByCodicefiscale(String codicefiscale){
		log.debug("checkByCodicefiscale codicescale" + codicefiscale);
		Utente utente = utenteRepository.findByCodicefiscale(codicefiscale);
		String stato = "";
		if(utente!=null && utente.getStato()!=null){
			if(utente.getStato().toString().equals(UtenteStato.ATTIVO.toString())){
				User user = userService.findOneByLogin(utente.getUsername());
				List<String> roles = new ArrayList<>();
		        for (Authority authority : user.getAuthorities()) {
		            roles.add(authority.getName());
		        }
		        if(!roles.contains(AuthoritiesConstants.ADMIN) && !roles.contains(AuthoritiesConstants.AMMINISTRATORE_RP)){
		        	List<Profilo> profiliUtente = profiloService.findActiveByUsername(user.getLogin());
		        	if(profiliUtente == null || profiliUtente.size()==0){
		        		stato = "NESSUN_PROFILO";
		        	}else{
		        		stato = utente.getStato().toString();
		        	}
		        }else{
		        	stato = utente.getStato().toString();
		        }
			}else{
				stato = utente.getStato().toString();
			}
		}
		return stato;
	}
	
	@Transactional(readOnly=false)
	public void disableUtente(Long id){
		log.debug("disableUtente idutente" + id);
		Utente utente = utenteRepository.findOne(id);
		if(utente!=null){
			if(utente.getValidita()==null){
				utente.setValidita(new Validita());
			}
			Calendar cal = Calendar.getInstance();
//			cal.add(Calendar.DATE, -1);
			utente.getValidita().setValidoal(new LocalDate(cal.getTimeInMillis()));
			utente.setStato(UtenteStato.DISABILITATO);
			utenteRepository.save(utente);
			
			User user = userService.findOneByLogin(utente.getUsername());
			userService.disableRegistrationInternal(user.getLogin());
		}
	}

	@Transactional(readOnly = true)
	public Utente findOne(Long id) {
		log.debug("findOne id" + id);
		Utente utente = utenteRepository.findOne(id);
		if(utente != null ){
			User user = userService.findOneByLogin(utente.getUsername());
			if(user != null ){
				List<String> roles = new ArrayList<>();
				for (Authority authority : user.getAuthorities()) {
					roles.add(authority.getName());
				}
		
				UserDTO userDTO = new UserDTO(user.getLogin(), null,
						user.getFirstName(), user.getLastName(), user.getEmail(),
						user.getLangKey(), roles);
		
				utente.setUser(userDTO);
			}
		}
		return utente;
	}
	
	@Transactional(readOnly = true)
	public Page<Utente> findAll(Pageable pageInfo){
		return utenteRepository.findAll(pageInfo);
	}
	
	@Transactional(readOnly = true)
	public List<Utente> findDirigentiAttivi(){
		return utenteRepository.findDirigentiAttivi();
	}
	
	@Transactional(readOnly = true)
	public Page<Utente> findAll(UtenteSearchDTO search, Pageable pageInfo){
		
		BooleanExpression predicateUtente = QUtente.utente.isNotNull();
		
		Long idLong = null;
		if(search.getId()!=null && !"".equals(search.getId().trim())){
			try{
				idLong = Long.parseLong(search.getId().trim());
			}catch(Exception e){};
		}
		
		if(idLong!=null){
			predicateUtente = predicateUtente.and(QUtente.utente.id.eq(idLong));
		}
		if(search.getAoo()!=null && !"".equals(search.getAoo().trim())){
			BooleanExpression predicateAoo = ((QAoo.aoo.codice.concat(" - ").concat(QAoo.aoo.descrizione)).containsIgnoreCase(search.getAoo())).or(
							(QAoo.aoo.codice.concat("-").concat(QAoo.aoo.descrizione)).containsIgnoreCase(search.getAoo()));
			Iterable<Aoo> aoos = aooService.findByPredicate(predicateAoo);
			List<Aoo> aoosList = new ArrayList<Aoo>();
			for(Aoo aoo : aoos){
				aoosList.add(aoo);
			}
			Iterable<Profilo> profilos = profiloService.findByAoos(aoosList);
			List<Long> ids = new ArrayList<Long>(); 
			for(Profilo profilo : profilos){
				ids.add(profilo.getUtente().getId());
			}			
			predicateUtente = predicateUtente.and(QUtente.utente.id.in(ids));
		}
		if(search.getStato()!=null && !"".equals(search.getStato().trim())){
			predicateUtente = predicateUtente.and(QUtente.utente.stato.in(UtenteStato.searchInStato(search.getStato())));
		}
		if(search.getUsername()!=null && !"".equals(search.getUsername().trim())){
			predicateUtente = predicateUtente.and(QUtente.utente.username.containsIgnoreCase(search.getUsername()));
		}
		if(search.getNome()!=null && !"".equals(search.getNome().trim())){
			predicateUtente = predicateUtente.and(QUtente.utente.nome.containsIgnoreCase(search.getNome()));
		}
		if(search.getCognome()!=null && !"".equals(search.getCognome().trim())){
			predicateUtente = predicateUtente.and(QUtente.utente.cognome.containsIgnoreCase(search.getCognome()));
		}
		
		Page<Utente> page = utenteRepository.findAll(predicateUtente, pageInfo);
		
		for(Utente utente : page){
			utente.setAoos(utenteRepository.getAooDescriptionsByUser(utente.getId()));
		}
		
		return page;
	}
	
	@Transactional
	public void lastProfile(Long idUtente, Long idProfilo) {
		Utente utente = utenteRepository.findOne(idUtente);
		utente.setLastProfileId(idProfilo);
		utenteRepository.save(utente);
	}
	
	@Transactional
	public void save(Utente utente) throws GestattiCatchedException{
		if(utente.getValidita()==null || utente.getValidita().getValidoal()==null){
			BooleanExpression validazione = (QUtente.utente.email.equalsIgnoreCase(utente.getEmail().trim())
											.or(QUtente.utente.codicefiscale.equalsIgnoreCase(utente.getCodicefiscale()))
											.or(QUtente.utente.username.equalsIgnoreCase(utente.getUsername())))
											.and(QUtente.utente.stato.ne(UtenteStato.DISABILITATO));
			if(utente.getId()!=null){
				validazione = validazione.and(QUtente.utente.id.ne(utente.getId()));
			}
			if(utenteRepository.count(validazione) > 0L){
				throw new GestattiCatchedException("Esiste un altro utente attivo con stesso codice fiscale, e-mail o username.");
			}
		}
		boolean update = (utente.getId()!=null);
		if(utente.getIndirizzo()!=null && utente.getIndirizzo().getId()!=null){
			Indirizzo indirizzo = indirizzoService.getById(utente.getIndirizzo().getId());
			utente.setIndirizzo(indirizzo);
		}
		utenteRepository.save(utente);
		User userDB = userService.findOneByLogin(utente.getUsername());
		if(userDB == null ){
			User user = userService.createUserInformation(utente.getUsername(),
					utente.getUsername(), utente.getCognome(), utente.getNome(),
					utente.getEmail(), IT);
			if (UtenteStato.ATTIVO.equals(utente.getStato())) {
				userService.activateRegistrationInternal(user.getLogin());
			}
		}else if(update){
			userDB.setEmail(utente.getEmail());
			userService.save(userDB);
		}
	 
	}
	
	public Utente findByUsername(String username){
		return utenteRepository.findByUsername(username);
	}
	
	public List<Utente> findByNomeCognome(String nomeCognome){
		return  utenteRepository.findByNomeCognome("%"+nomeCognome+"%");
	}
	
	public String getNomeLeggibileByUsername(String username){
		Utente u = utenteRepository.findByUsername(username);
		if(u!=null) {
			return u.getNome() + " " + u.getCognome();
		}else {
			return "";
		}
	}
	
	public void delete(Long id) {
		//find username per cancellazione bonita
		Utente utente = utenteRepository.findOne(id);
		utenteRepository.delete(id);
	}
	
	@Transactional(readOnly = true)
	public Iterable<Profilo> activeprofilos(String username){
		BooleanExpression predicateProfilo = QProfilo.profilo.utente.username
				.eq(username);
		predicateProfilo = predicateProfilo.and(validitaService
				.createPredicate(QProfilo.profilo.validita));
		Iterable<Profilo> profili = profiloRepository.findAll(predicateProfilo);
		for (Profilo profilo : profili) {
			
			
			if(profilo.getAoo()!= null ){
				profilo.getAoo().getDescrizione();
				profilo.getAoo().setAooPadre(null);
				profilo.getAoo().setSottoAoo(null);
				profilo.getAoo().setProfiloResponsabileId(null);
				profilo.getAoo().setLogo(null);
			}
			
			if(profilo.getGrupporuolo()!= null ){
				profilo.getGrupporuolo().getDenominazione();
				profilo.getGrupporuolo().getHasRuoli();
			}
		}
		return profili;
	}

	public Utente registrazione(UserDTO userDTO) {
		userDTO.getUtente().setEmail(userDTO.getEmail());
		userDTO.getUtente().setUsername(userDTO.getLogin());
		userDTO.getUtente().setStato(UtenteStato.REGISTRATO);
		if(userDTO.getUtente().getIndirizzo()!=null && userDTO.getUtente().getIndirizzo().getId()!=null){
			Indirizzo indirizzo = indirizzoService.getById(userDTO.getUtente().getIndirizzo().getId());
			userDTO.getUtente().setIndirizzo(indirizzo);
		}
		return utenteRepository.save(userDTO.getUtente());
	}

	public void attiva(Utente utente) throws GestattiCatchedException {
		BooleanExpression validazione = (QUtente.utente.email.equalsIgnoreCase(utente.getEmail().trim())
				.or(QUtente.utente.codicefiscale.equalsIgnoreCase(utente.getCodicefiscale()))
				.or(QUtente.utente.username.equalsIgnoreCase(utente.getUsername())))
				.and(QUtente.utente.validita.validoal.isNull());
		if(utente.getId()!=null){
			validazione = validazione.and(QUtente.utente.id.ne(utente.getId()));
		}
		if(utenteRepository.count(validazione) > 0L){
			throw new GestattiCatchedException("Esiste un altro utente attivo con stesso codice fiscale, e-mail o username.");
		}
		utente.setStato(UtenteStato.ATTIVO);
		if(utente.getValidita()!=null){
			utente.getValidita().setValidoal(null);
		}
		utenteRepository.save(utente);
		User user = userService.findOneByLogin(utente.getUsername());

		if (user == null) {
			user = userService.createUserInformation(utente.getUsername(),
					utente.getUsername(), utente.getCognome(),
					utente.getNome(), utente.getEmail(), IT);
		}
		userService.activateRegistrationInternal(user.getLogin());
		user = userService.findOneByLogin(utente.getUsername());
		List<String> authorities = new ArrayList<String>();
		authorities.add(AuthoritiesConstants.USER);
		userService.updateAuthoritiesOfUser(user.getId(), authorities);
	}

	public void activeAmministratoreIP(Utente utente) throws GestattiCatchedException {
		BooleanExpression validazione = (QUtente.utente.email.equalsIgnoreCase(utente.getEmail().trim())
				.or(QUtente.utente.codicefiscale.equalsIgnoreCase(utente.getCodicefiscale()))
				.or(QUtente.utente.username.equalsIgnoreCase(utente.getUsername())))
				.and(QUtente.utente.validita.validoal.isNull());
		if(utente.getId()!=null){
			validazione = validazione.and(QUtente.utente.id.ne(utente.getId()));
		}
		if(utenteRepository.count(validazione) > 0L){
			throw new GestattiCatchedException("Esiste un altro utente attivo con stesso codice fiscale, e-mail o username.");
		}
		utente.setStato(UtenteStato.ATTIVO);
		if(utente.getValidita()!=null){
			utente.getValidita().setValidoal(null);
		}
		utenteRepository.save(utente);
		User user = userService.findOneByLogin(utente.getUsername());

		if (user == null) {
			user = userService.createUserInformation(utente.getUsername(),
					utente.getUsername(), utente.getCognome(),
					utente.getNome(), utente.getEmail(), IT);
		}
		userService.activateRegistrationInternal(user.getLogin());

		user = userService.findOneByLogin(utente.getUsername());
		List<String> authorities = new ArrayList<String>();
		authorities.add(AuthoritiesConstants.ADMIN);
		authorities.add(AuthoritiesConstants.USER);
		userService.updateAuthoritiesOfUser(user.getId(), authorities);

	}
	
	public void activeAmministratoreRP(Utente utente) throws GestattiCatchedException {
		BooleanExpression validazione = (QUtente.utente.email.equalsIgnoreCase(utente.getEmail().trim())
				.or(QUtente.utente.codicefiscale.equalsIgnoreCase(utente.getCodicefiscale()))
				.or(QUtente.utente.username.equalsIgnoreCase(utente.getUsername())))
				.and(QUtente.utente.validita.validoal.isNull());
		if(utente.getId()!=null){
			validazione = validazione.and(QUtente.utente.id.ne(utente.getId()));
		}
		if(utenteRepository.count(validazione) > 0L){
			throw new GestattiCatchedException("Esiste un altro utente attivo con stesso codice fiscale, e-mail o username.");
		}
		utente.setStato(UtenteStato.ATTIVO);
		if(utente.getValidita()!=null){
			utente.getValidita().setValidoal(null);
		}
		utenteRepository.save(utente);
		User user = userService.findOneByLogin(utente.getUsername());

		if (user == null) {
			user = userService.createUserInformation(utente.getUsername(),
					utente.getUsername(), utente.getCognome(),
					utente.getNome(), utente.getEmail(), IT);
		}
		userService.activateRegistrationInternal(user.getLogin());

		user = userService.findOneByLogin(utente.getUsername());
		List<String> authorities = new ArrayList<String>();
		authorities.add(AuthoritiesConstants.AMMINISTRATORE_RP);
		authorities.add(AuthoritiesConstants.USER);
		userService.updateAuthoritiesOfUser(user.getId(), authorities);
	}

}
