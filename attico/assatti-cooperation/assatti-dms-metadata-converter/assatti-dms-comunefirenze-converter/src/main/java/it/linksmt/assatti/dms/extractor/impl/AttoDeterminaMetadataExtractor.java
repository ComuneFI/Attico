package it.linksmt.assatti.dms.extractor.impl;

import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.dms.util.MetadataCommonUtil;
import it.linksmt.assatti.service.converter.DmsMetadataExtractor;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.StringUtil;

public class AttoDeterminaMetadataExtractor implements DmsMetadataExtractor {
	
	@Override
	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext) {
		
		DmsPermanentProperties retVal = new DmsPermanentProperties();
		
		// Proprietà Comuni
		MetadataCommonUtil.addCommonProperties(retVal, atto, documentoPrincipale);
		MetadataCommonUtil.addAutoreByCodiceIncarico(retVal, atto, appContext, ConfigurazioneTaskEnum.RESPONSABILE_TECNICO.getCodice());
		
		// Proprietà specifiche delle determine
		Map<String, Object> documentProperties = retVal.getDocumentProperties();
		if (atto.getDataEsecutivita() != null) {
			documentProperties.put(DmsComuneFirenzeConstants.DATA_ESECUTIVITA,
					MetadataCommonUtil.DATA_ESECUTIVITA_DF.format(atto.getDataEsecutivita().toDate()));
			
			documentProperties.put(DmsComuneFirenzeConstants.DATA_RIFERIMENTO, atto.getDataEsecutivita().toDate());
		}
		
		// Firmatario (Emanante)
		if (atto.getEmananteProfilo() != null) {
			String firmatario = atto.getEmananteProfilo().getUtente().getCognome() + " " + 
					atto.getEmananteProfilo().getUtente().getNome();
			documentProperties.put(DmsComuneFirenzeConstants.FIRMATARIO_DETERMINA, firmatario);
			if (!StringUtil.isNull(atto.getDescrizioneQualificaEmanante())) {
				documentProperties.put(DmsComuneFirenzeConstants.RUOLO_FIRMATARIO_DETERMINA, atto.getDescrizioneQualificaEmanante());
			}
		}
		
		// Parere contabile
		boolean parereContabile = false;
		if(atto.getPareri()!=null) {
			Set<Parere> listParere = atto.getPareri();
			for (Parere parere : listParere) {
				if ((parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) &&
					(parere.getTipoAzione() != null) && (!StringUtil.isNull(parere.getTipoAzione().getCodice()))) {
					
					// i pareri sono ordinati per data dal più recente, viene inserito il primo per ogni tipologia
					String tipoPar = StringUtil.trimStr(parere.getTipoAzione().getCodice());
					if(tipoPar.equalsIgnoreCase(TipiParereEnum.VISTO_CONTABILE.getCodice()) ) {
						parereContabile = true;
						
						// Visto sempre OK
						documentProperties.put(DmsComuneFirenzeConstants.FLAG_VISTO_REGOLARITA_CONTABILE, Boolean.TRUE);
						documentProperties.put(DmsComuneFirenzeConstants.TIPO_VISTO_REGOLARITA_CONTABILE, DmsComuneFirenzeConstants.VAL_PARERE_POSITIVO);
						
						DateTime dataParere = parere.getData() != null ? parere.getData() : parere.getCreatedDate();
						if (dataParere != null) {
							documentProperties.put(DmsComuneFirenzeConstants.DATA_VISTO_REGOLARITA_CONTABILE, dataParere.toDate());
						}
						break;
					} 
				}
			}
		}
		
		if (!parereContabile) {
			documentProperties.put(DmsComuneFirenzeConstants.FLAG_VISTO_REGOLARITA_CONTABILE, Boolean.FALSE);
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
