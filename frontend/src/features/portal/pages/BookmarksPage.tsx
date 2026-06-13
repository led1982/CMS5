import { Link } from 'react-router-dom';
import { Button } from '../../../shared/components/Button';

export function BookmarksPage() {
  return (
    <section className="state-panel">
      <h1>내 북마크</h1>
      <p className="muted">북마크 API는 저장과 삭제를 지원하며, 목록 화면은 후속 확장에서 개인화 정렬과 함께 확장됩니다.</p>
      <Button as={Link} to="/portal/search" variant="primary">
        콘텐츠 검색
      </Button>
    </section>
  );
}
