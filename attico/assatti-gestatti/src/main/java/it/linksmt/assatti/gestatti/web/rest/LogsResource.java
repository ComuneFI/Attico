package it.linksmt.assatti.gestatti.web.rest;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.LoggerContext;
import it.linksmt.assatti.gestatti.web.rest.dto.LoggerDTO;

import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.security.AuthoritiesConstants;
import com.codahale.metrics.annotation.Timed;

/**
 * Controller for view and managing Log Level at runtime.
 */
@RestController
@RequestMapping("/api")
public class LogsResource {

    @RequestMapping(value = "/logs",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public List<LoggerDTO> getList() throws GestattiCatchedException{
    	try{
	    	LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	        List<LoggerDTO> loggers = new ArrayList<>();
	        for (ch.qos.logback.classic.Logger logger : context.getLoggerList()) {
	            loggers.add(new LoggerDTO(logger));
	        }
	        return loggers;
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    @RequestMapping(value = "/logs",
            method = RequestMethod.PUT)
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Secured(AuthoritiesConstants.ADMIN)
    @Timed
    public void changeLevel(@RequestBody LoggerDTO jsonLogger) throws GestattiCatchedException {
    	try{
	    	LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
	        context.getLogger(jsonLogger.getName()).setLevel(Level.valueOf(jsonLogger.getLevel()));
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
