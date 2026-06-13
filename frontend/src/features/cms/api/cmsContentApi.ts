import {
  CategoryDto,
  ContentDetail,
  ContentMutationRequest,
  ContentStatus,
  ContentType,
  PagedContentSummary,
  TagDto,
} from '../../../shared/api/openapiClient';
import { http } from '../../../shared/api/httpClient';

export function listCmsContents(params: {
  status?: ContentStatus;
  type?: ContentType;
  page?: number;
  size?: number;
}) {
  const query = new URLSearchParams();
  if (params.status) query.set('status', params.status);
  if (params.type) query.set('type', params.type);
  query.set('page', String(params.page ?? 0));
  query.set('size', String(params.size ?? 20));
  return http<PagedContentSummary>(`/cms/contents?${query.toString()}`);
}

export function createContent(payload: ContentMutationRequest) {
  return http<ContentDetail>('/cms/contents', { method: 'POST', body: JSON.stringify(payload) });
}

export function updateContent(contentId: string, payload: ContentMutationRequest) {
  return http<ContentDetail>(`/cms/contents/${contentId}`, { method: 'PATCH', body: JSON.stringify(payload) });
}

export function submitContent(contentId: string, changeNote?: string) {
  return http<ContentDetail>(`/cms/contents/${contentId}/submit`, {
    method: 'POST',
    body: JSON.stringify({ changeNote }),
  });
}

export function reviewContent(contentId: string, decision: 'APPROVE' | 'REJECT', comment?: string) {
  return http<ContentDetail>(`/cms/contents/${contentId}/review`, {
    method: 'POST',
    body: JSON.stringify({ decision, comment }),
  });
}

export function publishContent(contentId: string) {
  return http<ContentDetail>(`/cms/contents/${contentId}/publish`, {
    method: 'POST',
    body: JSON.stringify({}),
  });
}

export function archiveContent(contentId: string, reason?: string) {
  return http<ContentDetail>(`/cms/contents/${contentId}/archive`, {
    method: 'POST',
    body: JSON.stringify({ reason }),
  });
}

export function listCategories() {
  return http<CategoryDto[]>('/cms/categories');
}

export function createCategory(payload: { name: string; slug: string; parentId?: string; sortOrder: number; active: boolean }) {
  return http<CategoryDto>('/cms/categories', { method: 'POST', body: JSON.stringify(payload) });
}

export function listTags(q?: string) {
  const query = q ? `?q=${encodeURIComponent(q)}` : '';
  return http<TagDto[]>(`/cms/tags${query}`);
}

export function createTag(name: string) {
  return http<TagDto>('/cms/tags', { method: 'POST', body: JSON.stringify({ name }) });
}
