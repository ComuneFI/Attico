package it.linksmt.assatti.bpm.util;

import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.apache.commons.lang.StringUtils;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.bpm.dto.TipoTaskDTO;
import it.linksmt.assatti.bpm.wrapper.BpmWrapperUtil;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.Avanzamento;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.utility.DateUtil;
import it.linksmt.assatti.utility.StringUtil;

@Service
public final class TipoTaskConverter {
	
	@Inject
	private BpmWrapperUtil bpmWrapperUtil;
	
	@Inject
	private TaskService taskService;
	
	@Inject
	private UtenteService utenteService;
	
	private DatatypeFactory XML_DATA_TYPE_FACTORY = null;

	public TipoTaskDTO toTipoTask(Task task, Atto atto) throws DatatypeConfigurationException {
		if (task == null) {
			return null;
		}
		
		TipoTaskDTO retVal = new TipoTaskDTO();
		
		// Dati da Camunda
		retVal.setId(task.getId());
		retVal.setNomeVisualizzato(task.getName());
		retVal.setIdAssegnatario(task.getAssignee());
		if(retVal.getIdAssegnatario()!=null) {
			retVal.setAssegnatarioName(utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(retVal.getIdAssegnatario())));
		}
		retVal.setDataAvvioTask(toXmlGregorianCalendar(task.getCreateTime()));
		
		/*
		 * Ottengo i candidateGroups
		 */
		List<String> candidategroups = bpmWrapperUtil.getCandidategroups(task.getId());
		if ((candidategroups != null) && (candidategroups.size() > 0)) {
			retVal.setCandidateGroups(StringUtils.join(candidategroups, ","));
		}
		
		/*
		 * Task preso in carico per delega
		 */
		String profDelegante = (String)taskService.getVariableLocal(task.getId(), AttoProcessVariables.PROFILO_DELEGANTE);
		if (!StringUtil.isNull(profDelegante)) {
			retVal.setIdDelegante(profDelegante);
		}
		String profOriginario = (String)taskService.getVariableLocal(task.getId(), AttoProcessVariables.PROFILO_ORIGINARIO);
		if (!StringUtil.isNull(profOriginario)) {
			retVal.setIdProfOriginario(profOriginario);
		}
		
		Boolean isTaskInDelegaSingolaLavorazione = (Boolean)taskService.getVariableLocal(task.getId(), AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE);
		if (isTaskInDelegaSingolaLavorazione!=null) {
			retVal.setIsTaskInDelegaSingolaLavorazione(isTaskInDelegaSingolaLavorazione);
		}else {
			retVal.setIsTaskInDelegaSingolaLavorazione(false);
		}
		
		retVal = fillDatiAtto(retVal, atto);
		return retVal;
	}
	
	public TipoTaskDTO toTipoTask(Task task) throws DatatypeConfigurationException {
		if (task == null) {
			return null;
		}
		
		TipoTaskDTO retVal = new TipoTaskDTO();
		
		// Dati da Camunda
		retVal.setId(task.getId());
		retVal.setNomeVisualizzato(task.getName());
		retVal.setIdAssegnatario(task.getAssignee());
		if(retVal.getIdAssegnatario()!=null) {
			retVal.setAssegnatarioName(utenteService.getNameByUser(bpmWrapperUtil.getUsernameByUsernameBpm(retVal.getIdAssegnatario())));
		}
		retVal.setDataAvvioTask(toXmlGregorianCalendar(task.getCreateTime()));
		
		/*
		 * Ottengo i candidateGroups
		 */
		List<String> candidategroups = bpmWrapperUtil.getCandidategroups(task.getId());
		if ((candidategroups != null) && (candidategroups.size() > 0)) {
			retVal.setCandidateGroups(StringUtils.join(candidategroups, ","));
		}
		
		/*
		 * Task preso in carico per delega
		 */
		String profDelegante = (String)taskService.getVariableLocal(task.getId(), AttoProcessVariables.PROFILO_DELEGANTE);
		if (!StringUtil.isNull(profDelegante)) {
			retVal.setIdDelegante(profDelegante);
		}
		String profOriginario = (String)taskService.getVariableLocal(task.getId(), AttoProcessVariables.PROFILO_ORIGINARIO);
		if (!StringUtil.isNull(profOriginario)) {
			retVal.setIdProfOriginario(profOriginario);
		}
		
		Boolean isTaskInDelegaSingolaLavorazione = (Boolean)taskService.getVariableLocal(task.getId(), AttoProcessVariables.DELEGA_SINGOLA_LAVORAZIONE);
		if (isTaskInDelegaSingolaLavorazione!=null) {
			retVal.setIsTaskInDelegaSingolaLavorazione(isTaskInDelegaSingolaLavorazione);
		}else {
			retVal.setIsTaskInDelegaSingolaLavorazione(false);
		}
		
		return retVal;
	}
	
	
	public TipoTaskDTO toTipoTask(Avanzamento av, final Iterable<Profilo> profili, boolean filtroProfili)  throws Exception {
		TipoTaskDTO retVal = new TipoTaskDTO();
		retVal.setNomeVisualizzato(av.getAttivita());
		if(av.getProfilo()!=null) {
			retVal.setIdAssegnatario(bpmWrapperUtil.getUsernameForBpm(av.getProfilo(), null));
		}
		if(av.getProfilo()!=null && av.getProfilo().getUtente()!=null && av.getProfilo().getUtente().getNome()!=null && av.getProfilo().getUtente().getCognome()!=null) {
			retVal.setAssegnatarioName(av.getProfilo().getUtente().getNome() + " " + av.getProfilo().getUtente().getCognome());
		}
		retVal.setDataAttivita(DateUtil.dateTimeToXMLGregorianCalendar(av.getDataAttivita()));
		if(!filtroProfili) {
			retVal = fillDatiAtto(retVal, av.getAtto());
		}else {
			retVal = fillDatiAtto(retVal, av.getAtto(),profili);
		}
		return retVal;
	}
	
	
	private TipoTaskDTO fillDatiAtto(TipoTaskDTO task, Atto atto) throws DatatypeConfigurationException {
		// Dati da DB Cifra
		if (atto != null) {
			task.setBusinessKey(String.valueOf(atto.getId().longValue()));
			task.setCodiceCifra(atto.getCodiceCifra());
			task.setNumeroAdozione(atto.getNumeroAdozione()!=null?atto.getNumeroAdozione():null);
			task.setDataAdozione(atto.getDataAdozione()!=null?toXmlGregorianCalendar(atto.getDataAdozione().toDate()):null);
			task.setDataEsecutivita(atto.getDataEsecutivita()!=null?toXmlGregorianCalendar(atto.getDataEsecutivita().toDate()):null);
			task.setTipoAtto(atto.getTipoAtto().getCodice());
			if(atto.getAoo()!=null && atto.getAoo().getId()!=null) {
				task.setAooId(atto.getAoo().getId() + "");
			}
			task.setDataAvvioProcesso(toXmlGregorianCalendar(atto.getCreatedDate()));
			task.setDataUltimoAggiornamento(toXmlGregorianCalendar(atto.getLastModifiedDate()));
			
			task.setOggetto(atto.getOggetto());
			task.setStartedBy(atto.getCreatedBy());
			if(atto.getCreatedBy()!=null) {
				task.setStartedByName(utenteService.getNameByUser(atto.getCreatedBy()));
			}
			Set<Avanzamento> lavorazioni = atto.getAvanzamento();
			if ( (lavorazioni != null) && (lavorazioni.size() > 0) ) {
				Avanzamento ultimaAzione = lavorazioni.iterator().next();
				task.setDataUltimaAzione(toXmlGregorianCalendar(ultimaAzione.getDataAttivita()));
				task.setEsecutore(ultimaAzione.getCreatedBy());
				if(task.getEsecutore()!=null) {
					task.setEsecutoreName(utenteService.getNameByUser(task.getEsecutore()));
				}
				task.setUltimaAzione(ultimaAzione.getAttivita());
			}
		}
		
		return task;
	}
	
	private TipoTaskDTO fillDatiAtto(TipoTaskDTO task, Atto atto,final Iterable<Profilo> profili) throws DatatypeConfigurationException {
		// Dati da DB Cifra
		if (atto != null) {
			task.setBusinessKey(String.valueOf(atto.getId().longValue()));
			task.setCodiceCifra(atto.getCodiceCifra());
			task.setNumeroAdozione(atto.getNumeroAdozione()!=null?atto.getNumeroAdozione():null);
			task.setDataAdozione(atto.getDataAdozione()!=null?toXmlGregorianCalendar(atto.getDataAdozione().toDate()):null);
			task.setDataEsecutivita(atto.getDataEsecutivita()!=null?toXmlGregorianCalendar(atto.getDataEsecutivita().toDate()):null);
			task.setTipoAtto(atto.getTipoAtto().getCodice());
			if(atto.getAoo()!=null && atto.getAoo().getId()!=null) {
				task.setAooId(atto.getAoo().getId() + "");
			}
			task.setDataAvvioProcesso(toXmlGregorianCalendar(atto.getCreatedDate()));
			task.setDataUltimoAggiornamento(toXmlGregorianCalendar(atto.getLastModifiedDate()));
			
			task.setOggetto(atto.getOggetto());
			task.setStartedBy(atto.getCreatedBy());
			if(atto.getCreatedBy()!=null) {
				task.setStartedByName(utenteService.getNameByUser(atto.getCreatedBy()));
			}
			Set<Avanzamento> lavorazioni = atto.getAvanzamento();
			if ( (lavorazioni != null) && (lavorazioni.size() > 0) ) {
				
				
				List<Long> idProfiliList = new ArrayList<Long>();
				for (Profilo profilo : profili) {
					idProfiliList.add(profilo.getId());
				}
				
				
				for (Iterator iterator = lavorazioni.iterator(); iterator.hasNext();) {
					Avanzamento ultimaAzione = (Avanzamento)iterator.next();
					if(ultimaAzione!=null && ultimaAzione.getProfilo()!=null) {
						Long idProfiloUltimaAzione = ultimaAzione.getProfilo().getId();
						boolean isProfilo = false;
						
						for (int i = 0; i < idProfiliList.size() && !isProfilo; i++) {
							isProfilo = idProfiloUltimaAzione.longValue() == idProfiliList.get(i).longValue();
						}
						if(isProfilo) {
							task.setDataUltimaAzione(toXmlGregorianCalendar(ultimaAzione.getDataAttivita()));
							task.setEsecutore(ultimaAzione.getCreatedBy());
							if(task.getEsecutore()!=null) {
								task.setEsecutoreName(utenteService.getNameByUser(task.getEsecutore()));
							}
							task.setUltimaAzione(ultimaAzione.getAttivita());
							break;
						}
					}
				}
			}
		}
		
		return task;
	}
			
			
	
	public XMLGregorianCalendar toXmlGregorianCalendar(Date value) throws DatatypeConfigurationException {
		GregorianCalendar c = new GregorianCalendar();
		c.setTime(value);
		
		return DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
	}
	
	public XMLGregorianCalendar toXmlGregorianCalendar(DateTime value) throws DatatypeConfigurationException {
		return getDataTypeFactory().newXMLGregorianCalendar(value.toGregorianCalendar());
	}
	
	private DatatypeFactory getDataTypeFactory() throws DatatypeConfigurationException {
		if (XML_DATA_TYPE_FACTORY == null) {
			XML_DATA_TYPE_FACTORY = DatatypeFactory.newInstance();
		}
		return XML_DATA_TYPE_FACTORY;
	}
}
