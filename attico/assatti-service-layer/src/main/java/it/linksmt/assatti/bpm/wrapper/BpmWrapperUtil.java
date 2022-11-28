package it.linksmt.assatti.bpm.wrapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.transaction.Transactional;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.Task;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuolo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoIter;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoProfiloRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRuoloRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.RuoloRepository;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.datalayer.repository.TipoIterRepository;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.ConfigurazioneIncaricoService;
import it.linksmt.assatti.service.TaskRiassegnazioneService;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;

@Service
public class BpmWrapperUtil {
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private TipoAttoRepository tipoAttoRepository;
	
	@Inject
	private TipoIterRepository tipoIterRepository;

	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private RuoloRepository ruoloRepository;

	@Inject
	private ConfigurazioneTaskRepository configurazioneTaskRepository;
	
	@Inject
	private ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	private ConfigurazioneIncaricoProfiloRepository configurazioneIncaricoProfiloRepository;
	
	@Inject
	private ConfigurazioneTaskRuoloRepository configurazioneTaskRuoloRepository;
	
	@Inject
	private ConfigurazioneIncaricoRepository configurazioneIncaricoRepository;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private TaskService taskService;
	
	@Inject
	private RuntimeService runtimeService;
	
	@Inject
	private TaskRiassegnazioneService taskRiassegnazioneService;
	
	@Transactional
	public void checkTasksAssignmentAndReasignee(String currentAssignment, String newAssignment, Long attoId, boolean withoutGruopOnly) {
		List<Task> tasks = taskService.createTaskQuery().active().processInstanceBusinessKey(attoId.toString()).list();
		for(Task task : tasks) {
			if(task.getAssignee()!=null && task.getAssignee().equals(currentAssignment)) {
				if(!withoutGruopOnly) {
					taskService.setAssignee(task.getId(), newAssignment);
				}else {
					boolean hasGroup = this.taskHasGroup(task.getId());
					if(!hasGroup) {
						taskService.setAssignee(task.getId(), newAssignment);
					}
				}
			}
		}
	}
	
	public boolean taskHasGroup(String taskId) {
		List<IdentityLink> ils = taskService.getIdentityLinksForTask(taskId);
		boolean hasGroup = false;
		if(ils!=null) {
			for(IdentityLink il : ils) {
				if(il!=null && il.getGroupId()!=null && !il.getGroupId().isEmpty()) {
					hasGroup = true;
					break;
				}
			}
		}
		return hasGroup;
	}
	
	public Task getTask(String taskId) {
		return taskService.createTaskQuery().active().taskId(taskId).singleResult();
	}
	
	public Long getAttoId(Task task) {
		Long attoId = null;
		if(task!=null) {
			String businessKey = runtimeService.createProcessInstanceQuery().active().processInstanceId(task.getProcessInstanceId()).list().get(0).getBusinessKey();
			attoId = Long.parseLong(businessKey);
		}
		return attoId;
	}
	
	public Long getAttoIdByTaskId(String taskId) {
		Long attoId = null;
		if(taskId!=null) {
			Task task = taskService.createTaskQuery().active().taskId(taskId).singleResult();
			if(task!=null) {
				String businessKey = runtimeService.createProcessInstanceQuery().active().processInstanceId(task.getProcessInstanceId()).list().get(0).getBusinessKey();
				attoId = Long.parseLong(businessKey);
			}
		}
		return attoId;
	}

	public Map<String, Object> fillVariabiliAvvioProcesso(Atto atto, long profiloId) {
		
		TipoIter tipoIter = tipoIterRepository.findOne(atto.getTipoIter().getId());
		TipoAtto tipoAtto = tipoAttoRepository.findOne(atto.getTipoAtto().getId());
		
		
		Map<String, Object> variables = new HashMap<String, Object>();
		
		variables.put(AttoProcessVariables.ID_ATTO, String.valueOf(atto.getId()));
		
		variables.put(AttoProcessVariables.TIPO_ATTO, tipoAtto.getCodice());
		variables.put(AttoProcessVariables.OGGETTO_ATTO, atto.getOggetto());
		variables.put(AttoProcessVariables.TIPO_ITER, tipoIter.getCodice());
		
		if (profiloId > 0) {
			variables.put(AttoProcessVariables.ISTRUTTORE_ATTO, getUsernameForBpm(profiloId, -1L));
		}

		if(atto.getAoo()!=null) {
			variables.put(AttoProcessVariables.AOO_CODICE, atto.getAoo().getCodice());
			variables.put(AttoProcessVariables.AOO_DESCRIZIONE, atto.getAoo().getDescrizione());
		}
		
		
		return variables;
	}
	
	public String getUsernameForBpm(long profiloId, Long idConfigurazioneIncarico) {
		Profilo profilo = profiloRepository.findOne(profiloId);
		ConfigurazioneIncarico confIncarico = null;
		
		if (idConfigurazioneIncarico!=null && idConfigurazioneIncarico.longValue() > 0L) {
			confIncarico = configurazioneIncaricoRepository.findOne(idConfigurazioneIncarico);
		}
		
		return getUsernameForBpm(profilo, confIncarico);
	}
	
	public String getUsernameForBpm(Profilo profilo, ConfigurazioneIncarico confIncarico) {
		String retVal = profilo.getUtente().getUsername() + Constants.BPM_USERNAME_SEPARATOR + String.valueOf(profilo.getId());
		if (confIncarico != null) {
			retVal += Constants.BPM_INCARICO_SEPARATOR + String.valueOf(confIncarico.getId());
		}
		return retVal;
	}
	
	public List<String> getGroupLikeForBpm(Profilo profilo) {
		List<String> retVal = new ArrayList<String>();
		String codiceAoo = StringUtil.trimStr(profilo.getAoo().getCodice());
		
		Set<Ruolo> ruoliProf = profilo.getGrupporuolo().getHasRuoli();
		for (Ruolo ruolo : ruoliProf) {
			retVal.add(StringUtil.trimStr(ruolo.getCodice()));
			retVal.add(StringUtil.trimStr(ruolo.getCodice()) + Constants.BPM_ROLE_SEPARATOR + codiceAoo + "%");
		}
		return retVal;
	}
	
	public List<String> getGroupLikeForBpmForMonitoraggio(Profilo profilo) {
		List<String> retVal = new ArrayList<String>();
		String codiceAoo = StringUtil.trimStr(profilo.getAoo().getCodice());
		
		Set<Ruolo> ruoliProf = profilo.getGrupporuolo().getHasRuoli();
		for (Ruolo ruolo : ruoliProf) {
			retVal.add(StringUtil.trimStr(ruolo.getCodice()));
			retVal.add(StringUtil.trimStr(ruolo.getCodice()) + Constants.BPM_ROLE_SEPARATOR + codiceAoo + Constants.BPM_INCARICO_SEPARATOR +"%");
		}
		return retVal;
	}
	
	public List<String> getCandidategroups(final String taskBpmId) {
		List<String> candidategroups = new ArrayList<String>();
		
		List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskBpmId);
		for (IdentityLink identityLink : identityLinks) {
			
			// corrisponde alle costanti definite in IdentityLinkType. "candidate" identifica una relazione di tipo candidate.
			String type = identityLink.getType(); 
			String groupId = identityLink.getGroupId();

			if (IdentityLinkType.CANDIDATE.equals(type) && groupId != null) {
				candidategroups.add(groupId);
			}
		}
		return candidategroups;
	}
	
	public String getNominativo(String usernameForBpm) {
		Profilo profilo = getProfiloByUsernameBpm(usernameForBpm);
		return profilo.getUtente().getNome() + " " + profilo.getUtente().getCognome();
	}
	
	public long getIdProfiloByUsernameBpm(String usernameForBpm) {
		String profiloId = usernameForBpm.substring(usernameForBpm.lastIndexOf(Constants.BPM_USERNAME_SEPARATOR)
				+ Constants.BPM_USERNAME_SEPARATOR.length());
		
		int idxQualifica = profiloId.indexOf(Constants.BPM_INCARICO_SEPARATOR);
		if (idxQualifica > -1) {
			profiloId = profiloId.substring(0, idxQualifica);
		}
		Long pId = null;
		try {
			pId = Long.parseLong(profiloId);
		}catch(Exception e) {
			pId = -1L;
		}
		return pId;
	}
	
	public String getUsernameByUsernameBpm(String usernameForBpm) {
		if(usernameForBpm.indexOf(Constants.BPM_USERNAME_SEPARATOR) < 0) {
			return usernameForBpm;
		}else {
			return usernameForBpm.substring(0, usernameForBpm.indexOf(Constants.BPM_USERNAME_SEPARATOR));
		}
	}
	
	public Profilo getProfiloByUsernameBpm(String usernameForBpm) {
		return profiloRepository.findOne(getIdProfiloByUsernameBpm(usernameForBpm));
	}
	
	public long getIdConfigurazioneIncaricoByUsernameBpm(String usernameForBpm) {
		
		int idxQualifica = usernameForBpm.indexOf(Constants.BPM_INCARICO_SEPARATOR);
		if (idxQualifica < 0) {
			return -1;
		}
				
		String configurazioneIncaricoId = usernameForBpm.substring(usernameForBpm.lastIndexOf(Constants.BPM_INCARICO_SEPARATOR)
				+ Constants.BPM_INCARICO_SEPARATOR.length());
		
		return Long.parseLong(configurazioneIncaricoId);
	}
	
	public Aoo getAoo(String usernameForBpm) {
		Profilo profilo = getProfiloByUsernameBpm(usernameForBpm);
		return profilo.getAoo();
	}
	
	public List<Aoo> getAooByCandidate(String candidateGroup) {
		int idxAoo = candidateGroup.indexOf(Constants.BPM_ROLE_SEPARATOR);
		if (idxAoo > -1) {
			String codiceAoo = candidateGroup.substring(idxAoo + Constants.BPM_ROLE_SEPARATOR.length());
			
			int idxIncarico = codiceAoo.lastIndexOf(Constants.BPM_INCARICO_SEPARATOR);
			if (idxIncarico > -1) {
				codiceAoo = codiceAoo.substring(0, idxIncarico);
			}
			
			return aooRepository.findAoosByCodice(codiceAoo);
		}
		
		return null;
	}
	
	public List<Ruolo> getRuoliByCandidate(String candidateGroup) {
		List<Ruolo> ruoliList = new ArrayList<Ruolo>();
		
		String ruoliStr = candidateGroup;
		
		int idxAoo = candidateGroup.indexOf(Constants.BPM_ROLE_SEPARATOR);
		if (idxAoo > 0) {
			ruoliStr = candidateGroup.substring(0, idxAoo);
		}
		
		String[] codRuoli = ruoliStr.split(",");
		for (int i = 0; i < codRuoli.length; i++) {
			ruoliList.addAll(ruoloRepository.findByCodice(StringUtil.trimStr(codRuoli[i])));
		}
		
		return ruoliList;
	}
	
	public ActivityInstance findActivityInstance(ActivityInstance instance, String activityId) {
		if ((instance == null) || StringUtil.isNull(activityId)) {
			return null;
		}
		
		if (activityId.equals(instance.getActivityId())) {
			return instance;
		}
		
		for (ActivityInstance instEl : instance.getChildActivityInstances()) {
			ActivityInstance foundAct = findActivityInstance(instEl, activityId);
			if (foundAct != null) {
				return foundAct;
			}
		}
		
		return null;
	}
	
	public Atto getAtto(String businessKey) {
		if (StringUtil.isNull(businessKey)) {
			return null;
		}
		
		return attoRepository.findOne(Long.parseLong(businessKey));
	}
	
	public void salvaAssegnazioniIncarichi(long idAtto, final String taskBpmId, Iterable<AssegnazioneIncaricoDTO> assegnazioni) {
		List<ConfigurazioneIncarico> incarichiAtto = configurazioneIncaricoService.findByAttoId(idAtto);
		
		for (AssegnazioneIncaricoDTO assegnazioneIncaricoDTO : assegnazioni) {
			String codiceConfig = StringUtil.trimStr(assegnazioneIncaricoDTO.getCodiceConfigurazione());
			String assegnazioneVar = StringUtil.trimStr(assegnazioneIncaricoDTO.getVarAssegnatario());
			
			if (!StringUtil.isNull(assegnazioneVar) && !StringUtil.isNull(codiceConfig)) {
				
				// Elimino eventuali dati già presenti
				List<ProcessInstance> processes = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(idAtto+"").list();
				for(ProcessInstance pi : processes){
					runtimeService.removeVariable(((ExecutionEntity)pi).getId(), assegnazioneVar);
				}
				taskService.removeVariable(taskBpmId, assegnazioneVar);
				
				// Dal codice configurazione risalire agli elementi impostati in atto
				ConfigurazioneTask confTask = configurazioneTaskRepository.findByCodice(codiceConfig);
				for (ConfigurazioneIncarico incarico : incarichiAtto) {
					if (incarico.getConfigurazioneTask().getIdConfigurazioneTask().longValue() ==
							confTask.getIdConfigurazioneTask().longValue()) {
						
						List<ConfigurazioneIncaricoProfilo> listConfigurazioneIncaricoProfilo = 
								configurazioneIncaricoProfiloRepository.findByPrimaryKey_IdConfigurazioneIncarico(incarico.getId());
						
						if ((listConfigurazioneIncaricoProfilo != null) && (listConfigurazioneIncaricoProfilo.size() > 0)) {
							List<String> assignmentList = salvaProfiliIncaricati(taskBpmId, assegnazioneVar, listConfigurazioneIncaricoProfilo, confTask.isMultipla());
							List<String> reassignmentList = taskRiassegnazioneService.getReassignmentList(incarico.getId(), idAtto);
							if(reassignmentList!=null && reassignmentList.size() > 0) {
								for(String reassignment : reassignmentList) {
									if(reassignment!=null && !assignmentList.contains(reassignment)) {
										registrazioneAvanzamentoService.registraAvanzamento(idAtto, BpmThreadLocalUtil.getProfiloId(), "Modificata riassegnazione incarico di " + confTask.getNome(), null);
										break;
									}
								}
								taskRiassegnazioneService.removeReassignment(incarico.getId(), idAtto);
							}
						}
						else {
							List<ConfigurazioneIncaricoAoo> listConfigurazioneIncaricoAoo = 
									configurazioneIncaricoAooRepository.findByConfigurazioneIncarico(incarico.getId());
							
							List<ConfigurazioneTaskRuolo> listConfigurazioneTaskRuolo = 
									configurazioneTaskRuoloRepository.findByConfigurazioneTask(confTask.getIdConfigurazioneTask());
							
							List<Ruolo> listRuoli = new ArrayList<>();
							for (ConfigurazioneTaskRuolo ruoloTask : listConfigurazioneTaskRuolo) {
								listRuoli.add(ruoloRepository.findOne(ruoloTask.getPrimaryKey().getIdRuolo()));
							}
							
							salvaUfficiIncaricati(taskBpmId, assegnazioneVar, listConfigurazioneIncaricoAoo, listRuoli);
						}
					}
				}
			}
		}
	}
	
	public void salvaCambioCoda(long idAtto, final String taskBpmId, ConfigurazioneIncarico configurazioneIncarico, Iterable<AssegnazioneIncaricoDTO> assegnazioni, Long idProfilo, Long idAooPrecedente) {
		
		ConfigurazioneTask confTask = configurazioneIncarico.getConfigurazioneTask();
		List<ConfigurazioneIncaricoAoo> listConfigurazioneIncaricoAoo = 
				configurazioneIncaricoAooRepository.findByConfigurazioneIncarico(configurazioneIncarico.getId());
		
		if(confTask!=null&&confTask.getCodice()!=null) {
			List<ConfigurazioneIncarico> incarichiAtto = configurazioneIncaricoService.findByAttoId(idAtto);
			
			List<ConfigurazioneTaskRuolo> listConfigurazioneTaskRuolo = 
					configurazioneTaskRuoloRepository.findByConfigurazioneTask(confTask.getIdConfigurazioneTask());
			
			List<Ruolo> listRuoli = new ArrayList<>();
			for (ConfigurazioneTaskRuolo ruoloTask : listConfigurazioneTaskRuolo) {
				listRuoli.add(ruoloRepository.findOne(ruoloTask.getPrimaryKey().getIdRuolo()));
			}
			
			// Elimino eventuali dati già presenti
			List<ProcessInstance> processes = runtimeService.createProcessInstanceQuery().processInstanceBusinessKey(idAtto+"").list();
			for(ProcessInstance pi : processes){
				runtimeService.removeVariable(((ExecutionEntity)pi).getId(), confTask.getProcessVarName());
			}
			taskService.removeVariable(taskBpmId, confTask.getProcessVarName());
					
			// Dal codice configurazione risalire agli elementi impostati in atto
			for (ConfigurazioneIncarico incarico : incarichiAtto) {
				if (incarico.getConfigurazioneTask().getIdConfigurazioneTask().longValue() ==
						confTask.getIdConfigurazioneTask().longValue()) {
					
					List<ConfigurazioneIncaricoProfilo> listConfigurazioneIncaricoProfilo = 
							configurazioneIncaricoProfiloRepository.findByPrimaryKey_IdConfigurazioneIncarico(incarico.getId());
					
					
					if ((listConfigurazioneIncaricoProfilo != null) && (listConfigurazioneIncaricoProfilo.size() > 0)) {
						List<String> assignmentList = salvaProfiliIncaricati(taskBpmId, confTask.getProcessVarName() , listConfigurazioneIncaricoProfilo, confTask.isMultipla());
						List<String> reassignmentList = taskRiassegnazioneService.getReassignmentList(incarico.getId(), idAtto);
						if(reassignmentList!=null && reassignmentList.size() > 0) {
							for(String reassignment : reassignmentList) {
								if(reassignment!=null && !assignmentList.contains(reassignment)) {
									registrazioneAvanzamentoService.registraAvanzamento(idAtto, idProfilo, "Modificata riassegnazione incarico di " + confTask.getNome(), null);
									break;
								}
							}
							taskRiassegnazioneService.removeReassignment(incarico.getId(), idAtto);
						}
					}
					else {
						salvaUfficiIncaricatiECambiaCoda(idAtto, taskBpmId, confTask.getProcessVarName(), listConfigurazioneIncaricoAoo, listRuoli, idProfilo, idAooPrecedente);
					}
				}
			}
		}

	}
	
	
	
	
	@Transactional
	public void saveProcessVariables(Long attoId, Map<String, Object> vars) {
		if(vars!=null && vars.size() > 0) {
			List<Execution> exs = runtimeService.createExecutionQuery().processInstanceBusinessKey(attoId.toString()).active().list();
			for(Execution ex : exs) {
				runtimeService.setVariables(ex.getId(), vars);
			}
		}
	}
	
	public Object getProcessVarValue(Long attoId, String processVarName) {
		Object var = null;
		List<Execution> exs = runtimeService.createExecutionQuery().processInstanceBusinessKey(attoId.toString()).active().list();
		for(Execution ex : exs) {
			var = runtimeService.getVariable(ex.getId(), processVarName);
			if(var!=null) {
				break;
			}
		}
		return var;
	}
	
	/**
	 * @param taskBpmId
	 * @param variableSet
	 * @param valProfili
	 */
	private List<String> salvaProfiliIncaricati(
			final String taskBpmId, 
			final String variableSet, 
			final List<ConfigurazioneIncaricoProfilo> profili,
			boolean multiple) {
		
		if (profili == null || profili.isEmpty()) {
			return null;
		}
		
		List<String> valProfili = new ArrayList<String>();
		Collections.sort(profili, new Comparator<ConfigurazioneIncaricoProfilo>() {
			@Override
			public int compare(ConfigurazioneIncaricoProfilo o1, ConfigurazioneIncaricoProfilo o2) {
				if(o1.getOrdineFirma()!=null && o2.getOrdineFirma()!=null && !o1.getOrdineFirma().equals(o2.getOrdineFirma())) {
					return o1.getOrdineFirma().compareTo(o2.getOrdineFirma());
				}else {
					return 1;
				}
			}
		});
		for (ConfigurazioneIncaricoProfilo confProfilo : profili) {
			String usernameBpm = getUsernameForBpm(
					confProfilo.getPrimaryKey().getIdProfilo().longValue(), 
					confProfilo.getPrimaryKey().getIdConfigurazioneIncarico().longValue());
			valProfili.add(usernameBpm);
		}
		
		Map<String, Object> variables = new HashMap<String, Object>();
		if (multiple) {
			variables.put(StringUtil.trimStr(variableSet), valProfili);
		}
		else if (valProfili.size() > 0) {
			variables.put(StringUtil.trimStr(variableSet), valProfili.get(0));
		}
		
		taskService.setVariables(taskBpmId, variables);
		return valProfili;
	}
	
	
	/**
	 * @param taskBpmId
	 * @param variableSet
	 * @param valAoos
	 * @param valRuoli
	 */
	private void salvaUfficiIncaricati(
			final String taskBpmId, 
			final String variableSet, 
			final List<ConfigurazioneIncaricoAoo> listConfAoo, 
			final List<Ruolo> listRuoli) {
		
		if ( (listConfAoo == null)  || listConfAoo.isEmpty() ||
			 (listRuoli == null) || listRuoli.isEmpty()) {
			return;
		}
		
		List<String> valGruppiBpm = new ArrayList<String>();
		for (ConfigurazioneIncaricoAoo confAoo : listConfAoo) {
			Aoo aoo = aooRepository.findOne(confAoo.getPrimaryKey().getIdAoo());
			String suffix = Constants.BPM_ROLE_SEPARATOR + StringUtil.trimStr(aoo.getCodice()) + 
					Constants.BPM_INCARICO_SEPARATOR + confAoo.getPrimaryKey().getIdConfigurazioneIncarico().longValue();
			
			for (Ruolo ruolo : listRuoli) {
				valGruppiBpm.add(StringUtil.trimStr(ruolo.getCodice()) + suffix);
			}
		}
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(StringUtil.trimStr(variableSet), valGruppiBpm);
		
		taskService.setVariables(taskBpmId, variables);
	}
	
	private void salvaUfficiIncaricatiECambiaCoda(
			final Long idAtto,
			final String taskBpmId, 
			final String variableSet, 
			final List<ConfigurazioneIncaricoAoo> listConfAoo, 
			final List<Ruolo> listRuoli,
			final Long idProfiloDirigente,
			final Long idAooPrecedente) {
		
		if ( (listConfAoo == null)  || listConfAoo.isEmpty() ||
			 (listRuoli == null) || listRuoli.isEmpty()) {
			return;
		}
		
		List<String> valGruppiBpm = new ArrayList<String>();
		Aoo aoo = null;
		for (ConfigurazioneIncaricoAoo confAoo : listConfAoo) {
			aoo = aooRepository.findOne(confAoo.getPrimaryKey().getIdAoo());
			String suffix = Constants.BPM_ROLE_SEPARATOR + StringUtil.trimStr(aoo.getCodice()) + 
					Constants.BPM_INCARICO_SEPARATOR + confAoo.getPrimaryKey().getIdConfigurazioneIncarico().longValue();
			
			for (Ruolo ruolo : listRuoli) {
				valGruppiBpm.add(StringUtil.trimStr(ruolo.getCodice()) + suffix);
			}
		}
		
		Map<String, Object> variables = new HashMap<String, Object>();
		variables.put(StringUtil.trimStr(variableSet), valGruppiBpm);
		
		taskService.setVariables(taskBpmId, variables);
		List<IdentityLink> idLi = taskService.getIdentityLinksForTask(taskBpmId);
		if(idLi!=null && idLi.size()>0 && valGruppiBpm != null && valGruppiBpm.size()>0) {
			String old = idLi.get(0).getGroupId();
			taskService.deleteCandidateGroup(taskBpmId, old);
			
		}
		if(valGruppiBpm != null && valGruppiBpm.size()>0) {
			taskService.addCandidateGroup(taskBpmId, valGruppiBpm.get(0));
			
			String statoAttoVal = attoRepository.findOne(idAtto).getStato();
			Aoo aooPrecedente = aooRepository.findOne(idAooPrecedente);
			registrazioneAvanzamentoService.impostaStatoAtto(
					idAtto, idProfiloDirigente, statoAttoVal, 
					"Cambio Ufficio", " Da: " + aooPrecedente.getDescrizione() + " A: " +aoo.getDescrizione());
		}
	}
	
}
