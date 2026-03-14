import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-solutions',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './solutions.component.html',
  styleUrl: './solutions.component.css'
})
export class SolutionsComponent {
  solutions = [
    {
      icon: '🚀',
      title: 'Startups',
      description: 'Comece a aceitar pagamentos hoje. Zero taxas de setup, integração em minutos e escale conforme seu crescimento.',
      color: '#5B4FE8',
      features: ['Integração em 10 minutos', 'Sem taxa de adesão', 'Dashboard completo', 'Suporte 24/7'],
      cta: 'Começar grátis',
    },
    {
      icon: '🏢',
      title: 'Empresas',
      description: 'Soluções enterprise com SLA premium, suporte dedicado, contratos personalizados e integração com ERPs.',
      color: '#00D4AA',
      features: ['SLA 99,999%', 'Gerente dedicado', 'Contratos customizados', 'Integração ERP'],
      cta: 'Falar com vendas',
    },
    {
      icon: '🏗️',
      title: 'Plataformas',
      description: 'Torne sua plataforma um sistema financeiro completo. Ofereça serviços de pagamento como produto para seus clientes.',
      color: '#FF5C8A',
      features: ['Pagamentos embarcados', 'Revenue share', 'White-label', 'API completa'],
      cta: 'Ver documentação',
    },
    {
      icon: '🛒',
      title: 'E-commerce',
      description: 'Checkout otimizado para conversão, recuperação de carrinho abandonado e análise de dados de vendas em tempo real.',
      color: '#F59E0B',
      features: ['Checkout 1-clique', 'Recuperação de carrinho', 'Relatórios de vendas', 'A/B de checkout'],
      cta: 'Ver integração',
    },
  ];

  trustedBy = [
    { name: 'NuBank', initial: 'N' },
    { name: 'iFood', initial: 'i' },
    { name: 'Mercado Livre', initial: 'ML' },
    { name: 'B2W', initial: 'B2' },
    { name: 'Rappi', initial: 'R' },
    { name: 'Stone', initial: 'S' },
  ];
}
