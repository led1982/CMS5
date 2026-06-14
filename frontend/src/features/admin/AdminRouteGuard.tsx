import type { ReactNode } from "react";
import { Navigate } from "react-router-dom";
import { useCurrentUser } from "../../app/providers/AppProviders";
import type { RoleCode } from "../../data/mockCms";

const defaultAdminRoles: RoleCode[] = ["ADMIN", "EDITOR", "REVIEWER"];

export function AdminRouteGuard({ allowedRoles = defaultAdminRoles, children }: { allowedRoles?: RoleCode[]; children: ReactNode }) {
  const { user } = useCurrentUser();
  const allowed = user.roles.some((role) => allowedRoles.includes(role));
  return allowed ? <>{children}</> : <Navigate to="/forbidden" replace />;
}
