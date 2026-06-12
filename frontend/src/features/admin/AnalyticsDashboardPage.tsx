import { BarChart3, FileText, History, Megaphone } from "lucide-react";
import type { ReactNode } from "react";
import { useEffect, useState } from "react";
import { AnalyticsSummary, api } from "../../api/client";

export function AnalyticsDashboardPage() {
  const [summary, setSummary] = useState<AnalyticsSummary | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    api<AnalyticsSummary>("/admin/analytics/summary")
      .then((result) => {
        setSummary(result);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }, []);

  return (
    <div className="grid">
      <div className="section-header">
        <h1 className="page-title">Analytics</h1>
        <BarChart3 size={22} aria-hidden="true" />
      </div>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <section className="metric-row">
        <Metric icon={<FileText size={20} />} label="Published Content" value={summary?.publishedContentCount} />
        <Metric icon={<FileText size={20} />} label="Draft Content" value={summary?.draftContentCount} />
        <Metric icon={<Megaphone size={20} />} label="Required Notices" value={summary?.requiredNoticeCount} />
        <Metric icon={<History size={20} />} label="Audit Events" value={summary?.recentAuditEventCount} />
      </section>
      <section className="panel">
        <h2>Acknowledgement Rate</h2>
        <div className="markdown-preview">
          {summary ? `${Math.round(summary.acknowledgementRate * 100)}% of required notice versions have acknowledgements recorded.` : "Loading analytics..."}
        </div>
      </section>
    </div>
  );
}

function Metric({ icon, label, value }: { icon: ReactNode; label: string; value?: number }) {
  return (
    <div className="card metric">
      <div className="section-header">
        <strong>{value ?? "-"}</strong>
        {icon}
      </div>
      <div className="meta">{label}</div>
    </div>
  );
}
