import { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { AcknowledgementReport, getAcknowledgementReport } from '../../admin/api/adminApi';
import { Badge } from '../../../shared/components/Badge';
import { Table } from '../../../shared/components/Table';

export function AcknowledgementReportPage() {
  const { contentId = '' } = useParams();
  const [report, setReport] = useState<AcknowledgementReport | null>(null);

  useEffect(() => {
    if (contentId) {
      getAcknowledgementReport(contentId).then(setReport);
    }
  }, [contentId]);

  if (!report) {
    return <section className="state-panel">확인 현황을 불러오는 중입니다.</section>;
  }

  return (
    <section className="section">
      <div className="section-header">
        <div>
          <h2>{report.title}</h2>
          <p className="muted">
            {report.acknowledgedCount}/{report.totalTargets} 확인 완료
          </p>
        </div>
      </div>
      <Table columns={['사용자', '부서', '상태', '확인 일시']}>
        {report.rows.map((row) => (
          <tr key={row.user.id}>
            <td>{row.user.displayName}</td>
            <td>{row.user.department?.name}</td>
            <td>
              <Badge tone={row.acknowledged ? 'green' : 'amber'}>{row.acknowledged ? '확인' : '미확인'}</Badge>
            </td>
            <td>{row.acknowledgedAt ? new Date(row.acknowledgedAt).toLocaleString() : '-'}</td>
          </tr>
        ))}
      </Table>
    </section>
  );
}
