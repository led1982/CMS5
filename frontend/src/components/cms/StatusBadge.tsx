import type { ContentStatus, ContentType } from "../../data/mockCms";

const statusTone: Record<ContentStatus, string> = {
  DRAFT: "neutral",
  IN_REVIEW: "accent",
  REJECTED: "danger",
  APPROVED: "secondary",
  SCHEDULED: "accent",
  PUBLISHED: "success",
  ARCHIVED: "neutral",
  EXPIRED: "danger"
};

export function StatusBadge({ status }: { status: ContentStatus }) {
  return <span className={`badge ${statusTone[status]}`}>{status}</span>;
}

export function ContentTypeBadge({ type }: { type: ContentType }) {
  const tone = type === "NOTICE" ? "primary" : type === "DOCUMENT" ? "secondary" : "neutral";
  return <span className={`badge ${tone}`}>{type}</span>;
}
