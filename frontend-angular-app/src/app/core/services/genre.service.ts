import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Genre, GenreCreateRequest } from '../models/genre';

@Injectable({ providedIn: 'root' })
export class GenreService {
  private readonly http = inject(HttpClient);
  
  getAll() {
    return this.http.get<Genre[]>('/genres');
  }

  getById(id: number | string) {
    return this.http.get<Genre>(`/genres/${id}`);
  }

  create(payload: GenreCreateRequest) {
    return this.http.post<Genre>('/genres', payload);
  }

  update(id: number | string, payload: GenreCreateRequest) {
    return this.http.put<Genre>(`/genres/${id}`, payload);
  }

  delete(id: number | string) {
    return this.http.delete<void>(`/genres/${id}`);
  }
}
