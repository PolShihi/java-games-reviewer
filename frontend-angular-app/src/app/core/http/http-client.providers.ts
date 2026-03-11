import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { apiErrorInterceptor } from './api-error.interceptor';
import { baseUrlInterceptor } from './base-url.interceptor';

export const HTTP_CLIENT_PROVIDERS = provideHttpClient(
  withInterceptors([baseUrlInterceptor, apiErrorInterceptor])
);
