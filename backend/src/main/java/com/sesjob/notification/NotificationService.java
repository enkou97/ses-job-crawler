package com.sesjob.notification;

import com.sesjob.entity.Job;
import com.sesjob.entity.NotificationSettings;
import com.sesjob.repository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 通知サービス - 統合管理
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final NotificationSettingsRepository settingsRepository;
    private final EmailNotificationService emailService;
    private final LineNotificationService lineService;
    private final SlackNotificationService slackService;

    /**
     * 新着案件を通知
     */
    @Transactional
    public void notifyNewJobs(List<Job> allJobs) {
        NotificationSettings settings = settingsRepository.getOrCreateSettings();

        // フィルタリング
        List<Job> filteredJobs = filterJobs(allJobs, settings);

        if (filteredJobs.isEmpty()) {
            log.info("No jobs matching notification criteria");
            return;
        }

        log.info("Sending notifications for {} jobs", filteredJobs.size());

        // 各チャネルで通知
        boolean emailSent = emailService.sendNewJobsNotification(settings, filteredJobs);
        boolean lineSent = lineService.sendNewJobsNotification(settings, filteredJobs);
        boolean slackSent = slackService.sendNewJobsNotification(settings, filteredJobs);

        // 最終通知日時を更新
        if (emailSent || lineSent || slackSent) {
            settings.setLastNotifiedAt(LocalDateTime.now());
            settingsRepository.save(settings);
            log.info("Notification completed. Email: {}, LINE: {}, Slack: {}",
                    emailSent, lineSent, slackSent);
        }
    }

    /**
     * 設定条件に基づいてフィルタリング
     */
    private List<Job> filterJobs(List<Job> jobs, NotificationSettings settings) {
        return jobs.stream()
                .filter(job -> {
                    // 単価フィルター
                    if (settings.getMinPriceThreshold() != null && job.getMaxPrice() != null) {
                        if (job.getMaxPrice() < settings.getMinPriceThreshold()) {
                            return false;
                        }
                    }

                    // リモートフィルター
                    if (Boolean.TRUE.equals(settings.getRemoteOnly())) {
                        if (job.getRemoteType() == null ||
                                job.getRemoteType() == Job.RemoteType.NONE) {
                            return false;
                        }
                    }

                    // スキルフィルター
                    if (settings.getSkillsFilter() != null && !settings.getSkillsFilter().isBlank()) {
                        List<String> requiredSkills = Arrays.stream(settings.getSkillsFilter().split(","))
                                .map(String::trim)
                                .map(String::toLowerCase)
                                .filter(s -> !s.isEmpty())
                                .collect(Collectors.toList());

                        if (!requiredSkills.isEmpty() && job.getRequiredSkills() != null) {
                            boolean hasMatchingSkill = job.getRequiredSkills().stream()
                                    .anyMatch(skill -> requiredSkills.stream()
                                            .anyMatch(req -> skill.toLowerCase().contains(req)));
                            if (!hasMatchingSkill) {
                                return false;
                            }
                        }
                    }

                    return true;
                })
                .collect(Collectors.toList());
    }

    /**
     * 設定取得
     */
    public NotificationSettings getSettings() {
        return settingsRepository.getOrCreateSettings();
    }

    /**
     * 設定更新
     */
    @Transactional
    public NotificationSettings updateSettings(
            Boolean emailEnabled, String emailAddress,
            Boolean lineEnabled, String lineToken,
            Boolean slackEnabled, String slackWebhookUrl,
            Integer minPriceThreshold, String skillsFilter,
            Boolean remoteOnly, Integer notifyIntervalHours) {

        NotificationSettings settings = settingsRepository.getOrCreateSettings();

        if (emailEnabled != null)
            settings.setEmailEnabled(emailEnabled);
        if (emailAddress != null)
            settings.setEmailAddress(emailAddress);
        if (lineEnabled != null)
            settings.setLineEnabled(lineEnabled);
        if (lineToken != null)
            settings.setLineToken(lineToken);
        if (slackEnabled != null)
            settings.setSlackEnabled(slackEnabled);
        if (slackWebhookUrl != null)
            settings.setSlackWebhookUrl(slackWebhookUrl);
        if (minPriceThreshold != null)
            settings.setMinPriceThreshold(minPriceThreshold);
        if (skillsFilter != null)
            settings.setSkillsFilter(skillsFilter);
        if (remoteOnly != null)
            settings.setRemoteOnly(remoteOnly);
        if (notifyIntervalHours != null)
            settings.setNotifyIntervalHours(notifyIntervalHours);

        return settingsRepository.save(settings);
    }

    /**
     * テスト通知送信
     */
    public boolean sendTestNotification(String channel) {
        NotificationSettings settings = settingsRepository.getOrCreateSettings();

        // ダミー案件でテスト
        Job testJob = Job.builder()
                .title("【テスト通知】SES Job Crawler 動作確認")
                .maxPrice(80)
                .location("東京")
                .remoteType(Job.RemoteType.FULL)
                .sourceUrl("https://example.com/test")
                .build();

        List<Job> testJobs = List.of(testJob);

        return switch (channel.toLowerCase()) {
            case "email" -> emailService.sendNewJobsNotification(settings, testJobs);
            case "line" -> lineService.sendNewJobsNotification(settings, testJobs);
            case "slack" -> slackService.sendNewJobsNotification(settings, testJobs);
            default -> false;
        };
    }
}
