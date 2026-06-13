import { FilePlus, RefreshCcw } from 'lucide-react';
import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { ContentStatus, ContentSummary, ContentType } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Table } from '../../../shared/components/Table';
import { listCmsContents } from '../api/cmsContentApi';
import { ContentStatusBadge } from '../components/ContentStatusBadge';

export function CmsContentListPage() {
  const [status, setStatus] = useState<ContentStatus | ''>('');
  const [type, setType] = useState<ContentType | ''>('');
  const [items, setItems] = useState<ContentSummary[]>([]);

  function load() {
    listCmsContents({ status: status || undefined, type: type || undefined }).then((result) => setItems(result.items));
  }

  useEffect(() => {
    load();
  }, [status, type]);

  return (
    <div className="page">
      <section className="section">
        <div className="section-header">
          <h2>CMS 콘텐츠 목록</h2>
          <Button as={Link} to="/cms/contents/new" icon={<FilePlus size={16} />} variant="primary">
            새 콘텐츠
          </Button>
        </div>
        <div className="toolbar">
          <select className="select" value={status} onChange={(event) => setStatus(event.target.value as ContentStatus | '')}>
            <option value="">전체 상태</option>
            <option value="DRAFT">DRAFT</option>
            <option value="IN_REVIEW">IN_REVIEW</option>
            <option value="PUBLISHED">PUBLISHED</option>
            <option value="ARCHIVED">ARCHIVED</option>
          </select>
          <select className="select" value={type} onChange={(event) => setType(event.target.value as ContentType | '')}>
            <option value="">전체 유형</option>
            <option value="KNOWLEDGE">KNOWLEDGE</option>
            <option value="DOCUMENT">DOCUMENT</option>
            <option value="ANNOUNCEMENT">ANNOUNCEMENT</option>
          </select>
          <Button icon={<RefreshCcw size={16} />} onClick={load}>
            새로고침
          </Button>
        </div>
      </section>
      <section className="section">
        <Table columns={['제목', '유형', '상태', '작성자', '수정일', '작업']}>
          {items.map((item) => (
            <tr key={item.id}>
              <td>
                <strong>{item.title}</strong>
                <div className="muted">{item.summary}</div>
              </td>
              <td>{item.type}</td>
              <td>
                <ContentStatusBadge status={item.status} />
              </td>
              <td>{item.author?.displayName}</td>
              <td>{new Date(item.updatedAt).toLocaleString()}</td>
              <td>
                <Button as={Link} to={`/cms/contents/${item.id}/edit`} variant="secondary">
                  편집
                </Button>
              </td>
            </tr>
          ))}
        </Table>
      </section>
    </div>
  );
}
