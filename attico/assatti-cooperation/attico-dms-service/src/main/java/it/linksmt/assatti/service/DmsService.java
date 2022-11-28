package it.linksmt.assatti.service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.model.CmisAssociationDTO;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.service.CmisService;
import it.linksmt.assatti.service.exception.DmsEmptyFileException;
import it.linksmt.assatti.service.exception.DmsException;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;


/**
 * Service class for managing DMS.
 */
@Service
public class DmsService {

	private final Logger log = LoggerFactory.getLogger(DmsService.class);
	
	@Autowired
	private CmisService cmisService;
	
	public String save(String attoFolderPath, byte[] content, String filename, 
			String contentType, Map<String, Object> properties) throws DmsException, DmsEmptyFileException {
		
		Long originContentLength = new Long(0);
		
		if(content==null || content.length == 0) {
			throw new DmsEmptyFileException();
		}else {
			originContentLength = new Long(content.length);
		}
		
		String objectId = null;
		/*
		 * Salvo documento su repository documentale
		 */
		CmisDocumentDTO cmisDocumentDTO = null;
		try {
			/*
			 * Ricerca/creazione directory su DMS
			 */
			CmisFolderDTO mainFolder = cmisService.getMainFolder();
			
			String processFolderPath = mainFolder.getPath() + attoFolderPath;
			log.debug("DmsService :: save() :: processFolderPath: " + processFolderPath);

			CmisFolderDTO processFolder = cmisService.getFolderByPath(processFolderPath, true);
			
			/*
			 * salvataggio documento su DMS
			 */
			cmisDocumentDTO = cmisService.createDocument(
					content,
					filename,
					contentType,
					processFolder.getId(),
					properties,
					true);
			
			try {
				this.checkEmptyCmisDocument(cmisDocumentDTO, originContentLength);
			}catch(DmsException e) {
				Long delay = new Long("2"); //seconds
				try {
					delay = Long.parseLong(WebApplicationProps.getProperty(ConfigPropNames.ALFRESCO_RETRY_SECONDS, delay + "").trim());
				}catch(Exception ex) {}
				log.info("empty document error - ritrieving will retry after " + delay + " seconds..");
				TimeUnit.SECONDS.sleep(delay);
				try {
					cmisDocumentDTO = this.getDocumentMetadata(cmisDocumentDTO.getId());
					this.checkEmptyCmisDocument(cmisDocumentDTO, originContentLength);
					log.info("retrieving retry  end with success - documentId " + (cmisDocumentDTO.getId() + " documentName: " + cmisDocumentDTO.getName()));
				}catch(DmsException e2) {
					cmisDocumentDTO = cmisService.createDocument(
							content,
							filename,
							contentType,
							processFolder.getId(),
							properties,
							true);
					this.checkEmptyCmisDocument(cmisDocumentDTO, originContentLength);
					log.info("sending retry end with success - documentId " + (cmisDocumentDTO.getId() + " documentName: " + cmisDocumentDTO.getName()));
				}
			}
			
		} 
		catch (Exception e) {
			log.error("errore durante il salvataggio del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
		
		if(cmisDocumentDTO!=null) {
			objectId = cmisDocumentDTO.getId();
		}
		
		return objectId;
	}
	
	private void checkEmptyCmisDocument(CmisDocumentDTO cmisDocumentDTO, Long originContentLength) {
		if(cmisDocumentDTO.getContentSize()==null || cmisDocumentDTO.getContentSize().equals(0L)) {
			throw new DmsEmptyFileException(cmisDocumentDTO.getId(), cmisDocumentDTO.getName());
		}else if(!cmisDocumentDTO.getContentSize().equals(originContentLength)) {
			log.error("content of cmis document is different of original content in attico\noriginContentLength: " + originContentLength + "\ncmisDocumentDTO.getContentSize(): " + cmisDocumentDTO.getContentSize());
			throw new DmsException("content of cmis document is different of original content in attico");
		}
	}
	
	public void addSecondaryTypes(String documentId, List<String> secondaryTypes) throws DmsException {
		
		if (secondaryTypes == null || secondaryTypes.isEmpty()) {
			return;
		}
		
		try {
			cmisService.addAspect(documentId, secondaryTypes);
		}
		catch (Exception e) {
			log.error("errore durante il salvataggio del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
	}
	
	public void updateDocumentMetadata(String documentId, Map<String, Object> updProperties)  throws DmsException {
		if (updProperties == null || updProperties.isEmpty()) {
			return;
		}
		
		try {
			cmisService.updateDocumentMetadata(documentId, updProperties);
		}
		catch (Exception e) {
			log.error("errore durante il salvataggio del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
	}
	
	public void addAssociations(String sourceDocumentId, List<CmisAssociationDTO> associations) {
		if (associations == null || associations.isEmpty()) {
			return;
		}
		
		try {
			for (CmisAssociationDTO association : associations) {
				cmisService.createAssociation(association.getAssociationType(), sourceDocumentId, association.getTargetNodeId());
			}
		}
		catch (Exception e) {
			log.error("errore durante il salvataggio del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
	}
	
	public CmisDocumentDTO getDocumentMetadata(String documentId) throws DmsException {
		
		try {
			return cmisService.getDocument(documentId, false, false, null);
		}
		catch(CmisServiceException e) {
			log.error("errore durante la lettura dei dati del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
	}
	
	
	public byte[] getContent(String documentId) throws DmsException {
		byte[] content = null;
		try {
			content = cmisService.getDocumentContent(documentId);
		}
		catch (CmisServiceException e) {
			log.error("errore durante il download del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
		return content;
	}
	
	public void remove(String documentId) throws DmsException {
		
		try {
			cmisService.deleteDocument(documentId);
		}
		catch(CmisServiceException e) {
			log.error("errore durante l'eliminazione del documento :: " + e.getMessage(), e);
			throw new DmsException(e);
		}
	}
}
