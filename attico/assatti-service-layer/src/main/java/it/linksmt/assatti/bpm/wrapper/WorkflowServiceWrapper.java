package it.linksmt.assatti.bpm.wrapper;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.xml.datatype.DatatypeConfigurationException;

import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.impl.persistence.entity.ExecutionEntity;
import org.camunda.bpm.engine.impl.persistence.entity.IdentityLinkEntity;
import org.camunda.bpm.engine.impl.persistence.entity.VariableInstanceEntity;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.camunda.bpm.engine.runtime.NativeExecutionQuery;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.runtime.VariableInstance;
import org.camunda.bpm.engine.runtime.VariableInstanceQuery;
import org.camunda.bpm.engine.task.IdentityLink;
import org.camunda.bpm.engine.task.IdentityLinkType;
import org.camunda.bpm.engine.task.NativeTaskQuery;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import it.linksmt.assatti.bpm.dto.AssegnazioneIncaricoDTO;
import it.linksmt.assatti.bpm.dto.DecisioneWorkflowDTO;
import it.linksmt.assatti.bpm.dto.StatoAttoDTO;
import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.bpm.util.AttoProcessVariables;
import it.linksmt.assatti.bpm.util.BpmThreadLocalUtil;
import it.linksmt.assatti.bpm.util.JsonUtil;
import it.linksmt.assatti.bpm.util.MessageKeys;
import it.linksmt.assatti.bpm.util.NomiAttivitaAtto;
import it.linksmt.assatti.bpm.util.StatoAttoConverter;
import it.linksmt.assatti.bpm.util.TipoTaskConverter;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.cooperation.service.contabilita.ContabilitaService;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfiloId;
import it.linksmt.assatti.datalayer.domain.Delega;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.domain.RiassegnazioneIncarico;
import it.linksmt.assatti.datalayer.domain.SedutaGiuntaConstants;
import it.linksmt.assatti.datalayer.domain.TipoAtto;
import it.linksmt.assatti.datalayer.domain.TipoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoRuoloEnum;
import it.linksmt.assatti.datalayer.domain.TipologiaRicercaCodeEnum;
import it.linksmt.assatti.datalayer.domain.UfficioFilterEnum;
import it.linksmt.assatti.datalayer.domain.dto.AooDto;
import it.linksmt.assatti.datalayer.domain.dto.DocumentoPdfDto;
import it.linksmt.assatti.datalayer.domain.util.AooTransformer;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoProfiloRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.DelegaRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.datalayer.repository.RiassegnazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.TipoAttoRepository;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ProfiloService;
import it.linksmt.assatti.service.TipoDocumentoService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.converter.DocumentoPdfConverter;
import it.linksmt.assatti.service.converter.TipoDocumentoConverter;
import it.linksmt.assatti.service.dto.AttoWorkflowDTO;
import it.linksmt.assatti.service.dto.ModelloHtmlDto;
import it.linksmt.assatti.service.dto.ResocontoDTO;
import it.linksmt.assatti.service.dto.TaskDesktopDTO;
import it.linksmt.assatti.service.dto.TaskDesktopGroupDTO;
import it.linksmt.assatti.service.dto.TipoDocumentoDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.exception.LiquidazioniEmptyException;
import it.linksmt.assatti.service.exception.MovimentiPresentiException;
import it.linksmt.assatti.service.util.PaginationUtil;
import it.linksmt.assatti.service.util.TaskDesktopEnum;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.DataSourceProps;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

@Service
public class WorkflowServiceWrapper {
	
	private final Logger log = LoggerFactory.getLogger(WorkflowServiceWrapper.class);
	
	/* SPRING COMPONENTS - DB ATTI */		
	@Inject
	private AooService aooService;
	
	@PersistenceContext
	private EntityManager em;
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private TipoAttoRepository tipoAttoRepository;
	
	@Inject
	private TipoTaskConverter tipoTaskConverter;
	
	@Inject
	private StatoAttoConverter statoAttoConverter;
	
	@Inject
	private RegistrazioneAvanzamentoService registrazioneAvanzamentoService;
	
	@Inject
	private TipoDocumentoService tipoDocumentoService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private DelegaRepository delegaRepository;
	
	@Inject
	private ConfigurazioneIncaricoRepository configurazioneIncaricoRepository;
	
	@Inject
	private ConfigurazioneIncaricoProfiloRepository configurazioneIncaricoProfiloRepository;
	
	@Inject
	private ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	private RiassegnazioneIncaricoRepository riassegnazioneIncaricoRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private QualificaProfessionaleRepository qualificaProfessionaleRepository;
	
	@Inject
	private ContabilitaService contabilitaService;
	
	/* SPRING COMPONENTS - CAMUNDA */
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private TaskService taskService;
	
	@Inject
	private RuntimeService runtimeService;
	
	@Inject
	private ManagementService managementService;
	
	@Inject
	private AttoService attoService;
	
	@Inject
	private UtenteService utenteService;
	
	@Inject
	private ProfiloService profiloService;
	
	private static String PREFISSO_DELEGATO_DEFAULT = "Delegato per:";
	

	/**
	 * @param atto
	 * @param profiloId
	 * @return
	 */
	public String avviaProcessoBpm(final Atto atto, final Long profiloId) {		
		// Setto Threadlocal con Id Profilo per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
				
		if (atto.getId() == null || atto.getId().longValue() < 1) {
			throw new IllegalArgumentException("Id Atto nullo. Impossibile avviare una istanza di processo.");
		}
		if (profiloId == null || profiloId.longValue() < 1) {
			throw new IllegalArgumentException("Id Profilo istruttore atto non specificato. Impossibile avviare una istanza di processo.");
		}
		
		Map<String, Object> variables = bpmWrapperUtil.fillVariabiliAvvioProcesso(atto, profiloId);

		TipoAtto tipoAtto = tipoAttoRepository.findOne(atto.getTipoAtto().getId());
		
		log.info("AVVIO PROCESSO: Id Atto=" + String.valueOf(atto.getId()) + 
				" - " + tipoAtto.getProcessoBpmName());
		
		ProcessInstance instance = runtimeService.startProcessInstanceByKey(
				tipoAtto.getProcessoBpmName(), String.valueOf(atto.getId()), variables);

		return instance.getProcessInstanceId();
	}
	
	public String avviaProcessoBpmSecondario(final Atto atto, String processDefinitionKey,
			Map<String, Object> additionalVariables) {
		Map<String, Object> variables = bpmWrapperUtil.fillVariabiliAvvioProcesso(atto, -1);
		
		if (additionalVariables != null) {
			variables.putAll(additionalVariables);
		}
		
		ProcessInstance instance = runtimeService.startProcessInstanceByKey( 
				processDefinitionKey, String.valueOf(atto.getId()), variables);
		
		return instance.getProcessInstanceId();
	}
	
	public void verifichePreliminariPreCompleteTask(final String taskId,
			final Long profiloId,
			final String taskBpmId,
			final Long sedutaId,
			AttoWorkflowDTO dto) throws GestattiCatchedException{
		if(dto!=null && dto.getDecisione()!=null && dto.getDecisione().getCodiceAzioneUtente()!=null && 
				(dto.getDecisione().getCodiceAzioneUtente().equalsIgnoreCase("RITIRO_FASE_ISTRUTTORIA")
				|| dto.getDecisione().getCodiceAzioneUtente().equalsIgnoreCase("EDIT_ITER_DD_TO_SENZA_VERIFICA")
				)) {
			List<MovimentoContabileDto> list = null;
			try {
				String codiceTipoAtto = dto.getAtto().getTipoAtto().getCodice();
				String numeroRif = dto.getAtto().getCodiceCifra().substring(dto.getAtto().getCodiceCifra().length() - 5);
				int annoRif = dto.getAtto().getDataCreazione().getYear();
				boolean isBozza = true;
				boolean esisteBozza = contabilitaService.esisteBozzaAtto(codiceTipoAtto, numeroRif, annoRif, isBozza);
				if(esisteBozza) {
					list = contabilitaService.getMovimentiContabili(codiceTipoAtto, numeroRif, annoRif, isBozza);
				}
			}catch(LiquidazioniEmptyException e) {
				list = new ArrayList<MovimentoContabileDto>();
			} catch (Exception e) {
				throw new GestattiCatchedException(e);
			}
			
			if(list!=null && list.size() > 0) {
				String op = "Attenzione. Occorre rimuovere i movimenti da JEnte prima di poter procedere con l'operazione";
				if(dto.getDecisione().getCodiceAzioneUtente().equalsIgnoreCase("RITIRO_FASE_ISTRUTTORIA")) {
					op = "Attenzione. Occorre rimuovere i movimenti da JEnte prima di poter ritirare l'atto";
				}else if(dto.getDecisione().getCodiceAzioneUtente().equalsIgnoreCase("EDIT_ITER_DD_TO_SENZA_VERIFICA")) {
					op = "ATTENZIONE!!! Occorre ELIMINARE MANUALMENTE I MOVIMENTI DAL SISTEMA CONTABILE prima di poter procedere con la trasformazione dell'iter burocratico di questo atto";
				}
				throw new MovimentiPresentiException(op);
			}
		}
	}
	
	/**
	 * @param attoId
	 * @param profiloId
	 * @return
	 */
	@Transactional
	public String getMyNextTaskId(final Long attoId, final Long profiloId) {

		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
		
		// Task che dipendono e che non dipendono anche dalla qualifica
		TaskQuery taskQuery = taskService.createTaskQuery()
				.or().taskAssignee(usernameBpm).taskAssigneeLike(usernameBpm +
						Constants.BPM_INCARICO_SEPARATOR + "%").endOr()
				.processInstanceBusinessKey(String.valueOf(attoId)).active();
				
		List<Task> list = taskQuery.list();
		
		String taskId = "";
		if ((list != null) && (list.size() == 1)) {
			taskId = list.get(0).getId();				
		}
		log.debug("My taskId: " + taskId);
		return taskId;
	}
	
	@Transactional
	public Boolean isTaskAssigneedToProfile(final String taskId, final Long profiloId) {
		if(taskId!=null && !taskId.isEmpty() && profiloId!=null) {
			String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
			
			// Task che dipendono e che non dipendono anche dalla qualifica
			TaskQuery taskQuery = taskService.createTaskQuery()
					.or().taskAssignee(usernameBpm).taskAssigneeLike(usernameBpm +
							Constants.BPM_INCARICO_SEPARATOR + "%").endOr()
					.taskId(taskId).active();
					
			List<Task> list = taskQuery.list();
			
			return list != null && list.size() > 0;
		}else {
			return false;
		}
	}
		
	
	/**
	 * @param attoId
	 * @return
	 */
	public String getNextTaskId(final Long attoId, boolean onlyActive) {
		
		TaskQuery query = taskService.createTaskQuery()
				.processInstanceBusinessKey(String.valueOf(attoId));
		
		if (onlyActive) {
			query = query.active();
		}
		
		List<Task> list = query.list();
		
		String taskId = "";
		if ((list != null) && (list.size() == 1)) {
			taskId = list.get(0).getId();				
		}
		log.debug("Next taskId: " + taskId);
		return taskId;
	}
	
	
	public List<StatoAttoDTO> getStatoAtto(Atto atto) {
				
		// Caso 1: il processo è in attesa di un messaggio
		List<ProcessInstance> instList = runtimeService.createProcessInstanceQuery()
				.processInstanceBusinessKey(String.valueOf(atto.getId().longValue())).active().list();
		
		List<EventSubscription> allWaiting = new ArrayList<EventSubscription>();
		for (ProcessInstance processInstance : instList) {
			List<EventSubscription> waitEvents = runtimeService.createEventSubscriptionQuery()
					.processInstanceId(processInstance.getProcessInstanceId())
					.eventType("message").list();
							
			if ((waitEvents != null) && (!waitEvents.isEmpty())) {
				allWaiting.addAll(waitEvents);
			}
		}
		
		// Caso 2: sono presenti task in attesa sul processo
		List<Task> list = taskService.createTaskQuery()
				.processInstanceBusinessKey(
						String.valueOf(atto.getId().longValue()))
				.active().list();
		
		return statoAttoConverter.toStatoAtto(atto, list, allWaiting);
	}
	
	/**
	 * @param profili
	 * @return
	 */
	public List<Task> getTaskAssegnati(final Iterable<Profilo> profili, 
			String azioneDaEffettuare, String assegnatario,
			Date createdAfter, Date createdBefore, String taskId, UfficioFilterEnum ufficioFilter) {
		return getTaskAssegnati(profili, azioneDaEffettuare, assegnatario, 
				createdAfter, createdBefore, taskId, null, null, ufficioFilter);
	}
	
	public boolean existsActiveTask(String taskId) {
		TaskQuery query = taskService.createTaskQuery();
		long count = query.active().taskId(taskId).taskAssigned().count();
		return count > 0;
	}
	
	public Set<Long> getAttoIdsTaskItinere(final JsonObject criteriJson) throws DatatypeConfigurationException{
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		
		String sql = " FROM " + managementService.getTableName(Task.class) + " T ";
		sql += " INNER JOIN " + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id";
		sql += " where 1 = 1";
		
		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			sql += " AND TIPOATTO.codice = '"+StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim())+"'";
		}
		
		int i = -1;
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "data_task_start")) {
			i++;
			sql += " AND T.CREATE_TIME_ >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("data_task_start").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "data_task_end")) {
			i++;
			sql += " AND T.CREATE_TIME_ <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("data_task_end").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilterArray(criteriJson, "assegnatari")) {
			sql += " AND (";
			String subSql = "";
			for(JsonElement assegnatarioJson : criteriJson.get("assegnatari").getAsJsonArray()) {
				if(!subSql.isEmpty()) {
					subSql += " OR ";
				}
				i++;
				subSql += "(substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) LIKE #{variableValue"+i+"})";
				query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(assegnatarioJson.getAsJsonPrimitive().getAsString().trim())+"%");
			}
			sql += subSql + ")";
		}
		
		if(JsonUtil.hasFilter(criteriJson, "deleganteId")) {
			i++;
			sql += " AND SUBSTRING_INDEX(SUBSTRING_INDEX(T.ASSIGNEE_, '"+Constants.BPM_USERNAME_SEPARATOR+"', -1), '_', 1) = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("deleganteId").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			i++;
			sql += " AND LOWER(T.NAME_) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
		}
		
		query = query.sql("SELECT T.*" + sql);
		
		List<Task> tasks = query.list();
		Set<Long> attoIds = new HashSet<Long>();
		if(tasks!=null && tasks.size() > 0) {
			for(Task task : tasks) {
				Long attoId = Long.parseLong(this.getBusinessKey(task.getProcessInstanceId()));
				attoIds.add(attoId);
			}
		}

		return attoIds;
	}
	
	public Page<TaskDesktopDTO> getAllTaskAssegnati(final Pageable paginazione, final JsonObject criteriJson) throws DatatypeConfigurationException{
		Page<TaskDesktopDTO> foundPage = null;
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		
		String sql = " FROM " + managementService.getTableName(Task.class) + " T ";
		sql += " INNER JOIN " + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id";
		if(JsonUtil.hasFilter(criteriJson, "onlyHavingCandidateGroup")) {
			sql += " INNER JOIN " + managementService.getTableName(IdentityLinkEntity.class) + " IDE on IDE.TASK_ID_ = T.ID_";
		}
		sql += " where T.ASSIGNEE_ is not null";
		
		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			sql += " AND TIPOATTO.codice = '"+StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim())+"'";
		}
		
		int i = -1;
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "assegnatario")) {
			i++;
			sql += " AND substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("assegnatario").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "assegnatarioWithProf")) {
			i++;
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("assegnatarioWithProf").getAsString().trim()));
			sql += " AND (T.ASSIGNEE_ = #{variableValue"+i+"} or T.ASSIGNEE_ LIKE #{variableValue"+(++i)+"})";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("assegnatarioWithProf").getAsString().trim()+"_%"));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "deleganteId")) {
			i++;
			sql += " AND SUBSTRING_INDEX(SUBSTRING_INDEX(T.ASSIGNEE_, '"+Constants.BPM_USERNAME_SEPARATOR+"', -1), '_', 1) = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("deleganteId").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			i++;
			sql += " AND LOWER(T.NAME_) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
		}
		
		long totalElements = query.sql("SELECT COUNT(distinct T.ID_)" + sql).count();
		
		sql += " order by T.CREATE_TIME_ desc";
		
		query = query.sql("SELECT T.*" + sql);
		
		List<Task> tasks = null;
		if(paginazione!=null) {
			tasks = query.listPage(paginazione.getOffset(), paginazione.getPageSize());
		}else {
			tasks = query.list();
		}
		
		List<TaskDesktopDTO> lista = null;
		
		for(Task task : tasks) {
			String businessKey = this.getBusinessKey(task.getProcessInstanceId());
			TaskDesktopDTO dto = new TaskDesktopDTO();
			TipoTaskDTO tipoTaskDTO = tipoTaskConverter.toTipoTask(task);
			if(dto.getTaskBpm()==null) {
				dto.setTaskBpm(new TipoTaskDTO());
			}
			dto.setTaskBpm(tipoTaskDTO);
			Profilo profilo = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee().trim());
			if(profilo!=null) {
				dto.getTaskBpm().setUfficioGiacenza("(" + profilo.getAoo().getCodice() + ") " + profilo.getAoo().getDescrizione());
				if(profilo.getValidita()!=null && profilo.getValidita().getValidoal() != null) {
					dto.setHighLighted(true);
				}
			}
			dto.setAtto(attoService.findOneScrivaniaBasic(Long.parseLong(businessKey)));
			if(lista==null) {
				lista = new ArrayList<TaskDesktopDTO>();
			}
			lista.add(dto);
		}
		
		if(totalElements > 0 && paginazione==null){
			foundPage = new PageImpl<TaskDesktopDTO>(lista);
		}
		else if(totalElements > 0){			
			foundPage = new PageImpl<TaskDesktopDTO>(lista, paginazione, totalElements);
		}
		else{
			foundPage = new PageImpl<TaskDesktopDTO>(new ArrayList<TaskDesktopDTO>());
		}
		

		return foundPage;
	}
	
	public List<Task> getTaskByIdAtto(Long idAtto){
		List<Task> retVal = new ArrayList<Task>();
		
		
		/*
		 * SELECT DISTINCT T.* FROM camunda78.act_ru_task T
where
T.PROC_INST_ID_ in (select id_ from camunda78.act_ru_execution where BUSINESS_KEY_ = 545)
ORDER BY T.CREATE_TIME_ desc;
		 */
		
		
		String idExecution = null;
		
		ExecutionQuery queryExecution = runtimeService.createExecutionQuery();
		
		List<Execution> processi = queryExecution.processInstanceBusinessKey(String.valueOf(idAtto)).active().list();
		
		if(processi!=null) {
			for (Iterator<Execution> iterator = processi.iterator(); iterator.hasNext();) {
				Execution execution = (Execution) iterator.next();
				
				if(idExecution==null) {
					idExecution="'"+execution.getId()+"'";
				}else {
					idExecution+=(",'"+execution.getId()+"'");
				}
				
			}
		}
		
		String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T where T.PROC_INST_ID_ in ("+idExecution+")";
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		
		query = query.sql(sql);
		
		retVal = query.list();

		return retVal;
	}
	
	public long countTaskInCarico(final Profilo profilo) {
		long count = 0L;
		
		if (profilo!=null && profilo.getId()!=null) {
			String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profilo, null);
			NativeTaskQuery query = taskService.createNativeTaskQuery();
			
			String sql = "SELECT COUNT(distinct T.ID_) FROM " + managementService.getTableName(Task.class) + " T WHERE T.ASSIGNEE_ IS NOT NULL";
			sql += " AND (T.ASSIGNEE_ LIKE #{assignee} or T.ASSIGNEE_ LIKE #{assigneeLike})";
			query = query.parameter("assignee", usernameBpm);
			query = query.parameter("assigneeLike", usernameBpm + Constants.BPM_INCARICO_SEPARATOR + "%");
			
			count = query.sql(sql).count();
		}
		return count;
	}
	
	public List<Task> getTaskAssegnati(final Iterable<Profilo> profili, 
			String azioneDaEffettuare, String assegnatario,
			Date createdAfter, Date createdBefore, String taskId,
			String taskVariable, String taskVariableText, UfficioFilterEnum ufficioFilter) {
		
		List<Task> retVal = new ArrayList<Task>();
		boolean searchVar = false;
		
		if ((profili != null) && (!Iterables.isEmpty(profili)) ) {
			NativeTaskQuery query = taskService.createNativeTaskQuery();
			
			String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T ";
			sql += " INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_";
			sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ ";
			
			if (!StringUtil.isNull(taskVariable) && (taskVariableText != null)) {
				searchVar = true;
				
				sql += " JOIN " + managementService.getTableName(VariableInstanceEntity.class) + " VAR on VAR.EXECUTION_ID_ = T.EXECUTION_ID_ ";
			}
			
			sql += "WHERE T.ASSIGNEE_ IS NOT NULL ";
			
			if (!StringUtil.isNull(taskId)) {
				sql += " AND T.TASK_DEF_KEY_ LIKE '"+taskId+"'";
			}
			
			if (!StringUtil.isNull(assegnatario)) {
				sql += " AND T.ASSIGNEE_ LIKE #{assigneeLike} ";
				query = query.parameter("assigneeLike", "%"+assegnatario+"%");
			}
			
			if(profili!=null){
				int cntProf = 0;
				for (Profilo profilo : profili) {
					if (cntProf == 0) {
						sql += " AND (";
					}
					else {
						sql += " OR ";
					}
					
					String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profilo, null);
					
					cntProf++;
					query = query.parameter("assigneeLike" + cntProf, usernameBpm);
					sql += " T.ASSIGNEE_ LIKE #{assigneeLike" + cntProf + "}";
					
					cntProf++;
					sql += " OR T.ASSIGNEE_ LIKE #{assigneeLike" + cntProf + "}";
					query = query.parameter("assigneeLike" + cntProf, usernameBpm + Constants.BPM_INCARICO_SEPARATOR + "%");
				}
				
				if (cntProf > 0) {
					sql += ") ";
				}
			}
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				sql += " AND LOWER(T.NAME_) LIKE #{nameLike} ";
			}
			if (createdAfter != null) {
				sql += " AND T.CREATE_TIME_ >= #{createdAfter} ";
			}
			if (createdBefore != null) {
				sql += " AND T.CREATE_TIME_ <= #{createdBefore} ";
			}
			
			if (searchVar) {
				sql += " AND VAR.NAME_ LIKE #{variableName} AND VAR.TEXT_ LIKE #{variableValue} ";
			}
			
			if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
				if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
					sql += " AND ATTO.aoo_id is null";
				}
			}
			
			sql += " ORDER BY T.CREATE_TIME_ DESC ";
			
			// log.info("NATIVE QUERY: " + sql);
			query = query.sql(sql);
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				query = query.parameter("nameLike", "%" + azioneDaEffettuare.toLowerCase() + "%");
			}
			if (createdAfter != null) {
				createdAfter.setHours(0);
				createdAfter.setMinutes(0);
				createdAfter.setSeconds(0);
				query = query.parameter("createdAfter", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdAfter));
			}
			if (createdBefore != null) {
				createdBefore.setHours(23);
				createdBefore.setMinutes(59);
				createdBefore.setSeconds(59);
				query = query.parameter("createdBefore", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdBefore));
			}
			if (searchVar) {
				query = query.parameter("variableName",  taskVariable);
				query = query.parameter("variableValue", taskVariableText);
				
			}
			
			retVal = query.list();
		}
		return retVal;
	}
	
	public List<TaskDesktopGroupDTO> findRagioneriaGroup(JsonObject search, String taskType, final Iterable<Profilo> profili, final Integer firstPageSize, UfficioFilterEnum ufficioFilter) throws Exception {
		Pageable paginazione = PaginationUtil.generatePageRequest(1, firstPageSize);
		String query = this.getTaskInRagioneriaQuery();
		query = "select distinct AOOGIACENZA.id" + query;
		query = query.replace("#{varInRagioneria}", "'" + AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA + "'");
		List<Aoo> ufficiGiacenza = aooService.findAooByNativeQuery(query);
		List<AooDto> ufficiGiacenzaDto = AooTransformer.toDto(ufficiGiacenza);
		List<TaskDesktopGroupDTO> groups = new ArrayList<TaskDesktopGroupDTO>();
		boolean scadenzaSort = false;
		if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.ATTI_IN_RAGIONERIA_PER_SCADENZA.getDescrizione())) {
			scadenzaSort = true;
		}
		
		if(ufficiGiacenzaDto!=null) {
			if(search==null) {
				search = new JsonObject();
			}
			for(AooDto ufficio : ufficiGiacenzaDto) {
				if(ufficio!=null) {
					TaskDesktopGroupDTO group = new TaskDesktopGroupDTO();
					group.setUfficio(ufficio);
					search.addProperty("idUfficioGiacenza", ufficio.getId());
					group.setPage(this.getTaskInRagioneria(search, paginazione, scadenzaSort, ufficioFilter, profili));
					groups.add(group);
				}
			}
		}
		return groups;
	}
	
	public List<TaskDesktopGroupDTO> findMonitoraggioGroup(JsonObject search, String taskType, final Iterable<Profilo> profili, final Integer firstPageSize, UfficioFilterEnum ufficioFilter) throws Exception {
		Pageable paginazione = PaginationUtil.generatePageRequest(1, firstPageSize);
		String query = this.getTaskInRagioneriaQuery();
		query = "select distinct AOOGIACENZA.id" + query;
		query = query.replace("#{varInRagioneria}", "'" + AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA + "'");
		List<Aoo> ufficiGiacenza = aooService.findAooByNativeQuery(query);
		List<AooDto> ufficiGiacenzaDto = AooTransformer.toDto(ufficiGiacenza);
		List<TaskDesktopGroupDTO> groups = new ArrayList<TaskDesktopGroupDTO>();
		boolean scadenzaSort = false;
		if(StringUtil.trimStr(taskType).equalsIgnoreCase(TaskDesktopEnum.ATTI_IN_RAGIONERIA_PER_SCADENZA.getDescrizione())) {
			scadenzaSort = true;
		}
		
		if(ufficiGiacenzaDto!=null) {
			if(search==null) {
				search = new JsonObject();
			}
			for(AooDto ufficio : ufficiGiacenzaDto) {
				if(ufficio!=null) {
					TaskDesktopGroupDTO group = new TaskDesktopGroupDTO();
					group.setUfficio(ufficio);
					search.addProperty("idUfficioGiacenza", ufficio.getId());
					group.setPage(this.getTaskInRagioneria(search, paginazione, scadenzaSort, ufficioFilter, profili));
					groups.add(group);
				}
			}
		}
		return groups;
	}
	
	
	
	private String getTaskInRagioneriaQuery() {
		String sql = " FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(Task.class) + " T ";
		sql += " INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(VariableInstanceEntity.class) + " VAR on VAR.EXECUTION_ID_ = T.EXECUTION_ID_ AND VAR.NAME_ = #{varInRagioneria}";
		sql += " INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo AOO on AOO.id = ATTO.aoo_id";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".daticontabili DATI_CONTABILI on DATI_CONTABILI.atto_id = ATTO.id";
		sql += " LEFT OUTER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(IdentityLinkEntity.class) + " IDE on IDE.TASK_ID_ = T.ID_";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".utente ASSEGNATARIO on ASSEGNATARIO.username = (case when T.ASSIGNEE_ is not null then substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) else '' end)";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo AOOGIACENZA ON AOOGIACENZA.id = coalesce(";
		sql += "(" + 
				"SELECT " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo.aoo_id" + 
					" FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo" + 
				" where " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo.id = (" + 
					"SELECT " + 
						"Substring_index(" + 
						"Substring_index(T.ASSIGNEE_," + 
						"'_!U#_', -1), '_', 1)" +
					")" + 
				")";
		sql += ",";
		sql += "(SELECT distinct "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) +".aoo.id FROM "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) +".aoo WHERE " + 
				DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo.validoal is null and " + 
				DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo.codice in (SELECT " + 
				"Substring_index( " + 
					"Substring_index(IDE.group_id_, " + 
					"'_!R#_', -1), '_', 1)" + 
				"))";
		sql += ")";
		return sql;
	}
	
	private String getTaskRiassegnazioneQuery() {
		String sql = " FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(Task.class) + " T ";
		sql += " INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ and T.ASSIGNEE_ is not null";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".utente ASSEGNATARIO on ASSEGNATARIO.username = substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1)";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo PROFILO_ASSEGNATARIO on PROFILO_ASSEGNATARIO.id = (SELECT Substring_index(Substring_index(T.ASSIGNEE_,'_!U#_', -1), '_', 1))";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".grupporuolo GR_ASSEGNATARIO on GR_ASSEGNATARIO.id = PROFILO_ASSEGNATARIO.grupporuolo_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".ruolo_hasgruppi RGR_ASSEGNATARIO on RGR_ASSEGNATARIO.grupporuolo_id = GR_ASSEGNATARIO.id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".ruolo RUOLO_ASSEGNATARIO on RUOLO_ASSEGNATARIO.id = RGR_ASSEGNATARIO.ruolo_id AND RUOLO_ASSEGNATARIO.tipo = '"+TipoRuoloEnum.PROCESSO.toString()+"'";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo AOOGIACENZA ON AOOGIACENZA.id = ";
		sql += "(" + 
				"SELECT " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo.aoo_id" + 
					" FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo" + 
				" where " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo.id = (" + 
					"SELECT " + 
						"Substring_index(" + 
						"Substring_index(T.ASSIGNEE_," + 
						"'_!U#_', -1), '_', 1)" +
					")" + 
				")";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".aoo AOO on AOO.id = ATTO.aoo_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".avanzamento ULTIMA_LAVORAZIONE on ULTIMA_LAVORAZIONE.id = (select id from "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".avanzamento where atto_id = ATTO.id order by data_attivita desc limit 1)";
		return sql;
	}
	
	@Transactional
	public Page<TaskDesktopDTO> getTaskRiassegnazione(JsonObject criteriJson, Pageable paginazione, Iterable<Profilo> profili) throws Exception {
		Page<TaskDesktopDTO> foundPage = null;
		Profilo profilo = profili.iterator().next();
		List<Long> ruoliProcessoCurrentProfIds = profiloService.getRuoloIdsByType(profilo.getId(), TipoRuoloEnum.PROCESSO);
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		String sql = this.getTaskRiassegnazioneQuery();
		
		if(ruoliProcessoCurrentProfIds!=null && !ruoliProcessoCurrentProfIds.isEmpty()) {
			sql += " where 1=1";
		}else {
			sql += " where 1<>1";
		}
		
		Aoo profAoo = aooService.findOneSimple(profilo.getAoo().getId());
		if(profAoo!=null && profAoo.getProfiloResponsabileId()!= null && profAoo.getProfiloResponsabileId().equals(profilo.getId())) {
			List<Aoo> aoos = aooService.getAooRicorsiva(profilo.getAoo().getId());
			String aooStrIn = "(";
			for(Aoo aoo : aoos) {
				if(aooStrIn.length() > 1) {
					aooStrIn += ",";
				}
				aooStrIn += aoo.getId();
			}
			aooStrIn += ")";
			sql += " AND AOOGIACENZA.id in " + aooStrIn;
		}else {
			sql += " AND AOOGIACENZA.id = " + profilo.getAoo().getId() ;
		}
		
		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profilo, null);
		query = query.parameter("assigneeNotLike1", usernameBpm);
		query = query.parameter("assigneeNotLike2", usernameBpm + Constants.BPM_INCARICO_SEPARATOR + "%");
		
		sql += " and T.ASSIGNEE_ not LIKE #{assigneeNotLike1}";
		sql += " and T.ASSIGNEE_ not LIKE #{assigneeNotLike2}";
		
		int i = -1;
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}

		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			i++;
			sql += " AND TIPOATTO.codice = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "avviatoDa")) {
			i++;
			sql += " AND ATTO.created_by = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("avviatoDa").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneDa")) {
			i++;
			sql += " AND ATTO.created_date >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneA")) {
			i++;
			sql += " AND ATTO.created_date <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneA").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataAdozioneDa")) {
			i++;
			sql += " AND ATTO.data_adozione >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataAdozioneA")) {
			i++;
			sql += " AND ATTO.data_adozione <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneA").getAsString().trim()) + " 23:59:59");
		}
		if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaDa")) {
			i++;
			sql += " AND ATTO.data_esecutivita >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEsecutivitaDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaA")) {
			i++;
			sql += " AND ATTO.data_esecutivita <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEsecutivitaA").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "ultimaAzione")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.attivita = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("ultimaAzione").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "esecutore")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.created_by = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("esecutore").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataStart")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.data_attivita >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataStart").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataEnd")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.data_attivita <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEnd").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			i++;
			sql += " AND LOWER(T.NAME_) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inCaricoA")) {
			i++;
			sql += " AND LOWER(ASSEGNATARIO.username) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("inCaricoA").getAsString().trim().toLowerCase())+"%");
		}

		if(JsonUtil.hasFilter(criteriJson, "dataAssegnazioneStart")) {
			i++;
			sql += " AND T.CREATE_TIME_ >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAssegnazioneStart").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataAssegnazioneEnd")) {
			i++;
			sql += " AND T.CREATE_TIME_ <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAssegnazioneEnd").getAsString().trim()) + " 23:59:59");
		}
		
		String ruoloStrIn = "(";
		for(Long id : ruoliProcessoCurrentProfIds) {
			if(ruoloStrIn.length() > 1) {
				ruoloStrIn += ",";
			}
			ruoloStrIn += id;
		}
		ruoloStrIn += ")";
		
		//i ruoli di processo dell'attuale assegnatario devono essere un sottoinsieme di quelli del profilo dell'utente corrente
		sql += " group by T.ID_ having count(RUOLO_ASSEGNATARIO.id in " + ruoloStrIn + " or null) = count(RUOLO_ASSEGNATARIO.id)";
		long totalElements = query.sql("SELECT count(*) from (select distinct T.ID_" + sql + ") a").count();
		if(JsonUtil.hasFilter(criteriJson, "ordinamento")) {
			String ordinamento = StringUtil.trimStr(criteriJson.get("ordinamento").getAsString().trim());
			String tipoOrdinamento = "asc";
			if(JsonUtil.hasFilter(criteriJson, "tipoOrinamento")) {
				tipoOrdinamento = StringUtil.trimStr(criteriJson.get("tipoOrinamento").getAsString().trim());
			}
			switch(ordinamento) {
				case "codiceCifra": 
					sql += " order by ATTO.codice_cifra " + tipoOrdinamento;
					break;
				case "oggetto": 
					sql += " order by ATTO.oggetto " + tipoOrdinamento;
					break;
				case "tipoAtto":
					sql += " order by TIPOATTO.descrizione" + tipoOrdinamento;
					break;
				case "avviatoDa": 
					sql += " order by IFNULL(ATTO.created_by, '9999-12-31') " + tipoOrdinamento;
					break;
				case "dataCreazione": 
					sql += " order by IFNULL(ATTO.created_date, '9999-12-31') " + tipoOrdinamento;
					break;
				case "ultimaAzione": 
					sql += " order by ULTIMA_LAVORAZIONE.attivita " + tipoOrdinamento;
					break;
				case "esecutore": 
					sql += " order by ULTIMA_LAVORAZIONE.created_by " + tipoOrdinamento;
					break;
				case "dataUltimaAzioneEffettuata": 
					sql += " order by IFNULL(ULTIMA_LAVORAZIONE.data_attivita, '9999-12-31') " + tipoOrdinamento;
					break;
				case "lavorazione": 
					sql += " order by T.NAME_ " + tipoOrdinamento;
					break;
				case "inCaricoA": 
					sql += " order by ASSEGNATARIO.nome " + tipoOrdinamento + ", ASSEGNATARIO.cognome " + tipoOrdinamento;
					break;
				case "dataAssegnazione": 
					sql += " order by IFNULL(T.CREATE_TIME_, '9999-12-31') " + tipoOrdinamento;
					break;
				default: 
					sql += " order by T.CREATE_TIME_ desc";
			}
		}else {
			sql += " order by T.CREATE_TIME_ desc";
		}
		query = query.sql("SELECT DISTINCT T.*" + sql);
		
		List<Task> tasks = null;
		if(paginazione!=null) {
			tasks = query.listPage(paginazione.getOffset(), paginazione.getPageSize());
		}else {
			tasks = query.list();
		}
		
		List<TaskDesktopDTO> lista = null;
		
		for(Task task : tasks) {
			String businessKey = this.getBusinessKey(task.getProcessInstanceId());
			Atto attoDb = attoService.findOneSimple(Long.parseLong(businessKey));
			TaskDesktopDTO dto = new TaskDesktopDTO();
			TipoTaskDTO tipoTaskDTO = tipoTaskConverter.toTipoTask(task, attoDb);
			if(dto.getTaskBpm()==null) {
				dto.setTaskBpm(new TipoTaskDTO());
			}
			dto.setTaskBpm(tipoTaskDTO);
			if(task.getAssignee()!=null) {
				Profilo p = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee().trim());
				if(p!=null) {
					if(p.getValidita()!=null && p.getValidita().getValidoal() != null) {
						dto.setHighLighted(true);
					}
				}
			}
			
			VariableInstance varRagioneria = getVariabileTask(
					task.getId(), AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA);
			
			if (varRagioneria != null && attoDb.getDatiContabili() != null && 
					attoDb.getDatiContabili().getNumArriviRagioneria() != null &&
					attoDb.getDatiContabili().getNumArriviRagioneria().intValue() > 1) {
				
				dto.setHighLighted(true);
				dto.setTrasformazioneWarning(attoDb.getDatiContabili().getTrasformazioneWarning());
				dto.setNumArriviRagioneria(attoDb.getDatiContabili().getNumArriviRagioneria());
			}
			
			String highLightedInfo = getHighLightedInfo(task.getId(), attoDb);
			if(highLightedInfo!=null && !highLightedInfo.isEmpty()) {
				dto.setNoteStepPrecedente(highLightedInfo);
				dto.setHighLighted(true);
			}else {
				dto.setNoteStepPrecedente(null);
				dto.setHighLighted(false);
			}
			
			dto.setAtto(attoService.findOneScrivaniaBasic(Long.parseLong(businessKey)));
			if(lista==null) {
				lista = new ArrayList<TaskDesktopDTO>();
			}
			lista.add(dto);
		}
		
		if(totalElements > 0 && paginazione==null){
			foundPage = new PageImpl<TaskDesktopDTO>(lista);
		}
		else if(totalElements > 0){			
			foundPage = new PageImpl<TaskDesktopDTO>(lista, paginazione, totalElements);
		}
		else{
			foundPage = new PageImpl<TaskDesktopDTO>(new ArrayList<TaskDesktopDTO>());
		}
		
		return foundPage;
	}
	
	public String getHighLightedInfo(String taskBpmId, Atto atto){
		//warning_type#_#note
		String highLightedInfo = null;
		
		final String SEPARATORE = "#_#";
		
		VariableInstance varPredisposizione = getVariabileTask(taskBpmId, AttoProcessVariables.VAR_ATTO_IN_PREDISPOSIZIONE);
		if(varPredisposizione==null) {
			if (atto.getAvanzamento() != null && (atto.getAvanzamento().size() > 0)) {
				Avanzamento lastAv = atto.getAvanzamento().iterator().next();
				if (!StringUtil.isNull(lastAv.getNote()) && lastAv.getWarningType() != null && !lastAv.getWarningType().isEmpty()) {
					highLightedInfo = (lastAv.getWarningType()!=null && !lastAv.getWarningType().isEmpty() ? lastAv.getWarningType() : "nd") + SEPARATORE + StringUtil.trimStr(lastAv.getNote());
				}
			}
		}else {
			if (atto.getAvanzamento() != null && (atto.getAvanzamento().size() > 0)) {
				Iterator<Avanzamento> it = atto.getAvanzamento().iterator();
				boolean isLast = true;
				while(it.hasNext()) {
					Avanzamento lastAv = it.next();
					if (!StringUtil.isNull(lastAv.getNote()) && lastAv.getWarningType() != null && !lastAv.getWarningType().isEmpty()) {
						if(isLast || lastAv.getWarningType().equalsIgnoreCase("RESTITUISCI_PROPONENTE")) {
							highLightedInfo = (lastAv.getWarningType()!=null && !lastAv.getWarningType().isEmpty() ? lastAv.getWarningType() : "nd") + SEPARATORE + StringUtil.trimStr(lastAv.getNote());
							break;
						}
					}
					
					if(isLast) {
						isLast = false;
					}
				}
			}
		}
		return highLightedInfo;
	}
	
	public Page<Atto> findAttiRicercaCommissione(TipologiaRicercaCodeEnum code, Pageable paginazione, JsonObject criteriJson){
		Page<Atto> foundPage = null;
		NativeExecutionQuery query = runtimeService.createNativeExecutionQuery();
		String sql = this.getRicercaPerTaskQuery(code);
		int i = -1;
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "statoProposta")) {
			i++;
			sql += " AND ATTO.stato = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("statoProposta").getAsString().trim()));
		}

		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			i++;
			sql += " AND TIPOATTO.codice = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneDa")) {
			i++;
			sql += " AND ATTO.created_date >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneA")) {
			i++;
			sql += " AND ATTO.created_date <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneA").getAsString().trim()) + " 23:59:59");
		}
		
		sql = sql + " ORDER BY ATTO.last_modified_date desc, ATTO.codice_cifra desc ";


		query = query.sql("SELECT DISTINCT EXE.* " +sql);
		List<Execution> lstExec = null;
		if(paginazione!=null) {
			lstExec = query.listPage(paginazione.getOffset(), paginazione.getPageSize());
		}else {
			lstExec = query.list();
		}
		
		Set<Long> lstId = null;
		if(lstExec != null) {
			for(Execution exec : lstExec) {
				String businessKey = this.getBusinessKey(exec.getProcessInstanceId());
				if(lstId==null) {
					lstId = new HashSet<Long>();
				}
				lstId.add(Long.parseLong(businessKey));
			}
			List<Atto> atti = null;
			if(lstId!= null && !lstId.isEmpty()) {
				atti = attoService.findByIdIn(lstId);
			}
			long totalElements = query.sql("SELECT COUNT(distinct EXE.ID_)" + sql).count();
			if(totalElements > 0 && paginazione==null){
				foundPage = new PageImpl<Atto>(atti);
			}
			else if(totalElements > 0){			
				foundPage = new PageImpl<Atto>(atti, paginazione, totalElements);
			}
			else{
				foundPage = new PageImpl<Atto>(new ArrayList<Atto>());
			}
		}
		
		return foundPage;
		
	}
	
	private String getRicercaPerTaskQuery(TipologiaRicercaCodeEnum code) {
		String sql = " FROM "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) +"."+ managementService.getTableName(Execution.class)+" EXE " +
				" LEFT JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) +"."+ managementService.getTableName(Task.class) +" T on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ " +
				" LEFT JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) +".act_ru_event_subscr EVS on EVS.PROC_INST_ID_ = EXE.PROC_INST_ID_ " +
				" INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ "+
				" INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".task_per_ricerca TASK_RIC on TASK_RIC.tipoatto_id = ATTO.tipoatto_id "+
				" INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipologia_ricerca_task TRT on TRT.task_id = TASK_RIC.id "+
				" INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipologia_ricerca TR on TR.id = tip_ricerca_id "+
				" INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id " +
				" where TR.code = '"+code.name()+"' "+
				" AND ((TASK_RIC.type_code = 'TASK' AND T.TASK_DEF_KEY_ = TASK_RIC.key_code) OR (TASK_RIC.type_code = 'EVENT' AND EVS.ACTIVITY_ID_ = TASK_RIC.key_code))";
		return sql;
	}
	
	private String getTaskAttuazioneInScadenzaQuery() {
		String sql = " FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(Task.class) + " T ";
		sql += " INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".attiodg ATTIODG on ATTIODG.id = ";
		sql += "(select id from "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".attiodg ATTIODGIN where ATTIODGIN.atto_id = ATTO.id order by ATTIODGIN.created_date desc limit 1)";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".ordinegiorno ORDINEGIORNO on ORDINEGIORNO.id = ATTIODG.odg_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".sedutagiunta SEDUTA on SEDUTA.id = ORDINEGIORNO.sedutagiunta_id";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id and TIPOATTO.codice in('MZ', 'RIS', 'ODG')";
		sql += " INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".avanzamento ULTIMA_LAVORAZIONE on ULTIMA_LAVORAZIONE.id = (select id from "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".avanzamento where atto_id = ATTO.id order by data_attivita desc limit 1)";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".parere RISPOSTA on RISPOSTA.id = (select id from "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".parere where parere.atto_id = ATTO.id and tipo_azione in ('inserimento_risposta', 'selezione_relatore') order by created_date desc limit 1)";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".profilo PROFILO_RELATORE on PROFILO_RELATORE.id = RISPOSTA.profilo_relatore_id";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".utente UTENTE_RELATORE on UTENTE_RELATORE.id = PROFILO_RELATORE.utente_id";
		sql += " LEFT OUTER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".utente ASSEGNATARIO on ASSEGNATARIO.username = (case when T.ASSIGNEE_ is not null then substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) else '' end)";
		return sql;
	}
	
	private Query getLavoratiQuery(boolean count, String listaProfiliSql, JsonObject criteriJson, UfficioFilterEnum ufficioFilter, String listaAooSql) {
		Map<String, Object> parameters = new HashMap<String, Object>();
		
		List<Object> attivitaNonIncluseInTabLavorati = WebApplicationProps.getPropertyList(
				ConfigPropNames.ATTIVITA_NON_INCLUSE_IN_TAB_LAVORATI);
		
		String qAv = "";
		if(attivitaNonIncluseInTabLavorati!=null && attivitaNonIncluseInTabLavorati.size()>0) {
			qAv = "select AV.atto_id, max(AV.data_attivita) as LAST_OP_DATE from ("+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".AVANZAMENTO AV "
					+ "INNER JOIN "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".atto ATTOAV on ATTOAV.ID  =  AV.atto_id "
					+ "INNER JOIN "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".tipoatto TIPOATTOAV on TIPOATTOAV.id = ATTOAV.tipoatto_id) "
					+ "where AV.profilo_id in ("+listaProfiliSql+") ";
			for (Object coppia : attivitaNonIncluseInTabLavorati) {
				StringTokenizer tokenizer = new StringTokenizer(coppia.toString(), "###");
				while (tokenizer.hasMoreElements() ) {
					String tipoAtto = tokenizer.nextToken();
					String attivita = tokenizer.nextToken();
					
					if("*".equalsIgnoreCase(tipoAtto)) {
						qAv+="AND (AV.attivita not like '"+attivita+"' )";
					}else {
						qAv+="AND (AV.attivita not like '"+attivita+"' or TIPOATTOAV.codice not like '"+tipoAtto+"')";
					}
				}
			}
			qAv += " group by AV.atto_id";
		}else {
			qAv = "select AV.atto_id, max(AV.data_attivita) as LAST_OP_DATE from "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".AVANZAMENTO AV where AV.profilo_id in ("+listaProfiliSql+") group by AV.atto_id";
		}
		
		String sql = count ? "SELECT count(distinct ATTO.id)" : "SELECT AV1.id ";
		sql += " FROM " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".ATTO ATTO ";
		sql += "INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".tipoatto TIPOATTO on TIPOATTO.id = ATTO.tipoatto_id ";
		sql += "INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".AVANZAMENTO AV1 on AV1.atto_id = ATTO.id ";
		sql += "inner join ( " +qAv + " ) G1 on G1.atto_id = ATTO.id and AV1.data_attivita = G1.LAST_OP_DATE";
		
		int i = -1;
		sql += " where 1=1";
		
		if(criteriJson!=null && !criteriJson.isJsonNull()) {
			if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaDa")) {
				i++;
				sql += " AND ATTO.data_esecutivita >= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEsecutivitaDa").getAsString().trim()) + " 00:00:00");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataEsecutivitaA")) {
				i++;
				sql += " AND ATTO.data_esecutivita <= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEsecutivitaA").getAsString().trim()) + " 23:59:59");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
				i++;
				sql += " AND LOWER(AV1.attivita) LIKE :variableValue"+i;
				parameters.put("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataStart")) {
				i++;
				sql += " AND AV1.data_attivita >= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataStart").getAsString().trim()) + " 00:00:00");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataEnd")) {
				i++;
				sql += " AND AV1.data_attivita <= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEnd").getAsString().trim()) + " 23:59:59");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
				i++;
				sql += " AND ATTO.codice_cifra LIKE :variableValue"+i;
				parameters.put("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
				i++;
				sql += " AND ATTO.oggetto LIKE :variableValue"+i;
				parameters.put("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "numeroAdozione")) {
				i++;
				sql += " AND ATTO.numero_adozione LIKE :variableValue"+i;
				parameters.put("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("numeroAdozione").getAsString().trim())+"%");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
				i++;
				sql += " AND TIPOATTO.codice = :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim()));
			}
			
			if(JsonUtil.hasFilter(criteriJson, "avviatoDa")) {
				i++;
				sql += " AND ATTO.created_by = :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("avviatoDa").getAsString().trim()));
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataCreazioneDa")) {
				i++;
				sql += " AND ATTO.created_date >= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneDa").getAsString().trim()) + " 00:00:00");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataCreazioneA")) {
				i++;
				sql += " AND ATTO.created_date <= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneA").getAsString().trim()) + " 23:59:59");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataAdozioneDa")) {
				i++;
				sql += " AND ATTO.data_adozione >= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneDa").getAsString().trim()) + " 00:00:00");
			}
			
			if(JsonUtil.hasFilter(criteriJson, "dataAdozioneA")) {
				i++;
				sql += " AND ATTO.data_adozione <= :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneA").getAsString().trim()) + " 23:59:59");
			}
	
			if(JsonUtil.hasFilter(criteriJson, "ultimaAzione")) {
				i++;
				sql += " AND AV1.attivita = :variableValue"+i;
				parameters.put("variableValue"+i, StringUtil.trimStr(criteriJson.get("ultimaAzione").getAsString().trim()));
			}
		}
		if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey()) && listaAooSql!=null && !listaAooSql.trim().isEmpty()) {
			if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
				i++;
				sql += " AND ATTO.aoo_id in (:variableValue"+i+")";
				parameters.put("variableValue"+i, StringUtil.trimStr(listaAooSql.trim()));
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
				i++;
				sql += " AND ATTO.aoo_id not in (:variableValue"+i+")";
				parameters.put("variableValue"+i, StringUtil.trimStr(listaAooSql.trim()));
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
				i++;
				sql += " AND ATTO.aoo_id is null";
			}
		}
		if(!count) {
			sql += " GROUP BY ATTO.id order by AV1.data_attivita desc";
		}
		Query query = em.createNativeQuery(sql);
		for(String par : parameters.keySet()) {
			query.setParameter(par, parameters.get(par));
		}
		return query;
	}
	
	public Page<Object> getLavorati(String listaProfiliSql, Pageable paginazione, JsonObject criteriJson, UfficioFilterEnum ufficioFilter, String listaAooSql) {
		Query query = this.getLavoratiQuery(false, listaProfiliSql, criteriJson, ufficioFilter, listaAooSql);
		@SuppressWarnings("unchecked")
		List<Object> lista = query.setFirstResult(paginazione.getOffset()).setMaxResults(paginazione.getPageSize()).getResultList();
		Query countQuery = this.getLavoratiQuery(true, listaProfiliSql, criteriJson, ufficioFilter, listaAooSql);
		Long totalElements = ((BigInteger)countQuery.getSingleResult()).longValue();
		return new PageImpl<Object>(lista, paginazione, totalElements);
	}
	
	public Page<TaskDesktopDTO> getTaskAttuazioneInScadenza(JsonObject criteriJson, Pageable paginazione, Iterable<Profilo> profili) throws Exception {
		Page<TaskDesktopDTO> foundPage = null;
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		String sql = this.getTaskAttuazioneInScadenzaQuery();
		int ggScadenza = 45;
		try {
			ggScadenza = Integer.parseInt(WebApplicationProps.getProperty(ConfigPropNames.GIORNI_SCADENZA_ATTUAZIONE, "45"));
		}catch(Exception e) {};
		
		sql += " where 1=1";
		sql += " and SEDUTA.prima_convocazione_fine is not null and DATEDIFF(now(), SEDUTA.prima_convocazione_fine) > " + ggScadenza;
		sql += " AND T.TASK_DEF_KEY_ in('gestione_attuazione_mz_ris_odg', 'inserimento_risposta_int', 'inserimento_risposta_dat')";
		
		int i = -1;
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			i++;
			sql += " AND LOWER(T.NAME_) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataStart")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.data_attivita >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataStart").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataEnd")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.data_attivita <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataEnd").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "assessoreAttuazione")) {
			i++;
			sql += " AND TRIM(CONCAT(IFNULL(UTENTE_RELATORE.nome, ''), ' ', IFNULL(UTENTE_RELATORE.cognome, ''))) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("assessoreAttuazione").getAsString().trim())+ "%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "assegnee")) {
			i++;
			String assegneeSql = "(case when T.ASSIGNEE_ is not null then substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) else '' end)";
			sql +=  " AND ("
					+ assegneeSql + " LIKE #{variableValue"+i+"}"
					+ ")";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("assegnee").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "numeroAdozione")) {
			i++;
			sql += " AND ATTO.numero_adozione LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("numeroAdozione").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "tipoAtto")) {
			i++;
			sql += " AND TIPOATTO.codice = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("tipoAtto").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "avviatoDa")) {
			i++;
			sql += " AND ATTO.created_by = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("avviatoDa").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneDa")) {
			i++;
			sql += " AND ATTO.created_date >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataCreazioneA")) {
			i++;
			sql += " AND ATTO.created_date <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataCreazioneA").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataAdozioneDa")) {
			i++;
			sql += " AND ATTO.data_adozione >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataAdozioneA")) {
			i++;
			sql += " AND ATTO.data_adozione <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataAdozioneA").getAsString().trim()) + " 23:59:59");
		}

		if(JsonUtil.hasFilter(criteriJson, "ultimaAzione")) {
			i++;
			sql += " AND ULTIMA_LAVORAZIONE.attivita = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("ultimaAzione").getAsString().trim()));
		}
		
		query = query.sql("SELECT DISTINCT T.*" + sql);
		
		List<Task> tasks = null;
		if(paginazione!=null) {
			tasks = query.listPage(paginazione.getOffset(), paginazione.getPageSize());
		}else {
			tasks = query.list();
		}
		
		List<TaskDesktopDTO> lista = null;
		
		for(Task task : tasks) {
			String businessKey = this.getBusinessKey(task.getProcessInstanceId());
			TaskDesktopDTO dto = new TaskDesktopDTO();
			TipoTaskDTO tipoTaskDTO = tipoTaskConverter.toTipoTask(task);
			if(dto.getTaskBpm()==null) {
				dto.setTaskBpm(new TipoTaskDTO());
			}
			dto.setTaskBpm(tipoTaskDTO);
			
			dto.setAtto(attoService.findOneScrivaniaBasicAttuazione(Long.parseLong(businessKey)));
			if(lista==null) {
				lista = new ArrayList<TaskDesktopDTO>();
			}
			lista.add(dto);
		}
		long totalElements = query.sql("SELECT COUNT(distinct T.ID_)" + sql).count();
		if(totalElements > 0 && paginazione==null){
			foundPage = new PageImpl<TaskDesktopDTO>(lista);
		}
		else if(totalElements > 0){			
			foundPage = new PageImpl<TaskDesktopDTO>(lista, paginazione, totalElements);
		}
		else{
			foundPage = new PageImpl<TaskDesktopDTO>(new ArrayList<TaskDesktopDTO>());
		}
		

		return foundPage;
	}
	
	public Page<TaskDesktopDTO> getTaskInRagioneria(JsonObject criteriJson, Pageable paginazione, boolean scadenzaSort, UfficioFilterEnum ufficioFilter, Iterable<Profilo> profili) throws Exception {
		
		Page<TaskDesktopDTO> foundPage = null;
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
		
		String sql = this.getTaskInRagioneriaQuery();
		
		sql += " where 1=1";
		query = query.parameter("varInRagioneria", AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA);
		
		sql += " AND TIPOATTO.codice in ('DD', 'DL', 'DC', 'DG', 'DPC')";
		
		if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
			if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
				sql += " AND ATTO.aoo_id is null";
			}
		}
		
		int i = -1;
		
		if(JsonUtil.hasFilter(criteriJson, "codiceCifra")) {
			i++;
			sql += " AND ATTO.codice_cifra LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("codiceCifra").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "idUfficioGiacenza")) {
			i++;
			sql += " AND AOOGIACENZA.id = #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, criteriJson.get("idUfficioGiacenza").getAsInt());
		}
		
		if(JsonUtil.hasFilter(criteriJson, "descUfficioGiacenza")) {
			i++;
			sql += " AND concat('(', AOOGIACENZA.codice, ') ', AOOGIACENZA.descrizione) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("descUfficioGiacenza").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "oggetto")) {
			i++;
			sql += " AND ATTO.oggetto LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("oggetto").getAsString().trim())+"%");
		}
		if(JsonUtil.hasFilter(criteriJson, "aoo")) {
			i++;
			sql += " AND concat('(', AOO.codice, ') ', AOO.descrizione) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("aoo").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "lavorazione")) {
			i++;
			sql += " AND LOWER(T.NAME_) LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("lavorazione").getAsString().trim().toLowerCase())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inCaricoA")) {
			i++;
//			String codiceAooSql = "(case when IDE.GROUP_ID_ is not null then substring(IDE.GROUP_ID_, POSITION('_!R#_' in IDE.GROUP_ID_) + 5, (case when POSITION('_!I#_' in IDE.GROUP_ID_) = 0 then length(IDE.GROUP_ID_) + 1 else POSITION('_!I#_' in IDE.GROUP_ID_) end)  - POSITION('_!R#_' in IDE.GROUP_ID_) - 5) else '' end)";
			String assegneeSql = "(case when T.ASSIGNEE_ is not null then substring(T.ASSIGNEE_, 1, POSITION('_' in T.ASSIGNEE_) - 1) else '' end)";
			sql +=  " AND ("
					+ assegneeSql + " LIKE #{variableValue"+i+"}"
//					+ " OR "
//					+ codiceAooSql + " LIKE #{variableValue"+i+"}"
//					+ " OR "
//					+ "concat('(', "+codiceAooSql+", ') ', (select UFF.descrizione from "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".aoo UFF where UFF.codice = ("+codiceAooSql+") limit 1)) LIKE #{variableValue"+i+"}"
//					+ " OR "
//					+ "concat("+assegneeSql+", ' - (', "+codiceAooSql+", ') ', (select UFF.descrizione from "+DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME)+".aoo UFF where UFF.codice = ("+codiceAooSql+") limit 1)) LIKE #{variableValue"+i+"}"
					+ ")";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("inCaricoA").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "tipoBilancio")) {
			i++;
			sql += " AND DATI_CONTABILI.tipo_bilancio LIKE #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, "%"+StringUtil.trimStr(criteriJson.get("tipoBilancio").getAsString().trim())+"%");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoEntrataDa")) {
			i++;
			sql += " AND DATI_CONTABILI.importo_entrata >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("importoEntrataDa").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoEntrataA")) {
			i++;
			sql += " AND DATI_CONTABILI.importo_entrata <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("importoEntrataA").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoUscitaDa")) {
			i++;
			sql += " AND DATI_CONTABILI.importo_uscita >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("importoUscitaDa").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "importoUscitaA")) {
			i++;
			sql += " AND DATI_CONTABILI.importo_uscita <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("importoUscitaA").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "differenzaImportoDa")) {
			i++;
			sql += " AND (ifnull(DATI_CONTABILI.importo_entrata, 0) - ifnull(DATI_CONTABILI.importo_uscita, 0)) >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("differenzaImportoDa").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "differenzaImportoA")) {
			i++;
			sql += " AND (ifnull(DATI_CONTABILI.importo_entrata, 0) - ifnull(DATI_CONTABILI.importo_uscita, 0)) <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("differenzaImportoA").getAsString().trim()
					.replaceAll("\\.", "").replaceAll(",", ".")));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataArrivoRagioneriaDa")) {
			i++;
			sql += " AND DATI_CONTABILI.data_arrivo_ragioneria >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataArrivoRagioneriaDa").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataArrivoRagioneriaA")) {
			i++;
			sql += " AND DATI_CONTABILI.data_arrivo_ragioneria <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataArrivoRagioneriaA").getAsString().trim()) + " 23:59:59");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataScadenzaDa")) {
			i++;
			sql += " AND DATI_CONTABILI.data_scadenza >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataScadenzaDa").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "dataScadenzaA")) {
			i++;
			sql += " AND DATI_CONTABILI.data_scadenza <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("dataScadenzaA").getAsString().trim()));
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inCaricoDal")) {
			i++;
			sql += " AND T.CREATE_TIME_ >= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("inCaricoDal").getAsString().trim()) + " 00:00:00");
		}
		
		if(JsonUtil.hasFilter(criteriJson, "inCaricoAl")) {
			i++;
			sql += " AND T.CREATE_TIME_ <= #{variableValue"+i+"}";
			query = query.parameter("variableValue"+i, StringUtil.trimStr(criteriJson.get("inCaricoAl").getAsString().trim()) + " 23:59:59");
		}
		
		
		long totalElements = query.sql("SELECT COUNT(distinct T.ID_)" + sql).count();
		if(!JsonUtil.hasFilter(criteriJson, "ordinamento")) {
			if(scadenzaSort) {
				sql += " order by IFNULL(DATI_CONTABILI.data_scadenza, '9999-12-31') asc, DATI_CONTABILI.importo_uscita desc";
			}else {
				sql += " order by DATI_CONTABILI.data_arrivo_ragioneria desc, DATI_CONTABILI.importo_uscita desc";
			}
		}else {
			String ordinamento = StringUtil.trimStr(criteriJson.get("ordinamento").getAsString().trim());
			String tipoOrdinamento = "asc";
			if(JsonUtil.hasFilter(criteriJson, "tipoOrinamento")) {
				tipoOrdinamento = StringUtil.trimStr(criteriJson.get("tipoOrinamento").getAsString().trim());
			}
			switch(ordinamento) {
				case "codiceCifra": 
					sql += " order by ATTO.codice_cifra " + tipoOrdinamento;
					break;
				case "oggetto": 
					sql += " order by ATTO.oggetto " + tipoOrdinamento;
					break;
				case "aoo": 
					sql += " order by concat('(', AOO.codice, ') ', AOO.descrizione) " + tipoOrdinamento;
					break;
				case "dataScadenza": 
					sql += " order by IFNULL(DATI_CONTABILI.data_scadenza, '9999-12-31') " + tipoOrdinamento;
					break;
				case "dataArrivoRagioneria": 
					sql += " order by DATI_CONTABILI.data_arrivo_ragioneria " + tipoOrdinamento;
					break;
				case "lavorazione": 
					sql += " order by T.NAME_ " + tipoOrdinamento;
					break;
				case "inCaricoA": 
					sql += " order by ASSEGNATARIO.nome " + tipoOrdinamento + ", ASSEGNATARIO.cognome " + tipoOrdinamento;
					break;
				case "inCaricoDal": 
					sql += " order by IFNULL(T.CREATE_TIME_, '9999-12-31') " + tipoOrdinamento;
					break;
				case "importoEntrata": 
					sql += " order by DATI_CONTABILI.importo_entrata " + tipoOrdinamento;
					break;
				case "importoUscita": 
					sql += " order by DATI_CONTABILI.importo_uscita " + tipoOrdinamento;
					break;
				case "differenzaImporto": 
					sql += " order by (ifnull(DATI_CONTABILI.importo_entrata, 0) - ifnull(DATI_CONTABILI.importo_uscita, 0)) " + tipoOrdinamento;
					break;
				case "tipoBilancio": 
					sql += " order by DATI_CONTABILI.tipo_bilancio " + tipoOrdinamento;
					break;
				default: if(scadenzaSort) {
					sql += " order by IFNULL(DATI_CONTABILI.data_scadenza, '9999-12-31') asc, DATI_CONTABILI.importo_uscita desc";
				}else {
					sql += " order by DATI_CONTABILI.data_arrivo_ragioneria desc, DATI_CONTABILI.importo_uscita desc";
				}
			}
		}
		
		query = query.sql("SELECT DISTINCT T.*" + sql);
		
		List<Task> tasks = null;
		if(paginazione!=null) {
			tasks = query.listPage(paginazione.getOffset(), paginazione.getPageSize());
		}else {
			tasks = query.list();
		}
		
		List<TaskDesktopDTO> lista = null;
		
		for(Task task : tasks) {
			String businessKey = this.getBusinessKey(task.getProcessInstanceId());
			TaskDesktopDTO dto = new TaskDesktopDTO();
			TipoTaskDTO tipoTaskDTO = tipoTaskConverter.toTipoTask(task);
			if(dto.getTaskBpm()==null) {
				dto.setTaskBpm(new TipoTaskDTO());
			}
			dto.setTaskBpm(tipoTaskDTO);
			if(task.getAssignee()!=null) {
				Profilo profilo = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee().trim());
				if(profilo!=null) {
					dto.getTaskBpm().setUfficioGiacenza("(" + profilo.getAoo().getCodice() + ") " + profilo.getAoo().getDescrizione());
					if(profilo.getValidita()!=null && profilo.getValidita().getValidoal() != null) {
						dto.setHighLighted(true);
					}
				}
			}else if(dto.getTaskBpm()!=null && dto.getTaskBpm().getCandidateGroups()!=null && !dto.getTaskBpm().getCandidateGroups().isEmpty()){
				String uffici = "";
				List<String> candidateGroups = Lists.newArrayList(dto.getTaskBpm().getCandidateGroups().split(","));
				for(String candidateGroup : candidateGroups) {
					List<Aoo> aoos = bpmWrapperUtil.getAooByCandidate(candidateGroup);
					if(aoos!=null) {
						for(Aoo aoo : aoos) {
							if(!uffici.isEmpty()) {
								uffici += ", ";
							}
							uffici += "(" + aoo.getCodice() + ") " + aoo.getDescrizione();
						}
					}
				}
				dto.getTaskBpm().setUfficioGiacenza(uffici);
			}
			
			VariableInstance varRagioneria = getVariabileTask(
					task.getId(), AttoProcessVariables.VAR_ATTO_IN_RAGIONERIA);
			
			Atto attoDb = attoService.findOneSimple(Long.parseLong(businessKey));
			if (varRagioneria != null && attoDb.getDatiContabili() != null && 
					attoDb.getDatiContabili().getNumArriviRagioneria() != null &&
					attoDb.getDatiContabili().getNumArriviRagioneria().intValue() > 1) {
				
				dto.setHighLighted(true);
				dto.setTrasformazioneWarning(attoDb.getDatiContabili().getTrasformazioneWarning());
				dto.setNumArriviRagioneria(attoDb.getDatiContabili().getNumArriviRagioneria());
			}
			
			if (attoDb.getAvanzamento() != null && (attoDb.getAvanzamento().size() > 0)) {
				Avanzamento lastAv = attoDb.getAvanzamento().iterator().next();
				if (!StringUtil.isNull(lastAv.getNote()) && lastAv.getWarningType() != null && !lastAv.getWarningType().isEmpty()) {
					dto.setNoteStepPrecedente(StringUtil.trimStr(lastAv.getNote()));
					dto.setHighLighted(true);
				}
				else {
					dto.setNoteStepPrecedente(null);
				}
			}
			
			dto.setAtto(attoService.findOneScrivaniaBasic(Long.parseLong(businessKey)));
			if(lista==null) {
				lista = new ArrayList<TaskDesktopDTO>();
			}
			lista.add(dto);
		}
		
		if(totalElements > 0 && paginazione==null){
			foundPage = new PageImpl<TaskDesktopDTO>(lista);
		}
		else if(totalElements > 0){			
			foundPage = new PageImpl<TaskDesktopDTO>(lista, paginazione, totalElements);
		}
		else{
			foundPage = new PageImpl<TaskDesktopDTO>(new ArrayList<TaskDesktopDTO>());
		}
		

		return foundPage;
	}
	
	/**
	 * @param profili
	 * @return
	 */
	public List<Task> getTaskInCoordinametoTesto(Iterable<Profilo> profili, boolean giunta, UfficioFilterEnum ufficioFilter) {
		
		List<Task> retVal = new ArrayList<Task>();
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
			
		
		String taskDefKey = giunta?"coordinamento_testo_giunta":"coordinamento_testo";
		
		String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T " +
				"INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ " +
				"INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ " +
				"WHERE T.TASK_DEF_KEY_ LIKE '%"+taskDefKey+"'";
		
		if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
			if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
				sql += " AND ATTO.aoo_id is null";
			}
		}
		
		sql += " ORDER BY T.CREATE_TIME_ DESC ";
			
		query = query.sql(sql);
			
		retVal = query.list();

		return retVal;
	}
	
	
	/**
	 * @param profili
	 * @return
	 */
	public List<Task> getTaskInPostSeduta(Iterable<Profilo> profili, UfficioFilterEnum ufficioFilter, List<String> taskDefKeys) {
		
		List<Task> retVal = new ArrayList<Task>();
		
		NativeTaskQuery query = taskService.createNativeTaskQuery();
			
		
		String inTaskDefKey = "";
		
		if(taskDefKeys!=null) {
			for (String taskDefKey: taskDefKeys) {
				
				if(inTaskDefKey.length()>0) {
					inTaskDefKey += ",";
				}
				inTaskDefKey+="'"+taskDefKey+"'";
			}
		}
		
		
		
		String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T " +
				"INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ " +
				"INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ " +
				//"WHERE T.TASK_DEF_KEY_ LIKE '%"+taskDefKey+"'";
				"WHERE T.TASK_DEF_KEY_ in ("+inTaskDefKey+")";
		
		if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
			if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
				if(profili!=null){
					String aooFilterSql = "";
					for (Profilo profilo : profili) {
						if(!aooFilterSql.isEmpty()) {
							aooFilterSql += ",";
						}
						aooFilterSql += profilo.getAoo().getId();
					}
					sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
				}
			}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
				sql += " AND ATTO.aoo_id is null";
			}
		}
		
		sql += " ORDER BY T.CREATE_TIME_ DESC ";
			
		query = query.sql(sql);
			
		retVal = query.list();

		return retVal;
	}
	/**
	 * @param profili
	 * @return
	 */
	public List<Task> getTaskInArrivo(final Iterable<Profilo> profili, 
			String azioneDaEffettuare, Date createdAfter, Date createdBefore, String taskId, UfficioFilterEnum ufficioFilter, String inLavorazione) {
		List<Task> retVal = new ArrayList<Task>();
		
		if ((profili != null) && (!Iterables.isEmpty(profili)) ) {
			NativeTaskQuery query = taskService.createNativeTaskQuery();
			
			String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T left outer join " + 
							managementService.getTableName(IdentityLinkEntity.class) + " I on T.ID_ = I.TASK_ID_ " +
							"INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ " +
							"INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ " ;
			
			
			if(!StringUtil.isNull(inLavorazione) && inLavorazione.equalsIgnoreCase("N")) {
				sql+=" WHERE ASSIGNEE_ IS NULL ";
			}else if(!StringUtil.isNull(inLavorazione) && inLavorazione.equalsIgnoreCase("S")){
				sql+=" WHERE ASSIGNEE_ IS NOT NULL";
			}else {
				sql+=" WHERE (ASSIGNEE_ IS NULL OR ASSIGNEE_ IS NOT NULL) ";
			}
			
			
			if (!StringUtil.isNull(taskId)) {
				sql += " AND T.TASK_DEF_KEY_ LIKE '"+taskId+"'";
			}
			
			int cntProf = 0;
			for (Profilo profilo : profili) {				
				List<String> groupPref = bpmWrapperUtil.getGroupLikeForBpm(profilo);
				for (int i = 0; i < groupPref.size(); i++) {
					if (cntProf == 0) {
						sql += " AND (";
					}
					else {
						sql += " OR ";
					}
					cntProf++;
					
					sql += " (I.TYPE_ = 'candidate' AND I.GROUP_ID_ LIKE #{groupLike" + cntProf + "} )";
				}
			}
			
			if (cntProf > 0) {
				sql += ") ";
			}
			
			if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
				if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
					sql += " AND ATTO.aoo_id is null";
				}
			}
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				sql += " AND LOWER(T.NAME_) LIKE #{nameLike} ";
			}
			if (createdAfter != null) {
				sql += " AND T.CREATE_TIME_ >= #{createdAfter} ";
			}
			if (createdBefore != null) {
				sql += " AND T.CREATE_TIME_ <= #{createdBefore} ";
			}
			
			sql += " ORDER BY T.CREATE_TIME_ DESC ";
			
			// log.info("NATIVE QUERY: " + sql);
			query = query.sql(sql);
			
			cntProf = 0;
			for (Profilo profilo : profili) {				
				List<String> groupPref = bpmWrapperUtil.getGroupLikeForBpm(profilo);
				
				for (String pref : groupPref) {
					cntProf++;
					query = query.parameter("groupLike" + cntProf, pref);
				}
			}
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				query = query.parameter("nameLike", "%" + azioneDaEffettuare.toLowerCase() + "%");
			}
			if (createdAfter != null) {
				createdAfter.setHours(0);
				createdAfter.setMinutes(0);
				createdAfter.setSeconds(0);
				query = query.parameter("createdAfter", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdAfter));
			}
			if (createdBefore != null) {
				createdBefore.setHours(23);
				createdBefore.setMinutes(59);
				createdBefore.setSeconds(59);
				query = query.parameter("createdBefore", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdBefore));
			}
			
			retVal = query.list();
		}
		return retVal;
	}
	
	public List<Task> getTaskInArrivoPerMonitoraggioGroup(final Iterable<Profilo> profili, 
			String azioneDaEffettuare, Date createdAfter, Date createdBefore, String taskId, UfficioFilterEnum ufficioFilter, String inLavorazione) {
		List<Task> retVal = new ArrayList<Task>();
		
		if ((profili != null) && (!Iterables.isEmpty(profili)) ) {
			NativeTaskQuery query = taskService.createNativeTaskQuery();
			
			String sql = "SELECT DISTINCT T.* FROM " + managementService.getTableName(Task.class) + " T left outer join " + 
							managementService.getTableName(IdentityLinkEntity.class) + " I on T.ID_ = I.TASK_ID_ " +
							"INNER JOIN " + DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CAMUNDA_DB_NAME) + "." + managementService.getTableName(ExecutionEntity.class) + " EXE on EXE.PROC_INST_ID_ = T.PROC_INST_ID_ " +
							"INNER JOIN "+ DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_ATTICO_DB_NAME) + ".atto ATTO on ATTO.id = EXE.BUSINESS_KEY_ " ;
			
			
			if(!StringUtil.isNull(inLavorazione) && inLavorazione.equalsIgnoreCase("N")) {
				sql+=" WHERE ASSIGNEE_ IS NULL ";
			}else if(!StringUtil.isNull(inLavorazione) && inLavorazione.equalsIgnoreCase("S")){
				sql+=" WHERE ASSIGNEE_ IS NOT NULL";
			}else {
				sql+=" WHERE (ASSIGNEE_ IS NULL OR ASSIGNEE_ IS NOT NULL) ";
			}
			
			
			if (!StringUtil.isNull(taskId)) {
				sql += " AND T.TASK_DEF_KEY_ LIKE '"+taskId+"'";
			}
			
			int cntProf = 0;
			for (Profilo profilo : profili) {				
				List<String> groupPref = bpmWrapperUtil.getGroupLikeForBpmForMonitoraggio(profilo);
				for (int i = 0; i < groupPref.size(); i++) {
					if (cntProf == 0) {
						sql += " AND (";
					}
					else {
						sql += " OR ";
					}
					cntProf++;
					
					sql += " (I.TYPE_ = 'candidate' AND I.GROUP_ID_ LIKE #{groupLike" + cntProf + "} )";
				}
			}
			
			if (cntProf > 0) {
				sql += ") ";
			}
			
			if(ufficioFilter!=null && !ufficioFilter.getKey().equals(UfficioFilterEnum.NESSUN_FILTRO.getKey())) {
				if(ufficioFilter.getKey().equals(UfficioFilterEnum.PROPRIO_UFFICIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.UFFICI_DIVERSI_DAL_PROPRIO.getKey())) {
					if(profili!=null){
						String aooFilterSql = "";
						for (Profilo profilo : profili) {
							if(!aooFilterSql.isEmpty()) {
								aooFilterSql += ",";
							}
							aooFilterSql += profilo.getAoo().getId();
						}
						sql += " AND ATTO.aoo_id not in(" + aooFilterSql + ")";
					}
				}else if(ufficioFilter.getKey().equals(UfficioFilterEnum.NON_PREVISTO.getKey())) {
					sql += " AND ATTO.aoo_id is null";
				}
			}
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				sql += " AND LOWER(T.NAME_) LIKE #{nameLike} ";
			}
			if (createdAfter != null) {
				sql += " AND T.CREATE_TIME_ >= #{createdAfter} ";
			}
			if (createdBefore != null) {
				sql += " AND T.CREATE_TIME_ <= #{createdBefore} ";
			}
			
			sql += " ORDER BY T.CREATE_TIME_ DESC ";
			
			// log.info("NATIVE QUERY: " + sql);
			query = query.sql(sql);
			
			cntProf = 0;
			
			for (Profilo profilo : profili) {				
				
				List<String> groupPref = bpmWrapperUtil.getGroupLikeForBpmForMonitoraggio(profilo);
				for (String pref : groupPref) {
					cntProf++;
					query = query.parameter("groupLike" + cntProf, pref);
				}
			}
			
			if (!StringUtil.isNull(azioneDaEffettuare)) {
				query = query.parameter("nameLike", "%" + azioneDaEffettuare.toLowerCase() + "%");
			}
			if (createdAfter != null) {
				createdAfter.setHours(0);
				createdAfter.setMinutes(0);
				createdAfter.setSeconds(0);
				query = query.parameter("createdAfter", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdAfter));
			}
			if (createdBefore != null) {
				createdBefore.setHours(23);
				createdBefore.setMinutes(59);
				createdBefore.setSeconds(59);
				query = query.parameter("createdBefore", StringUtil.MYSQL_DATE_TIME_FORMAT.format(createdBefore));
			}
			
			retVal = query.list();
		}
		return retVal;
	}

	
	/**
	 * @param idAtto
	 * @return
	 *
	 * CASISTICA ELIMINATA
	 *
	public boolean isAttoNumerabile(long idAtto) {
		List<Execution> foundExecution = runtimeService.createExecutionQuery()
				.processInstanceBusinessKey(String.valueOf(idAtto))
				.messageEventSubscriptionName(MessageKeys.NUMERAZIONE_ATTO)
				.active().list();
		
		return(foundExecution != null && !foundExecution.isEmpty());
	}
	*/
	
	
	
	/**
	 * @param isOdgGiunta
	 * @return
	 */
	public List<Execution> getAttesaEsitoSeduta(boolean isOdgGiunta) {
		String messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO;
		if (isOdgGiunta) {
			messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA;
		}
		
		return runtimeService.createExecutionQuery().messageEventSubscriptionName(messageName).active().list();
	}
	
	public List<Execution> getAttiParereCommEditable() {
		return runtimeService.createExecutionQuery().processVariableValueEquals(AttoProcessVariables.COM_CONSQUART_REVCONT_EDITABLE, "SI").active().list();
	}

	
	public void loadAllTasks(final Atto atto) {
		
		List<Task> list = taskService.createTaskQuery()
				.processInstanceBusinessKey(
						String.valueOf(atto.getId().longValue()))
				.active().list();
		
		atto.setTaskAttivi(statoAttoConverter.toStatoAtto(list));
	}
	
	public void loadAllTasksPostSeduta(final Atto atto) {
		
		List<Task> list = taskService.createTaskQuery()
				.processInstanceBusinessKey(
						String.valueOf(atto.getId().longValue()))
				.active().list();
		
		atto.setTaskAttivi(statoAttoConverter.toStatoAttoPostSeduta(list));
	}
	
	public List<Task> getAllTasks(final Long idatto) {
		return taskService.createTaskQuery()
				.processInstanceBusinessKey(
						String.valueOf(idatto))
				.active().list();
	}
	
	public List<Task> getTaskAttiviAssessori(Atto atto) {
		return taskService.createTaskQuery()
		.processInstanceBusinessKey(
				String.valueOf(atto.getId().longValue()))
		.active().list();
	}
	
	
	public boolean isAttesaEsitoGiunta(long idAtto) {
		List<Execution> procList = runtimeService.createExecutionQuery().processInstanceBusinessKey(
				String.valueOf(idAtto)).messageEventSubscriptionName(MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA).active().list();
		return (procList != null) && (procList.size() > 0);
	}
	
	public boolean isAttesaEsitoConsiglio(long idAtto) {
		List<Execution> procList = runtimeService.createExecutionQuery().processInstanceBusinessKey(
				String.valueOf(idAtto)).messageEventSubscriptionName(MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO).active().list();
		return (procList != null) && (procList.size() > 0);
	}
	
	
	/**
	 * @param isOdgGiunta
	 * @param idAtto
	 * @param resocontoDTO
	 */
	public void registrazioneEsitoSeduta(boolean isOdgGiunta, long idAtto, ResocontoDTO resocontoDTO) {
		
		
		String profiloPresidente = null;
		if (resocontoDTO.getPresidenteFine() != null && resocontoDTO.getPresidenteFine().getProfilo() != null && 
				resocontoDTO.getPresidenteFine().getProfilo().getId() != null && resocontoDTO.getPresidenteFine().getProfilo().getId().longValue()>0) {
			profiloPresidente = bpmWrapperUtil.getUsernameForBpm(resocontoDTO.getPresidenteFine().getProfilo(), null);
		}
		String profiloSegretario = null;
		if (resocontoDTO.getSegretarioFine() != null&& resocontoDTO.getSegretarioFine().getProfilo() != null && 
				resocontoDTO.getSegretarioFine().getProfilo().getId() != null && resocontoDTO.getSegretarioFine().getProfilo().getId().longValue()>0) {
			profiloSegretario = bpmWrapperUtil.getUsernameForBpm(resocontoDTO.getSegretarioFine().getProfilo(), null);
		}
		
		Map<String, Object> esitoAtto = new HashMap<String, Object>();
		esitoAtto.put(AttoProcessVariables.ESITO_PROPOSTA_ODG, StringUtil.trimStr(resocontoDTO.getEsito()));
		if (!StringUtil.isNull(profiloPresidente)) {
			esitoAtto.put(AttoProcessVariables.PRESIDENTE_SEDUTA, StringUtil.trimStr(profiloPresidente));
		}
		if (!StringUtil.isNull(profiloSegretario)) {
			esitoAtto.put(AttoProcessVariables.SEGRETARIO_SEDUTA, StringUtil.trimStr(profiloSegretario)); 
		}
		
		String messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO;
		if (isOdgGiunta) {
			messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA;
		}
		
		List<Execution> corrExec = runtimeService.createExecutionQuery()
				.messageEventSubscriptionName(messageName)
				.processInstanceBusinessKey(String.valueOf(idAtto))
				.active().list();
		
		if(corrExec!=null && corrExec.size() > 0) {
			//aggiornamento dati durante la seduta
			for (Execution exec : corrExec) {
				runtimeService.setVariables(exec.getId(), esitoAtto);
			}
		}else {
			//aggiornamento dati fuori seduta
			corrExec = runtimeService.createExecutionQuery()
					.processInstanceBusinessKey(String.valueOf(idAtto))
					.active().list();
			if(corrExec!=null && corrExec.size() > 0) {
				for (Execution exec : corrExec) {
					runtimeService.setVariables(exec.getId(), esitoAtto);
				}
			}
		}
	}
	
	//@Transactional
	public void confermaEsitoSeduta(boolean isOdgGiunta, long idAtto, long idProfilo, Map<String, Object> variables) {
		String messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO;
		if (isOdgGiunta) {
			messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA;
		}
		
		List<Execution> assList = runtimeService.createExecutionQuery()
				.messageEventSubscriptionName(messageName)
				.processInstanceBusinessKey(String.valueOf(idAtto))
				.active().list();
		
		if (assList == null || assList.isEmpty()) {
			return;
		}
		
		BpmThreadLocalUtil.setProfiloId(idProfilo);
		
		if(variables!=null && variables.get(AttoProcessVariables.ESITO_PROPOSTA_ODG) != null && variables.get(AttoProcessVariables.ESITO_PROPOSTA_ODG) instanceof String && ((String)variables.get(AttoProcessVariables.ESITO_PROPOSTA_ODG)).equalsIgnoreCase(SedutaGiuntaConstants.esitiAttoOdg.nonTrattato.getCodice())){
			BpmThreadLocalUtil.setNomeAttivita(NomiAttivitaAtto.REGISTRAZIONE_ESITO_SEDUTA_NON_TRATTATO);
		}else{
			BpmThreadLocalUtil.setNomeAttivita(NomiAttivitaAtto.REGISTRAZIONE_ESITO_SEDUTA);
		}
		
		if (variables != null) {
			runtimeService.correlateMessage(messageName, String.valueOf(idAtto), variables);
		}
		else {
			runtimeService.correlateMessage(messageName, String.valueOf(idAtto));
		}
	}
	
	public void infoPubblicazioneAlboRicevute(long idAtto) {
		String messageName = MessageKeys.OTTENUTI_DATI_PUBBLICAZIONE_ALBO;
		
		List<Execution> assList = runtimeService.createExecutionQuery()
				.messageEventSubscriptionName(messageName)
				.processInstanceBusinessKey(String.valueOf(idAtto))
				.active().list();
		
		if (assList == null || assList.isEmpty()) {
			return;
		}
		
		runtimeService.correlateMessage(messageName, String.valueOf(idAtto));
	}
	
	/*
	 * NON PIU' UTILIZATO
	 *
	public void modificaComponentiSeduta(long idAtto, ResocontoDTO resocontoDTO) {
		
		List<Execution> executions = runtimeService.createExecutionQuery()
				.processInstanceBusinessKey(String.valueOf(idAtto)).list();
		
		String profiloPresidente = null;
		if (resocontoDTO.getPresidenteFine() != null) {
			profiloPresidente = bpmWrapperUtil.getUsernameForBpm(
					resocontoDTO.getPresidenteFine().getProfilo(), null);
		}
		String profiloSegretario = null;
		if (resocontoDTO.getSegretarioFine() != null) {
			profiloSegretario = bpmWrapperUtil.getUsernameForBpm(
					resocontoDTO.getSegretarioFine().getProfilo(), null);
		}
		
		if (executions != null) {
			for (Execution execution : executions) {								
				if (!StringUtil.isNull(profiloPresidente)) {
					runtimeService.setVariable(execution.getId(), 
							AttoProcessVariables.PRESIDENTE_SEDUTA, StringUtil.trimStr(profiloPresidente));
				}
				if (!StringUtil.isNull(profiloSegretario)) {
					runtimeService.setVariable(execution.getId(), 
							AttoProcessVariables.SEGRETARIO_SEDUTA, StringUtil.trimStr(profiloSegretario)); 
				}
			}
		}
	}
	*/
	
	public void ritiroAttoNonDiscusso(boolean isOdgGiunta,
			long idAtto, long idProfilo,
			String esito, String nomeAttivita,
			String motivazione) {
		
		String messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_CONSIGLIO;
		if (isOdgGiunta) {
			messageName = MessageKeys.REGISTRA_ESITO_SEDUTA_GIUNTA;
		}
		
		Map<String, Object> esitoAtto = new HashMap<String, Object>();
		esitoAtto.put(AttoProcessVariables.ESITO_PROPOSTA_ODG, esito);
		
		BpmThreadLocalUtil.setProfiloId(idProfilo);
		BpmThreadLocalUtil.setNomeAttivita(nomeAttivita);
		BpmThreadLocalUtil.setMotivazione(motivazione);
		
		runtimeService.correlateMessage(
				messageName, String.valueOf(idAtto), esitoAtto);
	}
	
	
	/**
	 * @param isOdgGiunta
	 * @param idAtto
	 * @param resocontoDTO
	 *
	 * CASISTICA ELIMINATA
	 *
	public void sendNumerazioneAtto(long idAtto, long idProfilo) {
		
		BpmThreadLocalUtil.setProfiloId(idProfilo);
		BpmThreadLocalUtil.setNomeAttivita(NomiAttivitaAtto.NUMERAZIONE_PROVVEDIMENTO);
		
		runtimeService.correlateMessage(
				MessageKeys.NUMERAZIONE_ATTO, String.valueOf(idAtto));
	}
	*/
	
	/**
	 * @param task
	 * @return
	 */
	public String getBusinessKey(String processInstanceId) {
		ProcessInstance processInstance = runtimeService.createProcessInstanceQuery()
				.processInstanceId(processInstanceId).singleResult();
		
		if (processInstance != null) {
			return processInstance.getBusinessKey();
		}
		return null;
	}
	
	
	
	/**
	 * @param codiceTipoAtto
	 * @return
	 */
	public List<Long> getIdsAttiItinere(String codiceTipoAtto) {
		
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
		if (!StringUtil.isNull(codiceTipoAtto)) {
			query.variableValueEquals(AttoProcessVariables.TIPO_ATTO, StringUtil.trimStr(codiceTipoAtto));
		}
		
		List<ProcessInstance> foundProc = query.list();
				
		// Evito duplicati
		Set<Long> result = new HashSet<Long>();
		for (ProcessInstance proc : foundProc) {
			result.add(new Long(proc.getBusinessKey()));
		}
		
		return new ArrayList<Long>(result);
	}
	
	
	public List<ProcessInstance> processInstanceQuery(String processDefinitionKey, long idAtto, Map<String, Object> processVariables) {
		ProcessInstanceQuery query = runtimeService.createProcessInstanceQuery();
		
		if (!StringUtil.isNull(processDefinitionKey)) {
			query = query.processDefinitionKey(StringUtil.trimStr(processDefinitionKey));
		}
		if (idAtto > 0) {
			query = query.processInstanceBusinessKey(String.valueOf(idAtto));
		}
		
		if (processVariables != null) {
			Iterator<Entry<String, Object>> pi = processVariables.entrySet().iterator();
			while (pi.hasNext()) {
				Entry<String, Object> entry = pi.next();
				query = query.variableValueEquals(StringUtil.trimStr(entry.getKey()), entry.getValue());
			}
		}
		
		return query.list();
	}
	
	public List<Task> processInstanceQuery(String processDefinitionKey, 
			long idAtto, String variableName, String variableLike) {
		
		if (StringUtil.isNull(variableName) || StringUtil.isNull(variableLike)) {
			throw new IllegalArgumentException("Occorre specificare il nome e il valore della variabile di processo.");
		}
		
		TaskQuery query = taskService.createTaskQuery();
		
		if (!StringUtil.isNull(processDefinitionKey)) {
			query = query.processDefinitionKey(StringUtil.trimStr(processDefinitionKey));
		}
		if (idAtto > 0) {
			query = query.processInstanceBusinessKey(String.valueOf(idAtto));
		}
		
		query = query.processVariableValueLike(StringUtil.trimStr(variableName), "%" + StringUtil.trimStr(variableLike) + "%");
		return query.list();
	}
	
	
	public void eliminaIstanzaProcesso(String processInstanceId, String motivazione) {
		if (StringUtil.isNull(motivazione)) {
			return;
		}
		runtimeService.deleteProcessInstance(processInstanceId, StringUtil.trimStr(motivazione));
	}
	
	
	
	/**
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 * @throws IOException 
	 * @throws JsonMappingException 
	 * @throws JsonParseException 
	 */
	public Iterable<DecisioneWorkflowDTO> getPulsantiWorkflow(final String taskBpmId, final long profiloId) 
			throws Exception {
		
		// Giorgio: modificato per gestione qualifica, rientra nella casistica generale
		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		
		if ((task == null) || (!task.getAssignee().equalsIgnoreCase(usernameBpm) && 
			!task.getAssignee().startsWith(usernameBpm+Constants.BPM_INCARICO_SEPARATOR))) {
			return null;
		}
		
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DECISIONI);
		
		Iterable<DecisioneWorkflowDTO> retVal = new ArrayList<DecisioneWorkflowDTO>();
		if (taskVar == null) {
			return retVal;
		}
			
		String valDecisioni = (String)taskVar.getValue();
		List<DecisioneWorkflowDTO> decisioni = new ObjectMapper().readValue(valDecisioni, new TypeReference<List<DecisioneWorkflowDTO>>() { });
		List<DecisioneWorkflowDTO> decisioniFiltrate = new ArrayList<DecisioneWorkflowDTO>();
		List<Object> azioniDisabilitate = WebApplicationProps.getPropertyList(ConfigPropNames.AZIONI_DISABILITATE_X_TASK);
		for (DecisioneWorkflowDTO decisione : decisioni) {
			if(!isOperazioneDisabilitata(task,decisione,azioniDisabilitate)) {
				decisioniFiltrate.add(decisione);
			}
			
		}
		return decisioniFiltrate;
	}
	
	private boolean isOperazioneDisabilitata(Task task, DecisioneWorkflowDTO decisione, List<Object> azioniDisabilitate) {
		if(azioniDisabilitate!=null && azioniDisabilitate.size()>0) {
			for (Object azioneDisabilitata : azioniDisabilitate) {
				if(azioneDisabilitata!=null) {
					String azDis = azioneDisabilitata.toString();
					if(azDis.equalsIgnoreCase(task.getTaskDefinitionKey()+"#"+decisione.getCodiceAzioneUtente())){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param taskBpmId
	 * @return
	 * @throws Exception
	 */
	public List<String> getCodiceAzioneMassiva(final String taskBpmId) {
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.TASK_OPERAZIONE_MASSIVA);
		if (taskVar != null) {
			String valMassiva = (String)taskVar.getValue();
			if (!StringUtil.isNull(valMassiva)) {
				int idxSep = valMassiva.indexOf(AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP);
				if (idxSep > 0) {
					valMassiva = valMassiva.substring(idxSep + AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP.length());
					return Arrays.asList(valMassiva.split(AttoProcessVariables.TASK_OPERAZIONE_MASSIVA_SEP));
				}
			} 
		}
		return null;
	}
	
	
	/**
	 * @param taskBpmId
	 * @return
	 * @throws Exception
	 */
	public Iterable<AssegnazioneIncaricoDTO> getAssegnazioniIncarichi(final String taskBpmId)
			throws Exception {
		
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.ASSEGNAZIONE_INCARICHI);
				
		Iterable<AssegnazioneIncaricoDTO> retVal = new ArrayList<AssegnazioneIncaricoDTO>();
		if (taskVar == null) {
			return retVal;
		}
		
		String valAssegnazioni = (String)taskVar.getValue();
		
		return new ObjectMapper().readValue(valAssegnazioni, new TypeReference<List<AssegnazioneIncaricoDTO>>() { });
	}

	/**
	 * Restituisce, per uno specifico task, una lista di TipoDocumento per i quali
	 * visualizzare una preview.
	 * 
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<TipoDocumentoDto> getElencoTipoDocumentoPreview(final String taskBpmId) throws Exception {
		
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_ANTEPRIMA);
		if (taskVar == null) {
			return null;
		}
		
		List<TipoDocumentoDto> elencoTipoDocumentoDto = null;
		List<String> elencoCodiceTipoDocumento = new ArrayList<String>();
		if (taskVar.getValue() instanceof List) {
			elencoCodiceTipoDocumento = (List<String>) taskVar.getValue();
		} else if (taskVar.getValue() instanceof String) {
			elencoCodiceTipoDocumento.add((String) taskVar.getValue());
		}
		
		if(elencoCodiceTipoDocumento!=null) {
			elencoTipoDocumentoDto = new ArrayList<>();

			for(String codiceTipoDocumento : elencoCodiceTipoDocumento) {
				TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(codiceTipoDocumento);
				List<ModelloHtml> listModelloHtml= modelloHtmlService.findByTipoDocumento(codiceTipoDocumento);
				
				TipoDocumentoDto tipoDocumentoDto = TipoDocumentoConverter.convertToDto(tipoDocumento);
				
				List<ModelloHtmlDto> elencoModelloHtmlDto = null;
				if(listModelloHtml!=null) {
					elencoModelloHtmlDto = new ArrayList<>();
					for(ModelloHtml modelloHtml : listModelloHtml) {
						ModelloHtmlDto modelloHtmlDto = new ModelloHtmlDto();
						modelloHtmlDto.setIdModelloHtml(modelloHtml.getId());
						modelloHtmlDto.setTitolo(modelloHtml.getTitolo());
						elencoModelloHtmlDto.add(modelloHtmlDto);
					}
				}
				
				tipoDocumentoDto.setElencoModelloHtml(elencoModelloHtmlDto);
				
				elencoTipoDocumentoDto.add(tipoDocumentoDto);
			}
		}

		return elencoTipoDocumentoDto;
	}
	
	
	
	/**
	 * Restituisce, per uno specifico task, una lista di TipoDocumento per i quali
	 * visualizzare una preview.
	 * 
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<TipoDocumentoDto> getElencoTipoDocumentoDaGenerare(final String taskBpmId) throws Exception {
		
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_GENERAZIONE);
		if (taskVar == null) {
			return null;
		}
		
		List<TipoDocumentoDto> elencoTipoDocumentoDto = null;
		List<String> elencoCodiceTipoDocumento = new ArrayList<String>();
		if (taskVar.getValue() instanceof List) {
			elencoCodiceTipoDocumento = (List<String>) taskVar.getValue();
		} else if (taskVar.getValue() instanceof String) {
			elencoCodiceTipoDocumento.add((String) taskVar.getValue());
		}
		
		if(elencoCodiceTipoDocumento!=null) {
			elencoTipoDocumentoDto = new ArrayList<>();

			for(String codiceTipoDocumento : elencoCodiceTipoDocumento) {
				TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(codiceTipoDocumento);
				List<ModelloHtml> listModelloHtml= modelloHtmlService.findByTipoDocumento(codiceTipoDocumento);
				
				TipoDocumentoDto tipoDocumentoDto = TipoDocumentoConverter.convertToDto(tipoDocumento);
				
				List<ModelloHtmlDto> elencoModelloHtmlDto = null;
				if(listModelloHtml!=null) {
					elencoModelloHtmlDto = new ArrayList<>();
					for(ModelloHtml modelloHtml : listModelloHtml) {
						ModelloHtmlDto modelloHtmlDto = new ModelloHtmlDto();
						modelloHtmlDto.setIdModelloHtml(modelloHtml.getId());
						modelloHtmlDto.setTitolo(modelloHtml.getTitolo());
						elencoModelloHtmlDto.add(modelloHtmlDto);
					}
				}
				
				tipoDocumentoDto.setElencoModelloHtml(elencoModelloHtmlDto);
				
				elencoTipoDocumentoDto.add(tipoDocumentoDto);
			}
		}

		return elencoTipoDocumentoDto;
	}
	
	public List<DocumentoPdfDto> getElencoDocumentiDaFirmare(final String taskBpmId) throws Exception {
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_FIRMA);
		if (taskVar == null) {
			return null;
		}
		
		List<DocumentoPdfDto> elencoDocumentoPdfDto = null;
		List<String> elencoCodiceTipoDocumento = new ArrayList<String>();
		
		if (taskVar.getValue() instanceof List) {
			elencoCodiceTipoDocumento = (List<String>) taskVar.getValue();
		} else if (taskVar.getValue() instanceof String) {
			elencoCodiceTipoDocumento.add((String) taskVar.getValue());
		}
	
		if(elencoCodiceTipoDocumento!=null) {
			Atto atto = getAttoByTaskId(taskBpmId);
			elencoDocumentoPdfDto = new ArrayList<DocumentoPdfDto>();

			for(String codiceTipoDocumento : elencoCodiceTipoDocumento) {
				//TipoDocumento tipoDocumento = tipoDocumentoService.findByCodice(codiceTipoDocumento);
				List<DocumentoPdfDto> listDocPdfDaAtto = findDocumentiInAtto(atto, codiceTipoDocumento);
				if(listDocPdfDaAtto!=null && listDocPdfDaAtto.size() > 0){
					for(DocumentoPdfDto doc : listDocPdfDaAtto) {
						elencoDocumentoPdfDto.add(doc);
					}
				}
			}
		}

		return elencoDocumentoPdfDto;
	}
	
	public List<String> getElencoDocumentiSolaGenerazione(final String taskBpmId) throws Exception {
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_FIRMA);
		List<String> elencoSolaGenerazione = new ArrayList<String>();
		
		if (taskVar == null) {
			return null;
		}else {
			List<String> elencoFirma = new ArrayList<String>();
			if (taskVar.getValue() instanceof List) {
				elencoFirma = (List<String>) taskVar.getValue();
			} else if (taskVar.getValue() instanceof String) {
				elencoFirma.add((String) taskVar.getValue());
			}
			taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_GENERAZIONE);
			if (taskVar!=null && taskVar.getValue() instanceof List) {
				elencoSolaGenerazione = (List<String>) taskVar.getValue();
			} else if (taskVar!=null && taskVar.getValue() instanceof String) {
				elencoSolaGenerazione.add((String) taskVar.getValue());
			}
			elencoSolaGenerazione.removeAll(elencoFirma);
		}
		
		return elencoSolaGenerazione;
	}
	
	public List<String> getCodiciTipiDocumentoDaFirmare(final String taskBpmId) throws Exception {
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.LISTA_DOCUMENTI_FIRMA);
		if (taskVar == null) {
			return null;
		}
		
		List<String> elencoCodiceTipoDocumento = new ArrayList<String>();
		
		if (taskVar.getValue() instanceof List) {
			elencoCodiceTipoDocumento = (List<String>) taskVar.getValue();
		} else if (taskVar.getValue() instanceof String) {
			elencoCodiceTipoDocumento.add((String) taskVar.getValue());
		}

		return elencoCodiceTipoDocumento;
	}
	
	
	public List<TipoDocumentoDto> getListaTipoDocumento(Long idAtto, boolean firma){
		List<TipoDocumentoDto> listTipoDocumentoDto = null;
		
		if(idAtto!=null && idAtto>0){
			
			String taskBpmId = getNextTaskId(idAtto, false);
			if(firma){
				//listTipoDocumentoDto = this.getElencoDocumentiDaFirmare(taskBpmId);
			}
			else{
				try {
					listTipoDocumentoDto = this.getElencoTipoDocumentoDaGenerare(taskBpmId);
				}
				catch (Exception e) {
					log.error("Errore di lettura dei documenti da generare", e);
				}
			}
		}
		
		return listTipoDocumentoDto;
	}

	
	private List<DocumentoPdfDto> findDocumentiInAtto(Atto atto, String codiceTipoDocumento) {
		
		List<DocumentoPdfDto> listDocumentoPdfDto = new ArrayList<DocumentoPdfDto>(); 
		DocumentoPdfDto docPdfDto = null;
		
		
		if(atto!=null && atto.getDocumentiPdf() != null){
			for (int i = 0; i < atto.getDocumentiPdf().size(); i++) {
				DocumentoPdf app = atto.getDocumentiPdf().get(i);
				if(app.getTipoDocumento().getCodice().equals(codiceTipoDocumento)){
					DocumentoPdf doc = documentoPdfService.findById(app.getId());
					Aoo aooSerieDoc = null;
					if(doc!=null && doc.getAooSerieId()!=null) {
						aooSerieDoc = aooService.findOne(doc.getAooSerieId());
					}
					docPdfDto = DocumentoPdfConverter.convertToDto(doc, aooSerieDoc);
					listDocumentoPdfDto.add(docPdfDto);
					break;
				}
			}
			
		}
		
		if(atto!=null && atto.getDocumentiPdfOmissis() != null){
			for (int i = 0; i < atto.getDocumentiPdfOmissis().size(); i++) {
				DocumentoPdf app = atto.getDocumentiPdfOmissis().get(i);
				if(app.getTipoDocumento().getCodice().equals(codiceTipoDocumento)){
					DocumentoPdf doc = documentoPdfService.findById(app.getId());
					Aoo aooSerieDoc = null;
					if(doc!=null && doc.getAooSerieId()!=null) {
						aooSerieDoc = aooService.findOne(doc.getAooSerieId());
					}
					docPdfDto = DocumentoPdfConverter.convertToDto(doc, aooSerieDoc);
					listDocumentoPdfDto.add(docPdfDto);
					break;
				}
			}
		}
		
		if(atto!=null && atto.getDocumentiPdfAdozione() != null){
			for (int i = 0; i < atto.getDocumentiPdfAdozione().size(); i++) {
				DocumentoPdf app = atto.getDocumentiPdfAdozione().get(i);
				if(app.getTipoDocumento().getCodice().equals(codiceTipoDocumento)){
					DocumentoPdf doc = documentoPdfService.findById(app.getId());
					Aoo aooSerieDoc = null;
					if(doc!=null && doc.getAooSerieId()!=null) {
						aooSerieDoc = aooService.findOne(doc.getAooSerieId());
					}
					docPdfDto = DocumentoPdfConverter.convertToDto(doc, aooSerieDoc);
					listDocumentoPdfDto.add(docPdfDto);
					break;
				}
			}
		}
		
		if(atto!=null && atto.getDocumentiPdfAdozioneOmissis() != null){
			
			for (int i = 0; i < atto.getDocumentiPdfAdozioneOmissis().size(); i++) {
				DocumentoPdf app = atto.getDocumentiPdfAdozioneOmissis().get(i);
				if(app.getTipoDocumento().getCodice().equals(codiceTipoDocumento)){
					DocumentoPdf doc = documentoPdfService.findById(app.getId());
					Aoo aooSerieDoc = null;
					if(doc != null && doc.getAooSerieId()!=null) {
						aooSerieDoc = aooService.findOne(doc.getAooSerieId());
					}
					docPdfDto = DocumentoPdfConverter.convertToDto(doc, aooSerieDoc);
					listDocumentoPdfDto.add(docPdfDto);
					break;
				}
			}
		}
			
		return listDocumentoPdfDto;
	}


	/**
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Iterable<String> getScenariDisabilitazione(final String taskBpmId) 
			throws Exception {
		
		ArrayList<String> retVal = new ArrayList<String>();
		
		VariableInstance taskVar = getVariabileTask(taskBpmId, AttoProcessVariables.SCENARI_DISABILITAZIONE);
		if (taskVar == null) {
			return retVal;
		}
		
		if (taskVar.getValue() instanceof List) {
			return (Iterable<String>)taskVar.getValue();
		}
		else if (taskVar.getValue() instanceof String) {
			retVal.add((String)taskVar.getValue());
		}
		
		return retVal;
	}
	
	
	/**
	 * @param taskBpmId
	 * @return
	 * @throws DatatypeConfigurationException
	 */
	public TipoTaskDTO getDettaglioTask(final String taskBpmId) throws DatatypeConfigurationException {
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		Atto atto = getAttoByTask(task);
		
		if (atto != null) {
			return tipoTaskConverter.toTipoTask(task, atto);
		}
		
		return null;
	}
	
	/**
	 * @param taskBpmId
	 * @return
	 */
	public Atto getAttoByTaskId(final String taskBpmId) {
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		return getAttoByTask(task);
	}
	
	
	/**
	 * @param task
	 * @return
	 */
	public Atto getAttoByTask(final Task task) {
		if (task == null) {
			return null;
		}
		
		String businessKey = getBusinessKey(task.getProcessInstanceId());
		
		// La business key deve corrispondere all'id Atto
		long idAtto = Long.parseLong(businessKey.trim());
		return attoRepository.findOne(idAtto);
	}
	
	
	/**
	 * @param taskBpmId
	 * @return
	 */
	public List<VariableInstance> getVariabiliTask(final String taskBpmId) {
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		VariableInstanceQuery queryVar = runtimeService.createVariableInstanceQuery()
				.executionIdIn(task.getExecutionId());
		
		return queryVar.list();
	}
	
	/**
	 * @param taskBpmId
	 * @return
	 */
	public List<VariableInstance> getVariabiliProcess(final String taskBpmId) {
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		
		VariableInstanceQuery queryVar = runtimeService.createVariableInstanceQuery()
				.processInstanceIdIn(task.getProcessInstanceId());
		
		return queryVar.list();
	}
	
	
	/**
	 * @param taskBpmId
	 * @param nomeVar
	 * @return
	 */
	public VariableInstance getVariabileTask(final String taskBpmId, String nomeVar) {
		if (StringUtil.isNull(nomeVar)) {
			return null;
		}
		
		List<VariableInstance> taskVar = getVariabiliTask(taskBpmId);
		for (VariableInstance variableInstance : taskVar) {
			if (StringUtil.trimStr(variableInstance.getName()).equalsIgnoreCase(nomeVar)) {
				return variableInstance;
			}
		}
		
		List<VariableInstance> processVar = getVariabiliProcess(taskBpmId);
		for (VariableInstance variableInstance : processVar) {
			if (StringUtil.trimStr(variableInstance.getName()).equalsIgnoreCase(nomeVar)) {
				return variableInstance;
			}
		}
		
		return null;
	}
	
	
	/**
	 * @param taskBpmId
	 * @param profiloId
	 * @param decisione
	 */
	@Transactional
	public void eseguiAzione(final String taskBpmId, final Long profiloId, final DecisioneWorkflowDTO decisione, String newTipoIter) 
		throws Exception {
		// Setto Threadlocal con Id Profilo e attività per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
		BpmThreadLocalUtil.setNomeAttivita(decisione.getDescrizioneAlternativa()!=null?decisione.getDescrizioneAlternativa():decisione.getDescrizione());
		BpmThreadLocalUtil.setCodiceDecisioneLocal(decisione.getCodiceAzioneUtente());
		
		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		
		// Considero i task che dipendono anche dalla qualifica e non
		if ((task == null) || (!task.getAssignee().equalsIgnoreCase(usernameBpm) &&
			!task.getAssignee().startsWith(usernameBpm+Constants.BPM_INCARICO_SEPARATOR))) {
			// TODO: sollevare eccezione  ????
			return;
		}
		
		// Gestione assegnazione incarichi
		Iterable<AssegnazioneIncaricoDTO> assegnazioni = getAssegnazioniIncarichi(taskBpmId);
		if (assegnazioni != null && !Iterables.isEmpty(assegnazioni)) {
			String businessKey = getBusinessKey(task.getProcessInstanceId());
			
			// La business key deve corrispondere all'id Atto
			long idAtto = Long.parseLong(businessKey.trim());		
			bpmWrapperUtil.salvaAssegnazioniIncarichi(idAtto, taskBpmId, assegnazioni);
		}

		Map<String, Object> variables = null;
		if (!StringUtil.isNull(decisione.getVariableSet())) {
			variables = new HashMap<String, Object>();
			variables.put(StringUtil.trimStr(decisione.getVariableSet()), StringUtil.trimStr(decisione.getVariableValue()));
		}
		
		if(newTipoIter!=null && !newTipoIter.isEmpty()) {
			if(variables == null) {
				variables = new HashMap<String, Object>();
			}
			variables.put(AttoProcessVariables.TIPO_ITER, newTipoIter);
		}
		
		//eventuale delega
		String profDelegante = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
		if (!StringUtil.isNull(profDelegante)) {
			Profilo profiloDelegante = bpmWrapperUtil.getProfiloByUsernameBpm(profDelegante);
			if (profiloDelegante != null) {
				BpmThreadLocalUtil.setProfiloDeleganteId(profiloDelegante.getId());
			}
		}
		
		//eventuale riassegnazione
		String profOriginarioStr = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
		if (!StringUtil.isNull(profOriginarioStr)) {
			Profilo profOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioStr);
			if (profOriginario != null) {
				BpmThreadLocalUtil.setProfiloOriginarioId(profOriginario.getId());
			}
		}
		
		if(decisione.isCompletaTask()) {
			taskService.complete(taskBpmId, variables);
		}
		else if( (variables != null) && (!variables.isEmpty()) ) {
			taskService.setVariables(taskBpmId, variables);
		}
	}
	
	
	public void eseguiTask(final String taskBpmId, final Long profiloId, final String nomeAttivita) {
		// Setto Threadlocal con Id Profilo e attività per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
		BpmThreadLocalUtil.setNomeAttivita(nomeAttivita);
		
		taskService.complete(taskBpmId);
	}
	
	public void eseguiTask(final String taskBpmId, final Long profiloId, final String nomeAttivita, final String note) {
		// Setto Threadlocal con Id Profilo e attività per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
		BpmThreadLocalUtil.setNomeAttivita(nomeAttivita);
		BpmThreadLocalUtil.setMotivazione(note);
		taskService.complete(taskBpmId);
	}
	
	public void effettuaRiassegnazionePerDelegaSingolaLavorazione(final boolean logAvanzamento, final String taskBpmId, final String assigneeOriginario, final String nuovoAssignee, Long profiloOperatoreId, final Atto atto, final Profilo profiloDelegato, final Profilo profiloDelegante) throws GestattiCatchedException {
		if(profiloOperatoreId!=null && profiloOperatoreId.equals(0L)) {
			profiloOperatoreId = profiloDelegante.getId();
		}
		if(profiloOperatoreId!=null) {
			BpmThreadLocalUtil.setProfiloId(profiloOperatoreId);
		}
		
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if(task!=null && task.getAssignee().startsWith(assigneeOriginario)) {
			taskService.setVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE, task.getAssignee());
			String delegaInfo = task.getAssignee() + Constants.DELEGA_INFO_SEPARATOR;
			taskService.setVariableLocal(taskBpmId, AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE, true);
			String delegato = task.getAssignee().replaceAll(assigneeOriginario, nuovoAssignee);
			taskService.setAssignee(taskBpmId, delegato);
			delegaInfo += delegato;
			taskService.setVariableLocal(taskBpmId, AttoProcessVariables.DELEGA_ONE_TASK_ONLY, delegaInfo);
			if(logAvanzamento) {
				String nome = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(task.getAssignee()));
				registrazioneAvanzamentoService.impostaStatoAtto(
						atto.getId().longValue(), profiloOperatoreId, atto.getStato(), NomiAttivitaAtto.DELEGA_SINGOLA_LAVORAZIONE,
						"Effettuata delega su singola lavorazione. Il task in carico in precedenza a " + nome
						+ " \u00E8 stato assegnato a " + profiloDelegato.getUtente().getNome() + " " + profiloDelegato.getUtente().getCognome() + ".");
			}
		}else {
			throw new GestattiCatchedException("Il task risulta lavorato per cui la delega non pu\u00F2 essere effettuata.");
		}
	}
	
	public void effettuaRiassegnazioneLavorazioniInteroIter(final boolean logAvanzamento, final String assigneeOriginario, final String nuovoAssignee, Long profiloOperatoreId, final Atto atto, final Profilo profiloDelegato, final Profilo profiloDelegante) throws GestattiCatchedException {
		if(profiloOperatoreId!=null && profiloOperatoreId.equals(0L)) {
			profiloOperatoreId = profiloDelegante.getId();
		}
		if(profiloOperatoreId!=null) {
			BpmThreadLocalUtil.setProfiloId(profiloOperatoreId);
		}
		String businessKey = atto.getId().toString();
		List<Execution> executions = runtimeService.createExecutionQuery().processInstanceBusinessKey(businessKey).active().list();
		List<Task> tasks = taskService.createTaskQuery().processInstanceBusinessKey(businessKey).active().list();
		for(Task task : tasks) {
			if(task!=null && task.getAssignee()!=null && task.getAssignee().startsWith(assigneeOriginario)) {
				taskService.setVariableLocal(task.getId(), AttoProcessVariables.PROFILO_DELEGANTE, task.getAssignee());
				String delegante = task.getAssignee().split(Constants.BPM_INCARICO_SEPARATOR)[0];
				taskService.setVariableLocal(task.getId(), AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE, true);
				String delegato = task.getAssignee().replaceAll(assigneeOriginario, nuovoAssignee);
				taskService.setAssignee(task.getId(), delegato);
				for(Execution e : executions) {
					@SuppressWarnings("unchecked")
					Map<String, String> mapDeleganteDelegato = (Map<String, String>)runtimeService.getVariable(e.getId(), AttoProcessVariables.DELEGA_TASK_FULL_ITER);
					if(mapDeleganteDelegato==null) {
						mapDeleganteDelegato = new HashMap<String, String>();
					}
					mapDeleganteDelegato.put(delegante, delegato);
					runtimeService.setVariable(e.getId(), AttoProcessVariables.DELEGA_TASK_FULL_ITER, mapDeleganteDelegato);
				}
				if(logAvanzamento) {
					String nome = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(task.getAssignee()));
					registrazioneAvanzamentoService.impostaStatoAtto(
							atto.getId().longValue(), profiloOperatoreId, atto.getStato(), NomiAttivitaAtto.DELEGA_SINGOLA_LAVORAZIONE, 
							"Effettuata delega su intero iter. I task in carico " +  nome
							+ " saranno assegnati a " + profiloDelegato.getUtente().getNome() + " " + profiloDelegato.getUtente().getCognome() + ".");
				}
			}
		}
	}
	
	public void rimuoviDelegaSingolaLavorazione(final boolean logAvanzamento, final String taskBpmId, final String assigneeOriginario, final String nuovoAssignee, Long profiloOperatoreId, final Atto atto, final Profilo profiloDelegante) throws GestattiCatchedException {
		if(profiloOperatoreId!=null && profiloOperatoreId.equals(0L)) {
			profiloOperatoreId = profiloDelegante.getId();
		}
		if(profiloOperatoreId!=null) {
			BpmThreadLocalUtil.setProfiloId(profiloOperatoreId);
		}
		
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().taskAssignee(nuovoAssignee).singleResult();
		String delegaInfo = null;
		//considero l'eventualità che il task assignee abbia l'assegnazione incarico
		if(task == null) {
			Task t = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().taskAssigned().singleResult();
			if(t.getAssignee().startsWith(nuovoAssignee) || nuovoAssignee.startsWith(t.getAssignee())) {
				delegaInfo = (String)taskService.getVariableLocal(t.getId(), AttoProcessVariables.DELEGA_ONE_TASK_ONLY);
			}
		}else {
			delegaInfo = (String)taskService.getVariableLocal(task.getId(), AttoProcessVariables.DELEGA_ONE_TASK_ONLY);
		}
		if(delegaInfo!=null && !delegaInfo.isEmpty()) {
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE);
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.DELEGA_ONE_TASK_ONLY);
			String deleganteOriginario = delegaInfo.split(Constants.DELEGA_INFO_SEPARATOR)[0];
			taskService.setAssignee(taskBpmId, deleganteOriginario);
			if(logAvanzamento) {
				String nomeAssignee = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(nuovoAssignee));
				String nomeAssigneeOriginario = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(assigneeOriginario));
				registrazioneAvanzamentoService.impostaStatoAtto(
						atto.getId().longValue(), profiloOperatoreId, atto.getStato(), NomiAttivitaAtto.RIMOSSA_DELEGA_SINGOLA_LAVORAZIONE, 
						"Rimossa delega su singola lavorazione. Il task in carico in precedenza a " + nomeAssignee
						+ " \u00E8 stato assegnato a " + nomeAssigneeOriginario + ".");
			}
		}else {
			task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().taskAssigned().singleResult();
			if(task!=null) {
				String nomeAssignee = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(task.getAssignee()));
				throw new GestattiCatchedException("Il task risulta assegnato ad un utente differente rispetto alla delega, ossia " + nomeAssignee);
			}else {
				throw new GestattiCatchedException("Il task risulta non riassegnabile.");
			}
		}
	}
	
	public void rimuoviDelegaLavorazioniInteroIter(final boolean logAvanzamento, final String assigneeOriginario, final String nuovoAssignee, Long profiloOperatoreId, final Atto atto, final Profilo profiloDelegante) throws GestattiCatchedException {
		if(profiloOperatoreId!=null && profiloOperatoreId.equals(0L)) {
			profiloOperatoreId = profiloDelegante.getId();
		}
		if(profiloOperatoreId!=null) {
			BpmThreadLocalUtil.setProfiloId(profiloOperatoreId);
		}
		String delegaInfo = null;
		String businesskey = atto.getId().toString();
		List<Execution> executions = runtimeService.createExecutionQuery().processInstanceBusinessKey(businesskey).active().list();
		for(Execution e : executions) {
			@SuppressWarnings("unchecked")
			Map<String, String> mapDeleganteDelegato = (Map<String, String>)runtimeService.getVariable(e.getId(), AttoProcessVariables.DELEGA_TASK_FULL_ITER);
			if(mapDeleganteDelegato!=null) {
				Set<String> toRemove = new HashSet<String>();
				for(String delegante : mapDeleganteDelegato.keySet()) {
					if(delegante.startsWith(assigneeOriginario) || assigneeOriginario.startsWith(delegante)) {
						delegaInfo = delegante;
						toRemove.add(delegante);
					}
				}
				for(String delegante : toRemove) {
					mapDeleganteDelegato.remove(delegante);
				}
				if(mapDeleganteDelegato.size() == 0) {
					runtimeService.removeVariable(e.getId(), AttoProcessVariables.DELEGA_TASK_FULL_ITER);
				}else {
					runtimeService.setVariable(e.getId(), AttoProcessVariables.DELEGA_TASK_FULL_ITER, mapDeleganteDelegato);
				}
			}
		}
		String deleganteInfo = delegaInfo.split(Constants.DELEGA_INFO_SEPARATOR)[0];
		List<Task> tasks = taskService.createTaskQuery().processInstanceBusinessKey(businesskey).active().taskVariableValueEquals(AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE, true).taskVariableValueLike(AttoProcessVariables.PROFILO_DELEGANTE, deleganteInfo + "%").list();
		for(Task task : tasks) {
			if(task!=null) {
				String deleganteOriginario = (String)taskService.getVariable(task.getId(), AttoProcessVariables.PROFILO_DELEGANTE);
				taskService.removeVariableLocal(task.getId(), AttoProcessVariables.PROFILO_DELEGANTE);
				taskService.removeVariableLocal(task.getId(), AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE);
				taskService.setAssignee(task.getId(), deleganteOriginario);
			}
		}
		if(logAvanzamento) {
			String nomeAssignee = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(nuovoAssignee));
			String nomeAssigneeOriginario = utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(assigneeOriginario));
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), profiloOperatoreId, atto.getStato(), NomiAttivitaAtto.RIMOSSA_DELEGA_SINGOLA_LAVORAZIONE, 
					"Rimossa delega su intero iter. Delega rimossa al delegato " + nomeAssignee 
					+ " per il delegante " + nomeAssigneeOriginario + ".");
		}
	}
	
	/**
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 */
	public boolean prendiInCaricoTask(final String taskBpmId, final Long profiloId, boolean isPresaInCaricoPerDelega) {
		Profilo p = profiloService.findOneBase(profiloId);
		if(p.getFutureEnabled()==null || p.getFutureEnabled().equals(false)) {
			throw new GestattiCatchedException("Impossibile prendere in carico in task. Il vostro profilo risulta disabilitato");
		}
		// Setto Threadlocal con Id Profilo per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
				
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if (task == null) {
			return false;
		}
		
		// Verifico la presa in carico per delega
		String currentAssignee = task.getAssignee();
		if (!StringUtil.isNull(currentAssignee) && !currentAssignee.trim().isEmpty()) {
			if(isPresaInCaricoPerDelega) {
				Object deleganteGiaPresente = taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
				if(deleganteGiaPresente!=null) {
					throw new GestattiCatchedException("Impossibile prendere in carico il task in quanto esso risulta al momento gi\u00E0 preso in carico da un delegato.");
				}
				Profilo profiloDelegante = bpmWrapperUtil.getProfiloByUsernameBpm(currentAssignee);
				if (profiloDelegante != null) {
					List<Delega> delegaLst = delegaRepository.findByDeleganteAndDelegatoActive(profiloDelegante.getId(), profiloId);
					
					if ((delegaLst != null) && (delegaLst.size() > 0)) {
						taskService.setVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE, currentAssignee);
					}
					BpmThreadLocalUtil.setProfiloDeleganteId(profiloDelegante.getId());
				}
			}else {
				throw new GestattiCatchedException("Impossibile prendere in carico il task in quanto esso risulta al momento gi\u00E0 assegnato ad un utente.");
			}
		}
		
		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, -1L);
		taskService.setAssignee(taskBpmId, usernameBpm);
		
		Atto atto = getAttoByTask(task);
		
		registrazioneAvanzamentoService.impostaStatoAtto(
				atto.getId().longValue(), atto.getStato(), NomiAttivitaAtto.ATTO_PRESO_IN_CARICO);
		
		return true;
	}
	
	public boolean riassegnazioneTaskMyOwn(final String taskBpmId, final Long profiloId) {
		Profilo p = profiloService.findOneBase(profiloId);
		if(p.getFutureEnabled()==null || p.getFutureEnabled().equals(false)) {
			throw new GestattiCatchedException("Impossibile prendere in carico in task. Il vostro profilo risulta disabilitato");
		}
		BpmThreadLocalUtil.setProfiloId(profiloId);
				
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if (task == null) {
			return false;
		}
		
		String currentAssignee = null;
		if(task.getAssignee()!=null && !task.getAssignee().isEmpty()) {
			Long currentAssigneeProfId = bpmWrapperUtil.getIdProfiloByUsernameBpm(task.getAssignee());
			if(!profiloService.areProfiliCompatibili(profiloId, currentAssigneeProfId)) {
				long idAtto = Long.parseLong(getBusinessKey(task.getProcessInstanceId()));
				String codiceAtto = attoRepository.findOne(idAtto).getCodiceCifra();
				throw new GestattiCatchedException("Impossibile procedere con la riassegnazione dell\'atto con codice "+codiceAtto+" in quanto il profilo di destinazione non risulta compatibile con quello dell\'attuale assegnatario del task.");
			}
			String profOriginarioCod = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
			currentAssignee = bpmWrapperUtil.getNominativo(task.getAssignee());
			if(profOriginarioCod==null || profOriginarioCod.isEmpty()) {
				Profilo profiloOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee());
				if (profiloOriginario != null) {
					taskService.setVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO, task.getAssignee());
					BpmThreadLocalUtil.setProfiloOriginarioId(profiloOriginario.getId());
				}
			}else {
				Profilo profiloOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioCod);
				BpmThreadLocalUtil.setProfiloOriginarioId(profiloOriginario.getId());
			}
		}
		if(currentAssignee==null) {
			currentAssignee = "n.d.";
		}
		
		String usernameBpm = bpmWrapperUtil.getUsernameForBpm(profiloId, bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(task.getAssignee()));
		Profilo nuovoProf = bpmWrapperUtil.getProfiloByUsernameBpm(usernameBpm);
		taskService.setAssignee(taskBpmId, usernameBpm);
		
		Atto atto = getAttoByTask(task);
		String attivita = "Da " + currentAssignee + " a " + nuovoProf.getUtente().getNome() + " " + nuovoProf.getUtente().getCognome();
//		registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), atto.getStato(), attivita); ogni istanza viene aggiunta nella select del filtro delle attivita
		registrazioneAvanzamentoService.impostaStatoAtto(atto.getId(), profiloId, atto.getStato(), NomiAttivitaAtto.ATTO_PRESO_IN_CARICO_RIASSEGNAZIONE, attivita); //lo considera restituzione (messaggio in giallo)
//		registrazioneAvanzamentoService.impostaStatoAtto(atto.getId().longValue(), atto.getStato(), NomiAttivitaAtto.ATTO_PRESO_IN_CARICO_RIASSEGNAZIONE);
		
		return true;
	}
	
	/**
	 * @param taskBpmId
	 * @param profiloId
	 * @return
	 */
	public boolean rilasciaTask(final String taskBpmId, final Long profiloId, final Long verifyOriginalProfId) {
		// Setto Threadlocal con Id Profilo per registrare successivamente l'avanzamento
		BpmThreadLocalUtil.setProfiloId(profiloId);
				
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if (task == null) {
			return false;
		}
		String noteAggiuntive = null;
		if(verifyOriginalProfId!=null) {
			String atteso = bpmWrapperUtil.getUsernameForBpm(verifyOriginalProfId, null);
			if(task.getAssignee()==null || !task.getAssignee().toLowerCase().startsWith(atteso.toLowerCase())) {
				return false;
			}else {
				Profilo profAss = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee());
				if(profAss!=null && !profAss.getId().equals(profiloId)) {
					long confInc = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(task.getAssignee());
					if(confInc > 0) {
						ConfigurazioneIncarico ci = configurazioneIncaricoRepository.findOne(confInc);
						noteAggiuntive = "Rilasciata attivit\u00E0 di " + ci.getConfigurazioneTask().getNome() + " che era assegnata a " + profAss.getUtente().getNome() + " " + profAss.getUtente().getCognome();
					}else {
						String taskName = taskService.createTaskQuery().active().taskId(taskBpmId).singleResult().getName();
						noteAggiuntive = "Rilasciata attivit\u00E0 di " + taskName + " che era assegnata a " + profAss.getUtente().getNome() + " " + profAss.getUtente().getCognome();
					}
				}
			}
		}
		
		List<String> candidateGroups = bpmWrapperUtil.getCandidategroups(taskBpmId);
		String profDeleganteCod = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
		
		//il task era stato oggetto di riassegnazione e si vuole rilasciare il task al vecchio assegnatario
		String profOriginarioCod = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
		
		Profilo profiloIniziale = null;
		
		// Riassegnazione consentita solo se il task era stato inserito in una coda oppure per delega
		if ((candidateGroups != null) && (candidateGroups.size() > 0)) {
			taskService.setAssignee(taskBpmId, null);
		}
		else if (!StringUtil.isNull(profOriginarioCod)) {
			taskService.setAssignee(task.getId(), profOriginarioCod);
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
			profiloIniziale = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginarioCod);
		}
		else if (!StringUtil.isNull(profDeleganteCod)) {
			taskService.setAssignee(task.getId(), profDeleganteCod);
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
			profiloIniziale = bpmWrapperUtil.getProfiloByUsernameBpm(profDeleganteCod);
		}
		else {
			return false;
		}
		
		Atto atto = getAttoByTask(task);
		if(profiloIniziale!=null) {
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), profiloId, atto.getStato(), NomiAttivitaAtto.ATTO_RILASCIATO, "Il task torna in carico a " + profiloIniziale.getUtente().getNome() + " " + profiloIniziale.getUtente().getCognome());
		}else if(noteAggiuntive==null){
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), atto.getStato(), NomiAttivitaAtto.ATTO_RILASCIATO);
		}else {
			registrazioneAvanzamentoService.impostaStatoAtto(
					atto.getId().longValue(), profiloId, atto.getStato(), NomiAttivitaAtto.ATTO_RILASCIATO, noteAggiuntive);
		}
		
		
		return true;
	}
	
	
	/**
	 * @param profiloDis
	 * @param profiloRias
	 */
	public void riassegnaTaskByProfilo(long idProfiloDisabilita, 
			long idProfiloRiassegna, long idQualificaRiassegna, long profiloResponsabileId) {
		
		Profilo profiloProvenienza = profiloRepository.findOne(idProfiloDisabilita);
		Profilo profiloRiassegna = profiloRepository.findOne(idProfiloRiassegna);
		Profilo profiloResponsabile = profiloRepository.findOne(profiloResponsabileId);
		// Profilo profiloResponsabile = profiloRepository.findOne(idProfiloResponsabile);
		QualificaProfessionale qualificaRiassegna = qualificaProfessionaleRepository.findOne(idQualificaRiassegna);
		
		List<Profilo> profDisabilita = new ArrayList<Profilo>();
		profDisabilita.add(profiloProvenienza);
		
		List<Task> taskAssegnati = getTaskAssegnati(
				profDisabilita, null, null, null, null, null, null);
		
		for (Task task : taskAssegnati) {
			riassegnaTask(task.getId(), profiloRiassegna,
					qualificaRiassegna, profiloResponsabile);
		}
	}
	
	
	/**
	 * @param taskBpmId
	 * @param profiloRiassegnazioneBpm
	 */
	public void riassegnaTask(final String taskBpmId,
			final Profilo profiloRiassegna, final QualificaProfessionale qualificaRiassegna, 
			final Profilo profiloResponsabile) {
		
		if (StringUtil.isNull(taskBpmId)) {
			throw new RuntimeException("Errore: TaskId non specificato.");
		}
		
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if (task == null) {
			throw new RuntimeException("Errore: Impossibile leggere i dati del task " + taskBpmId);
		}
		
		// Dati del task
		long idConfigIncarico = 0;
		Profilo profiloTask = null;
		if(task.getAssignee()!=null) {
			profiloTask = bpmWrapperUtil.getProfiloByUsernameBpm(task.getAssignee());
			idConfigIncarico = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(task.getAssignee());
		}
		// Viene tracciata una sola riassegnazione per ciascun TaskId
		RiassegnazioneIncarico riassIncarico = riassegnazioneIncaricoRepository.findByTaskId(taskBpmId);
		
		if (riassIncarico == null) {
			riassIncarico = new RiassegnazioneIncarico();
			
			riassIncarico.setTaskId(taskBpmId);
			riassIncarico.setTaskName(task.getName());
			if(profiloTask != null) {
				riassIncarico.setProfiloProvenienza(profiloTask);
			}
			
			if (idConfigIncarico > 0) {
				ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(idConfigIncarico);
				riassIncarico.setConfigurazioneIncarico(confIncarico);
			}
		}
		
		riassIncarico.setProfiloAssegnazione(profiloRiassegna);
		riassIncarico.setQualificaAssegnazione(qualificaRiassegna);
		
		riassIncarico.setProfiloResponsabile(profiloResponsabile);
		riassIncarico.setCreatedDate(new DateTime(new Date()));
		if(profiloTask!=null) {
			riassegnazioneIncaricoRepository.save(riassIncarico);
		}
		
		String profiloRiassegnazioneBpm = bpmWrapperUtil
				.getUsernameForBpm(profiloRiassegna.getId().longValue(), idConfigIncarico);
		
		String profOriginarioCod = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
		if(profOriginarioCod==null || profOriginarioCod.isEmpty()) {
			taskService.setVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO, task.getAssignee());
		}else if(profOriginarioCod.equalsIgnoreCase(profiloRiassegnazioneBpm)){
			taskService.removeVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
		}
		
		taskService.setAssignee(taskBpmId, profiloRiassegnazioneBpm);
		
		long idAtto = Long.parseLong(getBusinessKey(task.getProcessInstanceId()));
		String statoAttoVal = attoRepository.findOne(idAtto).getStato();
		
		//TODO viene commentato l'imposta stato atto. Valutare se in alternativa è necesasrio inserire un evento.
		
		
		if(profiloTask!=null) {
			registrazioneAvanzamentoService.impostaStatoAtto(
					idAtto, profiloResponsabile.getId().longValue(), statoAttoVal, 
					"Riassegnazione lavorazione", "Da "+ profiloTask.getUtente().getCognome() + " " + profiloTask.getUtente().getNome()+ " a " + profiloRiassegna.getUtente().getCognome() 
					+ " " + profiloRiassegna.getUtente().getNome());
		}else {
			registrazioneAvanzamentoService.impostaStatoAtto(
					idAtto, profiloResponsabile.getId().longValue(), statoAttoVal, 
					"Assegnazione lavorazione", "A " + profiloRiassegna.getUtente().getCognome() 
					+ " " + profiloRiassegna.getUtente().getNome());
		}
	}
	
	
	/**
	 * @param taskBpmId
	 * @return
	 */
	public ProfiloQualificaBean getProfiloQualificaEsecutore(String taskBpmId) {
		
		if (StringUtil.isNull(taskBpmId)) {
			return null;
		}
		
		Task task = taskService.createTaskQuery().taskId(StringUtil.trimStr(taskBpmId)).active().singleResult();
		if (task == null) {
			return null;
		}
		
		Profilo esecutore = null;
		Profilo profiloOriginario = null;
		String descrQualifica = null;
		
		ConfigurazioneIncaricoProfilo confIncaricoProf = null;
		ConfigurazioneIncaricoAoo confIncaricoAoo = null;
		
		String currentAssignee = task.getAssignee();
		if (!StringUtil.isNull(currentAssignee)) {
			esecutore = bpmWrapperUtil.getProfiloByUsernameBpm(currentAssignee);
		}
		
		if (esecutore == null) {
			log.error("Impossibile accedere alle informazioni dell'esecutore. TaskId: " + taskBpmId);
		}

		String profOriginario = null;
		profOriginario = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_ORIGINARIO);
		if (!StringUtil.isNull(profOriginario)) {
			profiloOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginario);			
		}else {
			profOriginario = (String)taskService.getVariableLocal(taskBpmId, AttoProcessVariables.PROFILO_DELEGANTE);
			if (!StringUtil.isNull(profOriginario)) {
				profiloOriginario = bpmWrapperUtil.getProfiloByUsernameBpm(profOriginario);			
			}
		}
		
		long idConfigIncarico = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(currentAssignee);
		if ((idConfigIncarico < 1) && (!StringUtil.isNull(profOriginario))) {
			idConfigIncarico = bpmWrapperUtil.getIdConfigurazioneIncaricoByUsernameBpm(profOriginario);
		}
		
		Date dataIncarico = null;
		
		// Gestione casistica riassegnazione task
		RiassegnazioneIncarico riassIncarico = riassegnazioneIncaricoRepository.findByTaskId(taskBpmId);
		if (riassIncarico != null) {
			if (riassIncarico.getQualificaAssegnazione() != null) {
				descrQualifica = riassIncarico.getQualificaAssegnazione().getDenominazione();
			}
			
			if (riassIncarico.getConfigurazioneIncarico() != null) {
				ConfigurazioneIncaricoProfiloId confIncaricoId = new ConfigurazioneIncaricoProfiloId();
				confIncaricoId.setIdConfigurazioneIncarico(riassIncarico.getConfigurazioneIncarico().getId().longValue());
				confIncaricoId.setIdProfilo(riassIncarico.getProfiloProvenienza().getId().longValue());
				
				confIncaricoProf = configurazioneIncaricoProfiloRepository.findOne(confIncaricoId);
				
				dataIncarico = riassIncarico.getConfigurazioneIncarico().getDataModifica();
				if (dataIncarico == null) {
					dataIncarico = riassIncarico.getConfigurazioneIncarico().getDataCreazione();
				}
			}
		}
		
		// Profilo e qualifica esecutore se l'incarico è stato dato al profilo
		else if (idConfigIncarico > 0) {
			ConfigurazioneIncaricoProfiloId confIncaricoId = new ConfigurazioneIncaricoProfiloId();
			confIncaricoId.setIdConfigurazioneIncarico(idConfigIncarico);
			confIncaricoId.setIdProfilo(profiloOriginario != null ? profiloOriginario.getId().longValue() : esecutore.getId().longValue());
			
			confIncaricoProf = configurazioneIncaricoProfiloRepository.findOne(confIncaricoId);
			
			//SEGNALAZIONE 20201103 PRODUZIONE
			if(confIncaricoProf!=null && confIncaricoProf.getQualificaProfessionale() != null) {
				descrQualifica = confIncaricoProf.getQualificaProfessionale().getDenominazione();
			}
			
			if (profiloOriginario != null && (profiloOriginario.getId().longValue() != esecutore.getId().longValue())) {
				
				if (!StringUtil.isNull(descrQualifica)) {
					String prefisso = WebApplicationProps.getProperty(ConfigPropNames.ATTO_PREFISSO_DELEGANTE);
					if (StringUtil.isNull(prefisso)) {
						prefisso = PREFISSO_DELEGATO_DEFAULT;
					}
					
					descrQualifica = prefisso + " " + descrQualifica;
				}
			}
			
			ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(idConfigIncarico);
			if (confIncarico != null) {
				dataIncarico = confIncarico.getDataModifica();
				if (dataIncarico == null) {
					dataIncarico = confIncarico.getDataCreazione();
				}
			}
		}
		// Dati configurazione incarico se assegnato ad AOO
		else {
			long idAoo = profiloOriginario != null ? profiloOriginario.getAoo().getId().longValue() : esecutore.getAoo().getId().longValue();							
			confIncaricoAoo = getConfigurazioneIncaricoByAoo(taskBpmId, idAoo);
			
			if (confIncaricoAoo != null && confIncaricoAoo.getPrimaryKey()!=null && confIncaricoAoo.getPrimaryKey().getIdConfigurazioneIncarico()!=null) {
				ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(confIncaricoAoo.getPrimaryKey().getIdConfigurazioneIncarico());
				if (confIncarico != null) {
					dataIncarico = confIncarico.getDataModifica();
					if (dataIncarico == null) {
						dataIncarico = confIncarico.getDataCreazione();
					}
				}
			}
		}
		
		ProfiloQualificaBean retVal = new ProfiloQualificaBean();
		if(!StringUtil.isNull(descrQualifica)) {
			retVal.setDescrizioneQualifica(descrQualifica);
		}
		retVal.setProfilo(esecutore);
		retVal.setConfigurazioneIncaricoProfilo(confIncaricoProf);
		retVal.setConfigurazioneIncaricoAoo(confIncaricoAoo);
		retVal.setDataIncarico(dataIncarico);
		retVal.setDataAvvioTask(task.getCreateTime());
		
		return retVal;
	}
	
	
	public ConfigurazioneIncaricoAoo getConfigurazioneIncaricoByAoo(String taskBpmId, long idAoo) {
		
		List<IdentityLink> identityLinks = taskService.getIdentityLinksForTask(taskBpmId);
		for (IdentityLink identityLink : identityLinks) {
			
			// corrisponde alle costanti definite in IdentityLinkType. "candidate" identifica una relazione di tipo candidate.
			String type = identityLink.getType(); 
			String groupId = identityLink.getGroupId();

			if (IdentityLinkType.CANDIDATE.equals(type) && groupId != null) {
				
				int idxIncarico = groupId.lastIndexOf(Constants.BPM_INCARICO_SEPARATOR);
				if (idxIncarico > -1) {
					long idConfigIncarico = Long.parseLong(groupId.substring(idxIncarico + Constants.BPM_INCARICO_SEPARATOR.length()));
					
					ConfigurazioneIncaricoAooId confIncaricoId = new ConfigurazioneIncaricoAooId();
					confIncaricoId.setIdConfigurazioneIncarico(idConfigIncarico);
					confIncaricoId.setIdAoo(idAoo);
					
					ConfigurazioneIncaricoAoo retVal = configurazioneIncaricoAooRepository.findOne(confIncaricoId);
					if (retVal != null) {
						return retVal;
					}
				}
			}
		}
		return null;
	}
}
