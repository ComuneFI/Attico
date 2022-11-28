package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuolo;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.TipoAzione;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.AvanzamentoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRuoloRepository;
import it.linksmt.assatti.datalayer.repository.ParereRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.datalayer.repository.TipoAzioneRepository;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
@Transactional
public class ParereConsQuartRevService {
	private final Logger log = LoggerFactory.getLogger(ParereConsQuartRevService.class);
	
	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private WorkflowServiceWrapper workflowServiceWrapper;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private ConfigurazioneTaskAooRepository configurazioneTaskAooRepository;
	
	@Inject
	private RuoloRepository ruoloRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private TipoAzioneRepository tipoAzioneRepository;
	
	@Inject
	private ParereRepository parereRepository;
	
	@Inject
	private AvanzamentoRepository avanzamentoRepository;
	
	@Inject
	private ConfigurazioneIncaricoRepository configurazioneIncaricoRepository;
	
	@Inject
	private ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	private ConfigurazioneTaskRuoloRepository configurazioneTaskRuoloRepository;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	
	@Transactional
	public ConfigurazioneIncaricoDto save(ConfigurazioneIncaricoDto configurazioneIncaricoDto) throws ServiceException {
		ConfigurazioneIncaricoDto retVal = configurazioneIncaricoService.save(configurazioneIncaricoDto);
		sincroProcessiCommissione(retVal);
		
		return retVal;
	}
	
	
	@Transactional
	public Parere saveParereQuartRevCommCons(Parere parere, final Long profiloId) {
		Parere par = null;
		if (parere == null) {
			return par;
		}
		
		boolean modifica = false;
		if(parere.getTipoAzione()!=null && parere.getTipoAzione().getCodice()!=null 
				&& parere.getTipoAzione().getCodice().equalsIgnoreCase("parere_quartiere_revisori")) {
			modifica = parere.getVersion()!=null && parere.getVersion().intValue()>0;
		}else if (parere.getTipoAzione()!=null && parere.getTipoAzione().getCodice()!=null 
				&& parere.getTipoAzione().getCodice().equalsIgnoreCase("parere_commissione")) {
			modifica = parere.getId()!=null && parere.getId().intValue()>0;
		}
		
		Profilo prof = profiloRepository.findOne(profiloId);
		parere.setProfilo(prof);
		
		TipoAzione tipoPar = tipoAzioneRepository.findOne(parere.getTipoAzione().getCodice());
		parere.setTipoAzione(tipoPar);
		
		Atto atto = attoRepository.findOne(parere.getAtto().getId());
		parere.setAtto(atto);
		
		Aoo aooPar = aooRepository.findOne(parere.getAoo().getId());
		parere.setAoo(aooPar);
		
		//check if already exists
		for(Parere p : atto.getPareri()) {
			if(p.getAnnullato()==null || !p.getAnnullato()) {
				if(p.getAoo().getId().equals(aooPar.getId()) && p.getTipoAzione().getCodice().equals(WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO))) {
					if(parere.getId()==null || !parere.getId().equals(p.getId())) {
						throw new GestattiCatchedException("Esiste gi\u00E0 il parere di questa commissione. Si priga di ricaricare la pagina e riprovare.");
					}
				}
			}
		}
		
		List<Task> taskCommissione = getTaskCommissione(atto, parere.getAoo());
		if (taskCommissione != null && taskCommissione.size() > 0) {
			Task taskParere = taskCommissione.get(0);		
			ConfigurazioneIncaricoAoo confIncaricoAoo = workflowServiceWrapper.getConfigurazioneIncaricoByAoo(
					taskParere.getId(), parere.getAoo().getId().longValue());
			
			if (confIncaricoAoo == null) {
				throw new RuntimeException("Impossibile associare la configurazione incarico al task corrispondente.");
			}
			
			int giorniScadenza = -1;
			DateTime dataScadenzaDaConfIncaricoAoo = null;
			if (confIncaricoAoo.getGiorniScadenza() != null) {
				giorniScadenza = confIncaricoAoo.getGiorniScadenza().intValue();
				dataScadenzaDaConfIncaricoAoo = new DateTime(confIncaricoAoo.getDataManuale()).plusDays(giorniScadenza);
			}
			
			if (confIncaricoAoo.getDataCreazione() != null) {
				parere.setDataInvio(new DateTime(confIncaricoAoo.getDataCreazione()));
			}else if (taskParere.getCreateTime() != null) {
				parere.setDataInvio(new DateTime(taskParere.getCreateTime()));
			}
			
			ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(
					confIncaricoAoo.getPrimaryKey().getIdConfigurazioneIncarico().longValue());
			
			Date dataIncarico = null;
			if (confIncarico != null) {
				dataIncarico = confIncarico.getDataModifica();
				if (dataIncarico == null) {
					dataIncarico = confIncarico.getDataCreazione();
				}
			}
			if(dataScadenzaDaConfIncaricoAoo!=null && parere.getDataScadenza() == null) {
				parere.setDataScadenza(dataScadenzaDaConfIncaricoAoo);
			}
			
			if ((parere.getDataScadenza() == null) && 
				(dataIncarico != null) && (giorniScadenza > 0)) {
							
				DateTime dataScadenza = new DateTime(dataIncarico).plusDays(giorniScadenza);
				parere.setDataScadenza(dataScadenza);
			}
			String insmod = "";
			if(modifica) {
				insmod = "Modifica";
			}else {
				insmod = "Inserisci";
			}
			
			par = parereRepository.save(parere);
			String labelAzione = parere.getTipoAzione() != null && 
					!parere.getTipoAzione().getDescrizione().trim().isEmpty() ? insmod+" " +  parere.getTipoAzione().getDescrizione().trim() : insmod+" Parere";
			String labelNota = parere.getAoo()!= null && parere.getAoo().getDescrizione() != null ? parere.getAoo().getDescrizione(): null;
			workflowServiceWrapper.eseguiTask(taskParere.getId(), profiloId, labelAzione, labelNota);
			
		}else{
			
			String insmod = "";
			if(modifica) {
				insmod = "Modifica";
			}else {
				insmod = "Inserisci";
			}
			
			par = parereRepository.save(parere);
			
			String labelAzione = parere.getTipoAzione() != null && 
					!parere.getTipoAzione().getDescrizione().trim().isEmpty() ? insmod+" "+  parere.getTipoAzione().getDescrizione().trim() : insmod+" Parere";
			String labelNota = parere.getAoo()!= null && parere.getAoo().getDescrizione() != null ? parere.getAoo().getDescrizione(): null;
			registrazioneAvanzamentoService.registraAvanzamento(parere.getAtto().getId(), profiloId, labelAzione , labelNota);
		}
		return par;
	}
	
	@Transactional
	public Parere saveParereNonEspresso(Parere parere, final Long profiloId) {
		if (parere == null) {
			return null;
		}
		
		if (parere.getData() == null) {
			parere.setData(new DateTime());
		}
		
		Profilo prof = profiloRepository.findOne(profiloId);
		parere.setProfilo(prof);
		
		Aoo aooPar = aooRepository.findOne(parere.getAoo().getId());
		parere.setAoo(aooPar);
		
		TipoAzione tipoPar = tipoAzioneRepository.findOne(parere.getTipoAzione().getCodice());
		parere.setTipoAzione(tipoPar);
		
		Atto atto = attoRepository.findOne(parere.getAtto().getId());
		parere.setAtto(atto);
		
		List<Task> taskCommissione = getTaskCommissione(atto, aooPar);
		if (taskCommissione == null || taskCommissione.size() != 1) {
			throw new RuntimeException("Non esiste un processo attivo per l'inserimento del parere della Commissione.");
		}
		
		Task taskParere = taskCommissione.get(0);		
		ConfigurazioneIncaricoAoo confIncaricoAoo = workflowServiceWrapper.getConfigurazioneIncaricoByAoo(
				taskParere.getId(), aooPar.getId().longValue());
		
		if (confIncaricoAoo == null) {
			throw new RuntimeException("Impossibile associare la configurazione incarico al task corrispondente.");
		}
		
		int giorniScadenza = -1;
		if (confIncaricoAoo.getGiorniScadenza() != null) {
			giorniScadenza = confIncaricoAoo.getGiorniScadenza().intValue();
		}
		
		if (confIncaricoAoo.getDataCreazione() != null) {
			parere.setDataInvio(new DateTime(confIncaricoAoo.getDataCreazione()));
		}else if (taskParere.getCreateTime() != null) {
			parere.setDataInvio(new DateTime(taskParere.getCreateTime()));
		}
		
		ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(
				confIncaricoAoo.getPrimaryKey().getIdConfigurazioneIncarico().longValue());
		
		Date dataIncarico = null;
		if (confIncarico != null) {
			dataIncarico = confIncarico.getDataModifica();
			if (dataIncarico == null) {
				dataIncarico = confIncarico.getDataCreazione();
			}
		}
		
		if ((parere.getDataScadenza() == null) && 
			(dataIncarico != null) && (giorniScadenza > 0)) {
						
			DateTime dataScadenza = new DateTime(dataIncarico).plusDays(giorniScadenza);
			parere.setDataScadenza(dataScadenza);
		}
		
		Parere par = parereRepository.save(parere);
		workflowServiceWrapper.eseguiTask(taskParere.getId(), profiloId, "Parere non espresso");

		return par;
	}
	
	
	private List<Task> getTaskCommissione(Atto attoParere, Aoo aooParere) {
		
		String processDefinitionKey = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_PROCESS_DEFINITION_KEY);
		String startProcessElementVariable = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_PROCESS_ELEMENT_VARIABLE);
		
		String varLikeAooParere = Constants.BPM_ROLE_SEPARATOR + StringUtil.trimStr(aooParere.getCodice()) + Constants.BPM_INCARICO_SEPARATOR;
		
		List<Task> procCommissione = workflowServiceWrapper.processInstanceQuery(
				processDefinitionKey, attoParere.getId().longValue(), startProcessElementVariable, varLikeAooParere);
		
		if (procCommissione != null && procCommissione.size() > 1) {
			throw new RuntimeException("Impossibile individuare in maniera univoca il Task relativo al parere della commissione ");
		}
		
		return procCommissione;
	}
	
	
	@Transactional
	public long aggiornaScadenza(long idConfigIncarico, long idAoo, Integer giorniScadenza, DateTime dataManuale) {
		ConfigurazioneIncaricoAooId confIncaricoId = new ConfigurazioneIncaricoAooId();
		confIncaricoId.setIdConfigurazioneIncarico(idConfigIncarico);
		confIncaricoId.setIdAoo(idAoo);
		
		ConfigurazioneIncaricoAoo updVal = configurazioneIncaricoAooRepository.findOne(confIncaricoId);
		if ((giorniScadenza != null) && (giorniScadenza.intValue() > 0)) {
			updVal.setGiorniScadenza(giorniScadenza);
		}
		else {
			updVal.setGiorniScadenza(null);
		}
		if(dataManuale != null ) {
			updVal.setDataManuale(dataManuale);
		}
		else {
			updVal.setDataManuale(null);
		}
		
		updVal = configurazioneIncaricoAooRepository.save(updVal);
		return updVal.getPrimaryKey().getIdConfigurazioneIncarico().longValue();
	}
	
	
	
	private void sincroProcessiCommissione(ConfigurazioneIncaricoDto configurazioneIncaricoDto) {
		String processDefinitionKey = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_PROCESS_DEFINITION_KEY);
		String startProcessElementVariable = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_PROCESS_ELEMENT_VARIABLE);
		String codiceTipoParereCommissioniConsiliari = WebApplicationProps.getProperty(ConfigPropNames.PARERE_COMMISSIONI_CONSILIARI_CODICE_TIPO);
				
		if (StringUtil.isNull(processDefinitionKey) || StringUtil.isNull(startProcessElementVariable)) {
			throw new RuntimeException("Occorre specificare i seguenti parametri di configurazione: "
					+ "'processDefinitionKey', 'startProcessElementVariable'");
		}
		
		long idConfigurazioneTask = configurazioneIncaricoDto.getIdConfigurazioneTask().longValue();
		List<ConfigurazioneTaskAoo> listConfigurazioneTaskAoo = configurazioneTaskAooRepository.findByConfigurazioneTask(idConfigurazioneTask);
		
		if ((listConfigurazioneTaskAoo == null) || (listConfigurazioneTaskAoo.size() < 1)) {
			throw new RuntimeException("Non sono state trovate Configurazioni Task per Ufficio corrispondenti a idConfigurazioneTask: " + idConfigurazioneTask);
		}
		
		Atto attoProc = attoRepository.findOne(configurazioneIncaricoDto.getIdAtto().longValue());
		if (attoProc == null) {
			throw new RuntimeException("Impossibile accedere ai dati dell'Atto con id: " + 
											configurazioneIncaricoDto.getIdAtto().longValue());
		}
		
		List<ConfigurazioneIncaricoAooDto> newValAoo = configurazioneIncaricoDto.getListConfigurazioneIncaricoAooDto();
		
		for (ConfigurazioneTaskAoo aooTask : listConfigurazioneTaskAoo) {
			
			Aoo aoo = aooRepository.findOne(aooTask.getPrimaryKey().getIdAoo().longValue());
			String suffix = Constants.BPM_ROLE_SEPARATOR + StringUtil.trimStr(aoo.getCodice()) + 
					Constants.BPM_INCARICO_SEPARATOR + configurazioneIncaricoDto.getId().longValue();
			
			List<ConfigurazioneTaskRuolo> listConfigurazioneTaskRuolo = 
					configurazioneTaskRuoloRepository.findByConfigurazioneTask(idConfigurazioneTask);
			
			List<Ruolo> listRuoli = new ArrayList<>();
			for (ConfigurazioneTaskRuolo ruoloTask : listConfigurazioneTaskRuolo) {
				listRuoli.add(ruoloRepository.findOne(ruoloTask.getPrimaryKey().getIdRuolo()));
			}
			
			for (Ruolo ruolo : listRuoli) {
				String valAssengnaTask = StringUtil.trimStr(ruolo.getCodice()) + suffix;
				
				Map<String, Object> variables = new HashMap<String, Object>();
				variables.put(startProcessElementVariable, valAssengnaTask);
				
				List<ProcessInstance> procCommissioni = workflowServiceWrapper.processInstanceQuery(
						processDefinitionKey, configurazioneIncaricoDto.getIdAtto().longValue(), variables);
								
				if (!checkAoo(newValAoo, aooTask.getPrimaryKey().getIdAoo().longValue())) {
					// Elimino eventuali istanze di processo presenti
					for (ProcessInstance curInstance : procCommissioni) {
						workflowServiceWrapper.eliminaIstanzaProcesso(curInstance.getProcessInstanceId(), "Parere Commissione rimosso.");
					}
				}
				else if(!checkParereEspresso(attoProc, aoo.getId().longValue(), 
						codiceTipoParereCommissioniConsiliari) && procCommissioni.size() < 1) {
					// Se il parere non è stato già espresso, avvio una nuova istanza
					log.info("AVVIO PROCESSO: " + processDefinitionKey + " - VAR: " + startProcessElementVariable + "=" + valAssengnaTask);
					workflowServiceWrapper.avviaProcessoBpmSecondario(attoProc, processDefinitionKey, variables);
				}
			}	
		}
	}
	
	
	private boolean checkAoo(List<ConfigurazioneIncaricoAooDto> checkList, long idAoo) {
		for (ConfigurazioneIncaricoAooDto aooPresente : checkList) {
			if (aooPresente.getIdAoo().longValue() == idAoo) {
				return true;
			}
		}
		return false;
	}
	
	private boolean checkParereEspresso(Atto attoProc, long idAoo, String codiceTipoAzione) {
		
		Set<Parere> parSet = attoProc.getPareri();
		if (parSet != null) {
			Iterator<Parere> parIt = parSet.iterator();
			while (parIt.hasNext()) {
				Parere parere = parIt.next();
				if ((parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) &&
					(parere.getAoo() != null) && (parere.getTipoAzione() != null) && 
					(!StringUtil.isNull(parere.getTipoAzione().getCodice()))) {
				
					if (codiceTipoAzione.equalsIgnoreCase(parere.getTipoAzione().getCodice()) && 
							(parere.getAoo().getId().longValue() == idAoo)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}	
}
