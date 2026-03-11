import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { CompanyType } from '../models/company-type';

@Injectable({ providedIn: 'root' })
export class CompanyTypeService {
  private readonly http = inject(HttpClient);
  getAll() {
    return this.http.get<CompanyType[]>('/company-types');
  }

  getById(id: number | string) {
    return this.http.get<CompanyType>(`/company-types/${id}`);
  }
}
