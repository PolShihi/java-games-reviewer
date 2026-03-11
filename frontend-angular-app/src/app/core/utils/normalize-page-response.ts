import { PageResponse } from '../models/page-response';

const DEFAULT_PAGE: PageResponse<unknown> = {
  content: [],
  pageNumber: 0,
  pageSize: 10,
  totalElements: 0,
  totalPages: 0,
  first: true,
  last: true,
};

export const normalizePageResponse = <T>(
  payload: PageResponse<T> | T[] | null | undefined
): PageResponse<T> => {
  if (Array.isArray(payload)) {
    return {
      ...DEFAULT_PAGE,
      content: payload,
      pageSize: payload.length,
      totalElements: payload.length,
      totalPages: payload.length > 0 ? 1 : 0,
    } as PageResponse<T>;
  }

  if (payload && Array.isArray(payload.content)) {
    return {
      ...DEFAULT_PAGE,
      ...payload,
      content: payload.content,
    } as PageResponse<T>;
  }

  return { ...DEFAULT_PAGE } as PageResponse<T>;
};
