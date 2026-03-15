import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface Session {
  token: string;
  merchantId: string;
  email: string;
  name: string;
  phone?: string;
}

interface LoginResponse {
  token: string;
  merchantId: string;
  email: string;
}

const SESSION_KEY = 'dpag_session';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);
  private readonly _session = signal<Session | null>(this.readSession());

  readonly session = this._session.asReadonly();

  get isAuthenticated(): boolean {
    return !!this._session();
  }

  login(email: string, password: string): Observable<LoginResponse> {
    return this.http
      .post<LoginResponse>('/api/v1/auth/login', { email, password })
      .pipe(tap(res => {
        const session: Session = {
          token: res.token,
          merchantId: res.merchantId,
          email: res.email,
          name: res.email.split('@')[0],
        };
        this.saveSession(session);
      }));
  }

  saveSession(session: Session): void {
    localStorage.setItem(SESSION_KEY, JSON.stringify(session));
    this._session.set(session);
  }

  updateSession(partial: Partial<Session>): void {
    const current = this._session();
    if (!current) return;
    const updated = { ...current, ...partial };
    this.saveSession(updated);
  }

  logout(): void {
    localStorage.removeItem(SESSION_KEY);
    this._session.set(null);
  }

  private readSession(): Session | null {
    try {
      const raw = localStorage.getItem(SESSION_KEY);
      return raw ? JSON.parse(raw) : null;
    } catch {
      return null;
    }
  }
}
