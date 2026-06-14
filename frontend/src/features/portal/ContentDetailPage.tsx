import { Download, Star } from "lucide-react";
import { useState } from "react";
import { Link, useParams } from "react-router-dom";
import { ContentTypeBadge } from "../../components/cms/StatusBadge";
import { NotFoundState } from "../../components/ui/StateViews";
import { NoticeAckBanner } from "../notices/NoticeAckBanner";
import { getPortalContent } from "./portalApi";

export function ContentDetailPage() {
  const { contentId } = useParams();
  const content = contentId ? getPortalContent(contentId) : undefined;
  const [bookmarked, setBookmarked] = useState(false);

  if (!content) {
    return <NotFoundState />;
  }

  return (
    <article>
      {content.requiresAcknowledgement ? <NoticeAckBanner noticeId={content.id} title={content.title} /> : null}
      <div className="section-header">
        <div>
          <div className="badge-row">
            <ContentTypeBadge type={content.contentType} />
            <span className="badge secondary">{content.category.name}</span>
          </div>
          <h1 className="page-title">{content.title}</h1>
          <p className="page-subtitle">{content.summary}</p>
        </div>
        <button className="btn secondary" type="button" onClick={() => setBookmarked((value) => !value)}>
          <Star size={18} aria-hidden="true" />
          {bookmarked ? "Saved" : "Save"}
        </button>
      </div>

      <section className="grid two section">
        <div className="panel">
          <div className="preview-body">
            {content.body.split("\n").map((line, index) => {
              if (line.startsWith("## ")) {
                return <h2 key={index}>{line.replace("## ", "")}</h2>;
              }
              if (line.startsWith("### ")) {
                return <h3 key={index}>{line.replace("### ", "")}</h3>;
              }
              if (!line.trim()) {
                return <br key={index} />;
              }
              return <p key={index}>{line}</p>;
            })}
          </div>
        </div>
        <aside className="panel">
          <h2>문서 정보</h2>
          <p className="muted">작성자 {content.author}</p>
          <p className="muted">버전 {content.versionNumber}</p>
          <p className="muted">수정일 {new Date(content.updatedAt).toLocaleString("ko-KR")}</p>
          <h3>첨부파일</h3>
          {content.attachments.length ? (
            <div className="grid">
              {content.attachments.map((attachment) => (
                <Link className="btn ghost" key={attachment.id} to="#">
                  <Download size={16} aria-hidden="true" />
                  {attachment.fileName}
                </Link>
              ))}
            </div>
          ) : (
            <p className="muted">첨부파일 없음</p>
          )}
        </aside>
      </section>
    </article>
  );
}
