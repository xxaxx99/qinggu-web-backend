package com.lzh.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存
 * @author lzh
 */
@Component
public class CacheManager {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 本地缓存
     */
    Cache<String, Object> localCache = Caffeine.newBuilder()
            .expireAfterWrite(100, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, Object value) {
        localCache.put(key, value);
        redisTemplate.opsForValue().set(key, value, 100, TimeUnit.MINUTES);
    }

    /**
     * 读缓存
     *
     * @param key
     * @return
     */
    public Object get(String key) {
        // 先从本地缓存中获取
        Object value = localCache.getIfPresent(key);
        if (value != null) {
            return value;
        }

        // 本地缓存未命中，尝试从 Redis 获取
        value = redisTemplate.opsForValue().get(key);
        if (value != null) {
            // 将 redis 的值写入到本地缓存
            localCache.put(key, value);
        }

        return value;
    }

    /**
     * 一处缓存
     */
    public void delete() {
        localCache.invalidateAll();

        // 构建模糊匹配的键前缀，以便删除相关的 Redis 缓存项
        String redisKeyPrefix = "generator:page:";

        // 获取匹配指定前缀的键集合
        Set<String> keys = redisTemplate.keys(redisKeyPrefix + "*");

        // 如果 keys 为 null 或为空集合，则无需执行删除操作
        if (keys == null || keys.isEmpty()) {
            return;
        }
        redisTemplate.delete(keys);
    }

}
