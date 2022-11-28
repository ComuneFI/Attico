package it.linksmt.assatti.fdr.service;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.linksmt.assatti.fdr.client.BaseResponse;
import it.linksmt.assatti.fdr.client.ErrorResponse;
import it.linksmt.assatti.fdr.client.FdrWsResponse;
import it.linksmt.assatti.fdr.client.FdrWsService;
import it.linksmt.assatti.fdr.client.PadesParam;
import it.linksmt.assatti.fdr.client.PadesProfile;
import it.linksmt.assatti.fdr.client.PdfFile;
import it.linksmt.assatti.fdr.client.SignPdfFiles;
import it.linksmt.assatti.fdr.client.SignatureAuth;
import it.linksmt.assatti.fdr.client.SupportAuth;
import it.linksmt.assatti.fdr.config.FdrClientProps;
import it.linksmt.assatti.fdr.dto.FirmaRemotaRequestDTO;
import it.linksmt.assatti.fdr.dto.FirmaRemotaResponseDTO;
import it.linksmt.assatti.fdr.exception.FirmaRemotaException;
import it.linksmt.assatti.utility.StringUtil;

public class FirmaRemotaService {
	
	private static Logger log = LoggerFactory.getLogger(FirmaRemotaService.class);
	
	private static final String fdrWsUsername		= FdrClientProps.getProperty("fdrws.username");
	private static final String fdrWsPassword		= FdrClientProps.getProperty("fdrws.password");
	private static final String fdrWsUrl			= FdrClientProps.getProperty("fdrws.urlwsdl");
	
	private static final String codiceFiscaleTest	= FdrClientProps.getProperty("fdrws.codFiscale.test");
	private static final String passwordTest		= FdrClientProps.getProperty("fdrws.password.test");
	private static final String otpTest				= FdrClientProps.getProperty("fdrws.otp.test");
	private static final String fdrMode				= FdrClientProps.getProperty("fdrws.mode");
	
	public static String openSession(final String codFiscale, final String password, final String otp) throws Exception {
		if(fdrMode!=null && fdrMode.trim().equalsIgnoreCase("fake")) {
			return "ok";
		}else {
			SignatureAuth auth = getSignatureAuth(codFiscale, password, otp);
			FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
			FdrWsResponse responsePS = fdrService.openSession(auth);
			if(responsePS!=null && responsePS.getEsito()!=null && !responsePS.getEsito().trim().isEmpty() && FdrWsUtil.ESITO_OK.equalsIgnoreCase(responsePS.getEsito())) {
				return responsePS.getSessionId();
			}else {
				throw new RuntimeException("Impossibile creare la sessione di firma");
			}
		}
	}
	
	public static boolean closeSession(final String sessionId, final String codFiscale, final String password, final String otp) throws Exception {
		if(fdrMode!=null && fdrMode.trim().equalsIgnoreCase("fake")) {
			return true;
		}else {
			SignatureAuth auth = getSignatureAuth(codFiscale, password, otp);
			auth.setSessionId(sessionId);
			FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
			FdrWsResponse responsePS = fdrService.closeSession(auth);
			if(responsePS!=null && responsePS.getEsito()!=null && !responsePS.getEsito().trim().isEmpty() && FdrWsUtil.ESITO_OK.equalsIgnoreCase(responsePS.getEsito())) {
				return true;
			}else {
				log.error("Chiusura sessione di firma " + sessionId + " non avvenuta con successo - User " + codFiscale);
				return false;
			}
		}
	}
	
	public static List<FirmaRemotaResponseDTO> firmaPades(
			final String codFiscale, final String password, final String otp,
			List<FirmaRemotaRequestDTO> documents, final String sessionId) throws Exception {
		
		if(fdrMode!=null && fdrMode.trim().equalsIgnoreCase("fake")) {
			return toResponseDTOFake (documents)   ;    
		}
		SignPdfFiles files = new SignPdfFiles();			
		for (int i = 0; i < documents.size(); i++) {
			PdfFile pdf = new PdfFile();
			pdf.setFile(documents.get(i).getDocument());
			files.getPdf().add(pdf);
		}
		
		SignatureAuth auth = getSignatureAuth(codFiscale, password, otp);
		
		// Il tipo di firma potrebbe essere modificato senza riavviare l'AS
		PadesParam padesParam = new PadesParam();
		if(sessionId!=null && !sessionId.trim().isEmpty()) {
			padesParam.setSessionId(sessionId);
		}
		String valPdfProfile = StringUtil.trimStr(FdrClientProps.getProperty("fdrws.pdf.profile"));
		try {
			padesParam.setPadesProfile(PadesProfile.fromValue(valPdfProfile));
		}
		catch (Exception e) {
			log.warn("Valore non valido per il valore 'PdfProfile': "+valPdfProfile + ". Viene utilizzato il default 'BASIC'.");
			padesParam.setPadesProfile(PadesProfile.BASIC);
		}
		
		FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
		FdrWsResponse responsePS = fdrService.padesSignature(auth, files, padesParam);
		
		return toResponseDTO(documents, responsePS, null, null);
	}
	
	
	public static void sendCredential(final String codFiscale, final String password) throws Exception {
		SupportAuth auth = getSupportAuth(codFiscale, password);
		
		FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
		BaseResponse responseSC = fdrService.sendCredential(auth);
		
		if(FdrWsUtil.ESITO_OK.equalsIgnoreCase(responseSC.getEsito())) {
			log.info("Servizio Firma Remota - richiesta OTP effettuata");
		} 
		else if(FdrWsUtil.ESITO_KO.equalsIgnoreCase(responseSC.getEsito())) {
			String message = "";
			if (responseSC.getListaErrori()!=null){
				for (ErrorResponse err : responseSC.getListaErrori().getErrore()) {
					message = message.concat(err.getCode() + "-" + err.getDescription()+";");						
					if(FdrWsUtil.ERR_PSW_GENERIC.equalsIgnoreCase(err.getCode()) || 
					   FdrWsUtil.ERR_OTP_GENERIC.equalsIgnoreCase(err.getCode()) ||
					   FdrWsUtil.ERR_CODE_PROFILE_NOT_FOUND.equalsIgnoreCase(err.getCode()) ||
					   FdrWsUtil.ERR_CODE_PROFILE_DUPLICATE.equalsIgnoreCase(err.getCode())) {
							log.error("Servizio Firma Remota - Error:"+ message);
							throw new FirmaRemotaException(err.getCode(), FdrWsUtil.ERR_CREDENTIAL);
					}
				}
			}
			log.error("Servizio Firma Remota:"+ FdrWsUtil.ERR_GENERIC);
			throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC);
		}
		else {
			log.error("Servizio Firma Remota - Error:"+ FdrWsUtil.ESITO_KNOWN);
			throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ESITO_KNOWN);
		}
	}
	
	/*
	 * TODO: da usare per documenti non PDF
	 *
	public static List<FirmaRemotaResponseDTO> firmaCades(List<FirmaRemotaRequestDTO> documents) throws Exception {
		SignatureAuth auth = getSignatureAuth();
		
		SignFiles files = new SignFiles();
		for (int i = 0; i < documents.size(); i++) {
			files.getFile().add(documents.get(i).getDocument());
		}
		
		FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
		FdrWsResponse responsePS = fdrService.cadesSignature(auth, files);
		
		return toResponseDTO(documents, responsePS, null, ".p7m");
	}
	
	public static FirmaRemotaResponseDTO aggiungiFirmaCades(FirmaRemotaRequestDTO document) throws Exception {
		SignatureAuth auth = getSignatureAuth();
		
		FdrParam param = new FdrParam();
		param.setFile(document.getDocument());
		
		FdrWsService fdrService = FdrWsUtil.getServiceWithWsSecurity(fdrWsUsername, fdrWsPassword, fdrWsUrl);
		FdrWsResponse responsePS = fdrService.addCadesSignature(auth, param);
		
		List<FirmaRemotaRequestDTO> documents = new ArrayList<FirmaRemotaRequestDTO>();
		documents.add(document);
		
		List<FirmaRemotaResponseDTO> results = toResponseDTO(documents, responsePS, null, null);
		
		return (results != null) ? results.get(0) : null;
	}
	*/
	
	private static SignatureAuth getSignatureAuth(final String codFiscale, final String password, final String otp) {
		SignatureAuth auth = new SignatureAuth();
		if ( (codiceFiscaleTest == null) || (codiceFiscaleTest.trim().length() == 0) ) {
			auth.setCodiceFiscale(codFiscale.toUpperCase());
			auth.setPassword(password);
			auth.setOtpPassword(otp);
		}
		else {
			auth.setCodiceFiscale(codiceFiscaleTest.toUpperCase());
			auth.setPassword(passwordTest);
			auth.setOtpPassword(otpTest);
		}

		return auth;
	}
	
	private static SupportAuth getSupportAuth(final String codFiscale, final String password) {
		SupportAuth auth = new SupportAuth();
		if ( (codiceFiscaleTest == null) || (codiceFiscaleTest.trim().length() == 0) ) {
			auth.setCodiceFiscale(codFiscale.toUpperCase());
			auth.setPassword(password);
		}
		else {
			auth.setCodiceFiscale(codiceFiscaleTest.toUpperCase());
			auth.setPassword(passwordTest);
		}

		return auth;
	}
	
	private static List<FirmaRemotaResponseDTO> toResponseDTO(
			List<FirmaRemotaRequestDTO> documents, FdrWsResponse responsePS,
			String prefix, String suffix) 
					throws FirmaRemotaException {
		
		if(FdrWsUtil.ESITO_OK.equalsIgnoreCase(responsePS.getEsito())) {
			log.info("Servizio Firma Remota - File firmati con successo");
			
			List<FirmaRemotaResponseDTO> listResponse = new ArrayList<FirmaRemotaResponseDTO>();
			
			if(responsePS.getFiles().getFile().isEmpty()) {
				log.error("Servizio Firma Remota:"+ FdrWsUtil.ERR_GENERIC);
				throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC);	
			}
			
			for(int i=0;i<responsePS.getFiles().getFile().size();i++) {
				String filename = documents.get(i).getFileName();
				if (prefix != null) {
					filename = prefix + filename;
				}
				if (suffix != null) {
					filename = filename + suffix;
				}
				
				byte[] document = responsePS.getFiles().getFile().get(i);
				
				FirmaRemotaResponseDTO response = new FirmaRemotaResponseDTO();
				response.setFileName(filename);
				response.setDocument(document);
				listResponse.add(response);
			}
			
			return listResponse;
		} 
		else if(FdrWsUtil.ESITO_KO.equalsIgnoreCase(responsePS.getEsito())) {
			String message = "";
			if (responsePS.getListaErrori()!=null){
				for (ErrorResponse err : responsePS.getListaErrori().getErrore()) {
					message = message.concat(err.getCode() + "-" + err.getDescription()+";");						
					if(FdrWsUtil.ERR_PSW_GENERIC.equalsIgnoreCase(err.getCode()) || 
					   FdrWsUtil.ERR_OTP_GENERIC.equalsIgnoreCase(err.getCode()) ||
					   FdrWsUtil.ERR_CODE_PROFILE_NOT_FOUND.equalsIgnoreCase(err.getCode()) ||
					   FdrWsUtil.ERR_CODE_PROFILE_DUPLICATE.equalsIgnoreCase(err.getCode())) {
							log.error("Servizio Firma Remota - Error:"+ message);
							throw new FirmaRemotaException(err.getCode(), FdrWsUtil.ERR_CREDENTIAL);
					}
				}
			}
			log.error("Servizio Firma Remota:"+ FdrWsUtil.ERR_GENERIC + ":: error details :: " + message);
			throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ERR_GENERIC);	
		}
		else {
			log.error("Servizio Firma Remota - Error:"+ FdrWsUtil.ESITO_KNOWN);
			throw new FirmaRemotaException(FdrWsUtil.ERR_CODE_GENERIC, FdrWsUtil.ESITO_KNOWN);
		}
	}
	
	private static List<FirmaRemotaResponseDTO> toResponseDTOFake(List<FirmaRemotaRequestDTO> documents) 
					throws FirmaRemotaException {
			List<FirmaRemotaResponseDTO> listResponse = new ArrayList<FirmaRemotaResponseDTO>();
		
			if(documents!=null) {
				for(int i=0;i<documents.size();i++) {
					String filename = documents.get(i).getFileName();
					
					byte[] document = documents.get(i).getDocument(); 
			
					FirmaRemotaResponseDTO response = new FirmaRemotaResponseDTO();
					response.setFileName(filename);
					response.setDocument(document);
					listResponse.add(response);
				}
			}
			
			return listResponse;
		
	}
}
