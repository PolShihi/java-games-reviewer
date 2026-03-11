import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { ProductionCompany, ProductionCompanyCreateRequest } from '../models/production-company';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class ProductionCompanyService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<ProductionCompany>>(`${this.baseUrl}/production-companies`, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<ProductionCompany>(`${this.baseUrl}/production-companies/${id}`);
  }

  create(payload: ProductionCompanyCreateRequest) {
    return this.http.post<ProductionCompany>(`${this.baseUrl}/production-companies`, payload);
  }

  update(id: number | string, payload: ProductionCompanyCreateRequest) {
    return this.http.put<ProductionCompany>(`${this.baseUrl}/production-companies/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/production-companies/${id}`);
  }
}
