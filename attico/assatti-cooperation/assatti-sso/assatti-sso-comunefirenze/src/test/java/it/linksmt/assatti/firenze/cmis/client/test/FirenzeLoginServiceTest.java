package it.linksmt.assatti.firenze.cmis.client.test;

import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.firenze.cmis.client.login.service.impl.FirenzeLoginService;

public class FirenzeLoginServiceTest {

	public static void main(String[] args) {
		FirenzeLoginService firenzeLoginService = new FirenzeLoginService();
		try {
			firenzeLoginService.getLoginSession();
		} catch (CmisServiceException e) {
			e.printStackTrace();
		}
	}

}