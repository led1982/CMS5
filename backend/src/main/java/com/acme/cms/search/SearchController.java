package com.acme.cms.search;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.content.model.ContentType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {
    private final SearchService searchService;

    public SearchController(SearchService searchService) {
        this.searchService = searchService;
    }

    @GetMapping
    ApiDtos.SearchResults search(
        @RequestParam("q") String query,
        @RequestParam(required = false) ContentType type,
        @RequestParam(required = false) String categoryId,
        @RequestParam(required = false) String tag,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return searchService.search(query, type, categoryId, tag, page, size);
    }
}
