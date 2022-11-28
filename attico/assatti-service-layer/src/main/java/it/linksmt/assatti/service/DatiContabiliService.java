package it.linksmt.assatti.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.datalayer.domain.DatiContabili;
import it.linksmt.assatti.datalayer.repository.DatiContabiliRepository;
import it.linksmt.assatti.utility.StringUtil;

/**
 * Service class for managing liquidazione.
 */
@Service
public class DatiContabiliService {
	
	@Autowired
	private DatiContabiliRepository datiContabiliRepository;

	private final Logger log = LoggerFactory.getLogger(DatiContabiliService.class);

	public List<MovimentoContabileDto> elencoMovimento(long idAtto) {
		log.debug("LiquidazioneService :: elencoMovimento()");
		
		List<MovimentoContabileDto> listLiquidazioneDto = null;
		
		DatiContabili datiCont = datiContabiliRepository.findOne(idAtto);
		if ((datiCont != null) && (!StringUtil.isNull(datiCont.getDaticontabili()))) {
			listLiquidazioneDto = new Gson().fromJson(
				StringUtil.trimStr(datiCont.getDaticontabili()),
				new TypeToken<ArrayList<MovimentoContabileDto>>(){}.getType());
		}
		
		return listLiquidazioneDto;
	}
	
	public DatiContabili findOne(Long idAtto) {
		DatiContabili dc = null;
		if(idAtto!=null) {
			dc = datiContabiliRepository.findOne(idAtto);
		}
		return dc;
	}
	
	public void deleteFromAtto(Long attoId) {
		datiContabiliRepository.delete(attoId);
	}

}
