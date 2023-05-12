package com.zw.dcom;

import cn.hutool.core.util.RandomUtil;
import com.zw.RedissonDemoApplication;
import com.zw.common.core.lock.MyLockUtil;
import com.zw.common.utils.CacheUtil;
import com.zw.common.utils.MyDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

@Slf4j
@SpringBootTest(classes= RedissonDemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)

public class CacheTest {
   @Resource
    private CacheUtil cacheUtil;
   @Resource
   private MyLockUtil myLockUtil;

    @Test
    public void test1(){
        for (int i = 0; i <50 ; i++) {
            cacheUtil.setCacheObject("AK"+i,"我在哦"+i, 10,TimeUnit.MINUTES);
            int finalI = i;
            Thread thread=new Thread(()->{
                for (int j = 0; j <1*60*60 ; j++) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    Object o=   cacheUtil.getCacheObject("AK"+ finalI);
                    log.info("缓存值:{}",o);
                }
            });
            thread.start();
        }
        try {
            Thread.sleep(1000*60*20);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    static Integer sum=0;
    @Test
    public void  test2(){
        long stime=System.currentTimeMillis();
        Lock lock=myLockUtil.getLock("testLock");
        int size=1000*10;
        CountDownLatch c=new CountDownLatch(size);
        for (int i = 0; i <size ; i++) {
            Thread t=new Thread(()->{
                try {
                    myLockUtil.lock(lock);
                    sum++;
                    log.info("当前sum值:"+sum);

                } catch (Exception e) {
                    throw new RuntimeException(e);
                } finally {
                    myLockUtil.unlock(lock);
                    c.countDown();
                }
            });
            t.start();

        }

        try {
            c.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        log.info(MyDateUtils.execTime("线程累加值:"+sum,stime));
    }
}
