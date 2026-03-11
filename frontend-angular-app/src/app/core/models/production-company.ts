export interface ProductionCompany {
  id: number;
  name: string;
  foundedYear: number | null;
  websiteUrl: string | null;
  ceo: string | null;
  companyTypeName: string | null;
}

export interface ProductionCompanyCreateRequest {
  name: string;
  foundedYear?: number | null;
  websiteUrl?: string | null;
  ceo?: string | null;
  companyTypeId?: number | null;
}
