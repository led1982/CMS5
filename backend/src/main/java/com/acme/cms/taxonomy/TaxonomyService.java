package com.acme.cms.taxonomy;

import com.acme.cms.api.ApiDtos;
import com.acme.cms.api.ApiException;
import com.acme.cms.audit.AuditEventService;
import com.acme.cms.content.model.Category;
import com.acme.cms.content.model.Tag;
import com.acme.cms.content.repository.CategoryRepository;
import com.acme.cms.content.repository.TagRepository;
import com.acme.cms.security.CurrentUser;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TaxonomyService {
    private final CategoryRepository categories;
    private final TagRepository tags;
    private final CurrentUser currentUser;
    private final AuditEventService audit;

    public TaxonomyService(
        CategoryRepository categories,
        TagRepository tags,
        CurrentUser currentUser,
        AuditEventService audit
    ) {
        this.categories = categories;
        this.tags = tags;
        this.currentUser = currentUser;
        this.audit = audit;
    }

    @Transactional(readOnly = true)
    public List<Category> listCategories() {
        return categories.findAllByOrderBySortOrderAscNameAsc();
    }

    @Transactional
    public Category createCategory(ApiDtos.CategoryWriteRequest request) {
        currentUser.requirePermission("ADMIN_ACCESS");
        if (request.name() == null || request.name().isBlank() || request.slug() == null || request.slug().isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Category name and slug are required.");
        }
        Category category = new Category();
        if (request.parentId() != null && !request.parentId().isBlank()) {
            category.setParentId(UUID.fromString(request.parentId()));
        }
        category.setName(request.name().trim());
        category.setSlug(request.slug().trim());
        category.setDescription(request.description());
        category.setSortOrder(request.sortOrder() == null ? 0 : request.sortOrder());
        category = categories.save(category);
        audit.record(currentUser.get(), "CATEGORY_CREATED", "CATEGORY", category.getId().toString(), category.getName());
        return category;
    }

    @Transactional(readOnly = true)
    public List<Tag> listTags() {
        return tags.findByActiveTrueOrderByNameAsc();
    }
}
