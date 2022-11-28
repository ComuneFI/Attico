package it.linksmt.assatti.firenze.cmis.client.login.service.impl;

import java.io.IOException;
import java.io.StringReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.SessionFactory;
import org.apache.chemistry.opencmis.client.runtime.SessionFactoryImpl;
import org.apache.chemistry.opencmis.commons.SessionParameter;
import org.apache.chemistry.opencmis.commons.enums.BindingType;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import it.linksmt.assatti.cmis.client.exception.CmisServiceErrorCode;
import it.linksmt.assatti.cmis.client.exception.CmisServiceException;
import it.linksmt.assatti.cmis.client.login.service.LoginService;
import it.linksmt.assatti.firenze.sso.configuration.SsoProps;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.CmisProps;

/**
 * Concrete Product for login abstract factory, suitable for Firenze environment
 * 
 * @author marco ingrosso
 *
 */
public class FirenzeLoginService extends LoginService {
	
	private static Logger log = LoggerFactory.getLogger(FirenzeLoginService.class.getName());
	
	private static String ssoAlfrescoLoginUrl = SsoProps.getProperty(ConfigPropNames.SSO_ALFRESCO_TOKEN_URL);
	private static String key = SsoProps.getProperty(ConfigPropNames.SSO_ALFRESCO_TOKEN_KEY); // 128 bit key
	private static String initVector = SsoProps.getProperty(ConfigPropNames.SSO_ALFRESCO_TOKEN_INIT_VECTOR); // 16 bytes IV
	
	private static String cmisVersion = CmisProps.getProperty(ConfigPropNames.CMIS_VERSION);	// cmis version

	private static Session lastSession = null;
	
	@Override
	public Session getLoginSession() throws CmisServiceException {
		log.info("FirenzeLoginService :: getLoginSession()");

        log.info("FirenzeLoginService :: getLoginSession() :: cmisVersion: " + cmisVersion);
        
        String alfrescoToken = null;
        String alfrescoBaseUrl = null;
        String alfrescoUrl = null;
        String alfrescoLoginUrl = null;
		
		/*
		 * Chiamo il servizio SSO per ottenere il token criptato di alfresco
		 */
		String cryptedToken = null;
		HttpGet request = new HttpGet(ssoAlfrescoLoginUrl);
		try {
			TrustStrategy trustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
					return true;
				}
			};
			
			SSLContextBuilder sslContextbuilder = new SSLContextBuilder();
			sslContextbuilder.loadTrustMaterial(null, trustStrategy);
		    SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContextbuilder.build());

		    // execute http call
		    CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(sslsf).build();
		    CloseableHttpResponse response = client.execute(request);

			int statusCode = response.getStatusLine().getStatusCode();
			log.info("FirenzeLoginService :: getLoginSession() :: response code:\t" + statusCode);
			
			if(statusCode==200) {
				// Getting the response body.
				String responseBody = EntityUtils.toString(response.getEntity());
				log.info("FirenzeLoginService :: getLoginSession() :: responseBody:\t" + responseBody);
				// Parsing response body
				try {
					DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
					DocumentBuilder builder = factory.newDocumentBuilder();
					Document document = builder.parse(new InputSource(new StringReader(responseBody)));
		 
					XPathFactory xPathfactory = XPathFactory.newInstance();
					XPath xpath = xPathfactory.newXPath();
					XPathExpression exprToken = xpath.compile("/SSO/ALFRESCOINFO/ALFRESCO/NODES/NODE[ID=1]/TOKEN");
					XPathExpression exprBaseUrl = xpath.compile("/SSO/ALFRESCOINFO/ALFRESCO/NODES/NODE[ID=1]/BASEURL");
					XPathExpression exprUrl = xpath.compile("/SSO/ALFRESCOINFO/ALFRESCO/NODES/NODE[ID=1]/URL");
					XPathExpression exprLoginUrl = xpath.compile("/SSO/ALFRESCOINFO/ALFRESCO/NODES/NODE[ID=1]/LOGINURL");
					alfrescoUrl = exprUrl.evaluate(document, XPathConstants.STRING).toString();
					alfrescoBaseUrl = exprBaseUrl.evaluate(document, XPathConstants.STRING).toString();
					alfrescoLoginUrl = exprLoginUrl.evaluate(document, XPathConstants.STRING).toString();
					cryptedToken = exprToken.evaluate(document, XPathConstants.STRING).toString();
					log.info("FirenzeLoginService :: getLoginSession() :: alfrescoBaseUrl:\t" + alfrescoBaseUrl);
					log.info("FirenzeLoginService :: getLoginSession() :: alfrescoUrl:\t" + alfrescoUrl);
					log.info("FirenzeLoginService :: getLoginSession() :: alfrescoLoginUrl:\t" + alfrescoLoginUrl);
					log.info("FirenzeLoginService :: getLoginSession() :: cryptedToken:\t" + cryptedToken);
				} catch (ParserConfigurationException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				} catch (XPathExpressionException e) {
					e.printStackTrace();
				}
			}
		} catch (ClientProtocolException e1) {
			e1.printStackTrace();
//			throw new CmisServiceException("Impossibile connettersi al servizio SSO di alfresco: errore nel protocollo usato dal client.");
		} catch (IOException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e1) {
			e1.printStackTrace();
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyStoreException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
		if(cryptedToken!=null && !cryptedToken.isEmpty()) {
			alfrescoToken = decrypt(key, initVector, cryptedToken);
		}

		Map<String, String> parameter = new HashMap<String, String>();
		
		String cmisUrl = alfrescoBaseUrl + "/api/-default-/public/cmis/versions/1.1/atom";

		// connection settings
		parameter.put(SessionParameter.ATOMPUB_URL, cmisUrl);
		parameter.put(SessionParameter.BINDING_TYPE, BindingType.ATOMPUB.value());
		parameter.put(SessionParameter.USER, "ROLE_TICKET");
		parameter.put(SessionParameter.PASSWORD, alfrescoToken);

		if (cmisVersion!=null && cmisVersion.equals("1.0")) {
			// set the alfresco object factory
			parameter.put(SessionParameter.OBJECT_FACTORY_CLASS, "org.alfresco.cmis.client.impl.CmisObjectFactoryImpl");
		}

		try {
			// create session
			SessionFactory factory = SessionFactoryImpl.newInstance();
			lastSession = factory.getRepositories(parameter).get(0).createSession();

			return lastSession;
		} catch (Exception e) {
			e.printStackTrace();
			throw new CmisServiceException(CmisServiceErrorCode.LOGIN_ERROR);
		}
	}

	private static String decrypt(String key, String initVector, String encrypted) {
		String decrypted = null;
		try {
			byte[] keyBytes = key.getBytes("UTF-8");
			
			byte[] ivBytes = DatatypeConverter.parseHexBinary(initVector);

			IvParameterSpec iv = new IvParameterSpec(ivBytes);
			SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, "AES");

			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
			cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);

			byte[] original = cipher.doFinal(DatatypeConverter.parseHexBinary(encrypted));

			decrypted = new String(original, "UTF-8");
			return decrypted;
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}

}
