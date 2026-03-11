import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { SystemRequirementType } from '../models/system-requirement-type';

@Injectable({ providedIn: 'root' })
export class SystemRequirementTypeService {
  private readonly http = inject(HttpClient);
  getAll() {
    return this.http.get<SystemRequirementType[]>(API_ENDPOINTS.systemRequirementTypes);
  }

  getById(id: number | string) {
    return this.http.get<SystemRequirementType>(`${API_ENDPOINTS.systemRequirementTypes}/${id}`);
  }
}
