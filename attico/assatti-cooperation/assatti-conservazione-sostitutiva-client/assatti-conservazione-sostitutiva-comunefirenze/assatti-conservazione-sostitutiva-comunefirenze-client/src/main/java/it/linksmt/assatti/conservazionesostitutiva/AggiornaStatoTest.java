package it.linksmt.assatti.conservazionesostitutiva;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import it.linksmt.assatti.conservazionesostitutiva.exception.ConservazioneSostitutivaServiceException;
import it.linksmt.assatti.conservazionesostitutiva.service.ConservazioneSostitutivaService;

@ComponentScan(basePackages = { "it.linksmt.assatti.conservazionesostitutiva" })
public class AggiornaStatoTest {

	@Autowired
	private ConservazioneSostitutivaService conservazioneSostitutivaService;

	public static void main(String[] args) {
		
		ApplicationContext context = new AnnotationConfigApplicationContext(AggiornaStatoTest.class);

		AggiornaStatoTest p = context.getBean(AggiornaStatoTest.class);
		p.testModificaStato();
	}
	
	
	private void testModificaStato() {
		try {
			conservazioneSostitutivaService.aggiornaStatoConservazione(
					"62afc26a-96c7-4778-b209-729fe97b234b;1.3", 
					"atc:statoInvioConservazione", "CONSERVATO", 
					"atc:dataInvioConservazione", new Date(), 
					"atc:notaInvioConservazione", "Documento correttamente inviato in conservazione");
		} 
		catch (ConservazioneSostitutivaServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
