import { Bell, Bookmark, Clock3, FileText, Search, Sparkles } from "lucide-react";
import { type FormEvent, type ReactNode, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ContentTypeBadge } from "../../components/cms/StatusBadge";
import type { ContentSummary } from "../../data/mockCms";
import { getPortalHome } from "./portalApi";

export function PortalHomePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const home = getPortalHome();

  function submit(event: FormEvent) {
    event.preventDefault();
    navigate(`/search?q=${encodeURIComponent(query.trim())}`);
  }

  return (
    <>
      <section className="portal-home-grid" aria-label="Portal dashboard">
        <div className="panel portal-search-panel">
          <div>
            <div className="badge-row">
              <span className="badge primary">확인 필요 {home.requiredNotices.length}</span>
              <span className="badge secondary">북마크 {home.bookmarks.length}</span>
            </div>
            <h1 className="page-title">포털 홈</h1>
            <p className="page-subtitle">중요 공지, 최근 콘텐츠, 북마크를 한 화면에서 확인합니다.</p>
          </div>
          <form className="search-form dashboard-search" onSubmit={submit}>
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="문서, 정책, 공지 검색" aria-label="Search content" />
            <button className="btn primary" type="submit">
              <Search size={18} aria-hidden="true" />
              Search
            </button>
          </form>
          <div className="portal-metrics" aria-label="Portal content summary">
            <Metric icon={<Bell size={18} aria-hidden="true" />} label="공지" value={home.requiredNotices.length} />
            <Metric icon={<Clock3 size={18} aria-hidden="true" />} label="최근" value={home.latestUpdates.length} />
            <Metric icon={<Bookmark size={18} aria-hidden="true" />} label="북마크" value={home.bookmarks.length} />
          </div>
        </div>

        <div className="panel dashboard-panel portal-notice-panel">
          <div className="section-header compact">
            <h2>중요 공지</h2>
            <Link className="btn secondary icon-btn" to="/notices" aria-label="공지 센터">
              <Bell size={16} aria-hidden="true" />
            </Link>
          </div>
          <div className="stack-list">
            {home.requiredNotices.map((notice) => (
              <DashboardListItem key={notice.id} item={notice} meta="확인 필요" />
            ))}
          </div>
        </div>

        <div className="panel dashboard-panel portal-recent-panel">
          <div className="section-header compact">
            <h2>최근 콘텐츠</h2>
            <span className="badge neutral">{home.latestUpdates.length}건</span>
          </div>
          <div className="stack-list">
            {home.latestUpdates.map((item) => (
              <DashboardListItem key={item.id} item={item} meta={new Date(item.updatedAt).toLocaleDateString("ko-KR")} />
            ))}
          </div>
        </div>

        <div className="panel dashboard-panel portal-bookmark-panel">
          <div className="section-header compact">
            <h2>북마크</h2>
            <Link className="btn secondary icon-btn" to="/bookmarks" aria-label="북마크">
              <Bookmark size={16} aria-hidden="true" />
            </Link>
          </div>
          <div className="stack-list">
            {home.bookmarks.map((item) => (
              <DashboardListItem key={item.id} item={item} meta={item.category.name} />
            ))}
          </div>
        </div>
      </section>

      <section className="section grid two">
        <div className="panel dashboard-panel">
          <div className="section-header compact">
            <h2>인기 지식</h2>
            <span className="badge accent">
              <Sparkles size={14} aria-hidden="true" />
              Popular
            </span>
          </div>
          <div className="stack-list">
            {home.popularContent.map((item) => (
              <DashboardListItem key={item.id} item={item} meta={`${item.views.toLocaleString("ko-KR")} views`} />
            ))}
          </div>
        </div>

        <div className="panel dashboard-panel">
          <div className="section-header compact">
            <h2>추천 카테고리</h2>
          </div>
          <div className="category-shortcuts">
            {home.categoryShortcuts.map((category) => (
              <Link key={category.id} className="category-shortcut" to={`/search?category=${category.id}`}>
                <span>{category.name}</span>
                <small>{category.description}</small>
              </Link>
            ))}
          </div>
        </div>
      </section>
    </>
  );
}

function Metric({ icon, label, value }: { icon: ReactNode; label: string; value: number }) {
  return (
    <div className="portal-metric">
      {icon}
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function DashboardListItem({ item, meta }: { item: ContentSummary; meta: string }) {
  return (
    <Link className="dashboard-list-item" to={`/content/${item.id}`}>
      <span className="dashboard-list-icon">
        <FileText size={16} aria-hidden="true" />
      </span>
      <span className="dashboard-list-copy">
        <span className="badge-row">
          <ContentTypeBadge type={item.contentType} />
          {item.isImportant ? <span className="badge primary">IMPORTANT</span> : null}
        </span>
        <strong>{item.title}</strong>
        <span className="muted">{item.summary}</span>
      </span>
      <span className="dashboard-list-meta">{meta}</span>
    </Link>
  );
}
