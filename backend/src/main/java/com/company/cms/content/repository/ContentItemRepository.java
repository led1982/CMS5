package com.company.cms.content.repository;

import com.company.cms.content.domain.ContentItem;
import com.company.cms.content.domain.ContentStatus;
import com.company.cms.content.domain.ContentType;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.domain.Specification;

public interface ContentItemRepository extends JpaRepository<ContentItem, UUID>, JpaSpecificationExecutor<ContentItem> {
    @Override
    @EntityGraph(attributePaths = {"category", "tags", "author", "author.department", "author.roles", "ownerDepartment"})
    Page<ContentItem> findAll(Specification<ContentItem> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags", "author", "author.department", "author.roles", "ownerDepartment"})
    Page<ContentItem> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"category", "tags", "author", "author.department", "author.roles", "ownerDepartment"})
    List<ContentItem> findTop20ByStatusOrderByUpdatedAtDesc(ContentStatus status);

    @EntityGraph(attributePaths = {"category", "tags", "author", "author.department", "author.roles", "ownerDepartment"})
    List<ContentItem> findByStatusAndTypeOrderByPinnedDescPublishedAtDesc(ContentStatus status, ContentType type);

    @EntityGraph(attributePaths = {"category", "tags", "author", "author.department", "author.roles", "ownerDepartment"})
    @Query("select c from ContentItem c where c.id = :id")
    Optional<ContentItem> findWithCategoryById(@Param("id") UUID id);

    long countByStatus(ContentStatus status);

    long countByType(ContentType type);
}
