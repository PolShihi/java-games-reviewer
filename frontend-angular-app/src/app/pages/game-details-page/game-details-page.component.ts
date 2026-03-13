import { CommonModule } from '@angular/common';
import { ChangeDetectorRef, Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { MatDialog, MatDialogModule } from '@angular/material/dialog';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiError } from '../../core/http/api-error';
import { GameDetail } from '../../core/models/game';
import { GameService } from '../../core/services/game.service';
import { SystemRequirementService } from '../../core/services/system-requirement.service';
import { SystemRequirementDialogComponent } from '../../components/system-requirement-dialog/system-requirement-dialog.component';
import { ReviewService } from '../../core/services/review.service';
import { ReviewDialogComponent } from '../../components/review-dialog/review-dialog.component';

@Component({
  selector: 'app-game-details-page',
  standalone: true,
  imports: [
    CommonModule,
    MatButtonModule,
    MatCardModule,
    MatChipsModule,
    MatIconModule,
    MatProgressSpinnerModule,
    MatTableModule,
    MatDialogModule,
    RouterLink,
  ],
  templateUrl: './game-details-page.component.html',
  styleUrl: './game-details-page.component.scss',
})
export class GameDetailsPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly gameService = inject(GameService);
  private readonly requirementService = inject(SystemRequirementService);
  private readonly reviewService = inject(ReviewService);
  private readonly dialog = inject(MatDialog);
  private readonly cdr = inject(ChangeDetectorRef);

  game: GameDetail | null = null;
  loading = true;
  error: string | null = null;
  deleting = false;

  readonly requirementColumns = [
    'type',
    'storageGb',
    'ramGb',
    'cpuGhz',
    'gpuTflops',
    'vramGb',
    'actions',
  ];

  ngOnInit(): void {
    this.loadGame();
  }

  async loadGame(): Promise<void> {
    const id = this.route.snapshot.paramMap.get('id');
    if (!id) {
      this.error = 'Game ID is missing.';
      this.loading = false;
      return;
    }

    try {
      this.loading = true;
      this.error = null;
      this.game = await firstValueFrom(this.gameService.getById(id));
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to load game details.';
    } finally {
      this.loading = false;
      this.cdr.markForCheck();
    }
  }

  async deleteGame(): Promise<void> {
    if (!this.game?.id) {
      return;
    }

    const confirmed = globalThis.confirm('Delete this game? This action cannot be undone.');
    if (!confirmed) {
      return;
    }

    try {
      this.deleting = true;
      await firstValueFrom(this.gameService.delete(this.game.id));
      this.router.navigate(['/']);
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to delete game.';
    } finally {
      this.deleting = false;
      this.cdr.markForCheck();
    }
  }

  openAddRequirement() {
    if (!this.game?.id) {
      return;
    }

    const dialogRef = this.dialog.open(SystemRequirementDialogComponent, {
      width: '600px',
      data: { gameId: this.game.id },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadGame();
      }
    });
  }

  openEditRequirement(requirement: any) {
    if (!this.game?.id) {
      return;
    }

    const dialogRef = this.dialog.open(SystemRequirementDialogComponent, {
      width: '600px',
      data: { gameId: this.game.id, requirement },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadGame();
      }
    });
  }

  async deleteRequirement(requirementId: number) {
    const confirmed = globalThis.confirm('Delete this system requirement?');
    if (!confirmed) {
      return;
    }

    try {
      await firstValueFrom(this.requirementService.delete(requirementId));
      await this.loadGame();
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to delete system requirement.';
    } finally {
      this.cdr.markForCheck();
    }
  }

  openAddReview() {
    if (!this.game?.id) {
      return;
    }

    const dialogRef = this.dialog.open(ReviewDialogComponent, {
      width: '600px',
      data: { gameId: this.game.id },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadGame();
      }
    });
  }

  openEditReview(review: any) {
    if (!this.game?.id) {
      return;
    }

    const dialogRef = this.dialog.open(ReviewDialogComponent, {
      width: '600px',
      data: { gameId: this.game.id, review },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result?.changed) {
        this.loadGame();
      }
    });
  }

  async deleteReview(reviewId: number) {
    const confirmed = globalThis.confirm('Delete this review?');
    if (!confirmed) {
      return;
    }

    try {
      await firstValueFrom(this.reviewService.delete(reviewId));
      await this.loadGame();
    } catch (error) {
      const apiError = error as ApiError;
      this.error = apiError?.message || 'Failed to delete review.';
    } finally {
      this.cdr.markForCheck();
    }
  }
}
