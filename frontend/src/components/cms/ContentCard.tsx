import { ArrowRight, FileText } from "lucide-react";
import { Link } from "react-router-dom";
import type { ContentSummary } from "../../data/mockCms";
import { ContentTypeBadge, StatusBadge } from "./StatusBadge";

export function ContentCard({ item, to }: { item: ContentSummary; to?: string }) {
  return (
    <article className="card content-card">
      <div className="badge-row">
        <ContentTypeBadge type={item.contentType} />
        <StatusBadge status={item.status} />
        {item.isImportant ? <span className="badge primary">IMPORTANT</span> : null}
      </div>
      <h3 className="card-title">{item.title}</h3>
      <p className="muted">{item.summary}</p>
      <div className="badge-row muted">
        <FileText size={16} aria-hidden="true" />
        <span>{item.category.name}</span>
        <span>{new Date(item.updatedAt).toLocaleDateString("ko-KR")}</span>
      </div>
      <Link className="btn ghost" to={to ?? `/content/${item.id}`}>
        열람
        <ArrowRight size={16} aria-hidden="true" />
      </Link>
    </article>
  );
}
