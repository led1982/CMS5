INSERT INTO departments (id, code, name)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'ENG', 'Engineering'),
    ('20000000-0000-0000-0000-000000000002', 'SEC', 'Security'),
    ('20000000-0000-0000-0000-000000000003', 'HR', 'HR')
ON CONFLICT (code) DO NOTHING;

INSERT INTO roles (id, code, name, description)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'ADMIN', 'Administrator', 'System administration'),
    ('30000000-0000-0000-0000-000000000002', 'EDITOR', 'Editor', 'Draft and submit content'),
    ('30000000-0000-0000-0000-000000000003', 'REVIEWER', 'Reviewer', 'Review and publish content'),
    ('30000000-0000-0000-0000-000000000004', 'EMPLOYEE', 'Employee', 'Portal user'),
    ('30000000-0000-0000-0000-000000000005', 'VIEWER', 'Viewer', 'Read-only portal user')
ON CONFLICT (code) DO NOTHING;

INSERT INTO users (id, external_id, email, display_name, department_id)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@example.com', '관리자', '20000000-0000-0000-0000-000000000001'),
    ('00000000-0000-0000-0000-000000000002', 'editor', 'editor@example.com', '콘텐츠 편집자', '20000000-0000-0000-0000-000000000001'),
    ('00000000-0000-0000-0000-000000000003', 'reviewer', 'reviewer@example.com', '검토자', '20000000-0000-0000-0000-000000000002'),
    ('00000000-0000-0000-0000-000000000004', 'employee', 'employee@example.com', '일반 사용자', '20000000-0000-0000-0000-000000000001'),
    ('00000000-0000-0000-0000-000000000005', 'viewer', 'viewer@example.com', '열람 사용자', '20000000-0000-0000-0000-000000000003')
ON CONFLICT (external_id) DO NOTHING;

INSERT INTO user_roles (user_id, role_id, assigned_by)
SELECT u.id, r.id, '00000000-0000-0000-0000-000000000001'
FROM users u
JOIN roles r ON r.code =
    CASE u.external_id
        WHEN 'admin' THEN 'ADMIN'
        WHEN 'editor' THEN 'EDITOR'
        WHEN 'reviewer' THEN 'REVIEWER'
        WHEN 'employee' THEN 'EMPLOYEE'
        ELSE 'VIEWER'
    END
ON CONFLICT DO NOTHING;

INSERT INTO categories (id, name, slug, description, sort_order)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'Security', 'security', 'Security policy and incident response', 1),
    ('10000000-0000-0000-0000-000000000002', 'Engineering', 'engineering', 'Engineering standards and release operations', 2),
    ('10000000-0000-0000-0000-000000000003', 'HR', 'hr', 'People programs and benefits', 3),
    ('10000000-0000-0000-0000-000000000004', 'Policy', 'policy', 'Company operating policies', 4)
ON CONFLICT (slug) DO NOTHING;
