import { getSessionEmail } from "../auth/session";

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "/api/v1";

export type ContentType = "KNOWLEDGE" | "DOCUMENT" | "NOTICE";
export type ContentStatus = "DRAFT" | "SUBMITTED" | "APPROVED" | "SCHEDULED" | "PUBLISHED" | "ARCHIVED" | "REJECTED";
export type NoticePriority = "LOW" | "NORMAL" | "HIGH" | "URGENT";

export interface RoleSummary {
  id: string;
  code: string;
  name: string;
  permissions: string[];
}

export interface UserSummary {
  id: string;
  email: string;
  displayName: string;
  department?: string;
  roles: RoleSummary[];
  permissions: string[];
}

export interface CategoryDto {
  id: string;
  parentId?: string | null;
  name: string;
  slug: string;
  description?: string;
  sortOrder: number;
  active: boolean;
}

export interface TagDto {
  id: string;
  name: string;
  slug: string;
  active: boolean;
}

export interface AudienceSummary {
  id: string;
  code: string;
  name: string;
  type: string;
  active: boolean;
}

export interface NoticeSettingsDto {
  priority: NoticePriority;
  requiresAcknowledgement: boolean;
  acknowledgementDueAt?: string | null;
}

export interface ContentSummary {
  id: string;
  type: ContentType;
  title: string;
  summary: string;
  status: ContentStatus;
  category: CategoryDto;
  owner: UserSummary;
  tags: string[];
  publishedAt?: string | null;
  updatedAt: string;
  acknowledgementRequired: boolean;
}

export interface AttachmentDto {
  id: string;
  filename: string;
  mediaType: string;
  sizeBytes: number;
  checksum: string;
  downloadUrl: string;
  uploadedAt: string;
}

export interface ContentDetail extends ContentSummary {
  body: string;
  versionNumber: number;
  attachments: AttachmentDto[];
  audiences: string[];
  publishStartAt?: string | null;
  publishEndAt?: string | null;
  notice?: NoticeSettingsDto | null;
}

export interface ContentPage {
  items: ContentSummary[];
  page: number;
  size: number;
  totalItems: number;
  totalPages: number;
}

export interface SearchResults extends ContentPage {
  query: string;
}

export interface NoticeRequiredItem {
  content: ContentSummary;
  acknowledged: boolean;
  acknowledgedAt?: string | null;
}

export interface AuditLogDto {
  id: string;
  actor?: UserSummary | null;
  action: string;
  targetType: string;
  targetId: string;
  outcome: "SUCCESS" | "FAILURE";
  details: Record<string, unknown>;
  occurredAt: string;
}

export interface AuditLogPage {
  items: AuditLogDto[];
  page: number;
  size: number;
  totalItems: number;
}

export interface AnalyticsSummary {
  publishedContentCount: number;
  draftContentCount: number;
  requiredNoticeCount: number;
  acknowledgementRate: number;
  recentAuditEventCount: number;
}

export class ApiError extends Error {
  code: string;
  status: number;

  constructor(status: number, code: string, message: string) {
    super(message);
    this.status = status;
    this.code = code;
  }
}

export async function api<T>(path: string, init: RequestInit = {}): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...init,
    headers: {
      "Content-Type": "application/json",
      "X-User-Email": getSessionEmail(),
      ...(init.headers ?? {})
    }
  });

  if (!response.ok) {
    let message = response.statusText;
    let code = "REQUEST_FAILED";
    try {
      const body = await response.json();
      message = body.message ?? message;
      code = body.code ?? code;
    } catch {
      // Keep the HTTP status text when the backend returns no JSON body.
    }
    throw new ApiError(response.status, code, message);
  }

  if (response.status === 204) {
    return undefined as T;
  }
  return response.json() as Promise<T>;
}

export function pageUrl(path: string, params: Record<string, string | number | undefined | null>): string {
  const search = new URLSearchParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== "") {
      search.set(key, String(value));
    }
  });
  const query = search.toString();
  return query ? `${path}?${query}` : path;
}
