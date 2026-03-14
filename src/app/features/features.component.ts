import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-features',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './features.component.html',
  styleUrl: './features.component.css'
})
export class FeaturesComponent {
  activeTab = signal(0);

  tabs = [
    { label: 'Pagamentos', icon: '💳' },
    { label: 'Assinaturas', icon: '🔄' },
    { label: 'Marketplace', icon: '🏪' },
    { label: 'Emissão de cartões', icon: '💎' },
  ];

  features = [
    {
      icon: '⚡',
      title: 'Aceite pagamentos em segundos',
      description: 'Integre em minutos com nossa API REST intuitiva. Suporte completo a Pix, boleto, cartão de crédito/débito, carteira digital e muito mais.',
      tag: 'Pagamentos Online',
      color: '#5B4FE8',
      highlights: ['Pix instantâneo', 'Cartão em 1 clique', 'Antifraude nativo', 'Checkout customizável'],
    },
    {
      icon: '🔄',
      title: 'Gestão de assinaturas inteligente',
      description: 'Cobre clientes automaticamente com dunning inteligente, planos flexíveis e relatórios em tempo real. Mais de 200 milhões de assinaturas ativas na plataforma.',
      tag: 'Assinaturas',
      color: '#00D4AA',
      highlights: ['Cobranças recorrentes', 'Dunning automático', 'Upgrades/downgrades', 'Métricas de MRR'],
    },
    {
      icon: '🏪',
      title: 'Plataforma de marketplace completa',
      description: 'Transforme seu SaaS em um ecossistema financeiro. Divida pagamentos entre vendedores, gerencie repasses e onboarding automático.',
      tag: 'Marketplace',
      color: '#FF5C8A',
      highlights: ['Split automático', 'Onboarding de sellers', 'KYC/KYB integrado', 'Dashboard unificado'],
    },
    {
      icon: '💳',
      title: 'Emita seus próprios cartões',
      description: 'Lance programas de cartão de crédito ou débito com sua marca. Controle total sobre limites, recompensas e regras de negócio.',
      tag: 'Cartões',
      color: '#F59E0B',
      highlights: ['Cartão com sua marca', 'Controle de limites', 'Programa de pontos', 'Virtual e físico'],
    },
  ];

  setTab(index: number) {
    this.activeTab.set(index);
  }
}
