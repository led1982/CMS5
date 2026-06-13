import { Bell, Search, Sparkles } from "lucide-react";
import { FormEvent, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { ContentCard } from "../../components/cms/ContentCard";
import { categories } from "../../data/mockCms";
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
      <section className="hero-search">
        <div className="badge-row">
          <span className="badge primary">확인 필요 공지 {home.requiredNotices.length}</span>
          <span className="badge secondary">통합 검색</span>
        </div>
        <h1>사내 지식과 공지를 한 곳에서 찾습니다</h1>
        <p>발행된 문서, 정책, 운영 런북, 중요 공지를 권한에 맞게 검색하고 열람할 수 있습니다.</p>
        <form className="search-form" onSubmit={submit}>
          <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="무엇을 찾고 있나요?" aria-label="Search content" />
          <button className="btn primary" type="submit">
            <Search size={18} aria-hidden="true" />
            Search
          </button>
        </form>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>확인 필요 공지</h2>
          <Link className="btn secondary" to="/notices">
            <Bell size={16} aria-hidden="true" />
            공지 센터
          </Link>
        </div>
        <div className="grid two">
          {home.requiredNotices.map((notice) => (
            <ContentCard key={notice.id} item={notice} to={`/content/${notice.id}`} />
          ))}
        </div>
      </section>

      <section className="section grid two">
        <div>
          <div className="section-header">
            <h2>최근 업데이트</h2>
          </div>
          <div className="grid">
            {home.latestUpdates.map((item) => (
              <ContentCard key={item.id} item={item} />
            ))}
          </div>
        </div>
        <div>
          <div className="section-header">
            <h2>인기 지식</h2>
            <span className="badge accent">
              <Sparkles size={14} aria-hidden="true" />
              Popular
            </span>
          </div>
          <div className="panel">
            <table>
              <thead>
                <tr>
                  <th>Title</th>
                  <th>Category</th>
                  <th>Views</th>
                </tr>
              </thead>
              <tbody>
                {home.popularContent.map((item) => (
                  <tr key={item.id}>
                    <td>
                      <Link to={`/content/${item.id}`}>{item.title}</Link>
                    </td>
                    <td>{item.category.name}</td>
                    <td>{item.views}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>추천 카테고리</h2>
        </div>
        <div className="grid four">
          {categories.map((category) => (
            <Link key={category.id} className="card" to={`/search?category=${category.id}`}>
              <h3 className="card-title">{category.name}</h3>
              <p className="muted">{category.description}</p>
            </Link>
          ))}
        </div>
      </section>
    </>
  );
}
