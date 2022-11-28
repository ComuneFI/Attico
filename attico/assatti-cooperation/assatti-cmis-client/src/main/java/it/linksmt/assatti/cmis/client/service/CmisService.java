package it.linksmt.assatti.cmis.client.service;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.Document;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;


/**
 * Servizio per l'interfacciamento con un server di alfresco 4.
 *
 * @author marco ingrosso
 *
 */
public interface CmisService {
	public CmisDocumentDTO getDocumentDto(Document doc) throws CmisServiceException;

//	/**
//	 * Restituisce l'elenco di tutte le categorie censite
//	 * nell'istanza di alfresco.
//	 * <BR />
//	 * Se uuid è null restituisce tutte le categorie di primo livello.
//	 * 
//	 * @throws CmisServiceException
//	 */
//	public List<CmisCategoryDTO> getCategories(final String uuid) throws CmisServiceException;

	/**
	 * Restituisce un elenco di folder salvate nell'istanza di ALfresco.
	 * 
	 * @param rootFolderId			root della folder di base da cui cominciare la ricerca. Se null sarà usata la root del DMS.
	 * @param searchMetadata		elenco dei metadati per affinare la ricerca.
	 * @param recursively			se true restituisce tutte le folder ricorsivamente contenute nella root folder indicata.
	 * @return
	 * @throws CmisServiceException
	 */
	public List<CmisFolderDTO> findFolders(final String rootFolderId, final List<CmisMetadataDTO> searchMetadata, final boolean recursively) throws CmisServiceException;

	/**
	 * Restituisce un elenco di folder salvate nell'istanza di ALfresco.
	 * 
	 * @param rootFolderId			root della folder di base da cui cominciare la ricerca. Se null sarà usata la root del DMS.
	 * @param searchMetadata		elenco dei metadati per affinare la ricerca.
	 * @param recursively			se true restituisce tutte le folder ricorsivamente contenute nella root folder indicata.
	 * @param includeSystemFolder	se true le cartelle di sistema saranno riportate tra i risultati di ricerca
	 * @return
	 * @throws CmisServiceException
	 */
	public List<CmisFolderDTO> findFolders(final String rootFolderId, final List<CmisMetadataDTO> searchMetadata, final boolean recursively, final boolean includeSystemFolder) throws CmisServiceException;

	/* 
	 * NON PREVISTI IN ATTICO
	 *
	 *
	/**
	 * Restituisce tutti i documenti contenuti in una folder.
	 * 
	 * @param folderId
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findByFolder(final String folderId) throws CmisServiceException;
	*/
	/**
	 * Restituisce tutti i documenti contenuti in una folder.
	 * 
	 * @param folderId
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findByFolder(final String folderId, boolean recursively) throws CmisServiceException;
	*/
	
	/**
	 * Restituisce un elenco di documenti corrispondenti ai metadati
	 * passati in input.
	 * 
	 * @param spaceName
	 * @param searchMetadata
	 * @return
	 * @throws CmisServiceException
	 */
	public List<String> findByFolderMetadata(final String folderId, final List<CmisMetadataDTO> searchMetadata) throws CmisServiceException;

	/**
	 * Restituisce un elenco di documenti corrispondenti ai metadati
	 * passati in input.
	 * 
	 * @param spaceName
	 * @param searchMetadata
	 * @return
	 * @throws CmisServiceException
	 */
	public List<String> findByFolderMetadata(final String folderId, final List<CmisMetadataDTO> searchMetadata, boolean recursively) throws CmisServiceException;
	
	/**
	 * Permette di ricercare documenti passando come input una query cmis.
	 * 
	 * @param statement
	 * @return
	 * @throws CmisServiceException
	 */
	public List<String> findDocumentByStatement(String statement) throws CmisServiceException;

	/**
	 * Restituisce una mappa in cui:<BR/>
	 * <UL>
	 * 		<LI>chiave	=	nodeId del documento</LI>
	 * 		<LI>valore	=	mappa di metadati (metadato, valore)</LI>
	 * </UL>
	 * 
	 * @param statement
	 * @param itemsPerPage
	 * @param page
	 * @param metadataToExtract
	 * @return
	 * @throws CmisServiceException
	 */
	public Map<String, Map<String, Object>> searchDocumentPropertiesByStatement(String statement, int itemsPerPage, int page, List<String> metadataToExtract) throws CmisServiceException;

	/**
	 * Permette di contare i documenti passando come input una query cmis.
	 * 
	 * @param statement
	 * @return
	 * @throws CmisServiceException
	 */
	public int countDocumentByStatement(String statement) throws CmisServiceException;

	/* 
	 * NON PREVISTI IN ATTICO
	 *
	/**
	 * Restituisce un elenco paginato di documenti passando come input una query cmis
	 * e i parametri per la paginazione.
	 * 
	 * @param statement
	 * @param itemsPerPage
	 * @param page
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findDocumentByStatement(String statement, int itemsPerPage, int page) throws CmisServiceException;
	*/
	
	/**
	 * Restituisce un elenco di documenti dato un elenco di nodeId.
	 * 
	 * @param nodeIdList
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findByNodeId(List<String> nodeIdList) throws CmisServiceException;
	*/
	/**
	 * Restituisce un elenco di documenti dati un elenco di nodeId
	 * ed un testo da cercare nel contenuto del documento in modalità
	 * full-text.
	 * 
	 * @param text
	 * @param nodeIdList
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findByTextNodes(String text, List<String> nodeIdList) throws CmisServiceException;
	*/
	/**
	 * Restituisce un elenco di documenti dati il nome del documento, un elenco di nodeId
	 * ed un testo da cercare nel contenuto del documento in modalità
	 * @param name
	 * @param text
	 * @param nodeIdList
	 * @return
	 * @throws CmisServiceException
	 *
	public List<CmisDocumentDTO> findByNameTextNodes(String name, String text, List<String> nodeIdList) throws CmisServiceException;
	*/
	
//    /**
//     * Get a list of ordered results of documents in the space specified matching the search
//     * text provided.
//     *
//     * @param spaceName     the name of the space (immediatly beneth the company home space) to search
//     * @param searchValue   the FTS search value
//     * @return              list of results
//     */
//    public List<CmisDocumentDTO> getRankedContent(String spaceName, String searchValue) {
//    	Map<String, String> metadata = new HashMap<String, String>();
//    	metadata.put("name", searchValue);
//        return findByMetadata(spaceName, metadata);
//    }

	/**
	 * Restituisce la root folder di alfresco.
	 * 
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO getRootFolder()  throws CmisServiceException;

	/**
	 * Restituisce la folder principale del dominio.<BR/>
	 * La mainFolder è definita mediante file di properties, può trovarsi
	 * in qualsiasi posizione del repository e non coincide necessariamente
	 * con la ROOT folder del repository).
	 * 
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO getMainFolder()  throws CmisServiceException;

	/**
	 * Restituisce una folder passata in input.
	 * 
	 * @param nodeId
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO getFolder(final String nodeId)  throws CmisServiceException;
	
	/**
	 * Cerca una folder e se non la trova la crea se createIfNotExist è true
	 * 
	 * @param path
	 * @param createIfNotExist
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO getFolderByPath(final String path, boolean createIfNotExist)  throws CmisServiceException;

	/**
	 * Restituisce una folder dato il suo percorso.
	 * 
	 * @param path
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO getFolderByPath(final String path)  throws CmisServiceException;

	/**
	 * Crea una nuova folder.
	 * 
	 * @param folderName
	 * @param parentFolderId
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO createFolder(String folderName, String parentFolderId) throws CmisServiceException;

	/**
	 * Crea una nuova folder usando il path specificato in input.
	 * 
	 * @param folderPath				full path from root, ex.: /Sites/mycompany/myfolder
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO createFolder(String folderPath) throws CmisServiceException;

	/**
	 * Crea una nuova folder usando il path specificato in input
	 * e assegnandoli il title e la description passate in input.
	 * 
	 * NB: in caso di path composto da più subfolders, title e description
	 * saranno assegnate solo alla ultima subfolder del path.
	 * 
	 * @param folderPath
	 * @param folderTitle
	 * @param folderDescription
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO createFolder(String folderPath, String folderTitle, String folderDescription) throws CmisServiceException;

    /**
     * Restituisce il contenuto di un documento caricato
     * in alfresco.
     * 
     * @param nodeId
     * @return
     * @throws CmisServiceException
     */
    public byte[] getDocumentContent(final String documentId) throws CmisServiceException;

//    /**
//     * Restituisce la thumbnail di una risorsa
//     * 
//     * @param nodeId
//     * @return
//     * @throws CmisServiceException
//     */
//    public byte[] getThumbnail(String nodeId) throws CmisServiceException;
//    
//    /**
//     * Restituisce la preview di una risorsa
//     * 
//     * @param nodeId
//     * @return
//     * @throws CmisServiceException
//     */
//    public byte[] getPreview(String nodeId) throws CmisServiceException;

    /**
     * Restituisce il set dei metadati di un documento.
     * 
     * @param uuid
     * @return
     * @throws CmisServiceException
     */
    public Map<String,String> getMetadataSet(final String documentId) throws CmisServiceException;

	/**
	 * Restituisce un elenco di aspects associati al documento.
	 * @param uuid
	 * @return
	 * @throws CmisServiceException
	 */
	public List<String> getAspects(final String documentId) throws CmisServiceException;

	/**
	 * Aggiunge un aspect alla risorsa (folder o document) indicata
	 * @param uuid
	 * @return
	 * @throws CmisServiceException
	 */
	public void addAspect(final String nodeId, List<String> aspects) throws CmisServiceException;

	/**
	 * Crea un documento nella folder specificata.
	 * 
	 * @param content
	 * @param fileName
	 * @param fileType
	 * @param folderId
	 * @param documentProperties
	 * @param updateIfExists
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO createDocument(final byte[] content, String fileName, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfExists) throws CmisServiceException;

	/**
	 * Crea un documento nella folder specificata.
	 * 
	 * @param content
	 * @param fileName
	 * @param fileType
	 * @param folderId
	 * @param documentProperties
	 * @param updateIfExists
	 * @param versioningState		uno dei valori definiti nell'enum VersioningState di alfresco cmis
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO createDocument(final byte[] content, String fileName, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfExists, String versioningState) throws CmisServiceException;

	/**
	 * Crea un documento nella folder specificata.
	 * 
	 * @param file
	 * @param fileType
	 * @param folderId
	 * @param documentProperties
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO createDocument(final File file, String fileType, String folderId, Map<String, Object> documentProperties)  throws CmisServiceException;

	/**
	 * Crea un documento nella folder specificata.
	 * 
	 * @param file
	 * @param fileType
	 * @param folderId
	 * @param documentProperties
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO createDocument(final File file, String fileType, String folderId, Map<String, Object> documentProperties, boolean updateIfAlreadyExists)  throws CmisServiceException;

	
	/**
	 * Crea una associazione tra due nodi
	 * 
	 * @param associationType
	 * @param sourceNodeId
	 * @param targetNodeId
	 */
	public void createAssociation(final String associationType, final String sourceNodeId, final String targetNodeId) throws CmisServiceException;
	
	/**
	 * Sposta una risorsa di alfresco da una sourceFolder ad una targetFolder
	 * 
	 * @param documentId
	 * @param sourceFolderId
	 * @param targetFolderId
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO moveDocument(String documentId, String sourceFolderId, String targetFolderId)  throws CmisServiceException;

	/**
	 * Aggiorna un documento
	 * 
	 * @param file
	 * @param fileType
	 * @param folderId
	 * @param documentProperties
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO updateDocument(String nodeId, final File file, String fileType, Map<String, Object> documentProperties)  throws CmisServiceException;

	/**
	 * Elimina un documento.
	 * 
	 * @param nodeId
	 * @return
	 * @throws CmisServiceException
	 */
	public void deleteDocument(String nodeId)  throws CmisServiceException;

	/**
	 * Restituisce un documento caricato in alfresco.
	 * 
	 * @param uuid
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO getDocument(String documentId, boolean withContent, boolean withAttachments, String attachmentAssociationType) throws CmisServiceException;

	/**
	 * Restituisce un documento caricato in alfresco.
	 * 
	 * @param uuid
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO getDocumentByPath(String documentFullPath, boolean withContent, boolean withAttachments, String attachmentAssociationType) throws CmisServiceException;

//	/**
//	 * Restituisce un nodo dato il suo uid.
//	 * 
//	 * @param uuid
//	 * @return
//	 * @throws CmisServiceException
//	 */
//	public Node getNode(final String uuid) throws CmisServiceException;

	/**
	 * Aggiorna i metadati di un documento
	 * 
	 * @param nodeId
	 * @param updateProperties
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisDocumentDTO updateDocumentMetadata(final String nodeId, final Map<String, Object> updateProperties) throws CmisServiceException;

	/**
	 * Aggiorna i metadati di una folder
	 * 
	 * @param nodeId
	 * @param updateProperties
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisFolderDTO updateFolderMetadata(final String nodeId, final Map<String, Object> updateProperties) throws CmisServiceException;

	/**
	 * Cerca il metadato desiderato tra quelli disponibili del documento specificato.
	 * <BR />
	 * <B>NB: se alfrescoDocumentDTO.metadata è null o vuoto allora sarà restituito null!!!</B><BR/>
	 * <B>NB: se prefix è null e se esistono più metadati con lo stesso nome, allora sarà restituito il primo ad essere trovato.</B>
	 * 
	 * @param alfrescoDocumentDTO
	 * @param prefix
	 * @param name
	 * @return
	 * @throws CmisServiceException
	 */
	public CmisMetadataDTO getMetadata(CmisDocumentDTO alfrescoDocumentDTO, String prefix, String name)  throws CmisServiceException;
	
	/**
	 * Restituisce un elenco di folder salvate nell'istanza di Cmis.
	 * 
	 * @param statement			
	 * @param includeSystemFolder		
	 * @return List<CmisFolderDTO>
	 * @throws CmisServiceException
	 */
	public List<CmisFolderDTO> findFolderByStatement(String statement, boolean includeSystemFolder) throws CmisServiceException;

}
