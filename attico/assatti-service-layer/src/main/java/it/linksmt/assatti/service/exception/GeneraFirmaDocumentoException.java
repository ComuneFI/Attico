package it.linksmt.assatti.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GeneraFirmaDocumentoException extends RuntimeException{
	
	private final Logger log = LoggerFactory
			.getLogger(GeneraFirmaDocumentoException.class);
	
	private static final long serialVersionUID = 1L;
	
	public GeneraFirmaDocumentoException(String message) {
        super(message);
        log.error("Si è verificato un errore applicativo: " + message);
    }
	
}
