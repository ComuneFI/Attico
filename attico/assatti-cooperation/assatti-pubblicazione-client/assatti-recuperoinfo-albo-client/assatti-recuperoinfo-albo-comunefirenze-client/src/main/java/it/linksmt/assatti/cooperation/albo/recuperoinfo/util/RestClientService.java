package it.linksmt.assatti.cooperation.albo.recuperoinfo.util;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import it.linksmt.assatti.cooperation.albo.recuperoinfo.exception.RestClientException;
import it.linksmt.assatti.cooperation.dto.RelataAlboDto;
import it.linksmt.assatti.utility.configuration.SchedulerProps;

@Service
public class RestClientService {
	
	private final Logger log = LoggerFactory.getLogger(RestClientService.class);
	
	private static final String TIME_OUT_SECONDS = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.restTimeoutInSeconds", "10");
	
	public String getJsonInfo(String operationPath, String loginUrl, String user, String password) throws RestClientException{
		CloseableHttpClient client = null;
		try {
			Map<String, Object> map = this.executeGet(operationPath, loginUrl, user, password);
			HttpResponse response = (HttpResponse)map.get("response");
			client = (CloseableHttpClient)map.get("client");
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
				byte[] res = EntityUtils.toByteArray(entity);
				ContentType contentType = ContentType.getOrDefault(entity);
		        Charset charset = contentType.getCharset();
		        try {
					if(client!=null) {
				    	try {
							client.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				    }
				}catch(Exception e) {
				}
				return new String(res, charset);
	    	}else {
	    		throw new RestClientException("service " + operationPath + " return null entity");
	    	}
		}catch(Exception e) {
			try {
				if(client!=null) {
			    	try {
						client.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
			    }
			}catch(Exception ex) {
			}
			log.error("RestClientService :: executeOperation :: " + e.getMessage());
			throw new RestClientException(e);
		}
	}
	
	public RelataAlboDto downloadRelata(String operationPath, String loginUrl, String user, String password) throws RestClientException{
		RelataAlboDto relata = null;
		try {
			Map<String, Object> map = this.executeGet(operationPath, loginUrl, user, password);
			HttpResponse response = (HttpResponse)map.get("response");
			CloseableHttpClient client = (CloseableHttpClient)map.get("client");
			HttpEntity entity = response.getEntity();
			if(entity!=null) {
				byte[] res = EntityUtils.toByteArray(entity);
				relata = new RelataAlboDto();
				relata.setContent(res);
				
				ContentType contentType = ContentType.getOrDefault(entity);
		        String mimeType = contentType.getMimeType();
		        relata.setContentType(mimeType);
				
				String filename = "";
				Header[] headers = response.getAllHeaders();
				if(headers!=null) {
					for(Header header : headers) {
						if(header!=null && header.getName().equalsIgnoreCase("Content-Disposition")) {
							HeaderElement[] hes = header.getElements();
							if(hes!=null) {
								for(HeaderElement he : hes) {
									if(he.getParameterByName("filename") != null) {
										NameValuePair nvp = he.getParameterByName("filename");
										if(nvp!=null && nvp.getValue()!=null) {
											filename = nvp.getValue();
										}
										break;
									}
								}
							}
							break;
						}
					}
				}
				relata.setFileName(filename);
				try {
					if(client!=null) {
				    	try {
							client.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
				    }
				}catch(Exception e) {
				}
			}else {
	    		throw new RestClientException("service " + operationPath + " return null entity");
	    	}
		}catch(Exception e) {
			relata = null;
			log.error("RestClientService :: executeOperation :: " + e.getMessage());
			throw new RestClientException(e);
		}
		return relata;
	}
	
	private Map<String, Object> executeGet(String operationPath, String loginUrl, String user, String password) throws RestClientException{
		CloseableHttpClient client = null;
		try {
			client = this.doLogin(loginUrl, user, password);
			
		    HttpResponse response = client.execute(new HttpGet(operationPath));
		    if(response!=null) {
			    int statusCode = response.getStatusLine().getStatusCode();
			    if(statusCode >= 200 && statusCode < 300) {
			    	Map<String, Object> map = new HashMap<String, Object>();
			    	map.put("response", response);
			    	map.put("client", client);
			    	return map;
			    }else {
			    	log.error("service " + operationPath + " return http code " + statusCode);
			    	throw new RestClientException("service " + operationPath + " return http code " + statusCode);
			    }
		    }else {
		    	throw new RestClientException("service " + operationPath + " return null response");
		    }
		}catch(Exception e) {
			if(client!=null) {
		    	try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
			log.error("RestClientService :: executeOperation :: " + e.getMessage());
			throw new RestClientException(e);
		}
    }
	
	private CloseableHttpClient doLogin(String loginUrl, String user, String password) throws RestClientException{
		CloseableHttpClient client = null;
		try {
			client = HttpClientBuilder.create().build();
			RequestConfig config = RequestConfig.custom()
								   .setConnectTimeout(Integer.parseInt(TIME_OUT_SECONDS) * 1000)
								   .setConnectionRequestTimeout(Integer.parseInt(TIME_OUT_SECONDS) * 1000)
								   .setSocketTimeout(Integer.parseInt(TIME_OUT_SECONDS) * 1000).build();
			client = HttpClientBuilder.create().setDefaultRequestConfig(config).build();
			
			String rememberMe = SchedulerProps.getProperty("scheduler.job.pubblicazione.recuperoInfoAlbo.loginRemember", "false");
			/*
			JsonObject jsonLogin = new JsonObject();
			jsonLogin.addProperty("userName", user);
			jsonLogin.addProperty("password", password);
			jsonLogin.addProperty("rememberMe", rememberMe);
			*/
			
			String body = "userName=" + user + "&password=" + password + "&rememberMe=" + rememberMe;
			
			log.debug("infoLogin: " + body);
			
			StringEntity postEntity = new StringEntity(body);
			HttpPost post = new HttpPost(loginUrl);
			post.setEntity(postEntity);
			post.setHeader("Content-type", "application/x-www-form-urlencoded");
			post.setHeader("X-Requested-With", "XMLHttpRequest");
			
		    HttpResponse response = client.execute(post);
		    if(response!=null) {
			    int statusCode = response.getStatusLine().getStatusCode();
			    if(statusCode >= 200 && statusCode < 300) {
			    	HttpEntity responseEntity = response.getEntity();
					if(responseEntity!=null) {
						byte[] res = EntityUtils.toByteArray(responseEntity);
						ContentType contentType = ContentType.getOrDefault(responseEntity);
				        Charset charset = contentType.getCharset();
				        String jsonLoginRespStr = new String(res, charset);
				        JsonParser jsonParser = new JsonParser();
						JsonObject jsonLoginResp = jsonParser.parse(jsonLoginRespStr).getAsJsonObject();
						Boolean loginSuccess = false;
						if(jsonLoginResp.has("success")) {
							loginSuccess = jsonLoginResp.get("success").getAsBoolean();
						}else if(jsonLoginResp.has("Success")) {
							loginSuccess = jsonLoginResp.get("Success").getAsBoolean();
						}
						if(loginSuccess==null || !loginSuccess) {
							log.error("login failed");
							if(jsonLoginResp.has("message")) {
								log.error("login failed message: " + jsonLoginResp.get("message").getAsString());
							}else if(jsonLoginResp.has("Message")) {
								log.error("login failed message: " + jsonLoginResp.get("Message").getAsString());
							}else {
								log.error("login failed return json: " + jsonLoginResp);
							}
							throw new RestClientException("login exception");
						}else {
							log.debug("login success");
						}
			    	}else {
			    		throw new RestClientException("login service " + loginUrl + " return null entity");
			    	}
			    }else {
			    	log.error("login service " + loginUrl + " return http code " + statusCode);
			    	throw new RestClientException("login service " + loginUrl + " return http code " + statusCode);
			    }
		    }else {
		    	throw new RestClientException("login service " + loginUrl + " return null response");
		    }
		}catch(Exception e) {
			if(client!=null) {
		    	try {
					client.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
		    }
			log.error("RestClientService :: executeOperation :: " + e.getMessage());
			throw new RestClientException(e);
		}
		return client;
	}
}
