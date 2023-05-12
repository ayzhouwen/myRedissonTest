package com.zw.common.core.plus.thread;

import cn.hutool.core.util.StrUtil;
import com.zw.common.core.pool.MyThreadFactory;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 任务执行池
 */
@Slf4j
public class TaskExecutePool {
    /**
     * 线程数
     */
    private Integer threadNum;

    /**
     * 执行器名称
     */
    private String name;
    /**
     * 单线程池列表
     */

    /**
     * 队列大小
     */
    private Integer poolSize;
    private List<ThreadPoolExecutor> poolExecutors=new ArrayList<>();

    public TaskExecutePool(Integer threadNum, String name, Integer poolSize){
        this.threadNum=threadNum;
        this.name=name;
        this.poolSize=poolSize;
        if (threadNum==null||threadNum<=0){
           log.error("任务执行池线程数量不能为空");
           throw  new RuntimeException("任务执行池线程数量不能为空");
        }
        if (StrUtil.isEmpty(name)){
            log.error("任务执行池名称不能为空");
            throw  new RuntimeException("任务执行池名称不能为空");
        }

        if (poolSize==null){
            log.error("任务执行池队列大小不能为空");
            throw  new RuntimeException("任务执行池队列大小不能为空");

        }
        for (int i = 0; i <this.threadNum ; i++) {
            ThreadPoolExecutor executor=new ThreadPoolExecutor(1, 1, 600,
                    TimeUnit.SECONDS, new LinkedBlockingQueue<>(poolSize), new MyThreadFactory(this.name+i));
            poolExecutors.add(executor);
        }
    }

    /**
     * 按分片建执行任务
     * @param sliceKey 分片建
     * @param runnable
     */
    public void execTask(String sliceKey,Runnable runnable){
        try {
            ThreadPoolExecutor executor =poolExecutors.get(Math.abs(sliceKey.hashCode()%threadNum));
            executor.execute(runnable);
        } catch (Exception e) {
            log.error("执行任务sliceKey:{} 异常:",sliceKey,e);
        }
    }

}
