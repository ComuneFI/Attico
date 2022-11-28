package it.linksmt.assatti.gestatti;

import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;

/**
 * This is a helper Java class that provides an alternative to creating a web.xml.
 */
public class ApplicationWebXml extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.showBanner(false)
                .sources(Application.class);
    }
}
