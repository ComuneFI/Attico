package it.linksmt.assatti.datalayer.config;

import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.jpa.HibernatePersistenceProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.orm.jpa.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.context.ApplicationContextException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaDialect;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.codahale.metrics.MetricRegistry;
import com.fasterxml.jackson.datatype.hibernate4.Hibernate4Module;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.StringUtil;
import it.linksmt.assatti.utility.configuration.DataSourceProps;
import liquibase.integration.spring.SpringLiquibase;

@Configuration
@EntityScan(Constants.PACKAGE_ASSATTI_DOMAIN)
@EnableJpaRepositories(Constants.PACKAGE_ASSATTI_REPOSITORY)
@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware")
@EnableTransactionManagement
public class DatabaseConfiguration {
	
	private final Logger log = LoggerFactory.getLogger(DatabaseConfiguration.class);
	
	@Autowired(required = false)
    private MetricRegistry metricRegistry;
	
	@Autowired
	@Qualifier("dataSource")
	private DataSource dataSource;
	
	
	@Primary
	@Bean(destroyMethod = "close")
	public DataSource dataSource(CacheManager cacheManager) {
        log.debug("Configuring Datasource");
        if (StringUtil.isNull(DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_DB_URL))) {
            log.error("Your database connection pool configuration is incorrect! The application cannot start.");
            throw new ApplicationContextException("Database connection pool is not configured correctly");
        }
        
        HikariConfig config = new HikariConfig();
        config.setDataSourceClassName(DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_CLASSNAME));
        config.addDataSourceProperty("url", DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_DB_URL));
        config.setPoolName(DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_POOL_NAME, "GestattiCP"));
        config.addDataSourceProperty("user", DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_DB_USERNAME));
        config.addDataSourceProperty("password", DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_DB_PASSWORD));

        if (metricRegistry != null) {
            config.setMetricRegistry(metricRegistry);
        }
        if(DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_MAXIMUMPOOLSIZE) != null ){
        	config.setMaximumPoolSize(Integer.parseInt(DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_MAXIMUMPOOLSIZE)));
        }else{
        	config.setMaximumPoolSize(50);
        }
        
        String minIdleConnection = DataSourceProps.getProperty(ConfigPropNames.DATASOURCE_MINPOOLSIZE);
        if(minIdleConnection!=null && !minIdleConnection.trim().isEmpty()) {
	        try {
	        	Integer min = Integer.parseInt(minIdleConnection);
	        	if(min!=null && min < config.getMaximumPoolSize()) {
	        		config.setMinimumIdle(min);
	        	}
	        }catch(Exception e) {}
        }
        
        return new HikariDataSource(config);
    }
	
	@Bean
	@Primary
    public JpaTransactionManager transactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(entityManagerFactory().getObject());
        transactionManager.setJpaDialect(jpaDialect());
        return transactionManager;
    }

   private HibernateJpaVendorAdapter vendorAdaptor() {
        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
        vendorAdapter.setShowSql(Boolean.parseBoolean(DataSourceProps.getProperty("jpa.show_sql")));
        return vendorAdapter;
   }

   @Bean
   public JpaDialect jpaDialect() {
       return new org.springframework.orm.jpa.vendor.HibernateJpaDialect();
   }
   
   @Bean
   @Primary
   public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
	    log.debug("Configuring EntityManagerFactoryBean");
	    
        LocalContainerEntityManagerFactoryBean entityManagerFactoryBean = new LocalContainerEntityManagerFactoryBean();
        entityManagerFactoryBean.setJpaVendorAdapter(vendorAdaptor());
        entityManagerFactoryBean.setDataSource(dataSource);
        entityManagerFactoryBean.setPersistenceProviderClass(HibernatePersistenceProvider.class);
        entityManagerFactoryBean.setPackagesToScan(Constants.PACKAGE_ASSATTI_REPOSITORY);             
        entityManagerFactoryBean.setJpaProperties(jpaHibernateProperties());
        entityManagerFactoryBean.setJpaDialect(jpaDialect());
        return entityManagerFactoryBean;
    }

    private Properties jpaHibernateProperties() {

        Properties properties = new Properties();
        
        properties.put("hibernate.hbm2ddl.auto", DataSourceProps.getProperty(ConfigPropNames.JPA_HIBERNATE_DDL_AUTO));
        properties.put("hibernate.dialect", DataSourceProps.getProperty(ConfigPropNames.JPA_DATABASE_PLATFORM));
        properties.put("hibernate.cache.region.factory_class", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CACHE_REGION_FACTORY_CLASS));
        properties.put("hibernate.ejb.naming_strategy", DataSourceProps.getProperty(ConfigPropNames.JPA_HIBERNATE_NAMING_STRATEGY));
        properties.put("hibernate.show_sql", DataSourceProps.getProperty(ConfigPropNames.JPA_SHOW_SQL));
        properties.put("hibernate.cache.use_second_level_cache", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CACHE_USE_SECOND_LEVEL_CACHE));
        properties.put("hibernate.cache.use_query_cache", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CACHE_USE_QUERY_CACHE));
        properties.put("hibernate.generate_statistics", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_GENERATE_STATISTICS));
        properties.put("hibernate.cache.use_minimal_puts", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CACHE_USE_MINIMAL_PUTS));
        properties.put("hibernate.cache.hazelcast.use_lite_member", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CACHE_HAZELCAST_USE_LITE_MEMBER));
        properties.put("hibernate.connection.characterEncoding", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CONNECTION_CHARACTERENCODING));
        properties.put("hibernate.connection.useUnicode", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CONNECTION_USEUNICODE));
        properties.put("hibernate.connection.CharSet", DataSourceProps.getProperty(ConfigPropNames.JPA_PROPERTIES_HIBERNATE_CONNECTION_CHARSET));
        
        return properties;       
    }
	

    @Bean
    public SpringLiquibase liquibase() {
        SpringLiquibase liquibase = new SpringLiquibase();
        liquibase.setDataSource(dataSource);
        liquibase.setChangeLog("classpath:config/liquibase/master.xml");
       //  liquibase.setContexts(liquiBasePropertyResolver.getProperty("context"));
        
        log.debug("Configuring Liquibase");
        
        return liquibase;
    }  

    @Bean
    public Hibernate4Module hibernate4Module() {
        return new Hibernate4Module();
    }
}
