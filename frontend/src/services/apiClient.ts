import type { RoleCode } from "../data/mockCms";

export type ApiError = {
  code: string;
  message: string;
  details?: string[];
};

type RequestOptions = RequestInit & {
  previewRole?: RoleCode;
};

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? "";

export async function apiClient<T>(path: string, options: RequestOptions = {}): Promise<T> {
  const headers = new Headers(options.headers);
  headers.set("Accept", "application/json");
  if (!(options.body instanceof FormData)) {
    headers.set("Content-Type", "application/json");
  }
  const previewRole = options.previewRole ?? (window.localStorage.getItem("cms.previewRole") as RoleCode | null);
  if (previewRole) {
    headers.set("X-CMS-User", previewRole.toLowerCase());
    headers.set("X-CMS-Roles", previewRole);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers
  });

  if (!response.ok) {
    const error = (await response.json().catch(() => ({
      code: "HTTP_ERROR",
      message: `Request failed with ${response.status}`
    }))) as ApiError;
    throw error;
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}
