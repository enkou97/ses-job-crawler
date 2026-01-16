package com.sesjob.notification;

import com.sesjob.entity.Job;
import com.sesjob.entity.NotificationSettings;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * メール通知サービス
 */
@Service
@Slf4j
public class EmailNotificationService {

    private final JavaMailSender mailSender;

    @Autowired(required = false)
    public EmailNotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean sendNewJobsNotification(NotificationSettings settings, List<Job> jobs) {
        if (mailSender == null) {
            log.warn("Email notification skipped: JavaMailSender is not configured");
            return false;
        }

        if (!settings.getEmailEnabled() || settings.getEmailAddress() == null) {
            return false;
        }

        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(settings.getEmailAddress());
            message.setSubject(String.format("[SES Job Crawler] 新着案件 %d 件", jobs.size()));
            message.setText(buildEmailBody(jobs));

            mailSender.send(message);
            log.info("Email notification sent to {}: {} jobs", settings.getEmailAddress(), jobs.size());
            return true;
        } catch (Exception e) {
            log.error("Failed to send email notification: {}", e.getMessage());
            return false;
        }
    }

    private String buildEmailBody(List<Job> jobs) {
        StringBuilder sb = new StringBuilder();
        sb.append("新着案件が見つかりました！\n\n");
        sb.append("━━━━━━━━━━━━━━━━━━━━\n\n");

        for (Job job : jobs.subList(0, Math.min(10, jobs.size()))) {
            sb.append("■ ").append(job.getTitle()).append("\n");
            if (job.getMaxPrice() != null) {
                sb.append("  単価: ").append(job.getMaxPrice()).append("万円\n");
            }
            if (job.getLocation() != null) {
                sb.append("  場所: ").append(job.getLocation()).append("\n");
            }
            if (job.getRemoteType() != null) {
                sb.append("  リモート: ").append(job.getRemoteType()).append("\n");
            }
            sb.append("  詳細: ").append(job.getSourceUrl()).append("\n\n");
        }

        if (jobs.size() > 10) {
            sb.append("... 他 ").append(jobs.size() - 10).append(" 件\n\n");
        }

        sb.append("━━━━━━━━━━━━━━━━━━━━\n");
        sb.append("SES Job Crawler\n");

        return sb.toString();
    }
}
