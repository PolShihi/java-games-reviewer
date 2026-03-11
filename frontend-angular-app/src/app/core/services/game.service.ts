import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { GameCreateRequest, GameDetail, GameListItem, GameUpdateRequest } from '../models/game';
import { PageResponse } from '../models/page-response';
import { GameFilterParams, PageQueryParams } from '../models/query-params';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly http = inject(HttpClient);

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<GameListItem>>(API_ENDPOINTS.games, {
      params: buildHttpParams(params),
    });
  }

  filter(params: GameFilterParams) {
    return this.http.get<PageResponse<GameListItem>>(API_ENDPOINTS.gamesFilter, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<GameDetail>(`${API_ENDPOINTS.games}/${id}`);
  }

  create(payload: GameCreateRequest) {
    return this.http.post<GameDetail>(API_ENDPOINTS.games, payload);
  }

  update(id: number | string, payload: GameUpdateRequest) {
    return this.http.put<GameDetail>(`${API_ENDPOINTS.games}/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${API_ENDPOINTS.games}/${id}`);
  }
}
