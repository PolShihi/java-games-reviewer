import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { CompanyType } from '../models/company-type';

@Injectable({ providedIn: 'root' })
export class CompanyTypeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll() {
    return this.http.get<CompanyType[]>(`${this.baseUrl}/company-types`);
  }

  getById(id: number | string) {
    return this.http.get<CompanyType>(`${this.baseUrl}/company-types/${id}`);
  }
}
