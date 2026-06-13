import { useEffect, useState } from 'react';
import { Badge } from '../../../shared/components/Badge';
import { Table } from '../../../shared/components/Table';
import { AuditLogDto, listAuditLogs } from '../api/adminApi';

export function AuditLogPage() {
  const [logs, setLogs] = useState<AuditLogDto[]>([]);

  useEffect(() => {
    listAuditLogs().then(setLogs);
  }, []);

  return (
    <section className="section">
      <div className="section-header">
        <h2>감사 이력</h2>
      </div>
      <Table columns={['시각', '작업자', '행위', '대상', '요약']}>
        {logs.map((log) => (
          <tr key={log.id}>
            <td>{new Date(log.createdAt).toLocaleString()}</td>
            <td>{log.actorEmail}</td>
            <td>
              <Badge tone="blue">{log.action}</Badge>
            </td>
            <td>{log.targetType}</td>
            <td>{log.afterSnapshot}</td>
          </tr>
        ))}
      </Table>
    </section>
  );
}
