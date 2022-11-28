package it.linksmt.assatti.gestatti.web.rest;

import it.linksmt.assatti.datalayer.domain.File;
import it.linksmt.assatti.datalayer.repository.FileRepository;
import it.linksmt.assatti.gestatti.web.rest.util.DownloadFileUtil;
import it.linksmt.assatti.service.DmsService;
import it.linksmt.assatti.service.exception.GestattiCatchedException;
import it.linksmt.assatti.service.util.PaginationUtil;

import java.net.URI;
import java.util.List;

import javax.inject.Inject;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
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

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.codec.binary.Base64;
/**
 * REST controller for managing File.
 */
@RestController
@RequestMapping("/api")
public class FileResource {

    private final Logger log = LoggerFactory.getLogger(FileResource.class);

    @Inject
    private FileRepository fileRepository;
    
    @Autowired
	private DmsService dmsService;

    /**
     * POST  /files -> Create a new file.
     */
    @RequestMapping(value = "/files",
            method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> create(@RequestBody File file) throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to save File : {}", file);
	        if (file.getId() != null) {
	            return ResponseEntity.badRequest().header("Failure", "A new file cannot already have an ID").build();
	        }
	        fileRepository.save(file);
	        return ResponseEntity.created(new URI("/api/files/" + file.getId())).build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * PUT  /files -> Updates an existing file.
     */
    @RequestMapping(value = "/files",
        method = RequestMethod.PUT,
        produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<Void> update(@RequestBody File file) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to update File : {}", file);
	        if (file.getId() == null) {
	            return create(file);
	        }
	        fileRepository.save(file);
	        return ResponseEntity.ok().build();
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * GET  /files -> get all the files.
     */
    @RequestMapping(value = "/files",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<List<File>> getAll(@RequestParam(value = "page" , required = false) Integer offset,
                                  @RequestParam(value = "per_page", required = false) Integer limit)
                                		  throws GestattiCatchedException {
    	try{
	    	Page<File> page = fileRepository.findAll(PaginationUtil.generatePageRequest(offset, limit));
	        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/files", offset, limit);
	        return new ResponseEntity<List<File>>(page.getContent(), headers, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }
    
    @RequestMapping(value = "/files/{cmisid}/download", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadDocumentoOpen(
  			@PathVariable final String cmisid,
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
			File file = fileRepository.findByCmisObjectId(new String(Base64.decodeBase64(cmisid.getBytes("UTF-8"))));
			if (file == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			byte[] content = dmsService.getContent(file.getCmisObjectId());
			return DownloadFileUtil.responseStream(content, file.getNomeFile(), file.getSize(), file.getContentType());
  		}
  		catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	
  	}
    
    /**
     * GET  /files -> get all the files.
     */
    @RequestMapping(value = "/files/manuale", method = RequestMethod.GET, produces={ MediaType.APPLICATION_OCTET_STREAM_VALUE } )
  	@Timed
  	public ResponseEntity<FileSystemResource> downloadManuale(
  			final HttpServletResponse response) throws GestattiCatchedException {
  		try{
	  		log.debug("REST request to downloadManuale");
	  		ClassPathResource resource = new ClassPathResource("Manuale_Utente.pdf");
			if (resource.getFile() == null) {
				return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	  		}
			FileSystemResource body = new FileSystemResource(resource.getFile());
			return ResponseEntity.ok()
		 			.header("Content-Disposition", "attachment;filename=\""+ resource.getFilename() + "\"" )
		 			.contentLength(resource.getFile().length())
		            .contentType(MediaType.parseMediaType("application/pdf"))
		            .body( body );
  		}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}	
  	}

    /**
     * GET  /files/:id -> get the "id" file.
     */
    @RequestMapping(value = "/files/{id}",
            method = RequestMethod.GET,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public ResponseEntity<File> get(@PathVariable Long id, HttpServletResponse response) throws GestattiCatchedException{
    	try{
	    	log.debug("REST request to get File : {}", id);
	        File file = fileRepository.findOne(id);
	        if (file == null) {
	            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	        }
	        return new ResponseEntity<>(file, HttpStatus.OK);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	}
    }

    /**
     * DELETE  /files/:id -> delete the "id" file.
     */
    @RequestMapping(value = "/files/{id}",
            method = RequestMethod.DELETE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @Timed
    public void delete(@PathVariable Long id)throws GestattiCatchedException {
    	try{
	    	log.debug("REST request to delete File : {}", id);
	        fileRepository.delete(id);
    	}catch(Exception e){
    		throw new GestattiCatchedException(e);
    	} 
    }
}
