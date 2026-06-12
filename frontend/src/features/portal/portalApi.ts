import { api, ContentDetail, ContentPage, pageUrl } from "../../api/client";

export function getPortalFeed(mode?: "saved") {
  return mode === "saved" ? api<ContentPage>("/bookmarks") : api<ContentPage>("/portal/feed?size=12");
}

export function getPortalContent(contentId: string) {
  return api<ContentDetail>(`/portal/content/${contentId}`);
}

export function getContentList(status?: string) {
  return api<ContentPage>(pageUrl("/content", { status, size: 100 }));
}
