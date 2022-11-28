package it.linksmt.assatti.contabilita;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import com.google.gson.Gson;

import it.linksmt.assatti.contabilita.service.ContabilitaServiceImpl;
import it.linksmt.assatti.cooperation.dto.contabilita.MovimentoContabileDto;
import it.linksmt.assatti.service.exception.ServiceException;

@ComponentScan(basePackages = { "it.linksmt.assatti.contabilita"})
public class MainTest {

	@Autowired
	private ContabilitaServiceImpl contabilitaService;
	
	public static void main(String[] args) {
	
		ApplicationContext context = new AnnotationConfigApplicationContext(MainTest.class);

		MainTest p = context.getBean(MainTest.class);
		
		try {
			p.testGetMovimentiContabili();
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void testGetMovimentiContabili() throws ServiceException {

		List<MovimentoContabileDto> movimentoContabileDtos = 
				contabilitaService.getMovimentiContabili("DD", "103", 2019, true);
		
		System.out.println("RISULTATO:");
		System.out.println(new Gson().toJson(movimentoContabileDtos));
	}
}
