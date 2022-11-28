/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.conservazionesostitutiva.test;

import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.cmis.client.model.CmisDocumentDTO;
import it.linksmt.assatti.cmis.client.model.CmisMetadataDTO;
import it.linksmt.assatti.conservazionesostitutiva.configuration.ConservazioneSostitutivaConfiguration;
import it.linksmt.assatti.conservazionesostitutiva.exception.ConservazioneSostitutivaServiceException;
import it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService;

/**
 * @author Gianluca Pindinelli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { ConservazioneSostitutivaConfiguration.class })
public class ConservazioneSostitutivaTest {

	@Autowired
	private ConservazioneSostitutivaService conservazioneSostitutivaService;

	@Test
	public void testGetDaInviareInConservazione() throws ConservazioneSostitutivaServiceException {
		List<String> daInviareInConservazione = conservazioneSostitutivaService.daInviareInConservazione(
				//"3aa750c8-ba95-47b3-9da8-66e96d57d301", //test-prod,
				//"6f0e84c0-a17e-4459-9ae8-7e0e05af2b19",    // web35
				"21d32ee5-8cb5-4aab-9f18-36f257256f19", //013 web35
				// "ebc028b0-876e-447e-946d-4971c46625c0", // giorgio
				"atc:statoInvioConservazione", 
				"DA_INVIARE",
				"atc:ConservazioneSostituitivaAspect");
		
		int count = 1;
		for (String documentId : daInviareInConservazione) {
			System.out.println("\n" + count + ". ================== DOCUMENTO DA CONSERVARE - Id: " 
							+ documentId + " ==================");
			
			CmisDocumentDTO cmisDocumentDTO = conservazioneSostitutivaService
					.getUnitaDocumentaleConservazione(documentId, "R:atc:AssociazioneAllegatiAtto");
			
			System.out.println("Dimensione: " + (cmisDocumentDTO.getContent() != null ? 
														cmisDocumentDTO.getContent().length : 0));
			
			List<CmisMetadataDTO> listMeta = cmisDocumentDTO.getMetadata();
			for (CmisMetadataDTO cmisMeta : listMeta) {
				System.out.println(cmisMeta.getPrefix() + ":" + cmisMeta.getName()
						+ " -> " + cmisMeta.getValue());
			}
			
			List<CmisDocumentDTO> attachmentList = cmisDocumentDTO.getAttachments();
			if (attachmentList != null && attachmentList.size() > 0) {
				
				int allCount = 1;
				for (CmisDocumentDTO allegato : attachmentList) {
					System.out.println(count + "." + allCount + ". ================== ALLEGATO - Id: " 
							+ documentId + " ==================");
					
					System.out.println("Dimensione: " + (allegato.getContent() != null ? 
							allegato.getContent().length : 0));
					
					List<CmisMetadataDTO> metaAllegati = allegato.getMetadata();
					for (CmisMetadataDTO allegMeta : metaAllegati) {
						System.out.println(allegMeta.getPrefix() + ":" + allegMeta.getName()
							+ " -> " + allegMeta.getValue());
					}
					
					allCount++;
				}
			}
			count++;
		}
	}

	// @Test
	public void testAggiornaStatoConservazione() throws ConservazioneSostitutivaServiceException {
		conservazioneSostitutivaService.aggiornaStatoConservazione("04ef83f3-24e7-4b7b-a436-9d23396bcbd8;1.1", 
				"atc:statoInvioConservazione", "INVIATO", "atc:dataInvioConservazione", new Date(),
				"atc:notaInvioConservazione", "TEST TEST TEST");
	}

	// @Test
	public void testGetUnitaDocumentaleConservazione() throws ConservazioneSostitutivaServiceException {
		CmisDocumentDTO unitaDocumentaleConservazione = conservazioneSostitutivaService
				.getUnitaDocumentaleConservazione("425154d4-60a7-4d71-a159-b18e72e38e8b", "R:atc:AssociazioneAllegatiAtto");
		System.out.println(unitaDocumentaleConservazione);
	}

}
