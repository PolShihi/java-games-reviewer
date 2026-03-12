import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { API_ENDPOINTS } from '../config/api-endpoints';
import { Genre, GenreCreateRequest } from '../models/genre';

@Injectable({ providedIn: 'root' })
export class GenreService {
  private readonly http = inject(HttpClient);

  getAll() {
    return this.http.get<Genre[]>(API_ENDPOINTS.genres);
  }

  getById(id: number | string) {
    return this.http.get<Genre>(`${API_ENDPOINTS.genres}/${id}`);
  }

  create(payload: GenreCreateRequest) {
    return this.http.post<Genre>(API_ENDPOINTS.genres, payload);
  }

  update(id: number | string, payload: GenreCreateRequest) {
    return this.http.put<Genre>(`${API_ENDPOINTS.genres}/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`${API_ENDPOINTS.genres}/${id}`);
  }
}
