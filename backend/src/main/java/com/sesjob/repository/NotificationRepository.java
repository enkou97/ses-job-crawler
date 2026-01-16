package com.sesjob.repository;

import com.sesjob.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findBySentAtAfter(LocalDateTime since);

    List<Notification> findByJobId(Long jobId);

    List<Notification> findByChannel(String channel);
}
