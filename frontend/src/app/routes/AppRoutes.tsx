import { Navigate, Route, Routes } from "react-router-dom";
import { AppShell } from "../../components/layout/AppShell";
import { EmptyState, ForbiddenState, NotFoundState } from "../../components/ui/StateViews";
import { AnalyticsDashboardPage } from "../../features/analytics/AnalyticsDashboardPage";
import { AdminRouteGuard } from "../../features/admin/AdminRouteGuard";
import { AdminDashboardPage } from "../../features/admin/AdminDashboardPage";
import { AuditLogPage } from "../../features/admin/AuditLogPage";
import { TaxonomyManagerPage } from "../../features/admin/TaxonomyManagerPage";
import { UserRoleManagementPage } from "../../features/admin/UserRoleManagementPage";
import { ApprovalDecisionDialogDemo } from "../../features/content/ApprovalDecisionDialog";
import { ContentEditorPage } from "../../features/content/ContentEditorPage";
import { ContentListPage } from "../../features/content/ContentListPage";
import { ReviewQueuePage } from "../../features/content/ReviewQueuePage";
import { NoticeCenterPage } from "../../features/notices/NoticeCenterPage";
import { NoticeReportPage } from "../../features/notices/NoticeReportPage";
import { BookmarksPage } from "../../features/portal/BookmarksPage";
import { ContentDetailPage } from "../../features/portal/ContentDetailPage";
import { PortalHomePage } from "../../features/portal/PortalHomePage";
import { SearchResultsPage } from "../../features/portal/SearchResultsPage";

export function AppRoutes() {
  return (
    <Routes>
      <Route element={<AppShell />}>
        <Route index element={<Navigate to="/portal" replace />} />
        <Route path="portal" element={<PortalHomePage />} />
        <Route path="search" element={<SearchResultsPage />} />
        <Route path="content/:contentId" element={<ContentDetailPage />} />
        <Route path="notices" element={<NoticeCenterPage />} />
        <Route path="bookmarks" element={<BookmarksPage />} />
        <Route path="forbidden" element={<ForbiddenState />} />
        <Route
          path="admin"
          element={
            <AdminRouteGuard>
              <AdminDashboardPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/content"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN", "EDITOR", "REVIEWER"]}>
              <ContentListPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/content/new"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN", "EDITOR"]}>
              <ContentEditorPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/content/:contentId/edit"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN", "EDITOR"]}>
              <ContentEditorPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/review"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN", "REVIEWER"]}>
              <ReviewQueuePage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/review/dialog-preview"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN", "REVIEWER"]}>
              <ApprovalDecisionDialogDemo />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/notices/report"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN"]}>
              <NoticeReportPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/taxonomy"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN"]}>
              <TaxonomyManagerPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/users"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN"]}>
              <UserRoleManagementPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/audit"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN"]}>
              <AuditLogPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="admin/analytics"
          element={
            <AdminRouteGuard allowedRoles={["ADMIN"]}>
              <AnalyticsDashboardPage />
            </AdminRouteGuard>
          }
        />
        <Route
          path="login/callback"
          element={<EmptyState title="로그인 완료" description="프로필과 권한을 확인했습니다." actionLabel="포털 홈" actionHref="/portal" />}
        />
        <Route path="404" element={<NotFoundState />} />
        <Route path="*" element={<Navigate to="/404" replace />} />
      </Route>
    </Routes>
  );
}
