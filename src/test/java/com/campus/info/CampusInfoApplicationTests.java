package com.campus.info;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.StringRedisTemplate;

import javax.sql.DataSource;
import java.sql.SQLException;

@SpringBootTest
class CampusInfoApplicationTests {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;


    @Test
    void testDatabaseConnection() throws SQLException {
        System.out.println("数据库连接对象: " + dataSource.getConnection());
    }

    @Test
    void testRedisConnection() {
        stringRedisTemplate.opsForValue().set("test_key", "Hello Redis!");
        System.out.println("Redis获取值: " + stringRedisTemplate.opsForValue().get("test_key"));
    }
}