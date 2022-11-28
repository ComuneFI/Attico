package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.itextpdf.text.DocumentException;

import it.linksmt.assatti.service.ConfigurazioneTaskService;
import it.linksmt.assatti.service.dto.ConfigurazioneTaskDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing ConfigurazioneTask.
 */
@RestController
@RequestMapping("/api/configurazionetask")
public class ConfigurazioneTaskResource {

	private final Logger log = LoggerFactory.getLogger(ConfigurazioneTaskResource.class);

	@Inject
	private ConfigurazioneTaskService configurazioneTaskService;

	/**
	 * POST /save -> Create a new configurazioneTask.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> save(
			@RequestBody ConfigurazioneTaskDto configurazioneTaskDto
			) throws GestattiCatchedException {
		try {
			log.debug("REST request to save configurazioneTaskDto : {}", configurazioneTaskDto);
			if(configurazioneTaskDto.isProponente() || configurazioneTaskDto.isUfficioCorrente()) {
				configurazioneTaskDto.setListAoo(new ArrayList<Long>());
			}
			configurazioneTaskDto = configurazioneTaskService.save(configurazioneTaskDto);
			log.info("configurazioneTaskDto Salvato. Id: " + configurazioneTaskDto.getIdConfigurazioneTask());

			Gson gson = new Gson();
			String json = gson.toJson(configurazioneTaskDto);

			return new ResponseEntity<>(json, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new GestattiCatchedException(e, "Errore salvataggio configurazioneTask - " + e.getMessage());
		}
	}

	/**
	 * GET /findAll -> get all the configurazioneTask.
	 */
	@RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> findAll() throws GestattiCatchedException {
		try {
			List<ConfigurazioneTaskDto> listConfigurazioneTaskDto = configurazioneTaskService.findAll();
			
			Gson gson = new Gson();
			String json = gson.toJson(listConfigurazioneTaskDto);

			return new ResponseEntity<>(json, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

	/**
	 * GET /get/:id -> get the "id" ConfigurazioneTask.
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ConfigurazioneTaskDto> get(
			@PathVariable("id") final Long idConfigurazioneTaskDto,
			final HttpServletResponse response
			) throws GestattiCatchedException {
		try {
			log.debug("REST request to get ConfigurazioneTask : {}", idConfigurazioneTaskDto);
			ConfigurazioneTaskDto configurazioneTaskDto = configurazioneTaskService.get(idConfigurazioneTaskDto);
			if (configurazioneTaskDto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(configurazioneTaskDto, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}
	
	/**
	 * GET /findByCodice/:id -> get the ConfigurazioneTask by Codice.
	 */
	@RequestMapping(value = "/findByCodice/{codice}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ConfigurazioneTaskDto> findByCodice(
			@PathVariable("codice") final String codiceConfigurazioneTaskDto,
			final HttpServletResponse response
			) throws GestattiCatchedException {
		try {
			log.debug("REST request to get ConfigurazioneTask by Codice: {}", codiceConfigurazioneTaskDto);
			ConfigurazioneTaskDto configurazioneTaskDto = configurazioneTaskService.findByCodice(codiceConfigurazioneTaskDto);
			if (configurazioneTaskDto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(configurazioneTaskDto, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

}
