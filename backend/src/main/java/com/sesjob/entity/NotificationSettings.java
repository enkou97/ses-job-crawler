package com.sesjob.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 通知設定エンティティ
 */
@Entity
@Table(name = "notification_settings")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // メール通知
    @Column(name = "email_enabled")
    @Builder.Default
    private Boolean emailEnabled = false;

    @Column(name = "email_address", length = 200)
    private String emailAddress;

    // LINE通知
    @Column(name = "line_enabled")
    @Builder.Default
    private Boolean lineEnabled = false;

    @Column(name = "line_token", length = 200)
    private String lineToken;

    // Slack通知
    @Column(name = "slack_enabled")
    @Builder.Default
    private Boolean slackEnabled = false;

    @Column(name = "slack_webhook_url", length = 500)
    private String slackWebhookUrl;

    // 通知条件
    @Column(name = "min_price")
    private Integer minPriceThreshold;

    @Column(name = "skills_filter", length = 500)
    private String skillsFilter; // カンマ区切り

    @Column(name = "remote_only")
    @Builder.Default
    private Boolean remoteOnly = false;

    // スケジュール設定
    @Column(name = "notify_interval_hours")
    @Builder.Default
    private Integer notifyIntervalHours = 6;

    @Column(name = "last_notified_at")
    private LocalDateTime lastNotifiedAt;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
