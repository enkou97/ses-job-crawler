package com.sesjob.repository;

import com.sesjob.entity.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {

    // 設定は1レコードのみ想定
    default Optional<NotificationSettings> findSettings() {
        return findAll().stream().findFirst();
    }

    default NotificationSettings getOrCreateSettings() {
        return findSettings().orElseGet(() -> save(NotificationSettings.builder().build()));
    }
}
