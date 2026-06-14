import { apiClient } from "../../services/apiClient";
import { contents } from "../../data/mockCms";

export function getAnalyticsSummary() {
  return {
    metrics: [
      { name: "Views", value: contents.reduce((sum, item) => sum + item.views, 0), unit: "count" },
      { name: "Searches", value: 1842, unit: "count" },
      { name: "No-result rate", value: 3.8, unit: "%" },
      { name: "Ack rate", value: 63.2, unit: "%" }
    ],
    popularContent: [...contents].sort((a, b) => b.views - a.views).slice(0, 4),
    topSearchQueries: ["보안", "릴리스", "복리후생", "비밀번호"],
    noResultQueries: ["출장 정산 v3", "legacy vpn"]
  };
}

export const analyticsApi = {
  contents: (from: string, to: string) => apiClient(`/api/v1/admin/analytics/contents?from=${from}&to=${to}`),
  export: (from: string, to: string) => apiClient(`/api/v1/admin/analytics/contents/export?from=${from}&to=${to}`)
};
