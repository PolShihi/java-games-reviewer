import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { SystemRequirementType } from '../models/system-requirement-type';

@Injectable({ providedIn: 'root' })
export class SystemRequirementTypeService {
  private readonly http = inject(HttpClient);
  getAll() {
    return this.http.get<SystemRequirementType[]>('/system-requirement-types');
  }

  getById(id: number | string) {
    return this.http.get<SystemRequirementType>(`/system-requirement-types/${id}`);
  }
}
