package com.campus.info;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@MapperScan("com.campus.info.mapper")
@EnableScheduling
@EnableCaching
public class CampusInfoApplication {
    public static void main(String[] args) {
        SpringApplication.run(CampusInfoApplication.class, args);
        System.out.println("====== 校园公告与资讯发布平台 启动成功 ======");
    }
}