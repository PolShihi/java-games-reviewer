import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { Review, ReviewCreateRequest } from '../models/review';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private readonly http = inject(HttpClient);
  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<Review>>('/reviews', {
      params: buildHttpParams(params),
    });
  }

  getByGameId(gameId: number | string, params?: Pick<PageQueryParams, 'page' | 'size'>) {
    return this.http.get<PageResponse<Review>>(`/reviews/game/${gameId}`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<Review>(`/reviews/${id}`);
  }

  create(payload: ReviewCreateRequest) {
    return this.http.post<Review>('/reviews', payload);
  }

  update(id: number | string, payload: ReviewCreateRequest) {
    return this.http.put<Review>(`/reviews/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`/reviews/${id}`);
  }
}
