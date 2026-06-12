package com.acme.cms.content.repository;

import com.acme.cms.content.model.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, UUID> {
    List<Tag> findByActiveTrueOrderByNameAsc();
}
