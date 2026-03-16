import { Component, inject, signal, computed, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterLink } from '@angular/router';
import { ReactiveFormsModule, FormBuilder, Validators } from '@angular/forms';
import { AuthService } from '../services/auth.service';
import { MerchantService } from '../services/merchant.service';

type Section = 'overview' | 'payments' | 'apikeys' | 'settings';
type TxStatus = 'approved' | 'pending' | 'failed';

interface Transaction {
  id: string;
  customer: string;
  method: string;
  methodIcon: string;
  amount: number;
  status: TxStatus;
  date: Date;
}

interface ApiKey {
  id: string;
  maskedKey: string;
  createdAt: Date;
  lastUsed: Date | null;
}

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  private readonly auth = inject(AuthService);
  private readonly merchant = inject(MerchantService);
  private readonly router = inject(Router);
  private readonly fb = inject(FormBuilder);

  // ── Layout ──────────────────────────────────────────────────────────────
  activeSection = signal<Section>('overview');
  sidebarOpen = signal(false);

  // ── Session ──────────────────────────────────────────────────────────────
  session = this.auth.session;

  // ── Transactions ─────────────────────────────────────────────────────────
  allTransactions = signal<Transaction[]>([]);
  statusFilter = signal<TxStatus | 'all'>('all');
  currentPage = signal(1);
  pageSize = 8;

  filteredTransactions = computed(() => {
    const f = this.statusFilter();
    return f === 'all'
      ? this.allTransactions()
      : this.allTransactions().filter(t => t.status === f);
  });

  pagedTransactions = computed(() => {
    const page = this.currentPage();
    const start = (page - 1) * this.pageSize;
    return this.filteredTransactions().slice(start, start + this.pageSize);
  });

  totalPages = computed(() =>
    Math.ceil(this.filteredTransactions().length / this.pageSize)
  );

  // ── Stats ────────────────────────────────────────────────────────────────
  statsMonth = computed(() =>
    this.allTransactions()
      .filter(t => t.status === 'approved')
      .reduce((s, t) => s + t.amount, 0)
  );

  approvalRate = computed(() => {
    const txs = this.allTransactions();
    if (!txs.length) return 0;
    return Math.round(txs.filter(t => t.status === 'approved').length / txs.length * 100);
  });

  recentTransactions = computed(() => this.allTransactions().slice(0, 5));

  // ── API Keys ─────────────────────────────────────────────────────────────
  apiKeys = signal<ApiKey[]>([]);
  newKey = signal('');
  newKeyCopied = signal(false);
  generatingKey = signal(false);
  keyError = signal('');

  // ── Settings ─────────────────────────────────────────────────────────────
  settingsForm = this.fb.group({
    name:        ['', Validators.required],
    phone:       ['', Validators.required],
    callbackUrl: ['', Validators.pattern(/^https?:\/\/.+/)],
  });
  settingsSaving = signal(false);
  settingsSaved = signal(false);

  // ── Lifecycle ────────────────────────────────────────────────────────────
  ngOnInit(): void {
    if (!this.auth.isAuthenticated) {
      this.router.navigate(['/entrar']);
      return;
    }
    const s = this.session();
    if (s) {
      this.settingsForm.patchValue({ name: s.name, phone: s.phone ?? '' });
    }
    this.allTransactions.set(this.mockTransactions());
    this.apiKeys.set(this.mockApiKeys());
  }

  // ── Navigation ───────────────────────────────────────────────────────────
  navigate(section: Section): void {
    this.activeSection.set(section);
    this.sidebarOpen.set(false);
    this.currentPage.set(1);
    this.newKey.set('');
    this.keyError.set('');
    this.settingsSaved.set(false);
  }

  toggleSidebar(): void {
    this.sidebarOpen.update(open => !open);
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/']);
  }

  // ── Payments ─────────────────────────────────────────────────────────────
  setFilter(f: TxStatus | 'all'): void {
    this.statusFilter.set(f);
    this.currentPage.set(1);
  }

  prevPage(): void { if (this.currentPage() > 1) this.currentPage.update(p => p - 1); }
  nextPage(): void { if (this.currentPage() < this.totalPages()) this.currentPage.update(p => p + 1); }

  pageNumbers = computed(() =>
    Array.from({ length: this.totalPages() }, (_, i) => i + 1)
  );

  // ── API Keys ─────────────────────────────────────────────────────────────
  generateKey(): void {
    const s = this.session();
    if (!s) return;
    this.generatingKey.set(true);
    this.keyError.set('');
    this.newKey.set('');
    this.newKeyCopied.set(false);

    this.merchant.generateApiKey(s.merchantId).subscribe({
      next: (res) => {
        this.newKey.set(res.key);
        this.apiKeys.update(keys => [{
          id: res.id,
          maskedKey: res.key.slice(0, 14) + '••••••••••••••••',
          createdAt: new Date(),
          lastUsed: null,
        }, ...keys]);
        this.generatingKey.set(false);
      },
      error: (err) => {
        this.keyError.set(err?.error?.message ?? 'Erro ao gerar a chave. Tente novamente.');
        this.generatingKey.set(false);
      }
    });
  }

  copyKey(): void {
    navigator.clipboard.writeText(this.newKey()).then(() => {
      this.newKeyCopied.set(true);
      setTimeout(() => this.newKeyCopied.set(false), 2500);
    });
  }

  dismissKey(): void { this.newKey.set(''); }

  copyText(text: string): void { navigator.clipboard.writeText(text); }

  // ── Settings ─────────────────────────────────────────────────────────────
  saveSettings(): void {
    if (this.settingsForm.invalid) { this.settingsForm.markAllAsTouched(); return; }
    this.settingsSaving.set(true);
    // Optimistic update — swap with real API call when available
    setTimeout(() => {
      const { name, phone } = this.settingsForm.value;
      this.auth.updateSession({ name: name!, phone: phone! });
      this.settingsSaving.set(false);
      this.settingsSaved.set(true);
      setTimeout(() => this.settingsSaved.set(false), 3000);
    }, 700);
  }

  isSettingInvalid(field: string): boolean {
    const c = this.settingsForm.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  // ── Filter options ───────────────────────────────────────────────────────
  readonly filterOptions: { label: string; value: TxStatus | 'all' }[] = [
    { label: 'Todos', value: 'all' },
    { label: 'Aprovados', value: 'approved' },
    { label: 'Pendentes', value: 'pending' },
    { label: 'Falhou', value: 'failed' },
  ];

  statusLabel(s: TxStatus): string {
    return { approved: 'Aprovado', pending: 'Pendente', failed: 'Falhou' }[s];
  }

  // ── Helpers ──────────────────────────────────────────────────────────────
  currency(v: number): string {
    return v.toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
  }

  dateStr(d: Date): string {
    return d.toLocaleString('pt-BR', { day: '2-digit', month: '2-digit', year: '2-digit', hour: '2-digit', minute: '2-digit' });
  }

  initials(name: string): string {
    return name.split(' ').slice(0, 2).map(w => w[0]).join('').toUpperCase();
  }

  // ── Mock data ─────────────────────────────────────────────────────────────
  private mockTransactions(): Transaction[] {
    const methods = [
      { name: 'Pix', icon: '⚡' },
      { name: 'Mastercard', icon: '💳' },
      { name: 'Visa', icon: '💳' },
      { name: 'Apple Pay', icon: '👛' },
      { name: 'Boleto', icon: '📄' },
    ];
    const statuses: TxStatus[] = ['approved', 'approved', 'approved', 'approved', 'pending', 'failed'];
    const customers = ['João Mello', 'Ana Santos', 'Carlos Pereira', 'Beatriz Faria', 'Lucas Rocha', 'Mariana Telles', 'Rafael Costa', 'Julia Alves', 'Pedro Lima', 'Fernanda Cruz'];
    const amounts = [89.90, 249.00, 1250.00, 499.90, 79.99, 2000.00, 350.00, 5000.00, 129.90, 999.00, 45.00, 189.50, 3200.00, 67.80, 789.00];

    return Array.from({ length: 40 }, (_, i) => ({
      id: `dpag_tx_${Math.random().toString(36).slice(2, 11)}`,
      customer: customers[i % customers.length],
      method: methods[i % methods.length].name,
      methodIcon: methods[i % methods.length].icon,
      amount: amounts[i % amounts.length],
      status: statuses[i % statuses.length],
      date: new Date(Date.now() - i * 3_600_000 * (i % 5 + 1)),
    }));
  }

  private mockApiKeys(): ApiKey[] {
    return [{
      id: 'key_demo_1',
      maskedKey: 'dpag_live_••••••••••••••••••••••',
      createdAt: new Date(Date.now() - 30 * 86_400_000),
      lastUsed: new Date(Date.now() - 3_600_000),
    }];
  }
}
