import { History, Search } from "lucide-react";
import { FormEvent, useEffect, useState } from "react";
import { AuditLogPage as AuditLogPageDto, api, pageUrl } from "../../api/client";

export function AuditLogPage() {
  const [targetType, setTargetType] = useState("");
  const [logs, setLogs] = useState<AuditLogPageDto | null>(null);
  const [error, setError] = useState<string | null>(null);

  function load(filter = targetType) {
    api<AuditLogPageDto>(pageUrl("/admin/audit-logs", { targetType: filter, size: 50 }))
      .then((result) => {
        setLogs(result);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }

  useEffect(() => {
    load("");
  }, []);

  function submit(event: FormEvent) {
    event.preventDefault();
    load(targetType);
  }

  return (
    <div className="grid">
      <div className="section-header">
        <h1 className="page-title">Audit Logs</h1>
        <History size={22} aria-hidden="true" />
      </div>
      <form className="panel toolbar" onSubmit={submit}>
        <input value={targetType} onChange={(event) => setTargetType(event.target.value)} placeholder="Target type, e.g. CONTENT" />
        <button type="submit"><Search size={16} /> Filter</button>
      </form>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <section className="panel table-wrap">
        <table>
          <thead>
            <tr><th>Time</th><th>Actor</th><th>Action</th><th>Target</th><th>Outcome</th></tr>
          </thead>
          <tbody>
            {logs?.items.map((event) => (
              <tr key={event.id}>
                <td>{new Date(event.occurredAt).toLocaleString()}</td>
                <td>{event.actor?.email ?? "system"}</td>
                <td>{event.action}</td>
                <td>{event.targetType} · {event.targetId}</td>
                <td><span className={`badge ${event.outcome === "SUCCESS" ? "success" : "danger"}`}>{event.outcome}</span></td>
              </tr>
            ))}
          </tbody>
        </table>
        {logs?.items.length === 0 && <p className="meta">No audit events matched the filter.</p>}
      </section>
    </div>
  );
}
