import { Bookmark, CalendarDays, FileText, Megaphone, Search } from "lucide-react";
import type { ReactNode } from "react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { ContentPage, ContentSummary, NoticeRequiredItem, api } from "../../api/client";
import { getPortalFeed } from "./portalApi";

interface Props {
  mode?: "saved";
}

export function PortalHomePage({ mode }: Props) {
  const [feed, setFeed] = useState<ContentPage | null>(null);
  const [notices, setNotices] = useState<NoticeRequiredItem[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    let mounted = true;
    Promise.all([getPortalFeed(mode), api<NoticeRequiredItem[]>("/notices/required")])
      .then(([feedResult, noticeResult]) => {
        if (mounted) {
          setFeed(feedResult);
          setNotices(noticeResult);
          setError(null);
        }
      })
      .catch((err: Error) => mounted && setError(err.message));
    return () => {
      mounted = false;
    };
  }, [mode]);

  const requiredNotices = notices.filter((notice) => !notice.acknowledged);

  return (
    <div className="grid">
      <div className="section-header">
        <div>
          <h1 className="page-title">{mode === "saved" ? "Saved Content" : "Portal"}</h1>
          <p className="meta">Published content is filtered by your role and audience membership.</p>
        </div>
        <Link className="button secondary" to="/search"><Search size={16} /> Search</Link>
      </div>

      {error && <div className="panel error-text" role="alert">{error}</div>}

      {mode !== "saved" && (
        <section className="grid two" aria-label="Portal highlights">
          <div className="panel">
            <div className="section-header">
              <h2>Required Notices</h2>
              <Link to="/notices">View all</Link>
            </div>
            <div className="list">
              {requiredNotices.length === 0 && <p className="meta">No pending acknowledgements.</p>}
              {requiredNotices.slice(0, 3).map((notice) => (
                <ContentCard key={notice.content.id} content={notice.content} icon={<Megaphone size={18} />} />
              ))}
            </div>
          </div>
          <div className="panel">
            <h2>Portal Status</h2>
            <div className="metric-row" style={{ gridTemplateColumns: "repeat(2, minmax(0, 1fr))" }}>
              <div className="card metric">
                <strong>{feed?.totalItems ?? "-"}</strong>
                <div className="meta">Visible items</div>
              </div>
              <div className="card metric">
                <strong>{requiredNotices.length}</strong>
                <div className="meta">Pending notices</div>
              </div>
            </div>
          </div>
        </section>
      )}

      <section className="panel">
        <div className="section-header">
          <h2>{mode === "saved" ? "Bookmarked Content" : "Latest Knowledge"}</h2>
          <span className="meta">{feed ? `${feed.totalItems} items` : "Loading"}</span>
        </div>
        <div className="grid three">
          {!feed && [1, 2, 3].map((item) => <div className="card" key={item}>Loading content...</div>)}
          {feed?.items.length === 0 && <p className="meta">No content is available for this view.</p>}
          {feed?.items.map((content) => (
            <ContentCard key={content.id} content={content} icon={content.type === "NOTICE" ? <Megaphone size={18} /> : <FileText size={18} />} />
          ))}
        </div>
      </section>
    </div>
  );
}

function ContentCard({ content, icon }: { content: ContentSummary; icon: ReactNode }) {
  return (
    <article className="card">
      <div className="section-header">
        <span className={`badge ${content.status === "PUBLISHED" ? "success" : ""}`}>{content.type}</span>
        {content.acknowledgementRequired && <Bookmark size={16} aria-label="Acknowledgement required" />}
      </div>
      <h3><Link to={`/portal/content/${content.id}`}>{content.title}</Link></h3>
      <p>{content.summary}</p>
      <div className="meta">
        <span>{icon} {content.category.name}</span>
        <span><CalendarDays size={14} /> {new Date(content.updatedAt).toLocaleDateString()}</span>
      </div>
    </article>
  );
}
