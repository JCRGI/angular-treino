import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export type OperationType = 'NACIONAL' | 'OFFSHORE';

export interface CreateMerchantRequest {
  name: string;
  document: string;
  email: string;
  phone: string;
  callbackUrl?: string;
  preferredPsp?: string;
}

export interface CreateMerchantResponse {
  id: string;
  name: string;
  document: string;
  email: string;
  preferredPsp: string;
  callbackUrl?: string;
  createdAt: string;
  operationType?: OperationType;
}

export interface RegisterUserRequest {
  merchantId: string;
  email: string;
  password: string;
}

export interface ApiKeyResponse {
  id: string;
  key: string;
  createdAt: string;
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

  generateApiKey(merchantId: string): Observable<ApiKeyResponse> {
    return this.http.post<ApiKeyResponse>(`${this.baseUrl}/merchants/${merchantId}/api-keys`, {});
  }
}
