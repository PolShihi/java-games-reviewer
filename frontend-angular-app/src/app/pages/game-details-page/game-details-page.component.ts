import { CommonModule } from '@angular/common';
import { Component, OnInit, inject } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatIconModule } from '@angular/material/icon';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatTableModule } from '@angular/material/table';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { firstValueFrom } from 'rxjs';
import { ApiError } from '../../core/http/api-error';
import { GameDetail } from '../../core/models/game';
import { GameService } from '../../core/services/game.service';

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
    RouterLink,
  ],
  templateUrl: './game-details-page.component.html',
  styleUrl: './game-details-page.component.scss',
})
export class GameDetailsPageComponent implements OnInit {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly gameService = inject(GameService);

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
    }
  }

  async deleteGame(): Promise<void> {
    if (!this.game?.id) {
      return;
    }

    const confirmed = window.confirm('Delete this game? This action cannot be undone.');
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
    }
  }
}
