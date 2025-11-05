import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Dia, InfoBadge } from '../models/workout.models';
import { WorkoutService } from '../services/workout.service';

@Component({
  selector: 'app-workout',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './workout.component.html',
  styleUrl: './workout.component.css'
})
export class WorkoutComponent implements OnInit {
  title = 'Treino por Supersets (1 músculo/dia) — Estações A/B/C coesas';
  infoBadges: InfoBadge[] = [];
  dias: Dia[] = [];

  constructor(private workoutService: WorkoutService) {}

  ngOnInit(): void {
    this.infoBadges = this.workoutService.getInfoBadges();
    this.dias = this.workoutService.getDias();
  }
}
