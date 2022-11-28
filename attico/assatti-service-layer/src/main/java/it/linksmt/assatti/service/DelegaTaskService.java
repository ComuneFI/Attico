package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonObject;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.bpm.wrapper.WorkflowServiceWrapper;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DelegaTask;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QDelegaTask;
import it.linksmt.assatti.datalayer.domain.TipoDelegaTaskEnum;
import it.linksmt.assatti.datalayer.repository.DelegaTaskRepository;
import it.linksmt.assatti.service.converter.DelegaTaskConverter;
import it.linksmt.assatti.service.dto.DelegaTaskDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * Service class for managing delega.
 */
@Service
@Transactional
public class DelegaTaskService {

	private final Logger log = LoggerFactory.getLogger(DelegaTaskService.class);
	
	@Inject
	private DelegaTaskRepository delegaTaskRepository;
	
	@Inject
	private WorkflowServiceWrapper workflowServiceWrapper;
	
	@Inject
	private ProfiloService profiloService;
	
	@Inject
	private AooService aooService;
	
	@Transactional(readOnly=true)
	public boolean profiloExistsAsDelegatoByTipoDelega(Long profiloDelegatoId, TipoDelegaTaskEnum tipoDelega) {
		BooleanExpression predicate = QDelegaTask.delegaTask.id.isNotNull();
		Profilo profilo = profiloService.findOneBase(profiloDelegatoId);
		predicate = predicate.and(QDelegaTask.delegaTask.tipo.eq(tipoDelega));
		predicate = predicate.and(QDelegaTask.delegaTask.profiloDelegato.eq(profilo));
		predicate = predicate.and(QDelegaTask.delegaTask.atto.iterStatus.eq("I"));
		predicate = predicate.and(QDelegaTask.delegaTask.enabled.eq(true));
		return delegaTaskRepository.count(predicate) > 0L;
	}
	
	@Transactional(readOnly = true)
	public DelegaTaskDto get(Long id) {
		log.debug("DelegaService :: get() :: id:" + id);
		DelegaTaskDto delegaDto = null;
		DelegaTask domain = delegaTaskRepository.findOne(id);
		if(domain != null ){
			delegaDto = DelegaTaskConverter.convertToDto(domain);
		}
		return delegaDto;
	}
	
	@Transactional(readOnly = true)
	public boolean alreadyExists(DelegaTaskDto delega) {
		boolean exists = false;
		if(delega.getTipo().equalsIgnoreCase(TipoDelegaTaskEnum.ONE_TASK_ONLY.name())) {
			BooleanExpression p = QDelegaTask.delegaTask.taskBpmId.eq(delega.getTaskBpmId()).and(QDelegaTask.delegaTask.enabled.isTrue());
			if(delega.getId()!=null) {
				p = p.and(QDelegaTask.delegaTask.id.ne(delega.getId()));
			}
			exists = delegaTaskRepository.count(p) > 0L;
		}
		return exists;
	}
	
	@Transactional(readOnly = true)
	public boolean isTaskDaLavorare(DelegaTaskDto delega) {
		boolean isTaskDaLavorare = false;
		isTaskDaLavorare = workflowServiceWrapper.existsActiveTask(delega.getTaskBpmId());
		return isTaskDaLavorare;
	}
	
	@Transactional(readOnly = true)
	private boolean isInsertValid(DelegaTaskDto delega) {
		boolean exists = false;
		BooleanExpression p = QDelegaTask.delegaTask.enabled.isTrue().and(QDelegaTask.delegaTask.atto.id.eq(delega.getAtto().getId()));
		BooleanExpression validation = (QDelegaTask.delegaTask.profiloDelegante.id.eq(delega.getProfiloDelegante().getId())); //stesso delegante
		validation = validation.or(QDelegaTask.delegaTask.profiloDelegato.id.eq(delega.getProfiloDelegante().getId())); //delegato un delegante
		validation = validation.or(QDelegaTask.delegaTask.profiloDelegante.id.eq(delega.getProfiloDelegato().getId())); //delegato un delegante
		if(delega.getId()!=null) {
			p = QDelegaTask.delegaTask.id.ne(delega.getId()).and(p);
		}
		p = p.and(validation);
		exists = delegaTaskRepository.count(p) > 0L;
		return !exists;
	}

	@Transactional
	public void save(DelegaTaskDto delegaDto, Long profiloOperatoreId) throws GestattiCatchedException {
		if(this.isInsertValid(delegaDto)) {
			DelegaTask delegaDb = null;
			if(delegaDto.getId()==null || delegaDto.getId()<=0L) {
				delegaDto.setEnabled(true);
				Profilo delegato = profiloService.findOneBase(delegaDto.getProfiloDelegato().getId());
				if(delegato.getFutureEnabled()==null || delegato.getFutureEnabled().equals(false)) {
					throw new GestattiCatchedException("Impossibile inserire come delegato un profilo disabilitato.");
				}
			}else {
				delegaDb = delegaTaskRepository.findOne(delegaDto.getId());
				String tipoDb = delegaDb.getTipo().name();
				boolean logAttivita = false;
				if(!tipoDb.equals(delegaDto.getTipo())) {
					logAttivita = true;
				}
				if(delegaDto.getTipo().equalsIgnoreCase(TipoDelegaTaskEnum.ONE_TASK_ONLY.name())) {
					if(!workflowServiceWrapper.existsActiveTask(delegaDto.getTaskBpmId())) {
						throw new GestattiCatchedException("Il task risulta lavorato per cui la delega non pu\u00F2 essere effettuata.");
					}
				}
				if(tipoDb.equals(TipoDelegaTaskEnum.FULL_ITER.name())) {
					workflowServiceWrapper.rimuoviDelegaLavorazioniInteroIter(logAttivita, delegaDb.getAssigneeOriginario(), delegaDb.getAssigneeDelegato(), profiloOperatoreId, delegaDb.getAtto(), delegaDb.getProfiloDelegante());
				}else if(tipoDb.equals(TipoDelegaTaskEnum.ONE_TASK_ONLY.name())) {
					workflowServiceWrapper.rimuoviDelegaSingolaLavorazione(logAttivita, delegaDb.getTaskBpmId(), delegaDb.getAssigneeOriginario(), delegaDb.getAssigneeDelegato(), profiloOperatoreId, delegaDb.getAtto(), delegaDb.getProfiloDelegante());
				}
			}
			DelegaTask delega = DelegaTaskConverter.convertToModel(delegaDto);
			if(delega.getTipo().name().equals(TipoDelegaTaskEnum.FULL_ITER.name())) {
				workflowServiceWrapper.effettuaRiassegnazioneLavorazioniInteroIter(true, delega.getAssigneeOriginario(), delega.getAssigneeDelegato(), profiloOperatoreId, delega.getAtto(), delega.getProfiloDelegato(), delega.getProfiloDelegante());
			}else if(delega.getTipo().name().equals(TipoDelegaTaskEnum.ONE_TASK_ONLY.name())) {
				workflowServiceWrapper.effettuaRiassegnazionePerDelegaSingolaLavorazione(true, delega.getTaskBpmId(), delega.getAssigneeOriginario(), delega.getAssigneeDelegato(), profiloOperatoreId, delega.getAtto(), delega.getProfiloDelegato(), delega.getProfiloDelegante());
			}else {
				throw new GestattiCatchedException("Tipo non valido");
			}
			delega = delegaTaskRepository.save(delega);
		}else {
			throw new GestattiCatchedException("Impossibile inserire la delega. I motivi potrebbero essere i seguenti\u003A\u003Cbr\u003E" +
		    "- Non \u00E8 possibile inserire lo stesso delegante su pi\u00F9 deleghe riferite allo stesso iter" + "\u003A\u003Cbr\u003E" +
			"- Non \u00E8 possibile inserire come delegante un profilo configurato come delegato in altra delega sullo stesso iter" + "\u003A\u003Cbr\u003E" +
		    "Prima di poter inserire questa delega occorre modificare o cancellare le altre deleghe non conformi a questa");
		}
	}
	
	@Transactional(readOnly=true)
	public List<DelegaTaskDto> findAll() {
		return DelegaTaskConverter.convertToDto(delegaTaskRepository.findAll());
	}
	
	@Transactional(readOnly=true)
	public long countAll() {
		return delegaTaskRepository.count();
	}
	
	@Transactional(readOnly=true)
	public List<DelegaTaskDto> findAll(Integer offset, Integer limit, String orderColumn, String orderDirection) {
		List<DelegaTaskDto> listDelega = null;
		if(orderColumn==null || orderColumn.isEmpty()) {
			orderColumn = "id";
		}
		Pageable pageable = getPageable(offset, limit, orderColumn, orderDirection);
		
		Page<DelegaTask> pDelega = delegaTaskRepository.findAll(pageable);
		if(pDelega!=null) {
			listDelega = new ArrayList<>();
			for(DelegaTask d : pDelega) {
				listDelega.add(DelegaTaskConverter.convertToDto(d));
			}
		}

		return listDelega;
	}
	
	@Transactional(readOnly=true)
	public Page<DelegaTaskDto> search(JsonObject search, Long totalElements, Integer offset, Integer limit, String orderColumn, String orderDirection){
		if(orderColumn==null || orderColumn.isEmpty()) {
			orderColumn = "id";
		}
		List<DelegaTaskDto> lista = null;
		Page<DelegaTask> page = null;
		Pageable paginazione = getPageable(offset, limit, orderColumn, orderDirection);
		if(search == null){
			page = delegaTaskRepository.findAll(QDelegaTask.delegaTask.id.isNull(), paginazione);
		}else{
			BooleanExpression predicate = QDelegaTask.delegaTask.id.isNotNull();
			if(search.has("profiloAooId") && !search.get("profiloAooId").isJsonNull() && !search.get("profiloAooId").getAsString().isEmpty()){
				List<Aoo> aoos = aooService.getAooRicorsiva(Long.parseLong(search.get("profiloAooId").getAsString()));
				predicate = predicate.and(QDelegaTask.delegaTask.profiloDelegante.aoo.in(aoos).and(QDelegaTask.delegaTask.profiloDelegato.aoo.in(aoos)));
			}
			if(search.has("delegante") && !search.get("delegante").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						QDelegaTask.delegaTask.profiloDelegante.utente.cognome.concat(" ")
						.concat(QDelegaTask.delegaTask.profiloDelegante.utente.nome)
						.concat(" (").concat(QDelegaTask.delegaTask.profiloDelegante.descrizione).concat(")")
						.containsIgnoreCase(search.get("delegante").getAsString().trim()));
			}
			if(search.has("delegato") && !search.get("delegato").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						QDelegaTask.delegaTask.profiloDelegato.utente.cognome.concat(" ")
						.concat(QDelegaTask.delegaTask.profiloDelegato.utente.nome)
						.concat(" (").concat(QDelegaTask.delegaTask.profiloDelegato.descrizione).concat(")")
						.containsIgnoreCase(search.get("delegato").getAsString().trim()));
			}
			if(search.has("tipoDelega") && !search.get("tipoDelega").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
					QDelegaTask.delegaTask.tipo.eq(TipoDelegaTaskEnum.valueOf(search.get("tipoDelega").getAsString())));
			}
			if(search.has("delegatoProfId") && !search.get("delegatoProfId").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
					QDelegaTask.delegaTask.profiloDelegato.id.eq(search.get("delegatoProfId").getAsLong()));
				predicate = predicate.and(
						QDelegaTask.delegaTask.enabled.isTrue());
			}
			if(search.has("codiceCifra") && !search.get("codiceCifra").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						QDelegaTask.delegaTask.atto.codiceCifra
						.containsIgnoreCase(search.get("codiceCifra").getAsString().trim()));
			}
			if(search.has("lavorazione") && !search.get("lavorazione").getAsString().trim().equalsIgnoreCase("")){
				predicate = predicate.and(
						QDelegaTask.delegaTask.lavorazione
						.containsIgnoreCase(search.get("lavorazione").getAsString().trim()));
			}
			if(search.has("itinereOnly") && search.get("itinereOnly").getAsBoolean()) {
				predicate = predicate.and(
						QDelegaTask.delegaTask.atto.iterStatus.equalsIgnoreCase("I"));
			}
			page = delegaTaskRepository.findAll(predicate, paginazione);
		}
		Page<DelegaTaskDto> dtoPage = null;
		if(page!=null) {
			totalElements = page.getTotalElements();
			lista = new ArrayList<>();
			for(DelegaTask d : page) {
				lista.add(DelegaTaskConverter.convertToDto(d));
			}
		}
		dtoPage = new PageImpl<DelegaTaskDto>(lista, paginazione, page !=null ? page.getTotalElements() : null);
		return dtoPage;
	}
	
	@Transactional
	public void cancellaDelega(Long idDelega, Long profiloOperatoreId) throws GestattiCatchedException {
		DelegaTask del = delegaTaskRepository.findOne(idDelega);
		boolean exists = workflowServiceWrapper.existsActiveTask(del.getTaskBpmId());
		if(exists || del.getTipo().name().equals(TipoDelegaTaskEnum.FULL_ITER.name())) {
			if ((del.getEnabled() != null) && Boolean.TRUE.equals(del.getEnabled())) {
				del.setEnabled(false);
			}
			if(del.getTipo().name().equals(TipoDelegaTaskEnum.FULL_ITER.name())) {
				workflowServiceWrapper.rimuoviDelegaLavorazioniInteroIter(true, del.getAssigneeOriginario(), del.getAssigneeDelegato(), profiloOperatoreId, del.getAtto(), del.getProfiloDelegante());
			}else if(del.getTipo().name().equals(TipoDelegaTaskEnum.ONE_TASK_ONLY.name())) {
				workflowServiceWrapper.rimuoviDelegaSingolaLavorazione(true, del.getTaskBpmId(), del.getAssigneeOriginario(), del.getAssigneeDelegato(), profiloOperatoreId, del.getAtto(), del.getProfiloDelegante());
			}else {
				throw new GestattiCatchedException("Tipo non valido");
			}
		}else {
			throw new GestattiCatchedException("La lavorazione che si intende cancellare risulta gi\u00E0 lavorata.");
		}
		delegaTaskRepository.save(del);
	}
	
	private Pageable getPageable(Integer offset, Integer limit, String orderColumn, String orderDirection) {
		Pageable pageable = null;

		Direction d = null;
		if(orderDirection==null || orderDirection.isEmpty() || orderDirection.equalsIgnoreCase("DESC")) {
			d = Direction.DESC;
		} else {
			d = Direction.ASC;
		}
		
		Sort sort = new Sort(new Order(d, orderColumn));
		pageable = PaginationUtil.generatePageRequest(offset, limit, sort);
		return pageable;
	}
	
}
