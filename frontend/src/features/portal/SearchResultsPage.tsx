import { Search } from "lucide-react";
import { FormEvent, useEffect, useMemo, useState } from "react";
import { useSearchParams } from "react-router-dom";
import { ContentCard } from "../../components/cms/ContentCard";
import { EmptyState } from "../../components/ui/StateViews";
import type { ContentType } from "../../data/mockCms";
import { getSearchResults } from "./portalApi";

type SearchTypeFilter = "ALL" | ContentType;

const contentTypeValues: ContentType[] = ["ARTICLE", "DOCUMENT", "NOTICE"];
const contentTypeOptions: Array<{ value: SearchTypeFilter; label: string }> = [
  { value: "ALL", label: "전체" },
  { value: "ARTICLE", label: "게시글" },
  { value: "DOCUMENT", label: "문서" },
  { value: "NOTICE", label: "공지" }
];

function parseContentType(params: URLSearchParams): SearchTypeFilter {
  const requestedType = params.get("contentType");
  return contentTypeValues.includes(requestedType as ContentType) ? (requestedType as ContentType) : "ALL";
}

export function SearchResultsPage() {
  const [params, setParams] = useSearchParams();
  const submittedQuery = params.get("q") ?? "";
  const selectedType = parseContentType(params);
  const [query, setQuery] = useState(submittedQuery);

  useEffect(() => {
    setQuery(submittedQuery);
  }, [submittedQuery]);

  const allResults = useMemo(() => getSearchResults(submittedQuery), [submittedQuery]);
  const results = useMemo(
    () => (selectedType === "ALL" ? allResults : allResults.filter((item) => item.contentType === selectedType)),
    [allResults, selectedType]
  );
  const resultCounts = useMemo(
    () =>
      contentTypeOptions.reduce<Record<SearchTypeFilter, number>>(
        (counts, option) => {
          counts[option.value] = option.value === "ALL" ? allResults.length : allResults.filter((item) => item.contentType === option.value).length;
          return counts;
        },
        { ALL: 0, ARTICLE: 0, DOCUMENT: 0, NOTICE: 0 }
      ),
    [allResults]
  );
  const selectedTypeLabel = contentTypeOptions.find((option) => option.value === selectedType)?.label ?? "전체";

  function applySearchParams(nextQuery: string, nextType: SearchTypeFilter) {
    const nextParams = new URLSearchParams(params);
    const trimmedQuery = nextQuery.trim();

    if (trimmedQuery) {
      nextParams.set("q", trimmedQuery);
    } else {
      nextParams.delete("q");
    }

    if (nextType === "ALL") {
      nextParams.delete("contentType");
    } else {
      nextParams.set("contentType", nextType);
    }

    setParams(nextParams);
  }

  function submit(event: FormEvent) {
    event.preventDefault();
    applySearchParams(query, selectedType);
  }

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">검색 결과</h1>
          <p className="page-subtitle">제목, 요약, 본문, 태그를 함께 검색하고 유형별 카드로 확인합니다.</p>
        </div>
      </section>
      <form className="search-form" onSubmit={submit}>
        <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="검색어 입력" aria-label="Search query" />
        <button className="btn primary" type="submit">
          <Search size={18} aria-hidden="true" />
          검색
        </button>
      </form>
      <div className="toolbar section search-results-toolbar">
        <div className="type-filter-tabs" role="group" aria-label="콘텐츠 유형 필터">
          {contentTypeOptions.map((option) => (
            <button
              key={option.value}
              className={`type-filter-tab ${selectedType === option.value ? "is-active" : ""}`}
              type="button"
              aria-pressed={selectedType === option.value}
              onClick={() => applySearchParams(submittedQuery, option.value)}
            >
              <span>{option.label}</span>
              <span className="filter-count">{resultCounts[option.value].toLocaleString("ko-KR")}</span>
            </button>
          ))}
        </div>
        <span className="muted" aria-live="polite">
          {selectedTypeLabel} {results.length.toLocaleString("ko-KR")}건
        </span>
      </div>
      {results.length ? (
        <div className="grid three search-results-grid" aria-label="검색 결과 카드">
          {results.map((item) => (
            <ContentCard key={item.id} item={item} />
          ))}
        </div>
      ) : (
        <EmptyState title="검색 결과 없음" description="필터를 줄이거나 다른 키워드로 검색해 보세요." actionLabel="인기 콘텐츠" actionHref="/portal" />
      )}
    </>
  );
}
