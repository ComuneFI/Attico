package it.linksmt.checkdb;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@PropertySources({
	@PropertySource("classpath:application.properties") 
})
@SpringBootApplication
public class ConnectionApplication extends SpringBootServletInitializer {

  public static void main(String... args) {
    SpringApplication.run(ConnectionApplication.class, args);
  }
}