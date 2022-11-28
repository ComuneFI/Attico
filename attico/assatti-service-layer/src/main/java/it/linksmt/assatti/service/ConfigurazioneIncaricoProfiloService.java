package it.linksmt.assatti.service;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.querydsl.QSort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.common.collect.Lists;
import com.mysema.query.types.expr.BooleanExpression;

import it.linksmt.assatti.datalayer.domain.ConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.QConfigurazioneIncaricoProfilo;
import it.linksmt.assatti.datalayer.repository.ConfigurazioneIncaricoProfiloRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.service.converter.ConfigurazioneIncaricoProfiloConverter;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoProfiloDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Service class for managing ConfigurazioneIncaricoProfilo.
 */
@Service
@Transactional
public class ConfigurazioneIncaricoProfiloService {

	@SuppressWarnings("unused")
	private final Logger log = LoggerFactory.getLogger(ConfigurazioneIncaricoProfiloService.class);

	@Inject
	private ConfigurazioneIncaricoProfiloRepository configurazioneIncaricoProfiloRepository;
	
	@Inject
	private ProfiloRepository profiloRepository;

	@Transactional(readOnly = true)
	public long countConfIncaricoByProfiloAttiAttivi(long profiloId) {
		long count = 0;
		if(profiloId > 0L) {
			BigInteger c = configurazioneIncaricoProfiloRepository.countConfIncaricoByProfiloAttiAttivi(profiloId);
			count = c.longValue();
		}
		return count;
	}
	
	public List<ConfigurazioneIncaricoProfilo> findByConfigurazioneIncaricoOrderByOrdineFirma(Long idConfigurazioneIncarico){
		BooleanExpression p = QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.primaryKey.idConfigurazioneIncarico.eq(idConfigurazioneIncarico);
		Iterable<ConfigurazioneIncaricoProfilo> it = configurazioneIncaricoProfiloRepository.findAll(p, new QSort(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.ordineFirma.asc()).getOrderSpecifiers().get(0));
		List<ConfigurazioneIncaricoProfilo> list =  Lists.newArrayList(it);
		return list;
	}
	
	@Transactional(readOnly = true)
	public Page<ConfigurazioneIncaricoProfiloDto> findByProfiloId(Long idProfilo, boolean onlyAttoInItinere, Pageable paginazione, Collection<Long> confsExclusion, boolean notReassignedOnly) throws ServiceException {
		List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto = null;
		
		BooleanExpression p = QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.primaryKey.idProfilo.eq(idProfilo);
		if(onlyAttoInItinere) {
			p = p.and(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.attoIdItinere.isNotNull());
		}
		if(confsExclusion!=null && confsExclusion.size() > 0) {
			p = p.and(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.primaryKey.idConfigurazioneIncarico.notIn(confsExclusion));
		}
		if(notReassignedOnly) {
			p = p.and(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.reassigned.isNull().or(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.reassigned.isFalse()));
		}
		Page<ConfigurazioneIncaricoProfilo> itPage = configurazioneIncaricoProfiloRepository.findAll(p, paginazione);
		
		if(itPage!=null) {
			listConfigurazioneIncaricoProfiloDto = new ArrayList<>();
			
			for(ConfigurazioneIncaricoProfilo c : itPage) {
				listConfigurazioneIncaricoProfiloDto.add(fillDto(c, null));
			}
		}
		listConfigurazioneIncaricoProfiloDto = listConfigurazioneIncaricoProfiloDto!=null ? listConfigurazioneIncaricoProfiloDto : new ArrayList<ConfigurazioneIncaricoProfiloDto>();
		Page<ConfigurazioneIncaricoProfiloDto> page = new PageImpl<ConfigurazioneIncaricoProfiloDto>(listConfigurazioneIncaricoProfiloDto, paginazione, itPage.getTotalElements());
		
		return page;
	}
	
	@Transactional(readOnly = true)
	public ConfigurazioneIncaricoProfiloDto findByProfiloIdConfIncaricoId(Long idProfilo, Long confIncaricoId, boolean onlyAttoInItinere) throws ServiceException {
		ConfigurazioneIncaricoProfiloDto configurazioneIncaricoProfiloDto = null;
		
		BooleanExpression p = QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.primaryKey.idProfilo.eq(idProfilo).and(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.primaryKey.idConfigurazioneIncarico.eq(confIncaricoId));
		if(onlyAttoInItinere) {
			p = p.and(QConfigurazioneIncaricoProfilo.configurazioneIncaricoProfilo.attoIdItinere.isNotNull());
		}
		ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo = configurazioneIncaricoProfiloRepository.findOne(p);
		
		if(configurazioneIncaricoProfilo!=null) {
			configurazioneIncaricoProfiloDto = fillDto(configurazioneIncaricoProfilo, null);
		}
		
		return configurazioneIncaricoProfiloDto;
	}
	
	@Transactional(readOnly = true)
	public List<ConfigurazioneIncaricoProfiloDto> findByConfigurazioneIncarico(Long idConfigurazioneIncarico, Date dataIncarico) throws ServiceException {
		List<ConfigurazioneIncaricoProfiloDto> listConfigurazioneIncaricoProfiloDto = null;
		
		List<ConfigurazioneIncaricoProfilo> listConfigurazioneIncaricoProfilo = this.findByConfigurazioneIncaricoOrderByOrdineFirma(idConfigurazioneIncarico);
		
		if(listConfigurazioneIncaricoProfilo!=null) {
			listConfigurazioneIncaricoProfiloDto = new ArrayList<>();
			
			for(ConfigurazioneIncaricoProfilo c : listConfigurazioneIncaricoProfilo) {
				listConfigurazioneIncaricoProfiloDto.add(fillDto(c, dataIncarico));
			}
		}
		
		return listConfigurazioneIncaricoProfiloDto;
	}
	
	private ConfigurazioneIncaricoProfiloDto fillDto(ConfigurazioneIncaricoProfilo configurazioneIncaricoProfilo, Date dataIncarico) {
		ConfigurazioneIncaricoProfiloDto configurazioneIncaricoProfiloDto = null;
		if(configurazioneIncaricoProfilo!=null) {
			configurazioneIncaricoProfiloDto = ConfigurazioneIncaricoProfiloConverter
					.convertToDto(configurazioneIncaricoProfilo, dataIncarico);

			/*
			 * reperisco profilo relativo alla configurazione
			 */
			Profilo profilo = profiloRepository.getOne(configurazioneIncaricoProfilo.getPrimaryKey().getIdProfilo());
			if(profilo!=null) {
				configurazioneIncaricoProfiloDto.setDescrizioneProfilo(profilo.getDescrizione());
				if(profilo.getUtente()!=null) {
					configurazioneIncaricoProfiloDto.setUtenteNome(profilo.getUtente().getNome());
					configurazioneIncaricoProfiloDto.setUtenteCognome(profilo.getUtente().getCognome());
				}
			}
		}
		return configurazioneIncaricoProfiloDto;
	}

}
