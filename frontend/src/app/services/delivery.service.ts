import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import { Delivery, DeliveryPriority, DeliveryRequest, DeliveryStatus } from '../models/api.models';

@Injectable({ providedIn: 'root' })
export class DeliveryService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiUrl}/deliveries`;

  list(filters?: { status?: DeliveryStatus | ''; priority?: DeliveryPriority | '' }): Observable<Delivery[]> {
    let params = new HttpParams();
    if (filters?.status) {
      params = params.set('status', filters.status);
    }
    if (filters?.priority) {
      params = params.set('priority', filters.priority);
    }
    return this.http.get<Delivery[]>(this.baseUrl, { params });
  }

  create(request: DeliveryRequest): Observable<Delivery> {
    return this.http.post<Delivery>(this.baseUrl, request);
  }

  update(id: number, request: DeliveryRequest): Observable<Delivery> {
    return this.http.put<Delivery>(`${this.baseUrl}/${id}`, request);
  }

  updateStatus(id: number, status: DeliveryStatus): Observable<Delivery> {
    return this.http.patch<Delivery>(`${this.baseUrl}/${id}/status`, { status });
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }
}
