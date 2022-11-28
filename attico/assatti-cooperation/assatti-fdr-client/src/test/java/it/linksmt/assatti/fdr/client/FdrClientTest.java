package it.linksmt.assatti.fdr.client;

import it.linksmt.assatti.fdr.service.FirmaRemotaService;

public class FdrClientTest {

	public static void main(String[] args) {
		try {
			FirmaRemotaService.sendCredential("AAAAAA", "BBBB");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
