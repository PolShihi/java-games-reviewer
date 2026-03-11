import { Observable, firstValueFrom } from 'rxjs';
import { PageResponse } from '../models/page-response';
import { normalizePageResponse } from './normalize-page-response';

export const fetchAllPageContent = async <T, P extends Record<string, unknown>>(
  fetchPage: (params: P) => Observable<PageResponse<T> | T[]>,
  baseParams: P
): Promise<T[]> => {
  const firstResponse = await firstValueFrom(
    fetchPage({
      ...baseParams,
      page: 0,
      size: 1,
    } as P)
  );
  const firstPage = normalizePageResponse(firstResponse);

  if (firstPage.totalElements <= firstPage.pageSize) {
    return firstPage.content;
  }

  const secondResponse = await firstValueFrom(
    fetchPage({
      ...baseParams,
      page: 0,
      size: firstPage.totalElements,
    } as P)
  );
  const secondPage = normalizePageResponse(secondResponse);

  return secondPage.content;
};
