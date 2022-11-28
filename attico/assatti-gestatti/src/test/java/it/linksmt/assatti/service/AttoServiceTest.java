package it.linksmt.assatti.service;

import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import it.linksmt.assatti.ServiceConfiguration;
import it.linksmt.assatti.datalayer.domain.Atto;

/**
 * Test class for the UserResource REST controller.
 *
 * @see UserService
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = ServiceConfiguration.class)
@IntegrationTest
public class AttoServiceTest {

	private final List<Long> ids = Arrays.asList(2506L, 2491L, 2486L);

	@Autowired
	private AttoService attoService;

	@Test()
	public void createAttoTest() {
		final Iterator<Long> iterator = ids.iterator();

		ExecutorService executor = Executors.newFixedThreadPool(ids.size());

		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Atto atto = attoService.findOne(iterator.next());
					assertNotNull(atto);
					attoService.numeraGeneraFirma(atto, null, null, null, false);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Atto atto = attoService.findOne(iterator.next());
					assertNotNull(atto);
					attoService.numeraGeneraFirma(atto, null, null, null, false);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		executor.execute(new Runnable() {

			@Override
			public void run() {
				try {
					Atto atto = attoService.findOne(iterator.next());
					assertNotNull(atto);
					attoService.numeraGeneraFirma(atto, null, null, null, false);
				}
				catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
		try {
			executor.awaitTermination(60, TimeUnit.SECONDS);
		}
		catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
