import { Search } from 'lucide-react';
import { FormEvent, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ContentSummary } from '../../../shared/api/openapiClient';
import { Button } from '../../../shared/components/Button';
import { Field, TextInput } from '../../../shared/components/Form';
import { PinnedAnnouncementList } from '../components/PinnedAnnouncementList';
import { listPortalAnnouncements, searchPortalContents } from '../api/portalApi';

export function PortalHomePage() {
  const navigate = useNavigate();
  const [query, setQuery] = useState('');
  const [announcements, setAnnouncements] = useState<ContentSummary[]>([]);
  const [recent, setRecent] = useState<ContentSummary[]>([]);

  useEffect(() => {
    listPortalAnnouncements(false).then(setAnnouncements).catch(() => setAnnouncements([]));
    searchPortalContents({ sort: 'LATEST', size: 6 }).then((result) => setRecent(result.items)).catch(() => setRecent([]));
  }, []);

  function handleSearch(event: FormEvent) {
    event.preventDefault();
    navigate(`/portal/search?q=${encodeURIComponent(query)}`);
  }

  return (
    <div className="page">
      <section className="section">
        <form className="toolbar" onSubmit={handleSearch}>
          <Field label="통합 검색">
            <TextInput value={query} onChange={(event) => setQuery(event.target.value)} placeholder="무엇을 찾고 있나요?" />
          </Field>
          <Button icon={<Search size={16} />} variant="primary" type="submit">
            검색
          </Button>
        </form>
      </section>

      <section className="section">
        <div className="section-header">
          <h2>중요 공지</h2>
        </div>
        <PinnedAnnouncementList announcements={announcements.filter((item) => item.pinned || item.requiresAcknowledgement)} />
      </section>

      <section className="section">
        <div className="section-header">
          <h2>최근 게시 콘텐츠</h2>
        </div>
        <div className="cards">
          {recent.map((item) => (
            <a className="content-card" href={`/portal/contents/${item.id}`} key={item.id}>
              <strong>{item.title}</strong>
              <span className="muted">{item.type} · {item.category?.name}</span>
              <p>{item.summary}</p>
            </a>
          ))}
          {recent.length === 0 ? <p className="muted">게시된 콘텐츠가 없습니다.</p> : null}
        </div>
      </section>
    </div>
  );
}
