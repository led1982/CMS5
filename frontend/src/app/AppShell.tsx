import { BookOpen, ClipboardList, Gauge, LayoutDashboard, Search, Shield, Users } from 'lucide-react';
import { NavLink, Outlet } from 'react-router-dom';
import { demoProfiles, hasAnyRole } from '../features/auth/authModel';
import { useAuth } from '../features/auth/AuthContext';

const navItems = [
  { to: '/portal', label: '포털', icon: Search, roles: ['EMPLOYEE', 'EDITOR', 'REVIEWER', 'ADMIN'] as const },
  { to: '/portal/bookmarks', label: '북마크', icon: BookOpen, roles: ['EMPLOYEE', 'EDITOR', 'REVIEWER', 'ADMIN'] as const },
  { to: '/cms/contents', label: 'CMS', icon: ClipboardList, roles: ['EDITOR', 'REVIEWER', 'ADMIN'] as const },
  { to: '/cms/reviews', label: '검토', icon: LayoutDashboard, roles: ['REVIEWER', 'ADMIN'] as const },
  { to: '/admin/metrics', label: '운영 지표', icon: Gauge, roles: ['ADMIN'] as const },
  { to: '/admin/users', label: '권한', icon: Users, roles: ['ADMIN'] as const },
  { to: '/admin/audit-logs', label: '감사', icon: Shield, roles: ['ADMIN'] as const },
];

export function AppShell() {
  const auth = useAuth();
  return (
    <div className="app-shell">
      <aside className="sidebar" aria-label="주요 메뉴">
        <div className="brand">
          <strong>CMS5</strong>
          <span>Knowledge Portal</span>
        </div>
        <nav>
          {navItems
            .filter((item) => hasAnyRole(auth.roles, [...item.roles]))
            .map((item) => {
              const Icon = item.icon;
              return (
                <NavLink key={item.to} to={item.to}>
                  <Icon size={17} />
                  <span>{item.label}</span>
                </NavLink>
              );
            })}
        </nav>
      </aside>
      <main className="main">
        <header className="topbar">
          <div>
            <p className="eyebrow">Internal CMS</p>
            <h1>사내 지식·문서·공지</h1>
          </div>
          <label className="profile-switcher">
            <span>프로필</span>
            <select value={auth.token} onChange={(event) => auth.setToken(event.target.value)}>
              {Object.entries(demoProfiles).map(([token, profile]) => (
                <option key={token} value={token}>
                  {profile.label}
                </option>
              ))}
            </select>
          </label>
        </header>
        <Outlet />
      </main>
    </div>
  );
}
