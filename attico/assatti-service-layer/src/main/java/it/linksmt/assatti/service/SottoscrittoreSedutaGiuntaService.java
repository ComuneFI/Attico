package it.linksmt.assatti.service;

import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.domain.SottoscrittoreSedutaGiunta;
import it.linksmt.assatti.datalayer.domain.Verbale;
import it.linksmt.assatti.datalayer.repository.SottoscrittoreSedutaGiuntaRepository;

import javassist.NotFoundException;


/**
 * Service class for managing SottoscrittoreSedutaGiunta.
 */
@Service
@Transactional
public class SottoscrittoreSedutaGiuntaService {
	private final Logger log = LoggerFactory.getLogger(SottoscrittoreSedutaGiuntaService.class);
	
	@Inject
	private SottoscrittoreSedutaGiuntaRepository sottoscrittoreRepository;
	
	@Transactional(readOnly = true)
	public List<SottoscrittoreSedutaGiunta> getSottoscrittoriVerbale(Long idVerbale){
		log.debug(String.format("getSottoscrittoriVerbale - idVerbale: %s", idVerbale));
		
		return sottoscrittoreRepository.findByVerbaleOrderByOrdineFirmaAsc(idVerbale);
	}
	
	@Transactional(readOnly = true)
	public SottoscrittoreSedutaGiunta getNextFirmatarioVerbale(Long idVerbale){
		log.debug(String.format("getNextFirmatarioVerbale - idVerbale: %s", idVerbale));
		
		SottoscrittoreSedutaGiunta retValue = null;
		List<SottoscrittoreSedutaGiunta> listaFirmatariSuccessivi = sottoscrittoreRepository.findByVerbaleAndFirmatoOrderByOrdineFirmaAsc(idVerbale, false);
		
		if (listaFirmatariSuccessivi != null && listaFirmatariSuccessivi.size()>0)
			retValue = listaFirmatariSuccessivi.get(0);
		
		if (retValue != null)
			log.debug(String.format("getNextFirmatarioVerbale - Verbale con id:%s - Il next firmatario ha idProfilo:%s", idVerbale, retValue.getProfilo().getId()));
		else
			log.debug(String.format("getNextFirmatarioVerbale - Verbale con id:%s - Nessun utente deve ancora firmare questo verbale!!", idVerbale));
		
		return retValue;
	}
	
	public Verbale setVerbaleFirmato(Long idVerbale, Long idProfilo) throws NotFoundException{
		SottoscrittoreSedutaGiunta sottoscrittore = sottoscrittoreRepository.findByVerbaleAndProfilo(idVerbale, idProfilo);
		
		if (sottoscrittore != null){
			sottoscrittore.setFirmato(true);
			sottoscrittore.setDataFirma(new DateTime());
			
			return sottoscrittoreRepository.save(sottoscrittore).getVerbale();
		} else {
			throw new NotFoundException(String.format("Nessun SottoscrittoreSedutaGiunta trovato con idVerbale:%s - idProfilo:%s", idVerbale, idProfilo));
		}
	}
	
	public Set<SottoscrittoreSedutaGiunta> svuotaRiferimenti(Set<SottoscrittoreSedutaGiunta> sottoscrittori){
		
		for (SottoscrittoreSedutaGiunta ssg : sottoscrittori){
			
			ssg.getProfilo().setAoo(null);
			if(ssg.getProfilo().getHasQualifica() != null){
				log.debug("svuotaRiferimenti getHasQualifica: " + ssg.getProfilo().getHasQualifica().size());
			}
			ssg.setSedutaresoconto(new SedutaGiunta(
					ssg.getSedutaresoconto().getId(), 
					ssg.getSedutaresoconto().getLuogo(), 
					ssg.getSedutaresoconto().getPrimaConvocazioneInizio()));
		}
		
		return sottoscrittori;
	}

}
