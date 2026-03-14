import { Component } from '@angular/core';
import { NavbarComponent } from '../navbar/navbar.component';
import { HeroComponent } from '../hero/hero.component';
import { StatsComponent } from '../stats/stats.component';
import { FeaturesComponent } from '../features/features.component';
import { SolutionsComponent } from '../solutions/solutions.component';
import { PricingComponent } from '../pricing/pricing.component';
import { FooterComponent } from '../footer/footer.component';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    NavbarComponent,
    HeroComponent,
    StatsComponent,
    FeaturesComponent,
    SolutionsComponent,
    PricingComponent,
    FooterComponent,
  ],
  templateUrl: './home.component.html',
  styleUrl: './home.component.css',
})
export class HomeComponent {
  devSteps = [
    {
      step: '01',
      title: 'Instale o SDK',
      description: 'npm install dom-pagamentos',
      code: `npm install dom-pagamentos`,
      icon: '📦',
    },
    {
      step: '02',
      title: 'Configure suas credenciais',
      description: 'Obtenha sua chave API no dashboard',
      code: `const dom = new DomPagamentos('sk_live_...');`,
      icon: '🔑',
    },
    {
      step: '03',
      title: 'Crie sua primeira cobrança',
      description: 'Em 3 linhas você já está cobrando',
      code: `await dom.cobrar.criar({ valor: 10000, metodo: 'pix' });`,
      icon: '⚡',
    },
  ];

  securityFeatures = [
    { icon: '🔒', title: 'PCI DSS Level 1', description: 'O mais alto nível de conformidade de segurança para dados de pagamento.' },
    { icon: '🛡️', title: 'Criptografia TLS 1.3', description: 'Todos os dados em trânsito são protegidos com a mais recente criptografia.' },
    { icon: '🤖', title: 'Antifraude com IA', description: 'Machine learning analisa milhares de sinais para bloquear fraudes em tempo real.' },
    { icon: '📜', title: 'Banco Central', description: 'Licenciado e regulamentado pelo Banco Central do Brasil como Instituição de Pagamento.' },
    { icon: '🔍', title: 'Auditoria 24/7', description: 'Monitoramento contínuo e logs imutáveis para compliance e rastreabilidade.' },
    { icon: '🌐', title: 'ISO 27001', description: 'Certificação internacional de gestão de segurança da informação.' },
  ];
}
