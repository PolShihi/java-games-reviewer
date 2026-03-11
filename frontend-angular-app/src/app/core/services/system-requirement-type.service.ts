import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { SystemRequirementType } from '../models/system-requirement-type';

@Injectable({ providedIn: 'root' })
export class SystemRequirementTypeService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll() {
    return this.http.get<SystemRequirementType[]>(`${this.baseUrl}/system-requirement-types`);
  }

  getById(id: number | string) {
    return this.http.get<SystemRequirementType>(`${this.baseUrl}/system-requirement-types/${id}`);
  }
}
