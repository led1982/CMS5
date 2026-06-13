import { ReactNode } from 'react';
import { Link } from 'react-router-dom';
import { RoleCode } from '../../shared/api/openapiClient';
import { Button } from '../../shared/components/Button';
import { hasAnyRole } from './authModel';
import { useAuth } from './AuthContext';

export function RoleGuard({ roles, children }: { roles: RoleCode[]; children: ReactNode }) {
  const auth = useAuth();
  if (hasAnyRole(auth.roles, roles)) {
    return <>{children}</>;
  }

  return (
    <section className="state-panel" aria-labelledby="forbidden-title">
      <p className="eyebrow">403</p>
      <h1 id="forbidden-title">접근 권한이 없습니다</h1>
      <p>현재 프로필로는 이 관리 화면을 열 수 없습니다.</p>
      <Button as={Link} to="/portal" variant="primary">
        포털로 이동
      </Button>
    </section>
  );
}
