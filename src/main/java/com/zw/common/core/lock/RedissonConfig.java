package com.zw.common.core.lock;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "", name = "demo.cacheType", havingValue = "1")
public class RedissonConfig {
    @Value("${spring.redis.host}")
    private String host;
    @Value("${spring.redis.port}")
    private String port;
    @Value("${spring.redis.database}")
    private Integer database;
    @Value("${spring.redis.password}")
    private String password;

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://"+host+":"+port)
                .setDatabase(database).setPassword(password)
                .setTimeout(1000*20)
        ;
        return (Redisson) Redisson.create(config);
    }

}
