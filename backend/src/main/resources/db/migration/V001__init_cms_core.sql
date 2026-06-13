CREATE EXTENSION IF NOT EXISTS pgcrypto;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

CREATE TABLE departments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(40) NOT NULL UNIQUE,
    name VARCHAR(120) NOT NULL,
    parent_id UUID REFERENCES departments(id),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    code VARCHAR(30) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL,
    description TEXT
);

CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    employee_id VARCHAR(60) NOT NULL UNIQUE,
    display_name VARCHAR(120) NOT NULL,
    email VARCHAR(180) NOT NULL UNIQUE,
    department_id UUID REFERENCES departments(id),
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    assigned_by UUID REFERENCES users(id),
    assigned_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE audience_groups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    description TEXT,
    owner_user_id UUID NOT NULL REFERENCES users(id),
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE audience_group_members (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    audience_group_id UUID NOT NULL REFERENCES audience_groups(id) ON DELETE CASCADE,
    visibility_type VARCHAR(30) NOT NULL,
    target_id UUID NOT NULL
);

CREATE TABLE categories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(120) NOT NULL,
    slug VARCHAR(140) NOT NULL UNIQUE,
    parent_id UUID REFERENCES categories(id),
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    UNIQUE (parent_id, name),
    UNIQUE (parent_id, slug)
);

CREATE TABLE tags (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(40) NOT NULL UNIQUE,
    normalized_name VARCHAR(40) NOT NULL UNIQUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE content_items (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type VARCHAR(30) NOT NULL,
    status VARCHAR(30) NOT NULL,
    title VARCHAR(150) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    summary VARCHAR(500),
    category_id UUID NOT NULL REFERENCES categories(id),
    owner_department_id UUID REFERENCES departments(id),
    author_id UUID NOT NULL REFERENCES users(id),
    reviewer_id UUID REFERENCES users(id),
    current_version_id UUID,
    published_at TIMESTAMPTZ,
    publish_start_at TIMESTAMPTZ,
    publish_end_at TIMESTAMPTZ,
    pinned BOOLEAN NOT NULL DEFAULT FALSE,
    requires_acknowledgement BOOLEAN NOT NULL DEFAULT FALSE,
    view_count INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_publish_window CHECK (publish_end_at IS NULL OR publish_start_at IS NULL OR publish_end_at > publish_start_at),
    CONSTRAINT chk_ack_type CHECK (requires_acknowledgement = FALSE OR type = 'ANNOUNCEMENT')
);

CREATE TABLE content_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    title VARCHAR(150) NOT NULL,
    summary VARCHAR(500),
    body TEXT NOT NULL,
    change_note VARCHAR(500),
    created_by UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (content_id, version_number)
);

ALTER TABLE content_items
    ADD CONSTRAINT fk_content_current_version FOREIGN KEY (current_version_id) REFERENCES content_versions(id);

CREATE TABLE content_tags (
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id) ON DELETE CASCADE,
    PRIMARY KEY (content_id, tag_id)
);

CREATE TABLE content_audiences (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    visibility_type VARCHAR(30) NOT NULL,
    target_id UUID,
    UNIQUE (content_id, visibility_type, target_id)
);

CREATE TABLE attachments (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    version_id UUID NOT NULL REFERENCES content_versions(id) ON DELETE CASCADE,
    original_filename VARCHAR(255) NOT NULL,
    content_type VARCHAR(120) NOT NULL,
    file_size BIGINT NOT NULL,
    storage_key VARCHAR(500) NOT NULL,
    status VARCHAR(30) NOT NULL,
    uploaded_by UUID NOT NULL REFERENCES users(id),
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    CONSTRAINT chk_file_size_positive CHECK (file_size > 0)
);

CREATE TABLE announcement_acknowledgements (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    acknowledged_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (content_id, user_id)
);

CREATE TABLE bookmarks (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, content_id)
);

CREATE TABLE content_view_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    content_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    viewed_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    source VARCHAR(40)
);

CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    actor_id UUID NOT NULL REFERENCES users(id),
    action VARCHAR(40) NOT NULL,
    target_type VARCHAR(80) NOT NULL,
    target_id UUID NOT NULL,
    before_snapshot JSONB,
    after_snapshot JSONB,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    ip_address VARCHAR(80)
);

CREATE TABLE system_settings (
    key VARCHAR(120) PRIMARY KEY,
    value TEXT NOT NULL,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE search_indexes (
    content_id UUID PRIMARY KEY REFERENCES content_items(id) ON DELETE CASCADE,
    document TSVECTOR,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE file_storage_objects (
    storage_key VARCHAR(500) PRIMARY KEY,
    file_size BIGINT NOT NULL,
    checksum VARCHAR(128),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE backup_histories (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    status VARCHAR(30) NOT NULL,
    storage_key VARCHAR(500),
    started_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    finished_at TIMESTAMPTZ,
    message TEXT
);

INSERT INTO departments (id, code, name) VALUES
    ('10000000-0000-0000-0000-000000000001', 'HR', 'Human Resources'),
    ('10000000-0000-0000-0000-000000000002', 'ENG', 'Engineering')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (id, code, name, description) VALUES
    ('20000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrator', 'Full governance and operations access'),
    ('20000000-0000-0000-0000-000000000002', 'EDITOR', 'Editor', 'Content authoring access'),
    ('20000000-0000-0000-0000-000000000003', 'REVIEWER', 'Reviewer', 'Review and publish access'),
    ('20000000-0000-0000-0000-000000000004', 'EMPLOYEE', 'Employee', 'Portal access')
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (id, employee_id, display_name, email, department_id) VALUES
    ('30000000-0000-0000-0000-000000000001', 'A001', 'Admin User', 'admin@example.com', '10000000-0000-0000-0000-000000000002'),
    ('30000000-0000-0000-0000-000000000002', 'E001', 'Editor User', 'editor@example.com', '10000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000003', 'R001', 'Reviewer User', 'reviewer@example.com', '10000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000004', 'P001', 'Employee User', 'employee@example.com', '10000000-0000-0000-0000-000000000002')
ON CONFLICT (email) DO NOTHING;

INSERT INTO user_roles (user_id, role_id) VALUES
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000001', '20000000-0000-0000-0000-000000000004'),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000002'),
    ('30000000-0000-0000-0000-000000000002', '20000000-0000-0000-0000-000000000004'),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000003'),
    ('30000000-0000-0000-0000-000000000003', '20000000-0000-0000-0000-000000000004'),
    ('30000000-0000-0000-0000-000000000004', '20000000-0000-0000-0000-000000000004')
ON CONFLICT DO NOTHING;

INSERT INTO categories (id, name, slug, sort_order) VALUES
    ('40000000-0000-0000-0000-000000000001', '정책', 'policy', 10),
    ('40000000-0000-0000-0000-000000000002', '업무가이드', 'work-guide', 20),
    ('40000000-0000-0000-0000-000000000003', '공지', 'announcement', 30)
ON CONFLICT (slug) DO NOTHING;
