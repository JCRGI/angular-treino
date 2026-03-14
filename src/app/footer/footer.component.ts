import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-footer',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './footer.component.html',
  styleUrl: './footer.component.css'
})
export class FooterComponent {
  currentYear = new Date().getFullYear();

  columns = [
    {
      title: 'Produtos',
      links: [
        { label: 'Pagamentos', href: '#' },
        { label: 'Assinaturas', href: '#' },
        { label: 'Marketplace', href: '#' },
        { label: 'Emissão de Cartões', href: '#' },
        { label: 'Pix', href: '#' },
        { label: 'Antifraude', href: '#' },
      ],
    },
    {
      title: 'Soluções',
      links: [
        { label: 'Startups', href: '#' },
        { label: 'E-commerce', href: '#' },
        { label: 'Empresas', href: '#' },
        { label: 'Plataformas', href: '#' },
        { label: 'SaaS', href: '#' },
        { label: 'Finanças Embarcadas', href: '#' },
      ],
    },
    {
      title: 'Desenvolvedores',
      links: [
        { label: 'Documentação', href: '#' },
        { label: 'API Reference', href: '#' },
        { label: 'SDKs', href: '#' },
        { label: 'Webhooks', href: '#' },
        { label: 'Status', href: '#' },
        { label: 'Changelog', href: '#' },
      ],
    },
    {
      title: 'Empresa',
      links: [
        { label: 'Sobre nós', href: '#' },
        { label: 'Blog', href: '#' },
        { label: 'Carreiras', href: '#' },
        { label: 'Imprensa', href: '#' },
        { label: 'Parceiros', href: '#' },
        { label: 'Contato', href: '#' },
      ],
    },
  ];
}
