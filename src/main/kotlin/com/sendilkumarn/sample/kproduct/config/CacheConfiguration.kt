package com.sendilkumarn.sample.kproduct.config

import com.hazelcast.config.Config
import com.hazelcast.config.EvictionPolicy
import com.hazelcast.config.ManagementCenterConfig
import com.hazelcast.config.MapConfig
import com.hazelcast.config.MaxSizeConfig
import com.hazelcast.core.Hazelcast
import com.hazelcast.core.HazelcastInstance
import io.github.jhipster.config.JHipsterConstants
import io.github.jhipster.config.JHipsterProperties
import io.github.jhipster.config.cache.PrefixedKeyGenerator
import javax.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.info.BuildProperties
import org.springframework.boot.info.GitProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles

@Configuration
@EnableCaching
class CacheConfiguration(
    @Autowired val gitProperties: GitProperties?,
    @Autowired val buildProperties: BuildProperties?,
    private val env: Environment
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @PreDestroy
    fun destroy() {
        log.info("Closing Cache Manager")
        Hazelcast.shutdownAll()
    }

    @Bean
    fun cacheManager(hazelcastInstance: HazelcastInstance): CacheManager {
        log.debug("Starting HazelcastCacheManager")
        return com.hazelcast.spring.cache.HazelcastCacheManager(hazelcastInstance)
    }

    @Bean
    fun hazelcastInstance(jHipsterProperties: JHipsterProperties): HazelcastInstance {
        log.debug("Configuring Hazelcast")
        val hazelCastInstance = Hazelcast.getHazelcastInstanceByName("kproduct")
        if (hazelCastInstance != null) {
            log.debug("Hazelcast already initialized")
            return hazelCastInstance
        }
        val config = Config()
        config.instanceName = "kproduct"
        config.networkConfig.port = 5701
        config.networkConfig.isPortAutoIncrement = true

        // In development, remove multicast auto-configuration
        if (env.acceptsProfiles(Profiles.of(JHipsterConstants.SPRING_PROFILE_DEVELOPMENT))) {
            System.setProperty("hazelcast.local.localAddress", "127.0.0.1")

            config.networkConfig.join.awsConfig.isEnabled = false
            config.networkConfig.join.multicastConfig.isEnabled = false
            config.networkConfig.join.tcpIpConfig.isEnabled = false
        }
        config.mapConfigs["default"] = initializeDefaultMapConfig(jHipsterProperties)

        // Full reference is available at: https://docs.hazelcast.org/docs/management-center/3.9/manual/html/Deploying_and_Starting.html
        config.managementCenterConfig = initializeDefaultManagementCenterConfig(jHipsterProperties)
        config.mapConfigs["com.sendilkumarn.sample.kproduct.domain.*"] = initializeDomainMapConfig(jHipsterProperties)
        return Hazelcast.newHazelcastInstance(config)
    }

    private fun initializeDefaultManagementCenterConfig(jHipsterProperties: JHipsterProperties): ManagementCenterConfig {
        return ManagementCenterConfig().apply {
            isEnabled = jHipsterProperties.cache.hazelcast.managementCenter.isEnabled
            url = jHipsterProperties.cache.hazelcast.managementCenter.url
            updateInterval = jHipsterProperties.cache.hazelcast.managementCenter.updateInterval
        }
    }

    private fun initializeDefaultMapConfig(jHipsterProperties: JHipsterProperties): MapConfig {
        val mapConfig = MapConfig()

        /*
        Number of backups. If 1 is set as the backup-count for example,
        then all entries of the map will be copied to another JVM for
        fail-safety. Valid numbers are 0 (no backup), 1, 2, 3.
        */
        mapConfig.backupCount = jHipsterProperties.cache.hazelcast.backupCount

        /*
        Valid values are:
        NONE (no eviction),
        LRU (Least Recently Used),
        LFU (Least Frequently Used).
        NONE is the default.
        */
        mapConfig.evictionPolicy = EvictionPolicy.LRU

        /*
        Maximum size of the map. When max size is reached,
        map is evicted based on the policy defined.
        Any integer between 0 and Integer.MAX_VALUE. 0 means
        Integer.MAX_VALUE. Default is 0.
        */
        mapConfig.maxSizeConfig = MaxSizeConfig(0, MaxSizeConfig.MaxSizePolicy.USED_HEAP_SIZE)

        return mapConfig
    }

    private fun initializeDomainMapConfig(jHipsterProperties: JHipsterProperties): MapConfig =
        MapConfig().apply { timeToLiveSeconds = jHipsterProperties.cache.hazelcast.timeToLiveSeconds }

        @Bean
        fun keyGenerator() = PrefixedKeyGenerator(gitProperties, buildProperties)
}
