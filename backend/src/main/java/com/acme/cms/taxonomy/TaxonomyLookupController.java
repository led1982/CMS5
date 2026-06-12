package com.acme.cms.taxonomy;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.security.repository.AudienceGroupRepository;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/taxonomy")
public class TaxonomyLookupController {
    private final TaxonomyService taxonomy;
    private final AudienceGroupRepository audiences;
    private final CmsMapper mapper;

    public TaxonomyLookupController(TaxonomyService taxonomy, AudienceGroupRepository audiences, CmsMapper mapper) {
        this.taxonomy = taxonomy;
        this.audiences = audiences;
        this.mapper = mapper;
    }

    @GetMapping("/categories")
    List<ApiDtos.CategoryDto> categories() {
        return taxonomy.listCategories().stream().map(mapper::toCategory).toList();
    }

    @GetMapping("/tags")
    List<ApiDtos.TagDto> tags() {
        return taxonomy.listTags().stream().map(mapper::toTag).toList();
    }

    @GetMapping("/audiences")
    List<ApiDtos.AudienceSummary> audiences() {
        return audiences.findByActiveTrueOrderByNameAsc().stream().map(mapper::toAudienceSummary).toList();
    }
}
