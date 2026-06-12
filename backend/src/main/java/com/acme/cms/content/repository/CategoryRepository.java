package com.acme.cms.content.repository;

import com.acme.cms.content.model.Category;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, UUID> {
    List<Category> findAllByOrderBySortOrderAscNameAsc();
    Optional<Category> findBySlug(String slug);
}
