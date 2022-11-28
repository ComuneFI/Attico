package it.linksmt.assatti.service.exception;

import it.linksmt.assatti.service.exception.ServiceException;

public class LiquidazioniEmptyException extends ServiceException{

	private static final long serialVersionUID = 1L;

	public LiquidazioniEmptyException(String message) {
		super(message);
	}
}
