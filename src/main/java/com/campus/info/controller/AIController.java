package com.campus.info.controller;

import com.campus.info.common.ai.AIService;
import com.campus.info.common.api.ApiResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AIController {

    @Resource
    private AIService aiService;

    @PostMapping("/polish")
    public ApiResult<String> polish(@RequestBody Map<String, String> body) {
        String content = body.get("content");
        if (content == null || content.isBlank()) {
            return ApiResult.error("内容不能为空");
        }
        String polished = aiService.polish(content);
        return ApiResult.success(polished);
    }
}
