import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { createContext, useContext, useMemo, useState, type ReactNode } from "react";
import type { RoleCode } from "../../data/mockCms";

type CurrentUser = {
  id: string;
  email: string;
  displayName: string;
  department: string;
  roles: RoleCode[];
};

type UserContextValue = {
  user: CurrentUser;
  setPreviewRole: (role: RoleCode) => void;
};

const queryClient = new QueryClient();
const UserContext = createContext<UserContextValue | null>(null);

const roleProfiles: Record<RoleCode, CurrentUser> = {
  ADMIN: {
    id: "admin",
    email: "admin@example.com",
    displayName: "관리자",
    department: "Platform",
    roles: ["ADMIN", "EMPLOYEE"]
  },
  EDITOR: {
    id: "editor",
    email: "editor@example.com",
    displayName: "콘텐츠 편집자",
    department: "Engineering",
    roles: ["EDITOR", "EMPLOYEE"]
  },
  REVIEWER: {
    id: "reviewer",
    email: "reviewer@example.com",
    displayName: "검토자",
    department: "Security",
    roles: ["REVIEWER", "EMPLOYEE"]
  },
  EMPLOYEE: {
    id: "employee",
    email: "employee@example.com",
    displayName: "일반 사용자",
    department: "Engineering",
    roles: ["EMPLOYEE"]
  },
  VIEWER: {
    id: "viewer",
    email: "viewer@example.com",
    displayName: "열람 사용자",
    department: "HR",
    roles: ["VIEWER"]
  }
};

export function AppProviders({ children }: { children: ReactNode }) {
  const [previewRole, setPreviewRoleState] = useState<RoleCode>(() => {
    const stored = window.localStorage.getItem("cms.previewRole") as RoleCode | null;
    return stored && roleProfiles[stored] ? stored : "EMPLOYEE";
  });

  const value = useMemo<UserContextValue>(
    () => ({
      user: roleProfiles[previewRole],
      setPreviewRole: (role) => {
        window.localStorage.setItem("cms.previewRole", role);
        setPreviewRoleState(role);
      }
    }),
    [previewRole]
  );

  return (
    <QueryClientProvider client={queryClient}>
      <UserContext.Provider value={value}>{children}</UserContext.Provider>
    </QueryClientProvider>
  );
}

export function useCurrentUser() {
  const value = useContext(UserContext);
  if (!value) {
    throw new Error("useCurrentUser must be used inside AppProviders");
  }
  return value;
}
