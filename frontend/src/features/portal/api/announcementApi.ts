import { http } from '../../../shared/api/httpClient';

export function acknowledgeAnnouncement(contentId: string) {
  return http<void>(`/portal/contents/${contentId}/acknowledgements`, { method: 'POST' });
}
