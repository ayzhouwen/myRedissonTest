package com.zw.common.utils;
import com.zw.common.utils.cache.LocalCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存工具类 ,注意如果代码中使用了该方法，请确保在redis缓存和其他缓存都增加对应的功能
 * @author kingsmartsi
 **/
@SuppressWarnings(value = {"unchecked", "rawtypes"})
@Component
@Slf4j
public class CacheUtil {
    @Autowired(required = false)
    public RedisTemplate redisTemplate;
    @Value("${demo.cacheType}")
    public String cacheType;
    @Autowired
    public LocalCache localCache;
    private final  static String UnsupportError="缓存类型不支持";
    /**
     * 缓存基本的对象，Integer、String、实体类等
     * @param key   缓存的键值
     * @param value 缓存的值
     */
    public <T> void setCacheObject(final String key, final T value) {
        switch (Integer.valueOf(cacheType)) {
            case 0:
                localCache.put(key, value, null,null);
                break;
            case 1:
                redisTemplate.opsForValue().set(key, value);
                break;
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 缓存基本的对象，Integer、String、实体类等
     *
     * @param key      缓存的键值
     * @param value    缓存的值
     * @param timeout  时间
     * @param timeUnit 时间颗粒度
     */
    public <T> void setCacheObject(final String key, final T value, final Integer timeout, final TimeUnit timeUnit) {

        switch (Integer.valueOf(cacheType)) {
            case 0:
                localCache.put(key, value, timeout,timeUnit);
                break;
            case 1:
                redisTemplate.opsForValue().set(key, value, timeout, timeUnit);
                break;
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                return expire(key, timeout, TimeUnit.SECONDS);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 设置有效时间
     *
     * @param key     Redis键
     * @param timeout 超时时间
     * @param unit    时间单位
     * @return true=设置成功；false=设置失败
     */
    public boolean expire(final String key, final long timeout, final TimeUnit unit) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                return redisTemplate.expire(key, timeout, unit);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 获得缓存的基本对象。
     *
     * @param key 缓存键值
     * @return 缓存键值对应的数据
     */
    public <T> T getCacheObject(final String key) {
        switch (Integer.valueOf(cacheType)) {
            case 0:
                return (T) localCache.get(key).getValue();
            case 1:
                ValueOperations<String, T> operation = redisTemplate.opsForValue();
                return operation.get(key);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 删除单个对象
     *
     * @param key
     */
    public boolean deleteObject(final String key) {
        switch (Integer.valueOf(cacheType)) {
            case 0:
                localCache.delete(key);
                return true;
            case 1:
                return redisTemplate.delete(key);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 删除集合对象
     *
     * @param collection 多个对象
     * @return
     */
    public long deleteObject(final Collection collection) {
        switch (Integer.valueOf(cacheType)) {
            case 0:
                return localCache.delete(collection);
            case 1:
                return redisTemplate.delete(collection);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 缓存List数据
     *
     * @param key      缓存的键值
     * @param dataList 待缓存的List数据
     * @return 缓存的对象
     */
    public <T> long setCacheList(final String key, final List<T> dataList) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                Long count = redisTemplate.opsForList().rightPushAll(key, dataList);
                return count == null ? 0 : count;
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 获得缓存的list对象
     *
     * @param key 缓存的键值
     * @return 缓存键值对应的数据
     */
    public <T> List<T> getCacheList(final String key) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                return redisTemplate.opsForList().range(key, 0, -1);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 缓存Set
     *
     * @param key     缓存键值
     * @param dataSet 缓存的数据
     * @return 缓存数据的对象
     */
    public <T> BoundSetOperations<String, T> setCacheSet(final String key, final Set<T> dataSet) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                BoundSetOperations<String, T> setOperation = redisTemplate.boundSetOps(key);
                Iterator<T> it = dataSet.iterator();
                while (it.hasNext()) {
                    setOperation.add(it.next());
                }
                return setOperation;
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 获得缓存的set
     *
     * @param key
     * @return
     */
    public <T> Set<T> getCacheSet(final String key) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                return redisTemplate.opsForSet().members(key);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 缓存Map
     *
     * @param key
     * @param dataMap
     */
    public <T> void setCacheMap(final String key, final Map<String, T> dataMap) {

        switch (Integer.valueOf(cacheType)) {
            case 1:
                if (dataMap != null) {
                    redisTemplate.opsForHash().putAll(key, dataMap);
                }
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 获得缓存的Map
     *
     * @param key
     * @return
     */
    public <T> Map<String, T> getCacheMap(final String key) {

        switch (Integer.valueOf(cacheType)) {
            case 1:
                return redisTemplate.opsForHash().entries(key);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }
    /**
     * 往Hash中存入数据
     *
     * @param key   Redis键
     * @param hKey  Hash键
     * @param value 值
     */
    public <T> void setCacheMapValue(final String key, final String hKey, final T value) {

        switch (Integer.valueOf(cacheType)) {
            case 1:
                redisTemplate.opsForHash().put(key, hKey, value);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }

    /**
     * 获取Hash中的数据
     *
     * @param key  Redis键
     * @param hKey Hash键
     * @return Hash中的对象
     */
    public <T> T getCacheMapValue(final String key, final String hKey) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                HashOperations<String, String, T> opsForHash = redisTemplate.opsForHash();
                return opsForHash.get(key, hKey);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 删除Hash中的数据
     *
     * @param key
     * @param hKey
     */
    public void delCacheMapValue(final String key, final String hKey) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                HashOperations hashOperations = redisTemplate.opsForHash();
                hashOperations.delete(key, hKey);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 获取多个Hash中的数据
     *
     * @param key   Redis键
     * @param hKeys Hash键集合
     * @return Hash对象集合
     */
    public <T> List<T> getMultiCacheMapValue(final String key, final Collection<Object> hKeys) {
        switch (Integer.valueOf(cacheType)) {
            case 1:
                return redisTemplate.opsForHash().multiGet(key, hKeys);
            default:
                throw new RuntimeException(UnsupportError);
        }
    }

    /**
     * 获得缓存的基本对象列表
     *
     * @param pattern 字符串前缀
     * @return 对象列表
     */
    public Collection<String> keys(final String pattern) {
        switch (Integer.valueOf(cacheType)) {
            case 0:
                return localCache.keys(pattern);
            case 1:
                return redisTemplate.keys(pattern);
            default:
                throw new RuntimeException(UnsupportError);
        }

    }
}
