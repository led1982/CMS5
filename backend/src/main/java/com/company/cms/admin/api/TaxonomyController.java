package com.company.cms.admin.api;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import com.company.cms.admin.domain.Category;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasRole('ADMIN')")
public class TaxonomyController {
    private final CopyOnWriteArrayList<Category> categories = new CopyOnWriteArrayList<>(List.of(
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000001"), "Security", "security", "보안 정책과 사고 대응", 1),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000002"), "Engineering", "engineering", "개발 표준과 릴리스 운영", 2),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000003"), "HR", "hr", "인사 제도와 복리후생", 3),
            new Category(UUID.fromString("10000000-0000-0000-0000-000000000004"), "Policy", "policy", "전사 운영 정책", 4)
    ));
    private final CopyOnWriteArrayList<Category.Tag> tags = new CopyOnWriteArrayList<>(List.of(
            new Category.Tag("security"),
            new Category.Tag("release"),
            new Category.Tag("benefits")
    ));

    @GetMapping("/categories")
    List<Category> listCategories() {
        return categories;
    }

    @PostMapping("/categories")
    @ResponseStatus(HttpStatus.CREATED)
    Category createCategory(@Valid @RequestBody CategoryRequest request) {
        Category category = new Category(UUID.randomUUID(), request.name(), request.slug(), request.description(), request.sortOrder() == null ? categories.size() + 1 : request.sortOrder());
        category.setParentId(request.parentId());
        categories.add(category);
        return category;
    }

    @GetMapping("/tags")
    List<Category.Tag> listTags() {
        return tags;
    }

    @PostMapping("/tags")
    @ResponseStatus(HttpStatus.CREATED)
    Category.Tag createTag(@Valid @RequestBody TagRequest request) {
        Category.Tag tag = new Category.Tag(request.name());
        tags.add(tag);
        return tag;
    }

    public record CategoryRequest(
            UUID parentId,
            @NotBlank @Size(max = 80) String name,
            @NotBlank @Size(max = 120) String slug,
            String description,
            Integer sortOrder
    ) {
    }

    public record TagRequest(@NotBlank @Size(max = 40) String name) {
    }
}
