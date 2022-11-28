package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoAooId;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfiloId;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTask;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneIncarico;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneIncaricoAoo;
import it.linksmt.assatti.datalayer.domain.QualificaProfessionale;
import it.linksmt.assatti.datalayer.repository.AooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoAooRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoProfiloRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoRepository;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneTaskRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.datalayer.repository.QualificaProfessionaleRepository;
import it.linksmt.assatti.service.converter.ConfigurazioneIncaricoAooConverter;
import it.linksmt.assatti.service.converter.ConfigurazioneIncaricoConverter;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Service class for managing ConfigurazioneIncarico.
 */
@Service
public class ConfigurazioneIncaricoService {

	private final Logger log = LoggerFactory.getLogger(ConfigurazioneIncaricoService.class);

	@Inject
	private AooRepository aooRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private QualificaProfessionaleRepository qualificaProfessionaleRepository;
	
	@Inject
	private ConfigurazioneIncaricoRepository configurazioneIncaricoRepository;
	
	@Inject
	private ConfigurazioneIncaricoAooRepository configurazioneIncaricoAooRepository;
	
	@Inject
	private ConfigurazioneIncaricoProfiloService configurazioneIncaricoProfiloService;
	
	@Inject
	private ConfigurazioneIncaricoProfiloRepository configurazioneIncaricoProfiloRepository;
	
	@Inject
	private ConfigurazioneTaskRepository configurazioneTaskRepository;
	
	@Transactional(readOnly = true)
	public boolean existsConfIncaricoByConfTask(Long attoId, ConfigurazioneTaskEnum configurazioneTaskEnum) {
		boolean exists = false;
		if(attoId!=null) {
			BooleanExpression p = QConfigurazioneIncarico.configurazioneIncarico.atto.id.eq(attoId).and(QConfigurazioneIncarico.configurazioneIncarico.configurazioneTask.codice.eq(configurazioneTaskEnum.getCodice()));
			exists = configurazioneIncaricoRepository.count(p) > 0L;
		}			
		return exists;
	}
	
	@Transactional(readOnly = true)
	public List<ConfigurazioneIncaricoDto> getConfIncaricoByConfTask(Long attoId, ConfigurazioneTaskEnum configurazioneTaskEnum) throws ServiceException {
		List<ConfigurazioneIncaricoDto> listDto = null;
		if(attoId!=null) {
			BooleanExpression p = QConfigurazioneIncarico.configurazioneIncarico.atto.id.eq(attoId).and(QConfigurazioneIncarico.configurazioneIncarico.configurazioneTask.codice.eq(configurazioneTaskEnum.getCodice()));
			Iterable<ConfigurazioneIncarico> it = configurazioneIncaricoRepository.findAll(p);
			if(it!=null) {
				listDto = new ArrayList<ConfigurazioneIncaricoDto>();
				for(ConfigurazioneIncarico c : it) {
					ConfigurazioneIncaricoDto configurazioneIncaricoDto = fillDto(c);
					listDto.add(configurazioneIncaricoDto);
				}
			}
		}			
		return listDto;
	}
	

	@Transactional(readOnly = true)
	public ConfigurazioneIncaricoDto get(Long idConfigurazioneIncaricoDto) throws ServiceException {
		ConfigurazioneIncaricoDto configurazioneIncaricoDto = null;
		
		if(idConfigurazioneIncaricoDto!=null && idConfigurazioneIncaricoDto>0L) {
			ConfigurazioneIncarico configurazioneIncarico = configurazioneIncaricoRepository.findOne(idConfigurazioneIncaricoDto);
			
			configurazioneIncaricoDto = fillDto(configurazioneIncarico);
		}
		
		return configurazioneIncaricoDto;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneTask getConfTaskByConfIncaricoId(Long confIncaricoId) throws ServiceException {
		ConfigurazioneIncarico confIncarico = configurazioneIncaricoRepository.findOne(confIncaricoId);
		ConfigurazioneTask conf = null;
		if(confIncarico!=null) {
			conf = confIncarico.getConfigurazioneTask();
		}
		return conf;
	}

	@Transactional(readOnly = true)
	public List<ConfigurazioneIncaricoDto> findAll() throws ServiceException {
		List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto = null;
		
		List<ConfigurazioneIncarico> listConfigurazioneIncarico = configurazioneIncaricoRepository.findAll(new Sort(Sort.Direction.ASC, "dataCreazione"));
		
		if(listConfigurazioneIncarico!=null) {
			listConfigurazioneIncaricoDto = new ArrayList<>();
			
			for(ConfigurazioneIncarico c : listConfigurazioneIncarico) {
				listConfigurazioneIncaricoDto.add(fillDto(c));
			}
		}
		
		return listConfigurazioneIncaricoDto;
	}
	
	@Transactional(readOnly = true)
	public List<ConfigurazioneIncarico> findByAttoId(Long idAtto){
		BooleanExpression p = QConfigurazioneIncarico.configurazioneIncarico.atto.id.eq(idAtto);
		Iterable<ConfigurazioneIncarico> it = configurazioneIncaricoRepository.findAll(p);
		List<ConfigurazioneIncarico> list = null;
		if(it!=null) {
			list = Lists.newArrayList(it);
		}else {
			list = new ArrayList<ConfigurazioneIncarico>();
		}
		for(ConfigurazioneIncarico inc : list) {
			if(inc != null && inc.getConfigurazioneTask() != null) {
				if(inc.getConfigurazioneTask().getIdConfigurazioneTask()!=null && inc.getConfigurazioneTask().getCodice()==null) {
					ConfigurazioneTask cTask = configurazioneTaskRepository.findOne(inc.getConfigurazioneTask().getIdConfigurazioneTask());
					inc.getConfigurazioneTask().setCodice(cTask.getCodice());
				}
			}
		}
		return list;
	}

	@Transactional(readOnly = true)
	public List<ConfigurazioneIncaricoDto> findByAtto(Long idAtto) throws ServiceException {
		List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto = null;
		
		List<ConfigurazioneIncarico> listConfigurazioneIncarico = this.findByAttoId(idAtto);
		
		if(listConfigurazioneIncarico!=null) {
			listConfigurazioneIncaricoDto = new ArrayList<>();
			
			for(ConfigurazioneIncarico c : listConfigurazioneIncarico) {
				listConfigurazioneIncaricoDto.add(fillDto(c));
			}
		}
		
		return listConfigurazioneIncaricoDto;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneIncaricoDto findByLastByCodiceAndAtto(String codiceConfigurazioneTask, Long idAtto) throws ServiceException {
		List<ConfigurazioneIncarico> listConfigurazioneIncarico = configurazioneIncaricoRepository
				.findLastByCodiceAndAtto_Id(codiceConfigurazioneTask, idAtto);
		
		if(listConfigurazioneIncarico!=null && listConfigurazioneIncarico.size() > 0) {
			return fillDto(listConfigurazioneIncarico.get(0));
		}
		
		return null;
	}
	
	public List<Profilo> getMinimalProfiliIncaricati(String codiceTask, long idAtto) {
		try {
			List<ConfigurazioneIncarico> listConfigurazioneIncarico = configurazioneIncaricoRepository
					.findLastByCodiceAndAtto_Id(codiceTask, idAtto);
			
			if(listConfigurazioneIncarico!=null && listConfigurazioneIncarico.size() > 0) {
				ConfigurazioneIncarico confIncarico = listConfigurazioneIncarico.get(0);
				
				List<ConfigurazioneIncaricoProfilo> listConfigurazioneIncaricoProfilo = configurazioneIncaricoProfiloRepository
						.findByPrimaryKey_IdConfigurazioneIncarico(confIncarico.getId().longValue());

				if(listConfigurazioneIncaricoProfilo!=null && !listConfigurazioneIncaricoProfilo.isEmpty()) {
					List<Profilo> lstProfili = new ArrayList<Profilo>();
					for (ConfigurazioneIncaricoProfilo confProf : listConfigurazioneIncaricoProfilo) {
						Profilo profilo = profiloRepository.getOne(confProf.getPrimaryKey().getIdProfilo());
						if(profilo!=null) {
							lstProfili.add(DomainUtil.minimalProfilo(profilo));
						}
					}
					Collections.sort(lstProfili,new Comparator<Profilo>(){
						@Override
					    public int compare(Profilo prof1, Profilo prof2){
							if((prof1 == null || prof1.getOrdineGiunta() == null) && (prof2 == null || prof2.getOrdineGiunta() == null)) {
								return 0;
							}
							else if(prof1 == null||prof1.getOrdineGiunta() == null) {
								return -1;
							}
							else if(prof2 == null || prof2.getOrdineGiunta() == null) {
								return 1;
							}
							return prof1.getOrdineGiunta().compareTo(prof2.getOrdineGiunta());
						}
					});
					return lstProfili;
				}
			}
		}
		catch (Exception e) {
			log.error("Errore di lettura dell'Assessore proponente", e);
		}
		
		return null;
	}
	
	public String getNomiIncaricati(String codiceTask, long idAtto) {
		try {
			List<ConfigurazioneIncarico> listConfigurazioneIncarico = configurazioneIncaricoRepository
					.findLastByCodiceAndAtto_Id(codiceTask, idAtto);
			
			if(listConfigurazioneIncarico!=null && listConfigurazioneIncarico.size() > 0) {
				ConfigurazioneIncarico confIncarico = listConfigurazioneIncarico.get(0);
				
				List<ConfigurazioneIncaricoProfilo> listConfigurazioneIncaricoProfilo = configurazioneIncaricoProfiloRepository
						.findByPrimaryKey_IdConfigurazioneIncarico(confIncarico.getId().longValue());
				
				String nomiProponenti = "";
				if(listConfigurazioneIncaricoProfilo!=null) {
					for (ConfigurazioneIncaricoProfilo confProf : listConfigurazioneIncaricoProfilo) {
						Profilo profilo = profiloRepository.getOne(confProf.getPrimaryKey().getIdProfilo());
						if(profilo!=null) {
							String valAssessore = profilo.getUtente().getCognome() + " " + profilo.getUtente().getNome();
							if (nomiProponenti.length() > 0) {
								nomiProponenti += ", ";
							}
							nomiProponenti += valAssessore;
						}
					}
				}
				return nomiProponenti;
			}
		}
		catch (Exception e) {
			log.error("Errore di lettura dell'Assessore proponente", e);
		}
		
		return "";
	}
	
	@Transactional
	public void removeConfigurazioniIncarico(Collection<ConfigurazioneIncaricoDto> confIncs) {
		if(confIncs!=null) {
			for(ConfigurazioneIncaricoDto conf : confIncs) {
				if(conf!=null) {
					this.removeConfigurazioneIncarico(conf);
				}
			}
		}
	}
	
	@Transactional
	public void removeConfigurazioneIncarico(ConfigurazioneIncaricoDto confInc) {
		if(confInc!=null && confInc.getId()!=null && confInc.getId().longValue() > 0L) {
			if(confInc.getListConfigurazioneIncaricoAooDto()!=null && confInc.getListConfigurazioneIncaricoAooDto().size() > 0) {
				for(ConfigurazioneIncaricoAooDto confAoo : confInc.getListConfigurazioneIncaricoAooDto()) {
					if(confAoo!=null && confAoo.getIdAoo()!=null && confAoo.getIdAoo().longValue() > 0L && confAoo.getIdConfigurazioneIncarico()!=null && confAoo.getIdConfigurazioneIncarico().longValue() > 0L) {
						ConfigurazioneIncaricoAooId confIncId = new ConfigurazioneIncaricoAooId();
						confIncId.setIdAoo(confAoo.getIdAoo());
						confIncId.setIdConfigurazioneIncarico(confAoo.getIdConfigurazioneIncarico());
						configurazioneIncaricoAooRepository.delete(confIncId);
					}
				}
			}
			if(confInc.getListConfigurazioneIncaricoProfiloDto()!=null && confInc.getListConfigurazioneIncaricoProfiloDto().size() > 0) {
				for(ConfigurazioneIncaricoProfiloDto confProf : confInc.getListConfigurazioneIncaricoProfiloDto()) {
					if(confProf!=null && confProf.getIdProfilo()!=null && confProf.getIdProfilo().longValue() > 0L && confProf.getIdConfigurazioneIncarico()!=null && confProf.getIdConfigurazioneIncarico().longValue() > 0L) {
						ConfigurazioneIncaricoProfiloId confIncId = new ConfigurazioneIncaricoProfiloId();
						confIncId.setIdProfilo(confProf.getIdProfilo());
						confIncId.setIdConfigurazioneIncarico(confProf.getIdConfigurazioneIncarico());
						configurazioneIncaricoProfiloRepository.delete(confIncId);
					}
				}
			}
			configurazioneIncaricoRepository.delete(confInc.getId());
		}
	}
	
	/**
	 * Return if exists the old qualificaprofissionale id
	 * @param confIncId
	 * @param currentProfId
	 * @param newProfId
	 * @param currentQualifId
	 * @param newQualifId
	 * @return
	 */
	@Transactional
	public Long updateConfIncaricoProfilo(Long confIncId, Long currentProfId, Long newProfId, Long newQualifId, boolean reassigned) {
		ConfigurazioneIncaricoProfiloId currentPK = new ConfigurazioneIncaricoProfiloId(confIncId, currentProfId);
		ConfigurazioneIncaricoProfilo cip = configurazioneIncaricoProfiloRepository.findOne(currentPK);
		Long oldQualificaId = cip != null && cip.getQualificaProfessionale()!=null ? cip.getQualificaProfessionale().getId() : null;
		if(cip!=null) {
			configurazioneIncaricoProfiloRepository.delete(currentPK);
			ConfigurazioneIncaricoProfilo newCip = new ConfigurazioneIncaricoProfilo();
			newCip.setPrimaryKey(new ConfigurazioneIncaricoProfiloId(confIncId, newProfId));
			newCip.setDataCreazione(DateTime.now());
			newCip.setGiorniScadenza(cip.getGiorniScadenza());
			newCip.setOrdineFirma(cip.getOrdineFirma());
			if(newQualifId!=null) {
				newCip.setQualificaProfessionale(new QualificaProfessionale(newQualifId));
			}
			newCip.setReassigned(reassigned);
			newCip = configurazioneIncaricoProfiloRepository.save(newCip);
		}else {
			log.error("Configurazione incarico non presente: " + currentPK);
			throw new RuntimeException("Configurazione incarico non presente.");
		}
		return oldQualificaId;
	}
	

	@Transactional
	public ConfigurazioneIncaricoDto save(ConfigurazioneIncaricoDto configurazioneIncaricoDto) throws ServiceException {
		ConfigurazioneIncaricoDto s = null;
		if(configurazioneIncaricoDto!=null) {
			ConfigurazioneIncarico configurazioneIncarico = null;
			
			boolean update = false;
			
			// if exist, it's an update
			try {
				if(configurazioneIncaricoDto.getId()!=null && configurazioneIncaricoDto.getId()>0L) {
					update = true;
					configurazioneIncarico = configurazioneIncaricoRepository.findOne(configurazioneIncaricoDto.getId());
					configurazioneIncarico.setEditor(configurazioneIncaricoDto.getEditor());
					configurazioneIncarico.setEnabled(configurazioneIncaricoDto.getEnabled());
					configurazioneIncarico.setTipologia(configurazioneIncaricoDto.getTipologia());
					configurazioneIncarico.setDataModifica(new Date());
				}
			} catch(Exception e) {
				log.debug(e.getMessage());
			}

			// if not exist, it's an add
			if(configurazioneIncarico==null) {
				configurazioneIncarico = ConfigurazioneIncaricoConverter.convertToModel(configurazioneIncaricoDto);
				configurazioneIncarico.setDataCreazione(new Date());
			}
			
			/*
			 * Salvo la configurazione incarico
			 */
			configurazioneIncarico.setDataModifica(new Date());
			configurazioneIncarico = configurazioneIncaricoRepository.save(configurazioneIncarico);
			
			/*
			 * Salvo le associazioni con le aoo
			 */
			if(configurazioneIncaricoDto.getListConfigurazioneIncaricoAooDto()!=null) {
				if(update) {
					List<ConfigurazioneIncaricoAoo> dbList = configurazioneIncaricoAooRepository.findByConfigurazioneIncarico(configurazioneIncaricoDto.getId());
					Map<Long, ConfigurazioneIncaricoAoo> dbMap = new HashMap<Long, ConfigurazioneIncaricoAoo>();
					Map<Long, ConfigurazioneIncaricoAooDto> mapRt = new HashMap<Long, ConfigurazioneIncaricoAooDto>();
					for(ConfigurazioneIncaricoAoo c : dbList) {
						dbMap.put(c.getPrimaryKey().getIdAoo(), c);
					}
					for(ConfigurazioneIncaricoAooDto aoo : configurazioneIncaricoDto.getListConfigurazioneIncaricoAooDto()) {
						mapRt.put(aoo.getIdAoo(), aoo);
					}
					for(Long id : dbMap.keySet()) {
						if(!mapRt.keySet().contains(id)) {
							ConfigurazioneIncaricoAooId configurazioneIncaricoAooId = new ConfigurazioneIncaricoAooId();
							configurazioneIncaricoAooId.setIdAoo(id);
							configurazioneIncaricoAooId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
							configurazioneIncaricoAooRepository.delete(configurazioneIncaricoAooId);
						}
					}
					for(Long id : mapRt.keySet()) {
						//nuove configurazioni
						if(!dbMap.keySet().contains(id)) {
							ConfigurazioneIncaricoAooId configurazioneIncaricoAooId = new ConfigurazioneIncaricoAooId();
							configurazioneIncaricoAooId.setIdAoo(id);
							configurazioneIncaricoAooId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
							
							ConfigurazioneIncaricoAoo configurazioneIncaricoAoo = new ConfigurazioneIncaricoAoo();
							configurazioneIncaricoAoo.setPrimaryKey(configurazioneIncaricoAooId);
							configurazioneIncaricoAoo.setOrdineFirma(mapRt.get(id).getOrdineFirma());
							if ((mapRt.get(id).getGiorniScadenza() != null) && (mapRt.get(id).getGiorniScadenza().intValue() > 0)) {
								configurazioneIncaricoAoo.setGiorniScadenza(mapRt.get(id).getGiorniScadenza());
							}
							if (mapRt.get(id).getDataManuale() != null) {
								configurazioneIncaricoAoo.setDataManuale(mapRt.get(id).getDataManuale());
							}
		
							if(mapRt.get(id).getQualificaProfessionaleDto()!=null && mapRt.get(id).getQualificaProfessionaleDto().getId()!=null) {
								QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(mapRt.get(id).getQualificaProfessionaleDto().getId());
								configurazioneIncaricoAoo.setQualificaProfessionale(qualificaProfessionale);
							}
							
							configurazioneIncaricoAooRepository.save(configurazioneIncaricoAoo);
						}else {
							//aggiornamento configurazioni
							ConfigurazioneIncaricoAoo configurazioneIncaricoAoo = dbMap.get(id);
							
							configurazioneIncaricoAoo.setOrdineFirma(mapRt.get(id).getOrdineFirma());
							if ((mapRt.get(id).getGiorniScadenza() != null) && (mapRt.get(id).getGiorniScadenza().intValue() > 0)) {
								configurazioneIncaricoAoo.setGiorniScadenza(mapRt.get(id).getGiorniScadenza());
							}else {
								configurazioneIncaricoAoo.setGiorniScadenza(null);
							}
							if ((mapRt.get(id).getDataManuale() != null)) {
								configurazioneIncaricoAoo.setDataManuale(mapRt.get(id).getDataManuale());
							}
							else {
								configurazioneIncaricoAoo.setDataManuale(null);
							}
		
							if(mapRt.get(id).getQualificaProfessionaleDto()!=null && mapRt.get(id).getQualificaProfessionaleDto().getId()!=null) {
								QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(mapRt.get(id).getQualificaProfessionaleDto().getId());
								configurazioneIncaricoAoo.setQualificaProfessionale(qualificaProfessionale);
							}else {
								configurazioneIncaricoAoo.setQualificaProfessionale(null);
							}
							
							configurazioneIncaricoAooRepository.save(configurazioneIncaricoAoo);
						}
					}
				}else {
					for(ConfigurazioneIncaricoAooDto aoo : configurazioneIncaricoDto.getListConfigurazioneIncaricoAooDto()) {
						// preparo id della relazione
						ConfigurazioneIncaricoAooId configurazioneIncaricoAooId = new ConfigurazioneIncaricoAooId();
						configurazioneIncaricoAooId.setIdAoo(aoo.getIdAoo());
						configurazioneIncaricoAooId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
						
						ConfigurazioneIncaricoAoo configurazioneIncaricoAoo = new ConfigurazioneIncaricoAoo();
						configurazioneIncaricoAoo.setPrimaryKey(configurazioneIncaricoAooId);
						configurazioneIncaricoAoo.setOrdineFirma(aoo.getOrdineFirma());
						if ((aoo.getGiorniScadenza() != null) && (aoo.getGiorniScadenza().intValue() > 0)) {
							configurazioneIncaricoAoo.setGiorniScadenza(aoo.getGiorniScadenza());
						}
						if(aoo.getDataManuale() != null) {
							configurazioneIncaricoAoo.setDataManuale(aoo.getDataManuale());
						}
	
						if(aoo.getQualificaProfessionaleDto()!=null && aoo.getQualificaProfessionaleDto().getId()!=null) {
							QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(aoo.getQualificaProfessionaleDto().getId());
							configurazioneIncaricoAoo.setQualificaProfessionale(qualificaProfessionale);
						}
						
						configurazioneIncaricoAooRepository.save(configurazioneIncaricoAoo);
					}
				}
			}
			
			/*
			 * Salvo le associazioni con i profili
			 */
			if(configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto()!=null) {
				if(update) {
					List<ConfigurazioneIncaricoProfilo> dbList = configurazioneIncaricoProfiloService.findByConfigurazioneIncaricoOrderByOrdineFirma(configurazioneIncaricoDto.getId());
					Map<Long, ConfigurazioneIncaricoProfilo> dbMap = new HashMap<Long, ConfigurazioneIncaricoProfilo>();
					Map<Long, ConfigurazioneIncaricoProfiloDto> mapRt = new HashMap<Long, ConfigurazioneIncaricoProfiloDto>();
					for(ConfigurazioneIncaricoProfilo c : dbList) {
						dbMap.put(c.getPrimaryKey().getIdProfilo(), c);
					}
					for(ConfigurazioneIncaricoProfiloDto profilo : configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto()) {
						mapRt.put(profilo.getIdProfilo(), profilo);
					}
					for(Long id : dbMap.keySet()) {
						if(!mapRt.keySet().contains(id)) {
							ConfigurazioneIncaricoProfiloId configurazioneIncaricoProfiloId = new ConfigurazioneIncaricoProfiloId();
							configurazioneIncaricoProfiloId.setIdProfilo(id);
							configurazioneIncaricoProfiloId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
							configurazioneIncaricoProfiloRepository.delete(configurazioneIncaricoProfiloId);
						}
					}
					for(Long id : mapRt.keySet()) {
						if(!dbMap.keySet().contains(id)) {
							//nuove configurazioni
							ConfigurazioneIncaricoProfiloId configurazioneIncaricoProfiloId = new ConfigurazioneIncaricoProfiloId();
							configurazioneIncaricoProfiloId.setIdProfilo(id);
							configurazioneIncaricoProfiloId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
							
							ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo = new ConfigurazioneIncaricoProfilo();
							configurazioneIncaricoProfilo.setPrimaryKey(configurazioneIncaricoProfiloId);
							configurazioneIncaricoProfilo.setOrdineFirma(mapRt.get(id).getOrdineFirma());
							configurazioneIncaricoProfilo.setReassigned(false);
							if(mapRt.get(id).getQualificaProfessionaleDto()!=null && mapRt.get(id).getQualificaProfessionaleDto().getId()!=null) {
								QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(mapRt.get(id).getQualificaProfessionaleDto().getId());
								configurazioneIncaricoProfilo.setQualificaProfessionale(qualificaProfessionale);
							}
							
							if ((mapRt.get(id).getGiorniScadenza() != null) && (mapRt.get(id).getGiorniScadenza().intValue() > 0)) {
								configurazioneIncaricoProfilo.setGiorniScadenza(mapRt.get(id).getGiorniScadenza());
							}
							
							configurazioneIncaricoProfiloRepository.save(configurazioneIncaricoProfilo);
						}else {
							//aggiornamento configurazioni
							ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo = dbMap.get(id);
							configurazioneIncaricoProfilo.setOrdineFirma(mapRt.get(id).getOrdineFirma());
							configurazioneIncaricoProfilo.setReassigned(false);
							if(mapRt.get(id).getQualificaProfessionaleDto()!=null && mapRt.get(id).getQualificaProfessionaleDto().getId()!=null) {
								QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(mapRt.get(id).getQualificaProfessionaleDto().getId());
								configurazioneIncaricoProfilo.setQualificaProfessionale(qualificaProfessionale);
							}else {
								configurazioneIncaricoProfilo.setQualificaProfessionale(null);
							}
							
							if ((mapRt.get(id).getGiorniScadenza() != null) && (mapRt.get(id).getGiorniScadenza().intValue() > 0)) {
								configurazioneIncaricoProfilo.setGiorniScadenza(mapRt.get(id).getGiorniScadenza());
							}else {
								configurazioneIncaricoProfilo.setGiorniScadenza(null);
							}
							
							configurazioneIncaricoProfiloRepository.save(configurazioneIncaricoProfilo);
						}
					}
				}else {
					for(ConfigurazioneIncaricoProfiloDto profilo : configurazioneIncaricoDto.getListConfigurazioneIncaricoProfiloDto()) {
						// preparo id della relazione
						ConfigurazioneIncaricoProfiloId configurazioneIncaricoProfiloId = new ConfigurazioneIncaricoProfiloId();
						configurazioneIncaricoProfiloId.setIdProfilo(profilo.getIdProfilo());
						configurazioneIncaricoProfiloId.setIdConfigurazioneIncarico(configurazioneIncarico.getId());
						
						ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo = new ConfigurazioneIncaricoProfilo();
						configurazioneIncaricoProfilo.setPrimaryKey(configurazioneIncaricoProfiloId);
						configurazioneIncaricoProfilo.setOrdineFirma(profilo.getOrdineFirma());
						configurazioneIncaricoProfilo.setReassigned(false);
						if(profilo.getQualificaProfessionaleDto()!=null && profilo.getQualificaProfessionaleDto().getId()!=null) {
							QualificaProfessionale qualificaProfessionale = qualificaProfessionaleRepository.findOne(profilo.getQualificaProfessionaleDto().getId());
							configurazioneIncaricoProfilo.setQualificaProfessionale(qualificaProfessionale);
						}
						
						if ((profilo.getGiorniScadenza() != null) && (profilo.getGiorniScadenza().intValue() > 0)) {
							configurazioneIncaricoProfilo.setGiorniScadenza(profilo.getGiorniScadenza());
						}
						
						configurazioneIncaricoProfiloRepository.save(configurazioneIncaricoProfilo);
					}
				}
			}
			
			/*
			 * impostazione oggetto DTO di ritorno
			 */
			s = fillDto(configurazioneIncarico);
		} else {
			log.debug("Errore salvando configurazioneIncaricoDto!");
		}
		return s;
	}
	
	private ConfigurazioneIncaricoDto fillDto(ConfigurazioneIncarico configurazioneIncarico) throws ServiceException {
		ConfigurazioneIncaricoDto configurazioneIncaricoDto = null;
		if(configurazioneIncarico!=null) {
			configurazioneIncaricoDto = ConfigurazioneIncaricoConverter.convertToDto(configurazioneIncarico);

			/*
			 * reperisco elenco di Aoo relative alla configurazione
			 */
			BooleanExpression p = QConfigurazioneIncaricoAoo.configurazioneIncaricoAoo.primaryKey.idConfigurazioneIncarico.eq(configurazioneIncarico.getId());
			Iterable<ConfigurazioneIncaricoAoo> itConfigurazioneIncaricoAoo = configurazioneIncaricoAooRepository.findAll(p, new QSort(QConfigurazioneIncaricoAoo.configurazioneIncaricoAoo.ordineFirma.asc()).getOrderSpecifiers().get(0));
			List<ConfigurazioneIncaricoAoo> listConfigurazioneIncaricoAoo = Lists.newArrayList(itConfigurazioneIncaricoAoo);
			
			Date dataIncarico = configurazioneIncarico.getDataModifica();
			if (dataIncarico == null) {
				dataIncarico = configurazioneIncarico.getDataCreazione();
			}
			
			if(listConfigurazioneIncaricoAoo!=null) {
				List<ConfigurazioneIncaricoAooDto> liConfigurazioneIncaricoAooDto = ConfigurazioneIncaricoAooConverter
						.convertToDto(listConfigurazioneIncaricoAoo, dataIncarico);
				
				if(liConfigurazioneIncaricoAooDto!=null) {
					Iterator<ConfigurazioneIncaricoAooDto> it = liConfigurazioneIncaricoAooDto.iterator();
					while(it.hasNext()) {
						ConfigurazioneIncaricoAooDto configurazioneIncaricoAooDto = it.next();
						Aoo aoo = aooRepository.findOne(configurazioneIncaricoAooDto.getIdAoo());
						configurazioneIncaricoAooDto.setDescrizioneAoo(aoo.getDescrizione());
					}
					configurazioneIncaricoDto.setListConfigurazioneIncaricoAooDto(liConfigurazioneIncaricoAooDto);
				}
			}

			/*
			 * reperisco elenco di Profilo relativi alla configurazione
			 */
			List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto = configurazioneIncaricoProfiloService
					.findByConfigurazioneIncarico(configurazioneIncarico.getId(), dataIncarico);
			
			if(listConfigurazioneIncaricoProfiloDto!=null) {
				configurazioneIncaricoDto.setListConfigurazioneIncaricoProfiloDto(listConfigurazioneIncaricoProfiloDto);
			}
		}
		return configurazioneIncaricoDto;
	}

}
