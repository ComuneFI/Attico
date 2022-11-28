package it.linksmt.assatti.datalayer.config.hazelcast;

import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.hazelcast.config.Config;
import com.hazelcast.config.EvictionPolicy;
import com.hazelcast.config.MapConfig;
import com.hazelcast.config.MaxSizeConfig;
import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.instance.HazelcastInstanceFactory;

import it.linksmt.assatti.datalayer.config.DatabaseConfiguration;
import it.linksmt.assatti.utility.ConfigPropNames;
import it.linksmt.assatti.utility.Constants;
import it.linksmt.assatti.utility.configuration.DataSourceProps;

@Configuration
@EnableCaching
@AutoConfigureAfter(value = {DatabaseConfiguration.class})
// MetricsConfiguration.class, 
public class CacheConfiguration {

    private static final Logger log = LoggerFactory.getLogger(CacheConfiguration.class);

    private static HazelcastInstance hazelcastInstance;
    private CacheManager cacheManager;

    @PreDestroy
    public void destroy() {
        log.info("Closing Cache Manager");
        Hazelcast.shutdownAll();
    }

    @Bean
    public CacheManager cacheManager(HazelcastInstance hazelcastInstance) {
        log.debug("Starting HazelcastCacheManager");
        cacheManager = new com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance);
        return cacheManager;
    }

    @Bean
    public HazelcastInstance hazelcastInstance() {
        return buildInstance();
    }

    private static MapConfig initializeDefaultMapConfig() {
        MapConfig mapConfig = new MapConfig();

        /*
            Number of backups. If 1 is set as the backup-count for example,
            then all entries of the map will be copied to another JVM for
            fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
         */
        mapConfig.setBackupCount(0);

        /*
            Valid values are:
            NONE (no eviction),
            LRU (Least Recently Used),
            LFU (Least Frequently Used).
            NONE is the default.
         */
        mapConfig.setEvictionPolicy(EvictionPolicy.LRU);

        /*
            Maximum size of the map. When max size is reached,
            map is evicted based on the policy defined.
            Any integer between 0 and Integer.MAX_VALUE. 0 means
            Integer.MAX_VALUE. Default is 0.
         */
        mapConfig.setMaxSizeConfig(new MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE));

        /*
            When max. size is reached, specified percentage of
            the map will be evicted. Any integer between 0 and 100.
            If 25 is set for example, 25% of the entries will
            get evicted.
         */
        mapConfig.setEvictionPercentage(25);

        return mapConfig;
    }

    private static MapConfig initializeDomainMapConfig() {
        MapConfig mapConfig = new MapConfig();

        mapConfig.setTimeToLiveSeconds(Integer.parseInt(DataSourceProps.getProperty(ConfigPropNames.CACHE_TIME_TO_LIVE_SECONDS)));
        return mapConfig;
    }
    
    private static MapConfig initializeClusteredSession() {
        MapConfig mapConfig = new MapConfig();

        mapConfig.setBackupCount(Integer.parseInt(DataSourceProps.getProperty(ConfigPropNames.CACHE_HAZELCAST_BACKUPCOUNT)));
        mapConfig.setTimeToLiveSeconds(Integer.parseInt(DataSourceProps.getProperty(ConfigPropNames.CACHE_TIME_TO_LIVE_SECONDS)));
        return mapConfig;
    }

    /**
    * @return the unique instance.
    */
    public static HazelcastInstance getHazelcastInstance() {
    	return buildInstance();
    }
    
    private static HazelcastInstance buildInstance() {
    	if (hazelcastInstance != null) {
    		return hazelcastInstance;
    	}
    	
    	log.debug("Configuring Hazelcast");
	      Config config = new Config();
	      config.setInstanceName("cifra2gestatti");
//	      GroupConfig groupConfig = new GroupConfig("cifra2gestatti", "cifra2gestatti");
//	      config.setGroupConfig(groupConfig);

	      
      config.getNetworkConfig().setPort(5702);
      config.getNetworkConfig().setPortAutoIncrement(false);

//      if (env.acceptsProfiles(Constants.SPRING_PROFILE_DEVELOPMENT)) {
          System.setProperty("hazelcast.local.localAddress", "127.0.0.1");

          config.getNetworkConfig().getJoin().getAwsConfig().setEnabled(false);
          config.getNetworkConfig().getJoin().getMulticastConfig().setEnabled(false);
          config.getNetworkConfig().getJoin().getTcpIpConfig().setEnabled(false);
//      }
      
      config.getMapConfigs().put("default", initializeDefaultMapConfig());
      config.getMapConfigs().put(Constants.PACKAGE_ASSATTI_DOMAIN + ".*", initializeDomainMapConfig());
      config.getMapConfigs().put("my-sessions", initializeClusteredSession());

      hazelcastInstance = HazelcastInstanceFactory.newHazelcastInstance(config);

      return hazelcastInstance;
    }
}
