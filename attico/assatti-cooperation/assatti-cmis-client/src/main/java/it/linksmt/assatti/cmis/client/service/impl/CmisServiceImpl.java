package it.linksmt.assatti.cmis.client.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectId;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.QueryResult;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.data.ObjectData;
import org.apache.chemistry.opencmis.commons.enums.BaseTypeId;
import org.apache.chemistry.opencmis.commons.enums.PropertyType;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.chemistry.opencmis.commons.enums.VersioningState;
import org.apache.chemistry.opencmis.commons.exceptions.CmisContentAlreadyExistsException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisStorageException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import it.linksmt.assatti.cmis.client.converter.CmisDocumentConverter;
import it.linksmt.assatti.cmis.client.converter.CmisFolderConverter;
import it.linksmt.assatti.cmis.client.exception.CmisServiceErrorCode;
import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.login.LoginFactory;
import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.cmis.client.login.session.SessionStore;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.cmis.client.service.CmisService;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.CmisProps;

/**
 * Classe per l'interfacciamento con un DMS che espone servizi CMIS.
 *
 * Nel caso di Alfresco, sono supportate funzionalità non disponibili tramite cmis, come ad esempio
 * la gestione delle categorie, disponibile solo tramite webservice di alfresco.
 *
 * Versioni di CMIS supportate: - 1.0 - 1.1
 *
 * Versioni di alfresco supportate: - 4.2.c - 4.2.e - 5.0.c - 5.2.0
 *
 * @author marco ingrosso
 *
 */
@Service("cmisService")
public class CmisServiceImpl implements CmisService {

	protected static Logger log = LoggerFactory.getLogger(CmisServiceImpl.class.getName());

	private static final int MAX_ITEMS_PER_PAGE_DEFAULT_VALUE = 1000000;

	String cmisHost = CmisProps.getProperty(ConfigPropNames.CMIS_HOST); // nome del dominio dove
																		// recuperare i file
	String token; // token per il login su alfresco
	String cmisVersion = CmisProps.getProperty(ConfigPropNames.CMIS_VERSION); // cmis version
	String cmisMainFolderPath = CmisProps.getProperty(ConfigPropNames.CMIS_MAIN_FOLDER_PATH);
	int maxItemsPerPage = MAX_ITEMS_PER_PAGE_DEFAULT_VALUE; // numero di elementi restituiti da una
															// ricerca con cmis sql - override (no
															// overwrite!) della configurazione di
															// alfresco

	private static long lastSessionTime = 0;

	@Override
	public CmisFolderDTO getRootFolder() throws CmisServiceException {
		CmisFolderDTO alfrescoFolderDTO = null;
		Folder rootFolder = getLoginSession().getRootFolder();

		alfrescoFolderDTO = CmisFolderConverter.convertToDto(rootFolder);

		return alfrescoFolderDTO;
	}

	@Override
	public CmisFolderDTO getMainFolder() throws CmisServiceException {
		CmisFolderDTO cmisFolderDTO = getFolderByPath(cmisMainFolderPath, true);
		return cmisFolderDTO;
	}

	@Override
	public CmisFolderDTO getFolder(final String nodeId) throws CmisServiceException {
		CmisFolderDTO cmisFolderDTO = null;

		if (nodeId != null && !nodeId.isEmpty()) {
			CmisObject cmisFolder = getLoginSession().getObject(nodeId);
			if (cmisFolder != null && cmisFolder.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				Folder folder = (Folder) cmisFolder;
				cmisFolderDTO = CmisFolderConverter.convertToDto(folder);
			}
		}
		else {
			cmisFolderDTO = getRootFolder();
		}

		return cmisFolderDTO;
	}

	@Override
	public CmisFolderDTO getFolderByPath(final String path) throws CmisServiceException {
		return CmisFolderConverter.convertToDto(getCmisFolderByPath(path));
	}

	@Override
	public CmisFolderDTO getFolderByPath(final String path, boolean createIfNotExist) throws CmisServiceException {
		CmisFolderDTO cmisFolderDTO = null;
		Folder f = null;
		try {
			f = getCmisFolderByPath(path);
		}
		catch (CmisServiceException e) {
			if (e.getErrorCode().equals(CmisServiceErrorCode.OBJECT_NOT_FOUND)) {
				log.info("CmisServiceImpl :: getFolderByPath() :: Folder " + path + " non trovata.");
			}
			else {
				throw new CmisServiceException(e.getErrorCode());
			}
		}
		if (f != null) {
			cmisFolderDTO = CmisFolderConverter.convertToDto(f);
		}
		else {
			if (createIfNotExist) {
				cmisFolderDTO = createFolder(path);
			}
		}
		return cmisFolderDTO;
	}

	private Folder getCmisFolderByPath(final String path) throws CmisServiceException {
		Folder folder = null;
		Session cmisSession = getLoginSession();

		if (path != null && !path.isEmpty()) {
			CmisObject cmisFolder = null;
			try {
				// String encodedPath = path.replace(" ", "+");
				cmisFolder = getLoginSession().getObjectByPath(path);
			}
			catch (CmisObjectNotFoundException e) {
				throw new CmisServiceException(CmisServiceErrorCode.OBJECT_NOT_FOUND);
			}
			if (cmisFolder != null && cmisFolder.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				folder = (Folder) cmisFolder;
			}
		}
		else {
			folder = cmisSession.getRootFolder();
		}

		return folder;
	}

	private Folder getCmisFolder(final String nodeId) throws CmisServiceException {
		Folder folder = null;
		Session cmisSession = getLoginSession();

		if (nodeId != null && !nodeId.isEmpty()) {
			CmisObject cmisFolder = getLoginSession().getObject(nodeId);
			if (cmisFolder != null && cmisFolder.getBaseTypeId().equals(BaseTypeId.CMIS_FOLDER)) {
				folder = (Folder) cmisFolder;
			}
		}
		else {
			folder = cmisSession.getRootFolder();
		}

		return folder;
	}

	@Override
	public CmisFolderDTO createFolder(String folderName, String parentFolderId) throws CmisServiceException {
		CmisFolderDTO cmisFolderDTO = null;
		// Session cmisSession = getLoginSession(endpointCmis, cmisUsername, cmisPassword);
		Folder parentFolder = getCmisFolder(parentFolderId);

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
		properties.put(PropertyIds.NAME, folderName);
		Folder newFolder = parentFolder.createFolder(properties);
		log.debug("Created new folder: " + newFolder.getId());

		cmisFolderDTO = CmisFolderConverter.convertToDto(newFolder);

		return cmisFolderDTO;
	}

	@Override
	public CmisFolderDTO createFolder(String folderPath, String folderTitle, String folderDescription) throws CmisServiceException {
		CmisFolderDTO cmisFolderDTO = null;

		/*
		 * Elimino il simbolo / che rappresenta la root, in quanto genera un elemento vuoto
		 * nell'array ottenuto facendo lo split
		 */
		if (folderPath.startsWith("/")) {
			folderPath = folderPath.substring(1);
		}

		String[] folders = folderPath.split("/");

		Folder parentFolder = getLoginSession().getRootFolder();

		for (String f : folders) {
			String subFolderPath = parentFolder.getPath();
			if (!subFolderPath.endsWith("/")) {
				subFolderPath += "/";
			}
			subFolderPath += f;
			Folder subFolder = null;
			try {
				subFolder = getCmisFolderByPath(subFolderPath);
			}
			catch (CmisServiceException e) {
				if (e.getErrorCode() != CmisServiceErrorCode.OBJECT_NOT_FOUND) {
					log.error("unable to get cmis folder by path :: " + e.getMessage(), e);
				}
			}
			catch (Exception e) {
				log.error("unable to get cmis folder by path  :: " + e.getMessage(), e);
			}
			if (subFolder == null) {
				Map<String, String> properties = new HashMap<String, String>();
				// Following sets the content type and adds the webable and productRelated aspects
				// This works because we are using the OpenCMIS extension for Cmis
				if (cmisVersion.equals("1.1")) {
					properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:folder");
				}
				else {
					properties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
				}
				properties.put(PropertyIds.NAME, f);

				if (folderDescription != null && !folderDescription.isEmpty()) {
					properties.put("cm:description", folderDescription);
				}
				if (folderTitle != null && !folderTitle.isEmpty()) {
					properties.put("cm:title", folderTitle);
				}

				subFolder = parentFolder.createFolder(properties);
			}
			parentFolder = subFolder;
		}

		log.debug("Created new folder: " + parentFolder.getId() + " - " + parentFolder.getName());

		cmisFolderDTO = CmisFolderConverter.convertToDto(parentFolder);

		return cmisFolderDTO;
	}

	@Override
	public CmisFolderDTO createFolder(String folderPath) throws CmisServiceException {
		return createFolder(folderPath, null, null);
	}

	@Override
	public CmisDocumentDTO createDocument(final File file, String fileType, String folderId, Map<String, Object> documentProperties) throws CmisServiceException {
		return createDocument(file, fileType, folderId, documentProperties, true);
	}

	@Override
	public CmisDocumentDTO createDocument(final File file, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfExists) throws CmisServiceException {
		try {
			final byte[] content = FileUtils.readFileToByteArray(file);
			String fileName = file.getName();
			return createDocument(content, fileName, fileType, folderId, documentProperties, updateIfExists);
		}
		catch (Exception e) {
			log.error("unable to create document :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}
	}

	@Override
	public CmisDocumentDTO createDocument(final byte[] content, String fileName, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfExists)
			throws CmisServiceException {
		return createDocument(content, fileName, fileType, folderId, documentProperties, updateIfExists, VersioningState.MAJOR.value());
	}

	@Override
	public CmisDocumentDTO createDocument(final byte[] content, String fileName, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfExists,
			String versioningState) throws CmisServiceException {
		CmisDocumentDTO cmisDocumentDTO = null;
		Session cmisSession = getLoginSession();

		// create a map of properties if one wasn't passed in
		if (documentProperties == null) {
			documentProperties = new HashMap<String, Object>();
		}

		// Add the object type ID if it wasn't already
		if (documentProperties.get(PropertyIds.OBJECT_TYPE_ID) == null) {
			if (cmisVersion.equals("1.1")) {
				String propertyValue = "cmis:document";
				documentProperties.put(PropertyIds.OBJECT_TYPE_ID, propertyValue);
			}
			else {
				documentProperties.put(PropertyIds.OBJECT_TYPE_ID, "cmis:document,P:cm:titled");
			}
		}

		// Add the name if it wasn't already
		if (documentProperties.get(PropertyIds.NAME) == null) {
			documentProperties.put(PropertyIds.NAME, fileName);
		}

		Folder folder = getCmisFolder(folderId);
		Document document = null;

		/*
		 * check if document already exists
		 */
		String folderPath = folder.getPath();
		if (!folderPath.endsWith("/")) {
			folderPath += "/";
		}
		try {
			document = (Document) cmisSession.getObjectByPath(folderPath + documentProperties.get(PropertyIds.NAME));
		}
		catch (CmisObjectNotFoundException e) {
			log.info("Document " + folderPath + documentProperties.get(PropertyIds.NAME) + " not already existing.... trying save it....");
		}
		catch (Exception e) {
			log.error("unable to create document :: " + e.getMessage(), e);
		}

		try {
			if (document == null) { // documento da creare
				ContentStream contentStream = cmisSession.getObjectFactory().createContentStream(fileName, content.length, fileType, new ByteArrayInputStream(content));

				VersioningState versioningStateVal = VersioningState.MAJOR;
				if (versioningState.equals(VersioningState.CHECKEDOUT)) {
					versioningStateVal = VersioningState.CHECKEDOUT;
				}
				else if (versioningState.equals(VersioningState.MINOR)) {
					versioningStateVal = VersioningState.MINOR;
				}
				else if (versioningState.equals(VersioningState.NONE)) {
					versioningStateVal = VersioningState.NONE;
				}
				document = folder.createDocument(documentProperties, contentStream, versioningStateVal);
				if(contentStream.getStream()!=null) {
					try {
						contentStream.getStream().close();
					} catch (IOException e) {}
				}
				if(document!=null) {
					log.info("document created with id " + document.getId());
				}else{
					log.error("created document is null");
				}

			}
			else { // documento eventualmente da aggiornare
				if (updateIfExists) {
					if (!document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
						document.cancelCheckOut();
						document.refresh();
					}
					if (document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
						document.refresh();
						ObjectId idOfCheckedOutDocument = document.checkOut();
						Document pwc = (Document) cmisSession.getObject(idOfCheckedOutDocument);
						ContentStream contentStream = cmisSession.getObjectFactory().createContentStream(fileName, content.length, fileType, new ByteArrayInputStream(content));
						
						ObjectId objectId = null;
						try {
							objectId = pwc.checkIn(false, null, contentStream, "document changed on " + (new Date()).toString());
						}
						catch (CmisStorageException e) {
							// L'operazione di update potrebbe essere troppo veloce, effettuo un secondo tentativo
							Thread.sleep(1500);
							objectId = pwc.checkIn(false, null, contentStream, "document changed on " + (new Date()).toString());
						}
						
						document = (Document) cmisSession.getObject(objectId);
						if(contentStream!=null && contentStream.getStream()!=null) {
							try {
								contentStream.getStream().close();
							} catch (IOException e) {}
						}
						log.info("Version label of "+document.getName()+" is now:" + document.getVersionLabel());
					}else {
						log.error("documento non aggiornato in quanto azione CAN_CHECK_OUT non era disponibile");
						throw new CmisServiceException("Impossibile aggiornare il file. Operazione di Check Out non disponibile. Riprovare.");
					}
				}else {
					log.info("documento non aggiornato in quanto non previsto dal flag updateIfExists");
				}
			}
		}
		catch (CmisContentAlreadyExistsException e) {
			throw new CmisServiceException(CmisServiceErrorCode.DOCUMENT_ALREADY_EXIST);
		}
		catch (Exception e) {
			log.error("unable to create document :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}
		if (document != null && document.getId() != null && !document.getId().isEmpty()) {
			cmisDocumentDTO = getDocumentDto(document);
		}else {
			throw new CmisServiceException(CmisServiceErrorCode.OBJECT_NOT_FOUND);
		}
		return cmisDocumentDTO;
	}
	
	
	@Override
	public void createAssociation(String associationType, String sourceNodeId, String targetNodeId) 
			throws CmisServiceException {
		
		Session cmisSession = getLoginSession();
		
		try {
			// Controllo se l'associazione risulta esistente
	        ObjectType typeDefinition = cmisSession.getTypeDefinition(associationType);
	        OperationContext operationContext = cmisSession.createOperationContext();

	        ItemIterable<Relationship> check = cmisSession.getRelationships(
	        		cmisSession.createObjectId(sourceNodeId), true,
	        		RelationshipDirection.EITHER,
	                typeDefinition, operationContext);
			
			if (check != null) {
				for (Relationship relationship : check) {
					if (relationship.getSourceId().getId().equals(sourceNodeId) &&
						relationship.getTargetId().getId().equals(targetNodeId)) {
						
						log.warn("La seguente relazione risulta esistente: " + associationType + " - " + 
							relationship.getSourceId().getId() + " -> " + relationship.getTargetId().getId());
						return;
					}
				}
			}
			
			Map<String, Serializable> relProps = new HashMap<String, Serializable>(); 
		    relProps.put(PropertyIds.SOURCE_ID, sourceNodeId); 
		    relProps.put(PropertyIds.TARGET_ID, targetNodeId); 
		    relProps.put(PropertyIds.OBJECT_TYPE_ID, associationType);
		    cmisSession.createRelationship(relProps, null, null, null);
		}
		catch (Exception e) {
			log.error("unable to create association :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}
	}
	

	@Override
	public CmisDocumentDTO updateDocument(String nodeId, final File file, String fileType, Map<String, Object> documentProperties) throws CmisServiceException {
		CmisDocumentDTO cmisDocumentDTO = null;
		Session cmisSession = getLoginSession();
		String fileName = file.getName();

		Document document = (Document) cmisSession.getObject(cmisSession.createObjectId(nodeId));

		try {
			if (!document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
				document.cancelCheckOut();
				document.refresh();
			}
			if (document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
				document.refresh();
				ObjectId idOfCheckedOutDocument = document.checkOut();
				Document pwc = (Document) cmisSession.getObject(idOfCheckedOutDocument);
				ContentStream contentStream = cmisSession.getObjectFactory().createContentStream(fileName, file.length(), fileType, new FileInputStream(file));
				ObjectId objectId = pwc.checkIn(false, documentProperties, contentStream, "document changed on " + (new Date()).toString());
				document = (Document) cmisSession.getObject(objectId);
				if(contentStream!=null && contentStream.getStream()!=null) {
					try {
						contentStream.getStream().close();
					} catch (IOException e) {}
				}
				log.debug("Version label is now:" + document.getVersionLabel());

				if (document != null) {
					cmisDocumentDTO = getDocumentDto(document);
				}
			}
		}
		catch (FileNotFoundException e) {
			log.error("unable to update document :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return cmisDocumentDTO;
	}

	@Override
	public CmisDocumentDTO moveDocument(String documentId, String sourceFolderId, String targetFolderId) throws CmisServiceException {
		CmisDocumentDTO cmisDocumentDTO = null;
		Session cmisSession = getLoginSession();

		Document document = (Document) cmisSession.getObject(cmisSession.createObjectId(documentId));

		try {
			document.move(cmisSession.createObjectId(sourceFolderId), cmisSession.createObjectId(targetFolderId));

			if (document.getAllowableActions().getAllowableActions().contains(org.apache.chemistry.opencmis.commons.enums.Action.CAN_CHECK_OUT)) {
				document.refresh();
				document = (Document) cmisSession.getObject(document.getId());

				log.debug("Version label is now:" + document.getVersionLabel());

				if (document != null) {
					cmisDocumentDTO = getDocumentDto(document);
				}
			}
		}
		catch (Exception e) {
			log.error("unable to move document :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return cmisDocumentDTO;
	}

	@Override
	public void deleteDocument(String nodeId) throws CmisServiceException {
		Session cmisSession = getLoginSession();
		Document document = (Document) cmisSession.getObject(cmisSession.createObjectId(nodeId));
		
		try {
			cmisSession.delete(document);
		}
		catch(CmisObjectNotFoundException ne) {
			log.warn("L'oggetto da eliminare non risulta presente.");
		}
	}

	@Override
	public List<CmisFolderDTO> findFolders(final String rootFolderId, final List<CmisMetadataDTO> searchMetadata, final boolean recursively) throws CmisServiceException {
		return findFolders(rootFolderId, searchMetadata, recursively, false);
	}

	@Override
	public List<CmisFolderDTO> findFolders(final String rootFolderId, final List<CmisMetadataDTO> searchMetadata, final boolean recursively, final boolean includeSystemFolder)
			throws CmisServiceException {
		String statement = "";

		statement = "SELECT * FROM cmis:folder";

		if (rootFolderId != null && !rootFolderId.isEmpty()) {
			if (recursively) {
				statement += " WHERE in_tree('" + rootFolderId + "')";
			}
			else {
				statement += " WHERE in_folder('" + rootFolderId + "')";
			}
		}

		// add where for metadata TODO
		if (searchMetadata != null) {
			for (@SuppressWarnings("unused")
			CmisMetadataDTO cmisMetadataDTO : searchMetadata) {
				// statement += "
				// +@"+alfrescoMetadataDTO.getPrefix()+"\\:"+alfrescoMetadataDTO.getName()+":\""+alfrescoMetadataDTO.getValue()+"\"";
				statement += "";
			}
		}

		// log.info("CmisService :: findByFolderMetadata(folderId, searchMetadata, recursively) ::
		// statement: " + statement);

		return findFolderByStatement(statement, includeSystemFolder);
	}

	/* 
	 * NON PREVISTI IN ATTICO
	 *
	@Override
	public List<CmisDocumentDTO> findByFolder(String folderId) throws CmisServiceException {
		return findByFolder(folderId, false);
	}

	@Override
	public List<CmisDocumentDTO> findByFolder(String folderId, boolean recursively) throws CmisServiceException {
		return findByFolderMetadata(folderId, null, recursively);
	}
	*/

	@Override
	public List<String> findByFolderMetadata(final String folderId, final List<CmisMetadataDTO> searchMetadata) throws CmisServiceException {
		return findByFolderMetadata(folderId, searchMetadata, false);
	}

	@Override
	public List<String> findByFolderMetadata(final String folderId, final List<CmisMetadataDTO> searchMetadata, final boolean recursively) throws CmisServiceException {
		String statement = "";

		statement = "SELECT * FROM cmis:document AS d";

		if (searchMetadata != null) {
			for (CmisMetadataDTO cmisMetadataDTO : searchMetadata) {
				if (cmisMetadataDTO.getCustomType() != null) {
					String joinTableName = cmisMetadataDTO.getCustomType().substring(cmisMetadataDTO.getCustomType().lastIndexOf(":") + 1);
					statement += " JOIN " + cmisMetadataDTO.getCustomType() + " AS " + joinTableName + " ON d.cmis:objectId = " + joinTableName + ".cmis:objectId";
				}
			}
		}

		if (folderId != null && !folderId.isEmpty()) {
			if (recursively) {
				statement += " WHERE in_tree(d, '" + folderId + "')";
			}
			else {
				statement += " WHERE in_folder(d, '" + folderId + "')";
			}
		}

		// add where for metadata
		if (searchMetadata != null) {
			if (!statement.contains("WHERE")) {
				statement += " WHERE ";
			}
			else {
				statement += " AND ";
			}
			for (int i = 0; i < searchMetadata.size(); i++) {
				if (i != 0) {
					statement += " AND ";
				}

				CmisMetadataDTO cmisMetadataDTO = searchMetadata.get(i);
				if (cmisMetadataDTO.getCustomType() != null) {
					String joinTableName = cmisMetadataDTO.getCustomType().substring(cmisMetadataDTO.getCustomType().lastIndexOf(":") + 1);
					if (joinTableName != null) {
						statement += joinTableName + ".";
					}
				}

				if (cmisMetadataDTO.getPrefix() != null) {
					statement += cmisMetadataDTO.getPrefix() + ":";
				}
				statement += cmisMetadataDTO.getName();
				statement += " = ";

				// Controllo data
				if (cmisMetadataDTO.getType() != null && cmisMetadataDTO.getType().equals(PropertyType.DATETIME.value())) {
					statement += " TIMESTAMP ";
				}

				statement += "'" + cmisMetadataDTO.getValue() + "'";
			}
		}

		return findDocumentByStatement(statement);
	}

	/* 
	 * NON PREVISTI IN ATTICO
	 *
	@Override
	public List<CmisDocumentDTO> findByNodeId(List<String> nodeIdList) throws CmisServiceException {
		List<CmisDocumentDTO> documentList = null;
		if (nodeIdList != null && nodeIdList.size() > 0) {
			documentList = new ArrayList<CmisDocumentDTO>();
			for (String nodeId : nodeIdList) {
				try {
					CmisDocumentDTO doc = getDocument(nodeId);
					if (doc != null) {
						documentList.add(doc);
					}
				}
				catch (CmisObjectNotFoundException e) {
					log.warn("CmisService :: findByNodeId() :: Document not found: " + nodeId);
				}
				catch (Exception e) {
					log.error("unable to find documents by node id :: " + e.getMessage(), e);
				}
			}
		}
		return documentList;
	}

	@Override
	public List<CmisDocumentDTO> findByTextNodes(String text, List<String> nodeIdList) throws CmisServiceException {
		return findByNameTextNodes(null, text, nodeIdList);
	}

	@Override
	public List<CmisDocumentDTO> findByNameTextNodes(String name, String text, List<String> nodeIdList) throws CmisServiceException {
		List<CmisDocumentDTO> documentList = null;
		if ((text != null && !text.isEmpty()) || (nodeIdList != null && nodeIdList.size() > 0)) {
			documentList = new ArrayList<CmisDocumentDTO>();

			// ricavo i documenti dalla ricerca fulltext e sul nome
			List<CmisDocumentDTO> filteredDocs = null;

			String statement = null;

			if (text != null && !text.isEmpty()) {
				statement = "SELECT * FROM cmis:document WHERE CONTAINS('" + text + "')";
			}

			if (statement != null && !statement.isEmpty() && name != null && !name.isEmpty()) {
				statement += " AND cmis:name LIKE '%" + name + "%'";
			}
			else if ((statement == null || statement.isEmpty()) && name != null && !name.isEmpty()) {
				statement = "SELECT * FROM cmis:document WHERE cmis:name LIKE '%" + name + "%'";
			}

			if (statement != null && !statement.isEmpty()) {
				filteredDocs = findDocumentByStatement(statement);
			}

			// ricavo i documenti dalla clausola sui nodeIds
			List<CmisDocumentDTO> nodeIdDocList = null;
			if (nodeIdList != null && nodeIdList.size() > 0) {
				nodeIdDocList = findByNodeId(nodeIdList);
				// if(!statement.endsWith("WHERE")) {
				// statement += " AND ";
				// }
				// statement += " cmis:objectId IN (";
				// String csvNodes = "";
				// for(String nodeId : nodeIdList) {
				// if(nodeId!=null && !nodeId.isEmpty()) { // in caso di liste in ingresso corrotte
				// perchè contenenti dati sporchi....
				// if(!csvNodes.isEmpty()) {
				// csvNodes += ", ";
				// }
				// csvNodes += "'" + nodeId + "'";
				// }
				// }
				// statement += csvNodes + " )";
			}

			// documentList = findByStatement(statement);

			if (((text != null && !text.isEmpty()) || (name != null && !name.isEmpty())) && (nodeIdDocList == null || nodeIdDocList.isEmpty()) && filteredDocs != null && !filteredDocs.isEmpty()) {
				documentList.addAll(filteredDocs);
			}
			else if ((text == null || text.isEmpty()) && (name == null || name.isEmpty()) && nodeIdDocList != null && !nodeIdDocList.isEmpty()) {
				documentList.addAll(nodeIdDocList);
			}
			else if (filteredDocs != null && !filteredDocs.isEmpty() && nodeIdDocList != null && !nodeIdDocList.isEmpty()) {
				
				// prendo solo gli elementi comuni a tutte e due le liste
				for (CmisDocumentDTO d1 : filteredDocs) {
					for (CmisDocumentDTO d2 : nodeIdDocList) {
						if (d1.getId().equals(d2.getId())) {
							documentList.add(d1);
							break;
						}
					}
				}
			}
		}
		return documentList;
	}
	*/

	@Override
	public List<CmisFolderDTO> findFolderByStatement(String statement, boolean includeSystemFolder) throws CmisServiceException {
		List<CmisFolderDTO> results = new ArrayList<CmisFolderDTO>();

		try {
			Session cmisSession = getLoginSession();

			OperationContext context = cmisSession.createOperationContext();
			context.setMaxItemsPerPage(maxItemsPerPage);

			// execute the query
			ItemIterable<QueryResult> queryResults = cmisSession.query(statement, false, context);

			for (QueryResult qResult : queryResults) {
				/*
				 * Verifico se si tratta di cartella di sistema
				 */
				String objectTypeId = qResult.getPropertyValueById(PropertyIds.OBJECT_TYPE_ID);

				if (!includeSystemFolder) {
					if (objectTypeId.equals("F:cm:systemfolder")) {
						continue; // non riporto la folder tra i risultati
					}
				}

				/*
				 * Aggiungo la folder ai risultati di ricerca
				 */
				String objectId = qResult.getPropertyValueById(PropertyIds.OBJECT_ID);

				Folder folder = (Folder) cmisSession.getObject(cmisSession.createObjectId(objectId));

				CmisFolderDTO cmisFolderDTO = CmisFolderConverter.convertToDto(folder);

				results.add(cmisFolderDTO);
			}
		}
		catch (Exception serviceException) {
			log.error(serviceException.getMessage());
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return results;
	}

	@Override
	public int countDocumentByStatement(String statement) throws CmisServiceException {
		int count = 0;

		try {
			Session cmisSession = getLoginSession();

			OperationContext context = cmisSession.createOperationContext();
			context.setMaxItemsPerPage(maxItemsPerPage);

			// execute the query
			ItemIterable<QueryResult> queryResults = cmisSession.query(statement, false, context);
			// count = queryResults.getTotalNumItems();

			for (@SuppressWarnings("unused")
			QueryResult qResult : queryResults) {
				count++;
			}
		}
		catch (Exception serviceException) {
			log.error(serviceException.getMessage());
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return count;
	}

	@Override
	public List<String> findDocumentByStatement(String statement) throws CmisServiceException {
		return searchDocumentByStatement(statement, 0, 0);
	}

	/* 
	 * NON PREVISTI IN ATTICO
	 *
	@Override
	public List<CmisDocumentDTO> findDocumentByStatement(String statement, int itemsPerPage, int page) throws CmisServiceException {
		return searchDocumentByStatement(statement, itemsPerPage, page);
	}
	*/

	private List<String> searchDocumentByStatement(String statement, int itemsPerPage, int page) throws CmisServiceException {
		List<String> results = new ArrayList<String>();

		try {
			Session cmisSession = getLoginSession();

			OperationContext context = cmisSession.createOperationContext();
			context.setMaxItemsPerPage(maxItemsPerPage);

			// execute the query
			ItemIterable<QueryResult> queryResults = cmisSession.query(statement, false, context);

			if (page > 0 && itemsPerPage > 0) {
				queryResults = queryResults.skipTo((page - 1) * itemsPerPage).getPage(itemsPerPage);
			}

			for (QueryResult qResult : queryResults) {
				String objectId = qResult.getPropertyValueById(PropertyIds.OBJECT_ID);
//				Document doc = (Document) cmisSession.getObject(cmisSession.createObjectId(objectId));
//
//				CmisDocumentDTO cmisDocumentDTO = CmisDocumentConverter.convertToDto(doc, cmisHost);
//
//				ObjectData bindingParent = cmisSession.getBinding().getNavigationService().getFolderParent(cmisSession.getRepositoryInfo().getId(), doc.getId(), null, null);
//				cmisDocumentDTO.setFolderId(bindingParent.getId());

//				results.add(cmisDocumentDTO);
				results.add(objectId);
			}
		}
		catch (Exception serviceException) {
			log.error(serviceException.getMessage(), serviceException);
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return results;
	}

	// TODO per count: provare a inserire un filtro a posteriori come parametro nel metodo
	/**
	 * Restituisce una mappa in cui: chiave = nodeId del documento valore = mappa di metadati
	 * (metadato, valore)
	 *
	 * @param statement
	 * @param itemsPerPage
	 * @param page
	 * @return
	 * @throws CmisServiceException
	 */
	@Override
	public Map<String, Map<String, Object>> searchDocumentPropertiesByStatement(String statement, int itemsPerPage, int page, List<String> metadataToExtract) throws CmisServiceException {
		Map<String, Map<String, Object>> objectMap = null;

		try {
			Session cmisSession = getLoginSession();

			OperationContext context = cmisSession.createOperationContext();
			context.setMaxItemsPerPage(maxItemsPerPage);

			// execute the query
			ItemIterable<QueryResult> queryResults = cmisSession.query(statement, false, context);

			if (page > 0 && itemsPerPage > 0) {
				queryResults = queryResults.skipTo((page - 1) * itemsPerPage).getPage(itemsPerPage);
			}

			if (queryResults != null) {
				objectMap = new HashMap<String, Map<String, Object>>();
			}

			for (QueryResult qResult : queryResults) {
				String objectId = qResult.getPropertyValueById(PropertyIds.OBJECT_ID);
				Map<String, Object> metadataMap = new HashMap<String, Object>();
				for (String metadata : metadataToExtract) {
					Object val = qResult.getPropertyValueById(metadata);
					metadataMap.put(metadata, val);
				}
				objectMap.put(objectId, metadataMap);
			}
		}
		catch (Exception serviceException) {
			log.error(serviceException.getMessage());
			throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
		}

		return objectMap;
	}

	// /**
	// * Get a list of ordered results of documents in the space specified matching the search
	// * text provided.
	// *
	// * @param spaceName the name of the space (immediatly beneth the company home space) to search
	// * @param searchValue the FTS search value
	// * @return list of results
	// */
	// public List<CmisDocumentDTO> getRankedContent(String spaceName, String searchValue) {
	// Map<String, String> metadata = new HashMap<String, String>();
	// metadata.put("name", searchValue);
	// return findByMetadata(spaceName, metadata);
	// }

	@Override
	public byte[] getDocumentContent(final String documentId) throws CmisServiceException {
		byte[] fileContent = null;

		Session cmisSession = getLoginSession();

		CmisObject object = cmisSession.getObject(cmisSession.createObjectId(documentId));
		Document document = (Document) object;
		// String filename = document.getName();
		try {
			fileContent = IOUtils.toByteArray(document.getContentStream().getStream());
		}
		catch (IOException e) {
			log.error(e.getMessage());
			throw new CmisServiceException(CmisServiceErrorCode.DOCUMENT_CONTENT_IO_EXCEPTION);
		}

		return fileContent;
	}

	// public byte[] getThumbnail(String nodeId) throws CmisServiceException {
	// byte[] content = null;
	//
	//
	// /*
	// * LOGIN
	// */
	// String baseDomaniUrl = this.hostNameCmis.replace("http://", "");
	// baseDomaniUrl = baseDomaniUrl.indexOf(":")>=0 ? baseDomaniUrl.substring(0,
	// baseDomaniUrl.indexOf(":")) : baseDomaniUrl;
	//
	// CredentialsProvider credsProvider = new BasicCredentialsProvider();
	// credsProvider.setCredentials(
	// new AuthScope(baseDomaniUrl, 8080),
	// new UsernamePasswordCredentials(this.cmisUsername, this.cmisPassword));
	// CloseableHttpClient httpclient = HttpClients.custom()
	// .setDefaultCredentialsProvider(credsProvider)
	// .build();
	//
	//// client.setCredentials(
	////
	//// new AuthScope("localhost", 8080, "Cmis"),
	////
	//// new UsernamePasswordCredentials("admin", "admin"));
	//
	//
	//
	//// // add request header
	//// request.addHeader("User-Agent", USER_AGENT);
	//// StringBuffer result = null;
	// String alfTicket = "";
	// try {
	//// String input = "{ \"username\" : \"" + this.cmisUsername +"\", \"password\" : \"" +
	// this.cmisPassword +"\" }";
	//// method.setRequestEntity(new StringRequestEntity(input,"application/json", null));
	// HttpGet request = new HttpGet(this.hostNameCmis +
	// "/alfresco/service/api/login"+"?u="+this.cmisUsername+"&pw="+ this.cmisPassword);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	// log.debug("LOGIN Response Code : " + response.getStatusLine().getStatusCode());
	//
	// BufferedReader rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	//// content = IOUtils.toByteArray(new InputStreamReader(response.getEntity().getContent()));
	//
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	//
	// log.debug(result.toString());
	// alfTicket = result.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ticket>",
	// "").replace("</ticket>", "");
	// log.debug("alfTicket:"+alfTicket);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	//// // add request header
	//// request.addHeader("User-Agent", USER_AGENT);
	//// StringBuffer result = null;
	// try {
	//// String url = this.hostNameCmis +
	// "/share/proxy/alfresco/api/node/workspace/SpacesStore/"+nodeId+"/content/thumbnails/doclib?c=queue&ph=true&lastModified=doclib:1470295509342";
	// String url = this.hostNameCmis +
	// "/alfresco/service/api/node/workspace/SpacesStore/"+nodeId+"/content/thumbnails/doclib?c=force&ph=true&alf_ticket="+alfTicket+"&u="+this.cmisUsername+"&pw="+
	// this.cmisPassword;
	// log.debug("url:"+url);
	// HttpGet request = new HttpGet(url);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	// log.debug("Response Code : " + response.getStatusLine().getStatusCode());
	//
	//// BufferedReader rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// entity = new BufferedHttpEntity(entity);
	// }
	// content = IOUtils.toByteArray(entity.getContent());
	//
	//// result = new StringBuffer();
	//// String line = "";
	//// while ((line = rd.readLine()) != null) {
	//// result.append(line);
	//// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	//// JsonReader jsonReader = new JsonReader(new StringReader(nodeJson));
	//// TypeAdapter<JsonElement> jsonElementTypeAdapter = gson.getAdapter(JsonElement.class);
	//// JsonObject jsonObject = (JsonObject) jsonElementTypeAdapter.read(jsonReader);
	//// JsonElement routes = jsonObject.get("parentNodeRef");
	//// parentNodeRef = routes.getAsString();
	//// log.info("CmisService :: getParentNodeRef() :: parentNodeRef:"+parentNodeRef);
	//
	// return content;
	// }
	//

	// public byte[] getPreview(String nodeId) throws CmisServiceException {
	// byte[] content = null;
	//
	// /*
	// * LOGIN
	// */
	// String baseDomaniUrl = this.hostNameCmis.replace("http://", "");
	// baseDomaniUrl = baseDomaniUrl.indexOf(":")>=0 ? baseDomaniUrl.substring(0,
	// baseDomaniUrl.indexOf(":")) : baseDomaniUrl;
	//
	// CredentialsProvider credsProvider = new BasicCredentialsProvider();
	// credsProvider.setCredentials(
	// new AuthScope(baseDomaniUrl, 8080),
	// new UsernamePasswordCredentials(this.cmisUsername, this.cmisPassword));
	// CloseableHttpClient httpclient = HttpClients.custom()
	// .setDefaultCredentialsProvider(credsProvider)
	// .build();
	//
	// String alfTicket = "";
	// try {
	// HttpGet request = new HttpGet(this.hostNameCmis +
	// "/alfresco/service/api/login"+"?u="+this.cmisUsername+"&pw="+ this.cmisPassword);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	// log.debug("LOGIN Response Code : " + response.getStatusLine().getStatusCode());
	//
	// BufferedReader rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	//
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	//
	// log.debug(result.toString());
	// alfTicket = result.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ticket>",
	// "").replace("</ticket>", "");
	// log.debug("alfTicket:"+alfTicket);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	//// String url = this.hostNameCmis +
	// "/share/proxy/alfresco/api/node/workspace/SpacesStore/"+nodeId+"/content/thumbnails/doclib?c=queue&ph=true&lastModified=doclib:1470295509342";
	// String url = this.hostNameCmis +
	// "/alfresco/service/api/node/workspace/SpacesStore/"+nodeId+"/content/thumbnails/imgpreview?c=force&lastModified=imgpreview&ph=true&alf_ticket="+alfTicket+"&u="+this.cmisUsername+"&pw="+
	// this.cmisPassword;
	// log.debug("url:"+url);
	// HttpGet request = new HttpGet(url);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	// log.debug("Response Code : " + response.getStatusLine().getStatusCode());
	//
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// entity = new BufferedHttpEntity(entity);
	// }
	// content = IOUtils.toByteArray(entity.getContent());
	//
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return content;
	// }

	// /**
	// * Restituisce l'elenco di categorie salvate su alfresco
	// * figlie della categoria passata in ingresso.
	// * <BR />
	// * Se uuid è null restituisce tutte le categorie di primo livello.
	// *
	// *
	// * @param uuid id della categoria
	// * @return
	// * @throws CmisServiceException
	// */
	// public List<CmisCategoryDTO> getCategories(final String uuid) throws CmisServiceException {
	// List<CmisCategoryDTO> categories = null;
	//
	// /*
	// * LOGIN
	// */
	// String baseDomaniUrl = this.hostNameCmis.replace("http://", "");
	// baseDomaniUrl = baseDomaniUrl.indexOf(":")>=0 ? baseDomaniUrl.substring(0,
	// baseDomaniUrl.indexOf(":")) : baseDomaniUrl;
	//
	// CredentialsProvider credsProvider = new BasicCredentialsProvider();
	// credsProvider.setCredentials(
	// new AuthScope(baseDomaniUrl, 8080),
	// new UsernamePasswordCredentials(this.cmisUsername, this.cmisPassword));
	// CloseableHttpClient httpclient = HttpClients.custom()
	// .setDefaultCredentialsProvider(credsProvider)
	// .build();
	//
	// String alfTicket = "";
	// try {
	// HttpGet request = new HttpGet(this.hostNameCmis +
	// "/alfresco/service/api/login"+"?u="+this.cmisUsername+"&pw="+ this.cmisPassword);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	//// log.debug("LOGIN Response Code : " + response.getStatusLine().getStatusCode());
	//
	// BufferedReader rd = new BufferedReader(new
	// InputStreamReader(response.getEntity().getContent()));
	//
	// StringBuffer result = new StringBuffer();
	// String line = "";
	// while ((line = rd.readLine()) != null) {
	// result.append(line);
	// }
	//
	//// log.info(result.toString());
	// alfTicket = result.toString().replace("<?xml version=\"1.0\" encoding=\"UTF-8\"?><ticket>",
	// "").replace("</ticket>", "");
	//// log.info("alfTicket:"+alfTicket);
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// try {
	// String url = null;
	// if(uuid==null || uuid.isEmpty()) {
	// url = this.hostNameCmis +
	// "/alfresco/service/api/forms/picker/category/alfresco/category/root/children?alf_ticket="+alfTicket+"&u="+this.cmisUsername+"&pw="+
	// this.cmisPassword;
	// } else {
	// url = this.hostNameCmis +
	// "/alfresco/service/api/forms/picker/category/workspace/SpacesStore/"+uuid+"/children?alf_ticket="+alfTicket+"&u="+this.cmisUsername+"&pw="+
	// this.cmisPassword;
	// }
	//// log.info("url:"+url);
	// HttpGet request = new HttpGet(url);
	//
	// HttpResponse response = httpclient.execute(request);
	//
	//// log.debug("Response Code : " + response.getStatusLine().getStatusCode());
	//
	// HttpEntity entity = response.getEntity();
	// if (entity != null) {
	// entity = new BufferedHttpEntity(entity);
	// }
	//// content = IOUtils.toByteArray(entity.getContent());
	// String jsonCategories = IOUtils.toString(entity.getContent());
	//// log.debug("CmisServiceImpl :: getCatgeories() :: jsonCategories: "+jsonCategories);
	// Gson gson = new Gson();
	// CmisCategoryDTO.CategoryMain c = gson.fromJson(jsonCategories,
	// CmisCategoryDTO.CategoryMain.class);
	// if(c!=null && c.getData()!=null) {
	// CmisCategoryDTO[] catArray = c.getData().getItems();
	// if(catArray!=null) {
	// categories = Arrays.asList(catArray);
	// }
	// }
	// } catch (ClientProtocolException e) {
	// e.printStackTrace();
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	//
	// return categories;
	// }

	@Override
	public Map<String, String> getMetadataSet(final String uuid) throws CmisServiceException {
		Map<String, String> metadataSet = new HashMap<String, String>();

		// TODO

		return metadataSet;
	}

	@Override
	public List<String> getAspects(final String uuid) throws CmisServiceException {
		List<String> lista = new ArrayList<String>();

		// TODO
		// Node nodo = getNode(uuid);
		// if(nodo!=null) {
		// String[] aspects = nodo.getAspects();
		//
		// for (int i = 0; i < aspects.length; i++) {
		// lista.add(aspects[i]);
		// }
		// } else {
		// log.error("Non è stato trovato nessun nodo con nodeId: "+uuid);
		// }

		return lista;
	}

	@Override
	public void addAspect(final String nodeId, List<String> aspects) throws CmisServiceException {
		 if(nodeId!=null && !nodeId.isEmpty() && aspects!= null && !aspects.isEmpty()) {
			Session cmisSession = getLoginSession();
			CmisObject cmisObject = cmisSession.getObject(cmisSession.createObjectId(nodeId));
			
			List<Object> aspectsOrig = cmisObject.getProperty(
					 PropertyIds.SECONDARY_OBJECT_TYPE_IDS).getValues();
			
			for (String aspectName : aspects) {
				if (!aspectsOrig.contains(aspectName)) {
					aspectsOrig.add(aspectName);
				}
			}
			 
			Map<String, Object> properties = new HashMap<String, Object>();
            properties.put(PropertyIds.SECONDARY_OBJECT_TYPE_IDS, aspectsOrig);
			 
			cmisObject.updateProperties(properties);
		 }
	}

	@Override
	public CmisDocumentDTO getDocument(String nodeId, 
			boolean withContent, boolean withAttachments, 
			String attachmentAssociationType) throws CmisServiceException {
		
		CmisDocumentDTO cmisDocumentDTO = null;

		if (nodeId != null && !nodeId.isEmpty()) {
			Session cmisSession = getLoginSession();
			Document doc = null;
			try {
				doc = (Document) cmisSession.getObject(cmisSession.createObjectId(nodeId));
			}
			catch (CmisObjectNotFoundException e) {
				throw new CmisServiceException(CmisServiceErrorCode.OBJECT_NOT_FOUND);
			}
			catch (Exception e) {
				log.error("unable to get document :: " + e.getMessage(), e);
				throw new CmisServiceException(CmisServiceErrorCode.UNABLE_TO_PERFORM_ACTION);
			}
			if (doc != null && doc.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
				cmisDocumentDTO = CmisDocumentConverter.convertToDto(cmisSession, doc, cmisHost, 
						withContent, withAttachments, attachmentAssociationType);
			}
			ObjectData bindingParent = cmisSession.getBinding().getNavigationService()
					.getFolderParent(cmisSession.getRepositoryInfo().getId(), doc.getId(), null, null);
			cmisDocumentDTO.setFolderId(bindingParent.getId());
		}

		return cmisDocumentDTO;
	}
	
	@Override
	public CmisDocumentDTO getDocumentDto(Document doc) throws CmisServiceException {
		CmisDocumentDTO cmisDocumentDTO = null;
		Session cmisSession = getLoginSession();
		if (doc != null && doc.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
			cmisDocumentDTO = CmisDocumentConverter.convertToDto(null, doc, cmisHost, false, false, null);
			ObjectData bindingParent = cmisSession.getBinding().getNavigationService()
					.getFolderParent(cmisSession.getRepositoryInfo().getId(), doc.getId(), null, null);
			cmisDocumentDTO.setFolderId(bindingParent.getId());
		}

		return cmisDocumentDTO;
	}

	@Override
	public CmisDocumentDTO getDocumentByPath(String documentFullPath, 
			boolean withContent, boolean withAttachments, 
			String attachmentAssociationType) throws CmisServiceException {
		CmisDocumentDTO cmisDocumentDTO = null;

		if (documentFullPath != null && !documentFullPath.isEmpty()) {
			Session cmisSession = getLoginSession();
			Document document = (Document) cmisSession.getObjectByPath(documentFullPath);
			if (document != null && document.getBaseTypeId().equals(BaseTypeId.CMIS_DOCUMENT)) {
				cmisDocumentDTO = CmisDocumentConverter.convertToDto(cmisSession, document, cmisHost, 
						withContent, withAttachments, attachmentAssociationType);
			}
		}

		return cmisDocumentDTO;
	}

	@Override
	public CmisDocumentDTO updateDocumentMetadata(final String nodeId, final Map<String, Object> updateProperties) throws CmisServiceException {
		CmisObject cmisobject = updateMetadata(nodeId, updateProperties);
		Document d = (Document) cmisobject;
		return CmisDocumentConverter.convertToDto(getLoginSession(), d, cmisHost, false, false, null);
	}

	@Override
	public CmisFolderDTO updateFolderMetadata(final String nodeId, final Map<String, Object> updateProperties) throws CmisServiceException {
		CmisObject cmisobject = updateMetadata(nodeId, updateProperties);
		Folder f = (Folder) cmisobject;
		return CmisFolderConverter.convertToDto(f);
	}

	private CmisObject updateMetadata(final String nodeId, final Map<String, Object> updateProperties) throws CmisServiceException {
		CmisObject cmisobject = getLoginSession().getObject(nodeId);

		return cmisobject.updateProperties(updateProperties);
	}

	private static synchronized Session getLoginSession() throws CmisServiceException {
		// Ottimizzo un po' le richieste di login
		if (Math.abs(System.currentTimeMillis() - lastSessionTime) < 60000) {
			return SessionStore.INSTANCE.getSession();
		}

		log.info("CmisServiceImpl :: getLoginSession() :: cmis session NOT INITIALIZED or EXPIRED!");
		try {
			LoginService loginService = LoginFactory.getLoginFactory().getLoginService();
			// create session
			SessionStore.INSTANCE.setSession(loginService.getLoginSession());
			
			if(SessionStore.INSTANCE.getSession()!=null && SessionStore.INSTANCE.getSession().getDefaultContext() !=null){
				SessionStore.INSTANCE.getSession().getDefaultContext().setCacheEnabled(false);
			}
			
			lastSessionTime = System.currentTimeMillis();

			return SessionStore.INSTANCE.getSession();
		}
		catch (Exception e) {
			log.error("unable to get login session :: " + e.getMessage(), e);
			throw new CmisServiceException(CmisServiceErrorCode.LOGIN_ERROR);
		}
	}

	@Override
	public CmisMetadataDTO getMetadata(CmisDocumentDTO cmisDocumentDTO, String prefix, String name) throws CmisServiceException {
		CmisMetadataDTO cmisMetadataDTO = null;
		if (cmisDocumentDTO != null && cmisDocumentDTO.getMetadata() != null && cmisDocumentDTO.getMetadata().size() > 0) {
			for (CmisMetadataDTO metadata : cmisDocumentDTO.getMetadata()) {
				if (metadata.getName().equalsIgnoreCase(name)) {
					if (prefix != null) {
						if (metadata.getPrefix().equalsIgnoreCase(prefix)) {
							cmisMetadataDTO = metadata;
						}
					}
					else {
						cmisMetadataDTO = metadata;
						break;
					}
				}
			}
		}
		return cmisMetadataDTO;
	}

}
