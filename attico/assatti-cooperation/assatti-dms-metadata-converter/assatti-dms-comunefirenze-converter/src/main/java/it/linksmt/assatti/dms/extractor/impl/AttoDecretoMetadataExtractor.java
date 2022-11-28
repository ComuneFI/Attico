package it.linksmt.assatti.dms.extractor.impl;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.dms.util.MetadataCommonUtil;
import it.linksmt.assatti.service.converter.DmsMetadataExtractor;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.utility.StringUtil;

public class AttoDecretoMetadataExtractor implements DmsMetadataExtractor {

	@Override
	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext) {
		
		DmsPermanentProperties retVal = new DmsPermanentProperties();
		
		// Proprietà Comuni
		MetadataCommonUtil.addCommonProperties(retVal, atto, documentoPrincipale);
		MetadataCommonUtil.addAutoreByCodiceIncarico(retVal, atto, appContext, 
				ConfigurazioneTaskEnum.DEC_RESPONSABILE_PROPONENTE.getCodice());
		
		// Proprietà specifiche dei decreti
		Map<String, Object> documentProperties = retVal.getDocumentProperties();
		documentProperties.put(DmsComuneFirenzeConstants.DATA_RIFERIMENTO, atto.getDataAdozione().toDate());
		
		if (atto.getDataEsecutivita() != null) {
			documentProperties.put(DmsComuneFirenzeConstants.DATA_ESECUTIVITA,
					MetadataCommonUtil.DATA_ESECUTIVITA_DF.format(atto.getDataEsecutivita().toDate()));
		}
				
		// Firmatario (Emanante)
		if (atto.getEmananteProfilo() != null) {
			String firmatario = atto.getEmananteProfilo().getUtente().getCognome() + " " + 
					atto.getEmananteProfilo().getUtente().getNome();
			documentProperties.put(DmsComuneFirenzeConstants.FIRMATARIO_DECRETO, firmatario);
			if (!StringUtil.isNull(atto.getDescrizioneQualificaEmanante())) {
				documentProperties.put(DmsComuneFirenzeConstants.RUOLO_FIRMATARIO_DECRETO, atto.getDescrizioneQualificaEmanante());
			}
		}
		
		// Pubblicazione
		MetadataCommonUtil.addPubblicazioneProperties(retVal, atto);
		
		// Conservazione
		MetadataCommonUtil.addConservazioneProperties(retVal, atto);
		
		// Associazioni (Allegati)
		MetadataCommonUtil.addAttachments(retVal, atto);
		
		retVal.setDocumentProperties(documentProperties);
		return retVal;
	}

}
