import { Component, inject, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import {
  ReactiveFormsModule,
  FormBuilder,
  FormGroup,
  Validators,
  AbstractControl,
  ValidationErrors
} from '@angular/forms';
import { MerchantService } from '../../services/merchant.service';

type Step = 'empresa' | 'acesso' | 'sucesso';

function passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
  const password = control.get('password');
  const confirm = control.get('confirmPassword');
  if (password && confirm && password.value !== confirm.value) {
    return { passwordMismatch: true };
  }
  return null;
}

@Component({
  selector: 'app-cadastro',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './cadastro.component.html',
  styleUrl: './cadastro.component.css'
})
export class CadastroComponent {
  private readonly fb = inject(FormBuilder);
  private readonly merchantService = inject(MerchantService);

  step = signal<Step>('empresa');
  loading = signal(false);
  errorMsg = signal('');
  merchantId = signal('');

  empresaForm: FormGroup = this.fb.group({
    name:     ['', [Validators.required, Validators.maxLength(200)]],
    document: ['', [Validators.required, Validators.pattern(/^\d{11}$|^\d{14}$/)]],
    phone:    ['', [Validators.required, Validators.maxLength(20)]]
  });

  acessoForm: FormGroup = this.fb.group(
    {
      email:           ['', [Validators.required, Validators.email]],
      password:        ['', [Validators.required, Validators.minLength(8)]],
      confirmPassword: ['', Validators.required],
      callbackUrl:     ['', [Validators.pattern(/^https?:\/\/.+/)]]
    },
    { validators: passwordMatchValidator }
  );

  // ── helpers ──────────────────────────────────────────────────────────────
  isInvalid(form: FormGroup, field: string): boolean {
    const c = form.get(field);
    return !!(c && c.invalid && (c.dirty || c.touched));
  }

  get passwordMismatch(): boolean {
    return !!(
      this.acessoForm.hasError('passwordMismatch') &&
      this.acessoForm.get('confirmPassword')?.touched
    );
  }

  formatDocument(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = input.value.replace(/\D/g, '').slice(0, 14);
    this.empresaForm.get('document')!.setValue(digits, { emitEvent: false });
    input.value = digits;
  }

  formatPhone(event: Event): void {
    const input = event.target as HTMLInputElement;
    const digits = input.value.replace(/\D/g, '').slice(0, 11);
    this.empresaForm.get('phone')!.setValue(digits, { emitEvent: false });
    input.value = digits;
  }

  // ── navigation ────────────────────────────────────────────────────────────
  goToAcesso(): void {
    if (this.empresaForm.invalid) {
      this.empresaForm.markAllAsTouched();
      return;
    }
    this.errorMsg.set('');
    this.step.set('acesso');
  }

  backToEmpresa(): void {
    this.errorMsg.set('');
    this.step.set('empresa');
  }

  // ── submission ────────────────────────────────────────────────────────────
  submit(): void {
    if (this.acessoForm.invalid) {
      this.acessoForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.errorMsg.set('');

    const { name, document, phone } = this.empresaForm.value;
    const { email, password, callbackUrl } = this.acessoForm.value;

    this.merchantService.createMerchant({
      name: name.trim(),
      document: document.replace(/\D/g, ''),
      email: email.trim().toLowerCase(),
      phone: phone.replace(/\D/g, ''),
      ...(callbackUrl ? { callbackUrl: callbackUrl.trim() } : {})
    }).subscribe({
      next: (merchant) => {
        this.merchantId.set(merchant.id);
        this.merchantService.registerUser({
          merchantId: merchant.id,
          email: email.trim().toLowerCase(),
          password
        }).subscribe({
          next: () => {
            this.loading.set(false);
            this.step.set('sucesso');
          },
          error: (err) => {
            this.loading.set(false);
            this.errorMsg.set(
              err?.error?.message ?? 'Erro ao criar conta de acesso. Tente novamente.'
            );
          }
        });
      },
      error: (err) => {
        this.loading.set(false);
        if (err?.status === 409) {
          this.errorMsg.set('Este CPF/CNPJ já está cadastrado.');
          this.step.set('empresa');
        } else {
          this.errorMsg.set(
            err?.error?.message ?? 'Erro ao criar empresa. Verifique os dados e tente novamente.'
          );
        }
      }
    });
  }
}
