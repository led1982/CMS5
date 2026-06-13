package com.company.cms.portal.service;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContentViewEventRepository extends JpaRepository<ContentViewEvent, UUID> {
}
