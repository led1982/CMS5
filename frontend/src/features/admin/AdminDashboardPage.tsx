import { AlertTriangle, FileText, Plus, ShieldCheck } from "lucide-react";
import { Link } from "react-router-dom";
import { contents } from "../../data/mockCms";

export function AdminDashboardPage() {
  const inReview = contents.filter((item) => item.status === "IN_REVIEW").length;
  const published = contents.filter((item) => item.status === "PUBLISHED").length;
  const exceptions = contents.filter((item) => item.attachments.some((attachment) => attachment.scanStatus !== "CLEAN")).length;

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">Dashboard</h1>
          <p className="page-subtitle">콘텐츠 운영 현황과 우선 작업을 고밀도로 확인합니다.</p>
        </div>
        <Link className="btn primary" to="/admin/content/new">
          <Plus size={18} aria-hidden="true" />
          콘텐츠 작성
        </Link>
      </section>
      <div className="grid three">
        <div className="card kpi">
          <FileText size={24} aria-hidden="true" />
          <span className="muted">Published</span>
          <strong>{published}</strong>
        </div>
        <div className="card kpi">
          <ShieldCheck size={24} aria-hidden="true" />
          <span className="muted">Review Queue</span>
          <strong>{inReview}</strong>
        </div>
        <div className="card kpi">
          <AlertTriangle size={24} aria-hidden="true" />
          <span className="muted">Exceptions</span>
          <strong>{exceptions}</strong>
        </div>
      </div>
      <section className="section panel">
        <div className="section-header">
          <h2>빠른 작업</h2>
        </div>
        <div className="filters">
          <Link className="btn secondary" to="/admin/content">
            콘텐츠 목록
          </Link>
          <Link className="btn secondary" to="/admin/review">
            검토 큐
          </Link>
          <Link className="btn secondary" to="/admin/audit">
            감사 로그
          </Link>
        </div>
      </section>
    </>
  );
}
