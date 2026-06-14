import { AlertTriangle, FileSearch, Loader2, Lock, SearchX } from "lucide-react";
import { Link } from "react-router-dom";

type StateViewProps = {
  title: string;
  description: string;
  actionLabel?: string;
  actionHref?: string;
};

export function LoadingState({ title = "로딩 중" }: { title?: string }) {
  return (
    <div className="state-view" role="status" aria-live="polite">
      <Loader2 size={36} aria-hidden="true" />
      <h1>{title}</h1>
    </div>
  );
}

export function EmptyState({ title, description, actionLabel, actionHref }: StateViewProps) {
  return (
    <div className="state-view">
      <FileSearch size={42} aria-hidden="true" />
      <h1>{title}</h1>
      <p className="muted">{description}</p>
      {actionLabel && actionHref ? (
        <Link className="btn primary" to={actionHref}>
          {actionLabel}
        </Link>
      ) : null}
    </div>
  );
}

export function ErrorState({ title, description, actionLabel, actionHref }: StateViewProps) {
  return (
    <div className="state-view" role="alert">
      <AlertTriangle size={42} aria-hidden="true" />
      <h1>{title}</h1>
      <p className="muted">{description}</p>
      {actionLabel && actionHref ? (
        <Link className="btn secondary" to={actionHref}>
          {actionLabel}
        </Link>
      ) : null}
    </div>
  );
}

export function ForbiddenState() {
  return (
    <div className="state-view" role="alert">
      <Lock size={42} aria-hidden="true" />
      <h1>권한이 없습니다</h1>
      <p className="muted">요청한 화면에 접근할 수 있는 역할이 현재 계정에 없습니다.</p>
      <Link className="btn primary" to="/">
        포털 홈
      </Link>
    </div>
  );
}

export function NotFoundState() {
  return (
    <div className="state-view" role="alert">
      <SearchX size={42} aria-hidden="true" />
      <h1>찾을 수 없습니다</h1>
      <p className="muted">콘텐츠가 삭제되었거나 접근 가능한 경로가 아닙니다.</p>
      <Link className="btn primary" to="/">
        포털 홈
      </Link>
    </div>
  );
}
