import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { GameCreateRequest, GameDetail, GameListItem, GameUpdateRequest } from '../models/game';
import { PageResponse } from '../models/page-response';
import { GameFilterParams, PageQueryParams } from '../models/query-params';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class GameService {
  private readonly http = inject(HttpClient);
  
  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<GameListItem>>('/games', {
      params: buildHttpParams(params),
    });
  }

  filter(params: GameFilterParams) {
    return this.http.get<PageResponse<GameListItem>>('/games/filter', {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<GameDetail>(`/games/${id}`);
  }

  create(payload: GameCreateRequest) {
    return this.http.post<GameDetail>('/games', payload);
  }

  update(id: number | string, payload: GameUpdateRequest) {
    return this.http.put<GameDetail>(`/games/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`/games/${id}`);
  }
}
