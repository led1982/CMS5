import { ContentDetail, ContentSummary, ContentType, PagedContentSummary } from '../../../shared/api/openapiClient';
import { http } from '../../../shared/api/httpClient';

export function searchPortalContents(params: {
  q?: string;
  type?: ContentType;
  tag?: string;
  page?: number;
  size?: number;
  sort?: 'RELEVANCE' | 'LATEST' | 'MOST_VIEWED';
}) {
  const query = new URLSearchParams();
  if (params.q) query.set('q', params.q);
  if (params.type) query.set('type', params.type);
  if (params.tag) query.set('tag', params.tag);
  query.set('page', String(params.page ?? 0));
  query.set('size', String(params.size ?? 20));
  query.set('sort', params.sort ?? 'RELEVANCE');
  return http<PagedContentSummary>(`/portal/contents?${query.toString()}`);
}

export function getPortalContent(contentId: string) {
  return http<ContentDetail>(`/portal/contents/${contentId}`);
}

export function bookmarkContent(contentId: string) {
  return http<void>(`/portal/contents/${contentId}/bookmarks`, { method: 'POST' });
}

export function removeBookmark(contentId: string) {
  return http<void>(`/portal/contents/${contentId}/bookmarks`, { method: 'DELETE' });
}

export function listPortalAnnouncements(onlyUnacknowledged = false) {
  return http<ContentSummary[]>(`/portal/announcements?onlyUnacknowledged=${onlyUnacknowledged}`);
}
