import { api, AudienceSummary, CategoryDto, ContentDetail, ContentPage, ContentType, NoticeSettingsDto, TagDto } from "../../api/client";

export interface ContentEditorRequest {
  type: ContentType;
  title: string;
  summary: string;
  body: string;
  categoryId: string;
  tagIds: string[];
  audienceIds: string[];
  notice?: NoticeSettingsDto;
}

export function listManagedContent() {
  return api<ContentPage>("/content?size=100");
}

export function getManagedContent(contentId: string) {
  return api<ContentDetail>(`/content/${contentId}`);
}

export function createDraft(request: ContentEditorRequest) {
  return api<ContentDetail>("/content", { method: "POST", body: JSON.stringify(request) });
}

export function updateDraft(contentId: string, request: Partial<ContentEditorRequest> & { changeNote?: string }) {
  return api<ContentDetail>(`/content/${contentId}`, { method: "PATCH", body: JSON.stringify(request) });
}

export function submitForReview(contentId: string) {
  return api<ContentDetail>(`/content/${contentId}/submit`, { method: "POST" });
}

export function approveContent(contentId: string) {
  return api<ContentDetail>(`/content/${contentId}/approve`, { method: "POST", body: JSON.stringify({ note: "Approved from CMS" }) });
}

export function publishContent(contentId: string) {
  return api<ContentDetail>(`/content/${contentId}/publish`, { method: "POST", body: JSON.stringify({ note: "Published from CMS" }) });
}

export function archiveContent(contentId: string) {
  return api<ContentDetail>(`/content/${contentId}/archive`, { method: "POST", body: JSON.stringify({ note: "Archived from CMS" }) });
}

export function listCategories() {
  return api<CategoryDto[]>("/taxonomy/categories");
}

export function listTags() {
  return api<TagDto[]>("/taxonomy/tags");
}

export function listAudiences() {
  return api<AudienceSummary[]>("/taxonomy/audiences");
}
