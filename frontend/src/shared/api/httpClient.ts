import { API_BASE_URL, ApiErrorResponse } from './openapiClient';

export class ApiClientError extends Error {
  status: number;
  code: string;
  fieldErrors: ApiErrorResponse['fieldErrors'];

  constructor(error: ApiErrorResponse) {
    super(error.message);
    this.status = error.status;
    this.code = error.code;
    this.fieldErrors = error.fieldErrors;
  }
}

export function selectedToken(): string {
  return localStorage.getItem('cms5-demo-token') ?? 'employee';
}

export function setSelectedToken(token: string): void {
  localStorage.setItem('cms5-demo-token', token);
}

export async function http<T>(path: string, init: RequestInit = {}): Promise<T> {
  const headers = new Headers(init.headers);
  if (!(init.body instanceof FormData)) {
    headers.set('Content-Type', 'application/json');
  }
  headers.set('Authorization', `Bearer ${selectedToken()}`);
  const response = await fetch(`${API_BASE_URL}${path}`, { ...init, headers });
  if (response.status === 204) {
    return undefined as T;
  }
  const payload = await response.json();
  if (!response.ok) {
    throw new ApiClientError(payload as ApiErrorResponse);
  }
  return payload as T;
}
