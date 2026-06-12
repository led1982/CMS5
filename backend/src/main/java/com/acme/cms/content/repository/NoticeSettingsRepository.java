package com.acme.cms.content.repository;

import com.acme.cms.content.model.NoticeSettings;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeSettingsRepository extends JpaRepository<NoticeSettings, UUID> {
}
