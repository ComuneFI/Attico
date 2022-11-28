/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.conservazionesostitutiva.service;

import java.util.Date;
import java.util.List;

import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.conservazionesostitutiva.exception.ConservazioneSostitutivaServiceException;

/**
 * Service per l'integrazione del sistema di conservazione sostitutiva con i servizi necessari al
 * caricamento delle unità documentali presenti nel sistema.
 *
 * @author Gianluca Pindinelli
 *
 */
public interface ConservazioneSostitutivaService {

	/**
	 * Estrapola dal sistema documentale Alfresco la lista di unità documentali da inviare in
	 * conservazione.
	 *
	 * @param folderId
	 * @param nomeAttributoInvioConservazione
	 * @param valoreAttributoInvioConservazione
	 * @return
	 */
	List<String> daInviareInConservazione(
			String folderId, 
			String attributoInvioConservazione, 
			String valoreAttributoInvioConservazione, 
			String aspectConservazione) 
					throws ConservazioneSostitutivaServiceException;

	/**
	 * Estrapola dal sistema documentale Alfresco la singola unità documentale da inviare in
	 * conservazione.
	 *
	 * @param idDocumento
	 * @return
	 * @throws ConservazioneSostitutivaServiceException
	 */
	CmisDocumentDTO getUnitaDocumentaleConservazione(
			String idUnitaDocumentale, String attachmentAssociationType) throws ConservazioneSostitutivaServiceException;

	/**
	 * Aggiorna lo stato relativo alla conservazione di una data unità documentale sul sistema
	 * Alfresco.
	 *
	 * @param idUnitaDocumentale
	 * @param nomeAttributoInvioConservazione
	 * @param valoreAttributoInvioConservazione
	 * @param nomeAttributoDataConservazione
	 * @param valoreAttributoDataConservazione
	 * @throws ConservazioneSostitutivaServiceException
	 */
	void aggiornaStatoConservazione(String idUnitaDocumentale, 
			String nomeAttributoInvioConservazione, String valoreAttributoInvioConservazione, 
			String nomeAttributoDataConservazione, Date valoreAttributoDataConservazione,
			String nomeAttributoNoteConservazione, String valoreAttributoNoteConservazione) throws ConservazioneSostitutivaServiceException;

}
