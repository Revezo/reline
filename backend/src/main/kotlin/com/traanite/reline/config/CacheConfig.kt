package com.traanite.reline.config

import com.google.common.cache.CacheBuilder
import org.springframework.cache.Cache
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurer
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cache.concurrent.ConcurrentMapCache
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Configuration
import java.util.concurrent.TimeUnit


@EnableCaching
@Configuration
class CacheConfig : CachingConfigurer {

    override fun cacheManager(): CacheManager {
        val cacheManager: ConcurrentMapCacheManager = object : ConcurrentMapCacheManager() {
            override fun createConcurrentMapCache(name: String): Cache {
                return ConcurrentMapCache(
                    name,
                    CacheBuilder.newBuilder()
                        .expireAfterWrite(6, TimeUnit.HOURS)
                        .maximumSize(100).build<Any, Any>().asMap(), false
                )
            }
        }
        return cacheManager;
    }
}