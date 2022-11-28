package it.linksmt.assatti.gestatti.camunda;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProcessInstanceMigration {

	private static String ENGINE_REST_URL = "http://attico-test.comune.intranet:7080/engine-rest/";
	
	private static String sourceProcessDefinitionId = "AttoDL:36:baeeea01-0d47-11ea-8876-005056a7f78b";
	private static String targetProcessDefinitionId = "AttoDL:37:d67506c2-1d89-11ea-92f1-005056a7f78b";
	
	private static boolean updateEventTriggers = true;
	private static boolean skipCustomListeners = true;
	
	private static String listProcessInstances = "[\n" + 
      				"    \"da6944ef-1d81-11ea-99e5-005056a7f78b\",\n" + 
    				"    \"0cca4fc5-1810-11ea-9efe-005056a7f78b\",\n" + 
    				"    \"b118dcd1-17fe-11ea-9efe-005056a7f78b\"\n" + 
    				"  ]";
	
	private static String executionPlan = 
			"{\"sourceProcessDefinitionId\":\"AttoDL:36:baeeea01-0d47-11ea-8876-005056a7f78b\",\"targetProcessDefinitionId\":\"AttoDL:37:d67506c2-1d89-11ea-92f1-005056a7f78b\",\"instructions\":[{\"sourceActivityIds\":[\"SubProcess_0xt1xwv\"],\"targetActivityIds\":[\"SubProcess_0xt1xwv\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"verificaContabile\"],\"targetActivityIds\":[\"verificaContabile\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"UserTask_1ku1nj0\"],\"targetActivityIds\":[\"UserTask_1ku1nj0\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"Task_0dgxpxj\"],\"targetActivityIds\":[\"Task_0dgxpxj\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"istruttoriaDD\"],\"targetActivityIds\":[\"istruttoriaDD\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"UserTask_093a3if\"],\"targetActivityIds\":[\"UserTask_093a3if\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"UserTask_0ms9zli\"],\"targetActivityIds\":[\"UserTask_0ms9zli\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"UserTask_1xcp1a6\"],\"targetActivityIds\":[\"UserTask_1xcp1a6\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"UserTask_1us5yzv\"],\"targetActivityIds\":[\"UserTask_1us5yzv\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"BoundaryEvent_1jy5ea3\"],\"targetActivityIds\":[\"BoundaryEvent_1jy5ea3\"],\"updateEventTrigger\":true},{\"sourceActivityIds\":[\"UserTask_0bp4p2q\"],\"targetActivityIds\":[\"UserTask_0bp4p2q\"],\"updateEventTrigger\":false},{\"sourceActivityIds\":[\"SubProcess_0xt1xwv#multiInstanceBody\"],\"targetActivityIds\":[\"SubProcess_0xt1xwv#multiInstanceBody\"],\"updateEventTrigger\":false}]}";
	
	
	public static void main(String[] args) {
		
		try {
			generateMigration();
			// validateMigration();
			// executeMigration();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private static void generateMigration() {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String migrationReq = "{" + 
				"  \"sourceProcessDefinitionId\": \"" + sourceProcessDefinitionId + "\", " + 
				"  \"targetProcessDefinitionId\": \"" + targetProcessDefinitionId + "\", " + 
				"  \"updateEventTriggers\": " + String.valueOf(updateEventTriggers) + " " + 
				"}";
		
		HttpEntity entity = new HttpEntity(migrationReq, headers);
		ResponseEntity<String> response = restTemplate.exchange(
				ENGINE_REST_URL + "migration/generate", HttpMethod.POST, entity, String.class);
		
		System.out.println("GENERATE MIGRATION RESPONSE:\n" + response.getBody());
	}
	
	private static void validateMigration() {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		HttpEntity entity = new HttpEntity(executionPlan, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				ENGINE_REST_URL + "migration/validate", HttpMethod.POST, entity, String.class);
		
		System.out.println("VALIDATE MIGRATION RESPONSE:\n" + response.getBody());
	}
	
	private static void executeMigration() {
		RestTemplate restTemplate = new RestTemplate();
		
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		String migrationReq = "{\n" + 
				"  \"migrationPlan\": " + executionPlan + ",\n" + 
				"  \"processInstanceIds\": " + listProcessInstances + ",\n" + 
				"  \"skipCustomListeners\": " + String.valueOf(skipCustomListeners) + "\n" + 
				"}";
		
		HttpEntity entity = new HttpEntity(migrationReq, headers);
		
		ResponseEntity<String> response = restTemplate.exchange(
				ENGINE_REST_URL + "migration/execute", HttpMethod.POST, entity, String.class);
		
		System.out.println("EXECUTE MIGRATION RESPONSE CODE:\n" + response.getStatusCode());
	}
}
