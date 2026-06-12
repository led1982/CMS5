CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE roles (
    id UUID PRIMARY KEY,
    code VARCHAR(80) NOT NULL UNIQUE,
    name VARCHAR(160) NOT NULL,
    description TEXT,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission VARCHAR(120) NOT NULL,
    PRIMARY KEY (role_id, permission)
);

CREATE TABLE users (
    id UUID PRIMARY KEY,
    employee_number VARCHAR(80),
    email VARCHAR(320) NOT NULL UNIQUE,
    display_name VARCHAR(200) NOT NULL,
    department VARCHAR(160),
    status VARCHAR(40) NOT NULL,
    last_login_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE user_roles (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);

CREATE TABLE audience_groups (
    id UUID PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(180) NOT NULL,
    description TEXT,
    type VARCHAR(60) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE user_audiences (
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    audience_id UUID NOT NULL REFERENCES audience_groups(id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, audience_id)
);

CREATE TABLE categories (
    id UUID PRIMARY KEY,
    parent_id UUID REFERENCES categories(id),
    name VARCHAR(180) NOT NULL,
    slug VARCHAR(180) NOT NULL UNIQUE,
    description TEXT,
    sort_order INTEGER NOT NULL DEFAULT 0,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(120) NOT NULL UNIQUE,
    slug VARCHAR(120) NOT NULL UNIQUE,
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE content_items (
    id UUID PRIMARY KEY,
    type VARCHAR(40) NOT NULL,
    title VARCHAR(240) NOT NULL,
    summary TEXT NOT NULL,
    owner_user_id UUID NOT NULL REFERENCES users(id),
    category_id UUID NOT NULL REFERENCES categories(id),
    status VARCHAR(40) NOT NULL,
    visibility VARCHAR(40) NOT NULL,
    publish_start_at TIMESTAMPTZ,
    publish_end_at TIMESTAMPTZ,
    current_version_id UUID,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    archived_at TIMESTAMPTZ
);

CREATE TABLE content_versions (
    id UUID PRIMARY KEY,
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    version_number INTEGER NOT NULL,
    title VARCHAR(240) NOT NULL,
    summary TEXT NOT NULL,
    body TEXT NOT NULL,
    change_note TEXT,
    created_by_user_id UUID NOT NULL REFERENCES users(id),
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    approved_by_user_id UUID REFERENCES users(id),
    approved_at TIMESTAMPTZ,
    UNIQUE (content_item_id, version_number)
);

CREATE TABLE content_tags (
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    tag_id UUID NOT NULL REFERENCES tags(id),
    PRIMARY KEY (content_item_id, tag_id)
);

CREATE TABLE content_audiences (
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    audience_id UUID NOT NULL REFERENCES audience_groups(id),
    PRIMARY KEY (content_item_id, audience_id)
);

CREATE TABLE attachments (
    id UUID PRIMARY KEY,
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    filename VARCHAR(260) NOT NULL,
    media_type VARCHAR(160) NOT NULL,
    size_bytes BIGINT NOT NULL,
    checksum VARCHAR(128) NOT NULL,
    storage_key VARCHAR(320) NOT NULL,
    uploaded_by_user_id UUID NOT NULL REFERENCES users(id),
    uploaded_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    active BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE notice_settings (
    content_item_id UUID PRIMARY KEY REFERENCES content_items(id) ON DELETE CASCADE,
    priority VARCHAR(40) NOT NULL DEFAULT 'NORMAL',
    requires_acknowledgement BOOLEAN NOT NULL DEFAULT FALSE,
    acknowledgement_due_at TIMESTAMPTZ
);

CREATE TABLE bookmarks (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    UNIQUE (user_id, content_item_id)
);

CREATE TABLE notice_acknowledgements (
    id UUID PRIMARY KEY,
    content_item_id UUID NOT NULL REFERENCES content_items(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    acknowledged_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    content_version_id UUID NOT NULL REFERENCES content_versions(id),
    UNIQUE (content_item_id, user_id, content_version_id)
);

CREATE TABLE audit_events (
    id UUID PRIMARY KEY,
    actor_user_id UUID REFERENCES users(id),
    action VARCHAR(120) NOT NULL,
    target_type VARCHAR(80) NOT NULL,
    target_id VARCHAR(120) NOT NULL,
    outcome VARCHAR(40) NOT NULL,
    details TEXT,
    occurred_at TIMESTAMPTZ NOT NULL DEFAULT now()
);

INSERT INTO roles (id, code, name, description, active) VALUES
('00000000-0000-0000-0000-000000000101', 'EMPLOYEE', 'Employee', 'Portal reader', TRUE),
('00000000-0000-0000-0000-000000000102', 'CONTENT_MANAGER', 'Content Manager', 'Creates and publishes CMS content', TRUE),
('00000000-0000-0000-0000-000000000103', 'REVIEWER', 'Reviewer', 'Reviews submitted content', TRUE),
('00000000-0000-0000-0000-000000000104', 'ADMIN', 'Administrator', 'CMS governance and operations', TRUE);

INSERT INTO role_permissions (role_id, permission) VALUES
('00000000-0000-0000-0000-000000000101', 'PORTAL_READ'),
('00000000-0000-0000-0000-000000000102', 'PORTAL_READ'),
('00000000-0000-0000-0000-000000000102', 'CONTENT_WRITE'),
('00000000-0000-0000-0000-000000000102', 'CONTENT_PUBLISH'),
('00000000-0000-0000-0000-000000000103', 'PORTAL_READ'),
('00000000-0000-0000-0000-000000000103', 'CONTENT_REVIEW'),
('00000000-0000-0000-0000-000000000104', 'PORTAL_READ'),
('00000000-0000-0000-0000-000000000104', 'CONTENT_WRITE'),
('00000000-0000-0000-0000-000000000104', 'CONTENT_REVIEW'),
('00000000-0000-0000-0000-000000000104', 'CONTENT_PUBLISH'),
('00000000-0000-0000-0000-000000000104', 'ADMIN_ACCESS'),
('00000000-0000-0000-0000-000000000104', 'AUDIT_READ');

INSERT INTO users (id, employee_number, email, display_name, department, status) VALUES
('00000000-0000-0000-0000-000000000201', 'A001', 'admin@example.com', 'Admin User', 'Operations', 'ACTIVE'),
('00000000-0000-0000-0000-000000000202', 'E001', 'editor@example.com', 'Content Editor', 'HR', 'ACTIVE'),
('00000000-0000-0000-0000-000000000203', 'R001', 'reviewer@example.com', 'Content Reviewer', 'Compliance', 'ACTIVE'),
('00000000-0000-0000-0000-000000000204', 'H001', 'employee@example.com', 'HR Employee', 'HR', 'ACTIVE'),
('00000000-0000-0000-0000-000000000205', 'O001', 'outsider@example.com', 'Outside Employee', 'Finance', 'ACTIVE');

INSERT INTO user_roles (user_id, role_id) VALUES
('00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000104'),
('00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000102'),
('00000000-0000-0000-0000-000000000203', '00000000-0000-0000-0000-000000000103'),
('00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000101'),
('00000000-0000-0000-0000-000000000205', '00000000-0000-0000-0000-000000000101');

INSERT INTO audience_groups (id, code, name, description, type, active) VALUES
('00000000-0000-0000-0000-000000000301', 'HR_EMPLOYEES', 'HR Employees', 'Employees in the HR audience', 'DEPARTMENT', TRUE),
('00000000-0000-0000-0000-000000000302', 'ALL_STAFF', 'All Staff', 'General employee-wide audience', 'CUSTOM', TRUE);

INSERT INTO user_audiences (user_id, audience_id) VALUES
('00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000301'),
('00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000301'),
('00000000-0000-0000-0000-000000000201', '00000000-0000-0000-0000-000000000302'),
('00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000302'),
('00000000-0000-0000-0000-000000000203', '00000000-0000-0000-0000-000000000302'),
('00000000-0000-0000-0000-000000000204', '00000000-0000-0000-0000-000000000302'),
('00000000-0000-0000-0000-000000000205', '00000000-0000-0000-0000-000000000302');

INSERT INTO categories (id, parent_id, name, slug, description, sort_order, active) VALUES
('00000000-0000-0000-0000-000000000401', NULL, 'Policies', 'policies', 'Company policies and handbook content', 10, TRUE),
('00000000-0000-0000-0000-000000000402', NULL, 'Knowledge Base', 'knowledge-base', 'Reusable internal knowledge', 20, TRUE);

INSERT INTO tags (id, name, slug, active) VALUES
('00000000-0000-0000-0000-000000000501', 'Onboarding', 'onboarding', TRUE),
('00000000-0000-0000-0000-000000000502', 'Remote Work', 'remote-work', TRUE);

INSERT INTO content_items (
    id, type, title, summary, owner_user_id, category_id, status, visibility,
    publish_start_at, publish_end_at, current_version_id, created_at, updated_at
) VALUES
('00000000-0000-0000-0000-000000000601', 'KNOWLEDGE', 'Remote Work Policy',
 'Eligibility, equipment, and collaboration rules for remote work.',
 '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000401',
 'PUBLISHED', 'SELECTED_AUDIENCES', now() - interval '7 days', NULL,
 '00000000-0000-0000-0000-000000000701', now() - interval '10 days', now() - interval '7 days'),
('00000000-0000-0000-0000-000000000602', 'NOTICE', 'Annual Compliance Training',
 'Mandatory annual compliance training for HR employees.',
 '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000401',
 'PUBLISHED', 'SELECTED_AUDIENCES', now() - interval '1 day', now() + interval '30 days',
 '00000000-0000-0000-0000-000000000702', now() - interval '2 days', now() - interval '1 day'),
('00000000-0000-0000-0000-000000000603', 'DOCUMENT', 'Expense Guide Draft',
 'Draft finance document that should not appear in the portal.',
 '00000000-0000-0000-0000-000000000202', '00000000-0000-0000-0000-000000000402',
 'DRAFT', 'SELECTED_AUDIENCES', NULL, NULL,
 '00000000-0000-0000-0000-000000000703', now() - interval '1 day', now() - interval '1 day');

INSERT INTO content_versions (
    id, content_item_id, version_number, title, summary, body, change_note,
    created_by_user_id, created_at, approved_by_user_id, approved_at
) VALUES
('00000000-0000-0000-0000-000000000701', '00000000-0000-0000-0000-000000000601', 1,
 'Remote Work Policy', 'Eligibility, equipment, and collaboration rules for remote work.',
 '# Remote Work Policy

Employees in eligible roles may work remotely with manager approval. Use approved collaboration tools, protect company information, and keep team availability visible.',
 'Initial policy publication', '00000000-0000-0000-0000-000000000202', now() - interval '10 days',
 '00000000-0000-0000-0000-000000000203', now() - interval '8 days'),
('00000000-0000-0000-0000-000000000702', '00000000-0000-0000-0000-000000000602', 1,
 'Annual Compliance Training', 'Mandatory annual compliance training for HR employees.',
 '# Annual Compliance Training

Complete the annual training by the due date and acknowledge this notice after reading the instructions.',
 'Initial notice', '00000000-0000-0000-0000-000000000202', now() - interval '2 days',
 '00000000-0000-0000-0000-000000000203', now() - interval '1 day'),
('00000000-0000-0000-0000-000000000703', '00000000-0000-0000-0000-000000000603', 1,
 'Expense Guide Draft', 'Draft finance document that should not appear in the portal.',
 '# Expense Guide

Draft content pending review.',
 'Draft', '00000000-0000-0000-0000-000000000202', now() - interval '1 day', NULL, NULL);

INSERT INTO content_tags (content_item_id, tag_id) VALUES
('00000000-0000-0000-0000-000000000601', '00000000-0000-0000-0000-000000000501'),
('00000000-0000-0000-0000-000000000601', '00000000-0000-0000-0000-000000000502'),
('00000000-0000-0000-0000-000000000602', '00000000-0000-0000-0000-000000000501');

INSERT INTO content_audiences (content_item_id, audience_id) VALUES
('00000000-0000-0000-0000-000000000601', '00000000-0000-0000-0000-000000000301'),
('00000000-0000-0000-0000-000000000602', '00000000-0000-0000-0000-000000000301'),
('00000000-0000-0000-0000-000000000603', '00000000-0000-0000-0000-000000000301');

INSERT INTO notice_settings (content_item_id, priority, requires_acknowledgement, acknowledgement_due_at) VALUES
('00000000-0000-0000-0000-000000000602', 'HIGH', TRUE, now() + interval '30 days');

INSERT INTO attachments (
    id, content_item_id, filename, media_type, size_bytes, checksum, storage_key, uploaded_by_user_id, uploaded_at, active
) VALUES
('00000000-0000-0000-0000-000000000801', '00000000-0000-0000-0000-000000000601',
 'remote-work-policy.pdf', 'application/pdf', 245760, 'seed-checksum', 'seed/remote-work-policy.pdf',
 '00000000-0000-0000-0000-000000000202', now() - interval '7 days', TRUE);

INSERT INTO audit_events (id, actor_user_id, action, target_type, target_id, outcome, details, occurred_at) VALUES
('00000000-0000-0000-0000-000000000901', '00000000-0000-0000-0000-000000000202',
 'CONTENT_PUBLISHED', 'CONTENT', '00000000-0000-0000-0000-000000000601', 'SUCCESS',
 'Seeded Remote Work Policy publication', now() - interval '7 days'),
('00000000-0000-0000-0000-000000000902', '00000000-0000-0000-0000-000000000202',
 'CONTENT_PUBLISHED', 'CONTENT', '00000000-0000-0000-0000-000000000602', 'SUCCESS',
 'Seeded Annual Compliance Training notice', now() - interval '1 day');
