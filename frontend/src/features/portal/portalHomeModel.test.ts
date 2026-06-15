import assert from "node:assert/strict";
import { describe, it } from "node:test";
import { categories, searchContent } from "../../data/mockCms";
import { buildPortalHomeDashboard } from "./portalHomeModel";

describe("buildPortalHomeDashboard", () => {
  it("provides the sections required by the portal dashboard", () => {
    const home = buildPortalHomeDashboard();

    assert.ok(home.requiredNotices.length > 0);
    assert.ok(home.latestUpdates.length > 0);
    assert.ok(home.bookmarkedContent.length > 0);
    assert.ok(home.categoryShortcuts.length > 0);
    assert.equal(home.metrics.pendingNotices, home.requiredNotices.length);
    assert.equal(home.metrics.bookmarks, home.bookmarkedContent.length);
  });

  it("sorts recent content by the latest update first", () => {
    const { latestUpdates } = buildPortalHomeDashboard();

    for (let index = 1; index < latestUpdates.length; index += 1) {
      assert.ok(Date.parse(latestUpdates[index - 1].updatedAt) >= Date.parse(latestUpdates[index].updatedAt));
    }
  });

  it("filters search results for dashboard category shortcuts", () => {
    const security = categories.find((category) => category.slug === "security");
    assert.ok(security);

    const results = searchContent("", undefined, security.id);
    assert.ok(results.length > 0);
    assert.ok(results.every((item) => item.category.id === security.id));
  });
});
