package com.sesjob.repository;

import com.sesjob.entity.Job;
import com.sesjob.entity.Job.JobStatus;
import com.sesjob.entity.Job.RemoteType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JpaSpecificationExecutor<Job> {

    Optional<Job> findBySourceAndSourceUrl(String source, String sourceUrl);

    Optional<Job> findBySourceAndSourceId(String source, String sourceId);

    Page<Job> findByStatus(JobStatus status, Pageable pageable);

    Page<Job> findByIsFavoriteTrue(Pageable pageable);

    Page<Job> findBySource(String source, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.maxPrice >= :minPrice AND j.maxPrice <= :maxPrice")
    Page<Job> findByPriceRange(@Param("minPrice") Integer minPrice,
            @Param("maxPrice") Integer maxPrice,
            Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.remoteType = :remoteType")
    Page<Job> findByRemoteType(@Param("remoteType") RemoteType remoteType, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.location LIKE %:location%")
    Page<Job> findByLocationContaining(@Param("location") String location, Pageable pageable);

    @Query("SELECT j FROM Job j WHERE j.crawledAt >= :since")
    List<Job> findNewJobsSince(@Param("since") LocalDateTime since);

    @Query("SELECT j FROM Job j WHERE j.status = 'NEW' ORDER BY j.crawledAt DESC")
    Page<Job> findNewJobs(Pageable pageable);

    @Query("SELECT COUNT(j) FROM Job j WHERE j.source = :source")
    long countBySource(@Param("source") String source);

    @Query("SELECT j.source, COUNT(j) FROM Job j GROUP BY j.source")
    List<Object[]> countBySourceGrouped();

    @Query("SELECT AVG(j.maxPrice) FROM Job j WHERE j.maxPrice IS NOT NULL")
    Double averageMaxPrice();

    boolean existsBySourceAndSourceUrl(String source, String sourceUrl);
}
