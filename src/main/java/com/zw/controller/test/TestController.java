package com.zw.controller.test;


import com.zw.common.core.lock.MyLockUtil;
import com.zw.common.domain.AjaxResult;
import com.zw.common.utils.CacheUtil;
import com.zw.common.utils.MyDateUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;

/**
 * redisson测试
 * 总结：由于jdk8 windws下nio机制很拉跨，很可能是windws下创建1000以上线程放方式，加锁时会超时，
 * 解决办法，用线程池模式，或者用centos7.5
 * 注意：linux下创建50000线程去加锁，稳如狗，更不用说用线程池了
 * centsos 7.5 4g 8线程 虚拟机(宿主机 dell g3 Intel(R)Core(TM) i5-9300H 32g内存 win10 64位专业版,512固态 )测试结果
 * 2023-05-16 11:34:30.518  INFO 105097 --- [io-19001-exec-1] com.zw.controller.test.TestController    : 线程累加值:50000:111839.0毫秒,111.84秒,1.86分钟 (注意这个是创建50000个线程进行加锁)
 * 2023-05-16 11:38:56.553  INFO 105097 --- [io-19001-exec-3] com.zw.controller.test.TestController    : 线程池大小：8线程累加值:50000:40671.0毫秒,40.67秒,0.68分钟
 * 2023-05-16 11:40:23.625  INFO 105097 --- [io-19001-exec-4] com.zw.controller.test.TestController    : 线程池大小：16线程累加值:50000:39903.0毫秒,39.9秒,0.67分钟
 * 2023-05-16 11:41:40.756  INFO 105097 --- [io-19001-exec-5] com.zw.controller.test.TestController    : 线程池大小：32线程累加值:50000:42066.0毫秒,42.07秒,0.7分钟
 *
 *
 * @author kingsmartsi
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    @Resource
    private CacheUtil cacheUtil;
    @Resource
    private MyLockUtil myLockUtil;
    Integer sum=0;
    @GetMapping("/redisson-thread")
    public AjaxResult redissonThread(Integer size)  {
        long stime=System.currentTimeMillis();
        Lock lock=myLockUtil.getLock("testLock");
        if (size==null){
            size=1;
        }
        CountDownLatch c=new CountDownLatch(size);

        for (int i = 0; i <size ; i++) {
            Thread t=new Thread(()->{
                try {
                    myLockUtil.lock(lock);
                    sum++;
                    log.info("当前sum值:"+sum);

                } catch (Exception e) {
                    log.error("加锁异常:",e);
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
        String str=MyDateUtils.execTime("线程累加值:"+sum,stime);
        log.info(str);
        sum=0;
        return AjaxResult.success(str);
    }


    @GetMapping("/redisson-pool")
    public AjaxResult redissonPool(Integer size,Integer poolSize)  {
        long stime=System.currentTimeMillis();
        Lock lock=myLockUtil.getLock("testLock");
        if (size==null){
            size=1;
        }
        if (poolSize==null){
            poolSize=8;
        }
        CountDownLatch c=new CountDownLatch(size);
        ExecutorService exec = Executors.newFixedThreadPool(poolSize);
        for (int i = 0; i <size ; i++) {
            exec.execute(()->{
                try {
                    myLockUtil.lock(lock);
                    sum++;
                    log.info("当前sum值:"+sum);

                } catch (Exception e) {
                    log.error("加锁异常:",e);
                    throw new RuntimeException(e);
                } finally {
                    myLockUtil.unlock(lock);
                    c.countDown();
                }
            });

        }
        try {
            c.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        String str=MyDateUtils.execTime("线程池大小："+poolSize+ "线程累加值:"+sum,stime);
        log.info(str);
        sum=0;
        exec.shutdown();
        return AjaxResult.success(str);
    }


}
