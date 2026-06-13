ALTER TABLE content_items
    ADD COLUMN search_vector tsvector
    GENERATED ALWAYS AS (
        setweight(to_tsvector('simple', coalesce(title, '')), 'A') ||
        setweight(to_tsvector('simple', coalesce(summary, '')), 'B') ||
        setweight(to_tsvector('simple', coalesce(body, '')), 'C')
    ) STORED;

CREATE INDEX idx_content_items_search_vector ON content_items USING GIN (search_vector);
CREATE INDEX idx_content_items_status_type_updated ON content_items (status, content_type, updated_at DESC);
CREATE INDEX idx_content_items_category_status ON content_items (category_id, status);
CREATE INDEX idx_content_audiences_lookup ON content_audiences (audience_type, audience_ref_id, content_item_id);
CREATE INDEX idx_content_versions_content_created ON content_versions (content_item_id, created_at DESC);
CREATE INDEX idx_audit_logs_created ON audit_logs (created_at DESC);
CREATE INDEX idx_audit_logs_actor_action ON audit_logs (actor_id, action);
CREATE INDEX idx_metrics_type_occurred ON content_metrics (metric_type, occurred_at DESC);
CREATE INDEX idx_search_queries_text ON search_queries USING GIN (to_tsvector('simple', query_text));
