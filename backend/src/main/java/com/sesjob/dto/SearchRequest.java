package com.sesjob.dto;

import com.sesjob.entity.Job.RemoteType;
import lombok.*;

import java.util.List;

/**
 * 検索条件DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchRequest {

    private String keyword;
    private List<String> skills;
    private Integer minPrice;
    private Integer maxPrice;
    private String location;
    private RemoteType remoteType;
    private List<String> sources;
    private String sortBy;
    private String sortOrder;
    private Integer page;
    private Integer size;
}
