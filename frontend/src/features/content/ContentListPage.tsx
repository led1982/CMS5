import { Edit, Plus } from "lucide-react";
import { useMemo, useState } from "react";
import { Link } from "react-router-dom";
import { ContentTypeBadge, StatusBadge } from "../../components/cms/StatusBadge";
import type { ContentStatus, ContentType } from "../../data/mockCms";
import { listManagedContent } from "./contentApi";

const statuses: Array<"ALL" | ContentStatus> = ["ALL", "DRAFT", "IN_REVIEW", "APPROVED", "PUBLISHED", "ARCHIVED"];
const types: Array<"ALL" | ContentType> = ["ALL", "ARTICLE", "DOCUMENT", "NOTICE"];

export function ContentListPage() {
  const [status, setStatus] = useState<"ALL" | ContentStatus>("ALL");
  const [type, setType] = useState<"ALL" | ContentType>("ALL");
  const [query, setQuery] = useState("");
  const rows = useMemo(() => listManagedContent({ status, contentType: type, query }), [status, type, query]);

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Content</h1>
          <p className="page-subtitle">상태, 게시 여부, 첨부, 수정일, 작성자를 한 화면에서 확인합니다.</p>
        </div>
        <Link className="btn primary" to="/admin/content/new">
          <Plus size={18} aria-hidden="true" />
          콘텐츠 작성
        </Link>
      </section>

      <div className="toolbar">
        <div className="filters">
          <input className="role-select" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search title" aria-label="Search managed content" />
          <select className="role-select" value={status} onChange={(event) => setStatus(event.target.value as "ALL" | ContentStatus)} aria-label="Status filter">
            {statuses.map((item) => (
              <option key={item}>{item}</option>
            ))}
          </select>
          <select className="role-select" value={type} onChange={(event) => setType(event.target.value as "ALL" | ContentType)} aria-label="Type filter">
            {types.map((item) => (
              <option key={item}>{item}</option>
            ))}
          </select>
        </div>
        <span className="muted" aria-live="polite">
          {rows.length} items
        </span>
      </div>

      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Title</th>
              <th>Type</th>
              <th>Status</th>
              <th>Category</th>
              <th>Attachments</th>
              <th>Updated</th>
              <th>Author</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {rows.map((item) => (
              <tr key={item.id}>
                <td>{item.title}</td>
                <td>
                  <ContentTypeBadge type={item.contentType} />
                </td>
                <td>
                  <StatusBadge status={item.status} />
                </td>
                <td>{item.category.name}</td>
                <td>{item.attachments.length}</td>
                <td>{new Date(item.updatedAt).toLocaleDateString("ko-KR")}</td>
                <td>{item.author}</td>
                <td>
                  <Link className="btn ghost" to={`/admin/content/${item.id}/edit`}>
                    <Edit size={16} aria-hidden="true" />
                    Edit
                  </Link>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
