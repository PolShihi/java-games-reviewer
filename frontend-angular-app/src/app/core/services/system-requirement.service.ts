import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { SystemRequirement, SystemRequirementCreateRequest } from '../models/system-requirement';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class SystemRequirementService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<SystemRequirement>>(`${this.baseUrl}/system-requirements`, {
      params: buildHttpParams(params),
    });
  }

  getByGameId(gameId: number | string, params?: Pick<PageQueryParams, 'page' | 'size'>) {
    return this.http.get<PageResponse<SystemRequirement>>(`${this.baseUrl}/system-requirements/game/${gameId}`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<SystemRequirement>(`${this.baseUrl}/system-requirements/${id}`);
  }

  create(payload: SystemRequirementCreateRequest) {
    return this.http.post<SystemRequirement>(`${this.baseUrl}/system-requirements`, payload);
  }

  update(id: number | string, payload: SystemRequirementCreateRequest) {
    return this.http.put<SystemRequirement>(`${this.baseUrl}/system-requirements/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/system-requirements/${id}`);
  }
}
