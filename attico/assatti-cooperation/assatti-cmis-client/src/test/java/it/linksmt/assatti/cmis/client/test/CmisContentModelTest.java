package it.linksmt.assatti.cmis.client.test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;

import org.apache.chemistry.opencmis.commons.PropertyIds;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.cmis.client.model.CmisFolderDTO;
import it.linksmt.assatti.cmis.client.service.CmisService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/cmis-client-test-context.xml" })
public class CmisContentModelTest {
	
	private static final String FILE_PATH = "/home/chiriacog/Scrivania/1-pdf-sample.pdf";
	private static final String ATTACHMENT_PATH = "/home/chiriacog/Scrivania/test-pdf-sample-omissis.pdf";
	
	private static final String FOLDER_ALFRESCO = "/dev/giorgio/TEST_CONTENT_MODEL";
	private static final String NODE_ID = "8fefb959-66ce-48fa-bbe4-c7bb6d213c40";
	private static final String ATTACH_NODE_ID = "d738dac6-c2d5-4a1c-823d-f4741e5331ca";
	
	// private static final String FOLDER_ALFRESCO = "/LinksRoot/TEST_CONTENT_MODEL";
	// private static final String NODE_ID = "2f58e25a-9174-49a7-b88e-1b329a42f0d3";
	// private static final String ATTACH_NODE_ID = "2274d8e1-df28-4efe-b412-1757e66a08e5";

	@Autowired
	private CmisService alfrescoProxy;
	
	// @Test
	public void testCreateDocumentType() {
		try {
			FileInputStream in = new FileInputStream(new File(FILE_PATH));
			
			byte[] contenuto = IOUtils.toByteArray(in);
			IOUtils.closeQuietly(in);
			
			CmisFolderDTO testFolder = alfrescoProxy.getFolderByPath(FOLDER_ALFRESCO, false);
			
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "D:atc:DeterminaType");
			
			alfrescoProxy.createDocument(contenuto, 
				System.currentTimeMillis() +".pdf", "application/pdf", testFolder.getId(),
				properties, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// @Test
	public void testAddAspects() {
		try {
			List<String> aspects = new ArrayList<String>(
					Arrays.asList("P:atc:PubblicazioneAspect", "P:atc:ConservazioneSostituitivaAspect"));
			
			alfrescoProxy.addAspect(NODE_ID, aspects);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// @Test
	public void testUpdateProperties() {
		try {
			HashMap<String, Object> updateProperties = new HashMap<String, Object>();
			
			// Properties del Content Type
			updateProperties.put("atc:numeroRegistrazione", new Long(9999999999l));
			updateProperties.put("atc:oggetto", "OGGETTO TEST GIORGIO");
			updateProperties.put("atc:flagVistoRegolaritaContabile", new Boolean(true));
			
			// Properties degli Aspects
			GregorianCalendar calPub = new GregorianCalendar();
			calPub.setTime(new Date());
			
			updateProperties.put("atc:dataInizioPubblicazione", calPub);
			updateProperties.put("atc:numeroPubblicazione",  new Long(7777777777l));
			updateProperties.put("atc:statoInvioConservazione", "INVIATO");
			updateProperties.put("atc:dataInvioConservazione", calPub);
			
			alfrescoProxy.updateDocumentMetadata(NODE_ID, updateProperties);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// @Test
	public void testCreateAttachment() {
		try {
			FileInputStream in = new FileInputStream(new File(ATTACHMENT_PATH));
			
			byte[] contenuto = IOUtils.toByteArray(in);
			IOUtils.closeQuietly(in);
			
			CmisFolderDTO testFolder = alfrescoProxy.getFolderByPath(FOLDER_ALFRESCO, false);
			
			HashMap<String, Object> properties = new HashMap<String, Object>();
			properties.put(PropertyIds.OBJECT_TYPE_ID, "D:atc:AllegatoType");
			
			properties.put("atc:nomeAllegato", "ALLEGATO DA INSERIRE IN ASSOCIAZIONE");
			properties.put("atc:tipoAllegato", "ATTACHMENT");
			
			alfrescoProxy.createDocument(contenuto, 
				System.currentTimeMillis() +".pdf", "application/pdf", testFolder.getId(),
				properties, false);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testCreaAssociazione() {
		try {
			alfrescoProxy.createAssociation("R:atc:AssociazioneAllegatiAtto", NODE_ID + ";1.0", ATTACH_NODE_ID + ";1.0");
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
