import { CheckCircle2 } from "lucide-react";
import { useState } from "react";

export function NoticeAckBanner({ noticeId, title }: { noticeId: string; title: string }) {
  const [acknowledgedAt, setAcknowledgedAt] = useState<string | null>(window.localStorage.getItem(`notice.ack.${noticeId}`));

  function acknowledge() {
    const value = new Date().toISOString();
    window.localStorage.setItem(`notice.ack.${noticeId}`, value);
    setAcknowledgedAt(value);
  }

  return (
    <section className="panel section" aria-live="polite">
      <div className="section-header">
        <div>
          <span className={acknowledgedAt ? "badge success" : "badge primary"}>{acknowledgedAt ? "ACKNOWLEDGED" : "PENDING"}</span>
          <h2>{title}</h2>
          {acknowledgedAt ? <p className="muted">확인 시각 {new Date(acknowledgedAt).toLocaleString("ko-KR")}</p> : <p className="muted">대상 공지 확인 기록이 필요합니다.</p>}
        </div>
        <button className="btn primary" type="button" disabled={Boolean(acknowledgedAt)} onClick={acknowledge}>
          <CheckCircle2 size={18} aria-hidden="true" />
          확인
        </button>
      </div>
    </section>
  );
}
