package com.zw.common.core.lock;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@ConditionalOnProperty(prefix = "", name = "demo.cacheType", havingValue = "1")
public class RedissonClusterConfig {
    // 如果这样写只能写字符串，不能写列表否则报错
//    @Value("${spring.redis.cluster.nodes}")
//    private List<String> clusterNodes;
    @Autowired
    private RedisProperties redisProperties;

    // 2. 注入密码 (集群模式下通常也需要密码)
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public RedissonClient redisson() {
        Config config = new Config();

        // 2. 获取集群配置
        RedisProperties.Cluster cluster = redisProperties.getCluster();

        // 3. 获取节点列表 (List<String>)
        List<String> nodes = cluster.getNodes();

        // 4. 构建集群服务器配置
        // 注意：Redisson 的 addNodeAddress 需要 "redis://" 前缀
        config.useClusterServers()
                .addNodeAddress(nodes.stream()
                        .map(node -> node.startsWith("redis://") ? node : "redis://" + node)
                        .toArray(String[]::new))
                // 5. 设置密码 (从 redisProperties 获取)
                .setPassword(redisProperties.getPassword());

        // (可选) 设置超时时间，Redisson 单位是毫秒
        // config.useClusterServers().setTimeout(redisProperties.getTimeout().toMillis());

        return Redisson.create(config);
    }

}
