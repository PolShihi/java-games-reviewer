import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { MediaOutlet, MediaOutletCreateRequest } from '../models/media-outlet';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class MediaOutletService {
  private readonly http = inject(HttpClient);
  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<MediaOutlet>>(API_ENDPOINTS.mediaOutlets, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<MediaOutlet>(`${API_ENDPOINTS.mediaOutlets}/${id}`);
  }

  create(payload: MediaOutletCreateRequest) {
    return this.http.post<MediaOutlet>(API_ENDPOINTS.mediaOutlets, payload);
  }

  update(id: number | string, payload: MediaOutletCreateRequest) {
    return this.http.put<MediaOutlet>(`${API_ENDPOINTS.mediaOutlets}/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${API_ENDPOINTS.mediaOutlets}/${id}`);
  }
}
