import { Bell, BookMarked, ChevronRight, Clock3, FileText, Search, Sparkles } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import type { ContentSummary } from "../../data/mockCms";
import { getPortalHome } from "./portalApi";

const dateFormatter = new Intl.DateTimeFormat("ko-KR", {
  month: "short",
  day: "numeric"
});

function formatDate(value: string) {
  return dateFormatter.format(new Date(value));
}

function DashboardContentRow({ item, important = false }: { item: ContentSummary; important?: boolean }) {
  return (
    <Link className={`dashboard-list-item ${important ? "important" : ""}`} to={`/content/${item.id}`}>
      <div className="dashboard-item-title-row">
        <span className="dashboard-item-title">{item.title}</span>
        <ChevronRight size={16} aria-hidden="true" />
      </div>
      <p className="dashboard-item-summary">{item.summary}</p>
      <div className="dashboard-item-meta">
        <span>
          <FileText size={14} aria-hidden="true" />
          {item.category.name}
        </span>
        <span>
          <Clock3 size={14} aria-hidden="true" />
          {formatDate(item.updatedAt)}
        </span>
      </div>
    </Link>
  );
}

export function PortalHomePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const home = getPortalHome();

  function submit(event: FormEvent) {
    event.preventDefault();
    const trimmedQuery = query.trim();
    navigate(trimmedQuery ? `/search?q=${encodeURIComponent(trimmedQuery)}` : "/search");
  }

  return (
    <div className="portal-dashboard">
      <section className="dashboard-header" aria-labelledby="portal-home-title">
        <div>
          <span className="badge secondary">CMS5 Portal</span>
          <h1 id="portal-home-title" className="page-title">
            포털 홈
          </h1>
        </div>
        <div className="dashboard-metrics" aria-label="Portal summary">
          <div className="metric-card">
            <Bell size={18} aria-hidden="true" />
            <strong>{home.metrics.pendingNotices}</strong>
            <span>확인 필요</span>
          </div>
          <div className="metric-card">
            <Clock3 size={18} aria-hidden="true" />
            <strong>{home.metrics.latestUpdates}</strong>
            <span>최근 콘텐츠</span>
          </div>
          <div className="metric-card">
            <BookMarked size={18} aria-hidden="true" />
            <strong>{home.metrics.bookmarks}</strong>
            <span>북마크</span>
          </div>
          <div className="metric-card">
            <FileText size={18} aria-hidden="true" />
            <strong>{home.metrics.searchableContent}</strong>
            <span>검색 가능</span>
          </div>
        </div>
      </section>

      <div className="portal-dashboard-grid">
        <section className="dashboard-panel search-panel" aria-labelledby="portal-search-title">
          <div className="dashboard-panel-header">
            <div className="panel-title-row">
              <Search size={20} aria-hidden="true" />
              <h2 id="portal-search-title">통합 검색</h2>
            </div>
          </div>
          <form className="search-form dashboard-search-form" onSubmit={submit}>
            <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="제목, 태그, 본문 검색" aria-label="Search content" />
            <button className="btn primary" type="submit">
              <Search size={18} aria-hidden="true" />
              Search
            </button>
          </form>
          <div className="category-shortcuts" aria-label="Category shortcuts">
            {home.categoryShortcuts.map((category) => (
              <Link key={category.id} className="category-chip" to={`/search?category=${category.id}`}>
                {category.name}
              </Link>
            ))}
          </div>
        </section>

        <section className="dashboard-panel notice-panel" aria-labelledby="important-notices-title">
          <div className="dashboard-panel-header">
            <div className="panel-title-row">
              <Bell size={20} aria-hidden="true" />
              <h2 id="important-notices-title">중요 공지</h2>
            </div>
            <Link className="panel-link" to="/notices">
              공지 센터
              <ChevronRight size={16} aria-hidden="true" />
            </Link>
          </div>
          <div className="dashboard-list">
            {home.requiredNotices.map((notice) => (
              <DashboardContentRow key={notice.id} item={notice} important />
            ))}
          </div>
        </section>

        <section className="dashboard-panel recent-panel" aria-labelledby="latest-content-title">
          <div className="dashboard-panel-header">
            <div className="panel-title-row">
              <Clock3 size={20} aria-hidden="true" />
              <h2 id="latest-content-title">최근 콘텐츠</h2>
            </div>
            <span className="badge accent">
              <Sparkles size={14} aria-hidden="true" />
              New
            </span>
          </div>
          <div className="dashboard-list">
            {home.latestUpdates.map((item) => (
              <DashboardContentRow key={item.id} item={item} />
            ))}
          </div>
        </section>

        <section className="dashboard-panel bookmark-panel" aria-labelledby="bookmarked-content-title">
          <div className="dashboard-panel-header">
            <div className="panel-title-row">
              <BookMarked size={20} aria-hidden="true" />
              <h2 id="bookmarked-content-title">북마크</h2>
            </div>
            <Link className="panel-link" to="/bookmarks">
              전체 보기
              <ChevronRight size={16} aria-hidden="true" />
            </Link>
          </div>
          {home.bookmarkedContent.length ? (
            <div className="dashboard-list">
              {home.bookmarkedContent.map((item) => (
                <DashboardContentRow key={item.id} item={item} />
              ))}
            </div>
          ) : (
            <div className="dashboard-empty">저장된 콘텐츠가 없습니다.</div>
          )}
        </section>
      </div>
    </div>
  );
}
