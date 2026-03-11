import { Observable, firstValueFrom } from 'rxjs';
import { PageResponse } from '../models/page-response';
import { normalizePageResponse } from './normalize-page-response';

export const fetchAllPageContent = async <T>(
  fetchPage: (params: Record<string, unknown>) => Observable<PageResponse<T> | T[]>,
  baseParams: Record<string, unknown> = {}
): Promise<T[]> => {
  const firstResponse = await firstValueFrom(
    fetchPage({
      ...baseParams,
      page: 0,
      size: 1,
    })
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
    })
  );
  const secondPage = normalizePageResponse(secondResponse);

  return secondPage.content;
};
