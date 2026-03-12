import { Routes } from '@angular/router';
import { AppLayoutComponent } from './layout/app-layout.component';
import { GameDetailsPageComponent } from './pages/game-details-page/game-details-page.component';
import { GameFormPageComponent } from './pages/game-form-page/game-form-page.component';
import { GamesPageComponent } from './pages/games-page/games-page.component';
import { NotFoundPageComponent } from './pages/not-found-page/not-found-page.component';

export const routes: Routes = [
  {
    path: '',
    component: AppLayoutComponent,
    children: [
      { path: '', component: GamesPageComponent },
      { path: 'games/new', component: GameFormPageComponent, data: { mode: 'create' } },
      { path: 'games/:id', component: GameDetailsPageComponent },
      { path: 'games/:id/edit', component: GameFormPageComponent, data: { mode: 'edit' } },
      { path: 'home', redirectTo: '', pathMatch: 'full' },
    ],
  },
  { path: '**', component: NotFoundPageComponent },
];
