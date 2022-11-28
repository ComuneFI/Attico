package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.CampoTipoAtto;
import it.linksmt.assatti.datalayer.domain.CampoTipoAttoId;
import it.linksmt.assatti.datalayer.repository.CampoTipoAttoRepository;
import it.linksmt.assatti.service.converter.CampoTipoAttoConverter;
import it.linksmt.assatti.service.dto.CampoTipoAttoDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Service class for managing CampoTipoAtto.
 */
@Service
@Transactional
public class CampoTipoAttoService {

	private final Logger log = LoggerFactory.getLogger(CampoTipoAttoService.class);

	@Inject
	private CampoTipoAttoRepository campoTipoAttoRepository;

	@Transactional(readOnly = true)
	public List<CampoTipoAttoDto> findAll() throws ServiceException {
		return CampoTipoAttoConverter.convertToDto(campoTipoAttoRepository.findAll());
	}

	@Transactional(readOnly = true)
	public List<CampoTipoAttoDto> findByTipoAtto(Long idTipoAtto) throws ServiceException {
		List<CampoTipoAtto> listCampotipoAtto = campoTipoAttoRepository.findByTipoAtto(idTipoAtto);
		return CampoTipoAttoConverter.convertToDto(listCampotipoAtto);
	}

	@Transactional
	public CampoTipoAttoDto save(CampoTipoAttoDto campoTipoAttoDto) throws ServiceException {
		CampoTipoAttoDto s = null;
		if(campoTipoAttoDto!=null) {
			CampoTipoAtto campoTipoAtto = null;
			
			// if exist, it's an update
			try {
				CampoTipoAttoId id = new CampoTipoAttoId();
				id.setIdCampo(campoTipoAttoDto.getId());
				id.setIdTipoAtto(campoTipoAttoDto.getIdTipoAtto());
				campoTipoAtto = campoTipoAttoRepository.findOne(id);
				campoTipoAtto.setVisibile(campoTipoAttoDto.getVisibile());
			} catch(Exception e) {
				log.debug(e.getMessage());
			}

			// if not exist, it's an add
			if(campoTipoAtto==null) {
				campoTipoAtto = CampoTipoAttoConverter.convertToModel(campoTipoAttoDto);
			}

			campoTipoAtto = campoTipoAttoRepository.save(campoTipoAtto);
			s = CampoTipoAttoConverter.convertToDto(campoTipoAtto);
		} else {
			log.debug("Errore salvando campoTipoAttoDto!");
		}
		return s;
	}

	@Transactional
	public List<CampoTipoAttoDto> save(List<CampoTipoAttoDto> campoTipoAttoDto) throws ServiceException {
		List<CampoTipoAttoDto> results = null;
		if (campoTipoAttoDto != null) {
			results = new ArrayList<>();
			for(CampoTipoAttoDto s : campoTipoAttoDto) {
				results.add(save(s));
			}
		}
		return results;
	}

	@Transactional
	public void delete(CampoTipoAttoDto campoTipoAttoDto) throws ServiceException {
		if(campoTipoAttoDto!=null) {
			CampoTipoAttoId id = new CampoTipoAttoId();
			id.setIdCampo(campoTipoAttoDto.getId());
			id.setIdTipoAtto(campoTipoAttoDto.getIdTipoAtto());
			CampoTipoAtto campoTipoAtto = campoTipoAttoRepository.findOne(id);

			campoTipoAttoRepository.delete(campoTipoAtto);
		}
	}

	@Transactional
	public void delete(List<CampoTipoAttoDto> campoTipoAttoDto) throws ServiceException {
		if (campoTipoAttoDto != null) {
			for(CampoTipoAttoDto s : campoTipoAttoDto) {
				delete(s);
			}
		}
	}

	@Transactional
	public void deleteByTipoAtto(Long idTipoAtto) throws ServiceException {
		List<CampoTipoAttoDto> campoTipoAttoDto = findByTipoAtto(idTipoAtto);
		if(campoTipoAttoDto!=null) {
			delete(campoTipoAttoDto);
		}
	}


}
