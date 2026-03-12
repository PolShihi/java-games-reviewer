import { HttpParams } from '@angular/common/http';

type HttpParamValue = string | number | boolean | readonly (string | number | boolean)[] | null | undefined;

export const buildHttpParams = (params?: Record<string, HttpParamValue>): HttpParams | undefined => {
  if (!params) {
    return undefined;
  }

  let httpParams = new HttpParams();
  Object.entries(params).forEach(([key, value]) => {
    if (value === null || value === undefined || value === '') {
      return;
    }

    if (Array.isArray(value)) {
      httpParams = httpParams.set(key, value.join(','));
      return;
    }

    httpParams = httpParams.set(key, String(value));
  });

  return httpParams;
};
