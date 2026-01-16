package com.sesjob.dto;

import lombok.*;

import java.util.Map;

/**
 * 統計情報DTO
 */
public class StatsDto {

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Overview {
        private long totalJobs;
        private long newJobs;
        private long favoriteJobs;
        private Double averagePrice;
        private Map<String, Long> jobsBySource;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillStats {
        private String skill;
        private long count;
        private Double averagePrice;
    }
}
