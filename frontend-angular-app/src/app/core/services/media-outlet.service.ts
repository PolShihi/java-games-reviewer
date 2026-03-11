import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { MediaOutlet, MediaOutletCreateRequest } from '../models/media-outlet';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class MediaOutletService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<MediaOutlet>>(`${this.baseUrl}/media-outlets`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<MediaOutlet>(`${this.baseUrl}/media-outlets/${id}`);
  }

  create(payload: MediaOutletCreateRequest) {
    return this.http.post<MediaOutlet>(`${this.baseUrl}/media-outlets`, payload);
  }

  update(id: number | string, payload: MediaOutletCreateRequest) {
    return this.http.put<MediaOutlet>(`${this.baseUrl}/media-outlets/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/media-outlets/${id}`);
  }
}
