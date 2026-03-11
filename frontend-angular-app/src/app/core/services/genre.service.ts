import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { appConfig } from '../config/app-config';
import { Genre, GenreCreateRequest } from '../models/genre';

@Injectable({ providedIn: 'root' })
export class GenreService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = appConfig.api.baseUrl;

  getAll() {
    return this.http.get<Genre[]>(`${this.baseUrl}/genres`);
  }

  getById(id: number | string) {
    return this.http.get<Genre>(`${this.baseUrl}/genres/${id}`);
  }

  create(payload: GenreCreateRequest) {
    return this.http.post<Genre>(`${this.baseUrl}/genres`, payload);
  }

  update(id: number | string, payload: GenreCreateRequest) {
    return this.http.put<Genre>(`${this.baseUrl}/genres/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${this.baseUrl}/genres/${id}`);
  }
}
