import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { Review, ReviewCreateRequest } from '../models/review';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<Review>>(`${this.baseUrl}/reviews`, {
      params: buildHttpParams(params),
    });
  }

  getByGameId(gameId: number | string, params?: Pick<PageQueryParams, 'page' | 'size'>) {
    return this.http.get<PageResponse<Review>>(`${this.baseUrl}/reviews/game/${gameId}`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<Review>(`${this.baseUrl}/reviews/${id}`);
  }

  create(payload: ReviewCreateRequest) {
    return this.http.post<Review>(`${this.baseUrl}/reviews`, payload);
  }

  update(id: number | string, payload: ReviewCreateRequest) {
    return this.http.put<Review>(`${this.baseUrl}/reviews/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/reviews/${id}`);
  }
}
