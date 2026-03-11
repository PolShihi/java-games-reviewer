import { HttpInterceptorFn } from '@angular/common/http';
import { appConfig } from '../config/app-config';

const isAbsoluteUrl = (url: string) => /^https?:\/\//i.test(url);

export const baseUrlInterceptor: HttpInterceptorFn = (req, next) => {
  if (isAbsoluteUrl(req.url)) {
    return next(req);
  }

  const normalizedBase = appConfig.api.baseUrl.replace(/\/+$/, '');
  const normalizedPath = req.url.startsWith('/') ? req.url : `/${req.url}`;

  return next(req.clone({ url: `${normalizedBase}${normalizedPath}` }));
};
