package com.zs.elasticsearch;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Company
 * @Author Zs
 * @Date Create in 2019/9/18
 **/
@SpringBootApplication
@MapperScan("com.zs.elasticsearch.mapper")
public class ApplicationRun {
    public static void main(String[] args) {
        SpringApplication.run(ApplicationRun.class, args);
    }
}
