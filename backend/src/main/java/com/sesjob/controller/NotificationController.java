package com.sesjob.controller;

import com.sesjob.dto.NotificationSettingsDto;
import com.sesjob.entity.NotificationSettings;
import com.sesjob.notification.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notifications", description = "通知設定API")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/settings")
    @Operation(summary = "通知設定取得", description = "現在の通知設定を取得")
    public ResponseEntity<NotificationSettingsDto.Response> getSettings() {
        NotificationSettings settings = notificationService.getSettings();
        return ResponseEntity.ok(toResponse(settings));
    }

    @PutMapping("/settings")
    @Operation(summary = "通知設定更新", description = "通知設定を更新")
    public ResponseEntity<NotificationSettingsDto.Response> updateSettings(
            @RequestBody NotificationSettingsDto.UpdateRequest request) {

        NotificationSettings settings = notificationService.updateSettings(
                request.getEmailEnabled(),
                request.getEmailAddress(),
                request.getLineEnabled(),
                request.getLineToken(),
                request.getSlackEnabled(),
                request.getSlackWebhookUrl(),
                request.getMinPriceThreshold(),
                request.getSkillsFilter(),
                request.getRemoteOnly(),
                request.getNotifyIntervalHours());

        return ResponseEntity.ok(toResponse(settings));
    }

    @PostMapping("/test/{channel}")
    @Operation(summary = "テスト通知送信", description = "指定チャネルでテスト通知を送信")
    public ResponseEntity<Map<String, Object>> sendTestNotification(
            @PathVariable String channel) {

        boolean success = notificationService.sendTestNotification(channel);

        return ResponseEntity.ok(Map.of(
                "success", success,
                "channel", channel,
                "message", success ? "テスト通知を送信しました" : "テスト通知の送信に失敗しました"));
    }

    private NotificationSettingsDto.Response toResponse(NotificationSettings settings) {
        return NotificationSettingsDto.Response.builder()
                .id(settings.getId())
                .emailEnabled(settings.getEmailEnabled())
                .emailAddress(settings.getEmailAddress())
                .lineEnabled(settings.getLineEnabled())
                .slackEnabled(settings.getSlackEnabled())
                .minPriceThreshold(settings.getMinPriceThreshold())
                .skillsFilter(settings.getSkillsFilter())
                .remoteOnly(settings.getRemoteOnly())
                .notifyIntervalHours(settings.getNotifyIntervalHours())
                .lastNotifiedAt(settings.getLastNotifiedAt())
                .build();
    }
}
