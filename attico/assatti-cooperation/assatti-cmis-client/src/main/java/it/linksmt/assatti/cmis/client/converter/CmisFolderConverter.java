package it.linksmt.assatti.cmis.client.converter;

import java.util.ArrayList;
import java.util.List;

import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Property;

import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;

public class CmisFolderConverter {

	public static CmisFolderDTO convertToDto(Folder cmisFolder) {
		CmisFolderDTO alfrescoFolderDTO = null;
		if(cmisFolder!=null) {
			alfrescoFolderDTO = new CmisFolderDTO(cmisFolder.getId());
			alfrescoFolderDTO.setName(cmisFolder.getName());
			alfrescoFolderDTO.setDescription(cmisFolder.getDescription());
			alfrescoFolderDTO.setParentId(cmisFolder.getParentId());
			alfrescoFolderDTO.setPath(cmisFolder.getPath());
			alfrescoFolderDTO.setCreationDate(cmisFolder.getCreationDate().getTime());

			// metadati della folder
			List<CmisMetadataDTO> metadata = new ArrayList<CmisMetadataDTO>();

			for (Property<?> p : cmisFolder.getProperties()) {
				String key = p.getQueryName();
				String value = p.getValueAsString();
				String prefix = key.substring(0, key.indexOf(":"));
				//                }
				String name = key.substring(key.indexOf(":") + 1);

				// aggiungo il metadato alla lista di metadati della folder
				CmisMetadataDTO alfrescoMetadataDTO = new CmisMetadataDTO();
				alfrescoMetadataDTO.setPrefix(prefix); // FIXME verificare
				alfrescoMetadataDTO.setName(name); // FIXME verificare
				alfrescoMetadataDTO.setValue(value);
				metadata.add(alfrescoMetadataDTO);
			}

			alfrescoFolderDTO.setMetadata(metadata);

		}
		return alfrescoFolderDTO;
	}

}