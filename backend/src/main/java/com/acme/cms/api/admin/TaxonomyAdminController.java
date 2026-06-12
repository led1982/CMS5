package com.acme.cms.api.admin;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.CmsMapper;
import com.acme.cms.security.CurrentUser;
import com.acme.cms.taxonomy.TaxonomyService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class TaxonomyAdminController {
    private final TaxonomyService taxonomy;
    private final CmsMapper mapper;
    private final CurrentUser currentUser;

    public TaxonomyAdminController(TaxonomyService taxonomy, CmsMapper mapper, CurrentUser currentUser) {
        this.taxonomy = taxonomy;
        this.mapper = mapper;
        this.currentUser = currentUser;
    }

    @GetMapping("/categories")
    List<ApiDtos.CategoryDto> categories() {
        currentUser.requirePermission("ADMIN_ACCESS");
        return taxonomy.listCategories().stream().map(mapper::toCategory).toList();
    }

    @PostMapping("/categories")
    ResponseEntity<ApiDtos.CategoryDto> createCategory(@RequestBody ApiDtos.CategoryWriteRequest request) {
        var category = taxonomy.createCategory(request);
        return ResponseEntity.created(URI.create("/api/v1/admin/categories/" + category.getId()))
            .body(mapper.toCategory(category));
    }

    @GetMapping("/tags")
    List<ApiDtos.TagDto> tags() {
        currentUser.requirePermission("ADMIN_ACCESS");
        return taxonomy.listTags().stream().map(mapper::toTag).toList();
    }
}
