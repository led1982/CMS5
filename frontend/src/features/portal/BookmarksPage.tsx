import { ContentCard } from "../../components/cms/ContentCard";
import { EmptyState } from "../../components/ui/StateViews";
import { bookmarkedContent } from "../../data/mockCms";

export function BookmarksPage() {
  const saved = bookmarkedContent();

  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">즐겨찾기</h1>
          <p className="page-subtitle">자주 보는 콘텐츠를 빠르게 다시 엽니다.</p>
        </div>
      </section>
      {saved.length ? (
        <div className="grid three">
          {saved.map((item) => (
            <ContentCard key={item.id} item={item} />
          ))}
        </div>
      ) : (
        <EmptyState title="즐겨찾기 없음" description="최근 업데이트와 추천 카테고리에서 콘텐츠를 저장할 수 있습니다." actionLabel="포털 홈" actionHref="/portal" />
      )}
    </>
  );
}
