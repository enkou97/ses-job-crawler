package com.sesjob.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件エンティティ
 * SES/フリーランス案件の情報を保持
 */
@Entity
@Table(name = "jobs", uniqueConstraints = @UniqueConstraint(columnNames = { "source", "source_url" }), indexes = {
        @Index(name = "idx_jobs_status", columnList = "status"),
        @Index(name = "idx_jobs_source", columnList = "source"),
        @Index(name = "idx_jobs_posted_at", columnList = "posted_at"),
        @Index(name = "idx_jobs_max_price", columnList = "max_price")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ソース情報
    @Column(name = "source", nullable = false, length = 50)
    private String source;

    @Column(name = "source_url", nullable = false, length = 500)
    private String sourceUrl;

    @Column(name = "source_id", length = 100)
    private String sourceId;

    @Column(name = "title", nullable = false, length = 300)
    private String title;

    // 報酬情報
    @Column(name = "min_price")
    private Integer minPrice;

    @Column(name = "max_price")
    private Integer maxPrice;

    @Enumerated(EnumType.STRING)
    @Column(name = "price_type", length = 20)
    private PriceType priceType;

    @Column(name = "settlement_hours", length = 50)
    private String settlementHours;

    // スキル要件
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "required_skills", columnDefinition = "json")
    private List<String> requiredSkills;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "preferred_skills", columnDefinition = "json")
    private List<String> preferredSkills;

    @Column(name = "experience_years", length = 50)
    private String experienceYears;

    // 勤務条件
    @Column(name = "location", length = 100)
    private String location;

    @Enumerated(EnumType.STRING)
    @Column(name = "remote_type", length = 20)
    private RemoteType remoteType;

    @Column(name = "work_days", length = 50)
    private String workDays;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "contract_period", length = 100)
    private String contractPeriod;

    // その他
    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "industry", length = 100)
    private String industry;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // メタ情報
    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private JobStatus status = JobStatus.NEW;

    @Column(name = "is_favorite")
    @Builder.Default
    private Boolean isFavorite = false;

    @Column(name = "posted_at")
    private LocalDateTime postedAt;

    @Column(name = "crawled_at", nullable = false)
    private LocalDateTime crawledAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Enums
    public enum PriceType {
        MONTHLY, HOURLY
    }

    public enum RemoteType {
        FULL, PARTIAL, NONE
    }

    public enum JobStatus {
        NEW, READ, APPLIED, CLOSED
    }
}
