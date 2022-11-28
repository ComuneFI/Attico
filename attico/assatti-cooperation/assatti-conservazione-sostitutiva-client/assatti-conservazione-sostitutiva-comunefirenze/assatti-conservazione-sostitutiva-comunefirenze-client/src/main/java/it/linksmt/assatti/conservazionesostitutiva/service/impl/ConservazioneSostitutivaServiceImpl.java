/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.conservazionesostitutiva.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.cmis.client.service.CmisService;
import it.linksmt.assatti.cmis.client.service.impl.CmisServiceImpl;
import it.linksmt.assatti.conservazionesostitutiva.exception.ConservazioneSostitutivaServiceException;
import it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService;

/**
 * @author Gianluca Pindinelli
 *
 */
@Service
public class ConservazioneSostitutivaServiceImpl implements ConservazioneSostitutivaService {
	
	// Prefissi
	public static final String PREFIX_SEPARATOR = ":";

	/*
	 * (non-Javadoc)
	 *
	 * @see it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService#
	 * daInviareInConservazione(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public List<String> daInviareInConservazione(String folderId, String attributoInvioConservazione, String valoreAttributoInvioConservazione, String aspectConservazione)
			throws ConservazioneSostitutivaServiceException {

		List<String> results = null;
		
		String attrPrefix = attributoInvioConservazione.split(PREFIX_SEPARATOR)[0];
		String attrName   = attributoInvioConservazione.split(PREFIX_SEPARATOR)[1];

		List<CmisMetadataDTO> searchMetadata = Arrays.asList(new CmisMetadataDTO(
				attrPrefix, attrName, valoreAttributoInvioConservazione, aspectConservazione));

		try {
			CmisService cmisService = new CmisServiceImpl();
			results = cmisService.findByFolderMetadata(folderId, searchMetadata, true);
		}
		catch (CmisServiceException e) {
			throw new ConservazioneSostitutivaServiceException("Non è stato possibile caricare la lista delle unità documentali da inviare in conservazione :: errore riscontrato : " + e.getMessage(),
					e);
		}

		return results;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService#
	 * getUnitaDocumentaleConservazione(java.lang.String)
	 */
	@Override
	public CmisDocumentDTO getUnitaDocumentaleConservazione(
			String idUnitaDocumentale, String attachmentAssociationType) throws ConservazioneSostitutivaServiceException {

		CmisDocumentDTO document = null;

		try {
			CmisService cmisService = new CmisServiceImpl();
			document = cmisService.getDocument(idUnitaDocumentale, true, true, attachmentAssociationType);
		}
		catch (CmisServiceException e) {
			throw new ConservazioneSostitutivaServiceException("Non è stato possibile caricare l'unità documentale con ID " 
					+ idUnitaDocumentale + " :: errore riscontrato : " + e.getMessage(), e);
		}

		return document;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService#
	 * aggiornaStatoConservazione(java.lang.String, java.lang.String, java.lang.String,
	 * java.lang.String, java.util.Date)
	 */
	@Override
	public void aggiornaStatoConservazione(String idUnitaDocumentale, 
			String nomeAttributoInvioConservazione, 
			String valoreAttributoInvioConservazione, 
			String nomeAttributoDataConservazione,
			Date valoreAttributoDataConservazione,
			String nomeAttributoNoteConservazione, 
			String valoreAttributoNoteConservazione) throws ConservazioneSostitutivaServiceException {

		try {
			CmisService cmisService = new CmisServiceImpl();
			Map<String, Object> updateProperties = new HashMap<>();
			
			if (nomeAttributoInvioConservazione != null && !nomeAttributoInvioConservazione.isEmpty()) {
				updateProperties.put(nomeAttributoInvioConservazione, valoreAttributoInvioConservazione);
			}
			if (nomeAttributoDataConservazione != null && !nomeAttributoDataConservazione.isEmpty()) {
				updateProperties.put(nomeAttributoDataConservazione, valoreAttributoDataConservazione);
			}
			if (nomeAttributoNoteConservazione != null && !nomeAttributoNoteConservazione.isEmpty()) {
				updateProperties.put(nomeAttributoNoteConservazione, valoreAttributoNoteConservazione);
			}
			cmisService.updateDocumentMetadata(idUnitaDocumentale, updateProperties);
		}
		catch (CmisServiceException e) {
			throw new ConservazioneSostitutivaServiceException(
					"Non è stato possibile aggiornare im metadati per l'unità documentale con ID " + idUnitaDocumentale + " :: errore riscontrato : " + e.getMessage(), e);
		}
	}
}
