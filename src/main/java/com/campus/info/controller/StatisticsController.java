package com.campus.info.controller;

import com.campus.info.common.api.ApiResult;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;

@RestController
@RequestMapping("/api/statistics")
public class StatisticsController {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @GetMapping("/announcements")
    public ApiResult<List<Map<String, Object>>> announcementStats(@RequestParam(defaultValue = "30") int period) {
        String sql = "SELECT DATE(create_time) AS date, COUNT(*) AS count FROM biz_announcement "
                + "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                + "GROUP BY DATE(create_time) ORDER BY date";
        return ApiResult.success(jdbcTemplate.queryForList(sql, period));
    }

    @GetMapping("/users")
    public ApiResult<List<Map<String, Object>>> userStats(@RequestParam(defaultValue = "365") int period) {
        String sql;
        if (period <= 31) {
            sql = "SELECT DATE(create_time) AS date, COUNT(*) AS count FROM sys_user "
                    + "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                    + "GROUP BY DATE(create_time) ORDER BY date";
        } else {
            sql = "SELECT DATE_FORMAT(create_time, '%Y-%m') AS month, COUNT(*) AS count FROM sys_user "
                    + "WHERE create_time >= DATE_SUB(CURDATE(), INTERVAL ? DAY) "
                    + "GROUP BY DATE_FORMAT(create_time, '%Y-%m') ORDER BY month";
        }
        return ApiResult.success(jdbcTemplate.queryForList(sql, period));
    }
}
