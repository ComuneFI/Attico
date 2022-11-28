package it.linksmt.assatti.service.util;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.thymeleaf.TemplateProcessingParameters;
import org.thymeleaf.resourceresolver.IResourceResolver;
import org.thymeleaf.util.Validate;

import it.linksmt.assatti.datalayer.domain.ReportRuntime;
import it.linksmt.assatti.datalayer.repository.ReportRuntimeRepository;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.configuration.WebApplicationProps;


/**
 * <p>
 *   Implementation of {@link IResourceResolver} that resolves
 *   resources as dbloader resources, using
 *   {@link com.cifra2.gestatti.service.util#getClassLoader(Class)} for
 *   obtaining the class loader and then executing
 *   {@link ReportRuntimeLoaderResourceResolver#getResourceAsStream(String)}
 * </p>
 * 
 * @author Ivan Testaverde
 * @since 1.0
 *
 */
public final class ReportRuntimeLoaderResourceResolver 
        implements IResourceResolver {
	
	private final Logger log = LoggerFactory.getLogger(ReportRuntimeLoaderResourceResolver.class);

    public static final String NAME = "RUNTIMELOADER";
    
    public static final String START_HTML = 
			"<!DOCTYPE html>"
			+"<html xmlns:th=\"http://www.thymeleaf.org\"> "
			+"<head>"
			+"<title th:text=\"${title}\"></title>"
			+"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\" />"
			+"<style type=\"text/css\" media=\"all\"> "
			+".break { page-break-before: always; }"
			+" @page {"
			+ "	size: [[${formato}]]; "
			+ "	margin-top: "+WebApplicationProps.getProperty(ConfigPropNames.PDF_MARGIN_TOP, "2.0in")+";"
			+ " margin-bottom: "+WebApplicationProps.getProperty(ConfigPropNames.PDF_MARGIN_BOTTOM, "2.0in")+";"
			+ " margin-right: "+WebApplicationProps.getProperty(ConfigPropNames.PDF_MARGIN_RIGHT, "120px")+";"
			+ " margin-left: "+WebApplicationProps.getProperty(ConfigPropNames.PDF_MARGIN_LEFT, "100px")+";"
			+ " border: thin solid white;"
			+ " padding: 1em;"
			+ "}"
			+"</style>"
			+"</head>"
			+"<body th:inline=\"text\">";

public static final String CLOSE_HTML = "</body></html>";
    
    private ReportRuntimeRepository reportRuntimeRepository ;

    
    private ReportRuntimeLoaderResourceResolver() {
        super();
    }
    
    
    public ReportRuntimeLoaderResourceResolver( ReportRuntimeRepository reportRuntimeRepository) {
    	 super();
    	 this.reportRuntimeRepository=reportRuntimeRepository;
	}


	public String getName() {
        return NAME; 
    }


	@Override
	public InputStream getResourceAsStream(final TemplateProcessingParameters templateProcessingParameters, final String resourceName) {
		Validate.notNull(resourceName, "Resource name cannot be null");
		Validate.notEmpty(resourceName, "Resource name cannot be null");
		
		if(resourceName.indexOf("reportruntime:reportId:")==-1)
			return null;
		
		String subId = resourceName.substring("reportruntime:reportId:".length(),resourceName.length());
		
		Validate.notNull(subId, "Resource name cannot be null");
		Validate.notEmpty(subId, "Resource name cannot be empty");
		
		Long id = Long.valueOf(subId);
		
		log.debug("reportRuntimeRepository.findOne(id)"+id);
		
		ReportRuntime reportRuntime = reportRuntimeRepository.findOne(id);
		if (reportRuntime==null)
			return null;
		
		String html = reportRuntime.getHtml();
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
		
//		log.debug("html:reportRuntime:"+htmlComplete);
		
		return new ByteArrayInputStream(output,0, output.length );
	}
    
    
}
