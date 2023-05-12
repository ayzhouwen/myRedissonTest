package com.zw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

/**
 * 启动程序
 *
 * @author kingsmartsi
 */
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class RedissonDemoApplication {
    public static void main(String[] args) {
        SpringApplication.run(RedissonDemoApplication.class, args);
    }

}
