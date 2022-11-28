package it.linksmt.assatti.gestatti.web.rest;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
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
import com.google.common.collect.Lists;

import it.linksmt.assatti.datalayer.domain.Atto;
import it.linksmt.assatti.datalayer.domain.DocumentoPdf;
import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.domain.Profilo;
import it.linksmt.assatti.datalayer.domain.Utente;
import it.linksmt.assatti.datalayer.repository.AttoRepository;
import it.linksmt.assatti.datalayer.repository.ProfiloRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoPdfService;
import it.linksmt.assatti.service.FileService;
import it.linksmt.assatti.service.ModelloHtmlService;
import it.linksmt.assatti.service.ReportService;
import it.linksmt.assatti.service.dto.ReportDTO;
import it.linksmt.assatti.service.dto.ReportDTO.DelegaFirma;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AssistenzaTecnicaResource {

    private final Logger log = LoggerFactory.getLogger(AssistenzaTecnicaResource.class);
    
    @Inject
	private ReportService reportService;
	
	@Inject
	private ModelloHtmlService modelloHtmlService;
	
	@Inject
	private DocumentoPdfService documentoPdfService;
	
	@Inject
	private ProfiloRepository profiloRepository;
	
	@Inject
	private AttoRepository attoRepository;
	
	@Inject
	private FileService fileService;
	
	@Autowired
	private DmsService dmsService;

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
            if(!user.equalsIgnoreCase(WebApplicationProps.getProperty(ConfigPropNames.ASSISTENZA_TECNICA_USER)) || !pwd.equalsIgnoreCase(WebApplicationProps.getProperty(ConfigPropNames.ASSISTENZA_TECNICA_PASSWORD))) {
                return fail;
            }else {
                return null;
            }
        }catch(Exception e) {
            return fail;
        }
    }
    //esempio link: http://127.0.0.1:8080/api/assistenzatecnica/generadocumentocompleto/1986/DETERMINA_DIRIGENZIALE_COMPLETA/206/0/?appkey=L3NlbGV6aW9uYXByb2ZpbG8jIUBsYWZvcmdpYS5kb21lbmljbzIwNg
    @RequestMapping(value = "/assistenzatecnica/generadocumentocompleto/{idAtto}/{tipoDocumento}/{idProfiloFirmatario}/{idProfiloDelegante}/", method = RequestMethod.GET, produces = MediaType.TEXT_PLAIN_VALUE)
	@Timed
	public ResponseEntity<?> generaDocumento(
			@PathVariable final Long idAtto,
			@PathVariable final String tipoDocumento,
			@PathVariable final Long idProfiloFirmatario,
			@PathVariable final Long idProfiloDelegante,
			final HttpServletResponse response, final HttpServletRequest request)
			throws GestattiCatchedException {
   
    	
    	ResponseEntity<String> fail = this.checkBasicAuth(request);
    	try{
    		if(fail == null && idAtto != null && idAtto.longValue()>0 && idProfiloFirmatario!=null && idProfiloFirmatario.longValue()>0) {
        		
    			

    			Atto atto = attoRepository.findOne(idAtto);
    			ReportDTO reportDto = new ReportDTO();
    			Profilo profiloFirmatario = profiloRepository.findOne(idProfiloFirmatario);
    			Utente utFirmatario = profiloFirmatario.getUtente();
    			Profilo profiloDelegante = null;
    			if(idProfiloDelegante > 0) {
    				profiloDelegante = profiloRepository.findOne(idProfiloDelegante);
    			}
    			 
    			if (atto.getTipoAtto().getCodice().equalsIgnoreCase("DD")) {
    				if (idProfiloDelegante > 0) {
    					if ((profiloDelegante != null) && (profiloDelegante.getUtente() != null)) {
    						Utente utDelegante = profiloDelegante.getUtente();
    						if(reportDto.getDelegheFirme()==null) {
    							reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
    						}
    						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utDelegante.getNome() + " " + utDelegante.getCognome(), utFirmatario.getNome() + " " + utFirmatario.getCognome()));
    					}
    				}
    			}else if(Lists.newArrayList("DG", "DPC", "DC").contains(atto.getTipoAtto().getCodice())){
    				if(reportDto.getDelegheFirme()==null) {
    					reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
    				}
    				if (profiloFirmatario != null && profiloFirmatario.getUtente() != null && profiloDelegante != null && profiloDelegante.getUtente() != null && !profiloDelegante.getUtente().getId().equals(profiloFirmatario.getUtente().getId())) {
    					Utente utDelegante = profiloDelegante.getUtente();
    					reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utDelegante.getNome() + " " + utDelegante.getCognome(), utFirmatario.getNome() + " " + utFirmatario.getCognome()));
    				}else {
    					reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(utFirmatario.getNome() + " " + utFirmatario.getCognome(), null));
    				}
    				this.manageDeleghe(atto, reportDto, 1);
    			}
//    			
				List<ModelloHtml> modelloHtmlList = modelloHtmlService.findByTipoDocumento(tipoDocumento);
				if(modelloHtmlList!=null && modelloHtmlList.size() == 1) {
					boolean omissis = false;
					if ((atto.getRiservato() == null || atto.getRiservato().equals(false)) && (atto.getPubblicazioneIntegrale() == null || atto.getPubblicazioneIntegrale().equals(false))) {
						omissis = true;
					}
					
					ModelloHtml modelloHtml = modelloHtmlList.get(0);
					reportDto.setIdAtto(atto.getId());
					reportDto.setIdModelloHtml(modelloHtml.getId());
					reportDto.setOmissis(omissis);
					java.io.File pdfAtto = reportService.previewAtto(atto, reportDto);
					
					DocumentoPdf documentoPdf = documentoPdfService.salvaDocumentoPdf(atto, pdfAtto, tipoDocumento, omissis, false, false, true);
					
					Long idFile = documentoPdf.getFile().getId();
			  		
					File file = fileService.findByFileId(idFile); // documentoPdfService.download( idDocumento);
					if (file == null) {
						return new ResponseEntity<>(HttpStatus.NOT_FOUND);
			  		}
					byte[] content = dmsService.getContent(file.getCmisObjectId());
					return DownloadFileUtil.responseStream(content, file.getNomeFile(), file.getSize(), file.getContentType());
				}
    			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    		}else{
    			return fail;
    		}
    	}
    	catch(Exception e){
    		e.printStackTrace();
    		return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    	}
    }
  //non include il documento appena firmato, in quanto non ancora committato sul db il savataggio
  	private void manageDeleghe(Atto atto, ReportDTO reportDto, int nDoc) {
  		if(atto.getDocumentiPdfAdozione()!=null && atto.getDocumentiPdfAdozione().size() > 0) {
  			int i = 1;
  			for(DocumentoPdf doc : atto.getDocumentiPdfAdozione()) {
  				if(doc!=null && doc.getFirmato()!=null && doc.getFirmato()) {
  					if(reportDto.getDelegheFirme()==null) {
  						reportDto.setDelegheFirme(new ArrayList<DelegaFirma>());
  					}
  					if(doc.getFirmatarioDelegante()!=null && !doc.getFirmatarioDelegante().trim().isEmpty()) {
  						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(doc.getFirmatarioDelegante(), doc.getFirmatario()));
  					}else {
  						reportDto.getDelegheFirme().add(reportDto.new DelegaFirma(doc.getFirmatario(), null));
  					}
  				}
  				if(nDoc <= i) {
  					break;
  				}else {
  					i++;
  				}
  			}
  		}
  		
  	}
}
