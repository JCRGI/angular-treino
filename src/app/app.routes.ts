import { Routes } from '@angular/router';
import { WorkoutComponent } from './workout/workout.component';

export const routes: Routes = [
  { path: '', component: WorkoutComponent },
  { path: 'workout', component: WorkoutComponent }
];
