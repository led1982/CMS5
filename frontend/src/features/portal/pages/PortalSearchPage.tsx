import { Search } from 'lucide-react';
import { FormEvent, useEffect, useMemo, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { ContentSummary, ContentType } from '../../../shared/api/openapiClient';
import { Badge } from '../../../shared/components/Badge';
import { Button } from '../../../shared/components/Button';
import { Field, TextInput } from '../../../shared/components/Form';
import { searchPortalContents } from '../api/portalApi';

const contentTypes: Array<ContentType | ''> = ['', 'KNOWLEDGE', 'DOCUMENT', 'ANNOUNCEMENT'];

export function PortalSearchPage() {
  const [params, setParams] = useSearchParams();
  const [query, setQuery] = useState(params.get('q') ?? '');
  const [type, setType] = useState<ContentType | ''>((params.get('type') as ContentType | null) ?? '');
  const [items, setItems] = useState<ContentSummary[]>([]);
  const [error, setError] = useState('');

  const searchParams = useMemo(() => ({ q: params.get('q') ?? '', type: (params.get('type') as ContentType | null) ?? undefined }), [params]);

  useEffect(() => {
    searchPortalContents(searchParams)
      .then((result) => {
        setItems(result.items);
        setError('');
      })
      .catch((err: Error) => {
        setError(err.message);
        setItems([]);
      });
  }, [searchParams]);

  function submit(event: FormEvent) {
    event.preventDefault();
    const next = new URLSearchParams();
    if (query) next.set('q', query);
    if (type) next.set('type', type);
    setParams(next);
  }

  return (
    <div className="page">
      <section className="section">
        <form className="toolbar" onSubmit={submit}>
          <Field label="검색어">
            <TextInput value={query} onChange={(event) => setQuery(event.target.value)} placeholder="제목, 본문, 태그 검색" />
          </Field>
          <Field label="유형">
            <select className="select" value={type} onChange={(event) => setType(event.target.value as ContentType | '')}>
              {contentTypes.map((value) => (
                <option key={value || 'ALL'} value={value}>
                  {value || '전체'}
                </option>
              ))}
            </select>
          </Field>
          <Button icon={<Search size={16} />} variant="primary" type="submit">
            검색
          </Button>
        </form>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>검색 결과</h2>
          <span className="muted">{items.length}건</span>
        </div>
        {error ? <p className="toast toast-error">{error}</p> : null}
        <div className="cards">
          {items.map((item) => (
            <Link className="content-card" to={`/portal/contents/${item.id}`} key={item.id}>
              <Badge tone={item.type === 'ANNOUNCEMENT' ? 'amber' : 'blue'}>{item.type}</Badge>
              <h3>{item.title}</h3>
              <p>{item.summary}</p>
              <span className="muted">{item.category?.name} · {item.tags.map((tag) => tag.name).join(', ')}</span>
            </Link>
          ))}
          {items.length === 0 && !error ? (
            <div className="state-panel">
              <h1>검색 결과가 없습니다</h1>
              <p className="muted">검색어를 줄이거나 유형 필터를 초기화하세요.</p>
            </div>
          ) : null}
        </div>
      </section>
    </div>
  );
}
