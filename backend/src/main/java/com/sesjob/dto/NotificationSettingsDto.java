package com.sesjob.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 通知設定DTO
 */
public class NotificationSettingsDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private Boolean emailEnabled;
        private String emailAddress;
        private Boolean lineEnabled;
        private Boolean slackEnabled;
        private Integer minPriceThreshold;
        private String skillsFilter;
        private Boolean remoteOnly;
        private Integer notifyIntervalHours;
        private LocalDateTime lastNotifiedAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private Boolean emailEnabled;
        private String emailAddress;
        private Boolean lineEnabled;
        private String lineToken;
        private Boolean slackEnabled;
        private String slackWebhookUrl;
        private Integer minPriceThreshold;
        private String skillsFilter;
        private Boolean remoteOnly;
        private Integer notifyIntervalHours;
    }
}
