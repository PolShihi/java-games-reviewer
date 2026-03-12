import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { PageResponse } from '../models/page-response';
import { PageQueryParams } from '../models/query-params';
import { ProductionCompany, ProductionCompanyCreateRequest } from '../models/production-company';
import { buildHttpParams } from '../utils/http-params';

@Injectable({ providedIn: 'root' })
export class ProductionCompanyService {
  private readonly http = inject(HttpClient);
  
  getAll(params?: PageQueryParams) {
    return this.http.get<PageResponse<ProductionCompany>>(API_ENDPOINTS.productionCompanies, {
      params: buildHttpParams(params),
    });
  }

  getById(id: number | string) {
    return this.http.get<ProductionCompany>(`${API_ENDPOINTS.productionCompanies}/${id}`);
  }

  create(payload: ProductionCompanyCreateRequest) {
    return this.http.post<ProductionCompany>(API_ENDPOINTS.productionCompanies, payload);
  }

  update(id: number | string, payload: ProductionCompanyCreateRequest) {
    return this.http.put<ProductionCompany>(`${API_ENDPOINTS.productionCompanies}/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${API_ENDPOINTS.productionCompanies}/${id}`);
  }
}
