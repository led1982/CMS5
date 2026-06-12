const SESSION_KEY = "cms.session.email";

export const demoUsers = [
  { email: "employee@example.com", label: "Employee" },
  { email: "outsider@example.com", label: "Outside Employee" },
  { email: "editor@example.com", label: "Content Manager" },
  { email: "reviewer@example.com", label: "Reviewer" },
  { email: "admin@example.com", label: "Administrator" }
];

export function getSessionEmail(): string {
  return localStorage.getItem(SESSION_KEY) ?? "employee@example.com";
}

export function setSessionEmail(email: string): void {
  localStorage.setItem(SESSION_KEY, email);
  window.dispatchEvent(new CustomEvent("cms-session-changed", { detail: email }));
}

export function can(permissions: string[] | undefined, permission: string): boolean {
  return Boolean(permissions?.includes(permission) || permissions?.includes("ADMIN_ACCESS"));
}
