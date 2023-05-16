package com.zw.common.core.lock;
import com.zw.common.constant.CacheConstants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 如果使用了redis使用分布式锁,否则则使用本地锁
 */
@Component
@Slf4j
public class MyLockUtil {
    @Autowired(required = false)
    public RedissonClient redissonClient;
    @Value("${demo.cacheType}")
    public String cacheType;

    /**
     * 获取锁
     * @param key
     * @return
     */
    public  Lock getLock(String key){
        Lock lock = null;
        int n=Integer.valueOf(cacheType);
        switch (n) {
            case 0:
                lock=new ReentrantLock();
                break;
            case 1:
                lock= redissonClient.getLock(key);
                break;
            default:
                throw new RuntimeException(CacheConstants.UnsupportError);
        }
        return lock;
    };

    /**
     * 尝试加锁
     * @param lock
     * @param timeOut 单位毫秒
     */
    public boolean tryLock(Lock lock,long timeOut){
        int n=Integer.valueOf(cacheType);
        switch (n) {
            case 0:
            case 1:
                try {
                    return lock.tryLock(timeOut, TimeUnit.MILLISECONDS);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            default:
                throw new RuntimeException(CacheConstants.UnsupportError);
        }
    };

    /**
     * 阻塞加锁
     * @param lock
     * @return
     */
    public void lock(Lock lock){
        int n=Integer.valueOf(cacheType);
        switch (n) {
            case 0:
            case 1:
                    lock.lock();
                break;
            default:
                throw new RuntimeException(CacheConstants.UnsupportError);
        }
    };

    /**
     * 释放锁
     * @param lock
     */
    public void unlock(Lock lock){
        int n=Integer.valueOf(cacheType);
        switch (n) {
            case 0:
            case 1:
                try {
                    lock.unlock();
                } catch (Exception e) {
                    log.error("释放锁异常:",e);
                }
                break;
            default:
                throw new RuntimeException(CacheConstants.UnsupportError);
        }
    };


}
