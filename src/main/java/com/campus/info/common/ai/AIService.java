package com.campus.info.common.ai;

import com.campus.info.common.config.AIConfig;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.SocketTimeoutException;

@Service
public class AIService {

    @Resource
    private AIConfig aiConfig;

    private final RestTemplate restTemplate = createRestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10000);
        factory.setReadTimeout(120000);
        return new RestTemplate(factory);
    }

    public String polish(String content) {
        String systemPrompt = "你是一个专业的公告撰写助手。只返回润色后的公告正文，不要添加任何解释、前缀、标题或格式标记。保持原文的正式风格，仅优化语句通顺度和专业性。";
        return callAI(systemPrompt, "请润色以下公告内容：\n\n" + content);
    }

    public String summarize(String announcementsText) {
        String systemPrompt = "你是一个校园公告分析助手。只返回简洁的中文 Markdown 格式总结，列出关键主题和重要信息。使用标题、列表等 Markdown 语法组织内容。不要添加问候语或无关评论。";
        return callAI(systemPrompt, "请总结以下公告列表：\n\n" + announcementsText);
    }

    private String callAI(String systemPrompt, String userMessage) {
        try {
            ObjectNode body = objectMapper.createObjectNode();
            body.put("model", resolveModel());
            body.put("temperature", 0.7);
            body.put("max_tokens", 2000);

            ArrayNode messages = objectMapper.createArrayNode();
            ObjectNode sysMsg = objectMapper.createObjectNode();
            sysMsg.put("role", "system");
            sysMsg.put("content", systemPrompt);
            messages.add(sysMsg);
            ObjectNode userMsg = objectMapper.createObjectNode();
            userMsg.put("role", "user");
            userMsg.put("content", userMessage);
            messages.add(userMsg);
            body.set("messages", messages);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(body), headers);

            String url = aiConfig.getBaseUrl() + "/chat/completions";
            String response = restTemplate.postForObject(url, request, String.class);

            JsonNode root = objectMapper.readTree(response);
            return root.path("choices").get(0).path("message").path("content").asText();
        } catch (Exception e) {
            if (e.getCause() instanceof SocketTimeoutException) {
                String msg = e.getMessage();
                if (msg != null && msg.contains("connect")) {
                    throw new RuntimeException("AI 服务连接超时，请检查 LM Studio 是否已启动");
                }
                throw new RuntimeException("AI 响应超时，内容较长请稍后重试");
            }
            throw new RuntimeException("AI 调用失败: " + e.getMessage(), e);
        }
    }

    private String resolveModel() {
        if (aiConfig.getModel() == null || "auto".equals(aiConfig.getModel())) {
            try {
                String url = aiConfig.getBaseUrl() + "/models";
                String response = restTemplate.getForObject(url, String.class);
                JsonNode root = objectMapper.readTree(response);
                JsonNode data = root.path("data");
                if (data.isArray() && data.size() > 0) {
                    return data.get(0).path("id").asText();
                }
            } catch (Exception ignored) {
            }
            return "default";
        }
        return aiConfig.getModel();
    }
}
