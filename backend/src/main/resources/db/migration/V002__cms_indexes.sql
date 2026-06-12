CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_content_status_updated ON content_items (status, updated_at DESC);
CREATE INDEX idx_content_type_status ON content_items (type, status);
CREATE INDEX idx_content_owner ON content_items (owner_user_id);
CREATE INDEX idx_content_category ON content_items (category_id);
CREATE INDEX idx_content_audiences_audience ON content_audiences (audience_id);
CREATE INDEX idx_content_versions_content ON content_versions (content_item_id, version_number DESC);
CREATE INDEX idx_attachments_content_active ON attachments (content_item_id, active);
CREATE INDEX idx_bookmarks_user_created ON bookmarks (user_id, created_at DESC);
CREATE INDEX idx_notice_ack_user_content ON notice_acknowledgements (user_id, content_item_id);
CREATE INDEX idx_audit_occurred ON audit_events (occurred_at DESC);
CREATE INDEX idx_audit_target ON audit_events (target_type, target_id);
CREATE INDEX idx_content_search_tsv
    ON content_items
    USING gin (to_tsvector('simple', coalesce(title, '') || ' ' || coalesce(summary, '')));
