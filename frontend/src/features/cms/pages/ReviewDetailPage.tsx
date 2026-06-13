import { Check, Rocket, X } from 'lucide-react';
import { useEffect, useState } from 'react';
import { ContentSummary } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Table } from '../../../shared/components/Table';
import { listCmsContents, publishContent, reviewContent } from '../api/cmsContentApi';
import { ContentStatusBadge } from '../components/ContentStatusBadge';

export function ReviewDetailPage() {
  const [items, setItems] = useState<ContentSummary[]>([]);

  function load() {
    listCmsContents({ status: 'IN_REVIEW' }).then((result) => setItems(result.items));
  }

  useEffect(load, []);

  async function approve(id: string) {
    await reviewContent(id, 'APPROVE', 'Approved');
    load();
  }

  async function publish(id: string) {
    await publishContent(id);
    load();
  }

  return (
    <section className="section">
      <div className="section-header">
        <h2>검토 대기 콘텐츠</h2>
      </div>
      <Table columns={['제목', '유형', '상태', '작성자', '작업']}>
        {items.map((item) => (
          <tr key={item.id}>
            <td>{item.title}</td>
            <td>{item.type}</td>
            <td>
              <ContentStatusBadge status={item.status} />
            </td>
            <td>{item.author?.displayName}</td>
            <td>
              <div className="toolbar">
                <Button icon={<Check size={16} />} onClick={() => approve(item.id)}>
                  승인
                </Button>
                <Button icon={<Rocket size={16} />} variant="primary" onClick={() => publish(item.id)}>
                  게시
                </Button>
                <Button icon={<X size={16} />} variant="danger" onClick={() => reviewContent(item.id, 'REJECT', 'Needs changes').then(load)}>
                  반려
                </Button>
              </div>
            </td>
          </tr>
        ))}
      </Table>
    </section>
  );
}
