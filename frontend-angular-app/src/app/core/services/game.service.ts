import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { GameCreateRequest, GameDetail, GameListItem, GameUpdateRequest } from '../models/game';
import { PageResponse } from '../models/page-response';
import { GameFilterParams, PageQueryParams } from '../models/query-params';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<GameListItem>>(`${this.baseUrl}/games`, {
      params: buildHttpParams(params),
    });
  }

  filter(params: GameFilterParams) {
    return this.http.get<PageResponse<GameListItem>>(`${this.baseUrl}/games/filter`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<GameDetail>(`${this.baseUrl}/games/${id}`);
  }

  create(payload: GameCreateRequest) {
    return this.http.post<GameDetail>(`${this.baseUrl}/games`, payload);
  }

  update(id: number | string, payload: GameUpdateRequest) {
    return this.http.put<GameDetail>(`${this.baseUrl}/games/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/games/${id}`);
  }
}
