package it.linksmt.assatti.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DmsEmptyFileException extends DmsException {
	
	private final Logger log = LoggerFactory
			.getLogger(DmsEmptyFileException.class);
	
	private static final long serialVersionUID = -6555026010167334365L;
	
	public DmsEmptyFileException(String message) {
		log.error(message);
    }
	
	public DmsEmptyFileException() {
        log.error("Il file risulta essere vuoto o non valido.");
    }
	
	public DmsEmptyFileException(String documentId, String documentName) {
		log.error("Il file risulta essere vuoto o non valido.<br/>Document Id " + documentId + " DocumentName: " + documentName);
	}
	
}
