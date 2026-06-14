import { apiClient } from "../../services/apiClient";
import { contents, type ContentDetail, type ContentStatus, type ContentType } from "../../data/mockCms";

export type ContentFilters = {
  status?: ContentStatus | "ALL";
  contentType?: ContentType | "ALL";
  query?: string;
};

export function listManagedContent(filters: ContentFilters = {}) {
  const query = filters.query?.toLowerCase().trim() ?? "";
  return contents.filter((item) => {
    const statusMatch = !filters.status || filters.status === "ALL" || item.status === filters.status;
    const typeMatch = !filters.contentType || filters.contentType === "ALL" || item.contentType === filters.contentType;
    const queryMatch = !query || `${item.title} ${item.summary} ${item.tags.join(" ")}`.toLowerCase().includes(query);
    return statusMatch && typeMatch && queryMatch;
  });
}

export function getManagedContent(contentId?: string): ContentDetail {
  return contents.find((item) => item.id === contentId) ?? contents[0];
}

export const contentApi = {
  list: () => apiClient("/api/v1/content"),
  create: (payload: unknown) => apiClient("/api/v1/content", { method: "POST", body: JSON.stringify(payload) }),
  submit: (contentId: string, payload: unknown) => apiClient(`/api/v1/content/${contentId}/submit`, { method: "POST", body: JSON.stringify(payload) })
};
