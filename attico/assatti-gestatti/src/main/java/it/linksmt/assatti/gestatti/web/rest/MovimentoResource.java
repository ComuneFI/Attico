package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;

import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.service.DatiContabiliService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing Movimento.
 */
@RestController
@RequestMapping("/api")
public class MovimentoResource {

	private final Logger log = LoggerFactory.getLogger(MovimentoResource.class);

	@Autowired
	private DatiContabiliService liquidazioneService;

	/**
	 * GET /elencoLiquidazione -> get liquidazione list.
	 */
	@RequestMapping(value = "/movimentos/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<String> elencoMovimenti(@PathVariable final Long id) throws GestattiCatchedException {
		try {
			List<MovimentoContabileDto> listLiquidazioneDto = liquidazioneService.elencoMovimento(id);
			if (listLiquidazioneDto == null) {
				return new ResponseEntity<>("[]", HttpStatus.OK);
			}
			
			Gson gson = new Gson();
			String json = gson.toJson(listLiquidazioneDto);

			return new ResponseEntity<>(json, HttpStatus.OK);
		}
		catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

}
