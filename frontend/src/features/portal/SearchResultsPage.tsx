import { Search } from "lucide-react";
import { FormEvent, useMemo, useState } from "react";
import { useNavigate, useSearchParams } from "react-router-dom";
import { ContentCard } from "../../components/cms/ContentCard";
import { EmptyState } from "../../components/ui/StateViews";
import { categories, type ContentType } from "../../data/mockCms";
import { getSearchResults } from "./portalApi";

const contentTypes: Array<"ALL" | ContentType> = ["ALL", "ARTICLE", "DOCUMENT", "NOTICE"];

export function SearchResultsPage() {
  const [params] = useSearchParams();
  const navigate = useNavigate();
  const [query, setQuery] = useState(params.get("q") ?? "");
  const categoryId = params.get("category") ?? undefined;
  const selectedCategory = categories.find((category) => category.id === categoryId);
  const [type, setType] = useState<"ALL" | ContentType>("ALL");
  const results = useMemo(() => getSearchResults(query, type === "ALL" ? undefined : type, categoryId), [query, type, categoryId]);

  function submit(event: FormEvent) {
    event.preventDefault();
    navigate(`/search?q=${encodeURIComponent(query.trim())}`);
  }

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">검색 결과</h1>
          <p className="page-subtitle">제목, 요약, 본문, 태그를 함께 검색합니다.</p>
        </div>
      </section>
      <form className="search-form" onSubmit={submit}>
        <input value={query} onChange={(event) => setQuery(event.target.value)} aria-label="Search query" />
        <button className="btn primary" type="submit">
          <Search size={18} aria-hidden="true" />
          Search
        </button>
      </form>
      <div className="toolbar section">
        <div className="filters">
          {selectedCategory ? <span className="badge secondary">{selectedCategory.name}</span> : null}
          {contentTypes.map((item) => (
            <button key={item} className={`btn ${type === item ? "secondary" : "ghost"}`} type="button" onClick={() => setType(item)}>
              {item}
            </button>
          ))}
        </div>
        <span className="muted" aria-live="polite">
          {results.length} results
        </span>
      </div>
      {results.length ? (
        <div className="grid three">
          {results.map((item) => (
            <ContentCard key={item.id} item={item} />
          ))}
        </div>
      ) : (
        <EmptyState title="검색 결과 없음" description="필터를 줄이거나 다른 키워드로 검색해 보세요." actionLabel="포털 홈" actionHref="/portal" />
      )}
    </>
  );
}
