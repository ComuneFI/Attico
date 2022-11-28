package it.linksmt.assatti.gestatti.web.rest;

import java.nio.charset.Charset;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.camunda.bpm.engine.RuntimeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller of utility date.
 */
@RestController
@RequestMapping("/api/manutenzione")
public class ManutenzioneResource {
	
	@Autowired
	private RuntimeService runtimeService;
	
	private final Logger log = LoggerFactory.getLogger(ManutenzioneResource.class);

	private ResponseEntity<String> checkBasicAuth(HttpServletRequest request){
		HttpHeaders headers_401 = new HttpHeaders();
        headers_401.add("WWW-Authenticate", "Basic realm=\"Richiesta Login\"");
        ResponseEntity<String> fail = new ResponseEntity<String>("UNAUTHORIZED", headers_401, HttpStatus.UNAUTHORIZED);
		try {
	        String authString = request.getHeader("Authorization");
			if(authString == null) {
				authString = request.getHeader("authorization");
			}
			if(authString == null) {
				return fail;
			}
			String[] authParts = authString.split("\\s+");
	        String authInfo = authParts[1];
	        byte[] bytes = Base64.decodeBase64(authInfo.getBytes());
	        String decodedAuth = new String(bytes, Charset.forName("UTF-8"));
	        String user = decodedAuth.split(":")[0];
	        String pwd = decodedAuth.split(":")[1];
			if(!user.equalsIgnoreCase(WebApplicationProps.getProperty("manutenzione_user", "gestattiadmin")) || !pwd.equalsIgnoreCase(WebApplicationProps.getProperty("manutenzione_pwd", "gestattipwd"))) {
				return fail;
			}else {
				return null;
			}
		}catch(Exception e) {
			return fail;
		}
	}
	
    @RequestMapping(value = "/rollbackActivityProcessInstance/{processInstanceId}/{taskIdDestinazione}/{taskIdAttuale}/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> rollbackActivityProcessInstance(final HttpServletRequest request, @PathVariable final String processInstanceId, @PathVariable final String taskIdDestinazione,
    		@PathVariable final String taskIdAttuale) throws GestattiCatchedException {
    	try{
    		ResponseEntity<String> fail = this.checkBasicAuth(request);
			if(fail == null) {
	    		log.info("START MODIFICA PROCESSO...\n\n\n");
				runtimeService.createProcessInstanceModification(processInstanceId)
				  .startBeforeActivity(taskIdDestinazione)
				  .cancelAllForActivity(taskIdAttuale)
				  .execute();
				log.info("PROCESSO MODIFICATO CON SUCCESSO!!!");
	    		return new ResponseEntity<String>("ok", HttpStatus.OK);
			}else {
				return fail;
			}
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/rollbackActivityProcessInstance/{processInstanceId}/{taskIdDestinazione}/{taskIdAttuale}/{variableName}/{variableValue}/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> rollbackActivityProcessInstance(final HttpServletRequest request, @PathVariable final String processInstanceId, @PathVariable final String taskIdDestinazione,
    		@PathVariable final String taskIdAttuale, @PathVariable final String variableName, @PathVariable final String variableValue) throws GestattiCatchedException {
    	try{
    		ResponseEntity<String> fail = this.checkBasicAuth(request);
			if(fail == null) {
	    		log.info("START MODIFICA PROCESSO...\n\n\n");
	    		log.info("variableName::"+variableName + "::variableValue::" + variableValue);
				runtimeService.createProcessInstanceModification(processInstanceId)
				  .startBeforeActivity(taskIdDestinazione)
				  .setVariable(variableName, variableValue)
				  .cancelAllForActivity(taskIdAttuale)
				  .execute();
				log.info("PROCESSO MODIFICATO CON SUCCESSO!!!");
	    		return new ResponseEntity<String>("ok", HttpStatus.OK);
			}else {
				return fail;
			}
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/test/rollbackActivityProcessInstance/{processInstanceId}/{taskIdDestinazione}/{taskIdAttuale}/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> testRollbackActivityProcessInstance(final HttpServletRequest request, @PathVariable final String processInstanceId, @PathVariable final String taskIdDestinazione,
    		@PathVariable final String taskIdAttuale) throws GestattiCatchedException {
    	try{
    		ResponseEntity<String> fail = this.checkBasicAuth(request);
			if(fail == null) {
	    		log.info("processInstanceId " + processInstanceId);
	    		log.info("taskIdDestinazione " + taskIdDestinazione);
	    		log.info("taskIdAttuale " + taskIdAttuale);
	    		return new ResponseEntity<String>("ok", HttpStatus.OK);
			}else {
				return fail;
			}
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * Example:
     * http://localattico/api/manutenzione/test/rollbackActivityProcessInstance/f9a83166-1bb6-11ec-808f-080027c310ad/UserTask_1ku1nj0/UserTask_1us5yzv/GROUP_PARERE_ISTRUTTORIO_RESPONSABILE/prova/
     */
    
    @RequestMapping(value = "/test/rollbackActivityProcessInstance/{processInstanceId}/{taskIdDestinazione}/{taskIdAttuale}/{variableName}/{variableValue}/",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> testRollbackActivityProcessInstance(final HttpServletRequest request, @PathVariable final String processInstanceId, @PathVariable final String taskIdDestinazione,
    		@PathVariable final String taskIdAttuale, @PathVariable final String variableName, @PathVariable final String variableValue) throws GestattiCatchedException {
    	try{
    		ResponseEntity<String> fail = this.checkBasicAuth(request);
			if(fail == null) {
	    		log.info("processInstanceId " + processInstanceId);
	    		log.info("taskIdDestinazione " + taskIdDestinazione);
	    		log.info("taskIdAttuale " + taskIdAttuale);
	    		log.info("variableName " + variableName);
	    		log.info("variableValue " + variableValue);
	    		return new ResponseEntity<String>("ok", HttpStatus.OK);
			}else {
				return fail;
			}
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
