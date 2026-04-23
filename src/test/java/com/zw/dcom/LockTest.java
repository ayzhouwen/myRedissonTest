package com.zw.dcom;

import com.zw.RedissonDemoApplication;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.Duration;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@SpringBootTest(classes= RedissonDemoApplication.class ,webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class LockTest {
    @Autowired(required = false)
    public RedisTemplate redisTemplate;
    @Autowired(required = false)
    public RedissonClient redissonClient;
    // 模拟业务执行计数器
    private final AtomicInteger actualExecutionCount = new AtomicInteger(0);
    // 模拟总请求数
    private final int TOTAL_REQUESTS = 100000;
    @Test
    public void test1() throws InterruptedException {
        String orderItemNo = "ORDER_123456";
        String actionType = "CREATE";
        String idempotentKey = String.format("workplan:%s:%s", orderItemNo, actionType);

        // 1. 清理环境
        redisTemplate.delete(idempotentKey);

        // 2. 创建固定大小为 10 的线程池
        ExecutorService executor = Executors.newFixedThreadPool(128);
        CountDownLatch latch = new CountDownLatch(TOTAL_REQUESTS);

        System.out.println("开始提交 " + TOTAL_REQUESTS + " 个并发任务...");

        // 3. 提交任务
        for (int i = 0; i < TOTAL_REQUESTS; i++) {
            executor.submit(() -> {
                try {
                    // --- 核心代码开始 ---
                    Boolean locked = redisTemplate.opsForValue().setIfAbsent(idempotentKey, "1", Duration.ofMinutes(1));

                    if (Boolean.TRUE.equals(locked)) {
                        try {
                            // 模拟业务处理耗时 (随机 100ms - 500ms)
                            // 注意：这里的时间必须小于锁的过期时间(1分钟)，否则测试不出锁过期的问题
                            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 200));
                            // 模拟业务逻辑执行
                            int count = actualExecutionCount.incrementAndGet();
                            System.out.println(Thread.currentThread().getName() + " 获取锁成功，正在执行业务... 当前执行次数: " + count);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    } else {
                        System.out.println(Thread.currentThread().getName() + " 获取锁失败，请求被拦截。");
                    }
                    // --- 核心代码结束 ---

                } finally {
                    latch.countDown();
                }
            });
        }

        // 4. 等待所有任务完成
        latch.await();
        executor.shutdown();

        // 5. 验证结果
        System.out.println("================================");
        System.out.println("总请求数: " + TOTAL_REQUESTS);
        System.out.println("实际业务执行次数: " + actualExecutionCount.get());

        // 预期结果：实际业务执行次数应该严格等于 1
        if (actualExecutionCount.get() == 1) {
            System.out.println("✅ 测试通过：幂等性生效，只执行了一次。");
        } else {
            System.out.println("❌ 测试失败：并发导致多次执行！");
        }
    }

}
