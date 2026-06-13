import { AuditAction, ContentStatus, ContentType, RoleCode, UserSummary } from '../../../shared/api/openapiClient';
import { http } from '../../../shared/api/httpClient';

export type { AuditAction } from '../../../shared/api/openapiClient';

export interface AuditLogDto {
  id: string;
  actorId: string;
  actorEmail: string;
  action: AuditAction;
  targetType: string;
  targetId: string;
  afterSnapshot: string;
  createdAt: string;
}

export interface ContentMetrics {
  byStatus: Record<ContentStatus, number>;
  byType: Record<ContentType, number>;
  unacknowledgedAnnouncements: number;
  topViewed: Array<{ contentId: string; title: string; viewCount: number }>;
}

export interface AcknowledgementReport {
  contentId: string;
  title: string;
  totalTargets: number;
  acknowledgedCount: number;
  rows: Array<{
    user: UserSummary;
    acknowledged: boolean;
    acknowledgedAt?: string | null;
  }>;
}

export function listUsers(params: { q?: string; role?: RoleCode }) {
  const query = new URLSearchParams();
  if (params.q) query.set('q', params.q);
  if (params.role) query.set('role', params.role);
  return http<UserSummary[]>(`/admin/users?${query.toString()}`);
}

export function replaceUserRoles(userId: string, roles: RoleCode[]) {
  return http<UserSummary>(`/admin/users/${userId}/roles`, {
    method: 'PATCH',
    body: JSON.stringify({ roles }),
  });
}

export function listAuditLogs(action?: AuditAction) {
  const query = action ? `?action=${action}` : '';
  return http<AuditLogDto[]>(`/admin/audit-logs${query}`);
}

export function getContentMetrics() {
  return http<ContentMetrics>('/admin/metrics/content');
}

export function getAcknowledgementReport(contentId: string) {
  return http<AcknowledgementReport>(`/cms/announcements/${contentId}/acknowledgements`);
}
