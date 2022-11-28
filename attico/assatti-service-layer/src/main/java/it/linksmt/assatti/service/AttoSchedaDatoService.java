package it.linksmt.assatti.service;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Map;

import javax.inject.Inject;

import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.linksmt.assatti.datalayer.domain.AttoSchedaDato;
import it.linksmt.assatti.datalayer.domain.AttoSchedaDatoId;
import it.linksmt.assatti.datalayer.domain.Beneficiario;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.SchedaDato;
import it.linksmt.assatti.datalayer.domain.TipoDatoEnum;
import it.linksmt.assatti.datalayer.repository.AttoSchedaDatoRepository;
import it.linksmt.assatti.datalayer.repository.SchedaDatoRepository;

/**
 * Service class for managing users.
 */
@Service
@Transactional
public class AttoSchedaDatoService {
	private final Logger log = LoggerFactory.getLogger(AttoSchedaDatoService.class);
	private static DateTimeFormatter formatterDateTime = DateTimeFormat.forPattern("yyyy-MM-dd'T'HH:mm:ss.000'Z'");

	private static DateTimeFormatter formatterDate = DateTimeFormat.forPattern("yyyy-MM-dd");

	@Inject
	private AttoSchedaDatoRepository attoSchedaDatoRepository;
	@Inject
	private SchedaDatoRepository schedaDatoRepository;
	
	@Transactional
	public AttoSchedaDato salva(AttoSchedaDato asd){
		return attoSchedaDatoRepository.save(asd);
	}
	
	@Transactional(readOnly = true)
	public Object dbToVo(AttoSchedaDato attoSchedaDato) {
		Object object = null;

		switch (attoSchedaDato.getTipoDato()) {
		case text:
			object = attoSchedaDato.getTestoValore();
			break;
		case select:
			object = attoSchedaDato.getTestoValore();
			break;
		case date:
			object = formatterDate.print(attoSchedaDato.getDataValore());
			break;
		case datetime:
			object = formatterDateTime.print(attoSchedaDato.getDataValore());
			break;
		case number:
			object = attoSchedaDato.getNumeroValore();
			break;
		case file:
			if(attoSchedaDato.getFileValore() != null ){
				attoSchedaDato.getFileValore().getNomeFile();
				object = attoSchedaDato.getFileValore();
			}
			break;
		case beneficiario:
			if(attoSchedaDato.getBeneficiario() !=null){
				object = attoSchedaDato.getBeneficiario();
			}
			break;
		case url:
			object = attoSchedaDato.getUrlValore();
			break;
		case valuta:
			if(attoSchedaDato.getValutaValore()!=null){
		    	DecimalFormat df = new DecimalFormat();
		    	df.setMinimumFractionDigits(2);
		    	df.setGroupingUsed(false);
		        object = (df.format(attoSchedaDato.getValutaValore()).replaceAll("\\.", ","));
	    	}else{
	    		object = "";
	    	}
			break;
		default:
			break;
		}

		return object;
	}

	public void voToDb(AttoSchedaDato attoSchedaDato, Object valore)   {
		log.debug("voToDb valore:" + valore);
		if(valore != null ){
			log.debug("voToDb valore:" + valore.getClass());
		}
		
		
		if (valore != null) {
			switch (attoSchedaDato.getTipoDato()) {
			case text:
				attoSchedaDato.setTestoValore(valore + "");
				break;
			case select:
				attoSchedaDato.setTestoValore(valore + "");
				break;
			case date:
				attoSchedaDato.setDataValore(formatterDate.parseLocalDateTime(valore + ""));
				break;
			case datetime:
				attoSchedaDato.setDataValore(formatterDateTime.parseLocalDateTime(valore + ""));
				break;
			case number:
				attoSchedaDato.setNumeroValore(new BigDecimal(valore + ""));
				break;
			case file:
				Map<String,Object> fileValoreMap =(Map<String,Object>) valore;
				if(fileValoreMap != null ){
					Long id =  Long.parseLong( fileValoreMap.get("id") +"");
					log.debug("File id:" + id);
					File fileValore = new File(id);
					attoSchedaDato.setFileValore( fileValore  );
				}
				break;	
			case beneficiario:
				Map<String,Object> beneficiarioValoreMap =(Map<String,Object>) valore;
				if(beneficiarioValoreMap != null ){
					Long id =  Long.parseLong( beneficiarioValoreMap.get("id") +"");
					Beneficiario beneficiario = new Beneficiario();
					beneficiario.setId(id);
					if(id < 0L){
						attoSchedaDato.setBeneficiario(null);
					}else{
						attoSchedaDato.setBeneficiario(beneficiario);
					}
				}
				break;
			case url:
				attoSchedaDato.setUrlValore(valore +"");;
				break;
			case valuta:
		    	String valoreStr = valore.toString().replaceAll(",", "\\.");
		    	if(!valoreStr.isEmpty()){
		    		attoSchedaDato.setValutaValore(new BigDecimal(valoreStr));
		    	}else{
		    		attoSchedaDato.setValutaValore(null);
		    	}
				break;
			default:
				break;
			}

		}else{
			if(attoSchedaDato.getTipoDato().name().equals(TipoDatoEnum.file.name())){
				attoSchedaDato.setFileValore(null);
			}else if(attoSchedaDato.getTipoDato().name().equals(TipoDatoEnum.beneficiario.name())){
				attoSchedaDato.setBeneficiario(null);
			}
		}
		attoSchedaDatoRepository.save(attoSchedaDato);
	}

	@Transactional(readOnly = true)
	public AttoSchedaDato getAttoSchedaDato(Long attoId, Long schemaDatoId, Integer progressivoElemento) {

		SchedaDato schemaDato = schedaDatoRepository.findOne(schemaDatoId);
		AttoSchedaDatoId idS = new AttoSchedaDatoId(attoId, schemaDato.getScheda().getId(), progressivoElemento,
				schemaDatoId);
		AttoSchedaDato attoSchedaDato = attoSchedaDatoRepository.findOne(idS);
		return attoSchedaDato;

	}
	
	/**
	 * Delete an {@link AttoSchedaDato} object.
	 * @param asd The {@link AttoSchedaDato} instance to delete.
	 */
	@Transactional
	public void delete(AttoSchedaDato asd){
		attoSchedaDatoRepository.delete(asd);
	}

}
