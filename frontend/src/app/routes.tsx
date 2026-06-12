import { Navigate, RouteObject } from "react-router-dom";
import { AccessAdminPage } from "../features/admin/AccessAdminPage";
import { AnalyticsDashboardPage } from "../features/admin/AnalyticsDashboardPage";
import { AuditLogPage } from "../features/admin/AuditLogPage";
import { TaxonomyAdminPage } from "../features/admin/TaxonomyAdminPage";
import { ContentEditorPage } from "../features/editor/ContentEditorPage";
import { NoticeCenterPage } from "../features/notices/NoticeCenterPage";
import { ContentDetailPage } from "../features/portal/ContentDetailPage";
import { PortalHomePage } from "../features/portal/PortalHomePage";
import { SearchResultsPage } from "../features/search/SearchResultsPage";

export const routes: RouteObject[] = [
  { index: true, element: <PortalHomePage /> },
  { path: "portal/content/:contentId", element: <ContentDetailPage /> },
  { path: "search", element: <SearchResultsPage /> },
  { path: "notices", element: <NoticeCenterPage /> },
  { path: "saved", element: <PortalHomePage mode="saved" /> },
  { path: "cms", element: <ContentEditorPage /> },
  { path: "admin/taxonomy", element: <TaxonomyAdminPage /> },
  { path: "admin/access", element: <AccessAdminPage /> },
  { path: "admin/audit", element: <AuditLogPage /> },
  { path: "admin/analytics", element: <AnalyticsDashboardPage /> },
  { path: "*", element: <Navigate to="/" replace /> }
];
