import { Navigate, createBrowserRouter } from 'react-router-dom';
import { AppShell } from '../app/AppShell';
import { RoleGuard } from '../features/auth/RoleGuard';
import { AcknowledgementReportPage } from '../features/cms/pages/AcknowledgementReportPage';
import { CmsContentListPage } from '../features/cms/pages/CmsContentListPage';
import { ContentEditorPage } from '../features/cms/pages/ContentEditorPage';
import { ReviewDetailPage } from '../features/cms/pages/ReviewDetailPage';
import { AuditLogPage } from '../features/admin/pages/AuditLogPage';
import { ContentMetricsPage } from '../features/admin/pages/ContentMetricsPage';
import { TaxonomyManagementPage } from '../features/admin/pages/TaxonomyManagementPage';
import { UserRoleManagementPage } from '../features/admin/pages/UserRoleManagementPage';
import { BookmarksPage } from '../features/portal/pages/BookmarksPage';
import { ContentDetailPage } from '../features/portal/pages/ContentDetailPage';
import { PortalHomePage } from '../features/portal/pages/PortalHomePage';
import { PortalSearchPage } from '../features/portal/pages/PortalSearchPage';

export const router = createBrowserRouter([
  {
    path: '/',
    element: <AppShell />,
    children: [
      { index: true, element: <Navigate to="/portal" replace /> },
      { path: 'portal', element: <PortalHomePage /> },
      { path: 'portal/search', element: <PortalSearchPage /> },
      { path: 'portal/contents/:contentId', element: <ContentDetailPage /> },
      { path: 'portal/bookmarks', element: <BookmarksPage /> },
      {
        path: 'cms/contents',
        element: (
          <RoleGuard roles={['EDITOR', 'REVIEWER', 'ADMIN']}>
            <CmsContentListPage />
          </RoleGuard>
        ),
      },
      {
        path: 'cms/contents/new',
        element: (
          <RoleGuard roles={['EDITOR', 'ADMIN']}>
            <ContentEditorPage />
          </RoleGuard>
        ),
      },
      {
        path: 'cms/contents/:contentId/edit',
        element: (
          <RoleGuard roles={['EDITOR', 'ADMIN']}>
            <ContentEditorPage />
          </RoleGuard>
        ),
      },
      {
        path: 'cms/reviews',
        element: (
          <RoleGuard roles={['REVIEWER', 'ADMIN']}>
            <ReviewDetailPage />
          </RoleGuard>
        ),
      },
      {
        path: 'cms/announcements/:contentId/acknowledgements',
        element: (
          <RoleGuard roles={['REVIEWER', 'ADMIN']}>
            <AcknowledgementReportPage />
          </RoleGuard>
        ),
      },
      {
        path: 'admin/categories',
        element: (
          <RoleGuard roles={['ADMIN']}>
            <TaxonomyManagementPage />
          </RoleGuard>
        ),
      },
      {
        path: 'admin/users',
        element: (
          <RoleGuard roles={['ADMIN']}>
            <UserRoleManagementPage />
          </RoleGuard>
        ),
      },
      {
        path: 'admin/audit-logs',
        element: (
          <RoleGuard roles={['ADMIN']}>
            <AuditLogPage />
          </RoleGuard>
        ),
      },
      {
        path: 'admin/metrics',
        element: (
          <RoleGuard roles={['ADMIN']}>
            <ContentMetricsPage />
          </RoleGuard>
        ),
      },
    ],
  },
]);
