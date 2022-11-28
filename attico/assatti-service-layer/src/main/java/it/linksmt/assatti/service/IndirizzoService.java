package it.linksmt.assatti.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.Indirizzo;
import it.linksmt.assatti.datalayer.domain.QIndirizzo;
import it.linksmt.assatti.datalayer.repository.IndirizzoRepository;
import it.linksmt.assatti.service.dto.IndirizzoDTO;
import it.linksmt.assatti.service.dto.IndirizzoSearchDTO;
import it.linksmt.assatti.service.util.PaginationUtil;
import com.mysema.query.types.expr.BooleanExpression;

/**
 * Service class for managing indirizzo.
 */
@Service
@Transactional
public class IndirizzoService {

	private final Logger log = LoggerFactory.getLogger(IndirizzoService.class);

	@Inject
	private IndirizzoRepository indirizzoRepository;
	
	@Transactional(readOnly=true)
	public ResponseEntity<List<IndirizzoDTO>> getAll(IndirizzoSearchDTO search, Boolean onlyEnabled, Integer offset, Integer limit) throws URISyntaxException{
		BooleanExpression predicateIndirizzo = QIndirizzo.indirizzo.id.isNotNull();
    	
    	Long idLong = null;
		if(search.getId()!=null && !"".equals(search.getId().trim())){
			try{
				idLong = Long.parseLong(search.getId().trim());
			}catch(Exception e){};
		}
		
		if(idLong!=null){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.id.eq(idLong));
		}
		
		if(search.getDug()!=null && !"".equals(search.getDug().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.dug.containsIgnoreCase(search.getDug().trim()));
		}
		
		if(search.getToponimo()!=null && !"".equals(search.getToponimo().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.toponimo.containsIgnoreCase(search.getToponimo().trim()));
		}
		
		if(search.getCivico()!=null && !"".equals(search.getCivico().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.civico.containsIgnoreCase(search.getCivico().trim()));
		}
		
		if(search.getCap()!=null && !"".equals(search.getCap().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.cap.containsIgnoreCase(search.getCap().trim()));
		}
		
		if(search.getComune()!=null && !"".equals(search.getComune().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.comune.containsIgnoreCase(search.getComune().trim()));
		}
		
		if(search.getProvincia()!=null && !"".equals(search.getProvincia().trim())){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.provincia.containsIgnoreCase(search.getProvincia().trim()));
		}
		
		if(onlyEnabled!=null && onlyEnabled){
			predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.attivo.eq(true));
		} else {
			String stato = search.getStato();
			if(stato != null && !"".equals(stato)){

				if("0".equals(stato)){ // Indirizzi attivi
					predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.attivo.eq(true));
				}
				else if("1".equals(stato)){ // Indirizzi disattivati
					predicateIndirizzo = predicateIndirizzo.and(QIndirizzo.indirizzo.attivo.eq(false).or(QIndirizzo.indirizzo.attivo.isNull()));
				}
				else{ // Tutti

				}
			}
		}
    	
        Page<Indirizzo> page = indirizzoRepository.findAll(predicateIndirizzo, PaginationUtil.generatePageRequest(offset, limit));
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/indirizzos", offset, limit);
       
        return new ResponseEntity<List<IndirizzoDTO>>(this.convertToDTO(page.getContent()), headers, HttpStatus.OK);
	}
	
	private List<IndirizzoDTO> convertToDTO(List<Indirizzo> indirizzi){
		List<IndirizzoDTO> indirizziDto = new ArrayList<IndirizzoDTO>();
		for(Indirizzo indirizzo : indirizzi){
			IndirizzoDTO dto = new IndirizzoDTO();
			BeanUtils.copyProperties(indirizzo, dto);
			indirizziDto.add(dto);
		}
		return indirizziDto;
	}
	
	@Transactional(readOnly=false)
	public Indirizzo getById(Long id){
		Indirizzo indirizzo = indirizzoRepository.findOne(id);
		return indirizzo;
	}
	
	@Transactional(readOnly=false)
	public void disableIndirizzo(Long id){
		log.debug("disableIndirizzo idindirizzo" + id);
		Indirizzo indirizzo = indirizzoRepository.findOne(id);
		if(indirizzo!=null){
			indirizzo.setAttivo(false);
			indirizzoRepository.save(indirizzo);
		}
	}
	
	@Transactional(readOnly=false)
	public void enableIndirizzo(Long id){
		log.debug("enableIndirizzo idindirizzo" + id);
		Indirizzo indirizzo = indirizzoRepository.findOne(id);
		if(indirizzo!=null){
			indirizzo.setAttivo(true);
			indirizzoRepository.save(indirizzo);
		}
	}
}
