import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-hero',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './hero.component.html',
  styleUrl: './hero.component.css'
})
export class HeroComponent {
  metrics = [
    { value: 'R$ 2,4 tri', label: 'processados' },
    { value: '99,999%', label: 'de uptime' },
    { value: '135+', label: 'métodos de pagamento' },
  ];

  integrations = [
    { label: 'Pix', color: '#00D4AA', icon: '⚡' },
    { label: 'Boleto', color: '#5B4FE8', icon: '📄' },
    { label: 'Cartão', color: '#FF5C8A', icon: '💳' },
    { label: 'Carteira', color: '#F59E0B', icon: '👛' },
    { label: 'Cripto', color: '#8B5CF6', icon: '🪙' },
  ];
}
