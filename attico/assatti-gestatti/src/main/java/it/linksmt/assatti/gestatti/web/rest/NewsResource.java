package it.linksmt.assatti.gestatti.web.rest;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.SerializationUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
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

import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.News;
import it.linksmt.assatti.datalayer.domain.OrdineGiorno;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.StatoJob;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.AttoService;
import it.linksmt.assatti.service.NewsService;
import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.service.dto.PaginaDTO;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing News.
 */
@RestController
@RequestMapping("/api")
public class NewsResource {

    private final Logger log = LoggerFactory.getLogger(NewsResource.class);

    @Inject
    private NewsService newsService;
    
    @Inject
    private AttoService attoService;
    
    /**
     * GET  /newss/loadStoricoTentativi -> get all the newss.
     */
    @RequestMapping(value = "/newss/loadStoricoTentativi",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<?>> loadStoricoTentativi(
    		@RequestParam(value = "newsid" , required = true) String newsid,					  
    		@RequestParam(value = "page" , required = false) Integer offset,
            @RequestParam(value = "per_page", required = false) Integer limit)
//                                  ,@RequestBody final String searchStr)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<News> page = newsService.getStoricoTentativi(Long.parseLong(newsid), offset, limit);
	    	for(News n : page.getContent()){
	    		n.setAtto(null);
	    		n.setBeneficiario(null);
				n.setAutore(null);
				if(n.getDocumento()!=null){
					n.setDocumento(new DocumentoPdf(n.getDocumento().getId()));
				}
				n.setDestinatarioInterno(null);
				if(n.getOriginalNews()!=null){
					n.getOriginalNews().setDestinatarioInterno(null);
					n.getOriginalNews().setBeneficiario(null);
					n.getOriginalNews().setAtto(null);
					n.getOriginalNews().setAutore(null);
					if(n.getOriginalNews().getDocumento()!=null){
						n.getOriginalNews().setDocumento(new DocumentoPdf(n.getOriginalNews().getDocumento().getId()));
					}
				}
	    	}
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page.getTotalElements(), "/api/newss/loadStoricoTentativi", offset, limit);
	        return new ResponseEntity<List<?>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * POST  /newss/newsReport/lastState -> get all the newss.
     */
    @RequestMapping(value = "/newss/newsReport/lastState",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<?>> newsReportLastState(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestBody final String searchStr)
                                		  throws GestattiCatchedException {
    	try{
    		JsonParser parser = new JsonParser();
	  		JsonObject search = parser.parse(searchStr).getAsJsonObject();
	  		int mysqlOffset = 0;
	  		if(offset==1){
	  			mysqlOffset = 0;
	  		}else{
	  			mysqlOffset = ((offset-1)*limit)-1;
	  		}
	    	PaginaDTO page = newsService.getReportNews(search, limit, mysqlOffset);
	    	for(Object o : page.getListaOggetti()){
	    		News n = (News)o;
	    		n.setAtto(null);
	    		n.setBeneficiario(null);
				n.setAutore(null);
				if(n.getDocumento()!=null){
					n.setDocumento(new DocumentoPdf(n.getDocumento().getId()));
				}
				n.setDestinatarioInterno(null);
				if(n.getOriginalNews()!=null){
					n.getOriginalNews().setDestinatarioInterno(null);
					n.getOriginalNews().setBeneficiario(null);
					n.getOriginalNews().setAtto(null);
					n.getOriginalNews().setAutore(null);
					if(n.getOriginalNews().getDocumento()!=null){
						n.getOriginalNews().setDocumento(new DocumentoPdf(n.getOriginalNews().getDocumento().getId()));
					}
				}
	    	}
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page.getTotaleElementi(), "/api/newss/newsReport/lastState", offset, limit);
	        return new ResponseEntity<List<?>>(page.getListaOggetti(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /newss -> get all the newss.
     */
    @RequestMapping(value = "/newss",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<News>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit,
                                  @RequestParam(value = "profiloid", required = false) Long profiloid)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<News> page = newsService.getAllNotificheDestinatariInterni(offset, limit, profiloid);
	    	for(News n : page.getContent()){
	    		n.setAtto(null);
	    		n.setBeneficiario(null);
				n.setAutore(null);
				if(n.getDocumento()!=null){
					n.setDocumento(new DocumentoPdf(n.getDocumento().getId()));
				}
				n.setDestinatarioInterno(null);
				if(n.getOriginalNews()!=null){
					n.getOriginalNews().setDestinatarioInterno(null);
					n.getOriginalNews().setBeneficiario(null);
					n.getOriginalNews().setAtto(null);
					n.getOriginalNews().setAutore(null);
					if(n.getOriginalNews().getDocumento()!=null){
						n.getOriginalNews().setDocumento(new DocumentoPdf(n.getOriginalNews().getDocumento().getId()));
					}
				}
	    	}
	        
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/newss", offset, limit);
	        return new ResponseEntity<List<News>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /newss/:id -> get the "id" news.
     */
    @RequestMapping(value = "/newss/retryInvio",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> retryInvio(@RequestParam(value="newsid", required=true) String newsid, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to retryInvio News : {}", newsid);
	        News notifica = newsService.getNotificaById(Long.parseLong(newsid));
	        News copyOfNews = (News)SerializationUtils.clone(notifica);
			if(copyOfNews.getOriginalNews()==null){
				copyOfNews.setOriginalNews(notifica);
			}
			copyOfNews.setId(null);
			copyOfNews.setStato(StatoJob.NEW);
			copyOfNews.setDettaglioErrore(null);
			copyOfNews.setDataStartInvio(null);
			copyOfNews.setDataErrore(null);
			copyOfNews.setDataCreazione(new DateTime());
			copyOfNews.setProgressivoTentativo(1);
			newsService.createNews(copyOfNews);
	        return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /newss/annullaTentativo
     */
    @RequestMapping(value = "/newss/annullaTentativo",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> annullaTentativo(@RequestParam(value="newsid", required=true) String newsid, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to annullaTentativo News : {}", newsid);
        	News notifica = newsService.getLastTentativoByNewsId(Long.parseLong(newsid));
        	if(!notifica.getStato().equals(StatoJob.DONE)){
        		notifica.setStato(StatoJob.CANCELED);
        		notifica.setDataInvio(null);
        		notifica.setDataErrore(null);
        		notifica.setDataCreazione(new DateTime());
        		newsService.updateNews(notifica);
        	}
	        return new ResponseEntity<>(HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    /**
     * GET  /newss/:id -> get the "id" news.
     */
    @RequestMapping(value = "/newss/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<News> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get News : {}", id);
	        News news = newsService.getNotificaById(id);
	        if (news == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        if(news.getAutore()!=null){
	        	Profilo profilo = news.getAutore();
	        	news.setAutore(new Profilo());
	        	news.getAutore().setUtente(new Utente(0L, "", profilo.getUtente().getUsername(), profilo.getUtente().getCognome(), profilo.getUtente().getNome()));
	        }
	        if(news.getDocumento()!=null){
	        	OrdineGiorno odg = null;
	        	if (news.getDocumento().getOrdineGiornoId()!=null){
	        		odg = new OrdineGiorno();
	        		odg.setId(news.getDocumento().getOrdineGiornoId());
	        		//odg.setNumeroOdg(news.getDocumento().getOrdineGiorno().getNumeroOdg());
	        	} 
	        	
	        	DocumentoPdf doc = news.getDocumento();
	        	File file = null;
	        	if(doc.getFile()!=null){
	        		file = doc.getFile();
	        	}
	        	news.setDocumento(new DocumentoPdf());
	        	news.getDocumento().setId(doc.getId());
	        	news.getDocumento().setCreatedBy(doc.getCreatedBy());
	        	news.getDocumento().setCreatedDate(doc.getCreatedDate());
	        	news.getDocumento().setFirmatario(doc.getFirmatario());
	        	
	        	news.getDocumento().setOrdineGiornoId(odg.getId());
	        	if(file!=null){
	        		news.getDocumento().setFile(new File());
	        		news.getDocumento().getFile().setNomeFile(file.getNomeFile());
	        	}
	        }
	        
			if(news.getOriginalNews()!=null){
				news.getOriginalNews().setAutore(null);
				news.getOriginalNews().setDocumento(null);
			}
	        return new ResponseEntity<>(news, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /newss/:id -> delete the "id" news.
     */
    @RequestMapping(value = "/newss/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete News : {}", id);
	        newsService.deleteNews(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
}
