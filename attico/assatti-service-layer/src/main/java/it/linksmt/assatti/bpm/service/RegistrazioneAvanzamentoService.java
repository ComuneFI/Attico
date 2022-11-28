package it.linksmt.assatti.bpm.service;

import java.util.List;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.UtenteRepository;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
public class RegistrazioneAvanzamentoService {
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private UtenteRepository utenteRepository;
	

	@Transactional
	public Long impostaStatoAtto(long idAtto, 
			String nuovoStatoAtto, String denominazioneAttivita) {
		return this.impostaStatoAtto(idAtto, BpmThreadLocalUtil.getProfiloId(), 
				nuovoStatoAtto, denominazioneAttivita, BpmThreadLocalUtil.getMotivazione());		
	}
	
	@Transactional
	public void registraAvanzamento(long idAtto, long idProfilo, String attivita, String note) {
		Atto atto = attoRepository.findOne(idAtto);
		Profilo profilo = profiloRepository.findOne(idProfilo);
		
		if (atto == null) {
			throw new RuntimeException("Errore: l'atto per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		if (profilo == null) {
			throw new RuntimeException("Errore: il profilo utente per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(atto);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(atto.getStato());
		avanzamento.setAttivita(attivita);
		avanzamento.setProfilo(profilo);
		
		Long profiloImp = SecurityUtils.getProfiloIdImpersonificato();
		if (profiloImp != null && profiloImp.longValue() > 0) {
			Profilo profiloObj = profiloRepository.findOne(profiloImp);
			
			if (profiloObj != null) {
				if (!StringUtil.trimStr(profiloObj.getUtente().getUsername())
						.equalsIgnoreCase(SecurityUtils.getPrimitiveLogin())) {
					
					Utente orUtente = utenteRepository.findByUsername(SecurityUtils.getPrimitiveLogin());
					if (orUtente != null) {
						String dicImpers =  "Operazione eseguita da " + orUtente.getNome() + " " + 
								orUtente.getCognome() + " con funzionalità di impersonificazione.";
						
						if (!StringUtil.isNull(note)) {
							note = dicImpers + " - " + note;
						}
						else {
							note = dicImpers;
						}
					}
				}
			}
		}
		
		if (!StringUtil.isNull(note)) {
			avanzamento.setNote(note);
		}
		
		avanzamento = avanzamentoRepository.save(avanzamento);
	}
	
	@Transactional
	public void registraAvanzamentoConWarning(long idAtto, long idProfilo, String attivita, String warningType, String note) {
		Atto atto = attoRepository.findOne(idAtto);
		Profilo profilo = profiloRepository.findOne(idProfilo);
		
		if (atto == null) {
			throw new RuntimeException("Errore: l'atto per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		if (profilo == null) {
			throw new RuntimeException("Errore: il profilo utente per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(atto);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(atto.getStato());
		avanzamento.setAttivita(attivita);
		avanzamento.setProfilo(profilo);
		avanzamento.setWarningType(warningType);
		
		Long profiloImp = SecurityUtils.getProfiloIdImpersonificato();
		if (profiloImp != null && profiloImp.longValue() > 0) {
			Profilo profiloObj = profiloRepository.findOne(profiloImp);
			
			if (profiloObj != null) {
				if (!StringUtil.trimStr(profiloObj.getUtente().getUsername())
						.equalsIgnoreCase(SecurityUtils.getPrimitiveLogin())) {
					
					Utente orUtente = utenteRepository.findByUsername(SecurityUtils.getPrimitiveLogin());
					if (orUtente != null) {
						String dicImpers =  "Operazione eseguita da " + orUtente.getNome() + " " + 
								orUtente.getCognome() + " con funzionalità di impersonificazione.";
						
						if (!StringUtil.isNull(note)) {
							note = dicImpers + " - " + note;
						}
						else {
							note = dicImpers;
						}
					}
				}
			}
		}
		
		if (!StringUtil.isNull(note)) {
			avanzamento.setNote(note);
		}
		
		avanzamento = avanzamentoRepository.save(avanzamento);
	}
	
	@Transactional
	public Long impostaStatoAtto(long idAtto, long idProfilo, 
			String nuovoStatoAtto, String denominazioneAttivita, String note) {
		
		Atto atto = attoRepository.findOne(idAtto);
		Profilo profilo = profiloRepository.findOne(idProfilo);
		
		if (atto == null) {
			throw new RuntimeException("Errore: l'atto per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		if (profilo == null) {
			throw new RuntimeException("Errore: il profilo utente per la registrazione dell'avanzamento di stato risulta nullo.");
		}
		
		if (!StringUtil.trimStr(atto.getStato()).equalsIgnoreCase(
				StringUtil.trimStr(nuovoStatoAtto))) {
			atto.setStato(nuovoStatoAtto);
			attoRepository.save(atto);
		}
		
		Avanzamento avanzamento = new Avanzamento();
		avanzamento.setAtto(atto);
		avanzamento.setDataAttivita(new DateTime());
		avanzamento.setStato(nuovoStatoAtto);
		avanzamento.setAttivita(denominazioneAttivita);
		avanzamento.setProfilo(profilo);
		
		String codiceDecisione = BpmThreadLocalUtil.getCodiceDecisioneLocal();
		if(codiceDecisione!=null) {
			List<Object> avanzamentoTypes = WebApplicationProps.getPropertyList(Constants.WEB_APPLICATION_AVANZAMENTO_WARNING_TYPES);
			if(avanzamentoTypes!=null) {
				for(Object typeLabelObj : avanzamentoTypes) {
					if(typeLabelObj!=null && typeLabelObj instanceof String) {
						String typeLabel = (String)typeLabelObj;
						if(!typeLabel.isEmpty() && typeLabel.contains("#") && typeLabel.split("#").length == 2) {
							if(typeLabel.split("#")[0].equalsIgnoreCase(codiceDecisione)) {
								avanzamento.setWarningType(codiceDecisione);
								break;
							}
						}
					}
				}
			}
		}
		
		long idProfDelega = BpmThreadLocalUtil.getProfiloDeleganteId();
		if (idProfDelega > 0) {
			Profilo delega = profiloRepository.findOne(idProfDelega);
			
			if ((delega != null) && (delega.getUtente() != null)) {
				Utente ut = delega.getUtente();
				String dicDelega =  "Operazione effettuata per delega - Delegante " + ut.getNome() + " " + ut.getCognome();
				
				if (!StringUtil.isNull(note)) {
					note = dicDelega + " - " + note;
				}
				else {
					note = dicDelega;
				}
			}
		}
		
		Long profiloImp = SecurityUtils.getProfiloIdImpersonificato();
		if (profiloImp != null && profiloImp.longValue() > 0) {
			Profilo profiloObj = profiloRepository.findOne(profiloImp);
			
			if (profiloObj != null) {
				if (!StringUtil.trimStr(profiloObj.getUtente().getUsername())
						.equalsIgnoreCase(SecurityUtils.getPrimitiveLogin())) {
					
					Utente orUtente = utenteRepository.findByUsername(SecurityUtils.getPrimitiveLogin());
					if (orUtente != null) {
						String dicImpers =  "Operazione eseguita da " + orUtente.getNome() + " " + 
								orUtente.getCognome() + " con funzionalità di impersonificazione.";
						
						if (!StringUtil.isNull(note)) {
							note = dicImpers + " - " + note;
						}
						else {
							note = dicImpers;
						}
					}
				}
			}
		}
		
		if (!StringUtil.isNull(note)) {
			avanzamento.setNote(note);
		}
		
		avanzamento = avanzamentoRepository.save(avanzamento);
		return avanzamento.getId();
	}
}
