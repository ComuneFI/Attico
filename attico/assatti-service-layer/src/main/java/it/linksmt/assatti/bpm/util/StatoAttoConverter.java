package it.linksmt.assatti.bpm.util;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;

import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.history.HistoricIdentityLinkLog;
import org.camunda.bpm.engine.runtime.ActivityInstance;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.task.Task;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.dto.AooDTO;
import it.linksmt.assatti.bpm.dto.StatoAttoDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;
import it.linksmt.assatti.datalayer.domain.Ruolo;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;

@Service
public class StatoAttoConverter {
	
	@Inject
	private HistoryService historyService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private RuntimeService runtimeService;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	AooService aooService;
	
	public List<StatoAttoDTO> toStatoAtto(
			final Atto atto, 
			final List<Task> taskAtto,
			final List<EventSubscription> allWaiting) {
		List<StatoAttoDTO> statoList = new ArrayList<StatoAttoDTO>();
		
		if (allWaiting != null && allWaiting.size() > 0) {	
			for (EventSubscription eventSub : allWaiting) {
				StatoAttoDTO stato = new StatoAttoDTO();
				stato.setDatagiacenza(new DateTime(eventSub.getCreated().getTime()));
			
				ActivityInstance curActivityTree = runtimeService.getActivityInstance(eventSub.getProcessInstanceId());
				ActivityInstance foundAct = bpmWrapperUtil.findActivityInstance(curActivityTree, eventSub.getActivityId());
				if (foundAct != null) {
					stato.setDescrizione(foundAct.getActivityName());
				}
				else {
					stato.setDescrizione(eventSub.getEventName());
				}
				
				if (MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA.equals(eventSub.getEventName()) ||
					MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO.equals(eventSub.getEventName())) {
					
					stato.setDescrizione(atto.getStato());
					statoList.add(stato);
				}
			}
		}
		if (taskAtto != null && taskAtto.size() > 0) {
			for(Task task : taskAtto) {
				statoList.add(toStatoAtto(task));
			}
		}
		
		return statoList;
	}
	
	
	
	public List<StatoAttoDTO> toStatoAtto(final List<Task> taskAtto) {
		List<StatoAttoDTO> retVal = new ArrayList<StatoAttoDTO>();
		if (taskAtto != null) {
			for (Task task : taskAtto) {
				retVal.add(toStatoAtto(task));
			}
		}
		
		return retVal;
	}
	
	public List<StatoAttoDTO> toStatoAttoPostSeduta(final List<Task> taskAtto) {
		List<StatoAttoDTO> retVal = new ArrayList<StatoAttoDTO>();
		if (taskAtto != null) {
			for (Task task : taskAtto) {
				retVal.add(toStatoAttoPostSeduta(task));
			}
		}
		
		return retVal;
	}
	
	public StatoAttoDTO toStatoAtto(Task task) {
		StatoAttoDTO stato = new StatoAttoDTO();
		
		stato.setDescrizione(task.getName());
		stato.setDatagiacenza(new DateTime(task.getCreateTime()));
		
		if (!StringUtil.isNull(task.getAssignee())) {
			stato.setNominativo(bpmWrapperUtil.getNominativo(task.getAssignee()));
			stato.setAoo(toAooDTO(bpmWrapperUtil.getAoo(task.getAssignee())));
		}
			
		List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
				.taskId(task.getId()).orderByTime().desc().list();
		
		for (HistoricIdentityLinkLog identityLink : identityLinks) {
			if ("assignee".equalsIgnoreCase(identityLink.getType())) {
				if ((task.getAssignee() != null) && 
					 task.getAssignee().equals(identityLink.getUserId())) {
					
					stato.setDatacarico(new DateTime(identityLink.getTime()));
					break;
				}
			}
			else if ( (stato.getAoo() == null) && "candidate"
					.equalsIgnoreCase(identityLink.getType()) &&
					!StringUtil.isNull(identityLink.getGroupId())) {
				
				List<Aoo> candidates = bpmWrapperUtil.getAooByCandidate(identityLink.getGroupId());
				if (candidates != null && candidates.size() > 0) {
					stato.setAoo(toAooDTO(candidates.get(0)));
					break;
				}
				else {
					List<Ruolo> candRuoli = bpmWrapperUtil.getRuoliByCandidate(identityLink.getGroupId());
					if (candRuoli != null && candRuoli.size() > 0) {
						String ruoliStr = "";
						for (Ruolo ruolo : candRuoli) {
							if (!StringUtil.isNull(ruolo.getDescrizione())) {
								if (ruoliStr.length() > 0) {
									ruoliStr += ",";
								}
								ruoliStr += ruolo.getDescrizione();
							}
						}
						stato.setRuoliTask(ruoliStr);
						break;
					}
				}
			}
		}
		return stato;
	}
	
	public StatoAttoDTO toStatoAttoPostSeduta(Task task) {
		StatoAttoDTO stato = new StatoAttoDTO();
		
		stato.setDescrizione(task.getName());
		stato.setDatagiacenza(new DateTime(task.getCreateTime()));
		
		if (!StringUtil.isNull(task.getAssignee())) {
			stato.setNominativo(bpmWrapperUtil.getNominativo(task.getAssignee()));
			stato.setAoo(toAooDTO(bpmWrapperUtil.getAoo(task.getAssignee())));
		}
			
		List<HistoricIdentityLinkLog> identityLinks = historyService.createHistoricIdentityLinkLogQuery()
				.taskId(task.getId()).orderByTime().desc().list();
		
		for (HistoricIdentityLinkLog identityLink : identityLinks) {
			if ("assignee".equalsIgnoreCase(identityLink.getType())) {
				if ((task.getAssignee() != null) && 
					 task.getAssignee().equals(identityLink.getUserId())) {
					
					stato.setDatacarico(new DateTime(identityLink.getTime()));
					break;
				}
			}
			else if ( (stato.getAoo() == null) && "candidate"
					.equalsIgnoreCase(identityLink.getType()) &&
					!StringUtil.isNull(identityLink.getGroupId())) {
				
				boolean statoPresoDaConfInc = false;
				TipoTaskDTO taskDto;
				try {
					taskDto = workflowService.getDettaglioTask(task.getId());
					int pos1 = taskDto.getCandidateGroups().indexOf(Constants.BPM_INCARICO_SEPARATOR);
			    	long idConfInc = 0;
			    	if (pos1 > -1) {
			    		idConfInc = new Long(taskDto.getCandidateGroups().substring(pos1+Constants.BPM_INCARICO_SEPARATOR.length()));
					}
					
			    	if(idConfInc>0) {
			    		List<ConfigurazioneIncaricoAoo> confs = configurazioneIncaricoAooRepository.findByConfigurazioneIncarico(idConfInc);
			    		if(confs!=null) {
			    			for (ConfigurazioneIncaricoAoo configurazioneIncaricoAoo : confs) {

									ConfigurazioneIncaricoAooId configurazioneIncaricoAooIdPrec = configurazioneIncaricoAoo.getPrimaryKey();
									Long idAoo = configurazioneIncaricoAooIdPrec.getIdAoo();
									Aoo aoo = aooService.findOne(idAoo);
									stato.setAoo(toAooDTO(aoo));
									stato.setDatagiacenza(configurazioneIncaricoAoo.getDataCreazione());
									statoPresoDaConfInc = true;
									break;
			    			}
			    		}
			    	}
				} catch (DatatypeConfigurationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				List<Aoo> candidates = bpmWrapperUtil.getAooByCandidate(identityLink.getGroupId());
				if (candidates != null && candidates.size() > 0) {
					if(!statoPresoDaConfInc) {
						stato.setAoo(toAooDTO(candidates.get(0)));
					}
					break;
				}
				else {
					List<Ruolo> candRuoli = bpmWrapperUtil.getRuoliByCandidate(identityLink.getGroupId());
					if (candRuoli != null && candRuoli.size() > 0) {
						String ruoliStr = "";
						for (Ruolo ruolo : candRuoli) {
							if (!StringUtil.isNull(ruolo.getDescrizione())) {
								if (ruoliStr.length() > 0) {
									ruoliStr += ",";
								}
								ruoliStr += ruolo.getDescrizione();
							}
						}
						stato.setRuoliTask(ruoliStr);
						break;
					}
				}
			}
		}
		return stato;
	}
	
	
	public AooDTO toAooDTO(Aoo aoo) {
		AooDTO retVal = new AooDTO();
		retVal.setCodice(aoo.getCodice());
		retVal.setDescrizione(aoo.getDescrizione());
		
		return retVal;
	}
}
