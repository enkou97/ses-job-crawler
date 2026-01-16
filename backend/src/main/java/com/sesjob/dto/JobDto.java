package com.sesjob.dto;

import com.sesjob.entity.Job.JobStatus;
import com.sesjob.entity.Job.PriceType;
import com.sesjob.entity.Job.RemoteType;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 案件DTOクラス群
 */
public class JobDto {

    /**
     * 案件レスポンスDTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String source;
        private String sourceUrl;
        private String sourceId;
        private String title;
        private Integer minPrice;
        private Integer maxPrice;
        private PriceType priceType;
        private String settlementHours;
        private List<String> requiredSkills;
        private List<String> preferredSkills;
        private String experienceYears;
        private String location;
        private RemoteType remoteType;
        private String workDays;
        private LocalDate startDate;
        private String contractPeriod;
        private String companyName;
        private String industry;
        private String description;
        private JobStatus status;
        private Boolean isFavorite;
        private LocalDateTime postedAt;
        private LocalDateTime crawledAt;
        private LocalDateTime createdAt;
    }

    /**
     * 案件作成リクエストDTO（クローラーからの登録用）
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String source;
        private String sourceUrl;
        private String sourceId;
        private String title;
        private Integer minPrice;
        private Integer maxPrice;
        private PriceType priceType;
        private String settlementHours;
        private List<String> requiredSkills;
        private List<String> preferredSkills;
        private String experienceYears;
        private String location;
        private RemoteType remoteType;
        private String workDays;
        private LocalDate startDate;
        private String contractPeriod;
        private String companyName;
        private String industry;
        private String description;
        private LocalDateTime postedAt;
    }

    /**
     * ステータス更新リクエストDTO
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusUpdateRequest {
        private JobStatus status;
    }

    /**
     * 一覧表示用サマリーDTO
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Summary {
        private Long id;
        private String source;
        private String title;
        private Integer maxPrice;
        private String location;
        private RemoteType remoteType;
        private List<String> requiredSkills;
        private JobStatus status;
        private Boolean isFavorite;
        private LocalDateTime postedAt;
    }
}
