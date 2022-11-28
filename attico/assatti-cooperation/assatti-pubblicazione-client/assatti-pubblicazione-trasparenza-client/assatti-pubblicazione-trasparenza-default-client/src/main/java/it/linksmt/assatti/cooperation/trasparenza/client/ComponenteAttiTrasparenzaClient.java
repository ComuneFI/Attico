/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.cooperation.trasparenza.client;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriUtils;

import it.linksmt.assatti.cooperation.dto.AllegatoDto;
import it.linksmt.assatti.cooperation.trasparenza.dto.AttoDto;
import it.linksmt.assatti.cooperation.trasparenza.dto.DefaultResponse;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

/**
 * @author Gianluca Pindinelli
 *
 */
@Component
public class ComponenteAttiTrasparenzaClient {

	private static final String SCHEDULER_JOB_TRASPARENZA_URI = SchedulerProps.getProperty("scheduler.job.pubblicazione.trasparenza.uri");

	private final String componenteAttiTrasparenzaUser = SchedulerProps.getProperty("scheduler.job.pubblicazione.trasparenza.user");
	private final String componenteAttiTrasparenzaPassword = SchedulerProps.getProperty("scheduler.job.pubblicazione.trasparenza.password");
	
	private final String sharedFolder = SchedulerProps.getProperty("scheduler.job.pubblicazione.trasparenza.sharedFolder");

	/**
	 *
	 * @param attoDto
	 * @return
	 * @throws RestClientException
	 */
	public DefaultResponse sendAtto(AttoDto attoDto) throws RestClientException {
		
		// I file non vengono inviati al web service, ma vengono
		// memorizzati su una folder configurabile del filesystem
		// la quale deve essere condivisa con il componente trasparenza
		List<AllegatoDto> allegati = attoDto.getAllegati();
		List<String> filenameDaTenere = new ArrayList<String>();
		if (allegati != null) {
			
			for (AllegatoDto allegatoDto : allegati) {
				if(!allegatoDto.isRiservato() && (attoDto.getRiservato()==null || attoDto.getRiservato().equals(false))) {
					byte[] fileContent = allegatoDto.getContenuto();
					
					String nomeFile = allegatoDto.getNome();
					String fileNamePerUrl = allegatoDto.getNome();
					try {
						fileNamePerUrl = UriUtils.encodePath(nomeFile, "UTF-8");
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					String pathRelativa = "/" + String.valueOf(attoDto.getAttoId()) + "/" + nomeFile;
					String link = "/" + String.valueOf(attoDto.getAttoId()) + "/" + fileNamePerUrl;
					if(StringUtil.isNull(sharedFolder)) {
						throw new IllegalArgumentException("Occorre configurare la folder condivisa per la scrittura file in trasparenza.");
					}
					
					File destination = new File(sharedFolder + "/" + String.valueOf(attoDto.getAttoId()));
					if (!destination.exists()) {
						destination.mkdir();
					}
					
					try {
						Files.write(new File(sharedFolder+pathRelativa).toPath(), fileContent);
					}
					catch(Exception ex) {
						throw new RuntimeException("Errore durante la scrittura del file per il componente trasparenza.", ex);
					}
					
					allegatoDto.setContenuto(null);
					allegatoDto.setLink(link);
					filenameDaTenere.add(nomeFile);
				}else {
					allegatoDto.setContenuto(null);
					allegatoDto.setLink(null);
				}
			}
		}
		File attoFolder = new File(sharedFolder + "/" + String.valueOf(attoDto.getAttoId()));
		if(attoFolder.exists() && attoFolder.isDirectory()) {
			List<File> files = Arrays.asList(attoFolder.listFiles());
			for(File file : files) {
				if(!filenameDaTenere.contains(file.getName())) {
					file.delete();
				}
			}
		}
		
		RestTemplate restTemplate = new RestTemplate();

		String plainCreds = componenteAttiTrasparenzaUser + ":" + componenteAttiTrasparenzaPassword;
		byte[] plainCredsBytes = plainCreds.getBytes();
		byte[] base64CredsBytes = Base64.encode(plainCredsBytes);
		String base64Creds = new String(base64CredsBytes);

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.add("Authorization", "Basic " + base64Creds);

		HttpEntity<AttoDto> entity = new HttpEntity<AttoDto>(attoDto, headers);
		ResponseEntity<DefaultResponse> response = restTemplate.exchange(SCHEDULER_JOB_TRASPARENZA_URI, HttpMethod.PUT, entity, DefaultResponse.class);

		DefaultResponse body = response.getBody();

		return body;
	}

}
