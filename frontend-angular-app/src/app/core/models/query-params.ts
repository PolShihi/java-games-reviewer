export type SortDirection = 'ASC' | 'DESC';

type QueryParamValue =
  | string
  | number
  | boolean
  | readonly (string | number | boolean)[]
  | null
  | undefined;

export interface PageQueryParams {
  [key: string]: QueryParamValue;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDirection?: SortDirection;
}

export interface GameFilterParams extends PageQueryParams {
  [key: string]: QueryParamValue;
  title?: string;
  yearFrom?: number;
  yearTo?: number;
  genreIds?: string | number[];
  developerId?: number;
  publisherId?: number;
  ratingFrom?: number;
  ratingTo?: number;
}
