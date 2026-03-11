import { MediaOutlet } from './media-outlet';

export interface Review {
  id: number;
  gameId: number;
  gameTitle: string | null;
  mediaOutlet: MediaOutlet | null;
  score: number | null;
  summary: string | null;
}

export interface ReviewCreateRequest {
  gameId: number;
  mediaOutletId: number;
  score: number;
  summary?: string | null;
}
