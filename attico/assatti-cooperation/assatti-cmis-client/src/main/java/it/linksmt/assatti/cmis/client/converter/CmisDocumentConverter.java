package it.linksmt.assatti.cmis.client.converter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.ItemIterable;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.OperationContext;
import org.apache.chemistry.opencmis.client.api.Property;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.enums.RelationshipDirection;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.linksmt.assatti.cmis.client.exception.CmisServiceErrorCode;
import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;

public class CmisDocumentConverter {
	
	protected static Logger log = LoggerFactory.getLogger(CmisDocumentConverter.class.getName());

	public static CmisDocumentDTO convertToDto(Session cmisSession,
			Document cmisDocument, String alfrescoHostname, 
			boolean withContent, boolean withAttachments,
			String attachmentAssociationType) throws CmisServiceException {
		
		CmisDocumentDTO alfrescoDocumentDTO = null;
		if(cmisDocument!=null) {
			alfrescoDocumentDTO = new CmisDocumentDTO(cmisDocument.getId());
			alfrescoDocumentDTO.setContentSize(cmisDocument.getContentStreamLength());
			alfrescoDocumentDTO.setName(cmisDocument.getName());
			alfrescoDocumentDTO.setDescription(cmisDocument.getDescription());
			alfrescoDocumentDTO.setCreateDate(cmisDocument.getCreationDate());

            // metadati del documento
            List<CmisMetadataDTO> metadata = new ArrayList<CmisMetadataDTO>();

            for (Property<?> p : cmisDocument.getProperties()) {
            	String key = p.getQueryName();
            	String value = p.getValueAsString();

//                PropertyType t = p.getType();

//                switch (t) {
//                  case BOOLEAN:
//                      value = ((Boolean) p.getValue()).toString();
//                      break;
//                  case ID:
//                      value = ((Long) p.getValue()).toString();
//                      break;
//                  case INTEGER:
//                    value = ((Integer) p.getValue()).toString();
//                    break;
//                  case DATETIME:
//                      value = ((Date) p.getValue()).toString();
//                      break;
//                  case DECIMAL:
//                      value = ((BigDecimal) p.getValue()).toString();
//                      break;
//                  case HTML:
//                      value = ((String) p.getValue()).toString();
//                      break;
//                  case STRING:
//                      value = (String) p.getValue();
//                      break;
//                  case URI:
//                      value = ((URI) p.getValue()).getPath();
//                      break;
//                };
//                    // We could go to the content service and ask for the content to get the URL but to save time we
//                    // might as well dig the content URL out of the results.
//                    String contentString = value;
//                    String[] values = contentString.split("[|=]");
//                    alfrescoDocumentDTO.setUrl(values[1]);

//            	log.info("CmisServiceImpl :: findByStatement() :: "+doc.getId()+" metadata : "+ key+" - "+value);
            	// FIXME verificare procedura recupero metadati custom e non
                // reperifsco prefix e name del metadato
                String prefix = "";
//                if(!key.contains("http")) {
//                	prefix = key.substring(1, key.indexOf("."));
//                } else {
                	prefix = key.substring(0, key.indexOf(":"));
//                }
                String name = key.substring(key.indexOf(":") + 1);
                
                // aggiungo il metadato alla lista di metadati del documento
                CmisMetadataDTO alfrescoMetadataDTO = new CmisMetadataDTO();
                alfrescoMetadataDTO.setPrefix(prefix); // FIXME verificare
                alfrescoMetadataDTO.setName(name); // FIXME verificare
                alfrescoMetadataDTO.setValue(value);
                metadata.add(alfrescoMetadataDTO);
            }
            
            alfrescoDocumentDTO.setMetadata(metadata);

            /*
             * compongo la URL per accedere al documento tramite alfresco share
             *
             * http://web26.linksmt.it/alfresco/d/a/workspace/SpacesStore/35a40c1f-1154-44a7-a811-8277e74dab2a/determinaLiquidazione.pdf
             */
            String documentUrl = alfrescoHostname + "/alfresco/d/d/workspace/SpacesStore/"+getCmisNodeRef(cmisDocument.getId())+"/"+alfrescoDocumentDTO.getName();
            String downloadUrl = alfrescoHostname + "/alfresco/d/a/workspace/SpacesStore/"+getCmisNodeRef(cmisDocument.getId())+"/"+alfrescoDocumentDTO.getName();

            alfrescoDocumentDTO.setDocumentUrl(documentUrl);
            alfrescoDocumentDTO.setDownloadUrl(downloadUrl);
            
            if (withContent) {
				try {
					alfrescoDocumentDTO.setContent(IOUtils.toByteArray(
							cmisDocument.getContentStream().getStream()));
				}
				catch (IOException e) {
					log.error(e.getMessage());
					throw new CmisServiceException(CmisServiceErrorCode.DOCUMENT_CONTENT_IO_EXCEPTION);
				}
			}
			if (withAttachments) {
				ObjectType typeDefinition = cmisSession.getTypeDefinition(attachmentAssociationType);
		        OperationContext operationContext = cmisSession.createOperationContext();

		        ItemIterable<Relationship> check = cmisSession.getRelationships(
		        		cmisSession.createObjectId(cmisDocument.getId()), true,
		        		RelationshipDirection.EITHER,
		                typeDefinition, operationContext);
		        
		        List<CmisDocumentDTO> attachmentList = new ArrayList<CmisDocumentDTO>();
		        for (Relationship relationship : check) {
					if (!cmisDocument.getId().equals(relationship.getTargetId().getId())) {
						CmisDocumentDTO attachment = convertToDto(cmisSession,
								(Document)relationship.getTarget(), alfrescoHostname, 
								withContent, withAttachments,
								attachmentAssociationType);
						
						attachmentList.add(attachment);
					}
				}
		        alfrescoDocumentDTO.setAttachments(attachmentList); 
			}
		}
		return alfrescoDocumentDTO;
	}

	/**
	 * Estrae il nodeRef di alfresco a partire dal cmis nodeId.
	 * 
	 * @param cmisNodeId
	 * @return
	 */
	private static String getCmisNodeRef(String cmisNodeId) {
		String alfrescoNodeRef = "";
		if(cmisNodeId!=null && !cmisNodeId.isEmpty()) {
			int startIndex = cmisNodeId.lastIndexOf("/")>0 ? cmisNodeId.lastIndexOf("/") : 0;
			int endIndex = cmisNodeId.lastIndexOf(";")>0 ? cmisNodeId.lastIndexOf(";") : cmisNodeId.length()-1;
			alfrescoNodeRef = cmisNodeId.substring(startIndex, endIndex);
		}
		return alfrescoNodeRef;
	}

}