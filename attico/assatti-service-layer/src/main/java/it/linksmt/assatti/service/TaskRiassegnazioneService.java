package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QTaskRiassegnazione;
import it.linksmt.assatti.datalayer.domain.TaskRiassegnazione;
import it.linksmt.assatti.datalayer.repository.TaskRiassegnazioneRepository;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;
import it.linksmt.assatti.service.dto.TaskDesktopDTO;
import it.linksmt.assatti.service.dto.TaskRiassegnazioneDto;
import it.linksmt.assatti.service.exception.ServiceException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.utility.Constants;

/**
 * Service class for managing TaskRiassegnazione.
 */
@Service
@Transactional
public class TaskRiassegnazioneService {
	private final Logger log = LoggerFactory.getLogger(TaskRiassegnazioneService.class);

	@Inject
	private TaskRiassegnazioneRepository taskRiassegnazioneRepository;
	
	@Inject
	private WorkflowServiceWrapper workflowService;
	
	@Inject
	private ConfigurazioneIncaricoProfiloService configurazioneIncaricoProfiloService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private ConfigurazioneTaskService configurazioneTaskService;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private UtenteService utenteService;
	
//	@Inject
//	private QualificaprofessionaleService qualificaprofessionaleService;
	
	private static final String CODICE_CONF_TASTK_ISTRUTTORIA = "PREDISPOSIZIONE_PROPOSTA";
	
	@Transactional(readOnly = true)
	public Page<TaskRiassegnazioneDto> findTasksIstruttore(Long idProfiloOriginario, Integer offset, Integer limit) throws ServiceException, DatatypeConfigurationException {
		Page<TaskRiassegnazioneDto> page = null;
		List<TaskRiassegnazioneDto> dtos = new ArrayList<TaskRiassegnazioneDto>();
		Profilo profiloOriginario = profiloService.findOneBase(idProfiloOriginario);
		Pageable paginazione = PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, "id"));
		
		Collection<Long> idAttiGiaRiassegnati = this.findAttoIdsByProfiloOrigineIstruttore(idProfiloOriginario);
		Page<Atto> attiIstruttore = attoService.findListAttoItinereByIstruttore(idProfiloOriginario, paginazione, idAttiGiaRiassegnati);
		ConfigurazioneTaskDto confTaskIstruttoria = configurazioneTaskService.findByCodice(CODICE_CONF_TASTK_ISTRUTTORIA);
		if(attiIstruttore!=null) {
			for(Atto atto : attiIstruttore) {
				if(atto!=null) {
					List<Profilo> profili = configurazioneTaskService.getCandidateProfList(confTaskIstruttoria, atto.getAoo().getId(), profiloOriginario.getAoo().getId());
					TaskRiassegnazioneDto dto = new TaskRiassegnazioneDto();
//					TaskRiassegnazione riassegnazionePreesistente = this.findByProfiloOrigine(idProfiloOriginario, atto.getId(), "ISTRUTTORE_ATTO");
					if(profili!=null) {
						Profilo excludeProf = null;
						for(Profilo p : profili) {
							if(p!=null && p.getId().equals(idProfiloOriginario)) {
								excludeProf = p;
								break;
							}
						}
						if(excludeProf!=null) {
							profili.remove(excludeProf);
						}
					}
					dto.setProfili(profili);
					dto.setAttoId(atto.getId());
					dto.setCodiceAtto(atto.getCodiceCifra() + (atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty() ? " - " + atto.getNumeroAdozione() : ""));
					dto.setLavorazione("Istruttoria");
					dto.setOggettoAtto(atto.getOggetto());
					
//					if(riassegnazionePreesistente!=null) {
//						dto.setId(riassegnazionePreesistente.getId());
//						Profilo profiloNuovo = profiloService.findOneBase(riassegnazionePreesistente.getProfiloNuovo());
//						Profilo pMin = DomainUtil.minimalProfilo(profiloNuovo);
//						pMin.setHasQualifica(profiloNuovo.getHasQualifica());
//						dto.setProfiloNuovo(pMin);
//						QualificaProfessionale qualificaNuova = qualificaprofessionaleService.findOne(riassegnazionePreesistente.getQualificaNuova());
//						dto.setQualificaNuova(qualificaNuova);
//					}
					dtos.add(dto);
				}
			}
		}
		page = new PageImpl<TaskRiassegnazioneDto>(dtos, paginazione, attiIstruttore.getTotalElements());
		return page;
	}
	
	@Transactional(readOnly = true)
	public Page<TaskRiassegnazioneDto> findTasksRiassignee(Long idProfiloOriginario, Integer offset, Integer limit) throws ServiceException, DatatypeConfigurationException {
		Page<TaskRiassegnazioneDto> page = null;
		List<TaskRiassegnazioneDto> dtos = new ArrayList<TaskRiassegnazioneDto>();
		Profilo profiloOriginario = profiloService.findOneBase(idProfiloOriginario);
		Pageable paginazione = PaginationUtil.generatePageRequest(offset, limit, new Sort(Sort.Direction.DESC, "attoIdItinere"));
		Collection<Long> confRiassegnateIds = this.findConfIncaricoIdsByProfiloOrigineA(idProfiloOriginario);
		Page<ConfigurazioneIncaricoProfiloDto> confs = configurazioneIncaricoProfiloService.findByProfiloId(idProfiloOriginario, true, paginazione, confRiassegnateIds, true);
		if(confs!=null) {
			for(ConfigurazioneIncaricoProfiloDto incarico : confs) {
				if(incarico!=null) {
					ConfigurazioneIncaricoDto confIncarico = configurazioneIncaricoService.get(incarico.getIdConfigurazioneIncarico());
					ConfigurazioneTaskDto confTask = configurazioneTaskService.get(confIncarico.getIdConfigurazioneTask());
//					TaskRiassegnazione riassegnazionePreesistente = this.findByProfiloOrigineAndConfIncaricoId(idProfiloOriginario, confIncarico.getId());
					
					Atto atto = attoService.findOneSimple(confIncarico.getIdAtto());
					TaskRiassegnazioneDto dto = new TaskRiassegnazioneDto();
					dto.setAttoId(confIncarico.getIdAtto());
					dto.setCodiceAtto(atto.getCodiceCifra() + (atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty() ? " - " + atto.getNumeroAdozione() : ""));
					dto.setOggettoAtto(atto.getOggetto());
					dto.setLavorazione(confIncarico.getConfigurazioneTaskNome());
					List<Profilo> profili = configurazioneTaskService.getCandidateProfList(confTask, atto.getAoo().getId(), profiloOriginario.getAoo().getId());
					if(profili!=null) {
						if(confIncarico.getListConfigurazioneIncaricoProfiloDto()!=null) {
							for(ConfigurazioneIncaricoProfiloDto cip : confIncarico.getListConfigurazioneIncaricoProfiloDto()) {
								if(cip.getProfilo()!=null) {
									profili.remove(cip.getProfilo());
								}
							}
						}
					}
					dto.setProfili(profili);
					dto.setAssignmentVar(confIncarico.getConfigurazioneTaskCodice());
					dto.setConfIncaricoId(incarico.getIdConfigurazioneIncarico());
//					if(riassegnazionePreesistente!=null) {
//						dto.setId(riassegnazionePreesistente.getId());
//						Profilo profiloNuovo = profiloService.findOneBase(riassegnazionePreesistente.getProfiloNuovo());
//						Profilo pMin = DomainUtil.minimalProfilo(profiloNuovo);
//						pMin.setHasQualifica(profiloNuovo.getHasQualifica());
//						dto.setProfiloNuovo(pMin);
//						QualificaProfessionale qualificaNuova = qualificaprofessionaleService.findOne(riassegnazionePreesistente.getQualificaNuova());
//						dto.setQualificaNuova(qualificaNuova);
//					}
					dtos.add(dto);
				}
			}
		}
		page = new PageImpl<TaskRiassegnazioneDto>(dtos, paginazione, confs.getTotalElements());
		return page;
	}
		
	@Transactional(readOnly = true)
	public Page<TaskRiassegnazioneDto> findTaskDaRilasciare(Long idProfiloOriginario, Integer offset, Integer limit) throws ServiceException, DatatypeConfigurationException {
		Page<TaskRiassegnazioneDto> page = null;
		List<TaskRiassegnazioneDto> dtos = new ArrayList<TaskRiassegnazioneDto>();
		Pageable paginazione = PaginationUtil.generatePageRequest(offset, limit);
		
		//lista di task in carico al profilo, ma per cui è prevista una coda in cui rilasciare il task
		JsonObject criteriJson = new JsonObject();
		criteriJson.addProperty("onlyHavingCandidateGroup", true);
		criteriJson.addProperty("assegnatarioWithProf", bpmWrapperUtil.getUsernameForBpm(idProfiloOriginario, null));
		Page<TaskDesktopDTO> inCaricoRilasciabili = workflowService.getAllTaskAssegnati(paginazione, criteriJson);
		if(inCaricoRilasciabili!=null) {
			for(TaskDesktopDTO task : inCaricoRilasciabili) {
				if(task!=null) {
					TaskRiassegnazioneDto dto = new TaskRiassegnazioneDto();
					
//					TaskRiassegnazione riassegnazionePreesistente = this.findByProfiloOrigine(idProfiloOriginario, task.getAtto().getId(), "AssignmentVar");
					dto.setAttoId(task.getAtto().getId());
					dto.setCodiceAtto(task.getAtto().getCodiceCifra() + (task.getAtto().getNumeroAdozione()!=null && !task.getAtto().getNumeroAdozione().isEmpty() ? " - " + task.getAtto().getNumeroAdozione() : ""));
					dto.setLavorazione(task.getTaskBpm().getNomeVisualizzato());
					dto.setOggettoAtto(task.getAtto().getOggetto());
					dto.setTaskId(task.getTaskBpm().getId());
					
//					if(riassegnazionePreesistente!=null) {
//						dto.setId(riassegnazionePreesistente.getId());
//						Profilo profiloNuovo = profiloService.findOneBase(riassegnazionePreesistente.getProfiloNuovo());
//						Profilo pMin = DomainUtil.minimalProfilo(profiloNuovo);
//						pMin.setHasQualifica(profiloNuovo.getHasQualifica());
//						dto.setProfiloNuovo(pMin);
//						QualificaProfessionale qualificaNuova = qualificaprofessionaleService.findOne(riassegnazionePreesistente.getQualificaNuova());
//						dto.setQualificaNuova(qualificaNuova);
//					}
					dtos.add(dto);
				}
			}
		}
		page = new PageImpl<TaskRiassegnazioneDto>(dtos, paginazione, inCaricoRilasciabili.getTotalElements());
		return page;
	}
		
	@Transactional(readOnly=true)
	public Collection<Long> findAttoIdsByProfiloOrigineIstruttore(Long profiloOrigine) throws ServiceException {
		Collection<Long> attoIds = new ArrayList<Long>();
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOrigine);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.isNull());
		Iterable<TaskRiassegnazione> it = taskRiassegnazioneRepository.findAll(p);
		for(TaskRiassegnazione tr : it) {
			attoIds.add(tr.getAttoId());
		}
		return attoIds;
	}
	
	@Transactional(readOnly=true)
	public Collection<Long> findConfIncaricoIdsByProfiloOrigineA(Long profiloOrigine) throws ServiceException {
		Collection<Long> confIncaricoIds = new ArrayList<Long>();
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOrigine);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.isNotNull());
		Iterable<TaskRiassegnazione> it = taskRiassegnazioneRepository.findAll(p);
		for(TaskRiassegnazione tr : it) {
			confIncaricoIds.add(tr.getConfIncaricoId());
		}
		return confIncaricoIds;
	}
	
	@Transactional(readOnly=true)
	public TaskRiassegnazione findByProfiloOrigine(Long profiloOrigine, Long attoId, String assignmentVar) throws ServiceException {
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOrigine);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.attoId.eq(attoId));
		if(assignmentVar!=null && !assignmentVar.trim().isEmpty()) {
			ConfigurazioneTaskDto ci = configurazioneTaskService.findByProcessVarName(assignmentVar);
			p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.eq(ci.getIdConfigurazioneTask()));
		}
		return taskRiassegnazioneRepository.findOne(p);
	}
	
	@Transactional(readOnly=true)
	public TaskRiassegnazione findByProfiloOrigineAndConfIncaricoId(Long profiloOrigine, Long confIncaricoId) {
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOrigine);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.eq(confIncaricoId));
		return taskRiassegnazioneRepository.findOne(p);
	}
	
	@Transactional(readOnly=true)
	public List<TaskRiassegnazione> findByProfiloOrigine(Long profiloOrigine) {
		List<TaskRiassegnazione> list = Lists.newArrayList(taskRiassegnazioneRepository.findAll(QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOrigine)));
		return list;
	}
	
	@Transactional(readOnly=true)
	public TaskRiassegnazione findOne(Long id) {
		return taskRiassegnazioneRepository.findOne(id);
	}
	
	@Transactional(readOnly=true)
	public List<String> getReassignmentList(Long confIncaricoId, Long attoId) {
		List<String> list = new ArrayList<String>();
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.attoId.eq(attoId);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.eq(confIncaricoId));
		Iterable<TaskRiassegnazione> it = taskRiassegnazioneRepository.findAll(p);
		if(it!=null) {
			for(TaskRiassegnazione r : it) {
				String assignment = bpmWrapperUtil.getUsernameForBpm(r.getProfiloNuovo(), r.getConfIncaricoId());
				list.add(assignment);
			}
		}
		return list;
	}
	
	@Transactional(readOnly=true)
	public void removeReassignment(Long confIncaricoId, Long attoId) {
		BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.attoId.eq(attoId);
		p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.eq(confIncaricoId));
		Iterable<TaskRiassegnazione> it = taskRiassegnazioneRepository.findAll(p);
		if(it!=null && it.iterator().hasNext()) {
			taskRiassegnazioneRepository.delete(it);
		}
	}
	
	@Transactional(readOnly=true)
	public String getReassignmentValue(String taskId, String currentAssignment, Long attoId) {
		String reassignmentValue = null;
		if(currentAssignment!=null && !currentAssignment.trim().isEmpty()) {
			long profiloOriginario = bpmWrapperUtil.getIdProfiloByUsernameBpm(currentAssignment);
			long configurazioneIncarico = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(currentAssignment);
			boolean taskHasGroup = bpmWrapperUtil.taskHasGroup(taskId);
			BooleanExpression p = QTaskRiassegnazione.taskRiassegnazione.profiloOrigine.eq(profiloOriginario);
			p = p.and(QTaskRiassegnazione.taskRiassegnazione.attoId.eq(attoId));
			if(configurazioneIncarico > 0L) {
				p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.eq(configurazioneIncarico));
			}else if(!taskHasGroup){
				p = p.and(QTaskRiassegnazione.taskRiassegnazione.confIncaricoId.isNull());
			}else {
				p = QTaskRiassegnazione.taskRiassegnazione.id.isNull();
			}
			TaskRiassegnazione tr = taskRiassegnazioneRepository.findOne(p);
			if(tr!=null) {
				String currentUser = bpmWrapperUtil.getUsernameByUsernameBpm(currentAssignment);
				String newUsername = profiloService.getUsernameByProfiloId(tr.getProfiloNuovo());
				reassignmentValue = currentAssignment.replace(currentUser + Constants.BPM_USERNAME_SEPARATOR + profiloOriginario, newUsername + Constants.BPM_USERNAME_SEPARATOR + tr.getProfiloNuovo());
			}
		}
		return reassignmentValue;
	}
	
	@Transactional
	public TaskRiassegnazione save(TaskRiassegnazioneDto riassegnazioneDto, Long idProfiloEsecutoreRiassegnazione) throws ServiceException {
		TaskRiassegnazione entity = new TaskRiassegnazione();
		entity.setAttoId(riassegnazioneDto.getAttoId());
		entity.setConfIncaricoId(riassegnazioneDto.getConfIncaricoId());
		entity.setProfiloNuovo(riassegnazioneDto.getProfiloNuovo());
		entity.setProfiloOrigine(riassegnazioneDto.getProfiloOrigine());
		entity.setQualificaNuova(riassegnazioneDto.getQualificaNuova());
		
		//aggiornamento configurazione incarico
		if(entity.getConfIncaricoId()!=null) {
			Long oldQualificaId = configurazioneIncaricoService.updateConfIncaricoProfilo(entity.getConfIncaricoId(), entity.getProfiloOrigine(), entity.getProfiloNuovo(), entity.getQualificaNuova(), true);
			entity.setQualificaOrigine(oldQualificaId);
		}
		
		entity = taskRiassegnazioneRepository.save(entity);
		
		ConfigurazioneTask configurazioneTask = null;
		if(entity.getConfIncaricoId()!=null) {
			configurazioneTask = configurazioneIncaricoService.getConfTaskByConfIncaricoId(entity.getConfIncaricoId());
		}else {
			configurazioneTask = configurazioneTaskService.findEntityByCodice(CODICE_CONF_TASTK_ISTRUTTORIA);
		}
		
		String newUsername = profiloService.getUsernameByProfiloId(entity.getProfiloNuovo());
		String currentUser = profiloService.getUsernameByProfiloId(entity.getProfiloOrigine());
		
		String nominativoOrigine = utenteService.getNomeLeggibileByUsername(currentUser);
		String nominativoNew = utenteService.getNomeLeggibileByUsername(newUsername);
		
		//salvataggio log avanzamento
		String note = "Vecchio assegnatario\u003A " + nominativoOrigine + ", Nuovo assegnatario\u003A " + nominativoNew;
		registrazioneAvanzamentoService.registraAvanzamento(entity.getAttoId(), idProfiloEsecutoreRiassegnazione, "Riassegnazione incarico di " + configurazioneTask.getNome(), note);
				
		String currentAssignment = bpmWrapperUtil.getUsernameForBpm(entity.getProfiloOrigine(), entity.getConfIncaricoId());
		String newAssignment = bpmWrapperUtil.getUsernameForBpm(entity.getProfiloNuovo(), entity.getConfIncaricoId());
		bpmWrapperUtil.checkTasksAssignmentAndReasignee(currentAssignment, newAssignment, entity.getAttoId(), entity.getConfIncaricoId() == null);

		return entity;
	}
	
	@Transactional
	public void delete(Long id) {
		taskRiassegnazioneRepository.delete(id);
	}
	
	@Transactional(readOnly = true)
	public TaskRiassegnazioneDto findTaskIstruttore(Long idProfilo, Long attoId) throws ServiceException, DatatypeConfigurationException {
		Profilo profiloOriginario = profiloService.findOneBase(idProfilo);
		TaskRiassegnazioneDto dto = null;
		Atto atto = attoService.findListAttoItinereByIstruttore(idProfilo, attoId);
		ConfigurazioneTaskDto confTaskIstruttoria = configurazioneTaskService.findByCodice(CODICE_CONF_TASTK_ISTRUTTORIA);
		if(atto!=null) {
			List<Profilo> profili = configurazioneTaskService.getCandidateProfList(confTaskIstruttoria, atto.getAoo().getId(), profiloOriginario.getAoo().getId());
			dto = new TaskRiassegnazioneDto();
			if(profili!=null) {
				Profilo excludeProf = null;
				for(Profilo p : profili) {
					if(p!=null && p.getId().equals(idProfilo)) {
						excludeProf = p;
						break;
					}
				}
				if(excludeProf!=null) {
					profili.remove(excludeProf);
				}
			}
			dto.setProfili(profili);
			dto.setAttoId(atto.getId());
			dto.setCodiceAtto(atto.getCodiceCifra() + (atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty() ? " - " + atto.getNumeroAdozione() : ""));
			dto.setLavorazione("Istruttoria");
			dto.setOggettoAtto(atto.getOggetto());
		}
		return dto;
	}
	
	@Transactional(readOnly = true)
	public TaskRiassegnazioneDto findTaskRiassignee(Long idProfilo, Long confIncaricoId) throws ServiceException, DatatypeConfigurationException {
		TaskRiassegnazioneDto dto = null;
		Profilo profiloOriginario = profiloService.findOneBase(idProfilo);
		ConfigurazioneIncaricoProfiloDto incarico = configurazioneIncaricoProfiloService.findByProfiloIdConfIncaricoId(idProfilo, confIncaricoId, true);
		if(incarico!=null) {
			ConfigurazioneIncaricoDto confIncarico = configurazioneIncaricoService.get(incarico.getIdConfigurazioneIncarico());
			ConfigurazioneTaskDto confTask = configurazioneTaskService.get(confIncarico.getIdConfigurazioneTask());
			
			Atto atto = attoService.findOneSimple(confIncarico.getIdAtto());
			dto = new TaskRiassegnazioneDto();
			dto.setAttoId(confIncarico.getIdAtto());
			dto.setCodiceAtto(atto.getCodiceCifra() + (atto.getNumeroAdozione()!=null && !atto.getNumeroAdozione().isEmpty() ? " - " + atto.getNumeroAdozione() : ""));
			dto.setOggettoAtto(atto.getOggetto());
			dto.setLavorazione(confIncarico.getConfigurazioneTaskNome());
			List<Profilo> profili = configurazioneTaskService.getCandidateProfList(confTask, atto.getAoo().getId(), profiloOriginario.getAoo().getId());
			if(profili!=null) {
				if(confIncarico.getListConfigurazioneIncaricoProfiloDto()!=null) {
					for(ConfigurazioneIncaricoProfiloDto cip : confIncarico.getListConfigurazioneIncaricoProfiloDto()) {
						if(cip.getProfilo()!=null) {
							profili.remove(cip.getProfilo());
						}
					}
				}
			}
			dto.setProfili(profili);
			dto.setAssignmentVar(confIncarico.getConfigurazioneTaskCodice());
			dto.setConfIncaricoId(incarico.getIdConfigurazioneIncarico());
		}
		return dto;
	}
	
}
