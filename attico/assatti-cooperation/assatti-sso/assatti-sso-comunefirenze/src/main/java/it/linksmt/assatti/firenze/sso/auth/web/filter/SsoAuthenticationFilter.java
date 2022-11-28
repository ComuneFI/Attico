package it.linksmt.assatti.firenze.sso.auth.web.filter;

import java.io.IOException;
import java.io.StringReader;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

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

import it.linksmt.assatti.firenze.sso.configuration.SsoProps;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.GlobalProps;

@WebFilter(urlPatterns = { "/", "/*", "/?ssoid=*" })
public class SsoAuthenticationFilter implements Filter {

	private static final Logger log = LoggerFactory.getLogger(SsoAuthenticationFilter.class);

	private static final String EXCLUDE_REGEX = ".*(css|jpg|png|gif|js)$";

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.debug("SsoAuthenticationFilter :: INIT");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

		HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
		String requestUrl = httpServletRequest.getRequestURL().toString();

		if (requestUrl.matches(EXCLUDE_REGEX)) {
			filterChain.doFilter(servletRequest, servletResponse);
			return;
		}

		String concreteWebAuthFactory = GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_CONCRETE_FACTORY);

		if (concreteWebAuthFactory != null && concreteWebAuthFactory.equals("it.linksmt.assatti.firenze.sso.auth.web.concrete.SsoFirenzeWebAuthFactory")) {
			String queryString = httpServletRequest.getQueryString();

			log.debug("SsoAuthenticationFilter :: doFilter() :: request URL:" + requestUrl);
			log.debug("SsoAuthenticationFilter :: doFilter() :: queryString:" + queryString);
			/*
			 * Gestione login
			 */
			if (queryString != null && !queryString.isEmpty() && queryString.contains("ssoid=")) {
				String ssoid = httpServletRequest.getParameter(GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGIN_SSO_ID_PARAMETER_NAME));
				log.info("ssoid is " + ssoid);
				httpServletRequest.getSession().setAttribute(GlobalProps.getProperty(ConfigPropNames.WEB_AUTH_LOGIN_SSO_ID_PARAMETER_NAME), ssoid);
				String username = null;
				try {
					username = executeCheckLoginSsoAsync(ssoid);
				}
				catch (Exception e) {
					log.error("doFilter :: " + e.getMessage(), e);
				}
				if (username != null && !username.isEmpty()) {
					log.info("SsoAuthenticationFilter :: doFilter() :: ssoId: is Valid!");
					log.info("SsoAuthenticationFilter :: doFilter() :: username: " + username);

					HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
					String redirectUrl = "/#/login/sso/" + username + "/" + ssoid;
					try {
						httpServletResponse.sendRedirect(redirectUrl);
					}
					catch (Exception e) {
						log.error("unable to redirect to " + redirectUrl + " :: " + e.getMessage(), e);
					}
				}else {
					log.info("username is empty for ssoid " + ssoid);
				}
			}
		}
		filterChain.doFilter(servletRequest, servletResponse);
	}

	@Override
	public void destroy() {
		log.debug("SsoAuthenticationFilter :: :: DESTROY");
	}
	
	public static String executeCheckLoginSsoAsync(String ssoId) throws IOException, InterruptedException{
		String user = null;
		int delayMilliseconds = 500;
		
		try {
			delayMilliseconds = Integer.parseInt(SsoProps.getProperty(ConfigPropNames.SSO_DELAY_CHECK_MILLISECONDS, "500"));
		}catch(Exception e) {}
		log.info("waiting for " + delayMilliseconds + "milliseconds");
		try {
		    TimeUnit.MILLISECONDS.sleep(delayMilliseconds);
		    user = checkLoginSso(ssoId);
		} catch (InterruptedException ie) {
		    Thread.currentThread().interrupt();
		}catch(Exception e) {}
    	log.info("wait end extract user " + user);
    	return user;
    }
    
	public static String checkLoginSso(String ssoId) throws Exception {
		log.info("SsoAuthenticationFilter :: loginSso() :: checking ssoId: " + ssoId);
		String username = null; // given from ssoid check service if user is sso-authenticated

		String ssoLoginCheckBaseUrl = SsoProps.getProperty("sso.web.logincheck.url");
		String ssoLoginCheckUrl = ssoLoginCheckBaseUrl + ssoId;
		HttpGet request = new HttpGet(ssoLoginCheckUrl);
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
			log.info("SsoAuthenticationFilter :: loginSso() :: response code:\t" + statusCode);

			if (statusCode == 200) {
				// Getting the response body.
				String responseBody = EntityUtils.toString(response.getEntity());
				log.info("SsoAuthenticationFilter :: loginSso() :: responseBody:\t" + responseBody);
				// Parsing response body
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				DocumentBuilder builder = factory.newDocumentBuilder();
				Document document = builder.parse(new InputSource(new StringReader(responseBody)));

				XPathFactory xPathfactory = XPathFactory.newInstance();
				XPath xpath = xPathfactory.newXPath();
				XPathExpression exprUsername = xpath.compile("/SSO/LOGININFO/USERNAME");
				username = exprUsername.evaluate(document, XPathConstants.STRING).toString();
				log.info("SsoAuthenticationFilter :: loginSso() :: username:\t" + username);
			}else {
				log.info("SsoAuthenticationFilter :: statusCode ::\t" + statusCode);
			}
		}
		catch (Exception e) {
			throw new Exception("Impossibile effettuare il login : " + e.getMessage(), e);
		}

		if (username != null) {
			return username.trim().toUpperCase();
		}
		
		return null;
	}

}
