package com.sesjob.notification;

import com.sesjob.entity.Job;
import com.sesjob.entity.NotificationSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LINE Notify 通知サービス
 */
@Service
@Slf4j
public class LineNotificationService {

    private static final String LINE_NOTIFY_API = "https://notify-api.line.me/api/notify";
    private final RestTemplate restTemplate = new RestTemplate();

    public boolean sendNewJobsNotification(NotificationSettings settings, List<Job> jobs) {
        if (!settings.getLineEnabled() || settings.getLineToken() == null) {
            return false;
        }

        try {
            String message = buildLineMessage(jobs);

            org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
            headers.set("Authorization", "Bearer " + settings.getLineToken());
            headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

            String body = "message=" + java.net.URLEncoder.encode(message, "UTF-8");

            org.springframework.http.HttpEntity<String> entity = new org.springframework.http.HttpEntity<>(body,
                    headers);

            restTemplate.postForEntity(LINE_NOTIFY_API, entity, String.class);
            log.info("LINE notification sent: {} jobs", jobs.size());
            return true;
        } catch (Exception e) {
            log.error("Failed to send LINE notification: {}", e.getMessage());
            return false;
        }
    }

    private String buildLineMessage(List<Job> jobs) {
        StringBuilder sb = new StringBuilder();
        sb.append("\n🔔 新着案件 ").append(jobs.size()).append(" 件\n\n");

        for (Job job : jobs.subList(0, Math.min(5, jobs.size()))) {
            sb.append("📋 ").append(job.getTitle()).append("\n");
            if (job.getMaxPrice() != null) {
                sb.append("💰 ").append(job.getMaxPrice()).append("万円\n");
            }
            sb.append("🔗 ").append(job.getSourceUrl()).append("\n\n");
        }

        if (jobs.size() > 5) {
            sb.append("... 他 ").append(jobs.size() - 5).append(" 件");
        }

        return sb.toString();
    }
}
