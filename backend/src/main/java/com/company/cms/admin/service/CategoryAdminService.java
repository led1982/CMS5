package com.company.cms.admin.service;

import com.company.cms.common.api.ApiException;
import com.company.cms.content.api.ContentDtos.CategoryRequest;
import com.company.cms.content.domain.Category;
import com.company.cms.content.repository.CategoryRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CategoryAdminService {
    private final CategoryRepository categoryRepository;

    public CategoryAdminService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Transactional(readOnly = true)
    public List<Category> activeTree() {
        return categoryRepository.findByActiveTrueOrderBySortOrderAscNameAsc();
    }

    @Transactional
    public Category create(CategoryRequest request) {
        Category parent = request.parentId() == null ? null : categoryRepository.findById(request.parentId())
            .orElseThrow(() -> ApiException.badRequest("PARENT_CATEGORY_NOT_FOUND", "Parent category was not found."));
        Category category = new Category(request.name(), request.slug(), parent, request.sortOrder());
        category.rename(request.name(), request.slug(), parent, request.sortOrder(), request.active());
        return categoryRepository.save(category);
    }
}
