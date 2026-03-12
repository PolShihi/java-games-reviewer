import { HttpErrorResponse } from '@angular/common/http';

export class ApiError extends Error {
  status: number | null;
  errors: Record<string, string> | null;
  timestamp: string | null;
  method: string | null;
  url: string | null;
  originalError: unknown;

  constructor({
    message = 'Unexpected API error',
    status = null,
    errors = null,
    timestamp = null,
    method = null,
    url = null,
    originalError = null,
  }: {
    message?: string;
    status?: number | null;
    errors?: Record<string, string> | null;
    timestamp?: string | null;
    method?: string | null;
    url?: string | null;
    originalError?: unknown;
  } = {}) {
    super(message);
    this.name = 'ApiError';
    this.status = status;
    this.errors = errors;
    this.timestamp = timestamp;
    this.method = method;
    this.url = url;
    this.originalError = originalError;
  }
}

export const toApiError = (
  error: HttpErrorResponse,
  request?: { method?: string | null; url?: string | null }
): ApiError => {
  const payload = (error.error ?? {}) as {
    message?: string;
    errors?: Record<string, string>;
    timestamp?: string;
  };

  return new ApiError({
    message: payload.message || error.message || 'Unexpected API error',
    status: error.status ?? null,
    errors: payload.errors || null,
    timestamp: payload.timestamp || null,
    method: request?.method ?? null,
    url: request?.url ?? null,
    originalError: error,
  });
};
