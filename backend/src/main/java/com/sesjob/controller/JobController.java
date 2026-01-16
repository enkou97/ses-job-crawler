package com.sesjob.controller;

import com.sesjob.dto.JobDto;
import com.sesjob.dto.SearchRequest;
import com.sesjob.dto.StatsDto;
import com.sesjob.entity.Job.JobStatus;
import com.sesjob.service.JobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "案件管理API")
public class JobController {

    private final JobService jobService;

    @GetMapping
    @Operation(summary = "案件一覧取得", description = "ページネーション対応の案件一覧を取得")
    public ResponseEntity<Page<JobDto.Summary>> getAllJobs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "crawledAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortOrder) {
        return ResponseEntity.ok(jobService.getAllJobs(page, size, sortBy, sortOrder));
    }

    @GetMapping("/{id}")
    @Operation(summary = "案件詳細取得", description = "指定IDの案件詳細を取得")
    public ResponseEntity<JobDto.Response> getJob(@PathVariable Long id) {
        return jobService.getJobById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/search")
    @Operation(summary = "案件検索", description = "条件を指定して案件を検索")
    public ResponseEntity<Page<JobDto.Summary>> searchJobs(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(jobService.searchJobs(request));
    }

    @PostMapping
    @Operation(summary = "案件登録", description = "新規案件を登録（クローラー用）")
    public ResponseEntity<JobDto.Response> createJob(@RequestBody JobDto.CreateRequest request) {
        return ResponseEntity.ok(jobService.createJob(request));
    }

    @PostMapping("/batch")
    @Operation(summary = "案件一括登録", description = "複数案件を一括登録")
    public ResponseEntity<List<JobDto.Response>> createJobs(@RequestBody List<JobDto.CreateRequest> requests) {
        return ResponseEntity.ok(jobService.createJobs(requests));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "ステータス更新", description = "案件のステータスを更新")
    public ResponseEntity<JobDto.Response> updateStatus(
            @PathVariable Long id,
            @RequestBody JobDto.StatusUpdateRequest request) {
        return jobService.updateStatus(id, request.getStatus())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/{id}/favorite")
    @Operation(summary = "お気に入り切替", description = "お気に入り状態を切り替え")
    public ResponseEntity<JobDto.Response> toggleFavorite(@PathVariable Long id) {
        return jobService.toggleFavorite(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/favorites")
    @Operation(summary = "お気に入り一覧", description = "お気に入り案件の一覧を取得")
    public ResponseEntity<Page<JobDto.Summary>> getFavorites(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(jobService.getFavorites(page, size));
    }

    @GetMapping("/stats")
    @Operation(summary = "統計情報取得", description = "案件の統計情報を取得")
    public ResponseEntity<StatsDto.Overview> getStats() {
        return ResponseEntity.ok(jobService.getOverviewStats());
    }
}
