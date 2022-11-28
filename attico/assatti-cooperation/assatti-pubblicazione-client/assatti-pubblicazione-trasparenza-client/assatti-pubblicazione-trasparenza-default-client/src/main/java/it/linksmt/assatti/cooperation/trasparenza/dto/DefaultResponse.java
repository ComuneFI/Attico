/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.trasparenza.dto;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

/**
 * @author Gianluca Pindinelli
 *
 */
@JsonInclude(Include.NON_NULL)
public class DefaultResponse implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private ResponseResult esito;
	private String error;

	/**
	 * @return the esito
	 */
	public ResponseResult getEsito() {
		return esito;
	}

	/**
	 * @param esito the esito to set
	 */
	public void setEsito(ResponseResult esito) {
		this.esito = esito;
	}

	/**
	 * @return the error
	 */
	public String getError() {
		return error;
	}

	/**
	 * @param error the error to set
	 */
	public void setError(String error) {
		this.error = error;
	}

}
