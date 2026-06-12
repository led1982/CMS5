package com.acme.cms.search;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.content.ContentLifecycleService;
import com.acme.cms.content.model.ContentType;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SearchService {
    private final SearchRepository searchRepository;
    private final ContentLifecycleService lifecycle;
    private final CmsMapper mapper;

    public SearchService(SearchRepository searchRepository, ContentLifecycleService lifecycle, CmsMapper mapper) {
        this.searchRepository = searchRepository;
        this.lifecycle = lifecycle;
        this.mapper = mapper;
    }

    @Transactional(readOnly = true)
    public ApiDtos.SearchResults search(String query, ContentType type, String categoryId, String tag, int page, int size) {
        UUID categoryUuid = categoryId == null || categoryId.isBlank() ? null : UUID.fromString(categoryId);
        var visible = searchRepository.search(query, type, categoryUuid, tag).stream()
            .filter(lifecycle::canViewCurrentUser)
            .toList();
        var paged = visible.stream().skip((long) page * size).limit(size)
            .map(item -> mapper.toContentSummary(item, lifecycle.noticeFor(item)))
            .toList();
        return new ApiDtos.SearchResults(paged, page, size, visible.size(), totalPages(visible.size(), size), query);
    }

    private int totalPages(long totalItems, int size) {
        return (int) Math.ceil(totalItems / (double) size);
    }
}
