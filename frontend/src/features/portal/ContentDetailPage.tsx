import { Bookmark, Download, RotateCcw } from "lucide-react";
import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ContentDetail } from "../../api/client";
import { addBookmark } from "./bookmarkApi";
import { getPortalContent } from "./portalApi";

export function ContentDetailPage() {
  const { contentId } = useParams();
  const [content, setContent] = useState<ContentDetail | null>(null);
  const [message, setMessage] = useState<string | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (!contentId) {
      return;
    }
    getPortalContent(contentId)
      .then((result) => {
        setContent(result);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }, [contentId]);

  async function bookmark() {
    if (!content) {
      return;
    }
    await addBookmark(content.id);
    setMessage("Bookmarked");
  }

  if (error) {
    return (
      <div className="panel">
        <h1>Content unavailable</h1>
        <p className="error-text">{error}</p>
        <Link className="button secondary" to="/"><RotateCcw size={16} /> Back to portal</Link>
      </div>
    );
  }

  if (!content) {
    return <div className="panel">Loading content...</div>;
  }

  return (
    <article className="grid">
      <div className="section-header">
        <div>
          <span className="badge success">{content.status}</span>
          <h1 className="page-title">{content.title}</h1>
          <div className="meta">
            <span>{content.type}</span>
            <span>{content.category.name}</span>
            <span>Version {content.versionNumber}</span>
            <span>Updated {new Date(content.updatedAt).toLocaleString()}</span>
          </div>
        </div>
        <button type="button" onClick={bookmark}><Bookmark size={16} /> Bookmark</button>
      </div>
      {message && <div className="banner">{message}</div>}
      <div className="grid two">
        <section className="panel">
          <p>{content.summary}</p>
          <div className="markdown-preview">{content.body}</div>
        </section>
        <aside className="panel">
          <h2>Metadata</h2>
          <dl>
            <dt>Owner</dt>
            <dd>{content.owner.displayName}</dd>
            <dt>Audience</dt>
            <dd>{content.audiences.join(", ") || "All employees"}</dd>
            <dt>Tags</dt>
            <dd>{content.tags.join(", ") || "None"}</dd>
          </dl>
          <h3>Attachments</h3>
          <div className="list">
            {content.attachments.length === 0 && <p className="meta">No attachments.</p>}
            {content.attachments.map((attachment) => (
              <a className="button secondary" key={attachment.id} href={attachment.downloadUrl}>
                <Download size={16} /> {attachment.filename}
              </a>
            ))}
          </div>
        </aside>
      </div>
    </article>
  );
}
