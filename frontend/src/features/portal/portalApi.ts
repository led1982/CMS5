import { apiClient } from "../../services/apiClient";
import { publishedContent, searchContent, type ContentDetail, type ContentType } from "../../data/mockCms";
import { buildPortalHomeDashboard } from "./portalHomeModel";

export function getPortalHome() {
  return buildPortalHomeDashboard();
}

export function getPortalContent(contentId: string): ContentDetail | undefined {
  return publishedContent().find((item) => item.id === contentId);
}

export function getSearchResults(query: string, contentType?: ContentType, categoryId?: string) {
  return searchContent(query, contentType, categoryId);
}

export const portalApi = {
  home: () => apiClient("/api/v1/portal/home"),
  search: (query: string) => apiClient(`/api/v1/portal/search?q=${encodeURIComponent(query)}`),
  content: (contentId: string) => apiClient(`/api/v1/portal/content/${contentId}`)
};
