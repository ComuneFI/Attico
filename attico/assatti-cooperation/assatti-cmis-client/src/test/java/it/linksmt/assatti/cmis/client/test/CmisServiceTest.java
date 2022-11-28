package it.linksmt.assatti.cmis.client.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.cmis.client.service.CmisService;
import junit.framework.TestCase;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/cmis-client-test-context.xml" })
// @TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
// TransactionalTestExecutionListener.class, DbUnitTestExecutionListener.class })
// @Transactional
public class CmisServiceTest extends TestCase {

	// CmisService alfrescoProxy = new CmisServiceImpl("http://10.0.6.17:8082", "admin",
	// "Links2015", "4.2.f");
	// CmisService alfrescoProxy = new CmisServiceImpl("http://10.0.6.18:8080", "admin",
	// "Links2016", "5.0.c");

	@Autowired
	private CmisService alfrescoProxy;

	// @Test
	public void testCreateDocument() {
		// CmisService alfrescoProxy = new CmisService("http://web26.linksmt.it:80", "admin",
		// "Links2012");

		/*
		 * Reperisco l'elenco di Documenti caricati in Cmis associati alla classe documentale
		 */

		// Map<String, Object> properties = new HashMap<String, Object>();
		// properties.put("paDoc:versato", Boolean.TRUE);
		//
		// GregorianCalendar cal = new GregorianCalendar();
		// properties.put("paDoc:dataVersamento", cal);

		try {
			// alfrescoProxy.updateMetadata("d5c3e2ee-1f4a-4d40-a82c-93339652c9cf", properties,
			// "http://127.0.0.1:8555/alfresco/cmisatom");
			// alfrescoProxy.getFolder("");
			alfrescoProxy.createDocument(new File("D:/esercizi.pdf"), "pdf", "", new HashMap<String, Object>());
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		// List<CmisDocumentDTO> listCmisDocumentDTO =
		// alfrescoProxy.findByMetadata("AllegatiDetermine", metadataFilter);
		// List<CmisDocumentDTO> results = alfrescoProxy.findByMetadata("fatturazione-elettronica",
		// metadataFilter);

		// System.out.println("results:"+results.size());
		// int iCount = 1;
		// for (CmisDocumentDTO result : results) {
		// System.out.println("Result " + iCount + ": " + result.toString());
		// iCount ++;
		// }
	}

	@Test
	public void testGetDocument() {
		CmisDocumentDTO alfrescoDocumentDTO = null;
		try {
			alfrescoDocumentDTO = alfrescoProxy.getDocument("3b70d1da-7bd9-4baa-8742-50bf00c3288d");
			List<CmisMetadataDTO> metadati = alfrescoDocumentDTO.getMetadata();
			for (CmisMetadataDTO metadato : metadati) {
				System.out.println(metadato.getPrefix() + ":" + metadato.getName() + " = " + metadato.getValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testGetFolderByPath() {
		CmisFolderDTO alfrescoFolderDTO = null;
		try {
			// alfrescoFolderDTO = alfrescoProxy.getFolderByPath("/Evoland/Beni Paesaggistici/Ulivi
			// Monumentali");
			alfrescoFolderDTO = alfrescoProxy.getFolderByPath("/Evoland/Punti");
			List<CmisMetadataDTO> metadati = alfrescoFolderDTO.getMetadata();
			for (CmisMetadataDTO metadato : metadati) {
				System.out.println(metadato.getPrefix() + ":" + metadato.getName() + " = " + metadato.getValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	// @Test
	// public void testGetCategories() {
	// try {
	// List<CmisCategoryDTO> categorie = alfrescoProxy.getCategories(null);
	// if(categorie!=null) {
	// for(CmisCategoryDTO c : categorie){
	// System.out.println(c.getNodeRef() + ":" + c.getName() + " = " + c.getTitle());
	// }
	// } else {
	// System.out.println("categorie IS NULL!!!");
	// }
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	// }

	// @Test
	public void testFindDocumentByStatement() {
		try {
			List<CmisMetadataDTO> listMetadata = new ArrayList<CmisMetadataDTO>();
			CmisMetadataDTO titolo = new CmisMetadataDTO("attico", "statoConservazione", "da_inviare", "attico:conservazioneSostitutiva");
			listMetadata.add(titolo);
			// String statement = "SELECT D.*, J.* FROM cmis:document AS D JOIN rd:risorsadigitale
			// AS J ON D.cmis:objectId = J.cmis:objectId WHERE D.cmis:objectTypeId='cmis:document'
			// AND in_folder(D, '56151611-208e-405c-9365-a219ee25f536') AND J.rd:salvataggioCompleto
			// NOT IN ('true')";
			// String statement = "SELECT D.*, J.* FROM cmis:document AS D JOIN rd:risorsadigitale
			// AS J ON D.cmis:objectId = J.cmis:objectId WHERE D.cmis:objectTypeId='cmis:document'
			// AND in_tree(D, '14322982-38e9-4515-b7fb-6e4b8dd1b3f4') AND J.rd:titolo LIKE '%andiamo
			// a comandare%'";
			// String statement = "SELECT D.*, J.* FROM cmis:document AS D JOIN rd:risorsadigitale
			// AS J ON D.cmis:objectId = J.cmis:objectId WHERE D.cmis:objectTypeId='cmis:document'
			// AND in_tree(D, '7234d0cd-dc8c-41ea-b36d-ecdc86157ea8')";
			// String statement = "SELECT D.* FROM cmis:document AS D WHERE in_tree(D,
			// '56151611-208e-405c-9365-a219ee25f536')";
			List<CmisDocumentDTO> listResource = alfrescoProxy.findByFolderMetadata("d533c55f-762a-4330-8b7a-8648d4f8095f", listMetadata, true);
			// List<CmisDocumentDTO> listResource =
			// alfrescoProxy.findDocumentByStatement(statement);
			if (listResource != null) {
				for (CmisDocumentDTO d : listResource) {
					System.out.println("RISORSA: " + d.getName() + " - " + d.getId());
				}
				System.out.println("listResource.size():" + listResource.size());
			}
		}
		catch (CmisServiceException e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testFindFolderByStatement() {
		try {
			String rootFolderId = "14322982-38e9-4515-b7fb-6e4b8dd1b3f4";
			// String statement = "SELECT * FROM cmis:folder WHERE in_tree('" + rootFolderId + "')
			// AND rd:layer IS NOT NULL";
			String statement = "SELECT F.*, C.* from cmis:folder F join rd:categoria C on F.cmis:objectId = C.cmis:objectId where C.rd:layer like '%http%'  AND in_tree(F, '" + rootFolderId + "')";
			List<CmisFolderDTO> listResource = alfrescoProxy.findFolderByStatement(statement, false);
			for (CmisFolderDTO d : listResource) {
				System.out.println("RISORSA: " + d.getName() + " - " + d.getId());
			}
		}
		catch (CmisServiceException e) {
			e.printStackTrace();
		}
	}

	// @Test
	public void testUpdateFolderMetadata() {
		fail();
		Map<String, Object> updateProperties = new HashMap<String, Object>();
		updateProperties.put("rd:layer", "http://host/arcgis/rest/services/Editing/EVOLAND/FeatureServer/0");
		updateProperties.put("rd:layerType", "punti");

		try {
			CmisFolderDTO alfrescoFolderDTO = alfrescoProxy.updateFolderMetadata("1b21657a-7409-4fc8-9c11-fbf1fba62994", updateProperties);
			List<CmisMetadataDTO> metadati = alfrescoFolderDTO.getMetadata();
			for (CmisMetadataDTO metadato : metadati) {
				System.out.println(metadato.getPrefix() + ":" + metadato.getName() + " = " + metadato.getValue());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
