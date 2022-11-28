package it.linksmt.assatti.service.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class MovimentiPresentiException extends GestattiCatchedException {
	
	private final Logger log = LoggerFactory
			.getLogger(DmsEmptyFileException.class);
	
	private static final long serialVersionUID = -6555026010167334365L;
	
	public MovimentiPresentiException(String message) {
        super(message);
        log.error(message);
    }
	
	public MovimentiPresentiException() {
        super("Attenzione. Risultano presenti movimenti nel sistema contabile.");
    }
	
}
