export type RoleCode = 'ADMIN' | 'EDITOR' | 'REVIEWER' | 'EMPLOYEE';
export type ContentType = 'KNOWLEDGE' | 'DOCUMENT' | 'ANNOUNCEMENT';
export type ContentStatus = 'DRAFT' | 'IN_REVIEW' | 'PUBLISHED' | 'ARCHIVED';
export type VisibilityType = 'ALL_EMPLOYEES' | 'DEPARTMENT' | 'GROUP' | 'ROLE' | 'USER';
export type AttachmentStatus = 'UPLOADING' | 'READY' | 'REJECTED' | 'DELETED';
export type AuditAction =
  | 'CREATE'
  | 'UPDATE'
  | 'SUBMIT_REVIEW'
  | 'APPROVE'
  | 'REJECT'
  | 'PUBLISH'
  | 'ARCHIVE'
  | 'ROLE_CHANGE'
  | 'ACKNOWLEDGE'
  | 'ATTACHMENT_UPLOAD'
  | 'ATTACHMENT_DELETE';

export interface DepartmentDto {
  id: string;
  code: string;
  name: string;
}

export interface UserSummary {
  id: string;
  displayName: string;
  email: string;
  department?: DepartmentDto | null;
  roles: RoleCode[];
}

export interface CategoryDto {
  id: string;
  name: string;
  slug: string;
  parentId?: string | null;
  sortOrder: number;
  active: boolean;
}

export interface TagDto {
  id: string;
  name: string;
}

export interface ContentAudienceDto {
  id?: string;
  visibilityType: VisibilityType;
  targetId?: string | null;
}

export interface AttachmentDto {
  id: string;
  originalFilename: string;
  contentType: string;
  fileSize: number;
  storageKey: string;
  status: AttachmentStatus;
  uploadedAt: string;
}

export interface ContentVersionDto {
  id: string;
  versionNumber: number;
  title: string;
  summary?: string;
  body: string;
  changeNote?: string;
  createdBy: UserSummary;
  createdAt: string;
}

export interface ContentSummary {
  id: string;
  type: ContentType;
  status: ContentStatus;
  title: string;
  summary?: string;
  category: CategoryDto;
  tags: TagDto[];
  author: UserSummary;
  pinned: boolean;
  requiresAcknowledgement: boolean;
  acknowledged: boolean;
  publishedAt?: string | null;
  updatedAt: string;
}

export interface PagedContentSummary {
  items: ContentSummary[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}

export interface ContentDetail extends ContentSummary {
  slug: string;
  body: string;
  ownerDepartment?: DepartmentDto | null;
  currentVersion: ContentVersionDto;
  audiences: ContentAudienceDto[];
  attachments: AttachmentDto[];
  publishStartAt?: string | null;
  publishEndAt?: string | null;
}

export interface ContentMutationRequest {
  type: ContentType;
  title: string;
  summary: string;
  body: string;
  categoryId: string;
  ownerDepartmentId?: string | null;
  tags: string[];
  audiences: Array<{ visibilityType: VisibilityType; targetId?: string | null }>;
  pinned: boolean;
  requiresAcknowledgement: boolean;
  publishStartAt?: string | null;
  publishEndAt?: string | null;
  changeNote?: string;
}

export interface ApiErrorResponse {
  timestamp: string;
  status: number;
  code: string;
  message: string;
  fieldErrors: Array<{ field: string; message: string }>;
}

export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? '/api/v1';
