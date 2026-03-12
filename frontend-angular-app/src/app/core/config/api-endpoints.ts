export const API_ENDPOINTS = {
  games: '/games',
  gamesFilter: '/games/filter',
  genres: '/genres',
  productionCompanies: '/production-companies',
  mediaOutlets: '/media-outlets',
  reviews: '/reviews',
  reviewsByGame: (gameId: number | string) => `/reviews/game/${gameId}`,
  systemRequirements: '/system-requirements',
  systemRequirementsByGame: (gameId: number | string) =>
    `/system-requirements/game/${gameId}`,
  systemRequirementTypes: '/system-requirement-types',
  companyTypes: '/company-types',
} as const;
