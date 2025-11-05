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
      },
      {
        musculo: 'COSTAS',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Máquina Remada (peito apoiado)',
                exercicios: [
                  { nome: 'Remada neutra', reps: '8–10' },
                  { nome: 'Remada pronada', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia alta + baixa (torre dupla adjacente)',
                exercicios: [
                  { nome: 'Puxada aberta pronada', reps: '8–10' },
                  { nome: 'Remada baixa no triângulo', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Remada unilateral com halter', reps: '8–10 (cada)' },
                  { nome: 'Remada serrada com halter', reps: '10–12' }
                ]
              }
            ],
            observacao: 'Obs: Controle 3s na descida na 3ª volta.'
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Polia alta (mesma altura)',
                exercicios: [
                  { nome: 'Puxada aberta pronada', reps: '8–10' },
                  { nome: 'Puxada neutra (triângulo)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra fixa + banco (mesmo rack)',
                exercicios: [
                  { nome: 'Barra fixa pronada (assistida se preciso)', reps: '6–8' },
                  { nome: 'Remada invertida na barra (rack)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Máquina Remada (peito apoiado)',
                exercicios: [
                  { nome: 'Remada pegada neutra', reps: '8–10' },
                  { nome: 'Remada pegada supinada', reps: '8–10' }
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
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Pull-over na polia (corda)', reps: '12–15' },
                  { nome: 'Face pull', reps: '15–20' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Pull-over com halter', reps: '10–12' },
                  { nome: 'Remada halter leve (bombeamento)', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia baixa',
                exercicios: [
                  { nome: 'Remada baixa aberta', reps: '10–12' },
                  { nome: 'Remada baixa neutra', reps: '10–12' }
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
                equipamento: 'Banco + Halteres pesados',
                exercicios: [
                  { nome: 'Remada halter apoiado no banco', reps: '8–10' },
                  { nome: 'Encolhimento com halteres', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Remada curvada com barra', reps: '6–8' },
                  { nome: 'Encolhimento com barra', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Puxada supinada', reps: '8–10' },
                  { nome: 'Puxada neutra (paralela)', reps: '10–12' }
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
                equipamento: 'Polia alta (pegada aberta)',
                exercicios: [
                  { nome: 'Puxada por trás da cabeça (leve)', reps: '12–15' },
                  { nome: 'Puxada à frente com pausa 2s', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Banco romano / hiperextensões',
                exercicios: [
                  { nome: 'Hiperextensão lombar', reps: '12–15' },
                  { nome: 'Hiperextensão isométrica (topo)', reps: '20–30s' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Pullover leve (alongamento)', reps: '12–15' },
                  { nome: 'Remada unilateral leve (flush)', reps: '12–15' }
                ]
              }
            ]
          }
        ]
      },
      {
        musculo: 'PERNAS',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Leg Press',
                exercicios: [
                  { nome: 'Leg press amplitude completa', reps: '10–12' },
                  { nome: 'Panturrilha no leg press', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Cadeira extensora + Cadeira flexora (lado a lado)',
                exercicios: [
                  { nome: 'Extensora (3s descida)', reps: '12–15' },
                  { nome: 'Mesa flexora (3s descida)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Agachamento goblet', reps: '10–12' },
                  { nome: 'RDL com halteres', reps: '8–10' }
                ]
              }
            ],
            observacao: 'Obs: Se a perna estiver lotada, priorize A ou B e faça 2 voltas extras.'
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Agachamento no Smith (setor perna)',
                exercicios: [
                  { nome: 'Agachamento no Smith', reps: '6–8' },
                  { nome: 'Agachamento frontal no Smith', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Hack machine',
                exercicios: [
                  { nome: 'Hack machine (profundo)', reps: '8–10' },
                  { nome: 'Hack parcial no topo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Afundo caminhando', reps: '10–12 (cada)' },
                  { nome: 'Agacho búlgaro', reps: '10–12 (cada)' }
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
                equipamento: 'Cadeira extensora',
                exercicios: [
                  { nome: 'Extensora completa', reps: '12–15' },
                  { nome: 'Extensora parcial no topo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Mesa flexora (deitado ou sentado)',
                exercicios: [
                  { nome: 'Flexora completa', reps: '10–12' },
                  { nome: 'Flexora parcial no alongado', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco romano / lombar',
                exercicios: [
                  { nome: 'Hip extension', reps: '12–15' },
                  { nome: 'Boa-manhã com halteres', reps: '12–15' }
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
                equipamento: 'Panturrilha em pé (máquina)',
                exercicios: [
                  { nome: 'Panturrilha em pé', reps: '10–12' },
                  { nome: 'Panturrilha isometria no alto (5s)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Leg press (plataforma baixa)',
                exercicios: [
                  { nome: 'Panturrilha leg press (amplitude total)', reps: '12–15' },
                  { nome: 'Panturrilha leg press (parcial topo)', reps: '15–20' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Panturrilha com halteres (em pé)', reps: '12–15' },
                  { nome: 'Panturrilha unilateral com halter', reps: '10–12 (cada)' }
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
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Levantamento romeno com halteres', reps: '8–10' },
                  { nome: 'Stiff com halteres (pausa no alongado)', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra + Rack (se disponível no setor)',
                exercicios: [
                  { nome: 'Stiff com barra', reps: '6–8' },
                  { nome: 'Boa-manhã com barra', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco romano',
                exercicios: [
                  { nome: 'Hip extension com carga abraçada', reps: '10–12' },
                  { nome: 'Hip extension isométrico topo', reps: '20–30s' }
                ]
              }
            ]
          }
        ]
      },
      {
        musculo: 'OMBROS',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Desenvolvimento com halteres', reps: '6–8' },
                  { nome: 'Arnold press', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Militar em pé com barra', reps: '6–8' },
                  { nome: 'Press push leve', reps: '6–8' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia baixa/alta',
                exercicios: [
                  { nome: 'Press na polia (barra curta)', reps: '8–10' },
                  { nome: 'Elevação frontal na polia', reps: '10–12' }
                ]
              }
            ],
            observacao: 'Obs: Rest-pause na 3ª volta do Plano A.'
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Polia baixa (mesma altura)',
                exercicios: [
                  { nome: 'Elevação lateral na polia (unilateral alternando)', reps: '12–15 (cada)' },
                  { nome: 'Elevação frontal na polia', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Elevação lateral com halteres', reps: '12–15' },
                  { nome: 'Elevação frontal com halteres', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Cabo cruzado alto',
                exercicios: [
                  { nome: 'Lateral no cabo cruzado', reps: '12–15' },
                  { nome: 'Frontal no cabo cruzado', reps: '10–12' }
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
                equipamento: 'Peck-deck inverso',
                exercicios: [
                  { nome: 'Crucifixo inverso', reps: '12–15' },
                  { nome: 'Isometria no final', reps: '20–30s' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Face pull', reps: '15–20' },
                  { nome: 'Puxada alta para deltoide posterior', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres leves',
                exercicios: [
                  { nome: 'Crucifixo inverso halteres', reps: '12–15' },
                  { nome: 'Reverse fly parcial topo', reps: '15–20' }
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
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Militar com barra (sentado ou em pé)', reps: '6–8' },
                  { nome: 'Remada alta com barra', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Press neutro com halteres', reps: '6–8' },
                  { nome: 'Remada alta com halteres', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia média',
                exercicios: [
                  { nome: 'Press no cabo (barra curta)', reps: '8–10' },
                  { nome: 'Upright row na polia', reps: '8–10' }
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
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Encolhimento com halteres', reps: '10–12' },
                  { nome: 'Encolhimento com 2s no topo', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Trap bar/Barra: encolhimento', reps: '10–12' },
                  { nome: 'Encolhimento com pausa', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia baixa',
                exercicios: [
                  { nome: 'Shrug na polia', reps: '12–15' },
                  { nome: 'Shrug isométrico (topo)', reps: '20–30s' }
                ]
              }
            ]
          }
        ]
      },
      {
        musculo: 'BÍCEPS',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Barra EZ no suporte',
                exercicios: [
                  { nome: 'Rosca direta EZ', reps: '6–8' },
                  { nome: 'Rosca inversa EZ', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Rosca alternada', reps: '8–10' },
                  { nome: 'Rosca martelo', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia baixa',
                exercicios: [
                  { nome: 'Rosca na polia (barra W)', reps: '10–12' },
                  { nome: 'Rosca com corda (neutro/pronada)', reps: '12–15' }
                ]
              }
            ],
            observacao: 'Obs: Excêntrica 3s na 3ª volta do Plano A.'
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco Scott',
                exercicios: [
                  { nome: 'Rosca Scott', reps: '8–10' },
                  { nome: 'Scott parcial no alongado', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia baixa + Banco (ajudando)',
                exercicios: [
                  { nome: 'Scott no cabo', reps: '8–10' },
                  { nome: 'Isometria a 90° (cabo)', reps: '20–30s' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco inclinado + halteres',
                exercicios: [
                  { nome: 'Rosca inclinado com halteres', reps: '8–10' },
                  { nome: 'Rosca 1/2 amplitude no topo', reps: '12–15' }
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
                equipamento: 'Polia alta/baixa',
                exercicios: [
                  { nome: 'Pose bíceps no cabo alto', reps: '10–12' },
                  { nome: 'Rosca na polia baixa', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Halteres',
                exercicios: [
                  { nome: 'Rosca concentrada', reps: '10–12' },
                  { nome: 'Isometria em 90° (halter)', reps: '20–30s' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Rosca barra reta', reps: '6–8' },
                  { nome: 'Rosca inversa barra', reps: '10–12' }
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
                equipamento: 'Halteres (mesmo par)',
                exercicios: [
                  { nome: 'Rosca alternada cadenciada', reps: '8–10' },
                  { nome: 'Cross-body hammer', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia baixa (corda)',
                exercicios: [
                  { nome: 'Rosca neutra (corda)', reps: '10–12' },
                  { nome: 'Rosca martelo no cabo', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Rosca sentado', reps: '8–10' },
                  { nome: 'Rosca martelo sentado', reps: '10–12' }
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
                equipamento: 'Polia baixa',
                exercicios: [
                  { nome: 'Rosca com corda (alto volume)', reps: '12–15' },
                  { nome: 'Rosca 21 na polia', reps: '21' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Rosca 21 com barra', reps: '21' },
                  { nome: 'Rosca direta leve (flush)', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Halteres leves',
                exercicios: [
                  { nome: 'Rosca 21 com halteres', reps: '21' },
                  { nome: 'Rosca bombeamento', reps: '15–20' }
                ]
              }
            ]
          }
        ]
      },
      {
        musculo: 'TRÍCEPS',
        tempo: '5 blocos (A–E) · ~55–60 minutos',
        supersets: [
          {
            bloco: 'Bloco A',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Barra',
                exercicios: [
                  { nome: 'Supino fechado', reps: '6–8' },
                  { nome: 'Testa EZ (deitado)', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Paralelas + banco',
                exercicios: [
                  { nome: 'Paralelas (assistido se preciso)', reps: '6–8' },
                  { nome: 'Mergulho no banco', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Pressdown barra reta', reps: '10–12' },
                  { nome: 'Pressdown pegada supinada', reps: '12–15' }
                ]
              }
            ]
          },
          {
            bloco: 'Bloco B',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Polia alta (corda)',
                exercicios: [
                  { nome: 'Tríceps corda (pushdown)', reps: '10–12' },
                  { nome: 'Overhead corda', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia alta (barra V/reta)',
                exercicios: [
                  { nome: 'Pressdown barra V/reta', reps: '10–12' },
                  { nome: 'Overhead barra na polia', reps: '10–12' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Halteres (mesmo banco)',
                exercicios: [
                  { nome: 'Extensão unilateral acima da cabeça', reps: '12–15' },
                  { nome: 'Coice unilateral', reps: '12–15' }
                ]
              }
            ],
            observacao: 'Obs: Drop único na 3ª volta do Plano A.'
          },
          {
            bloco: 'Bloco C',
            descanso: '0–15s intra • 60–75s entre voltas',
            planos: [
              {
                titulo: 'Plano A',
                equipamento: 'Banco + Barra',
                exercicios: [
                  { nome: 'JM Press (variação do supino fechado)', reps: '6–8' },
                  { nome: 'Lockout no rack (parciais)', reps: '8–10' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Polia alta',
                exercicios: [
                  { nome: 'Pressdown com barra W', reps: '10–12' },
                  { nome: 'Extensão inversa no cabo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Paralelas + Banco',
                exercicios: [
                  { nome: 'Paralelas com pausa', reps: '6–8' },
                  { nome: 'Isometria no fundo', reps: '10–20s' }
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
                equipamento: 'Polia alta (barra/corda)',
                exercicios: [
                  { nome: 'Pressdown pausado 1s', reps: '10–12' },
                  { nome: 'Pressdown parcial topo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Banco + Halteres',
                exercicios: [
                  { nome: 'Testa com halteres', reps: '10–12' },
                  { nome: 'Skull crusher parcial topo', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Barra + Rack',
                exercicios: [
                  { nome: 'Supino fechado leve (volume)', reps: '10–12' },
                  { nome: 'Board press (2–3 boards)', reps: '8–10' }
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
                equipamento: 'Polia baixa (unilateral)',
                exercicios: [
                  { nome: 'Coice no cabo unilateral', reps: '12–15' },
                  { nome: 'Overhead no cabo unilateral', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano B',
                equipamento: 'Halteres (mesmo banco)',
                exercicios: [
                  { nome: 'Coice unilateral halter', reps: '12–15' },
                  { nome: 'Extensão unilateral acima da cabeça', reps: '12–15' }
                ]
              },
              {
                titulo: 'Plano C',
                equipamento: 'Paralelas/banco',
                exercicios: [
                  { nome: 'Mergulho no banco', reps: '12–15' },
                  { nome: 'Isometria lockout (banco)', reps: '20–30s' }
                ]
              }
            ]
          }
        ]
      }
    ];
  }
}
