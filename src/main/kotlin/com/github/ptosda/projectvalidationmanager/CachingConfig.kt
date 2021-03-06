package com.github.ptosda.projectvalidationmanager

import com.github.ptosda.projectvalidationmanager.model.DependencyInfo
import org.ehcache.PersistentCacheManager
import org.ehcache.config.builders.CacheConfigurationBuilder
import org.ehcache.config.builders.CacheManagerBuilder
import org.ehcache.config.builders.ResourcePoolsBuilder
import org.ehcache.config.units.MemoryUnit
import org.springframework.cache.annotation.EnableCaching
import org.springframework.context.annotation.Configuration
import java.io.File
import java.nio.file.Paths
import javax.annotation.PreDestroy

@Configuration
@EnableCaching
class CachingConfig {
    init{
        initDependenciesCache()
    }

    private final fun initDependenciesCache() {
        val cacheDirectory = File(Paths.get("src","main","resources","cache").toString()).absolutePath
        persistentCacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                .with(CacheManagerBuilder.persistence(cacheDirectory))
                .withCache(dependenciesCache,
                        CacheConfigurationBuilder.newCacheConfigurationBuilder(
                                String::class.java,
                                DependencyInfo::class.java,
                                ResourcePoolsBuilder.newResourcePoolsBuilder().disk(50, MemoryUnit.MB, true)
                        )
                )
                .build(true)
    }

    @PreDestroy
    private final fun closeCacheManager(){
        persistentCacheManager.close()
    }

    companion object {
        const val dependenciesCache = "dependenciesCache"

        lateinit var persistentCacheManager  : PersistentCacheManager

        fun getDependenciesCache() = persistentCacheManager.getCache(dependenciesCache, String::class.java, DependencyInfo::class.java)
    }
}