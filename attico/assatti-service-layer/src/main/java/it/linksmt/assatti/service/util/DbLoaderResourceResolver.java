package it.linksmt.assatti.service.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

import it.linksmt.assatti.datalayer.domain.ModelloHtml;
import it.linksmt.assatti.datalayer.repository.ModelloHtmlRepository;


/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as dbloader resources, using
 *   {@link com.cifra2.gestatti.service.util#getClassLoader(Class)} for
 *   obtaining the class loader and then executing
 *   {@link DbLoaderResourceResolver#getResourceAsStream(String)}
 * </p>
 * 
 * @author Ivan Testaverde
 * @since 1.0
 *
 */
public final class DbLoaderResourceResolver 
        implements IResourceResolver {
	
	private final Logger log = LoggerFactory.getLogger(DbLoaderResourceResolver.class);

    public static final String NAME = "DBLOADER";
    
    public static final String START_HTML = 
			"<!DOCTYPE html>"
			+"<html xmlns:th=\"http://www.thymeleaf.org\"> "
			+"<head>"
			+"<title th:text=\"${title}\"></title>"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
			+"<style type=\"text/css\" media=\"all\"> "
			+".break { page-break-before: always; }"
			+" @page {"
			+ "	size: A4 portrait; "
			+ "	margin-top: 2.0in;"
			+ " margin-bottom: 2.0in;"
			+ " margin-right: 120px;"
			+ " margin-left:  100px;"
			+ " border: thin solid black;"
			+ " padding: 1em;"
			+ "}"
			+"</style>"
			+"</head>"
			+"<body th:inline=\"text\">";

    public static final String CLOSE_HTML = "</body></html>";
    
    private ModelloHtmlRepository modelloHtmlRepository;

    
    private DbLoaderResourceResolver() {
        super();
    }
    
    
    public DbLoaderResourceResolver(ModelloHtmlRepository modelloHtmlRepository) {
    	 super();
    	 this.modelloHtmlRepository=modelloHtmlRepository;
	}


	public String getName() {
        return NAME; 
    }


	@Override
	public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
		Validate.notNull(resourceName, "Resource name cannot be null");
		Validate.notEmpty(resourceName, "Resource name cannot be null");
		
		if(resourceName.indexOf("db:modelloId:")==-1)
			return null;
		
		String subId = resourceName.substring("db:modelloId:".length(),resourceName.length());
		
		Validate.notNull(subId, "Resource name cannot be null");
		Validate.notEmpty(subId, "Resource name cannot be empty");
		
		Long id = Long.valueOf(subId);
		
		log.debug("modelloHtmlRepository.findOne(id)"+id);
		
		ModelloHtml modelloHtml = modelloHtmlRepository.findOne(id);
		if (modelloHtml==null)
			return null;
		
		String html = modelloHtml.getHtml();
		
//		log.debug("html"+html);
//		
//		StringBuilder stringBuilder = new StringBuilder(START_HTML);
//		stringBuilder.append(html);
//		stringBuilder.append(CLOSE_HTML);
//		
//		String htmlComplete = stringBuilder.toString();
		String htmlComplete = html;
		if(html.indexOf("<!DOCTYPE html>")==-1){
			StringBuilder stringBuilder = new StringBuilder(START_HTML);
			stringBuilder.append(html);
			stringBuilder.append(CLOSE_HTML);
			htmlComplete = stringBuilder.toString();
		}
		
		htmlComplete = htmlComplete.replace("<html>", "<html xmlns:th=\"http://www.thymeleaf.org\">");
		htmlComplete = htmlComplete.replace("<body>", "<body th:inline=\"text\">");
		
		byte[] output  = htmlComplete.getBytes();
		
//		log.debug("html:complete:"+htmlComplete);
		
		return new ByteArrayInputStream(output,0, output.length );
	}
    
    
}
