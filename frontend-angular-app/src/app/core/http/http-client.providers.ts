import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { apiErrorInterceptor } from './api-error.interceptor';

export const HTTP_CLIENT_PROVIDERS = [
  provideHttpClient(withInterceptors([apiErrorInterceptor])),
];
