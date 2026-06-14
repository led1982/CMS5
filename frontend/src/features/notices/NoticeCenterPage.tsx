import { ContentCard } from "../../components/cms/ContentCard";
import { requiredNotices } from "../../data/mockCms";

export function NoticeCenterPage() {
  return (
    <>
      <section className="section-header">
        <div>
          <h1 className="page-title">공지 센터</h1>
          <p className="page-subtitle">중요 공지와 확인 상태를 함께 봅니다.</p>
        </div>
      </section>
      <div className="grid two">
        {requiredNotices.map((notice) => (
          <ContentCard key={notice.id} item={notice} />
        ))}
      </div>
    </>
  );
}
