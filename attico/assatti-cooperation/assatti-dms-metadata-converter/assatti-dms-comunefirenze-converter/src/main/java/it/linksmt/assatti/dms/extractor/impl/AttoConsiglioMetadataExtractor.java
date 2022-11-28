package it.linksmt.assatti.dms.extractor.impl;

import java.util.Map;

import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.dms.util.MetadataCommonUtil;
import it.linksmt.assatti.service.converter.DmsMetadataExtractor;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;

public class AttoConsiglioMetadataExtractor implements DmsMetadataExtractor {

	@Override
	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext) {
		DmsPermanentProperties retVal = new DmsPermanentProperties();
		
		// Proprietà Comuni
		MetadataCommonUtil.addCommonProperties(retVal, atto, documentoPrincipale);
		
		Map<String, Object> documentProperties = retVal.getDocumentProperties();
		if (atto.getDataAdozione() != null) {
			documentProperties.put(DmsComuneFirenzeConstants.DATA_RIFERIMENTO,
					atto.getDataAdozione().toDate());
		}
		
		return retVal;
	}
}
