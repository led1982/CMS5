package com.company.cms.content.api;

import com.company.cms.common.api.ApiException;
import com.company.cms.content.api.ContentDtos.CategoryDto;
import com.company.cms.content.api.ContentDtos.CategoryRequest;
import com.company.cms.content.api.ContentDtos.TagDto;
import com.company.cms.content.domain.Category;
import com.company.cms.content.domain.Tag;
import com.company.cms.content.repository.CategoryRepository;
import com.company.cms.content.repository.TagRepository;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cms")
public class TaxonomyController {
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final ContentMapper mapper;

    public TaxonomyController(CategoryRepository categoryRepository, TagRepository tagRepository, ContentMapper mapper) {
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.mapper = mapper;
    }

    @GetMapping("/categories")
    public List<CategoryDto> categories() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAscNameAsc().stream()
            .map(mapper::category)
            .toList();
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public CategoryDto createCategory(@Valid @RequestBody CategoryRequest request) {
        Category parent = request.parentId() == null ? null : categoryRepository.findById(request.parentId())
            .orElseThrow(() -> ApiException.badRequest("PARENT_CATEGORY_NOT_FOUND", "Parent category was not found."));
        Category category = new Category(request.name(), request.slug(), parent, request.sortOrder());
        category.rename(request.name(), request.slug(), parent, request.sortOrder(), request.active());
        return mapper.category(categoryRepository.save(category));
    }

    @GetMapping("/tags")
    public List<TagDto> tags(@RequestParam(required = false) String q) {
        String normalized = Tag.normalize(q);
        return (normalized.isBlank()
                ? tagRepository.findAll()
                : tagRepository.findByNormalizedNameContainingOrderByNameAsc(normalized))
            .stream()
            .map(mapper::tag)
            .toList();
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('EDITOR','ADMIN')")
    public TagDto createTag(@RequestBody java.util.Map<String, String> request) {
        String name = request.get("name");
        if (name == null || name.trim().isEmpty()) {
            throw ApiException.badRequest("TAG_NAME_REQUIRED", "Tag name is required.");
        }
        return mapper.tag(tagRepository.findByNormalizedName(Tag.normalize(name)).orElseGet(() -> tagRepository.save(new Tag(name))));
    }
}
