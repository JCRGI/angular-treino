import { Component, HostListener, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar',
  standalone: true,
  imports: [CommonModule, RouterLink],
  templateUrl: './navbar.component.html',
  styleUrl: './navbar.component.css'
})
export class NavbarComponent {
  scrolled = signal(false);
  mobileOpen = signal(false);

  @HostListener('window:scroll')
  onScroll() {
    this.scrolled.set(window.scrollY > 40);
  }

  toggleMobile() {
    this.mobileOpen.update(v => !v);
  }

  closeMobile() {
    this.mobileOpen.set(false);
  }

  navLinks = [
    { label: 'Produtos', href: '#produtos' },
    { label: 'Soluções', href: '#solucoes' },
    { label: 'Preços', href: '#precos' },
    { label: 'Desenvolvedores', href: '#devs' },
    { label: 'Empresa', href: '#empresa' },
  ];
}
