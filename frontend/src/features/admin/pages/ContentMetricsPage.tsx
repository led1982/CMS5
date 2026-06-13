import { useEffect, useState } from 'react';
import { Badge } from '../../../shared/components/Badge';
import { Table } from '../../../shared/components/Table';
import { ContentMetrics, getContentMetrics } from '../api/adminApi';

export function ContentMetricsPage() {
  const [metrics, setMetrics] = useState<ContentMetrics | null>(null);

  useEffect(() => {
    getContentMetrics().then(setMetrics);
  }, []);

  if (!metrics) {
    return <section className="state-panel">운영 지표를 불러오는 중입니다.</section>;
  }

  return (
    <div className="page">
      <section className="section">
        <div className="section-header">
          <h2>콘텐츠 운영 지표</h2>
        </div>
        <div className="cards">
          {Object.entries(metrics.byStatus).map(([status, count]) => (
            <div className="content-card" key={status}>
              <Badge tone="blue">{status}</Badge>
              <strong>{count}</strong>
            </div>
          ))}
          <div className="content-card">
            <Badge tone="amber">UNACKNOWLEDGED</Badge>
            <strong>{metrics.unacknowledgedAnnouncements}</strong>
          </div>
        </div>
      </section>
      <section className="section">
        <div className="section-header">
          <h2>인기 콘텐츠</h2>
        </div>
        <Table columns={['제목', '조회수']}>
          {metrics.topViewed.map((item) => (
            <tr key={item.contentId}>
              <td>{item.title}</td>
              <td>{item.viewCount}</td>
            </tr>
          ))}
        </Table>
      </section>
    </div>
  );
}
