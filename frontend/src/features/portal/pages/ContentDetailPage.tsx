import { Download, Star } from 'lucide-react';
import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { ContentDetail } from '../../../shared/api/openapiClient';
import { Badge } from '../../../shared/components/Badge';
import { Button } from '../../../shared/components/Button';
import { AcknowledgementButton } from '../components/AcknowledgementButton';
import { bookmarkContent, getPortalContent } from '../api/portalApi';

export function ContentDetailPage() {
  const { contentId = '' } = useParams();
  const [content, setContent] = useState<ContentDetail | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!contentId) return;
    getPortalContent(contentId)
      .then((result) => {
        setContent(result);
        setError('');
      })
      .catch((err: Error) => {
        setError(err.message);
        setContent(null);
      });
  }, [contentId]);

  if (error) {
    return (
      <section className="state-panel">
        <h1>콘텐츠를 열 수 없습니다</h1>
        <p className="muted">{error}</p>
      </section>
    );
  }

  if (!content) {
    return <section className="state-panel">콘텐츠를 불러오는 중입니다.</section>;
  }

  return (
    <article className="page">
      <section className="section">
        <div className="section-header">
          <div>
            <Badge tone={content.type === 'ANNOUNCEMENT' ? 'amber' : 'blue'}>{content.type}</Badge>
            <h2>{content.title}</h2>
            <p className="muted">{content.summary}</p>
          </div>
          <Button icon={<Star size={16} />} variant="secondary" onClick={() => bookmarkContent(content.id)}>
            북마크
          </Button>
        </div>
        <div className="markdown-preview">{content.body}</div>
      </section>

      {content.requiresAcknowledgement ? (
        <section className="section">
          <div className="section-header">
            <h2>공지 확인</h2>
            <AcknowledgementButton contentId={content.id} acknowledged={content.acknowledged} />
          </div>
        </section>
      ) : null}

      <section className="section">
        <div className="section-header">
          <h2>첨부파일</h2>
        </div>
        {content.attachments.length === 0 ? <p className="muted">첨부파일이 없습니다.</p> : null}
        <div className="cards">
          {content.attachments.map((attachment) => (
            <div className="content-card" key={attachment.id}>
              <strong>{attachment.originalFilename}</strong>
              <span className="muted">{Math.ceil(attachment.fileSize / 1024)} KB · {attachment.status}</span>
              <Button icon={<Download size={16} />} variant="secondary">
                다운로드
              </Button>
            </div>
          ))}
        </div>
      </section>
    </article>
  );
}
