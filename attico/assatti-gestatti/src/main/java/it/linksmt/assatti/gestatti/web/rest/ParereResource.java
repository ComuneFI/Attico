package it.linksmt.assatti.gestatti.web.rest;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.codahale.metrics.annotation.Timed;
import com.google.gson.JsonObject;

import it.linksmt.assatti.datalayer.domain.Aoo;
import it.linksmt.assatti.datalayer.domain.DocumentoInformatico;
import it.linksmt.assatti.datalayer.domain.DomainUtil;
import it.linksmt.assatti.datalayer.domain.Parere;
import it.linksmt.assatti.datalayer.repository.ParereRepository;
import it.linksmt.assatti.gestatti.web.rest.util.AllegatoAttoUtil;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.DocumentoInformaticoService;
import it.linksmt.assatti.service.ParereService;
import it.linksmt.assatti.service.exception.AllegatoNonPermessoException;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

/**
 * REST controller for managing Parere.
 */
@RestController
@RequestMapping("/api")
public class ParereResource {

    private final Logger log = LoggerFactory.getLogger(ParereResource.class);

    @Inject
    private ParereRepository parereRepository;

    @Inject
   	private DocumentoInformaticoService documentoInformaticoService;
    @Inject
   	private ParereService parereService;
    
    @Inject
    private DmsService dmsService;
       
    
    /**
     * POST  /pareres -> Create a new parere.
     */
    @RequestMapping(value = "/pareres",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> create(@RequestBody Parere parere) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save Parere : {}", parere);
	        if (parere.getId() != null) {
	            return new ResponseEntity<String>(HttpStatus.BAD_REQUEST);
	        }
	        parere = parereRepository.save(parere);
	        JsonObject json = new JsonObject();
			json.addProperty("parereId", parere.getId());
	        return new ResponseEntity<String>(json.toString(), HttpStatus.CREATED);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /pareres -> Updates an existing parere.
     */
    @RequestMapping(value = "/pareres",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<String> update(@RequestBody Parere parere) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to update Parere : {}", parere);
	        if (parere.getId() == null) {
	            return create(parere);
	        }
	        parere = parereRepository.save(parere);
	        JsonObject json = new JsonObject();
			json.addProperty("parereId", parere.getId());
	        return new ResponseEntity<String>(json.toString(), HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    
    @RequestMapping(value = "/pareres/{id}/nonEspresso",
	    method = RequestMethod.PUT,
	    produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Parere> nonEspresso(@PathVariable Long id) throws GestattiCatchedException {
		try{
	    	log.debug("REST request to nonEspresso Parere : {}", id);
	    	Parere parere = parereService.nonEspresso(id);
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
	    	return new ResponseEntity<Parere>(parere, HttpStatus.OK);
		}catch(Exception e){
			throw new GestattiCatchedException(e);
		}
	}

    /**
     * GET  /pareres -> get all the pareres.
     */
    @RequestMapping(value = "/pareres",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<Parere>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<Parere> page = parereRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/pareres", offset, limit);
	        return new ResponseEntity<List<Parere>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /pareres/:id -> get the "id" parere.
     */
    @RequestMapping(value = "/pareres/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Parere> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get Parere : {}", id);
	        Parere parere = parereRepository.findOne(id);
	        if (parere == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(parere, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /pareres/:id -> delete the "id" parere.
     */
    @RequestMapping(value = "/pareres/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete Parere : {}", id);
	        parereService.rimuoviParere(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

  	/**
  	 * GET/pareres/{id}/allegato/{idAllegato}-> download documento allegato
	 * @throws IOException 
  	 */
  	@RequestMapping(value = "/pareres/allegato/{idAllegato}", method = RequestMethod.GET  )
  	@Timed
  	public ResponseEntity<ByteArrayResource> downloadAllegato(
  			@PathVariable Long idAllegato ) throws GestattiCatchedException {
  		try{
			DocumentoInformatico allegato = documentoInformaticoService.findOne( idAllegato );
//			File result = documentoInformaticoService.downloadAllegato(  allegato.getFile().getId(), allegato.getId() );
//			return responseStream(result);
			
			byte[] content = dmsService.getContent(allegato.getFile().getCmisObjectId());
			
			ByteArrayResource byteArrayResource = new ByteArrayResource(content);
			
			
//			return new ResponseEntity<ByteArrayResource>(byteArrayResource, HttpStatus.OK);
		 	return ResponseEntity.ok()
		 			.header("Content-Disposition", "attachment;filename=\""+ allegato.getNomeFile() + "\"" )
		 			.contentLength(content.length)
		            .contentType(MediaType.parseMediaType(allegato.getFile().getContentType()) )
		            .body( byteArrayResource );
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
  	}
  	
  	@RequestMapping(value = "/pareres/{id}/allegato", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
 	@Timed
 	public @ResponseBody ResponseEntity<Set<DocumentoInformatico>> allegato(@PathVariable Long id, @RequestParam("file") MultipartFile[] files) throws GestattiCatchedException{
     	try{
 	    	log.debug("REST request to allegato id : {}", id);
 			Parere parere = parereService.findOne(id);
 			if (parere == null) {
 				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
 			}
 			
 			if(!AllegatoAttoUtil.isEstensionePermessa(files)) {
				throw new AllegatoNonPermessoException("Estensione non consentita: l'allegato non e' stato caricato.");
			}
			if(AllegatoAttoUtil.isAFileZipWithZipInside(files)) {
				throw new AllegatoNonPermessoException("Allegato non consentito: assicurarsi che il file zip non contenga altri file zip al suo interno o che i file contenuti abbiano un'estensione consentita.");
			}
 			
 			Set<DocumentoInformatico> allegati = documentoInformaticoService.save(parere, files);
 			return new ResponseEntity< >(allegati ,HttpStatus.OK);
     	}
     	catch(Exception e){
     		throw new GestattiCatchedException(e);
     	}
 	}

//	private ResponseEntity<FileSystemResource> responseStream(File result)
//			throws CifraCatchedException {
//		try{
//			java.io.File fileTemp = java.io.File.createTempFile("downloadcifra" ,".tmp");
//			FileUtils.writeByteArrayToFile(fileTemp, result.getContenuto());
//			fileTemp.deleteOnExit();
//			FileSystemResource body = new FileSystemResource(fileTemp);
//	
//		 	return ResponseEntity.ok()
//		 			.header("Content-Disposition", "attachment;filename=\""+ result.getNomeFile() + "\"" )
//		 			.contentLength(result.getSize() )
//		            .contentType(MediaType.parseMediaType(result.getContentType()) )
//		            .body( body );
//		}catch(Exception e){
//    		throw new CifraCatchedException(e);
//    	}
//	}
  	
}
