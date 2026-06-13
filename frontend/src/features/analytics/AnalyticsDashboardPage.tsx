import { Download } from "lucide-react";
import { ContentCard } from "../../components/cms/ContentCard";
import { getAnalyticsSummary } from "./analyticsApi";

export function AnalyticsDashboardPage() {
  const summary = getAnalyticsSummary();

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Analytics</h1>
          <p className="page-subtitle">조회, 검색, 공지 확인률, 오래된 콘텐츠를 기간 조건으로 확인합니다.</p>
        </div>
        <button className="btn primary" type="button">
          <Download size={18} aria-hidden="true" />
          Export
        </button>
      </section>
      <div className="toolbar">
        <div className="filters">
          <input className="role-select" type="date" defaultValue="2026-06-01" aria-label="From date" />
          <input className="role-select" type="date" defaultValue="2026-06-13" aria-label="To date" />
          <select className="role-select" aria-label="Content type">
            <option>ALL</option>
            <option>ARTICLE</option>
            <option>DOCUMENT</option>
            <option>NOTICE</option>
          </select>
        </div>
      </div>
      <div className="grid four">
        {summary.metrics.map((metric) => (
          <div className="card kpi" key={metric.name}>
            <span className="muted">{metric.name}</span>
            <strong>
              {metric.value}
              {metric.unit === "%" ? "%" : ""}
            </strong>
          </div>
        ))}
      </div>
      <section className="section grid two">
        <div>
          <div className="section-header">
            <h2>Popular Content</h2>
          </div>
          <div className="grid">
            {summary.popularContent.map((item) => (
              <ContentCard key={item.id} item={item} />
            ))}
          </div>
        </div>
        <div className="panel">
          <h2>Search Terms</h2>
          <div className="grid">
            {summary.topSearchQueries.map((term) => (
              <span className="badge secondary" key={term}>
                {term}
              </span>
            ))}
          </div>
          <h2>No-result Queries</h2>
          <div className="grid">
            {summary.noResultQueries.map((term) => (
              <span className="badge accent" key={term}>
                {term}
              </span>
            ))}
          </div>
        </div>
      </section>
    </>
  );
}
