import { Routes } from '@angular/router';
import { HomeComponent } from './home/home.component';
import { WorkoutComponent } from './workout/workout.component';

export const routes: Routes = [
  { path: '', component: HomeComponent },
  { path: 'workout', component: WorkoutComponent },
];
