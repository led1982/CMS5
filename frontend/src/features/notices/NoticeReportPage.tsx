import { requiredNotices } from "../../data/mockCms";

export function NoticeReportPage() {
  const targetCount = 20000;
  const acknowledgedCount = 12640;
  const pendingCount = targetCount - acknowledgedCount;

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Notice Report</h1>
          <p className="page-subtitle">중요 공지 대상자 수, 확인률, 미확인 대상을 확인합니다.</p>
        </div>
      </section>
      <div className="grid three">
        <div className="card kpi">
          <span className="muted">Target</span>
          <strong>{targetCount.toLocaleString()}</strong>
        </div>
        <div className="card kpi">
          <span className="muted">Acknowledged</span>
          <strong>{acknowledgedCount.toLocaleString()}</strong>
        </div>
        <div className="card kpi">
          <span className="muted">Pending</span>
          <strong>{pendingCount.toLocaleString()}</strong>
        </div>
      </div>
      <section className="section table-wrap">
        <table>
          <thead>
            <tr>
              <th>Notice</th>
              <th>Status</th>
              <th>Rate</th>
              <th>Pending</th>
            </tr>
          </thead>
          <tbody>
            {requiredNotices.map((notice) => (
              <tr key={notice.id}>
                <td>{notice.title}</td>
                <td>{notice.acknowledgementStatus}</td>
                <td>{Math.round((acknowledgedCount / targetCount) * 100)}%</td>
                <td>{pendingCount.toLocaleString()}</td>
              </tr>
            ))}
          </tbody>
        </table>
      </section>
    </>
  );
}
