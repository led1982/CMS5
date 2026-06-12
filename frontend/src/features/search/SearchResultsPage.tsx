import { BookmarkPlus, Search } from "lucide-react";
import { FormEvent, useEffect, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { ContentType, SearchResults, api, pageUrl } from "../../api/client";
import { addBookmark } from "../portal/bookmarkApi";

export function SearchResultsPage() {
  const [params, setParams] = useSearchParams();
  const [query, setQuery] = useState(params.get("q") ?? "");
  const [type, setType] = useState<ContentType | "">((params.get("type") as ContentType) ?? "");
  const [results, setResults] = useState<SearchResults | null>(null);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    const q = params.get("q");
    if (!q) {
      setResults(null);
      return;
    }
    api<SearchResults>(pageUrl("/search", { q, type: params.get("type"), size: 50 }))
      .then((result) => {
        setResults(result);
        setError(null);
      })
      .catch((err: Error) => setError(err.message));
  }, [params]);

  function submit(event: FormEvent) {
    event.preventDefault();
    setParams({ q: query, ...(type ? { type } : {}) });
  }

  return (
    <div className="grid">
      <h1 className="page-title">Search Results</h1>
      <form className="panel filter-row" onSubmit={submit}>
        <label>
          <span className="sr-only">Keyword</span>
          <input value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search title and body" />
        </label>
        <label>
          <span className="sr-only">Content type</span>
          <select value={type} onChange={(event) => setType(event.target.value as ContentType | "")}>
            <option value="">All types</option>
            <option value="KNOWLEDGE">Knowledge</option>
            <option value="DOCUMENT">Document</option>
            <option value="NOTICE">Notice</option>
          </select>
        </label>
        <button type="submit"><Search size={16} /> Search</button>
      </form>
      {error && <div className="panel error-text" role="alert">{error}</div>}
      <section className="panel">
        <div className="section-header">
          <h2>{results ? `${results.totalItems} results for "${results.query}"` : "Enter a keyword"}</h2>
        </div>
        <div className="list">
          {results?.items.length === 0 && <p className="meta">No visible published content matched your search.</p>}
          {results?.items.map((item) => (
            <article className="card" key={item.id}>
              <div className="section-header">
                <h3><Link to={`/portal/content/${item.id}`}>{item.title}</Link></h3>
                <button type="button" className="secondary" onClick={() => addBookmark(item.id)}><BookmarkPlus size={16} /> Bookmark</button>
              </div>
              <p>{item.summary}</p>
              <div className="meta">
                <span>{item.type}</span>
                <span>{item.category.name}</span>
                <span>{item.tags.join(", ")}</span>
                <span>{new Date(item.updatedAt).toLocaleDateString()}</span>
              </div>
            </article>
          ))}
        </div>
      </section>
    </div>
  );
}
