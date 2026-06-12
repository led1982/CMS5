import { api } from "../../api/client";

export function addBookmark(contentId: string) {
  return api<void>("/bookmarks", {
    method: "POST",
    body: JSON.stringify({ contentId })
  });
}

export function removeBookmark(contentId: string) {
  return api<void>(`/bookmarks/${contentId}`, { method: "DELETE" });
}
