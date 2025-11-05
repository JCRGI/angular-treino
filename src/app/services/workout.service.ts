import { Injectable } from '@angular/core';
import { Dia, InfoBadge } from '../models/workout.models';

@Injectable({
  providedIn: 'root'
})
export class WorkoutService {

  getInfoBadges(): InfoBadge[] {
    return [
      { texto: 'Sessão: ~55–60 min (5 blocos × 3 voltas)' },
      { texto: 'Descanso: 0–15s intra • 60–75s entre voltas' },
      { texto: 'Progressão: topo da faixa → +2–5% carga' },
      { texto: 'Falha: apenas na 3ª volta' },
      { texto: 'Atualizado: 03/11/2025' }
    ];
  }

  getDias(): Dia[] {
    return [
      {
        musculo: 'PEITO',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Halteres (mesmo par)',
                exercicios: [
                  { nome: 'Supino inclinado com halteres', reps: '6–8' },
                  { nome: 'Crucifixo inclinado com halteres', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia dupla (mesma altura/pegada)',
                exercicios: [
                  { nome: 'Supino na polia (banco inclinado)', reps: '8–10' },
                  { nome: 'Cross-over alto→baixo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Barra (mesmo rack)',
                exercicios: [
                  { nome: 'Supino inclinado com barra', reps: '6–8' },
                  { nome: 'Crucifixo reto com halteres (mesmo banco)', reps: '10–12' }
                ]
              }
            ],
            observacao: 'Obs: Drop único na 3ª volta do Plano B.'
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Supino reto com halteres', reps: '8–10' },
                  { nome: 'Pull-over com halter', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Supino na polia (banco reto)', reps: '8–10' },
                  { nome: 'Pull-over na polia (corda ou barra)', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Peck-deck',
                exercicios: [
                  { nome: 'Peck-deck completo', reps: '12–15' },
                  { nome: 'Peck-deck parcial no alongado', reps: '12–15' }
                ]
              }
            ]
          },
          {
            bloco: 'Bloco C',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Barra (mesmo rack)',
                exercicios: [
                  { nome: 'Supino reto com barra (pegada média)', reps: '6–8' },
                  { nome: 'Flexão no rack (barra baixa)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Supino reto com halteres pesado', reps: '6–8' },
                  { nome: 'Flexão inclinada no banco', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Peso corporal + banco',
                exercicios: [
                  { nome: 'Paralelas para peito', reps: '8–10' },
                  { nome: 'Flexão com pausa 2s', reps: '10–12' }
                ]
              }
            ]
          },
          {
            bloco: 'Bloco D',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Polia dupla (altura média constante)',
                exercicios: [
                  { nome: 'Cross-over médio→médio', reps: '12–15' },
                  { nome: 'Cross-over isométrico (segurar no meio)', reps: '20–30s' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Peck-deck',
                exercicios: [
                  { nome: 'Peck-deck neutro', reps: '12–15' },
                  { nome: 'Peck-deck com 2s de pausa no alongado', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco inclinado + Halteres leves',
                exercicios: [
                  { nome: 'Crucifixo inclinado (cadência 3s descida)', reps: '12–15' },
                  { nome: 'Press inclinado leve (bombeamento)', reps: '15–20' }
                ]
              }
            ]
          },
          {
            bloco: 'Bloco E',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Paralelas',
                exercicios: [
                  { nome: 'Paralelas para peito', reps: '8–10' },
                  { nome: 'Isometria no fundo', reps: '10–20s' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Banco + Barra (rack reto)',
                exercicios: [
                  { nome: 'Supino declinado com barra', reps: '6–8' },
                  { nome: 'Press com pegada larga (reto)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Peso corporal',
                exercicios: [
                  { nome: 'Flexão com pés elevados', reps: '10–12' },
                  { nome: 'Flexão inclinada no banco', reps: '12–15' }
                ]
              }
            ]
          }
        ]
      }
      // Adicione outros músculos aqui seguindo o mesmo padrão
    ];
  }
}
