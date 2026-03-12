import { Component, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

@Component({
  selector: 'app-game-form-page',
  standalone: true,
  templateUrl: './game-form-page.component.html',
  styleUrl: './game-form-page.component.scss',
})
export class GameFormPageComponent {
  private readonly route = inject(ActivatedRoute);
  readonly mode = (this.route.snapshot.data['mode'] as 'create' | 'edit') ?? 'create';
}
