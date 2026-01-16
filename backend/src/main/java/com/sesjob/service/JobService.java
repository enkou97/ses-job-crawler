package com.sesjob.service;

import com.sesjob.dto.JobDto;
import com.sesjob.dto.SearchRequest;
import com.sesjob.dto.StatsDto;
import com.sesjob.entity.Job;
import com.sesjob.entity.Job.JobStatus;
import com.sesjob.repository.JobRepository;
import jakarta.persistence.criteria.Predicate;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobService {

    private final JobRepository jobRepository;

    /**
     * 全案件取得（ページネーション）
     */
    public Page<JobDto.Summary> getAllJobs(int page, int size, String sortBy, String sortOrder) {
        Sort sort = createSort(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page, size, sort);
        return jobRepository.findAll(pageable).map(this::toSummary);
    }

    /**
     * 案件詳細取得
     */
    public Optional<JobDto.Response> getJobById(Long id) {
        return jobRepository.findById(id).map(this::toResponse);
    }

    /**
     * 案件検索
     */
    public Page<JobDto.Summary> searchJobs(SearchRequest request) {
        Specification<Job> spec = buildSpecification(request);

        int page = request.getPage() != null ? request.getPage() : 0;
        int size = request.getSize() != null ? request.getSize() : 20;
        String sortBy = request.getSortBy() != null ? request.getSortBy() : "crawledAt";
        String sortOrder = request.getSortOrder() != null ? request.getSortOrder() : "desc";

        Sort sort = createSort(sortBy, sortOrder);
        Pageable pageable = PageRequest.of(page, size, sort);

        return jobRepository.findAll(spec, pageable).map(this::toSummary);
    }

    /**
     * 案件作成（クローラーからの登録）
     */
    @Transactional
    public JobDto.Response createJob(JobDto.CreateRequest request) {
        // 重複チェック
        Optional<Job> existing = jobRepository.findBySourceAndSourceUrl(
                request.getSource(), request.getSourceUrl());

        if (existing.isPresent()) {
            // 既存の案件を更新
            Job job = existing.get();
            updateJobFromRequest(job, request);
            job = jobRepository.save(job);
            log.info("Updated existing job: {} - {}", job.getId(), job.getTitle());
            return toResponse(job);
        }

        // 新規作成
        Job job = Job.builder()
                .source(request.getSource())
                .sourceUrl(request.getSourceUrl())
                .sourceId(request.getSourceId())
                .title(request.getTitle())
                .minPrice(request.getMinPrice())
                .maxPrice(request.getMaxPrice())
                .priceType(request.getPriceType())
                .settlementHours(request.getSettlementHours())
                .requiredSkills(request.getRequiredSkills())
                .preferredSkills(request.getPreferredSkills())
                .experienceYears(request.getExperienceYears())
                .location(request.getLocation())
                .remoteType(request.getRemoteType())
                .workDays(request.getWorkDays())
                .startDate(request.getStartDate())
                .contractPeriod(request.getContractPeriod())
                .companyName(request.getCompanyName())
                .industry(request.getIndustry())
                .description(request.getDescription())
                .postedAt(request.getPostedAt())
                .crawledAt(LocalDateTime.now())
                .status(JobStatus.NEW)
                .build();

        job = jobRepository.save(job);
        log.info("Created new job: {} - {}", job.getId(), job.getTitle());
        return toResponse(job);
    }

    /**
     * 一括登録
     */
    @Transactional
    public List<JobDto.Response> createJobs(List<JobDto.CreateRequest> requests) {
        return requests.stream()
                .map(this::createJob)
                .collect(Collectors.toList());
    }

    /**
     * ステータス更新
     */
    @Transactional
    public Optional<JobDto.Response> updateStatus(Long id, JobStatus status) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setStatus(status);
                    return toResponse(jobRepository.save(job));
                });
    }

    /**
     * お気に入り切り替え
     */
    @Transactional
    public Optional<JobDto.Response> toggleFavorite(Long id) {
        return jobRepository.findById(id)
                .map(job -> {
                    job.setIsFavorite(!job.getIsFavorite());
                    return toResponse(jobRepository.save(job));
                });
    }

    /**
     * お気に入り一覧
     */
    public Page<JobDto.Summary> getFavorites(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("crawledAt").descending());
        return jobRepository.findByIsFavoriteTrue(pageable).map(this::toSummary);
    }

    /**
     * 新着案件取得
     */
    public List<JobDto.Summary> getNewJobsSince(LocalDateTime since) {
        return jobRepository.findNewJobsSince(since).stream()
                .map(this::toSummary)
                .collect(Collectors.toList());
    }

    /**
     * 統計情報取得
     */
    public StatsDto.Overview getOverviewStats() {
        long total = jobRepository.count();
        long newJobs = jobRepository.countBySource("NEW"); // ステータス別
        long favorites = jobRepository.findByIsFavoriteTrue(Pageable.unpaged()).getTotalElements();
        Double avgPrice = jobRepository.averageMaxPrice();

        Map<String, Long> bySource = new HashMap<>();
        jobRepository.countBySourceGrouped().forEach(row -> bySource.put((String) row[0], (Long) row[1]));

        return StatsDto.Overview.builder()
                .totalJobs(total)
                .newJobs(newJobs)
                .favoriteJobs(favorites)
                .averagePrice(avgPrice)
                .jobsBySource(bySource)
                .build();
    }

    // Private helper methods

    private Sort createSort(String sortBy, String sortOrder) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortOrder)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;
        return Sort.by(direction, sortBy != null ? sortBy : "crawledAt");
    }

    private Specification<Job> buildSpecification(SearchRequest request) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // キーワード検索
            if (request.getKeyword() != null && !request.getKeyword().isBlank()) {
                String keyword = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("title")), keyword),
                        cb.like(cb.lower(root.get("description")), keyword)));
            }

            // 単価フィルター
            if (request.getMinPrice() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("maxPrice"), request.getMinPrice()));
            }
            if (request.getMaxPrice() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("minPrice"), request.getMaxPrice()));
            }

            // 勤務地
            if (request.getLocation() != null && !request.getLocation().isBlank()) {
                predicates.add(cb.like(root.get("location"), "%" + request.getLocation() + "%"));
            }

            // リモート
            if (request.getRemoteType() != null) {
                predicates.add(cb.equal(root.get("remoteType"), request.getRemoteType()));
            }

            // ソース
            if (request.getSources() != null && !request.getSources().isEmpty()) {
                predicates.add(root.get("source").in(request.getSources()));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    private void updateJobFromRequest(Job job, JobDto.CreateRequest request) {
        job.setTitle(request.getTitle());
        job.setMinPrice(request.getMinPrice());
        job.setMaxPrice(request.getMaxPrice());
        job.setPriceType(request.getPriceType());
        job.setSettlementHours(request.getSettlementHours());
        job.setRequiredSkills(request.getRequiredSkills());
        job.setPreferredSkills(request.getPreferredSkills());
        job.setExperienceYears(request.getExperienceYears());
        job.setLocation(request.getLocation());
        job.setRemoteType(request.getRemoteType());
        job.setWorkDays(request.getWorkDays());
        job.setStartDate(request.getStartDate());
        job.setContractPeriod(request.getContractPeriod());
        job.setCompanyName(request.getCompanyName());
        job.setIndustry(request.getIndustry());
        job.setDescription(request.getDescription());
        job.setPostedAt(request.getPostedAt());
        job.setCrawledAt(LocalDateTime.now());
    }

    private JobDto.Response toResponse(Job job) {
        return JobDto.Response.builder()
                .id(job.getId())
                .source(job.getSource())
                .sourceUrl(job.getSourceUrl())
                .sourceId(job.getSourceId())
                .title(job.getTitle())
                .minPrice(job.getMinPrice())
                .maxPrice(job.getMaxPrice())
                .priceType(job.getPriceType())
                .settlementHours(job.getSettlementHours())
                .requiredSkills(job.getRequiredSkills())
                .preferredSkills(job.getPreferredSkills())
                .experienceYears(job.getExperienceYears())
                .location(job.getLocation())
                .remoteType(job.getRemoteType())
                .workDays(job.getWorkDays())
                .startDate(job.getStartDate())
                .contractPeriod(job.getContractPeriod())
                .companyName(job.getCompanyName())
                .industry(job.getIndustry())
                .description(job.getDescription())
                .status(job.getStatus())
                .isFavorite(job.getIsFavorite())
                .postedAt(job.getPostedAt())
                .crawledAt(job.getCrawledAt())
                .createdAt(job.getCreatedAt())
                .build();
    }

    private JobDto.Summary toSummary(Job job) {
        return JobDto.Summary.builder()
                .id(job.getId())
                .source(job.getSource())
                .title(job.getTitle())
                .maxPrice(job.getMaxPrice())
                .location(job.getLocation())
                .remoteType(job.getRemoteType())
                .requiredSkills(job.getRequiredSkills())
                .status(job.getStatus())
                .isFavorite(job.getIsFavorite())
                .postedAt(job.getPostedAt())
                .build();
    }
}
