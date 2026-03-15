import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WorkoutComponent } from './workout/workout.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'workout', component: WorkoutComponent },
  {
    path: 'cadastro',
    loadComponent: () =>
      import('./auth/cadastro/cadastro.component').then(m => m.CadastroComponent)
  },
  {
    path: 'entrar',
    loadComponent: () =>
      import('./auth/login/login.component').then(m => m.LoginComponent)
  },
  {
    path: 'painel',
    loadComponent: () =>
      import('./dashboard/dashboard.component').then(m => m.DashboardComponent)
  },
];
