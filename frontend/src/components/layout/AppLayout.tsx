import { BookOpen, FileSearch, Gauge, History, LayoutDashboard, Library, Megaphone, Search, Settings, ShieldCheck, Tags } from "lucide-react";
import { FormEvent, PropsWithChildren, useState } from "react";
import { NavLink, useNavigate } from "react-router-dom";
import { demoUsers, getSessionEmail, setSessionEmail, can } from "../../auth/session";
import { UserSummary } from "../../api/client";

interface Props extends PropsWithChildren {
  user: UserSummary | null;
  sessionError: string | null;
}

export function AppLayout({ user, sessionError, children }: Props) {
  const navigate = useNavigate();
  const [query, setQuery] = useState("");
  const [email, setEmail] = useState(getSessionEmail());
  const permissions = user?.permissions ?? [];

  function submitSearch(event: FormEvent) {
    event.preventDefault();
    if (query.trim()) {
      navigate(`/search?q=${encodeURIComponent(query.trim())}`);
    }
  }

  function switchUser(nextEmail: string) {
    setEmail(nextEmail);
    setSessionEmail(nextEmail);
  }

  return (
    <div className="app-shell">
      <header className="topbar">
        <div className="brand">
          <Library size={22} aria-hidden="true" />
          <span>Internal CMS Portal</span>
        </div>
        <form className="global-search" onSubmit={submitSearch}>
          <Search size={16} aria-hidden="true" />
          <label className="sr-only" htmlFor="global-search">Search knowledge and notices</label>
          <input id="global-search" value={query} onChange={(event) => setQuery(event.target.value)} placeholder="Search knowledge and notices" />
        </form>
        <label className="user-switcher">
          <span className="sr-only">Current user</span>
          <select value={email} onChange={(event) => switchUser(event.target.value)}>
            {demoUsers.map((demoUser) => (
              <option key={demoUser.email} value={demoUser.email}>{demoUser.label}</option>
            ))}
          </select>
        </label>
      </header>

      <nav className="primary-nav" aria-label="Primary">
        <NavLink to="/"><BookOpen size={16} aria-hidden="true" /> Portal</NavLink>
        <NavLink to="/notices"><Megaphone size={16} aria-hidden="true" /> Notices</NavLink>
        <NavLink to="/saved"><FileSearch size={16} aria-hidden="true" /> Saved</NavLink>
        {can(permissions, "CONTENT_WRITE") && <NavLink to="/cms"><LayoutDashboard size={16} aria-hidden="true" /> CMS</NavLink>}
        {can(permissions, "ADMIN_ACCESS") && <NavLink to="/admin/taxonomy"><Tags size={16} aria-hidden="true" /> Taxonomy</NavLink>}
        {can(permissions, "ADMIN_ACCESS") && <NavLink to="/admin/access"><ShieldCheck size={16} aria-hidden="true" /> Access</NavLink>}
        {can(permissions, "AUDIT_READ") && <NavLink to="/admin/audit"><History size={16} aria-hidden="true" /> Audit</NavLink>}
        {can(permissions, "ADMIN_ACCESS") && <NavLink to="/admin/analytics"><Gauge size={16} aria-hidden="true" /> Analytics</NavLink>}
      </nav>

      {sessionError && <div className="banner error" role="alert">{sessionError}</div>}
      <main className="page-frame">
        {children}
      </main>
      <footer className="app-footer">
        <Settings size={14} aria-hidden="true" />
        <span>{user ? `${user.displayName} · ${user.roles.map((role) => role.name).join(", ")}` : "Loading session"}</span>
      </footer>
    </div>
  );
}
