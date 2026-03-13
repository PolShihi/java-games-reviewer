import { CommonModule } from '@angular/common';
import { Component, Inject, OnInit, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatDialog, MatDialogModule, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatSelectModule } from '@angular/material/select';
import { firstValueFrom } from 'rxjs';
import { ApiError } from '../../core/http/api-error';
import { MediaOutlet } from '../../core/models/media-outlet';
import { Review } from '../../core/models/review';
import { MediaOutletService } from '../../core/services/media-outlet.service';
import { ReviewService } from '../../core/services/review.service';
import { fetchAllPageContent } from '../../core/utils/fetch-all-page-content';
import { ReferenceManagerDialogComponent } from '../reference-manager-dialog/reference-manager-dialog.component';

export interface ReviewDialogData {
  gameId: number;
  review?: Review | null;
}

@Component({
  selector: 'app-review-dialog',
  standalone: true,
  imports: [
    CommonModule,
    ReactiveFormsModule,
    MatButtonModule,
    MatDialogModule,
    MatFormFieldModule,
    MatIconModule,
    MatInputModule,
    MatSelectModule,
  ],
  templateUrl: './review-dialog.component.html',
  styleUrl: './review-dialog.component.scss',
})
export class ReviewDialogComponent implements OnInit {
  private readonly fb = inject(FormBuilder);
  private readonly dialogRef = inject(MatDialogRef<ReviewDialogComponent>);
  private readonly dialog = inject(MatDialog);
  private readonly reviewService = inject(ReviewService);
  private readonly mediaOutletService = inject(MediaOutletService);

  readonly gameId: number;
  readonly initialReview: Review | null;

  outlets: MediaOutlet[] = [];
  error: string | null = null;
  submitting = false;

  readonly form = this.fb.group({
    mediaOutletId: this.fb.control<number | null>(null, [Validators.required]),
    score: this.fb.control<number | null>(null, [Validators.required, Validators.min(0), Validators.max(100)]),
    summary: this.fb.control<string>(''),
  });

  constructor(@Inject(MAT_DIALOG_DATA) data: ReviewDialogData) {
    this.gameId = data.gameId;
    this.initialReview = data.review ?? null;
  }

  get title(): string {
    return this.initialReview?.id ? 'Edit Review' : 'Add Review';
  }

  ngOnInit(): void {
    this.loadOutlets();
    this.prefillForm();
  }

  private async loadOutlets(): Promise<void> {
    try {
      this.outlets = await fetchAllPageContent(
        (params) => this.mediaOutletService.getAll(params),
        { sortBy: 'name', sortDirection: 'ASC' as const }
      );
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to load media outlets.';
    }
  }

  private prefillForm(): void {
    if (!this.initialReview) {
      return;
    }

    this.form.patchValue({
      mediaOutletId: this.initialReview.mediaOutlet?.id ?? null,
      score: this.initialReview.score ?? null,
      summary: this.initialReview.summary || '',
    });
  }

  async submit(): Promise<void> {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    try {
      this.submitting = true;
      this.error = null;

      const value = this.form.getRawValue();
      const payload = {
        gameId: this.gameId,
        mediaOutletId: Number(value.mediaOutletId),
        score: Number(value.score),
        summary: value.summary?.trim() || null,
      };

      if (this.initialReview?.id) {
        await firstValueFrom(this.reviewService.update(this.initialReview.id, payload));
      } else {
        await firstValueFrom(this.reviewService.create(payload));
      }

      this.dialogRef.close({ changed: true });
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to save review.';
    } finally {
      this.submitting = false;
    }
  }

  openManageOutlets(): void {
    const dialogRef = this.dialog.open(ReferenceManagerDialogComponent, {
      width: '960px',
      data: { entityType: 'media-outlets' },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadOutlets();
      }
    });
  }

  close(): void {
    this.dialogRef.close();
  }
}
