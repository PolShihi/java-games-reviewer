import { CommonModule } from '@angular/common';
import { Component, DestroyRef, OnInit, ViewChild, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatPaginator, MatPaginatorModule, PageEvent } from '@angular/material/paginator';
import { MatSelectModule } from '@angular/material/select';
import { MatSort, MatSortModule, Sort } from '@angular/material/sort';
import { MatTableModule } from '@angular/material/table';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { firstValueFrom } from 'rxjs';
import { ApiError } from '../../core/http/api-error';
import { GameService } from '../../core/services/game.service';
import { GenreService } from '../../core/services/genre.service';
import { ProductionCompanyService } from '../../core/services/production-company.service';
import { fetchAllPageContent } from '../../core/utils/fetch-all-page-content';
import { normalizePageResponse } from '../../core/utils/normalize-page-response';
import { GameListItem } from '../../core/models/game';
import { Genre } from '../../core/models/genre';
import { ProductionCompany } from '../../core/models/production-company';
import { GameFilterParams, SortDirection } from '../../core/models/query-params';

@Component({
  selector: 'app-games-page',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatPaginatorModule,
    MatSelectModule,
    MatSortModule,
    MatTableModule,
    MatProgressSpinnerModule,
    FormsModule,
  ],
  templateUrl: './games-page.component.html',
  styleUrl: './games-page.component.scss',
})
export class GamesPageComponent implements OnInit {
  private readonly gameService = inject(GameService);
  private readonly genreService = inject(GenreService);
  private readonly companyService = inject(ProductionCompanyService);
  private readonly router = inject(Router);
  private readonly destroyRef = inject(DestroyRef);

  @ViewChild(MatPaginator) paginator?: MatPaginator;
  @ViewChild(MatSort) sort?: MatSort;

  readonly displayedColumns = [
    'id',
    'title',
    'releaseYear',
    'genres',
    'developerName',
    'publisherName',
    'averageRating',
    'actions',
  ];

  games: GameListItem[] = [];
  genres: Genre[] = [];
  companies: ProductionCompany[] = [];
  totalElements = 0;
  loading = true;
  error: string | null = null;

  page = 0;
  pageSize = 10;
  sortBy = 'id';
  sortDirection: SortDirection = 'ASC';

  filters = {
    title: '',
    yearFrom: '',
    yearTo: '',
    genreIds: [] as number[],
    developerId: '',
    publisherId: '',
    ratingFrom: '',
    ratingTo: '',
  };

  appliedFilters = {
    title: '',
    yearFrom: '',
    yearTo: '',
    genreIds: [] as number[],
    developerId: '',
    publisherId: '',
    ratingFrom: '',
    ratingTo: '',
  };

  ngOnInit(): void {
    this.loadFiltersData();
    this.fetchGames();
  }

  async loadFiltersData() {
    try {
      const [genresResponse, companies] = await Promise.all([
        firstValueFrom(this.genreService.getAll()),
        fetchAllPageContent((params) => this.companyService.getAll(params), {
          sortBy: 'name',
          sortDirection: 'ASC' as const,
        }),
      ]);
      this.genres = Array.isArray(genresResponse) ? genresResponse : [];
      this.companies = companies;
    } catch {
      // ignore filter data errors for now
    }
  }

  private buildFilterParams(): GameFilterParams {
    return {
      page: this.page,
      size: this.pageSize,
      sortBy: this.sortBy,
      sortDirection: this.sortDirection,
      title: this.appliedFilters.title || undefined,
      yearFrom: this.appliedFilters.yearFrom ? Number(this.appliedFilters.yearFrom) : undefined,
      yearTo: this.appliedFilters.yearTo ? Number(this.appliedFilters.yearTo) : undefined,
      genreIds: this.appliedFilters.genreIds.length
        ? this.appliedFilters.genreIds.join(',')
        : undefined,
      developerId: this.appliedFilters.developerId
        ? Number(this.appliedFilters.developerId)
        : undefined,
      publisherId: this.appliedFilters.publisherId
        ? Number(this.appliedFilters.publisherId)
        : undefined,
      ratingFrom: this.appliedFilters.ratingFrom
        ? Number(this.appliedFilters.ratingFrom)
        : undefined,
      ratingTo: this.appliedFilters.ratingTo ? Number(this.appliedFilters.ratingTo) : undefined,
    };
  }

  get hasActiveFilters(): boolean {
    return Boolean(
      this.appliedFilters.title ||
        this.appliedFilters.yearFrom ||
        this.appliedFilters.yearTo ||
        this.appliedFilters.genreIds.length ||
        this.appliedFilters.developerId ||
        this.appliedFilters.publisherId ||
        this.appliedFilters.ratingFrom ||
        this.appliedFilters.ratingTo
    );
  }

  fetchGames() {
    this.loading = true;
    this.error = null;

    const request$ = this.hasActiveFilters
      ? this.gameService.filter(this.buildFilterParams())
      : this.gameService.getAll({
          page: this.page,
          size: this.pageSize,
          sortBy: this.sortBy,
          sortDirection: this.sortDirection,
        });

    request$.pipe(takeUntilDestroyed(this.destroyRef)).subscribe({
      next: (response) => {
        const page = normalizePageResponse(response);
        this.games = page.content;
        this.totalElements = page.totalElements;
        this.loading = false;
      },
      error: (err: unknown) => {
        const apiError = err as ApiError;
        this.error = apiError?.message || 'Failed to load game list. Check backend connection and try again.';
        this.loading = false;
      },
    });
  }

  applyFilters() {
    this.appliedFilters = {
      title: this.filters.title.trim(),
      yearFrom: this.filters.yearFrom,
      yearTo: this.filters.yearTo,
      genreIds: [...this.filters.genreIds],
      developerId: this.filters.developerId,
      publisherId: this.filters.publisherId,
      ratingFrom: this.filters.ratingFrom,
      ratingTo: this.filters.ratingTo,
    };
    this.page = 0;
    this.fetchGames();
  }

  resetFilters() {
    this.filters = {
      title: '',
      yearFrom: '',
      yearTo: '',
      genreIds: [],
      developerId: '',
      publisherId: '',
      ratingFrom: '',
      ratingTo: '',
    };
    this.appliedFilters = { ...this.filters, genreIds: [] };
    this.page = 0;
    this.fetchGames();
  }

  onSortChange(sort: Sort) {
    if (!sort.active || !sort.direction) {
      return;
    }

    this.sortBy = sort.active;
    this.sortDirection = sort.direction === 'asc' ? 'ASC' : 'DESC';
    this.page = 0;
    this.fetchGames();
  }

  onPageChange(event: PageEvent) {
    this.page = event.pageIndex;
    this.pageSize = event.pageSize;
    this.fetchGames();
  }

  navigateToGame(id: number) {
    this.router.navigate(['/games', id]);
  }

  getGenreName(id: number): string {
    return this.genres.find((genre) => genre.id === id)?.name ?? '';
  }
}
