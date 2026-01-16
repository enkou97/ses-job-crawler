package com.sesjob.scheduler;

import com.sesjob.entity.Job;
import com.sesjob.entity.NotificationSettings;
import com.sesjob.notification.NotificationService;
import com.sesjob.repository.JobRepository;
import com.sesjob.repository.NotificationSettingsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * クローラー定期実行スケジューラー
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class CrawlerScheduler {

    private final JobRepository jobRepository;
    private final NotificationSettingsRepository settingsRepository;
    private final NotificationService notificationService;

    /**
     * 定期的に新着案件をチェックして通知
     * デフォルト: 6時間ごと (cron式で設定可能)
     */
    @Scheduled(cron = "${crawler.schedule.cron:0 0 */6 * * *}")
    public void checkAndNotifyNewJobs() {
        log.info("=== Scheduled job check started ===");

        try {
            NotificationSettings settings = settingsRepository.getOrCreateSettings();

            // 最後の通知以降の新着案件を取得
            LocalDateTime since = settings.getLastNotifiedAt();
            if (since == null) {
                // 初回は24時間前から
                since = LocalDateTime.now().minusHours(24);
            }

            List<Job> newJobs = jobRepository.findNewJobsSince(since);

            if (newJobs.isEmpty()) {
                log.info("No new jobs since {}", since);
                return;
            }

            log.info("Found {} new jobs since {}", newJobs.size(), since);

            // 通知を送信
            notificationService.notifyNewJobs(newJobs);

        } catch (Exception e) {
            log.error("Error in scheduled job check: {}", e.getMessage(), e);
        }

        log.info("=== Scheduled job check completed ===");
    }

    /**
     * 毎日午前9時に統計サマリーを通知（オプション）
     */
    @Scheduled(cron = "${crawler.schedule.daily-summary:0 0 9 * * *}")
    public void sendDailySummary() {
        log.info("Daily summary check (not implemented yet)");
        // TODO: 実装時に統計サマリー通知を追加
    }
}
