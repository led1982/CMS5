import { Pin } from 'lucide-react';
import { Link } from 'react-router-dom';
import { ContentSummary } from '../../../shared/api/openapiClient';
import { Badge } from '../../../shared/components/Badge';

export function PinnedAnnouncementList({ announcements }: { announcements: ContentSummary[] }) {
  if (announcements.length === 0) {
    return <p className="muted">현재 표시할 중요 공지가 없습니다.</p>;
  }

  return (
    <div className="cards">
      {announcements.map((announcement) => (
        <Link className="content-card" to={`/portal/contents/${announcement.id}`} key={announcement.id}>
          <Badge tone={announcement.acknowledged ? 'green' : 'amber'}>
            {announcement.acknowledged ? '확인 완료' : '확인 필요'}
          </Badge>
          <h3>
            <Pin size={15} /> {announcement.title}
          </h3>
          <p className="muted">{announcement.summary}</p>
        </Link>
      ))}
    </div>
  );
}
