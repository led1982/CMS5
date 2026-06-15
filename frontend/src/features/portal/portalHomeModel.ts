import { bookmarkedContent, categories, publishedContent, requiredNotices, type Category, type ContentSummary, type NoticeSummary } from "../../data/mockCms";

export type PortalHomeDashboard = {
  requiredNotices: NoticeSummary[];
  latestUpdates: ContentSummary[];
  popularContent: ContentSummary[];
  bookmarkedContent: ContentSummary[];
  categoryShortcuts: Category[];
  metrics: {
    pendingNotices: number;
    latestUpdates: number;
    bookmarks: number;
    searchableContent: number;
  };
};

export function buildPortalHomeDashboard(): PortalHomeDashboard {
  const visibleContent = publishedContent();
  const latestUpdates = [...visibleContent].sort((a, b) => Date.parse(b.updatedAt) - Date.parse(a.updatedAt)).slice(0, 4);
  const popularContent = [...visibleContent].sort((a, b) => b.views - a.views).slice(0, 4);
  const savedContent = bookmarkedContent().slice(0, 3);
  const activeCategories = categories.filter((category) => category.isActive).sort((a, b) => a.sortOrder - b.sortOrder);

  return {
    requiredNotices: requiredNotices.slice(0, 3),
    latestUpdates,
    popularContent,
    bookmarkedContent: savedContent,
    categoryShortcuts: activeCategories.slice(0, 4),
    metrics: {
      pendingNotices: requiredNotices.length,
      latestUpdates: latestUpdates.length,
      bookmarks: savedContent.length,
      searchableContent: visibleContent.length
    }
  };
}
