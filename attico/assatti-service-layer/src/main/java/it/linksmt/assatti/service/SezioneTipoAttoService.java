package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.SezioneTipoAtto;
import it.linksmt.assatti.datalayer.domain.SezioneTipoAttoId;
import it.linksmt.assatti.datalayer.repository.SezioneTipoAttoRepository;
import it.linksmt.assatti.service.converter.SezioneTipoAttoConverter;
import it.linksmt.assatti.service.dto.SezioneTipoAttoDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Service class for managing SezioneTipoAtto.
 */
@Service
@Transactional
public class SezioneTipoAttoService {

	private final Logger log = LoggerFactory.getLogger(SezioneTipoAttoService.class);

	@Inject
	private SezioneTipoAttoRepository sezioneTipoAttoRepository;

	@Transactional(readOnly = true)
	public List<SezioneTipoAttoDto> findAll() throws ServiceException {
		return SezioneTipoAttoConverter.convertToDto(sezioneTipoAttoRepository.findAll());
	}

	@Transactional(readOnly = true)
	public List<SezioneTipoAttoDto> findByTipoAtto(Long idTipoAtto) throws ServiceException {
		List<SezioneTipoAtto> listSezionetipoAtto = sezioneTipoAttoRepository.findByTipoAtto(idTipoAtto);
		return SezioneTipoAttoConverter.convertToDto(listSezionetipoAtto);
	}
	
	@Transactional(readOnly = true)
	public Map<String, Boolean> findByTipoAttoVisibilityMap(Long idTipoAtto) throws ServiceException {
		List<SezioneTipoAttoDto> list = this.findByTipoAtto(idTipoAtto);
		Map<String, Boolean> visibilityMap = new HashMap<String, Boolean>();
		if (list!=null) {
			for(SezioneTipoAttoDto item : list) {
				if(item!=null && item.getCodice()!=null && !item.getCodice().trim().isEmpty()) {
					visibilityMap.put(item.getCodice().trim().toLowerCase(), item.getVisibile());
				}
			}
		}
		return visibilityMap;
	}
	

	public List<SezioneTipoAtto> findSezioniTipoAttoByTipoAtto(Long idTipoAtto) throws ServiceException {
		return sezioneTipoAttoRepository.findByTipoAtto(idTipoAtto);
	}


	@Transactional
	public SezioneTipoAttoDto save(SezioneTipoAttoDto sezioneTipoAttoDto) throws ServiceException {
		SezioneTipoAttoDto s = null;
		if(sezioneTipoAttoDto!=null) {
			SezioneTipoAtto sezioneTipoAtto = null;
			
			// if exist, it's an update
			try {
				SezioneTipoAttoId id = new SezioneTipoAttoId();
				id.setIdSezione(sezioneTipoAttoDto.getId());
				id.setIdTipoAtto(sezioneTipoAttoDto.getIdTipoAtto());
				sezioneTipoAtto = sezioneTipoAttoRepository.findOne(id);
				sezioneTipoAtto.setVisibile(sezioneTipoAttoDto.getVisibile());
			} catch(Exception e) {
				log.debug(e.getMessage());
			}

			// if not exist, it's an add
			if(sezioneTipoAtto==null) {
				sezioneTipoAtto = SezioneTipoAttoConverter.convertToModel(sezioneTipoAttoDto);
			}

			sezioneTipoAtto = sezioneTipoAttoRepository.save(sezioneTipoAtto);
			s = SezioneTipoAttoConverter.convertToDto(sezioneTipoAtto);
		} else {
			log.debug("Errore salvando sezioneTipoAttoDto!");
		}
		return s;
	}

	@Transactional
	public List<SezioneTipoAttoDto> save(List<SezioneTipoAttoDto> sezioneTipoAttoDto) throws ServiceException {
		List<SezioneTipoAttoDto> results = null;
		if (sezioneTipoAttoDto != null) {
			results = new ArrayList<>();
			for(SezioneTipoAttoDto s : sezioneTipoAttoDto) {
				results.add(save(s));
			}
		}
		return results;
	}

	@Transactional
	public void delete(SezioneTipoAttoDto sezioneTipoAttoDto) throws ServiceException {
		if(sezioneTipoAttoDto!=null) {
			SezioneTipoAttoId id = new SezioneTipoAttoId();
			id.setIdSezione(sezioneTipoAttoDto.getId());
			id.setIdTipoAtto(sezioneTipoAttoDto.getIdTipoAtto());
			SezioneTipoAtto sezioneTipoAtto = sezioneTipoAttoRepository.findOne(id);

			sezioneTipoAttoRepository.delete(sezioneTipoAtto);
		}
	}

	@Transactional
	public void delete(List<SezioneTipoAttoDto> sezioneTipoAttoDto) throws ServiceException {
		if (sezioneTipoAttoDto != null) {
			for(SezioneTipoAttoDto s : sezioneTipoAttoDto) {
				delete(s);
			}
		}
	}

	@Transactional
	public void deleteByTipoAtto(Long idTipoAtto) throws ServiceException {
		List<SezioneTipoAttoDto> sezioneTipoAttoDto = findByTipoAtto(idTipoAtto);
		if(sezioneTipoAttoDto!=null) {
			delete(sezioneTipoAttoDto);
		}
	}


}
