export interface MediaOutlet {
  id: number;
  name: string;
  websiteUrl: string | null;
  foundedYear: number | null;
}

export interface MediaOutletCreateRequest {
  name: string;
  websiteUrl?: string | null;
  foundedYear?: number | null;
}
