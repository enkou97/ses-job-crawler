package com.sesjob.notification;

import com.sesjob.entity.Job;
import com.sesjob.entity.NotificationSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Slack Webhook 通知サービス
 */
@Service
@Slf4j
public class SlackNotificationService {

    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendNewJobsNotification(NotificationSettings settings, List<Job> jobs) {
        if (!settings.getSlackEnabled() || settings.getSlackWebhookUrl() == null) {
            return false;
        }

        try {
            Map<String, Object> payload = buildSlackPayload(jobs);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);
            restTemplate.postForEntity(settings.getSlackWebhookUrl(), entity, String.class);

            log.info("Slack notification sent: {} jobs", jobs.size());
            return true;
        } catch (Exception e) {
            log.error("Failed to send Slack notification: {}", e.getMessage());
            return false;
        }
    }

    private Map<String, Object> buildSlackPayload(List<Job> jobs) {
        Map<String, Object> payload = new HashMap<>();

        // Header block
        List<Map<String, Object>> blocks = new ArrayList<>();

        Map<String, Object> header = new HashMap<>();
        header.put("type", "header");
        Map<String, String> headerText = new HashMap<>();
        headerText.put("type", "plain_text");
        headerText.put("text", "🔔 新着案件 " + jobs.size() + " 件");
        header.put("text", headerText);
        blocks.add(header);

        // Divider
        Map<String, Object> divider = new HashMap<>();
        divider.put("type", "divider");
        blocks.add(divider);

        // Job blocks (max 5)
        for (Job job : jobs.subList(0, Math.min(5, jobs.size()))) {
            Map<String, Object> section = new HashMap<>();
            section.put("type", "section");

            Map<String, String> text = new HashMap<>();
            text.put("type", "mrkdwn");

            StringBuilder sb = new StringBuilder();
            sb.append("*").append(job.getTitle()).append("*\n");
            if (job.getMaxPrice() != null) {
                sb.append("💰 ").append(job.getMaxPrice()).append("万円");
            }
            if (job.getLocation() != null) {
                sb.append(" | 📍 ").append(job.getLocation());
            }
            if (job.getRemoteType() != null) {
                sb.append(" | 🏠 ").append(job.getRemoteType());
            }
            sb.append("\n<").append(job.getSourceUrl()).append("|詳細を見る>");

            text.put("text", sb.toString());
            section.put("text", text);
            blocks.add(section);

            blocks.add(divider);
        }

        if (jobs.size() > 5) {
            Map<String, Object> context = new HashMap<>();
            context.put("type", "context");
            List<Map<String, String>> elements = new ArrayList<>();
            Map<String, String> elem = new HashMap<>();
            elem.put("type", "mrkdwn");
            elem.put("text", "... 他 " + (jobs.size() - 5) + " 件の新着案件があります");
            elements.add(elem);
            context.put("elements", elements);
            blocks.add(context);
        }

        payload.put("blocks", blocks);
        return payload;
    }
}
