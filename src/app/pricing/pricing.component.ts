import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-pricing',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './pricing.component.html',
  styleUrl: './pricing.component.css'
})
export class PricingComponent {
  annual = signal(false);

  plans = [
    {
      name: 'Starter',
      icon: '⚡',
      description: 'Ideal para negócios iniciantes que querem começar a aceitar pagamentos',
      priceMonthly: 0,
      priceAnnual: 0,
      priceLabel: 'Grátis',
      priceSuffix: 'para sempre',
      txFee: '3,99% + R$ 0,30',
      highlighted: false,
      features: [
        'Até R$ 10.000/mês em transações',
        'Pix, boleto e cartão',
        'Dashboard básico',
        'API completa',
        'Suporte por email',
        '1 conta bancária',
      ],
      notIncluded: ['Antifraude avançado', 'Split de pagamentos', 'Relatórios avançados'],
      cta: 'Começar grátis',
    },
    {
      name: 'Pro',
      icon: '🚀',
      description: 'Para empresas em crescimento que precisam de recursos avançados',
      priceMonthly: 299,
      priceAnnual: 249,
      priceLabel: 'R$ 299',
      priceSuffix: '/mês',
      txFee: '2,99% + R$ 0,25',
      highlighted: true,
      badge: 'Mais popular',
      features: [
        'Transações ilimitadas',
        'Todos os métodos de pagamento',
        'Antifraude avançado com IA',
        'Split de pagamentos',
        'Relatórios e analytics',
        'Checkout customizável',
        'Webhooks avançados',
        'Suporte prioritário 24/7',
        'Múltiplas contas bancárias',
      ],
      notIncluded: [],
      cta: 'Começar 14 dias grátis',
    },
    {
      name: 'Enterprise',
      icon: '🏢',
      description: 'Soluções sob medida para grandes empresas com requisitos específicos',
      priceMonthly: 0,
      priceAnnual: 0,
      priceLabel: 'Sob consulta',
      priceSuffix: '',
      txFee: 'Taxa negociada',
      highlighted: false,
      features: [
        'Tudo do plano Pro',
        'SLA 99,999% garantido',
        'Gerente de conta dedicado',
        'Integração personalizada',
        'Contrato customizado',
        'Onboarding assistido',
        'Treinamento da equipe',
        'Compliance e auditoria',
        'Suporte ao desenvolvedor',
      ],
      notIncluded: [],
      cta: 'Falar com vendas',
    },
  ];
}
