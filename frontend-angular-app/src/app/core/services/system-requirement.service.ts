import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { SystemRequirement, SystemRequirementCreateRequest } from '../models/system-requirement';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class SystemRequirementService {
  private readonly http = inject(HttpClient);
  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<SystemRequirement>>(API_ENDPOINTS.systemRequirements, {
      params: buildHttpParams(params),
    });
  }

  getByGameId(gameId: number | string, params?: Pick<PageQueryParams, 'page' | 'size'>) {
    return this.http.get<PageResponse<SystemRequirement>>(
      API_ENDPOINTS.systemRequirementsByGame(gameId),
      {
        params: buildHttpParams(params),
      }
    );
  }

  getById(id: number | string) {
    return this.http.get<SystemRequirement>(`${API_ENDPOINTS.systemRequirements}/${id}`);
  }

  create(payload: SystemRequirementCreateRequest) {
    return this.http.post<SystemRequirement>(API_ENDPOINTS.systemRequirements, payload);
  }

  update(id: number | string, payload: SystemRequirementCreateRequest) {
    return this.http.put<SystemRequirement>(`${API_ENDPOINTS.systemRequirements}/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${API_ENDPOINTS.systemRequirements}/${id}`);
  }
}
