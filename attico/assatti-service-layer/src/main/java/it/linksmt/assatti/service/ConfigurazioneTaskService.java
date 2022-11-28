package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskAooId;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuolo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskRuoloId;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneTask;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRuoloRepository;
import it.linksmt.assatti.service.converter.ConfigurazioneTaskConverter;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Service class for managing ConfigurazioneTask.
 */
@Service
@Transactional
public class ConfigurazioneTaskService {

	private final Logger log = LoggerFactory.getLogger(ConfigurazioneTaskService.class);

	@Inject
	private ConfigurazioneTaskRepository configurazioneTaskRepository;
	
	@Inject
	private ConfigurazioneTaskAooRepository configurazioneTaskAooRepository;
	
	@Inject
	private ConfigurazioneTaskRuoloRepository configurazioneTaskRuoloRepository;
	
	@Inject
	private AooService aooService;
	
	@Inject
	private ProfiloService profiloService;
	
	public List<Profilo> getCandidateProfList(ConfigurazioneTaskDto configurazioneTaskDto, Long proponenteAooId, Long profiloAooId){
		List<Profilo> listProfilo = null;
		if(configurazioneTaskDto.isProponente() || configurazioneTaskDto.isUfficioCorrente()) {
			List<Long> listIdAoo = new ArrayList<>();
			if(configurazioneTaskDto.isProponente()) {
				listIdAoo.add(proponenteAooId);
			}
			if(configurazioneTaskDto.isUfficioCorrente()) {
				if(!listIdAoo.contains(profiloAooId)) {
					listIdAoo.add(profiloAooId);
				}
			}
			
			if(configurazioneTaskDto.isUfficiLivelloSuperiore()) {
				listIdAoo = addIdAooUfficiLivelloSuperiore(listIdAoo);
			}
			listProfilo = profiloService.findEnabledByRuoloAoo(configurazioneTaskDto.getListRuolo(), listIdAoo);
		} else {
			if(configurazioneTaskDto.isUfficiLivelloSuperiore()) {
				List<Long>  listIdAoo = addIdAooUfficiLivelloSuperiore(configurazioneTaskDto.getListAoo());
				listProfilo = profiloService.findEnabledByRuoloAoo(configurazioneTaskDto.getListRuolo(), listIdAoo);
			}else {
				listProfilo = profiloService.findEnabledByRuoloAoo(configurazioneTaskDto.getListRuolo(), configurazioneTaskDto.getListAoo());
			}
		}
		List<Profilo> listProfiloMinimal = null;
		if(listProfilo!=null && listProfilo.size() > 0) {
			for(Profilo p : listProfilo) {
				if(p.getFutureEnabled()!=null && p.getFutureEnabled().equals(true)) {
					Profilo pMin = DomainUtil.minimalProfilo(p);
					pMin.setHasQualifica(p.getHasQualifica());
					if(listProfiloMinimal==null) {
						listProfiloMinimal = new ArrayList<Profilo>();
					}
					listProfiloMinimal.add(pMin);
				}
			}
		}
		return listProfiloMinimal;
	}

	private List<Long> addIdAooUfficiLivelloSuperiore(List<Long> listIdAoo) {
	    
    	List<Long> out = null;
    	if(listIdAoo != null && listIdAoo.size()>0) {
    		out = new ArrayList<>();
    		for (Long idAoo : listIdAoo) {
    			out.add(idAoo);
    		}
    		
    		for (Long idAoo : listIdAoo) {
    			//prendere le uo dei livelli superiori
    			Aoo aooUfficioCorrente = aooService.findOne(idAoo);
    			Aoo aooPadre = aooUfficioCorrente.getAooPadre();
    			
    			while(aooPadre != null && aooPadre.getId() > 0) {
    				if(!out.contains(aooPadre.getId())) {
    					out.add(aooPadre.getId());
    				}
    				
					aooUfficioCorrente = aooService.findOne(aooPadre.getId());
					if(aooUfficioCorrente != null) {
						aooPadre = aooUfficioCorrente.getAooPadre();
					}
					else {
						aooPadre = null;
					}
    			}
			}
    	}
    	return out;
    }
	
	@Transactional(readOnly = true)
	public ConfigurazioneTaskDto get(Long idConfigurazioneTaskDto) throws ServiceException {
		ConfigurazioneTaskDto configurazioneTaskDto = null;
		
		if(idConfigurazioneTaskDto!=null && idConfigurazioneTaskDto>0L) {
			ConfigurazioneTask configurazioneTask = configurazioneTaskRepository.findOne(idConfigurazioneTaskDto);
			
			configurazioneTaskDto = fillDto(configurazioneTask);
		}
		
		return configurazioneTaskDto;
	}
	
	@Transactional(readOnly = true)
	public String getVarNameByConfTaskId(Long confTaskId) throws ServiceException {
		String varName = null;
		ConfigurazioneTask ct = configurazioneTaskRepository.findOne(QConfigurazioneTask.configurazioneTask.idConfigurazioneTask.eq(confTaskId));
		if(ct!=null) {
			varName = ct.getProcessVarName();
		}

		return varName;
	}

	@Transactional(readOnly = true)
	public List<ConfigurazioneTaskDto> findAll() throws ServiceException {
		List<ConfigurazioneTaskDto> listConfigurazioneTaskDto = null;
		
		List<ConfigurazioneTask> listConfigurazioneTask = configurazioneTaskRepository.findAll(new Sort(Sort.Direction.ASC, "nome"));
		
		if(listConfigurazioneTask!=null) {
			listConfigurazioneTaskDto = new ArrayList<>();
			
			for(ConfigurazioneTask c : listConfigurazioneTask) {
				listConfigurazioneTaskDto.add(fillDto(c));
			}
		}
		
		return listConfigurazioneTaskDto;
	}

	@Transactional
	public ConfigurazioneTaskDto save(ConfigurazioneTaskDto configurazioneTaskDto) throws ServiceException {
		ConfigurazioneTaskDto s = null;
		if(configurazioneTaskDto!=null) {
			ConfigurazioneTask configurazioneTask = null;
			
			boolean update = false;
			
			// if exist, it's an update
			try {
				if(configurazioneTaskDto.getIdConfigurazioneTask()!=null && configurazioneTaskDto.getIdConfigurazioneTask()>0L) {
					update = true;
					configurazioneTask = configurazioneTaskRepository.findOne(configurazioneTaskDto.getIdConfigurazioneTask());
					configurazioneTask.setMultipla(configurazioneTaskDto.isMultipla());
					configurazioneTask.setNome(configurazioneTaskDto.getNome());
					configurazioneTask.setProponente(configurazioneTaskDto.isProponente());
					configurazioneTask.setUfficiLivelloSuperiore(configurazioneTaskDto.isUfficiLivelloSuperiore());
					configurazioneTask.setUfficioCorrente(configurazioneTaskDto.isUfficioCorrente());
					configurazioneTask.setImpostaScadenza(configurazioneTaskDto.isImpostaScadenza());
					configurazioneTask.setScadenzaObbligatoria(configurazioneTaskDto.isScadenzaObbligatoria());
					configurazioneTask.setDataManuale(configurazioneTaskDto.isDataManuale());
					configurazioneTask.setDataModifica(new Date());
					configurazioneTask.setStessaDirezioneProponente(configurazioneTaskDto.isStessaDirezioneProponente());
				}
			} catch(Exception e) {
				log.debug(e.getMessage());
			}
			
			/*
			 * se è un ADD
			 */
			if(!update) {
				/*
				 * Verifico che il codice non sia già in uso
				 */
				if(configurazioneTaskRepository.findByCodice(configurazioneTaskDto.getCodice())!=null) {
					throw new ServiceException("Esiste già una configurazione con il codice " + configurazioneTaskDto.getCodice());
				}
			}

			// if not exist, it's an add
			if(configurazioneTask==null) {
				configurazioneTask = ConfigurazioneTaskConverter.convertToModel(configurazioneTaskDto);
				configurazioneTask.setDataCreazione(new Date());
			}
			
			/*
			 * Salvo la configurazione task
			 */
			configurazioneTask.setDataModifica(new Date());
			configurazioneTask = configurazioneTaskRepository.save(configurazioneTask);
			
			/*
			 * Salvo le associazioni con le aoo
			 */
			if(configurazioneTaskDto.getListAoo()!=null) {
				if(update) {
					configurazioneTaskAooRepository.deleteByPrimaryKey_IdConfigurazioneTask(configurazioneTaskDto.getIdConfigurazioneTask());
				}
				for(Long aoo : configurazioneTaskDto.getListAoo()) {
					// preparo id della relazione
					ConfigurazioneTaskAooId configurazioneTaskAooId = new ConfigurazioneTaskAooId();
					configurazioneTaskAooId.setIdAoo(aoo);
					configurazioneTaskAooId.setIdConfigurazioneTask(configurazioneTask.getIdConfigurazioneTask());
					
					ConfigurazioneTaskAoo configurazioneTaskAoo = new ConfigurazioneTaskAoo();
					configurazioneTaskAoo.setPrimaryKey(configurazioneTaskAooId);
					
					configurazioneTaskAooRepository.save(configurazioneTaskAoo);
				}
			}
			
			/*
			 * Salvo le associazioni con i ruoli
			 */
			if(configurazioneTaskDto.getListRuolo()!=null) {
				if(update) {
					configurazioneTaskRuoloRepository.deleteByPrimaryKey_IdConfigurazioneTask(configurazioneTaskDto.getIdConfigurazioneTask());
				}
				for(Long ruolo : configurazioneTaskDto.getListRuolo()) {
					// preparo id della relazione
					ConfigurazioneTaskRuoloId configurazioneTaskRuoloId = new ConfigurazioneTaskRuoloId();
					configurazioneTaskRuoloId.setIdRuolo(ruolo);
					configurazioneTaskRuoloId.setIdConfigurazioneTask(configurazioneTask.getIdConfigurazioneTask());
					
					ConfigurazioneTaskRuolo configurazioneTaskRuolo = new ConfigurazioneTaskRuolo();
					configurazioneTaskRuolo.setPrimaryKey(configurazioneTaskRuoloId);
					
					configurazioneTaskRuoloRepository.save(configurazioneTaskRuolo);
				}
			}
			
			/*
			 * impostazione oggetto DTO di ritorno
			 */
			s = fillDto(configurazioneTask);
		} else {
			log.debug("Errore salvando configurazioneTaskDto!");
		}
		return s;
	}
	
	private ConfigurazioneTaskDto fillDto(ConfigurazioneTask configurazioneTask) {
		ConfigurazioneTaskDto configurazioneTaskDto = null;
		if(configurazioneTask!=null) {
			configurazioneTaskDto = ConfigurazioneTaskConverter.convertToDto(configurazioneTask);

			/*
			 * reperisco elenco di Aoo relative alla configurazione
			 */
			List<Long> listAoo = null;
			List<ConfigurazioneTaskAoo> listConfigurazioneTaskAoo = configurazioneTaskAooRepository.findByConfigurazioneTask(configurazioneTask.getIdConfigurazioneTask());
			if(listConfigurazioneTaskAoo!=null) {
				listAoo = new ArrayList<>();
				for(ConfigurazioneTaskAoo c : listConfigurazioneTaskAoo) {
					listAoo.add(c.getPrimaryKey().getIdAoo());
				}
			}
			configurazioneTaskDto.setListAoo(listAoo);

			/*
			 * reperisco elenco di Ruolo relativi alla configurazione
			 */
			List<Long> listRuolo = null;
			List<ConfigurazioneTaskRuolo> listConfigurazioneTaskRuolo = configurazioneTaskRuoloRepository.findByConfigurazioneTask(configurazioneTask.getIdConfigurazioneTask());
			if(listConfigurazioneTaskRuolo!=null) {
				listRuolo = new ArrayList<>();
				for(ConfigurazioneTaskRuolo c : listConfigurazioneTaskRuolo) {
					listRuolo.add(c.getPrimaryKey().getIdRuolo());
				}
			}
			configurazioneTaskDto.setListRuolo(listRuolo);
		}
		return configurazioneTaskDto;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneTaskDto findByCodice(String codiceConfigurazioneTaskDto) throws ServiceException {
		ConfigurazioneTaskDto configurazioneTaskDto = null;
		
		if(codiceConfigurazioneTaskDto!=null && codiceConfigurazioneTaskDto.length()>0L) {
			ConfigurazioneTask configurazioneTask = configurazioneTaskRepository.findByCodice(codiceConfigurazioneTaskDto);
			configurazioneTaskDto = fillDto(configurazioneTask);
		}
		
		return configurazioneTaskDto;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneTask findEntityByCodice(String codiceConfigurazioneTask) throws ServiceException {
		ConfigurazioneTask configurazioneTask = null;
		if(codiceConfigurazioneTask!=null && codiceConfigurazioneTask.length()>0L) {
			configurazioneTask = configurazioneTaskRepository.findByCodice(codiceConfigurazioneTask);
			
		}
		return configurazioneTask;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneTaskDto findByProcessVarName(String processVarName) throws ServiceException {
		ConfigurazioneTaskDto configurazioneTaskDto = null;
		BooleanExpression p = QConfigurazioneTask.configurazioneTask.processVarName.eq(processVarName).or(
			QConfigurazioneTask.configurazioneTask.processVarName.like(processVarName + "|%")
		).or(
			QConfigurazioneTask.configurazioneTask.processVarName.like("%|" + processVarName)
		);
		ConfigurazioneTask configurazioneTask = configurazioneTaskRepository.findOne(p);
		
		configurazioneTaskDto = fillDto(configurazioneTask);
		
		return configurazioneTaskDto;
	}


}
