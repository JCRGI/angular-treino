import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface CreateMerchantRequest {
  name: string;
  document: string;
  email: string;
  phone: string;
  callbackUrl?: string;
}

export interface CreateMerchantResponse {
  id: string;
  name: string;
  document: string;
  email: string;
  preferredPsp: string;
  callbackUrl?: string;
  createdAt: string;
}

export interface RegisterUserRequest {
  merchantId: string;
  email: string;
  password: string;
}

@Injectable({ providedIn: 'root' })
export class MerchantService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = '/api/v1';

  createMerchant(data: CreateMerchantRequest): Observable<CreateMerchantResponse> {
    return this.http.post<CreateMerchantResponse>(`${this.baseUrl}/merchants`, data);
  }

  registerUser(data: RegisterUserRequest): Observable<void> {
    return this.http.post<void>(`${this.baseUrl}/auth/register`, data);
  }
}
