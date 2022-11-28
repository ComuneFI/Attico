package it.linksmt.assatti.conservazionesostitutiva;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.conservazionesostitutiva.exception.ConservazioneSostitutivaServiceException;
import it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService;

@ComponentScan(basePackages = { "it.linksmt.assatti.conservazionesostitutiva" })
public class MainTest {

	@Autowired
	private ConservazioneSostitutivaService conservazioneSostitutivaService;

	public static void main(String[] args) {

		String linksRoot = "7e34f493-0fd0-49e3-8b82-283cd44e7a38"; // Firenze
		// String linksRoot = "ebc028b0-876e-447e-946d-4971c46625c0"; // WEB35

		ApplicationContext context
		= new AnnotationConfigApplicationContext(MainTest.class);

		MainTest p = context.getBean(MainTest.class);

		System.out.println("=== TEST INTERROGAZIONE ALFRESCO: Nodo \"LinksRoot\" " + linksRoot);
		try {
//			p.test(linksRoot);

			 String idUnitaDocumentale = "72cb928b-ba14-4d57-b441-09f697c00c40"; // Firenze
			 p.test2(idUnitaDocumentale);
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void test(String folderId) throws ConservazioneSostitutivaServiceException {
		List<String> daInviareInConservazione = conservazioneSostitutivaService.daInviareInConservazione(
				folderId, 
				"atc:statoInvioConservazione", 
				"DA_INVIARE",
				"atc:ConservazioneSostituitivaAspect");

		System.out.println("=== DOCUMENTI TROVATI: " + daInviareInConservazione.size());

		int count = 1;
		for (String documentId : daInviareInConservazione) {
			System.out.println(count + ". === DOCUMENTO DA CONSERVARE - Id: " + documentId);
			count++;
		}
	}

	private void test2(String documentId) throws ConservazioneSostitutivaServiceException {
		CmisDocumentDTO testMeta = conservazioneSostitutivaService.getUnitaDocumentaleConservazione(
				documentId, "R:atc:AssociazioneAllegatiAtto");

		List<CmisMetadataDTO> listMeta = testMeta.getMetadata();
		System.out.println("\n  *****Metadati documento principale*****");
		for (CmisMetadataDTO cmisMeta : listMeta) {
			System.out.println(cmisMeta.getPrefix() + ":" + cmisMeta.getName()
			+ " -> " + cmisMeta.getValue());
		}

		List<CmisDocumentDTO> cmisDocumentDTOs = testMeta.getAttachments();

		System.out.println("\n *****Elenco allegati*****");
		for (CmisDocumentDTO cmisDocumentDTO : cmisDocumentDTOs) {
			byte[] content = cmisDocumentDTO.getContent();
			System.out.println("dimensioni byte allegato: " + content.length);

			List<CmisMetadataDTO> listMetaAll = cmisDocumentDTO.getMetadata();
			System.out.println("*****Metadati allegato*****");
			for (CmisMetadataDTO cmisMeta : listMetaAll) {
				System.out.println(cmisMeta.getPrefix() + ":" + cmisMeta.getName()
				+ " -> " + cmisMeta.getValue());
			}

			System.out.println("dimensioni byte allegato: " + content.length);

		}
	}
}
