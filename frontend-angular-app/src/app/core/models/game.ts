import { Genre } from './genre';
import { ProductionCompany } from './production-company';
import { Review } from './review';
import { SystemRequirement } from './system-requirement';

export interface GameListItem {
  id: number;
  title: string;
  releaseYear: number | null;
  developerName: string | null;
  publisherName: string | null;
  genreNames: string[];
  averageRating: number | null;
}

export interface GameDetail {
  id: number;
  title: string;
  releaseYear: number | null;
  description: string | null;
  developer: ProductionCompany | null;
  publisher: ProductionCompany | null;
  genres: Genre[];
  systemRequirements: SystemRequirement[];
  reviews: Review[];
  averageRating: number | null;
}

export interface GameCreateRequest {
  title: string;
  releaseYear: number;
  description?: string | null;
  developerId?: number | null;
  publisherId?: number | null;
  genreIds: number[];
}

export interface GameUpdateRequest {
  title?: string | null;
  releaseYear?: number | null;
  description?: string | null;
  developerId?: number | null;
  publisherId?: number | null;
  genreIds?: number[] | null;
}
