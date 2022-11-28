package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.StoricoAttoDirigenziale;
import it.linksmt.assatti.datalayer.domain.StoricoAttoGiunta;
import it.linksmt.assatti.datalayer.domain.StoricoDocumento;
import it.linksmt.assatti.datalayer.domain.TipoFinanziamento;
import it.linksmt.assatti.datalayer.domain.dto.StoricoLavorazioniDto;
import it.linksmt.assatti.datalayer.domain.dto.StoricoSedutaDto;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.StoricoAttoService;
import it.linksmt.assatti.service.TipoFinanziamentoService;
import it.linksmt.assatti.service.dto.StoricoAttoCriteriaDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

/**
 * REST controller for managing StoricoAtto.
 */
@RestController
@RequestMapping("/api")
public class StoricoAttoResource {
	
	private final Logger log = LoggerFactory.getLogger(StoricoAttoResource.class);
	
	@Inject
	private StoricoAttoService attoService;
	
	@Inject
	private TipoFinanziamentoService tipoFinanziamentoService;
	
	/**
  	 * GET/attos/{id}/allegato/{idAllegato}-> download documento versione pdf
	 * @throws IOException
  	 */
  	@RequestMapping(value = "/storicoattos/{id}/documento/{idDocumento}", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumento(
  			@PathVariable final Long id,
  			@PathVariable final Long idDocumento,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug(String.format("REST request to get download documento [idAtto=%s, idDocumento=%s]...", id, idDocumento));
	
			StoricoDocumento file = attoService.download(idDocumento);
			if (file == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			return DownloadFileUtil.responseStream(file);
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	
  	}
	
	/**
	 * GET /attos/:id -> get the "id" atto.
	 */
	@RequestMapping(value = "/storicoattos/{id}/attoDirigenziale", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StoricoAttoDirigenziale> getAttoDirigenziale(@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to get Storico Atto Dirigenziale: {}", id);
			StoricoAttoDirigenziale atto = attoService.findOneAttoDirigenziale(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			return new ResponseEntity<>(atto, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /attos/:id -> get the "id" atto.
	 */
	@RequestMapping(value = "/storicoattos/{id}/attoGiunta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<StoricoAttoGiunta> getAttoGiunta(@PathVariable final Long id,
			final HttpServletResponse response) throws GestattiCatchedException{
		try{
			log.debug("REST request to get Storico Atto Giunta: {}", id);
			StoricoAttoGiunta atto = attoService.findOneAttoGiunta(id);
			if (atto == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			}
	
			return new ResponseEntity<>(atto, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/getAllAoo -> recupera le Aoo contenenti atti storici per tipologia e anno.
	 */
	@RequestMapping(value = "/storicoattos/getAllAoo", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<Aoo>> getAllAooByTipoAttoAndAnno(
			@RequestParam(value = "codTipoAtto", required = true) final String codTipoAtto,
			@RequestParam(value = "anno", required = true) final String anno,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to get all Aoo containing Storico Atto...");

			Page<Aoo> page = attoService.getAllAooByTipoAttoAndAnno(codTipoAtto, anno, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/getAllAoo", offset, limit);
			log.debug(String.format("getAllAoo :: chiamata al attoService.getAllAoo eseguita... recuperati %s aoo...",page.getContent().size()));

			return new ResponseEntity<List<Aoo>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/getAllSedute -> recupera le Sedute di Giunta contenenti atti storici per tipologia e anno.
	 */
	@RequestMapping(value = "/storicoattos/getAllSedute", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoSedutaDto>> getAllSedute(
			@RequestParam(value = "codTipiAtto", required = true) final String[] codTipiAtto,
			@RequestParam(value = "anno", required = true) final String anno,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to get all Sedute containing Storico Atto...");

			Page<StoricoSedutaDto> page = attoService.getAllSeduteByTipiAttoAndAnno(codTipiAtto, anno, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/getAllSedute", offset, limit);
			
			return new ResponseEntity<List<StoricoSedutaDto>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * POST /storicoattos/searchAttiDir_groupByAoo -> search on StoricoAttiDirigenziali.
	 */
	@RequestMapping(value = "/storicoattos/searchAttiDir_groupByAoo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoAttoDirigenziale>> searchAttiDir_groupByAoo(
			@Valid @RequestBody final StoricoAttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Storico Atti Dirigenziali : {}");
			
			Page<StoricoAttoDirigenziale> page = attoService.findAllAttoDirigenzialeByAoo(criteria, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/searchAttiDir_groupByAoo", offset, limit);
			log.debug("searchAttiDir_groupByAoo :: chiamata al attoService.findAllAttoDirigenzialeByAoo eseguita");
			
			return new ResponseEntity<List<StoricoAttoDirigenziale>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
			log.error(e.getMessage(), e);
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * POST /storicoattos/searchAttiDir_groupByAoo -> search on StoricoAttiDirigenziali.
	 */
	@RequestMapping(value = "/storicoattos/searchAttiGiunta_groupByAoo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoAttoGiunta>> searchAttiGiunta_groupByAoo(
			@Valid @RequestBody final StoricoAttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Storico Atti Giunta group by Aoo: {}");
			
			Page<StoricoAttoGiunta> page = attoService.findAllAttoGiuntaByAoo(criteria, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/searchAttiGiunta_groupByAoo", offset, limit);
			log.debug("searchAttiGiunta_groupByAoo :: chiamata al attoService.findAllAttoGiuntaByAoo eseguita");
			
			return new ResponseEntity<List<StoricoAttoGiunta>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
			log.error(e.getMessage(), e);
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * POST /storicoattos/searchAttiDir_groupByAoo -> search on StoricoAttiDirigenziali.
	 */
	@RequestMapping(value = "/storicoattos/searchAttiGiunta_groupBySeduta", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoAttoGiunta>> searchAttiGiunta_groupBySeduta(
			@Valid @RequestBody final StoricoAttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Storico Atti Giunta group by Seduta: {}");
			
			Page<StoricoAttoGiunta> page = attoService.findAllAttoGiuntaBySeduta(criteria, PaginationUtil.generatePageRequest(offset, limit));
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/searchAttiGiunta_groupBySeduta", offset, limit);
			log.debug("searchAttiGiunta_groupBySeduta :: chiamata al attoService.findAllAttoGiuntaBySeduta eseguita");
			
			return new ResponseEntity<List<StoricoAttoGiunta>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
			log.error(e.getMessage(), e);
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/getLavorazioniAttoGiunta -> get all lavorazioni di un dato atto di Giunta.
	 */
	@RequestMapping(value = "/storicoattos/getLavorazioniAttoGiunta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoLavorazioniDto>> getLavorazioniAttoGiunta(@RequestParam(value = "codiceCifra") final String codiceCifra)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getLavorazioniAttoGiunta : ");

			List<StoricoLavorazioniDto> content = attoService.findAllLavorazioniAttoGiunta(codiceCifra);

			return new ResponseEntity<List<StoricoLavorazioniDto>>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/getLavorazioniAttoGiunta -> get all lavorazioni di un dato atto di Giunta.
	 */
	@RequestMapping(value = "/storicoattos/getLavorazioniAttoDirigenziale", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoLavorazioniDto>> getLavorazioniAttoDirigenziale(@RequestParam(value = "codiceCifra") final String codiceCifra)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getLavorazioniAttoDirigenziale : ");

			List<StoricoLavorazioniDto> content = attoService.findAllLavorazioniAttoDirigenziale(codiceCifra);

			return new ResponseEntity<List<StoricoLavorazioniDto>>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * POST /storicoattos/searchAttiDirigenziali -> search on StoricoAttiDirigenziali.
	 */
	@RequestMapping(value = "/storicoattos/searchAttiDirigenziali", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoAttoDirigenziale>> getAllAttiDirigenziali(
			@Valid @RequestBody final StoricoAttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Storico Atti Dirigenziali : {}", criteria);

			Page<StoricoAttoDirigenziale> page = attoService.findAllAttoDirigenzialePaginato(
					PaginationUtil.generatePageRequest(
							offset, limit, 
							getSort(criteria.getTipoOrdinamento(), criteria.getOrdinamento(), "codiceCifra")), 
					criteria);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/searchAttiDirigenziali", offset, limit);

			return new ResponseEntity<List<StoricoAttoDirigenziale>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/searchAttiGiunta -> search on StoricoAttiGiunta.
	 */
	@RequestMapping(value = "/storicoattos/searchAttiGiunta", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<StoricoAttoGiunta>> getAllAttiGiunta(
			@Valid @RequestBody final StoricoAttoCriteriaDTO criteria,
			@RequestParam(value = "page", required = false) final Integer offset,
			@RequestParam(value = "per_page", required = false) final Integer limit)
			throws GestattiCatchedException {
		try{
			log.debug("POST request to get Storico Atti Giunta : {}", criteria);

			Page<StoricoAttoGiunta> page = attoService.findAllAttoGiuntaPaginato(
					PaginationUtil.generatePageRequest(
							offset, limit, 
							getSort(criteria.getTipoOrdinamento(), criteria.getOrdinamento(), "codiceCifra")), 
					criteria);
			
			HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(
					page, "/storicoattos/searchAttiGiunta", offset, limit);

			return new ResponseEntity<List<StoricoAttoGiunta>>(page.getContent(), headers, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	
	/**
	 * GET /storicoattos/tipiIter -> get all tipiIter.
	 */
	@RequestMapping(value = "/storicoattos/tipiIter", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getAllTipiIterAttiDirigenziali()
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getAllTipiIterAttiDirigenziali : ");

			List<String> content = attoService.getAllTipiIter();

			return new ResponseEntity<>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/tipiIter -> get all tipiFinanziamento.
	 */
	@RequestMapping(value = "/storicoattos/tipiFinanziamento", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getAllTipiFinanziamento()
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getAllTipiFinanziamento : ");

			List<TipoFinanziamento> tipiFinanziamento = tipoFinanziamentoService.findAll();
			
			List<String> content = new ArrayList<String>();
			
			for (TipoFinanziamento t : tipiFinanziamento) {
				content.add(t.getDescrizione());
			}

			return new ResponseEntity<>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/tipiIter -> get all tipiIAdem.
	 */
	@RequestMapping(value = "/storicoattos/tipiAdem", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getAllTipiAdempimentiAttiDirigenziali()
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getAllTipiIterAttiDirigenziali : ");

			List<String> content = attoService.getAllTipiAdempimenti();

			return new ResponseEntity<>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	
	/**
	 * GET /storicoattos/tipiRiunioneGiunta -> get all tipiRiunioneGiunta.
	 */
	@RequestMapping(value = "/storicoattos/tipiRiunioneGiunta", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<String>> getAllTipiRiunioneGiunta()
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getAllTipiRiunioneGiunta : ");

			List<String> content = attoService.getAllTipiRiunioneGiunta();

			return new ResponseEntity<>(content, HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	/**
	 * GET /storicoattos/minAnnoAttiDirigenziali -> get min_maxAnnoAttiStorici.
	 */
	@RequestMapping(value = "/storicoattos/min_maxAnnoAttiStorici", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<String> getMinMaxAnnoAttiStorici(
			@RequestParam(value = "tipoRicerca", required = true) final String tipoRicerca)
			throws GestattiCatchedException {
		try{
			log.debug("GET request to getMinMaxAnnoAttiStorici : {}", tipoRicerca);
			
			Map<String, String> anniMap = attoService.getMinMaxAnno(tipoRicerca); 
			
			JsonObject json = new JsonObject();
			json.addProperty("minAnno", anniMap.get("minAnno"));
			json.addProperty("maxAnno", anniMap.get("maxAnno"));

			return new ResponseEntity<>(json.toString(), HttpStatus.OK);
		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	private Sort getSort(String tipoOrdinamento, String ordinamento, String ordinamentoDef){
		log.debug(String.format("tipoOrdinamento:$s - ordinamento:%s - ordinamentoDef:%s", tipoOrdinamento, ordinamento, ordinamentoDef));
		
		List<String> ord_col = new ArrayList<String>();
		ord_col.add(ordinamentoDef);
		if(ordinamento!=null && !"".equals(ordinamento.trim())){
			ord_col = Arrays.asList(ordinamento.split(";"));
		}
		
		if (tipoOrdinamento!=null && tipoOrdinamento.equalsIgnoreCase("desc"))
			return new Sort(Sort.Direction.DESC, ord_col);
		else
			return new Sort(Sort.Direction.ASC, ord_col);
	}
	
	
	//TODO: Verificare se serve l'export in xml per gli atti dello storico??
//	@Inject
//	private XmlBuilderService xmlBuilderService;
//	@RequestMapping(value = "/storicoattos/{tipo}/xml/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
//	@Timed
//	public ResponseEntity<FileSystemResource> oneXml(
//			@PathVariable final String tipo,
//			@PathVariable final Long id,
//			HttpServletRequest request)
//			throws CifraCatchedException {
//		try{
//			log.debug("GET request to xml search Attos");
//			
//			
//			
//			java.io.File file = xmlBuilderService.creaXml(attoService.findOneAttoDirigenziale(id));
//			file.deleteOnExit();
//			return DownloadFileUtil.responseStream(file, "atto_"+id+".xml");
//		}catch(Exception e){
//    		throw new CifraCatchedException(e);
//    	}
//	}
	
	
	

}
