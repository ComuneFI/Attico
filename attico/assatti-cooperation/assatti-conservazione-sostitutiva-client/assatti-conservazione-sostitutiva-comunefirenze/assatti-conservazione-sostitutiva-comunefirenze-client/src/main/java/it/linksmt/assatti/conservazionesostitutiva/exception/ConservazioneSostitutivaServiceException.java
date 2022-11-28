/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.conservazionesostitutiva.exception;

/**
 * @author Gianluca Pindinelli
 *
 */
public class ConservazioneSostitutivaServiceException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = -8361164013066823730L;

	public ConservazioneSostitutivaServiceException(Throwable t) {
		super(t);
	}

	public ConservazioneSostitutivaServiceException(String message) {
		super(message);
	}

	public ConservazioneSostitutivaServiceException(String message, Throwable t) {
		super(message, t);
	}
}
