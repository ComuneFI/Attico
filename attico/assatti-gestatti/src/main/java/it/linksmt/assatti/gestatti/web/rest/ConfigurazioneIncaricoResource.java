package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.itextpdf.text.DocumentException;

import it.linksmt.assatti.bpm.service.RegistrazioneAvanzamentoService;
import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.service.ConfigurazioneIncaricoService;
import it.linksmt.assatti.service.ParereConsQuartRevService;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoAooDto;
import it.linksmt.assatti.service.dto.ConfigurazioneIncaricoDto;
import it.linksmt.assatti.service.exception.GestattiCatchedException;

/**
 * REST controller for managing ConfigurazioneIncaricoResource.
 */
@RestController
@RequestMapping("/api/configurazioneincarico")
public class ConfigurazioneIncaricoResource {

	private final Logger log = LoggerFactory.getLogger(ConfigurazioneIncaricoResource.class);

	@Inject
	private ConfigurazioneIncaricoService configurazioneIncaricoService;
	
	@Inject
	private ParereConsQuartRevService parereConsQuartRevService;
	

	/**
	 * POST /save -> Create a new configurazioneIncarico.
	 * 
	 * @throws DocumentException
	 * @throws IOException
	 */
	@RequestMapping(value = "/saveCommissioneConsiliare", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> saveCommissioneConsiliare(
			@RequestBody ConfigurazioneIncaricoDto configurazioneIncaricoDto
			) throws GestattiCatchedException {
		try {
			log.debug("REST request to save configurazioneIncaricoDto : {}", configurazioneIncaricoDto);
			
			configurazioneIncaricoDto = parereConsQuartRevService.save(configurazioneIncaricoDto);
			log.info("configurazioneIncaricoDto Salvato. Id: " + configurazioneIncaricoDto.getId());

			Gson gson = new Gson();
			String json = gson.toJson(configurazioneIncaricoDto);

			return new ResponseEntity<>(json, HttpStatus.CREATED);
		} catch (Exception e) {
			throw new GestattiCatchedException(e, "Errore salvataggio configurazioneIncarico - " + e.getMessage());
		}
	}
	
	
	@RequestMapping(value = "/insertParere",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> insertParereNonEspresso(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestBody Parere parere) throws GestattiCatchedException {
	
		parere = parereConsQuartRevService.saveParereNonEspresso(parere, profiloId);
		
		JsonObject json = new JsonObject();
		json.addProperty("parereId", parere.getId());
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/saveParere",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Parere> saveParere(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestBody Parere parere) throws GestattiCatchedException {
	
		parere = parereConsQuartRevService.saveParereQuartRevCommCons(parere, profiloId);

		if(parere.getAoo()!=null) {
			parere.setAoo(new Aoo(parere.getAoo().getId(), parere.getAoo().getDescrizione(), parere.getAoo().getCodice(), null));
		}
		if(parere.getProfilo()!=null) {
			parere.setProfilo(DomainUtil.minimalProfilo(parere.getProfilo()));
		}
		if(parere.getProfiloRelatore()!=null) {
			parere.setProfiloRelatore(DomainUtil.minimalProfilo(parere.getProfiloRelatore()));
		}
		if(parere.getAtto()!=null) {
			parere.setAtto(DomainUtil.minimalAtto(parere.getAtto()));
		}
		
		if (parere.getDocumentiPdf() != null) {
			for (DocumentoPdf docParere : parere.getDocumentiPdf()) {
				docParere.setAttoId(null);
				docParere.setParereId(null);
				if (docParere.getFile() != null) {
					docParere.getFile().getNomeFile();
				}
			}
		}

		if (parere.getAllegati() != null) {
			for (DocumentoInformatico allegato : parere.getAllegati()) {
				allegato.setAtto(null);
				allegato.setParere(null);
				if (allegato.getFile() != null) {
					allegato.getFile().getNomeFile();
				}
			}
		}
        return new ResponseEntity<Parere>(parere, HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value = "/aggiornaScadenza",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> aggiornaScadenza(
    		@RequestHeader(value="profiloId" ,required=true) final Long profiloId,
    		@RequestBody ConfigurazioneIncaricoAooDto configurazioneIncaricoAooDto) throws GestattiCatchedException {
	
		long idConfigurzioneIncarico = parereConsQuartRevService.aggiornaScadenza(
				configurazioneIncaricoAooDto.getIdConfigurazioneIncarico().longValue(),
				configurazioneIncaricoAooDto.getIdAoo().longValue(),
				configurazioneIncaricoAooDto.getGiorniScadenza(),
				configurazioneIncaricoAooDto.getDataManuale());
		
		JsonObject json = new JsonObject();
		json.addProperty("configurazioneIncaricoId", idConfigurzioneIncarico);
        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
	}

	/**
	 * GET /findAll -> get all the configurazioneIncarico.
	 */
	@RequestMapping(value = "/findAll", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> findAll() throws GestattiCatchedException {
		try {
			List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto = configurazioneIncaricoService.findAll();
			
			Gson gson = new Gson();
			String json = gson.toJson(listConfigurazioneIncaricoDto);

			return new ResponseEntity<>(json, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

	/**
	 * GET /findByAtto -> get all the configurazioneIncarico by attoId.
	 */
	@RequestMapping(value = "/findByAtto/{idAtto}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> findByAtto(
			@PathVariable("idAtto") final Long idAtto
			) throws GestattiCatchedException {
		try {
			List<ConfigurazioneIncaricoDto> listConfigurazioneIncaricoDto = configurazioneIncaricoService.findByAtto(idAtto);
			
			Gson gson = new Gson();
			String json = gson.toJson(listConfigurazioneIncaricoDto);

			return new ResponseEntity<>(json, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

	/**
	 * GET /get/:id -> get the "id" ConfigurazioneIncarico.
	 */
	@RequestMapping(value = "/get/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<ConfigurazioneIncaricoDto> get(
			@PathVariable("id") final Long idConfigurazioneIncaricoDto,
			final HttpServletResponse response
			) throws GestattiCatchedException {
		try {
			log.debug("REST request to get ConfigurazioneIncarico : {}", idConfigurazioneIncaricoDto);
			ConfigurazioneIncaricoDto configurazioneIncaricoDto = configurazioneIncaricoService.get(idConfigurazioneIncaricoDto);
			if (configurazioneIncaricoDto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}

			return new ResponseEntity<>(configurazioneIncaricoDto, HttpStatus.OK);
		} catch (Exception e) {
			throw new GestattiCatchedException(e);
		}
	}

}
