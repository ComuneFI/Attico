package it.linksmt.assatti.fdr.service;

import java.net.URL;

import javax.xml.namespace.QName;

import it.linksmt.assatti.fdr.client.FdrWsService;
import it.linksmt.assatti.fdr.client.FdrWsServiceImplService;
import it.linksmt.assatti.fdr.security.FdrHandler;
import it.linksmt.assatti.fdr.security.FdrWsSecurityHandler;

public class FdrWsUtil {

	public static final String ESITO_OK = "OK";
    public static final String ESITO_KO = "KO";
    public static final String ERR_CODE_GENERIC = "999";
    public static final String ERR_PSW_GENERIC = "0003";
    public static final String ERR_OTP_GENERIC = "0004";
    
    public static final String ERR_CODE_PROFILE_NOT_FOUND = "0011";
    public static final String ERR_CODE_PROFILE_DUPLICATE = "0012";
    
    public static final String ESITO_KNOWN = "Esito sconosciuto";
    public static final String ERR_GENERIC = "Errore generico";
    public static final String ERR_CREDENTIAL = "Credenziali non valide";
    public static final String ERR_MARKFILE = "Marcatura temporale fallita";
	
	public static FdrWsService getServiceWithWsSecurity(String fdrWsUsername, String fdrWsPassword, String fdrWsUrl) throws Exception {
		FdrWsService service = null;
		String fdrWsNamespace = "http://impl.service.ws.fdr.linksmt.it/";
		URL url = new URL(fdrWsUrl);
		QName qname = new QName(fdrWsNamespace, "FdrWsServiceImplService");
		FdrWsServiceImplService fdrWsService = new FdrWsServiceImplService(url, qname);
		fdrWsService.setHandlerResolver(new FdrHandler(new FdrWsSecurityHandler(fdrWsUsername, fdrWsPassword)));
		service = fdrWsService.getFdrWsServiceImplPort();
		return service;
	}
}
