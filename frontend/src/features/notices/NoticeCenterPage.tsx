import { CheckCircle2, Megaphone } from "lucide-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { NoticeRequiredItem, api } from "../../api/client";

export function NoticeCenterPage() {
  const [items, setItems] = useState<NoticeRequiredItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  function load() {
    api<NoticeRequiredItem[]>("/notices/required")
      .then((result) => {
        setItems(result);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }

  useEffect(load, []);

  async function acknowledge(noticeId: string) {
    await api(`/notices/${noticeId}/acknowledgements`, { method: "PUT" });
    load();
  }

  return (
    <div className="grid">
      <div className="section-header">
        <h1 className="page-title">Notice Center</h1>
        <span className="meta">{items.filter((item) => !item.acknowledged).length} pending</span>
      </div>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <section className="panel">
        <div className="list">
          {items.length === 0 && <p className="meta">No required notices are assigned to you.</p>}
          {items.map((item) => (
            <article className="card" key={item.content.id}>
              <div className="section-header">
                <h2><Link to={`/portal/content/${item.content.id}`}>{item.content.title}</Link></h2>
                <span className={`badge ${item.acknowledged ? "success" : "warning"}`}>
                  {item.acknowledged ? "Acknowledged" : "Pending"}
                </span>
              </div>
              <p>{item.content.summary}</p>
              <div className="toolbar">
                <div className="meta">
                  <span><Megaphone size={14} /> {item.content.category.name}</span>
                  {item.acknowledgedAt && <span>{new Date(item.acknowledgedAt).toLocaleString()}</span>}
                </div>
                <button type="button" disabled={item.acknowledged} onClick={() => acknowledge(item.content.id)}>
                  <CheckCircle2 size={16} /> Acknowledge
                </button>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
