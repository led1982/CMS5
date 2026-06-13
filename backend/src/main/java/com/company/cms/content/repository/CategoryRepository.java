package com.company.cms.content.repository;

import com.company.cms.content.domain.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findByActiveTrueOrderBySortOrderAscNameAsc();

    Optional<Category> findBySlug(String slug);
}
