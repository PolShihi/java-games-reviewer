import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, DestroyRef, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute, Router } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiError } from '../../core/http/api-error';
import { GameService } from '../../core/services/game.service';
import { GenreService } from '../../core/services/genre.service';
import { ProductionCompanyService } from '../../core/services/production-company.service';
import { Genre } from '../../core/models/genre';
import { ProductionCompany } from '../../core/models/production-company';
import { fetchAllPageContent } from '../../core/utils/fetch-all-page-content';
import { ReferenceManagerDialogComponent, ReferenceEntityType } from '../../components/reference-manager-dialog/reference-manager-dialog.component';

@Component({
  selector: 'app-game-form-page',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatCardModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatProgressSpinnerModule,
    MatSelectModule,
    MatDialogModule,
  ],
  templateUrl: './game-form-page.component.html',
  styleUrl: './game-form-page.component.scss',
})
export class GameFormPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);
  private readonly gameService = inject(GameService);
  private readonly genreService = inject(GenreService);
  private readonly companyService = inject(ProductionCompanyService);
  private readonly destroyRef = inject(DestroyRef);
  private readonly cdr = inject(ChangeDetectorRef);
  private readonly dialog = inject(MatDialog);

  readonly mode = (this.route.snapshot.data['mode'] as 'create' | 'edit') ?? 'create';
  readonly gameId = this.route.snapshot.paramMap.get('id');

  genres: Genre[] = [];
  companies: ProductionCompany[] = [];
  loading = true;
  submitError: string | null = null;
  submitting = false;

  readonly form = this.fb.group({
    title: this.fb.control<string>('', [Validators.required, Validators.maxLength(150)]),
    releaseYear: this.fb.control<number | null>(null, [Validators.required, Validators.min(1950)]),
    description: this.fb.control<string>(''),
    developerId: this.fb.control<number | null>(null),
    publisherId: this.fb.control<number | null>(null),
    genreIds: this.fb.control<number[]>([], [Validators.required]),
  });

  ngOnInit(): void {
    this.loadPageData();
  }

  private async loadPageData(): Promise<void> {
    try {
      this.loading = true;
      await this.loadReferenceData();
      if (this.mode === 'edit') {
        await this.loadGame();
      }
    } catch (error) {
      const apiError = error as ApiError;
      this.submitError = apiError?.message || 'Failed to load form data. Please try again.';
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  private async loadReferenceData(): Promise<void> {
    const [genresResponse, companies] = await Promise.all([
      firstValueFrom(this.genreService.getAll()),
      fetchAllPageContent((params) => this.companyService.getAll(params), {
        sortBy: 'name',
        sortDirection: 'ASC' as const,
      }),
    ]);
    this.genres = Array.isArray(genresResponse) ? genresResponse : [];
    this.companies = companies;
  }

  openReferenceDialog(entityType: ReferenceEntityType) {
    const dialogRef = this.dialog.open(ReferenceManagerDialogComponent, {
      width: '960px',
      data: { entityType },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadReferenceData();
      }
    });
  }

  private async loadGame(): Promise<void> {
    if (!this.gameId) {
      throw new Error('Game id is required for edit mode.');
    }

    const game = await firstValueFrom(this.gameService.getById(this.gameId));
    this.form.patchValue({
      title: game.title || '',
      releaseYear: game.releaseYear ?? null,
      description: game.description || '',
      developerId: game.developer?.id ?? null,
      publisherId: game.publisher?.id ?? null,
      genreIds: game.genres?.map((genre) => genre.id) ?? [],
    });
  }

  get titleLabel(): string {
    return this.mode === 'edit' ? 'Edit Game' : 'Create Game';
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.submitError = null;
    this.submitting = true;

    const value = this.form.getRawValue();
    const payload = {
      title: value.title?.trim() ?? '',
      releaseYear: Number(value.releaseYear),
      description: value.description?.trim() || null,
      developerId: value.developerId ?? null,
      publisherId: value.publisherId ?? null,
      genreIds: value.genreIds ?? [],
    };

    try {
      if (this.mode === 'edit') {
        if (!this.gameId) {
          throw new Error('Game id is required for update.');
        }
        const updated = await firstValueFrom(this.gameService.update(this.gameId, payload));
        this.router.navigate(['/games', updated.id]);
      } else {
        const created = await firstValueFrom(this.gameService.create(payload));
        this.router.navigate(['/games', created.id]);
      }
    } catch (error) {
      const apiError = error as ApiError;
      const validationMessage =
        apiError?.errors && typeof apiError.errors === 'object'
          ? Object.values(apiError.errors)[0]
          : null;
      this.submitError = validationMessage || apiError?.message || 'Failed to save game. Please try again.';
    } finally {
      this.submitting = false;
      this.cdr.markForCheck();
    }
  }

  cancel() {
    if (this.mode === 'edit' && this.gameId) {
      this.router.navigate(['/games', this.gameId]);
      return;
    }

    this.router.navigate(['/']);
  }

  getFieldError(fieldName: string): string | null {
    const control = this.form.get(fieldName);
    if (!control?.touched || !control?.errors) {
      return null;
    }

    if (control.errors['required']) {
      return 'This field is required';
    }

    if (control.errors['maxlength']) {
      return `Max length is ${control.errors['maxlength'].requiredLength}`;
    }

    if (control.errors['min']) {
      return `Minimum value is ${control.errors['min'].min}`;
    }

    return 'Invalid value';
  }
}
