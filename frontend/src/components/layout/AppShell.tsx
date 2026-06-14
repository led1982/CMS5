import { BarChart3, Bell, BookMarked, FileText, Home, LayoutDashboard, Library, ShieldCheck, Tags, Users } from "lucide-react";
import { NavLink, Outlet, useLocation } from "react-router-dom";
import { useCurrentUser } from "../../app/providers/AppProviders";
import type { RoleCode } from "../../data/mockCms";

const portalLinks = [
  { to: "/", label: "Portal", icon: Home },
  { to: "/search", label: "Search", icon: Library },
  { to: "/notices", label: "Notices", icon: Bell },
  { to: "/bookmarks", label: "Bookmarks", icon: BookMarked }
];

const adminLinks = [
  { to: "/admin", label: "Dashboard", icon: LayoutDashboard },
  { to: "/admin/content", label: "Content", icon: FileText },
  { to: "/admin/review", label: "Review Queue", icon: ShieldCheck },
  { to: "/admin/taxonomy", label: "Taxonomy", icon: Tags },
  { to: "/admin/users", label: "Users & Roles", icon: Users },
  { to: "/admin/analytics", label: "Analytics", icon: BarChart3 },
  { to: "/admin/audit", label: "Audit Logs", icon: Library }
];

const previewRoles: RoleCode[] = ["EMPLOYEE", "VIEWER", "EDITOR", "REVIEWER", "ADMIN"];

export function AppShell() {
  const { user, setPreviewRole } = useCurrentUser();
  const location = useLocation();
  const isAdmin = location.pathname.startsWith("/admin");
  const canSeeAdmin = user.roles.some((role) => ["ADMIN", "EDITOR", "REVIEWER"].includes(role));

  return (
    <div className="app-shell">
      <header className="topbar">
        <NavLink to="/" className="brand">
          <span className="brand-mark">C</span>
          <span>Company CMS</span>
        </NavLink>
        <nav className="nav-links" aria-label="Portal navigation">
          {portalLinks.map((link) => {
            const Icon = link.icon;
            return (
              <NavLink key={link.to} to={link.to} className="nav-link">
                <Icon size={16} aria-hidden="true" />
                {link.label}
              </NavLink>
            );
          })}
          {canSeeAdmin ? (
            <NavLink to="/admin" className="nav-link">
              <LayoutDashboard size={16} aria-hidden="true" />
              Admin
            </NavLink>
          ) : null}
        </nav>
        <div className="profile-menu">
          <span className="muted">{user.displayName}</span>
          <select
            className="role-select"
            aria-label="Preview role"
            value={user.roles[0]}
            onChange={(event) => setPreviewRole(event.target.value as RoleCode)}
          >
            {previewRoles.map((role) => (
              <option key={role} value={role}>
                {role}
              </option>
            ))}
          </select>
        </div>
      </header>
      <div className={`layout ${isAdmin ? "with-sidebar" : ""}`}>
        {isAdmin ? (
          <aside className="sidebar" aria-label="Admin navigation">
            <p className="sidebar-title">Admin Console</p>
            {adminLinks.map((link) => {
              const Icon = link.icon;
              return (
                <NavLink key={link.to} to={link.to} end={link.to === "/admin"}>
                  <Icon size={16} aria-hidden="true" />
                  {link.label}
                </NavLink>
              );
            })}
          </aside>
        ) : null}
        <main className={`main-content ${isAdmin ? "admin-content" : ""}`}>
          <Outlet />
        </main>
      </div>
    </div>
  );
}
