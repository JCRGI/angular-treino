import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-stats',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './stats.component.html',
  styleUrl: './stats.component.css'
})
export class StatsComponent {
  stats = [
    { value: 'R$ 2,4 tri', label: 'em volume de pagamentos processados em 2025' },
    { value: '99,999%', label: 'de uptime garantido em nossos serviços' },
    { value: '135+', label: 'moedas e métodos de pagamento aceitos' },
    { value: '500M', label: 'requisições de API processadas por dia' },
  ];

  logos = [
    { name: 'NuBank' },
    { name: 'iFood' },
    { name: 'Mercado Livre' },
    { name: 'Magazine Luiza' },
    { name: 'Rappi' },
    { name: 'Stone' },
    { name: 'B2W Digital' },
    { name: 'Localiza' },
  ];
}
