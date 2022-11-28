/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.service.trasparenza;

import it.linksmt.assatti.service.exception.ServiceException;

/**
 * Interfaccia Service per l'invio degli atti al sistema esterno per la trasparenza.
 *
 * @author Gianluca Pindinelli
 *
 */
public interface JobPubblicazioneTrasparenzaService {

	/**
	 * Pubblica gli atti nel sistema esterno per la trasparenza.
	 *
	 * @throws ServiceException
	 */
	public void sendAtti() throws ServiceException;

}
