package it.linksmt.assatti.dms.extractor.impl;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.context.ApplicationContext;

import it.linksmt.assatti.bpm.service.CodiceProgressivoService;
import it.linksmt.assatti.datalayer.domain.AttiOdg;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.ConfigurazioneTaskEnum;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.domain.SedutaGiunta;
import it.linksmt.assatti.datalayer.repository.AttiOdgRepository;
import it.linksmt.assatti.dms.util.DmsComuneFirenzeConstants;
import it.linksmt.assatti.dms.util.MetadataCommonUtil;
import it.linksmt.assatti.service.converter.DmsMetadataExtractor;
import it.linksmt.assatti.service.dto.DmsPermanentProperties;
import it.linksmt.assatti.service.util.TipiParereEnum;
import it.linksmt.assatti.utility.StringUtil;

public class AttoDeliberaMetadataExtractor implements DmsMetadataExtractor {

	@Override
	public DmsPermanentProperties extractMetadataByTipoAtto(Atto atto, DocumentoPdf documentoPrincipale, ApplicationContext appContext) {
		
		DmsPermanentProperties retVal = new DmsPermanentProperties();
		
		// Proprietà Comuni
		MetadataCommonUtil.addCommonProperties(retVal, atto, documentoPrincipale);
		
		// Per DIG e DIC Responsabile tecnico non presente e quindi non viene aggiunto l'autore
		MetadataCommonUtil.addAutoreByCodiceIncarico(retVal, atto, appContext, ConfigurazioneTaskEnum.RESPONSABILE_TECNICO.getCodice());
		
		// Proprietà specifiche delle delibere
		Map<String, Object> documentProperties = retVal.getDocumentProperties();
		documentProperties.put(DmsComuneFirenzeConstants.DATA_RIFERIMENTO, atto.getDataAdozione().toDate());
		
		if (atto.getDataEsecutivita() != null) {
			documentProperties.put(DmsComuneFirenzeConstants.DATA_ESECUTIVITA,
					MetadataCommonUtil.DATA_ESECUTIVITA_DF.format(atto.getDataEsecutivita().toDate()));
		}
		
		documentProperties.put(DmsComuneFirenzeConstants.FLAG_INIZIATIVA_POPOLARE, Boolean.FALSE);
		
		String numProposta = atto.getCodiceCifra();
		numProposta = numProposta.substring(numProposta.lastIndexOf(CodiceProgressivoService.DELIMITATORE) 
				+ CodiceProgressivoService.DELIMITATORE.length());
		
		documentProperties.put(DmsComuneFirenzeConstants.NUMERO_PROPOSTA, numProposta);
		documentProperties.put(DmsComuneFirenzeConstants.ANNO_PROPOSTA, atto.getCreatedDate().getYear());
		documentProperties.put(DmsComuneFirenzeConstants.DATA_PROPOSTA, atto.getCreatedDate().toDate());
		
		// Pareri tecnico e contabile
		if(atto.getPareri()!=null) {
			Set<Parere> listParere = atto.getPareri();
			
			boolean parContabile = false;
			boolean parTecnico = false;
			for (Parere parere : listParere) {
				if ((parere.getAnnullato() == null || parere.getAnnullato().booleanValue() == false) && 
					(parere.getTipoAzione() != null) && (!StringUtil.isNull(parere.getTipoAzione().getCodice()))) {
					
					// i pareri sono ordinati per data dal più recente, viene inserito il primo per ogni tipologia
					String tipoPar = StringUtil.trimStr(parere.getTipoAzione().getCodice());
					
					// Parere Contabile
					if(!parContabile && tipoPar.equalsIgnoreCase(TipiParereEnum.VISTO_CONTABILE.getCodice()) ) {
						parContabile = true;
						
						// Visto sempre OK
						documentProperties.put(DmsComuneFirenzeConstants.TIPO_VISTO_REGOLARITA_CONTABILE, 
								DmsComuneFirenzeConstants.VAL_PARERE_POSITIVO);
						
						if (parere.getProfilo() != null) {
							String respParere = parere.getProfilo().getUtente().getCognome() + " " +
									parere.getProfilo().getUtente().getNome();
							documentProperties.put(DmsComuneFirenzeConstants.RESPONSABILE_VISTO_REGOLARITA_CONTABILE,
									respParere);
						}
						
						DateTime dataParere = parere.getData() != null ? parere.getData() : parere.getCreatedDate();
						if (dataParere != null) {
							documentProperties.put(DmsComuneFirenzeConstants.DATA_VISTO_REGOLARITA_CONTABILE, dataParere.toDate());
						}
					}
					// Parere Tecnico
					if(!parTecnico && tipoPar.equalsIgnoreCase(TipiParereEnum.VISTO_TECNICO.getCodice()) ) {
						parTecnico = true;
						
						// Visto sempre OK
						documentProperties.put(DmsComuneFirenzeConstants.TIPO_VISTO_REGOLARITA_TECNICA, 
								DmsComuneFirenzeConstants.VAL_PARERE_POSITIVO);
						
						if (parere.getProfilo() != null) {
							String respParere = parere.getProfilo().getUtente().getCognome() + " " +
									parere.getProfilo().getUtente().getNome();
							documentProperties.put(DmsComuneFirenzeConstants.RESPONSABILE_PARERE_REGOLARITA_TECNICA,
									respParere);
						}
						
						DateTime dataParere = parere.getData() != null ? parere.getData() : parere.getCreatedDate();
						if (dataParere != null) {
							documentProperties.put(DmsComuneFirenzeConstants.DATA_PARERE_REGOLARITA_TECNICA, dataParere.toDate());
						}
					}
				}
			}
		}
		
		// Dati Seduta
		AttiOdgRepository attiOdgRepo = (AttiOdgRepository)appContext.getBean(AttiOdgRepository.class);
		List<AttiOdg> ordineGiornos = attiOdgRepo.findByAtto(atto);
		
		AttiOdg aOdg = null;
		if (ordineGiornos != null) {
			for(AttiOdg tmp : ordineGiornos) {					
				if(tmp.getEsito()!=null && atto.getEsito()!=null && tmp.getEsito().equalsIgnoreCase(atto.getEsito())){
					if (aOdg==null || (tmp.getId().longValue() > aOdg.getId().longValue())) {
						aOdg = tmp;
					}
				}
			}
		}
		
		if (aOdg != null && aOdg.getOrdineGiorno()!=null && aOdg.getOrdineGiorno().getSedutaGiunta()!=null) {
			SedutaGiunta seduta = aOdg.getOrdineGiorno().getSedutaGiunta();
			documentProperties.put(DmsComuneFirenzeConstants.NUMERO_SEDUTA, Long.parseLong(seduta.getNumero()));
		}
		
		// Pubblicazione
		MetadataCommonUtil.addPubblicazioneProperties(retVal, atto);
		// come da Excel condiviso, il flag pubblicazione presente solo nelle delibere
		documentProperties.put(DmsComuneFirenzeConstants.FLAG_PUBBLICAZIONE, Boolean.TRUE);
		
		// Conservazione
		MetadataCommonUtil.addConservazioneProperties(retVal, atto);
		
		// Associazioni (Allegati)
		MetadataCommonUtil.addAttachments(retVal, atto);
		
		retVal.setDocumentProperties(documentProperties);
		return retVal;
	}
}
