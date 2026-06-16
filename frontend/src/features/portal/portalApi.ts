import { apiClient } from "../../services/apiClient";
import { bookmarkedContent, categories, publishedContent, requiredNotices, searchContent, type ContentDetail, type ContentType } from "../../data/mockCms";

export function getPortalHome() {
  return {
    requiredNotices,
    latestUpdates: publishedContent().slice(0, 4),
    bookmarks: bookmarkedContent().slice(0, 3),
    popularContent: [...publishedContent()].sort((a, b) => b.views - a.views).slice(0, 3),
    categoryShortcuts: categories
  };
}

export function getPortalContent(contentId: string): ContentDetail | undefined {
  return publishedContent().find((item) => item.id === contentId);
}

export function getSearchResults(query: string, contentType?: ContentType) {
  return searchContent(query, contentType);
}

export const portalApi = {
  home: () => apiClient("/api/v1/portal/home"),
  search: (query: string, contentType?: ContentType) => {
    const params = new URLSearchParams({ q: query });
    if (contentType) {
      params.set("contentType", contentType);
    }
    return apiClient(`/api/v1/portal/search?${params.toString()}`);
  },
  content: (contentId: string) => apiClient(`/api/v1/portal/content/${contentId}`)
};
