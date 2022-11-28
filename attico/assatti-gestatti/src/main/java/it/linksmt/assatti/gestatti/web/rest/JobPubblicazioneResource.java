package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.itextpdf.text.DocumentException;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.JobPubblicazione;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.JobPubblicazioneRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.security.SecurityUtils;
import it.linksmt.assatti.service.AooService;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.JobPubblicazioneService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.UtenteService;
import it.linksmt.assatti.service.dto.CriteriReportPdfRicercaDTO;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing JobPubblicazioneResource.
 */
@RestController
@RequestMapping("/api")
public class JobPubblicazioneResource {

    private final Logger log = LoggerFactory.getLogger(JobPubblicazioneResource.class);
    
    @Inject
    private JobPubblicazioneService jobPubblicazioneService;
    
    @Inject
    private AttoService attoService;
    
    @Inject
    private JobPubblicazioneRepository jobPubblicazioneRepository;
    
    @Inject
    private UtenteService utenteService;
    
    @Inject
    private AooService aooService;
    
    @Inject
    private ReportService reportService;
    
    
    /**
     * POST  /jobPubblicaziones/search -> get all the jobPubblicaziones match with filters.
     */
    @RequestMapping(value = "/jobPubblicaziones/search",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<JobPubblicazione>> search(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody String searchStr)
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		Sort sort = new Sort(new Order(Direction.DESC, "id" ));
    		Page<JobPubblicazione> page = jobPubblicazioneService.findAll(PaginationUtil.generatePageRequest(offset, limit, sort), search);
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/jobPubblicaziones", offset, limit);
	        return new ResponseEntity<List<JobPubblicazione>>(page.getContent(), headers, HttpStatus.OK);
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /jobPubblicaziones/:id -> get the "id" jobPubblicazione.
     */
    @RequestMapping(value = "/jobPubblicaziones/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<JobPubblicazione> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to get jobPubblicazione : {}", id);
	    	JobPubblicazione jobPubblicazione = jobPubblicazioneRepository.findOne(id);
	    	
	    	if (jobPubblicazione == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	    	
	    	Atto atto = attoService.findOne(jobPubblicazione.getAtto().getId());
	    	jobPubblicazione.setAtto(atto);
	    	
	        
	        return new ResponseEntity<>(jobPubblicazione, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * GET  /jobPubblicaziones/:id -> get the "id" jobPubblicazione.
     */
    @RequestMapping(value = "/jobPubblicaziones/{id}/disable",
            method = RequestMethod.PUT,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> disableJobPubblicazione(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
    		log.debug("REST request to disable jobPubblicazione : {}", id);
			JsonObject json = new JsonObject();
			try{
				if(id!=null){
					jobPubblicazioneService.disable(id);
					json.addProperty("stato", "ok");
				}else{
					json.addProperty("stato", "errore");
				}
			}catch(Exception e){
				json.addProperty("stato", "errore");
			}
			return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
     * PUT  /jobPubblicaziones -> Updates dati Annullamento.
     */
    @RequestMapping(value = "/jobPubblicaziones",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> updateDatiAnnullamento(@RequestBody JobPubblicazione jobPubblicazione) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to annulla JobPubblicazione : {}", jobPubblicazione);

	    	boolean oscuramento = jobPubblicazione.getAtto().getOscuramentoAttoPubblicato()!=null && 
	    			jobPubblicazione.getAtto().getOscuramentoAttoPubblicato().booleanValue();
	    	
	    	attoService.updateDatiAnnullamentoPubblicazione(
	    			jobPubblicazione.getAtto().getMotivazioneRichiestaAnnullamento(), 
	    			oscuramento, jobPubblicazione.getId());
	    	
	        return ResponseEntity.ok().build();
    	}
    	catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    /**
	 * GET /report -> Create search pdf report.
	 * 
	 * @throws IOException
	 * @throws DocumentException
	 */
	@RequestMapping(value = "/jobPubblicaziones/reportpdf/{idModelloHtml}/{criteriaStr}/endSearch", method = RequestMethod.GET)
	@Timed
	public ResponseEntity<FileSystemResource> report(@PathVariable("criteriaStr") String criteriaStr,
			@PathVariable("idModelloHtml") Long idModelloHtml) throws GestattiCatchedException {
		try{
			ObjectMapper mapper = new ObjectMapper();
			
			List<Atto> atti = null;
			List<JobPubblicazione> jp= null;
			Map<String, String> criteriMap = new HashMap<String, String>();
			criteriaStr = criteriaStr.replaceAll("slash", "/");
			
			JsonParser parser = new JsonParser();
	  		JsonObject searchJsonObj = parser.parse(criteriaStr).getAsJsonObject();

			Properties prop = new Properties();
			ClassPathResource res = new ClassPathResource("xls.properties");
			prop.load(res.getInputStream());
			
			TypeReference<HashMap<String,Object>> typeRef = new TypeReference<HashMap<String,Object>>() {};
			Map<String, Object> tempCriteriMap = mapper.readValue(criteriaStr, typeRef);
			
			for (Map.Entry<String, Object> entry : tempCriteriMap.entrySet()) {
			   if (entry.getValue() instanceof String && (!"viewtype".equalsIgnoreCase(entry.getKey()))) {
				   criteriMap.put(entry.getKey(), (String)entry.getValue());
			   }
			   if ("statoRelata".equalsIgnoreCase(entry.getKey()) && entry.getValue() instanceof HashMap) {
				   
				   Integer idStato = (Integer)((HashMap)entry.getValue()).get("id");
				   if ((idStato != null) && (idStato.intValue() > 0)) {
					   criteriMap.put(entry.getKey(), (String)((HashMap)entry.getValue()).get("denominazione"));
					   searchJsonObj.remove("statoRelata");
					   searchJsonObj.addProperty("statoRelata", idStato);
				   }
			   }
			   if ("errori".equalsIgnoreCase(entry.getKey()) && entry.getValue() instanceof Boolean) {
				   if (((Boolean)entry.getValue()).booleanValue()) {
					   criteriMap.put(entry.getKey(), "SI");
				   }
				   else {
					   criteriMap.put(entry.getKey(), "NO");
				   }
			   }
			}
			
			// criteriMap = reportPdfCriteriTrasformer.generateMap(criteria, prop);
//			if(criteria.getViewtype().equals("tutti-nonpaginati")){
//				atti = Lists.newArrayList(attoService.findAll(criteria));
//			}else{
				jp = search(searchJsonObj, 0, Integer.MAX_VALUE).getContent();
//			}
			
			if(jp!=null){
				atti = new ArrayList<Atto>();
				for (int i = 0; i < jp.size(); i++) {
					atti.add(jp.get(i).getAtto());
				}
			}
				
				
			Utente user = utenteService.findByUsername(SecurityUtils.getCurrentLogin());
			Map<String, String> userMap = new HashMap<String, String>();
			userMap.put("nome", user.getNome());
			userMap.put("cognome", user.getCognome());
			
			List<Aoo> aoos = new ArrayList<Aoo>(); 
			if (tempCriteriMap.get("profiloId") instanceof Integer) {
				Long profiloVal = ((Integer)tempCriteriMap.get("profiloId")).longValue();
				if (profiloVal.longValue() > 0) {
					aoos = aooService.findGerarchiaAooByProfiloId(profiloVal);
				}
			}
			
			TreeSet<String> colNamesSorted = new TreeSet<String>();
			if (tempCriteriMap.get("colnames") instanceof ArrayList) {
				colNamesSorted = new TreeSet<String>((ArrayList)tempCriteriMap.get("colnames"));
			}
			
			java.io.File file = reportService.executeReportRicerca(idModelloHtml, colNamesSorted, atti, userMap, aoos, 
				criteriMap, new CriteriReportPdfRicercaDTO());
			
			return DownloadFileUtil.responseStream(file, "report.pdf");
		}
		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
	}
	
	private Page<JobPubblicazione> search(JsonObject searchJsonObj, Integer offset, Integer limit){
		
		Page<JobPubblicazione> page = null;
		try {
			page = jobPubblicazioneService.findAll(PaginationUtil.generatePageRequest(offset, limit, null), searchJsonObj);
		} catch (ParseException e) {
			log.error("Errore durante la generazione dei report di ricerca.", e);
		}
		
		return page;
	}
    
}
