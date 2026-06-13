import { render, screen, waitFor } from '@testing-library/react';
import { afterEach, describe, expect, it, vi } from 'vitest';
import { AuditLogPage } from '../pages/AuditLogPage';
import { ContentMetricsPage } from '../pages/ContentMetricsPage';
import { TaxonomyManagementPage } from '../pages/TaxonomyManagementPage';
import { UserRoleManagementPage } from '../pages/UserRoleManagementPage';

describe('AdminGovernance', () => {
  afterEach(() => {
    vi.unstubAllGlobals();
  });

  it('renders metrics widget data', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify({
            byStatus: { DRAFT: 1, IN_REVIEW: 2, PUBLISHED: 3, ARCHIVED: 0 },
            byType: { KNOWLEDGE: 1, DOCUMENT: 1, ANNOUNCEMENT: 1 },
            unacknowledgedAnnouncements: 4,
            topViewed: [{ contentId: '1', title: '휴가 신청 절차', viewCount: 9 }],
          }),
          { status: 200, headers: { 'Content-Type': 'application/json' } },
        ),
      ),
    );
    render(<ContentMetricsPage />);
    await waitFor(() => expect(screen.getByText('휴가 신청 절차')).toBeInTheDocument());
    expect(screen.getByText('UNACKNOWLEDGED')).toBeInTheDocument();
  });

  it('renders audit table rows', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify([
            {
              id: 'log-1',
              actorId: 'user-1',
              actorEmail: 'admin@example.com',
              action: 'ROLE_CHANGE',
              targetType: 'User',
              targetId: 'user-2',
              afterSnapshot: '{}',
              createdAt: new Date().toISOString(),
            },
          ]),
          { status: 200, headers: { 'Content-Type': 'application/json' } },
        ),
      ),
    );
    render(<AuditLogPage />);
    await waitFor(() => expect(screen.getByText('ROLE_CHANGE')).toBeInTheDocument());
  });

  it('renders role editor rows', async () => {
    vi.stubGlobal(
      'fetch',
      vi.fn().mockResolvedValue(
        new Response(
          JSON.stringify([
            {
              id: 'user-1',
              displayName: 'Admin User',
              email: 'admin@example.com',
              department: { id: 'dep-1', code: 'ENG', name: 'Engineering' },
              roles: ['ADMIN', 'EMPLOYEE'],
            },
          ]),
          { status: 200, headers: { 'Content-Type': 'application/json' } },
        ),
      ),
    );
    render(<UserRoleManagementPage />);
    await waitFor(() => expect(screen.getByText('admin@example.com')).toBeInTheDocument());
  });

  it('renders category tree and tags', async () => {
    vi.stubGlobal(
      'fetch',
      vi
        .fn()
        .mockResolvedValueOnce(
          new Response(JSON.stringify([{ id: 'cat-1', name: '업무가이드', slug: 'work-guide', sortOrder: 1, active: true }]), {
            status: 200,
            headers: { 'Content-Type': 'application/json' },
          }),
        )
        .mockResolvedValueOnce(
          new Response(JSON.stringify([{ id: 'tag-1', name: 'HR' }]), {
            status: 200,
            headers: { 'Content-Type': 'application/json' },
          }),
        ),
    );
    render(<TaxonomyManagementPage />);
    await waitFor(() => expect(screen.getByText('업무가이드')).toBeInTheDocument());
    expect(screen.getByText('HR')).toBeInTheDocument();
  });
});
