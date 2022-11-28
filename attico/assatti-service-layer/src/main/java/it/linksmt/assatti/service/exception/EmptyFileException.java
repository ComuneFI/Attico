package it.linksmt.assatti.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class EmptyFileException extends GestattiCatchedException {
	
	private final Logger log = LoggerFactory
			.getLogger(DmsEmptyFileException.class);
	
	private static final long serialVersionUID = -6555026010167334365L;
	
	public EmptyFileException(String message) {
        super(message);
        log.error(message);
    }
	
	public EmptyFileException() {
        super("Il file risulta essere vuoto o non valido.");
        log.error("Il file risulta essere vuoto o non valido.");
    }
	
}
