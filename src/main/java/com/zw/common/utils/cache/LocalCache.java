package com.zw.common.utils.cache;


import com.zw.common.core.pool.MyThreadFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

@Component
@Slf4j
public class LocalCache {

    //默认的缓存容量
    private static int DEFAULT_CAPACITY = 16;
    /**
     * 按照每个平均元素占用1kb ，缓存占用32m
     */
    private static int MAX_CAPACITY = 1024 * 32;
    private ReentrantLock lock = new ReentrantLock();    private static Map<String, CacheData> linkedHashMap = new LinkedHashMap<String, CacheData>(DEFAULT_CAPACITY, 0.75f, true) {

        @Override
        protected boolean removeEldestEntry(Map.Entry eldest) {
            //缓存淘汰
            return MAX_CAPACITY < linkedHashMap.size();
        }
    };
    private ScheduledExecutorService scheduledExecutorService =
            new ScheduledThreadPoolExecutor(Runtime.getRuntime().availableProcessors() * 2, new MyThreadFactory("localCacheCleanPool"));
    private LocalCache() {
    }

    public static LocalCache getInstance() {
        return CacheLocal.cache;
    }

    public void put(String key, Object value, Integer timeout, final TimeUnit timeUnit) {
        lock.lock();
        try {
            CacheData data = new CacheData();
            data.setKey(key);
            data.setValue(value);
            if (timeout!=null&&timeUnit!=null){
                linkedHashMap.put(key, data);
                removeAfterExpireTime(key, timeout,timeUnit);
            } else {
                linkedHashMap.put(key, data);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 过期删除
     */
    public CacheData get(String key) {
        lock.lock();
        try {
            return linkedHashMap.getOrDefault(key, new CacheData());
        } finally {
            lock.unlock();
        }
    }

    private void removeAfterExpireTime(String key, long expireTime,TimeUnit timeUnit) {
        scheduledExecutorService.schedule(() -> {
            try {
                linkedHashMap.remove(key);
            } catch (Exception e) {
                log.error("缓存清除执行数据异常:", e);
            }
        }, expireTime, timeUnit);
    }

    public void delete(String key) {
        lock.lock();
        try {
            linkedHashMap.remove(key);
        } finally {
            lock.unlock();
        }

    }

    public long delete(Collection collection) {
        lock.lock();
        try {
            for (Object o : collection) {
                linkedHashMap.remove(o);
            }
            return collection.size();
        } finally {
            lock.unlock();
        }

    }

    public Collection<String> keys(final String pattern) {
        List<String> result = new ArrayList<>();
        lock.lock();
        try {
            linkedHashMap.forEach((k, v) -> {
                if (k.startsWith(pattern)) {
                    result.add(k);
                }
            });

        } finally {
            lock.unlock();
        }

        return result;
    }

    private static class CacheLocal {
        private static LocalCache cache = new LocalCache();
    }




}
