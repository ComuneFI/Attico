/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.service;

import java.util.List;

import it.linksmt.assatti.cooperation.dto.AllegatoDto;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * @author Gianluca Pindinelli
 *
 */
public interface AllegatoService {

	/**
	 * Ritorna il documento principale di un atto.
	 * 
	 * @param atto
	 * @return
	 * @throws ServiceException
	 */
	AllegatoDto getDocumenoPrincipaleAsAllegatoDto(Atto atto, boolean completo) throws ServiceException;

	/**
	 * Ritorna la lista degli allegati di un atto.
	 * 
	 * @param atto
	 * @return
	 * @throws ServiceException
	 */
	List<AllegatoDto> getAllegati(Atto atto, boolean includiNonPubblicabili) throws ServiceException;

}
