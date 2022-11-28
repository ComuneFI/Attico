/************************************************************************************
 * Copyright (c) 2011, 2018 Link Management & Technology S.p.A. via R. Scotellaro, 55 73100 - Lecce
 * - http://www.linksmt.it - All rights reserved.
 *
 * Contributors: Links Management & Technology S.p.A. - initial API and implementation
 *************************************************************************************/
package it.linksmt.assatti.contabilita.test;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.contabilita.config.JenteConfiguration;
import it.linksmt.assatti.contabilita.service.ContabilitaServiceImpl;
import it.linksmt.assatti.cooperation.dto.contabilita.ContabilitaDto;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.service.exception.ServiceException;

/**
 * @author Gianluca Pindinelli
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = { JenteConfiguration.class })
public class ContabilitaTest {

	private final Logger log = LoggerFactory.getLogger(ContabilitaTest.class.getName());

	@Autowired
	private ContabilitaServiceImpl contabilitaService;
	
	// @Test
	public void testEsisteBozzaAtto() throws ServiceException {

		boolean esiste = contabilitaService.esisteBozzaAtto("DT", "123", 2018, true);
		log.info("esiste Bozza : " + esiste);
	}

	@Test
	public void testGetMovimentiContabili() throws ServiceException {

		List<MovimentoContabileDto> movimentoContabileDtos = 
				contabilitaService.getMovimentiContabili("DD", "103", 2019, false);

		log.info("contabilitaDto : " + movimentoContabileDtos);
	}

	// @Test
	public void testSendAtto() throws ServiceException {

		ContabilitaDto contabilitaDto = new ContabilitaDto();
		
		contabilitaDto.setCodiceTipoAtto("DD");
		contabilitaDto.setAnnoCreazioneProposta(2018);
		contabilitaDto.setNumeroProposta("00009");
		
		contabilitaDto.setDataCreazioneProposta(new Date());
		
		contabilitaDto.setImportoTotale(new BigDecimal(10.23));
		contabilitaDto.setOggetto("TEST GIO - JENTE");

		contabilitaService.sendBozza(contabilitaDto);

		log.info("Invio in contabilità effettuato");
	}
	
	// @Test
	public void testTrasformaBozzaAtto() throws ServiceException {
		
		ContabilitaDto contabilitaDto = new ContabilitaDto();

		contabilitaDto.setAnnoAtto(2018);
		contabilitaDto.setAnnoCreazioneProposta(2018);
		contabilitaDto.setCodiceTipoAtto("DD");
		contabilitaDto.setDataAdozioneAtto(new Date());
		// contabilitaDto.setDataCreazioneProposta(new Date());
		// contabilitaDto.setDataEsecutivita(new Date());
		contabilitaDto.setImportoTotale(new BigDecimal(10.234));
		contabilitaDto.setNumeroAtto("00099");
		contabilitaDto.setNumeroProposta("00009");
		// contabilitaDto.setOggetto("Oggetto di TEST LINKSMT test 2");
		// contabilitaDto.setResponsabileProcedimento(null);
		
		contabilitaService.confirmAtto(contabilitaDto);

		log.info("Trasformazione bozza/atto effettuata");
	}
	
	// @Test
	public void testDataEsecutivitaAtto() throws ServiceException {

		contabilitaService.dataEsecutivitaAtto("DD", "00099", 2018, new Date());
		log.info("Esecutivita");
	}
	
	// @Test
	public void testRevertBozza() throws ServiceException {
		
		ContabilitaDto contabilitaDto = new ContabilitaDto();
		contabilitaDto.setCodiceTipoAtto("DD");
		contabilitaDto.setAnnoAtto(2018);
		contabilitaDto.setNumeroAtto("00099");
		
		contabilitaService.revertBozza(contabilitaDto);

		log.info("Trasformazione bozza/atto effettuata");
	}
	
	// @Test
	public void testEliminaBozza() throws ServiceException {
		
		contabilitaService.eliminaBozza("DD", "00009", 2018);
		log.info("Eliminazione bozza effettuata");
	}
}
