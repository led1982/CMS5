package com.company.cms.content.repository;

import com.company.cms.content.domain.Tag;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    Optional<Tag> findByNormalizedName(String normalizedName);

    List<Tag> findByNormalizedNameContainingOrderByNameAsc(String normalizedName);

    List<Tag> findByNameIn(Collection<String> names);
}
