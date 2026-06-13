import { auditLogs } from "../../data/mockCms";

export function AuditLogPage() {
  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Audit Logs</h1>
          <p className="page-subtitle">주요 운영 이벤트를 행위자, 대상, 시간 기준으로 추적합니다.</p>
        </div>
      </section>
      <div className="toolbar">
        <div className="filters">
          <input className="role-select" placeholder="Actor" aria-label="Actor filter" />
          <input className="role-select" placeholder="Action" aria-label="Action filter" />
          <input className="role-select" type="date" aria-label="From date" />
        </div>
      </div>
      <div className="table-wrap">
        <table>
          <thead>
            <tr>
              <th>Actor</th>
              <th>Action</th>
              <th>Target</th>
              <th>Summary</th>
              <th>Created</th>
            </tr>
          </thead>
          <tbody>
            {auditLogs.map((log) => (
              <tr key={log.id}>
                <td>{log.actor}</td>
                <td>{log.action}</td>
                <td>{log.target}</td>
                <td>{log.summary}</td>
                <td>{new Date(log.createdAt).toLocaleString("ko-KR")}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </>
  );
}
