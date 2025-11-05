export interface Exercicio {
  nome: string;
  reps: string;
}

export interface Plano {
  titulo: string;
  equipamento: string;
  exercicios: Exercicio[];
}

export interface Superset {
  bloco: string;
  descanso: string;
  planos: Plano[];
  observacao?: string;
}

export interface Dia {
  musculo: string;
  tempo: string;
  supersets: Superset[];
}

export interface InfoBadge {
  texto: string;
}
